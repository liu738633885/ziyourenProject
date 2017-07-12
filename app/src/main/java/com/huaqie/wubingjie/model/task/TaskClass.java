package com.huaqie.wubingjie.model.task;

import android.text.TextUtils;

import java.util.List;

/**
 * Created by lewis on 2016/10/8.
 */

public class TaskClass {
    private String class_id;
    private String class_name;
    private String pid;
    private List<TaskClass> chlid;

    public TaskClass() {
    }

    public TaskClass(String class_id, String class_name) {
        this.class_id = class_id;
        this.class_name = class_name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public List<TaskClass> getChlid() {
        return chlid;
    }

    public void setChlid(List<TaskClass> chlid) {
        this.chlid = chlid;
    }

    public String getClass_id() {
        return class_id;
    }
    public int getIntClass_id() {
        return Integer.parseInt(TextUtils.isEmpty(getClass_id())?"0":getClass_id());
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }
}
