package com.huaqie.wubingjie.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;
import com.huaqie.wubingjie.utils.T;
import com.huaqie.wubingjie.utils.UserManager;
import com.huaqie.wubingjie.widgets.TitleBar;

public class ComplainActivity extends BaseActivity {
    private String type,serve_task_id;
    private TitleBar titleBar;
    private EditText edt;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_complain;
    }
    public static void goTo(Context context, String type, String serve_task_id) {
        Intent intent;
        if (UserManager.isLogin()) {
            intent = new Intent(context, ComplainActivity.class);
            intent.putExtra("type", type);
            intent.putExtra("serve_task_id", serve_task_id);
        } else {
            intent = new Intent(context, LoginActivity.class);
        }
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        type = intent.getStringExtra("type");
        serve_task_id = intent.getStringExtra("serve_task_id");
    }
    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titlebar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serveTaskComplain();
            }
        });
        edt = (EditText) findViewById(R.id.edt);
    }
    private void serveTaskComplain(){
        NetBaseRequest netBaseRequest = RequsetFactory.creatBaseRequest(this, Constants.SERVE_TASK_COMPLAIN);
        netBaseRequest.add("serve_task_id", serve_task_id);
        netBaseRequest.add("type", type);
       /* if (edt.getText().toString().length() < 5) {
            T.showShort(this, "最少5个字");
            return;
        }*/
        netBaseRequest.add("complain_content", edt.getText().toString());
        CallServer.getRequestInstance().add(this, 0x01, netBaseRequest, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                T.showShort(ComplainActivity.this,netBaseBean.getMessage());
                ComplainActivity.this.finish();
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, false, true);
    }
}
