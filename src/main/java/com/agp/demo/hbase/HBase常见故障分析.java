package com.agp.demo.hbase;

/**
 * •Full GC异常：长时间的Full GC是导致RegionServer宕机的最主要原因，据不完全统计，
 * 80%以上的宕机原因都和JVM Full GC有关。
 *
 * HBase对于Java堆内内存管理的不完善
 * ，HBase未合理使用堆外内存，
 * JVM启动参数设置不合理，
 * 业务写入或读取吞吐量太大，
 * 写入读取字段太大，
 * 其中部分原因要归结于HBase系统本身，另一部分原因和用户业务以及HBase相关配置有关
 *
 * •HDFS异常：RegionServer写入读取数据都是直接操作HDFS的，
 * 如果HDFS发生异常会导致RegionServer直接宕机
 *
 * •机器宕机：物理节点直接宕机也是导致RegionServer进程挂掉的一个重要原因。通常情况下，
 * 物理机直接宕机的情况相对比较少，但虚拟云主机发生宕机的频率比较高。
 *
 * •HBase Bug ：生产线上因为HBase系统本身bug导致RegionServer宕机的情况很少，
 * 但在之前的版本中有一个问题让笔者印象深刻：
 * RegionServer经常会因为耗尽了机器的端口资源而自行宕机
 *
 */
public class HBase常见故障分析 {
}
