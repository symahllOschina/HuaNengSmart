package com.huaneng.zhgd.network;

import com.huaneng.zhgd.BuildConfig;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by TH on 2017/11/8.
 */

public class RetrofitUtils {

//    public static final String SERVER = "http://113.200.203.98:8000/";
    public static final String SERVER = "http://113.200.203.89/wisdom/public/index.php/";
    public static final String baseUrl = SERVER + "api/v1/";
    private static final long DEFAULT_TIME_OUT = 60;
    private static final Retrofit retrofit;

    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new HeaderInterceptor())
                .connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)//连接超时时间
                .writeTimeout(DEFAULT_TIME_OUT,TimeUnit.SECONDS)//写操作 超时时间
                .readTimeout(DEFAULT_TIME_OUT,TimeUnit.SECONDS);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLogger());
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        }

        retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static <T> T getService(Class<T> t) {
        return retrofit.create(t);
    }
}
