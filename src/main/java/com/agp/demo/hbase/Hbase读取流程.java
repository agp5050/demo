package com.agp.demo.hbase;

/**
 * HBase读数据的流程更加复杂。主要基于两个方面的原因：
 * 一是因为HBase一次范围查询
 * 可能会涉及多个Region、多块缓存甚至多个数据存储文件；
 *
 * 二是因为HBase中更新操作以及删除操作的实现都很简单，
 * 更新操作并没有更新原有数据，而是使用时间戳属性实现了多版本；
 * 删除操作也并没有真正删除原有数据，只是插入了一条标记为"deleted"标签的数据，
 * 而真正的数据删除发生在系统异步执行Major Compact的时候。
 *
 * 这种实现思路大大简化了数据更新、删除流程，但是对于数据读取来说却意味着套上了层层枷锁：
 * 读取过程需要根据版本进行过滤，对已经标记删除的数据也要进行过滤。
 *
 * 读流程从头到尾可以分为如下4个步骤：
 *
 * Client-Server读取交互逻辑，
 * Server端Scan框架体系，
 * 过滤淘汰不符合查询条件的HFile，
 * 从HFile中读取待查找Key
 *
 * Client-Server读取交互逻辑：
 *
 * Client首先会从ZooKeeper中获取元数据hbase:meta表所在的RegionServer，
 * 然后根据待读写rowkey发送请求到元数据所在RegionServer，
 * 获取数据所在的目标RegionServer和Region（并将这部分元数据信息缓存到本地），
 * 最后将请求进行封装发送到目标RegionServer进行处理。
 *
 * 从API的角度看，HBase数据读取可以分为get和scan两类
 * scan请求通常根据给定的startkey和stopkey查找多行满足条件的记录
 * get请求也是一种scan请求（最简单的scan请求，scan的条数为1）
 * 从这个角度讲，所有读取操作都可以认为是一次scan操作。
 *
 * HBase Client端与Server端的scan操作 并没有 设计为一次RPC请求
 * 这是因为一次大规模的scan操作很有可能就是一次全表扫描，扫描结果非常之大
 * 通过一次RPC将大量扫描结果返回客户端会带来至少两个非常严重的后果：
 *  •大量数据传输会导致集群网络带宽等系统资源短时间被大量占用，严重影响集群中其他业务。
 *  •客户端很可能因为内存无法缓存这些数据而导致客户端OOM。
 *
 * 实际上HBase会根据设置条件将一次大的scan操作拆分为多个RPC请求，
 * 每个RPC请求称为一次next请求，
 * 每次只返回规定数量的结果。
 *
 *6.3.2 Server端Scan框架体系
 *
 * 一次scan可能会同时扫描一张表的多个Region，对于这种扫描，
 * 客户端会根据hbase:meta元数据将扫描的起始区间[startKey, stopKey)进行切分，
 * 切分成多个互相独立的查询子区间，每个子区间对应一个Region
 *
 * 比如当前表有3个Region，Region的起始区间分别为：["a", "c")，["c", "e")，["e","g")，
 * 客户端设置scan的扫描区间为["b", "f")。因为扫描区间明显跨越了多个Region，需要进行切分，
 * 按照Region区间切分后的子区间为["b", "c")，["c","e")，["e", "f ")。
 *
 * HBase中每个Region都是一个独立的存储引擎，因此客户端可以将每个子区间请求分别发送给对应的Region进行处理。
 *
 * RegionServer接收到客户端的get/scan请求之后做了两件事情：
 *      首先构建scanneriterator体系；
 *      然后执行next函数获取KeyValue，并对其进行条件过滤。
 *Scanner的核心体系包括三层Scanner
 *      RegionScanner
 *      StoreScanner
 *      MemStoreScanner和StoreFileScanner
 * 层级的关系:
 * •一个RegionScanner由多个StoreScanner构成。一张表由多少个列簇组成，就有多少个StoreScanner，
 * 每个StoreScanner负责对应Store的数据查找。
 *
 *•一个StoreScanner由MemStoreScanner和StoreFileScanner构成。
 *  每个Store的数据由内存中的MemStore和磁盘上的StoreFile文件组成
 *  StoreScanner会为当前该Store中 每个 HFile构造一个StoreFileScanner
 *  会为对应MemStore构造一个MemStoreScanner，用于执行该Store中MemStore的数据检索。
 *
 *RegionScanner以及StoreScanner并不负责实际查找操作，它们更多地承担组织调度任务，
 * 负责KeyValue最终查找操作的是StoreFileScanner和MemStoreScanner。
 *
 *
 *Scanner工作流程
 * 1）过滤淘汰部分不满足查询条件的Scanner
 *主要过滤策略有：
 *      Time Range过滤、
 *          StoreFile中元数据有一个关于该File的TimeRange属性[ miniTimestamp, maxTimestamp ]
 *          如果待检索的TimeRange与该文件时间范围没有交集，就可以过滤掉该StoreFile；
 *          另外，如果该文件所有数据已经过期，也可以过滤淘汰。
 *      Rowkey Range过滤
 *          KeyValue数据都是有序排列的，所以如果待检索row范围[ startrow，stoprow ]
 *          与文件起始key范围[ f irstkey，lastkey ]没有交集
 *      布隆过滤器
 *          根据待检索的rowkey获取对应的Bloom Block并加载到内存
 *          再用hash函数对待检索rowkey进行hash，根据hash后的结果在布隆过滤器数据中进行寻址，
 *          即可确定待检索rowkey是否一定不存在于该HFile
 *
 *2）每个Scanner seek到startKey。这个步骤在每个HFile文件中（或MemStore）中seek扫描起始点startKey。
 *
 * 3）KeyValueScanner合并构建最小堆。
 * 将该Store中的所有StoreFileScanner和MemStoreScanner结果合并形成一个heap（最小堆）
 *
 *
 * 2. 执行next函数获取KeyValue并对其进行条件过滤
 *  经过Scanner体系的构建，KeyValue此时已经可以由小到大依次经过KeyValueScanner获得
 *  但这些KeyValue是否满足用户设定的TimeRange条件、版本号条件以及Filter条件还需要进一步的检查。检查规则如下
 *  1）检查该KeyValue的KeyType是否是Deleted/DeletedColumn/DeleteFamily等，
 *  如果是，则直接忽略该列所有其他版本，跳到下列（列簇）
 *  2）检查该KeyValue的Timestamp是否在用户设定的Timestamp Range范围，如果不在该范围，忽略。
 *  3）检查该KeyValue是否满足用户设置的各种f ilter过滤器，如果不满足，忽略。
 *  4）检查该KeyValue是否满足用户查询中设定的版本数，比如用户只查询最新版本，则忽略该列的其他版本；
 *  反之，如果用户查询所有版本，则还需要查询该cell的其他版本。
 *
 *
 *6.3.4 从HFile中读取待查找Key
 *  1. 根据HFile索引树定位目标Block
 *  HRegionServer打开HFile时会将所有HFile的Trailer部分和Load-on-open部分加载到内存，
 *  Load-on-open部分有个非常重要的Block——Root Index Block，即索引树的根节点
 *      HFile中文件索引 Index Entry，由BlockKey、Block Offset、BlockDataSize三个字段组成
 *
 *  箭头表示一次查询的索引过程，基本流程可以表示为：
 *
 *  1）用户输入rowkey为'fb'，在Root Index Block中通过二分查找定位到'fb'在'a'和'm'之间
 *  因此需要访问索引'a'指向的中间节点
 *
 *  2）将索引'a'指向的中间节点索引块加载到内存，
 *  然后通过二分查找定位到fb在index 'd'和'h'之间，接下来访问索引'd'指向的叶子节点
 *
 *  3）同理，将索引'd'指向的中间节点索引块加载到内存，
 *  通过二分查找定位找到fb在index 'f'和'g'之间，最后需要访问索引'f'指向的Data Block节点。
 *
 *  4）将索引'f'指向的Data Block加载到内存，通过遍历的方式找到对应KeyValue。
 *
 *
 *
 *
 *  2. BlockCache中检索目标Block
 *
 *  3. HDFS文件中检索目标Block
 *      使用FSDataInputStream读取HFile中的数据块，命令下发到HDFS，
 *      首先会联系NameNode组件。NameNode组件会做两件事情：
 *      找到属于这个HFile的所有HDFSBlock列表，确认待查找数据在哪个HDFSBlock上
 *      HDFS会将一个给定文件切分为多个大小等于128M的Data Block，
 *      NameNode上会存储数据文件与这些HDFSBlock的对应关系。
 *
 *      •确认定位到的HDFSBlock在哪些DataNode上，选择一个最优DataNode返回给客户端。
 *      HDFS将文件切分成多个HDFSBlock之后，
 *      采取一定的策略按照三副本原则将其分布在集群的不同节点，实现数据的高可靠存储
 *
 *  之后，HBase会再联系对应DataNode。DataNode首先找到指定HDFSBlock，
 *  seek到指定偏移量，并从磁盘读出指定大小的数据返回。
 *  DataNode读取数据实际上是向磁盘发送读取指令，
 * 磁盘接收到读取指令之后会移动磁头到给定位置，
 * 读取出完整的64K数据返回。
 *
 *4. 从Block中读取待查找KeyValueHFile Block由KeyValue（由小到大依次存储）构成，
 * 但这些KeyValue并不是固定长度的，只能遍历扫描查找。
 *
 *
 */
public class Hbase读取流程 {
}
