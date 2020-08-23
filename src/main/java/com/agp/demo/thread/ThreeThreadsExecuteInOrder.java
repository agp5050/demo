package com.agp.demo.thread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 3个线程调用3个方法顺序调用。
 */
public class ThreeThreadsExecuteInOrder {
    static ReentrantLock lock=new ReentrantLock();
    static Condition firstPark=lock.newCondition();
    static Condition secondPark=lock.newCondition();
    static Condition thirdPark=lock.newCondition();
    private static volatile int index=0;

    public void first(){
        while (true) {
            lock.lock();
            try{
                if (index==0){
                    System.out.println("first method is invoking...by "+Thread.currentThread().getName());
                    index++;
                    break;
                }
            }catch (Exception e){

            }finally {
                lock.unlock();
            }

        }

    }
    public void second(){
        while (true) {
            lock.lock();
            try{
                if (index==1){
                    System.out.println("second method is invoking...by "+Thread.currentThread().getName());
                    index++;
                    break;
                }
            }catch (Exception e){

            }finally {
                lock.unlock();
            }
        }
    }

    public void third(){
        while (true){
            lock.lock();
            try{
                if (index==2){
                    System.out.println("third method is invoking...by "+Thread.currentThread().getName());
                    index=0;
                    break;
                }
            }catch (Exception e){

            }finally {
                lock.unlock();
            }
        }
    }
}
