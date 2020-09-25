package com.agp.demo.map;

import java.util.Hashtable;

/**
 *HashTable value不能为null。key必须实现hashcode和equals方法
 * 新cap是原来的2倍+1
 * int newCapacity = (oldCapacity << 1) + 1;
 * 扩容后，将旧的entry，拿出，遍历分别散落到新的数组里面，并将原来的值设置为next。
 * 整个是数组+链表格式。 不能快速的散列到新的数组。
 *
 *
 *             for (Entry<K,V> old = (Entry<K,V>)oldMap[i] ; old != null ; ) {
 *                 Entry<K,V> e = old;
 *                 old = old.next;
 *
 *                 int index = (e.hash & 0x7FFFFFFF) % newCapacity;
 *                 e.next = (Entry<K,V>)newMap[index];
 *                 newMap[index] = e;
 *             }
 *
 *
 *
 *             public synchronized V put(K key, V value)
 *             方法都加上synchronized，是线程安全的。
 *
 */
public class HashtableAnnotation {
    Hashtable a;
    /*HashMap的位运算快速定位--》tab[i = (n - 1) & hash]*/
    /*HashTable的hash计算就比较慢了(e.hash & 0x7FFFFFFF) % newCapacity*/
    public static void main(String[] args) {
        Hashtable hashtable = new Hashtable();
        hashtable.put(null,"aaa");
    }
}
