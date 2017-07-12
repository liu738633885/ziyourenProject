package com.huaqie.wubingjie.model.task;

import com.huaqie.wubingjie.model.Comment;

import java.util.List;

/**
 * Created by lewis on 2016/10/11.
 */

public class CommentList {
    private int pageno;
    private List<Comment> list;

    public int getPageno() {
        return pageno;
    }

    public void setPageno(int pageno) {
        this.pageno = pageno;
    }

    public List<Comment> getList() {
        return list;
    }

    public void setList(List<Comment> list) {
        this.list = list;
    }
}
