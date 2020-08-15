package com.agp.demo.juc;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Came from Conditions doug lea example..
 *
 * 分化为两个等待队列，分别等待full 或者empty的情况
 * 全靠 AQS的Condition。
 *
 *
 */
public class BoundedBuffer {
  final Lock lock=new ReentrantLock();
  final Condition notFull=lock.newCondition();
  final Condition notEmpty=lock.newCondition();
  final Object[] items=new Object[100];
  int putptr,takeptr,count;
  public void put(Object object) throws InterruptedException {
      lock.lock();
      try{
          while (count==items.length)
            notFull.await();
          items[putptr]=object;
          if (++putptr==items.length) putptr=0;
          ++count;
          notEmpty.signal();
      }finally {
          lock.unlock();
      }
  }
  public Object take() throws InterruptedException {
      lock.lock();
      try {
          while (count==0)
              notEmpty.await();
          Object rst=items[takeptr];
          if (++takeptr == items.length) takeptr=0;
          --count;
          notFull.signal();
          return rst;
      }finally {
          lock.unlock();
      }
  }
}
