package com.agp.demo.kafka;

/**
 * 虽然Kafka无法确定网络故障期间发生了什么，
 * 但生产者可以进行多次重试来确保消息已经写入 Kafka，
 * 这个重试的过程中有可能会造成消息的重复写入，
 * 所以这里 Kafka 提供的消息传输保障为 at least once。
 *
 *
 * 显式地将生产者客户端参数enable.idempotence设置为true即可
 *
 */
public class 事务 {
}
