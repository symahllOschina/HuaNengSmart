package com.huaneng.zhgd.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 试题
 */
public class TestQuestion implements Serializable {

    public String id;
    public String title;
    public String status;
    public String update_time;
    public String create_time;
    public String min;
    public String max;
    public String last;
    public String exam_time;

    public boolean isJoin() {
        return !TextUtils.isEmpty(min) || !TextUtils.isEmpty(max);
    }

    public int getExamTimeSecond() {
        try {
            int time = Integer.valueOf(exam_time) * 60;
            return time;
        } catch (Exception e) {
            return 600;
        }
    }
}
