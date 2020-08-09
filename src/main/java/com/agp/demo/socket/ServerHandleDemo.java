package com.agp.demo.socket;

public class ServerHandleDemo {
    private static int DEFAULT_POST = 12345;
    private static ServerHandle serverHandle;
    public static void start(){
        start(DEFAULT_POST);
    }

    public static synchronized void start(int post) {
        if (serverHandle != null){
            serverHandle.shop();
        }
        serverHandle = new ServerHandle(post);
        new Thread(serverHandle,"server").start();
    }

    public static void main(String[] args) {
        start();
    }
}
