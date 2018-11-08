package com.huaneng.zhgd;

import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.File;

/**
 * PDF预览
 */
@ContentView(R.layout.activity_pdf_view)
public class PDFViewActivity extends FilePreviewActivity {

    @ViewInject(R.id.pdfView)
    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("PDF文件预览");
    }

    @Override
    protected void previewFile(File file) {
        pdfView.fromFile(file).load();
    }
}
