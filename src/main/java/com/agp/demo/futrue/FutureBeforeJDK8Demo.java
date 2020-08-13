package com.agp.demo.futrue;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 在JDK8以前的Future使用比较简单，
 * 我们只需要把我们需要用来异步计算的过程封装在Callable或者Runnable中，
 * 比如一些很耗时的操作(不能占用我们的调用线程时间的)，
 * 然后再将它提交给我们的线程池ExecutorService。
 */

/*submit job then doSomething else. then get() block to get*/
public class FutureBeforeJDK8Demo {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return Thread.currentThread().getName();
            }
        });

//        doSomethingElse();//在我们异步操作的同时一样可以做其他操作
        try {
            //我们必须依赖我们的异步结果的时候我们就可以调用get方法去获得
            String res = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
