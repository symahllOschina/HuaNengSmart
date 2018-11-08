package com.huaneng.zhgd.bean;

/**
 * Created by TH on 2017/12/5.
 */

class HeBaseWeather {

    public HeWeather6Basic basic;
    public HeWeather6Update update;
    public String status;

    public boolean success() {
        return "ok".equals(status);
    }
}
