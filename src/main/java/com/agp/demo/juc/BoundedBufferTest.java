package com.agp.demo.juc;

import com.alibaba.fastjson.JSONArray;

public class BoundedBufferTest {
    public static void main(String[] args) {
        BoundedBuffer boundedBuffer=new BoundedBuffer();
        for (int i=0;i<30;i++){
            new Thread(()->{
                try {
                    System.out.println(Thread.currentThread().getName()+" has value: "+boundedBuffer.take());
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
            }).start();
        }
        for (int i=0;i<20;i++){
            new Thread(()->{
                try {
                    boundedBuffer.put("1");
                    System.out.println(Thread.currentThread().getName()+"put:");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        System.out.println(JSONArray.toJSONString(boundedBuffer.items));
    }
}
