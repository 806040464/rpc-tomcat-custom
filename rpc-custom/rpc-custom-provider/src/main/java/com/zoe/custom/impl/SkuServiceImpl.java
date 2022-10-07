package com.zoe.custom.impl;


import com.zoe.custom.interfaces.SkuService;

public class SkuServiceImpl implements SkuService {
    @Override
    public String findByName(String name) {
        return "sku{}:" + name;
    }
}
