package com.huaqie.wubingjie.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.MainApplication;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;
import com.huaqie.wubingjie.utils.SettingsManager;
import com.huaqie.wubingjie.utils.SystemUtils;
import com.huaqie.wubingjie.utils.UserManager;
import com.hyphenate.chat.EMClient;

public class SplashActivity extends BaseActivity {
    private TextView tv_info;
    private Handler handler = new Handler();

    @Override
    protected int getContentViewId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        tv_info = (TextView) findViewById(R.id.tv_info);
        tv_info.setText("版本:" + SystemUtils.getAppVersionName(this) + (MainApplication.getInstance().getResources().getInteger(R.integer.HTTP_CONFIG) == 2 ? "测试服务器" : ""));
        if (SettingsManager.isNewCodeFirst()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                   startActivity(new Intent(SplashActivity.this,WelcomeActivity.class));
                }
            }, 1500);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 2500);
        } else {
            openMainActivity();
        }
    }
    private void openMainActivity() {
        if (UserManager.isLogin()) {
            NetBaseRequest getUserRequest = RequsetFactory.creatNoUidRequest(Constants.GET_USER_INFO);
            getUserRequest.add("uid", UserManager.getUid());
            CallServer.getRequestInstance().addWithLogin(this, 0x01, getUserRequest, new HttpListenerCallback() {
                @Override
                public void onSucceed(int what, NetBaseBean netBaseBean) {
                    if (netBaseBean.getStatus() == 4001) {
                        UserManager.logout();
                    } else {
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                    }
                    openMainActivityWait(1000);
                }

                @Override
                public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                    openMainActivityWait(800);
                }
            }, true, false);
        } else {
            openMainActivityWait(1500);
            EMClient.getInstance().groupManager().loadAllGroups();
            EMClient.getInstance().chatManager().loadAllConversations();
        }

    }

    private void openMainActivityWait(int millis) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        }, millis);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, millis+1000);
    }
}
