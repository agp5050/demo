package com.agp.demo.hystrix;

import java.util.HashMap;
import java.util.Map;

public class LocalCache {
    private static Map<String,String> cache=new HashMap<>();
    static {
        cache.put("xx","xx's wife");
        cache.put("abc","abc's wife");
    }
    public static String getWife(String key){
        return cache.get(key);
    }
}
