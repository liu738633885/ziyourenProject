package com.huaqie.wubingjie.model.user;

import java.util.List;

/**
 * Created by lewis on 2016/10/21.
 */

public class Account {
    private AccountInfo info;
    private List<AccountItem> list;

    public AccountInfo getInfo() {
        return info;
    }

    public void setInfo(AccountInfo info) {
        this.info = info;
    }

    public List<AccountItem> getList() {
        return list;
    }

    public void setList(List<AccountItem> list) {
        this.list = list;
    }
}
