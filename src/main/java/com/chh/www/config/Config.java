package com.chh.www.config;

import com.chh.www.zk.ZKClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @Author: chh
 * @Description:
 * @Date: Created in 19:06 2019-03-22
 * @Modified:
 */
@Slf4j
@Component
public class Config {
    @Value("${zk.host}")
    private String zkHost;

    @Bean(destroyMethod = "disconnect")
    public ZKClient zkClient() throws IOException, InterruptedException {
        ZKClient zkClient = new ZKClient();
        zkClient.connection(zkHost);
        return zkClient;
    }

    @Bean
    public ScheduledThreadPoolExecutor scheduler(){
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        //延迟任务shutdown后不执行
        executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        executor.setRemoveOnCancelPolicy(true);
        return executor;
    }

    @Bean
    public Properties work(){
        return new Properties();
    }
}
