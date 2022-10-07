package com.zoe.custom.servlet;

/**
 * @author zhaoccf
 * @version 1.0.0
 * @description
 * @date 2022/10/7 19:20
 */
public interface ZoeResponse {
    // 将响应写入到Channel
    void write(String content) throws Exception;
}
