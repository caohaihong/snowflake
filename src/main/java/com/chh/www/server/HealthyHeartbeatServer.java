package com.chh.www.server;

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
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: chh
 * @Description:
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
    private boolean init = false;


    @Autowired
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    @Autowired
    private ZKClient zkClient;
    @Autowired
    private Properties work;

    @Override
    public void run(String... args) throws Exception {
        File file = new File(workConfFilepath);
        if(file.exists()){
            FileReader reader = new FileReader(file);
            work.load(reader);
        } else {
            file.createNewFile();
        }
        String zNode = work.getProperty("zNode");
        if(!init){
            if(StringUtils.isNotBlank(zNode)){
                Stat stat = zkClient.exits(zNode);
                if(stat != null){
                    String data = zkClient.getData(zNode);
                    if(System.currentTimeMillis() < Long.valueOf(data)){
                        throw new RuntimeException("node check fail, shut down");
                    }
                }
            }
            init = true;
        }

        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {

            if(StringUtils.isNotBlank(zNode)){
                // 有节点信息,更新节点数据
                try {
                    zkClient.setData(zNode, String.valueOf(System.currentTimeMillis()));
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
                        if(StringUtils.isBlank(names[i])){
                            continue;
                        }
                        nodeName += "/" + names[i];
                        Stat stat = zkClient.exits(nodeName);
                        if(stat == null){
                            if(i < names.length - 1){
                                zkClient.createPNode(nodeName, String.valueOf(System.currentTimeMillis()));
                            }else {
                                String node = zkClient.createNode(nodeName, String.valueOf(System.currentTimeMillis()));
                                work.setProperty("zNode", node);
                                work.setProperty("id", node.substring(node.length() - 10, node.length()));
                                FileWriter writer = new FileWriter(file);
                                work.store(writer, "work info");
                            }
                        }
                    }

                } catch (KeeperException | InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }

        }, 0, 3, TimeUnit.SECONDS);
    }
}
