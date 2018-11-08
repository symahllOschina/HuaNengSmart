package com.huaneng.zhgd.modules;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.huaneng.zhgd.BaseActivity;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.WebViewActivity;
import com.huaneng.zhgd.adapter.ArticleAdapter;
import com.huaneng.zhgd.adapter.ArticleBigImgAdapter;
import com.huaneng.zhgd.bean.Article;
import com.huaneng.zhgd.bean.Menu;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.network.Subscriber;
import com.huaneng.zhgd.utils.DateUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@ContentView(R.layout.activity_article_list)
public class ArticleListActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @ViewInject(R.id.listView)
    protected ListView mListView;
    @ViewInject(R.id.emptyView)
    protected View emptyView;

    protected List<Article> articles = new ArrayList<Article>();
    protected BaseAdapter articleAdapter;
    protected String keywords;

    protected Menu menu;
    protected String norm;
    protected String major;
    protected String companyid;
    protected boolean loadDataonCreate = true;
    protected boolean clearData;
    protected boolean isBigImgMode;
    protected String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menu = (Menu)getIntent().getSerializableExtra("menu");
        setTitle(menu.name);
        this.url = menu.url;
        initData();
        keywords = getIntent().getStringExtra("keywords");
        mListView.setOnItemClickListener(this);
        if (isBigImgMode) {
            articleAdapter = new ArticleBigImgAdapter(this, articles);
        } else {
            articleAdapter = new ArticleAdapter(this, articles);
        }
        mListView.setAdapter(articleAdapter);
        if (loadDataonCreate) {
            load();
        }
    }

    protected void initData() {

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
    public void search() {
        final EditText edit = new EditText(this);
        edit.setText(keywords);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("请输入关键字")
                .setView(edit)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        keywords = edit.getText().toString();
                        articles.clear();
                        articleAdapter.notifyDataSetChanged();
                        load();
                    }
                })
                .setNegativeButton("取消", null).create();
        dialog.show();
    }

    public void load() {
        if (TextUtils.isEmpty(this.url)) {
            toast("暂无数据.");
            return;
        }
        if (!checkNetwork()) {
            return;
        }
        showWaitDialog();
        HTTP.service.getArticleList(this.url, keywords, norm, major, companyid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Article>>(ctx){

                    @Override
                    public void onSuccess(Response<List<Article>> response) {
                        List<Article> list = response.data;
                        if (clearData) {
                            articles.clear();
                            clearData = false;
                        }
                        if (list == null || list.isEmpty()) {
                            mListView.setEmptyView(emptyView);
                        } else {
                            articles.addAll(list);
                        }
                        articleAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (!checkNetwork()) {
            return;
        }
        showWaitDialog();
        HTTP.service.getArticle(this.url, articles.get(i).id)
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
