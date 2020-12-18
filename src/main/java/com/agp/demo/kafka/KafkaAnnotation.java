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


/**
 * afka在0.11.0.0在引入幂等性概念的同时也引入了事务的概念。
 * 一般来说默认消费者消费的信息级别是read_uncommited数据；
 * 这有可能读取到事务失败的数据，所以在开启生产者事务之后，
 * 需要用户设置消费者的事务隔离级别
 *
 *
 * SpringBoot 开始kafka事务： application.yml
 * producer:
 * 	  #开启事务，当开启时retries必须>0 acks必须为all
 *       transaction-id-prefix: transaction
 *       # 写入失败时，重试次数。当leader节点失效，
 *       一个repli节点会替代成为leader节点，此时可能出现写入失败，
 *       # 当retris为0时，produce不会重复。retirs重发，
 *       此时repli节点完全成为leader节点，不会产生消息丢失。
 *       retries: 1
 *       #procedure要求leader在考虑完成请求之前收到的确认数，
 *       用于控制发送记录在服务端的持久化，其值可以为如下：
 *       #acks = 0 如果设置为零，则生产者将不会等待来自服务器的任何确认，
 *       该记录将立即添加到套接字缓冲区并视为已发送。在这种情况下，无法保证服务器已收到记录，
 *       并且重试配置将不会生效（因为客户端通常不会知道任何故障），为每条记录返回的偏移量始终设置为-1。
 *       #acks = 1 这意味着leader会将记录写入其本地日志，
 *       但无需等待所有副本服务器的完全确认即可做出回应，在这种情况下，
 *       如果leader在确认记录后立即失败，但在将数据复制到所有的副本服务器之前，则记录将会丢失。
 *       #acks = all 这意味着leader将等待完整的同步副本集以确认记录，
 *       这保证了只要至少一个同步副本服务器仍然存活，记录就不会丢失，
 *       这是最强有力的保证，这相当于acks = -1的设置。
 *       #可以设置的值为：all, -1, 0, 1
 *       acks: all
 *
 * */

/*enable.idempotence：true启用幂等性,服务器启用不影响已经运行的程序！！！*/
/** From Kafka 0.11, the KafkaProducer supports two additional modes:
 * the idempotent producer and the transactional producer.  幂等和事务
 * The idempotent producer strengthens Kafka's delivery semantics from
 * at least once to exactly once delivery.
 * In particular producer retries will no longer introduce duplicates.  //producer重复发送不会再导致重复
 *
 * The transactional producer allows an application to send messages
 * to multiple partitions (and topics!) atomically.  事务发送保证发送到多个分区，原子性！
 *
 *
 * To enable idempotence, the enable.idempotence  configuration must be set to true
 *
 *If set, the retries config will be defaulted to Integer.MAX_VALUE
 *
 * the  max.in.flight.requests.per.connection
 *config will be defaulted to  1
 *and  ack config will be defaulted to all
 * There are no API changes for the idempotent
 *producer, so existing applications will not need to be modified to take advantage of this feature.
 *
 * To take advantage of the idempotent producer,
 * it is imperative to avoid application level re-sends since these cannot
 *be de-duplicated.  As such, if an application enables idempotence, it is recommended to leave the
 *retries config unset,
 * //幂等性的启用，只能确保在单个session中。 如果重启服务，不同session直接的幂等性不能保证。所以producer端还好启用事务。
 * the producer can only guarantee idempotence for messages sent within a single session
 *
 *transactional.id 事务，producer端必须指定
 *To use the transactional producer and the attendant APIs, you must set the
 *transactional.id configuration property.
 *
 * If the transactional.id is set, idempotence is automatically enabled along with
 * the producer configs which idempotence depends on
 *Further, topics which are included in transactions should be configured for durability.
 *In particular, the replication.factor should be at least 3
 * and the min.insync.replicas for these topics should be set to 2.
 *
 *Finally, in order for transactional guarantees
 *to be realized from end-to-end, the consumers must be configured to read only
 *committed messages as well.  消费端必须配置只读已提交消息
 *
 * The purpose of the transactional.id is to enable transaction recovery across multiple sessions of a
 *single producer instance.
 *
 *
 * */

/*
* 0.11kafka producer端事务代码：
** Properties props = new Properties();
 * props.put("bootstrap.servers", "localhost:9092");
 * props.put("transactional.id", "my-transactional-id");
 * Producer<String, String> producer = new KafkaProducer<>(props, new StringSerializer(), new StringSerializer());
 *
 * producer.initTransactions();
 *
 * try {
 *     producer.beginTransaction();
 *     for (int i = 0; i < 100; i++)
 *         producer.send(new ProducerRecord<>("my-topic", Integer.toString(i), Integer.toString(i)));
 *     producer.commitTransaction();
 * } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
 *     // We can't recover from these exceptions, so our only option is to close the producer and exit.
 *     producer.close();
 * } catch (KafkaException e) {
 *     // For all other exceptions, just abort the transaction and try again.
 *     producer.abortTransaction();
 * }
 * producer.close();
 * }
 *
 * */

}
