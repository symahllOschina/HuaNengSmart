package com.huaneng.zhgd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.Attendance;
import com.huaneng.zhgd.utils.UIUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 文章Adapter
 */
public class AttendanceAdapter extends BaseAdapter {

    private Context mContext;

    private List<Attendance> list;

    public AttendanceAdapter(Context context, List<Attendance> list) {
        this.mContext = context;
        this.list = list;
    }

    public void setList(List<Attendance> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_attendance, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Attendance entity = list.get(position);
        UIUtils.showText(viewHolder.nameTv, entity.pname);
        UIUtils.showText(viewHolder.timeTv, entity.getTime());
        UIUtils.showText(viewHolder.companyTv, entity.companyObj.company_name);
        UIUtils.showText(viewHolder.deptTv, entity.dept_name);
        return convertView;
    }

    private class ViewHolder {
        @ViewInject(R.id.nameTv)
        TextView nameTv;
        @ViewInject(R.id.timeTv)
        TextView timeTv;
        @ViewInject(R.id.companyTv)
        TextView companyTv;
        @ViewInject(R.id.deptTv)
        TextView deptTv;
    }
}
