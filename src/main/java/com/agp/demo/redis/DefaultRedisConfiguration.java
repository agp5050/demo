package com.agp.demo.redis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import redis.clients.jedis.JedisPoolConfig;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class DefaultRedisConfiguration {

    @Bean(name = "redisProperties")
    @Primary
    @ConfigurationProperties(prefix = "spring.redis")
    public RedisProperties defaultRedisProperties() {
        RedisProperties redisProperties = new RedisProperties();
        return redisProperties;
    }

    @Bean("redisTemplate") // redis sentinal 集群 或者 redis cluster集群
    @Primary
    public RedisTemplate defaultRedisTemplate(@Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean("redisConnectionFactory")
    public RedisConnectionFactory redisConnectionFactory(@Qualifier("redisProperties") RedisProperties redisProperties) throws UnknownHostException {
        return setRedisConnectionFactory(redisProperties, null, null);
    }


    private JedisConnectionFactory setRedisConnectionFactory(RedisProperties redisProperties, RedisSentinelConfiguration sentinelConfiguration, RedisClusterConfiguration clusterConfiguration) throws UnknownHostException {
        return applyProperties(createJedisConnectionFactory(redisProperties,sentinelConfiguration,clusterConfiguration),redisProperties);
    }

    private final JedisConnectionFactory applyProperties(JedisConnectionFactory factory, RedisProperties redisProperties) {
        factory.setHostName(redisProperties.getHost());
        factory.setPort(redisProperties.getPort());
        if (StringUtils.isNotBlank(redisProperties.getPassword())) {
            factory.setPassword(redisProperties.getPassword());
        }
        factory.setDatabase(redisProperties.getDatabase());
        if (redisProperties.getTimeout().toMillis() > 0) {
            factory.setTimeout((int) redisProperties.getTimeout().toMillis());
        }
        return factory;
    }

    private JedisConnectionFactory createJedisConnectionFactory(RedisProperties redisProperties,
                                                                RedisSentinelConfiguration sentinelConfiguration, RedisClusterConfiguration clusterConfiguration) {
        JedisPoolConfig poolConfig = redisProperties.getJedis().getPool() != null ? jedisPoolConfig(redisProperties) : new JedisPoolConfig();

        if (getSentinelConfig(redisProperties, sentinelConfiguration) != null) {
            return new JedisConnectionFactory(getSentinelConfig(redisProperties, sentinelConfiguration), poolConfig);
        }
        if (getClusterConfiguration(redisProperties, clusterConfiguration) != null) {
            return new JedisConnectionFactory(getClusterConfiguration(redisProperties, clusterConfiguration), poolConfig);
        }
        return new JedisConnectionFactory(poolConfig);
    }

    /**
     * 109      * Create a {@link RedisClusterConfiguration} if necessary.
     * 110      *
     * 111      * @return {@literal null} if no cluster settings are set.
     * 112
     */
    private final RedisClusterConfiguration getClusterConfiguration(RedisProperties redisProperties,
                                                                    RedisClusterConfiguration clusterConfiguration) {
        if (clusterConfiguration != null) {
            return clusterConfiguration;
        }
        if (redisProperties.getCluster() == null) {
            return null;
        }
        RedisProperties.Cluster clusterProperties = redisProperties.getCluster();
        RedisClusterConfiguration config = new RedisClusterConfiguration(clusterProperties.getNodes());

        if (clusterProperties.getMaxRedirects() != null) {
            config.setMaxRedirects(clusterProperties.getMaxRedirects());
        }
        return config;
    }

    private JedisPoolConfig jedisPoolConfig(RedisProperties redisProperties) {
        JedisPoolConfig config = new JedisPoolConfig();
        RedisProperties.Pool props = redisProperties.getJedis().getPool();
        config.setMaxTotal(props.getMaxActive());
        config.setMaxIdle(props.getMaxIdle());
        config.setMinIdle(props.getMinIdle());
        config.setMaxWaitMillis(props.getMaxWait().toMillis());
        return config;
    }

    private final RedisSentinelConfiguration getSentinelConfig(RedisProperties redisProperties, RedisSentinelConfiguration sentinelConfiguration) {
        if (sentinelConfiguration != null) {
            return sentinelConfiguration;
        }
        RedisProperties.Sentinel sentinelProperties = redisProperties.getSentinel();
        if (sentinelProperties != null) {
            RedisSentinelConfiguration config = new RedisSentinelConfiguration();
            config.master(sentinelProperties.getMaster());
            config.setSentinels(createSentinels(sentinelProperties));
            return config;
        }
        return null;
    }

    private List<RedisNode> createSentinels(RedisProperties.Sentinel sentinel) {
        List<RedisNode> nodes = new ArrayList<RedisNode>();
        for (String node : sentinel.getNodes()) {
            try {
                String[] parts = StringUtils.split(node, ":");
                Assert.state(parts.length == 2, "Must be defined as 'host:port'");
                nodes.add(new RedisNode(parts[0], Integer.valueOf(parts[1])));
            } catch (RuntimeException ex) {
                throw new IllegalStateException("Invalid redis sentinel " + "property '" + node + "'", ex);
            }
        }
        return nodes;
    }


    public static void main(String[] args) {
//        String redisVal = customRedisTemplate.opsForValue().get(redisKey);
//        customRedisTemplate.opsForValue().set(redisKey, FastJsonUtils.toJson(crmMap), cacheSeconds, TimeUnit.SECONDS);

    }
}
