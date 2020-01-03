package com.chh.www.bean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.StampedLock;

/**
 * @Author: chh
 * @Description: snowflake
 * @Date: Created in 4:50 下午 2019/12/25
 * @Modified:
 */
@Slf4j
@Component
public class AtomicCounterBean {
    /**
     * 当前序号对应的时间戳，单位 秒
     */
    private long time;
    /**
     * 序号
     */
    private int serial;

    /**
     * 节点id
     */
    private long workId;

    /**
     * 乐观锁
     */
    private AtomicInteger atomicInteger = new AtomicInteger(0);
    private AtomicInteger atomicIntegerForUpdateTime = new AtomicInteger(0);

    private ReentrantLock lock = new ReentrantLock();
    Condition condition = lock.newCondition();


    /**
     * 初始化数据
     *
     * @param time 时间戳
     * @param serial 序号
     */
    public AtomicCounterBean(long time, int serial) {
        init(time, serial);
    }

    /**
     * 初始化默认数据
     */
    public AtomicCounterBean() {
        init(System.currentTimeMillis() / 1000, 0);
    }

    /**
     * 初始化数据
     *
     * @param time 时间戳
     * @param serial 序号
     */
    private void init(long time, int serial){
        this.time = time;
        this.serial = serial;
    }

    /**
     * 设置机器id
     *
     * @param workId
     */
    public void setWorkId(long workId){
        this.workId = workId;
    }

    /**
     * 获取编号
     *
     * @return
     */
    public long getSerial() {
        while (true){
            if(atomicInteger.compareAndSet(0, 1) && atomicIntegerForUpdateTime.compareAndSet(0, 0)){
                long res = (time << 31) + (workId << 21) + ++serial;
//                log.info("get serial is [{}], time is [{}]", serial, time);
                while (true){
                    if(atomicInteger.compareAndSet(1, 0)){
                        break;
                    }
                }
                return res;
            }else {
//                log.info("compare fail, continue");
            }
        }
    }

    /**
     * 重置时间序号
     *
     * @param time 时间戳，单位秒
     * @param serial 序号
     */
    private void updateTimeAndSerial(long time, int serial){
        atomicIntegerForUpdateTime.set(1);
        log.warn("=========> serial is [{}]", this.serial);
        init(time, serial);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        atomicIntegerForUpdateTime.set(0);
        atomicInteger.set(0);
        log.info("timestamp updated, now is [{}]", time);
    }

    /**
     * 重置时间序号
     */
    public void updateTimeAndSerial(){
        updateTimeAndSerial(System.currentTimeMillis() / 1000, 0);
    }
}
