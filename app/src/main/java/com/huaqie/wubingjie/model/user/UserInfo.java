package com.huaqie.wubingjie.model.user;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lewis on 16/6/16.
 */
public class UserInfo implements Serializable {
    @JSONField
    public static final String SEX_MAN = "1";
    public static final String SEX_WOMAN = "2";
    private String uid;
    private String member_phone;
    private String member_easemob;
    private String member_password;
    private String member_avatar;
    private String member_nickname;
    //用户状态 0为封停，1为正常
    private String member_status;
    //是否注册环信，0为未注册，1为已注册
    private String member_hx;
    private String member_cover;
    private String member_money;
    private String member_sex;
    private String member_city_id;
    private String member_age;
    private String member_city_name;
    //是否实名认证  0为没有实名认证，1为已实名认证
    private String member_is_identity;
    //个人介绍
    private String member_introduce;
    //个人爱好
    private String member_qrcode;
    private String member_hobby;
    //支付宝账户
    private String member_alipay_name;
    private String member_alipay_account;
    //微信账号
    private String member_wechat_name;
    private String member_wechat_account;

    private String city_id;
    //身份证验证信息
    private String id;
    private String member_id;
    private String member_idcard;
    private String member_idcard_name;
    private String member_idcard_img;
    //审核身份证，默认为0未审核,1为拒绝，2为审核成功
    private String is_idcard;
    private String create_time;
    private List<String> photolist;
    private String friend_flag;// 0不是好友 1是好友

    private String member_all_money;
    private String member_surplus_money;
    private String member_receive_num;
    private String member_send_num;
    private String member_birthday;
    private String member_school;
    private String member_is_student;
    private String member_alipay;
    //后加的
    private String yq_code;
    private String member_is_card;
    private String member_is_zyr;
    private String is_friend;

    public String getIs_friend() {
        return is_friend;
    }

    public void setIs_friend(String is_friend) {
        this.is_friend = is_friend;
    }

    public String getMember_is_zyr() {
        return member_is_zyr;
    }

    public void setMember_is_zyr(String member_is_zyr) {
        this.member_is_zyr = member_is_zyr;
    }

    public String getMember_is_card() {
        return member_is_card;
    }

    public void setMember_is_card(String member_is_card) {
        this.member_is_card = member_is_card;
    }

    public String getYq_code() {
        return yq_code;
    }

    public void setYq_code(String yq_code) {
        this.yq_code = yq_code;
    }

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

    public String getMember_receive_num() {
        return member_receive_num;
    }

    public void setMember_receive_num(String member_receive_num) {
        this.member_receive_num = member_receive_num;
    }

    public String getMember_send_num() {
        return member_send_num;
    }

    public void setMember_send_num(String member_send_num) {
        this.member_send_num = member_send_num;
    }

    public String getMember_birthday() {
        return member_birthday;
    }

