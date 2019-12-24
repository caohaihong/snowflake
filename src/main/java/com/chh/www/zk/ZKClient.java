package com.chh.www.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: chh
 * @Description:
 * @Date: Created in 19:05 2019-03-22
 * @Modified:
 */
@Slf4j
public class ZKClient implements Watcher {
    private ZooKeeper zooKeeper;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public void connection(String zkHostPort) throws IOException, InterruptedException {
        zooKeeper = new ZooKeeper(zkHostPort, 3000, this);
        countDownLatch.await();
        log.info("zk connect  success");
    }

    public void disconnect() throws InterruptedException {
        zooKeeper.close();
    }

    @Override
    public void process(WatchedEvent event) {
        log.info("watch received event");
        countDownLatch.countDown();
    }

    /**
     * 创建节点
     *
     * @param path
     * @param data
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public String createNode(String path, String data) throws KeeperException, InterruptedException {
        return zooKeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
    }

    /**
     * 创建父节点
     *
     * @param path
     * @param data
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public String createPNode(String path, String data) throws KeeperException, InterruptedException {
        return zooKeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    /**
     * 设置节点数据
     *
     * @param path
     * @param data
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public Stat setData(String path, String data) throws KeeperException, InterruptedException {
        return zooKeeper.setData(path, data.getBytes(), zooKeeper.exists(path, false).getVersion());
    }

    /**
     * 获取节点数据
     *
     * @param path
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public String getData(String path) throws KeeperException, InterruptedException {
        return new String(zooKeeper.getData(path, false, null));
    }

    /**
     * 检查节点是否存在
     *
     * @param path
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public Stat exits(String path) throws KeeperException, InterruptedException {
        return zooKeeper.exists(path, false);
    }
}
