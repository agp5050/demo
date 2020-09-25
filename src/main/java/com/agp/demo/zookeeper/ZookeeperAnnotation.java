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

/***
 * zookeeper作为HA协调者，分布式集群的元数据集中存放者，需要哪些特性：
 * 1. 集群部署
 * 2. all request in orders
 * 3. atomic。 all success or failure
 * 4. data consistency 数据一致性
 * 5. high availability. 高可用
 * 6. 实时性，实时感知
 *
 */

/**
 * ZK数据结构： znode 树形结构 --》简单 纯内存保存
 * 类似linux文件系统，有层级结构。
 * 每个节点包括非叶子节点都能存数据。
 *
 * 顺序写： only one member can write， others can read。 all write request allocate one cluster global
 * unique auto-increment zxid, ensure all clients write request is in order.
 * 一台写，全局唯一 zxid。 （高32位，leader的ID，低32位是顺序序列号）
 *
 * 一致性： write after  will  synchronize to others . make sure date strong consistency.
 *
 * 高性能： 3台机器，承载几万qps没问题（内存操作）
 *
 * 高可用： half members failed in cluster , cluster still can run normally.
 *
 * 高并发：16core 32G 支持 几万qps写没问题。 10几万读取
 *
 *
 * ZK： leader -- follower--observer 3种角色
 * follower收到写请求转发到leader。
 *
 * Node类型： 临时节点 ephemeral, 持久节点 permanent.
 * 顺序节点-》创建节点时自增全局递增的序号
 *
 * 核心机制*** 实现client 通过Watcher 对node进行监听。
 * node改变---zookeeper--通知对应的client，客户端根据通知回调接口
 *
 *
 * ZK数据一致性： ZAB协议， zookeeper atomic broadcast 原子广播协议
 *
 *leader发起proposal(每个都有一个zxid 顺序递增)，并记录到磁盘。follower收到proposal写到磁盘，然后ACK。
 * leader收到more than half members， then ，send commit to follower。
 * then leader load from disk。 新记录正式写入。
 * 各个follower也从磁盘读取之前保存的proposal信息。
 * 》》》Leader将proposal是放到队列queue里面的，然后FIFO发送给follower。
 *
 *集群恢复 启动   过半选举 leader
 * 消息写入 ZAB： 2PC（prepareStatement，commit）+过半写机制
 * 崩溃恢复，只要超过1半机器运行就能选到leader。
 *
 *
 *
 * */
public class ZookeeperAnnotation {
    /* 崩溃恢复阶段会进行数据同步 */

    /*所有的事务请求都由 Leader 节点来处理 -->事务请求转换为事务 Proposal
    * -->完成广播之后，Leader 等待 Follwer 反馈 ACK ***当有过半数的 Follower 反馈信息后
    * -->Leader 将再次向集群内 Follower 广播 Commit 信息*/
    /*leader和follower收到请求后都落磁盘*/
    /**过半确认机制，ZAB和leader选举*/
}
