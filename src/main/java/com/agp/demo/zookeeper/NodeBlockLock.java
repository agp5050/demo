package com.agp.demo.zookeeper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 阻塞锁，添加watch，等待。  //TODO to bee modified...
 */
@Slf4j
@Data
public class NodeBlockLock implements ZookeeperLock {
    private volatile ZooKeeper zooKeeper=null;
    CountDownLatch countDownLatch=new CountDownLatch(1);
    private class ZooKeeperWatcher implements Watcher{
        @Override
        public void process(WatchedEvent event) {
            System.out.println(Thread.currentThread().getName()+"Client Received watch event :"+event.getState()+" type:"+event.getType());
            if (event.getState()== Event.KeeperState.SyncConnected){
                countDownLatch.countDown();
                System.out.println(countDownLatch.getCount()+" x count");
            }else{
                countDownLatch.countDown();
                countDownLatch=null;
            }
        }
    }
    @Override
    public boolean lock(String guidNodeName, String clientGuid) {
        boolean locked=false;
        try{
            getZooKeeper().create(guidNodeName, clientGuid.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println(Thread.currentThread().getName()+" Locked for "+clientGuid+" success.");

        } catch (Exception e) {
            int retryTimes=1;
            while (!locked){
                try {
                    //每次执行 exists都会向
                    Stat stat = zooKeeper.exists(guidNodeName, true);
                    if (stat!=null){
                        this.countDownLatch=new CountDownLatch(1);
                        System.out.println(countDownLatch.hashCode()+" hashcode");
                        this.countDownLatch.await();
                        this.countDownLatch=null;
                    }
                    retryTimes++;
                    getZooKeeper().create(guidNodeName, clientGuid.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                    locked=true;
                    System.out.println(Thread.currentThread().getName()+" has retry locked for "+retryTimes+" times.");
                    System.out.println(Thread.currentThread().getName()+" Locked for "+clientGuid+" success.");
                }catch (Exception e1){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    continue;
                }

            }

        }
        System.out.println(Thread.currentThread().getName()+" finished locking..");
        return locked;
    }

    private ZooKeeper getZooKeeper() {
        if (zooKeeper==null){
            synchronized (ZooKeeper.class){
                if (zooKeeper==null){
                    try {
                        zooKeeper=new ZooKeeper("192.168.42.129:2181",30000,new ZooKeeperWatcher());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return zooKeeper;
    }

    @Override
    public boolean release(String guidNodeName, String clientGuid) {
        boolean result =false;

        Stat stat = new Stat();

        byte[] data;
        try {
            data = getZooKeeper().getData(guidNodeName, false, stat);
            if (data != null && clientGuid.equals(new String(data))) {

                getZooKeeper().delete(guidNodeName, stat.getVersion());

                result = true;

            }
//            else {
//                Thread.sleep(100);
//                return release(guidNodeName,clientGuid);
//            }
        } catch (KeeperException e) {
//            e.printStackTrace();
        } catch (InterruptedException e) {
//            e.printStackTrace();
        }


        System.out.println(Thread.currentThread().getName()+" release success...");
        return result;
    }

    @Override
    public boolean exists(String guidNodeName) {
        boolean result = false;

        Stat stat = null;
        try {
            stat = getZooKeeper().exists(guidNodeName, false);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        result = stat != null;

        return result;
    }
}
