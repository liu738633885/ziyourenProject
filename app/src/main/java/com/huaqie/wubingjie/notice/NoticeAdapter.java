package com.huaqie.wubingjie.notice;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.activity.BaseActivity;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.model.notice.Notice;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;
import com.huaqie.wubingjie.task.ServeTaskDetailActivity2;
import com.huaqie.wubingjie.utils.T;
import com.huaqie.wubingjie.utils.ViewUtils;
import com.lewis.utils.DateUtils;

import java.util.ArrayList;

/**
 * Created by lewis on 2016/10/30.
 */

public class NoticeAdapter extends BaseQuickAdapter<Notice> {
    private Context context;
    public NoticeAdapter(Context context) {
        super(R.layout.item_notice, new ArrayList<Notice>());
        this.context = context;
    }

    @Override
    protected void convert(final BaseViewHolder baseViewHolder, final Notice notice) {
        ImageView imv_avatar = baseViewHolder.getView(R.id.imv_avatar);
        //ViewUtils.setAvatar(mContext, notice.getMember_avatar(), imv_avatar, notice.getUid());
        ViewUtils.setAvatar(mContext, notice.getMember_avatar(), imv_avatar, "");
        if (!TextUtils.isEmpty(notice.getMember_nickname())) {
            baseViewHolder.setText(R.id.tv_nickname, notice.getMember_nickname());
        }
        if (!TextUtils.isEmpty(notice.getNotice_msg())) {
            baseViewHolder.setText(R.id.tv_status, notice.getNotice_msg());

        } else {
            baseViewHolder.setText(R.id.tv_status, "");
        }
        if (!TextUtils.isEmpty(notice.getServe_task_title())) {
            baseViewHolder.setText(R.id.tv_title, notice.getServe_task_title());
        }
        if (!TextUtils.isEmpty(notice.getCreate_time())) {
            baseViewHolder.setText(R.id.tv_time, DateUtils.translateDate3(notice.getCreate_time()));
        } else {
            baseViewHolder.setText(R.id.tv_time, "");
        }
        baseViewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (notice.getOpen_function()){
                    case "serveTaskDetail":
                        ServeTaskDetailActivity2.goTo(mContext, notice.getServe_task_id(), notice.getServe_task_type());
                        break;
                    default:
                        NoticeDetailActivity.goTo(mContext,notice.getNotice_id());
                }
                readNotice(notice.getNotice_id());
                notice.setIs_read("1");
                notifyItemChanged(baseViewHolder.getAdapterPosition());
            }
        });
        baseViewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ((BaseActivity) context).showWarningDialog("是否删除该条通知?", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        delNotice(notice.getNotice_id(), baseViewHolder.getAdapterPosition());
                        ((BaseActivity) context).dialog.myDismiss();
                    }
                });
                return true;
            }
        });
        TextView tv_is_read=baseViewHolder.getView(R.id.tv_is_read);
        if(!TextUtils.isEmpty(notice.getIs_read())&&notice.getIs_read().equals("0")){
            //未读
            tv_is_read.setText("未读");
            tv_is_read.setTextColor(ContextCompat.getColor(mContext, R.color.super_green));
        } else {
            //已读
            tv_is_read.setText("已读");
            tv_is_read.setTextColor(ContextCompat.getColor(mContext, R.color.gray09));
        }
    }
    private void readNotice(String noticeId){
        NetBaseRequest request= RequsetFactory.creatBaseRequest(context, Constants.READ_NOTICE);
        request.add("notice_id",noticeId);
        CallServer.getRequestInstance().add(context, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {

            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, false, false);
    }

    private void delNotice(String noticeId, final int position) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(context, Constants.DEL_NOTICE);
        request.add("notice_id",noticeId);
        CallServer.getRequestInstance().add(context, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    remove(position);
                }
                T.showShort(context, netBaseBean.getMessage());
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, false, true);
    }
}
