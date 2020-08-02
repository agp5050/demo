package com.agp.demo.zookeeper;

import org.junit.Test;

public class NodeBlocklessLockTest {
    public static void main(String[] args) {
        new NodeBlocklessLockTest().testMultiLockAndRelease();
//        NodeBlocklessLock nodeBlocklessLock=new NodeBlocklessLock();
//        nodeBlocklessLock.lock("/testA","w");
//        int i=0;
//        while (i<10){
//            int finalI = i;
//            new Thread(()->{
//                NodeBlocklessLock nodeBlocklessLock=new NodeBlocklessLock();
//                boolean lock = nodeBlocklessLock.lock("/testA", "w" + finalI);
//                System.out.println(Thread.currentThread().getName()+": lock status "+lock);
//            }).start();
//            ++i;
//        }
    }
    @Test
    public void testSingle(){
        NodeBlocklessLock nodeBlocklessLock=new NodeBlocklessLock();
        boolean lock = nodeBlocklessLock.lock("/testA", "w" + 0);
        System.out.println(Thread.currentThread().getName()+": lock status "+lock);
    }
    @Test
    public void testMultiLockAndRelease(){
        int i=0;
        while (i<10){
            int finalI = i;
            new Thread(()->{
                NodeBlocklessLock nodeBlocklessLock=new NodeBlocklessLock();
                String node="/testA";
                String clientGuid="w"+finalI;
                String tName = Thread.currentThread().getName();
                while (!nodeBlocklessLock.lock(node, clientGuid)){
                    System.out.println(tName+" try locking...");
                    try {
                        Thread.currentThread().sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(tName+" locked success...");
                while (!nodeBlocklessLock.release(node,clientGuid)){
                    System.out.println(tName+" try releasing lock ...");
                    try {
                        Thread.currentThread().sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(tName+" released lock...");
            }).start();
            ++i;
        }
    }
}
