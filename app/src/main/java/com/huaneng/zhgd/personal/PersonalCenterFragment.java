package com.huaneng.zhgd.personal;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.huaneng.zhgd.BaseFragment;
import com.huaneng.zhgd.MainActivity;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.adapter.MenuListAdapter;
import com.huaneng.zhgd.bean.Menu;
import com.huaneng.zhgd.utils.MenuUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人中心
 */
@ContentView(R.layout.fragment_personal_center)
public class PersonalCenterFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    @ViewInject(R.id.listView)
    ListView mListView;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) activity).initPersonalCenterMenu();
    }

    public void initMenus(List<Menu> list) {
        List<Menu> menus = new ArrayList<>();
        for (Menu menu: list) {
            if (!"12".equals(menu.list_type) && !"13".equals(menu.list_type)) {
                menus.add(menu);
            }
        }
        MenuListAdapter moduleAdapter = new MenuListAdapter(activity, menus);
        mListView.setAdapter(moduleAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        MenuListAdapter adapter = (MenuListAdapter)adapterView.getAdapter();
        Menu menu = (Menu)adapter.getItem(i);
        MenuUtils.router(menu, getActivity());
    }
}
