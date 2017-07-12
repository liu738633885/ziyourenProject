/*
 * Copyright © YOLANDA. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huaqie.wubingjie.net;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.orhanobut.logger.Logger;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.JsonObjectRequest;
import com.yolanda.nohttp.rest.RestRequest;
import com.yolanda.nohttp.rest.StringRequest;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;

import java.util.List;


public class NetBaseRequest extends RestRequest<NetBaseBean> {

    public NetBaseRequest(String url, RequestMethod requestMethod) {
        super(url, requestMethod);
    }

    public NetBaseRequest(String url) {
        super(url, RequestMethod.POST);
    }

    @Override
    public NetBaseBean parseResponse(String url, Headers responseHeaders, byte[] responseBody) {
        String result = StringRequest.parseResponseString(url, responseHeaders, responseBody);
//        Logger.d("后台返回的数据:"+result);
        NetBaseBean baseBean;
        try {
            baseBean = JSON.parseObject(result, NetBaseBean.class);
            Logger.d(baseBean.toString());
            return baseBean;
        } catch (Exception e) {
            return new NetBaseBean();
        }
    }

    @Override
    public String getAccept() {
        return JsonObjectRequest.ACCEPT;
    }

    public void addJsonArray(String key, List list) {
        if(list==null){
            return;
        }
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(list);
        add(key, jsonArray.toJSONString());
    }
}
