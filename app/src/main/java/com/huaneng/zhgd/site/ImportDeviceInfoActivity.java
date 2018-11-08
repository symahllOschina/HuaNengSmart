package com.huaneng.zhgd.site;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaneng.zhgd.BaseActivity;
import com.huaneng.zhgd.GlideApp;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.WebViewActivity;
import com.huaneng.zhgd.bean.Article;
import com.huaneng.zhgd.bean.ImportDeviceInfo;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.network.Subscriber;
import com.huaneng.zhgd.utils.MyWebViewClient;
import com.huaneng.zhgd.utils.UIUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Collections;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.iwf.photopicker.PhotoPreview;

/**
 * 重要甲供设备材料信息
 */
@ContentView(R.layout.activity_import_device_info)
public class ImportDeviceInfoActivity extends BaseActivity {

    @ViewInject(R.id.webView)
    private WebView webView;

    @ViewInject(R.id.titleTv)
    TextView titleTv;
    @ViewInject(R.id.subTitleTv)
    TextView subTitleTv;
    @ViewInject(R.id.timeTv)
    TextView timeTv;
//    @ViewInject(R.id.iconIv)
//    ImageView iconIv;

    ImportDeviceInfo entity;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getIntent().getStringExtra("title"));
        entity = getSerializableExtra("entity");
        url = getIntent().getStringExtra("url");
        UIUtils.showText(titleTv, entity.title);
        UIUtils.showText(subTitleTv, entity.subtitle);
        UIUtils.showText(timeTv, entity.getTime());
        if (TextUtils.isEmpty(entity.create_time)) {
            timeTv.setVisibility(View.GONE);
        } else {
            timeTv.setVisibility(View.VISIBLE);
        }
//        if (entity.hasImage()) {
//            iconIv.setVisibility(View.VISIBLE);
//            GlideApp.with(this)
//                    .load(entity.getImageUrl())
//                    .placeholder(R.drawable.img_default)
//                    .into(iconIv);
//        }
        initView();
        getDetail();
    }

    public void getDetail() {
        if (!checkNetwork()) {
            return;
        }
        showWaitDialog();
        HTTP.service.getImportDeviceInfo(url, entity.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ImportDeviceInfo>(ctx){

                    @Override
                    public void onSuccess(Response<ImportDeviceInfo> response) {
                        ImportDeviceInfo data = response.data;
                        if (data != null && !TextUtils.isEmpty(data.content)) {
                            String html = decorate(data.content);
                            webView.loadDataWithBaseURL("about:blank",html, "text/html", "utf-8",null);
                        }
                    }
                });
    }

    public void initView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);//关键点
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true); // 设置支持javascript脚本
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true); // 允许访问文件
        webSettings.setBuiltInZoomControls(false); // 设置显示缩放按钮
        webSettings.setSupportZoom(false); // 支持缩放
        webView.addJavascriptInterface(new JavascriptInterface(this), "imagelistener");
        webView.setWebViewClient(new MyWebViewClient(this));
    }

    public static String decorate(String htmlContent){
        Document doc_Dis = Jsoup.parse(htmlContent);
        Elements ele_Img = doc_Dis.getElementsByTag("img");
        Elements viewport = doc_Dis.getElementsByAttributeValue("name", "viewport");

        if (viewport != null && !viewport.isEmpty()) {
            viewport.attr("content", "width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no");
        } else {
            Element element = new Element("meta");
            element.attr("name", "viewport");
            element.attr("content", "width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no");
            doc_Dis.head().appendChild(element);
        }

        if (ele_Img.size() != 0){
            for (Element e_Img : ele_Img) {
                e_Img.parent().removeAttr("style");
//                e_Img.attr("style", "width:100%");
            }
        }
        String newHtmlContent=doc_Dis.toString();
        return newHtmlContent;
    }

    // js通信接口
    public class JavascriptInterface {

        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

        @android.webkit.JavascriptInterface
        public void openImage(String object, int position) {
            if (object == null) {
                return;
            }
            String[] imageArray = object.toLowerCase().split(",");
            if (imageArray.length < 1) {
                return;
            }
            ArrayList<String> list = new ArrayList<>();
            Collections.addAll(list, imageArray);
            PhotoPreview.builder()
                    .setPhotos(list)
                    .setCurrentItem(position)
                    .setShowDeleteButton(false)
                    .start(ImportDeviceInfoActivity.this);
        }
    }
}
