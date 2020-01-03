package com.chh.www.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: chh
 * @Description:
 * @Date: Created in 16:21 2019-03-22
 * @Modified:
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TimeBaseServiceTest {
    @Autowired
    private TimeBaseService timeBaseService;

    @Test
    public void getTest() throws InterruptedException {
        int i = 0;
        int times = 10;
        while (true) {
//            long id = timeBaseService.get();
//            log.info(String.valueOf(id));
            Thread thread = new Thread(() -> {
                while (true){
                    Long id = timeBaseService.get();
                    log.info(String.valueOf(id));
                }

            });
            thread.setName("test-" + i);
            thread.start();
            if(++i >= times){
                break;
            }
        }
        Thread.sleep(100000);
    }

    @Test
    public void subTest(){
        String s = "FirstZnode0000000023";
        String substring = s.substring(s.length() - 10, s.length());
        System.out.println(substring);
    }
}
