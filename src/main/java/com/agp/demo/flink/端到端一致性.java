package com.agp.demo.flink;

/**
 * Flink内部状态一致性：
 * 就是计算结果要保证准确
 * 一条数据都不能丢，也不应该重复计算
 * 遇到故障后，恢复后的继续计算，结果也是完全正确的
 *
 * Flink使用轻量级的快照机制---checkpoint 来保证exactly-once
 *
 *
 * 端到端一致性。
 * 算子 Flink内部实现一致性。 但是Sink存到其它位置，也需要保证一致性。
 *  Source端数据应该保证可以重放。重置。
 *  Sink端 从故障恢复时，数据不会重复写入外部数据。
 * 所以是 start--end 端到端一致性。
 *
 * Sink端：
 *  幂等写入
 *  事物写入
 *
 *幂等写入（idempotent writes）：
 * 一个操作，可以重复执行很多次，但是只导致一次结果更改（HashMap.put()).
 *
 * 事物写入实现方式：
 *  1.预写日志  WAL日志
 *      先把结果们当成状态缓存，收到checkpoint完成时，再一次性写入sink系统
 *      优点：简单易于实现。由于数据提前在状态后端中做了缓存，所以无论什么sink系统，
 *      都可以用这种方式一批搞定。
 *      缺点：批量sink。sink的时候不能实时写入了。 （可以降低cp的 interval优化）
 *      有可能会导致at least once。 部分写入成功。又重新提交
 *
 *      实现GenericWriteAheadSink 来实现这种预写日志的提交。
 *  2.两阶段提交
 *      对于每一个checkpoint，sink启动对应的一个事务，并将接下来所有的数据添加到事务里。
 *      然后将数据写入到sink系统，但不提交他们---这时只是预提交（autoCommit关闭）
 *
 *      当它接收到checkpoint完成的通知的时候，它才真正的提交事务，实现结果的真正写入。
 *
 *      这种方式真正实现了exactly-once,它需要一个事务支持的外部sink系统（比如mysql）。
 *      Flink提供了TwoPhaseCommitSinkFunction接口。
 *
 *
 *   2PC（两阶段提交）对外部Sink系统的要求：
 *      1.必须支持事务，或者sink任务必须能够模拟外部系统事务
 *      2.checkpoint间隔期间，必须能开启一个事务并接受数据写入
 *      3.在收到checkpoint完成的通知之前，事务必须是“等待提交”的状态。
 *          在故障恢复时，可能需要一些时间。如果sink系统超时关闭事务了，数据就丢失了。
 *      4.sink任务必须能够在进程失败后恢复事务。
 *      5.提交事务必须是幂等操作。
 *
 *
 *
 *
 *   注意点：
 *      Sink端遇到barrier时，开启新的事务。但是不提交旧的。
 *      当JobManager确认(所有算子状态快照完成)上一个Checkpoint完成时，
 *      会通知所有的算子。此时sink算子
 *      提交对应的那个事务。
 *
 *
 *      确保sink时开启事务的超时时间，要大于checkpoint的时间。 否则事务超时，回滚后，
 *      Flink checkpoint完成，再提交就丢数据了。
 *
 *kafka两阶段提交大体过程：
 *
 *  1.第一条数据来后，开启kafka的事务，写入kafka分区日志，但是标记为未提交。
 *  这就是‘预提交’
 *  2.jobmanager触发checkpoint，barrier向下游传播。算子挨个保存状态，并向下游支流广播。
 *  3.sink连接器接收到barrier后，保存当前状态。并开启下一个阶段的事务。用于提交检查点后一个数据。
 *  4.jobmanger通知所有算子，对应的checkpoint完成。
 *  5.sink任务收到确认信息后，提交对应的事务。
 *  6.关闭已经提交的事务，提交的数据可以正常消费了。
 *
 *
 *
 *
 *
 *
 */
public class 端到端一致性 {
}
