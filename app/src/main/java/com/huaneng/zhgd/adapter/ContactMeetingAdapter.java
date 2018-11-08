package com.huaneng.zhgd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
 * 视频会议联系人
 */
public class ContactMeetingAdapter extends BaseExpandableListAdapter {

    List<Company> companies;
    public List<Contact> partners;
    LayoutInflater layoutInflater;
    Context context;

    public ContactMeetingAdapter(Context context, List<Company> companies, List<Contact> partners) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.partners = partners;
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
            convertView = layoutInflater.inflate(R.layout.item_meeting_contact, null);
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
        viewHolder.checkBox.setTag(contact);
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Contact partner = (Contact)compoundButton.getTag();
                if (isChecked) {
                    // 选中partner、且其未被添加到partners，则将其添加到partners
                    if (!partners.contains(partner)) {
                        partners.add(partner);
                    }
                } else {
                    partners.remove(partner);
                }
            }
        });
        viewHolder.checkBox.setChecked(partners.contains(contact));
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
        @ViewInject(R.id.headIv)
        ImageView headIv;
        @ViewInject(R.id.nameTv)
        TextView nameTv;
        @ViewInject(R.id.checkBox)
        CheckBox checkBox;
    }
}
