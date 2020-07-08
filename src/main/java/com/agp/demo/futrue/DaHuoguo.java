package com.agp.demo.futrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class DaHuoguo {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        FutureTask futureTask=new FutureTask(()->{
            System.out.println("准备烧开水");
            Thread.currentThread().sleep(2000);
            System.out.println("水已经烧开");
            return "开水";
        });
        Thread thread=new Thread(futureTask);
        thread.start();
        System.out.println(Thread.currentThread().getName() + ":"  + " 此时开启了一个线程执行future的逻辑（烧开水），此时我们可以干点别的事情（比如准备火锅食材）...");
        Thread.sleep(1000);
        System.out.println(Thread.currentThread().getName() + ":"  + "火锅食材准备好了");
        String shicai = "火锅食材";
        Object boilWater = futureTask.get();
        System.out.println(Thread.currentThread().getName() + ":"  + boilWater + "和" + shicai + "已经准备好，我们可以开始打火锅啦");

    }
}
