package com.agp.demo.kafka;

/**
 * 高吞吐原因：
 * 1.顺序写
 * 2.zero copy
 * 3.mmp  memory mapped files 物理内存映射，虚拟内存，page直接写入磁盘
 * 4.partition 文件分区，每个topic可以分散到不同的分区队列中写入。
 * 5. 分段日志，每个data文件，对应有index文件，用户消费时，方便查询offset。
 * 6. 批量压缩，降低磁盘IO消耗 gzip
 * 7.批量读写 不是单条读写
 */
public class KafkaAnnotation {
}
