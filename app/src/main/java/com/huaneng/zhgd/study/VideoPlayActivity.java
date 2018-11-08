package com.huaneng.zhgd.study;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.huaneng.zhgd.BaseActivity;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.Attachment;
import com.huaneng.zhgd.bean.VideoPlayEvent;
import com.huaneng.zhgd.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * 播放视频文件
 */
@ContentView(R.layout.activity_video_play)
public class VideoPlayActivity extends BaseActivity {

    @ViewInject(R.id.videoView)
    private VideoView videoView;

    Attachment attachment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attachment = (Attachment)getIntent().getSerializableExtra("attachment");
//        setTitle(attachment.title);
        Uri uri = Uri.parse(attachment.path);
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.requestFocus();// 可显示控制面板，几秒后消失，需点击获取到焦点后再次显示
    }

    protected boolean isPortrait() {
        return false;
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().post(new VideoPlayEvent(videoView.getCurrentPosition()));
        super.onPause();
    }
}
