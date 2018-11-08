package com.huaneng.zhgd.utils;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.huaneng.zhgd.App;
import com.huaneng.zhgd.bean.User;

public class UserUtils {

    private static final String SPR_USER_JSON = "user_json";

    public static void save(User user) {
        if (user != null) {
            SharedPreferencesUtils.create(App.myself).put(SPR_USER_JSON, JSON.toJSONString(user));
        }
    }

    public static User getUser() {
        String json = SharedPreferencesUtils.create(App.myself).get(SPR_USER_JSON);
        Log.e("getUser()的json值",json);
        if (!TextUtils.isEmpty(json)) {
            User user = JSON.parseObject(json, User.class);
            return user;
        }
        return null;
    }

    public static void clearUser() {
        SharedPreferencesUtils.create(App.myself).put(SPR_USER_JSON, null);
    }

}
