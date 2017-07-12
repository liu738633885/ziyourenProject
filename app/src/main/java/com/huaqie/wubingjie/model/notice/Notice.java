package com.huaqie.wubingjie.model.notice;

import java.util.List;

/**
 * Created by lewis on 2016/10/30.
 */

public class Notice {
    private String notice_id;
    private String notice_type;
    private String uid;
    private String to_uid;
    private String serve_task_id;
    private String serve_task_type;
    private String open_function;
    private String order_id;
    private List<String> pic_list;
    private String content;
    private String check_status;//如果为取消通知时候0为显示下面同意，拒绝按钮，1为不显示
    private String serve_task_title;
    private String serve_task_content;
    private String create_time;
    private String member_nickname;
    private String member_avatar;
    private String member_school;
    private String notice_msg;
    private String member_is_student;
    private String member_phone;
    private String is_read;

    public String getIs_read() {
        return is_read;
    }

    public void setIs_read(String is_read) {
        this.is_read = is_read;
    }

    public String getServe_task_content() {
        return serve_task_content;
    }

    public void setServe_task_content(String serve_task_content) {
        this.serve_task_content = serve_task_content;
    }

    public String getMember_phone() {
        return member_phone;
    }

    public void setMember_phone(String member_phone) {
        this.member_phone = member_phone;
    }

    public String getNotice_id() {
        return notice_id;
    }

    public void setNotice_id(String notice_id) {
        this.notice_id = notice_id;
    }

    public String getNotice_type() {
        return notice_type;
    }

    public void setNotice_type(String notice_type) {
        this.notice_type = notice_type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTo_uid() {
        return to_uid;
    }

    public void setTo_uid(String to_uid) {
        this.to_uid = to_uid;
    }

    public String getServe_task_id() {
        return serve_task_id;
    }

    public void setServe_task_id(String serve_task_id) {
        this.serve_task_id = serve_task_id;
    }

    public String getServe_task_type() {
        return serve_task_type;
    }

    public void setServe_task_type(String serve_task_type) {
        this.serve_task_type = serve_task_type;
    }

    public String getOpen_function() {
        return open_function;
    }

    public void setOpen_function(String open_function) {
        this.open_function = open_function;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
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

    public String getCheck_status() {
        return check_status;
    }

    public void setCheck_status(String check_status) {
        this.check_status = check_status;
    }

    public String getServe_task_title() {
        return serve_task_title;
    }

    public void setServe_task_title(String serve_task_title) {
        this.serve_task_title = serve_task_title;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
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

    public String getNotice_msg() {
        return notice_msg;
    }

    public void setNotice_msg(String notice_msg) {
        this.notice_msg = notice_msg;
    }

    public String getMember_is_student() {
        return member_is_student;
    }

    public void setMember_is_student(String member_is_student) {
        this.member_is_student = member_is_student;
    }

}
