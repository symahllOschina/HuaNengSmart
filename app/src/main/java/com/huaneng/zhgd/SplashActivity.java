package com.huaneng.zhgd;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.huaneng.zhgd.utils.SharedPreferencesUtils;
import com.tencent.bugly.crashreport.CrashReport;

import org.xutils.view.annotation.ContentView;

@ContentView(R.layout.activity_splash)
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        CrashReport.testJavaCrash();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Class<?> clazz = LoginActivity.class;
                if (SharedPreferencesUtils.create(ctx).getBoolean(Constants.SPR_LOGIN)) {
                    clazz = MainActivity.class;
                }
                Intent intent = new Intent(SplashActivity.this, clazz);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
