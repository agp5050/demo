package com.agp.demo.hbase;

/**
 * HBase体系结构：
 * *鉴了BigTable论文，是典型的Master-Slave模型
 * 系统中有一个管理集群的Master节点以及大量实际服务用户读写的RegionServer节点
 * HBase中所有数据最终都存储在HDFS系统中
 * 系统中还有一个ZooKeeper节点，协助Master对集群进行管理
 *
 * HBase客户端访问数据行之前：
 * 1、首先需要通过 元数据表 定位 目标数据 所在RegionServer
     * 之后才会发送请求到该RegionServer
 * 同时这些元数据会被缓存在客户端本地，以方便之后的请求访问
     * 如果集群RegionServer发生宕机或者执行了负载均衡等，从而导致数据分片发生迁移，
     * 客户端需要重新请求最新的元数据并缓存在本地。
 *
 * ZOOKEEPER：
 * 实现Master高可用：系统中只有一个Master工作，一旦ActiveMaster由于异常宕机，
 *  ZooKeeper会检测到该宕机事件，
 *  并通过一定机制选举出新的Master
 *管理系统核心元数据:
 *  管理当前系统中正常工作的RegionServer集合
 *  保存系统元数据表hbase:meta所在的RegionServer地址
 *参与RegionServer宕机恢复:
 *  ZooKeeper通过心跳可以感知到RegionServer是否宕机 并在宕机后通知Master进行宕机处理
 *实现分布式表锁：
 *  HBase中对一张表进行各种管理操作（比如alter操作）需要先加表锁
 *  防止其他用户对同一张表进行管理操作，造成表状态不一致
 *  ZooKeeper可以通过特定机制实现分布式表锁
 *
 *3. Master：
 * Master主要负责HBase系统的各种管理工作：
 * •处理用户的各种管理请求，包括建表、修改表、权限操作、切分表、合并数据分片以及Compaction等。
 * •管理集群中所有RegionServer，包括RegionServer中Region的负载均衡、
 * RegionServer的宕机恢复以及Region的迁移等。
 * •清理过期日志以及文件，Master会每隔一段时间检查HDFS中HLog是否过期、
 * HFile是否已经被删除，并在过期之后将其删除。
 *
 * 4. RegionServer
 * RegionServer主要用来响应用户的IO请求，
 *
 *是HBase中最核心的模块，由WAL(HLog)、BlockCache以及多个Region构成
 *  WAL(HLog)：
 *      HBase数据随机写入时，并非直接写入HFile数据文件，
 *      而是先写入缓存，再异步刷新落盘。为了防止缓存数据丢失
 *      为了防止缓存数据丢失，数据写入缓存之前需要首先顺序写入HLog
 *
 *      用于实现HBase集群间主从复制，
 *      通过回放主集群推送过来的HLog日志实现主从复制
 *  BlockCache：
 *      HBase系统中的读缓存，
 *      客户端从磁盘读取数据之后通常会将数据缓存到 系统内存 中，
 *      后续访问 同一行数据 可以直接从内存中获取而不需要访问磁盘。
 *
 *大量热点读业务请求 缓存机制会带来极大的性能提升
 *      BlockCache缓存对象是一系列Block块，一个Block默认为64K，
 *      由物理上相邻的多个KV数据组成
 *      BlockCache主要有两种实现——LRUBlockCache和BucketCache
 *
 *Table                    (HBase table)
 *     Region               (Regions for the table)
 *         Store            (Store per ColumnFamily for each Region for the table)
 *             MemStore     (MemStore for each Store for each Region for the table)
 *             StoreFile    (StoreFiles for each Store for each Region for the table)
 *                 Block    (Blocks within a StoreFile within a Store for each Region for the table)
 *
 *
 *
 */
public class Hbase体系结构 {
}
