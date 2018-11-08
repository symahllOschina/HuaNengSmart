package com.huaneng.zhgd.bean;

/**
 * Created by TH on 2017/11/28.
 */

public class JPushMsg {

    public static final String TYPE_MEETING_RECEIVED = "1";
    public static final String TYPE_MEETING_DELETED = "2";
    public static final String TYPE_MEETING_MESSAGE = "3";//消息通知

    public String roomid;
    // type 1-邀请会议，2-删除会议 markid 当前登录者id
    public String type;
    public String alert;
    public String update_time;

    public String pwd;
}
