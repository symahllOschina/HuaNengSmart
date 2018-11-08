package com.huaneng.zhgd.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.huaneng.zhgd.Constants;

/**
 * SharedPreferences工具类
 * 
 */
public class SharedPreferencesUtils {
    private SharedPreferences sp;
    private Editor editor;

    public static SharedPreferencesUtils create(Context context) {
        return new SharedPreferencesUtils(context);
    }

    /**
     * 构造函数
     * @param context
     */
    public SharedPreferencesUtils(Context context) {
        sp = context.getSharedPreferences(Constants.APP_CODE, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    /**
     * 存入键值对
     * @param key
     * @param value
     */
    public void put(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }
    
    /**
     * 存入键值对
     * @param key
     * @param value
     */
    public void putInt(String key, int value) {
    	editor.putInt(key, value);
    	editor.commit();
    }

    /**
     * 根据键，取出值
     * @param key
     * @return
     */
    public String get(String key) {
        return get(key, null);
    }
    
    /**
     * 根据键，取出值
     * @param key 键值
     * @param defaultValue 默认值
     * @return
     */
    public String get(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }
    
    /**
     * 根据键，取出值
     * @param key 键值
     * @param defaultValue 默认值
     * @return
     */
    public int getInt(String key, int defaultValue) {
    	return sp.getInt(key, defaultValue);
    }
    
    /**
     * 存入键值对
     * @param key
     * @param value
     */
    public void putBoolean(String key, boolean value) {
    	editor.putBoolean(key, value);
    	editor.commit();
    }

    /**
     * 根据键，取出值
     * @param key
     * @return
     */
    public boolean getBoolean(String key) {
    	 return sp.getBoolean(key, false);
    }
    /**
     * 根据键，取出值
     * @param key
     * @return
     */
    public boolean getBoolean(String key, boolean defaultValue) {
    	 return sp.getBoolean(key, defaultValue);
    }

    public void clear() {
        put(Constants.SPR_NAME, null);
        put(Constants.SPR_PASSWD, null);
        putInt(Constants.SPR_VERSION, 0);
        putBoolean(Constants.SPR_LOGIN, false);
        put(Constants.SPR_MENU_JSON, null);
    }
}