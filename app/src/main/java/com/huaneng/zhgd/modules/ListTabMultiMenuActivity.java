package com.huaneng.zhgd.modules;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.zhouwei.library.CustomPopWindow;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.PopWindowMenu;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.network.Subscriber;
import com.huaneng.zhgd.utils.UIUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

// 带2个Tab的、右上角带菜单(两级)的列表
@ContentView(R.layout.activity_list_tab)
public class ListTabMultiMenuActivity extends ListTabMenuActivity implements RadioGroup.OnCheckedChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                        menus = response.data;
                    }
                });
    }

    @Override
    protected void showMenu() {
        if (menus.isEmpty()) {
            return;
        }
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_expand_list,null);
        ExpandableListView expandableListView = contentView.findViewById(R.id.expandableListView);
        int width = UIUtils.dip2px(ctx, 200);
        expandableListView.setIndicatorBounds(width - 100, width - 10);
        final PopWindowMultiMenuAdapter adapter = new PopWindowMultiMenuAdapter();
        expandableListView.setAdapter(adapter);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                mListPopWindow.dissmiss();
                PopWindowMenu entity = (PopWindowMenu)adapter.getChild(i, i1);
                major = entity.id;
                clearData = true;
                load();
                return false;
            }
        });
        mListPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
                .setBgDarkAlpha(0.7f) // 控制亮度
                .create()
                .showAsDropDown(mToolbar,1000,0);
    }

    class PopWindowMultiMenuAdapter extends BaseExpandableListAdapter {

        LayoutInflater layoutInflater;

        public PopWindowMultiMenuAdapter() {
            layoutInflater = LayoutInflater.from(ctx);
        }

        @Override
        public int getGroupCount() {
            if (menus != null) {
                return menus.size();
            }
            return 0;
        }

        @Override
        public int getChildrenCount(int i) {
            if (getGroupCount() > 0 && menus.get(i)._child != null) {
                return menus.get(i)._child.size();
            }
            return 0;
        }

        @Override
        public Object getGroup(int i) {
            if (getGroupCount() > 0) {
                return menus.get(i);
            }
            return null;
        }

        @Override
        public Object getChild(int i, int i1) {
            if (getChildrenCount(i) > 0 && menus.get(i)._child != null) {
                return menus.get(i)._child.get(i1);
            }
            return null;
        }

        @Override
        public long getGroupId(int i) {
            return 0;
        }

        @Override
        public long getChildId(int i, int i1) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (getGroupCount() < 1) {
                return null;
            }
            GroupViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new GroupViewHolder();
                convertView = layoutInflater.inflate(R.layout.item_major, null);
                x.view().inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (GroupViewHolder)convertView.getTag();
            }
            PopWindowMenu entity = (PopWindowMenu)getGroup(groupPosition);
            UIUtils.showText(viewHolder.titleTv, entity.name);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (getChildrenCount(groupPosition) < 1) {
                return null;
            }
            ChildViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ChildViewHolder();
                convertView = layoutInflater.inflate(R.layout.item_major_sub, null);
                x.view().inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ChildViewHolder)convertView.getTag();
            }
            PopWindowMenu entity = (PopWindowMenu)getChild(groupPosition, childPosition);
            UIUtils.showText(viewHolder.titleTv, entity.name);
            return convertView;
        }

        class GroupViewHolder {
            @ViewInject(R.id.titleTv)
            TextView titleTv;
        }

        class ChildViewHolder {
            @ViewInject(R.id.titleTv)
            TextView titleTv;
        }
    }
}
