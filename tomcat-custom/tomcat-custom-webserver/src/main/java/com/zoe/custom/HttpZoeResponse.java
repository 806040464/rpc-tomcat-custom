package com.zoe.custom;

import com.zoe.custom.servlet.ZoeResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.internal.StringUtil;
import lombok.AllArgsConstructor;

import java.nio.charset.StandardCharsets;

/**
 * @author zhaoccf
 * @version 1.0.0
 * @description 返回写入Response并返回给客户端浏览器
 * @date 2022/10/7 19:47
 */
@AllArgsConstructor
public class HttpZoeResponse implements ZoeResponse {
    private HttpRequest request;
    private ChannelHandlerContext context;

    @Override
    public void write(String content) throws Exception {
        if (StringUtil.isNullOrEmpty(content)) {
            return;
        }

        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(content.getBytes(StandardCharsets.UTF_8)));
        // 获取响应头
        HttpHeaders headers = response.headers();
        // 设置响应体类型
        headers.set(HttpHeaderNames.CONTENT_TYPE, "text/json");
        // 设置响应体长度
        headers.set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        // 设置缓存过期时间
        headers.set(HttpHeaderNames.EXPIRES, 0);
        // 若HTTP请求是长连接，则响应也使用长连接
        if (HttpUtil.isKeepAlive(request)) {
            headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        // 将响应写入到Channel
        context.writeAndFlush(response);
    }
}
