package com.huaneng.zhgd.warehouse;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import com.huaneng.zhgd.BaseActivity;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.adapter.WarehouseTypeAdapter;
import com.huaneng.zhgd.bean.Menu;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.network.Subscriber;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 仓库
 */
@ContentView(R.layout.activity_warehouse)
public class WarehouseActivity extends BaseActivity {

    @ViewInject(R.id.expandableListView)
    ExpandableListView expandableListView;

    WarehouseTypeAdapter warehouseTypeAdapter;
    List<Warehouse> warehouseTypes = new ArrayList<Warehouse>();

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menu = (Menu)getIntent().getSerializableExtra("menu");
        int width = getWindowManager().getDefaultDisplay().getWidth();
        expandableListView.setIndicatorBounds(width - 100, width - 10);
        warehouseTypeAdapter = new WarehouseTypeAdapter(this, warehouseTypes);
        expandableListView.setAdapter(warehouseTypeAdapter);
        load();
    }

    public void load() {
        if (!checkNetwork()) {
            return;
        }
        showWaitDialog();
        HTTP.service.warehouse(menu.url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Warehouse>>(ctx){

                    @Override
                    public void onSuccess(Response<List<Warehouse>> response) {
                        List<Warehouse> list = response.data;
                        warehouseTypes.addAll(list);
                        warehouseTypeAdapter.notifyDataSetChanged();

                        int groupCount = expandableListView.getCount();
                        for (int i = 0; i < groupCount; i++) {
                            expandableListView.expandGroup(i);
                        }
                    }
                });
    }
}
