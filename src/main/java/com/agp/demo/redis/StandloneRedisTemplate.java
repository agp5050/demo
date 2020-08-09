package com.agp.demo.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StopWatch;
import redis.clients.jedis.JedisPoolConfig;
@Slf4j
public class StandloneRedisTemplate {
    @Bean("standloneRedisTemplate")
    public RedisTemplate getRedisTemplate(){

        StopWatch stopWatch = new StopWatch("redisTemplate");
        stopWatch.start();

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();

        redisStandaloneConfiguration.setHostName("redisConfig.getHost()");
        redisStandaloneConfiguration.setPassword(RedisPassword.of("redisConfig.getPassword()"));
//        redisStandaloneConfiguration.setPort("redisConfig.getPort()");
        JedisPoolConfig poolConfig = new JedisPoolConfig();
//        poolConfig.setMaxIdle("redisConfig.getIdle()");
//        poolConfig.setMaxTotal("redisConfig.getTotal()");
//        poolConfig.setMaxWaitMillis("redisConfig.getWaitmillis()");

        JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder().usePooling().poolConfig(poolConfig).build();

        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);




        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 配置连接工厂
        template.setConnectionFactory(jedisConnectionFactory);

        // 值采用json序列化
        template.setValueSerializer(new StringRedisSerializer());
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());

        // 设置hash key 和value序列化模式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();

//        redisTemplateMap.put(redisConfig.getId(),template);
//        redisVersion.put(redisConfig.getId(),redisConfig.getVersion());
        stopWatch.stop();
        log.warn("创建redisTemplate:{},耗时:{}ms", "redisConfig.getId()", stopWatch.getTotalTimeMillis());
        return template;
    }
}
