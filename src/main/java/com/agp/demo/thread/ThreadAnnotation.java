package com.agp.demo.thread;

/**
 * 线程状态：
 * NEW ：
 *  not yet started
 * RUNNING:
 *  is running
 * BLOCKED:
 *  be blocked waiting for a monitor block.  --> object.wait(); synchronized
 * WAITING:   object.wait(); 等待另一个线程notify or notifyAll
 *  join(); 主线程等待另一个线程terminated ==》             while (isAlive()) {wait(0);}  ===》当前thread
 *  LockSupport.park
 *
 *TIMED_WAITING 等待另一个线程terminated until overtime
 *
 * TERMINATED: 线程结束
 *
 */
public class ThreadAnnotation {
    Thread t;
    Object obj;
}
