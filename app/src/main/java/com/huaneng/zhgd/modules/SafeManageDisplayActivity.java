package com.huaneng.zhgd.modules;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.zhouwei.library.CustomPopWindow;
import com.huaneng.zhgd.Constants;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.adapter.PopWindowMenuAdapter;
import com.huaneng.zhgd.bean.PopWindowMenu;
import com.huaneng.zhgd.utils.SharedPreferencesUtils;

import org.xutils.view.annotation.ContentView;

// 7、安全管理 - 安全管理展示
@ContentView(R.layout.activity_list_tab)
public class SafeManageDisplayActivity extends ListTabMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initData() {
        super.initData();
        this.isBigImgMode = true;
    }

    @Override
    protected void onRestart() {
        clearData = true;
        load();
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        boolean hasExposureAuth = SharedPreferencesUtils.create(ctx).getBoolean(Constants.SPR_EXPOSURE_SAFEMANAGE, false);
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
                intent.putExtra("type", 37);
                intent.putExtra("norm", "2");
                intent.putExtra("subTitle", "安全隐患曝光");
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
}
