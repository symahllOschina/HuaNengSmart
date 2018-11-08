package com.huaneng.zhgd.utils;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.huaneng.zhgd.Constants;
import com.huaneng.zhgd.LoginActivity;
import com.huaneng.zhgd.MainActivity;
import com.huaneng.zhgd.bean.JPushMsg;
import com.huaneng.zhgd.bean.MeetingDeleteEvent;
import com.huaneng.zhgd.video.meeting.VideoMeetingActivity;
import com.inpor.fastmeetingcloud.ui.StartTheMiddleTierActivity;
import com.inpor.fastmeetingcloud.util.Constant;

import org.greenrobot.eventbus.EventBus;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by TH on 2017/11/28.
 */

public class JPushReceiver extends BroadcastReceiver {

    private static final String TAG = "【JPushReceiver】";

    private NotificationManager nm;

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[JPush用户注册成功] 接收Registration Id : " + regId);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
//            parseMsg(bundle); 同时收到JPushInterface.ACTION_MESSAGE_RECEIVED、JPushInterface.ACTION_NOTIFICATION_RECEIVED的通知，导致通知2次
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "收到了通知");
            parseMsg(bundle);
            receivingNotification(context, bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "用户点击打开了通知");
            show(bundle);
        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }

    /**
     * 用户点击打开了通知
     */
    private void show(Bundle bundle) {
        if (!SharedPreferencesUtils.create(context).getBoolean(Constants.SPR_LOGIN)) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return;
        }
        String msg = bundle.getString(JPushInterface.EXTRA_EXTRA);
        if (!TextUtils.isEmpty(msg)) {
            JPushMsg jPushMsg = JSONUtils.parseObject(msg, JPushMsg.class);
            jPushMsg.alert = bundle.getString(JPushInterface.EXTRA_ALERT);
            if (JPushMsg.TYPE_MEETING_RECEIVED.equals(jPushMsg.type)) {
                meetingLogin(jPushMsg);
            } else if(JPushMsg.TYPE_MEETING_DELETED.equals(jPushMsg.type)) {
                // 结束会议
            } else if(JPushMsg.TYPE_MEETING_MESSAGE.equals(jPushMsg.type)) {
                // 消息通知
                showMainActivity(2);
            } else {
                showMainActivity(0);
            }
        } else {
            showMainActivity(0);
        }
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
        context.startActivity(intent);
    }

    private void showMainActivity(int tab) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("tabIndex", tab);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
    private void parseMsg(Bundle bundle) {
//        String msg = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String alert = bundle.getString(JPushInterface.EXTRA_ALERT);
        String msg = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Log.d(TAG, "收到了自定义消息。消息内容是：" + msg);
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        JPushMsg jPushMsg = JSONUtils.parseObject(msg, JPushMsg.class);
        jPushMsg.alert = alert;

        if (JPushMsg.TYPE_MEETING_RECEIVED.equals(jPushMsg.type)) {
            EventBus.getDefault().post(jPushMsg);
        } else if (JPushMsg.TYPE_MEETING_DELETED.equals(jPushMsg.type)) {
            EventBus.getDefault().post(new MeetingDeleteEvent());
        }
//        String title = context.getString(R.string.app_name);
//        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification.Builder builder = new Notification.Builder(context);
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//        builder.setTicker("您有新消息");
//        builder.setContentTitle(title);
//        builder.setContentText(jPushMsg.alert); //消息内容
//        builder.setWhen(System.currentTimeMillis()); //发送时间
//        builder.setDefaults(Notification.DEFAULT_ALL);
//        builder.setAutoCancel(true);
//        Intent msgIntent = new Intent(context, MainActivity.class);
//        msgIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, msgIntent, 0);
//        builder.setContentIntent(pendingIntent);
//        Notification notification = builder.getNotification();
//        manager.notify((int) SystemClock.currentThreadTimeMillis(), notification);
    }

    private void receivingNotification(Context context, Bundle bundle) {
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        Log.d(TAG, " title : " + title);
        String message = bundle.getString(JPushInterface.EXTRA_ALERT);
        Log.d(TAG, "message : " + message);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Log.d(TAG, "extras : " + extras);
    }
}
