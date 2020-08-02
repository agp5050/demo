package com.agp.demo.codedesign;

public class Singleton {
   private volatile static Singleton instance;
   Object socket;

   private Singleton() {
       try {
           Thread.sleep(1000L);
       } catch (InterruptedException e) {
           e.printStackTrace();
       }
       this.socket = new Object();

   }

   public static Singleton getInstance(){
       if (instance!=null) return  instance;
       else {
           synchronized (Singleton.class){
               if (instance!=null) return instance;
               else {
                   instance =new Singleton();
                   return instance;
               }
           }
       }
   }

    public static void main(String[] args) {
        for (int i=0;i<20;i++){
            new Thread(()->{
                System.out.println(Singleton.getInstance().socket.hashCode());
            }).start();
        }
    }
}
