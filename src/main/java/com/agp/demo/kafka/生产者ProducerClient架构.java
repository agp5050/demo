package com.agp.demo.kafka;

/**
 * 整体架构，如图2-1所示。
 *
 * 整个生产者客户端由两个线程协调运行，
 * 这两个线程分别为主线程和Sender线程（发送线程）
 *
 *主线程中由KafkaProducer创建消息，然后通过可能的
 * 拦截器、
 * 序列化器
 * 分区器
 * 的作用之后缓存到 消息累加器（RecordAccumulator，也称为消息收集器）中。
 *
 * Sender 线程负责从RecordAccumulator中获取消息并将其发送到Kafka中。
 *
 * RecordAccumulator 主要用来缓存消息以便 Sender 线程可以批量发送，
 * 进而减少网络传输的资源消耗以提升性能。
 *
 * RecordAccumulator 缓存的大小可以通过
 * 生产者客户端参数buffer.memory 配置，默认值为 33554432B，即 32MB。
 *
 * 如果生产者发送消息的速度超过发送到服务器的速度，
 * 则会导致生产者空间不足，这个时候KafkaProducer的send（）
 * 方法调用要么被阻塞，
 * 要么抛出异常，
 * 这个取决于参数max.block.ms的配置，此参数的默认值为60000，即60秒。
 *
 * 每个分区--1个--Deque！
 * 主线程中发送过来的消息都会被追加到RecordAccumulator的某个双端队列（Deque）
 * 在RecordAccumulator 的内部为每个分区都维护了一个双端队列
 *
 *队列中的内容就是ProducerBatch，即 Deque＜ProducerBatch＞
 *
 * 消息写入缓存时，追加到双端队列的尾部；
 * Sender读取消息时，从双端队列的头部读取
 *
 * ProducerBatch不是ProducerRecord，
 * ProducerBatch中可以包含一至多个 ProducerRecord。
 * ProducerRecord 是生产者中创建的消息，而ProducerBatch是指一个消息批次
 * ProducerRecord会被包含在ProducerBatch中，这样可以使字节的使用更加紧凑。
 *
 * 将较小的ProducerRecord拼凑成一个较大的ProducerBatch，
 * 也可以减少网络请求的次数以提升整体的吞吐量。
 *
 * 如果生产者客户端需要向很多分区发送消息，
 * 则可以将buffer.memory参数适当调大以增加整体的吞吐量
 *
 * 消息在网络上都是以字节（Byte）的形式传输的，
 * 在发送之前需要创建一块内存区域来保存对应的消息。
 *
 * 在Kafka生产者客户端中，通过java.io.ByteBuffer实现消息内存的创建和释放
 *
 * 不过频繁的创建和释放是比较耗费资源的，
 * 在RecordAccumulator的内部还有一个BufferPool，
 * 它主要用来实现ByteBuffer的复用，以实现缓存的高效利用。
 *
 * 不过BufferPool只针对特定大小的ByteBuffer进行管理，
 * 而其他大小的ByteBuffer不会缓存进BufferPool中，
 * 这个特定的大小由batch.size参数来指定，默认值为16384B，即16KB。
 * 我们可以适当地调大batch.size参数以便多缓存一些消息。
 *
 *
 * 。当一条消息（ProducerRecord）流入RecordAccumulator时，
 * 会先寻找与消息分区所对应的双端队列（如果没有则新建），
 * 再从这个双端队列的尾部获取一个 ProducerBatch（如果没有则新建），
 * 查看 ProducerBatch 中是否还可以写入这个 ProducerRecord，
 * 如果可以则写入，如果不可以则需要创建一个新的ProducerBatch。
 * 在新建ProducerBatch时评估这条消息的大小是否超过batch.size参数的大小，
 * 如果不超过，那么就以batch.size 参数的大小来创建 ProducerBatch，
 * 这样在使用完这段内存区域之后，可以通过BufferPool 的管理来进行复用；
 * 如果超过，那么就以评估的大小来创建ProducerBatch，这段内存区域不会被复用。
 *
 * Sender 从 RecordAccumulator 中获取缓存的消息之后，
 * 会进一步将原本＜分区，Deque＜ProducerBatch＞＞的保存形式
 * 转变成＜Node，List＜ ProducerBatch＞的形式，
 * 其中Node表示Kafka集群的broker节点。
 *
 * 在转换成＜Node，List＜ProducerBatch＞＞的形式之后，
 * Sender 还会进一步封装成＜Node，Request＞的形式，
 * 这样就可以将Request请求发往各个Node了
 *
 * 请求在从Sender线程发往Kafka之前还会保存到InFlightRequests，
 * InFlightRequests保存对象的具体形式为 Map＜NodeId，Deque＜Request＞＞，
 * 它的主要作用是缓存了已经发出去但还没有收到响应的请求
 * （NodeId 是一个 String 类型，表示节点的 id 编号）
 *
 * InFlightRequests 通过配置参数还可以限制每个连接
 * （也就是客户端与Node之间的连接）最多缓存的请求数。
 * 这个配置参数为max.in.flight.requests.per.connection，默认值为 5
 *
 * 即每个连接最多只能缓存 5 个未响应的请求，超过该数值之后就不能再向这个连接发送更多的请求
 *
 * 除非有缓存的请求收到了响应（Response）。
 * 通过比较Deque＜Request＞的size与这个参数的大小
 * 来判断对应的Node中是否已经堆积了很多未响应的消息，
 * 如果真是如此，那么说明这个 Node节点负载较大或网络连接有问题，
 * 再继续向其发送请求会增大请求超时的可能
 *
 *
 * .2.1节中提及的InFlightRequests还可以获得leastLoadedNode，
 * 即所有Node中负载最小的那一个。
 * 这里的负载最小是通过每个Node在InFlightRequests中还未确认的请求决定的
 *
 *
 * KafkaProducer要将此消息追加到指定主题的某个分区所对应的leader副本之前，
 * 首先需要知道主题的分区数量，然后经过计算得出（或者直接指定）目标分区，
 * 之后KafkaProducer需要知道目标分区的leader副本所在的
 * broker 节点的地址、端口等信息才能建立连接，最终才能将消息发送到 Kafka，
 * 在这一过程中所需要的信息都属于元数据信息。
 *
 * bootstrap.servers参数只需要配置部分broker节点的地址即可，
 * 不需要配置所有broker节点的地址，
 * 因为客户端可以自己发现其他broker节点的地址，这一过程也属于元数据相关的更新操作。
 *与此同时，分区数量及leader副本的分布都会动态地变化，客户端也需要动态地捕捉这些变化。
 *
 * 元数据
     * 是指Kafka集群的元数据，
     * 这些元数据具体记录了集群中有哪些主题，
     * 这些主题有哪些分区，
     * 每个分区的leader副本分配在哪个节点上，
     * follower副本分配在哪些节点上，
     * 哪些副本在AR、ISR等集合中，
     * 集群中有哪些节点，
     * 控制器节点又是哪一个等信息。
 *
 * 客户端中没有需要使用的元数据信息时，比如没有指定的主题信息，
 * 或者超过metadata.max.age.ms 时间
 * 没有更新元数据都会  引起元数据的更新操作
 *
 * 客户端参数metadata.max.age.ms的默认值为300000，即5分钟。
 *
 * 元数据的更新操作是在客户端内部进行的，对客户端的外部使用者不可见。
 *
 * 当需要更新元数据时，会先挑选出leastLoadedNode，
 * 然后向这个Node发送MetadataRequest请求来获取具体的元数据信息
 *
 * 这个更新操作是由Sender线程发起的，在创建完MetadataRequest之后同样会存入InFlightRequests
 * 之后的步骤就和发送消息时的类似。元数据虽然由Sender线程负责更新，但是主线程也需要读取这些信息，
 * 这里的数据同步通过synchronized和final关键字来保障。
 *
 * 重要的生产者参数：
 *      1.acks
 *          =1, 默认值即为1。生产者发送消息之后，
 *          只要分区的 leader副本 成功写入消息，那么它就会收到来自服务端的成功响应
 *              如果消息写入leader副本并返回成功响应给生产者，
 *              且在被其他follower副本拉取之前leader副本崩溃，
 *              那么此时消息还是会丢失，因为新选举的leader副本中并没有这条对应的消息。
 *          =0,
 *          生产者发送消息之后 不需要 等待 任何服务端的响应。
 *          如果在消息从发送到写入Kafka的过程中出现某些异常，导致Kafka并没有收到这条消息，
 *          那么生产者也无从得知，消息也就丢失了。
 *          在其他配置环境相同的情况下，acks 设置为 0 可以达到最大的吞吐量。
 *
 *          =-1(或者all),
 *          生产者在消息发送之后，
 *          需要等待ISR中的所有副本 都 成功写入消息 之后才能够收到来自服务端的成功响应。
 *          可以达到最强的可靠性。
 *          不意味着消息就一定可靠，因为ISR中可能只有leader副本，这样就退化成了acks=1的情况
 *          要获得更高的消息可靠性需要配合 min.insync.replicas
 *
 *
 *      2.max.request.size
 *          这个参数用来限制生产者客户端能发送的消息的最大值，默认值为 1048576B，即 1MB。
 *          一般情况下，这个默认值就可以满足大多数的应用场景了
 *
 *      3.retries和retry.backoff.ms
 *          retries参数用来配置生产者重试的次数，默认值为0，
 *          即在发生异常的时候不进行任何重试动作
 *          消息在从生产者发出到成功写入服务器之前可能发生一些临时性的异常，
 *          比如网络抖动、leader副本的选举等，
 *          这种异常往往是可以自行恢复的，生产者可以通过配置retries大于0的值
 *
 *          retry.backoff.ms有关，这个参数的默认值为100，
 *          它用来设定两次重试之间的时间间隔，避免无效的频繁重试
 *
 *          顺序性非常重要，比如MySQL的binlog传输，如果出现错误就会造成非常严重的后果。
 *          如果将retries参数配置为非零值，并且max.in.flight.requests.per.connection
 *          参数配置为大于1的值，那么就会出现错序的现象：
 *          如果第一批次消息写入失败，而第二批次消息写入成功，
 *          么生产者会重试发送第一批次的消息，
 *          此时如果第一批次的消息写入成功，
 *          那么这两个批次的消息就出现了错序。
 *
 *          一般而言，在需要保证消息顺序的场合建议把参数
 *          max.in.flight.requests.per.connection配置为1，
 *          而不是把retries配置为0，不过这样也会影响整体的吞吐。
 *
 *     4.compression.type
 *          这个参数用来指定消息的压缩方式，默认值为“none”
 *          即默认情况下，消息不会被压缩。该参数还可以配置为“gzip”“snappy”和“lz4”
 *          对消息进行压缩可以极大地减少网络传输量、降低网络I/O，从而提高整体的性能。
 *          消息压缩是一种使用  时间换空间  的优化方式，
 *          如果对时延有一定的要求，则不推荐对消息进行压缩。
 *      5.connections.max.idle.ms
 *          这个参数用来指定在多久之后关闭限制的连接，默认值是540000（ms），即9分钟。
 *      6.linger.ms
 *          这个参数用来指定生产者发送 ProducerBatch 之前
 *          等待更多消息（ProducerRecord）加入ProducerBatch 的时间，默认值为 0
 *          生产者客户端会在 ProducerBatch 被填满或等待时间超过linger.ms 值时发送出去。
 *          增大这个参数的值会增加消息的延迟，但是同时能提升一定的吞吐量
 *
 *     7.receive.buffer.bytes
 *          这个参数用来设置Socket接收消息缓冲区（SO_RECBUF）的大小，
 *          默认值为32768（B），即32KB。如果设置为-1，则使用操作系统的默认值。
 *          如果Producer与Kafka处于不同的机房，则可以适地调大这个参数值。
 *
 *     8.send.buffer.bytes
 *          这个参数用来设置Socket发送消息缓冲区（SO_SNDBUF）的大小，默认值为131072（B），即128KB。
 *          与receive.buffer.bytes参数一样，如果设置为-1，则使用操作系统的默认值。
 *
 *     9.request.timeout.ms
 *          这个参数用来配置Producer等待请求响应的最长时间，默认值为30000（ms）。
 *          请求超时之后可以选择进行重试。注意这个参数
 *          需要比broker端参数replica.lag.time.max.ms的值要大，
 *          这样可以减少因客户端重试而引起的消息重复的概率。
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
 *
 *
 *
 *
 *
 *
 *
 *
 */
public class 生产者ProducerClient架构 {
}