    public void setMember_birthday(String member_birthday) {
        this.member_birthday = member_birthday;
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

    public String getMember_alipay() {
        return member_alipay;
    }

    public void setMember_alipay(String member_alipay) {
        this.member_alipay = member_alipay;
    }

    public String getMember_phone() {
        return member_phone;
    }

    public String getMember_password() {
        return member_password;
    }

    public void setMember_password(String member_password) {
        this.member_password = member_password;
    }

    public void setMember_phone(String member_phone) {
        this.member_phone = member_phone;
    }

    public String getMember_status() {
        return member_status;
    }

    public void setMember_status(String member_status) {
        this.member_status = member_status;
    }

    public String getMember_hx() {
        return member_hx;
    }

    public void setMember_hx(String member_hx) {
        this.member_hx = member_hx;
    }

    public String getFriend_flag() {
        return friend_flag;
    }

    public void setFriend_flag(String friend_flag) {
        this.friend_flag = friend_flag;
    }

    public String getUid() {
        return uid;
    }

    public String getMember_qrcode() {
        return member_qrcode;
    }

    public void setMember_qrcode(String member_qrcode) {
        this.member_qrcode = member_qrcode;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMember_easemob() {
        return member_easemob;
    }

    public void setMember_easemob(String member_easemob) {
        this.member_easemob = member_easemob;
    }

    public String getMember_avatar() {
        return member_avatar;
    }

    public void setMember_avatar(String member_avatar) {
        this.member_avatar = member_avatar;
    }

    public String getMember_nickname() {
        return member_nickname;
    }

    public void setMember_nickname(String member_nickname) {
        this.member_nickname = member_nickname;
    }

    public String getMember_city_id() {
        return member_city_id;
    }

    public void setMember_city_id(String member_city_id) {
        this.member_city_id = member_city_id;
    }

    public String getMember_city_name() {
        return member_city_name;
    }

    public void setMember_city_name(String member_city_name) {
        this.member_city_name = member_city_name;
    }

    public String getMember_cover() {
        return member_cover;
    }

    public void setMember_cover(String member_cover) {
        this.member_cover = member_cover;
    }

    public String getMember_money() {
        return member_money;
    }

    public void setMember_money(String member_money) {
        this.member_money = member_money;
    }

    public String getMember_sex() {
        return member_sex;
    }

    public void setMember_sex(String member_sex) {
        this.member_sex = member_sex;
    }

    public String getMember_age() {
        return member_age;
    }

    public void setMember_age(String member_age) {
        this.member_age = member_age;
    }

    public String getMember_is_identity() {
        return member_is_identity;
    }

    public void setMember_is_identity(String member_is_identity) {
        this.member_is_identity = member_is_identity;
    }

    public String getMember_introduce() {
        return member_introduce;
    }

    public void setMember_introduce(String member_introduce) {
        this.member_introduce = member_introduce;
    }

    public String getMember_hobby() {
        return member_hobby;
    }

    public void setMember_hobby(String member_hobby) {
        this.member_hobby = member_hobby;
    }

    public String getMember_alipay_name() {
        return member_alipay_name;
    }

    public void setMember_alipay_name(String member_alipay_name) {
        this.member_alipay_name = member_alipay_name;
    }

    public String getMember_alipay_account() {
        return member_alipay_account;
    }

    public void setMember_alipay_account(String member_alipay_account) {
        this.member_alipay_account = member_alipay_account;
    }

    public String getMember_wechat_name() {
        return member_wechat_name;
    }

    public void setMember_wechat_name(String member_wechat_name) {
        this.member_wechat_name = member_wechat_name;
    }

    public String getMember_wechat_account() {
        return member_wechat_account;
    }

    public void setMember_wechat_account(String member_wechat_account) {
        this.member_wechat_account = member_wechat_account;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getMember_idcard() {
        return member_idcard;
    }

    public void setMember_idcard(String member_idcard) {
        this.member_idcard = member_idcard;
    }

    public String getMember_idcard_name() {
        return member_idcard_name;
    }

    public void setMember_idcard_name(String member_idcard_name) {
        this.member_idcard_name = member_idcard_name;
    }

    public String getMember_idcard_img() {
        return member_idcard_img;
    }

    public void setMember_idcard_img(String member_idcard_img) {
        this.member_idcard_img = member_idcard_img;
    }

    public List<String> getPhotolist() {
        return photolist;
    }

    public void setPhotolist(List<String> photolist) {
        this.photolist = photolist;
    }

    public String getIs_idcard() {
        return is_idcard;
    }

    public void setIs_idcard(String is_idcard) {
        this.is_idcard = is_idcard;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "uid='" + uid + '\'' +
                ", member_phone='" + member_phone + '\'' +
                ", member_easemob='" + member_easemob + '\'' +
                ", member_password='" + member_password + '\'' +
                ", member_avatar='" + member_avatar + '\'' +
                ", member_nickname='" + member_nickname + '\'' +
                ", member_status='" + member_status + '\'' +
                ", member_hx='" + member_hx + '\'' +
                ", member_cover='" + member_cover + '\'' +
                ", member_money='" + member_money + '\'' +
                ", member_sex='" + member_sex + '\'' +
                ", member_city_id='" + member_city_id + '\'' +
                ", member_age='" + member_age + '\'' +
                ", member_city_name='" + member_city_name + '\'' +
                ", member_is_identity='" + member_is_identity + '\'' +
                ", member_introduce='" + member_introduce + '\'' +
                ", member_qrcode='" + member_qrcode + '\'' +
                ", member_hobby='" + member_hobby + '\'' +
                ", member_alipay_name='" + member_alipay_name + '\'' +
                ", member_alipay_account='" + member_alipay_account + '\'' +
                ", member_wechat_name='" + member_wechat_name + '\'' +
                ", member_wechat_account='" + member_wechat_account + '\'' +
                ", city_id='" + city_id + '\'' +
                ", id='" + id + '\'' +
                ", member_id='" + member_id + '\'' +
                ", member_idcard='" + member_idcard + '\'' +
                ", member_idcard_name='" + member_idcard_name + '\'' +
                ", member_idcard_img='" + member_idcard_img + '\'' +
                ", is_idcard='" + is_idcard + '\'' +
                ", create_time='" + create_time + '\'' +
                ", photolist=" + photolist +
                ", friend_flag='" + friend_flag + '\'' +
                ", member_all_money='" + member_all_money + '\'' +
                ", member_surplus_money='" + member_surplus_money + '\'' +
                ", member_receive_num='" + member_receive_num + '\'' +
                ", member_send_num='" + member_send_num + '\'' +
                ", member_birthday='" + member_birthday + '\'' +
                ", member_school='" + member_school + '\'' +
                ", member_is_student='" + member_is_student + '\'' +
                ", member_alipay='" + member_alipay + '\'' +
                '}';
    }
}
