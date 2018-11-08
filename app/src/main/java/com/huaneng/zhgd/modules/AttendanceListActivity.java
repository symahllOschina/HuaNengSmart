package com.huaneng.zhgd.modules;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.zhouwei.library.CustomPopWindow;
import com.huaneng.zhgd.BaseActivity;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.WebViewActivity;
import com.huaneng.zhgd.adapter.AttendanceAdapter;
import com.huaneng.zhgd.bean.Attendance;
import com.huaneng.zhgd.bean.AttendanceCompany;
import com.huaneng.zhgd.bean.AttendanceDept;
import com.huaneng.zhgd.bean.Menu;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.network.RetrofitUtils;
import com.huaneng.zhgd.network.Subscriber;
import com.huaneng.zhgd.utils.UIUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 人员到岗信息
 */
@ContentView(R.layout.activity_attendance_list)
public class AttendanceListActivity extends BaseActivity {

    @ViewInject(R.id.listView)
    protected ListView mListView;
    @ViewInject(R.id.emptyView)
    protected View emptyView;

    List<AttendanceCompany> depts = new ArrayList<>();
    protected List<Attendance> attendanceList = new ArrayList<Attendance>();
    protected AttendanceAdapter adapter;
    protected String keywords;
    protected String company_id;
    CustomPopWindow mListPopWindow;
    private boolean clearData;

    protected Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menu = (Menu)getIntent().getSerializableExtra("menu");
        setTitle(menu.name);
        keywords = getIntent().getStringExtra("keywords");
        adapter = new AttendanceAdapter(this, attendanceList);
        mListView.setAdapter(adapter);
        load();
    }


    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_attendance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_chart:
                showChart();
                break;
            case R.id.action_menu:
                showMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showChart() {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("title", "人员到岗信息");
        intent.putExtra("url", RetrofitUtils.baseUrl + "workcount/workcheck");
        startActivity(intent);
    }

    private void showMenu() {
        if (attendanceList.isEmpty()) {
            return;
        }
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_list,null);
        ListView listView = contentView.findViewById(R.id.menuLv);
        listView.setAdapter(new AttendanceMenuAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mListPopWindow.dissmiss();

//                List<Attendance> filterList = new ArrayList<Attendance>();
//                AttendanceCompany dept = depts.get(i);
//                for (Attendance item: list) {
//                    if (dept == item.companyObj) {
//                        filterList.add(item);
//                    }
//                }
//                adapter.setList(filterList);
//                adapter.notifyDataSetChanged();

                company_id = depts.get(i).company_id;
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

    public void load() {
        if (!checkNetwork()) {
            return;
        }
        showWaitDialog();
        HTTP.service.getAttendanceList(company_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<AttendanceCompany>>(ctx){

                    @Override
                    public void onSuccess(Response<List<AttendanceCompany>> response) {
                        List<AttendanceCompany> deptList = response.data;
                        if (deptList == null || deptList.isEmpty()) {
                            mListView.setEmptyView(emptyView);
                        } else {
                            if (clearData) {
                                attendanceList.clear();
                                clearData = false;
                            }
                            for (AttendanceCompany company: deptList) {

                                // 单位列表只在首次加载，此时数据最全(如果不分页)
                                if (depts.isEmpty()) {
//                                    if (!company.company_name.equals("-") && !company.company_name.equals("--")) {
                                        depts.addAll(deptList);
//                                    }
                                }

                                // 遍历单位及其部门，获取所有人员的出勤信息
                                if (company.dept != null && !company.dept.isEmpty()) {
                                    for (AttendanceDept dept: company.dept) {
                                        if (dept.data != null && !dept.data.isEmpty()) {
//                                            if (!dept.dept_name.equals("-") && !dept.dept_name.equals("--")) {
                                                for (Attendance attendance: dept.data) {
//                                                    if (!attendance.PName.equals("-")) {
                                                        attendance.companyObj = company;
                                                        attendance.dept_name = dept.dept_name;
                                                        attendanceList.add(attendance);
//                                                    }
//                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFail(String msg) {
                        mListView.setEmptyView(emptyView);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (mListPopWindow != null) {
            mListPopWindow.dissmiss();
        }
        super.onDestroy();
    }

    class AttendanceMenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (depts != null) {
                return depts.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return attendanceList.get(position);
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
            AttendanceCompany entity = depts.get(position);
            UIUtils.showText(viewHolder.titleTv, entity.company_name);
            return convertView;
        }

        private class ViewHolder {
            @ViewInject(R.id.titleTv)
            TextView titleTv;
        }
    }
}
