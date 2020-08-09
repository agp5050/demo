package com.agp.demo.nio;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 直接内存则介于两者之间，效率一般且可操作文件数据。
 * 直接内存（mmap技术）将文件直接映射到内核空间的内存，
 * 返回==一个操作地址（address）==，
 * 它解决了文件数据需要拷贝到JVM才能进行操作的窘境。
 * 而是**直接在内核空间**直接进行操作，
 * 省去了内核空间拷贝到用户空间这一步操作。
 *

 */
public class NIOCopyDemo {
    public static void main(String[] args) throws IOException {
        File file = new File("test.zip");
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        FileChannel fileChannel = raf.getChannel();
        MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
    }
}
