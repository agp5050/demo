package com.agp.demo.thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 CyclicBarrier  可重复设置最大门闩
 CountdownLatch 不能重复设置最大门闩

 主线程.await();
 工作线程们.countdown（）

 */
public class CountdownLatchAnnotation {
    public static void main(String[] args) {
        DelayQueue delayQueue;
        CountDownLatch latch;
        CyclicBarrier cyclicBarrier;
        try {
            new Driver2().main();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
 class Driver2 { // ...
     void main() throws InterruptedException {
      CountDownLatch doneSignal = new CountDownLatch(100);
     Executor e = Executors.newCachedThreadPool();

     for (int i = 0; i < 100; ++i) // create and start threads
        e.execute(new WorkerRunnable(doneSignal, i));
     doneSignal.await();           // wait for all to finish
    }
  }

  class WorkerRunnable implements Runnable {
    private final CountDownLatch doneSignal;
    private final int i;
    WorkerRunnable(CountDownLatch doneSignal, int i) {
      this.doneSignal = doneSignal;
      this.i = i;
    }
    public void run() {
        doWork(i);
//        try {
////            Thread.sleep(20);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        doneSignal.countDown();
    }

            void doWork(int i) {
                System.out.println(Thread.currentThread().getName()+" "+i);
            }
  }
