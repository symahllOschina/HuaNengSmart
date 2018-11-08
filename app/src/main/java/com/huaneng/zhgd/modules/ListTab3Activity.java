package com.huaneng.zhgd.modules;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.huaneng.zhgd.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import info.hoang8f.android.segmented.SegmentedGroup;

// 带3个Tab的List
@ContentView(R.layout.activity_list_tab3)
public class ListTab3Activity extends ArticleListActivity implements RadioGroup.OnCheckedChangeListener {

    @ViewInject(R.id.segmented)
    SegmentedGroup segmented;
    @ViewInject(R.id.tab1)
    RadioButton tab1;
    @ViewInject(R.id.tab2)
    RadioButton tab2;
    @ViewInject(R.id.tab3)
    RadioButton tab3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        segmented.setTintColor(getResources().getColor(R.color.colorPrimary));//设置默认线条颜色及背景色
        segmented.setOnCheckedChangeListener(this);//绑定单选按钮选择监听
        tab1.setText(getIntent().getStringExtra("tab1Title"));
        tab2.setText(getIntent().getStringExtra("tab2Title"));
        tab3.setText(getIntent().getStringExtra("tab3Title"));
        tab1.setChecked(true);
    }

    protected void initData() {
        loadDataonCreate = false;
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        return true;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.tab1:
                norm = "1";
                break;
            case R.id.tab2:
                norm = "2";
                break;
            case R.id.tab3:
                norm = "3";
                break;
        }
        articles.clear();
        load();
    }
}