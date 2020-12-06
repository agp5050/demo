package com.agp.demo.hbase;

/**
 * Hbase的Memstore固化为Hfile的主要触发条件为三个：
 * 1、Hlog的存储文件数目达到上限32（默认配置），一个Hlog的大小默认配置为HDFS的block的95%，
 * 这样的话，假定HDFS的块大小为64M，32个文件的话，可以存储的数据量为2G左右。
 * 如果当Hlog的数据达到这个值后，会根据seqid把一些老的region 固化到Hfile中。
 *
 * 2、Memstore的大小设置为64M（默认配置），如果大于这个值则触发Flush的操作，
 * 大于64*3时，这个region将阻止put的写入操作。
 *
 * 3、RS上的内存达到内存配置下限0.35（hbase.regionserver.global.memstore.lowerLimit），
 * 上限为0.4,这里为了防止OOM,进行put操作的流控。
 * 这里有有一个算法，选择compaction压力小，且Memstore相对大的region。
 *
 * Region server在运行过程中，在达到上述三个任何一个条件后，
 * 都会触发Flush的操作。如果在写量比较大的系统上
 * ，我们尽量要采用第2种方式来Flush，以减轻compaction的压力。
 * 因为在Hbase中compaction是单线程处理，我们要尽量减少IO操作和compaction的数量。
 *
 * 经过测试，关于memstore在写量比较大的情况下一般的设置规则为：
 *
 * If 方式3 成立
 * 活跃 Regions = (HBASE_HEAPSIZE *memstore.lowerLimit )/( flush.size / (2~3))
 * else if 方式 1
 * 活跃 Regions = (Hlognumber*hdfsblock)/ (flush.size / (2~3))
 * else
 * 我们期望的状态
 */
public class MemStore大小设置 {
}
