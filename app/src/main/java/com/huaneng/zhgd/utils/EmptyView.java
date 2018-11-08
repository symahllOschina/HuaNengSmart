package com.huaneng.zhgd.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.huaneng.zhgd.R;

/**
 * Created by mashenghai on 2018/1/17.
 */

public class EmptyView extends LinearLayout {

    public enum Type {
        LOADING,EMPTY,FAIL,ERROR
    }

    private LinearLayout rootView;
    private LinearLayout loadingLayout;
    private LinearLayout emptyLayout;
    private LinearLayout failLayout;
    private LinearLayout errorLayout;

    public EmptyView(Context context) {
        this(context, null);
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    private void init(Context context) {
        inflate(context, R.layout.layout_empty_view, this);
        rootView = findViewById(R.id.rootView);
        loadingLayout = findViewById(R.id.loadingLayout);
        emptyLayout = findViewById(R.id.emptyLayout);
        failLayout = findViewById(R.id.failLayout);
        errorLayout = findViewById(R.id.errorLayout);
    }

    // 点击重试
    public void setRetryListener(OnClickListener onClickListener) {
        failLayout.setOnClickListener(onClickListener);
        errorLayout.setOnClickListener(onClickListener);
    }

    public void hide() {
        rootView.setVisibility(GONE);
    }

    public void loading() {
        setType(Type.LOADING);
    }

    public void empty() {
        setType(Type.EMPTY);
    }

    public void fail() {
        setType(Type.FAIL);
    }

    public void error() {
        setType(Type.ERROR);
    }

    // 设置无数据的类型
    public void setType(Type type) {
        loadingLayout.setVisibility(GONE);
        emptyLayout.setVisibility(GONE);
        failLayout.setVisibility(GONE);
        errorLayout.setVisibility(GONE);
        switch (type) {
            case LOADING:
                loadingLayout.setVisibility(VISIBLE);
                break;
            case EMPTY:
                emptyLayout.setVisibility(VISIBLE);
                break;
            case FAIL:
                failLayout.setVisibility(VISIBLE);
                break;
            case ERROR:
                errorLayout.setVisibility(VISIBLE);
                break;
        }
    }

}
