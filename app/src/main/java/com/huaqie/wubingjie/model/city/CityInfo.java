package com.huaqie.wubingjie.model.city;

import me.yokeyword.indexablelistview.IndexEntity;

/**
 * Created by AINANA-RD-X on 2016/7/11.
 */
public class CityInfo extends IndexEntity {
    //城市id
    private String id;
    //父级id  0为省或者直辖市活特别行政区
    private String pid;
    //名称
    private String name;
    //等级 1为省或者直辖市活特别行政区  等级 2为市
    private String level;
    //拼音名称
    private String pinyin;
    //首字母
    private String prefix;
    //是否是热门城市  0为不是，1为是
    private String is_hot;

    public CityInfo() {
    }

    public CityInfo(String id, String pid, String name, String level, String pinyin, String prefix) {
        this.id = id;
        this.pid = pid;
        this.name = name;
        this.level = level;
        this.pinyin = pinyin;
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        return "CityList{" +
                "id='" + id + '\'' +
                ", pid='" + pid + '\'' +
                ", name='" + name + '\'' +
                ", level='" + level + '\'' +
                ", pinyin='" + pinyin + '\'' +
                ", prefix='" + prefix + '\'' +
                ", is_hot='" + is_hot + '\'' +
                '}';
    }

    public String getIs_hot() {
        return is_hot;
    }

    public void setIs_hot(String is_hot) {
        this.is_hot = is_hot;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
