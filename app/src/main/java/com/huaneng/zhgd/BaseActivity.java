package com.huaneng.zhgd;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.huaneng.zhgd.utils.NetworkUtils;
import com.huaneng.zhgd.utils.SharedPreferencesUtils;
import com.huaneng.zhgd.utils.SnackbarAction;
import com.huaneng.zhgd.utils.UIUtils;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TH on 2017/11/2.
 */

public class BaseActivity extends RxAppCompatActivity {

    protected final String TAG = getClass().getSimpleName();
    protected App app;
    @ViewInject(R.id.toolbar)
    protected Toolbar mToolbar;
    @ViewInject(R.id.toolbar_title)
    private TextView mToolbarTitle;
    private ProgressDialog waitingDialog;
    protected BaseActivity ctx;
    protected boolean isVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (App) getApplication();
        ctx = this;
        if (isPortrait()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        x.view().inject(this);
        if (isEnableEventBus()) {
            EventBus.getDefault().register(this);
        }
        if (mToolbar != null) {
            //将Toolbar显示到界面
            setSupportActionBar(mToolbar);
            if (isShowBacking()) {
                ActionBar actionBar = getSupportActionBar();
                actionBar.setDisplayHomeAsUpEnabled(true);
                mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
            }
        }
        if (mToolbarTitle != null) {
            //getTitle()的值是activity的android:lable属性值
            mToolbarTitle.setText(getTitle());
            //设置默认的标题不显示
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Long>bindToLifecycle())
                .subscribe();
    }

    protected boolean isPortrait() {
        return true;
    }

    @Override
    public void setTitle(int resId) {
        setTitle(getString(resId));
    }

    @Override
    public void setTitle(CharSequence title) {
        if (mToolbar != null && mToolbarTitle != null) {
            mToolbarTitle.setText(title);
        } else {
            super.setTitle(title);
        }
    }

    public <T> T getSerializableExtra(String key) {
        return (T)getIntent().getSerializableExtra(key);
    }

    public void showText(TextView textView, String prefix, Object value) {
        UIUtils.showText(textView, prefix, value);
    }

    public void showText(TextView textView, Object value) {
        UIUtils.showText(textView, value);
    }

    public static void showNum(TextView textView, String prefix, Object value) {
        UIUtils.showNum(textView, prefix, value);
    }

    public static void showNum(TextView textView, Object value) {
        UIUtils.showNum(textView, value);
    }

    /**
     * 是否显示返回按钮
     */
    protected boolean isShowBacking() {
        return true;
    }

    protected boolean isEnableEventBus() {
        return false;
    }

    public void toast(String msg) {
        app.toast(msg);
    }

    public void toast(String msg, String defaultStr) {
        if (TextUtils.isEmpty(msg)) {
            msg = defaultStr;
        }
        toast(msg);
    }

    protected void toast_debug(String msg) {
        if (BuildConfig.DEBUG) {
            toast(msg);
        }
    }

    protected void activity(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    public void showWaitDialog() {
        showWaitDialog("正在加载...");
    }

    public void showWaitDialog(String msg) {
        if (this.isFinishing() || this.isDestroyed()) {
            return;
        }
        if (waitingDialog == null) {
            waitingDialog = new ProgressDialog(this, R.style.Translucent_Theme);
//        waitingDialog.setTitle("我是一个等待Dialog");
            waitingDialog.setMessage(msg);
            waitingDialog.setIndeterminate(true);
            //waitingDialog.setCancelable(false);
            waitingDialog.setCanceledOnTouchOutside(false);
        }
        waitingDialog.show();
    }

    public void hideWaitDialog() {
        if (waitingDialog != null && !(this.isFinishing() || this.isDestroyed())) {
            waitingDialog.dismiss();
        }
    }

    public void snackError(String msg) {
        snack(msg, Prompt.ERROR);
    }

    public void snackError(String msg, SnackbarAction action) {
        snack(msg, Prompt.ERROR, action);
    }

    public void snackError(String msg, String defaultMsg) {
        if (TextUtils.isEmpty(msg)) {
            msg = defaultMsg;
        }
        snackError(msg);
    }

    public void snackSuccess(String msg) {
        snack(msg, Prompt.SUCCESS);
    }

    public void snackWarning(String msg) {
        snack(msg, Prompt.WARNING);
    }

    private void snack(String msg, Prompt prompt) {
        snack(msg, prompt, null);
    }
    private void snack(String msg, Prompt prompt, SnackbarAction action) {
        final ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content).getRootView();
        TSnackbar snackbar = TSnackbar.make(viewGroup, msg, TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN).setPromptThemBackground(prompt);
        if (action != null) {
            snackbar.setAction(action.title, action.onClickListener);
        }
        snackbar.show();
    }

    public boolean checkNetwork() {
        if (!NetworkUtils.isAvailable(this)) {
            snackError("网络未连接");
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        isVisible = true;
        super.onResume();
    }

    @Override
    protected void onStop() {
        isVisible = false;
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        hideWaitDialog();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (isEnableEventBus()) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }
}
