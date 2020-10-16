package com.agp.demo.thread;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 自旋锁&可重复锁&公平锁&共享锁&分段锁
 *    无锁&偏向锁&轻量级锁&重量级锁如何膨胀升级
 *
 *
 */
public class LockAnnotation {


    //Ticket Lock 是为了解决上面的公平性问题，
    // 类似于现实中银行柜台的排队叫号：
    // 锁拥有一个服务号，表示正在服务的线程，还有一个排队号
    public class TicketLock implements Lock {
        private AtomicInteger serviceNum = new AtomicInteger(0);
        private AtomicInteger ticketNum = new AtomicInteger(0);
        private final ThreadLocal<Integer> myNum = new ThreadLocal<>();

        @Override
        public void lock() {
            myNum.set(ticketNum.getAndIncrement());
            while (serviceNum.get() != myNum.get()) {
            }
        }

        @Override
        public void unlock() {
            serviceNum.compareAndSet(myNum.get(), myNum.get() + 1);
            myNum.remove();
        }
    }
    public interface Lock{
        void lock();
        void unlock();
    }


    /*cpu三大架构
    NUMA  ->>（Non-Uniform Memory Access）
            利用NUMA技术，可以把几十个CPU(甚至上百个CPU)组合在一个服务器内
            由于访问远地内存的延时远远超过本地内存，因此当CPU数量增加时，系统性能无法线性增加

            具有多个CPU模块，每个CPU模块由多个CPU(如4个)组成，并且具有独立的本地内存、I/O槽口
            节点之间可以通过互联模块(如称为Crossbar Switch)进行连接和信息交互
            每个CPU可以访问整个系统的内存(这是NUMA系统与MPP系统的重要差别)
            但远地访问的性能远远低于本地内存访问
            因此在开发应用程序时应该尽量避免远地内存访问


     smp  -》Symmetric Multiprocessing
            对称多处理器.在SMP中所有的处理器都是对等的,
     它们通过总线连接共享同一块物理内存，这也就导致了系统中所有资源(CPU、内存、I/O等)都是共享的，
     当我们打开服务器的背板盖，如果发现有多个cpu的槽位，但是却连接到同一个内存插槽的位置，
     那一般就是smp架构的服务器，日常中常见的pc啊，笔记本啊，手机还有一些老的服务器都是这个架构，
     其架构简单，但是拓展性能非常差
    ls /sys/devices/system/node/# 如果只看到一个node0 那就是smp架构

      mpp -》Massive Parallel Processing
            这个其实可以理解为刀片服务器，每个刀扇里的都是一台独立的smp架构服务器
      且每个刀扇之间均有高性能的网络设备进行交互，。
      是一种完全无共享(Share Nothing)结构，
      因而扩展能力最好，理论上其扩展无限制，
      目前的技术可实现512个节点互联，数千个CPU
      */
//    lscpu  可以看到


    /*CLHLock虚队列，通过两个threadlocal指向pred和now*/
    /**CLHLock 发明人是：Craig，Landin and Hagersten
     * 是一种基于链表的可扩展、高性能、公平的自旋锁
     * 申请线程只在本地变量上自旋，它不断轮询前驱的状态，
     * 如果发现前驱释放了锁就结束自旋
     *
     * 优点
     * 是空间复杂度低
     *
     * 唯一的缺点
     * 是在NUMA（一种CPU架构）系统结构下性能很差
     * 在这种系统结构下，每个线程有自己的内存，
     * 如果前趋结点的内存位置比较远，
     * 自旋判断前趋结点的locked域，性能将大打折扣
     *
     * 但是在SMP（一种CPU架构）系统结构下该法还是非常有效的。
     * 一种解决NUMA系统结构的思路是MCS队列锁
     *
     * */
    public class CLHLock implements Lock {

        /**
         * 锁等待队列的尾部
         */
        private AtomicReference<QNode> tail;
        private ThreadLocal<QNode> preNodeHolder;
        private ThreadLocal<QNode> myNodeHolder;

        public CLHLock() {
            tail = new AtomicReference<>(null);
            myNodeHolder = ThreadLocal.withInitial(QNode::new);  //ThreadLocal.withInitial ThreadLocal正确用法
            preNodeHolder = ThreadLocal.withInitial(() -> null);
        }

        @Override
        public void lock() {
            QNode qnode = myNodeHolder.get();
            //设置自己的状态为locked=true表示需要获取锁
            qnode.locked = true;
            //链表的尾部设置为本线程的qNode，并将之前的尾部设置为当前线程的preNodeHolder
            QNode pre = tail.getAndSet(qnode);
            preNodeHolder.set(pre);
            if (pre != null) {
                //当前线程在前驱节点的locked字段上旋转，直到前驱节点释放锁资源
                while (pre.locked) {
                }
            }
        }

        @Override
        public void unlock() {
            QNode qnode = myNodeHolder.get();
            //释放锁操作时将自己的locked设置为false，可以使得自己的后继节点可以结束自旋
            qnode.locked = false;
            //回收自己这个节点，从虚拟队列中删除
            //将当前节点引用置为自己的preNodeHolder，那么下一个节点的preNodeHolder就变为了当前节点的preNodeHolder，这样就将当前节点移出了队列
            myNodeHolder.set(preNodeHolder.get());
        }

        private class QNode {
            /**
             * true表示该线程需要获取锁，且不释放锁，为false表示线程释放了锁，且不需要锁
             */
            private volatile boolean locked = false;
        }
        }


    /**
     * 适用于NUMA架构的CPU
     * 一种基于链表的可扩展、高性能、公平的自旋锁
     * 申请线程只在本地变量上自旋，直接前驱负责通知其结束自旋
     *从而极大地减少了不必要的处理器缓存同步的次数，降低了总线和内存的开销
     *
     *
     * 每个线程在一个core上面运行，不同的CPU节点上面运行。 运行时都要copy到本地栈内存。
     * CLH是在前趋结点的locked域上自旋等待，而MCS是在自己的结点的locked域上自旋等待。
     * 正因为如此，它解决了CLH在NUMA系统架构中获取locked域状态内存过远的问题。
     *
     * //另一个CPU结点上面的值，自旋时要不断CPU远地内存获取。
     */
    public class MCSLock implements Lock {
        private AtomicReference<QNode> tail;
        private ThreadLocal<QNode> myNodeHolder;

        public MCSLock() {
            tail = new AtomicReference<>(null);
            myNodeHolder = ThreadLocal.withInitial(QNode::new);
        }

        @Override
        public void lock() {
            QNode qnode = myNodeHolder.get();
            QNode preNode = tail.getAndSet(qnode);
            if (preNode != null) {
                qnode.locked = false;
                preNode.next = qnode;
                //wait until predecessor gives up the lock
                while (!qnode.locked) {
                }
            }
            qnode.locked = true;
        }

        @Override
        public void unlock() {
            QNode qnode = myNodeHolder.get();
            if (qnode.next == null) {
                //后面没有等待线程的情况
                if (tail.compareAndSet(qnode, null)) {
                    //真的没有等待线程，则直接返回，不需要通知
                    return;
                }
                //wait until predecessor fills in its next field
                // 突然有人排在自己后面了，可能还不知道是谁，下面是等待后续者
                while (qnode.next == null) {  //自旋等待
                }
            }
            //后面有等待线程，则通知后面的线程
            qnode.next.locked = true;
            qnode.next = null;
        }

        private class QNode {
            /**
             * 是否被qNode所属线程锁定
             */
            private volatile boolean locked = false;
            /**
             * 与CLHLock相比，多了这个真正的next
             */
            private volatile QNode next = null;
        }
    }
    }
