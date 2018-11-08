package com.huaneng.zhgd.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 生活指数
 */
public class LifeStyle {

    public static final Map<String, String> typeMap = new HashMap<String, String>();
    static {
        typeMap.put("comf", "舒适度指数");
        typeMap.put("cw", "洗车指数");
        typeMap.put("drsg", "穿衣指数");
        typeMap.put("flu", "感冒指数");
        typeMap.put("sport", "运动指数");
        typeMap.put("trav", "旅游指数");
        typeMap.put("uv", "紫外线指数");
        typeMap.put("air", "空气污染扩散条件指数");
    }

    public String brf;
    public String txt;
    public String type;

    public String getComf() {
        return typeMap.get(type);
    }

}
