package com.huaneng.zhgd.video.monitor;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.PTZCommand;
import com.huaneng.zhgd.BaseActivity;
import com.huaneng.zhgd.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_single_monitor)
public class SingleMonitorActivity extends BaseActivity implements SurfaceHolder.Callback, ICount {

    public final static int DIRECTION_LEFT = 1;
    public final static int DIRECTION_RIGHT = 2;
    public final static int DIRECTION_UP = 3;
    public final static int DIRECTION_DOWN = 4;
    public final static int ZOOM_IN = 5;
    public final static int ZOOM_OUT = 6;

    public final static int ACTION_START = 0;
    public final static int ACTION_STOP = 1;

    @ViewInject(R.id.Sur_Player)
    private SurfaceView m_osurfaceView = null;

    @ViewInject(R.id.leftLayout)
    private LinearLayout leftLayout;
    @ViewInject(R.id.zoomInBtn)
    private ImageView zoomInBtn;
    @ViewInject(R.id.leftBtn)
    private ImageView leftBtn;
    @ViewInject(R.id.zoomOutBtn)
    private ImageView zoomOutBtn;

    @ViewInject(R.id.rightLayout)
    private LinearLayout rightLayout;
    @ViewInject(R.id.upBtn)
    private ImageView upBtn;
    @ViewInject(R.id.rightBtn)
    private ImageView rightBtn;
    @ViewInject(R.id.downBtn)
    private ImageView downBtn;

    PlaySurfaceView playSurfaceView;
    public int m_iUserID = -1;
    public int m_iChan = 0;
    int count = 0;
    private boolean isReady;
    private int retryCount = 0;
    private int retryMax = 3;
    private int mLastAction = -1;//最后一个action

    private boolean isOption = false;//监控是否可操作，默认不可操作

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        m_iUserID = getIntent().getIntExtra("m_iUserID", -1);
        m_iChan = getIntent().getIntExtra("m_iChan", 0);

        m_osurfaceView.getHolder().addCallback(this);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        playSurfaceView = new PlaySurfaceView(this, this);
        playSurfaceView.setSize(metric.widthPixels, metric.heightPixels);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        params.leftMargin = (metric.widthPixels - playSurfaceView.getM_iWidth())/2;
        addContentView(playSurfaceView, params);
        leftBtn.setOnTouchListener(new RotateOnTouchListener(DIRECTION_LEFT));
        rightBtn.setOnTouchListener(new RotateOnTouchListener(DIRECTION_RIGHT));
        upBtn.setOnTouchListener(new RotateOnTouchListener(DIRECTION_UP));
        downBtn.setOnTouchListener(new RotateOnTouchListener(DIRECTION_DOWN));
        zoomInBtn.setOnTouchListener(new RotateOnTouchListener(ZOOM_IN));
        zoomOutBtn.setOnTouchListener(new RotateOnTouchListener(ZOOM_OUT));
    }

    private void initData(){
        isOption = VideoMonitorActivity.isOption;
        leftLayout.setVisibility(View.GONE);
        rightLayout.setVisibility(View.GONE);
        if(isOption){
            leftLayout.setVisibility(View.VISIBLE);
            rightLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void count() {
        count++;
        if (count == 2) {
            isReady = true;
            playSurfaceView.startPreview(m_iUserID, m_iChan);
        }
    }

    @Override
    public boolean isPortrait() {
        return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        m_osurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

    @Override
    protected void onDestroy() {
        playSurfaceView.stopPreview();
        if (mLastAction != -1) {
            up(mLastAction);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
//        Intent intent = new Intent(this, VideoMonitorActivity.class);
//        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }

    class RotateOnTouchListener implements View.OnTouchListener {

        int direction;

        public RotateOnTouchListener(int direction) {
            this.direction = direction;
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (!isReady) {
                return false;
            }
            try {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (direction == DIRECTION_LEFT) {
                        left(ACTION_START);
                    } else if (direction == DIRECTION_RIGHT) {
                        right(ACTION_START);
                    } else if (direction == DIRECTION_UP) {
                        up(ACTION_START);
                    } else if (direction == DIRECTION_DOWN) {
                        down(ACTION_START);
                    } else if (direction == ZOOM_IN) {
                        focusNear(ACTION_START);
                    } else if (direction == ZOOM_OUT) {
                        focusFar(ACTION_START);
                    }

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (direction == DIRECTION_LEFT) {
                        left(ACTION_STOP);
                    } else if (direction == DIRECTION_RIGHT) {
                        right(ACTION_STOP);
                    } else if (direction == DIRECTION_UP) {
                        up(ACTION_STOP);
                    } else if (direction == DIRECTION_DOWN) {
                        down(ACTION_STOP);
                    } else if (direction == ZOOM_IN) {
                        focusNear(ACTION_STOP);
                    } else if (direction == ZOOM_OUT) {
                        focusFar(ACTION_STOP);
                    }
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public void left(final int action) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iUserID, m_iChan, PTZCommand.PAN_LEFT, action)) {
                    Log.e(TAG,"start PAN_LEFT failed with error code: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
                } else {
                    Log.i(TAG, "start PAN_LEFT success");
                }
            }
        }).start();
    }

    public void right(final int action) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iUserID, m_iChan, PTZCommand.PAN_RIGHT, action)) {
                    Log.e(TAG, "start PAN_RIGHT failed with error code: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
                } else {
                    Log.i(TAG, "start PAN_RIGHT success");
                }
            }
        }).start();
    }

    public void up(final int action) {
        retryCount = 0;
        new Thread(new Runnable() {



            @Override
            public void run() {
                boolean success;
                do {
                    success = action();
                    if (!success) {
                        if (action == ACTION_STOP) {
                            mLastAction = action;
                        }
                        Log.e(TAG,"失败：" + retryCount);
                        Log.e(TAG,"start PAN_LEFT failed with error code: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
                    } else {
                        mLastAction = -1;
                        Log.i(TAG, "start PAN_LEFT success");
                    }
                } while (!success && (action == ACTION_STOP ) && (retryCount < retryMax));
            }

            public boolean action() {
                return HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iUserID, m_iChan, PTZCommand.TILT_UP, action);
            }
        }).start();
    }

    public void down(final int action) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iUserID, m_iChan, PTZCommand.TILT_DOWN, action)) {
                    Log.e(TAG, "start PAN_RIGHT failed with error code: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
                } else {
                    Log.i(TAG, "start PAN_RIGHT success");
                }
            }
        }).start();
    }

    public void focusNear(final int action) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iUserID, m_iChan, PTZCommand.ZOOM_IN, action)) {
                    Log.e(TAG,"start PAN_LEFT failed with error code: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
                } else {
                    Log.i(TAG, "start PAN_LEFT success");
                }
            }
        }).start();
    }

    public void focusFar(final int action) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iUserID, m_iChan, PTZCommand.ZOOM_OUT, action)) {
                    Log.e(TAG, "start PAN_RIGHT failed with error code: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
                } else {
                    Log.i(TAG, "start PAN_RIGHT success");
                }
            }
        }).start();
    }
}
