package com.huaneng.zhgd.modules;

import android.content.Context;
import android.content.Intent;

import com.huaneng.zhgd.WebViewActivity;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.bean.Module;

import java.util.ArrayList;
import java.util.List;

/**
 * 技术管理
 */
public class TechnicalModuleFactory implements ModuleFactory {

    public List<Module> createModules(Context context) {
        ArrayList<Module> modules = new ArrayList<Module>();
        Module module = new Module("标准规范", "标准规范", R.mipmap.standard_specification);
        Intent intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        module = new Module("施工图", "施工图", R.mipmap.production_drawing);
        intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        module = new Module("规划方案", "规划方案", R.mipmap.planing_scheme);
        intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        module = new Module("设备、材料技术协议", "设备、材料技术协议", R.mipmap.technical_agreement);
        intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        module = new Module("设备随机资料", "设备随机资料", R.mipmap.random_data_equipment);
        intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        module = new Module("施工技术方案", "施工技术方案", R.mipmap.construction_technical_scheme);
        intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        module = new Module("调试措施", "调试措施", R.mipmap.debugging_measure);
        intent = new Intent(context, WebViewActivity.class);
        module.setIntent(intent);
        modules.add(module);

        return modules;
    }
}
