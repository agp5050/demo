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
 * HDFS写入流程：
 * 1）DFS Client在创建FSDataOutputStream时，把文件元数据发给NameNode，
 * 得到一个文件唯一标识的fileId，并向用户返回一个OutputStream。
 *2）用户拿到OutputStream之后，开始写数据。
 * 注意写数据都是按照Block来写的，不同的Block可能分布在不同的DataNode上，
 * 因此如果发现当前的Block已经写满，DFSClient就需要再发起请求向NameNode申请一个新的Block
 *在一个Block内部，数据由若干个Packet（默认64KB）组成，
 * 若当前的Packet写满了，就放入DataQueue队列，
 *
 * DataStreamer线程 异步 地把Packet写入到对应的DataNode。
 * 3个副本中的某个DataNode收到Packet之后，会先写本地文件，
 * 然后发送一份到第二个DataNode，第二个执行类似步骤后，发给第三个DataNode。
 *
 *等所有的DataNode都写完数据之后，
 * 就发送Packet的ACK给DFSClient，只有收到ACK的Packet才是写入成功的。
 *
 3）用户执行完写入操作后，需要关闭OutputStream
 关闭过程中，DFSClient会先把本地DataQueue中未发出去的Packet全部发送到DataNode。
 若忘记关闭，对那些已经成功缓存在DFS Client的DataQueue中但尚未成功写入DataNode的数据，
 将没有机会写入DataNode中。对用户来说，这部分数据将丢失。


 2）FSDataOutputStream中hflush和hsync的区别

 hflush成功返回，则表示DFSClient的DataQueue中所有Packet都已经成功发送到了3个DataNode上
 但是对每个DataNode而言，数据仍然可能存放在操作系统的Cache上，可能还没落盘。

 hsync成功返回，则表示DFSClient DataQueue中的Packet不但成功发送到了3个DataNode，
 而且每个DataNode上的数据都持久化（sync）到了磁盘上

 在小米内部大部分HBase集群上，综合考虑写入性能和数据可靠性两方面因素，
 我们选择使用默认的hf lush来保证WAL持久性。
 因为底层的HDFS集群已经保证了数据的三副本，
 并且每一个副本位于不同的机架上，而三个机架同时断电的概率极小。
 但是对那些依赖云服务的HBase集群来说，
 有可能没法保证副本落在不同机架，hsync是一个合理的选择



 *
 * HDFS读取流程：
 * 1）DFSClient请求NameNode，
 * 获取到对应read position的Block信息（包括该Block落在了哪些DataNode上）
 *
 * 2）DFSClient从Block对应的DataNode中选择一个合适的DataNode，
 * 对选中的DataNode创建一个BlockReader以进行数据读取
 *
 * HDFS读取流程很简单，但对HBase的读取性能影响重大，
 * 尤其是Locality和短路读这两个最为核心的因素。
 *
 *Locality:
 * 某些服务可能和DataNode部署在同一批机器上。
 * 因为DataNode本身需要消耗的内存资源和CPU资源都非常少，
 * 主要消耗网络带宽和磁盘资源。
 * 而HBase的RegionServer服务本身是内存和CPU消耗型服务，
 * 于是我们把RegionServer和DataNode部署在一批机器上。
 * 对某个DFSClient来说，一个文件在这台机器上的locality可以定义为：
 *
 * locality=该文件存储在本地机器的字节数之和 / 该文件总字节数
 *
 * 因此，locality是[0, 1]之间的一个数，locality越大，
 * 则读取的数据都在本地，无需走网络进行数据读取，性能就越好。反之，则性能越差
 *
 * （3）短路读（Short Circuit Read）：
 *
 * 短路读是指对那些Block落在和DFSClient同一台机器上的数据，
 * 可以不走TCP协议进行读取，
 * 而是直接由DFSClient向本机的DataNode请求对应Block的文件描述符（File Descriptor），
 * 然后创建一个BlockReaderLocal，
 * 通过fd进行数据读取，这样就节省了走本地TCP协议栈的开销。
 *
 * 测试数据表明，locality和短路读对HBase的读性能影响重大。
 * 在locality=1.0情况下，不开短路读的p99性能要比开短路读差10%左右。
 * 如果用locality=0和locality=1相比，读操作性能则差距巨大。
 *
 * 4. HDFS在HBase系统中扮演的角色
 * HBase使用HDFS存储所有数据文件，
 * 从HDFS的视角看，HBase就是它的客户端。
 * 这样的架构有几点需要说明：
 * •HBase本身并不存储文件，它只规定文件格式以及文件内容，
 * 实际文件存储由HDFS实现。
 * •HBase不提供机制保证存储数据的高可靠，
 * 数据的高可靠性由HDFS的多副本机制保证。
 * •HBase-HDFS体系是典型的计算存储分离架构。
 * 这种轻耦合架构的好处是，一方面可以非常方便地使用其他存储替代HDFS作为HBase的存储方案；
 * 另一方面对于云上服务来说，
 * 计算资源和存储资源可以独立扩容缩容，给云上用户带来了极大的便利。
 *
 *
 * HADOOP上主要的Hbase文件目录：
 * • .hbase-snapshot：snapshot文件存储目录。用户执行snapshot后，
 * 相关的snapshot元数据文件存储在该目录。
 *
 * • .tmp：临时文件目录，主要用于HBase表的创建和删除操作。
 * 表创建的时候首先会在tmp目录下执行，
 * 执行成功后再将tmp目录下的表信息移动到实际表目录下。
 * 表删除操作会将表目录移动到tmp目录下，
 * 一定时间过后再将tmp目录下的文件真正删除。
 *
 * • WALs：存储集群中所有RegionServer的HLog日志文件。
 * • archive：文件归档目录。这个目录主要会在以下几个场景下使用。
 * ○所有对HFile文件的删除操作都会将待删除文件临时放在该目录。
 * ○进行Snapshot或者升级时使用到的归档目录。
 * ○Compaction删除HFile的时候，也会把旧的HFile移动到这里。
 *
 * • data：存储集群中所有Region的HFile数据。HFile文件在data目录下的完整路径如下所示：
 *
 * /hbase/data/default/usertable/fa13562579a4c0ec84858f2c947e8723/family/105baeff31ed481cb708c65728965666
 * default表示命名空间，usertable为表名，fa13562579a4c0ec84858f2c947e8723为Region名称，
 * family为列簇名，105baeff31ed481cb708c65728965666为HFile文件名。
 * 一个region里面如果有多个family 就有多个子文件夹。
 *
 * Data目录下重要的子目录：
 * ○ .tabledesc：表描述文件，记录对应表的基本schema信息。
 * ○ .tmp：表临时目录，主要用来存储Flush和Compaction过程中的中间结果。
 * 以f lush为例，MemStore中的KV数据落盘形成HFile首先会生成在.tmp目录下，
 * 一旦完成再从.tmp目录移动到对应的实际文件目录。
 *
 * ○ .regioninfo：Region描述文件。
 *
 * ○ recovered.edits：存储故障恢复时该Region需要回放的WAL日志数据。
 * RegionServer宕机之后，该节点上还没有来得及f lush到磁盘的数据需要通过WAL回放恢复，
 * WAL文件首先需要按照Region进行切分，每个Region拥有对应的WAL数据片段，
 * 回放时只需要回放自己的WAL数据片段即可。
 *
 *
 *
 *
 * Region:
 * table在行的方向上分隔为多个Region
 * Region是HBase中分布式存储和负载均衡的最小单元，
 * 即不同的region可以分别在不同的Region Server上，
 * 但同一个Region是不会拆分到多个server上。
 *
 * Region按大小分隔，表中每一行只能属于一个region。随着数据不断插入表，region不断增大，
 * 当region的某个列族达到一个阈值（默认256M）时就会分成两个新的region。
 *
 * 2. Store:
 *
 * 每一个region有一个或多个store组成，至少是一个store
 * hbase会把一起访问的数据放在一个store里面，
 * 即为每个ColumnFamily建一个store（即有几个ColumnFamily，也就有几个Store）
 *一个Store由一个memStore和0或多个StoreFile组成。
 * HBase以store的大小来判断是否需要切分region。
 *
 * 3. MemStore
 * memStore 是放在内存里的。保存修改的数据即keyValues。
 * 当memStore的大小达到一个阀值（默认64MB）时，memStore会被flush到文件，即生成一个快照。
 * 目前hbase 会有一个线程来负责memStore的flush操作。
 *
 * 4. StoreFile
 * memStore内存中的数据写到文件后就是StoreFile
 * （即memstore的每次flush操作都会生成一个新的StoreFile），
 * 所以每个MemStore对应的StoreFile是多个。
 * StoreFile底层是以HFile的格式保存。
 * 5. HFile
 * HFile是HBase中KeyValue数据的存储格式，是hadoop的二进制格式文件。
 * 一个StoreFile对应着一个HFile。而HFile是存储在HDFS之上的。
 * HFile文件格式是基于Google Bigtable中的SSTable，如下图所示：
 *
 *
 *
 *
 *
 *
 */
public class HbaseHadoop {
}
