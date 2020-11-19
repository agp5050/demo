package com.agp.demo.thread;

import java.io.PipedInputStream;
import java.util.Date;

public class TwoThreadNotify {
    static boolean flag=true;
    static Object lock=new Object();
    public static void main(String[] args) throws InterruptedException {
        PipedInputStream a;
        new WaitThread().start();
        Thread.sleep(1000);
        new NotifyThread().start();
    }
    public static class WaitThread extends Thread{
        @Override
        public void run() {
            synchronized (lock){
                while (flag){
                    System.out.println(getThreadName()+" got the lock @"+new Date());
                    try {
                        lock.wait();// release current lock and wait on the lock。wait 再次获取到这个
                        //monitor

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }
    public static class NotifyThread extends Thread{
        @Override
        public void run() {
            synchronized (lock){
                 System.out.println(getThreadName()+" got the lock @"+new Date());
                lock.notify();
                flag=false;
            }
            synchronized (lock){
                System.out.println(getThreadName()+" got the lock again @"+new Date());
            }

        }
    }

    static String getThreadName(){
        return Thread.currentThread().getName();
    }
}
