package com.huaneng.zhgd.site;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.huaneng.zhgd.BaseRefreshActivity;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.adapter.ImportDeviceAdapter;
import com.huaneng.zhgd.bean.ImportDeviceInfo;
import com.huaneng.zhgd.bean.Menu;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.network.Subscriber;

import org.xutils.view.annotation.ContentView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 重要甲供设备材料信息 / 重要设备材料信息
 */
@ContentView(R.layout.activity_import_device)
public class ImportDeviceActivity extends BaseRefreshActivity<ImportDeviceInfo> {//

    ImportDeviceAdapter mAdapter;

    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menu = getSerializableExtra("menu");
        setTitle(menu.name);
        mAdapter = new ImportDeviceAdapter(this, mList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ctx, ImportDeviceInfoActivity.class);
                intent.putExtra("entity", mList.get(i));
                intent.putExtra("url", menu.url);
                intent.putExtra("title", menu.name);
                startActivity(intent);
            }
        });
        pagerHandler.adapter = mAdapter;
    }

    @Override
    protected void onResume() {
        mPager.reset();
        getList();
        super.onResume();
    }

    public void getList() {
        if (!checkNetwork()) {
            return;
        }
//        User user = UserUtils.getUser();
        HTTP.service.getImportDeviceInfo(menu.url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<ImportDeviceInfo>>(ctx) {

                    @Override
                    public void onSuccess(Response<List<ImportDeviceInfo>> response) {
                        pagerHandler.requestSuccess(response);
                    }

                    @Override
                    public void onWrong(String msg) {
                        super.onWrong(msg);
                        pagerHandler.requestFail();
                    }
                });
    }
}
