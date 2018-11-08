package com.huaneng.zhgd.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaneng.zhgd.GlideApp;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.Article;
import com.huaneng.zhgd.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 文章Adapter
 */
public class ArticleAdapter extends BaseAdapter {

    private Context mContext;

    private List<Article> articles = null;

    public ArticleAdapter(Context context, List<Article> articles) {
        this.mContext = context;
        this.articles = articles;
    }

    public void setArticles(ArrayList<Article> articles) {
        this.articles = articles;
    }

    @Override
    public int getCount() {
        if (articles != null) {
            return articles.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return articles.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_article, null);
            viewHolder.titleTv = convertView.findViewById(R.id.titleTv);
            viewHolder.subTitleTv = convertView.findViewById(R.id.subTitleTv);
            viewHolder.timeTv = convertView.findViewById(R.id.timeTv);
            viewHolder.iconIv = convertView.findViewById(R.id.iconIv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Article entity = articles.get(position);
        viewHolder.titleTv.setText(entity.title);
        viewHolder.subTitleTv.setText(entity.subtitle);
        if (TextUtils.isEmpty(entity.create_time)) {
            viewHolder.timeTv.setVisibility(View.GONE);
        } else {
            viewHolder.timeTv.setVisibility(View.VISIBLE);
            viewHolder.timeTv.setText(DateUtils.millisecondToDate(entity.create_time));
        }
        GlideApp.with(mContext)
                .load(entity.image)
                .centerCrop()
                .placeholder(R.drawable.head_img)
                .into(viewHolder.iconIv);

        return convertView;
    }

    private class ViewHolder {
        TextView titleTv;
        TextView subTitleTv;
        TextView timeTv;
        ImageView iconIv;
    }
}
