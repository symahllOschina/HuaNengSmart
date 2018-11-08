package com.huaneng.zhgd.personal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaneng.zhgd.Constants;
import com.huaneng.zhgd.MainActivity;
import com.huaneng.zhgd.bean.UpdateUserImgSuccessEvent;
import com.huaneng.zhgd.bean.UploadImagesResult;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.network.Subscriber;
import com.huaneng.zhgd.utils.SharedPreferencesUtils;
import com.huaneng.zhgd.utils.UIUtils;
import com.huaneng.zhgd.BaseActivity;
import com.huaneng.zhgd.GlideApp;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.User;
import com.huaneng.zhgd.utils.GlideCircleTransform;
import com.huaneng.zhgd.utils.SQLiteHelper;
import com.huaneng.zhgd.utils.UserUtils;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.iwf.photopicker.PhotoPicker;
import okhttp3.MediaType;
import okhttp3.RequestBody;

@ContentView(R.layout.activity_personal_info)
public class PersonalInfoActivity extends BaseActivity {

    @ViewInject(R.id.headImg)
    private ImageView headImg;
    @ViewInject(R.id.accountTv)
    private TextView accountTv;
    @ViewInject(R.id.nameTv)
    private TextView nameTv;
    @ViewInject(R.id.emailTv)
    private TextView emailTv;
    @ViewInject(R.id.departmentTv)
    private TextView departmentTv;
    @ViewInject(R.id.companyTv)
    private TextView companyTv;
    @ViewInject(R.id.mobileTv)
    private TextView mobileTv;
    @ViewInject(R.id.wechatTv)
    private TextView wechatTv;
    @ViewInject(R.id.jobTv)
    private TextView jobTv;

    @ViewInject(R.id.graduateTv)
    private TextView graduateTv;
    @ViewInject(R.id.majorTv)
    private TextView majorTv;
    @ViewInject(R.id.typeworkTv)
    private TextView typeworkTv;
    @ViewInject(R.id.certificateNoTv)
    private TextView certificateNoTv;
    @ViewInject(R.id.bloodTypeTv)
    private TextView bloodTypeTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User user = UserUtils.getUser();
        initUser(user);
    }

    private void initUser(User user) {
        if (user != null) {
            UIUtils.showText(accountTv, user.userid);
            UIUtils.showText(nameTv, user.name);
            UIUtils.showText(emailTv, user.email);
            UIUtils.showText(companyTv, user.company);
            UIUtils.showText(departmentTv, user.department);
            UIUtils.showText(jobTv, user.job);
            UIUtils.showText(mobileTv, user.mobile);
            UIUtils.showText(wechatTv, user.wechat);

            UIUtils.showText(graduateTv, user.graduate);
            UIUtils.showText(majorTv, user.major);
            UIUtils.showText(typeworkTv, user.typework);
            UIUtils.showText(certificateNoTv, user.certificate_no);
            UIUtils.showText(bloodTypeTv, user.blood_type);

            GlideApp.with(this)
                    .asBitmap()
                    .load(UIUtils.decorateUrl(user.images))
                    .centerCrop()
                    .placeholder(R.drawable.ic_head)
                    .transform(new GlideCircleTransform(this))
                    .into(headImg);
        }
    }

    /**
     * 更新头像
     */
    public void updateHeadImg(View v) {
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(false)
                .start(this, PhotoPicker.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                String image = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS).get(0);
                uploadFiles(image);
            }
        }
    }

    public void uploadFiles(String image) {
        if (!ctx.checkNetwork() || TextUtils.isEmpty(image)) {
            return;
        }
        Map<String, RequestBody> map = new HashMap<>();
        File file = new File(image);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        map.put("file\";filename=\"" + file.getName(), requestBody);
        showWaitDialog("正在上传图片...");
        HTTP.service.saveImages(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        String image = response.data.toString();
                        if (!TextUtils.isEmpty(image)) {
                            updateimage(image);
                        } else {
                            toast("图片上传失败");
                        }
                    }

                    @Override
                    public void onWrong(String msg) {
                        Logger.e(msg);
                        toast(msg);
                    }
                });
    }

    /**
     * 更改头像
     */
    public void updateimage(String image) {
        showWaitDialog("正在更新头像...");
        HTTP.service.updateimage(image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        toast("头像更新成功");
                        getinfo();
                    }

                    @Override
                    public void onWrong(String msg) {
                        Logger.e(msg);
                        toast(msg);
                    }
                });
    }

    public void getinfo() {
        HTTP.service.getinfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>(ctx){

                    @Override
                    public void onSuccess(Response<User> response) {
                        User user = response.data;
                        EventBus.getDefault().post(new UpdateUserImgSuccessEvent(user));
                        UserUtils.save(user);
                        initUser(user);
                    }

                    @Override
                    public void onWrong(String msg) {
                        Logger.e("getinfo()出错：" + msg);
                    }
                });
    }
}
