package com.zoe.custom.servlet;

/**
 * @author zhaoccf
 * @version 1.0.0
 * @description 仿照Servlet添加处理GETPOST请求抽象类，子类去实现
 * @date 2022/10/7 19:19
 */
public abstract class ZoeServlet {
    public abstract void doGet(ZoeRequest request, ZoeResponse response) throws Exception;

    public abstract void doPost(ZoeRequest request, ZoeResponse response) throws Exception;
}
