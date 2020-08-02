package com.agp.demo.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class SimpleTest {
    static ZooKeeper zk = null;
    static final Object mutex = new Object();

    String root;

   public void syncPrimitive(String address)
            throws KeeperException, IOException {
        if(zk == null){
            System.out.println("Starting ZK:");
            zk = new ZooKeeper(address, 15000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    System.out.println("get The Event :"+watchedEvent.getType());
                }
            });
            System.out.println(zk.getState());
            String s = null;

            try {
                s = zk.create("/test", "this is data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE , CreateMode.PERSISTENT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("create node response:"+s);
            System.out.println("Finished starting ZK: " + zk);
        }
    }

    public static void main(String[] args) {
       String str="192.168.42.129";
        try {
            new SimpleTest().syncPrimitive(str);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
