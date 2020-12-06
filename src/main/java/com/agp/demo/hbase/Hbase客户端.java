package com.agp.demo.hbase;

/**
 * 从HBase客户端到HBase服务端，再到HDFS客户端，最后到HDFS服务端，这是一整条路径，其
 * 中任何一个环节出现问题，都会影响业务的可用性并造成延迟。
 *
 * HBase提供了面向Java、C/C++、Python等多种语言的客户端
 * 另外，HBase也支持Shell交互式客户端。Shell客户端实质是用JRuby
 * （用Java编写的Ruby解释器，方便Ruby脚本跑在JVM虚拟机上）
 * 脚本调用官方HBase客户端来实现的。
 *
 * 因此，各种客户端的核心实现都在社区Java版本客户端上。
 *
 *
 * 步骤1：获取集群的Conf iguration对象。
 * 对访问HBase集群的客户端来说，
 * 一般需要3个配置文件：hbase-site.xml、core-site. xml、hdfs-site.xml。
 * 只需把这3个配置文件放到JVM能加载的classpath下即可，
 * 然后通过如下代码即可加载到Conf iguration对象：
 * Configuration config=HbaseConfiguration.create();
 * Connection connection=ConnectionFactory.createConnection(config)
 * Table table=connection.getTable(TableName.valueOf("test"))
 * rowKey=Bytes.toBytes("asdfa")
 * columnFamily=Bytes.toBytes("cf")
 * qualifier=Bytes.toBytes("name")
 * value=Bytes.toBytes("abc")
 * table.put(new Put(rowKey).addColumn(columnFamily,qualifier,value)
 *
 * 步骤2：通过Conf iguration初始化集群Connection。
 * Connection是HBase客户端进行一切操作的基础，
 * 它维持了客户端到整个HBase集群的连接，
 * 例如一个HBase集群中有2个Master、5个RegionServer，那么一般来说，
 * 这个Connection会维持一个到Active Master的TCP连接和5个到RegionServer的TCP连接。
 *
 *Connection还缓存了访问的Meta信息，
 * 这样后续的大部分请求都可以通过缓存的Meta信息定位到对应的RegionServer。
 *
 * 步骤3：通过Connection初始化Table。
 * Table是一个非常轻量级的对象，它实现了用户访问表的所有API操作，例如Put、Get、Delete、Scan等。
 * 本质上，它所使用的连接资源、配置信息、线程池、Meta缓存等，都来自步骤2创建的Connection对象
 * 。因此，由同一个Connection创建的多个Table，都会共享连接、配置信息、线程池、Meta缓存这些资源
 *
 * 4.1.1 定位Meta表
 *
 * HBase一张表的数据是由多个Region构成，而这些Region是分布在整个集群的RegionServer上的
 * 。那么客户端在做任何数据操作时，都要先确定数据在哪个Region上，
 * 然后再根据Region的RegionServer信息，去对应的RegionServer上读取数据。
 *
 * 因此，HBase系统内部设计了一张特殊的表——hbase:meta表
 * 专门用来存放整个集群所有的Region信息
 *
 * hbase:meta中的hbase指的是namespace
 *
 * HBase容许针对不同的业务设计不同的namespace，
 * 系统表采用统一的namespace，即hbase；meta指的是hbase这个namespace下的表名。
 *
 * 到hbase:meta表的结构如图
 *
 * hbase:meta表也是Hbase的一张表。结构非常简单，
 * 整个表只有一个名为info的ColumnFamily。
 * 而且HBase保证hbase:meta表始终只有一个Region，
 * 这是为了确保meta表多次操作的原子性，
 * 因为HBase本质上只支持Region级别的事务
 *
 * hbase:meta的一个rowkey就对应一个Region
 * rowkey主要由TableName（业务表名）、StartRow（业务表Region区间的起始rowkey）、
 * Timestamp（Region创建的时间戳）、EncodedName（上面3个字段的MD5Hex值）4个字段拼接而成
 *
 * 每一行数据又分为4列，
 * 分别是info:regioninfo、info:seqnumDuringOpen
 * 、info:server、info:serverstartcode
 *
 * info:regioninfo：该列对应的Value主要存储4个信息，
 * 即EncodedName、RegionName、Region的StartRow、Region的StopRow
 *
 * • info:seqnumDuringOpen：该列对应的Value主要存储Region打开时的sequenceId。
 *
 * • info:server：该列对应的Value主要
 *      存储Region落在哪个RegionServer  上。
 *  info:serverstartcode：该列对应的Value主要存储所在RegionServer的启动Timestamp。
 *
 * 理解了hbase:meta表的基本信息后，就可以根据rowkey来查找业务的Region了。例如，
 * 现在需要查找micloud:note表中rowkey='userid334452'所在的Region，可以设计如下查询语句
 *
 *
 *
 *
 *
 */
public class Hbase客户端 {
    /*
    * • table.put(put)：这是最常见的单行数据写入API，
    * 在服务端先写WAL，然后写MemStore，一旦MemStore写满就f lush到磁盘上。
    * 这种写入方式的特点是，默认每次写入都需要执行一次RPC和磁盘持久化。
    * 因此，写入吞吐量受限于磁盘带宽、网络带宽以及f lush的速度。
    * 但是，它能保证每次写入操作都持久化到磁盘，不会有任何数据丢失。
    * 最重要的是，它能保证put操作的原子性。
    *
    * • table.put(List<Put> puts)：HBase还提供了批量写入的接口，
    * 即在客户端缓存put，等凑足了一批put，就将这些数据打包成一次RPC发送到服务端，
    * 一次性写WAL，并写MemStore。相比第一种方式，
    * 此方法省去了多次往返RPC以及多次刷盘的开销，吞吐量大大提升。
    * 不过，这个RPC操作耗时一般都会长一点，因为一次写入了多行数据。
    * 另外，如果List<put>内的put分布在多个Region内，则不能保证这一批put的原子性，
    * 因为HBase并不提供跨Region的多行事务，换句话说，这些put中，可能有一部分失败，
    * 一部分成功，失败的那些put操作会经历若干次重试。
    *
    * • bulk load：通过HBase提供的工具直接将待写入数据生成HFile，
    * 将这些HFile直接加载到对应的Region下的CF内。
    *
    * bulk load应该是最快的批量写手段，
    *
    * 例如，我们之前碰到过一种情况，有两个集群，互为主备，
    * 其中一个集群由于工具bug导致数据缺失，想通过另一个备份集群的数据来修复异常集群。
    * 最快的方式就是，把备份集群的数据导一个快照拷贝到异常集群，
    * 然后通过CopyTable工具扫快照生成HFile，最后bulk load到异常集群，完成数据的修复
    *
    *
    *
    *
    * */
}
