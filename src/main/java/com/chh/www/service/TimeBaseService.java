package com.chh.www.service;

import com.chh.www.bean.AtomicCounterBean;
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

    @Autowired
    private AtomicCounterBean atomicCounterBean;

    public Long get(){
        return atomicCounterBean.getSerial();

    }
}
