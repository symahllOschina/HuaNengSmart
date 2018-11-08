package com.huaneng.zhgd.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.SpecialWork;
import com.huaneng.zhgd.bean.Vehicel;

import java.util.ArrayList;
import java.util.List;

/**
 * 现场流动机械Adapter
 */
public class VehicelAdapter extends BaseAdapter {

    private Context mContext;

    private List<Vehicel> vehicels = null;

    public VehicelAdapter(Context context, List<Vehicel> vehicels) {
        this.mContext = context;
        this.vehicels = vehicels;
    }

    public void setArticles(ArrayList<Vehicel> vehicels) {
        this.vehicels = vehicels;
    }

    @Override
    public int getCount() {
        if (vehicels != null) {
            return vehicels.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return vehicels.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_device_vehicel, null);
            viewHolder.sceneSideName = convertView.findViewById(R.id.item_sceneSideName);
            viewHolder.sceneName = convertView.findViewById(R.id.item_sceneName);
            viewHolder.sceneAll = convertView.findViewById(R.id.item_sceneAll);
            viewHolder.sceneOperator = convertView.findViewById(R.id.item_sceneOperator);
            viewHolder.sceneType = convertView.findViewById(R.id.item_sceneType);
            viewHolder.sceneAdmissionTime = convertView.findViewById(R.id.item_scene_admissionTime);
            viewHolder.sceneRetreatTime = convertView.findViewById(R.id.item_scene_retreatTime);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Vehicel vehicel = vehicels.get(position);
        viewHolder.sceneSideName.setText(String.format(mContext.getResources().getString(R.string.scene_sideName),vehicel.company_name));
        viewHolder.sceneName.setText(String.format(mContext.getResources().getString(R.string.scene_name),vehicel.vehicel_name));
        viewHolder.sceneAll.setText(String.format(mContext.getResources().getString(R.string.scene_all),vehicel.vehicel_holder));
        viewHolder.sceneOperator.setText(String.format(mContext.getResources().getString(R.string.scene_operator),vehicel.vehicel_operator));
        viewHolder.sceneType.setText(String.format(mContext.getResources().getString(R.string.scene_type),vehicel.vehicel_type));
        String entyTime = vehicel.vehicel_enty_time;
        if(!TextUtils.isEmpty(entyTime)){
            entyTime = entyTime.split(" ")[0];
        }
        viewHolder.sceneAdmissionTime.setText(String.format(mContext.getResources().getString(R.string.work_admission_time),entyTime));//进场时间
        String endTime = vehicel.vehicel_end_time;
        if(!TextUtils.isEmpty(endTime)){
            endTime = endTime.split(" ")[0];
        }
        viewHolder.sceneRetreatTime.setText(String.format(mContext.getResources().getString(R.string.work_retreat_time),endTime));//退场时间
        return convertView;
    }

    private class ViewHolder {
        TextView sceneSideName;
        TextView sceneName;
        TextView sceneAll;
        TextView sceneOperator;
        TextView sceneType;
        TextView sceneAdmissionTime;
        TextView sceneRetreatTime;
    }
}
