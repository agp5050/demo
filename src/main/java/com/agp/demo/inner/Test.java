package com.agp.demo.inner;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        AtomicInteger integer=new AtomicInteger(0);
/*        for (int i=0;i<100;i++){
            new Thread(()->{
                int getV = integer.getAndDecrement();
                System.out.println(Thread.currentThread().getName()+": "+getV);

            }).start();
        }*/
        System.out.println(int.class.getCanonicalName());
        System.out.println(int[].class.getCanonicalName());
        System.out.println(boolean[].class.getCanonicalName());
        System.out.println(String[].class.getCanonicalName());

        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);
        int i = unsafe.arrayIndexScale(int[].class);
        System.out.println(i);
        int shift = 31 - Integer.numberOfLeadingZeros(i);
        System.out.println("shift:"+shift);
        shift >>>= 2;
        System.out.println(shift);
    }
}
