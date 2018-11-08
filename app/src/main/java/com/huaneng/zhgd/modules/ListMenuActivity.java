package com.huaneng.zhgd.modules;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.zhouwei.library.CustomPopWindow;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.adapter.PopWindowMenuAdapter;
import com.huaneng.zhgd.bean.PopWindowMenu;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.network.Subscriber;

import org.xutils.view.annotation.ContentView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

// 右上角只带菜单的列表
@ContentView(R.layout.activity_article_list)
public class ListMenuActivity extends ArticleListActivity {

    private CustomPopWindow mListPopWindow;
    protected List<PopWindowMenu> menus = new ArrayList<PopWindowMenu>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getselect();
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

    private void showMenu() {
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
