package com.huaqie.wubingjie.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.activity.BaseActivity;
import com.huaqie.wubingjie.activity.LoginActivity;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;
import com.huaqie.wubingjie.utils.T;
import com.huaqie.wubingjie.utils.UserManager;
import com.huaqie.wubingjie.widgets.TitleBar;

public class FeedbackActivity extends BaseActivity {
    private EditText edt;
    private TitleBar titlebar;

    public static void goTo(Context context) {
        Intent intent;
        if (UserManager.isLogin()) {
            intent = new Intent(context, FeedbackActivity.class);
        } else {
            intent = new Intent(context, LoginActivity.class);
        }
        context.startActivity(intent);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titlebar = (TitleBar) findViewById(R.id.titlebar);
        titlebar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callUserFeedback();
            }
        });
        edt = (EditText) findViewById(R.id.edt);
    }

    private void callUserFeedback() {
        String content = edt.getText().toString();
        if (TextUtils.isEmpty(content)) {
            T.showShort(this, "反馈不能为空");
        }
        NetBaseRequest request = RequsetFactory.creatBaseRequest(this, Constants.USER_FEED_BACK);
        request.add("feedback_content", content);
        request.add("platform", 1);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                T.showShort(FeedbackActivity.this, netBaseBean.getMessage());
                if (netBaseBean.isSuccess()) {
                    showInfoDialog("已提交您的反馈,我们会尽快处理,感谢您的支持!", new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }
}
