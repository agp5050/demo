package com.agp.demo.map;

import com.alibaba.fastjson.JSON;

import java.util.LinkedHashMap;
import java.util.Map;

public class TestLRU<K,V> extends LinkedHashMap<K,V> {
    private int limit;
    private static int defaultLimit=1000;
    private static float defaultFactor=0.75f;
    public TestLRU(){
        super(defaultLimit,defaultFactor,true);
        this.limit=defaultLimit;
    }
    public TestLRU(int limit){
        super(limit,defaultFactor,true);
        this.limit=limit;
    }
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return this.size()>limit;
    }

    public static void main(String[] args) {
        TestLRU<String, Integer> stringIntegerTestLRU = new TestLRU<>(2);
        System.out.println("Size:"+stringIntegerTestLRU.size());
        stringIntegerTestLRU.put("a",1);
        stringIntegerTestLRU.put("b",2);
        stringIntegerTestLRU.put("c",3);
        System.out.println(JSON.toJSONString(stringIntegerTestLRU));
    }
}
