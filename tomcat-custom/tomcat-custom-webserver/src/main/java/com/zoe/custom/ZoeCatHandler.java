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
    //线程安全  servlet--> 对象
    private Map<String, ZoeServlet> nameToServletMap;
    //线程不安全  servlet--> 全限定名称
    private Map<String, String> nameToClassNameMap;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof HttpRequest)) {
            return;
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
                synchronized (this) {
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
