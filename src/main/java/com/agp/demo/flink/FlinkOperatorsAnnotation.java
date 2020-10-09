package com.agp.demo.flink;

/**
 *DataSet类里面有context(ExecutionEnvironment的引用)所有数据里面有这个context的引用。
 *算子有：
 * map
 * mapPartition
 *flatMap
 *filter
 *aggregate --sum,min,max
 *sum ==  aggregate(Aggregations.SUM, field)
 * max == aggregate(Aggregations.MAX, field)
 * min ==aggregate(Aggregations.MIN, field)
 * count
 *collect 收集数据  数据量比较大  适当调整client端的内存
 *reduce
 *reduceGroup
 * combineGroup
 *minBy（index）
 *maxBy（index）
 *first（n）  first N members
 *distinct
 *groupBy
 *join（DataSet other）
 *joinWithTiny  提示第二个dataset小，用第二个放到前面做join
 *joinWithHuge
 *leftOuterJoin
 *rightOuterJoin
 *fullOuterJoin
 *coGroup
 * union
 *cross
 *crossWithTiny
 * crossWithHuge
 *partitionByHash（fields）
 *partitionByRange
 *rebalance  ---强制平衡数据
 *sortPartition(int field, Order order)
 *writeAsText(String filePath)
 *writeAsCsv(String filePath)
 *print
 *
 */

public class FlinkOperatorsAnnotation {
}
