package com.agp.demo.spark;


/**
 * 1、Spark的速度比MapReduce快，Spark把运算的中间数据存放在内存，迭代计算效率更高；
 * mapreduce的中间结果需要落地，需要保存到磁盘，比较影响性能；
 *
 * 2、spark容错性高，它通过弹性分布式数据集RDD来实现高效容错；
 * mapreduce容错可能只能重新计算了，成本较高；
 *
 * 3、spark更加通用，spark提供了transformation和action这两大类的多个功能API，
 * 另外还有流式处理sparkstreaming模块、图计算GraphX等；
 * mapreduce只提供了map和reduce两种操作，
 * 流计算以及其他模块的支持比较缺乏，计算框架(API)比较局限；
 *
 * 4、spark框架和生态更为复杂，很多时候spark作业都需要根据不同业务场景的
 * 需要进行调优已达到性能要求；mapreduce框架及其生态相对较为简单，
 * 对性能的要求也相对较弱，但是运行较为稳定，适合长期后台运行；
 *
 * 5、Spark API方面- Scala: Scalable Language, 是进行并行计算的最好的语言.
 * 与Java相比，极大的减少代码量（Hadoop框架的很多部分是用Java编写的）。
 *
 *Hadoop实质上更多是一个分布式数据基础设施：
 * 它将巨大的数据集分派到一个由普通计算机组成的集群中的多个节点进行存储，
 * 意味着不需要购买和维护昂贵的服务器硬件，还会索引和跟踪这些数据，
 * 让大数据处理和分析效率达到前所未有的高度。
 *
 * Spark是一个
 *专为大规模数据处理而设计的快速通用的计算引擎
 * 不存数据。
 *
 *
 */
/*Shuffle区别：
Hash shuffle合并机制的问题：
M*R个小文件，启用spark.shuffle.consolidateFiles=true开启，默认false） spark1.2之前。
如果 Reducer 端的并行任务或者是数据分片过多的话
则 Core * Reducer Task 依旧过大，
也会产生很多小文件。进而引出了更优化的sort shuffle。

*在Spark 1.2以后的版本中，默认的ShuffleManager改成了SortShuffleManager。
*SortShuffleManager的运行机制主要分成两种，
一种是普通运行机制，另一种是bypass运行机制。
当shuffle read task的数量小于等于
spark.shuffle.sort.bypassMergeThreshold参数的值时(默认为200)，就会启用bypass机制。
*
普通机制的Sort Shuffle
这种机制和mapreduce差不多，在该模式下，数据会先写入一个内存数据结构中，
此时根据不同的shuffle算子，可能选用不同的数据结构。
如果是reduceByKey这种聚合类的shuffle算子，那么会选用Map数据结构，
一边通过Map进行聚合，一边写入内存；

如果是join这种普通的shuffle算子，那么会选用Array数据结构，直接写入内存。
每写一条数据进入内存数据结构之后，就会判断一下，是否达到了某个临界阈值。
如果达到临界阈值的话，
那么就会尝试将内存数据结构中的数据溢写到磁盘，然后清空内存数据结构。
在溢写到磁盘文件之前，会先根据key对内存数据结构中已有的数据进行排序。
排序过后，会分批将数据写入磁盘文件。
默认的batch数量是10000条，也就是说，排序好的数据，
会以每批1万条数据的形式分批写入磁盘文件。

一个task将所有数据写入内存数据结构的过程中，会发生多次磁盘溢写操作，也会产生多个临时文件。
最后会将之前所有的临时磁盘文件都进行合并，
由于一个task就只对应一个磁盘文件因此还会单独写一份索引文件，
其中标识了下游各个task的数据在文件中的start offset与end offset。
SortShuffleManager由于有一个磁盘文件merge的过程，因此大大减少了文件数量，
由于每个task最终只有一个磁盘文件所以文件个数等于上游shuffle write个数。


bypass运行机制的触发条件如下：

1. shuffle map task数量小于spark.shuffle.sort.bypassMergeThreshold参数的值，默认值200。
2. 不是聚合类的shuffle算子(比如reduceByKey)。

此时task会为每个reduce端都对应创建一个临时磁盘文件  M*R个文件
并将数据按key进行hash然后根据key的hash值，
将key写入对应的磁盘文件之中。当然，写入磁盘文件时也是先写入内存缓冲，
缓冲写满之后再溢写到磁盘文件的。最后，
同样会将所有临时磁盘文件都合并成一个磁盘文件，并创建一个单独的索引文件。

而该Bypass机制与普通SortShuffleManager运行机制的不同在于：
第一，磁盘写机制不同;
第二，不会进行排序。也就是说，启用该机制的最大好处在于，
shuffle write过程中，不需要进行数据的排序操作，也就节省掉了这部分的性能开销。

*
* */
public class MapReduce和Spark的区别 {
}
