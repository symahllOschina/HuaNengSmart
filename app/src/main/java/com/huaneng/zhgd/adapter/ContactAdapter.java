package com.huaneng.zhgd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaneng.zhgd.GlideApp;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.modules.Company;
import com.huaneng.zhgd.modules.Contact;
import com.huaneng.zhgd.utils.GlideCircleTransform;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by Administrator on 2017/11/27.
 */

public class ContactAdapter extends BaseExpandableListAdapter {

    List<Company> companies;
    LayoutInflater layoutInflater;
    Context context;

    public ContactAdapter(Context context, List<Company> companies) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.companies = companies;
    }

    @Override
    public int getGroupCount() {
        if (companies != null) {
            return companies.size();
        }
        return 0;
    }

    @Override
    public int getChildrenCount(int i) {
        if (getGroupCount() > 0 && companies.get(i).list != null) {
            return companies.get(i).list.size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int i) {
        if (getGroupCount() > 0) {
            return companies.get(i);
        }
        return null;
    }

    @Override
    public Object getChild(int i, int i1) {
        if (getChildrenCount(i) > 0) {
            return companies.get(i).list.get(i1);
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
            convertView = layoutInflater.inflate(R.layout.item_company, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder)convertView.getTag();
        }
        Company company = (Company)getGroup(groupPosition);
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
            convertView = layoutInflater.inflate(R.layout.item_contact, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder)convertView.getTag();
        }
        Contact contact = (Contact)getChild(groupPosition, childPosition);
        viewHolder.nameTv.setText(contact.name);
        GlideApp.with(convertView.getContext())
                .load(contact.images)
                .centerCrop()
                .placeholder(R.drawable.ic_head)
                .transform(new GlideCircleTransform(context))
                .into(viewHolder.headIv);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    class GroupViewHolder {
        @ViewInject(R.id.nameTv)
        TextView nameTv;
    }

    class ChildViewHolder {
        @ViewInject(R.id.headIv)
        ImageView headIv;
        @ViewInject(R.id.nameTv)
        TextView nameTv;
    }
}
