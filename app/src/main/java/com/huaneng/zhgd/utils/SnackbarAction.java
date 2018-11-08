package com.huaneng.zhgd.utils;

import android.view.View;

/**
 * Created by Administrator on 2017/12/25.
 */

public class SnackbarAction {

    public String title;

    public View.OnClickListener onClickListener;

    public SnackbarAction(String title, View.OnClickListener onClickListener) {
        this.title = title;
        this.onClickListener = onClickListener;
    }
}
