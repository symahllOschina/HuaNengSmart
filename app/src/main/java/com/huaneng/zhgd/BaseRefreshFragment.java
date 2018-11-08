package com.huaneng.zhgd;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;

import com.huaneng.zhgd.bean.Pager;
import com.huaneng.zhgd.utils.EmptyView;
import com.huaneng.zhgd.utils.PagerHandler;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 下拉刷新Fragment基类
 * 用于RefreshLayout与后台请求刷新数据在一起的Fragment
 * 功能：
 * 1、下拉刷新之前，做一些准备工作
 * 2、下拉刷新完成后，通知监听器刷新UI
 */
public abstract class BaseRefreshFragment<T> extends BaseFragment implements OnRefreshListener, OnLoadMoreListener {

    @ViewInject(R.id.refreshLayout)
    protected RefreshLayout refreshLayout;
    @ViewInject(R.id.listView)
    protected ListView mListView;
    @ViewInject(R.id.gridView)
    protected GridView mGridView;
    @ViewInject(R.id.emptyView)
    protected EmptyView emptyView;

    protected Pager mPager = new Pager();
    protected PagerHandler pagerHandler = new PagerHandler(mPager);
    protected List<T> mList = new ArrayList<T>();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRefreshLayout();
        if (emptyView != null) {
            pagerHandler.emptyView = emptyView;
            emptyView.setRetryListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getList();
                }
            });
        }
        if (mListView != null) {
            pagerHandler.listView = mListView;
        } else {
            pagerHandler.listView = mGridView;
        }
        pagerHandler.dataSource = mList;
    }

    private void initRefreshLayout() {
        if (refreshLayout != null) {
            refreshLayout.setOnRefreshListener(this);
            refreshLayout.setOnLoadMoreListener(this);
            refreshLayout.setEnableAutoLoadMore(false);
            pagerHandler.setRefreshLayout(refreshLayout);
        }
    }

    public void setRefreshLayout(RefreshLayout refreshLayout) {
        this.refreshLayout = refreshLayout;
        initRefreshLayout();
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        mPager.reset();
        mPager.setRefresh(true);
        getList();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        getList();
    }

    public abstract void getList();
}
