package com.huaneng.zhgd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.JoinMeeting;

import java.util.List;

/**
 * 待参加会议的Adapter
 */
public class JoinMeetingAdapter extends BaseAdapter {

    private Context mContext;

    private List<JoinMeeting> meetings = null;

    public JoinMeetingAdapter(Context context, List<JoinMeeting> meetings) {
        this.mContext = context;
        this.meetings = meetings;
    }

    @Override
    public int getCount() {
        if (meetings.size() > 3) {
            return 3;
        }
        return meetings.size();
    }

    @Override
    public Object getItem(int position) {
        return meetings.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_join_meeting, null);
            viewHolder.titleTv = convertView.findViewById(R.id.titleTv);
            viewHolder.joinBtn = convertView.findViewById(R.id.joinBtn);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        JoinMeeting entity = meetings.get(position);
        viewHolder.titleTv.setText(entity.message);
        viewHolder.joinBtn.setTag(entity.roomid);
        viewHolder.joinBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                String roomid = view.getTag().toString();
//                Intent intent = new Intent(mContext, JoinRoomActivity.class);
//                intent.putExtra("roomId", roomid);
//                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    private class ViewHolder {
        TextView titleTv;
        Button joinBtn;
    }
}
