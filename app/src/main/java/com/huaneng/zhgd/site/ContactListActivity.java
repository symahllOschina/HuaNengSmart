package com.huaneng.zhgd.site;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.huaneng.zhgd.BaseActivity;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.adapter.ContactAdapter;
import com.huaneng.zhgd.bean.Menu;
import com.huaneng.zhgd.modules.Company;
import com.huaneng.zhgd.modules.Contact;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.network.Subscriber;
import com.huaneng.zhgd.warehouse.Warehouse;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 2-通讯录
 */
@ContentView(R.layout.activity_contact_list)
public class ContactListActivity extends BaseActivity {

    @ViewInject(R.id.expandableListView)
    ExpandableListView expandableListView;

    ContactAdapter contactAdapter;
    List<Company> companies = new ArrayList<Company>();

    private Menu menu;
    private List<Company> allCompanies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int width = getWindowManager().getDefaultDisplay().getWidth();
        menu = (Menu)getIntent().getSerializableExtra("menu");
        // 将Group的Indicator图标放到右边
        expandableListView.setIndicatorBounds(width - 100, width - 10);
        contactAdapter = new ContactAdapter(this, companies);
        expandableListView.setAdapter(contactAdapter);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                Contact child = (Contact)contactAdapter.getChild(i, i1);
                Intent intent = new Intent(ctx, ContactActivity.class);
                intent.putExtra("contact", child);
                startActivity(intent);
                return false;
            }
        });
        load();
    }

    public void load() {
        if (!checkNetwork()) {
            return;
        }
        showWaitDialog();
        HTTP.service.getCompanyList(menu.url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Company>>(ctx){

                    @Override
                    public void onSuccess(Response<List<Company>> response) {
                        allCompanies = response.data;
                        companies.addAll(allCompanies);
                        contactAdapter.notifyDataSetChanged();
                        for (int i = 0; i < contactAdapter.getGroupCount(); i++) {
                            expandableListView.expandGroup(i);
                        }
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
        if (allCompanies == null || allCompanies.isEmpty()) {
            return;
        }
        final EditText edit = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("请输入关键字")
                .setView(edit)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = edit.getText().toString();
                        companies.clear();
                        if (TextUtils.isEmpty(name)) {
                            companies.addAll(allCompanies);
                        } else {
                            List<Company> list = new ArrayList<>();
                            for (Company company: allCompanies) {
                                Company cmp = hasUser(company, name);
                                if (cmp != null) {
                                    list.add(cmp);
                                }
                            }
                            companies.addAll(list);
                        }
                        if (companies.isEmpty()) {
                            toast("没有查询到数据");
                        }
                        contactAdapter = new ContactAdapter(ctx, companies);
                        expandableListView.setAdapter(contactAdapter);
                        for (int i = 0; i < contactAdapter.getGroupCount(); i++) {
                            expandableListView.expandGroup(i);
                        }
                    }
                })
                .setNegativeButton("取消", null).create();
        dialog.show();
    }

    /**
     * 判断company是否有姓名包含name的人员
     * @param company
     * @param name
     * @return
     */
    private Company hasUser(Company company, String name) {
        Company companyTemp = null;
        for (Contact contact: company.list) {
            if (contact.name.contains(name)) {
                if (companyTemp == null) {
                    companyTemp = new Company();
                    companyTemp.name = company.name;
                }
                companyTemp.addContact(contact);
            }
        }
        return companyTemp;
    }
}
