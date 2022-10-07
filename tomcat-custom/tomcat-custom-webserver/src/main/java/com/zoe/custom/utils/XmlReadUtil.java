package com.zoe.custom.utils;

import com.zoe.custom.ZoeCatServer;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;

/**
 * @author zhaoccf
 * @version 1.0.0
 * @description 读取xml配置文件中端口号
 * @date 2022/10/7 21:14
 */
public class XmlReadUtil {

    public static int initPort() throws DocumentException {
        //初始化端口
        //读取配置文件Server.xml中的端口号
        InputStream in = ZoeCatServer.class.getClassLoader().getResourceAsStream("server.xml");
        //获取配置文件输入流
        SAXReader saxReader = new SAXReader();
        Document doc = saxReader.read(in);
        //使用SAXReader + XPath读取端口配置
        Element portEle = (Element) doc.selectSingleNode("//port");
        return Integer.valueOf(portEle.getText());
    }
}
