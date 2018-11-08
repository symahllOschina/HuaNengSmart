package com.huaneng.zhgd.bean;

/**
 * 更新用户头像成功事件
 */
public class UpdateUserImgSuccessEvent {

    public User user;

    public UpdateUserImgSuccessEvent(User user) {
        this.user = user;
    }
}
