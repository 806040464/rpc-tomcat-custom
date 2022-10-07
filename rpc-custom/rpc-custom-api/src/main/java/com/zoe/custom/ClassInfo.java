package com.zoe.custom;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhaoccf
 * @version 1.0.0
 * @description 接口调用类与方法相关信息
 * @date 2022/10/7 16:52
 */
@Data
public class ClassInfo implements Serializable {
    private String className;
    private String methodName;
    private Class<?>[] types;
    private Object[] parameters;
}
