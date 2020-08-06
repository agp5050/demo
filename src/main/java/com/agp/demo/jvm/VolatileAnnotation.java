package com.agp.demo.jvm;

/**
 * 让线程具备有序性和可见性。  不具有原子性。
 *
 * volatile T1 write到主存的时候会将所有的其它Thread。会通知其它CPU缓存对应的这个a 失效。所以其它Thread会再次执行read load use等操作
 *
 *
 * volatile基于内存屏障 storeload 。。。等等确保有序性。
 *
 * volatile可见性保证： 执行write操作时，JVM发送lock前缀指令给CPU。 CPU计算完毕后立刻写入主存。由于有缓存一致性协议MESI
 * 所以各个CPU都会对总线进行嗅探本地缓存中的数据是否被被人修改。
 * Modified Exclusive Shared Or Invalid   MESI 四种状态
 *Modified  Tlocal
 * Exclusive TLocal
 * shared  T t t
 * Invalid 失效
 *
 */
public class VolatileAnnotation {
    static volatile int a=0;
    static  int b=0;
    public static void main(String[] args) {
        for (int i=0;i<300;i++){
            new Thread(()->{
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                a++;b++;}).start();
        }
        System.out.println("a:"+a);  // a的值小于30. 因为这个不具备原子性。volatile。
        System.out.println("b:"+b);
    }
}
