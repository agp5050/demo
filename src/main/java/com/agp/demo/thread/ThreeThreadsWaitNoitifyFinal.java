package com.agp.demo.thread;

import java.util.concurrent.atomic.AtomicInteger;

public class ThreeThreadsWaitNoitifyFinal {
    static AtomicInteger lock=new AtomicInteger(1);
    static AtomicInteger count=new AtomicInteger(0);

    public static void main(String[] args) {
        Object obj;
        for (int i=0;i<100;i++){
            int finalI = i;
            new Thread(()->{

                switch (finalI %3){
                    case 0: firstMtd();
                        break;
                    case 1: secondMtd();
                        break;
                    case 2: thirdMtd();
                        break;
                    default:
                        System.out.println("error...");

                }
            }).start();

        }


            System.out.println(count.get());

        try {
            Thread.sleep(2000);
            System.out.println(count.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
    public static void firstMtd(){
        synchronized (lock){
            while (lock.get()!=1){
                try {
                    lock.notify();
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

                System.out.println(Thread.currentThread().getName()+" 1 firstMtd is running ...");
                lock.incrementAndGet();
                count.getAndIncrement();
                lock.notify();

        }
    }
    public static void secondMtd(){
        synchronized (lock){
            while (lock.get()!=2){
                try {
                    lock.notify();
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

                System.out.println(Thread.currentThread().getName()+" 2 secondMtd is running ...");
                lock.incrementAndGet();
                count.getAndIncrement();
                lock.notify();

        }
    }
    public static void thirdMtd(){
        synchronized (lock){
            while (lock.get()!=3){
                try {
                    lock.notify();
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
                System.out.println(Thread.currentThread().getName()+" 3 thirdMtd is running ...");
                lock.set(1);
                count.getAndIncrement();
                lock.notify();

        }
    }

}
