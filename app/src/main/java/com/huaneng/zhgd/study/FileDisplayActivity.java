package com.huaneng.zhgd.study;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.huaneng.zhgd.BaseActivity;
import com.huaneng.zhgd.PDFViewActivity;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.TxtViewActivity;
import com.huaneng.zhgd.bean.Attachment;
import com.huaneng.zhgd.bean.Cell;
import com.huaneng.zhgd.utils.DownloadUtils;
import com.huaneng.zhgd.utils.TableView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPreview;

/**
 * 文件预览、下载
 */
@ContentView(R.layout.activity_file_display)
public class FileDisplayActivity extends BaseActivity {

    @ViewInject(R.id.tableView)
    private TableView tableView;

    Attachment attachment;
    boolean isDownloadFinish;
//    DownloadUtils downloadUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attachment = (Attachment)getIntent().getSerializableExtra("attachment");
        setTitle(attachment.type);

//        downloadUtils = new DownloadUtils(this);
        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell("文件名称：", attachment.title));
        cells.add(new Cell("文件类型：", attachment.souce_type));
        if (!TextUtils.isEmpty(attachment.extend)) {
            cells.add(new Cell("扩展名：", attachment.extend));
        }
        cells.add(new Cell("下载次数：", attachment.download_count));
        cells.add(new Cell("创建时间：", attachment.create_time));
        tableView.setData(cells);
    }

    public void download(View view) {
        Uri uri = Uri.parse(attachment.path);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");
        startActivity(intent);
//        downloadUtils.download(attachment.path);
        toast("开始下载...");
    }

    @Override
    protected void onDestroy() {
        if (!isDownloadFinish) {
//            downloadUtils.cancel(attachment.path);
        }
        super.onDestroy();
    }
}
