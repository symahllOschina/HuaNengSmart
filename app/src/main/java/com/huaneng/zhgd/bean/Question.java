package com.huaneng.zhgd.bean;

import android.text.TextUtils;

/**
 * 试题
 */
public class Question {

    public String id;
    public String questionid;
    public String title;
    public String answer_a;
    public String answer_b;
    public String answer_c;
    public String answer_d;
    public String score;
    public String update_time;
    public String create_time;

    public String getTitleAndScore() {
        if (!TextUtils.isEmpty(score)) {
            return title + "(" + score + "分)";
        }
        return title;
    }

}
