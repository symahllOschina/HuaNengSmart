package com.huaneng.zhgd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaneng.zhgd.GlideApp;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.Menu;
import com.huaneng.zhgd.utils.GlideCircleTransform;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MenuAdapter extends BaseAdapter {

    private Context mContext;

    private List<Menu> menus = null;

    public MenuAdapter(Context context, List<Menu> menus) {
        this.mContext = context;
        this.menus = menus;

    }

    public void setMenus(List<Menu> menuItems) {
        this.menus = menuItems;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_menu, null);
            viewHolder.gridview_tv = convertView.findViewById(R.id.item_gridvView_tv);
            viewHolder.gridview_iv = convertView.findViewById(R.id.item_gridvView_iv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Menu entity = menus.get(position);
        viewHolder.gridview_tv.setText(entity.name);
        GlideApp.with(mContext)
                .load(entity.images)
                .placeholder(R.drawable.img_default)
                .into(viewHolder.gridview_iv);
        return convertView;
    }

    private class ViewHolder {
        TextView gridview_tv;
        ImageView gridview_iv;
    }

}
