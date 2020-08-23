package com.agp.demo.thread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 3个线程调用3个方法顺序调用。
 */
public class OneTwoThreeExecuteInOrder {
    static ReentrantLock lock=new ReentrantLock();
    static Condition firstPark=lock.newCondition();
    static Condition secondPark=lock.newCondition();
    static Condition thirdPark=lock.newCondition();
    private static volatile int index=0;

    public  void one()  {
        lock.lock();
        while (!(index==0)){
            try {
                firstPark.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("one method is invoked ...");
        index++;
        secondPark.signal();
        lock.unlock();

    }
    public    void two(){
        lock.lock();
        while (!(index==1)){
            try {
                secondPark.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("two method is invoked ...");
        index++;
        thirdPark.signal();
        lock.unlock();
    }

    public    void three(){
        lock.lock();
        while (!(index==2)){
            try {
                thirdPark.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("three method is invoked ...");
        index=0;
        firstPark.signal();
        lock.unlock();
    }
}
