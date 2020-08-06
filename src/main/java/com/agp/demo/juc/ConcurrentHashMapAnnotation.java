package com.agp.demo.juc;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 如果数组的某个位置是 null 执行put， 使用cas进行操作。 success -》 break， other-》loop
 * 如果某个数组的位置not null， syncronized(f) 再比较下 f和tab（index）是否同一个对象。
 * 相同继续遍历next， until==null then set。
 *
 *
 * 如果某个index cas失败。就syncronized这个 index对象。执行链表或者红黑树插入。
 *
 * jdk1.7是使用 table数组分段加锁。
 */
public class ConcurrentHashMapAnnotation {
    ConcurrentHashMap map;
    HashMap map2; //里面没有CAS的锁操作，和synchronized 。单线程速度快。
}
