package com.zoe.custom;

import com.zoe.custom.servlet.ZoeRequest;
import com.zoe.custom.servlet.ZoeResponse;
import com.zoe.custom.servlet.ZoeServlet;

/**
 * @author zhaoccf
 * @version 1.0.0
 * @description Servlet默认实现
 * @date 2022/10/7 20:22
 */
public class DefaultZoeServlet extends ZoeServlet {
    @Override
    public void doGet(ZoeRequest request, ZoeResponse response) throws Exception {
        String uri = request.getUri();
        response.write("404 - no this servlet : "
                + (uri.contains("/") && uri.contains("?") ? uri.substring(uri.lastIndexOf("/") + 1, uri.indexOf("?")) : uri));
    }

    @Override
    public void doPost(ZoeRequest request, ZoeResponse response) throws Exception {
        doGet(request, response);
    }
}
