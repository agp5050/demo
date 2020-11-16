package com.agp.demo.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOSingleThreadWithSelectorDemo {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(8080));
        Selector selector = Selector.open();
        server.configureBlocking(false);
        SelectionKey serverRegisterKey =
                server.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("server started...");
        while (selector.select()>0){
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            int size = selector.keys().size();
            System.out.println("total registered channel is "+size);
            System.out.println(selectionKeys.size()+" channel is ready");
            /*total registered channel is 3
            1 channel is ready
            total registered channel is 3
            1 channel is ready
            total registered channel is 3
            1 channel is ready
            */
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey next = iterator.next();
//                SelectableChannel client = next.channel();
                if (next.isAcceptable()){
                    ServerSocketChannel ss=  (ServerSocketChannel)next.channel();
                    SocketChannel newClient = ss.accept();
                    newClient.configureBlocking(false);
                    newClient.register(selector,SelectionKey.OP_READ);
                    System.out.println("new client :"+newClient.getRemoteAddress()+" connected...");
                }else if(next.isReadable()) {
                    handle(next);
                }
                iterator.remove();
            }
        }


    }

    private static void handle(SelectionKey client) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1 << 13);
        SocketChannel channel = (SocketChannel) client.channel();
        //channel的通道里面的信息不可计量，循环读
        while (true){
            try {
                int read = channel.read(byteBuffer);
                if (read>0){
                    byteBuffer.flip();
                    while (byteBuffer.hasRemaining()){
                        channel.write(byteBuffer);
                    }
                    byteBuffer.clear();
                }else if(read==0){
                    break;
                } else {
                    channel.close();
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
