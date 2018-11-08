package com.huaneng.zhgd.site;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.huaneng.zhgd.BaseFragment;
import com.huaneng.zhgd.MainActivity;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.adapter.MenuAdapter;
import com.huaneng.zhgd.bean.Menu;
import com.huaneng.zhgd.utils.GlideImageLoader;
import com.huaneng.zhgd.utils.MenuUtils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.Arrays;
import java.util.List;

/**
 * 智慧工地
 */
@ContentView(R.layout.fragment_building_site)
public class BuildingSiteFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    @ViewInject(R.id.banner)
    Banner banner;
    @ViewInject(R.id.gridView)
    GridView mGridView;

    Integer[] images={R.drawable.banner};//, R.drawable.banner2, R.drawable.banner3};

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBanner();
        ((MainActivity) activity).initBuildingSiteMenu();
    }

    public void initMenus(List<Menu> menus) {
        MenuAdapter gridMenuAdapter = new MenuAdapter(getContext(), menus);
        mGridView.setAdapter(gridMenuAdapter);
        mGridView.setOnItemClickListener(this);
    }

    private void initBanner() {
        //设置banner样式
//        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
//        banner.setIndicatorGravity(BannerConfig.CENTER);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(Arrays.asList(images));
        //设置banner动画效果
//        banner.setBannerAnimation(Transformer.Default);
        //设置标题集合（当banner样式有显示title时）
//        banner.setBannerTitles(Arrays.asList(titles));
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(5000);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        //开始轮播
        banner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        MenuAdapter adapter = (MenuAdapter)adapterView.getAdapter();
        Menu menu = (Menu)adapter.getItem(i);
        MenuUtils.router(menu, getActivity());
    }
}
