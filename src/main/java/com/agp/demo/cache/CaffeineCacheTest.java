package com.agp.demo.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import java.util.concurrent.TimeUnit;

public class CaffeineCacheTest {
    @Test
    public void testCreate(){
        LoadingCache<Object, Object> build = Caffeine
                .newBuilder()
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .maximumSize(300000)
                .weakKeys()
                .build(new CacheLoader<Object, Object>() {
                    @Nullable
                    @Override
                    public Object load(@NonNull Object o) throws Exception {
                        return "UNKNOWN";
                    }
                });
        Object aaa = build.get("aaa");
        System.out.println(aaa);


    }
    @Test
    public void testFromManager(){
        CaffeineCacheManager manager=new CaffeineCacheManager();
        manager.setCacheLoader(new CacheLoader<Object, Object>() {
            @Nullable
            @Override
            public Object load(@NonNull Object key) throws Exception {
                return "UNKNOWN";
            }
        });
//        manager.setCaffeineSpec(CaffeineSpec.parse("abc=123,add=2323"));
        Cache aaa = manager.getCache("aaa");
        System.out.println(aaa.get("aaa").get());

    }
}
