package com.agp.demo.hbase;

/**
 * RegionServer是HBase系统中最核心的组件，主要负责用户数据写入、
 * 读取等基础操作。RegionServer组件实际上是一个综合体系，
 * 包含多个各司其职的核心模块：HLog、MemStore、HFile以及BlockCache。
 *
 *一个RegionServer由一个（或多个）HLog、一个BlockCache以及多个Region组成。
 * 其中，HLog用来保证数据写入的可靠性；
 * BlockCache可以将数据块缓存在内存中以提升数据读取性能
 *
 * Region是HBase中数据表的一个数据分片，
 * 一个RegionServer上通常会负责多个Region的数据读写。
 *
 * 一个Region由多个Store组成，每个Store存放对应列簇的数据
 * Store个数==列簇个数
 * 每个Store包含一个MemStore和多个HFile
 * 一旦写入数据的内存大小超过设定阈值，系统就会将MemStore中的数据落盘形成HFile文件
 *
 * HFile存放在HDFS上，是一种定制化格式的数据存储文件，方便用户进行数据读取
 *
 *
 * 5.2 HLog
 *
 * HBase中系统故障恢复以及主从复制都基于HLog实现。
 * 默认情况下，所有写入操作（写入、更新以及删除）的数据都先以追加形式写入HLog，再写入MemStore。
 *
 * 大多数情况下，HLog并不会被读取，但如果RegionServer在某些异常情况下发生宕机，
 * 此时已经写入MemStore中但尚未flush到磁盘的数据就会丢失，需要回放HLog补救丢失的数据。
 *
 * HBase主从复制需要主集群将HLog日志发送给从集群，
 * 从集群在本地执行回放操作，完成集群之间的数据复制。
 *
 * •每个RegionServer拥有一个或多个HLog
 * （默认只有1个，1.1版本可以开启MultiWAL功能，允许多个HLog）。
 * 每个HLog是多个Region共享的，图5-2中Region A、Region B和Region C共享一个HLog文件。
 *
 * HLog生命周期包含4个阶段：
 * 1）HLog构建：HBase的任何写入（更新、删除）操作都会先将记录追加写入到HLog文件中。
 * 2）HLog滚动：HBase后台启动一个线程，
 * 每隔一段时间（由参数'hbase.regionserver. logroll.period'决定，默认1小时）进行日志滚动
 *日志滚动会新建一个新的日志文件，接收新的日志数据。
 * 日志滚动机制主要是为了方便过期日志数据能够以文件的形式直接删除。
 * 3）HLog失效：写入数据一旦从MemStore中落盘，对应的日志数据就会失效。
 * 为了方便处理，HBase中日志失效删除总是以文件为单位执行。
 * 一旦日志文件失效，就会从WALs文件夹移动到oldWALs文件夹。注意此时HLog并没有被系统删除。
 *
 * 4）HLog删除：Master后台会启动一个线程，每隔一段时间
 * （参数'hbase.master.cleaner. interval'，默认1分钟）
 * 检查一次文件夹oldWALs下的所有失效日志文件，确认是否可以删除，
 * 确认可以删除之后执行删除操作。确认条件主要有两个：
 *
 * 该HLog文件是否还在参与主从复制。对于使用HLog进行主从复制的业务，
 * 需要继续确认是否该HLog还在应用于主从复制。
 *
 * 该HLog文件是否已经在OldWALs目录中存在10分钟。
 * 为了更加灵活地管理HLog生命周期，系统提供了参数设置日志文件的
 * TTL（参数'hbase.master.logcleaner.ttl'，默认10分钟），
 * 默认情况下oldWALs里面的HLog文件最多可以再保存10分钟。
 *
 *
 */
public class RegionServer核心模块 {
}
