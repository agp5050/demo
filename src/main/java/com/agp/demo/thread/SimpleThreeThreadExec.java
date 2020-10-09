package com.agp.demo.thread;

public class SimpleThreeThreadExec {
    static Object lock=new Object();
    static int count=0;
    public static void main(String[] args) {
        for (int i=0; i<100; i++){
            new Thread(()-> {
                synchronized(lock) {
                    int n  = count % 3;  // 求余这个是必须的。根据1->100 这样余数是固定的值，对应固定的方法
//                    单线程执行。
                    switch (n) {
                        case 0: firstMtd();break;
                        case 1: secondMtd();break;
                        case 2: thirdMtd();break;
                    }
                    count++;
                }
            }).start();
        }
        try {
            Thread.sleep(5000);
            System.out.println(count);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void firstMtd(){
        System.out.println(Thread.currentThread().getName()+" 1 firstMtd is running ...");
    }

    public static void secondMtd(){
        System.out.println(Thread.currentThread().getName()+" 2 secondMtd is running ...");
    }

    public static void thirdMtd(){
        System.out.println(Thread.currentThread().getName()+" 3 thirdMtd is running ...");
    }
}
