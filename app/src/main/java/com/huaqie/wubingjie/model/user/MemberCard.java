package com.huaqie.wubingjie.model.user;

import java.util.List;

/**
 * Created by lewis on 2016/11/10.
 */

public class MemberCard {
    private String card_name;
    private String card_number;
    private String card_status;
    private String check_status;
    private String domain;
    private int type;
    private List<String> card_pic_list;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCard_name() {
        return card_name;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getCard_status() {
        return card_status;
    }

    public void setCard_status(String card_status) {
        this.card_status = card_status;
    }

    public String getCheck_status() {
        return check_status;
    }

    public void setCheck_status(String check_status) {
        this.check_status = check_status;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<String> getCard_pic_list() {
        return card_pic_list;
    }

    public void setCard_pic_list(List<String> card_pic_list) {
        this.card_pic_list = card_pic_list;
    }
}
