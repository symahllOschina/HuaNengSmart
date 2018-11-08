package com.huaneng.zhgd.bean;

import java.util.List;

/**
 * 分页基本对象
 */
public class PagerBase<T> {

    // 服务端返回的数据
    public int total;// 总条数
    public int page_num;// 总页数
    public List<T> list;

    public List getList() {
        return list;
    }
}
