package com.huaneng.zhgd.utils;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huaneng.zhgd.BaseActivity;
import com.huaneng.zhgd.bean.AppVersion;
import com.huaneng.zhgd.network.RetrofitUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/11/9.
 */

public class UIUtils {

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        // dp = px * (160/dpi)
        return (int) (dipValue * scale + 0.5f);
    }

    public static ArrayList<String> decorateUrl(ArrayList<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return urls;
        }
        ArrayList<String> urlList = new ArrayList<>();
        for (String url : urls) {
            urlList.addAll(decorateUrls(url));
        }
        return urlList;
    }

    public static String decorateUrl(String image) {
        return decorateUrl(image, RetrofitUtils.SERVER);
    }

    // image有可能是个[]数组，此时只返回第一个元素
    public static String decorateUrl(String image, String server) {
        if (TextUtils.isEmpty(server)) {
            server = RetrofitUtils.SERVER;
        }
        if (!TextUtils.isEmpty(image) && !image.startsWith("http")) {
            image = image.replaceAll(" ", "").replaceAll("\\[", "").replaceAll("\\]", "");
            image = Arrays.asList(image.split(",")).get(0);
            image = server + image;
        }
        return image;
    }

    // image有可能是个[]数组，返回一个列表
    public static ArrayList<String> decorateUrls(String image) {
        ArrayList<String> urlList = new ArrayList<>();
        if (!TextUtils.isEmpty(image)) {
            if (!image.startsWith("http")) {
                image = image.replaceAll(" ", "").replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "").replaceAll("\\\\", "");
                List<String> urls = Arrays.asList(image.split(","));
                for (String url : urls) {
                    if (!url.startsWith("http")) {
                        urlList.add(RetrofitUtils.SERVER + url);
                    } else {
                        urlList.add(url);
                    }
                }
            } else {
                urlList.add(image);
            }
        }
        return urlList;
    }

    public static void showText(TextView textView, Object value) {
        if (value != null && !TextUtils.isEmpty(value.toString())) {
            textView.setText(value.toString());
        } else {
            textView.setText(null);
        }
    }

    public static void showText(TextView textView, String prefix, Object value) {
        if (value != null && !TextUtils.isEmpty(value.toString())) {
            textView.setText(prefix + value.toString());
        } else {
            textView.setText(prefix);
        }
    }

    public static void showNum(TextView textView, String prefix, Object value) {
        textView.setText(prefix + getNumValue(value));
    }

    public static void showNum(TextView textView, Object value) {
        if (value == null || TextUtils.isEmpty(value.toString()) || "null".equals(value.toString())) {
            value = "0";
        }
        textView.setText(getNumValue(value));
    }

    private static String getNumValue(Object value) {
        if (value == null || TextUtils.isEmpty(value.toString()) || "null".equals(value.toString())) {
            return "0";
        }
        return value.toString();
    }

    /**
     * 权限检查
     * @param context
     * @param permission
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean checkOpsPermission(Context context, String permission) {
        try {
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            String opsName = AppOpsManager.permissionToOp(permission);
            if (opsName == null) {
                return true;
            }
            int opsMode = appOpsManager.checkOpNoThrow(opsName, android.os.Process.myUid(), context.getPackageName());
            return opsMode == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ex) {
            return true;
        }
    }

    /**
     * 是否需要更新
     * @param context
     * @param appVersion
     * @return
     */
    public static boolean isNeedUpdate(Context context, AppVersion appVersion) {
        String oldVersion = getVersionName(context);
        String newVersion = appVersion.value;
        Log.e("旧版本：",oldVersion);
        Log.e("新版本：",newVersion);
        return isNeedUpdate(oldVersion, newVersion);
    }

    /**
     * 对比新旧版本号，确定是否需要更新
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     * @return 是否需要更新
     */
    public static boolean isNeedUpdate(String oldVersion, String newVersion) {
        try {
            String[] odlVersionArr = oldVersion.split("\\.");
            String[] newVersionArr = newVersion.split("\\.");
            int size = Math.max(odlVersionArr.length, newVersionArr.length);
            for (int i = 0; i < size; i++) {
                if (i >= odlVersionArr.length) {
                    return true;
                }
                if (i >= newVersionArr.length) {
                    return false;
                }
                if (Integer.valueOf(newVersionArr[i]) > Integer.valueOf(odlVersionArr[i])) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    // 获取版本名称
    public static String getVersionName(Context context) {
        try {
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (Exception e) {
            return "1.0";
        }
    }

    // 获取版本号
    public static int getVersionCode(Context context) {
        try {
            PackageInfo packInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionCode;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * 必填验证
     * @param act
     * @param editTexts
     * @return
     */
    public static boolean validRequired(BaseActivity act, EditText... editTexts) {
        for (EditText editText: editTexts) {
            if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                editText.requestFocus();
                act.toast(editText.getHint().toString());
                return false;
            }
        }
        return true;
    }

    public static String value(EditText editText) {
        return editText.getText().toString().trim();
    }

    public static boolean isNullOrEmpty(TextView textView) {
        if (textView != null && textView.getText() != null) {
            String text = textView.getText().toString().trim();
            if (text != null && !text.equals("")) {
                return true;
            }
        }
        return false;
    }

}
