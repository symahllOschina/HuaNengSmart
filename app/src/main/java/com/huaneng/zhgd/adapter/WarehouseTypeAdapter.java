package com.huaneng.zhgd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.huaneng.zhgd.R;
import com.huaneng.zhgd.warehouse.Warehouse;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 库存类型
 */
public class WarehouseTypeAdapter extends BaseExpandableListAdapter {

    List<Warehouse> warehouseTypes;
    public List<Warehouse> warehouses = new ArrayList<Warehouse>();
    LayoutInflater layoutInflater;
    Context context;

    public WarehouseTypeAdapter(Context context, List<Warehouse> warehouseTypes) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.warehouseTypes = warehouseTypes;
    }

    @Override
    public int getGroupCount() {
        if (warehouseTypes != null) {
            return warehouseTypes.size();
        }
        return 0;
    }

    @Override
    public int getChildrenCount(int i) {
        if (getGroupCount() > 0 && warehouseTypes.get(i).sub != null) {
            return warehouseTypes.get(i).sub.size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int i) {
        if (getGroupCount() > 0) {
            return warehouseTypes.get(i);
        }
        return null;
    }

    @Override
    public Object getChild(int i, int i1) {
        if (getChildrenCount(i) > 0) {
            return warehouseTypes.get(i).sub.get(i1);
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (getGroupCount() < 1) {
            return null;
        }
        GroupViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new GroupViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_warehouse_type, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder)convertView.getTag();
        }
        Warehouse company = (Warehouse)getGroup(groupPosition);
        viewHolder.nameTv.setText(company.name);
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
            convertView = layoutInflater.inflate(R.layout.item_warehouse, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder)convertView.getTag();
        }
        Warehouse warehouse = (Warehouse)getChild(groupPosition, childPosition);
        viewHolder.nameTv.setText(warehouse.name);
        viewHolder.nameTv.setTag(warehouse);
        viewHolder.nameTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Warehouse child = (Warehouse)view.getTag();
                child.isExpend = !child.isExpend;
                notifyDataSetChanged();
            }
        });
        viewHolder.listView.setAdapter(new StorageAdapter(context, warehouse.data));
        if (warehouse.isExpend) {
            viewHolder.listView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.listView.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    class GroupViewHolder {
        @ViewInject(R.id.nameTv)
        TextView nameTv;
    }

    class ChildViewHolder {
        @ViewInject(R.id.nameTv)
        TextView nameTv;
        @ViewInject(R.id.listView)
        ListView listView;
    }
}