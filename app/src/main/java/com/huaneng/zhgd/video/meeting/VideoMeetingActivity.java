package com.huaneng.zhgd.video.meeting;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huaneng.zhgd.BaseActivity;
import com.huaneng.zhgd.Constants;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.adapter.ContactMeetingAdapter;
import com.huaneng.zhgd.bean.InviteRoom;
import com.huaneng.zhgd.bean.JoinMeeting;
import com.huaneng.zhgd.bean.JoinMeetingEvent;
import com.huaneng.zhgd.bean.MyMenu;
import com.huaneng.zhgd.bean.User;
import com.huaneng.zhgd.modules.Company;
import com.huaneng.zhgd.modules.Contact;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.network.Subscriber;
import com.huaneng.zhgd.utils.DateUtils;
import com.huaneng.zhgd.utils.SharedPreferencesUtils;
import com.huaneng.zhgd.utils.UserUtils;
import com.inpor.fastmeetingcloud.ui.StartTheMiddleTierActivity;
import com.inpor.fastmeetingcloud.util.Constant;

import org.greenrobot.eventbus.Subscribe;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@ContentView(R.layout.activity_video_meeting)
public class VideoMeetingActivity extends BaseActivity {

//    public static final String SERVER = "http://113.200.203.89:8088/fmapi/webservice/jaxws?wsdl";
//    public static final String SERVER = "113.200.203.89";
    public static final String SERVER = "111.20.85.18";
    public static final String PORT = "1089";//"1089"

    @ViewInject(R.id.meetingLy)
    LinearLayout meetingLy;
    @ViewInject(R.id.meetingTips)
    TextView meetingTips;

    // 人员列表，选择多人进行会议
    @ViewInject(R.id.expandableListView)
    ExpandableListView expandableListView;

    ContactMeetingAdapter adapter;
    List<Company> companies = new ArrayList<Company>();
    public List<Contact> partners = new ArrayList<Contact>();
    List<JoinMeeting> meetings = new ArrayList<JoinMeeting>();

    private AlertDialog meetingDialog;
    public String lastRoomId;
    private JoinMeeting joinMeeting;
    protected String keywords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int width = getWindowManager().getDefaultDisplay().getWidth();

        expandableListView.setIndicatorBounds(width - 100, width - 10);
        adapter = new ContactMeetingAdapter(this, companies, partners);
        expandableListView.setAdapter(adapter);

