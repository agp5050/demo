package com.agp.demo.futrue;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureTest {
    @Test
    public void test(){
        StringBuilder sb=new StringBuilder();
        CompletableFuture
                // Supplier<T>=>T get()方法 匿名为 ()-> Object
                //BiConsumer<? super T, ? super Throwable>  => void accept(T t, U u) 方法 匿名为 (t,u) -> {}随便即可
                .supplyAsync(()->"aaa").whenComplete(( o,throwable)->sb.append(o));
        System.out.println(sb);
    }
}
