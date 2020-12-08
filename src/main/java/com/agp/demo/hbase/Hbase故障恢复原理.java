package com.agp.demo.hbase;

/**
 * 1. Master故障恢复原理
 *  Master主要负责实现集群的负载均衡和读写调度，
 *  并没有直接参与用户的请求，所以整体负载并不很高。
 *
 *  HBase采用基本的热备方式来实现Master高可用。
 *  通常情况下要求集群中至少启动两个Master进程，
 *  进程启动之后会到ZooKeeper上的Master节点进行注册，注册成功后会成为Active Master，
 *  其他在Master节点未注册成功的进程会到另一个节点Backup-Masters节点进行注册，
 *  并持续关注Active Master的情况，一旦ActiveMaster发生宕机，
 *  这些Backup-Masters就会立刻得到通知，
 *  它们再次竞争注册Master节点，注册成功就可成为Active Master。
 *
 *  一方面，Active Master会接管整个系统的元数据管理任务，
 *  包括管理ZooKeeper以及meta表中的元数据，
 *  并根据元数据决定集群是否需要执行负载均衡操作等。
 *
 *  另一方面，Active Master会响应用户的各种管理命令
 *  ，包括创建、删除、修改表，move、merge region等命令。
 *
 * 2. RegionServer故障恢复原理
 *      一旦RegionServer发生宕机，HBase会马上检测到这种宕机，
 *      并且在检测到宕机之后将宕机RegionServer上的
 *      所有Region重新分配到集群中其他正常的RegionServer上，
 *      再根据HLog进行丢失数据恢复，恢复完成之后就可以对外提供服务。
 *基本原理如图9-1所示。
 *      1）Master检测到RegionServer宕机。
 *      HBase检测宕机是通过ZooKeeper实现的，
 *      正常情况下RegionServer会周期性向ZooKeeper发送心跳，
 *      一旦发生宕机，心跳就会停止，超过一定时间（SessionTimeout）
 *      ZooKeeper就会认为RegionServer宕机离线，并将该消息通知给Master。
 *      2）切分未持久化数据的HLog日志。
 *      RegionServer宕机之后已经写入MemStore但还
 *      没有持久化到文件的这部分数据必然会丢失
 *      HLog中所有Region的数据都混合存储在同一个文件中，
 *      为了使这些数据能够按照Region进行组织回放，
 *      需要将HLog日志进行切分再合并
 *      同一个Region的数据最终合并在一起，方便后续按照Region进行数据恢复。
 *
 *      3）Master重新分配宕机RegionServer上的Region。
 *      RegionServer宕机之后，该Region Server上的Region实际上处于不可用状态，
 *      所有路由到这些Region上的请求都会返回异常。
 *      但这种情况是短暂的，
 *      因为Master会将这些不可用的Region重新分配到其他RegionServer上
 *
 *      4）回放HLog日志补救数据。第3）步中宕机RegionServer
 *      上的Region会被分配到其他RegionServer上，
 *      此时需要等待数据回放。第2）步中提到HLog已经按照Region将日志数据进行了切分再合并，
 *      针对指定的Region，
 *      将对应的HLog数据进行回放，就可以完成丢失数据的补救工作。
 *
 *      5）恢复完成，对外提供服务。数据补救完成之后，可以对外提供读写服务。
 *
 *
 *所有RegionServer在启动之后都
 * 会在ZooKeeper节点/rs上注册一个子节点，
 * 这种子节点的类型为临时节点（ephemeral）
 *
 * 一旦连接在该节点上的客户端因为某些原因发生会话超时，这个临时节点就会自动消失，
 * 并通知watch在该临时节点（及其父节点）上的其他客户端。
 *
 * Master会watch在/rs节点上
 * ZooKeeper的会话超时时间是可以在配置文件中进行配置的’
 * zookeeper.session. timeout，默认为180s。
 *
 *
 * （2）Distributed Log Splitting 分发切分日志：
 *
 * 它借助Master和所有RegionServer的计算能力进行日志切分，
 * 其中Master是协调者，RegionServer是实际的工作者
 *      1）Master将待切分日志路径发布到ZooKeeper节点上（/hbase/splitWAL），
 *      每个日志为一个任务，每个任务都有对应的状态，起始状态为TASK_UNASSIGNED。
 *      2）所有RegionServer启动之后都注册在这个节点上等待新任务，
 *      一旦Master发布任务，RegionServer就会抢占该任务。
 *      3）抢占任务实际上要先查看任务状态，如果是TASK_UNASSIGNED状态，
 *      说明当前没有被占有，此时修改该节点状态为TASK_OWNED。如果修改成功，
 *      表明任务抢占成功；如果修改失败，则说明其他RegionServer抢占成功。
 *      4）RegionServer抢占任务成功之后，将任务分发给相应线程处理，
 *      如果处理成功，则将该任务对应的ZooKeeper节点状态修改为TASK_DONE；
 *      如果处理失败，则将状态修改为TASK_ERR。
 *      5）Master一直监听该ZooKeeper节点，
 *      一旦发生状态修改就会得到通知。如果任务状态变更为TASK_ERR，则Master重新发布该任务；
 *      如果任务状态变更为TASK_DONE，则Master将对应的节点删除。
 *
 *
 *  9-4是RegionServer抢占任务以及日志切分的示意图
 *
 *  1）假设Master当前发布了4个任务，即
 *  当前需要回放4个日志文件，分别为hlog1、hlog2、hlog3和hlog4。
 *  2）RegionServer1抢占到了hlog1和hlog2日志，
 *  RegionServer2抢占到了hlog3日志，RegionServer3抢占到了hlog4日志。
 *  3）以RegionServer1为例，其抢占到hlog1和hlog2日志之后
 *  分别将任务分发给两个HLogSplitter线程进行处理，
 *  HLogSplitter负责对日志文件执行具体的切分——首先读出日志中每一个数据对，
 *  根据HLogKey所属Region写入不同的RegionBuffer。
 *  4）每个Region Buffer都会有一个对应的写线程，
 *  将buffer中的日志数据写入hdfs中，写入路径为/hbase/table/region2/sequenceid.temp，
 *  其中sequenceid是一个日志中某个Region对应的最大sequenceid。
 *  5）针对某一Region回放日志。
 *  只需要将该Region对应的所有文件按照sequenceid由小到大依次进行回放即可。
 *
 *  Distributed Log Splitting方式可以很大程度上加快故障恢复的进程，
 *  正常故障恢复时间可以降低到分钟级别。
 *
 *  这种方式会产生很多日志小文件，产生的文件数将会是M×N，
 *  其中M是待切分的总hlog数量，N是一个宕机RegionServer上的Region个数。
 *  假如一个RegionServer上有200个Region，并且有90个hlog日志，一旦该RegionServer宕机，
 *  那么DLS方式的恢复过程将会创建90×200=18000个小文件。
 *
 * （3）Distributed Log Replay
 *相比Distributed Log Splitting方案，
 * 流程上的改动主要有两点：先重新分配Region，再切分回放HLog。
 *
 *Region重新分配打开之后状态设置为recovering。
 * 核心在于recovering状态的Region可以对外提供写服务，
 * 不能提供读服务，而且不能执行split、merge等操作。
 *
 * DLR的HLog切分回放基本框架类似于Distributed Log Splitting，
 * 但在分解HLog为Region-Buffer之后并没有写入小文件，而是直接执行回放。
 * 这种设计可以大大减少小文件的读写IO消耗，解决DLS的短板。
 *
 * 在写可用率以及恢复性能上，DLR方案远远优于DLS方案，
 * 官方也给出了简单的测试报告，如图9-6所示。
 *
 *DLR在写可用恢复是最快的，读可用恢复稍微弱一点，但都比DLS好很多。
 * 在HBase的0.95版本中，DLR功能已经基本实现，一度在0.99版本设为默认，
 * 但是因为还存在一些功能性缺陷
 * （主要是在rolling upgrades的场景下可能导致数据丢失），在1.1版本取消了默认设置
 *
 *
 */
public class Hbase故障恢复原理 {
}
