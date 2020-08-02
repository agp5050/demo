package com.agp.demo.zookeeper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

/**
 * 无阻塞锁，不添加watch，不等待。 自己重试
 */
@Slf4j
@Data
public class NodeBlocklessLock  implements ZookeeperLock {
    private volatile ZooKeeper zooKeeper=null;
    @Override
    public boolean lock(String guidNodeName, String clientGuid) {
        boolean result = false;

        try {
            if (getZooKeeper().exists(guidNodeName, false) == null) {

                getZooKeeper().create(guidNodeName, clientGuid.getBytes(),

                        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

                byte[] data = getZooKeeper().getData(guidNodeName, false, null);

                if (data != null && clientGuid.equals(new String(data))) {

                    result = true;

                }

            }
        } catch (KeeperException e) {
//            e.printStackTrace();
        } catch (InterruptedException e) {
//            e.printStackTrace();
        }

        return result;
    }

    private ZooKeeper getZooKeeper() {
        if (zooKeeper==null){
            synchronized (ZooKeeper.class){
                if (zooKeeper==null){
                    try {
                        zooKeeper=new ZooKeeper("192.168.42.129:2181",30000,(watchEvent)->{
                            log.info("watch event Type:{}",watchEvent.getType());
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println(zooKeeper.hashCode());
        return zooKeeper;
    }

    @Override
    public boolean release(String guidNodeName, String clientGuid) {
        boolean result = false;

        Stat stat = new Stat();

        byte[] data;
        try {
            data = getZooKeeper().getData(guidNodeName, false, stat);
            if (data != null && clientGuid.equals(new String(data))) {

                getZooKeeper().delete(guidNodeName, stat.getVersion());

                result = true;

            }
        } catch (KeeperException e) {
//            e.printStackTrace();
        } catch (InterruptedException e) {
//            e.printStackTrace();
        }



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
