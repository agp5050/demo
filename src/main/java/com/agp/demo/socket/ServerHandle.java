package com.agp.demo.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 将Server适配Thread。
 */
public class ServerHandle implements Runnable{

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private volatile boolean started;

    public ServerHandle(int port){
        try {
            //创建选择器
            selector = Selector.open();
            //打开监听通道
            serverSocketChannel = ServerSocketChannel.open();
            //设置为非阻塞模式
            serverSocketChannel.configureBlocking(false);
            //判定端口，并设定连接队列最大为1024
            serverSocketChannel.socket().bind(new InetSocketAddress(port),1024);
            //监听客户端请求
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            //标记启动标志
            started = true;
            System.out.println("服务器已启动，端口号为：" + port);
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }
    public void shop(){
        started = false;
    }

    private void doWrite(SocketChannel channel, String response) throws IOException {
        byte[] bytes = response.getBytes();
        ByteBuffer wirteBuffer = ByteBuffer.allocate(bytes.length);
        wirteBuffer.put(bytes);
        wirteBuffer.flip();
        channel.write(wirteBuffer);
    }

    private void handleInput(SelectionKey key) throws IOException{
        if (key.isValid()){
            if (key.isAcceptable()){
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                socketChannel.register(selector, SelectionKey.OP_READ);
            }
            if (key.isReadable()){
                SocketChannel socketChannel = (SocketChannel) key.channel();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                int readBytes = socketChannel.read(byteBuffer);
                if (readBytes > 0){
                    byteBuffer.flip();
                    byte[] bytes = new byte[byteBuffer.remaining()];
                    byteBuffer.get(bytes);
                    String expression = new String(bytes, "UTF-8");
                    System.out.println("服务器收到的信息：" + expression);
                    doWrite(socketChannel, "+++++" + expression + "+++++");
                } else if (readBytes < 0){
                    key.cancel();
                    socketChannel.close();
                }
            }
        }
    }

    @Override
    public void run() {
        //循环遍历
        while (started) {
            try {
                selector.select();
                //System.out.println(selector.select());
                Set<SelectionKey> keys = selector.selectedKeys();
                //System.out.println(keys.size());
                Iterator<SelectionKey> iterator = keys.iterator();
                SelectionKey key;
                while (iterator.hasNext()){
                    key = iterator.next();
                    iterator.remove();
                    try {
                        handleInput(key);
                    } catch (Exception e){
                        if (key != null){
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            }catch (Throwable throwable){
                throwable.printStackTrace();
            }
        }
    }
}