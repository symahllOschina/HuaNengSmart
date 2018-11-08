package com.huaneng.zhgd.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.huaneng.zhgd.BaseActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;

import me.iwf.photopicker.PhotoPreview;

/**
 * Created by TH on 2018/2/1.
 */

public class WebViewUtils {

    private BaseActivity activity;
    private WebView webView;
//    private DownloadUtils downloadUtils;

    public static WebViewUtils me(BaseActivity activity, WebView webView) {
        WebViewUtils webViewUtils = new WebViewUtils();
        webViewUtils.activity = activity;
        webViewUtils.webView = webView;
//        webViewUtils.downloadUtils = new DownloadUtils(activity);
        webViewUtils.initView();
        return webViewUtils;
    }

    private WebViewUtils() {

    }

    public void url(String url) {
        if (TextUtils.isEmpty(url)) {
            activity.toast("URL路径为空.");
            return;
        }
        // file: 显示本地html，如 关于
        if (!url.startsWith("http") && !url.startsWith("file:")) {
            activity.toast("URL路径有误.");
            return;
        }
        webView.loadUrl(url);
    }

    public void html(String html) {
        if (TextUtils.isEmpty(html)) {
            activity.toast("没有数据");
            return;
        }
//            String style = "<style>img {width:100%;height:auto%;}</style>";
//            String meta = "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no\" />";
        html = decorate(html);
        webView.loadDataWithBaseURL("about:blank",html, "text/html", "utf-8",null);
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
        webView.addJavascriptInterface(new JavascriptInterface(activity), "imagelistener");
        webView.setWebViewClient(new MyWebViewClient(activity));
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");
                activity.startActivity(intent);
//                downloadUtils.download(url);
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView webView, int progress) {
                if(progress == 100){
                    tableReset(webView);
                }
                super.onProgressChanged(webView, progress);
            }
        });
    }

    private void tableReset(WebView webView) {
        webView.loadUrl("javascript:(function(){"
                + "var objs = document.getElementsByTagName('table'); "
                + "for(var i=0;i<objs.length;i++)  " + "{"
                + "var table = objs[i];   "
                + "    table.style.width = '100%';   "
                + "    table.style.height = 'auto';   "
                + "}" + "})()");
    }

    private void imgReset(WebView webView) {
        webView.loadUrl("javascript:(function(){"
                + "var objs = document.getElementsByTagName('img'); "
                + "for(var i=0;i<objs.length;i++)  " + "{"
                + "var img = objs[i];   "
                + "    img.style.width = '100%';   "
                + "    img.style.height = 'auto';   "
                + "}" + "})()");
    }

    private static String decorate(String htmlContent){
        htmlContent = htmlContent.replaceAll("\\\\", "");
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
                    .start(activity);
        }
    }

}
