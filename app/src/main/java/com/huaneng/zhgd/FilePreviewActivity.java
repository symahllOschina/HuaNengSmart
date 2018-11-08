package com.huaneng.zhgd;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.Setting;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.List;

/**
 * 文件预览
 */
public abstract class FilePreviewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("文件预览");
        requestPermissions();
    }

    private void requestPermissions() {

        Rationale mRationale = new Rationale<List<String>>() {
            @Override
            public void showRationale(Context context, List<String> permissions, final RequestExecutor executor) {
                // 这里使用一个Dialog询问用户是否继续授权。
                new AlertDialog.Builder(ctx)
                        .setTitle("提示")
                        .setMessage("预览需要下载文件到SD卡，请同意[读取SD卡]权限.")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
//                                requestPermissions();
                                // 如果用户继续：
                                executor.execute();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                // 如果用户中断：
                                executor.cancel();
                            }
                        }).show();
            }
        };

        AndPermission.with(this)
                .runtime()
                .permission(com.yanzhenjie.permission.Permission.Group.STORAGE)
                .rationale(mRationale)
                .onGranted(new com.yanzhenjie.permission.Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Log.d(TAG, "granted");
                        download();
                    }
                }).onDenied(new com.yanzhenjie.permission.Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Log.d(TAG, "onDenied");
        //                        JumpPermissionManagement.permissionDeniedAlert(ctx, "需要根据您当前的位置信息来获取天气信息，请打开[位置信息]权限.");
                        if (AndPermission.hasAlwaysDeniedPermission(ctx, permissions)) {
                            // 这里使用一个Dialog展示没有这些权限应用程序无法继续运行，询问用户是否去设置中授权。
                            AndPermission.with(ctx)
                                    .runtime()
                                    .setting()
                                    .onComeback(new Setting.Action() {
                                        @Override
                                        public void onAction() {
                                            // 用户从设置回来了。
                                            requestPermissions();
                                        }
                                    })
                                    .start();
                        }
                    }
                })
                .start();
    }

    private void download() {
        String url = getIntent().getStringExtra("url");
        if (!TextUtils.isEmpty(url)) {
            String tempPath = Environment.getExternalStorageDirectory() + "/temp/";
            new File(tempPath).mkdirs();
            String path = tempPath + new File(url).getName();
            File file = new File(path);
            if (file.exists() && file.length() > 0) {
                previewFile(file);
            } else {
                downloadFile(url, path);
            }
        }
    }

    /**
     * 预览文件
     */
    protected abstract void previewFile(File file);

    private void downloadFile(final String url, String path) {
        final String TAG = "---downloadFile---";
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("正在加载...");
        progressDialog.show();

        RequestParams requestParams = new RequestParams(url);
        requestParams.setSaveFilePath(path);
        x.http().get(requestParams, new Callback.ProgressCallback<File>() {

            @Override
            public void onWaiting() {
            }

            @Override
            public void onStarted() {
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                progressDialog.setMax((int) total);
                progressDialog.setProgress((int) current);
            }

            @Override
            public void onSuccess(File result) {
                progressDialog.dismiss();
                previewFile(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                toast("下载失败，请检查网络和SD卡");
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                progressDialog.dismiss();
            }

            @Override
            public void onFinished() {
                progressDialog.dismiss();
            }
        });
    }

}