        load();
        getproom();
    }

    /**
     * 获取当前用户要参加的会议
     */
    private void getproom() {
        if (!checkNetwork()) {
            return;
        }
        meetings.clear();
        HTTP.service.getproom()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<JoinMeeting>>(ctx){

                    @Override
                    public void onSuccess(Response<List<JoinMeeting>> response) {
                        List<JoinMeeting> list = response.data;
                        if (list != null && !list.isEmpty()) {
                            meetingLy.setVisibility(View.VISIBLE);
                            joinMeeting = list.get(0);
                            meetingTips.setText(joinMeeting.message);
                            String oldTime = SharedPreferencesUtils.create(ctx).get(Constants.SPR_LAST_MEETING_TIME);
                            String newTime = joinMeeting.update_time;
                            if (!DateUtils.isNewest(newTime, oldTime)) {
                                // 不比之前的会议时间新，则返回
                                return;
                            }
                            SharedPreferencesUtils.create(ctx).put(Constants.SPR_LAST_MEETING_TIME, newTime);
                            if (DateUtils.isExpired(newTime)) {
                                // 超期，则返回
                                return;
                            }
                            if (joinMeeting.roomid.equals(lastRoomId)) {
                                // 与之前的会议号相同，则返回
                                return;
                            }

                            // 有新的会议，则弹窗提示参加
                            meetingDialog = new AlertDialog.Builder(ctx)
                                    .setTitle("提示")
                                    .setMessage(joinMeeting.message)
                                    .setPositiveButton("进入会议", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            login(joinMeeting.roomid, joinMeeting.pwd);
                                        }
                                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                        } else {
                            meetingLy.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFail(String msg) {
                        Log.e(TAG, msg);
                    }
                });
    }

    /**
     * 进入会议
     */
    public void joinMeeting(View v) {
        login(joinMeeting.roomid, joinMeeting.pwd);
    }

    @Subscribe
    public void onJoinMeetingEvent(final JoinMeetingEvent event) {
        lastRoomId = event.roomid;
        if (meetingDialog != null) {
            meetingDialog.dismiss();
        }
    }

    /**
     * 加载所有公司的人员，用于邀请参加视频会议
     */
    public void load() {
        com.huaneng.zhgd.bean.Menu contactMenu = getContactMenu();
        if (!checkNetwork()) {
            return;
        }
        if (contactMenu == null || TextUtils.isEmpty(contactMenu.url)) {
            toast("URL地址不正确.");
            return;
        }
        showWaitDialog();
        HTTP.service.getCompanyList(contactMenu.url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Company>>(ctx){

                    @Override
                    public void onSuccess(Response<List<Company>> response) {
                        List<Company> list = response.data;

                        // 从人员列表中移除当前用户
                        User user = UserUtils.getUser();
                        Iterator<Company> iterator = list.iterator();
                        while(iterator.hasNext()){
                            Company company = iterator.next();
                            Iterator<Contact> contactItr = company.list.iterator();
                            while(contactItr.hasNext()){
                                Contact contact = contactItr.next();
                                if(user.markid.equals(contact.markid)) {
                                    contactItr.remove();
                                }
                            }
                        }
                        companies.addAll(list);
                        adapter.notifyDataSetChanged();
                        int groupCount = expandableListView.getCount();
                        for (int i = 0; i < groupCount; i++) {
                            expandableListView.expandGroup(i);
                        }
                    }
                });
    }

    /**
     * 获取通讯录menu
     */
    private com.huaneng.zhgd.bean.Menu getContactMenu() {
        String jsonMenu = SharedPreferencesUtils.create(ctx).get(Constants.SPR_MENU_JSON);
        MyMenu myMenu = JSON.parseObject(jsonMenu, MyMenu.class);
        if (myMenu != null && myMenu.menu != null && !myMenu.menu.isEmpty()) {
            return getContactMenu(myMenu.menu);
        }
        return null;
    }

    private com.huaneng.zhgd.bean.Menu getContactMenu(List<com.huaneng.zhgd.bean.Menu> menus) {
        for (com.huaneng.zhgd.bean.Menu menu: menus) {
            if (menu.list_type.equals("2")) {
                return menu;
            } else if (menu.sub != null) {
                com.huaneng.zhgd.bean.Menu contactMenu = getContactMenu(menu.sub);
                if (contactMenu != null) {
                    return contactMenu;
                }
            }
        }
        return null;
    }

    /**
     * 邀请多人参加会议
     */
    public void startMeeting(View v) {
//        login(null);
        if (adapter.partners.isEmpty()) {
            toast("请先选择参加会议的人员.");
        } else {
            String names = "";
            for (Contact contact: adapter.partners) {
                names += contact.name + "、";
            }
            names = names.substring(0, names.length() - 1);
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(String.format("您将邀请%s等%d人参加视频会议？", names, adapter.partners.size()))
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            sendMessage();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    /**
     * 邀请多人参加会议(推送消息)
     */
    private void sendMessage() {
        if (!checkNetwork()) {
            return;
        }
        showWaitDialog();
        int size = adapter.partners.size();
        String[] personids = new String[size];
        for (int i=0; i< size; i++) {
            personids[i] = adapter.partners.get(i).markid;
        }
        User user = UserUtils.getUser();
        String message = user.name + "邀请您参加视频会议.";

        String createid = user.markid;
        HTTP.service.sendMessage(personids, message, createid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<InviteRoom>(ctx){

                    @Override
                    public void onSuccess(Response<InviteRoom> response) {
                        toast("邀请发送成功.");
                        InviteRoom inviteRoom = response.data;
                        login(inviteRoom.roomid, inviteRoom.pwd);
                    }
                });
    }

    /**
     * 视频会议：账户密码登陆
     */
    public void login(String roomid, String pwd) {
        ComponentName componentName= new ComponentName("com.huaneng.zhgd", "com.inpor.fastmeetingcloud.ui.StartTheMiddleTierActivity");
        Intent intent = new Intent();
        intent.setAction(Constant.INTENT_APP_ACTION);
        intent.putExtra(StartTheMiddleTierActivity.ThirdLoginConstant.BUNDLE_USERNAME, UserUtils.getUser().markid);
        intent.putExtra(StartTheMiddleTierActivity.ThirdLoginConstant.BUNDLE_PASSWORD, pwd);
        intent.putExtra(StartTheMiddleTierActivity.ThirdLoginConstant.BUNDLE_ROOMID, Long.valueOf(roomid));
//        intent.putExtra(StartTheMiddleTierActivity.ThirdLoginConstant.BUNDLE_USERNAME, "test445");
//        intent.putExtra(StartTheMiddleTierActivity.ThirdLoginConstant.BUNDLE_PASSWORD, "test1234");
//        intent.putExtra(StartTheMiddleTierActivity.ThirdLoginConstant.BUNDLE_ROOMID, 10000L);
        intent.putExtra(StartTheMiddleTierActivity.ThirdLoginConstant.BUNDLE_SERVER_ADDRESS, SERVER);
        intent.putExtra(StartTheMiddleTierActivity.ThirdLoginConstant.BUNDLE_SERVER_PORT, PORT);
        intent.setComponent(componentName);
        startActivity(intent);
    }

    /**
     * 开启事件总线
     */
    protected boolean isEnableEventBus() {
        return true;
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
                        List<Company> list = new ArrayList<>();
                        if (TextUtils.isEmpty(keywords)) {
                            list.addAll(companies);
                        } else {
                            for (Company company: companies) {
                                Company cmp = hasPerson(company, keywords);
                                if (cmp != null) {
                                    list.add(cmp);
                                }
                            }
                            if (list.isEmpty()) {
                                toast("没有查询到数据");
                            }
                        }
                        adapter = new ContactMeetingAdapter(ctx, list, partners);
                        expandableListView.setAdapter(adapter);
                        for (int i = 0; i < list.size(); i++) {
                            expandableListView.expandGroup(i);
                        }
                    }
                })
                .setNegativeButton("取消", null).create();
        dialog.show();
    }

    /**
     * 判断company是否有名称包含name的人
     * 只要company中有一个contact的name包含参数name的，该company即符合条件
     */
    private Company hasPerson(Company company, String name) {
        if (company.list == null || company.list.isEmpty()) {
            return null;
        }
        // 创建一个新的Company，用来添加company中包含name的contact
        Company companyTemp = null;
        for (Contact contact : company.list) {
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
