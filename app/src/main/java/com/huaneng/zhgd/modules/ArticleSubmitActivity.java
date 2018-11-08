package com.huaneng.zhgd.modules;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.huaneng.zhgd.BaseActivity;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.UploadImagesResult;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.network.Subscriber;
import com.huaneng.zhgd.utils.MapParam;
import com.huaneng.zhgd.utils.NineGridlayout;
import com.huaneng.zhgd.utils.UIUtils;
import com.orhanobut.logger.Logger;

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

/**
 * 文章上传-我要曝光
 */
@ContentView(R.layout.activity_pionners_works_publish)
public class ArticleSubmitActivity extends BaseActivity {

    @ViewInject(R.id.titleEt)
    private EditText titleEt;
    @ViewInject(R.id.descriptionEt)
    private EditText descriptionEt;
    @ViewInject(R.id.nineGridlayout)
    private NineGridlayout nineGridlayout;

    private ArrayList<String> images;

    private boolean isSubmiting;

    private int type;
    private String subTitle;
    private String norm;
    private String major;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getIntent().getIntExtra("type", -1);
        subTitle = getIntent().getStringExtra("subTitle");
        setTitle(subTitle);
        norm = getIntent().getStringExtra("norm");
        major = getIntent().getStringExtra("major");
        nineGridlayout.setMaxCount(1);
        initAddImgButton();
    }

    // 添加图片的按钮
    private void initAddImgButton() {
        nineGridlayout.init(this);
        ArrayList<String> list = new ArrayList<>();
        list.add("");
        nineGridlayout.setImagesData(list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_submit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_submit:
                submit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void submit() {
        uploadFiles();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                images = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                if (images == null || images.isEmpty()) {
                    initAddImgButton();
                } else {
                    nineGridlayout.setImagesData(images);
                }
            } else {
                images = null;
                initAddImgButton();
            }
        }
    }

    public void save(String image) {
        if (!checkNetwork() || isSubmiting) {
            return;
        }
        showWaitDialog("正在提交...");
        isSubmiting = true;
        HTTP.service.post("generalinfo/save", MapParam.me().p("title", titleEt.getText().toString())
                .p("subtitle", subTitle)
                // type 必选  37-安全管理展示   68-服务监督曝光  69-安全监督曝光
                .p("type", type)
                .p("norm", norm)
                .p("major", major)
                .p("content", descriptionEt.getText().toString())
                .p("image", image).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        isSubmiting = false;
                        toast("提交成功.");
                        finish();
                    }

                    @Override
                    public void onWrong(String msg) {
                        isSubmiting = false;
                        toast(msg, "提交失败.");
                    }
                });
    }

    public void uploadFiles() {
        if (!ctx.checkNetwork() || !UIUtils.validRequired(this, titleEt, descriptionEt)) {
            return;
        }
        if (images != null && !images.isEmpty()) {
            Map<String, RequestBody> map = new HashMap<>();

            for (int i = 0; i < images.size(); i++) {
                String path = images.get(i);
                if (!TextUtils.isEmpty(path)) {
                    File file = new File(path);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    map.put("file\";filename=\"" + file.getName(), requestBody);
                }
            }
            showWaitDialog("正在上传图片...");
            HTTP.service.saveImages(map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber(ctx){

                        @Override
                        public void onSuccess(Response response) {
                            String image = response.data.toString();
                            if (!TextUtils.isEmpty(image)) {
                                save(image);
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
        } else {
            toast("请选择图片.");
        }
    }
}
