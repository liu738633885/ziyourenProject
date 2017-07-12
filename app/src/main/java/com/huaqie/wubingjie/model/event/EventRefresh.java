package com.huaqie.wubingjie.model.event;

/**
 * Created by lewis on 16/7/26.
 */
public class EventRefresh {
    public static final String ACTION_LOGIN="login";
    public static final String ACTION_REGISTER="register";
    public static final String ACTION_CITY="city";
    public static final String ACTION_WECHAT_PAY="wechat_pay";
    public static final String ACTION_REFRESH = "only_refresh";
    public static final String ACTION_ADDRESS = "address";
    public static final String ACTION_CHAT = "chat";
    private String action;

    public EventRefresh(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
    private Object data;
    private String where;

    public EventRefresh(String action, Object data, String where) {
        this.action = action;
        this.data = data;
        this.where = where;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }
}
