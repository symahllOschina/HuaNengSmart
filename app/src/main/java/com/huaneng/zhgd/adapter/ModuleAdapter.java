package com.huaneng.zhgd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.Module;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人中心模块Adapter
 */
public class ModuleAdapter extends BaseAdapter {

    private Context mContext;

    private List<Module> modules = null;

    public ModuleAdapter(Context context, List<Module> modules) {
        this.mContext = context;
        this.modules = modules;
    }

    public void setModules(ArrayList<Module> modules) {
        this.modules = modules;
    }

    @Override
    public int getCount() {
        if (modules != null) {
            return modules.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return modules.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_module, null);
            viewHolder.titleTv = convertView.findViewById(R.id.titleTv);
            viewHolder.subTitleTv = convertView.findViewById(R.id.subTitleTv);
            viewHolder.iconIv = convertView.findViewById(R.id.iconIv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Module entity = modules.get(position);
        viewHolder.titleTv.setText(entity.title);    //类型名称
        viewHolder.subTitleTv.setText(entity.subTitle);    //类型名称
        viewHolder.iconIv.setImageResource(entity.icon);   //类型图片
        return convertView;
    }

    private class ViewHolder {
        TextView titleTv;
        TextView subTitleTv;
        ImageView iconIv;
    }
}
