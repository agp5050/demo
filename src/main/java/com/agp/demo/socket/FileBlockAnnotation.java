package com.agp.demo.socket;

/**
 * 用户态内核态 切换
 *
 * FileInputStream BIO  线程调用操作系统读命令 ，等待内核直至读完 线程才能干其他事情
 *
 * NIO： 线程 用FileChannel发起个文件IO操作调用操作系统读取命令，发起之后就返回了，可以干别的事情。这就是非阻塞。但是时不时去轮询下
 * 操作系统读取数据的状态。看看调用的系统copy完成了没。
 *
 * AIO： 线程通知系统读取，系统读到用户buffer，并回调通知用户。
 *
 *
 *
 *
 *
 */
public class FileBlockAnnotation {
}
