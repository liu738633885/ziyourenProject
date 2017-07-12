package com.huaqie.wubingjie.task;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.activity.CancelServeTaskActivity;
import com.huaqie.wubingjie.activity.FinishActivity;
import com.huaqie.wubingjie.model.order.OrderInfo;
import com.huaqie.wubingjie.notice.NoticeDetailActivity;
import com.huaqie.wubingjie.utils.ViewUtils;

import java.util.ArrayList;

/**
 * Created by lewis on 2016/10/10.
 */

public class OrderAdapter extends BaseQuickAdapter<OrderInfo> {
    public OrderAdapter() {
        super(R.layout.item_order, new ArrayList<OrderInfo>());
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, final OrderInfo orderInfo) {
        ImageView imv_avatar = baseViewHolder.getView(R.id.imv_avatar);
        ViewUtils.setAvatar(mContext, orderInfo.getMember_avatar(), imv_avatar,orderInfo.getUid());
        baseViewHolder.setText(R.id.tv_nickname, orderInfo.getMember_nickname());
        String isstudent = "";
        if (orderInfo.getMember_is_student().equals("1")) {
            isstudent = "在校生";
        } else if (orderInfo.getMember_is_student().equals("2")) {
            isstudent = "毕业";
        } else {
            isstudent = "其他";
        }
        baseViewHolder.setText(R.id.tv_school_isstudent, orderInfo.getMember_school() + "   " + isstudent);
        //baseViewHolder.setText(R.id.tv_phonenum,orderInfo.getMember_phone());
        ViewUtils.setPhone(mContext, (TextView) baseViewHolder.getView(R.id.tv_phonenum), "", orderInfo.getMember_phone());
        Button btn_finish = baseViewHolder.getView(R.id.btn_finish);
        Button btn_cancel = baseViewHolder.getView(R.id.btn_cancel);
        btn_finish.setText(orderInfo.getFinish_msg());
        btn_cancel.setText(orderInfo.getOrder_agree_msg());
        if(!TextUtils.isEmpty(orderInfo.getOrder_finish_status())){
            btn_finish.setVisibility(View.VISIBLE);
            switch (orderInfo.getOrder_finish_status()) {
                case "0":
                    btn_finish.setEnabled(false);
                    break;
                case "1":
                    btn_finish.setEnabled(true);
                    btn_finish.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FinishActivity.goTo(mContext, orderInfo.getOrder_id(), orderInfo.getFinish_msg(), ServeTaskDetailActivity.class.getSimpleName());
                        }
                    });
                    break;
                case "2":
                    btn_finish.setEnabled(false);
                    break;
            }
        }else {
            btn_finish.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(orderInfo.getOrder_is_agree())){
            btn_cancel.setVisibility(View.VISIBLE);
            switch (orderInfo.getOrder_is_agree()) {
                case "0":
                    btn_cancel.setVisibility(View.GONE);
                    break;
                case "1":
                    btn_cancel.setVisibility(View.VISIBLE);
                    btn_cancel.setEnabled(true);
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CancelServeTaskActivity.goTo(mContext, orderInfo.getOrder_id(),CancelServeTaskActivity.CANCEL, ServeTaskDetailActivity.class.getSimpleName());
                    }
                    });
                    break;
                case "2":
                    btn_cancel.setVisibility(View.VISIBLE);
                    btn_cancel.setEnabled(false);
                    break;
                case "3":
                    btn_cancel.setVisibility(View.VISIBLE);
                    btn_cancel.setEnabled(false);
                    break;
                case "4":
                    btn_cancel.setVisibility(View.VISIBLE);
                    btn_cancel.setEnabled(false);
                    break;
            }
        }else {
            btn_cancel.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(orderInfo.getIs_click_agree())&&orderInfo.getIs_click_agree().equals("1")){
            btn_cancel.setEnabled(true);
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //可以跳转
                    NoticeDetailActivity.goTo(mContext, orderInfo.getOrder_id(), orderInfo.getOrder_is_agree());
                }
            });
        } else {

        }


    }
}
