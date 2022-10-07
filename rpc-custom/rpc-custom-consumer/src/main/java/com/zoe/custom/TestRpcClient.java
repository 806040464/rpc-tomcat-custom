package com.zoe.custom;

import com.zoe.custom.interfaces.SkuService;
import com.zoe.custom.interfaces.UserService;
import com.zoe.custom.stub.RpcProxy;

/**
 * @author zhaoccf
 * @version 1.0.0
 * @description
 * @date 2022/10/7 16:47
 */
public class TestRpcClient {
    public static void main(String[] args) {
        UserService userService = (UserService) RpcProxy.create(UserService.class);
        String user = userService.findById();
        System.out.println(user);
        SkuService skuService = (SkuService) RpcProxy.create(SkuService.class);
        String sku = skuService.findByName("zoe");
        System.out.println(sku);
    }
}
