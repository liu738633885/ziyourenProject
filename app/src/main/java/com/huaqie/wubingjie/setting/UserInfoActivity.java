package com.huaqie.wubingjie.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.huaqie.wubingjie.activity.LoginActivity;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.model.user.UserInfo;
import com.huaqie.wubingjie.model.user.UserInfoData;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;
import com.huaqie.wubingjie.task.PastTaskActivity;
import com.huaqie.wubingjie.utils.T;
import com.huaqie.wubingjie.utils.UserManager;
import com.huaqie.wubingjie.utils.ViewUtils;
import com.huaqie.wubingjie.utils.imageloader.ImageLoader;
import com.huaqie.wubingjie.widgets.TitleBar;
import com.huaqie.wubingjie.widgets.dialog.BottomDialog;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.HuanXinConstants;
import com.hyphenate.chatuidemo.db.UserDao;
import com.hyphenate.chatuidemo.ui.ChatActivity;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.exceptions.HyphenateException;
import com.lewis.utils.DateUtils;


public class UserInfoActivity extends BaseActivity{
    private TextView tv_nickname, tv_birthday, tv_school_isstudent,tv_phone;
    private ImageView imv_avatar,imv_sex;
    private LinearLayout ll_past_task;
    private UserInfo userInfo;
    private String userId;
    private Button btn_add_friend, btn_chat, btn_del_friend;
    private BottomDialog bottomDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_user_info;
    }

    public static void goTo(Context context, String userId) {
        Intent intent;
        if (UserManager.isLogin()) {
            intent = new Intent(context, UserInfoActivity.class);
            intent.putExtra("userId", userId);
        } else {
            intent = new Intent(context, LoginActivity.class);
        }
        context.startActivity(intent);
    }

    //获取Intent
    protected void handleIntent(Intent intent) {
        try {
            userId = getIntent().getStringExtra("userId");
            if(userId.equals(UserManager.getUid())){
                MyUserInfoActivity.goTo(this);
                finish();
            }
            //刷新环信头像
            DemoHelper.getInstance().getUserInfo(userId, true);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        userInfo = new UserInfo();
        bottomDialog=new BottomDialog(this);
        imv_avatar = (ImageView) findViewById(R.id.imv_avatar);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        imv_sex = (ImageView) findViewById(R.id.imv_sex);
        tv_birthday = (TextView) findViewById(R.id.tv_birthday);
        tv_school_isstudent = (TextView) findViewById(R.id.tv_school_isstudent);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        ll_past_task = (LinearLayout) findViewById(R.id.ll_past_task);
        btn_add_friend = (Button) findViewById(R.id.btn_add_friend);
        btn_chat = (Button) findViewById(R.id.btn_chat);
        btn_del_friend = (Button) findViewById(R.id.btn_del_friend);
        btn_add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseText();
            }
        });
        btn_del_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWarningDialog("是否删除好友?", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        delFriend();
                        dialog.myDismiss();
                    }
                });

            }
        });
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserInfoActivity.this, ChatActivity.class);
                intent.putExtra("userId",userId);
                intent.putExtra("chatType", HuanXinConstants.CHATTYPE_SINGLE);
                startActivity(intent);
                finish();
            }
        });
        ll_past_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PastTaskActivity.goTo(UserInfoActivity.this, userId);
            }
        });
        callUserInfo();

    }
    private void callUserInfo() {
        NetBaseRequest getUserRequest = RequsetFactory.creatNoUidRequest(Constants.GET_USER_INFO);
        getUserRequest.add("uid",userId);
        CallServer.getRequestInstance().addWithLogin(this, 0x01, getUserRequest, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    UserInfoData data = netBaseBean.parseObject(UserInfoData.class);
                    UserInfo userInfo = data.getInfo();
                    updataUI(userInfo);
                }

            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }
    private void updataUI(UserInfo userInfo) {
        try {
            this.userInfo = userInfo;

            ImageLoader.loadHeadImage(this, userInfo.getMember_avatar(), imv_avatar, 0);
            tv_nickname.setText(userInfo.getMember_nickname());
            String isstudent = "";
            if (userInfo.getMember_is_student().equals("1")) {
                isstudent = "在校生";
            } else if (userInfo.getMember_is_student().equals("2")) {
                isstudent = "毕业";
            } else {
                isstudent = "其他";
            }
            tv_school_isstudent.setText(userInfo.getMember_school() + "  " + isstudent);
            imv_sex.setVisibility(View.VISIBLE);
            if (userInfo.getMember_sex().equals(UserInfo.SEX_MAN)) {
                imv_sex.setImageResource(R.mipmap.icon_sex_man_checked);
            } else if (userInfo.getMember_sex().equals(UserInfo.SEX_WOMAN)) {
                imv_sex.setImageResource(R.mipmap.icon_sex_woman_checked);
            } else {
                imv_sex.setVisibility(View.GONE);
            }
           /* if(!TextUtils.isEmpty(userInfo.getMember_phone())){
                ViewUtils.setPhone(this, tv_phone, "", userInfo.getMember_phone());
                ll_phone.setVisibility(View.VISIBLE);
            }else {
                ll_phone.setVisibility(View.GONE);
            }*/
            ViewUtils.setPhone(this, tv_phone, "", userInfo.getMember_phone());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            tv_birthday.setText(DateUtils.stringToString(userInfo.getMember_birthday(), DateUtils.yyyyMMDD));
        } catch (Exception e) {
            tv_birthday.setText("");
            e.printStackTrace();
        }
        findFriend();
    }




    /*判断好友添加*/
    private void findFriend() {
        boolean isFriend=false;
       /* if (DemoHelper.getInstance().getContactList().containsKey(userId)) {
            //let the user know the contact already in your contact list
            if (EMClient.getInstance().contactManager().getBlackListUsernames().contains(userId)) {
                T.showShort(this, R.string.user_already_in_contactlist);
            } else {
                isFriend = true;
            }
        }*/
        if(!TextUtils.isEmpty(userInfo.getIs_friend())&&userInfo.getIs_friend().equals("1")){
            isFriend = true;
        }

        if(isFriend){

            btn_chat.setVisibility(View.VISIBLE);
            btn_del_friend.setVisibility(View.VISIBLE);
            btn_add_friend.setVisibility(View.GONE);
            ll_past_task.setVisibility(View.VISIBLE);
        } else {
            btn_chat.setVisibility(View.GONE);
            btn_del_friend.setVisibility(View.GONE);
            btn_add_friend.setVisibility(View.VISIBLE);
            ll_past_task.setVisibility(View.GONE);
        }
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
                addContact(content, userId);
                bottomDialog.myDismiss();

            }
        });
        bottomDialog.show();
    }

    /**
     * 添加contact
     *
     */
    public void addContact(final String content, final String userId) {
        if (EMClient.getInstance().getCurrentUser().equals(userId)) {
            new EaseAlertDialog(this, R.string.not_add_myself).show();
            return;
        }

        if (DemoHelper.getInstance().getContactList().containsKey(userId)) {
            //let the user know the contact already in your contact list
            if (EMClient.getInstance().contactManager().getBlackListUsernames().contains(userId)) {
                new EaseAlertDialog(this, R.string.user_already_in_contactlist).show();
                return;
            }
            new EaseAlertDialog(this, R.string.This_user_is_already_your_friend).show();
            return;
        }
        try {
            // 向服务器发送添加好友的请求
            NetBaseRequest getUserRequest = RequsetFactory.creatBaseRequest(UserInfoActivity.this, Constants.ADD_FRIEND);
            getUserRequest.add("to_uid", userId);
            getUserRequest.add("content", content);
            //getUserRequest.add(ChatConstants.SOURCE_TYPE, 1);
            CallServer.getRequestInstance().add(UserInfoActivity.this, 0x01, getUserRequest, new HttpListenerCallback() {
                @Override
                public void onSucceed(int what, NetBaseBean netBaseBean) {
                    if (netBaseBean.isSuccess()) {
                        String s1 = getResources().getString(R.string.send_successful);
                        T.showShort(UserInfoActivity.this, s1);
                        try {
                            EMClient.getInstance().contactManager().addContact(userId, content);
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    } else {
                        T.showShort(UserInfoActivity.this, netBaseBean.getMessage());
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

    private void delFriend() {
        //String st1 = getResources().getString(R.string.deleting);
        final String st2 = getResources().getString(R.string.Delete_failed);
       /* final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(st1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();*/
        NetBaseRequest netBaseRequest = RequsetFactory.creatBaseRequest(this, Constants.DEL_FRIEND);
        netBaseRequest.add("friend_id", userId);
        CallServer.getRequestInstance().add(this, 0x01, netBaseRequest, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if(netBaseBean.isSuccess()){
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                EMClient.getInstance().contactManager().deleteContact(userId);
                                // 删除db和内存中此用户的数据
                                UserDao dao = new UserDao(UserInfoActivity.this);
                                dao.deleteContact(userId);
                                DemoHelper.getInstance().getContactList().remove(userId);

                            } catch (final Exception e) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        //pd.dismiss();
                                        Toast.makeText(UserInfoActivity.this, st2 + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });

                            }

                        }
                    }).start();
                    //刷新下界面
                    //callUserInfo();
                    finish();
                }
                T.showShort(UserInfoActivity.this, netBaseBean.getMessage());
                //pd.dismiss();
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                //pd.dismiss();
            }
        }, false, true);
    }
}
