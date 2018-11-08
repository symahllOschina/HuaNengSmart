package com.huaneng.zhgd.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaneng.zhgd.GlideApp;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.Article;
import com.huaneng.zhgd.modules.CompanyInfo;
import com.huaneng.zhgd.utils.DateUtils;
import com.huaneng.zhgd.utils.GlideCircleTransform;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 项目进度
 */
public class ProjectProgressAdapter extends BaseExpandableListAdapter {

    List<CompanyInfo> companies;
    LayoutInflater layoutInflater;
    Context context;

    public ProjectProgressAdapter(Context context, List<CompanyInfo> companies) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.companies = companies;
    }

    public void setData(List<CompanyInfo> list) {
        companies = list;
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
        if (getGroupCount() > 0 && companies.get(i).projects != null) {
            return companies.get(i).projects.size();
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
            return companies.get(i).projects.get(i1);
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
        CompanyInfo company = (CompanyInfo)getGroup(groupPosition);
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
            convertView = layoutInflater.inflate(R.layout.item_project_progress, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder)convertView.getTag();
        }
        Article article = (Article)getChild(groupPosition, childPosition);
        viewHolder.titleTv.setText(article.title);
        viewHolder.subTitleTv.setText(article.subtitle);
        if (TextUtils.isEmpty(article.create_time)) {
            viewHolder.timeTv.setVisibility(View.GONE);
        } else {
            viewHolder.timeTv.setVisibility(View.VISIBLE);
            viewHolder.timeTv.setText(DateUtils.millisecondToDate(article.create_time));
        }
        GlideApp.with(convertView.getContext())
                .load(article.image)
                .centerCrop()
                .placeholder(R.drawable.img_default)
                .transform(new GlideCircleTransform(context))
                .into(viewHolder.iconIv);
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
        @ViewInject(R.id.titleTv)
        TextView titleTv;
        @ViewInject(R.id.subTitleTv)
        TextView subTitleTv;
        @ViewInject(R.id.timeTv)
        TextView timeTv;
        @ViewInject(R.id.iconIv)
        ImageView iconIv;
    }
}
