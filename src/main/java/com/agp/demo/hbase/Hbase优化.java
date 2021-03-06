package com.agp.demo.hbase;

/**
 * 1内存优化 CSM--》 G1
 * 2.优化G1的参数
 * 3.操作系统优化
 * 4.HDFS优化
 * 1.短路读
 * 2. Hedged ReadHBase数据在HDFS中默认存储三个副本，
 * 通常情况下HBase会根据一定算法优先选择一个DataNode进行数据读取。
 * 然而在某些情况下，有可能因为磁盘问题或者网络问题等引起读取超时，
 * 根据Hedged Read策略，如果在指定时间内读取请求没有返回，
 * HDFS客户端将会向第二个副本发送第二次数据请求，
 * 并且谁先返回就使用谁，之后返回的将会被丢弃。
 *
 * 3. Region Data Locality
 * region hfile本地化程度。
 * 数据本地率低通常是由于Region迁移
 * （自动balance开启、RegionServer宕机迁移、手动迁移等）导致的
 *
 * 如果数据本地率很低，还可以在
 * 业务低峰期通过执行major_compact将数据本地率提升到100%。
 *
 * HBase读取性能优化
 *  分为三种：
 *  服务器端、客户端、列簇设计
 *
 *HBase服务器端优化
 *  1. 读请求是否均衡？
 *      假如业务所有读请求都落在集群某一台RegionServer
 *      上的某几个Region上，很显然，这一方面不能发挥整个集群的并发处理能力，
 *      另一方面势必造成此台RegionServer资源严重消耗（比如IO耗尽、handler耗尽等）
 *   观察确认：观察所有RegionServer的读请求QPS曲线，确认是否存在读请求不均衡现象。
 *   优化建议：
 *   Rowkey必须进行散列化处理（比如MD5散列），
 *   同时建表必须进行预分区处理。
 *  2. BlockCache设置是否合理？
 *      BlockCache作为读缓存，对于读性能至关重要。
 *      默认情况下BlockCache和MemStore的配置相对比较均衡（各占40%）
 *      比如读多写少业务可以将BlockCache占比调大
 *
 *     另一方面，BlockCache的策略选择也很重要，（LRU，Bucket，混合）
 *     不同策略对读性能来说影响并不是很大，但是对GC的影响却相当显著，
 *     尤其在BucketCache的offheap模式下GC表现非常优秀。
 *
 *  优化建议：如果JVM内存配置量小于20G，BlockCache策略选择LRUBlockCache；
 *  否则选择BucketCache策略的offheap模式。
 *
 *  3. HFile文件是否太多？
 *      HBase在读取数据时通常先到MemStore和BlockCache中检索
 *      （读取最近写入数据和热点数据），如果查找不到则到文件中检索。
 *      HBase的类LSM树结构导致每个store包含多个HFile文件，
 *      文件越多，检索所需的IO次数越多，读取延迟也就越高。
 *
 *      文件数量通常取决于Compaction的执行策略，一般和两个配置参数有关：
 *      hbase.hstore. compactionThreshold
 *      和hbase.hstore.compaction.max.size，
 *      前者表示一个store中的文件数超过阈值就应该进行合并，
 *      后者表示参与合并的文件大小最大是多少，
 *      超过此大小的文件不能参与合并。
 *
 * 观察确认：观察RegionServer级别以及Region级别的HFile数，确认HFile文件是否过多。
 *
 * 优化建议：hbase.hstore.compactionThreshold设置不能太大，默认为3个。
 *
 * 4. Compaction是否消耗系统资源过多？
 *      优化原理：Compaction是将小文件合并为大文件，
 *      提高后续业务随机读性能，但是也会带来IO放大以及带宽消耗问题
 *
 *      常配置情况下，Minor Compaction并不会带来很大的系统资源消耗，
 *      除非因为配置不合理导致Minor Compaction太过频繁
 *
 *      或者Region设置太大发生Major Compaction。
 *
 *   观察确认：观察系统IO资源以及带宽资源使用情况，
 *   再观察Compaction队列长度，确认是否由于Compaction导致系统资源消耗过多。
 *
 *   优化建议：对于大Region读延迟敏感的业务（100G以上）
 *   通常不建议开启自动Major Compaction，手动低峰期触发。
 *   小Region或者延迟不敏感的业务可以开启Major Compaction，但建议限制流量。
 *
 * 13.5.2 HBase客户端优化
 *
 *  优化原理：HBase业务通常一次scan就会返回大量数据，因此客户端发起一次scan请求，
 *  实际并不会一次就将所有数据加载到本地，而是分成多次RPC请求进行加载，
 *
 *  但是对于一些大scan（一次scan可能需要查询几万甚至几十万行数据），
 *  每次请求100条数据意味着一次scan需要几百甚至几千次RPC请求，
 *  这种交互的代价无疑是很大的。因此可以考虑将scan缓存设置增大
 *
 *  比如设为500或者1000条可能更加合适。笔者之前做过一次试验，
 *  在一次scan 10w+条数据量的条件下，
 *  将scan缓存从100增加到1000条，可以有效降低scan请求的总体延迟，延迟降低了25%左右。
 *
 *  优化建议：大scan场景下将scan缓存从100增大到500或者1000，用以减少RPC次数。
 *
 * 2. get是否使用批量请求？
 * 优化原理：HBase分别提供了单条get以及批量get的API接口，
 * 使用批量get接口可以减少客户端到RegionServer之间的RPC连接数，提高读取吞吐量。
 *外需要注意的是，批量get请求要么成功返回所有请求数据，要么抛出异常。
 *
 * 3. 请求是否可以显式指定列簇或者列？
 *
 * 优化原理：HBase是典型的列簇数据库，
 * 意味着同一列簇的数据存储在一起，不同列簇的数据分开存储在不同的目录下。
 * 一个表有多个列簇，如果只是根据rowkey而不指定列簇进行检索，
 * 不同列簇的数据需要独立进行检索，
 * 性能必然会比指定列簇的查询差很多，很多情况下甚至会有2～3倍的性能损失。
 *
 * 优化建议：尽量指定列簇或者列进行精确查找。
 *
 * 4. 离线批量读取请求是否设置禁止缓存？
 *
 * 优化原理：通常在离线批量读取数据时会进行一次性全表扫描，
 * 一方面数据量很大，另一方面请求只会执行一次。
 *这种场景下如果使用scan默认设置，就会将数据从HDFS加载出来放到缓存。
 * 可想而知，大量数据进入缓存必将其他实时业务热点数据挤出，
 * 其他业务不得不从HDFS加载，进而造成明显的读延迟毛刺。
 *
 * 优化建议：离线批量读取请求设置禁用缓存，scan.setCacheBlocks (false)。
 *
 * 13.5.3 HBase列簇设计优化
 *
 * 布隆过滤器是否设置？
 *
 * 优化原理：布隆过滤器主要用来过滤不存在待检索rowkey的HFile文件，避免无用的IO操作。
 *
 * 布隆过滤器取值有两个——row以及rowcol，
 * 需要根据业务来确定具体使用哪种。如果业务中大多数随机查询仅仅使用row作为查询条件，
 * 布隆过滤器一定要设置为row；如果大多数随机查询使用row+column作为查询条件，
 * 布隆过滤器需要设置为rowcol。
 * 如果不确定业务查询类型，则设置为row。
 *
 * 优化建议：任何业务都应该设置布隆过滤器，通常设置为row，
 * 除非确认业务随机查询类型为row+column，则设置为rowcol。
 *
 *
 *13.6 HBase写入性能调优
 *
 * HBase系统主要应用于写多读少的业务场景，通常来说对系统的写入吞吐量要求都比较高。
 * 而在实际生产线环境中，HBase运维人员或多或少都会遇到写入吞吐量比较低、写入比较慢的情况
 *
 * ，可以从HBase服务器端和业务客户端两个角度分析，确认是否还有提高的空间。
 *如图13-22所示。
 *
 *  13.6.1 HBase服务器端优化
 *
 *      1. Region是否太少？
 *      优化原理：当前集群中表的Region个数如果小于RegionServer个数，
 *      即Num(Region of Table) < Num (RegionServer)，
 *      可以考虑切分Region并尽可能分布到不同的RegionServer上以提高系统请求并发度。
 *
 *      优化建议：在Num (Region of Table) < Num (RegionServer)
 *      的场景下切分部分请求负载高的Region，
 *      并迁移到其他RegionServer。
 *
 *      2. 写入请求是否均衡？
 *
 *      优化原理：写入请求如果不均衡，会导致系统并发度较低，还
 *      有可能造成部分节点负载很高，进而影响其他业务。
 *      分布式系统中特别需要注意单个节点负载很高的情况，
 *      单个节点负载很高可能会拖慢整个集群，这是因为很多业务会使用Mutli批量提交读写请求，
 *      一旦其中一部分请求落到慢节点无法得到及时响应，会导致整个批量请求超时。
 *
 *      优化建议：检查Rowkey设计以及预分区策略，保证写入请求均衡。
 *
 *      3. Utilize Flash storage for WAL
 *
 *      该特性会将WAL文件写到SSD上，对于写性能会有非常大的提升
 *
 *      1）使用HDFS Archival Storage机制，
 *      在确保物理机有SSD硬盘的前提下配置HDFS的部分文件目录为SSD介质。
 *
 *      hbase.wal.storage.policy默认为none，用户可以指定ONE_SSD或者ALL_SSD。
 *
 *      •ONE_SSD ：WAL在HDFS上的一个副本文件写入SSD介质，
 *      另两个副本写入默认存储介质。
 *      •ALL_SSD：WAL的三个副本文件全部写入SSD介质。
 *
 * 13.6.2 HBase客户端优化
 *  1. 是否可以使用Bulkload方案写入？
 *  Bulkload是一个MapReduce程序，运行在Hadoop集群。
 *  程序的输入是指定数据源，输出是HFile文件。
 *  HFile文件生成之后再通过LoadIncrementalHFiles工具
 *  将HFile中相关元数据加载到HBase中。
 *
 *  Bulkload方案适合将已经存在于HDFS上的数据批量导入HBase集群。
 *  相比调用API的写入方案，Bulkload方案可以更加高效、快速地导入数据，
 *  而且对HBase集群几乎不产生任何影响。
 *
 * 2. 是否需要写WAL? WAL是否需要同步写入？
 *
 * 优化原理：数据写入流程可以理解为一次顺序写WAL+一次写缓存，
 * 通常情况下写缓存延迟很低，因此提升写性能只能从WAL入手。
 * HBase中可以通过设置WAL的持久化等级决定是否开启WAL机制以及HLog的落盘方式。
 *
 * WAL的持久化分为四个等级：
 * SKIP_WAL，
 * ASYNC_WAL，
 * SYNC_WAL
 * FSYNC_WAL。
 * 如果用户没有指定持久化等级，HBase默认使用SYNC_WAL等级持久化数据。
 *
 * 在实际生产线环境中，部分业务可能并不特别关心异常情况下少量数据的丢失，
 * 而更关心数据写入吞吐量。比如某些推荐业务，
 * 这类业务即使丢失一部分用户行为数据可能对推荐结果也不会构成很大影响，
 * 但是对于写入吞吐量要求很高，不能造成队列阻塞。
 *
 * 这种场景下可以考虑关闭WAL写入。退而求其次，有些业务必须写WAL，
 * 但可以接受WAL异步写入，这是可以考虑优化的，通常也会带来一定的性能提升。
 *
 *优化推荐：根据业务关注点在WAL机制与写入吞吐量之间做出选择，
 * 用户可以通过客户端设置WAL持久化等级。
 *
 * 3. Put是否可以同步批量提交？
 *
 * 优化原理：HBase分别提供了单条put以及批量put的API接口，
 * 使用批量put接口可以减少客户端到RegionServer之间的RPC连接数，提高写入吞吐量。
 *外需要注意的是，批量put请求要么全部成功返回，要么抛出异常。
 * 优化建议：使用批量put写入请求。
 *
 *4. Put是否可以异步批量提交？
 *
 * 优化原理：如果业务可以接受异常情况下少量数据丢失，
 * 可以使用异步批量提交的方式提交请求。提交分两阶段执行：
 * 用户提交写请求，数据写入客户端缓存，并返回用户写入成功；
 * 当客户端缓存达到阈值（默认2M）后批量提交给RegionServer。
 * 需要注意的是，在某些客户端异常的情况下，缓存数据有可能丢失。
 *
 * 优化建议：在业务可以接受的情况下开启异步批量提交，用户可以设置setAutoFlush (false)
 *
 *5. 写入KeyValue数据是否太大？
 *  KeyValue大小对写入性能的影响巨大。一旦遇到写入性能比较差的情况，
 *  需要分析写入性能下降是否因为写入KeyValue的数据太大
 *
 *  随着单行数据不断变大，写入吞吐量急剧下降，写入延迟在100K之后急剧增大。
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
public class Hbase优化 {
}
