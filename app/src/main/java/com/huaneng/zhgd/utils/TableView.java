package com.huaneng.zhgd.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.Cell;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Label-Value 键值对显示形式的表格
 */
public class TableView extends LinearLayout {

    @ViewInject(R.id.rootView)
    private LinearLayout rootView;

    public TableView(Context context) {
        this(context, null);
    }

    public TableView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_table, this);
        x.view().inject(this);
    }

    public void setData(List<Cell> cells) {
        if (cells != null && !cells.isEmpty()) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            for (Cell cell: cells) {
                View cellView = inflater.inflate(R.layout.item_cell, null);
                UIUtils.showText(((TextView)cellView.findViewById(R.id.labelTv)), cell.label);
                UIUtils.showText(((TextView)cellView.findViewById(R.id.valueTv)), cell.value);
                rootView.addView(cellView);
            }

        }
    }

}
