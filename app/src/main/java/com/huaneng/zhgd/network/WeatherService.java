package com.huaneng.zhgd.network;

import com.huaneng.zhgd.bean.HeAir;
import com.huaneng.zhgd.bean.HeWeather;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/12/3.
 */
// 和风天气
public interface WeatherService {
    // 常规天气数据集合
//    @FormUrlEncoded
    @GET("weather")
    Observable<HeWeather> weather(@Query("key") String key, @Query("location") String location);

    // 空气质量数据集合
//    @FormUrlEncoded
    @GET("air")
    Observable<HeAir> air(@Query("key") String key, @Query("location") String location);
}
