package com.huaqie.wubingjie.model;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;

/**
 * Created by AINANA-RD-X on 2016/7/11.
 */
public class MyLatlng implements Serializable {
    private String address;
    private String lat;
    private String lng;

    public MyLatlng() {
    }

    public MyLatlng(String address,String lat,String lng) {
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public LatLng parse() {
        try {
            return new LatLng(Double.parseDouble(getLat()), Double.parseDouble(getLng()));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "MyLatlng{" +
                "address='" + address + '\'' +
                ", lng='" + lng + '\'' +
                ", lat='" + lat + '\'' +
                '}';
    }
}
