package com.zoe.custom.webapp;

import com.zoe.custom.servlet.ZoeRequest;
import com.zoe.custom.servlet.ZoeResponse;
import com.zoe.custom.servlet.ZoeServlet;

/**
 * @author zhaoccf
 * @version 1.0.0
 * @description 自定义servlet
 * @date 2022/10/7 20:48
 */
public class SkuServlet extends ZoeServlet {
    @Override
    public void doGet(ZoeRequest request, ZoeResponse response) throws Exception {
        String uri = request.getUri();
        String path = request.getPath();
        String method = request.getMethod();
        String name = request.getParameter("name");
        String content = "uri = " + uri + "\n" +
                "path = " + path + "\n" +
                "method = " + method + "\n" +
                "param = " + name;
        response.write(content);
    }

    @Override
    public void doPost(ZoeRequest request, ZoeResponse response) throws Exception {
        doGet(request, response);
    }
}
