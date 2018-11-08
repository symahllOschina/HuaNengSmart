package com.huaneng.zhgd.bean;

import android.text.TextUtils;

/**
 * 3-10天天气预报
 */
public class DailyForecast {

    public String cond_code_d;
    public String cond_code_n;
    public String cond_txt_d;
    public String cond_txt_n;
    public String date;
    public String hum;
    public String mr;
    public String ms;
    public String pcpn;
    public String pop;
    public String pres;
    public String sr;
    public String ss;
    public String tmp_max;
    public String tmp_min;
    public String uv_index;
    public String vis;
    public String wind_deg;
    public String wind_dir;
    public String wind_sc;
    public String wind_spd;

    public String formatDate() {
        if (!TextUtils.isEmpty(date)) {
            return date.replaceAll("-", "/").substring(5);
        }
        return null;
    }
}
