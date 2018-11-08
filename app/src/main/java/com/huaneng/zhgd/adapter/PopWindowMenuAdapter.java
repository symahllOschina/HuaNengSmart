package com.huaneng.zhgd.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.PopWindowMenu;
import com.huaneng.zhgd.utils.UIUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

public class PopWindowMenuAdapter extends BaseAdapter {

    private Activity activity;
    private List<PopWindowMenu> menus;
    private LayoutInflater layoutInflater;

    public PopWindowMenuAdapter(Activity activity, List<PopWindowMenu> menus) {
        this.activity = activity;
        this.menus = menus;
        layoutInflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        if (menus != null) {
            return menus.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return menus.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_poplist_menu, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        PopWindowMenu entity = menus.get(position);
        UIUtils.showText(viewHolder.titleTv, entity.name);
        return convertView;
    }

    private class ViewHolder {
        @ViewInject(R.id.titleTv)
        TextView titleTv;
    }
}
