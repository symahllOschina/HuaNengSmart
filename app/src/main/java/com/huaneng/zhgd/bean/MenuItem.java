package com.huaneng.zhgd.bean;

import android.content.Intent;

/**
 * Created by TH on 2017/11/2.
 */

public class MenuItem {

    public String itemName;   //名称

    public String iconUrl;

    public int itemDrawable;   //图片id

    public Intent intent = null;

    public MenuItem(String name, String iconUrl) {
        this.itemName = name;
        this.iconUrl = iconUrl;
    }

    public MenuItem(String name, int drawable) {
        this.itemName = name;
        this.itemDrawable = drawable;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }
}
