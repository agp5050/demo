package com.agp.demo.kafka;

/**
 * 消费者（Consumer）负责订阅Kafka中的主题（Topic），
 * 并且从订阅的主题上拉取消息
 *
 * 与其他一些消息中间件不同的是：
 * 在Kafka的消费理念中还有一层消费组（Consumer Group）的概念，每个消费者都有一个对应的消费组
 *
 * 当消息发布到主题后，只会被投递给订阅它的每个消费组中的一个消费者。
 *
 * 每一个分区只能被一个消费组中的一个消费者所消费
 *
 * 如果消费者过多，出现了消费者的个数大于分区个数的情况，就会有消费者分配不到任何分区
 * 分配不到任何分区而无法消费任何消息。
 *
 * 以上分配逻辑都是基于默认的分区分配策略进行分析的，
 * 可以通过消费者客户端参数partition.assignment.strategy
 * 来设置消费者与订阅主题之间的分区分配策略
 *
 *  消息中间件而言，一般有两种消息投递模式：
 *      点对点（P2P，Point-to-Point）模式  （1--to --1）
 *          点对点模式是基于队列的，消息生产者发送消息到队列，消息消费者从队列中接收消息。（解耦）
 *      发布/订阅（Pub/Sub）模式
 *          发布订阅模式定义了如何向一个内容节点发布和订阅消息，这个内容节点称为主题（Topic）
 *          主题可以认为是消息传递的中介，消息发布者将消息发布到某个主题，
 *          而消息订阅者从主题中订阅消息。
 *          主题使得消息的订阅者和发布者互相保持独立(解耦)
 *
 *     发布/订阅模式在消息的一对多广播时采用  1--》n（消费组）
 *
 *   Kafka 同时支持两种消息投递模式，而这正是得益于消费者与消费组模型的契合：
 *      · 如果所有的消费者都隶属于同一个消费组，那么所有的消息都会被均衡地投递给每一个消费者，
 *      即每条消息只会被一个消费者处理，这就相当于点对点模式的应用。
 *
 *      · 如果所有的消费者都隶属于不同的消费组，那么所有的消息都会被广播给所有的消费者，
 *      即每条消息会被所有的消费者处理，这就相当于发布/订阅模式的应用。
 *
 *
 *
 *
 *
 *
 *
 *
 */
public class 消费者Consumer架构 {
}
