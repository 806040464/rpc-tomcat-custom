package com.zoe.custom.stub;

import com.zoe.custom.ClassInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author zhaoccf
 * @version 1.0.0
 * @description 服务端接收类方法信息后，反射调用对应实现类方法
 * @date 2022/10/7 17:20
 */
public class InvokeHandler extends ChannelInboundHandlerAdapter {
    /**
     * 查找接口实现类
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ClassInfo classInfo = (ClassInfo) msg;
        Class cls = null;
        try {
            cls = getImplClass(classInfo);
        } catch (Exception e) {
            ctx.writeAndFlush(e.getMessage());
            return;
        }
        Method method = cls.getMethod(classInfo.getMethodName(), classInfo.getTypes());
        Object classInstance = cls.newInstance();
        Object result = method.invoke(classInstance, classInfo.getParameters());
        ctx.writeAndFlush(result);
    }

    /**
     * 读取客户端发来的数据并通过反射调用实现类的方法
     *
     * @param classInfo
     * @return
     * @throws Exception
     */
    private Class getImplClass(ClassInfo classInfo) throws Exception {
        String className = classInfo.getClassName();
        Class interfaceClass = Class.forName(className);
        String interfacePath = className.substring(0, className.lastIndexOf("."));
        interfacePath = interfacePath.substring(0, interfacePath.lastIndexOf("."));
        Reflections reflections = new Reflections(interfacePath);
        Set<Class> implSet = reflections.getSubTypesOf(interfaceClass);
        Class cls;
        if (0 == implSet.size()) {
            throw new RuntimeException("未找到实现类");
        } else if (1 < implSet.size()) {
            throw new RuntimeException("找到实现类不止一个，不知道使用哪个");
        } else {
            cls = implSet.iterator().next();
        }
        return cls;
    }

}
