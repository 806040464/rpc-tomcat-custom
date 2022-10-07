package com.zoe.custom;

import com.zoe.custom.servlet.ZoeServlet;
import com.zoe.custom.utils.XmlReadUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhaoccf
 * @version 1.0.0
 * @description 扫描指定包路径下的所有servlet类名缓存，启动netty服务端进行数据传输
 * @date 2022/10/7 20:28
 */
public class ZoeCatServer {
    private Map<String, ZoeServlet> nameToServletMap = new ConcurrentHashMap<>();
    //线程不安全  servlet--> 全限定名称
    private Map<String, String> nameToClassNameMap = new HashMap<>();
    private String basePackage;

    ZoeCatServer(String basePackage) {
        this.basePackage = basePackage;
    }

    void start() throws Exception {
        cacheClassName(basePackage);
        run();
    }

    private void run() throws Exception {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new HttpServerCodec());
                            socketChannel.pipeline().addLast(new HttpObjectAggregator(65536));
                            socketChannel.pipeline().addLast(new ChunkedWriteHandler());
                            socketChannel.pipeline().addLast(new ZoeCatStaticHandler());
                            socketChannel.pipeline().addLast(new ZoeCatHandler(nameToServletMap, nameToClassNameMap));
                        }
                    });
            int port = XmlReadUtil.initPort();
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            System.out.println("ZoeCat启动成功：监听端口号为:" + port);
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void cacheClassName(String basePackage) {
        URL resource = this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.", "/"));
        if (null == resource) {
            return;
        }
        File dir = new File(resource.getFile());
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                cacheClassName(basePackage + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                String simpleClassName = file.getName().replace(".class", "").trim();
                nameToClassNameMap.put(simpleClassName.toLowerCase(), basePackage + "." + simpleClassName);
            }
        }
    }
}
