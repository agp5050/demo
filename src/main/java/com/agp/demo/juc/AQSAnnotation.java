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
    /*ReentrantLock锁靠同步器实现（Sync），Sync又分为FairSync公平同步器，
    * 和NonFairSync非公平同步器。 lock区别是
    * NonFairSync：直接先尝试设定state为1，然后设置ownerThread为当前thread。然后再acquire
    *
    * if (compareAndSetState(0, 1))
                setExclusiveOwnerThread(Thread.currentThread());
            else
                acquire(1);
                *
    * FairSync和NonFairSync区别二： tryAcquire方法：
    * FairSync的tryAcquire里面有同步器队列飞空检查。如果非空就失败。空的就尝试设置state并设置当前thread为owner。
    *!hasQueuedPredecessors()*/
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


        /*shouldParkAfterFailedAcquire(Node pred, Node node)  循环执行。 将node添加到queue队尾后，
        * 从后向前遍历，如果prev的waitingStatus是>0 即canceled就一直向前，直到prev.waitingStatus<0  (-1 SINGLE -2 CONDITION)
        * 继续外循环，如果prev.waitingStatus==-1 也就是前一个Node的waitingStatus=SINGLE时，当前Node park。
        * 如果prev是head队首,直接park当前node然后设定当前node为head。之前head GC掉*/

        /**Sync  lock 、 release  加锁和释放锁*/


        /** 综合AQS的ConditionObject利用了Node双向队列数据结构。但是ConditionObject在构建队列时，只是用Node构建的单向队列。
         * 所以必须有3个指针指向Before，Now，after*/
        /*AQS自己的queue队列，里面构建的是双向指针。head，tail指向了首尾。*/
        /**ConditionObject 里面的doSingle ==》transferForSignal
         * 将ConditionObject上面的队列的first开始遍历，保证成功发送一个。到
         * AQS对象里面的head tail队列。并将Condition状态从-1 改成0. 然后 enq 并将前一个设置为single状态。然后LockSupport.unpark()*/



        /*Lock  tryAquire 不成功=》 acquireQueued park到 AQS对象上面。
        * ConditionObject 等待队列park到 ConditionObject对象上面。
        * */
        /**await（）只有AQS lock后，才能await。
         * await里面会release 外边AQS队列park的thread。 其他thread会继续lock住，然后await到ConditionObject阻塞
         *
         * 等到signal后 继续执行 acquireQueued 查看能不能获取获取到外部AQS的对象锁。获取成功后即可向下执行。获取失败后，继续
         * park到当前ConditionObject上面。*/

        /*释放锁两种方式：unlock ... AQS.release*/


        /*awaitNanos -》parkNanos  -->被唤醒之后 tryAcquire外部的AQS锁，如果成功就可以继续。不成功
        * 继续park到当前的ConditionObject上面去*/
        /**awaitNanos相当于最多n 时间后，自动唤醒一次（类似被signal然后当前线程被unpark），然后尝试进行
         * 获取外部AQS锁，如果不成功仍然跟await一样继续park到ConditionObject对象。*/

        /*X86平台上的CAS操作一般是通过CPU的CMPXCHG指令来完成的。
        CPU在执行此指令时会首先锁住CPU总线，
        禁止其它核心对内存的访问，然后再查看或修改*ptr的值。
        简单的说CAS利用了CPU的硬件锁来实现对共享资源的串行使用。*/
    }
}
