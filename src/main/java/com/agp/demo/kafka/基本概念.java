package com.agp.demo.kafka;

/**
 * 一个典型的 Kafka 体系架构包括若干 Producer、若干 Broker、若干 Consumer，以及一个ZooKeeper集群，
 *其中ZooKeeper是Kafka用来负责集群元数据的管理、控制器的选举等操作的。
 *（3）Broker：服务代理节点。对于Kafka而言，Broker可以简单地看作一个独立的Kafka服务节点或Kafka服务实例
 *
 * 主题是一个逻辑上的概念，它还可以细分为多个分区，
 * 一个分区只属于单个主题，很多时候也会把分区称为主题分区（Topic-Partition）
 *
 * 同一主题下的不同分区包含的消息是不同的，分区在存储层面可以看作一个可追加的日志（Log）文件
 *
 * 消息在被追加到分区日志文件的时候都会分配一个特定的偏移量（offset）
 *
 * offset是消息在分区中的唯一标识，Kafka通过它来保证消息在分区内的顺序性，
 *      不过offset并不跨越分区，
 * 也就是说，Kafka保证的是 分区有序 而  不是主题有序。
 *
 * 一个主题可以横跨多个broker （不同分区可以在不同broker上面）
 *
 *
 *每一条消息被发送到broker之前，会根据分区规则选择存储到哪个具体的分区。
 * 如果分区规则设定得合理，所有的消息都可以均匀地分配到不同的分区中。
 * （不合理可能数据偏移）
 *
 * 分区--》增加IO
 * 副本--》容灾
 *
 *
 * Kafka 消费端也具备一定的容灾能力。Consumer 使用拉（Pull）模式从服务端拉取消息，
 * 并且保存消费的具体位置，当消费者宕机后恢复上线时
 * 可以根据之前保存的消费位置重新拉取需要的消息进行消费，这样就不会造成消息丢失。
 *
 *
 * 分区中的所有副本统称为AR（Assigned Replicas）。
 * 所有与leader副本保持一定程度同步的副本（包括leader副本在内）组成ISR（In-Sync Replicas），
 * ISR集合是AR集合中的一个子集
 *
 *
 * 消息会先发送到leader副本，
 * 然后follower副本才能从leader副本中  拉取  消息进行同步，(Replica消费leader的数据)
 * 同步期间内follower副本相对于leader副本而言会有一定程度的滞后。
 *这个范围可以通过参数进行配置
 *
 * 与leader副本同步滞后过多的副本（不包括leader副本）组成OSR（Out-of-Sync Replicas），
 * 由此可见，AR=ISR+OSR
 *
 * 在正常情况下，所有的 follower 副本都应该与 leader 副本保持一定程度的同步，
 * 即 AR=ISR，OSR集合为空。
 *
 *
 *
 * 。默认情况下，当leader副本发生故障时，只有在ISR集合中的副本才有资格被选举为新的leader，
 * 而在OSR集合中的副本则没有任何机会（不过这个原则也可以通过修改相应的参数配置来改变）。
 *
 *
 * HW高水位 1-4
     * ISR与HW和LEO也有紧密的关系。
     * HW是High Watermark的缩写，俗称高水位，
     * 它标识了一个特定的消息偏移量（offset），
     * 消费者只能拉取到这个  offset之前   的消息。（HW=6，只能拉取到5）
 *
 *LEO是Log End Offset的缩写，它标识当前日志文件中下一条 待写入 消息的offset
 * LEO的大小相当于当前日志分区中  最后一条消息的offset + 1
 *
 *
 *
 *
 * 一个leader副本和2个follower副本，此时分区的LEO和HW都为3。
 * 消息3和消息4从生产者发出之后会被先存入leader副本，如图1-6所示。
 *
 * 在消息写入leader副本之后，follower副本会发送拉取请求来拉取消息3和消息4以进行消息同步。
 *
 * 不同的 follower 副本的同步效率也不尽相同。  --》导致各个分区的HW和LEO也不同
 *
 * 主分区以ISR里面最小分区的HW为准。 消费者只能消费这个HW之前的数据
 * 等慢的ISR副本同步完成后 HW和LEO上涨，leader的HW也跟着上涨。
 *
 * Kafka 的复制机制既不是完全的同步复制，也不是单纯的异步复制。
 *
 *
 * 同步复制要求所有能工作的 follower 副本都复制完，
 * 这条消息才会被确认为已成功提交，
 * 这种复制方式极大地影响了性能。
 *
 而在异步复制方式下，follower副本异步地从leader副本中复制数据，
     数据只要被leader副本写入就被认为已经成功提交。
     在这种情况下，如果follower副本都还没有复制完而落后于leader副本，
 突然leader副本宕机，则会造成数据丢失。

 *
 * Kafka使用的这种ISR的方式则有效地 权衡了 数据可靠性 和  性能之间的关系
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
public class 基本概念 {
}
