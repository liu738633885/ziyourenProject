package com.huaqie.wubingjie.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.model.event.EventRefresh;
import com.huaqie.wubingjie.model.netmodel.Login;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListener;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.setting.MyUserInfoActivity;
import com.huaqie.wubingjie.utils.MD5Util;
import com.huaqie.wubingjie.utils.SystemUtils;
import com.huaqie.wubingjie.utils.T;
import com.huaqie.wubingjie.utils.TrimNumberTest;
import com.huaqie.wubingjie.utils.UserManager;
import com.huaqie.wubingjie.utils.imageloader.GlideCircleTransform;
import com.huaqie.wubingjie.widgets.dialog.WaitDialog;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.DemoHelper;
import com.lewis.utils.EditTextUitls;
import com.orhanobut.logger.Logger;
import com.yolanda.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * Created by lewis on 16/6/22.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, HttpListener<NetBaseBean> {
    @Override
    protected int getContentViewId() {
        return R.layout.activity_login;
    }

    private EditText edt_phone, edt_password;
    private Button btn_login, btn_youke;
    //0为短信登录  1 为密码登录
    private int mode;
    private WaitDialog waitDialog;
    private ImageView btn_delete, lookPassword,imv_headicon;

    @Override
    protected void initView(Bundle savedInstanceState) {
        DemoHelper.getInstance().logout(true, null);
        waitDialog = new WaitDialog(this);
        waitDialog.setCancelable(false);
        waitDialog.setCanceledOnTouchOutside(false);
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        edt_password = (EditText) findViewById(R.id.edt_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_youke = (Button) findViewById(R.id.btn_youke);
        btn_delete = (ImageView) findViewById(R.id.btn_delete);
        lookPassword = (ImageView) findViewById(R.id.lookPassword);
        EditTextUitls.bindCleanToView(edt_phone, btn_delete);
        EditTextUitls.bindLookPasswordToImageView(edt_password, lookPassword, R.mipmap.icon_look, R.mipmap.icon_nolook);
        btn_login.setOnClickListener(this);
        btn_youke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //设置默认号码
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String phoneId = tm.getLine1Number();
            edt_phone.setText(TrimNumberTest.trimTelNum(phoneId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        imv_headicon = (ImageView) findViewById(R.id.imv_headicon);
        Glide.with(this).load(R.mipmap.app_icon).transform(new GlideCircleTransform(this)).into(imv_headicon);

    }

    public void forgetPassword(View v) {
        startActivity(new Intent(this, ForgetPasswordActivity.class));
    }

    public void register(View v) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void Login(String phone, String password) {
        NetBaseRequest loginRequest;
        loginRequest = new NetBaseRequest(Constants.LOGIN);
        loginRequest.add("member_phone", phone);
        loginRequest.add("member_password", MD5Util.MD5String(password));
        //loginRequest.add("platform", 1);
        loginRequest.add("imei", SystemUtils.getDeviceIMEI(LoginActivity.this));
        waitDialog.show();
        CallServer.getRequestInstance().add(this, 0x01, loginRequest, this, false, false);
    }


    @Override
    public void onClick(View v) {
        String username = edt_phone.getText().toString();
        String password = edt_password.getText().toString();
        if (TextUtils.isEmpty(username) | username.length() != 11) {
            showErrorDialog("请输入正确的手机号");
            return;
        }
        if (password.length()<6) {
            showErrorDialog("密码长度不能小于6");
            return;
        }
        switch (v.getId()) {
            case R.id.btn_login:
                Login(username, password);
                break;

            default:
        }
    }

    @Override
    public void onSucceed(int what, Response<NetBaseBean> response) {
        if (response.get().isSuccess()) {
            //T.showShort(LoginActivity.this, "登录" + response.get().getMessage());
            final Login login = response.get().parseObject(Login.class);
            if (UserManager.saveLoginInfo(login)) {
                // 登录环信
                EMClient.getInstance().login(UserManager.getUid(), UserManager.getKeyPassword(), new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        // ** manually load all local groups and conversation
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();

                        // update current user's display name for APNs
                        boolean updatenick = EMClient.getInstance().updateCurrentUserNick(
                                login.getInfo().getMember_nickname().trim());
                        if (!updatenick) {
                            Log.e("LoginActivity", "update current user nick fail");
                        }
                        //登陆成功
                        waitDialog.dismiss();
                        EventBus.getDefault().post(new EventRefresh(EventRefresh.ACTION_LOGIN));
                        try {
                            if (UserManager.getKeyAvatar().equals("http://ainana.b0.upaiyun.com/user/head_1.png") || UserManager.getKeyNickname().startsWith("用户")) {
                                MyUserInfoActivity.goTo(LoginActivity.this);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                        Logger.d("HXlogin: onProgress");
                    }

                    @Override
                    public void onError(final int code, final String message) {
                        waitDialog.dismiss();
                        Logger.d("HXlogin: onError:" + code);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                showErrorDialog("环信登录失败   详情:" + message);
                                Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            }


        } else {
            T.showShort(LoginActivity.this, response.get().getMessage());
            waitDialog.dismiss();
        }
    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        waitDialog.dismiss();
    }

    @Subscribe
    public void onEventMainThread(EventRefresh refresh) {
        if(refresh.getAction().equals(EventRefresh.ACTION_REGISTER)){
            try {
                String [] login= (String[]) refresh.getData();
                edt_phone.setText(login[0]);
                edt_password.setText(login[1]);
                Login(login[0], login[1]);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
