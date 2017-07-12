package com.huaqie.wubingjie.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.model.event.EventRefresh;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;
import com.huaqie.wubingjie.utils.T;
import com.huaqie.wubingjie.utils.UserManager;
import com.huaqie.wubingjie.widgets.TitleBar;

import org.greenrobot.eventbus.EventBus;

public class CancelServeTaskActivity extends  BaseActivity {
    private String order_id;
    private TitleBar titleBar;
    private EditText edt;
    public static final String CANCEL = "2";
    public static final String REFUSED = "3";
    public static final String AGREE = "4";
    private String agree_type;
    private String notice_id;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_cancel_serve_task;
    }

    public static void goTo(Context context, String order_id, String agree_type, String where) {
        goTo(context, order_id, agree_type, where, "");
    }
    public static void goTo(Context context, String order_id, String agree_type, String where, String notice_id) {
        Intent intent;
        if (UserManager.isLogin()) {
            intent = new Intent(context, CancelServeTaskActivity.class);
            intent.putExtra("order_id", order_id);
            intent.putExtra("agree_type", agree_type);
            intent.putExtra("notice_id", notice_id);
            intent.putExtra(KEY_WHERE, where);
        } else {
            intent = new Intent(context, LoginActivity.class);
        }
        context.startActivity(intent);
    }
    protected void handleIntent(Intent intent) {
        order_id = intent.getStringExtra("order_id");
        agree_type = intent.getStringExtra("agree_type");
        where = intent.getStringExtra(KEY_WHERE);
        notice_id = intent.getStringExtra("notice_id");
    }
    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titlebar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelServeTask();
            }
        });
        edt = (EditText) findViewById(R.id.edt);
        if (TextUtils.isEmpty(order_id)) {
            T.showShort(this, "order_id为 null");
            finish();
        }
        switch (agree_type){
            case CANCEL:
                edt.setHint("请简要描述你取消任务的原因");
                titleBar.setCenterText("取消任务");
                break;
            case REFUSED:
                edt.setHint("请简要描述你拒绝任务的原因");
                titleBar.setCenterText("拒绝任务");
                break;
            case AGREE:
                edt.setHint("同意无需上传同意原因");
                edt.setEnabled(false);
                titleBar.setCenterText("同意任务");
                break;
        }
    }
    private void cancelServeTask(){
        NetBaseRequest netBaseRequest = RequsetFactory.creatBaseRequest(this, Constants.CANCEL_SERVE_TASK);
        netBaseRequest.add("order_id", order_id);
        netBaseRequest.add("agree_type", agree_type);
        /*if (edt.getText().toString().length() < 5) {
            T.showShort(this, "最少5个字");
            return;
        }*/
        netBaseRequest.add("content", edt.getText().toString());
        if(!TextUtils.isEmpty(notice_id)){
            netBaseRequest.add("notice_id", notice_id);
        }
        CallServer.getRequestInstance().add(this, 0x01, netBaseRequest, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    EventBus.getDefault().post(new EventRefresh(EventRefresh.ACTION_REFRESH, null, where));
                    finish();
                    T.showShort(CancelServeTaskActivity.this, "已发送");
                }else {
                    T.showShort(CancelServeTaskActivity.this, netBaseBean.getMessage());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, false, true);
    }
}
