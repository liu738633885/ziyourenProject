package com.huaqie.wubingjie.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.activity.BaseActivity;
import com.huaqie.wubingjie.activity.LoginActivity;
import com.huaqie.wubingjie.model.event.EventRefresh;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.model.user.Account;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;
import com.huaqie.wubingjie.utils.SwlUtils;
import com.huaqie.wubingjie.utils.UserManager;

import org.greenrobot.eventbus.Subscribe;

public class AccountActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView rv;
    private AccountAdapter adapter;
    private TextView tv_surplus_money,tv_all_money,tv_pay_money;
    private Button btn_deposit;
    private SwipeRefreshLayout swl;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_account;
    }

    public static void goTo(Context context) {
        Intent intent;
        if (UserManager.isLogin()) {
            intent = new Intent(context, AccountActivity.class);
        } else {
            intent = new Intent(context, LoginActivity.class);
        }
        context.startActivity(intent);
    }
    @Override
    protected void initView(Bundle savedInstanceState) {
        swl = (SwipeRefreshLayout) findViewById(R.id.swl);
        SwlUtils.initSwl(this,swl);
        swl.setOnRefreshListener(this);
        rv= (RecyclerView) findViewById(R.id.rv);
        adapter=new AccountAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        View headview = LayoutInflater.from(this).inflate(R.layout.headview_account, rv, false);
        adapter.addHeaderView(headview);
        tv_surplus_money= (TextView) headview.findViewById(R.id.tv_surplus_money);
        tv_all_money = (TextView) headview.findViewById(R.id.tv_all_money);
        tv_pay_money = (TextView) headview.findViewById(R.id.tv_pay_money);
        btn_deposit= (Button) headview.findViewById(R.id.btn_deposit);
        btn_deposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountActivity.this, DepositActivity.class));
            }
        });
        getUserAccount();
    }

    private void getUserAccount() {
        swl.setRefreshing(true);
        NetBaseRequest request = RequsetFactory.creatBaseRequest(this, Constants.GET_USER_ACCOUNT);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    Account account = netBaseBean.parseObject(Account.class);
                    if (account.getInfo()!=null){
                        if(!TextUtils.isEmpty(account.getInfo().getMember_surplus_money())){
                            tv_surplus_money.setText("余额:  " + account.getInfo().getMember_surplus_money() + "元");
                        }
                        if(!TextUtils.isEmpty(account.getInfo().getMember_all_money())){
                            tv_all_money.setText("总收入:  " + account.getInfo().getMember_all_money() + "元");
                        }
                        if(!TextUtils.isEmpty(account.getInfo().getMember_pay_money())){
                            tv_pay_money.setText("总支出:  " + account.getInfo().getMember_pay_money() + "元");
                        }
                    }
                    adapter.setNewData(account.getList());
                }
                swl.setRefreshing(false);
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                swl.setRefreshing(false);
            }
        }, true, false);
    }

    @Override
    public void onRefresh() {
        getUserAccount();
    }

    @Subscribe
    public void onEventMainThread(EventRefresh refresh) {
        if (refresh.getWhere().equals(AccountActivity.class.getSimpleName())) {
            onRefresh();
        }
    }
}
