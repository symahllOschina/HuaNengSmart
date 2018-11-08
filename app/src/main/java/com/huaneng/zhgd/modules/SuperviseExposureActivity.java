package com.huaneng.zhgd.modules;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.example.zhouwei.library.CustomPopWindow;
import com.huaneng.zhgd.Constants;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.adapter.PopWindowMenuAdapter;
import com.huaneng.zhgd.bean.PopWindowMenu;
import com.huaneng.zhgd.utils.SharedPreferencesUtils;

import org.xutils.view.annotation.ContentView;

// 监督曝光
@ContentView(R.layout.activity_list_tab)
public class SuperviseExposureActivity extends ListTabMenuActivity implements RadioGroup.OnCheckedChangeListener {

    private String subTitle = "服务监督曝光";
    private int type = 68;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initData() {
        super.initData();
        this.isBigImgMode = true;
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        boolean hasExposureAuth = SharedPreferencesUtils.create(ctx).getBoolean(Constants.SPR_EXPOSURE_SUPERVISE, false);
        if (hasExposureAuth) {
            getMenuInflater().inflate(R.menu.menu_menu, menu);
        }
        return true;
    }

    /**
     * 获取右上角菜单
     */
    public void getselect() {
        PopWindowMenu menu = new PopWindowMenu();
        menu.name = "我要曝光";
        menus.add(menu);
    }

    @Override
    protected void onRestart() {
        clearData = true;
        load();
        super.onRestart();
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
                Intent intent = new Intent(ctx, ArticleSubmitActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("norm", norm);
                intent.putExtra("subTitle", subTitle);
                startActivity(intent);
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
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.tab1:
                this.url = menu.sub.get(0).url;
                this.subTitle = "服务监督曝光";
                type = 68;
                norm = "1";
                break;
            case R.id.tab2:
                this.url = menu.sub.get(1).url;
                this.subTitle = "安全隐患曝光";
                type = 69;
                norm = "2";
                break;
        }
        articles.clear();
        load();
    }
}
