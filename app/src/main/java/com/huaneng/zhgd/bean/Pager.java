package com.huaneng.zhgd.bean;

import com.huaneng.zhgd.App;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

/**
 * 分页工具类
 */
public class Pager {

    // 本地的变量
    public int size = 10;//每页的条数
    public int pageIndex;// 第几页
    // 是否有下一页，要在updatePage()之后取值才正确
    public boolean hasNext;
    private boolean isRefresh;

    private RefreshLayout mRefreshLayout;

    public Pager() {

    }

    public void setRefreshLayout(RefreshLayout refreshLayout) {
        this.mRefreshLayout = refreshLayout;
    }

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
    }

    /**
     * 判断是否有下一页
     * total: 总条数
     * count: 当前条数
     */
    public void update(int total, int count) {
        // 判断是否有下一页
        if (total > count) {
            hasNext = true;
        } else {
            hasNext = false;
        }

        if (mRefreshLayout != null) {
            if (hasNext) {
                mRefreshLayout.setEnableLoadMore(true);
            } else {
                mRefreshLayout.setEnableLoadMore(false);
            }

//            else if(total == 0) {
//                mRefreshLayout.setEnableLoadMore(false);
//            } else {
//                mRefreshLayout.finishLoadMoreWithNoMoreData();
//            }
        }

    }

    /**
     * 提示刷新结果
     */
    public void showRefreshToast(boolean success) {
        if (isRefresh) {
            if (success) {
                App.toast("刷新成功");
            } else {
                App.toast("刷新失败");
            }
        }
        isRefresh = false;
    }

    public void reset() {
        this.pageIndex = 0;
        if (mRefreshLayout != null) {
            mRefreshLayout.setNoMoreData(false);
        }
    }

    // 下一页
    public int nextPage() {
        // pageIndex自身不加1
        return pageIndex + 1;
    }

    /**
     * 是否加载的第一页
     * 加载第一页时，清空之前的数据
     */
    public boolean isFirstPage() {
        return pageIndex == 0;
    }
}
