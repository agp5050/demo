package com.agp.demo.hbase;

/**
 * HDFS本质上是一个分布式文件系统，可以部署在大量价格低廉的服务器上，
 * 提供可扩展的、高容错性的文件读写服务。
 * HBase项目本身并不负责文件层面的高可用和扩展性，
 *它通过把数据存储在HDFS上来实现大容量文件存储和文件备份。
 *
 * HDFS擅长的场景是大文件的顺序读、随机读和顺序写
 *
 * 从API层面，HDFS并
 * 不支持文件的随机写（Seek+Write）以及多个客户端同时写同一个文件。
 * 正是由于HDFS的这些优点和缺点，深刻地影响了HBase的设计。
 *
 *
 * 高可用HDFS集群主要由4个重要的服务组成：
 * NameNode、DataNode、JournalNode、ZkFailoverController
 *
 * 存储在HDFS上面的文件实际上是由若干个数据块（Block，大小默认为128MB)组成，
 * 每一个Block会设定一个副本数N
 * 注意读取时只需选择N个副本中的任何一个副本进行读取即可。
 * 1）NameNode
 * 线上需要部署2个NameNode ：一个节点是Active状态并对外提供服务；另一个节点是StandBy状态
 * 如果ZkFailoverController服务检测到Active状态的节点发生异常，
 * 会自动把StandBy状态的NameNode服务切换成Active的NameNode
 *
 * NameNode存储并管理HDFS的文件元数据
 *
 * 这些元数据主要包括文件属性（文件大小、文件拥有者、组
 * 以及各个用户的文件访问权限等以及文件的多个数据块分布在哪些存储节点上
 *
 *因此NameNode本质上是一个独立的维护所有文件元数据的高可用KV数据库系统
 *
 * 为了保证每一次文件元数据都不丢失，
 * NameNode采用写EditLog和FsImage的方式来保证元数据的高效持久化。
 * 每一次文件元数据的写入，
 * 都是先做一次EditLog的顺序写，然后再修改NameNode的内存状态
 *同时NameNode会有一个内部线程，周期性地把内存状态导出到本地磁盘持久化成FsImage
 *
 * （假设导出FsImage的时间点为t），那么对于小于时间点t的EditLog都认为是过期状态，
 * 是可以清理的，这个过程叫做推进checkpoint
 *
 * 注意:
 * NameNode会把所有文件的元数据全部维护在内存中。因此，如果在HDFS中存放大量的小文件，
 * 则造成分配大量的Block，这样可能耗尽NameNode所有内存而导致OOM
 *
 * 2）DataNode
 *
 * 组成文件的所有Block都是存放在DataNode节点上的。
 * 一个逻辑上的Block会存放在N个不同的DataNode上
 * 而NameNode、JournalNode、ZKFailoverController服务都是用来维护文件元数据的
 *
 * （3）JournaNode
 *
 * 由于NameNode是Active-Standby方式的高可用模型，
 * 且NameNode在本地写EditLog，
 * 那么存在一个问题——在StandBy状态下的NameNode切换成Active状态后，
 * 如何才能保证新Active的NameNode和切换前Active状态的NameNode拥有完全一致的数据
 *
 * 保证两个NameNode在切换前后能读到一致的EditLog，
 * HDFS单独实现了一个叫做JournalNode的服务。
 * 线上集群一般部署奇数个JournalNode（一般是3个，或者5个），
 * 在这些JournalNode内部，通过Paxos协议来保证数据一致性。
 * 因此可以认为，JournalNode其实就是用来维护EditLog一致性的Paxos组
 *
 *
 *
 *
 *
 * 。
 */
public class HbaseHadoop {
}
