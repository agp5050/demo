package com.agp.demo.futrue;


import org.springframework.util.StopWatch;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class FutureTaskTest {
    public static class Person implements Callable<Object>{
        private String name;
        private Integer age;
        public Person(String n,Integer a){
            this.age=a;
            this.name=n;
        }

        @Override
        public String toString() {
            return "Person:{name:"+name+",age:"+age+"}";
        }

        @Override
        public Object call() throws Exception {
            StopWatch stopWatch=new StopWatch();
            stopWatch.start();
            System.out.println(this+":"+System.currentTimeMillis());
            Thread.sleep(3000);
            stopWatch.stop();
            System.out.println(stopWatch.getTotalTimeMillis());
            return this.name+":"+age+" "+System.currentTimeMillis();
        }
    }
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Person p=new Person("xx",123);
        FutureTask futureTask=new FutureTask(p);
        new Thread(futureTask).start();
        System.out.println(futureTask.get());
    }
}
