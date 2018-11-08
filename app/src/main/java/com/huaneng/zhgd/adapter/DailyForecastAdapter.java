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
import com.huaneng.zhgd.bean.DailyForecast;
import com.huaneng.zhgd.bean.HeWeather;
import com.huaneng.zhgd.utils.DateUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 3-10天天气预报
 */
public class DailyForecastAdapter extends BaseAdapter {

    private Context mContext;

    private List<DailyForecast> dailyForecasts = null;

    public DailyForecastAdapter(Context context, List<DailyForecast> dailyForecasts) {
        this.mContext = context;
        this.dailyForecasts = dailyForecasts;
    }

    @Override
    public int getCount() {
        if (dailyForecasts != null) {
            return dailyForecasts.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return dailyForecasts.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.daily_forecast, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        DailyForecast entity = dailyForecasts.get(position);
        viewHolder.date.setText(entity.formatDate());
        viewHolder.cond_txt.setText(entity.cond_txt_d);
        viewHolder.tmp.setText(entity.tmp_max + "° ~ " + entity.tmp_min + "°");
        GlideApp.with(mContext)
                .load(HeWeather.weatherIcons.get(entity.cond_code_d))
                .into(viewHolder.cond_img);
        return convertView;
    }

    private class ViewHolder {
        @ViewInject(R.id.date)
        TextView date;
        @ViewInject(R.id.cond_txt)
        TextView cond_txt;
        @ViewInject(R.id.tmp)
        TextView tmp;
        @ViewInject(R.id.cond_img)
        ImageView cond_img;
    }
}
