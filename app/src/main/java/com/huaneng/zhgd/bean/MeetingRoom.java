package com.huaneng.zhgd.bean;

/**
 * Created by Administrator on 2017/11/29.
 */

public class MeetingRoom {

    public String roomId;

    public boolean isValid;

    public MeetingRoom(String roomId, int result) {
        this.roomId = roomId;
        isValid = (result == 0);
    }
}
