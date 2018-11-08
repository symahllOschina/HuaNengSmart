package com.huaneng.zhgd.study;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

import com.huaneng.zhgd.BaseActivity;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.Attachment;
import com.huaneng.zhgd.bean.VideoPlayEvent;

import org.greenrobot.eventbus.Subscribe;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * 播放视频文件
 */
@ContentView(R.layout.activity_video_play_v)
public class VideoPlayVerticalActivity extends BaseActivity {

    @ViewInject(R.id.videoView)
    private VideoView videoView;

    Attachment attachment;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attachment = (Attachment)getIntent().getSerializableExtra("attachment");
        setTitle(attachment.title);
        Uri uri = Uri.parse(attachment.path);
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.requestFocus();// 可显示控制面板，几秒后消失，需点击获取到焦点后再次显示
    }

    @Override
    protected boolean isEnableEventBus() {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_full_screen_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_play:
                fullScreenPlay();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fullScreenPlay() {
        Intent intent = new Intent(ctx, VideoPlayActivity.class);
        intent.putExtra("attachment", attachment);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        if (videoView.canPause()) {
            videoView.pause();
        }
        super.onPause();
    }

    @Subscribe
    public void onEvent(VideoPlayEvent event) {
        position = event.msec;
    }

    @Override
    protected void onRestart() {
        videoView.resume();
        videoView.start();
        videoView.seekTo(position);
        super.onRestart();
    }
}
