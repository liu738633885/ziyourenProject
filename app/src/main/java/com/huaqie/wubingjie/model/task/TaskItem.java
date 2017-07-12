package com.huaqie.wubingjie.model.task;

import android.text.TextUtils;

import com.huaqie.wubingjie.model.order.OrderInfo;

import java.util.List;

/**
 * Created by lewis on 2016/10/7.
 */

public class TaskItem {
    private String serve_task_id;
    private String city_id;//城市id
    private String uid;//用户id
    private String title;
    private String content;
    private List<String> pic_list;//图片
    private String type;//类型：1为服务，2为任务
    private String people_type;//人数类型：1为单人，2为多人
    private String people_num;
    private String grab_num;//被抢数量
    private String price;//单价
    private String member_sex;//性别：0为无限，1为男性，2为女性
    private String address;//地址
    private String create_time;//创建时间
    private String lat;//经度
    private String lng;// 纬度
    private String member_nickname; //昵称
    private String member_avatar; //头像
    private String member_school; //学校
    private String member_is_student; //是否为学生，1在校生，2毕业，3其他
    private String distance; //距离
    private String class_name;//类型名称
    private String class_id;//类型 id
    private String finish_status;//完成状态：默认为0，1结单，2取消
    private String finish_time;// 完成时间
    private String role;//角色，0为平常用户，1为发单人，2为抢单人
    private String is_suspend;//是否暂停 默认为0，1为暂停
    private String comment_num;//评论数
    private String member_phone;//电话
    private List<OrderInfo> order_info;// 抢单详情，不是发单人为空
    private String order_id;
    private String order_finish_status;
    private String finish_msg;

    public String getOrder_finish_status() {
        return order_finish_status;
    }

    public void setOrder_finish_status(String order_finish_status) {
        this.order_finish_status = order_finish_status;
    }

    public String getOrder_id() {
        return order_id;
    }

    public int getIntOrder_id() {
        return Integer.parseInt(TextUtils.isEmpty(getOrder_id()) ? "0" : getOrder_id());
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getServe_task_id() {
        return serve_task_id;
    }

    public void setServe_task_id(String serve_task_id) {
        this.serve_task_id = serve_task_id;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getPic_list() {
        return pic_list;
    }

    public void setPic_list(List<String> pic_list) {
        this.pic_list = pic_list;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPeople_num() {
        return people_num;
    }

    public int getIntPeople_num() {
        return Integer.parseInt(TextUtils.isEmpty(getPeople_num()) ? "0" : getPeople_num());
    }

    public void setPeople_num(String people_num) {
        this.people_num = people_num;
    }

    public String getGrab_num() {
        return grab_num;
    }

    public int getIntGrab_num() {
        return Integer.parseInt(TextUtils.isEmpty(getGrab_num()) ? "0" : getGrab_num());
    }

    public void setGrab_num(String grab_num) {
        this.grab_num = grab_num;
    }

    public String getPrice() {
        return price;
    }

    public double getDoublePrice() {
        return Double.parseDouble(TextUtils.isEmpty(getPrice()) ? "0.00" : getPrice());
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMember_sex() {
        return member_sex;
    }

    public void setMember_sex(String member_sex) {
        this.member_sex = member_sex;
    }

    public String getAddress() {
        return address;
    }

    public String getMember_phone() {
        return member_phone;
    }

    public void setMember_phone(String member_phone) {
        this.member_phone = member_phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreate_time() {
        return create_time;
    }

    public long getLongCreate_time() {
        return Long.parseLong(getCreate_time());
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getPeople_type() {
        return people_type;
    }

    public void setPeople_type(String people_type) {
        this.people_type = people_type;
    }

    public String getFinish_status() {
        return finish_status;
    }

    public void setFinish_status(String finish_status) {
        this.finish_status = finish_status;
    }

    public String getFinish_time() {
        return finish_time;
    }

    public long getLongFinish_time() {
        return Long.parseLong(getFinish_time());
    }

    public void setFinish_time(String finish_time) {
        this.finish_time = finish_time;
    }

    public String getRole() {
        return role;
    }

    public int getIntRole() {
        return Integer.parseInt(TextUtils.isEmpty(getRole()) ? "0" : getRole());
    }

    public String getFinish_msg() {
        return finish_msg;
    }

    public void setFinish_msg(String finish_msg) {
        this.finish_msg = finish_msg;
    }

    public int getIntMyRole() {
        /*if(TextUtils.isEmpty(getRole()))
        return Integer.parseInt(TextUtils.isEmpty(getRole()) ? "0" : getRole());*/
        //0什么都不是
        //1享受的
        //2干活的
        if (TextUtils.isEmpty(getRole()) || getRole().equals("0")) {
            return 0;
        }
        if (getType().equals("1")) {
            if (getRole().equals("1")) {
                return 2;
            } else {
                return 1;
            }
        } else if (getType().equals("2")) {
            if (getRole().equals("1")) {
                return 1;
            } else {
                return 2;
            }
        } else {
            return 0;
        }
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getIs_suspend() {
        return is_suspend;
    }

    public void setIs_suspend(String is_suspend) {
        this.is_suspend = is_suspend;
    }

    public String getComment_num() {
        return comment_num;
    }

    public int getIntComment_num() {
        return Integer.parseInt(TextUtils.isEmpty(getComment_num()) ? "0" : getComment_num());
    }

    public void setComment_num(int comment_num) {
        setComment_num("" + comment_num);
    }

    public void setComment_num(String comment_num) {
        this.comment_num = comment_num;
    }

    public List<OrderInfo> getOrder_info() {
        return order_info;
    }

    public void setOrder_info(List<OrderInfo> order_info) {
        this.order_info = order_info;
    }

    public int getStatus() {
        int status = 1;
        if (getIntGrab_num()>0) {
            status = 2;
        }
        if (getFinish_status().equals("1")) {
            status = 3;
        } else if (getFinish_status().equals("2")) {
            status = 4;
        }
        return status;
    }

    public int getPeopleType() {
        int type = 0;
        if (getPeople_type().equals("2")) {
            //多人
            type = 2;
        } else {
            type = 1;
        }
        return type;
    }
}
