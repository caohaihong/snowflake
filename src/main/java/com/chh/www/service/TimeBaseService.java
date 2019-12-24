package com.chh.www.service;

import com.chh.www.zk.ZKClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Properties;

/**
 * @Author: chh
 * @Description:
 * @Date: Created in 15:54 2019-03-22
 * @Modified:
 */
@Slf4j
@Service
public class TimeBaseService {
    private long lastSequenceValue;
    private long lastTime;
    private long workId = -1;

    @Autowired
    private ZKClient zkClient;
    @Autowired
    private Properties work;

    public Long get(){
        if(workId < 0){
            workId = Long.valueOf(work.getProperty("id"));
        }
        long timeMillis = System.currentTimeMillis();
        long sequenceValue;
        synchronized (this){
            if(timeMillis < lastTime){
                throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "时间回调");
            }else if(timeMillis == lastTime){
                lastSequenceValue ++;
            }else {
                lastSequenceValue = 0;
                lastTime = timeMillis;
            }
            sequenceValue = lastSequenceValue;
        }

        log.info("time is : {}, id is : {}", timeMillis, sequenceValue);
        return (timeMillis << 22) + (workId << 12) + sequenceValue;

    }
}
