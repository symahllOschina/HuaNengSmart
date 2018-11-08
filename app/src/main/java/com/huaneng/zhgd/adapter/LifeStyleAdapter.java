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
import com.huaneng.zhgd.bean.LifeStyle;
import com.huaneng.zhgd.bean.HeWeather;
import com.huaneng.zhgd.utils.DateUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 生活指数
 */
public class LifeStyleAdapter extends BaseAdapter {

    private Context mContext;

    private List<LifeStyle> lifeStyles = null;

    public LifeStyleAdapter(Context context, List<LifeStyle> lifeStyles) {
        this.mContext = context;
        this.lifeStyles = lifeStyles;
    }

    @Override
    public int getCount() {
        if (lifeStyles != null) {
            return lifeStyles.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return lifeStyles.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.lifestyle, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        LifeStyle entity = lifeStyles.get(position);
        viewHolder.comf.setText(entity.getComf());
        viewHolder.brf.setText(entity.brf);
        viewHolder.txt.setText(entity.txt);
        return convertView;
    }

    private class ViewHolder {
        @ViewInject(R.id.comf)
        TextView comf;
        @ViewInject(R.id.brf)
        TextView brf;
        @ViewInject(R.id.txt)
        TextView txt;
    }
}
