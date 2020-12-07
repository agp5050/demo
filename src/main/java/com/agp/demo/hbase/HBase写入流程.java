package com.agp.demo.hbase;

/**
 * HBase采用LSM树架构，天生适用于写多读少的应用场景
 * HBase服务端并没有提供update、delete接口，
 * HBase中对数据的更新、删除操作在服务器端也认为是写入操作
 * 更新操作会写入一个最新版本数据，
 * 删除操作会写入一条标记为deleted的KV数据
 * 所以HBase中更新、删除操作的流程与写入流程完全一致。
 *
 * 从整体架构的视角来看，写入流程可以概括为三个阶段。
 * 1）客户端处理阶段：客户端将用户的写入请求进行预处理，
 * 并根据集群元数据定位写入数据所在的RegionServer，将请求发送给对应的RegionServer。
 *
 * 2）Region写入阶段：RegionServer接收到写入请求之后将数据解析出来，
 * 首先写入WAL，再写入对应Region列簇的MemStore。
 *
 * 3）MemStore Flush阶段：当Region中MemStore容量超过一定阈值，
 * 系统会异步执行f lush操作，将内存中的数据写入文件，形成HFile。
 *
 *
 *
 * 1. 客户端处理阶段
 *
 * 步骤1：用户提交put请求后，HBase客户端会将写入的数据添加到本地缓冲区中，
 * 符合一定条件就会通过AsyncProcess异步批量提交。
 * HBase默认设置autoflush=true，表示put请求直接会提交给服务器进行处理；
 * 用户可以设置autoflush=false，这样，put请求会首先放到本地缓冲区，
 * 等到本地缓冲区大小超过一定阈值（默认为2M，可以通过配置文件配置）之后才会提交。
 * 很显然，后者使用批量提交请求，可以极大地提升写入吞吐量，但是因为没有保护机制，
 * 如果客户端崩溃，会导致部分已经提交的数据丢失
 *
 * 步骤2：在提交之前，HBase会在元数据表hbase:meta中根据rowkey找到它们归属的RegionServer，
 * 这个定位的过程是通过HConnection的locateRegion方法完成的。
 * 如果是批量请求，还会把这些rowkey按照HRegionLocation分组，
 * 不同分组的请求意味着发送到不同的RegionServer，因此每个分组对应一次RPC请求。
 *
 * 6-2图：
 * •客户端根据写入的表以及rowkey在元数据缓存中查找，
 * 如果能够查找出该rowkey所在的RegionServer以及Region，
 * 就可以直接发送写入请求（携带Region信息）到目标RegionServer。
 *
 *•如果客户端缓存中没有查到对应的rowkey信息，需要首先到ZooKeeper上
 * /hbase-root/meta-region-server节点查找HBase元数据表所在的RegionServer。
 * 然后向hbase:meta所在的RegionServer发送查询请求，
 * 在元数据表中查找rowkey所在的RegionServer以及Region信息。
 * 客户端接收到返回结果之后会将结果缓存到本地，以备下次使用。
 *
 * •客户端根据rowkey相关元数据信息将写入请求发送给目标RegionServer，
 * Region Server接收到请求之后会解析出具体的Region信息，
 * 查到对应的Region对象，并将数据写入目标Region的MemStore中。
 *
 * 步骤3：HBase会为每个HRegionLocation构造一个远程RPC请求MultiServerCallable，
 * 并通过rpcCallerFactory. newCaller()执行调用。
 * 将请求经过Protobuf序列化后发送给对应的RegionServer。
 *
 * 2. Region写入阶段
 *
 * 服务器端RegionServer接收到客户端的写入请求后，
 * 首先会反序列化为put对象，然后执行各种检查操作，
 * 比如检查Region是否是只读、MemStore大小是否超过blockingMemstoreSize等。
 * 检查完成之后，执行一系列核心操作
 *
 * 1）Acquire locks ：HBase中使用行锁保证对同一行数据的更新都是互斥操作，
 * 用以保证更新的原子性，要么更新成功，要么更新失败。
 *
 * 2）Update LATEST_TIMESTAMP timestamps ：
 * 更新所有待写入（更新）KeyValue的时间戳为当前系统时间。
 *
 * 3）Build WAL edit ：HBase使用WAL机制保证数据可靠性，
 * 即首先写日志再写缓存，即使发生宕机，也可以通过恢复HLog还原出原始数据。
 * 该步骤就是在内存中构建WALEdit对象，为了保证Region级别事务的写入原子性，
 *      一次写入操作中所有KeyValue会构建成一条WALEdit记录。
 *
 *4）Append WALEdit To WAL ：将步骤3中构造在内存中的WALEdit记录顺序写入HLog中，
 * 此时不需要执行sync操作。
 * 当前版本的HBase使用了disruptor实现了高效的生产者消费者队列，
 * 来实现WAL的追加写入操作。
 * 5）Write back to MemStore：写入WAL之后再将数据写入MemStore。
 *
 * 6）Release row locks：释放行锁。
 *
 * 7）Sync wal ：HLog真正sync到HDFS，
 * 在释放行锁之后执行sync操作是为了尽量减少持锁时间，
 * 提升写性能。如果sync失败，执行回滚操作将MemStore中已经写入的数据移除。
 *
 * 8）结束写事务：此时该线程的更新操作才会对其他读请求可见，更新才实际生效。
 *
 * 3. MemStore Flush阶段
 *
 * 1. 触发条件
 * •MemStore级别限制：当Region中任意一个MemStore的大小达到了上限
 * （hbase.hregion.memstore.flush.size，默认128MB）
 * ，会触发MemStore刷新。
 *
 * RegionServer级别限制：当RegionServer中MemStore的大小总和超过低水
 * 位阈值hbase.regionserver.global.memstore.size.lower.limit*hbase.regionserver.global.memstore.size，
 * RegionServer开始强制执行flush，
 * 先flush MemStore最大的Region，再flush次大的，依次执行。
 *如果此时写入吞吐量依然很高，导致总MemStore大小
 * 超过高水位阈值hbase.regionserver.global.memstore.size，
 * RegionServer会阻塞更新并强制执行flush，直至总MemStore大小下降到低水位阈值。
 *
 * •当一个RegionServer中HLog数量达到上限（可通过参数hbase.regionserver.maxlogs配置）时
 * ，系统会选取最早的HLog对应的一个或多个Region进行f lush
 *
 * HBase定期刷新MemStore ：默认周期为1小时，确保MemStore不会长时间没有持久化。
 * 为避免所有的MemStore在同一时间都进行flush而导致的问题，
 * 定期的f lush操作有一定时间的随机延时。
 *
 * •手动执行f lush ：用户可以通过shell命令flush 'tablename'
 * 或者f lush'regionname'分别对一个表或者一个Region进行flush。
 *
 *2. FLUSH执行流程
 * 1）prepare阶段：遍历当前Region中的所有MemStore，
 * 将MemStore中当前数据集CellSkipListSet（内部实现采用ConcurrentSkipListMap）做一个快照snapshot，
 * 然后再新建一个CellSkipListSet接收新的数据写入。
 * prepare阶段需要添加updateLock对写请求阻塞，结束之后会释放该锁。
 * 因为此阶段没有任何费时操作，因此持锁时间很短。
 *
 *2）flush阶段：遍历所有MemStore，将prepare阶段生成的snapshot持久化为临时文件，
 * 临时文件会统一放到目录.tmp下。这个过程因为涉及磁盘IO操作，因此相对比较耗时。
 *
 * 3）commit阶段：遍历所有的MemStore，将flush阶段生成的临时文件移到指定的ColumnFamily目录下，
 * 针对HFile生成对应的storef ile和Reader，
 * 把storefile添加到Store的storefiles列表中，
 * 最后再清空prepare阶段生成的snapshot。
 *
 *
 *
 *
 *
 *
 *
 *
 */
public class HBase写入流程 {
}
