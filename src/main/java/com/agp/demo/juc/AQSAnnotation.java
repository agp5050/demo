package com.agp.demo.juc;

import java.util.concurrent.locks.ReentrantLock;

/**
 * AbstractQueuedSynchronizer AQS 抽象队列同步器
 *
 * LockSupport.unpark(s.thread);  让继承者，thread从park变为unpark变成启动状态。
 *
 *exclusiveOwnerThread  --> AQS的当前排他线程。在AQS的父类中保存
 *
 * 多线程共享访问，synchronized,CAS,ConcurrentHashMap,Lock
 * ReentrantLock 基于AQS实现的，和synchronized有什么区别？
 *
 * 基于AQS实现的方法，所在的类，整体要比synchronized感觉要高些，毕竟不是加整个对象。
 *
 * exclusiveOwnerThread 可重入，如果是当前对象可以继续+1。 此时不再进行锁定，只是state+1而已
 *
 * non-fair  lock CAS不成功接着进行tryAcquire一次。 non-fair直接抢。
 * fair lock就是 acquire
 */

/*
*公平锁-》队列空直接加锁  else -> 添加到node tail尾部。-》检测 直接prev是不是head，如果是尝试执行一次acquire请求加锁。
* 加锁成功OK。 加锁失败. -> 尝试 遍历prev看看prev的状态是不是取消。如果yes-》设置prev为前面的一个非取消的，
* 如果是head接着尝试加锁一次。如果prev非head&&未cancel状态。设置next为node。 然后LockSupport park当前的Thread，并返回。
*
*/
public class AQSAnnotation {
    static ReentrantLock lock=new ReentrantLock(); //默认非公平锁    -》 new ReentrantLock(true)公平锁
    public static void main(String[] args) {
        /**
         * AQS核心 state=1和一个 当前加锁线程引用。 和一个加锁队列 Queue。 如果竞争加锁失败，入Queue。
         *
         *
         */
        lock.lock();
//        doSomething...
        /**
         * state=0  and  当前加锁thread =null。然后前一个加锁线程去Queue唤醒头部的线程，if 公平锁：新需求来了进发现Queue里面有人，入queue等待。
         * not 公平锁： 直接不进queue就去抢占AQS加锁。
         */
        lock.unlock();


    }
}
