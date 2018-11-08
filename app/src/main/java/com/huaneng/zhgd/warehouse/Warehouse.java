package com.huaneng.zhgd.warehouse;

import java.util.List;

/**
 * 仓库
 */
public class Warehouse {
    public String id;
    public String pid;
    public String name;
    public String number;
    public String level;
    public String create_time;
    public String update_time;

    public List<Storage> data;
    public List<Warehouse> sub;

    public boolean isExpend;
}
