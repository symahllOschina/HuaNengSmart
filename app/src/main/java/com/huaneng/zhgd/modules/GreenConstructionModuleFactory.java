package com.huaneng.zhgd.modules;

import android.content.Context;
import android.content.Intent;

import com.huaneng.zhgd.WebViewActivity;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.Module;

import java.util.ArrayList;
import java.util.List;

/**
 * 绿色施工管理
 */
public class GreenConstructionModuleFactory implements ModuleFactory {

    public List<Module> createModules(Context context) {
        ArrayList<Module> modules = new ArrayList<Module>();
        Module module = new Module("绿色施工策划方案", "绿色文明施工", R.mipmap.planning_scheme);
        Intent intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        module = new Module("绿色施工管理", "施工场容场貌，材料堆放", R.mipmap.construction_info);
        intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        module = new Module("绿色施工阶段性检查", "定点巡查情况", R.mipmap.periodic_inspection);
        intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        return modules;
    }
}
