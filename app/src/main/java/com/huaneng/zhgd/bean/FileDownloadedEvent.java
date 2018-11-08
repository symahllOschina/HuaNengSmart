package com.huaneng.zhgd.bean;

import java.io.File;

/**
 * 文件下载完成事件
 */
public class FileDownloadedEvent {

    public String url;

    public File result;

    public boolean success;

    public FileDownloadedEvent(String url, boolean success) {
        this.url = url;
    }

    public FileDownloadedEvent(String url, File result) {
        this.url = url;
        this.result = result;
        if (result != null && result.exists() && result.length() > 0) {
            success = true;
        }
    }
}
