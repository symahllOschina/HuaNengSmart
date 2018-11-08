package com.huaneng.zhgd.network;

import com.orhanobut.logger.Logger;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Administrator on 2018/1/6.
 */

public class HttpLogger implements HttpLoggingInterceptor.Logger {
    @Override
    public void log(String message) {
        if (message.startsWith("{")) {
            // json 字符串
            Logger.json(message);
        } else {
            Logger.d(message);
        }
    }
}