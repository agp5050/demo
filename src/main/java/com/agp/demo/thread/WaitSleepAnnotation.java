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
 *
 *
 * wait表示，放弃当前synchronized锁，进入monitor wait_set。等到被其它线程notify后再执行，剩余的代码。
 *
 * synchronized和wait不一样。 synchronized  monitor exit后，自动通知其它wait_set的
 *
 */
/*当多个线程同时访问一段同步代码时，这些线程会被放到一个EntrySet集合中，
处于阻塞状态的线程都会被放到该列表当中。
接下来，当线程获取到对象的Monitor时，
Monitor是依赖于底层操作系统的mutex lock来实现互斥的，
线程获取mutex成功，则会持有该mutex，这时其它线程就无法再获取到该mutex。

如果线程调用了wait()方法，那么该线程就会释放掉所持有的mutex，
并且该线程会进入到WaitSet集合（等待集合）中，
等待下一次被其他线程调用notify/notifyAll唤醒。
如果当前线程顺利执行完毕方法，那么它也会释放掉所持有的mutex。*/
/**两个不同的set。 EntrySet和WaitSet。 一个是进入锁，一个是等待锁，等待被唤醒后继续执行当前线程的代码*/
/*synchronized monitorObject::exit后不会执行notifyAll或者notify方法*/
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
