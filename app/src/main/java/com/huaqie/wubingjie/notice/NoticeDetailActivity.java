package com.huaqie.wubingjie.notice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.activity.BaseActivity;
import com.huaqie.wubingjie.activity.CancelServeTaskActivity;
import com.huaqie.wubingjie.model.event.EventRefresh;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.model.notice.Notice;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;
import com.huaqie.wubingjie.utils.T;
import com.huaqie.wubingjie.utils.ViewUtils;
import com.hyphenate.easeui.controller.EaseUI;

import org.greenrobot.eventbus.Subscribe;

public class NoticeDetailActivity extends BaseActivity {
    private ImageView imv_avatar,imv_tag;
    private TextView tv_status,tv_nickname,tv_school_isstudent,tv_phone,tv_phonenum,tv_serve_task_title,tv_serve_task_content,tv_content,tv_message;
    private LinearLayout ll_task,ll_btns;
    private Button btn_refuse, btn_agree;
    private Notice notice;
    private String notice_id, order_id, order_is_agree;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_notice_detail;
    }

    public static void goTo(Context context, String notice_id) {
        Intent intent = new Intent(context, NoticeDetailActivity.class);
        intent.putExtra("notice_id", notice_id);
        context.startActivity(intent);
    }

    public static void goTo(Context context, String order_id, String order_is_agree) {
        Intent intent = new Intent(context, NoticeDetailActivity.class);
        intent.putExtra("order_id", order_id);
        intent.putExtra("order_is_agree", order_is_agree);
        context.startActivity(intent);
    }
    //获取Intent
    protected void handleIntent(Intent intent) {
        notice_id=intent.getStringExtra("notice_id");
        order_id = intent.getStringExtra("order_id");
        order_is_agree = intent.getStringExtra("order_is_agree");

    }
    @Override
    protected void onResume() {
        super.onResume();
        EaseUI.getInstance().getNotifier().reset();
    }
    @Override
    protected void initView(Bundle savedInstanceState) {
        notice=new Notice();
       /* if(TextUtils.isEmpty(notice_id)){
            T.showShort(this,"notice_id为 null");
            finish();
        }*/
        imv_avatar = (ImageView) findViewById(R.id.imv_avatar);
        imv_tag = (ImageView) findViewById(R.id.imv_tag);
        tv_nickname= (TextView) findViewById(R.id.tv_nickname);
        tv_school_isstudent = (TextView) findViewById(R.id.tv_school_isstudent);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_phonenum = (TextView) findViewById(R.id.tv_phonenum);
        tv_serve_task_title = (TextView) findViewById(R.id.tv_serve_task_title);
        tv_serve_task_content = (TextView) findViewById(R.id.tv_serve_task_content);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_message = (TextView) findViewById(R.id.tv_message);
        ll_task = (LinearLayout) findViewById(R.id.ll_task);
        ll_btns = (LinearLayout) findViewById(R.id.ll_btns);
        btn_refuse = (Button) findViewById(R.id.btn_refuse);
        btn_agree = (Button) findViewById(R.id.btn_agree);
        btn_refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CancelServeTaskActivity.goTo(NoticeDetailActivity.this, notice.getOrder_id(), CancelServeTaskActivity.REFUSED, NoticeDetailActivity.class.getSimpleName(), notice.getNotice_id());
            }
        });
        btn_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CancelServeTaskActivity.goTo(NoticeDetailActivity.this, notice.getOrder_id(), CancelServeTaskActivity.AGREE, NoticeDetailActivity.class.getSimpleName(), notice.getNotice_id());
                cancelServeTask(CancelServeTaskActivity.AGREE);
            }
        });
        noticeDetail();
    }

    private void updateUI() {
        if(notice==null){
            return;
        }
        ViewUtils.setAvatar(this, notice.getMember_avatar(), imv_avatar, notice.getUid());
        if (notice.getServe_task_type().equals("1")) {
            imv_tag.setImageResource(R.mipmap.icon_tag01);
        } else {
            imv_tag.setImageResource(R.mipmap.icon_tag02);
        }
        tv_nickname.setText(notice.getMember_nickname());
        if(!TextUtils.isEmpty(notice.getMember_is_student())&&!TextUtils.isEmpty(notice.getMember_school())){
            tv_school_isstudent.setVisibility(View.VISIBLE);
            String isstudent = "";
            if (notice.getMember_is_student().equals("1")) {
                isstudent = "在校生";
            } else if (notice.getMember_is_student().equals("2")) {
                isstudent = "毕业";
            } else {
                isstudent = "其他";
            }
            tv_school_isstudent.setText(notice.getMember_school() + "  " + isstudent);
        } else {
            tv_school_isstudent.setVisibility(View.GONE);
        }
       /* if (!TextUtils.isEmpty(notice.getMember_phone())) {
            tv_phonenum.setText(notice.getMember_phone());
        }*/
        ViewUtils.setPhone(this, tv_phonenum, "", notice.getMember_phone());
        if (!TextUtils.isEmpty(notice.getServe_task_title())) {
            tv_serve_task_title.setText(notice.getServe_task_title());
        }
        if (!TextUtils.isEmpty(notice.getServe_task_content())) {
            tv_serve_task_content.setText(notice.getServe_task_content());
        }
        if (!TextUtils.isEmpty(notice.getContent())) {
            tv_content.setText(notice.getContent());
        }
        tv_message.setText("");
        ll_task.setVisibility(View.VISIBLE);
        ll_btns.setVisibility(View.GONE);
        switch (notice.getOpen_function()){
            case "cancelTask":
                ll_btns.setVisibility(View.VISIBLE);
                tv_message.setText("温馨提示: 如果24小时不处理该条通知,系统会默认为同意");
                btn_refuse.setEnabled(notice.getCheck_status().equals("0"));
                btn_agree.setEnabled(notice.getCheck_status().equals("0"));
                break;
            case "refuseTask":
                tv_message.setText("温馨提示: 自由人建议双方进行电话沟通,友好协商");
                if(notice.getNotice_type().equals("4")){
                    tv_message.setText("温馨提示: 自由人建议双方进行电话沟通,友好协商");
                    ll_btns.setVisibility(View.GONE);
                    tv_status.setText("拒绝原因:");
                }
                break;
            case "noFunction":
                ll_task.setVisibility(View.GONE);
                tv_phone.setText("客服电话:");
                break;
        }



    }
    private void noticeDetail() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(this, Constants.NOTICE_DETAIL);

        if (!TextUtils.isEmpty(notice_id)) {
            request.add("notice_id", notice_id);
        } else {
            request.add("order_id", order_id);
            request.add("order_is_agree", order_is_agree);
        }
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()){
                    notice = netBaseBean.parseObject(Notice.class);
                    updateUI();
                } else {
                    T.showShort(NoticeDetailActivity.this, netBaseBean.getMessage());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, false, true);
    }

    private void cancelServeTask(String agree_type) {
        NetBaseRequest netBaseRequest = RequsetFactory.creatBaseRequest(this, Constants.CANCEL_SERVE_TASK);
        netBaseRequest.add("order_id", notice.getOrder_id());
        netBaseRequest.add("agree_type", agree_type);
        if (!TextUtils.isEmpty(notice_id)) {
            netBaseRequest.add("notice_id", notice_id);
        }
        CallServer.getRequestInstance().add(this, 0x01, netBaseRequest, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    noticeDetail();
                }
                    T.showShort(NoticeDetailActivity.this, netBaseBean.getMessage());

            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, false, true);
    }
    @Subscribe
    public void onEventMainThread(EventRefresh refresh) {
        if (refresh.getWhere().equals(NoticeDetailActivity.class.getSimpleName())) {
            noticeDetail();
        }
    }
}
