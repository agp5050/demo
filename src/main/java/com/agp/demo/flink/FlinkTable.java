package com.agp.demo.flink;

/**
 * his module bridges Table/SQL API and runtime.
 * It contains all resources that are required during pre-flight and runtime phase.
 *<!-- https://mvnrepository.com/artifact/org.apache.flink/flink-table-planner -->
 * <dependency>
 *     <groupId>org.apache.flink</groupId>
 *     <artifactId>flink-table-planner_2.12</artifactId>
 *     <version>1.10.1</version>
 *     <scope>provided</scope>
 * </dependency>
 *
 *
 *This module contains the Table/SQL API for writing table programs that
 *  interact with other Flink APIs using the Scala programming language.
 * <!-- https://mvnrepository.com/artifact/org.apache.flink/flink-table-api-scala-bridge -->
 * <dependency>
 *     <groupId>org.apache.flink</groupId>
 *     <artifactId>flink-table-api-scala-bridge_2.12</artifactId>
 *     <version>1.10.1</version>
 * </dependency>
 *
 *
 * <!-- https://mvnrepository.com/artifact/org.apache.flink/flink-table-api-java-bridge -->
 * <dependency>
 *     <groupId>org.apache.flink</groupId>
 *     <artifactId>flink-table-api-java-bridge_2.12</artifactId>
 *     <version>1.10.1</version>
 *     <scope>provided</scope>
 * </dependency>
 *
 *
 */
public class FlinkTable {
    /*
    * FlinkTable 概念
    *Flink可以基于Catalog（目录）创建表
    *Table表默认由“标识符”指定，由3部分组成：
    * Catalog名，数据库（database）名和对象名
    *
    * 如果没有指定catalog和database名
    * 默认是 default catalog+default database+ "sensor"之类自定义名
    *
    * 支持实体表，也支持虚拟表（视图View， 中间结果集合）
    *
    *
    * 表输出，是通过数据写入TableSink来实现的
    * TableSink是一个通用接口可以支持不同的文件格式，存储数据库，消息队列
    *
    * 输出的最直接方法，就是通过Table.insertInto("") 注册到env的表。
    * 这个表应该是通过env.connect()连接到某个地方实现的。
    *
    *
    *Table落数据3种方式：
    *
    *Append模式
    *Retract模式
    *Upsert模式
    *
    *
    * ES写入：支持Upsert模式
    *
    * Maven依赖	支持自	Elasticsearch版本
flink-connector-elasticsearch_2.11	1.0.0	1.x
flink-connector-elasticsearch2_2.11	1.0.0	2.x
flink-connector-elasticsearch5_2.11	1.3.0	5.x
flink-connector-elasticsearch6_2.11	1.6.0	6 and later versions
    *
    *
    *
    *TableApi有机制可以展示对表的逻辑的执行计划
    * 可以用TableEnvironment.explain(table)来返回执行计划字符串
    * 1.优化前查询逻辑计划
    * 2.优化后的逻辑查询计划
    * 3.实际执行计划
    *
    *
    * 动态表： Table Dynamic
    *
    *
    *Table时间语义：
    *
    *可以指定proctime和eventtime
    *三种方式指定：
    * 1.DataStream转换表操作的时候指定
    * 2.定义TableSchema的时候指定
    * 3.创建表的DDL中定义
    *
    *
    *
    * */
}
