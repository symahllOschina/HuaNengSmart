package com.huaneng.zhgd.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.webkit.MimeTypeMap;

import com.huaneng.zhgd.BaseActivity;
import com.huaneng.zhgd.bean.FileDownloadedEvent;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件下载工具类
 */
public class DownloadUtils {

    private BaseActivity activity;

    public DownloadUtils(BaseActivity activity) {
        this.activity = activity;
    }
    //    private void startWithVideoView() {
//        //Uri uri = Uri.parse("file:" + Environment.getExternalStorageDirectory().getPath()+"/big_buck_bunny.mp4");
//        Uri uri = Uri.parse("file:///sdcard/big_buck_bunny.mp4");
//        VideoView videoView = (VideoView)this.findViewById(R.id.video_view);
//        videoView.setMediaController(new MediaController(this));
//        videoView.setVideoURI(uri);
//        videoView.start();
//        videoView.requestFocus();// 可显示控制面板，几秒后消失，需点击获取到焦点后再次显示
//    }

//    Bitmap videoIcon = ThumbnailUtils.createVideoThumbnail(paths[position], Video.Thumbnails.MINI_KIND);
//    Bitmap videoThumbnail = Bitmap.createScaledBitmap(videoIcon, width, height, true);

    private Map<String, Callback.Cancelable> map = new HashMap<>();

    public void download(final String url) {
        RequestParams params = new RequestParams(url);
        String path = Environment.getExternalStorageDirectory().getPath()+"/" + new File(url).getName();
        params.setSaveFilePath(path);
        Callback.Cancelable cancelable = x.http().get(params, new Callback.CommonCallback<File>() {
            @Override
            public void onSuccess(File result) {
                FileDownloadedEvent event = new FileDownloadedEvent(url, result);
                onDownloaded(event);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                FileDownloadedEvent event = new FileDownloadedEvent(url, false);
                onDownloaded(event);
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {

            }
        });
        map.put(url, cancelable);
    }

    public void onDownloaded(final FileDownloadedEvent event) {
        if (event.success) {
            new AlertDialog.Builder(activity)
                    .setTitle("提示")
                    .setMessage("下载成功，现在打开？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                Uri uri;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    uri = FileProvider.getUriForFile(activity, "com.huaneng.zhgd.provider", event.result);
                                } else {
                                    uri = Uri.fromFile(event.result);
                                }
                                intent.setDataAndType(uri, getMimeType(event.result));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                activity.startActivity(intent);
                            } catch (Exception e) {
                                Logger.e("打开文件失败", e);
                                activity.toast("打开失败，手机没有能打开该文件的APP");
                            }
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        } else {
            activity.toast("下载失败");
        }
    }

    private String getMimeType(File file) {
        return getMimeType(file.getName());
    }

    private String getMimeType(String fileName) {
        String nameSuffix= fileName.substring(fileName.lastIndexOf(".") + 1);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(nameSuffix.toLowerCase());
    }

    public void cancel(String url) {
        if (map.containsKey(url)) {
            Callback.Cancelable cancelable = map.get(url);
            if (!cancelable.isCancelled()) {
                cancelable.cancel();
                map.remove(url);
            }
        }
    }
}
