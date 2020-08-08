package com.agp.demo.sql;

/**
 * innodb: 表锁、行锁
 * 行锁：insert\add\delete 默认会增加一个X 排它锁。 select 啥锁也不加。
 * S共享锁，可以同时多个共享锁。  S里面不能加X。
 *
 * select * from abc where id =1 lock in share mode 共享锁添加
 * 手动加X锁 select * from abc where id =1 for update. 其他事物hang住，一般生产不用。  悲观锁
 *
 *
 * 乐观锁：
 * 就是在column加一个version。
 *先查询出来 select id,name,version from abc where id=1;
 * //执行一堆逻辑
 * 执行完毕将version+1.并更改保存数据。
 *
 *update abc set name=newName , version=version+1 where id =1 and version =oldVersion
 * 失败后，再查询，再修改。直至成功。  乐观锁。
 *
 * 死锁demo：
 *
 * 事物A： select * from abc where id =1 for update
 * 事物B： select * from abc where id=2 for update
 * 事物A： select * from abc where id =2 for update
 * 事物B： select * from abc where id=1 for update
 * 死锁。
 *
 *高并发乐观锁。
 *
 *
 * 执行计划：
 * explain select *  from abc.（改为你当前执行的那个sql）
 * type: all (全表）、const（常量）、eq_ref（走主键）,index(扫描全部索引） range(扫描部分索引)
 *
 * possible_keys 显示可用索引
 * key 实际使用的索引。
 * key_len:使用索引的长度
 * ref联合索引的哪一列被用了。
 * rows 一共扫描和返回了多少行
 * extra： using filesort(额外排序）、using temporary(构建临时表），using where（根据索引扫出来的数据再次根据where filter）
 */
public class MysqlLock {
}
