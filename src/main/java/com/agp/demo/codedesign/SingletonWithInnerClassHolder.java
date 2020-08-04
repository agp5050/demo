package com.agp.demo.codedesign;

public class SingletonWithInnerClassHolder {
    private static class SingletonHolder {
        static SingletonWithInnerClassHolder INSTANCE=new SingletonWithInnerClassHolder();
        static {
            System.out.println(INSTANCE.hashCode()+" static Inner");
        }
    }
    private SingletonWithInnerClassHolder(){}
    public static SingletonWithInnerClassHolder getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public static void main(String[] args) {
//        for (int i=0;i<50;i++){
//            new Thread(()->{
//                System.out.println(getInstance().hashCode());
//            }).start();
//        }
        //
        System.out.println(SingletonWithInnerClassHolder.SingletonHolder.class);// 執行此處時，只是引用，不會引起內部實例的初始化

        System.out.println(SingletonHolder.INSTANCE);//執行此時，內部初始化完成才可以返回。




    }
}
