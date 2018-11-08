package com.huaneng.zhgd.study;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.huaneng.zhgd.BaseActivity;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.adapter.MyFragmentPagerAdapter;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 移动课堂/安全培训
 */
@ContentView(R.layout.activity_mobile_classroom)
public class MobileClassroomActivity extends BaseActivity {

    @ViewInject(R.id.tabLayout)
    TabLayout tabLayout;
    @ViewInject(R.id.viewPager)
    ViewPager viewPager;

    List<Fragment> fragments = new ArrayList<Fragment>();

    // 1.学习资料2.网络党课3.内部资料
//    public String[] titles = {"学习资料", "微课堂", "内部资料", "文本资料", "视频资料", "PPT资料"};
    public String[] titles = {"应急预案", "安全事项", "经典案例"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("安全培训");

//        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        for (String title : titles) {
            tabLayout.addTab(tabLayout.newTab().setText(title));
        }
        fragments.add(StudyDocFragment.getInstance(StudyDocFragment.TYPE_1));
        fragments.add(StudyDocFragment.getInstance(StudyDocFragment.TYPE_2));
        fragments.add(StudyDocFragment.getInstance(StudyDocFragment.TYPE_3));
        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments, titles);

        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.setOffscreenPageLimit(titles.length);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }
}
