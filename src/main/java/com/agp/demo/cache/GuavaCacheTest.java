package com.agp.demo.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GuavaCacheTest {
    public static void main(String[] args) {

    }
    @Test
    public void createGuavaCache() throws ExecutionException {
        LoadingCache<Object, Object> build = CacheBuilder.newBuilder()
                .concurrencyLevel(10)
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .maximumSize(1000000)
                .build(new CacheLoader<Object, Object>() {
                    //如果这个Cache中没有命中这个Key，则通过这个cacheLoader去加载这个key。并返回。
                    @Override
                    public Object load(Object key) throws Exception {
                        return cacheTryToLoadTheKey(key);
                    }
                });
        build.put("abc","aa");
        System.out.println(build.get("abc1"));
        //get(K,Callable) if exist return; else do call() ,put into cache ,return value;
        Object value=build.get("aaa",()->{
            return "asdfas";
        });
        System.out.println(value);

    }

    private Object cacheTryToLoadTheKey(Object key) {
        return "null123";
    }
}
