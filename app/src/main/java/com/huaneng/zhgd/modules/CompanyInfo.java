package com.huaneng.zhgd.modules;

import com.huaneng.zhgd.bean.Article;

import java.util.ArrayList;
import java.util.List;

/**
 * 公司，不包含联系人
 */

public class CompanyInfo {

    public String id;
    public String pid;
    public String level;
    public String name;
    public String address;
    public String mobile;
    public String landline;
    public String create_time;
    public String update_time;

    public List<Article> projects;

    public void addProject(Article project) {
        if (projects == null) {
            projects = new ArrayList<>();
        }
        projects.add(project);
    }

}
