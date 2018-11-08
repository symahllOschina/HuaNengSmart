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

// 带2个Tab的List
@ContentView(R.layout.activity_list_tab)
public class ListTabActivity extends ArticleListActivity implements RadioGroup.OnCheckedChangeListener {

    @ViewInject(R.id.segmented)
    SegmentedGroup segmented;
    @ViewInject(R.id.tab1)
    RadioButton tab1;
    @ViewInject(R.id.tab2)
    RadioButton tab2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        segmented.setTintColor(getResources().getColor(R.color.colorPrimary));//设置默认线条颜色及背景色
        segmented.setOnCheckedChangeListener(this);//绑定单选按钮选择监听
        tab1.setText(getIntent().getStringExtra("tab1Title"));
        tab2.setText(getIntent().getStringExtra("tab2Title"));
        tab1.setChecked(true);
    }

    protected void initData() {
        // 作用：在父类的onCreate()方法里，不调用load()方法，而是在当前onCreate()里通过tab1.setChecked(true)触发load()方法
        this.isBigImgMode = getIntent().getBooleanExtra("isBigImgMode", false);
        loadDataonCreate = false;
    }

    // 重写父类菜单方法，使其不显示菜单
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
        }
        articles.clear();
        load();
    }
}
