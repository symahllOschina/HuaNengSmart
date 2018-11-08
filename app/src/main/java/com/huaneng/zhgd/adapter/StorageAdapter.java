package com.huaneng.zhgd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huaneng.zhgd.R;
import com.huaneng.zhgd.warehouse.Storage;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 进出库Adapter
 */
public class StorageAdapter extends BaseAdapter {

    private Context mContext;

    private List<Storage> storages = null;

    public StorageAdapter(Context context, List<Storage> storages) {
        this.mContext = context;
        this.storages = storages;
    }

    public void setStorages(ArrayList<Storage> storages) {
        this.storages = storages;
    }

    @Override
    public int getCount() {
        if (storages != null) {
            return storages.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return storages.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_storage, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Storage entity = storages.get(position);
        viewHolder.timeTv.setText(entity.state + "日期：" + entity.update_time);
        viewHolder.quantityTv.setText("数量：" + entity.num);
        return convertView;
    }

    private class ViewHolder {
        @ViewInject(R.id.timeTv)
        TextView timeTv;
        @ViewInject(R.id.quantityTv)
        TextView quantityTv;
    }
}
