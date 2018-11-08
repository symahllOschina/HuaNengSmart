package com.huaneng.zhgd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.MenuItem;

import java.util.ArrayList;

/**
 * 菜单模块中信息类型Adapter
 */
public class GridMenuAdapter extends BaseAdapter {

    private Context mContext;

    private ArrayList<MenuItem> menuItems = null;

    public GridMenuAdapter(Context context, ArrayList<MenuItem> menuItems) {
        this.mContext = context;
        this.menuItems = menuItems;

    }

    public void setMenuItems(ArrayList<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    @Override
    public int getCount() {
        if (menuItems != null) {
            return menuItems.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return menuItems.get(position);
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
        MenuItem entity = menuItems.get(position);
        if (entity.itemName == null || entity.itemName.equals("")) {
            viewHolder.gridview_tv.setVisibility(View.INVISIBLE);
            viewHolder.gridview_iv.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.gridview_tv.setText(entity.itemName);    //类型名称
            viewHolder.gridview_iv.setImageResource(entity.itemDrawable);   //类型图片
        }
        return convertView;
    }

    private class ViewHolder {
        TextView gridview_tv;
        ImageView gridview_iv;
    }

}
