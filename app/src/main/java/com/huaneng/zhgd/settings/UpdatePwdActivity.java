package com.huaneng.zhgd.settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.huaneng.zhgd.BaseActivity;
import com.huaneng.zhgd.Constants;
import com.huaneng.zhgd.MainActivity;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.User;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.network.Subscriber;
import com.huaneng.zhgd.utils.SQLiteHelper;
import com.huaneng.zhgd.utils.SharedPreferencesUtils;
import com.huaneng.zhgd.utils.UIUtils;
import com.huaneng.zhgd.utils.UserUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@ContentView(R.layout.activity_update_pwd)
public class UpdatePwdActivity extends BaseActivity {

    @ViewInject(R.id.oldPwdTv)
    private EditText oldPwdTv;
    @ViewInject(R.id.newPwd1Tv)
    private EditText newPwd1Tv;
    @ViewInject(R.id.newPwd2Tv)
    private EditText newPwd2Tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void login(View view) {
        if (!checkNetwork()) {
            return;
        }
        final String oldPwd = oldPwdTv.getText().toString();
        final String newPwd1 = newPwd1Tv.getText().toString();
        final String newPwd2 = newPwd2Tv.getText().toString();
        if (TextUtils.isEmpty(oldPwd)) {
            toast("原密码不能为空.");
            oldPwdTv.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(newPwd1)) {
            toast("新密码不能为空.");
            newPwd1Tv.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(newPwd2)) {
            toast("确认密码不能为空.");
            newPwd2Tv.requestFocus();
            return;
        }
        if (!newPwd1.equals(newPwd2)) {
            toast("两次输入的新密码不一致.");
            newPwd2Tv.requestFocus();
            return;
        }
        if (newPwd1.length() < 6) {
            toast("密码不能少于6位.");
            newPwd1Tv.requestFocus();
            return;
        }
        showWaitDialog("正在提交...");
        User user = UserUtils.getUser();
        HTTP.service.updatepwd(oldPwd, newPwd1, user.markid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        toast("密码修改成功.");
                        SharedPreferencesUtils.create(ctx).put(Constants.SPR_PASSWD, newPwd1);
                        finish();
                    }

                    @Override
                    public void onFail(String msg) {
                        toast("密码修改失败: " + msg);
                    }
                });
    }
}
