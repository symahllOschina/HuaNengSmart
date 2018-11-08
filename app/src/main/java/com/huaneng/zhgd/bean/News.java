package com.huaneng.zhgd.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 新闻
 */
public class News implements Serializable {

    public String id;
    public int catid;
    public String catname;
    public String author;
    public String commname;
    public String username;
    public String source;
    public String image;
    public String title;
    public String url;
    public String read_count;
    public String status;
    public String is_head_figure;
    public String update_time;
    public String create_time;

    public String content;
    public String content_type;
    public String upvote_count;
    public String description;
    public String is_allowcomments;

    public String company;
    public String active_status;

    // 党务办公-工作
    public String user_id;
    public String user_name;

    public String department;
    public String date;

    public ArrayList<String> getImageUrls() {
        if (!TextUtils.isEmpty(image)) {
            List<String> urls = new ArrayList<>();
            if (!image.startsWith("[")) {
                urls.add(image);
            } else {
                image = image.replaceAll(" ", "").replaceAll("\\[", "").replaceAll("\\]", "");
                urls = Arrays.asList(image.split(","));
            }
            return new ArrayList<>(urls);
        }
        return null;
    }

    public int getCompareValue() {
        return catid;
    }
}
