package com.chh.www.controller;

import com.chh.www.dto.ValueResult;
import com.chh.www.service.TimeBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

/**
 * @Author: chh
 * @Description:
 * @Date: Created in 15:50 2019-03-22
 * @Modified:
 */
@Slf4j
@RestController
@ResponseBody
@RequestMapping("/time_base")
public class TimeBaseController {
    @Autowired
    private TimeBaseService timeBaseService;

    @GetMapping
    public WebAsyncTask<ValueResult<Long>> get(){
        return new WebAsyncTask<>(() -> {
            return new ValueResult<>(timeBaseService.get());
        });
    }
}
