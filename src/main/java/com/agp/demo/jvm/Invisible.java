package com.agp.demo.jvm;

public class Invisible {
    public static void main(String[] args) {
        final int[] a = {0};
        new Thread(()->{

            for(int i=0;i<100;i++){
                int finalA= a[0];
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                a[0] = a[0] +1;
            }
            System.out.println("Thread A stopped...");
        }).start();

        //Thread B只执行了一次read。 不能把更新到主内存的值再次读取到。 这个就是内存的不可见性。
        new Thread(()->{
            int b=a[0];
            while (b==0){
                try {
                    System.out.println("B thread sleep 100ms");
                    Thread.sleep(100);
//                    b=a[0];  这是 再次从主存读取
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
