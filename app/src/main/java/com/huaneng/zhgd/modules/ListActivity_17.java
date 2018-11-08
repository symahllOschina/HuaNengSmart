package com.huaneng.zhgd.modules;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.huaneng.zhgd.BaseActivity;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.WebViewActivity;
import com.huaneng.zhgd.adapter.ArticleAdapter;
import com.huaneng.zhgd.bean.Article;
import com.huaneng.zhgd.bean.Menu;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.network.Subscriber;
import com.huaneng.zhgd.utils.DateUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import info.hoang8f.android.segmented.SegmentedGroup;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

// 施工道路管理信息
@ContentView(R.layout.activity_list_17)
public class ListActivity_17 extends ArticleListActivity implements RadioGroup.OnCheckedChangeListener {

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
        }
        articles.clear();
        load();
    }
}
