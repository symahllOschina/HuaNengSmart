package com.huaneng.zhgd.modules;

import android.content.Context;
import android.content.Intent;

import com.huaneng.zhgd.WebViewActivity;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.Module;

import java.util.ArrayList;
import java.util.List;

/**
 * 智慧党建
 */
public class EnterpriseCultureModuleFactory implements ModuleFactory {

    public List<Module> createModules(Context context) {
        ArrayList<Module> modules = new ArrayList<Module>();
        Module module = new Module("党建信息", "全面推进党务管理信息系统建设,提高党建管理科学水平,用信息化全面改造提升党建工作.推进党建信息化建设,提高党建信息化管理科学水平,全面改造提升党建信息化工作.", R.mipmap.red_culture);
        Intent intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        module = new Module("活动信息", "有效快速的管理活动信息", R.mipmap.green_culture);
        intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        module = new Module("党旗飘扬", "敬业务实”就是热爱自己的工作岗位，热爱本职工作，务实肯干。", R.mipmap.blue_culture);
        intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        return modules;
    }
}
