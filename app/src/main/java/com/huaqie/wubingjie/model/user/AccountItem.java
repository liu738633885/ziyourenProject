package com.huaqie.wubingjie.model.user;

import java.util.List;

/**
 * Created by lewis on 2016/10/21.
 */

public class AccountItem {
    private List<AccountItemChild> arr;
    private String date;
    private String pay_total_money;
    private String income_total_money;

    public String getIncome_total_money() {
        return income_total_money;
    }

    public void setIncome_total_money(String income_total_money) {
        this.income_total_money = income_total_money;
    }

    public String getPay_total_money() {
        return pay_total_money;
    }

    public void setPay_total_money(String pay_total_money) {
        this.pay_total_money = pay_total_money;
    }

    public List<AccountItemChild> getArr() {
        return arr;
    }

    public void setArr(List<AccountItemChild> arr) {
        this.arr = arr;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
