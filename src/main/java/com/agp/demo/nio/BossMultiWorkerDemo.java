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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BossMultiWorkerDemo {
    public static class WorkerThread extends Thread{
        static BlockingQueue<SocketChannel>[] queues;
        static int queueNum=1;
        private boolean boss=false;
        static AtomicInteger id=new AtomicInteger(0);
        static AtomicLong count=new AtomicLong(0);
        private int workerId;
        private Selector selector;
        public WorkerThread(){}
        //Boss线程构造器
        public WorkerThread(Selector selector,int queueNum){
            this.selector=selector;
            if (queueNum>0){
                WorkerThread.queueNum=queueNum;
                queues=new LinkedBlockingQueue[queueNum];
            }
            boss=true;
            for (int i=0;i<queueNum;++i){
                queues[i]=new LinkedBlockingQueue<SocketChannel>();
            }

        }

        public BlockingQueue<SocketChannel> getQueue(){
            if (boss) return null;
            return queues[workerId];
        }

        //Worker构造器
        public  WorkerThread(Selector selector){
            this.selector=selector;
            if (id.get()>=queueNum) throw new RuntimeException("workers outnumber limits");
            this.workerId=id.getAndIncrement();
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName()+" starting");
            while(true) {
                try {
                    while(!boss && !getQueue().isEmpty()){
                        SocketChannel newWorkerChanel = getQueue().take();
                        newWorkerChanel.configureBlocking(false);
                        if (newWorkerChanel.isConnected()){
                            newWorkerChanel.register(selector,SelectionKey.OP_READ);
                        }
                    }
                    /** 必须减worker设定为select（n)非阻塞。如果worker阻塞，但是对应selector
                     * 里面还没有注册client端
                     * 这样就一直阻塞到这里。 boss针对新连接client没问题。*/
                    while ((boss?selector.select():selector.select(10)) >0){
                        Set<SelectionKey> selectionKeys = selector.selectedKeys();
                        Iterator<SelectionKey> iterator = selectionKeys.iterator();
                        while(iterator.hasNext()){
                            SelectionKey next = iterator.next();
                            if (next.isAcceptable()){
                                handleAccept(next);
                            }else if (next.isReadable()){
                                handleRead(next);
                            }
                        iterator.remove();
                        }
                    }




                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    break;
                }

            }
        }

        private void handleRead(SelectionKey next) {
            SocketChannel channel = (SocketChannel)next.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1 << 13);
            while(true){
                try {
                    int read = channel.read(byteBuffer);
                    System.out.println(Thread.currentThread().getName()+" read size:"+read);
                    if (read==0) break;
                    else if (read>0){
                        byteBuffer.flip();
                        channel.write(byteBuffer);
                    }else if (read==-1){
                        //-1代表client端主动断开
                        channel.close();
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleAccept(SelectionKey next) {
            try {
                ServerSocketChannel ss =(ServerSocketChannel) next.channel();
                SocketChannel newClient = ss.accept();
                System.out.println("new client connected. "+newClient.getRemoteAddress());
                //轮询往子队列enqueue。
                queues[(int) (count.getAndIncrement()%queueNum)].put(newClient);
                System.out.println("client "+newClient.getRemoteAddress()+" assigned to worker"+((count.get()-1)%queueNum));

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8081));
        serverSocketChannel.configureBlocking(false);
        Selector mainSelector = Selector.open();
        Selector worker1Selector = Selector.open();
        Selector worker2Selector = Selector.open();
        WorkerThread boss=new WorkerThread(mainSelector,2);
        WorkerThread worker1=new WorkerThread(worker1Selector);
        WorkerThread worker2=new WorkerThread(worker2Selector);
//        WorkerThread worker3=new WorkerThread(worker2Selector); /*workers outnumber limits*/
        serverSocketChannel.register(mainSelector, SelectionKey.OP_ACCEPT);
        boss.start();
//        Thread.sleep(2000);
        worker1.start();
        worker2.start();
        System.out.println("service is running...");



    }
}

