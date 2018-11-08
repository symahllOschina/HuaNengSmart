package com.huaneng.zhgd.bean;

import android.content.Intent;

import java.io.Serializable;

/**
 * Created by TH on 2017/11/2.
 */

public class Module implements Serializable {

    public String title;
    public String subTitle;// 副标题
    public int icon;   //图片id
    public Intent intent = null;

    public Module(String title, String subTitle, int drawable) {
        this.title = title;
        this.subTitle = subTitle;
        this.icon = drawable;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }
}
