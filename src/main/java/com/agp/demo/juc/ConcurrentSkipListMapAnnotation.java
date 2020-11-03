package com.agp.demo.juc;


/**
 * 当我们希望快速存取<Key, Value>键值对时我们可以使用HashMap。
 * 当我们希望在多线程并发存取<Key, Value>键值对时，我们会选择ConcurrentHashMap。
 * TreeMap则会帮助我们保证数据是按照Key的自然顺序或者compareTo方法指定的排序规则进行排序。
 * OK，那么当我们需要多线程并发存取<Key, Value>数据并且希望保证数据有序时，我们需要怎么做呢？
 * 也许，我们可以选择ConcurrentTreeMap。不好意思，JDK没有提供这么好的数据结构给我们。
 * 当然，我们可以自己添加lock来实现ConcurrentTreeMap，但是随着并发量的提升，lock带来的性能开销也随之增大。
 * Don't cry......，JDK6里面引入的ConcurrentSkipListMap也许可以满足我们的需求。
 *
 *
 */
public class ConcurrentSkipListMapAnnotation {
}
