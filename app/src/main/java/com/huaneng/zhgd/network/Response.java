package com.huaneng.zhgd.network;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by TH on 2017/11/8.
 */

public class Response<T> {

    public int state;//0-失败 1-成功
    public int code;
    public int num;//总记录数
    public int page_num;//总页数
    public String msg;
    public String token;
    public String score;
    public String imageurl;
    public T data;

    public boolean suceess() {
        return state == 1;
    }

//    public <T> T getEntity(Class<T> t) {
//        if (validData()) {
//            return JSON.parseObject(data.toString(), t);
//        }
//        return null;
//    }
//
//    public <T> List<T> getEntityList(Class<T> t) {
//        if (validData()) {
//            return JSON.parseArray(data.toString(), t);
//        }
//        return null;
//    }
//
//    private boolean validData() {
//        if (data == null || TextUtils.isEmpty(data.toString()) || "[]".equals(data.toString())) {
//            return false;
//        }
//        return true;
//    }
}
