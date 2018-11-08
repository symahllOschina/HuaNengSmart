package com.huaneng.zhgd.bean;

import android.text.TextUtils;

import com.huaneng.zhgd.network.RetrofitUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/12/5.
 */

public class Menu implements Serializable {

    public String id;
    public String pid;
    public String name;
    // 1-普通内容列表展示
    // 2-通讯录
    // 3-人员到岗，因为第三方接口暂未提供，此模块待定，
    // 4-仓库
    // 5-视频会议
    // 6-现场监控
    // 7-设备数据（此模块暂时搁置，显示即可，不必做跳转处理）
    public String list_type;
    public String images;
    public String url;
    public List<Menu> sub;

//    public String getType() {
//        if (!TextUtils.isEmpty(url)) {
//            return url.replaceAll(RetrofitUtils.baseUrl + "content/getlist/", "");
//        }
//        return null;
//    }
}
