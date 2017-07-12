package com.huaqie.wubingjie.model.netmodel;


import com.huaqie.wubingjie.model.user.UserInfo;

/**
 * Created by lewis on 16/6/22.
 */
public class Login {
    private String token;
    private UserInfo info;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInfo getInfo() {
        return info;
    }

    public void setInfo(UserInfo info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "Login{" +
                "token='" + token + '\'' +
                ", info=" + info +
                '}';
    }
}
