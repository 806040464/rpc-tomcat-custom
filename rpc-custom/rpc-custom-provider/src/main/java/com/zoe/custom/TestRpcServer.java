package com.zoe.custom;

import com.zoe.custom.stub.RpcServer;

/**
 * @author zhaoccf
 * @version 1.0.0
 * @description
 * @date 2022/10/7 17:18
 */
public class TestRpcServer {

    public static void main(String[] args) throws Exception {
        new RpcServer(9999).start();
    }
}
