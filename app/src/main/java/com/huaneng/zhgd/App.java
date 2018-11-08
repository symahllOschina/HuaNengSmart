package com.huaneng.zhgd;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hikvision.netsdk.HCNetSDK;
import com.huaneng.zhgd.bean.JPushMsg;
import com.huaneng.zhgd.bean.JoinMeetingEvent;
import com.huaneng.zhgd.bean.UnLoginEvent;
import com.huaneng.zhgd.bean.UnauthorizedEvent;
import com.huaneng.zhgd.utils.SharedPreferencesUtils;
import com.huaneng.zhgd.utils.UserUtils;
import com.huaneng.zhgd.video.meeting.VideoMeetingActivity;
import com.inpor.fastmeetingcloud.receiver.HstApplication;
import com.inpor.fastmeetingcloud.ui.StartTheMiddleTierActivity;
import com.inpor.fastmeetingcloud.util.Constant;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by TH on 2017/11/2.
 */

public class App extends Application {

    private static final String TAG = "N2MApplication";
    public static App myself;
    String logfile;
    List<Activity> activityList = new ArrayList<>();
    private AlertDialog meetingDialog;

    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
                return new MaterialHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }

    public void onCreate() {
        super.onCreate();
        myself = this;
        HstApplication.init(getApplicationContext());
        EventBus.getDefault().register(this);
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);

        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)// 不显示线程信息
                .methodCount(0)// 不显示方法
                .tag("Okhttp")// 定义全局tag
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        if (!BuildConfig.DEBUG) {
            CrashReport.initCrashReport(getApplicationContext(), "07b15591eb", true);
//        CrashReport.initCrashReport(getApplicationContext());
        }

        // 腾讯TBS
        QbSdk.initX5Environment(this, null);
        QbSdk.setDownloadWithoutWifi(true);//设置支持非Wifi下载

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                Log.d(TAG, "onActivityCreated:" + activity.hashCode());
//                if (activity instanceof SplashActivity) {
//                    return;
//                }
                activityList.add(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.d(TAG, "onActivityDestroyed:" + activity.hashCode());
                activityList.remove(activity);
            }
        });
    }

    long lasgUnauthorizedEventTime = 0;
    // Http 401 没有登录
    @Subscribe
    public void onUnLogin(final UnLoginEvent event) {
        long nowTime = System.currentTimeMillis();
        long diff = nowTime - lasgUnauthorizedEventTime;
        if (lasgUnauthorizedEventTime > 0 && diff < 3000) {
            lasgUnauthorizedEventTime = nowTime;
            return;
        }
        lasgUnauthorizedEventTime = nowTime;
        SharedPreferencesUtils.create(this).clear();
        finishActivities();
        toast("请重新登录.");
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // 9008 没有权限
    @Subscribe
    public void onUnauthorized(final UnauthorizedEvent event) {
        toast("没有操作权限.");
    }

    @Subscribe
    public void onReceiveJPushMsg(final JPushMsg jPushMsg) {
        if (!SharedPreferencesUtils.create(this).getBoolean(Constants.SPR_LOGIN)) {
            return;
        }
        if (jPushMsg == null || TextUtils.isEmpty(jPushMsg.roomid) || activityList.isEmpty()) {
            return;
        }
        final Activity activity = activityList.get(activityList.size() - 1);
        meetingDialog = new AlertDialog.Builder(activity)
                .setTitle("提示")
                .setMessage(jPushMsg.alert)
                .setPositiveButton("进入会议", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        meetingLogin(jPushMsg);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    /**
     * 账户密码登陆
     */
    public void meetingLogin(JPushMsg msg) {
        ComponentName componentName= new ComponentName("com.huaneng.zhgd", "com.inpor.fastmeetingcloud.ui.StartTheMiddleTierActivity");
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Constant.INTENT_APP_ACTION);
        intent.putExtra(StartTheMiddleTierActivity.ThirdLoginConstant.BUNDLE_USERNAME, UserUtils.getUser().markid);
        intent.putExtra(StartTheMiddleTierActivity.ThirdLoginConstant.BUNDLE_PASSWORD, msg.pwd);
        intent.putExtra(StartTheMiddleTierActivity.ThirdLoginConstant.BUNDLE_ROOMID, Long.valueOf(msg.roomid));
        intent.putExtra(StartTheMiddleTierActivity.ThirdLoginConstant.BUNDLE_SERVER_ADDRESS, VideoMeetingActivity.SERVER);
        intent.putExtra(StartTheMiddleTierActivity.ThirdLoginConstant.BUNDLE_SERVER_PORT, VideoMeetingActivity.PORT);
        intent.setComponent(componentName);
        startActivity(intent);
    }

    @Subscribe
    public void onJoinMeetingEvent(final JoinMeetingEvent event) {
        if (meetingDialog != null) {
            meetingDialog.dismiss();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        HCNetSDK.getInstance().NET_DVR_Cleanup();
        EventBus.getDefault().unregister(this);
        Log.i(TAG, "onTerminate, after uninit AVDEngine ");
    }

    public void exit() {
        finishActivities();
        HCNetSDK.getInstance().NET_DVR_Cleanup();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void finishActivities() {
        for (Activity activity: activityList) {
            activity.finish();
        }
    }

    public static void toast(final int resId) {
        toast(myself.getString(resId));
    }

    public static void toast(final String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (Looper.myLooper() != Looper.getMainLooper()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(myself, msg, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(myself, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
