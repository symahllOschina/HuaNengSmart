package com.huaneng.zhgd.bean;

import java.io.Serializable;

/**
 * 通知公告
 */
public class Notice implements Serializable {

    public String id;
    public String title;
    public String image;
    public String subtitle;
    public String create_time;
    public String is_read;//0未读，1已读

    public boolean isUnRead() {
        return "0".equals(is_read);
    }

}
