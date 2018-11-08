package com.huaneng.zhgd.notice;

import android.os.Bundle;
import android.widget.TextView;

import com.huaneng.zhgd.BaseActivity;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.Notice;
import com.huaneng.zhgd.utils.DateUtils;
import com.huaneng.zhgd.utils.UIUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * 通知公告详情
 */
@ContentView(R.layout.activity_notice)
public class NoticeActivity extends BaseActivity {

    @ViewInject(R.id.titleTv)
    private TextView titleTv;
    @ViewInject(R.id.timeTv)
    private TextView timeTv;
    @ViewInject(R.id.contentTv)
    private TextView contentTv;

    Notice notice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("紧急通知");
        notice = getSerializableExtra("notice");
        titleTv.setText(notice.title);
        UIUtils.showText(timeTv, "发布时间：", DateUtils.millisecondToTime(notice.create_time));
        contentTv.setText(notice.subtitle);
    }
}
