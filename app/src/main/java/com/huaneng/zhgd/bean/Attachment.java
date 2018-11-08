package com.huaneng.zhgd.bean;

import java.io.Serializable;

/**
 * 附件
 */
public class Attachment implements Serializable {

    public String id;
    public String title;
    public String souce_type;//1.富文本 2.video 3.其它文件
    public String path;
    public String specialid;
    public String update_time;
    public String create_time;
    public String download_count;

    public String type;//1.学习资料2.网络党课3.内部资料
    public String extend;//文件扩展名
    public String status;

    public boolean isImage() {
        return "png".equalsIgnoreCase(extend) || "jpg".equalsIgnoreCase(extend) || "jpeg".equalsIgnoreCase(extend) || "gif".equalsIgnoreCase(extend);
    }

    public boolean isVideo() {
        return "视频".equals(souce_type) || "2".equals(souce_type) || "mp4".equalsIgnoreCase(extend);
    }

    public boolean isRichText() {
        return "富文本".equals(souce_type) || "1".equals(souce_type);
    }

}
