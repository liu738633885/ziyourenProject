package com.huaqie.wubingjie.task;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.activity.BaseActivity;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.model.order.OrderInfo;
import com.huaqie.wubingjie.model.order.OrderList;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;
import com.huaqie.wubingjie.utils.SwlUtils;
import com.huaqie.wubingjie.utils.ViewUtils;
import com.lewis.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class PastTaskActivity extends BaseActivity implements  SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private RecyclerView rv;
    private int pagenum;
    private Adapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View notLoadingView;
    private String user_id;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_past_task;
    }

    public static void goTo(Context context, String user_id) {
        Intent intent=new Intent(context,PastTaskActivity.class);
        intent.putExtra("user_id", user_id);
        context.startActivity(intent);
    }

    //获取Intent
    protected void handleIntent(Intent intent) {
        user_id=intent.getStringExtra("user_id");
    }
    @Override
    protected void initView(Bundle savedInstanceState) {
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        notLoadingView = LayoutInflater.from(this).inflate(R.layout.not_loading, rv, false);
        adapter = new Adapter();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swl);
        SwlUtils.initSwl(this, swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        adapter.setOnLoadMoreListener(this);
        View emptyView = this.getLayoutInflater().inflate(R.layout.empty_view, (ViewGroup) rv.getParent(), false);
        adapter.setEmptyView(emptyView);
        rv.setAdapter(adapter);
        onRefresh();
    }

    private void callOrder(int num) {
        swipeRefreshLayout.setRefreshing(true);
        NetBaseRequest netBaseRequest = RequsetFactory.creatBaseRequest(this, Constants.PAST_TASK);
        netBaseRequest.add("pageno", num);
        netBaseRequest.add("user_id", user_id);
        CallServer.getRequestInstance().addWithLogin(this, num, netBaseRequest, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {

                if (netBaseBean.isSuccess()) {
                    OrderList data = netBaseBean.parseObject(OrderList.class);
                    List<OrderInfo> list = data.getList();
                    pagenum = data.getPageno();
                    boolean isnodata = data.getPageno() == -1 | data.getPageno() == 0;
                    //添加没有更多 view
                    adapter.removeAllFooterView();
                    if (isnodata) {
                        adapter.addFooterView(notLoadingView);
                    }
                    if (what == 0) {
                        if (list != null && list.size() > 0) {
                            adapter.setNewData(list);
                            adapter.openLoadMore(list.size(), !isnodata);
                        } else {
                            adapter.openLoadMore(false);
                        }
                    } else {
                        if (list != null && list.size() > 0) {
                            adapter.notifyDataChangedAfterLoadMore(list, !isnodata);
                        } else {
                            adapter.notifyDataChangedAfterLoadMore(false);
                        }
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, true, false);

    }

    @Override
    public void onRefresh() {
        callOrder(0);
    }

    @Override
    public void onLoadMoreRequested() {
        callOrder(pagenum);

    }
    public class Adapter extends BaseQuickAdapter<OrderInfo> {

        public Adapter() {
            super(R.layout.item_order2, new ArrayList<OrderInfo>());
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, final OrderInfo orderInfo) {
            ImageView imv_avatar=baseViewHolder.getView(R.id.imv_avatar);
            ViewUtils.setAvatar(mContext, orderInfo.getMember_avatar(), imv_avatar, orderInfo.getUid());
            baseViewHolder.setText(R.id.tv_nickname,orderInfo.getMember_nickname());
            if(!TextUtils.isEmpty(orderInfo.getOrder_finish_status())){
                switch (orderInfo.getOrder_finish_status()){
                    case "0":
                        baseViewHolder.setText(R.id.tv_status,"进行中   ");
                        break;
                    case "1":
                        baseViewHolder.setText(R.id.tv_status,"已完成   ");
                        break;
                    case "2":
                        baseViewHolder.setText(R.id.tv_status,"已结单   ");
                        break;
                }
            }
            if(!TextUtils.isEmpty(orderInfo.getServe_task_title())){
                baseViewHolder.setText(R.id.tv_title,orderInfo.getServe_task_title());
            }
            if (!TextUtils.isEmpty(orderInfo.getCreate_time())) {
                baseViewHolder.setText(R.id.tv_time, DateUtils.translateDate3(orderInfo.getCreate_time()));
            } else {
                baseViewHolder.setText(R.id.tv_time, "");
            }

            baseViewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ServeTaskDetailActivity2.goTo(mContext, orderInfo.getServe_task_id(), orderInfo.getType());
                }
            });
        }
    }
}
