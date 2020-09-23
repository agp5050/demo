package com.agp.demo.thread;

/**
 * Object wait notify notifyAll
 * wait 是当前运行线程，进入object对象的object_monitor的wait_set里面
 * 当前线程必须先取得锁才可以。 notify也是，必须持有锁才可以通知其它等待线程。
 * notify通知wait_set里面的第一个线程，进入entry_list，但是不释放锁，等待当前线程执行ObjectMonitor::exit执行完时
 * 再进行抢夺。
 * Thread.sleep
 *
 * 用main作为主线程，然后wait是不合适的
 */
public class WaitSleepAnnotation {
   private static Object lock = new Object();
    public static void main(String[] args) {

        new Thread(()->{
            try {
                Thread.sleep(2000);
                synchronized (lock){
                    System.out.println(Thread.currentThread().getName()+" get the lock "+lock.hashCode());
                    Thread.sleep(1000);
                    System.out.println(Thread.currentThread().getName()+" sleeping...");
                    lock.notify();
                }
                System.out.println(Thread.currentThread().getName()+" released the lock , and notifyAll wait_set thread ");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        testWait();


        System.out.println("finished...");

    }

    public static  void testWait(){
        synchronized (lock){
            System.out.println(Thread.currentThread().getName()+" testWait begin...");
            try {
                System.out.println(Thread.currentThread().getName()+" lock.wait begin,released the lock "+lock.hashCode());
                lock.wait();
                System.out.println(Thread.currentThread().getName()+" lock.wait after");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
