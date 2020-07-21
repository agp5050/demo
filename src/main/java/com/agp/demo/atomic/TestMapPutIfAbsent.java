package com.agp.demo.atomic;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class TestMapPutIfAbsent {
    Map<Node<String,String>,String> map=new ConcurrentHashMap<>();
    @Test
    public void test(){
        Node<String,String> node=new Node<>("key","value");
        Node<String,String> node2=new Node<>("key","value");
        System.out.println(node.equals(node2)); //false ，对象可以作为key，但是要重写equals方法和hash方法。如果并发存到hashmap可能出现
//        堆溢出现象
        Assert.assertTrue("hashcode equals ",node.hashCode()==node2.hashCode());
        System.out.println(node.hashCode());

        String value = map.get(node); //从map中取数
        if (null == value){ //判空
            map.put(node,"xxx");//插入
        }
//        虽然用的ConcurrentHashMap但是上面的操作不是原子操作，多线程操作时还是有问题的。
//        也不可以用putIfAbsent替换 查看源码也没有加锁。
        String xx = map.putIfAbsent(node,"xx");
        Object lock = new Object();
        synchronized (lock){
           xx = map.putIfAbsent(node,"xx");
        }
    }
    public static class Node<K,V>{
        K key;
        V value;
        public Node(K key,V value){
            this.key=key;
            this.value=value;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Node){
                Node other=(Node)obj;
                return Objects.equals(this.key,other.key) && Objects.equals(this.value,other.value);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.key.hashCode()^this.value.hashCode();
        }
    }
}
