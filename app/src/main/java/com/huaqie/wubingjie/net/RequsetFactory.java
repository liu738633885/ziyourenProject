package com.huaqie.wubingjie.net;


import android.content.Context;

import com.huaqie.wubingjie.utils.SettingsManager;
import com.huaqie.wubingjie.utils.UserManager;


/**
 * Created by lewis on 16/6/23.
 */
public class RequsetFactory {
    //目前所有的 url 都带 token
    //都带 city

    /**
     * @param context
     * @param url     默认的创建 NetBaseRequest
     *                验证登录
     * @return
     */
    public static NetBaseRequest creatBaseRequest(Context context, String url) {
        NetBaseRequest netBaseRequest = creatNoUidRequest(url);
        if (UserManager.isLogin()) {
            netBaseRequest.add("uid",UserManager.getUid());
        }
        return  netBaseRequest;
    }

    public static NetBaseRequest creatNoUidRequest(String url) {
        NetBaseRequest netBaseRequest = new NetBaseRequest(getBaseUrl(url));
        netBaseRequest.add("city_id", SettingsManager.getKeyCityId());
        return  netBaseRequest;
    }

    public static String getBaseUrl(String url) {
        url += "/?vesion=1";
        if (UserManager.isLogin()) {
            url += ("&user_id=" + UserManager.getUid() + "&token=" + UserManager.getToken());
        }
        return url;
    }
}
