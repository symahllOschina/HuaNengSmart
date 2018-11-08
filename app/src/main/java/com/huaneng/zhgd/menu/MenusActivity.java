package com.huaneng.zhgd.menu;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.huaneng.zhgd.BaseActivity;
import com.huaneng.zhgd.Constants;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.adapter.MenuListAdapter;
import com.huaneng.zhgd.bean.Menu;
import com.huaneng.zhgd.utils.MenuUtils;
import com.huaneng.zhgd.utils.SharedPreferencesUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 模块列表
 */
@ContentView(R.layout.activity_menus)
public class MenusActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @ViewInject(R.id.listView)
    ListView mListView;
    private List<Menu> menus = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = getIntent().getStringExtra("title");
        setTitle(title);
        List<Menu> list = (List<Menu>)getIntent().getSerializableExtra("menus");
        for (Menu menu: list) {
            if (!"12".equals(menu.list_type) && !"13".equals(menu.list_type)) {
                menus.add(menu);
            }
        }
        String menuJsonStr = JSON.toJSONString(menus);
        Log.e("二级菜单json字符串",menuJsonStr);
        MenuListAdapter moduleAdapter = new MenuListAdapter(this, menus);
        mListView.setAdapter(moduleAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        MenuListAdapter adapter = (MenuListAdapter)adapterView.getAdapter();
        Menu menu = (Menu)adapter.getItem(i);
        MenuUtils.router(menu, this);
    }
}
