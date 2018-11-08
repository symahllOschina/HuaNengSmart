package com.huaneng.zhgd;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.huaneng.zhgd.utils.FileUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.File;

/**
 * Txt文件预览
 */
@ContentView(R.layout.activity_txt_view)
public class TxtViewActivity extends FilePreviewActivity {

    @ViewInject(R.id.textView)
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Override
    protected void previewFile(File file) {
        textView.setText(FileUtils.read(file, "GBK"));
    }
}
