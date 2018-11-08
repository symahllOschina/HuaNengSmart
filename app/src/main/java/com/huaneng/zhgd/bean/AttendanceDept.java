package com.huaneng.zhgd.bean;

import java.util.List;

/**
 * 人员到岗信息-部门
 */
public class AttendanceDept {

    public String dept_id;
    public String dept_name;
    public String dept_count;
    public String count;
    public List<Attendance> data;

    public AttendanceCompany company;
}
