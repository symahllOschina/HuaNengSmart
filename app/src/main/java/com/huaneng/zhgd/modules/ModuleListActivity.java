package com.huaneng.zhgd.modules;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.huaneng.zhgd.BaseActivity;
import com.huaneng.zhgd.Constants;
import com.huaneng.zhgd.MainActivity;
import com.huaneng.zhgd.R;
import com.huaneng.zhgd.WebViewActivity;
import com.huaneng.zhgd.adapter.ModuleAdapter;
import com.huaneng.zhgd.bean.Article;
import com.huaneng.zhgd.bean.Module;
import com.huaneng.zhgd.network.HTTP;
import com.huaneng.zhgd.network.Response;
import com.huaneng.zhgd.utils.SharedPreferencesUtils;
import com.huaneng.zhgd.utils.UIUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 模块列表
 */
@ContentView(R.layout.activity_module_list)
public class ModuleListActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    static final Map<String, ModuleFactory> factory = new HashMap<String, ModuleFactory>();
    static {
        factory.put("综合信息", new GeneralModuleFactory());
        factory.put("进度管理", new ScheduleModuleFactory());
        factory.put("技术管理", new TechnicalModuleFactory());
        factory.put("安全管理", new SafeModuleFactory());
        factory.put("质量管理", new QualityModuleFactory());
        factory.put("绿色施工管理", new GreenConstructionModuleFactory());
        factory.put("智慧党建", new EnterpriseCultureModuleFactory());
//        factory.put("视频会议", new GeneralModuleFactory());
        factory.put("现场监控", new MonitorModuleFactory());
//        factory.put("仓库管理", new GeneralModuleFactory());
        factory.put("设备数据", new EquipmentDataModuleFactory());
//        factory.put("监督曝光", new GeneralModuleFactory());
    }

    @ViewInject(R.id.listView)
    ListView mListView;
    List<Module> modules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = getIntent().getStringExtra("title");
        setTitle(title);
        modules = factory.get(title).createModules(this);
        ModuleAdapter moduleAdapter = new ModuleAdapter(this, modules);
        mListView.setAdapter(moduleAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        Intent intent = modules.get(i).intent;
//        if (intent != null) {
//            startActivity(modules.get(i).intent);
//        }

        Intent intent = new Intent(this, ArticleListActivity.class);
        intent.putExtra("title", modules.get(i).title);
        startActivity(intent);
    }
}
