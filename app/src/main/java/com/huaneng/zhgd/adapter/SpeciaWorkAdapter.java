package com.huaneng.zhgd.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.Entry;
import com.huaneng.zhgd.bean.SpecialWork;

import java.util.ArrayList;
import java.util.List;

/**
 * 特殊工种Adapter
 */
public class SpeciaWorkAdapter extends BaseAdapter {

    private Context mContext;

    private List<SpecialWork> works = null;

    public SpeciaWorkAdapter(Context context, List<SpecialWork> works) {
        this.mContext = context;
        this.works = works;
    }

    public void setArticles(ArrayList<SpecialWork> works) {
        this.works = works;
    }

    @Override
    public int getCount() {
        if (works != null) {
            return works.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return works.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_device_specia_work, null);
            viewHolder.workSideName = convertView.findViewById(R.id.item_workSideName);
            viewHolder.workNum = convertView.findViewById(R.id.item_workNum);
            viewHolder.workName = convertView.findViewById(R.id.item_workName);
            viewHolder.workType = convertView.findViewById(R.id.item_workType);
            viewHolder.workCompany = convertView.findViewById(R.id.item_workCompany);
            viewHolder.workIdentityNum = convertView.findViewById(R.id.item_workIdentityNum);
            viewHolder.workIsOption = convertView.findViewById(R.id.item_workIsOption);
            viewHolder.workAdmissionTime = convertView.findViewById(R.id.item_work_admissionTime);
            viewHolder.workRetreatTime = convertView.findViewById(R.id.item_work_retreatTime);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        SpecialWork work = works.get(position);
        viewHolder.workSideName.setText(String.format(mContext.getResources().getString(R.string.work_sideName),work.company_name));
        viewHolder.workNum.setText(String.format(mContext.getResources().getString(R.string.work_num),work.profession_serial));
        viewHolder.workName.setText(String.format(mContext.getResources().getString(R.string.work_name),work.profession_name));
        viewHolder.workType.setText(String.format(mContext.getResources().getString(R.string.work_type),work.profession_type));
        viewHolder.workCompany.setText(String.format(mContext.getResources().getString(R.string.work_company),work.profession_office));
        viewHolder.workIdentityNum.setText(String.format(mContext.getResources().getString(R.string.work_identity_num),work.profession_code));
        viewHolder.workIsOption.setText(String.format(mContext.getResources().getString(R.string.work_isoption),work.profession_authorization));
        String entyTime = work.profession_enty_time;
        if(!TextUtils.isEmpty(entyTime)){
            entyTime = entyTime.split(" ")[0];
        }
        viewHolder.workAdmissionTime.setText(String.format(mContext.getResources().getString(R.string.work_admission_time),entyTime));//进场时间
        String endTime = work.profession_end_time;
        if(!TextUtils.isEmpty(endTime)){
            endTime = endTime.split(" ")[0];
        }
        viewHolder.workRetreatTime.setText(String.format(mContext.getResources().getString(R.string.work_retreat_time),endTime));//退场时间
        return convertView;
    }

    private class ViewHolder {
        TextView workSideName;
        TextView workNum;
        TextView workName;
        TextView workType;
        TextView workCompany;
        TextView workIdentityNum;
        TextView workIsOption;
        TextView workAdmissionTime;
        TextView workRetreatTime;
    }
}
