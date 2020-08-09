package com.agp.demo.nio;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * 较大，读写较慢，追求速度
 * M内存不足，不能加载太大数据
 * 带宽不够，即存在其他程序或线程存在大量的IO操作，导致带宽本来就小
 * 不需要进行数据文件操作
 */
public class ZeroCopyDemo {
    public static void main(String[] args) throws IOException {
        File file = new File("D:\\study\\demo\\src\\main\\resources\\testpy.py");
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        FileChannel fileChannel = raf.getChannel();
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("", 8080));
        // 直接使用了transferTo()进行通道间的数据传输
        fileChannel.transferTo(0, fileChannel.size(), socketChannel);
    }
}
