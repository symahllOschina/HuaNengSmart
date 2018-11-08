package com.huaneng.zhgd;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.huaneng.zhgd.utils.MyWebViewClient;
import com.huaneng.zhgd.utils.WebViewUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Collections;

import me.iwf.photopicker.PhotoPreview;

@ContentView(R.layout.activity_webview)
public class WebViewActivity extends BaseActivity {

    @ViewInject(R.id.webView)
    private WebView webView;

    @ViewInject(R.id.subTitleTv)
    TextView subTitleTv;
    @ViewInject(R.id.timeTv)
    TextView timeTv;

    public static String bigData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getIntent().getStringExtra("title"));
        boolean isBigData = getIntent().getBooleanExtra("isBigData", false);
        if (isBigData) {
            WebViewUtils.me(this, webView).html(bigData);
            return;
        }
        String subtitle = getIntent().getStringExtra("subtitle");
        String time = getIntent().getStringExtra("time");
//        if (!TextUtils.isEmpty(subtitle)) {
//            subTitleTv.setVisibility(View.VISIBLE);
//            subTitleTv.setText(subtitle);
//        }
        if (!TextUtils.isEmpty(time)) {
            timeTv.setVisibility(View.VISIBLE);
            timeTv.setText("发布时间：" + time);
        }

        String url = getIntent().getStringExtra("url");
        if (!TextUtils.isEmpty(url)) {
            WebViewUtils.me(this, webView).url(url);
        } else {
            String html = getIntent().getStringExtra("html");
            if (!TextUtils.isEmpty(html)) {
                WebViewUtils.me(this, webView).html(html);
            }
        }
    }

    @Override
    protected void onDestroy() {
        bigData = null;
        super.onDestroy();
    }
}
