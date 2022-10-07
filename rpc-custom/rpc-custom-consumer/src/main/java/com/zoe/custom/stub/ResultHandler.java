package com.zoe.custom.stub;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Data;

/**
 * @author zhaoccf
 * @version 1.0.0
 * @description netty读取服务端返回接口方法调用返回
 * @date 2022/10/7 16:48
 */
@Data
public class ResultHandler extends ChannelInboundHandlerAdapter {
    private Object response;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        response = msg;
        ctx.close();
    }
}
