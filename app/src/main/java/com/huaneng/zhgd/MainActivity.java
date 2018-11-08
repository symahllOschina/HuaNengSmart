package com.huaneng.zhgd;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.alibaba.fastjson.JSON;
import com.allenliu.versionchecklib.core.AllenChecker;
import com.allenliu.versionchecklib.core.VersionParams;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.huaneng.zhgd.bean.AppVersion;
import com.huaneng.zhgd.bean.HNLocation;
import com.huaneng.zhgd.bean.JoinMeeting;
import com.huaneng.zhgd.bean.JoinMeetingEvent;
import com.huaneng.zhgd.bean.MyMenu;
import com.huaneng.zhgd.bean.NewNoticeEvent;
import com.huaneng.zhgd.bean.User;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.network.Subscriber;
import com.huaneng.zhgd.notice.NoticeFragment;
import com.huaneng.zhgd.personal.PersonalCenterFragment;
import com.huaneng.zhgd.settings.SettingsFragment;
import com.huaneng.zhgd.site.BuildingSiteFragment;
import com.huaneng.zhgd.utils.DateUtils;
import com.huaneng.zhgd.utils.FileUtils;
import com.huaneng.zhgd.utils.JumpPermissionManagement;
import com.huaneng.zhgd.utils.SQLiteHelper;
import com.huaneng.zhgd.utils.SharedPreferencesUtils;
import com.huaneng.zhgd.utils.SnackbarAction;
import com.huaneng.zhgd.utils.UIUtils;
import com.huaneng.zhgd.utils.UserUtils;
import com.huaneng.zhgd.weather.WeatherFragment;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.Setting;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @ViewInject(R.id.navRadioGroup)
    private RadioGroup navRadioGroup;
    int tabIndex = 0;

    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private List<String> titleList = new ArrayList<String>();
    private int[] btnIds = {R.id.main_tab1, R.id.main_tab2, R.id.main_tab3, R.id.main_tab4, R.id.main_tab5};

    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    BuildingSiteFragment buildingSiteFragment;
    PersonalCenterFragment personalCenterFragment;
    MyMenu myMenu;
    // 定位是否成功
    private Boolean locationSuccess;
    private AlertDialog meetingDialog;
    public String lastRoomId;
    String cacheJsonMenu;
    private Badge badge;
    private int oldMenuVersion;
    private boolean isNeedUpdateMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //HCNetSDKJNAInstance.getInstance().NET_DVR_Logout(0);
        initNavButtons();
        initFragments();
        badge = new QBadgeView(this).bindTarget(navRadioGroup);
        badge.setBadgeGravity(Gravity.CENTER | Gravity.TOP);
        int tabIndex = getIntent().getIntExtra("tabIndex", 0);
        changeHomeTab(tabIndex);
        checkNetwork();
        setJPushAlias();
        notificationAuth();
        cacheJsonMenu = SharedPreferencesUtils.create(ctx).get(Constants.SPR_MENU_JSON);
        if (TextUtils.isEmpty(cacheJsonMenu)) {
            getmenu();
        } else {
            Log.e("菜单列表：",cacheJsonMenu);
            myMenu = JSON.parseObject(cacheJsonMenu, MyMenu.class);
            initMenus();
        }
        getAppVersion();
        getproom();
        loginValid();
        permissionsInit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        int tabIndex = getIntent().getIntExtra("tabIndex", 0);
        changeHomeTab(tabIndex);
        super.onNewIntent(intent);
    }

    @Subscribe
    public void onEvent(NewNoticeEvent event) {
        badge.setBadgeNumber(event.badgeNumber);
    }

    protected boolean isEnableEventBus() {
        return true;
    }

    @Override
    protected void onResume() {
        if (locationSuccess != null && !locationSuccess) {
            baiduLocation();
        }
        super.onResume();
    }

    private void permissionsInit() {
        requestPermissions();
    }

    private void requestPermissions() {

        Rationale mRationale = new Rationale<List<String>>() {
            @Override
            public void showRationale(Context context, List<String> permissions, final RequestExecutor executor) {
                // 这里使用一个Dialog询问用户是否继续授权。
                new AlertDialog.Builder(ctx)
                        .setTitle("提示")
                        .setMessage("需要根据您当前的位置信息来获取天气信息，请同意获取[位置信息]权限.")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
//                                requestPermissions();
                                // 如果用户继续：
                                executor.execute();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                // 如果用户中断：
                                executor.cancel();
                            }
                        }).show();
            }
        };

        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.LOCATION)
                .rationale(mRationale)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Log.d(TAG, "granted");
                        baiduLocation();
                    }
                }).onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Log.d(TAG, "onDenied");
