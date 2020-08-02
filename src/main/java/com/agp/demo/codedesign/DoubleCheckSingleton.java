package com.agp.demo.codedesign;

/**
 * @author : houqian
 * @version : 1.0
 * @since : 2020/7/31
 */
public class DoubleCheckSingleton {

 /**
  * 必须添加 volatile 关键字才确保安全性。
  */
 private static /* volatile */ DoubleCheckSingleton INSTANCE;

 Object socket;

 public DoubleCheckSingleton() {
  this.socket = new Object();
 }

 public static DoubleCheckSingleton getInstance() {
  // 若没有对 INSTANCE 变量进行 volatile 修饰，此时线程 2 是可以读INSTANCE 变量的，此时INSTANCE 已经不是null 了，
  // 会直接 return，但是 INSTANCE 里的 socket 变量还未实例化，依旧是 null，于是就坑爹了，线程 2 会抛 NPE
  if (INSTANCE == null) {
   synchronized (DoubleCheckSingleton.class) {
    if (INSTANCE == null) {

     // Q: 不对共享变量加 volatile 为什么有问题？会有什么问题？
     // A:
     // 对象初始化的过程是分为几个步骤的：初始化一块内存空间，指针指向内存空间，给对象里的变量进行初始化
     // 有可能出现一个情况，就是指令重排，可能会导致说
     // DoubleCheckSingleton 对象里面的 socket 对象还没在构造函数里初始化，还是 null
     // 但是这个对象对应的内存空间和地址已经分配好了，指针指过去了，此时 DoubleCheckSingleton 自己
     // 此时还没有释放锁
     // 此时 DoubleCheckSingleton 对象不是 null 了，但是 socket 是 null

     INSTANCE = new DoubleCheckSingleton();
     // Q: 加了 volatile 后为什么没有问题？
     // 因为对加了 volatile 关键字的共享变量的写操作附近会加入内存屏障，
     // 会确保说只有共享变量实例化完成后，才能对共享变量读
     // 也就是说，直到 INSTANCE 完全实例化完成之前，第一层的INSTANCE == null是无法执行的，线程 2 会等待，等实例化完成后，
     // 线程 2读到的 INSTANCE 就是实例化完成的对象了，socket 肯定也不是 null 了
    }
   }
  }
  return INSTANCE;
 }

 public static void main(String[] args) {
  Thread thread1 = new Thread() {
   @Override
   public void run() {
    DoubleCheckSingleton instance = DoubleCheckSingleton.getInstance();
    System.out.println(instance.socket.toString());
   }
  };

  Thread thread2 = new Thread() {
   @Override
   public void run() {
    DoubleCheckSingleton instance = DoubleCheckSingleton.getInstance();
    System.out.println(instance.socket.toString());
   }
  };

  thread1.start();
  thread2.start();

 }

}