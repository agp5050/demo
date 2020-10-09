package com.agp.demo.flink;

/**
 * watermark水位线，每个task有几个分区，每个分区都有水位线。
 * 整个task的水位线是，分区里面最小的那个值。
 * datastream.assignTimestampsAndWatermarks 指定timestamp的提取器（TimestampAssigner），指定水位线延迟时间
 *Time.milliseconds(n) n-->就是水位线延迟等待的时间
 * 水位线延迟太久，会导致下游生成结果延迟。解决办法是水位线到达前生成近似结果。
 *
 *TimestampAssigner --》周期性间隔插入watermark：AssignerWithPeriodicWaterMarks
 * TimestampAssigner--》 无周期性watermark： AssignerWithPunctuatedWatermarks
 *
 * 排序好的数据不需要延迟触发。只指定时间戳。
 *
 *
 *
 */
public class FlinkWaterMarkAnnotation {
}
