package com.huaqie.wubingjie.utils;


import com.huaqie.wubingjie.MainApplication;

/**
 * Created by lewis on 16/6/22.
 */
public class SettingsManager {
    public static final String KEY_CITY_ID = "member_city_id";
    public static final String KEY_CITY_NAME = "member_city_name";
    public static final String KEY_APP_CODE = "now_app_code";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LNG = "lng";

    public static String getKeyCityId() {
        return (String) SPUtils.getSettingInstance().get(KEY_CITY_ID, "0");
    }

    public static void setKEY_CITY_ID(String cityId) {
        SPUtils.getSettingInstance().put(KEY_CITY_ID, cityId);
    }


    public static String getKeyCityName() {
        return (String) SPUtils.getSettingInstance().get(KEY_CITY_NAME, "全国");
    }

    public static void setKEY_CITY_NAME(String cityName) {
        SPUtils.getSettingInstance().put(KEY_CITY_NAME, cityName);
    }

    public static String getLat() {
        return (String) SPUtils.getSettingInstance().get(KEY_LAT, "");
    }

    public static void setLat(String lat) {
        SPUtils.getSettingInstance().put(KEY_LAT, lat);
    }

    public static String getLng() {
        return (String) SPUtils.getSettingInstance().get(KEY_LNG, "");
    }

    public static void setLng(String lng) {
        SPUtils.getSettingInstance().put(KEY_LNG, lng);
    }

    public static void saveAppCode() {
        SPUtils.getSettingInstance().put(KEY_APP_CODE, SystemUtils.getAppVersionCode(MainApplication.getInstance()));
    }

    public static int getAppCode() {
        return (int) SPUtils.getSettingInstance().get(KEY_APP_CODE, 0);
    }

    /**
     * 是否为新版本第一次启动
     *
     * @return
     */
    public static boolean isNewCodeFirst() {
        if (getAppCode() == SystemUtils.getAppVersionCode(MainApplication.getInstance())) {
            return false;
        }
        return true;
    }
    public static void clean(){
        SPUtils.getSettingInstance().clear();
    }
}
