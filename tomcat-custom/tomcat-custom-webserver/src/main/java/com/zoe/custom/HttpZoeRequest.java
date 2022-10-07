package com.zoe.custom;

import com.zoe.custom.servlet.ZoeRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

/**
 * @author zhaoccf
 * @version 1.0.0
 * @description 仿照ServletRequest获取请求相关属性
 * @date 2022/10/7 19:40
 */
public class HttpZoeRequest implements ZoeRequest {
    private HttpRequest request;
    private QueryStringDecoder decoder;

    public HttpZoeRequest(HttpRequest request) {
        this.request = request;
        decoder = new QueryStringDecoder(request.uri());
    }

    @Override
    public String getUri() {
        return request.uri();
    }

    @Override
    public String getPath() {
        return decoder.path();
    }

    @Override
    public String getMethod() {
        return request.method().name();
    }

    @Override
    public Map<String, List<String>> getParameters() {
        return decoder.parameters();
    }

    @Override
    public List<String> getParameters(String name) {
        return getParameters().get(name);
    }

    @Override
    public String getParameter(String name) {
        List<String> parameters = getParameters(name);
        if (null == parameters || 0 == parameters.size()) {
            return null;
        }
        return parameters.get(0);
    }
}
