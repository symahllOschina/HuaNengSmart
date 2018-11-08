package com.huaneng.zhgd.modules;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.zhouwei.library.CustomPopWindow;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.PopWindowMenu;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.network.Subscriber;
import com.huaneng.zhgd.utils.UIUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import info.hoang8f.android.segmented.SegmentedGroup;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

// 按公司作为筛选条件的的List
@ContentView(R.layout.activity_article_list)
public class ListFilterCompanyActivity extends ArticleListActivity {

    private CustomPopWindow mListPopWindow;
    private List<CompanyInfo> companies = new ArrayList<CompanyInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadCompany();
    }

    public void loadCompany() {
        if (!checkNetwork()) {
            return;
        }
        HTTP.service.getCompanies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<CompanyInfo>>(ctx){

                    @Override
                    public void onSuccess(Response<List<CompanyInfo>> response) {
                        companies.addAll(response.data);
                    }
                });
    }

    // 重写父类菜单方法，使其显示menu菜单
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu:
                showMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMenu() {
        if (companies.isEmpty()) {
            return;
        }
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_list,null);
        ListView listView = contentView.findViewById(R.id.menuLv);
        listView.setAdapter(new CompanyMenuAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mListPopWindow.dissmiss();
                companyid = companies.get(i).id;
                clearData = true;
                load();
            }
        });
        mListPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
                .setBgDarkAlpha(0.7f) // 控制亮度
                .create()
                .showAsDropDown(mToolbar,1000,0);
    }

    @Override
    protected void onDestroy() {
        if (mListPopWindow != null) {
            mListPopWindow.dissmiss();
        }
        super.onDestroy();
    }

    class CompanyMenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (companies != null) {
                return companies.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return companies.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(ctx).inflate(R.layout.item_poplist_menu, null);
                x.view().inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            CompanyInfo entity = companies.get(position);
            UIUtils.showText(viewHolder.titleTv, entity.name);
            return convertView;
        }

        private class ViewHolder {
            @ViewInject(R.id.titleTv)
            TextView titleTv;
        }
    }
}
