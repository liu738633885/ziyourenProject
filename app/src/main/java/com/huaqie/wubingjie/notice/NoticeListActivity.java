package com.huaqie.wubingjie.notice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.activity.BaseActivity;
import com.huaqie.wubingjie.activity.LoginActivity;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.model.notice.Notice;
import com.huaqie.wubingjie.model.notice.NoticeList;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;
import com.huaqie.wubingjie.utils.SwlUtils;
import com.huaqie.wubingjie.utils.UserManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.utils.EaseCommonUtils;

import java.util.List;

public class NoticeListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{
    private RecyclerView rv;
    private NoticeAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    protected EMConversation conversation;
    public static void goTo(Context context) {
        Intent intent;
        if (UserManager.isLogin()) {
            intent = new Intent(context, NoticeListActivity.class);
        } else {
            intent = new Intent(context, LoginActivity.class);
        }
        context.startActivity(intent);
    }
    @Override
    protected int getContentViewId() {
        return R.layout.activity_notice_list;
    }
    @Override
    protected void initView(Bundle savedInstanceState) {
        rv = (RecyclerView)findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoticeAdapter(this);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swl);
        SwlUtils.initSwl(this,swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_view, (ViewGroup) rv.getParent(), false);
        adapter.setEmptyView(emptyView);
        rv.setAdapter(adapter);
        onRefresh();
        conversation = EMClient.getInstance().chatManager().getConversation(Constants.ADMIN_ID, EaseCommonUtils.getConversationType(EaseConstant.CHATTYPE_SINGLE), true);
        conversation.markAllMessagesAsRead();
    }
    @Override
    protected void onResume() {
        super.onResume();
        EaseUI.getInstance().getNotifier().reset();
    }

    private void callNotice() {
        swipeRefreshLayout.setRefreshing(true);
        NetBaseRequest netBaseRequest = RequsetFactory.creatBaseRequest(this, Constants.NOTICE_LIST);
        CallServer.getRequestInstance().addWithLogin(this, 0X01, netBaseRequest, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {

                if (netBaseBean.isSuccess()) {
                    NoticeList data=netBaseBean.parseObject(NoticeList.class);
                    List<Notice> list=data.getList();
                    if (list.size() > 0) {
                        adapter.setNewData(list);
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
        callNotice();
    }
}
