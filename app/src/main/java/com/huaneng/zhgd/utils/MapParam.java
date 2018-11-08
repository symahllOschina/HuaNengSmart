package com.huaneng.zhgd.utils;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by TH on 2018/1/12.
 */

public class MapParam {

    private Map<String, Object> map;

    public static MapParam me() {
        MapParam mapUtils = new MapParam();
        mapUtils.map = new HashMap<String, Object>();
        return mapUtils;
    }

    public MapParam p(String key, Object param, boolean condition) {
        if (condition) {
            return p(key, param);
        }
        return this;
    }

    public MapParam p(String key, Object param) {
        if (param != null && !TextUtils.isEmpty(param.toString())) {
            map.put(key, param);
        }
        return this;
    }

    public Map<String, Object> build() {
        return map;
    }
}
