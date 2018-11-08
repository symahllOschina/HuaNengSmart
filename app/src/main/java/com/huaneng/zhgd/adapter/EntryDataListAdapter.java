package com.huaneng.zhgd.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.Entry;
import com.huaneng.zhgd.utils.TextStyleUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 进场数据录入Adapter
 */
public class EntryDataListAdapter extends BaseAdapter {

    private Context mContext;

    private List<Entry> entrys = null;

    public EntryDataListAdapter(Context context, List<Entry> entrys) {
        this.mContext = context;
        this.entrys = entrys;
    }

    public void setArticles(ArrayList<Entry> entrys) {
        this.entrys = entrys;
    }

    @Override
    public int getCount() {
        if (entrys != null) {
            return entrys.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return entrys.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_device_data_entry, null);
            viewHolder.deviceSideName = convertView.findViewById(R.id.item_deviceSideName);
            viewHolder.deviceNum = convertView.findViewById(R.id.item_deviceNum);
            viewHolder.deviceName = convertView.findViewById(R.id.item_deviceName);
            viewHolder.personInCharge = convertView.findViewById(R.id.item_personInCharge);
            viewHolder.deviceState = convertView.findViewById(R.id.item_deviceState);
            viewHolder.admissionTime = convertView.findViewById(R.id.item_admissionTime);
            viewHolder.retreatTime = convertView.findViewById(R.id.item_retreatTime);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Entry entry = entrys.get(position);
        viewHolder.deviceSideName.setText(String.format(mContext.getResources().getString(R.string.device_sideName),entry.COMPANY_NAME));
        viewHolder.deviceNum.setText(String.format(mContext.getResources().getString(R.string.device_num),entry.MECHANICAL_TYPE));
        viewHolder.deviceName.setText(String.format(mContext.getResources().getString(R.string.device_name),entry.MECHANICAL_NAME));
        viewHolder.personInCharge.setText(String.format(mContext.getResources().getString(R.string.device_person_in_charge),entry.PERSON_NAME));
        //MECHANICAL_STATE 这个字段表示的是 1：正常 0报废
        String stateStr = "";
        String state = entry.MECHANICAL_STATE;
        if(!TextUtils.isEmpty(state)){
            if(state.equals("1")){
                stateStr = "正常";
            }else{
                stateStr = "报废";
            }
        }
        viewHolder.deviceState.setText(String.format(mContext.getResources().getString(R.string.device_state),stateStr));
        //设备状态加颜色区分
        String deviceStateText = viewHolder.deviceState.getText().toString();
        if(deviceStateText.contains("报废")){
            SpannableStringBuilder style = TextStyleUtil.changeStyle(deviceStateText, 5, deviceStateText.length());
            viewHolder.deviceState.setText(style);
        }

        String entyTime = entry.MECHANICAL_ENTY_TIME;
        if(!TextUtils.isEmpty(entyTime)){
            entyTime = entyTime.split(" ")[0];
        }
        viewHolder.admissionTime.setText(String.format(mContext.getResources().getString(R.string.device_admission_time),entyTime));//进场时间
        String endTime = entry.MECHANICAL_END_TIME;
        if(!TextUtils.isEmpty(endTime)){
            endTime = endTime.split(" ")[0];
        }
        viewHolder.retreatTime.setText(String.format(mContext.getResources().getString(R.string.device_retreat_time),endTime));//退场时间
        return convertView;
    }

    private class ViewHolder {
        TextView deviceSideName;
        TextView deviceNum;
        TextView deviceName;
        TextView personInCharge;
        TextView deviceState;
        TextView admissionTime;
        TextView retreatTime;
    }
}
