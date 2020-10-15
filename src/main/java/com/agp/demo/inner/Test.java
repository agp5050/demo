package com.agp.demo.inner;

import com.agp.demo.Person;
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
        int i = unsafe.arrayIndexScale(long[].class);
        System.out.println(i);
        int shift = 31 - Integer.numberOfLeadingZeros(i);
        System.out.println("shift:"+shift);
        shift >>>= 2;
        System.out.println(shift);
        int a=10;int b=20;
        int c=0;
        add(c=a,c+b);  //先赋值第一个。 更新c的值，然后将更新后的值用到第二个参数。
    }
    @org.junit.Test
    public void testCircuit(){
        Person person=null;
        /*|| && 断路器*/
        System.out.println(1==1 || person.toString()=="");

        System.out.println(1==2 && person.toString()=="");
    }

    public static void add(int x,int y){
        System.out.println("x+y:"+(x+y));
    }

}
