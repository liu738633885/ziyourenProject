package com.huaqie.wubingjie.model.order;

import java.util.List;

/**
 * Created by lewis on 2016/10/10.
 */

public class OrderInfo {
    private String member_nickname;
    private String member_avatar;
    private String member_school;
    private String member_is_student;
    private String member_phone;
    private String order_id;
    private String order_is_agree;
    private String order_agree_msg;
    private String order_finish_status;
    private String finish_msg;
    private String serve_task_title;
    private String serve_task_id;
    private String type;
    private String uid;
    private String create_time;
    //订单完成信息
    private List<String> pic_list;
    private String content;
    private String is_click_agree;

    public String getIs_click_agree() {
        return is_click_agree;
    }

    public void setIs_click_agree(String is_click_agree) {
        this.is_click_agree = is_click_agree;
    }

    public String getFinish_msg() {
        return finish_msg;
    }

    public void setFinish_msg(String finish_msg) {
        this.finish_msg = finish_msg;
    }

    public String getOrder_agree_msg() {
        return order_agree_msg;
    }

    public void setOrder_agree_msg(String order_agree_msg) {
        this.order_agree_msg = order_agree_msg;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public List<String> getPic_list() {
        return pic_list;
    }

    public void setPic_list(List<String> pic_list) {
        this.pic_list = pic_list;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getServe_task_id() {
        return serve_task_id;
    }

    public void setServe_task_id(String serve_task_id) {
        this.serve_task_id = serve_task_id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getServe_task_title() {
        return serve_task_title;
    }

    public void setServe_task_title(String serve_task_title) {
        this.serve_task_title = serve_task_title;
    }

    public String getMember_phone() {
        return member_phone;
    }

    public void setMember_phone(String member_phone) {
        this.member_phone = member_phone;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_is_agree() {
        return order_is_agree;
    }

    public void setOrder_is_agree(String order_is_agree) {
        this.order_is_agree = order_is_agree;
    }

    public String getOrder_finish_status() {
        return order_finish_status;
    }

    public void setOrder_finish_status(String order_finish_status) {
        this.order_finish_status = order_finish_status;
    }

    public String getMember_nickname() {
        return member_nickname;
    }

    public void setMember_nickname(String member_nickname) {
        this.member_nickname = member_nickname;
    }

    public String getMember_avatar() {
        return member_avatar;
    }

    public void setMember_avatar(String member_avatar) {
        this.member_avatar = member_avatar;
    }

    public String getMember_school() {
        return member_school;
    }

    public void setMember_school(String member_school) {
        this.member_school = member_school;
    }

    public String getMember_is_student() {
        return member_is_student;
    }

    public void setMember_is_student(String member_is_student) {
        this.member_is_student = member_is_student;
    }
}
