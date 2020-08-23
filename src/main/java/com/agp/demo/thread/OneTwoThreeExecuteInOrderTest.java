package com.agp.demo.thread;

public class OneTwoThreeExecuteInOrderTest {
    public static void main(String[] args) {
        OneTwoThreeExecuteInOrder obj=new OneTwoThreeExecuteInOrder();
        for (int i=0;i<100;i++){
            int finalI = i % 3;
            new Thread(()->{
                switch (finalI){
                    case 0: obj.one(); break;
                    case 1: obj.two(); break;
                    case 2: obj.three(); break;
                };
            }).start();
        }
    }
}
