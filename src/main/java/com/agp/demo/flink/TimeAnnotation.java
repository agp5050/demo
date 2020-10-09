package com.agp.demo.flink;

/**
 * eventTime  ingestingTime   processingTime
 * 事件时间     接收时间        处理时间
 *
 * 可以在代码中，设定流的时间特性：
 * env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
 *
 * 设定时间特性后，还要指定流中record数据的那个属性是要提取的时间
 */
public class TimeAnnotation {
}
