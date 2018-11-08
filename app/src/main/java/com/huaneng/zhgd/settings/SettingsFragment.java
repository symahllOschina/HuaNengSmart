package com.huaneng.zhgd.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.allenliu.versionchecklib.core.AllenChecker;
import com.allenliu.versionchecklib.core.VersionParams;
import com.huaneng.zhgd.Constants;
import com.huaneng.zhgd.GlideApp;
import com.huaneng.zhgd.LoginActivity;
import com.huaneng.zhgd.WebViewActivity;
import com.huaneng.zhgd.BaseFragment;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.UpdateUserImgSuccessEvent;
import com.huaneng.zhgd.bean.User;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.network.Subscriber;
import com.huaneng.zhgd.personal.PersonalInfoActivity;
import com.huaneng.zhgd.utils.FileUtils;
import com.huaneng.zhgd.utils.GlideCircleTransform;
import com.huaneng.zhgd.utils.SQLiteHelper;
import com.huaneng.zhgd.utils.SharedPreferencesUtils;
import com.huaneng.zhgd.utils.UIUtils;
import com.huaneng.zhgd.utils.UserUtils;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 设置
 */
@ContentView(R.layout.fragment_settings)
public class SettingsFragment extends BaseFragment {

    @ViewInject(R.id.headImg)
    private ImageView headImg;
    @ViewInject(R.id.nameTv)
    private TextView nameTv;
    @ViewInject(R.id.accountTv)
    private TextView accountTv;
    @ViewInject(R.id.newTv)
    private TextView newTv;
    @ViewInject(R.id.versionTv)
    private TextView versionTv;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        User user = UserUtils.getUser();
        if (user != null) {
            nameTv.setText(user.name);
            accountTv.setText("账号：" + user.userid);
            showHeadImg(user.images);
        }
        versionTv.setText("版本：v" + UIUtils.getVersionName(activity));
        getinfo();
        checkUpdate();
    }

    /**
     * 检查是否有更新
     */
    private void checkUpdate() {
        boolean hasNewVersion = SharedPreferencesUtils.create(activity).getBoolean(Constants.SPR_UPDATE);
        if (hasNewVersion) {
            newTv.setVisibility(View.VISIBLE);
        }
    }

    @Event(value = R.id.updatePwdTv)
    private void updatePwd(View view) {
        Intent intent = new Intent(activity, UpdatePwdActivity.class);
        startActivity(intent);
    }

    @Event(value = {R.id.updateTv, R.id.updateLy})
    private void update(View view) {
        boolean hasNewVersion = SharedPreferencesUtils.create(activity).getBoolean(Constants.SPR_UPDATE);
        if (hasNewVersion) {
            String url = SharedPreferencesUtils.create(activity).get(Constants.SPR_APK_URL);
            VersionParams.Builder builder = new VersionParams.Builder()
                    .setOnlyDownload(true)
                    .setDownloadUrl(url)
                    .setDownloadAPKPath(Constants.APK_DOWNLOAD_PATH)
                    .setTitle("提示")
                    .setUpdateMsg("检测到新版本，现在更新？");
            AllenChecker.startVersionCheck(activity, builder.build());
        } else {
            activity.toast("已是最新版本.");
        }
    }

    @Event(value = R.id.personalInfo)
    private void personalInfo(View view) {
        activity(PersonalInfoActivity.class);
    }

    @Event(value = R.id.aboutTv)
    private void about(View view) {
        Intent intent = new Intent(getContext(), WebViewActivity.class);
        intent.putExtra("title", "关于");
        intent.putExtra("url", "file:///android_asset/about.html");
        startActivity(intent);
    }

    @Event(value = R.id.quitTv)
    private void quit(View view) {
        new AlertDialog.Builder(activity)
                .setTitle("提示")
                .setMessage("确认退出吗？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        FileUtils.clearGlideCacheDiskSelf(activity);
                        JPushInterface.setAlias(activity.getApplicationContext(), 1, "");
                        Set<String> set = new HashSet<String>();
                        JPushInterface.setTags(activity.getApplicationContext(), 1, set);
                        JPushInterface.stopPush(activity.getApplicationContext());

                        SharedPreferencesUtils.create(activity).clear();
                        activity.finish();
                        activity(LoginActivity.class);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    protected boolean isEnableEventBus() {
        return true;
    }

    @Subscribe
    public void onEvent(UpdateUserImgSuccessEvent event) {
        if (event.user != null) {
            showHeadImg(event.user.images);
        }
    }

    public void showHeadImg(String url) {
        if (!TextUtils.isEmpty(url)) {
            GlideApp.with(activity)
                    .load(UIUtils.decorateUrl(url))
                    .centerCrop()
                    .placeholder(R.drawable.ic_head)
                    .transform(new GlideCircleTransform(activity))
                    .into(headImg);
        }
    }

    public void getinfo() {
        HTTP.service.getinfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>(activity){

                    @Override
                    public void onSuccess(Response<User> response) {
                        User user = response.data;
                        UserUtils.save(user);
                        showHeadImg(user.images);
                    }

                    @Override
                    public void onWrong(String msg) {
                        Logger.e("getinfo()出错：" + msg);
                    }
                });
    }
}
