package com.huaneng.zhgd.modules;

import android.app.Activity;
import android.content.Context;

import com.huaneng.zhgd.bean.Module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模块工厂
 */
public interface ModuleFactory {

    List<Module> createModules(Context context);

}
