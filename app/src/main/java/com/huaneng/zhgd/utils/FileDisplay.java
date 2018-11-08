package com.huaneng.zhgd.utils;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.huaneng.zhgd.BaseActivity;
import com.huaneng.zhgd.OfficeActivity;
import com.huaneng.zhgd.PDFViewActivity;
import com.huaneng.zhgd.TxtViewActivity;
import com.huaneng.zhgd.bean.Attachment;
import com.huaneng.zhgd.bean.News;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.network.Subscriber;
import com.huaneng.zhgd.study.FileDisplayActivity;
import com.huaneng.zhgd.WebViewActivity;
import com.huaneng.zhgd.study.VideoPlayVerticalActivity;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.iwf.photopicker.PhotoPreview;

/**
 * Created by mashenghai on 2018/2/10.
 */

public class FileDisplay {

    public static void displayAttachment(BaseActivity activity, Attachment attachment, String apiUrl) {
        // "souce_type": "文件",富文本,视频
        String url = attachment.path.toLowerCase();
        if (attachment.isVideo()) {
            Intent intent = new Intent(activity, VideoPlayVerticalActivity.class);
            intent.putExtra("attachment", attachment);
            activity.startActivity(intent);
        } else if (url.endsWith(".pdf")) {
            Intent intent = new Intent(activity, PDFViewActivity.class);
            intent.putExtra("url", url);
            activity.startActivity(intent);
        } else if (url.endsWith(".txt")) {
            Intent intent = new Intent(activity, TxtViewActivity.class);
            intent.putExtra("url", url);
            activity.startActivity(intent);
        } else if (isOffice(url)) {
            Intent intent = new Intent(activity, OfficeActivity.class);
            intent.putExtra("title", attachment.title);
            intent.putExtra("url", url);
            activity.startActivity(intent);
        } else if (isImage(url)) {
            previewImage(url, activity);
        } else if ("文件".equals(attachment.souce_type)) {
            displayFile(activity, attachment);
        } else if (attachment.isRichText()) {
            displayHtml(activity, attachment, apiUrl);
//            if (attachment.isImage()) {
//                displayImage(activity, attachment);
//            } else {
//                displayRichText(activity, attachment);
//            }
        } else {
            displayFile(activity, attachment);
        }
    }

    private static boolean isOffice(String url) {
        return url.endsWith(".doc")||url.endsWith(".docx")||url.endsWith(".xls")||url.endsWith(".xlsx")||url.endsWith(".ppt")|| url.endsWith(".pptx");
    }

    private static boolean isImage(String url) {
        return url.endsWith(".jpg")||url.endsWith(".jpeg")||url.endsWith(".png")||url.endsWith(".gif")||url.endsWith(".bmp");
    }

    /**
     * 预览图片
     */
    private static void previewImage(String url, BaseActivity activity) {
        ArrayList<String> list = new ArrayList<>();
        list.add(url);
        PhotoPreview.builder()
                .setPhotos(list)
                .setCurrentItem(0)
                .setShowDeleteButton(false)
                .start(activity);
    }

    public static void displayImage(Activity activity, Attachment attachment) {
        ArrayList<String> list = UIUtils.decorateUrls(attachment.path);
        if (list != null && !list.isEmpty()) {
            PhotoPreview.builder()
                    .setPhotos(list)
                    .setCurrentItem(0)
                    .setShowDeleteButton(false)
                    .start(activity);
        }
    }

    public static void displayFile(Activity activity, Attachment attachment) {
        Intent intent = new Intent(activity, FileDisplayActivity.class);
        intent.putExtra("attachment", attachment);
        activity.startActivity(intent);
    }

    public static void displayRichText(Activity activity, Attachment attachment) {
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra("title", attachment.title);
        intent.putExtra("time", DateUtils.millisecondToDate(attachment.update_time));

        String content = attachment.path;
        if (!TextUtils.isEmpty(content) && content.length() > 100000) {
            WebViewActivity.bigData = content;
            intent.putExtra("isBigData", true);
        } else {
            intent.putExtra("html", content);
        }
        activity.startActivity(intent);
    }

    private static void displayHtml(final BaseActivity ctx, Attachment attachment, String url) {
        if (!ctx.checkNetwork()) {
            return;
        }
        HTTP.service.getAttachment(url, attachment.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<News>(ctx) {

                    @Override
                    public void onSuccess(Response<News> response) {
                        News news = response.data;
                        Intent intent = new Intent(ctx, WebViewActivity.class);
                        intent.putExtra("title", news.title);
                        intent.putExtra("time", DateUtils.millisecondToDate(news.update_time));

                        String content = news.content;
                        if (!TextUtils.isEmpty(content) && content.length() > 100000) {
                            WebViewActivity.bigData = content;
                            intent.putExtra("isBigData", true);
                        } else {
                            intent.putExtra("html", content);
                        }
                        ctx.startActivity(intent);
                    }

                    @Override
                    public void onWrong(String msg) {
                        ctx.toast(msg);
                    }
                });
    }
}
