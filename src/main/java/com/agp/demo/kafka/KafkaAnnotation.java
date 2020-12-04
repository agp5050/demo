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
    /*幂等性消费端控制： 消费时，加入到内存set中，中断后，再次消费查看set中是否已存在*/
    /*生产端，retry发送。  brokers ACK设置为-1 所有的副本都收到后再进行发送。*/

    /*broker本身：replica必须大于一、min.insync.replica必须大于1、ack=all 所有的follower必须确认收到*/


/*为了与首领保持同步，跟随者向首领发送获取数据的请求
，这种请求与消费者为了读取消息而发送的请求是一样的。
首领将相应消息发送给跟随者。
请求消息里包含了跟随者想要获取消息的偏移量，
而且这些偏移量总是有序的。

一个跟随者副本先请求消息1，
接着请求消息2，然后请求消息3，
在收到这3个请求的相应之前，它是不会发送第四个请求消息的。
如果跟随者发送了请求消息4，
那么首领就会知道它已经收到了前面3个请求的相应。
通过查看每个跟随者请求的最新偏移量，首领就会知道每个跟随者复制的进度*/
}
