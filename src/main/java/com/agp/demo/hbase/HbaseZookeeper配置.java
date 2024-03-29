package com.agp.demo.hbase;

/**
 * Zookeeper上面Hbase主要节点：
 * • meta-region-server
 *  存储HBase集群  hbase:meta元数据表 所在的RegionServer访问地址
 * 客户端读写数据首先会从此节点读取hbase:meta元数据的访问地址，
 * 将部分元数据加载到本地，根据元数据进行数据路由
 *
 *• master/backup-masters
 *
 * 通常来说生产线环境要求所有组件节点都避免单点服务，
 * HBase使用ZooKeeper的相关特性实现了Master的高可用功能。
 * 其中Master节点是集群中对外服务的管理服务器，
 * backup-masters下的子节点是集群中的备份节点，
 * 一旦对外服务的主Master节点发生了异常，
 * 备Master节点可以通过选举切换成主Master，
 * 继续对外服务。需要注意的是备Master节点可以是一个，也可以是多个
 *
 * • table：集群中所有表信息
 *
 * • region-in-transition：在当前HBase系统实现中，迁移Region是一个非常复杂的过程。
 * 首先对这个Region执行unassign操作，将此Region从open状态变为off line状态
 * （中间涉及PENDING_CLOSE、CLOSING以及CLOSED等过渡状态），
 * 再在目标RegionServer上执行assign操作
 * 将此Region从offline状态变成open状态。这个过程需要在Master上记录此Region的各个状态。
 * 目前，RegionServer将这些状态通知给Master是通过ZooKeeper实现的，
 * RegionServer会在region-in-transition中变更Region的状态
 *
 * • table-lock：
 * HBase系统使用ZooKeeper相关机制实现分布式锁。
 * HBase中一张表的数据会以Region的形式存在于多个RegionServer上，
 * 因此对一张表的DDL操作（创建、删除、更新等操作）通常都是典型的分布式操作
 *
 * 每次执行DDL操作之前都需要首先获取相应表的表锁，
 * 防止多个DDL操作之间出现冲突，这个表锁就是分布式锁
 *
 * • online-snapshot：用来实现在线snapshot操作。
 * 表级别在线snapshot同样是一个分布式操作，
 * 需要对目标表的每个Region都执行snapshot，
 * 全部成功之后才能返回成功。
 * Master作为控制节点给各个相关RegionServer下达snapshot命令，
 * 对应RegionServer对目标Region执行snapshot，成功后通知Master。
 * Master下达snapshot命令、RegionServer反馈snapshot结果都是通过ZooKeeper完成的。
 *
 *• replication：用来实现HBase复制功能。
 * • splitWAL/recovering-regions：用来实现HBase分布式故障恢复。
 * 为了加速集群故障恢复，HBase实现了分布式故障恢复，
 * 让集群中所有RegionServer都参与未回放日志切分。
 * ZooKeeper是Master和RegionServer之间的协调节点。
 *
 * • rs：集群中所有运行的RegionServer。
 *
 *
 *
 */
public class HbaseZookeeper配置 {
}
