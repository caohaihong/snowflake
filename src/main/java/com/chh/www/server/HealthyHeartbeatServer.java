package com.chh.www.server;

import com.chh.www.bean.AtomicCounterBean;
import com.chh.www.zk.ZKClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: chh
 * @Description: 初始化节点，启动心跳、时间点重置
 * @Date: Created in 11:32 2019-03-23
 * @Modified:
 */
@Slf4j
@Component
public class HealthyHeartbeatServer implements CommandLineRunner {
    @Value("${work.conf.znode}")
    private String zNodeName;
    @Value("${work.conf.filepath}")
    private String workConfFilepath;
    @Value("${zk.heartbeat.interval}")
    private Integer heartbeatInterval;
    @Value("${application.start.wait.milliseconds.max}")
    private Integer startWaitMax;
    @Value("${zk.node.time.check.times.max}")
    private Integer timeCheckTimesMax;
    private String zNode;
    private File file;
    private Integer nodeTimeCheckTimes = 0;
    private boolean initialized = false;


    @Autowired
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    @Autowired
    private ZKClient zkClient;
    @Autowired
    private Properties work;

    @Autowired
    private AtomicCounterBean atomicCounterBean;

    @Override
    public void run(String... args) throws Exception {
        file = new File(workConfFilepath);
        if (file.exists()) {
            // 读取本地节点信息
            FileReader reader = new FileReader(file);
            work.load(reader);
            initWorkId();
        } else {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        zNode = work.getProperty("zNode");

        nodeTimeCheck();
        heartbeatStart();
        serialClearStart();
    }

    /**
     * 检查节点时间
     *
     * @throws Exception
     */
    private void nodeTimeCheck() throws Exception {
        if(StringUtils.isBlank(zNode)){
            return;
        }
        Stat stat = zkClient.exits(zNode);
        if(stat == null){
            return;
        }
        String data = zkClient.getData(zNode);
        Long nodeTime = Long.valueOf(data);
        if(System.currentTimeMillis() > nodeTime + heartbeatInterval){
            return;
        }
        if(nodeTime - System.currentTimeMillis() > startWaitMax){
            // 节点时间记录与本机时间相差超过了限制
            throw new RuntimeException("节点时间记录与本机时间相差过大");
        }

        if(++nodeTimeCheckTimes > timeCheckTimesMax){
            throw new RuntimeException("检查节点时间次数超出限制");
        }

        Thread.sleep(nodeTime - System.currentTimeMillis() + heartbeatInterval);
        nodeTimeCheck();
    }

    /**
     * 启动心跳
     */
    private void heartbeatStart() {
        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
            String nodeInfo = String.valueOf(System.currentTimeMillis());

            if (StringUtils.isNotBlank(zNode)) {
                // 有节点信息,更新节点数据
                try {
                    zkClient.setData(zNode, nodeInfo);
                    log.info("zk node info update, info is [{}]", nodeInfo);
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                // 无节点信息创建节点
                try {
                    log.info("create node");
                    String[] names = zNodeName.split("/");
                    String nodeName = "";
                    for (int i = 0; i < names.length; i++) {
                        if (StringUtils.isBlank(names[i])) {
                            continue;
                        }
                        nodeName += "/" + names[i];
                        Stat stat = zkClient.exits(nodeName);
                        if (Objects.nonNull(stat)) {
                            continue;
                        }
                        if (i < names.length - 1) {
                            zkClient.createPNode(nodeName, String.valueOf(System.currentTimeMillis()));
                        } else {
                            zNode = zkClient.createNode(nodeName, nodeInfo);
                            work.setProperty("zNode", zNode);
                            work.setProperty("id", zNode.substring(zNode.length() - 10));
                            FileWriter writer = new FileWriter(file);
                            work.store(writer, "work info");
                        }
                    }
                    initWorkId();
                    log.info("zk node info init, info is [{}]", nodeInfo);
                } catch (KeeperException | InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }


        }, 0, heartbeatInterval, TimeUnit.MILLISECONDS);
    }

    /**
     * 启动每秒更新时间戳、序号
     */
    private void serialClearStart(){
        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> atomicCounterBean.updateTimeAndSerial(), 0, 1, TimeUnit.SECONDS);
    }

    /**
     * 初始化机器id
     */
    private void initWorkId(){
        if(!initialized && StringUtils.isNotBlank(work.getProperty("id"))){
            atomicCounterBean.setWorkId(Integer.parseInt(work.getProperty("id")));
            initialized = true;
        }
    }
}
