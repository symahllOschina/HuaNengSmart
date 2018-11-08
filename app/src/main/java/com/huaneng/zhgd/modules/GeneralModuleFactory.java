package com.huaneng.zhgd.modules;

import android.content.Context;
import android.content.Intent;

import com.huaneng.zhgd.WebViewActivity;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.Module;

import java.util.ArrayList;
import java.util.List;

/**
 * 综合信息
 */
public class GeneralModuleFactory implements ModuleFactory {

    public List<Module> createModules(Context context) {
        ArrayList<Module> modules = new ArrayList<Module>();
        Module module = new Module("通讯录", "人员联系方式人员联系方式", R.mipmap.qualitys_supervision);
        Intent intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        module = new Module("人员到岗信息", "单位人员未到岗信息（部门、专业）", R.mipmap.people_arrive);
        intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        module = new Module("施工道路管理信息", "施工道路占道信息（预警提示），施工道路通行信息", R.mipmap.road_management);
        intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        module = new Module("施工用水管理", "停供水信息（预警提示）", R.mipmap.water_mangement);
        intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        module = new Module("施工用电管理信息", "停供电通知信息（预警提示）", R.mipmap.electricity_management);
        intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        module = new Module("总平面管理信息", "总平面调度信息", R.mipmap.ichnography_management);
        intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        module = new Module("工程会议信息", "周调度会信息，月调度会信息，季度调度会信息", R.mipmap.project_meeting);
        intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        module = new Module("其它通知", "信息发布.", R.mipmap.other_notice);
        intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        return modules;
    }
}
