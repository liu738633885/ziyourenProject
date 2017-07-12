package com.huaqie.wubingjie.chat;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.model.Friend;
import com.huaqie.wubingjie.model.event.EventRefresh;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;
import com.huaqie.wubingjie.utils.T;
import com.huaqie.wubingjie.utils.ViewUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;


/**
 * Created by lewis on 2016/10/30.
 */

public class NewFriendAdapter extends BaseQuickAdapter<Friend> {
    public NewFriendAdapter() {
        super(R.layout.item_friend, new ArrayList<Friend>());
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, final Friend friend) {
        ImageView imv_avatar = baseViewHolder.getView(R.id.imv_avatar);
        ViewUtils.setAvatar(mContext, friend.getMember_avatar(), imv_avatar, friend.getTo_uid());
        if (!TextUtils.isEmpty(friend.getMember_nickname())) {
            baseViewHolder.setText(R.id.tv_nickname, friend.getMember_nickname());
        }
        if (!TextUtils.isEmpty(friend.getContent())) {
            baseViewHolder.setText(R.id.tv_message, friend.getContent());

        }
        Button btn = baseViewHolder.getView(R.id.btn);
        btn.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(friend.getMessage())) {
            btn.setText(friend.getMessage());
        }
        btn.setEnabled(true);
        if (!TextUtils.isEmpty(friend.getStatus())) {
            switch (friend.getStatus()){
                case "0":
                    btn.setEnabled(false);
                    break;
                case "1":
                    btn.setEnabled(false);
                    break;
                case "2":
                    btn.setEnabled(true);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            acceptInvitation(friend.getTo_uid());
                        }
                    });
                    break;
                default:
                    btn.setVisibility(View.INVISIBLE);
            }
        }


    }

    private void acceptInvitation(final String id) {
        NetBaseRequest request= RequsetFactory.creatBaseRequest(mContext, Constants.CHECK_FRIEND);
        request.add("friend_id",id);
        request.add("check_type", 1);
        CallServer.getRequestInstance().add(mContext, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    try {
                        EMClient.getInstance().contactManager().acceptInvitation(id);
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                    EventBus.getDefault().post(new EventRefresh(EventRefresh.ACTION_REFRESH, null, NewFriendActivity.class.getSimpleName()));
                }
                T.showShort(mContext, netBaseBean.getMessage());
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, false, true);


    }
}
