package com.agp.demo.nio.bio;

import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomTest {
    public static void main(String[] args) throws IOException {
        String path="D:\\study\\demo\\src\\main\\resources\\test";
        //随机写入的时候会覆盖原来数据
        RandomAccessFile file=new RandomAccessFile(path,"rw");
        System.out.println(file.length());
        file.seek(file.length()+10);
        file.writeBytes("王王");
        System.out.println(file.length());
        file.close();
    }
}
