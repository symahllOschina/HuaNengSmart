package com.huaneng.zhgd.bean;

import android.text.TextUtils;

/**
 * Created by Administrator on 2017/12/4.
 */

public class HeWeather6Basic {

    public String cid;
    public String location;
    public String parent_city;
    public String admin_area;
    public String cnty;
    public String lat;
    public String lon;
    public String tz;

    public String getCity() {
        if (!TextUtils.isEmpty(parent_city) && !parent_city.equals(location)) {
            return parent_city + "." + location;
        }
        return location;
    }
}
