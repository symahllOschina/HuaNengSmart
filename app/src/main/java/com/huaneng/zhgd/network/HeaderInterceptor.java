package com.huaneng.zhgd.network;

import android.text.TextUtils;
import android.util.Base64;

import com.huaneng.zhgd.App;
import com.huaneng.zhgd.Constants;
import com.huaneng.zhgd.bean.UnLoginEvent;
import com.huaneng.zhgd.bean.UnauthorizedEvent;
import com.huaneng.zhgd.utils.AESHelper;
import com.huaneng.zhgd.utils.SharedPreferencesUtils;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by mashenghai on 2018/1/10.
 */

public class HeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder();
        String token = SharedPreferencesUtils.create(App.myself).get(Constants.SPR_TOKEN, "");
        if (!TextUtils.isEmpty(token)) {
            String signstr = Base64.encodeToString((System.currentTimeMillis() + "sfsdf").getBytes(), Base64.NO_WRAP);
            String version = "pabudv1";
            String signtoken = AESHelper.encrypt("signstr=" + signstr + "&version=" + version + "&time=" + System.currentTimeMillis());
            requestBuilder.addHeader("accesstoken", token);
            requestBuilder.addHeader("signtoken", signtoken);
            requestBuilder.addHeader("signstr", signstr);
            requestBuilder.addHeader("apptype", "android");
            requestBuilder.addHeader("version", version);
            requestBuilder.addHeader("model", android.os.Build.MODEL);
        }
        Request request = requestBuilder.build();
        Response response = chain.proceed(request);
        Logger.e("【response.code】" + response.code());
        if (response.code() == 402) {// 没有权限
            EventBus.getDefault().post(new UnauthorizedEvent());
        } else if (response.code() == 401) {// 没有登录
            EventBus.getDefault().post(new UnLoginEvent());
        }
        return response;
    }
}
