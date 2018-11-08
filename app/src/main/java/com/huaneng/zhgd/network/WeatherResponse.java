package com.huaneng.zhgd.network;

/**
 * Created by TH on 2017/11/8.
 */

public class WeatherResponse<T> {

    public int state;//0-失败 1-成功
    public int code;
    public int num;
    public String msg;
    public T data;

    public boolean suceess() {
        return state == 1;
    }
}
