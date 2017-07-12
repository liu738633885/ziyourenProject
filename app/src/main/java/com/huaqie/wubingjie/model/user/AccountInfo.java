package com.huaqie.wubingjie.model.user;

/**
 * Created by lewis on 2016/10/21.
 */

public class AccountInfo {
    private String member_all_money; //总金额
    private String member_surplus_money; //余额
    private String member_pay_money; //支出金额

    public String getMember_all_money() {
        return member_all_money;
    }

    public void setMember_all_money(String member_all_money) {
        this.member_all_money = member_all_money;
    }

    public String getMember_surplus_money() {
        return member_surplus_money;
    }

    public void setMember_surplus_money(String member_surplus_money) {
        this.member_surplus_money = member_surplus_money;
    }

    public String getMember_pay_money() {
        return member_pay_money;
    }

    public void setMember_pay_money(String member_pay_money) {
        this.member_pay_money = member_pay_money;
    }

    @Override
    public String toString() {
        return "AccountInfo{" +
                "member_all_money='" + member_all_money + '\'' +
                ", member_surplus_money='" + member_surplus_money + '\'' +
                ", member_pay_money='" + member_pay_money + '\'' +
                '}';
    }
}
