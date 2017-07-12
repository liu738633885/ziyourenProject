package com.huaqie.wubingjie.model.user;

/**
 * Created by lewis on 2016/10/21.
 */

public class AccountItemChild {
    private String account_type;
    private String money;
    private String pay_type;
    private String account_content;
    private String create_time;

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getAccount_content() {
        return account_content;
    }

    public void setAccount_content(String account_content) {
        this.account_content = account_content;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    @Override
    public String toString() {
        return "AccountItem{" +
                "account_type='" + account_type + '\'' +
                ", money='" + money + '\'' +
                ", pay_type='" + pay_type + '\'' +
                ", account_content='" + account_content + '\'' +
                ", create_time='" + create_time + '\'' +
                '}';
    }
}
