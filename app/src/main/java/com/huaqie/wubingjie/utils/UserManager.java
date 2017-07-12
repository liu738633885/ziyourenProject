package com.huaqie.wubingjie.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.huaqie.wubingjie.activity.LoginActivity;
import com.huaqie.wubingjie.model.event.EventRefresh;
import com.huaqie.wubingjie.model.netmodel.Login;
import com.huaqie.wubingjie.model.user.UserInfo;
import com.hyphenate.chat.EMClient;

import org.greenrobot.eventbus.EventBus;

import static java.lang.Integer.parseInt;

/**
 * Created by lewis on 16/6/22.
 */
public class UserManager {
    public static final String KEY_TOKEN = "token";
    public static final String KEY_ISLOGIN = "isLogin";
    public static final String KEY_UID = "uid";
    public static final String KEY_EASEMOB = "member_easemob";
    public static final String KEY_MEMBER_PASSWORD= "member_password";
    public static final String KEY_AVATAR = "member_avatar";
    public static final String KEY_NICKNAME = "member_nickname";
    public static final String KEY_COVER = "member_cover";
    public static final String KEY_MONEY = "member_money";
    public static final String KEY_SEX = "member_sex";
    public static final String KEY_AGE = "member_age";
    public static final String KEY_IS_IDENTITY = "member_is_identity";
    public static final String KEY_QRCODE = "member_qrcode";
    public static final String KEY_SEND_NUM = "member_send_num";
    public static final String KEY_RECEIVE_NUM= "member_receive_num";
    public static final String KEY_IS_ZYRM= "is_zyr";
    public static final String KEY_PHONE= "member_phone";
    public static final String KEY_ALIPAY= "member_alipay";


    public static boolean isLogin() {
        return (boolean) SPUtils.getUserInstance().get(KEY_ISLOGIN, false);
    }

    public static boolean isLogin(Context context) {
        if (!isLogin()) {
            context.startActivity(new Intent(context, LoginActivity.class));
        }
        return isLogin();
    }

    public static boolean saveLoginInfo(Login login) {
        if (login == null) {
            return false;
        } else {
            SPUtils.getUserInstance().put(KEY_TOKEN, login.getToken());
            SPUtils.getUserInstance().put(KEY_ISLOGIN, true);
            return saveUserInfo(login.getInfo());
        }
    }

    public static boolean saveUserInfo(UserInfo userInfo) {
        if (userInfo == null) {
            return false;
        } else {
            SPUtils.getUserInstance().put(KEY_UID, userInfo.getUid());
            //SPUtils.getUserInstance().put(KEY_EASEMOB, userInfo.getMember_easemob());
            SPUtils.getUserInstance().put(KEY_MEMBER_PASSWORD, userInfo.getMember_password());
            SPUtils.getUserInstance().put(KEY_AVATAR, userInfo.getMember_avatar());
            SPUtils.getUserInstance().put(KEY_NICKNAME, userInfo.getMember_nickname());
            SPUtils.getUserInstance().put(KEY_COVER, userInfo.getMember_cover());
            SPUtils.getUserInstance().put(KEY_MONEY, userInfo.getMember_money());
            SPUtils.getUserInstance().put(KEY_SEX, userInfo.getMember_sex());
            SPUtils.getUserInstance().put(KEY_QRCODE, userInfo.getMember_qrcode());
            SPUtils.getUserInstance().put(KEY_SEND_NUM, userInfo.getMember_send_num());
            SPUtils.getUserInstance().put(KEY_RECEIVE_NUM, userInfo.getMember_receive_num());
            SPUtils.getUserInstance().put(KEY_PHONE, userInfo.getMember_phone());
            SPUtils.getUserInstance().put(KEY_IS_ZYRM, userInfo.getMember_is_zyr());
            SPUtils.getUserInstance().put(KEY_ALIPAY, userInfo.getMember_alipay());
            return true;
        }
    }


    public static String getToken() {
        return (String) SPUtils.getUserInstance().get(KEY_TOKEN, "");
    }

    public static String getToken(Context context) {
        if (isLogin(context)) {
            return getToken();
        } else {
            return "";
        }

    }
    public static String getAlipay() {
        return (String) SPUtils.getUserInstance().get(KEY_ALIPAY, "");
    }
    public static String getPhone() {
        return (String) SPUtils.getUserInstance().get(KEY_PHONE, "");
    }
    public static int getIsZYR() {
        return parseInt((String) SPUtils.getUserInstance().get(KEY_IS_ZYRM, "0"));
    }

    public static String getUid() {
        return (String) SPUtils.getUserInstance().get(KEY_UID, "");
    }

    public static String getKeyEasemob() {
        return (String) SPUtils.getUserInstance().get(KEY_EASEMOB, "");
    }

    public static String getKeyAvatar() {
        return (String) SPUtils.getUserInstance().get(KEY_AVATAR, "");
    }

    public static String getKeyNickname() {
        return (String) SPUtils.getUserInstance().get(KEY_NICKNAME, "");
    }
    public static String getKeyPassword() {
        return (String) SPUtils.getUserInstance().get(KEY_MEMBER_PASSWORD, "");
    }

    public static String getKeyCover() {
        return (String) SPUtils.getUserInstance().get(KEY_COVER, "");
    }

    public static String getKeyMoney() {
        return (String) SPUtils.getUserInstance().get(KEY_MONEY, "");
    }

    public static String getKeySex() {
        return (String) SPUtils.getUserInstance().get(KEY_SEX, "");
    }

    public static String getKeyIsIdentity() {
        return  (String) SPUtils.getUserInstance().get(KEY_IS_IDENTITY, "");
    }

    public static String getKeyQrcode() {
        return (String) SPUtils.getUserInstance().get(KEY_QRCODE, "");
    }


    public  static String getSendNum(){
        return (String) SPUtils.getUserInstance().get(KEY_SEND_NUM,"");
    }
    public  static void addSendNum(){
        if (TextUtils.isEmpty(getSendNum())) {
            return;
        }
        try {
            int now = Integer.parseInt(getSendNum());
            setSendNum(String.valueOf(now + 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public  static void setSendNum(String sendNum){
        SPUtils.getUserInstance().put(KEY_SEND_NUM, sendNum);
    }

    public  static String getReceiveNum(){
        return (String) SPUtils.getUserInstance().get(KEY_RECEIVE_NUM,"");
    }
    public  static String addReceiveNum(){
        return (String) SPUtils.getUserInstance().get(KEY_RECEIVE_NUM,"");
    }
    public  static void setReceiveNum(String sendNum){
        SPUtils.getUserInstance().put(KEY_RECEIVE_NUM, sendNum);
    }
    public static void logout() {
        logoutMyservice();
        EMClient.getInstance().logout(true);
    }
    public static boolean logoutMyservice() {
        if (isLogin()) {
            SPUtils.getUserInstance().clear();
            EventBus.getDefault().post(new EventRefresh(EventRefresh.ACTION_LOGIN));
            return true;
        } else {
            return false;
        }
    }


}
