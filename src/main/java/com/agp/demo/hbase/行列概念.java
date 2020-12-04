package com.agp.demo.hbase;

/**
 *table：表，一个表包含多行数据
 *row：行，一行数据包含一个唯一标识rowkey、多个column以及对应的值。
 * 在HBase中，一张表中所有row都按照rowkey的字典序由小到大排序
 * column：列，与关系型数据库中的列不同，
 * HBase中的column由columnfamily（列簇）以及qualifier（列名）两部分组成，
 * 两者中间使用":"相连
 * timestamp：时间戳，每个cell在写入HBase的时候都会默认分配一个时间戳作为该cell的版本
 * HBase支持多版本特性，即同一rowkey、column下可以有多个value存在，
 * 这些value使用timestamp作为版本号，版本越大，表示数据越新
 *
 * cell：单元格，由五元组（row, column, timestamp, type, value）组成的结构
 * type表示Put/Delete这样的操作类型，timestamp代表这个cell的版本
 * cell实际是以KV结构存储的，其中（row, column,timestamp, type）是K，value字段对应KV结构的V
 *
 * HBase中Map的key是一个复合键，由rowkey、column family、
 * qualifier、type以及timestamp组成，value即为cell的值
 *
 * 实际上在HBase中存储为如下KV结构  五元组-》Value
 * {"rowKey","columnFamily","column","type","Version"}->"Value"
 *
 * 多维：
 * key是一个复合数据结构，5元组组成
 * 稀疏：
 * 整整一行仅有一列（people:author）有值，其他列都为空值
 * 在其他数据库中，对于空值的处理一般都会填充null，
 * 而对于HBase，空值不需要任何填充
 * 这个特性为什么重要？因为HBase的列在理论上是允许无限扩展的，对于成百万列的表来说，
 * 通常都会存在大量的空值，如果使用填充null的策略，势必会造成大量空间的浪费
 *
 * 排序：
 *构成HBase的KV在同一个文件中都是有序的
 * KV中的key进行排序——先比较rowkey，rowkey小的排在前面；
 * 如果rowkey相同，再比较column，即column family:qualifier，column  小的排在前面；
 * 如果column还相同，再比较时间戳timestamp，即版本信息，timestamp  大的排在前面
 *  CF存储，一个cf一个文件目录
 * HBase中的数据是按照列簇存储的，即将数据按照列簇分别存储在不同的目录中
 *
 *
 * HBase要将数据按照列簇分别存储
 * 回答这个问题之前需要先了解两个非常常见的概念：行式存储、列式存储
 * 行式存储：
     * 行式存储系统会将一行数据存储在一起，
     * 一行数据写完之后再接着写下一行，最典型的如MySQL这类关系型数据库
     *行式存储在获取一行数据时是很高效的
     * 但是如果某个查询只需要读取表中指定列对应的数据，
     * 那么行式存储会先取出一行行数据，
     * 再在每一行数据中截取待查找目标列
     *这种处理方式在查找过程中引入了大量无用列信息，从而导致大量内存占用
     * 适用于OLTP类型负载
 * 列式存储：
     * 列式存储理论上会将一列数据存储在一起，
     * 不同列的数据 分别 集中存储，
     * 最典型的如Kudu、Parquet on HDFS等系统（文件格式）
 * 列式存储对于只 查找 某些列 数据的请求非常高效，
 * 只需要 连续读出 所有待查目标列，然后遍历处理即可
 *反过来，列式存储对于获取一行的请求就不那么高效了，
 * 需要多次IO读多个列数据，最终合并得到一行数据
 *因为同一列的数据通常都具有相同的数据类型，因此列式存储具有天然的高压缩特性
 *
 * 列簇式存储：
 * 概念上来说，列簇式存储介于行式存储和列式存储之间
 * 可以通过不同的设计思路在行式存储和列式存储两者之间相互切换
         * 一张表只设置一个列簇，这个列簇包含所有用户的列。
         * HBase中一个列簇的数据是存储在一起的，
         * 因此这种设计模式就等同于行式存储
 *  再比如，一张表设置大量列簇，每个列簇下仅有一列，
 *  很显然这种设计模式就等同于列式存储
 *
 *  上面两例当然是两种极端的情况，在当前体系中 不建议 设置太多列簇，
 *  但是这种架构为HBase将来演变成HTAP（Hybrid Transactional and Analytical Processing）
 *  系统提供了最核心的基础
 *
 * 借
 *
 *
 */


public class 行列概念 {
}
