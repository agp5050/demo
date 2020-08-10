package com.agp.demo.zookeeper;

/**
 * 1. 用来做分布式锁  ephemeral节点
 * 2. 分布式协调任务。比如选举leader。看谁先加上锁，然后执行leader的代码。
 * 3. 分布式系统集中的元数据存储
 *
 * Dubbo: Zookeeper做为注册中心， 分布式集群的集中式元数据存储
 * HBase：分布式集群的集中式元数据存储
 * HDFS：实现Master选举HA架构
 * Kafka：分布式集群的集中式元数据存储，分布式协调和通知。
 */
public class ZookeeperAnnotation {
}
