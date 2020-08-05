package com.agp.demo.juc;


import lombok.extern.slf4j.Slf4j;

/**
 * 底层执行入口、出口 两个命令：monitor enter 、monitor exit。
 * 因为所有的object对象里面都有一个关联的monitor，每个monitor都有一个计数器。未加锁时
 * 计数器=0， 加锁一次对象monitor计数器为1 是对 对象的加锁。
 */
@Slf4j
public class SynchronizedAnote {

    volatile int  i=0,j=0;
    public synchronized int increment() throws InterruptedException {  //对一个方法进行加锁，
        Thread.sleep(1000);
        return ++i;
    }
    public  int add() throws InterruptedException {  //对一个方法进行加锁，
        Thread.sleep(1000);
        return ++j;
    }
    public synchronized String incrementStr(String str) throws InterruptedException {
        Thread.sleep(10000);
        return str+str;
    }

    public static void main(String[] args) {
//        多个对象执行这个方法时
//        其实就是针对这个当前对象加锁
        SynchronizedAnote synchronizedAnote=new SynchronizedAnote();
        for (int i=0;i<10;i++){
            //10S执行一次。锁住object
            new Thread(
            ){
                public void run(){
                    try {
                        log.info(synchronizedAnote.incrementStr("aa"));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        for (int i=0;i<10;i++){
            //1s执行一次 锁住object
            new Thread(){
                public void run(){
                    try {
                        log.info(synchronizedAnote.increment()+"");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        for (int i=0;i<10;i++){
            //1s执行一次 锁住object
            new Thread(){
                public void run(){
                    try {
                        log.info(synchronizedAnote.add()+"");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
//        Synchronized 锁住整个对象性能特别低。 对象里面的其他加锁方法执行也会受影响。就跟顺序执行差不多了。 非加锁方法不受影响。
        //10s内，由于锁住的是对象，应该不会执行下面的操作。
        //由上面结果可知
        /**
         * 16:00:37.263 [Thread-2] INFO com.agp.demo.juc.SynchronizedAnote - aaaa
         * 16:00:38.259 [Thread-18] INFO com.agp.demo.juc.SynchronizedAnote - 1
         * 16:00:39.259 [Thread-19] INFO com.agp.demo.juc.SynchronizedAnote - 2
         * 16:00:40.259 [Thread-17] INFO com.agp.demo.juc.SynchronizedAnote - 3
         * 16:00:41.259 [Thread-16] INFO com.agp.demo.juc.SynchronizedAnote - 4
         * 16:00:42.260 [Thread-12] INFO com.agp.demo.juc.SynchronizedAnote - 5
         * 16:00:43.260 [Thread-13] INFO com.agp.demo.juc.SynchronizedAnote - 6
         * 16:00:53.260 [Thread-9] INFO com.agp.demo.juc.SynchronizedAnote - aaaa
         * 16:01:03.261 [Thread-8] INFO com.agp.demo.juc.SynchronizedAnote - aaaa
         * 16:01:13.261 [Thread-5] INFO com.agp.demo.juc.SynchronizedAnote - aaaa
         * 16:01:23.262 [Thread-4] INFO com.agp.demo.juc.SynchronizedAnote - aaaa
         * 16:01:24.262 [Thread-15] INFO com.agp.demo.juc.SynchronizedAnote - 7
         * 16:01:25.263 [Thread-14] INFO com.agp.demo.juc.SynchronizedAnote - 8
         * 16:01:26.263 [Thread-11] INFO com.agp.demo.juc.SynchronizedAnote - 9
         * 16:01:27.263 [Thread-10] INFO com.agp.demo.juc.SynchronizedAnote - 10
         * 16:01:37.264 [Thread-7] INFO com.agp.demo.juc.SynchronizedAnote - aaaa
         * 16:01:47.265 [Thread-6] INFO com.agp.demo.juc.SynchronizedAnote - aaaa
         * 16:01:57.266 [Thread-3] INFO com.agp.demo.juc.SynchronizedAnote - aaaa
         * 16:02:07.266 [Thread-0] INFO com.agp.demo.juc.SynchronizedAnote - aaaa
         * 16:02:17.267 [Thread-1] INFO com.agp.demo.juc.SynchronizedAnote - aaaa
         *
         * */


    }

}
