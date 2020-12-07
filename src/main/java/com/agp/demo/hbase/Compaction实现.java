package com.agp.demo.hbase;

/**
 * HFile小文件如果数量太多会导致读取低效。
 * 为了提高读取效率，LSM树体系架构设计了一个非常重要的模块——Compaction
 *
 * 般基于LSM树体系架构的系统都会设计Compaction，
 * 比如LevelDB、RocksDB以及Cassandra等，都可以看到Compaction的身影
 *
 * Compaction是从一个Region的一个Store中选择部分HFile文件进行合并。
 * 归并排序成一个文件继续服务。
 *
 * HBase根据合并规模将Compaction分为两类：
 * Minor Compaction和MajorCompaction。
 *
 * Minor Compaction是指
 * 选取部分小的、相邻的HFile，将它们合并成一个更大的HFile。
 *
 * Major Compaction是指将一个Store中所有的HFile合并成一个HFile，
 * 这个过程还会完全清理三类无意义数据：
     * 被删除的数据、
     * TTL过期数据、
     * 版本号超过设定版本号的数据。
 *
 * 一般情况下，Major Compaction持续时间会比较长，
 * 整个过程会消耗大量系统资源，对上层业务有比较大的影响。
 * 因此线上部分数据量较大的业务通常推荐关闭自动触发Major Compaction功能，
 * 改为在业务低峰期手动触发（或设置策略自动在低峰期触发）。
 *
 * •合并小文件，减少文件数，稳定随机读延迟。
 * •提高数据的本地化率。
 * •清除无效数据，减少数据存储量。
 *
 * Compaction合并小文件的同时会将落在远程DataNode上的数据读取出来重新写入大文件
 * 合并后的大文件在当前DataNode节点上有一个副本，因此可以提高数据的本地化率。极端情况下，
 * Major Compaction可以将当前Region的本地化率提高到100%。
 * ’这也是最常用的一种提高数据本地化率的方法。
 *
 * 7.1.1 Compaction基本流程
 *
 * 只有在特定的触发条件才会执行，
 * 比如部分flush操作完成之后、
 * 周期性的Compaction检查操作
 *
 * HBase会将该Compaction交由一个独立的线程处理，
 * 该线程首先会从对应Store中选择合适的HFile文件进行合并，
 * 这一步是整个Compaction的核心
 *
 * 比如待合并文件数不能太多也不能太少、
 * 文件大小不能太大等，
 * 最理想的情况是，选取那些IO负载重、文件小的文件集。
 *
 * 实际实现中，HBase提供了多种文件选取算法
 *
 * RatioBasedCompactionPolicy、
 * ExploringCompactionPolicy
 * 和StripeCompactionPolicy
 *
 * 选出待合并的文件后，
 * HBase会根据这些HFile文件总大小挑选对应的线程池处理，
 * 最后对这些文件执行具体的合并操作。
 *
 * 7.1.2 Compaction触发时机
 *      MemStoreFlush、后台线程周期性检查以及手动触发
 *
 *      MemStore Flush ：应该说Compaction操作的源头来自flush操作，
 *      MemStore Flush会产生HFile文件，文件越来越多就需要compact执行合并。
 *      因此在每次执行完f lush操作之后，都会对当前Store中的文件数进行判断，
 *      一旦Store中总文件数大于hbase.hstore.compactionThreshold
 *
 *      Compaction都是以Store为单位进行的，而在flush触发条件下，
 *      整个Region的所有Store都会执行compact检查，
 *      所以一个Region有可能会在短时间内执行多次Compaction。
 *
 *      •后台线程周期性检查：RegionServer会在后台启动一个线程CompactionChecker，
 *      定期触发检查对应Store是否需要执行Compaction
 *
 *      该线程优先检查Store中总文件数是否大于阈值hbase.hstore.compactionThreshold，
 *      一旦大于就会触发Compaction
 *      如果不满足，接着检查是否满足Major Compaction条件。
 *
 *      如果当前Store中HFile的最早更新时间早于某个值mcTime，就会触发MajorCompaction
 *
 *      浮动区间默认为[7-7×0.2，7+7×0.2]，
 *      其中7为hbase.hregion.majorcompaction，
 *      0.2为hbase.hregion.majorcompaction.jitter，
 *
 *      可见默认在7天左右就会执行一次Major Compaction。
 *
 *      用户如果想禁用Major Compaction，需要将参数hbase.hregion.majorcompaction设为0。
 *
 *      •手动触发：一般来讲，手动触发Compaction大多是为了执行MajorCompaction。
 *      原因通常有三个
 *      ——
 *      其一，因为很多业务担心自动Major Compaction影响读写性能，因此会选择低峰期手动触发；
 *      其二，用户在执行完alter操作之后希望立刻生效，手动触发Major Compaction；
 *      其三，HBase管理员发现硬盘容量不够时手动触发Major Compaction，删除大量过期数据。
 *
 *  7.1.3 待合并HFile集合选择策略
 *      选择合适的文件进行合并是整个Compaction的核心，因为合并文件的大小
 *      及其当前承载的IO数直接决定了Compaction的效果以及对整个系统其他业务的影响程度。
 *
 *  7.1.4 挑选合适的执行线程池
 *      HBase实现中有一个专门的类CompactSplitThead负责接收Compaction请求和split请求
 *      个类内部构造了多个线程池：
 *      largeCompactions、smallCompactions以及splits等
 *      splits线程池负责处理所有的split请求，
 *      largeCompactions用来处理大Compaction，
 *      smallCompaction负责处理小Compaction
 *
 *   7.1.5 HFile文件合并执行
 *   1）分别读出待合并HFile文件的KeyValue，进行归并排序处理，
 *   之后写到./tmp目录下的临时文件中。
 *   2）将临时文件移动到对应Store的数据目录。
 *   3）将Compaction的输入文件路径和输出文件路径封装为KV写入HLog日志，
 *   并打上Compaction标记，最后强制执行sync。
 *   4）将对应Store数据目录下的Compaction输入文件全部删除。
 *
 *      上述4个步骤看起来简单，但实际是很严谨的，具有很强的容错性和幂等性：
 *      •如果RegionServer在步骤2）之前发生异常，
 *      本次Compaction会被认定为失败，如果继续进行同样的Compaction，
 *      上次异常对接下来的Compaction不会有任何影响，
 *      也不会对读写有任何影响。唯一的影响就是多了一份多余的数据。
 *
 *      •如果RegionServer在步骤2）之后、步骤3）之前发生异常，同样，仅仅会多一份冗余数据
 *
 *      •如果在步骤3）之后、步骤4）之前发生异常，
 *  *      RegionServer在重新打开Region之后首先会从HLog中看到标有Compaction的日志，
 *  *      因为此时输入文件和输出文件已经持久化到HDFS，
 *  *      因此只需要根据HLog移除Compaction输入文件即可。
 *
 *
 *
 *
 *
 *
 */
public class Compaction实现 {
}
