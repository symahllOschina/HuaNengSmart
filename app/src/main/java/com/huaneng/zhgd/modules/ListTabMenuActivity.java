package com.huaneng.zhgd.modules;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.zhouwei.library.CustomPopWindow;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.adapter.PopWindowMenuAdapter;
import com.huaneng.zhgd.bean.PopWindowMenu;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.network.Subscriber;
import com.huaneng.zhgd.utils.UIUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import info.hoang8f.android.segmented.SegmentedGroup;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

// 带2个Tab的、右上角带菜单的列表
@ContentView(R.layout.activity_list_tab)
public class ListTabMenuActivity extends ArticleListActivity implements RadioGroup.OnCheckedChangeListener {

    protected CustomPopWindow mListPopWindow;
    protected List<PopWindowMenu> menus = new ArrayList<PopWindowMenu>();

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
        getselect();
    }

    protected void initData() {
        // 作用：在父类的onCreate()方法里，不调用load()方法，而是在当前onCreate()里通过tab1.setChecked(true)触发load()方法
        loadDataonCreate = false;
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

    /**
     * 获取右上角菜单
     */
    public void getselect() {
        if (!checkNetwork()) {
            return;
        }
        showWaitDialog();
        HTTP.service.getselect(menu.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<PopWindowMenu>>(ctx){

                    @Override
                    public void onSuccess(Response<List<PopWindowMenu>> response) {
                        List<PopWindowMenu> list = response.data;
                        if (list != null && !list.isEmpty() && list.get(0)._child != null) {
                            menus.addAll(list.get(0)._child);
                        }
                    }
                });
    }

    // 重写父类菜单方法，使其显示menu菜单
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu:
                showMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void showMenu() {
        if (menus.isEmpty()) {
            return;
        }
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_list,null);
        ListView listView = contentView.findViewById(R.id.menuLv);
        listView.setAdapter(new PopWindowMenuAdapter(ctx, menus));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mListPopWindow.dissmiss();
                major = menus.get(i).id;
                clearData = true;
                load();
            }
        });
        mListPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
                .setBgDarkAlpha(0.7f) // 控制亮度
                .create()
                .showAsDropDown(mToolbar,1000,0);
    }

    @Override
    protected void onDestroy() {
        if (mListPopWindow != null) {
            mListPopWindow.dissmiss();
        }
        super.onDestroy();
    }
}
