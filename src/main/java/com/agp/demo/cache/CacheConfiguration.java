package com.agp.demo.cache;

import com.google.common.cache.CacheLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfiguration {
    @Bean
    public CacheLoader<Object,Object> cacheLoader(){
        return new CacheLoader<Object, Object>() {
            @Override
            public Object load(Object key) throws Exception {
                return "UNKNOWN";
            }
        };
    }
}
