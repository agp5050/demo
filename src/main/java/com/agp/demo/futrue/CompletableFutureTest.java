package com.agp.demo.futrue;

import com.alibaba.fastjson.JSON;
import groovy.lang.Tuple2;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * CompletableFuture 提供了，各个task依赖执行。
 * 单个或者多个依赖， 比FutureTask，功能更丰富。
 *
 */
public class CompletableFutureTest {
    @Test
    public void test(){
        StringBuilder sb=new StringBuilder();
        CompletableFuture
                // Supplier<T>=>T get()方法 匿名为 ()-> Object
                //BiConsumer<? super T, ? super Throwable>  => void accept(T t, U u) 方法 匿名为 (t,u) -> {}随便即可
                .supplyAsync(()->"aaa").whenComplete(( o,throwable)->sb.append(o));
        System.out.println(sb);

        CompletableFuture
                .runAsync(()-> System.out.println("firstStage finished."))// Run 返回Void
                .thenApply(o-> "xxx")  // apply 有返回值 接收上个stage的结果，并将结果值返回
                .thenApply(o-> o+o) // 接收上个stage的结果，并将结果值返回
                .thenAccept(o-> System.out.println(" Accept:{"+o+"}")) // 接收上个结果的值，并返回Void
                .thenRun(()-> System.out.println("finished. lastStage.")); //

        List<String> strings = Arrays.asList("aa", "bb", "aa", "cc", "dd");
        Map<String, List<Tuple2<String,Integer>>> collect = strings.stream()
                .map(i -> new Tuple2<String,Integer>(i, 1))
                .collect(Collectors.groupingBy(Tuple2<String,Integer>::getFirst));
        collect.entrySet()
                .stream()
                .forEach(item->{
                    Optional<Tuple2<String, Integer>> reduce = item.getValue().stream()
                            .reduce(
                                    (v1, v2) ->
                                            new Tuple2(v1.getFirst(), v1.getSecond() + v2.getSecond()));
                    System.out.println(reduce.get().getFirst()+"="+reduce.get().getSecond());
                });


        Map<String, Long> collect1 = strings.stream().collect(Collectors.groupingBy(i -> i, Collectors.counting()));
        System.out.println(JSON.toJSONString(collect1));
    }
}
