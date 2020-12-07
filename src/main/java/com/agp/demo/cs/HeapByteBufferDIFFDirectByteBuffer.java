package com.agp.demo.cs;

/**
 * 堆外内存的优点和缺点
 * 堆外内存，其实就是不受JVM控制的内存。相比于堆内内存有几个优势：
 * 　　1 减少了垃圾回收的工作，因为垃圾回收会暂停其他的工作（可能使用多线程或者时间片的方式，根本感觉不到）
 * 　　2 加快了复制的速度。因为堆内在flush到远程时，会先复制到直接内存（非堆内存），然后在发送；而堆外内存相当于省略掉了这个工作。
 * 　　而福之祸所依，自然也有不好的一面：
 * 　　1 堆外内存难以控制，如果内存泄漏，那么很难排查
 * 　　2 堆外内存相对来说，不适合存储很复杂的对象。一般简单的对象或者扁平化的比较适合。
 *
 * 在原理上，前者可以看出分配的buffer是在heap区域的，
 * 其实真正flush到远程的时候会先拷贝得到直接内存，
 * 再做下一步操作（考虑细节还会到OS级别的内核区直接内存），
 * 其实发送静态文件最快速的方法是通过OS级别的send_file，
 * 只会经过OS一个内核拷贝，而不会来回拷贝
 *
 * 在NIO的框架下，很多框架会采用DirectByteBuffer来操作，
 * 这样分配的内存不再是在java heap上，而是在C heap上，经过性能测试，
 * 可以得到非常快速的网络交互，在大量的网络交互下，
 * 一般速度会比HeapByteBuffer要快速好几倍。
 *
 * 5.5.3 BucketCache
 *
 * BucketCache通过不同配置方式可以工作在三种模式下：heap，offheap和file。
 *
 * heap模式表示这些Bucket是从JVM Heap中申请的；
 * offheap模式使用DirectByteBuffer技术实现堆外内存存储管理；
 * f ile模式使用类似SSD的存储介质来缓存Data Block
 *
 * 实际实现中，HBase将BucketCache和LRUBlockCache搭配使用，称为CombinedBlock-Cache
 *
 * 系统在LRUBlockCache中主要存储Index Block和Bloom Block
 * 而将Data Block存储在BucketCache中。
 * 因此一次随机读需要先在LRUBlockCache中查到对应的Index Block
 *
 * 相比LRUBlockCache，BucketCache实现相对比较复杂。
 * 它没有使用JVM内存管理算法来管理缓存，而是自己对内存进行管理，
 * 因此大大降低了因为出现大量内存碎片导致Full GC发生的风险。
 * 鉴于生产线上CombinedBlockCache方案使用的普遍性，下文主要介绍BucketCache的具体实现方式
 * （包括BucketCache的内存组织形式、缓存写入读取流程等）以及配置使用方式
 *
 * 1. BucketCache的内存组织形式
 *
 */
public class HeapByteBufferDIFFDirectByteBuffer {
}
