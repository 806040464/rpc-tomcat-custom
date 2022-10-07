package com.zoe.custom;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;

/**
 * @author zhaoccf
 * @version 1.0.0
 * @description 静态文件业务处理类
 * @date 2022/10/7 23:13
 */
public class ZoeCatStaticHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        HttpRequest request = (HttpRequest) msg;
        String uri = request.uri();
        //参数不支持favicon.ico
        if (!uri.endsWith(".html") || uri.endsWith("favicon.ico")) {
            //ChannelInboundHandler之间的传递，通过调用 ctx.fireChannelRead(msg) 实现；调用ctx.write(msg) 将传递到ChannelOutboundHandler
            //ChannelOutboundHandler 在注册的时候需要放在最后一个ChannelInboundHandler之前，否则将无法传递到ChannelOutboundHandler
            //流水线pipeline中ChannelOutboundHandler不能放到最后，否则不生效
            //ctx.write()方法执行后，需要调用flush()方法才能令它立即执行
            ctx.fireChannelRead(msg);
        }
        HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
        File html = null;
        String path = null;
        try {
            path = "WEB-INF/web" + uri;
            URL resource = this.getClass().getClassLoader().getResource(path);
            html = new File(resource.getFile());
        } catch (Exception e) {
            // 文件没有发现设置状态为404，此处spacex.html代替404.html
            path = "WEB-INF/web/spacex.html";
            html = new File(this.getClass().getClassLoader().getResource(path).getFile());
            response.setStatus(HttpResponseStatus.NOT_FOUND);
        }
        RandomAccessFile file = new RandomAccessFile(html, "r");
        // 设置文件格式内容
        if (path.endsWith(".html")) {
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        } else if (path.endsWith(".js")) {
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/x-javascript");
        } else if (path.endsWith(".css")) {
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/css; charset=UTF-8");
        }

        if (HttpUtil.isKeepAlive(request)) {
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        ctx.write(response);

        if (ctx.pipeline().get(SslHandler.class) == null) {
            ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
        } else {
            ctx.write(new ChunkedNioFile(file.getChannel()));
        }
        // 写入文件尾部
        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!HttpUtil.isKeepAlive(request)) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
        file.close();
    }
}
