package com.huaqie.wubingjie.order;

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
import com.huaqie.wubingjie.fragment.BaseFragment;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.model.order.OrderInfo;
import com.huaqie.wubingjie.model.order.OrderList;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;
import com.huaqie.wubingjie.task.ServeTaskDetailActivity2;
import com.huaqie.wubingjie.utils.SwlUtils;
import com.huaqie.wubingjie.utils.ViewUtils;
import com.lewis.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lewis on 2016/10/16.
 */

public class MyOrderFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private RecyclerView rv;
    private int pagenum;
    private Adapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String netMode;
    private View notLoadingView;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_only_rv;
    }

    public static MyOrderFragment newInstance(String netmode) {
        MyOrderFragment fragment = new MyOrderFragment();
        Bundle bundle = new Bundle();
        bundle.putString("netMode", netmode);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        netMode = getArguments().getString("netMode");
        rv = (RecyclerView) view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        notLoadingView = LayoutInflater.from(getActivity()).inflate(R.layout.not_loading, rv, false);
        adapter = new Adapter();
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swl);
        SwlUtils.initSwl(getActivity(),swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        adapter.setOnLoadMoreListener(this);
        View emptyView = getActivity().getLayoutInflater().inflate(R.layout.empty_view, (ViewGroup) rv.getParent(), false);
        adapter.setEmptyView(emptyView);
        rv.setAdapter(adapter);
        onRefresh();
    }

    private void callOrder(int num) {
        swipeRefreshLayout.setRefreshing(true);
        NetBaseRequest netBaseRequest = RequsetFactory.creatBaseRequest(getActivity(), Constants.MY_ORDER);
        netBaseRequest.add("pageno", num);
        netBaseRequest.add("type", netMode);
        CallServer.getRequestInstance().addWithLogin(getActivity(), num, netBaseRequest, new HttpListenerCallback() {
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
            //ViewUtils.setAvatar(mContext, orderInfo.getMember_avatar(), imv_avatar, orderInfo.getUid());
            ViewUtils.setAvatar(mContext, orderInfo.getMember_avatar(), imv_avatar, "");
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
