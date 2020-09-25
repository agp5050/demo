package com.agp.demo.map;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *
 * 可以看到是有head和tail两个指针的双向链表。
 *     void afterNodeRemoval(Node<K,V> e) { // unlink
 *         LinkedHashMap.Entry<K,V> p =
 *             (LinkedHashMap.Entry<K,V>)e, b = p.before, a = p.after;
 *         p.before = p.after = null;
 *         if (b == null)
 *             head = a;
 *         else
 *             b.after = a;
 *         if (a == null)
 *             tail = b;
 *         else
 *             a.before = b;
 *     }
 */
public class LinkedHashMapAnnotation {
    /*LinkedHashMap extends HashMap*/
//    重写了父类的这些空方法
    /*    void afterNodeAccess(Node<K,V> p) { }   // move node to last
    void afterNodeInsertion(boolean evict) { }  // possibly remove eldest
    void afterNodeRemoval(Node<K,V> p) { }*/

//    有头有尾的双向列表
    /**
     * The head (eldest) of the doubly linked list.
     */
    /*transient LinkedHashMap.Entry<K,V> head;*/

    /**
     * The tail (youngest) of the doubly linked list.
     */
    /*transient LinkedHashMap.Entry<K,V> tail;*/


    /**LinkedHashMap 重写了HashMap的newNode方法--》返回的实际是Entry(extends Node) 但是Entry有pre和next两个指针。*/
/*    HashMap.Node<K,V> newNode(int hash, K key, V value, HashMap.Node<K,V> e) {
        LinkedHashMap.Entry<K,V> p =
                new LinkedHashMap.Entry<K,V>(hash, key, value, e);
        linkNodeLast(p);
        return p;
    }*/
}
