package com.huaqie.wubingjie.model.order;

import java.util.List;

/**
 * Created by lewis on 2016/10/8.
 */

public class OrderList {
    private int pageno;
    private List<OrderInfo> list;

    public int getPageno() {
        return pageno;
    }

    public void setPageno(int pageno) {
        this.pageno = pageno;
    }

    public List<OrderInfo> getList() {
        return list;
    }

    public void setList(List<OrderInfo> list) {
        this.list = list;
    }
}
