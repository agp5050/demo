package com.agp.demo.juc.volatiletest;

import java.util.concurrent.CountDownLatch;

/**
 * 各个线程内存空间， 对其他线程的变量不可见性。各自线程没有再去主存取数。
 *
 * 没有使用volatile。（内存屏障 load store)
 */
public class volatileTest2 {
    public static void main(String[] args) {
        CountDownLatch c;
        ThreadSafeCache threadSafeCache = new ThreadSafeCache();

        for (int i = 0; i < 8; i++) {
            new Thread(() -> {
                int x = 0;
                while (threadSafeCache.getResult() < 100) {
                    x++;
                }
                System.out.println(x);
            }).start();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        threadSafeCache.setResult(200);
    }
}
class ThreadSafeCache {
    int result;

    public int getResult() {
        return result;
    }

    public synchronized void setResult(int result) {
        this.result = result;
    }

}