package com.huaqie.wubingjie.task;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.fragment.ScrollAbleFragment;
import com.huaqie.wubingjie.model.Comment;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.model.task.CommentList;
import com.huaqie.wubingjie.model.order.OrderInfo;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;

import java.util.List;

/**
 * Created by lewis on 2016/10/10.
 */

public class CommentOrderFragment extends ScrollAbleFragment implements BaseQuickAdapter.RequestLoadMoreListener {
    private RecyclerView rv;
    private CommentAdapter commentAdapter;
    private OrderAdapter orderAdapter;
    public static final String KEY_ID = "key_id";
    public static final String KEY_TYPE = "key_type";
    private String id;
    private String type;
    private int pagenum;
    private View notLoadingView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_serve_task_comment;
    }

    public static CommentOrderFragment newInstance(String id, String type) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ID, id);
        bundle.putString(KEY_TYPE, type);
        CommentOrderFragment fragment = new CommentOrderFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    protected void handleBundle(Bundle bundle) {
        try {
            id = bundle.getString(KEY_ID);
            type = bundle.getString(KEY_TYPE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        rv = (RecyclerView) view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        commentAdapter = new CommentAdapter();
        orderAdapter = new OrderAdapter();
        notLoadingView = LayoutInflater.from(getActivity()).inflate(R.layout.not_loading, (ViewGroup) rv.getParent(), false);
        commentAdapter.setOnLoadMoreListener(this);
        callComment(0);
        changeAdapter(0);

    }

    @Override
    public View getScrollableView() {
        return rv;
    }

    public void changeAdapter(int num) {
        if (num == 0) {
            rv.setAdapter(orderAdapter);
        } else if (num == 1) {
            rv.setAdapter(commentAdapter);
        }
    }

    public void callComment(int num) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(getActivity(), Constants.SERVE_TASK_COMMENT);
        request.add("serve_task_id", id);
        request.add("type", type);
        request.add("pageno", num);
        CallServer.getRequestInstance().add(getActivity(), num, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    CommentList data = netBaseBean.parseObject(CommentList.class);
                    List<Comment> list = data.getList();
                    pagenum = data.getPageno();
                    boolean isnodata = data.getPageno() == -1 | data.getPageno() == 0;
                    //添加没有更多 view
                    commentAdapter.removeAllFooterView();
                    if (isnodata) {
                        commentAdapter.addFooterView(notLoadingView);
                    }
                    if (what == 0) {
                        commentAdapter.setNewData(list);
                        commentAdapter.openLoadMore(list.size(), !isnodata);
                    } else {
                        commentAdapter.notifyDataChangedAfterLoadMore(list, !isnodata);
                    }
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, false);
    }

    @Override
    public void onLoadMoreRequested() {
        callComment(pagenum);
    }

    public void setOrderData(List<OrderInfo> orderData) {
        orderAdapter.setNewData(orderData);
    }
}
