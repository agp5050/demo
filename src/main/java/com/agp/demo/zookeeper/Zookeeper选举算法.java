package com.agp.demo.zookeeper;

/**
 * 在3.4.0后的Zookeeper的版本只保留了TCP版本的FastLeaderElection选举算法。
 * 当一台机器进入Leader选举时，当前集群可能会处于以下两种状态
 *
 * 　　　　· 集群中已经存在Leader。
 *
 * 　　　　· 集群中不存在Leader。
 *
 * 　　2.1 Leader选举概述
 *
 * 　　Leader选举是保证分布式数据一致性的关键所在。
 * 当Zookeeper集群中的一台服务器出现以下两种情况之一时，需要进入Leader选举。
 *
 * 　　(1) 服务器初始化启动。
 *
 * 　　(2) 服务器运行期间无法和Leader保持连接。
 *
 * 1. 服务器启动时期的Leader选举
 * 当有一台服务器Server1启动时，其单独无法进行和完成Leader选举，当第二台服务器Server2启动时，
 * 此时两台机器可以相互通信，每台机器都试图找到Leader，于是进入Leader选举过程
 *
 * (1) 每个Server发出一个投票。 （将自己标记为Looking状态）
 * 每次投票会包含所推举的服务器的myid和ZXID，使用(myid, ZXID)来表示
 * 然后各自将这个投票广播发给集群中其他机器。
 * (2) 接受来自各个服务器的投票。
 * 首先判断该投票的有效性，如检查是否是本轮投票、是否来自LOOKING状态的服务器。
 * (3) 处理投票。
 * 　　　　· 优先检查ZXID。ZXID比较大的服务器优先作为Leader。
 *
 * 　　　　· 如果ZXID相同，那么就比较myid。myid较大的服务器作为Leader服务器。
 *
 * Server1而言，它的投票是(1, 0)，接收Server2的投票为(2, 0)，
 * 首先会比较两者的ZXID，均为0，再比较myid，
 * 此时Server2的myid最大，于是更新自己的投票为(2, 0)，然后server1重新投票，
 * 对于Server2而言，其无须更新自己的投票，
 * 只是再次向集群中所有机器发出上一次投票信息即可。
 *
 * 　(4) 统计投票。
 * 每次投票后，服务器都会统计投票信息，判断是否已经有过半机器接受到相同的投票信息
 * ，对于Server1、Server2而言，
 * 都统计出集群中已经有两台机器接受了(2, 0)的投票信息，此时便认为已经选出了Leader。
 *
 * 　　(5) 改变服务器状态。一旦确定了Leader，每个服务器就会更新自己的状态，
 * 如果是Follower，那么就变更为FOLLOWING，如果是Leader，就变更为LEADING。
 *
 *3.4.0版本后：
 * (SID, ZXID)  SID==myID   ZXID==事物ID
 *
 * 服务器具有四种状态，分别是LOOKING、FOLLOWING、LEADING、OBSERVING。
 *
 * 　　2. 投票数据结构
 *
 * 　　每个投票中包含了两个最基本的信息，所推举服务器的SID和ZXID，
 * 投票（Vote）在Zookeeper中包含字段如下
 *
 * 　　id：被推举的Leader的SID。
 *
 * 　　zxid：被推举的Leader事务ID。
 *
 * 　　electionEpoch：逻辑时钟，用来判断多个投票是否在同一轮选举周期中，该值在服务端是一个自增序列，每次进入新一轮的投票后，都会对该值进行加1操作。
 *
 * 　　peerEpoch：被推举的Leader的epoch。
 *
 * 　　state：当前服务器的状态。
 *
 * 3. QuorumCnxManager：网络I/O
 *
 * (1) 消息队列。QuorumCnxManager内部维护了一系列的队列，用来保存接收到的、
 * 待发送的消息以及消息的发送器，除接收队列以外，其他队列都按照SID分组形成队列集合，
 * 如一个集群中除了自身还有3台机器，那么就会为这3台机器分别创建一个发送队列，互不干扰。
 *
 * 　　　　· recvQueue：消息接收队列，用于存放那些从其他服务器接收到的消息。
 *
 * 　　　　· queueSendMap：消息发送队列，用于保存那些待发送的消息，按照SID进行分组。
 *
 * 　　　　· senderWorkerMap：发送器集合，每个SenderWorker消息发送器，
 * 都对应一台远程Zookeeper服务器，负责消息的发送，也按照SID进行分组。
 *
 * 　　　　· lastMessageSent：最近发送过的消息，为每个SID保留最近发送过的一个消息。
 *
 * 　(2) 建立连接。为了能够相互投票，Zookeeper集群中的所有机器都需要两两建立起网络连接。
 * QuorumCnxManager在启动时会创建一个ServerSocket来
 * 监听Leader选举的通信端口(默认为3888)。开启监听后，
 * Zookeeper能够不断地接收到来自其他服务器的创建连接请求，
 * 在接收到其他服务器的TCP连接请求时，会进行处理。
 *
 * 为了避免两台机器之间重复地创建TCP连接，
 * Zookeeper只允许SID大的服务器主动和其他机器建立连接，否则断开连接。
 * 一旦连接建立，就会根据远程服务器的SID来
 * 创建相应的
 * 消息发送器SendWorker和消息接收器RecvWorker，并启动。
 *
 * (3) 消息接收与发送。
 *
 * 因此，每个RecvWorker只需要不断地从这个TCP连接中读取消息，并将其保存到recvQueue队列中。
 * 每台机器的recvQueue是单独的，其他sendQueue是和链接对应的
 *
 * 在SendWorker中，一旦Zookeeper发现针对当前服务器的消息发送队列为空，
 * 那么此时需要从lastMessageSent中取出一个最近发送过的消息来进行再次发送，
 * 这是为了解决接收方在消息接收前或者接收到消息后服务器挂了，导致消息尚未被正确处理。
 * 同时，Zookeeper能够保证接收方在处理消息时，会对重复消息进行正确的处理。
 *
 *
 *
 *
 */
public class Zookeeper选举算法 {
}
