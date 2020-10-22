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
    static  int RESIZE_STAMP_BITS=16;
    public static void main(String[] args) {
        System.out.println(resizeStamp(16));
        System.out.println(1<<15);
        System.out.println((1<<15)>>>16);
        System.out.println(23 << 32 );
        System.out.println(32795-(1<<15));
        System.out.println(-1 >>> 32);
        System.out.println(-1 >>> 31);
        System.out.println(-1 >>> 33);
        System.out.println(1 >>>33);
        System.out.println(1 << 30);
    }
    static final int resizeStamp(int n) {
        return Integer.numberOfLeadingZeros(n) | (1 << (RESIZE_STAMP_BITS - 1));
    }
}
