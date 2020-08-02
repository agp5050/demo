package com.agp.demo.thread;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class ThreadLocalTest {
    public static void main(String[] args) {

        ThreadLocal<List<String>> threadLocal=new ThreadLocal();
        System.out.println(threadLocal.get());
        List<String> list=new ArrayList<>();
        list.add("abc");
        for (int i=0;i<20;i++){
            Thread thread=new Thread(){
                public void run(){
                    System.out.println("thread+"+this.getName()+": ");
                    threadLocal.set(list);
                    List<String> strings = threadLocal.get();
                    strings.add("abc");
                    threadLocal.set(strings);
                    System.out.println(JSONArray.toJSON(threadLocal.get())+" "+threadLocal.get().hashCode());
                }
            };
            thread.start();
        }



    }
}
