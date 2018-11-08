package com.huaneng.zhgd.bean;

/**
 * Created by Administrator on 2017/12/4.
 */

public class HNLocation {

    public String city;

    public String longLat;

    public HNLocation(String city, double longitude, double latitude) {
        this.city = city;
        this.longLat = longitude + "," + latitude;
    }
}
