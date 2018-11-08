package com.huaneng.zhgd.device;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.huaneng.zhgd.BaseActivity;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.adapter.ArticleAdapter;
import com.huaneng.zhgd.adapter.ArticleBigImgAdapter;
import com.huaneng.zhgd.adapter.EntryDataListAdapter;
import com.huaneng.zhgd.adapter.SpeciaWorkAdapter;
import com.huaneng.zhgd.adapter.VehicelAdapter;
import com.huaneng.zhgd.bean.Article;
import com.huaneng.zhgd.bean.Entry;
import com.huaneng.zhgd.bean.Menu;
import com.huaneng.zhgd.bean.SpecialWork;
import com.huaneng.zhgd.bean.Vehicel;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.network.Subscriber;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 *进场数据录入
 */
@ContentView(R.layout.activity_entry_list)
public class EntryDataListActivity extends BaseActivity{

    @ViewInject(R.id.listView)
    protected ListView mListView;
    @ViewInject(R.id.emptyView)
    protected View emptyView;

    protected List<Entry> entrys = new ArrayList<Entry>();//进场数据录入
    protected List<SpecialWork> works = new ArrayList<SpecialWork>();//特殊机械
    protected List<Vehicel> vehicels = new ArrayList<Vehicel>();//现场流动机械
    protected BaseAdapter mAdapter;

    protected Menu menu;
    protected boolean loadDataonCreate = true;
    protected boolean clearData;
    protected String url;


    private int page = 1;
    private int pageSize = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menu = (Menu)getIntent().getSerializableExtra("menu");
        setTitle(menu.name);
        this.url = menu.url;
        if("85".equals(menu.id)||menu.name.equals("进场数据录入")){
            mAdapter = new EntryDataListAdapter(this, entrys);
        }else if("86".equals(menu.id)||menu.name.equals("特殊工种")){
            mAdapter = new SpeciaWorkAdapter(this, works);
        }else if("87".equals(menu.id)||menu.name.equals("现场流动机械")){
            mAdapter = new VehicelAdapter(this, vehicels);
        }

        mListView.setAdapter(mAdapter);
        if (loadDataonCreate) {
            load();
        }
    }

    public void load(){
        if (TextUtils.isEmpty(this.url)) {
            toast("暂无数据.");
            return;
        }
        if (!checkNetwork()) {
            return;
        }
        showWaitDialog();
        if("85".equals(menu.id)||menu.name.equals("进场数据录入")){
            HTTP.service.getEntryList(this.url, menu.id, page+"", pageSize+"")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<Entry>>(ctx){

                        @Override
                        public void onSuccess(Response<List<Entry>> response) {
                            List<Entry> list = response.data;
                            if (clearData) {
                                entrys.clear();
                                clearData = false;
                            }
                            if (list == null || list.isEmpty()) {
                                mListView.setEmptyView(emptyView);
                            } else {
                                entrys.addAll(list);
                            }
                            String entryJsonList = JSON.toJSONString(entrys);
                            Log.e("json",entryJsonList);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
        }else if("86".equals(menu.id)||menu.name.equals("特殊工种")){
            HTTP.service.getWorkList(this.url, menu.id, page+"", pageSize+"")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<SpecialWork>>(ctx){

                        @Override
                        public void onSuccess(Response<List<SpecialWork>> response) {
                            List<SpecialWork> list = response.data;
                            if (clearData) {
                                works.clear();
                                clearData = false;
                            }
                            if (list == null || list.isEmpty()) {
                                mListView.setEmptyView(emptyView);
                            } else {
                                works.addAll(list);
                            }
                            String entryJsonList = JSON.toJSONString(works);
                            Log.e("json",entryJsonList);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
        }else if("87".equals(menu.id)||menu.name.equals("现场流动机械")){
            HTTP.service.getVehicelList(this.url, menu.id, page+"", pageSize+"")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<Vehicel>>(ctx){

                        @Override
                        public void onSuccess(Response<List<Vehicel>> response) {
                            List<Vehicel> list = response.data;
                            if (clearData) {
                                vehicels.clear();
                                clearData = false;
                            }
                            if (list == null || list.isEmpty()) {
                                mListView.setEmptyView(emptyView);
                            } else {
                                vehicels.addAll(list);
                            }
                            String entryJsonList = JSON.toJSONString(vehicels);
                            Log.e("json",entryJsonList);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
        }


    }
}
