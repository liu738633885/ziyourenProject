package com.huaqie.wubingjie.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.activity.BaseActivity;
import com.huaqie.wubingjie.activity.LoginActivity;
import com.huaqie.wubingjie.model.event.EventRefresh;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.model.user.UserInfo;
import com.huaqie.wubingjie.model.user.UserInfoData;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;
import com.huaqie.wubingjie.utils.CommonUtils;
import com.huaqie.wubingjie.utils.MD5Util;
import com.huaqie.wubingjie.utils.T;
import com.huaqie.wubingjie.utils.UserManager;
import com.huaqie.wubingjie.widgets.ChooseLayout;
import com.huaqie.wubingjie.widgets.TitleBar;
import com.huaqie.wubingjie.widgets.dialog.BottomDialog;

import org.greenrobot.eventbus.EventBus;

public class AccountSecurityActivity extends BaseActivity implements View.OnClickListener {

    private UserInfo userInfo;
    private ChooseLayout cl_nickname, cl_phone, cl_alipay, cl_password;
    private BottomDialog bottomDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_account_security;
    }

    public static void goTo(Context context) {
        Intent intent;
        if (UserManager.isLogin()) {
            intent = new Intent(context, AccountSecurityActivity.class);
        } else {
            intent = new Intent(context, LoginActivity.class);
        }
        context.startActivity(intent);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        bottomDialog = new BottomDialog(this);
        cl_nickname = (ChooseLayout) findViewById(R.id.cl_nickname);
        cl_phone = (ChooseLayout) findViewById(R.id.cl_phone);
        cl_alipay = (ChooseLayout) findViewById(R.id.cl_alipay);
        cl_password = (ChooseLayout) findViewById(R.id.cl_password);
        cl_nickname.setOnClickListener(this);
        cl_phone.setOnClickListener(this);
        cl_alipay.setOnClickListener(this);
        cl_password.setOnClickListener(this);
        callUserInfo();
    }

    private void callUserInfo() {
        NetBaseRequest getUserRequest = RequsetFactory.creatBaseRequest(this, Constants.GET_USER_INFO);
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
            //存储
            UserManager.saveUserInfo(userInfo);
            //刷新所有
            EventBus.getDefault().post(new EventRefresh(EventRefresh.ACTION_LOGIN));
            this.userInfo = userInfo;
            cl_nickname.setText2(userInfo.getMember_nickname());
            cl_phone.setText2(userInfo.getMember_phone());
            cl_alipay.setText2(userInfo.getMember_alipay());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cl_nickname:
                chooseText("昵称", userInfo.getMember_nickname(), 10);
                break;
            case R.id.cl_phone:
                showChangePhone();
                break;
            case R.id.cl_alipay:
                chooseText("支付宝", userInfo.getMember_alipay(), 0);
                break;
            case R.id.cl_password:
                showChangePassword();
                break;
            default:
        }
    }


    private void chooseText(final String text, String showText, final int textlength) {
        bottomDialog.setContentView(R.layout.dialog_edittext, true);
        TitleBar titleBar = (TitleBar) bottomDialog.findViewById(R.id.titlebar);
        titleBar.setMode(TitleBar.MODE_BACK_TV);
        titleBar.setCenterText("输入" + text);
        titleBar.setRightText("确认");
        final EditText edt = (EditText) bottomDialog.findViewById(R.id.edt);
        if (!TextUtils.isEmpty(showText)) {
            edt.setText(showText);
        }
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
                if (TextUtils.isEmpty(content)) {
                    T.showShort(AccountSecurityActivity.this, "请输入" + text);
                    return;
                }
                if (textlength > 0 && content.length() > textlength) {
                    T.showShort(AccountSecurityActivity.this, "请输入小于" + textlength + "位的" + text);
                    return;
                }
                switch (text) {
                    case "昵称":
                        editUserInfo("member_nickname", content);
                        break;
                    case "支付宝":
                        editUserInfo("member_alipay", content);
                        break;
                }

                bottomDialog.myDismiss();

            }
        });
        bottomDialog.show();
    }

    private void editUserInfo(String key, String value) {
        NetBaseRequest editUserRequest = RequsetFactory.creatBaseRequest(this, Constants.EDIT_USER_INFO);
        editUserRequest.add(key, value);
        CallServer.getRequestInstance().addWithLogin(this, 0x01, editUserRequest, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    callUserInfo();
                } else {
                    T.showShort(AccountSecurityActivity.this, "编辑用户失败");
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                T.showShort(AccountSecurityActivity.this, "编辑用户失败");
            }
        }, true, true);
    }

    private void showChangePassword() {
        bottomDialog.setContentView(R.layout.dialog_change_password, true);
        TitleBar titleBar = (TitleBar) bottomDialog.findViewById(R.id.titlebar);
        final ChooseLayout cl_old_password = (ChooseLayout) bottomDialog.findViewById(R.id.cl_old_password);
        final ChooseLayout cl_new_password = (ChooseLayout) bottomDialog.findViewById(R.id.cl_new_password);
        final ChooseLayout cl_new_password2 = (ChooseLayout) bottomDialog.findViewById(R.id.cl_new_password2);
        titleBar.setLeftClike(new TitleBar.LeftClike() {
            @Override
            public void onClick(View view) {
                bottomDialog.myDismiss();
            }
        });
        bottomDialog.findViewById(R.id.btn_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String old_password = cl_old_password.getEdt().getText().toString();
                String new_password = cl_new_password.getEdt().getText().toString();
                String new_password2 = cl_new_password2.getEdt().getText().toString();
                if (TextUtils.isEmpty(old_password) || TextUtils.isEmpty(new_password) || TextUtils.isEmpty(new_password2)) {
                    T.showShort(AccountSecurityActivity.this, "请填写完整");
                    return;
                }
                if (!new_password.equals(new_password2)) {
                    T.showShort(AccountSecurityActivity.this, "两次新密码不一致");
                    return;
                }
                if(new_password.length()<6||new_password.length()>25){
                    T.showShort(AccountSecurityActivity.this, "请输入6至25位新密码");
                    return;
                }
                editPassword(old_password, new_password);
                bottomDialog.myDismiss();
            }
        });
        bottomDialog.myShow();
    }

    private void editPassword(String old_password, String new_password) {
        NetBaseRequest editUserRequest = RequsetFactory.creatBaseRequest(this, Constants.EDIT_PASSWORD);
        editUserRequest.add("old_password", MD5Util.MD5String(old_password));
        editUserRequest.add("new_password", MD5Util.MD5String(new_password));
        CallServer.getRequestInstance().addWithLogin(this, 0x01, editUserRequest, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    callUserInfo();
                    T.showShort(AccountSecurityActivity.this, "修改成功");
                } else {
                    T.showShort(AccountSecurityActivity.this, netBaseBean.getMessage());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                T.showShort(AccountSecurityActivity.this, "修改失败");
            }
        }, true, true);
    }

    private void showChangePhone() {
        bottomDialog.setContentView(R.layout.dialog_change_phone, true);
        TitleBar titleBar = (TitleBar) bottomDialog.findViewById(R.id.titlebar);
        final Button btn_getcode = (Button) bottomDialog.findViewById(R.id.btn_getcode);

        final EditText edt1 = (EditText) bottomDialog.findViewById(R.id.edt1);
        final EditText edt2 = (EditText) bottomDialog.findViewById(R.id.edt2);
        titleBar.setLeftClike(new TitleBar.LeftClike() {
            @Override
            public void onClick(View view) {
                bottomDialog.myDismiss();
            }
        });
        btn_getcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt1.getText().toString().length() == 11) {
                    CommonUtils.hideSoftInput(edt1.getContext(), edt1);
                    getCode(edt1.getText().toString(), btn_getcode);
                    CommonUtils.showSoftInput(edt2.getContext(), edt2);
                    CommonUtils.getFocus(edt2);
                } else {
                    T.showShort(AccountSecurityActivity.this, "新手机号格式不对");
                }
            }
        });
        bottomDialog.findViewById(R.id.btn_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newphone = edt1.getText().toString();
                String code = edt2.getText().toString();
                if (newphone.length() != 11 || code.length() != 6) {
                    T.showShort(AccountSecurityActivity.this, "手机号或验证码格式有误");
                    return;
                }
                updatePhone(newphone, code);
                bottomDialog.myDismiss();
            }
        });
        bottomDialog.myShow();
    }

    private void getCode(String phone, final Button btn_getcode) {
        NetBaseRequest getCodeRequest = new NetBaseRequest(Constants.GET_CODE);
        getCodeRequest.add("member_phone", phone);
        getCodeRequest.add("type", "updatePhone");
        CallServer.getRequestInstance().add(this, 0x01, getCodeRequest, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    btn_getcode.setClickable(false);
                    new CountDownTimer(60000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            btn_getcode.setText("已发送" + millisUntilFinished / 1000 + "s");
                        }

                        public void onFinish() {
                            btn_getcode.setClickable(true);
                            btn_getcode.setText("重新发送");
                        }
                    }.start();
                } else {
                    btn_getcode.setClickable(true);
                    btn_getcode.setText("重新发送");
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, false, true);
    }

    private void updatePhone(String phone, String code) {
        NetBaseRequest getCodeRequest = new NetBaseRequest(Constants.UPDATE_PHOEN);
        getCodeRequest.add("new_phone", phone);
        getCodeRequest.add("code", code);
        CallServer.getRequestInstance().add(this, 0x01, getCodeRequest, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    callUserInfo();
                    T.showShort(AccountSecurityActivity.this, "修改成功");
                } else {
                    T.showShort(AccountSecurityActivity.this, netBaseBean.getMessage());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                T.showShort(AccountSecurityActivity.this, "修改失败");
            }
        }, false, true);
    }

}
