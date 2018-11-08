package com.huaneng.zhgd.modules;

import android.content.Context;
import android.content.Intent;

import com.huaneng.zhgd.WebViewActivity;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.Module;

import java.util.ArrayList;
import java.util.List;

/**
 * 进度管理
 */
public class ScheduleModuleFactory implements ModuleFactory {

    public List<Module> createModules(Context context) {
        ArrayList<Module> modules = new ArrayList<Module>();
        Module module = new Module("我的任务", "显示或创建任务", R.mipmap.renwujindu);
        Intent intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        module = new Module("施工图信息", "施工图信息，施工图信息，施工图信息，施工图信息，施工图信息施工图信息", R.mipmap.construction_drawing_info);
        intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        module = new Module("甲供设备、材料信息", "甲供设备、材料信息；甲供设备、材料信息；甲供设备、材料信息；甲供设备、材料信息", R.mipmap.equipment_info);
        intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        module = new Module("关键部位施工信息", "关键部位施工信息；关键部位施工信息；关键部位施工信息；关键部位施工信息；关键部位施工信息", R.mipmap.keyparts_construction_info);
        intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        module = new Module("进度管理信息", "进度管理信息", R.mipmap.schedule_management_info);
        intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        return modules;
    }
}
