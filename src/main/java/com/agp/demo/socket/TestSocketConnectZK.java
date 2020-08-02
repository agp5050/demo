package com.agp.demo.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TestSocketConnectZK {
    public static void main(String[] args) throws IOException {
        Socket socket=new Socket();
        socket.connect(new InetSocketAddress("192.168.42.129",2181),3000);
        System.out.println(socket.isConnected());
    }
}
