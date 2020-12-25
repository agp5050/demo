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
 * 常用命令：
 * standLone模式：
     * ./flink run -c aaa.bbb.cc.MainClass -p 2  /abc/ddd/jarpath --host hostname --port 7777
     *
     * ./flink cancel jobID
     * ./flink list 查看flink当前所有的job
     * ./flink list -a 历史和当前
     *
     * webUI ，提交jar后，点击对应jar包，配置 mainClass parallelism host port
 *
 * Yarn模式：
 *   session-cluster:
 *      特点：
 *         首先向yarn 申请资源，申请完毕不会改变。之后用这个session提交的任务，必须等待前一个任务执行完毕
 *         才能提交下一个任务。
 *         适合规模小，执行时间短的作业
 *
 *      启动：
 *          首先启动一个yarn-session：（相当于申请一个资源集群）
 *          ./yarn-session.sh -n 2 -s 2 -jm 1024 -tm 1024 -nm test -d
 *          -n:container(taskmanager的num，少用：可以动态添加）
 *          -s:slot（每个tm slot的数量）
 *          -jm:jobmanager
 *          -tm:taskmanager
 *          -d:后台执行
 *
 *          然后启动 /flink run -c aaa.bbb.cc.MainClass -p 2  /abc/ddd/jarpath --host hostname --port 7777
 *
 *          yarn-application kill jobId
 *
 *   per-job-cluster:
 *      每一个job都会单独向yarn申请资源，不受之前的job影响。
 *      适合规模大，时间长的作业。
 *
 *      启动：
 *          ./flink run -m yarn-cluster -c a...Main  ../.../abc.jar --host hostname --port 7777
 *
 *          -m: mode
 *
 *
 *
 */

public class FlinkOperatorsAnnotation {
}
