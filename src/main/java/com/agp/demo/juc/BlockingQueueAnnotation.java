package com.agp.demo.juc;

import java.util.concurrent.BlockingQueue;

/**
 * 阻塞队列：
 * Insert
 * boolean add(E e)    true if success or Exception
 *
 *  boolean offer(E e)   no check capacity  ,but check if have space (有界队列is full)  。 return true or false。
 *
 *  add 和 offer相似。 但是add会抛异常。
 *
 *  void put(E e) throws InterruptedException   check  if full waiting...  阻塞(有界队列is full)
 *
 *      boolean offer(E e, long timeout, TimeUnit unit)
 *         throws InterruptedException;    waiting if no space until time elapsed.
 *
 *
 *
 * Remove:
 *boolean remove(Object o)  删除1个，如果queue里存在
 *
 *
 *E poll()  Retrieves and removes the head of this queue从头部删除一个元素   if empty return null. 元素不能为nulll
 *
 *
 *
 *     public ArrayBlockingQueue(int capacity, boolean fair) {
 *         if (capacity <= 0)
 *             throw new IllegalArgumentException();
 *         this.items = new Object[capacity];
 *         lock = new ReentrantLock(fair);
 *         notEmpty = lock.newCondition();
 *         notFull =  lock.newCondition();
 *     }
 *
 *
 */
public class BlockingQueueAnnotation {
    BlockingQueue blockingQueue;

    /*        private void unlinkCancelledWaiters() {
            Node t = firstWaiter;
            Node trail = null;
            while (t != null) {
                Node next = t.nextWaiter;
                if (t.waitStatus != Node.CONDITION) {
                    t.nextWaiter = null;
                    if (trail == null)
                        firstWaiter = next;
                    else
                        trail.nextWaiter = next;
                    if (next == null)
                        lastWaiter = trail;
                }
                else
                    trail = t;
                t = next;
            }

            if (t.waitStatus != Node.CONDITION) =》为什么next！=null时，不设置next.prev = trail呢？
        }*/

    /*addConditionWaiter 也是单向链表，没有设置Node的prev，奇怪。*/

    /** 综合AQS的ConditionObject利用了Node双向队列数据结构。但是ConditionObject在构建队列时，只是用Node构建的单向队列。
     * 所以必须有3个指针指向Before，Now，after*/
    /*AQS自己的queue队列，里面构建的是双向指针。head，tail指向了首尾。*/



    /**enqueue 入队， dequeue离队两个主要的方法*/
}
