package com.huaneng.zhgd.network;

/**
 * Created by TH on 2017/11/8.
 */

public class HTTP {

    public static final HTTPService service;
    public static final WeatherService weatherService;
    static {
        service = RetrofitUtils.getService(HTTPService.class);
        weatherService = WeatherUtils.getService();
    }
}
