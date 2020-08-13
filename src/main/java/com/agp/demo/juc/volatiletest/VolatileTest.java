package com.agp.demo.juc.volatiletest;

import java.util.concurrent.TimeUnit;

public class VolatileTest {
    /*Never stop! */
        private static /*volatile*/ boolean stop = false;
        public static void main(String[] args) throws Exception {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    int i = 0;
                    while (!stop) {
                        i++;
//                    System.out.println("hello");
                    }
                }
            });
            t.start();

            Thread.sleep(1000);
            TimeUnit.SECONDS.sleep(1);
            System.out.println("Stop Thread");
            stop = true;
        }
}
