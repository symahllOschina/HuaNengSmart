package com.huaneng.zhgd.modules;

import java.util.ArrayList;
import java.util.List;

/**
 * 公司，包含联系人
 */

public class Company {

    public String name;

    public List<Contact> list;

    public void addContact(Contact contact) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(contact);
    }
}
