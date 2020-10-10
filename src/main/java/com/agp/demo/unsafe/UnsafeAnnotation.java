package com.agp.demo.unsafe;

public class UnsafeAnnotation {
    /** Unsafe对象获取  Field f = Unsafe.class.getDeclaredField("theUnsafe");
     f.setAccessible(true);
     Unsafe unsafe = (Unsafe) f.get(null);*/

    /*内存条中每个元器件在5V和0V电压变换下，代表0-1，一个元器件代表一个bit.
    * 8个元器件代表一个Byte。 1G数据需要 8*1024*1024*1024也就是内存条中80多亿个元器件
    * 8个元器件为一个控制单元 */

    /*数据根据类型不同占用不同的内存长度。比如int是4个Byte占用4个内存单元。*/
    /*[]数组是一个连续的内存空间。 每个类型的数组JUC里面都有偏移量。 比如int 偏移量为4. 如果位计算则为<<2
    * 所以内存寻址时 index=0. 地址=base+0<<2。 index=1,地址=base+1<<2  也就是0 4 8 12 +base 这样快速找到内存地址了。*/
    /**JUC下的AtomicIntegerArray 根据偏移量内存快速寻找地址如下
     *  unsafe.getAndSetInt(array, checkedByteOffset(i), newValue);*/


    /*compareAndSet  getAndSet getAndIncrement区别，第一个是比较一次。 第二三个是循环取出比较然后设定新值直到成功*/
}
