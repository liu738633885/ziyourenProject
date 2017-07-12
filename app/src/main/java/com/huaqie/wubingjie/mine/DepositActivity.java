package com.huaqie.wubingjie.mine;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.activity.BaseActivity;
import com.huaqie.wubingjie.model.event.EventRefresh;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;
import com.huaqie.wubingjie.setting.AccountSecurityActivity;
import com.huaqie.wubingjie.utils.MD5Util;
import com.huaqie.wubingjie.utils.T;
import com.huaqie.wubingjie.utils.UserManager;

import org.greenrobot.eventbus.EventBus;

public class DepositActivity extends BaseActivity {
    private EditText edt_deposit_money,edt_deposit_account,edt_password;
    private Button btn_deposit;
    private RadioGroup rg;
    int pay_type;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_deposit;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        edt_deposit_money= (EditText) findViewById(R.id.edt_deposit_money);
        edt_deposit_account= (EditText) findViewById(R.id.edt_deposit_account);
        edt_password= (EditText) findViewById(R.id.edt_password);

        btn_deposit= (Button) findViewById(R.id.btn_deposit);
        btn_deposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userDeposit();
            }
        });
        rg= (RadioGroup) findViewById(R.id.rg);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rb_alipay:
                        pay_type=2;
                        break;
                    case R.id.rb_wechat:
                        pay_type=3;
                        break;
                    default:
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(UserManager.getAlipay())) {
            showWarningDialog("您没有绑定支付宝,请先绑定支付宝!", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AccountSecurityActivity.goTo(DepositActivity.this);
                    dialog.myDismiss();
                }
            });
            btn_deposit.setEnabled(false);
        }else {
            edt_deposit_account.setText(UserManager.getAlipay());
            btn_deposit.setEnabled(true);
        }
    }

    private void userDeposit() {
        NetBaseRequest request= RequsetFactory.creatBaseRequest(this, Constants.USER_DEPOSIT);
        if(TextUtils.isEmpty(edt_deposit_money.getText().toString())){
            T.showShort(this, "请输入金额");
            return;
        }
        double money;
        try {
            money=Double.parseDouble(edt_deposit_money.getText().toString());
            if(money<0){
                T.showShort(this, "请输入大于0的金额");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            T.showShort(this, "请输入正确金额");
            return;
        }
        request.add("deposit_money", money);
       /* if(pay_type==0){
            T.showShort(this, "请选择提现方式");
            return;
        }*/
        request.add("pay_type", 2);
        if(TextUtils.isEmpty(edt_deposit_account.getText().toString())){
            T.showShort(this, "请输入支付宝号");
            return;
        }
        request.add("deposit_account",edt_deposit_account.getText().toString());
        if(TextUtils.isEmpty(edt_deposit_account.getText().toString())){
            T.showShort(this, "请输入登录密码");
            return;
        }
        request.add("password", MD5Util.MD5String(edt_password.getText().toString()));
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if(netBaseBean.isSuccess()){
                    finish();
                    EventBus.getDefault().post(new EventRefresh(EventRefresh.ACTION_REFRESH,null,AccountActivity.class.getSimpleName()));
                }
                T.showShort(DepositActivity.this,netBaseBean.getMessage());
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

}
