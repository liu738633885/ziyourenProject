/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.chatuidemo.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.activity.BaseActivity;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.model.user.UserInfo;
import com.huaqie.wubingjie.model.user.UserInfoList;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;
import com.huaqie.wubingjie.utils.T;
import com.huaqie.wubingjie.utils.imageloader.ImageLoader;
import com.huaqie.wubingjie.widgets.TitleBar;
import com.huaqie.wubingjie.widgets.dialog.BottomDialog;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;


public class AddContactActivity extends BaseActivity {
    private EditText editText;
    private LinearLayout searchedUserLayout;
    private TextView nameText, mTextView,indicator;
    private Button addBtn;
    private ImageView avatar;
    private String toAddUsername;
    private String to_uid;
    private TitleBar titleBar;
    private BottomDialog bottomDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.em_activity_add_contact;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        bottomDialog = new BottomDialog(this);
        titleBar = (TitleBar) findViewById(R.id.titlebar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchContact();
            }
        });
        editText = (EditText) findViewById(R.id.edit_note);
        searchedUserLayout = (LinearLayout) findViewById(R.id.ll_user);
        nameText = (TextView) findViewById(R.id.name);
        avatar = (ImageView) findViewById(R.id.avatar);
        addBtn = (Button) findViewById(R.id.indicator);
        indicator= (TextView) findViewById(R.id.indicator);
        indicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EMClient.getInstance().getCurrentUser().equals(to_uid)) {
                    //new EaseAlertDialog(this, R.string.not_add_myself).show();
                    showErrorDialog("不能添加自己");
                    return;
                }
                chooseText();
            }
        });
    }

    /**
     * 查找contact
     *
     * @param
     */
    public void searchContact() {
        toAddUsername = editText.getText().toString();
        String imageUrl = null;
        if (toAddUsername.length()!=11) {
            new EaseAlertDialog(this, R.string.Please_enter_a_username).show();
            return;
        }

        // TODO 从服务器获取此contact,如果不存在提示不存在此用户
        NetBaseRequest getUserRequest = RequsetFactory.creatBaseRequest(this, Constants.SEARCH_FRIEND);
        getUserRequest.add("member_phone", toAddUsername);
        CallServer.getRequestInstance().add(this, 0x01, getUserRequest, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if(netBaseBean.isSuccess()){
                    UserInfoList data =netBaseBean.parseObject(UserInfoList.class);
                    List<UserInfo> list=data.getList();
                    if(list!=null&&list.size()>0){
                        toAddUsername = list.get(0).getMember_nickname();
                        to_uid = list.get(0).getUid();
                        searchedUserLayout.setVisibility(View.VISIBLE);
                        nameText.setText(toAddUsername);
                        ImageLoader.loadHeadImage(AddContactActivity.this, list.get(0).getMember_avatar(), avatar, 0);
                       /* if (list.get(0).getFriend_flag().contains("1")) {
                            addBtn.setText("已添加");
                            addBtn.setClickable(false);
                        }*/
                    }else {
                        T.showShort(AddContactActivity.this, "没有查找到用户");
                    }
                }else{
                    T.showShort(AddContactActivity.this, netBaseBean.getMessage());
                }
            }

         @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }
    private void chooseText() {
        bottomDialog.setContentView(R.layout.dialog_edittext, true);
        TitleBar titleBar = (TitleBar) bottomDialog.findViewById(R.id.titlebar);
        titleBar.setMode(TitleBar.MODE_BACK_TV);
        titleBar.setCenterText("打个招呼");
        titleBar.setRightText("确认");
        final EditText edt = (EditText) bottomDialog.findViewById(R.id.edt);
        edt.setText("加个好友吧");
        titleBar.setLeftClike(new TitleBar.LeftClike() {
            @Override
            public void onClick(View view) {
                bottomDialog.myDismiss();
            }
        });
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = edt.getText().toString();
                addContact(content);
                bottomDialog.myDismiss();

            }
        });
        bottomDialog.show();
    }

    /**
     * 添加contact
     *
     * @param view
     */
    public void addContact(final String content) {


        if (DemoHelper.getInstance().getContactList().containsKey(to_uid)) {
            //let the user know the contact already in your contact list
            if (EMClient.getInstance().contactManager().getBlackListUsernames().contains(nameText.getText().toString())) {
                new EaseAlertDialog(this, R.string.user_already_in_contactlist).show();
                return;
            }
            new EaseAlertDialog(this, R.string.This_user_is_already_your_friend).show();
            return;
        }
        try {
            // 向服务器发送添加好友的请求
            NetBaseRequest getUserRequest = RequsetFactory.creatBaseRequest(AddContactActivity.this, Constants.ADD_FRIEND);
            getUserRequest.add("to_uid", to_uid);
            getUserRequest.add("content", content);
            //getUserRequest.add(ChatConstants.SOURCE_TYPE, 1);
            CallServer.getRequestInstance().add(AddContactActivity.this, 0x01, getUserRequest, new HttpListenerCallback() {
                @Override
                public void onSucceed(int what, NetBaseBean netBaseBean) {
                    if (netBaseBean.isSuccess()) {
                        String s1 = getResources().getString(R.string.send_successful);
                        T.showShort(AddContactActivity.this, s1);
                        try {
                            EMClient.getInstance().contactManager().addContact(to_uid, content);
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    } else {
                        T.showShort(AddContactActivity.this, netBaseBean.getMessage());
                    }
                }

                @Override
                public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

                }
            }, true, true);
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                public void run() {
                    String s2 = getResources().getString(R.string.Request_add_buddy_failure);
                    Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
