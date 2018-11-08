package com.huaneng.zhgd.bean;

import android.text.TextUtils;

import com.huaneng.zhgd.network.RetrofitUtils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 重要甲供设备材料信息
 */
public class ImportDeviceInfo implements Serializable {

    public String id;
    public String image;
    public String title;
    public String content;
    public String subtitle;
    public String create_time;

    public boolean hasImage() {
        return !TextUtils.isEmpty(image);
    }

    public String getImageUrl() {
        if (!image.startsWith("http")) {
            image = RetrofitUtils.SERVER + image;
        }
        return image;
    }

    public String getTime() {
        if (!TextUtils.isEmpty(create_time)) {
            Date date = new Date(Long.valueOf(create_time));
            DateFormat format = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
            return format.format(date);
        }
        return "";
    }
}
