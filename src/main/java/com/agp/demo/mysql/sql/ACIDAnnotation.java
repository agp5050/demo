package com.agp.demo.mysql.sql;

/**
 * A atomic
 * C consistency
 * I Isolation  --> 读未提交（脏读）。 读已提交（不可重复度,同一个事物内部再次查询相同语句可能结果不一样。 幻读)。 可重复度。 串行化。
 * D Durability
 *
 * I-》 default级别：可重复度。  如何实现：
 * MVCC机制。 multi version concurrent control 多版本并发控制。
 * InoDB引擎 row++ 两个隐藏列，一个是事物创建时间，一个是事物删除时间。但是不存时间，存ID的值。 事物ID是mysql维护的自增全局唯一ID。
 * id   name    create_time  delete_time
 * 1    agp     120            null
 * 2    lisa    119             null
 * 2    lisaJunior 122          null
 * 事物ID=121的事物查询的时候，只会查询ID<=121的。
 * 如果又有新的事物ID比如122创建data。 121事物查询不到。 只能查询select *from where create_time <= current_tx_id
 * 如果事物ID=122的删除了这行。 delete_time 值设置为122。 事物ID=121的查询时依旧可以查询到。  select *from abc where create_time<= cid
 * and delete_time is null or delete_time > cid
 *
 * 如果事物ID=122 修改了一行，不是原地修改，而是增加一行。 就是create_time变为最新的cid。
 *
 *
 */
public class ACIDAnnotation {
}
