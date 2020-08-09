package com.agp.demo.socket;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * BIO demo
 */
public class BIOSocketClientTest {
    public static void main(String[] args) throws IOException {
        new BIOSocketClientTest().testConnect();
//        new SocketClientTest().testReadLine();
//            // TODO 自动生成的方法存根
//
//            String readline = null;
//            String inTemp = null;
//            //String outTemp = null;
//            String turnLine = "\n";
//            final String client = "Client:";
//            final String server = "Server:";
//
//            int port = 9090;
//            byte ipAddressTemp[] = {127, 0, 0, 1};
//            InetAddress ipAddress = InetAddress.getByAddress(ipAddressTemp);
//
//            //首先直接创建socket,端口号1~1023为系统保存，一般设在1023之外
//            Socket socket = new Socket(ipAddress, port);
//
//            //创建三个流，系统输入流BufferedReader systemIn，socket输入流BufferedReader socketIn，socket输出流PrintWriter socketOut;
//            BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));
//            BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
//
//            while(readline != "bye"){
//
//                System.out.println(client);
//                readline = systemIn.readLine();
//                //System.out.println(readline);
//
//                socketOut.println(readline);
//                socketOut.flush();    //赶快刷新使Server收到，也可以换成socketOut.println(readline, ture)
//
//                //outTemp = readline;
//                inTemp = socketIn.readLine();
//
//                //System.out.println(client + outTemp);
//                System.out.println(server + turnLine + inTemp);
//
//            }
//
//            systemIn.close();
//            socketIn.close();
//            socketOut.close();
//            socket.close();

    }
    @Test
    public void testConnect() throws IOException {
        Socket socket=new Socket();
        socket.connect(new InetSocketAddress("127.0.0.1",9090));
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        BufferedReader systemIn=new BufferedReader(new InputStreamReader(System.in));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        PrintWriter bufferedWriter = new PrintWriter(new OutputStreamWriter(outputStream));
        String baseStr="Hello I am XX,";
        //String outTemp = null;
        String turnLine = "\n";
        final String client = "Client:";
        final String server = "Server:";
        String serverResp=null;
            bufferedWriter.write(baseStr+1+"\n");
            bufferedWriter.flush();

        new Thread(()->{
            String lin=null;
            while (!"bye".equals(lin)){
                try {
                    lin = systemIn.readLine();
                    bufferedWriter.write(lin+"\n");
                    bufferedWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }).start();
        while ((serverResp = bufferedReader.readLine())!=null) {
            System.out.println(client);
            System.out.println(server + turnLine + serverResp);
        }

        bufferedReader.close();
        bufferedWriter.close();
        systemIn.close();
        socket.close();
    }

    @Test
    public void testReadLine() throws IOException {
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(System.in));
        System.out.print("please input:");
        String s = bufferedReader.readLine();
        System.out.println(s);
    }
}
