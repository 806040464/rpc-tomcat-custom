package com.zoe.custom;

import com.zoe.custom.servlet.ZoeServlet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * @author zhaoccf
 * @version 1.0.0
 * @description 业务处理类，负责servlet懒加载并添加缓存
 * @date 2022/10/7 20:09
 */
@AllArgsConstructor
public class ZoeCatHandler extends ChannelInboundHandlerAdapter {
    private static Object lock = new Object();
    //线程安全  servlet--> 对象
    private Map<String, ZoeServlet> nameToServletMap;
    //线程不安全  servlet--> 全限定名称
    private Map<String, String> nameToClassNameMap;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof HttpRequest)) {
            ctx.fireChannelRead(msg);
        }
        HttpRequest request = (HttpRequest) msg;
        String uri = request.uri();
        String servletName = null;
        if (uri.contains("/") && uri.contains("?")) {
            servletName = uri.substring(uri.lastIndexOf("/") + 1, uri.indexOf("?")).toLowerCase();
        }
        ZoeServlet servlet = null;
        if (nameToServletMap.containsKey(servletName)) {
            servlet = nameToServletMap.get(servletName);
        } else if (nameToClassNameMap.containsKey(servletName)) {
            if (null == nameToServletMap.get(servletName)) {
                //synchronized经过JDK优化，性能某些场景不弱于lock
                //避免在静态方法中使用synchronized修饰方法或者使用synchronized (this) ，获取的是类锁，类锁会阻塞其它线程对同步方法的调用，尽量避免对类锁的使用，尤其是在多读多写的场景下
                //单例模式下synchronized (lock)并没有带来多大性能上的提升
                synchronized (lock) {
                    //DCL(双重检查锁)第二次检查，预防两个线程同时通过第一次检查并尝试获取锁，线程A获得锁，线程B等待，A创建对象实例释放，B获得锁，直接再次创建对象实例
                    if (null == nameToServletMap.get(servletName)) {
                        String clsName = nameToClassNameMap.get(servletName);
                        servlet = (ZoeServlet) Class.forName(clsName).newInstance();
                        nameToServletMap.put(clsName, servlet);
                    }
                }
            }
        } else {
            servlet = new DefaultZoeServlet();
        }

        HttpZoeRequest httpZoeRequest = new HttpZoeRequest(request);
        HttpZoeResponse httpZoeResponse = new HttpZoeResponse(request, ctx);
        if (httpZoeRequest.getMethod().equalsIgnoreCase("GET")) {
            servlet.doGet(httpZoeRequest, httpZoeResponse);
        } else if (httpZoeRequest.getMethod().equalsIgnoreCase("POST")) {
            servlet.doPost(httpZoeRequest, httpZoeResponse);
        }
        ctx.close();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
