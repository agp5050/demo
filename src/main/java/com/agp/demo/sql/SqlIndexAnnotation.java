package com.agp.demo.sql;

/**
 * Innodb 事务、外键、聚簇索引（索引和Data是一个文件），id auto increment最好，顺序分裂。
 * 避免B+树和频繁合并和分裂（对比使用UUID）。会将数据放到之前的
 * 索引数据里面插入引起磁盘空间浪费和时间浪费。  表、行 锁
 *
 * MyISAM引擎 索引文件分离、非事务、压缩、表锁 。大数据同步的时候可以用
 *
 *
 * 0/1（sex） 这种分类少的不适合做索引。
 *最好建立联合索引，2-3个索引。多了占空间
 * 最左前缀匹配：  create index ( name,age,level,index)
 * select * from abc where name=? and age=?  是可以用到联合索引的
 *
 * select * from abc where name=? and level=?  只能用到name的索引，然后scan结果用level再筛选。
 *
 *
 * select * from abc where name like "abc%" 可以用到索引。 但是 like "%abc"不可以用到索引。
 *
 * select * from abc where age>23 and name=1 。 范围可以使用索引，之后的就不能再用了。
 *
 * 前缀索引：（优化索引大小）
 * ALTER TABLE table_name ADD KEY(column_name(prefix_length));
 */

public class SqlIndexAnnotation {
}
