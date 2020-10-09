package com.agp.demo.map;

import java.util.concurrent.ConcurrentHashMap;

/**    static final int spread(int h) {
 return (h ^ (h >>> 16)) & HASH_BITS;
 } 也将前16位和后16位做了运算，更均匀分布  避免i%(capacity-1) 后面分母过小，前面就没作用了

死循环里面如果不能cas设定 数组i的值，则进入下一次循环逻辑。 没有对i作为分桶锁定了

 if (casTabAt(tab, i, null,
 new Node<K,V>(hash, key, value, null)))
 break;

 如果数组 i不为null
 f = tabAt(tab, i = (n - 1) & hash)
 则锁定 这个桶synchronized (f) 进行链表插入或者树插入。
 *
 */
public class ConcurrentHashMapAnnotation {
    ConcurrentHashMap map;
}
