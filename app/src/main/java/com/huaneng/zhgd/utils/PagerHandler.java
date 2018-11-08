package com.huaneng.zhgd.utils;

import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.huaneng.zhgd.bean.Pager;
import com.huaneng.zhgd.bean.PagerBase;
import com.huaneng.zhgd.network.Response;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

/**
 * 分页处理器
 * 解决：分页前后、请求成功与否，要做的工作太多、太麻烦了，每个分页的地方都写一遍简直是噩梦
 */
public class PagerHandler {

    private RefreshLayout mRefreshLayout;
    public AbsListView listView;
    public EmptyView emptyView;
    public BaseAdapter adapter;
    public Pager pager;
    public List dataSource;

    public PagerHandler(Pager pager) {
        this.pager = pager;
    }

    public void setRefreshLayout(RefreshLayout refreshLayout) {
        this.mRefreshLayout = refreshLayout;
        pager.setRefreshLayout(refreshLayout);
    }

    public void requestSuccess(Response response) {
        requestSuccess((List)response.data, response.num);
    }

    /**
     * 请求数据成功
     * @param list 请求到的数据
     * @param total 总记录数
     */
    public void requestSuccess(List list, int total) {
        mRefreshLayout.finishRefresh(true);
        mRefreshLayout.finishLoadMore(true);
        if (list == null || list.isEmpty()) {
            emptyView.empty();
            if (adapter.isEmpty()) {
                listView.setEmptyView(emptyView);
            }
        } else {
            emptyView.hide();
            listView.setVisibility(View.VISIBLE);
            if (pager.isFirstPage()) {
                dataSource.clear();
            }
            dataSource.addAll(list);
            adapter.notifyDataSetChanged();
            // 只有加载成功才会执行当前方法，此时才能将pageIndex的值加1
            pager.pageIndex++;
        }
        pager.update(total, dataSource.size());
        pager.showRefreshToast(true);
    }

    public void requestFail() {
        pager.showRefreshToast(false);
        mRefreshLayout.finishRefresh(false);
        mRefreshLayout.finishLoadMore(false);
        // 首次加载失败，才显示emptyView
        if (pager.isFirstPage()) {
            dataSource.clear();
            adapter.notifyDataSetChanged();
            emptyView.fail();
            listView.setEmptyView(emptyView);
        }
    }
}
