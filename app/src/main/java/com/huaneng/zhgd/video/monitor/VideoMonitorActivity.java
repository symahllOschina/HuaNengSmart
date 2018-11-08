/**
 * <p>DemoActivity Class</p>
 * @author zhuzhenlei 2014-7-17
 * @version V1.0  
 * @modificationHistory
 * @modify by user: 
 * @modify by reason:
 */
package com.huaneng.zhgd.video.monitor;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_COMPRESSIONCFG_V30;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_PLAYBACK_INFO;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.PTZCommand;
import com.hikvision.netsdk.PlaybackControlCommand;
import com.hikvision.netsdk.StdDataCallBack;
import com.huaneng.zhgd.BaseActivity;
import com.huaneng.zhgd.Constants;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.Menu;
import com.huaneng.zhgd.utils.SharedPreferencesUtils;
import com.huaneng.zhgd.utils.UIUtils;

import org.MediaPlayer.PlayM4.Player;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频监控
 */
@ContentView(R.layout.activity_video_monitor)
public class VideoMonitorActivity extends BaseActivity implements Callback, ICount {

    public static final String IP = "jkzhgd.xadragon.com";// 111.20.85.18 "113.200.203.98"
    public static final int PORT = 8000;//"8922"

    public VideoMonitorActivity Demo;

    @ViewInject(R.id.btn_Login)
    private Button m_oLoginBtn = null;
    @ViewInject(R.id.btn_Preview)
    private Button m_oPreviewBtn = null;
    @ViewInject(R.id.btn_Playback)
    private Button m_oPlaybackBtn = null;
    @ViewInject(R.id.btn_ParamCfg)
    private Button m_oParamCfgBtn = null;
    @ViewInject(R.id.btn_Capture)
    private Button m_oCaptureBtn = null;
    @ViewInject(R.id.btn_Record)
    private Button m_oRecordBtn = null;
    @ViewInject(R.id.btn_Talk)
    private Button m_oTalkBtn = null;
    @ViewInject(R.id.btn_PTZ)
    private Button m_oPTZBtn = null;
    @ViewInject(R.id.btn_OTHER)
    private Button m_oOtherBtn = null;
    @ViewInject(R.id.EDT_IPAddr)
    private EditText m_oIPAddr = null;
    @ViewInject(R.id.EDT_Port)
    private EditText m_oPort = null;
    @ViewInject(R.id.EDT_User)
    private EditText m_oUser = null;
    @ViewInject(R.id.EDT_Psd)
    private EditText m_oPsd = null;
    @ViewInject(R.id.operateLayout)
    private RelativeLayout operateLayout = null;
    @ViewInject(R.id.Sur_Player)
    private SurfaceView m_osurfaceView = null;

    private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;
    private StdDataCallBack stdDataCallBack = null;

    private int m_iLogID = -1; // return by NET_DVR_Login_v30
    private int m_iPlayID = -1; // return by NET_DVR_RealPlay_V30
    private int m_iPlaybackID = -1; // return by NET_DVR_PlayBackByTime

    private int m_iPort = -1; // play port
    private int m_iStartChan = 0; // start channel no
    private int m_iChanNum = 0; // channel number
    private int MAX_COUNT = 10; // channel number

    private List<PlaySurfaceView> playViews = new ArrayList<PlaySurfaceView>();

    private boolean m_bTalkOn = false;
    private boolean m_bPTZL = false;
    private boolean m_bMultiPlay = false;
    private boolean m_bInsideDecode = true;
    private boolean m_bSaveRealData = false;
    private boolean m_bStopPlayback = false;
    
    private String m_retUrl = "";
    private final static int REQUEST_CODE = 1;
    
    public static String accessToken = "";
	public static String areaDomain = "";
	public static String appkey = ""; // fill in with appkey
	public static String appSecret = ""; // fill in with appSecret

    PopupWindow loginWindow;
    EditText ipEt;
    EditText portEt;
    EditText loginIdEt;
    EditText passwdEt;

    private boolean isReady;


