package com.agp.demo.thread;

import java.util.concurrent.ThreadLocalRandom;

public class TestThreadLocalRandom {
    public static void main(String[] args) {
        int i=0;
        while (i<10){
            i++;
            new Thread(()->{
                System.out.println(ThreadLocalRandom.current().hashCode());
            }).start();
        }
        System.out.println(ThreadLocalRandom.current().hashCode());
    }


}

/**
 * Random： 是多线程分别获取的，会有锁的问题。
 *  protected int next(int bits) {
 long oldseed, nextseed;
 AtomicLong seed = this.seed;
 do {
 oldseed = seed.get();
 nextseed = (oldseed * multiplier + addend) & mask;
 } while (!seed.compareAndSet(oldseed, nextseed));
 return (int)(nextseed >>> (48 - bits));
 }*/

/*ThreadLocalRandom各个线程有自己的Random对象。并且调用的时候都是从Thread的Seed里面更新获取seed。
不会有锁竞争问题。

  final long nextSeed() {
        Thread t; long r; // read and update per-thread seed
        UNSAFE.putLong(t = Thread.currentThread(), SEED,
                       r = UNSAFE.getLong(t, SEED) + GAMMA);
        return r;
    }

    // We must define this, but never use it.
    protected int next(int bits) {
        return (int)(mix64(nextSeed()) >>> (64 - bits));
    }*/
