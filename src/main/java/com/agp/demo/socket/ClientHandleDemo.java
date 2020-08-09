package com.agp.demo.socket;

import java.util.Scanner;

public class ClientHandleDemo {

    private static String DEFAULT_HOST = "localhost";
    private static int DEFAULT_PORT = 12345;
    private static ClientHandle clientHandle;
    private static final String EXIT = "exit";

    public static void start() {
        start(DEFAULT_HOST, DEFAULT_PORT);
    }

    public static synchronized void start(String ip, int port) {
        if (clientHandle != null){
            clientHandle.stop();
        }
        clientHandle = new ClientHandle(ip, port);
        new Thread(clientHandle, "Server").start();
    }

    //向服务器发送消息
    public static boolean sendMsg(String msg) throws Exception {
        if (msg.equals(EXIT)){
            return false;
        }
        clientHandle.sendMsg(msg);
        return true;
    }

    public static void main(String[] args) throws Exception {
        start();
        while(true){
            clientHandle.sendMsg(new Scanner(System.in).nextLine());
        }
    }
}
