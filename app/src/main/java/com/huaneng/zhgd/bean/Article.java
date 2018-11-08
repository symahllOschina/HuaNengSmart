package com.huaneng.zhgd.bean;

import android.text.TextUtils;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by TH on 2017/11/10.
 */

public class Article {

    public String id;
    public String title;
    public String subtitle;
    public String content;
    public String image;
    public String isdelete;
    public String isallow;
    public String addtime;
    public String updatetime;
    public String create_time;

    public String getTime() {
        if (!TextUtils.isEmpty(updatetime)) {
            Date date = new Date(Long.valueOf(updatetime));
            DateFormat format = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
            return format.format(date);
        }
        return "";
    }
}
