package com.huaneng.zhgd.notice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.huaneng.zhgd.BaseFragment;
import com.huaneng.zhgd.MainActivity;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.WebViewActivity;
import com.huaneng.zhgd.adapter.NoticeAdapter;
import com.huaneng.zhgd.bean.Article;
import com.huaneng.zhgd.bean.NewNoticeEvent;
import com.huaneng.zhgd.bean.Notice;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.network.Subscriber;
import com.huaneng.zhgd.utils.DateUtils;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 紧急通知
 */
@ContentView(R.layout.fragment_notice)
public class NoticeFragment extends BaseFragment {

    @ViewInject(R.id.listView)
    ListView mListView;
    @ViewInject(R.id.emptyView)
    View emptyView;

    List<Notice> notices = new ArrayList<Notice>();
    NoticeAdapter noticeAdapter;

    // 上一次消息的数量
    private int lastCount;
    private TickHandler tickHandler = new TickHandler();

    NotificationManager manager;
    int notification_id;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noticeAdapter = new NoticeAdapter(activity, notices);
        mListView.setAdapter(noticeAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(activity, NoticeActivity.class);
//                intent.putExtra("notice", notices.get(position));
//                startActivity(intent);
                detail(notices.get(position).id);
            }
        });
        tickHandler.tick(60000);
        manager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onResume() {
        notices.clear();
        load();
        super.onResume();
    }

    public void load() {
        if (!activity.checkNetwork()) {
            return;
        }
        HTTP.service.getNotices()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Notice>>(activity){

                    @Override
                    public void onSuccess(Response<List<Notice>> response) {
                        List<Notice> list = response.data;
                        if (list == null || list.isEmpty()) {
                            mListView.setEmptyView(emptyView);
                        } else {
                            notices.addAll(list);
                            lastCount = notices.size();
                            unReadNoticeNotify(notices);
                        }
                        noticeAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (notices.isEmpty()) {
                            mListView.setEmptyView(emptyView);
                        }
                    }

                    @Override
                    public void onFail(String msg) {
                        if (notices.isEmpty()) {
                            mListView.setEmptyView(emptyView);
                        }
                    }
                });
    }

    /**
     * 查看是否有新的通知
     */
    public void getNewNotice() {
        if (!activity.checkNetwork()) {
            return;
        }
        HTTP.service.getNewNotice(lastCount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Notice>>(activity){

                    @Override
                    public void onSuccess(Response<List<Notice>> response) {
                        // //新增几条未读消息
                        if (response.num > 0) {
                            notices.clear();
                            noticeAdapter.notifyDataSetChanged();
                            load();
                        }
                        List<Notice> list = response.data;
                        if (list != null && !list.isEmpty()) {
                            showNotice(list.get(0));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onFail(String msg) {
                    }
                });
    }


    public void showNotice(Notice notice) {
        Notification.Builder builder = new Notification.Builder(activity);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker("您有新消息");
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle(notice.title);
        builder.setContentText(notice.subtitle);
        Intent intent = new Intent(activity, MainActivity.class);
        PendingIntent ma = PendingIntent.getActivity(activity, 0, intent, 0);
        builder.setContentIntent(ma);//设置点击过后跳转的activity
        builder.setDefaults(Notification.DEFAULT_ALL);//设置全部
        Notification notification = builder.build();//4.1以上用.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;// 点击通知的时候cancel掉
        manager.notify(notification_id, notification);
    }
        /**
         * 未读通知
         */
    private void unReadNoticeNotify(List<Notice> list) {
        int count = 0;
        for (Notice notice: list) {
            if (notice.isUnRead()) {
                count++;
            }
        }
        EventBus.getDefault().post(new NewNoticeEvent(count));
    }

    public void detail(String id) {
        if (!activity.checkNetwork()) {
            return;
        }
        activity.showWaitDialog();
        HTTP.service.getArticle("notice/getlist/1", id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Article>(activity){

                    @Override
                    public void onSuccess(Response<Article> response) {
                        Article data = response.data;
                        if (data == null) {
                            activity.snackWarning("没有查询到信息.");
                            return;
                        }
                        Intent intent = new Intent(activity, WebViewActivity.class);
                        intent.putExtra("title", data.title);
                        intent.putExtra("subtitle", data.subtitle);
                        intent.putExtra("time", DateUtils.millisecondToDate(data.create_time));

                        String content = data.content;
                        if (!TextUtils.isEmpty(content) && content.length() > 100000) {
                            WebViewActivity.bigData = content;
                            intent.putExtra("isBigData", true);
                        } else {
                            intent.putExtra("html", content);
                        }
                        startActivity(intent);
                    }
                });
    }

    class TickHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            getNewNotice();
            tick(60000);
        }

        public void tick(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };
}
