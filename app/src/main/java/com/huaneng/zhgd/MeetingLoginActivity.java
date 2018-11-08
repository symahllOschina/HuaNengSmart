package com.huaneng.zhgd;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import com.huaneng.zhgd.bean.User;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.network.Subscriber;
import com.huaneng.zhgd.utils.SharedPreferencesUtils;
import com.huaneng.zhgd.utils.UIUtils;
import com.huaneng.zhgd.utils.UserUtils;
import com.inpor.fastmeetingcloud.ui.StartTheMiddleTierActivity;
import com.inpor.fastmeetingcloud.util.Constant;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 视频会议登录
 * 进入会议室分为两种情况，账户密码登陆，会议号登陆。
 * 视频会议的用户名默认为markid
 *
 * 【接口】邀请参会人：roomid 值服务器返回的；服务器同时返回用户的密码(用户名默认是 markid)；
 *
 * 【接口】获取当前会议室roomid 的参会情况：
 *
 * 【接口】获取要参加的会议：message/getproom
 */
@ContentView(R.layout.activity_meeting_login)
public class MeetingLoginActivity extends BaseActivity {

    public static final String SERVER = "http://113.200.203.89:8088/fmapi/webservice/jaxws?wsdl";

    @ViewInject(R.id.nameTv)
    private EditText nameTv;
    @ViewInject(R.id.passwdTv)
    private EditText passwdTv;
    @ViewInject(R.id.roomIdTv)
    private EditText roomIdTv;
    @ViewInject(R.id.passwordVisibleImg)
    private ImageView passwordVisibleImg;
    boolean passwordVisible = false;

    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userName = UserUtils.getUser().markid;
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

    /**
     * 登陆
     */
    public void login(View view) {
        if (!checkNetwork()) {
            return;
        }
    }

    /**
     * 账户密码登陆
     */
    public void login() {
        ComponentName componentName= new ComponentName("com.tangzheng.hsttest", "com.inpor.fastmeetingcloud.ui.StartTheMiddleTierActivity");
        Intent intent = new Intent();
        intent.setAction(Constant.INTENT_APP_ACTION);
        intent.putExtra(StartTheMiddleTierActivity.ThirdLoginConstant.BUNDLE_USERNAME, nameTv.getText().toString().trim());
        intent.putExtra(StartTheMiddleTierActivity.ThirdLoginConstant.BUNDLE_PASSWORD, passwdTv.getText().toString().trim());
        intent.putExtra(StartTheMiddleTierActivity.ThirdLoginConstant.BUNDLE_ROOMID,Long.valueOf(roomIdTv.getText().toString().trim()));
        intent.putExtra(StartTheMiddleTierActivity.ThirdLoginConstant.BUNDLE_SERVER_ADDRESS, SERVER);
        intent.putExtra(StartTheMiddleTierActivity.ThirdLoginConstant.BUNDLE_SERVER_PORT, "1089");
        intent.setComponent(componentName);
        startActivity(intent);
    }

    /**
     * 会议号登陆
     */
    public void join(){
        ComponentName componentName= new ComponentName("com.tangzheng.hsttest", "com.inpor.fastmeetingcloud.ui.StartTheMiddleTierActivity");
        Intent intent = new Intent();
        intent.setAction(Constant.INTENT_ACTION_JOIN_MEETING);
        intent.putExtra(StartTheMiddleTierActivity.ThirdLoginConstant.BUNDLE_ROOMID,Long.valueOf(roomIdTv.getText().toString().trim()));
//        intent.putExtra(StartTheMiddleTierActivity.ThirdLoginConstant.BUNDLE_NICKNAME, edit_nickname.getText().toString().trim());
//        intent.putExtra(StartTheMiddleTierActivity.ThirdLoginConstant.BUNDLE_ROOMPASSWORD, edit_roomPwd.getText().toString().trim());
        intent.putExtra(StartTheMiddleTierActivity.ThirdLoginConstant.BUNDLE_SERVER_ADDRESS, SERVER);
        intent.putExtra(StartTheMiddleTierActivity.ThirdLoginConstant.BUNDLE_SERVER_PORT, "1089");
        intent.setComponent(componentName);
        startActivity(intent);
    }
}
