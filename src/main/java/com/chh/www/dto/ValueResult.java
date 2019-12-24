package com.chh.www.dto;

import lombok.Data;

/**
 * @Author: chh
 * @Description:
 * @Date: Created in 15:05 2019-03-23
 * @Modified:
 */
@Data
public class ValueResult<T> {
    private T data;

    public ValueResult(T data) {
        this.data = data;
    }
}
