package com.huaneng.zhgd;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import com.huaneng.zhgd.bean.User;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.network.Subscriber;
import com.huaneng.zhgd.utils.SQLiteHelper;
import com.huaneng.zhgd.utils.SharedPreferencesUtils;
import com.huaneng.zhgd.utils.UIUtils;
import com.huaneng.zhgd.utils.UserUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity {

    @ViewInject(R.id.nameTv)
    private EditText nameTv;
    @ViewInject(R.id.passwdTv)
    private EditText passwdTv;
    @ViewInject(R.id.passwordVisibleImg)
    private ImageView passwordVisibleImg;
    boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nameTv.setText(SharedPreferencesUtils.create(ctx).get(Constants.SPR_NAME));
        passwordVisibleImg.setSelected(passwordVisible);
        checkNetwork();
    }

    @Event(R.id.passwordVisibleImg)
    private void passwordVisible(View view) {
        passwordVisible = !passwordVisible;
        passwordVisibleImg.setSelected(passwordVisible);
        int type = EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD;
        if (passwordVisible) {
            type = EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
        }
        passwdTv.setInputType(type);
        passwdTv.setSelection(passwdTv.getText().length());
    }

    public void login(View view) {
        if (!checkNetwork()) {
            return;
        }
        if (UIUtils.validRequired(this, nameTv, passwdTv)) {
            showWaitDialog("正在登录...");
            final String name = UIUtils.value(nameTv);
            final String passwd = UIUtils.value(passwdTv);
            HTTP.service.login(name, passwd)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<User>(ctx){

                        @Override
                        public void onSuccess(Response<User> response) {
//                            toast("登录成功.");
                            User user = response.data;
                            UserUtils.save(user);
                            SharedPreferencesUtils.create(ctx).put(Constants.SPR_NAME, name);
                            SharedPreferencesUtils.create(ctx).put(Constants.SPR_PASSWD, passwd);
                            SharedPreferencesUtils.create(ctx).put(Constants.SPR_TOKEN, response.token);
                            SharedPreferencesUtils.create(ctx).putInt(Constants.SPR_VERSION, user.version);
                            SharedPreferencesUtils.create(ctx).putBoolean(Constants.SPR_LOGIN, true);
                            activity(MainActivity.class);
                            finish();
                        }

                        @Override
                        public void onWrong(String msg) {
                            hideWaitDialog();
                            toast("用户名或密码错误.");
//                            snackError("用户名或密码错误.");
                        }
                    });
        }
    }
}
