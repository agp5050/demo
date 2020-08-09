package com.agp.demo.redis;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

public class RedisClusterSimpleDemo {
    public static void main(String[] args) {
        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        //Jedis Cluster will attempt to discover cluster nodes automatically
        jedisClusterNodes.add(new HostAndPort("10.100.93.44", 6381));
        jedisClusterNodes.add(new HostAndPort("10.100.93.44", 6382));
        jedisClusterNodes.add(new HostAndPort("10.100.93.44", 6383));
        jedisClusterNodes.add(new HostAndPort("10.100.93.44", 6384));
        jedisClusterNodes.add(new HostAndPort("10.100.93.44", 6385));
        jedisClusterNodes.add(new HostAndPort("10.100.93.44", 6386));

        JedisPoolConfig jpc = new JedisPoolConfig();
        jpc.setMaxTotal(10);
        jpc.setMaxIdle(10);
        jpc.setMinIdle(10);

        JedisCluster jc = new JedisCluster(jedisClusterNodes, 5000, 5000, 2, "admin", jpc);
        jc.set("dijia478", "112233");

        String keys = jc.get("dijia478");
        System.out.println(keys);
    }
}
