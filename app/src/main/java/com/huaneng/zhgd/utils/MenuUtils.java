package com.huaneng.zhgd.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.huaneng.zhgd.bean.Menu;
import com.huaneng.zhgd.device.EntryDataListActivity;
import com.huaneng.zhgd.exam.ExamCenterActivity;
import com.huaneng.zhgd.menu.MenusActivity;
import com.huaneng.zhgd.modules.ArticleListActivity;
import com.huaneng.zhgd.modules.AttendanceListActivity;
import com.huaneng.zhgd.modules.ListFilterCompanyActivity;
import com.huaneng.zhgd.modules.ListMenuActivity;
import com.huaneng.zhgd.modules.ListTab3Activity;
import com.huaneng.zhgd.modules.ListTabActivity;
import com.huaneng.zhgd.modules.ListTabMenuActivity;
import com.huaneng.zhgd.modules.ListTabMultiMenuActivity;
import com.huaneng.zhgd.modules.SafeManageDisplayActivity;
import com.huaneng.zhgd.modules.ScheduleListActivity;
import com.huaneng.zhgd.modules.SuperviseExposureActivity;
import com.huaneng.zhgd.site.ContactListActivity;
import com.huaneng.zhgd.site.ImportDeviceActivity;
import com.huaneng.zhgd.study.MobileClassroomActivity;
import com.huaneng.zhgd.video.meeting.VideoMeetingActivity;
import com.huaneng.zhgd.video.monitor.VideoMonitorActivity;
import com.huaneng.zhgd.warehouse.WarehouseActivity;

import java.io.Serializable;
import java.util.List;

public class MenuUtils {

