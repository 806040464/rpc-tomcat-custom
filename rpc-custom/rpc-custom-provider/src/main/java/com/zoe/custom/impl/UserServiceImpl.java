package com.zoe.custom.impl;


import com.zoe.custom.interfaces.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public String findById() {
        return "user{id=1,username=zoe}";
    }
}
