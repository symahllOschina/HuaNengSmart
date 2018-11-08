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
import com.huaneng.zhgd.bean.Notice;
import com.huaneng.zhgd.utils.DateUtils;
import com.huaneng.zhgd.utils.GlideCircleTransform;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 通知Adapter
 */
public class NoticeAdapter extends BaseAdapter {

    private Context mContext;

    private List<Notice> notices = null;

    public NoticeAdapter(Context context, List<Notice> notices) {
        this.mContext = context;
        this.notices = notices;
    }

    public void setNotices(ArrayList<Notice> notices) {
        this.notices = notices;
    }

    @Override
    public int getCount() {
        if (notices != null) {
            return notices.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return notices.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_notice, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Notice entity = notices.get(position);
        viewHolder.titleTv.setText(entity.title);
        viewHolder.contentTv.setText(entity.subtitle);
        viewHolder.timeTv.setText(DateUtils.millisecondToTime(entity.create_time));
        GlideApp.with(mContext)
                .load(entity.image)
                .centerCrop()
                .placeholder(R.drawable.ic_notice)
                .transform(new GlideCircleTransform(mContext))
                .into(viewHolder.iconIv);
        return convertView;
    }

    private class ViewHolder {
        @ViewInject(R.id.iconIv)
        ImageView iconIv;
        @ViewInject(R.id.titleTv)
        TextView titleTv;
        @ViewInject(R.id.contentTv)
        TextView contentTv;
        @ViewInject(R.id.timeTv)
        TextView timeTv;
    }
}
