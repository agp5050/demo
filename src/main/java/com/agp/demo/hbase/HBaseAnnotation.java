package com.agp.demo.hbase;

/**
 * 1. 分布式架构，多个regionServer 分布式管理数据，分布式执行各个nosql数据操作
 * 2.分布式数据存储、自动数据分片     （存储到各个region上面，region达到一定程度会分裂成两个）
 * 3. 集成hdfs 作为分布式文件存储系统
 * 4.强一致读写 ，写成功立马可以读
 * 5. 高可用，任何一个regionserver挂掉也仍然可以读，也不会导致数据丢失，其他服务器可以接管他的工作
 * 6.  支持map reduce、spark计算引擎 抽取和存储数据
 * 7.支持web界面对hbase集群进行运维和管理
 */
/*
* 适用场景：
* 1，海量数据  增量多， 不要事物 ，简单查询
* */


/*
*数据模型：
*
* */
public class HBaseAnnotation {
}
