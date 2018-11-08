package com.huaneng.zhgd.modules;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;

import com.huaneng.zhgd.BaseActivity;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.WebViewActivity;
import com.huaneng.zhgd.adapter.ProjectProgressAdapter;
import com.huaneng.zhgd.bean.Article;
import com.huaneng.zhgd.bean.Menu;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.network.Subscriber;
import com.huaneng.zhgd.utils.DateUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 进度管理
 */
@ContentView(R.layout.activity_contact_list)
public class ScheduleListActivity extends BaseActivity {

    @ViewInject(R.id.expandableListView)
    ExpandableListView expandableListView;

    ProjectProgressAdapter adapter;
    List<CompanyInfo> companies = new ArrayList<CompanyInfo>();
    Map<String, List<Article>> maps = new HashMap<>();

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menu = (Menu)getIntent().getSerializableExtra("menu");
        setTitle(menu.name);
        int width = getWindowManager().getDefaultDisplay().getWidth();
        expandableListView.setIndicatorBounds(width - 100, width - 10);
        adapter = new ProjectProgressAdapter(this, companies);
        expandableListView.setAdapter(adapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                CompanyInfo company = companies.get(groupPosition);
                if (maps.containsKey(company.id)) {
                    List<Article> value = maps.get(company.id);
                    if (value == null) {
                        toast("没有数据");
                    } else {

                    }
                } else {
                    load(company);
                }
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                Article child = (Article)adapter.getChild(i, i1);
                onItemClick(child.id);
                return false;
            }
        });
        loadCompany();
    }

    public void loadCompany() {
        if (!checkNetwork()) {
            return;
        }
        showWaitDialog();
        HTTP.service.getCompanies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<CompanyInfo>>(ctx){

                    @Override
                    public void onSuccess(Response<List<CompanyInfo>> response) {
                        companies.addAll(response.data);
                        adapter.notifyDataSetChanged();
//                        for (int i = 0; i < contactAdapter.getGroupCount(); i++) {
//                            expandableListView.expandGroup(i);
//                        }
                    }
                });
    }

    public void load(final CompanyInfo company) {
        if (TextUtils.isEmpty(menu.url)) {
            toast("暂无数据.");
            return;
        }
        if (!checkNetwork()) {
            return;
        }
        showWaitDialog();
        HTTP.service.getCompanies(menu.url, company.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Article>>(ctx){

                    @Override
                    public void onSuccess(Response<List<Article>> response) {
                        List<Article> list = response.data;
                        if (list == null || list.isEmpty()) {
                            maps.put(company.id, null);
                            toast("没有数据");
                        } else {
                            maps.put(company.id, list);
                            company.projects = list;
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                search();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 搜索
     */
    private void search() {
        final EditText edit = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("请输入关键字")
                .setView(edit)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String keywords = edit.getText().toString();
                        if (!TextUtils.isEmpty(keywords)) {
                            Intent intent = new Intent(ctx, ArticleListActivity.class);
                            intent.putExtra("title", menu.name);
                            intent.putExtra("menu", menu);
                            intent.putExtra("keywords", keywords);
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton("取消", null).create();
        dialog.show();
    }

    /**
     * 搜索
     */
    private void search1() {
        final EditText edit = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("请输入关键字")
                .setView(edit)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = edit.getText().toString();
                        if (TextUtils.isEmpty(name)) {
                            adapter.setData(companies);
                        } else {
                            List<CompanyInfo> list = new ArrayList<>();
                            for (CompanyInfo company: companies) {
                                CompanyInfo cmp = hasProject(company, name);
                                if (cmp != null) {
                                    list.add(cmp);
                                }
                            }
                            if (list.isEmpty()) {
                                toast("没有查询到数据");
                            }
                            adapter.setData(list);
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("取消", null).create();
        dialog.show();
    }

    /**
     * 判断company是否有名称包含name的项目
     */
    private CompanyInfo hasProject(CompanyInfo company, String name) {
        if (company.projects == null || company.projects.isEmpty()) {
            return null;
        }
        CompanyInfo companyTemp = null;
        for (Article project : company.projects) {
            if (project.title.contains(name) || project.subtitle.contains(name)) {
                if (companyTemp == null) {
                    companyTemp = new CompanyInfo();
                    companyTemp.name = company.name;
                }
                companyTemp.addProject(project);
            }
        }
        return companyTemp;
    }

    public void onItemClick(String id) {
        if (!checkNetwork()) {
            return;
        }
        showWaitDialog();
        HTTP.service.getArticle(menu.url, id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Article>(ctx){

                    @Override
                    public void onSuccess(Response<Article> response) {
                        Article data = response.data;
                        if (data == null) {
                            snackWarning("没有查询到信息.");
                            return;
                        }
                        Intent intent = new Intent(ctx, WebViewActivity.class);
                        intent.putExtra("title", data.title);
                        intent.putExtra("subtitle", data.subtitle);
                        intent.putExtra("time", DateUtils.millisecondToDate(data.create_time));

                        String content = data.content;
                        if (!TextUtils.isEmpty(content) && content.length() > 100000) {
                            WebViewActivity.bigData = content;
                            intent.putExtra("isBigData", true);
                        } else {
                            intent.putExtra("html", content);
                        }
                        startActivity(intent);
                    }
                });
    }
}

