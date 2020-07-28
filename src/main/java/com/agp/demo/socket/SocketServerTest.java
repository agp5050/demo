package com.agp.demo.socket;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServerTest {
    public static void main(String[] args) throws IOException {
        new SocketServerTest().test();
//        // TODO 自动生成的方法存根
//
//        String readline = null;
//        String inTemp = null;
//        //String outTemp = null;
//        String turnLine = "\n";
//        final String client = "Client:";
//        final String server = "Server:";
//
//        int port = 9090;
//        //byte ipAddressTemp[] = {127, 0, 0, 1};
//        //InetAddress ipAddress = InetAddress.getByAddress(ipAddressTemp);
//
//        //首先直接创建serversocket
//        ServerSocket serverSocket = new ServerSocket(port);
//
//        Socket socket = serverSocket.accept();
//
//        //创建三个流，系统输入流BufferedReader systemIn，socket输入流BufferedReader socketIn，socket输出流PrintWriter socketOut;
//        BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));
//        BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
//
//        while(readline != "bye"){
//
//            inTemp = socketIn.readLine();
//            System.out.println(client + turnLine + inTemp);
//            System.out.println(server);
//
//            readline = systemIn.readLine();
//
//            socketOut.println(readline);
//            socketOut.flush();    //赶快刷新使Client收到，也可以换成socketOut.println(readline, ture)
//
//            //outTemp = readline;
//
//            //System.out.println(server);
//
//        }
//
//        systemIn.close();
//        socketIn.close();
//        socketOut.close();
//        socket.close();
//        serverSocket.close();
    }
    @Test
    public void test() throws IOException {

        ServerSocket serverSocket=new ServerSocket(9090);
        Socket accept = serverSocket.accept();
        InputStream inputStream = accept.getInputStream();
        OutputStream outputStream = accept.getOutputStream();
        BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(outputStream));
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line=bufferedReader.readLine())!=null){
            System.out.println("Remote said: "+line);
            bufferedWriter.write("Server get the msg:"+line+"\n");
            bufferedWriter.flush();
        }
    }
}
