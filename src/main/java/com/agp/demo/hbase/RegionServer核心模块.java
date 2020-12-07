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
 * HBase系统中一张表会被水平切分成多个Region，
 * 每个Region负责自己区域的数据读写请求。
 * 水平切分意味着每个Region会包含所有的列簇数据，
 * HBase将不同列簇的数据存储在不同的Store中，
 * 每个Store由一个MemStore和一系列HFile组成，如图5-4所示。
 *
 * HBase基于LSM树模型实现，所有的数据写入操作首先会顺序写入日志HLog，再写入MemStore，
 * 当MemStore中数据大小超过阈值之后再将这些数据批量写入磁盘，生成一个新的HFile文件
 *
 *
 * LSM树架构有如下几个非常明显的优势：
 * 这种写入方式将一次随机IO写入转换成一个顺序IO写入
 * （HLog顺序写入）加上一次内存写入（MemStore写入），
 * 使得写入性能得到极大提升。大数据领域中对写入性能有较高要求的数据库系统
 * 几乎都会采用这种写入模型，
 * 比如分布式列式存储系统Kudu、时间序列存储系统Druid等。
 *
 * HFile中KeyValue数据需要按照Key排序，排序之后可以在文件级别根据有序的Key建立索引树，
 * 极大提升数据读取效率。然而HDFS本身只允许顺序读写，不能更新，
 * 因此需要数据在落盘生成HFile之前就完成排序工作
 * ，MemStore就是KeyValue数据排序的实际执行者。
 *
 * •MemStore作为一个缓存级的存储组件，总是缓存着最近写入的数据。
 * 对于很多业务来说，最新写入的数据被读取的概率会更大，
 * 最典型的比如时序数据，80%的请求都会落到最近一天的数据上。
 * 实际上对于某些场景，新写入的数据存储在MemStore对读取性能的提升至关重要。
 *
 * 在数据写入HFile之前，可以在内存中对KeyValue数据进行很多更高级的优化。
 * 比如，如果业务数据保留版本仅设置为1，在业务更新比较频繁的场景下，
 * MemStore中可能会存储某些数据的多个版本。
 * 这样，MemStore在将数据写入HFile之前实际上可以丢弃老版本数据，
 * 仅保留最新版本数据。
 *
 * 5.3.1 MemStore内部结构
 * ，HBase并没有直接使用原始跳跃表，而是使用了JDK自带的数据结构ConcurrentSkipListMap。
 * ConcurrentSkipListMap底层使用跳跃表来保证数据的有序性，
 * 并保证数据的写入、查找、删除操作都可以在O(logN)的时间复杂度完成。
 *
 * 除此之外，ConcurrentSkipListMap有个非常重要的特点是线程安全，它在底层采用了CAS原子性操作，
 * 避免了多线程访问条件下昂贵的锁开销，极大地提升了多线程访问场景下的读写性能。
 *
 * MemStore由两个ConcurrentSkipListMap（称为A和B）实现，
 * 写入操作（包括更新删除操作）会将数据写入ConcurrentSkipListMap A，
 * 当ConcurrentSkipListMap A中数据量超过一定阈值之后
 * 会创建一个新的ConcurrentSkipListMap B来接收用户新的请求，
 * 之前已经写满的ConcurrentSkipListMap A会执行异步flush操作落盘形成HFile
 *
 * 5.3.2 MemStore的GC问题
 *
 * MemStore从本质上来看就是一块缓存，可以称为写缓存。
 * 众所周知在Java系统中，大内存系统总会面临GC问题，MemStore本身会占用大量内存，
 * 因此GC的问题不可避免。不仅如此，HBase中MemStore工作模式的特殊性更会引起严重的内存碎片，
 * 存在大量内存碎片会导致系统看起来似乎还有很多空间，但实际上这些空间都是一些非常小的碎片，
 * 已经分配不出一块完整的可用内存，这时会触发长时间的Full GC。
 *
 * 为什么MemStore的工作模式会引起严重的内存碎片？
 * 这是因为一个RegionServer由多个Region构成，
 * 每个Region根据列簇的不同又包含多个MemStore，这些MemStore都是共享内存的。
 * 这样，不同Region的数据写入对应的MemStore，
 * 因为共享内存，在JVM看来所有MemStore的数据都是混合在一起写入Heap的。
 *
 * MemStore flush引起的内存碎片.png
 * 。从JVM全局的视角来看，随着MemStore中数据的不断写入并且flush，
 * 整个JVM将会产生大量越来越小的内存条带，这些条带实际上就是内存碎片。
 * 随着内存碎片越来越小，最后甚至分配不出来足够大的内存给写入的对象，
 * 此时就会触发JVM执行Full GC合并这些内存碎片。
 *
 * 5.3.3 MSLAB内存管理方式
 *
 * 为了优化这种内存碎片可能导致的Full GC，
 * HBase借鉴了线程本地分配缓存（Thread-Local Allocation Buffer，TLAB）
 * 的内存管理方式，通过顺序化分配内存、
 * 内存数据分块等特性使得内存碎片更加粗粒度，有效改善Full GC情况。
 *
 * 实现步骤如下：1）每个MemStore会实例化得到一个MemStoreLAB对象。
 * 2）MemStoreLAB会申请一个2M大小的Chunk数组，同时维护一个Chunk偏移量，该偏移量初始值为0。
 * 3）当一个KeyValue值插入MemStore后，
 * MemStoreLAB会首先通过KeyValue.getBuffer()取得data数组，
 * 并将data数组复制到Chunk数组中，之后再将Chunk偏移量往前移动data. length。
 * 4）当前Chunk满了之后，再调用new byte[2 * 1024 * 1024]申请一个新的Chunk。
 *
 * 这种内存管理方式称为MemStore本地分配缓存（MemStore-Local AllocationBuffer，MSLAB）。
 * 图5-6是针对MSLAB的一个简单示意图，右侧为JVM中MemStore所占用的内存图，
 * 和优化前不同的是，不同颜色的细条带会聚集在一起形成了2M大小的粗条带。
 * 这是因为MemStore会在将数据写入内存时首先申请2M的Chunk，
 * 再将实际数据写入申请的Chunk中。这种内存管理方式，
 * 使得flush之后残留的内存碎片更加粗粒度，极大降低Full GC的触发频率。
 *
 *官方做了一个测试实验。这个实验主要查看在相同写入负载下开启
 * MSLAB前后RegionServer内存中最大内存碎片的大小。
 *
 * -xx:PrintFLSStatistics=1，可以打印每次GC前后内存碎片的统计信息
 *
 * Free Space表示老年代当前空闲的总内存容量，
 * MaxChunk Size表示老年代中最大的内存碎片所占的内存容量大小，
 * Num Chunks表示老年代中总的内存碎片数。
 * 该实验重点关注Max Chunk Size这个维度信息。
 *
 * 未开启MSLAB功能时内存碎片会大量出现，并导致频繁的Full GC  0---->MSLAB开启前后MaxChunkSize变化.png
 * （图中曲线每次出现波谷到波峰的剧变实际上就是一次Full GC，
 * 因为FullGC次数会重新整理内存碎片使得Max Chunk Size重新变大）；
 * 而优化后虽然依然会产生大量碎片，
 * 但是最大碎片大小一直会维持在1e+08左右，并没有出现频繁的Full GC。
 *
 * 5.3.4 MemStore Chunk Pool
 *
 * 系统因为MemStore内存碎片触发的Full GC次数会明显降低。
 * 然而这样的内存管理模式并不完美，还存在一些“小问题
 * 比如一旦一个Chunk写满之后，系统会重新申请一个新的Chunk，
 * 新建Chunk对象会在JVM新生代申请新内存，
 * 如果申请比较频繁会导致JVM新生代Eden区满掉，触发YGC。
 *
 * 试想如果这些Chunk能够被循环利用，系统就不需要申请新的Chunk，
 * 这样就会使得YGC频率降低，晋升到老年代的Chunk就会减少，
 * CMS GC发生的频率也会降低。
 *
 * 这就是MemStore Chunk Pool的核心思想，具体实现步骤如下:
 *
 * 1）系统创建一个Chunk Pool来管理所有未被引用的Chunk，
 * 这些Chunk就不会再被JVM当作垃圾回收。
 * 2）如果一个Chunk没有再被引用，将其放入Chunk Pool。
 * 3）如果当前Chunk Pool已经达到了容量最大值，就不会再接纳新的Chunk。
 * 4）如果需要申请新的Chunk来存储KeyValue，首先从Chunk Pool中获取，
 * 如果能够获取得到就重复利用，否则就重新申请一个新的Chunk。
 *

 *
 * HBase中MSLAB功能默认是开启的，默认的ChunkSize是2M，也可以通过参数"hbase.hregion.memstore.mslab.chunksize"进行设置，
 * 建议保持默认值
 * Chunk Pool功能默认是关闭的，需要配置参数
 * 数"hbase.hregion.memstore.chunkpool.maxsize"为大于0的值才能开启，
 * 该值默认是0。"hbase.hregion.memstore.chunkpool.maxsize"取值为[0, 1]，
 * 表示整个MemStore分配给Chunk Pool的
 * 总大小为hbase.hregion.memstore.chunkpool. maxsize * Memstore Size。
 * 另一个相关参数"hbase.hregion.memstore.chunkpool.initialsize"取值为[0, 1]，
 * 表示初始化时申请多少个Chunk放到Pool里面，默认是0，表示初始化时不申请内存。
 *

 *
 * HFile逻辑结构:
 *
 * 主要分为4个部分：Scanned block部分、Non-scanned block部分、
 * Load-on-open部分和Trailer。
 *
 * •Scanned Block部分：顾名思义，表示顺序扫描HFile时所有的数据块将会被读取。这个部分包含3种数据块
 * ：Data Block，Leaf Index Block以及BloomBlock。
 * Data Block数据
 * Leaf Index Block储索引树的叶子节点数据offset等索引信息
 * Bloom Block中存储布隆过滤器相关数据。
 *
 * Non-scanned Block部分：表示在HFile顺序扫描的时候数据不会被读取，
 * 主要包括Meta Block和Intermediate Level Data Index Blocks两部分。
 *
 * Load-on-open部分：这部分数据会在RegionServer打开HFile时直接加载到内存中
 *包括FileInfo、布隆过滤器MetaBlock、Root Data Index和MetaIndexBlock。
 *
 *•Trailer部分：这部分主要记录了HFile的版本信息、其他各个部分的偏移值和寻址信息
 *
 *整个HFile中仅有一个Bloom Index Block数据块，位于load-on-open部分。
 * Bloom Index Block（见图5-13左侧部分）从大的方面看由两部分内容构成，
 * 其一是HFile中布隆过滤器的元数据基本信息，
 * 其二是构建了指向Bloom Block的索引信息。
 *
 * Bloom Index Block结构中TotalByteSize表示位数组大小
 *
 * NumChunks表示Bloom Block的个数，
 * HashCount表示hash函数的个数，HashType表示hash函数的类型，
 *
 * TotalKeyCount表示布隆过滤器当前已经包含的Key的数目
 *
 * TotalMaxKeys表示布隆过滤器当前最多包含的Key的数目。
 *
 * Bloom Index Entry对应每一个Bloom Block的索引项，
 * 作为索引分别指向scanned block部分的Bloom Block，
 * Bloom Block中实际存储了对应的位数组。
 * Bloom Index Entry的结构见图5-13中间部分，
 * 其中BlockKey是一个非常关键的字段，
 * 表示该Index Entry指向的Bloom Block中第一个执行Hash映射的Key。
 * BlockOffset表示对应Bloom Block在HFile中的偏移量。
 *
 * * 因此，一次get请求根据布隆过滤器进行过滤查找需要执行以下三步操作：
 *  * 1）首先根据待查找Key在Bloom Index Block所有的索引项中根据BlockKey进行二分查找，
 *  * 定位到对应的Bloom Index Entry。
 *  * 2）再根据Bloom Index Entry中BlockOffset以及BlockOndiskSize加载该Key对应的 位数组 。
 *  * 3）对Key进行Hash映射，根据映射的结果在位数组中查看是否所有位都为1，
 *  * 如果不是，表示该文件中肯定不存在该Key，否则有可能存在。
 *
 *
 *
 *
 * Block的大小可以在创建表列簇的时候通过参数blocksize=> '65535'指定，默认为64K。
 *大号的Block有利于大规模的顺序扫描，而小号的Block更有利于随机查询。
 *因此用户在设置blocksize时需要根据业务查询特征进行权衡，默认64K是一个相对折中的大小。
 *
 * HFile中所有Block都拥有相同的数据结构:
 * BlockHeader和BlockData。
 * 其中BlockHeader主要存储Block相关元数据，BlockData用来存储具体数据。
 *
 * BlockHeader -->Block元数据中最核心的字段是BlockType字段
 * HBase中定义了8种BlockType，每种BlockType对应的Block都存储不同的内容
 * 有的存储用户数据，有的存储索引数据，有的存储元数据（meta）
 * 任意一种类型的HFileBlock，都拥有相同结构的BlockHeader，但是BlockData结构却不尽相同
 *
 *
 *
 *
 *
 *
 * HFile中索引结构分为两种：single-level和multi-level，
 * 前者表示单层索引，后者表示多级索引，一般为两级或三级。
 * 随着HFile文件越来越大，Data Block越来越多，
 * 索引数据也越来越大，已经无法全部加载到内存中
 * ，多级索引可以只加载部分索引，从而降低内存使用空间
 *
 * 同布隆过滤器内存使用问题一样，这也是V1版本升级到V2版本最重要的因素之一
 *
 * V2版本Index Block有两类：Root Index Block和NonRoot Index Block。
 *
 * NonRoot Index Block又分为Intermediate Index Block和Leaf Index Block两种。
 *
 * Root Index Block表示索引数根节点，Intermediate Index Block表示中间节点，
 * Leaf Index Block表示叶子节点，叶子节点直接指向实际Data Block
 *
 *
 *
 *
 */


