package com.huaneng.zhgd.video.monitor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.huaneng.zhgd.utils.UIUtils;

@SuppressLint("NewApi")
public class PlaySurfaceView extends SurfaceView implements Callback, View.OnClickListener {

    private final String TAG = "PlaySurfaceView";
    private int m_iWidth = 0;
    private int m_iHeight = 0;
    public int m_iPreviewHandle = -1;
    private SurfaceHolder m_hHolder;
    public boolean bCreate = false;
    public int m_iUserID = -1;
    public int m_iChan = 0;
    ICount counter;
    private boolean clickable;

    public PlaySurfaceView(Context context, ICount counter) {
        super(context);
        clickable = !(context instanceof SingleMonitorActivity);
        this.counter = counter;
        m_hHolder = this.getHolder();
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        System.out.println("surfaceChanged");
    }

    public void setParam(int nScreenSize, int height) {
        m_iWidth = nScreenSize / 2;
//        m_iWidth = nScreenSize;
        m_iHeight = (m_iWidth * 3) / 4;
//        m_iHeight = height / 2;
    }

    public void setSize(int width, int height) {
        m_iHeight = height;
        m_iWidth = (m_iHeight * 4) / 3;
    }

    public void startPreview(int iUserID, int iChan) {
        Log.i(TAG, "previewOrStop channel:" + iChan);
        while (!bCreate) {
            try {
                Thread.sleep(100);
                Log.i(TAG, "wait for surface create");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        m_iUserID = iUserID;
        m_iChan = iChan;
        if (clickable) {
            setOnClickListener(this);
        }
        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = iChan;
        previewInfo.dwStreamType = 1; // substream
        previewInfo.bBlocked = 1;
        previewInfo.hHwnd = m_hHolder;

        m_iPreviewHandle = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(iUserID, previewInfo, null);
        if (m_iPreviewHandle < 0) {
            Log.e(TAG, "NET_DVR_RealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
        }
        boolean bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_OpenSound(m_iPreviewHandle);
    }

    public void stopPreview() {
        HCNetSDK.getInstance().NET_DVR_StopRealPlay(m_iPreviewHandle);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getContext(), SingleMonitorActivity.class);
        intent.putExtra("m_iUserID", m_iUserID);
        intent.putExtra("m_iChan", m_iChan);
        getContext().startActivity(intent);
//        ((Activity)getContext()).finish();
//        UIUtils.showLongToast(view.getContext(), "m_iUserID: " + m_iUserID + ", m_iChan: " + m_iChan);
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        bCreate = true;
        counter.count();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        System.out.println("surfaceDestroyed");
        bCreate = false;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.setMeasuredDimension(m_iWidth - 1, m_iHeight - 1);
    }

    public int getM_iWidth() {
        return m_iWidth;
    }

    public void setM_iWidth(int m_iWidth) {
        this.m_iWidth = m_iWidth;
    }

    public int getM_iHeight() {
        return m_iHeight;
    }

    public void setM_iHeight(int m_iHeight) {
        this.m_iHeight = m_iHeight;
    }
}
