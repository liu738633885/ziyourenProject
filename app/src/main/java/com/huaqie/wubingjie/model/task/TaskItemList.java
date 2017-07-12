package com.huaqie.wubingjie.model.task;

import java.util.List;

/**
 * Created by lewis on 2016/10/8.
 */

public class TaskItemList {
    private int pageno;
    private List<TaskItem> list;

    public int getPageno() {
        return pageno;
    }

    public void setPageno(int pageno) {
        this.pageno = pageno;
    }

    public List<TaskItem> getList() {
        return list;
    }

    public void setList(List<TaskItem> list) {
        this.list = list;
    }
}
