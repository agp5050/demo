package com.agp.demo.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

/**
 * With no selectors（未添加多路复用器）, 这个程序，没有移除不用的socket，而且每次不管socket是否发来数据都在轮询。
 *
 * 如果10万个socket。 while里面轮询10万次。里面可能当前时刻也就1%100的有数据，这样轮询太慢。
 *
 * 可以调用系统内核让系统内核告诉你哪些socket来数据了。 就只执行内核命令 ： select,poll,epoll等等。--也就是用多路复用器。
 * 多路复用器只是告诉你socket状态，哪些ready。 给用户返回后，用户对这些candidate进行读取。
 *
 *
 */
public class NIODemo {
    public static void main(String[] args) throws IOException, InterruptedException {
        LinkedList<SocketChannel> clients=new LinkedList<>();
        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(8090));
        server.configureBlocking(false);
        while (true){
            Thread.sleep(1000);
            SocketChannel accept = server.accept();
            if (accept==null){
                System.out.println("no connection ...");
            }else {
                accept.configureBlocking(false);
                System.out.println("remote client port: "+accept.socket().getPort());
                clients.add(accept);
            }
            ByteBuffer byteBuffer=ByteBuffer.allocate(4096);
            for (SocketChannel client:clients){  //串行化  无线程 上下文切换，
                int read = client.read(byteBuffer);
                if (read>0){
                    byteBuffer.flip();  // 将读取的指针翻转。设置为0，以及limit。  读写模式翻转。
                    byte[] msg=new byte[byteBuffer.limit()];
                    byteBuffer.get(msg);
                    System.out.println("remote client said: "+new String(msg));
                    byteBuffer.clear(); //清空让其他client chanel使用。
                }
            }
        }
    }
}
