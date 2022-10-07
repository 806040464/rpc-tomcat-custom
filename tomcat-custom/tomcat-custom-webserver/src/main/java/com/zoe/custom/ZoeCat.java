package com.zoe.custom;

/**
 * @author zhaoccf
 * @version 1.0.0
 * @description 提供给码农的启动类
 * @date 2022/10/7 19:18
 */
public class ZoeCat {
    public static void run(String[] args) throws Exception {
        ZoeCatServer zoeCatServer = new ZoeCatServer("com.zoe.custom.webapp");
        zoeCatServer.start();
    }
}
