package com.agp.demo.zookeeper;

import org.junit.Test;

public class NodeBlockLockTest {
    public static void main(String[] args) {
        new NodeBlockLockTest().multiThreadTest();
    }
    @Test
    public void testSingle(){
        NodeBlockLock nodeBlockLock=new NodeBlockLock();
        String nodeName="/testB";
        String nodeData="w";
        nodeBlockLock.lock(nodeName,nodeData);
        nodeBlockLock.release(nodeName,nodeData);

    }
    @Test
    public void multiThreadTest(){
        int i=0;
        String nodeData="w"+i;
        String nodeName="/testB";
        while (i<10){
            int finalI = i++;
            new Thread(()->{
                NodeBlockLock nodeBlockLock=new NodeBlockLock();
                nodeBlockLock.lock(nodeName,nodeData+ finalI);
                nodeBlockLock.release(nodeName,nodeData+finalI);
            }).start();
        }
    }
}
