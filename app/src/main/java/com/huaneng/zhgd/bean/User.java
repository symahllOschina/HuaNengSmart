package com.huaneng.zhgd.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2017/11/11.
 */
@Table(name = "user")
public class User {

    @Column(name = "id", isId = true)
    public String userid;

    @Column(name = "markid")
    public String markid;

    @Column(name = "name")
    public String name;

    @Column(name = "mobile")
    public String mobile;

    @Column(name = "lanline")
    public String lanline;

    @Column(name = "wechat")
    public String wechat;

    @Column(name = "email")
    public String email;

    @Column(name = "images")
    public String images;

    @Column(name = "sex")
    public String sex;

    @Column(name = "company")
    public String company;

    @Column(name = "department")
    public String department;

    @Column(name = "trade")
    public String trade;

    @Column(name = "graduate")
    public String graduate;//毕业院校

    @Column(name = "major")
    public String major;//专业

    @Column(name = "typework")
    public String typework;//工种

    @Column(name = "certificate_no")
    public String certificate_no;//特种证书编号

    @Column(name = "blood_type")
    public String blood_type;//血型

    @Column(name = "job")
    public String job;//职务

    // 用于判断后台菜单权限是否更新
    public Integer version;
}
