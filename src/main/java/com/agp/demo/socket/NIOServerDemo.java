package com.agp.demo.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

public class NIOServerDemo {
    private ByteBuffer readBuffer = ByteBuffer.allocateDirect(1024);
    private ByteBuffer writeBuffer = ByteBuffer.allocateDirect(1024);
    private Selector selector;
    private volatile boolean started;
    private ServerSocketChannel serverSocketChannel;
    public NIOServerDemo() throws IOException {
        //创建选择器
        this.selector = Selector.open();;

        //打开监听通道
        serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.configureBlocking(false);
//        ServerSocket serverSocket = serverSocketChannel.socket();
        //判定端口，并设定连接队列最大为1024
        serverSocketChannel.bind(new InetSocketAddress(8080),1024);
        System.out.println("listening on port 8080");


        // 绑定channel的accept
        SelectionKey register = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public static void main(String[] args) throws Exception{
        new NIOServerDemo().go();
    }

    private void go() throws Exception{

        // block api
        //selector.select() 多路复用器，模拟调用内核select ,poll ,epoll。如果返回值大于0，说明有对应的
        //socket chanel 来数据了。
        while(started && selector.select()>0){

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while(iterator.hasNext()){
                SelectionKey selectionKey = iterator.next();
                //可能会导致死循环 ,比如这个里面是isAcceptable，里面会创建一个新的chanel，并注册到selector。
                //也就是添加到了iter，如果不remove，循环遍历的时候还会再注册一个channel，以此循环就死循环了。
                iterator.remove();
                // 新连接 Tests whether this key's channel is ready to accept a new socket
                //     * connection
                if(selectionKey.isAcceptable()){
                    System.out.println("isAcceptable");
                    ServerSocketChannel server = (ServerSocketChannel)selectionKey.channel();

                    // 新注册channel
                    SocketChannel socketChannel  = server.accept();
                    if(socketChannel==null){
                        continue;
                    }
                    socketChannel.configureBlocking(false);
                    // 注意！这里和阻塞io的区别非常大，在编码层面之前的等待输入已经变成了注册事件，这样我们就可以在等待的时候做别的事情，
                    // 比如监听更多的socket连接，也就是之前说了一个线程监听多个socket连接。这也是在编码的时候最直观的感受
                    //因为是新连接，所以也要注册到selector上面。
                    socketChannel.register(selector, SelectionKey.OP_READ| SelectionKey.OP_WRITE);


                    ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
                    buffer.put("hi new channel".getBytes());
                    buffer.flip();
                    socketChannel.write(buffer);
                }

                // 服务端关心的可读，意味着有数据从client传来了，根据不同的需要进行读取，然后返回
                if(selectionKey.isReadable()){
                    System.out.println("isReadable");
                    SocketChannel socketChannel = (SocketChannel)selectionKey.channel();

                    readBuffer.clear();
                    int read = socketChannel.read(readBuffer);
                    if (read>0){

                        readBuffer.flip();

                        String receiveData= Charset.forName("UTF-8").decode(readBuffer).toString();
                        System.out.println("receiveData:"+receiveData);

                        // 把读到的数据绑定到key中
                        selectionKey.attach("server message echo:"+receiveData);
                    }else if(read==0){ //The number of bytes read, possibly zero, or <tt>-1</tt> if the
                                        //     *          channel has reached end-of-stream
                        continue;
                    }else {  // -1  close_wait 死循环 100% CPU
                        socketChannel.close();  //需要关掉，以免导致死循环。
                    }
                }

                // 实际上服务端不在意这个，这个写入应该是client端关心的，这只是个demo,顺便试一下selectionKey的attach方法
                if(selectionKey.isWritable()){
                    SocketChannel socketChannel = (SocketChannel)selectionKey.channel();

                    String message = (String) selectionKey.attachment();
                    if(message==null){
                        continue;
                    }
                    selectionKey.attach(null);

                    writeBuffer.clear();
                    writeBuffer.put(message.getBytes());
                    writeBuffer.flip();
                    while(writeBuffer.hasRemaining()){
                        socketChannel.write(writeBuffer);
                    }
                }
            }
        }
    }
}
