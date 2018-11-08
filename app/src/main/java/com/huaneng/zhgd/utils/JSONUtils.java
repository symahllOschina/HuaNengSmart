package com.huaneng.zhgd.utils;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * 基于fastjson的JSON工具类
 */
public class JSONUtils {

    /**
     * 解析json对象
     */
    public static <T> T parseObject(Object obj, Class<T> t) {
        if (obj != null && !TextUtils.isEmpty(obj.toString())) {
            return JSON.parseObject(obj.toString(), t);
        }
        return null;
    }

    /**
     * 解析json对象数组
     */
    public static <T> List<T> parseArray(Object obj, Class<T> t) {
        if (obj != null && !TextUtils.isEmpty(obj.toString())) {
            return JSON.parseArray(obj.toString(), t);
        }
        return null;
    }
}