    public static Boolean isOption = false;//监控是否可操作，默认不可操作
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!initeSdk()) {
            this.finish();
            return;
        }
        if (!initViews()) {
            this.finish();
            return;
        }
        m_oIPAddr.setText(IP);
        m_oPort.setText(PORT + "");
        m_oUser.setText("admin");
        m_oPsd.setText("xinlong123");
        showWaitDialog("正在登陆...");

        //取出对象
        initData();
    }

    private void initData(){
        Intent in = getIntent();
        isOption = in.getBooleanExtra("isOption",isOption);

    }

    /**
     * @fn init net sdk
     * @return true - success;false - fail
     */
    private boolean initeSdk() {
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            toast("初始化监控模块失败.");
            Log.e(TAG, "HCNetSDK init is failed!");
            return false;
        }
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/", true);
        return true;
    }

    // GUI init
    private boolean initViews() {
        m_osurfaceView.getHolder().addCallback(this);
        setListeners();

        return true;
    }

    // listen
    private void setListeners() {
        m_oLoginBtn.setOnClickListener(Login_Listener);
        m_oPreviewBtn.setOnClickListener(Preview_Listener);
        m_oPlaybackBtn.setOnClickListener(Playback_Listener);
        m_oParamCfgBtn.setOnClickListener(ParamCfg_Listener);
        m_oCaptureBtn.setOnClickListener(Capture_Listener);
        m_oRecordBtn.setOnClickListener(Record_Listener);
        m_oTalkBtn.setOnClickListener(Talk_Listener);
        m_oPTZBtn.setOnTouchListener(PTZ_Listener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("m_iPort", m_iPort);
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        m_iPort = savedInstanceState.getInt("m_iPort");
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState");
    }

    // ------------- SurfaceView Callback begin ---------------------
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        m_osurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        login();
        Log.i(TAG, "surface is created" + m_iPort);
        if (-1 == m_iPort) {
            return;
        }
        Surface surface = holder.getSurface();
        if (surface.isValid()) {
            if (!Player.getInstance().setVideoWindow(m_iPort, 0, holder)) {
                Log.e(TAG, "Player setVideoWindow failed!");
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

     @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "Player setVideoWindow release!" + m_iPort);
        if (-1 == m_iPort) {
            return;
        }
        if (holder.getSurface().isValid()) {
            if (!Player.getInstance().setVideoWindow(m_iPort, 0, null)) {
                Log.e(TAG, "Player setVideoWindow failed!");
            }
        }
    }

    // ------------- SurfaceView Callback end ---------------------


    private void ChangeSingleSurFace(boolean bSingle) {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        initPlayViews();
        // 单个channel
        if (bSingle) {
            playViews.get(0).setVisibility(View.INVISIBLE);
//            for (int i = 0; i < 4; ++i) {
//                playViews[i].setVisibility(View.INVISIBLE);
//            }
//            playView[0].setParam(metric.widthPixels * 2, metric.heightPixels * 2);
//            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//                    FrameLayout.LayoutParams.WRAP_CONTENT,
//                    FrameLayout.LayoutParams.WRAP_CONTENT);
//            params.bottomMargin = playView[3].getM_iHeight() - (3 / 2)* playView[3].getM_iHeight();
////            params.bottomMargin = 0;
//            params.leftMargin = 0;
//            params.gravity = Gravity.BOTTOM | Gravity.LEFT;
//            playView[0].setLayoutParams(params);
        } else {
            // 多个channel
            for (int i = 0; i < m_iChanNum && i < MAX_COUNT; ++i) {
                playViews.get(i).setVisibility(View.VISIBLE);
//                playView[0].setParam(metric.widthPixels, metric.heightPixels);
//                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//                        FrameLayout.LayoutParams.WRAP_CONTENT,
//                        FrameLayout.LayoutParams.WRAP_CONTENT);
//                params.bottomMargin = playView[0].getM_iHeight() - (0 / 2)* playView[0].getM_iHeight();
//                params.leftMargin = (0 % 2) * playView[0].getM_iWidth();
//                params.gravity = Gravity.TOP | Gravity.LEFT;
//                playView[i].setLayoutParams(params);
            }
        }
    }

    private void initPlayViews() {
        if (m_iChanNum > 0 && playViews.isEmpty()) {
            playViews.clear();
            DisplayMetrics metric = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metric);
            for (int i = 0; i < m_iChanNum && i < MAX_COUNT; i++) {
                PlaySurfaceView playSurfaceView = new PlaySurfaceView(this, this);
                // 设置单个画面的宽高
                playSurfaceView.setParam(metric.widthPixels, metric.heightPixels);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
//                params.bottomMargin = playView[i].getM_iHeight() - (i / 2)* playView[i].getM_iHeight();
                params.topMargin = (i / 2)* playSurfaceView.getM_iHeight() + this.mToolbar.getHeight() + 10;
//                params.topMargin = i * playView[i].getM_iHeight();
                params.leftMargin = (i % 2) * playSurfaceView.getM_iWidth();
//                params.gravity = Gravity.TOP | Gravity.LEFT;
                addContentView(playSurfaceView, params);
//                playSurfaceView.setVisibility(View.INVISIBLE);
                playViews.add(playSurfaceView);
            }
        }
    }

    // 移动及镜头变倍、变焦控制
    private OnTouchListener PTZ_Listener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            try {
                if (m_iLogID < 0) {// 未登录
                    Log.e(TAG, "please login on a device first");
                    return false;
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (m_bPTZL == false) {
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.PAN_LEFT, 0)) {
                            Log.e(TAG,"start PAN_LEFT failed with error code: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
                        } else {
                            Log.i(TAG, "start PAN_LEFT success");
                        }
                    } else {
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.PAN_RIGHT, 0)) {
                            Log.e(TAG, "start PAN_RIGHT failed with error code: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
                        } else {
                            Log.i(TAG, "start PAN_RIGHT success");
                        }
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (m_bPTZL == false) {
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.PAN_LEFT, 1)) {
                            Log.e(TAG, "stop PAN_LEFT failed with error code: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
                        } else {
                            Log.i(TAG, "stop PAN_LEFT success");
                        }
                        m_bPTZL = true;
                        m_oPTZBtn.setText("PTZ(R)");
                    } else {
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.PAN_RIGHT, 1)) {
                            Log.e(TAG, "stop PAN_RIGHT failed with error code: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
                        } else {
                            Log.i(TAG, "stop PAN_RIGHT success");
                        }
                        m_bPTZL = false;
                        m_oPTZBtn.setText("PTZ(L)");
                    }
                }
                return true;
            } catch (Exception err) {
                Log.e(TAG, "error: " + err.toString());
                return false;
            }
        }
    };

    // Test Activity result
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode== REQUEST_CODE) {
            if (resultCode== 1 && data != null) {
            	m_retUrl = data.getStringExtra("Info");
            	Log.e(TAG, "m_retUrl: " + m_retUrl);
            	accessToken = m_retUrl.substring(m_retUrl.indexOf("access_token")+13, m_retUrl.indexOf("access_token")+77);
            	Log.e(TAG, "accessToken: " + accessToken);
            	areaDomain = m_retUrl.substring(m_retUrl.indexOf("areaDomain")+11);
            	Log.e(TAG, "areaDomain: " + areaDomain);
            }
            else {
            	Log.e(TAG, "resultCode!= 1");
            }
            Demo = new VideoMonitorActivity();
        	new Thread(new Runnable() {										//inner class - new thread to get device list
        		@Override
        		public void run()
        		{
                    Demo.get_device_ip();
        		}
        	}).start();
        }
    }

    // Talk listener
    private OnClickListener Talk_Listener = new OnClickListener() {
        public void onClick(View v) {
            try {
                if (m_bTalkOn == false) {
                    if (VoiceTalk.startVoiceTalk(m_iLogID) >= 0) {
                        m_bTalkOn = true;
                        m_oTalkBtn.setText("Stop");
                    }
                } else {
                    if (VoiceTalk.stopVoiceTalk()) {
                        m_bTalkOn = false;
                        m_oTalkBtn.setText("Talk");
                    }
                }
            } catch (Exception err) {
                Log.e(TAG, "error: " + err.toString());
            }
        }
    };

    // record listener
    private OnClickListener Record_Listener = new OnClickListener() {
        public void onClick(View v) {
            if (!m_bSaveRealData) {
                if (!HCNetSDKJNAInstance.getInstance().NET_DVR_SaveRealData_V30(m_iPlayID, 0x2, "/sdcard/test.mp4")) {
                    Log.e(TAG, "NET_DVR_SaveRealData_V30 failed! error: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
                    return;
                } else {
                    Log.e(TAG, "NET_DVR_SaveRealData_V30 succ!");
                }
                m_bSaveRealData = true;
            } else {
                if (!HCNetSDK.getInstance().NET_DVR_StopSaveRealData(m_iPlayID)) {
                    Log.e(TAG, "NET_DVR_StopSaveRealData failed! error: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
                } else {
                    Log.e(TAG, "NET_DVR_StopSaveRealData succ!");
                }
                m_bSaveRealData = false;
            }
        }
    };

    // capture listener
    private OnClickListener Capture_Listener = new OnClickListener() {
        public void onClick(View v) {
            try {
            	if(m_iPlayID < 0){
            		Log.e(TAG, "please start previewOrStop first");
            		return;
            	}else{
//            		HCNetSDKJNAInstance.getInstance().NET_DVR_SetCapturePictureMode(0x1);
            		if(HCNetSDKJNAInstance.getInstance().NET_DVR_CapturePictureBlock(m_iPlayID, "/sdcard/capblock.jpg", 0)){
                    	Log.e(TAG, "NET_DVR_CapturePictureBlock Succ!");
                    }
                    else {
                    	Log.e(TAG, "NET_DVR_CapturePictureBlock fail! Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
                    }
            	}
            } catch (Exception err) {
                Log.e(TAG, "error: " + err.toString());
            }
        }
    };

    private OnClickListener Playback_Listener = new OnClickListener() {

        public void onClick(View v) {
            try {
                if (m_iLogID < 0) {
                    Log.e(TAG, "please login on a device first");
                    return;
                }
                if (m_iPlaybackID < 0) {
                    if (m_iPlayID >= 0) {
                        Log.i(TAG, "Please stop previewOrStop first");
                        return;
                    }
                    ChangeSingleSurFace(true);
                    m_iPlaybackID = HCNetSDK.getInstance().NET_DVR_PlayBackByName(m_iLogID, new String("ch0001_00000000300000000"), null);  // playView[0].getHolder().getSurface()
                    if (m_iPlaybackID >= 0) {
                        NET_DVR_PLAYBACK_INFO struPlaybackInfo = null;
                        if (!HCNetSDK.getInstance().NET_DVR_PlayBackControl_V40(m_iPlaybackID, PlaybackControlCommand.NET_DVR_PLAYSTART, null, 0, struPlaybackInfo)) {
                            Log.e(TAG, "net sdk playback start failed!");
                            return;
                        }
                        Thread thread11 = new Thread() {
                            public void run() {
                                int nProgress = -1;
                                while (true) {
                                    nProgress = HCNetSDK.getInstance().NET_DVR_GetPlayBackPos(m_iPlaybackID);
                                    Log.e(TAG, "NET_DVR_GetPlayBackPos:" + nProgress);
                                    if (nProgress < 0 || nProgress >= 100) {
                                        break;
                                    }
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        };
                        thread11.start();
                    } else {
                        Log.i(TAG, "NET_DVR_PlayBackByName failed, error code: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
                    }
                } else {// 停止回放
                    m_bStopPlayback = true;
                    if (!HCNetSDK.getInstance().NET_DVR_StopPlayBack(m_iPlaybackID)) {
                        Log.e(TAG, "net sdk stop playback failed");
                    } // player stop play
                    m_oPlaybackBtn.setText("Playback");
                    m_iPlaybackID = -1;
                    ChangeSingleSurFace(false);
                }
            } catch (Exception err) {
                Log.e(TAG, "error: " + err.toString());
            }
        }
    };

    // login listener
    private OnClickListener Login_Listener = new OnClickListener() {
        public void onClick(View v) {
//            login();
        }
    };

    class VideoMonitorLoginInfo {
        public String ip;
        public int port;
        public String loginId;
        public String passwd;

        public VideoMonitorLoginInfo get() {
            VideoMonitorLoginInfo info = new VideoMonitorLoginInfo();
            info.ip = SharedPreferencesUtils.create(ctx).get(Constants.SPR_VM_IP, IP);
            info.port = SharedPreferencesUtils.create(ctx).getInt(Constants.SPR_VM_PORT, PORT);
            info.loginId = SharedPreferencesUtils.create(ctx).get(Constants.SPR_VM_LOGINID, "admin");
            info.passwd = SharedPreferencesUtils.create(ctx).get(Constants.SPR_VM_PASSWD, "xinlong123");
            return info;
        }

        public void save(String ip, int port, String loginId, String passwd) {
            SharedPreferencesUtils.create(ctx).put(Constants.SPR_VM_IP, ip);
            SharedPreferencesUtils.create(ctx).putInt(Constants.SPR_VM_PORT, port);
            SharedPreferencesUtils.create(ctx).put(Constants.SPR_VM_LOGINID, loginId);
            SharedPreferencesUtils.create(ctx).put(Constants.SPR_VM_PASSWD, passwd);
        }
    }

    private void login() {
        try {
                if (m_iLogID < 0) {
                    m_iLogID = loginNormalDevice(new VideoMonitorLoginInfo().get());
                    hideWaitDialog();
                    if (m_iLogID < 0) {// 登录失败
                        snackError("登录失败");
                        Log.e(TAG, "This device logins failed!");
                        showLoginWindow();
                        return;
                    } else {
                        Log.e(TAG, "m_iLogID=" + m_iLogID);
                    }
                    // get instance of exception callback and set
                    ExceptionCallBack oexceptionCbf = getExceptiongCbf();
                    if (oexceptionCbf == null) {
                        Log.e(TAG, "ExceptionCallBack object is failed!");
                        return;
                    }
                    if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(oexceptionCbf)) {
                        Log.e(TAG, "NET_DVR_SetExceptionCallBack is failed!");
                        return;
                    }
                    m_oLoginBtn.setText("Logout");
                    Log.i(TAG, "Login sucess ****************************1***************************");
                } else {//退出
                    // whether we have logout
//                    if (logout()) return;
                }
            }catch (Exception err) {
                Log.e(TAG, "error: " + err.toString());
            }
    }

    private boolean logout() {
        if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(m_iLogID)) {
            //退出失败
            Log.e(TAG, " NET_DVR_Logout is failed!");
            return true;
        }
        m_oLoginBtn.setText("Login");
        m_iLogID = -1;
        return false;
    }

    /**
     * @fn loginNormalDevice
     */
    private int loginNormalDevice(VideoMonitorLoginInfo loginInfo) {
        m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        if (null == m_oNetDvrDeviceInfoV30) {
            Log.e(TAG, "HKNetDvrDeviceInfoV30 new is failed!");
            return -1;
        }
        // call NET_DVR_Login_v30 to login on, port 8000 as default
        int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(loginInfo.ip, loginInfo.port, loginInfo.loginId, loginInfo.passwd, m_oNetDvrDeviceInfoV30);
        if (iLogID < 0) {// 登录失败
            Log.e(TAG, "NET_DVR_Login is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return -1;
        }
        if (m_oNetDvrDeviceInfoV30.byChanNum > 0) {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartChan;
            m_iChanNum = m_oNetDvrDeviceInfoV30.byChanNum;
        } else if (m_oNetDvrDeviceInfoV30.byIPChanNum > 0) {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartDChan;
            m_iChanNum = m_oNetDvrDeviceInfoV30.byIPChanNum + m_oNetDvrDeviceInfoV30.byHighDChanNum * 256;
        }
        if (m_iChanNum > 1) {
            ChangeSingleSurFace(false);
        } else {
            ChangeSingleSurFace(true);
        }
        Log.i(TAG, "NET_DVR_Login is Successful!");
        return iLogID;
    }

    /**
     * @brief process exception
     */
    private ExceptionCallBack getExceptiongCbf() {
        ExceptionCallBack oExceptionCbf = new ExceptionCallBack() {
            public void fExceptionCallBack(int iType, int iUserID, int iHandle) {
                System.out.println("recv exception, type:" + iType);
            }
        };
        return oExceptionCbf;
    }

    //-------------------- 预览 begin --------------------------
    // Preview listener
    private OnClickListener Preview_Listener = new OnClickListener() {
        public void onClick(View v) {
            previewOrStop();
        }
    };

    @Override
    public void count() {
        count++;
        if (count == playViews.size() * 2) {
            isReady = true;
            preview();
        }
    }

    int count = 0;
    public void preview() {
        hideWaitDialog();
        if (m_iChanNum > 1) {
            startMultiPreview();
        } else {
            startSinglePreview();
        }
    }

    public void previewOrStop() {
        try {
//            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
//                    .hideSoftInputFromWindow(VideoMonitorActivity.this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            if (m_iLogID < 0) {
                Log.e(TAG, "please login on device first");
                return;
            }
            if (m_iPlaybackID >= 0) {
                Log.i(TAG, "Please stop palyback first");
                return;
            }
            if (m_bInsideDecode)  {
                if (m_iChanNum > 1) {
                    // previewOrStop more than a channel
                    if (!m_bMultiPlay) {
                        startMultiPreview();
                        // startMultiPreview();
                        m_bMultiPlay = true;
                        m_oPreviewBtn.setText("Stop");
//                            operateLayout.setVisibility(View.GONE);
                    } else {
                        stopMultiPreview();
                        m_bMultiPlay = false;
                        m_oPreviewBtn.setText("Preview");
                    }
                } else {
                    // preivew a channel
                    if (m_iPlayID < 0) {
                        startSinglePreview();
                    } else {
                        stopSinglePreview();
                        m_oPreviewBtn.setText("Preview");
                    }
                }
            } else {
                // m_bInsideDecode一直为true，下面的代码不会执行
                if (m_iPlayID < 0) {
                    if (m_iPlaybackID >= 0) {
                        Log.i(TAG, "Please stop palyback first");
                        return;
                    }
                    Log.i(TAG, "m_iStartChan:" + m_iStartChan);
                    NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
                    previewInfo.lChannel = m_iStartChan;
                    previewInfo.dwStreamType = 1; // substream
                    previewInfo.bBlocked = 1;

                    m_iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(m_iLogID, previewInfo, null);
                    if (m_iPlayID < 0) {
                        Log.e(TAG, "NET_DVR_RealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
                        return;
                    }
                    Log.i(TAG, "NetSdk Play sucess ***********************3***************************");
                    m_oPreviewBtn.setText("Stop");
                    if(stdDataCallBack == null) {
                        stdDataCallBack = new StdDataCallBack() {
                            public void fStdDataCallback(int iRealHandle, int iDataType, byte[] pDataBuffer, int iDataSize) {
                                processRealData(1, iDataType, pDataBuffer, iDataSize, Player.STREAM_REALTIME);
                            }
                        };
                    }

                    if(!HCNetSDK.getInstance().NET_DVR_SetStandardDataCallBack(m_iPlayID, stdDataCallBack)){
                        Log.e(TAG, "NET_DVR_SetStandardDataCallBack is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
                    }
                    Log.i(TAG, "NET_DVR_SetStandardDataCallBack sucess ***************************************************");
                }else{
                    stopSinglePreview();
                    m_oPreviewBtn.setText("Preview");
                }
            }
        } catch (Exception err) {
            Log.e(TAG, "error: " + err.toString());
        }
    }

    /**
     * 预览一个channel
     */
    private void startSinglePreview() {
        if (m_iPlaybackID >= 0) {
            Log.i(TAG, "Please stop palyback first");
            return;
        }
        Log.i(TAG, "m_iStartChan:" + m_iStartChan);

        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = m_iStartChan;
        previewInfo.dwStreamType = 0; // substream
        previewInfo.bBlocked = 1;
        previewInfo.hHwnd = playViews.get(0).getHolder();

        m_iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(m_iLogID, previewInfo, null);
        if (m_iPlayID < 0) {
            Log.e(TAG, "NET_DVR_RealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }
        boolean bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_OpenSound(m_iPlayID);
        if(bRet){
            Log.e(TAG, "NET_DVR_OpenSound Succ!");
        }
        Log.i(TAG, "NetSdk Play sucess ***********************3***************************");
        m_oPreviewBtn.setText("Stop");
    }

    private void startMultiPreview() {
        for (int i = 0; i < m_iChanNum && i < MAX_COUNT; i++) {
            playViews.get(i).startPreview(m_iLogID, m_iStartChan + i);
        }
        m_iPlayID = playViews.get(0).m_iPreviewHandle;
    }

    private void stopMultiPreview() {
        int i = 0;
        for (i = 0; i < m_iChanNum && i < MAX_COUNT; i++) {
            playViews.get(i).stopPreview();
        }
        m_iPlayID = -1;
    }

    /**
     * @fn stopSinglePreview
     */
    private void stopSinglePreview() {
        if (m_iPlayID < 0) {
            Log.e(TAG, "m_iPlayID < 0");
            return;
        }
        if(HCNetSDKJNAInstance.getInstance().NET_DVR_CloseSound()){
            Log.e(TAG, "NET_DVR_CloseSound Succ!");
        }
        // net sdk stop previewOrStop
        if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(m_iPlayID)) {
            Log.e(TAG, "StopRealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }
        Log.i(TAG, "NET_DVR_StopRealPlay succ");
        m_iPlayID = -1;
    }

    //-------------------- 预览 end --------------------------

    /**
     * @fn processRealData
     * @author zhuzhenlei
     * @brief process real data
     * @param iPlayViewNo
     *            - player channel [in]
     * @param iDataType
     *            - data type [in]
     * @param pDataBuffer
     *            - data buffer [in]
     * @param iDataSize
     *            - data size [in]
     * @param iStreamMode
     *            - stream mode [in]
     */
    public void processRealData(int iPlayViewNo, int iDataType, byte[] pDataBuffer, int iDataSize, int iStreamMode) {
            if (HCNetSDK.NET_DVR_SYSHEAD == iDataType) {
                if (m_iPort >= 0) {
                    return;
                }
                m_iPort = Player.getInstance().getPort();
                if (m_iPort == -1) {
                    Log.e(TAG, "getPort is failed with: " + Player.getInstance().getLastError(m_iPort));
                    return;
                }
                Log.i(TAG, "getPort succ with: " + m_iPort);
                if (iDataSize > 0) {
                    if (!Player.getInstance().setStreamOpenMode(m_iPort, iStreamMode)) {
                        // set stream mode
                        Log.e(TAG, "setStreamOpenMode failed");
                        return;
                    }
                    if (!Player.getInstance().openStream(m_iPort, pDataBuffer, iDataSize, 2 * 1024 * 1024)) {
                        // open stream
                        Log.e(TAG, "openStream failed");
                        return;
                    }
                    if (!Player.getInstance().play(m_iPort, m_osurfaceView.getHolder())) {
                        Log.e(TAG, "play failed");
                        return;
                    }
                    if (!Player.getInstance().playSound(m_iPort)) {
                        Log.e(TAG, "playSound failed with error code:" + Player.getInstance().getLastError(m_iPort));
                        return;
                    }
                }
            } else {
            	try{
                	FileOutputStream file = new FileOutputStream("/sdcard/StdPlayData.mp4", true);
                    file.write(pDataBuffer, 0, iDataSize);
                    file.close();
                }catch(Exception e) {
                	e.printStackTrace();
                }
            }
    }

    // configuration listener
    private OnClickListener ParamCfg_Listener = new OnClickListener() {
        public void onClick(View v) {
            try {
                paramCfg(m_iLogID);
            } catch (Exception err) {
                Log.e(TAG, "error: " + err.toString());
            }
        }
    };

    /**
     * @fn paramCfg
     */
    private void paramCfg(final int iUserID) {
        // whether have logined on
        if (iUserID < 0) {
            Log.e(TAG, "iUserID < 0");
            return;
        }
        NET_DVR_COMPRESSIONCFG_V30 struCompress = new NET_DVR_COMPRESSIONCFG_V30();
        if (!HCNetSDK.getInstance().NET_DVR_GetDVRConfig(iUserID, HCNetSDK.NET_DVR_GET_COMPRESSCFG_V30, m_iStartChan, struCompress)) {
            Log.e(TAG, "NET_DVR_GET_COMPRESSCFG_V30 failed with error code:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
        } else {
            Log.i(TAG, "NET_DVR_GET_COMPRESSCFG_V30 succ");
        }
        // set substream resolution to cif
        struCompress.struNetPara.byResolution = 1;
        if (!HCNetSDK.getInstance().NET_DVR_SetDVRConfig(iUserID, HCNetSDK.NET_DVR_SET_COMPRESSCFG_V30, m_iStartChan, struCompress)) {
            Log.e(TAG, "NET_DVR_SET_COMPRESSCFG_V30 failed with error code:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
        } else {
            Log.i(TAG, "NET_DVR_SET_COMPRESSCFG_V30 succ");
        }
    }

    /**
     * @brief release net SDK resource
     */
    public void Cleanup() {
        HCNetSDK.getInstance().NET_DVR_Cleanup();
    }      

    public JSONObject get_ddns_Info(String appkey, String appSecret) {
    	try{
    		if(m_retUrl != "") {
    			Log.e(TAG, "m_retUrl != null ");
    			accessToken = m_retUrl.substring(m_retUrl.indexOf("access_token")+13, m_retUrl.indexOf("access_token")+77);	
    			Log.e(TAG, "accessToken: " + accessToken);
				areaDomain = m_retUrl.substring(m_retUrl.indexOf("areaDomain")+11);		
				Log.e(TAG, "areaDomain: " + areaDomain);
    		}else{
    			Demo = new VideoMonitorActivity();
            	new Thread(new Runnable() {										//inner class - new thread to get device list
            		@Override
            		public void run() {
                        Demo.get_access_token(Demo.getKey(), Demo.getSecret());
                        Demo.get_device_ip();
            		}
            	}).start();
            }
    	}catch (Exception e) {
            e.printStackTrace();
        }
    	return null;
    }

    public void get_access_token(String appKey,String appSecret) {
        Log.e(TAG, "get_access_token in" );
        if(appKey == "" || appSecret == "") {
            Log.e(TAG, "appKey or appSecret is null");
            return;
        }
        try {
            String url = "https://open.ezvizlife.com/api/lapp/token/get";
            URL getDeviceUrl = new URL(url);
            /*Set Http Request Header*/
            HttpURLConnection connection = (HttpURLConnection)getDeviceUrl.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.setRequestProperty("Host","isgpopen.ezvizlife.com");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            PrintWriter PostParam = new PrintWriter(connection.getOutputStream());
            String sendParam = "appKey=" + appKey + "&appSecret=" + appSecret;
            PostParam.print(sendParam);
            PostParam.flush();

            BufferedReader inBuf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            JSONObject RetValue = new JSONObject(new String(inBuf.readLine().getBytes(),"utf-8"));
            int RetCode = Integer.parseInt(RetValue.getString("code"));
            if(RetCode != 200) {
                Log.e(TAG, "Get DDNS Info fail! Err code: " + RetCode);
                return;
            }else{
                JSONObject DetailInfo = RetValue.getJSONObject("data");
                accessToken = DetailInfo.getString("accessToken");
                Log.e(TAG, "accessToken: " + accessToken);
                areaDomain = DetailInfo.getString("areaDomain");
                Log.e(TAG, "areaDomain: " + areaDomain);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getKey() {
        return appkey;
    }

    public String getSecret() {
        return appSecret;
    }

    void get_device_ip() {
        String deviceSerial = "711563208" /*m_oIPAddr.getText().toString()*/;  //IP text instead of deviceSerial
        if(deviceSerial == null) {
            Log.e(TAG, "deviceSerial is null ");
            return;
        }
        try {
            String url = areaDomain + "/api/lapp/ddns/get";
            URL getDeviceUrl = new URL(url);
            /*Set Http Request Header*/
            HttpURLConnection connection = (HttpURLConnection)getDeviceUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.setRequestProperty("Host","isgpopen.ezvizlife.com");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            PrintWriter PostParam = new PrintWriter(connection.getOutputStream());
            String sendParam = "accessToken=" + accessToken + "&deviceSerial=" + deviceSerial;
//            String sendParam = "accessToken=" + accessToken + "&domain=" + areaDomain;
            System.out.println(sendParam);
            PostParam.print(sendParam);
            PostParam.flush();

            BufferedReader inBuf = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            JSONObject RetValue = new JSONObject(new String(inBuf.readLine().getBytes(),"utf-8"));
            Log.e(TAG, "RetValue = " + RetValue);
            return;

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        stopMultiPreview();
        logout();
        Cleanup();
        super.onBackPressed();
    }

    /**
     * 自动登录失败后，显示登录窗口
     */
    private void showLoginWindow() {
        if (loginWindow == null) {
            loginWindow = new PopupWindow(this);
            loginWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            loginWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            loginWindow.setBackgroundDrawable(getResources().getDrawable(R.color.white));
            View contentView  = getLayoutInflater().inflate(R.layout.dlg_video_monitor_login, null);
            ipEt = contentView.findViewById(R.id.ipEt);
            portEt = contentView.findViewById(R.id.portEt);
            loginIdEt = contentView.findViewById(R.id.loginIdEt);
            passwdEt = contentView.findViewById(R.id.passwdEt);
            VideoMonitorLoginInfo info = new VideoMonitorLoginInfo().get();
            ipEt.setText(info.ip);
            portEt.setText(info.port + "");
            loginIdEt.setText(info.loginId);
            passwdEt.setText(info.passwd);
            Button loginBtn = contentView.findViewById(R.id.loginBtn);
            Button cancelBtn = contentView.findViewById(R.id.cancelBtn);
            cancelBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    loginWindow.dismiss();
                }
            });
            loginBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    String ip = UIUtils.value(ipEt);
                    int port = 8000;
                    if (!TextUtils.isEmpty(UIUtils.value(portEt))) {
                        port = Integer.valueOf(UIUtils.value(portEt));
                    }
                    String loginId = UIUtils.value(loginIdEt);
                    String passwd = UIUtils.value(passwdEt);
                    new VideoMonitorLoginInfo().save(ip, port, loginId, passwd);
                    loginWindow.dismiss();
                    login();
                }
            });

            loginWindow.setContentView(contentView);
            loginWindow.setOutsideTouchable(false);
            loginWindow.setFocusable(true);
        }
        loginWindow.showAsDropDown(mToolbar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        if (isReady) {
//            stopMultiPreview();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        if (isReady) {
//            preview();
            onBackPressed();
            Intent intent = new Intent(this, VideoMonitorActivity.class);
            startActivity(intent);
        }
        super.onResume();
    }
}