public class RegionServer核心模块 {

    /*
    * .5 BlockCache
    *
    * 为了提升读取性能，HBase也实现了一种读缓存结构——BlockCache
    * 客户端读取某个Block，首先会检查该Block是否存在于Block Cache，
    * 如果存在就直接加载出来，如果不存在则去HFile文件中加载，
    * 加载出来之后放到Block Cache中，
    * 后续同一请求或者邻近数据查找请求可以直接从内存中获取，以避免昂贵的IO操作。
    *
    * 从字面意思可以看出来，BlockCache主要用来缓存Block。
    *
    * Block是HBase中最小的数据读取单元，即数据从HFile中读取都是以Block为最小单元执行的。
    *到目前为止，HBase先后实现了3种BlockCache方案:
    * LRUBlockCache是最早的实现方案，也是默认的实现方案；
    * HBase 0.92版本实现了第二种方案SlabCache
    * HBase 0.96之后官方提供了另一种可选方案BucketCache
    *
    * 主要分别：内存管理模式，其中LRUBlockCache是将所有数据都放入JVM Heap中，交给JVM进行管理。
    * 后两种方案采用的机制允许将部分数据存储在堆外。
    * 这种演变本质上是因为LRUBlockCache方案中JVM垃圾回收机制经常导致程序长时间暂停，
    * 而采用堆外内存对数据进行管理可以有效缓解系统长时间GC。
    *
    * LRUBlockCache
    * ConcurrentHashMap管理BlockKey到Block的映射关系，
    * 缓存Block只需要将BlockKey和对应的Block放入该HashMap中
    *
    * 采用严格的LRU淘汰算法，当Block Cache总量达到一定阈值之后就会启动淘汰机制
    *
    * HBase采用了缓存分层设计，将整个BlockCache分为三个部分：
    * single-access、
    * multi-access
    * 和in-memory
    *分别占到整个BlockCache大小的25%、50%、25%。
    *
    * 一次随机读中，一个Block从HDFS中加载出来之后首先放入single-access区，
    * 后续如果有多次请求访问到这个Block，就会将这个Block移到multi-access区
    *而in-memory区表示数据可以常驻内存，一般用来存放访问频繁、量小的数据，比如元数据，
    * 用户可以在建表的时候设置列簇属性IN_MEMORY=true，
    * 设置之后该列簇的Block在从磁盘中加载出来之后会直接放入in-memory区
    *设置IN_MEMORY=true并不意味着数据在写入时就会被放到in-memory区，
    * 而是和其他BlockCache区一样，只有从磁盘中加载出Block之后才会放入该区
    *
    * 进入in-memory区的Block并不意味着会一直存在于该区，
    * 仍会基于LRU淘汰算法在空间不足的情况下淘汰最近最不活跃的一些Block
    *
    * 因为HBase系统元数据（hbase:meta，hbase:namespace等表）都存放在in-memory区，
    * 因此对于很多业务表来说，设置数据属性IN_MEMORY=true时需要非常谨慎
    *一定要确保此列簇数据量很小且访问频繁，
    * 否则可能会将hbase:meta等元数据挤出内存，严重影响所有业务性能。
    *
    * 2. LRU淘汰算法实现
    *
    * 系统将BlockKey和Block放入HashMap后都会检查BlockCache总量是否达到阈值，如果达到阈值
    * 就会唤醒淘汰线程对Map中的Block进行淘汰
    *
    * 系统设置3个MinMaxPriorityQueue，分别对应上述3个分层，每个队列中的元素按照最近最少被使用的规则排列，
    * 系统会优先取出最近最少使用的Block，将其对应的内存释放
    * 添加的时候就会将BlockKey放入到Queue中。
    * 3. LRUBlockCache方案优缺点
    *
    * HashMap管理缓存，简单有效
    * 从single-access区晋升到multi-access区或长时间停留在single-access区，
    * 对应的内存对象会从young区晋升到old区，晋升到old区的Block被淘汰后会变为内存垃圾，
    * 最终由CMS回收（Conccurent Mark Sweep），显然这种算法会带来大量的内存碎片
    *
    * ，碎片空间一直累计就会产生臭名昭著的FullGC。尤其在大内存条件下，
    * 一次Full GC很可能会持续较长时间，甚至达到分钟级别
    *
    * 5.5.2 SlabCache
    * 为了解决LRUBlockCache方案中因JVM垃圾回收导致的服务中断问题，
    * SlabCache方案提出使用Java NIO DirectByteBuffer技术实现堆外内存存储
    * 不再由JVM管理数据内存
    * 和LRUBlockCache不同的是，SlabCache淘汰Block时只需要将对应的BufferByte标记为空闲，
    * 后续cache对其上的内存直接进行覆盖即可。
    *
    *
    * 5.5.3 BucketCache
    * BucketCache通过不同配置方式可以工作在三种模式下：
    * heap，offheap和f ile。
    * heap模式表示这些Bucket是从JVM Heap中申请的；
    * offheap模式使用DirectByteBuffer技术实现堆外内存存储管理；
    * f ile模式使用类似SSD的存储介质来缓存Data Block
    *
    * heap模式和offheap模式都使用内存作为最终存储介质，
    * 内存分配查询也都使用Java NIO ByteBuffer技术。
    * 二者不同的是，heap模式分配内存会调用ByteBuffer.allocate方法，
    * 从JVM提供的heap区分配；
    * 而offheap模式会调用ByteBuffer.allocateDirect方法，直接从操作系统分配。
    *
    *这两种内存分配模式会对HBase实际工作性能产生一定的影响。
    * 影响最大的无疑是GC，相比heap模式，offheap模式因为内存属于操作系统，
    * 所以大大降低了因为内存碎片导致Full GC的风险。
    *在内存分配以及读取方面，两者性能也有不同
    * 内存分配时：
    *   heap模式需要首先从操作系统分配内存再拷贝到JVM heap，因此更耗时
    *   offheap直接从操作系统分配内
    * 读取缓存时：
    * heap模式可以从JVM heap中直接读取
    * 而offheap模式则需要首先从操作系统拷贝到JVM heap再读取，因此更费时。
    *
    * 配置BucketCache：
    * 配置文件中：
    * hbase.bucketcache.ioengine    (heap|offheap|file)
    * hbase.bucketcache.size (0.4  )( 0~1)
    *
    *
    *
    *
    *
    *
    *
    *
    * 但和SlabCache不同的是，BucketCache会在初始化的时候申请14种不同大小的Bucket
    *
    * 实际实现中，HBase将BucketCache和LRUBlockCache搭配使用，称为CombinedBlock-Cache
    * 系统在
    * LRUBlockCache中主要存储Index Block和Bloom Block，
    * 而将Data Block存储在BucketCache中
    *
    * BucketCache实现相对比较复杂。它没有使用JVM内存管理算法来管理缓存，
    * 而是自己对内存进行管理，
    *因此大大降低了因为出现大量内存碎片导致Full GC发生的风险
    *
    *BucketCache的内存组织形式
    * HBase启动之后会在内存中申请大量的Bucket，每个Bucket的大小默认为2MB。
    * 每个Bucket会有一个baseoffset变量和一个size标签，
    * 其中baseoffset变量表示这个Bucket在实际物理空间中的起始地址，
    * 因此Block的物理地址就可以通过baseoffset和该Block在Bucket的偏移量唯一确定；
    *
    * HBase中使用BucketAllocator类实现对Bucket的组织管理。
    *
    * HBase会根据每个Bucket的size标签对Bucket进行分类，
    * 相同size标签的Bucket由同一个BucketSizeInfo管理，
    * 如图5-17所示，左侧存放64KB Block的Bucket由65KB BucketSizeInfo管理，
    * 右侧存放128KB Block的Bucket由129KBBucketSizeInfo管理。
    * 可见，BucketSize大小总会比Block本身大1KB，这是因为Block本身并不是严格固定大小的，
    * 总会大那么一点，比如64K的Block总是会比64K大一些。
    *
    * 2）HBase在启动的时候就决定了size标签的分类，
    * 默认标签有(4+1)K，(8+1)K，(16+1)K...(48+1)K，(56+1)K，(64+1)K，(96+1)K...(512+1)K
    * 。而且系统会首先从小到大遍历一次所有size标签，为每种size标签分配一个Bucket，
    * 最后所有剩余的Bucket都分配最大的size标签，默认分配 (512+1)K
    *
    * 3）Bucket的size标签可以动态调整，比如64K的Block数目比较多，65K的Bucket用完了以后，
    * 其他size标签的完全空闲的Bucket可以转换成为65K的Bucket，
    * 但是会至少保留一个该size的Bucket。
    *
    *
    * BucketCache中Block缓存写入及读取流程 TU：
    *•RAMCache是一个存储blockKey和Block对应关系的HashMap。
    *•WriteThead是整个Block写入的中心枢纽，主要负责异步地将Block写入到内存空间。
    *
    * •BucketAllocator主要实现对Bucket的组织管理，为Block分配内存空间。
    * •IOEngine是具体的内存管理模块，将Block数据写入对应地址的内存空间。
    * •BackingMap也是一个HashMap，用来存储blockKey与对应物理内存偏移量的映射关系，
    * 并且根据blockKey定位具体的Block。图中实线表示Block写入流程，虚线表示Block缓存读取流程。
    *
    *
    *
    *
    *
    *
    *
    *
    *
    *
    * */
}