    public static void router(Menu menu, Activity activity) {
        Intent intent = null;

        // 1、综合信息
        // 施工道路管理信息
        if (menu.name.equals("施工道路管理信息") || "17".equals(menu.id)) {
            intent = new Intent(activity, ListTabActivity.class);
            intent.putExtra("menu", menu);
            intent.putExtra("tab1Title", "施工道路占道信息");
            intent.putExtra("tab2Title", "施工道路通行信息");
            activity.startActivity(intent);
        }

        // 6、技术管理
        // 标准规范-二级menu
        else if (menu.name.equals("标准规范") || "28".equals(menu.id)) {
            intent = new Intent(activity, ListTabMultiMenuActivity.class);
            intent.putExtra("menu", menu);
            intent.putExtra("tab1Title", "标准");
            intent.putExtra("tab2Title", "规范");
            activity.startActivity(intent);
        }
        //施工图
        else if (menu.name.equals("施工图") || "29".equals(menu.id)
                || menu.name.equals("设备材料技术协议") || "31".equals(menu.id) // 设备材料技术协议
                || menu.name.equals("设备随机资料") || "32".equals(menu.id) // 设备随机资料
                || menu.name.equals("施工技术方案") || "33".equals(menu.id) // 施工技术方案
                || menu.name.equals("调试措施") || "34".equals(menu.id) // 调试措施

                // 7、安全管理
                || menu.name.equals("安全管理制度") || "36".equals(menu.id) // 安全管理制度

                // 8、质量管理
                || menu.name.equals("建设单位工程创优文件") || "44".equals(menu.id) // 建设单位工程创优文件
                || menu.name.equals("质量监督") || "47".equals(menu.id) // 质量监督
                || menu.name.equals("设备缺陷") || "49".equals(menu.id) // 设备缺陷
                ) {
            intent = new Intent(activity, ListMenuActivity.class);
            intent.putExtra("menu", menu);
            activity.startActivity(intent);
        }

        // 7、安全管理
        // 安全管理展示--大图
        else if (menu.name.equals("安全管理展示") || "37".equals(menu.id)) {
            intent = new Intent(activity, SafeManageDisplayActivity.class);
            intent.putExtra("menu", menu);
            intent.putExtra("tab1Title", "安全亮点展示");
            intent.putExtra("tab2Title", "安全隐患曝光");
            activity.startActivity(intent);
        }

        // 安全文明考核
        else if (menu.name.equals("安全文明考核") || "38".equals(menu.id)) {
            intent = new Intent(activity, ListTabActivity.class);
            intent.putExtra("menu", menu);
            intent.putExtra("tab1Title", "安全文明考核公示");
            intent.putExtra("tab2Title", "安全文明奖励公示");
            activity.startActivity(intent);
        }

        // 8、质量管理
        // 设备监造
        else if (menu.name.equals("设备监造") || "48".equals(menu.id)) {
            intent = new Intent(activity, ListTabMenuActivity.class);
            intent.putExtra("menu", menu);
            intent.putExtra("tab1Title", "设备监造信息");
            intent.putExtra("tab2Title", "监造月报信息");
            activity.startActivity(intent);
        }
        // 机组调试信息
        else if (menu.name.equals("机组调试信息") || "50".equals(menu.id)) {
            intent = new Intent(activity, ListTabMenuActivity.class);
            intent.putExtra("menu", menu);
            intent.putExtra("tab1Title", "专业日分部试运信息");
            intent.putExtra("tab2Title", "专业日系统调试信息");
            activity.startActivity(intent);
        }
        // 质量例会
        else if (menu.name.equals("质量例会") || "52".equals(menu.id)) {
            intent = new Intent(activity, ListTab3Activity.class);
            intent.putExtra("menu", menu);
            intent.putExtra("tab1Title", "月度质量例会");
            intent.putExtra("tab2Title", "季度质量例会");
            intent.putExtra("tab3Title", "年度质量例会");
            activity.startActivity(intent);
        }
        // 质量管理展示--大图
        else if (menu.name.equals("质量管理展示") || "53".equals(menu.id)) {
            intent = new Intent(activity, ListTabActivity.class);
            intent.putExtra("menu", menu);
            intent.putExtra("isBigImgMode", true);
            intent.putExtra("tab1Title", "日质量亮点展示");
            intent.putExtra("tab2Title", "日质量问题曝光");
            activity.startActivity(intent);
        }
        // 热机专业焊口质量信息
        else if (menu.name.equals("热机专业焊口质量信息") || "55".equals(menu.id)) {
            intent = new Intent(activity, ListTabActivity.class);
            intent.putExtra("menu", menu);
            intent.putExtra("tab1Title", "锅炉专业");
            intent.putExtra("tab2Title", "汽车专业");
            activity.startActivity(intent);
        }

        // 样板墙展示
        else if (menu.name.equals("样板墙展示") || "45".equals(menu.id)) {
            intent = new Intent(activity, ListFilterCompanyActivity.class);
            intent.putExtra("menu", menu);
            activity.startActivity(intent);
        }
        // 现场实体质量展示
        else if (menu.name.equals("现场实体质量展示") || "46".equals(menu.id)) {
            intent = new Intent(activity, ListFilterCompanyActivity.class);
            intent.putExtra("menu", menu);
            activity.startActivity(intent);
        }

        // 9、绿色工程管理
        // 绿色施工管理
        else if (menu.name.equals("绿色施工管理") || "58".equals(menu.id)) {
            intent = new Intent(activity, ListTabActivity.class);
            intent.putExtra("menu", menu);
            intent.putExtra("tab1Title", "绿色施工亮点展示");
            intent.putExtra("tab2Title", "绿色施工问题曝光");
            activity.startActivity(intent);
        }

        // 监督曝光 - 大图
        else if (menu.name.equals("监督曝光") || "12".equals(menu.id)) {
            intent = new Intent(activity, SuperviseExposureActivity.class);
            intent.putExtra("menu", menu);
            intent.putExtra("tab1Title", "服务监督曝光");
            intent.putExtra("tab2Title", "安全隐患曝光");
            activity.startActivity(intent);
        }

        // 进度管理
        else if (menu.name.equals("进度管理信息")) {
            intent = new Intent(activity, ScheduleListActivity.class);
            intent.putExtra("menu", menu);
            activity.startActivity(intent);
        }

        else if (menu.name.equals("施工图信息") || "24".equals(menu.id)) {
            intent = new Intent(activity, ListTabActivity.class);
            intent.putExtra("menu", menu);
            intent.putExtra("tab1Title", "急需施工图到图信息");
            intent.putExtra("tab2Title", "施工图到图信息");
            activity.startActivity(intent);
        }

        else if (menu.name.equals("关键部位施工信息") || "26".equals(menu.id)) {
            intent = new Intent(activity, ListTabActivity.class);
            intent.putExtra("menu", menu);
            intent.putExtra("isBigImgMode", true);
            intent.putExtra("tab1Title", "施工进度信息");
            intent.putExtra("tab2Title", "影响施工进度信息");
            activity.startActivity(intent);
        }

        // 1-普通内容列表展示
        else if ("0".equals(menu.list_type) || "1".equals(menu.list_type) || "7".equals(menu.list_type)) {
            if (menu.sub != null && !menu.sub.isEmpty()) {
                intent = new Intent(activity, MenusActivity.class);
                intent.putExtra("title", menu.name);
                intent.putExtra("menus", (Serializable) menu.sub);
                String menuJsonStr = JSON.toJSONString(menu);
                Log.e("普通内容菜单json字符串",menuJsonStr);
                activity.startActivity(intent);
            } else {
                intent = new Intent(activity, ArticleListActivity.class);
                intent.putExtra("title", menu.name);
                intent.putExtra("menu", menu);
                String menuJsonStr = JSON.toJSONString(menu);
                Log.e("普通内容菜单json字符串22",menuJsonStr);
                activity.startActivity(intent);
            }
        } else if ("2".equals(menu.list_type)) {// 2-通讯录
            intent = new Intent(activity, ContactListActivity.class);
            intent.putExtra("menu", menu);
            activity.startActivity(intent);
        } else if ("3".equals(menu.list_type)) {// 3-人员到岗信息
            intent = new Intent(activity, AttendanceListActivity.class);
            intent.putExtra("menu", menu);
            activity.startActivity(intent);
        } else if ("4".equals(menu.list_type)) {// 4-仓库
            intent = new Intent(activity, WarehouseActivity.class);
            intent.putExtra("menu", menu);
            activity.startActivity(intent);
        } else if ("5".equals(menu.list_type)) {// 5-视频会议
            intent = new Intent(activity, VideoMeetingActivity.class);
//            intent = new Intent(activity, MeetingLoginActivity.class);
            intent.putExtra("menu", menu);
            activity.startActivity(intent);
        } else if ("6".equals(menu.list_type)) {// 6-现场监控
            boolean isOption = false;
            intent = new Intent(activity, VideoMonitorActivity.class);
            intent.putExtra("menu", menu);
            if (menu.sub != null && !menu.sub.isEmpty()) {
                List<Menu> list = menu.sub;
                for (Menu sunMenu: list) {
                    if(sunMenu.id.equals("80")||sunMenu.name.equals("监控操作")){
                        isOption = true;
                        Log.e("监控是否可操作","可以");
                    }
                }
            }
            intent.putExtra("isOption", isOption);
            String menuJsonStr = JSON.toJSONString(menu);
            Log.e("现场监控菜单json字符串",menuJsonStr);
            activity.startActivity(intent);
        }

//        else if ("7".equals(menu.list_type)) {// 7-设备数据（此模块暂时搁置，显示即可，不必做跳转处理）
//            if (menu.sub != null && !menu.sub.isEmpty()) {
//                intent = new Intent(activity, ArticleListActivity.class);
//                intent.putExtra("title", menu.name);
//                intent.putExtra("menu", menu);
//                startActivity(intent);
//            } else {
//                activity.toast("暂无数据");
//            }
//        }

        else if ("8".equals(menu.list_type)) {// 安全培训
            intent = new Intent(activity, MobileClassroomActivity.class);
            activity.startActivity(intent);
        } else if ("9".equals(menu.list_type)) {// 考试中心
            intent = new Intent(activity, ExamCenterActivity.class);
            activity.startActivity(intent);
        } else if ("10".equals(menu.list_type)) {// 重要甲供设备材料信息
//            intent = new Intent(activity, ImportDeviceActivity.class);
//            intent.putExtra("menu", menu);
//            activity.startActivity(intent);

            intent = new Intent(activity, ListTabActivity.class);
            intent.putExtra("menu", menu);
            intent.putExtra("tab1Title", "急需设备、材料物流信息");
            intent.putExtra("tab2Title", "急需设备、材料到场信息");
            activity.startActivity(intent);
        }
        //设备管理
        //二级菜单：进场数据录入
        else if ("85".equals(menu.id)||menu.name.equals("进场数据录入")||"86".equals(menu.id)||menu.name.equals("特殊工种")||"87".equals(menu.id)||menu.name.equals("现场流动机械")) {
            intent = new Intent(activity, EntryDataListActivity.class);
            intent.putExtra("menu", menu);
            intent.putExtra("title", menu.name);
            activity.startActivity(intent);
        }
    }
}
