package com.huaneng.zhgd;

import android.os.Bundle;
import android.os.Environment;
import android.widget.FrameLayout;

import com.orhanobut.logger.Logger;
import com.tencent.smtt.sdk.TbsReaderView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.File;

/**
 * Office文件预览
 */
@ContentView(R.layout.activity_office)
public class OfficeActivity extends FilePreviewActivity {

    @ViewInject(R.id.container)
    private FrameLayout container;
    private TbsReaderView readerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getIntent().getStringExtra("title"));
    }

    @Override
    protected void previewFile(File file) {
        readerView = new TbsReaderView(this, new TbsReaderView.ReaderCallback() {
            @Override
            public void onCallBackAction(Integer integer, Object o, Object o1) {
                Logger.d(o);
            }
        });
        //通过bundle把文件传给x5,打开的事情交由x5处理
        Bundle bundle = new Bundle();
        //传递文件路径
        bundle.putString("filePath", file.getAbsolutePath());
        //加载插件保存的路径
        bundle.putString("tempPath", Environment.getExternalStorageDirectory().getPath());
        //加载文件前的初始化工作,加载支持不同格式的插件
        String type = file.getName().split("\\.")[1];
        boolean b = readerView.preOpen(type, false);
        if (b) {
            readerView.openFile(bundle);
        }
        container.addView(readerView);
    }

    @Override
    protected void onDestroy() {
        readerView.onStop();
        super.onDestroy();
    }
}
