package com.huaneng.zhgd.bean;

import android.text.TextUtils;

/**
 * 人员到岗信息
 */
public class Attendance {

    public String id;
    public String pcode;
    public String pname;
    public String data_time;
    public String company;//单位编码
    public String dept_name;
    public String department;// 部门编码

    public AttendanceCompany companyObj;

    public String getTime() {
        if (!TextUtils.isEmpty(data_time) && data_time.length() > 19) {
            return data_time.substring(0, 19);
        }
        return data_time;
    }
}
