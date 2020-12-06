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

    /**
     * ZooKeeper在使用ZAB协议保证多节点数据一致性的基础上实现了很多其他工程特性，
     * 以下这些特性对于实现分布式集群管理的诸多功能至关重要
     *1）多类型节点。ZooKeeper数据树节点可以设置多种节点类型，每种节点类型具有不同节点特性
     *      持久节点（PERSISTENT）：节点创建后就会一直存在，直到有删除操作来主动清除这个节点。
     *
     *      临时节点（EPHEMERAL）：和持久节点不同，临时节点的生命周期和客户端session绑定。
     *      也就是说，如果客户端session失效，那么这个节点就会自动被清除掉
     *      这里提到的是session失效，而非连接断开
     *
     *      •持久顺序节点（PERSISTENT_SEQUENTIAL）
     *      这类节点具有持久特性和顺序特性
     *      顺序特性表示父节点会为它的第一级子节点维护一份时序
     *      记录每个子节点创建的先后顺序
     *      实际实现中，Zookeeper会为顺序节点加上一个自增的数字后缀作为新的节点名。
     *
     *      •临时顺序节点（EPHEMERAL_SEQUENTIAL）
     *      这类节点具有临时特性和顺序特性。临时特性即客户端session一旦结束，节点就消失
     *
     *2）Watcher机制
     * Watcher机制是ZooKeeper实现的一种事件异步反馈机制，
     * 就像现实生活中某读者订阅了某个主题，这个主题一旦有任何更新都会第一时间反馈给该读者一样
     *
     * watcher设置：ZooKeeper可以为所有的读操作设置watcher，
     * 这些读操作包括getChildren()、exists()以及getData()
     *其中通过getChildren()设置的watcher为子节点watcher，
     * 这类watcher关注的事件包括子节点创建、删除等
     *
     * 通过exists()和getData()设置的watcher为数据watcher
     * 这类watcher关注的事件包含节点数据发生更新、子节点发生创建删除操作
     *
     * •watcher触发反馈：ZooKeeper中客户端与服务器端之间的连接是 长连接。
     * watcher事件发生之后服务器端会发送一个 信息 给客户端，
     * 客户端会调用预先准备的处理逻辑进行应对。
     *
     * •watcher特性：watcher事件是 一次性 的触发器，当watcher关注的对象状态发生改变时，
     * 将会触发此对象上所设置的watcher对应事件。
     * 再次改变，那么将不再有watcher事件反馈给客户端，除非客户端重新设置了一个watcher。
     *
     * 3）Session机制。ZooKeeper在启动时，客户端会根据配置文件中ZooKeeper服务器列表配置项，
     * 选择其中任意一台服务器相连，如果连接失败，它会尝试连接另一台服务器，
     * 直到与一台服务器成功建立连接或因为所有ZooKeeper服务器都不可用而失败。
     *
     * 一旦建立连接，ZooKeeper就会为该客户端创建一个新的session。
     * 每个session都会有一个超时时间设置
     *
     * 一旦session过期，任何与该session相关联的临时znode都会被清理。
     * 临时znode一旦被清理，注册在其上的watch事件就会被触发
     *
     * 需要注意的是，ZooKeeper对于网络连接断开和session过期是两种处理机制
     *  在客户端与服务端之间维持的是一个 长连接，在session超时时间内，
     * 服务端会不断检测该客户端是否还处于正常连接
     *      服务端会将客户端的每次操作视为一次有效的心跳检测来反复地进行session激活
     * 因此，在正常情况下，客户端session是一直有效的
     * 然而，当客户端与服务端之间的连接断开后，
     * 用户在客户端可能主要看到：CONNECTION_LOSS和SESSION_EXPIRED两类异常。
     *
     * CONNECTION_LOSS ：网络一旦断连，客户端就会收到CONNECTION_LOSS异常，
     * 此时它会自动从ZooKeeper服务器列表中重新选取新的地址，
     * 并尝试重新连接，直到最终成功连接上服务器。
     *
     * SESSION_EXPIRED ：客户端与服务端断开连接后，
     * 如果重连时间耗时太长，超过了session超时时间，
     * 服务器会进行session清理
     *
     *
     *
     *
     *
     * */

    /* 崩溃恢复阶段会进行数据同步 */

    /*所有的事务请求都由 Leader 节点来处理 -->事务请求转换为事务 Proposal
    * -->完成广播之后，Leader 等待 Follwer 反馈 ACK ***当有过半数的 Follower 反馈信息后
    * -->Leader 将再次向集群内 Follower 广播 Commit 信息*/
    /*leader和follower收到请求后都落磁盘*/
    /**过半确认机制，ZAB和leader选举*/
}
