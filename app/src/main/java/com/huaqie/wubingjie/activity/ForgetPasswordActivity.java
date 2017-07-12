package com.huaqie.wubingjie.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.utils.MD5Util;
import com.huaqie.wubingjie.utils.T;
import com.huaqie.wubingjie.utils.TrimNumberTest;
import com.huaqie.wubingjie.utils.imageloader.GlideCircleTransform;
import com.lewis.utils.EditTextUitls;

public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener{
    private EditText edt_phone, edt_password, edt_code;
    private ImageView btn_delete, lookPassword, back,imv_headicon;
    private Button btn_commit,btn_getcode;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_forget_password;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        edt_password = (EditText) findViewById(R.id.edt_password);
        edt_code = (EditText) findViewById(R.id.edt_code);
        btn_delete= (ImageView) findViewById(R.id.btn_delete);
        lookPassword= (ImageView) findViewById(R.id.lookPassword);
        back= (ImageView) findViewById(R.id.back);
        btn_commit = (Button) findViewById(R.id.btn_commit);
        btn_getcode= (Button) findViewById(R.id.btn_getcode);
        //监听
        back.setOnClickListener(this);
        btn_getcode.setOnClickListener(this);
        btn_commit.setOnClickListener(this);
        EditTextUitls.bindCleanToView(edt_phone, btn_delete);
        EditTextUitls.bindLookPasswordToImageView(edt_password, lookPassword, R.mipmap.icon_look, R.mipmap.icon_nolook);

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_getcode:
                getCode();
                break;
            case R.id.btn_commit:
                findPassword();
                break;

            default:
        }
    }

    private void findPassword() {
        NetBaseRequest request = new NetBaseRequest(Constants.FIND_PASSWORD);
        if (edt_phone.getText().toString().length() != 11) {
            T.showShort(this, "电话号码格式不符合11位");
            return;
        }
        request.add("member_phone", edt_phone.getText().toString());
        if (edt_password.getText().toString().length()<6) {
            T.showShort(this, "请输入不小于6位的密码");
            return;
        }
        request.add("member_password", MD5Util.MD5String(edt_password.getText().toString()));
        request.add("member_platform", 1);
        if (edt_code.getText().toString().length() != 6) {
            T.showShort(this, "请输入6位验证码");
            return;
        }
        request.add("code", edt_code.getText().toString());
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                T.showShort(ForgetPasswordActivity.this, netBaseBean.getMessage());
                if (netBaseBean.isSuccess()) {
                    finish();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, false, true);
    }

    private void getCode() {
        NetBaseRequest getCodeRequest = new NetBaseRequest(Constants.GET_CODE);
        if (edt_phone.getText().toString().length() != 11) {
            T.showShort(this, "电话号码格式不符合11位");
            return;
        }
        getCodeRequest.add("member_phone", edt_phone.getText().toString());
        getCodeRequest.add("type", "findPassword");
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
}
