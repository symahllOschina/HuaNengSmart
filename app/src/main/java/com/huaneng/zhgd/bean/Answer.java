package com.huaneng.zhgd.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 试题-答案
 */
public class Answer implements Serializable {

    public String id;
    public String title;
    public String answer_a;
    public String answer_b;
    public String answer_c;
    public String answer_d;
    public String score;
    public String result; //1 为正确 2为答案错误
    public String choice;
    public String answer;// 正确答案

    public String getTitleAndScore() {
        if (!TextUtils.isEmpty(score)) {
            return title + "(" + score + "分)";
        }
        return title;
    }

    public int getChoiceIndex() {
        if ("a".equals(choice)) {
            return 0;
        } else if ("b".equals(choice)) {
            return 1;
        } else if ("c".equals(choice)) {
            return 2;
        } else {
            return 3;
        }
    }

    public boolean isCorrect() {
        return "1".equals(result);
    }



}
