package com.agp.demo.kafka;

/**
 *我们往已经部署好的Kafka集群里面添加机器是最正常不过的需求，
 * 而且添加起来非常地方便，我们需要做的事是从已经部署好的Kafka节点中复制相应的配置文件，
 * 然后把里面的broker id修改成全局唯一的
 * ，最后启动这个节点即可将它加入到现有Kafka集群中。
 *
 *新添加的Kafka节点并不会自动地分配数据，所以无法自动分担集群的负载
 *除非我们新建一个topic。
 *
 * 解决办法：
 * 手动将部分分区移到新添加的Kafka节点上
 * Kafka内部提供了相关的工具来重新分布某个topic的分区
 * 、、--describe描述topic信息
 * ./bin/kafka-topics.sh --topic test --describe --zookeeper www.ag.com:2181
 * Topic:test PartitionCount:7  ReplicationFactor:2 Configs:
 *   Topic: test  Partition: 0  Leader: 1 Replicas: 1,2 Isr: 1,2
 *   Topic: test  Partition: 1  Leader: 2 Replicas: 2,3 Isr: 2,3
 *   Topic: test  Partition: 2  Leader: 3 Replicas: 3,4 Isr: 3,4
 *   Topic: test  Partition: 3  Leader: 4 Replicas: 4,1 Isr: 4,1
 *   Topic: test  Partition: 4  Leader: 1 Replicas: 1,3 Isr: 1,3
 *   Topic: test  Partition: 5  Leader: 2 Replicas: 2,4 Isr: 2,4
 *   Topic: test  Partition: 6  Leader: 3 Replicas: 3,1 Isr: 3,1
 *
 *
 * kafka-reassign-partitions.sh 工具来重新分布分区
 *kafka-reassign-partitions.sh  bin目录下
 *
 * 将Replica扩展为3个，并且同时将partition扩展为9个，示例：
 *
 * (1). 扩展partitions 到9个
// * 这一个命令就完成了partition的重分区。 后面的2命令是扩展replica的。
 *
 * /kafka-topics.sh --zookeeper kafka-zookeeper01:2181,kafka-zookeeper02:2181,kafka-zookeeper03:2181/kafka --alter --topic Message --partitions 9
 *
 * （2）扩展ReplicationFactor 为3
 * 需要创建一个json文件，例如：
 * {"version":1,
 * "partitions":[
 * {"topic":"Message","partition":0,"replicas":[1,2,3]},
 * {"topic":"Message","partition":1,"replicas":[1,2,3]},
 * {"topic":"Message","partition":2,"replicas":[1,2,3]},
 * {"topic":"Message","partition":3,"replicas":[1,2,3]},
 * {"topic":"Message","partition":4,"replicas":[1,2,3]},
 * {"topic":"Message","partition":5,"replicas":[1,2,3]},
 * {"topic":"Message","partition":6,"replicas":[1,2,3]},
 * {"topic":"Message","partition":7,"replicas":[1,2,3]}
 *
 * {"topic":"Message","partition":8,"replicas":[1,2,3]}
 * ]}
 *
 *
 * 再执行如下命令：
 *
 * /kafka-reassign-partitions.sh --zookeeper kafka-zookeeper01:2181,kafka-zookeeper02:2181,kafka-zookeeper03:2181/kafka --reassignment-json-file increace-factor.json --execute
 *
 *
 *最后 /kafka-topics.sh --describe  检测一遍
 *
 *
 */
public class Kafka手动增加partition {
}
