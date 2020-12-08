package com.agp.demo.hbase;

/**
 * 作为一个分布式系统，分片迁移是最基础的核心功能。
 * 集群负载均衡、故障恢复等功能都是建立在分片迁移的基础之上的。
 *
 * 实际执行分片迁移时可以分为两个步骤：
 * 第一步，根据负载均衡策略制定分片迁移计划；
 * 第二步，根据迁移计划执行分片的实际迁移。
 *
 * HBase系统中，分片迁移就是Region迁移。
 * 和其他很多分布式系统不同，HBase中Region迁移是一个非常轻量级的操作。
 * 所谓轻量级，是因为HBase的数据实际存储在HDFS上，不需要独立进行管理，
 * 因而Region在迁移的过程中不需要迁移实际数据，只要将读写服务迁移即可。
 *
 * 1. Region迁移的流程
 *
 *  在当前的HBase版本中，Region迁移虽然是一个轻量级操作，但实现逻辑依然比较复杂。
 *      其一，Region迁移过程涉及多种状态的改变；
 *      其二，迁移过程中涉及Master、ZooKeeper（ZK）以及RegionServer等多个组件的相互协调。
 *  在实际执行过程中，Region迁移操作分两个阶段：unassign阶段和assign阶段
 *      1）unassign阶段
 *          unassign表示Region从源RegionServer上下线，如图8-1所示。
 *          1）Master生成事件M_ZK_REGION_CLOSING并更新到ZooKeeper组件，
 *          同时将本地内存中该Region的状态修改为PENDING_CLOSE。
 *          2）Master通过RPC发送close命令给拥有该Region的RegionServer，令其关闭该Region。
 *          3）RegionServer接收到Master发送过来的命令后，
 *          生成一个RS_ZK_REGION_CLOSING事件，更新到ZooKeeper。
 *          4）Master监听到ZooKeeper节点变动后，更新内存中Region的状态为CLOSING。
 *          5）RegionServer执行Region关闭操作。如果该Region正在执行f lush或者Compaction，等待操作完成；
 *          否则将该Region下的所有MemStore强制flush，然后关闭Region相关的服务。
 *          6）关闭完成后生成事件RS_ZK_REGION_CLOSED，更新到ZooKeeper。
 *          Master监听到ZooKeeper节点变动后，更新该Region的状态为CLOSED。
 *     2）assign阶段
 *      assign表示Region在目标RegionServer上上线，如图8-2所示。
 *          1）Master生成事件M_ZK_REGION_OFFLINE并更新到ZooKeeper组件，
 *          同时将本地内存中该Region的状态修改为PENDING_OPEN。
 *          2）Master通过RPC发送open命令给拥有该Region的RegionServer，令其打开该Region。
 *          3）RegionServer接收到Master发送过来的命令后，
 *          生成一个RS_ZK_REGION_OPENING事件，更新到ZooKeeper。
 *          4）Master监听到ZooKeeper节点变动后，更新内存中Region的状态为OPENING。
 *          5）RegionServer执行Region打开操作，初始化相应的服务。
 *          6）打开完成之后生成事件RS_ZK_REGION_OPENED，更新到ZooKeeper，
 *          Master监听到ZooKeeper节点变动后，更新该Region的状态为OPEN。
 *   整个unassign/assign操作是一个比较复杂的过程，
 *   涉及Master、RegionServer和ZooKeeper三个组件，
 *   三个组件的主要职责如下：
 *      •Master负责维护Region在整个操作过程中的状态变化，起到枢纽的作用。
 *      •RegionServer负责接收Master的指令执行具体unassign/assign操作，
 *      实际上就是关闭Region或者打开Region操作。
 *      •ZooKeeper负责存储操作过程中的事件。
 *      ZooKeeper有一个路径为/hbase/region-in-transition的节点，一旦Region发生unssign操作，
 *      就会在这个节点下生成一个子节点，子节点的内容是“事件”经过序列化的字符串，
 *      并且Master会在这个子节点上监听，一旦发生任何事件，Master会监听到并更新Region的状态。
 *
 *1）为什么需要设置这些状态？
 *  无论是unassign操作还是assign操作，都是由多个子操作组成，涉及多个组件的协调合作，
 *  只有通过记录Region状态才能知道当前unassign或者assign的进度，
 *  在异常发生后才能根据具体进度继续执行。
 *
 *2）如何管理这些状态？
 *  Region的这些状态会存储在三个区域：meta表，Master内存，ZooKeeper的region-in-transition节点
 *
 *  •meta表只存储Region所在的RegionServer
 *  如果Region从rs1成功迁移到rs2，那么meta表中就持久化存有Region与rs2的对应关系
 *  迁移中间出现异常，那么meta表就仅持久化存有Region与rs1的对应关系。
 *
 *  •Master内存中存储整个集群所有的Region信息，
 *  根据这个信息可以得出此Region当前以什么状态在哪个RegionServer上。
 *
 *  Master存储的Region状态变更都是由RegionServer通过ZooKeeper通知给Master的，
 *  所以Master上的Region状态变更总是滞后于真正的Region状态变更
 *
 *  我们在HBaseMaster WebUI上看到的Region状态都来自于Master内存信息。
 *
 *  •ZooKeeper中存储的是临时性的状态转移信息，作为Master和RegionServer之间反馈Region状态的通道。
 *
 *
 */
public class Region迁移 {
}