//                        JumpPermissionManagement.permissionDeniedAlert(ctx, "需要根据您当前的位置信息来获取天气信息，请打开[位置信息]权限.");
                        if (AndPermission.hasAlwaysDeniedPermission(ctx, permissions)) {
                            // 这里使用一个Dialog展示没有这些权限应用程序无法继续运行，询问用户是否去设置中授权。
                            AndPermission.with(ctx)
                                    .runtime()
                                    .setting()
                                    .onComeback(new Setting.Action() {
                                        @Override
                                        public void onAction() {
                                            // 用户从设置回来了。
                                            requestPermissions();
                                        }
                                    })
                                    .start();
                        }
                    }
                })
                .start();
    }

    /**
     * 获取当前登陆者要开的会议
     */
    private void getproom() {
        User user = UserUtils.getUser();
        if (!checkNetwork() || user == null || TextUtils.isEmpty(user.markid)) {
            return;
        }
        HTTP.service.getproom()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<JoinMeeting>>(ctx){

                    @Override
                    public void onSuccess(Response<List<JoinMeeting>> response) {
                        List<JoinMeeting> list = response.data;
                        if (list != null && !list.isEmpty()) {
                            final JoinMeeting joinMeeting = list.get(0);
                            String oldTime = SharedPreferencesUtils.create(ctx).get(Constants.SPR_LAST_MEETING_TIME);
                            String newTime = joinMeeting.update_time;
                            if (!DateUtils.isNewest(newTime, oldTime)) {
                                return;
                            }
                            SharedPreferencesUtils.create(ctx).put(Constants.SPR_LAST_MEETING_TIME, newTime);
                            if (DateUtils.isExpired(newTime)) {
                                return;
                            }
                            if (joinMeeting.roomid.equals(lastRoomId)) {
                                return;
                            }
//                            meetingDialog = new AlertDialog.Builder(ctx)
//                                    .setTitle("提示")
//                                    .setMessage(joinMeeting.message)
//                                    .setPositiveButton("进入会议", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.dismiss();
////                                            Intent intent = new Intent(ctx, VideoMeetingActivity.class);
////                                            startActivity(intent);
//
//                                            Intent intent = new Intent(ctx, JoinRoomActivity.class);
//                                            intent.putExtra("roomId", joinMeeting.roomid);
//                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                            startActivity(intent);
//                                        }
//                                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                }).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                        super.onError(e);
                    }

                    @Override
                    public void onFail(String msg) {
                        Log.e(TAG, msg);
                    }
                });
    }

    @Subscribe
    public void onJoinMeetingEvent(final JoinMeetingEvent event) {
        lastRoomId = event.roomid;
        if (meetingDialog != null) {
            meetingDialog.dismiss();
        }
    }

    /**
     * 登录验证(后台)
     */
    public void loginValid() {
        if (!checkNetwork()) {
            return;
        }
        final String name = SharedPreferencesUtils.create(ctx).get(Constants.SPR_NAME);
        final String passwd = SharedPreferencesUtils.create(ctx).get(Constants.SPR_PASSWD);
        final String token = SharedPreferencesUtils.create(ctx).get(Constants.SPR_TOKEN);
        if (TextUtils.isEmpty(token)) {
            loginForce();
            return;
        }
        oldMenuVersion = SharedPreferencesUtils.create(ctx).getInt(Constants.SPR_VERSION, 0);
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(passwd)) {
            return;
        }
        HTTP.service.login(name, passwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>(ctx){

                    @Override
                    public void onSuccess(Response<User> response) {
                        Log.d(TAG, "登录验证成功.");
                        User user = response.data;
                        if (user.version > oldMenuVersion) {
                            isNeedUpdateMenu = true;
                            getmenu();
                        }
                        SharedPreferencesUtils.create(ctx).put(Constants.SPR_TOKEN, response.token);
                        UserUtils.save(user);
                    }

                    @Override
                    public void onWrong(String msg) {
                        loginForce();
                    }
                });
    }

    /**
     * 登录验证失败，强制重新登录
     */
    private void loginForce() {
        UserUtils.clearUser();
        SharedPreferencesUtils.create(ctx).clear();
        new AlertDialog.Builder(ctx)
                .setTitle("提示")
                .setMessage("用户验证失败,请重新登录.")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(ctx, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).setCancelable(false).show();
    }

    private void initMenus() {
        if (buildingSiteMenuFlag) {
            buildingSiteFragment.initMenus(myMenu.menu);
        }
        if (personalCenterMenuFlag) {
            personalCenterFragment.initMenus(myMenu.personcent);
        }
        checkExposureAuth(myMenu.personcent);
    }

    /**
     * 检查【我要曝光】操作的权限
     */
    private void checkExposureAuth(List<com.huaneng.zhgd.bean.Menu> list) {
        SharedPreferencesUtils.create(ctx).putBoolean(Constants.SPR_EXPOSURE_SUPERVISE, false);
        SharedPreferencesUtils.create(ctx).putBoolean(Constants.SPR_EXPOSURE_SAFEMANAGE, false);
        for (com.huaneng.zhgd.bean.Menu menu: list) {
            if ("12".equals(menu.list_type)) {
                // 监督曝光-我要曝光
                SharedPreferencesUtils.create(ctx).putBoolean(Constants.SPR_EXPOSURE_SUPERVISE, true);
            } else if ("13".equals(menu.list_type)) {
                // 安全管理-安全管理展示曝光
                SharedPreferencesUtils.create(ctx).putBoolean(Constants.SPR_EXPOSURE_SAFEMANAGE, true);
            }
        }
    }

    // 防止buildingSiteFragment还未初始化完成就调用其方法出现错误
    boolean buildingSiteMenuFlag = false;
    public void initBuildingSiteMenu() {
        buildingSiteMenuFlag = true;
        if (myMenu != null) {
            buildingSiteFragment.initMenus(myMenu.menu);
        }
    }
    boolean personalCenterMenuFlag = false;
    public void initPersonalCenterMenu() {
        personalCenterMenuFlag = true;
        if (myMenu != null) {
            personalCenterFragment.initMenus(myMenu.personcent);
        }
    }

    private void getmenu() {
        User user = UserUtils.getUser();
        if (user == null) {
            user = SQLiteHelper.create().getOne(User.class);
            if (user != null) {
                UserUtils.save(user);
            } else {
                toast("请重新登录.");
                Intent intent = new Intent(ctx, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        }
        HTTP.service.getmenu(user.markid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MyMenu>(ctx){

                    @Override
                    public void onSuccess(Response<MyMenu> response) {
                        myMenu = response.data;
                        String jsonMenu = JSON.toJSONString(myMenu);
                        if (!TextUtils.isEmpty(jsonMenu) && (isNeedUpdateMenu || !jsonMenu.equals(cacheJsonMenu))) {
                            SharedPreferencesUtils.create(ctx).put(Constants.SPR_MENU_JSON, jsonMenu);
                            FileUtils.clearGlideCacheDiskSelf(ctx);
                            if (oldMenuVersion > 0) {
                                SharedPreferencesUtils.create(ctx).putInt(Constants.SPR_VERSION, oldMenuVersion);
                            }
                            initMenus();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        toast("获取菜单失败.");
                    }
                });
    }

    private void baiduLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location){
            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息
            if (TextUtils.isEmpty(city)) {
                snackError("定位失败，无法获取天气信息.", new SnackbarAction("设置", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        JumpPermissionManagement.goToSettings(ctx);
                    }
                }));

                if (locationSuccess == null) {
                    // 确保只弹窗一次
                    JumpPermissionManagement.permissionDeniedAlert(ctx, "需要根据位置来获取天气信息，请打开[位置信息]权限.");
                }
                locationSuccess = false;
                mLocationClient.stop();
                return;
            }
            locationSuccess = true;
            HNLocation loc = new HNLocation(city, longitude, latitude);
            SharedPreferencesUtils.create(ctx).put(Constants.SPR_LOC_CITY, loc.city);
            SharedPreferencesUtils.create(ctx).put(Constants.SPR_LOC_LONGLAT, loc.longLat);
            EventBus.getDefault().post(loc);
            mLocationClient.stop();
        }

        @Override
        public void onLocDiagnosticMessage(int locType, int diagnosticType, String diagnosticMessage) {
            super.onLocDiagnosticMessage(locType, diagnosticType, diagnosticMessage);
        }
    }

    /**
     * 通知栏权限判断
     */
    private void notificationAuth() {
        boolean isNotiAuth = NotificationManagerCompat.from(this).areNotificationsEnabled();
        if (!isNotiAuth) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("开启通知栏权限，以便接收重要通知消息？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            JumpPermissionManagement.goToSettings(ctx);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    /**
     * 设置极光推送别名
     */
    private void setJPushAlias() {
        JPushInterface.resumePush(getApplicationContext());
        User user = UserUtils.getUser();
        if (user != null) {
            JPushInterface.setAlias(getApplicationContext(), 1, user.markid);
            Set<String> set = new HashSet<String>();
            set.add(user.markid);
            JPushInterface.setTags(getApplicationContext(), 1, set);
        }
    }

    private void initNavButtons() {
        navRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.main_tab1:
                        tabIndex = 0;
                        break;
                    case R.id.main_tab2:
                        tabIndex = 1;
                        break;
                    case R.id.main_tab3:
                        tabIndex = 2;
                        break;
                    case R.id.main_tab4:
                        tabIndex = 3;
                        break;
                    case R.id.main_tab5:
                        tabIndex = 4;
                        break;
                }
                changeHomeTab(tabIndex);
            }
        });
    }

    private void initFragments() {
        buildingSiteFragment = new BuildingSiteFragment();
        personalCenterFragment = new PersonalCenterFragment();
        fragmentList.add(buildingSiteFragment);
        fragmentList.add(new WeatherFragment());
        fragmentList.add(new NoticeFragment());
        fragmentList.add(personalCenterFragment);
        fragmentList.add(new SettingsFragment());
        for (Fragment fragment : fragmentList) {
            addFragment(fragment);
        }
        titleList.add(getString(R.string.main_tab1));
        titleList.add(getString(R.string.main_tab2));
        titleList.add(getString(R.string.main_tab3));
        titleList.add(getString(R.string.main_tab4));
        titleList.add(getString(R.string.main_tab5));
    }

    public void changeHomeTab(int index) {
//        setTitle(titleList.get(index));
        for (int i = 0; i < fragmentList.size(); i++) {
            if (i == index) {
                showFragment(fragmentList.get(i));
            } else {
                hideFragment(fragmentList.get(i));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                info();
//                UIUtils.goToAppSetting(ctx);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void info() {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("title", "关于");
        intent.putExtra("url", "file:///android_asset/about.html");
        startActivity(intent);
    }

    private void addFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, fragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    private void hideFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().hide(fragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().show(fragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }


    private void getAppVersion() {
        HTTP.service.getAppVersion("1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AppVersion>(ctx){

                    @Override
                    public void onSuccess(Response<AppVersion> response) {
                        AppVersion appVersion = response.data;
                        Log.e("更新日志：",appVersion.comments);
                        if (appVersion != null && UIUtils.isNeedUpdate(ctx, appVersion)) {
                            SharedPreferencesUtils.create(ctx).putBoolean(Constants.SPR_UPDATE, true);
                            SharedPreferencesUtils.create(ctx).put(Constants.SPR_APK_URL, appVersion.path);
                            VersionParams.Builder builder = new VersionParams.Builder()
                                    .setOnlyDownload(true)
                                    .setDownloadUrl(appVersion.path)
                                    .setDownloadAPKPath(Constants.APK_DOWNLOAD_PATH)
                                    .setTitle("提示")
                                    .setUpdateMsg("检测到新版本，现在更新？"+"\n"+"\n"+appVersion.comments
                                    );
                            AllenChecker.startVersionCheck(ctx, builder.build());
                        } else {
                            SharedPreferencesUtils.create(ctx).putBoolean(Constants.SPR_UPDATE, false);
                        }
                    }
                });
    }

    protected boolean isShowBacking() {
        return false;
    }

    private long mBackTime = -1;
    @Override
    public void onBackPressed() {
        long nowTime = System.currentTimeMillis();
        long diff = nowTime - mBackTime;
        if (diff >= 2000) {
            mBackTime = nowTime;
            toast("再按一次退出.");
        } else {
            app.exit();
        }
    }
}
