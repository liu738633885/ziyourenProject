package com.huaqie.wubingjie.chat;

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
import com.huaqie.wubingjie.model.Friend;
import com.huaqie.wubingjie.model.event.EventRefresh;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;
import com.huaqie.wubingjie.utils.DensityUtil;
import com.huaqie.wubingjie.utils.SwlUtils;
import com.hyphenate.chatuidemo.db.InviteMessgeDao;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class NewFriendActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView rv;
    private NewFriendAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_new_friend;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewFriendAdapter();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swl);
        SwlUtils.initSwl(this,swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_view, (ViewGroup) rv.getParent(), false);
        adapter.setEmptyView(emptyView);
        rv.setAdapter(adapter);
        onRefresh();
        InviteMessgeDao dao = new InviteMessgeDao(this);
        dao.saveUnreadMessageCount(0);
    }

    private void callData() {
        swipeRefreshLayout.setRefreshing(true);
        NetBaseRequest netBaseRequest = RequsetFactory.creatBaseRequest(this, Constants.NEW_FRIEND);
        CallServer.getRequestInstance().addWithLogin(this, 0X01, netBaseRequest, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {

                if (netBaseBean.isSuccess()) {
                    List<Friend> list=netBaseBean.parseList(Friend.class);
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
        callData();
    }

    @Subscribe
    public void onEventMainThread(EventRefresh refresh) {
        if (refresh.getAction().equals(EventRefresh.ACTION_REFRESH) && refresh.getWhere().equals(NewFriendActivity.class.getSimpleName())) {
            onRefresh();
        }
    }
}

