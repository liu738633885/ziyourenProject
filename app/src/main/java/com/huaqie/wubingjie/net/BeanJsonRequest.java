package com.huaqie.wubingjie.net;

import com.alibaba.fastjson.JSON;
import com.orhanobut.logger.Logger;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.JsonObjectRequest;
import com.yolanda.nohttp.rest.RestRequest;
import com.yolanda.nohttp.rest.StringRequest;

/**
 * Created by lewis on 16/5/30.
 */
public class BeanJsonRequest<E> extends RestRequest<E> {
    private Class<E> clazz;

    public BeanJsonRequest(String url, Class<E> clazz) {
        this(url, RequestMethod.POST, clazz);
    }

    public BeanJsonRequest(String url, RequestMethod requestMethod, Class<E> clazz) {
        super(url, requestMethod);
        this.clazz = clazz;
    }

    @Override
    public E parseResponse(String url, Headers responseHeaders, byte[] responseBody) {
        String string = StringRequest.parseResponseString(url, responseHeaders, responseBody);
        Logger.d(string);
        try {
            return JSON.parseObject(string, clazz);
        } catch (Exception e) {
            E instance = null;
            try {
                // 服务端返回数据格式错误时，返回一个空构造
                // 但是前提是传进来的JavaBean必须提供了默认实现
                instance = clazz.newInstance();
            } catch (Exception e1) {
            }
            return instance;
        }
    }

    @Override
    public String getAccept() {
        return JsonObjectRequest.ACCEPT;
    }
}
