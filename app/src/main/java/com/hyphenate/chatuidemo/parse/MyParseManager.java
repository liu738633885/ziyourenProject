package com.hyphenate.chatuidemo.parse;

import android.content.Context;

import com.huaqie.wubingjie.model.user.UserInfoData;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.orhanobut.logger.Logger;
import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.MainApplication;
import com.huaqie.wubingjie.model.user.UserInfo;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;
import com.huaqie.wubingjie.utils.UserManager;

import java.util.ArrayList;
import java.util.List;

public class MyParseManager {

    private static final String TAG = MyParseManager.class.getSimpleName();


    private Context appContext;
    private static MyParseManager instance = new MyParseManager();


    private MyParseManager() {
    }

    public static MyParseManager getInstance() {
        return instance;
    }

    public void onInit(Context context) {
        this.appContext = context.getApplicationContext();

    }

    /**
     * 获取联系人
     *
     * @param usernames
     * @param callback
     */
    int i = 0;

    public void getContactInfos(final List<String> usernames, final EMValueCallBack<List<EaseUser>> callback) {
        if(usernames.size()<1){
            return;
        }
        i = 0;
        final List<EaseUser> list = new ArrayList<>();
        EMValueCallBack<EaseUser> callBack = new EMValueCallBack<EaseUser>() {
            @Override
            public void onSuccess(EaseUser easeUser) {
                EaseCommonUtils.setUserInitialLetter(easeUser);
                list.add(easeUser);
                if (i >= usernames.size() - 1) {
                    callback.onSuccess(list);
                    return;
                }
                i++;
                asyncGetUserInfo(usernames.get(i), this);
            }

            @Override
            public void onError(int ii, String s) {
                callback.onSuccess(list);
            }
        };
        asyncGetUserInfo(usernames.get(i), callBack);
    }


    /**
     * 获取自己的用户
     *
     * @param callback
     */
    public void asyncGetCurrentUserInfo(final EMValueCallBack<EaseUser> callback) {
        Logger.e("xxxx");
        final String username = EMClient.getInstance().getCurrentUser();
        asyncGetUserInfo(username, new EMValueCallBack<EaseUser>() {

            @Override
            public void onSuccess(EaseUser value) {
                callback.onSuccess(value);
            }

            @Override
            public void onError(int error, String errorMsg) {
                if (UserManager.isLogin()) {
                    EaseUser easeUser = new EaseUser(UserManager.getUid());
                    easeUser.setAvatar(UserManager.getKeyAvatar());
                    easeUser.setNick(UserManager.getKeyNickname());
                    callback.onSuccess(easeUser);
                } else {
                    callback.onError(error, errorMsg);
                }
            }
        });
    }

    /**
     * 获取单独的用户
     *
     * @param callback
     */
    public void asyncGetUserInfo(final String username, final EMValueCallBack<EaseUser> callback) {
        Logger.e("xxxx");
        Logger.e("调用asyncGetUserInfo");
        NetBaseRequest netBaseRequest = RequsetFactory.creatNoUidRequest(Constants.GET_USER_INFO);
        netBaseRequest.add("uid", username);
        netBaseRequest.add("tpye", 1);
        CallServer.getRequestInstance().add(MainApplication.getInstance(), 0x01, netBaseRequest, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    UserInfoData data = netBaseBean.parseObject(UserInfoData.class);
                    UserInfo info = data.getInfo();
                    if (callback != null) {
                        EaseUser user = DemoHelper.getInstance().getContactList().get(username);
                        if (user != null) {
                            user.setNick(info.getMember_nickname());
                            user.setAvatar(info.getMember_avatar());

                        } else {
                            user = new EaseUser(username);
                            user.setNick(info.getMember_nickname());
                            user.setAvatar(info.getMember_avatar());
                        }
                        callback.onSuccess(user);
                        Logger.d("从网络获取成功" + info.toString());
                    }

                } else {
                    callback.onError(netBaseBean.getStatus(), netBaseBean.getMessage());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                callback.onError(responseCode, exception.getMessage());
            }
        }, true, false);
    }
}
