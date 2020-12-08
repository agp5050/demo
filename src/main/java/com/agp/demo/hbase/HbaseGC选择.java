package com.agp.demo.hbase;

/**
 * G1类似于CMS，也会分年轻代和老年代。
 * 不同的地方在于，G1的年轻代并不像CMS那样分配两个连续的内存块，
 * 然后两个内存块交替使用。
 *
 * G1的年轻代是由分散的多个Region组成，
 * 而且Region的个数会随着前面多次young GC的STW时间动态调整。
 *
 * 若之前的young GC耗时比较长，则G1会调小young区大小
 * 反之则调大，因为更大的young区明显会导致更长的STW耗时。
 *
 * 另外，G1一旦触发老年代垃圾回收，会将待回收的Region分成若干批次，
 * 每一批次从young区和old区中按照Garbage First策略选若干个Region进行垃圾回收，
 * 每一批垃圾回收都叫做mixed GC。
 *因此G1本质上是将一次大的内存整理过程分摊成多次小的内存整理过程，
 * 从而达到控制STW延迟和避免full GC的目的。
 *
 * 综上所述，CMS和G1 GC本质区别如下：
 *
 *  •CMS老年代GC并不会挪动对象，只有在做full GC的时候才会挪动对象，
 *  处理碎片问题。所以，理论上使用CMS无法避免STW的full GC。
 *  而G1可以通过多次mixed GC增量地处理内存碎片，
 *  所以，G1有能力完全避免STW的full GC。
 *
 *  •由于G1的老年代回收是增量式的，所以G1更加适合大堆。
 *
 * 3. G1 GC核心参数
 *
 * •MaxGCPauseMillis ：每次G1 GC的目标停顿时间，这是一个软限制，
 * 也就是G1会尽量控制STW的时间不超过该值，但不保证每次STW都严格小于该值。
 *
 * •G1NewSizePercent ：young区占比的下限。
 * G1会根据MaxGCPauseMillis动态调整young区的大小，
 * 但要保证young区占比肯定大于或等于该值。
 *
 * •InitiatingHeapOccupancyPercent（IHOP）：G1开始进行并发标记的阈值。
 * 并发标记之后，就会进入Mixed GC周期。
 * 注意，一次Mixed GC周期内会执行多次Mixed GC。
 *
 * •MaxTenuringThreshold：一个对象最多经历多少次young GC会被放入老年代。
 *
 *
 * •G1HeapRegionSize ：G1会把堆分成很多Region，
 * G1HeapRegionSize用来控制每个Region的大小。
 * 注意这里需要写成32m，而不能写成32。
 *
 * •G1OldCSetRegionThresholdPercent ：
 * 一次mixed GC最多能回收多大的OldRegion空间。
 *
 * 
 *
 */
public class HbaseGC选择 {
}
