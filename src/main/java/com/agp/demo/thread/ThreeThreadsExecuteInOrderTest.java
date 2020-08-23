package com.agp.demo.thread;

import org.junit.Test;

public class ThreeThreadsExecuteInOrderTest {
    public static void main(String[] args) {
        new ThreeThreadsExecuteInOrderTest().testInOrder();
    }
    @Test
    public void testWait(){
        try {
            wait(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("after wait.");
    }
    @Test
    public void testInOrder(){
        ThreeThreadsExecuteInOrder object=new ThreeThreadsExecuteInOrder();
        for(int i=0;i<30;i++){
            int finalI = i;
            new Thread(()->{
                int j= finalI %3;
                switch (j){
                    case 0: object.first();
                        break;
                    case 1: object.second();
                        break;
                    case 2: object.third();
                        break;
                }
            }).start();
        }
    }
}
