package com.huaqie.wubingjie.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.model.event.EventRefresh;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.utils.MD5Util;
import com.huaqie.wubingjie.utils.T;
import com.huaqie.wubingjie.utils.TrimNumberTest;
import com.huaqie.wubingjie.utils.imageloader.GlideCircleTransform;
import com.lewis.utils.EditTextUitls;

import org.greenrobot.eventbus.EventBus;

import static com.huaqie.wubingjie.R.id.imv_headicon;

public class RegisterActivity extends BaseActivity  implements View.OnClickListener{
    private EditText edt_phone, edt_password, edt_code,edt_yq_code;
    private ImageView btn_delete, lookPassword, back,imv_headicon;
    private Button btn_commit,btn_getcode;
    private TextView tv_deal;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        edt_password = (EditText) findViewById(R.id.edt_password);
        edt_code = (EditText) findViewById(R.id.edt_code);
        edt_yq_code = (EditText) findViewById(R.id.edt_yq_code);
        btn_delete= (ImageView) findViewById(R.id.btn_delete);
        lookPassword= (ImageView) findViewById(R.id.lookPassword);
        back= (ImageView) findViewById(R.id.back);
        btn_commit = (Button) findViewById(R.id.btn_commit);
        btn_getcode= (Button) findViewById(R.id.btn_getcode);
        tv_deal = (TextView) findViewById(R.id.tv_deal);
        //监听
        back.setOnClickListener(this);
        tv_deal.setOnClickListener(this);
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
                register();
                break;
            case R.id.tv_deal:
                WebViewActivity.goTo(this, Constants.DEAL, true);
                break;

            default:
        }
    }

    private void register() {
        NetBaseRequest request = new NetBaseRequest(Constants.REGISTER);
        if (edt_phone.getText().toString().length() != 11) {
            T.showShort(this, "电话号码格式不符合11位");
            return;
        }
        request.add("member_phone", edt_phone.getText().toString());
        if(TextUtils.isEmpty(edt_password.getText().toString())){
            T.showShort(this, "请输入密码");
            return;
        }
        request.add("member_password", MD5Util.MD5String(edt_password.getText().toString()));
        request.add("member_platform", 1);
        if(edt_code.getText().toString().length() != 6){
            T.showShort(this, "请输入6位验证码");
            return;
        }
        request.add("code", edt_code.getText().toString());
        request.add("yq_code", edt_yq_code.getText().toString());
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                T.showShort(RegisterActivity.this, netBaseBean.getMessage());
                if (netBaseBean.isSuccess()) {
                    finish();
                    String[] loginInfo = {edt_phone.getText().toString(), edt_password.getText().toString()};
                    EventBus.getDefault().post(new EventRefresh(EventRefresh.ACTION_REGISTER,loginInfo,LoginActivity.class.getSimpleName()));
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
       getCodeRequest.add("type", "register");
       CallServer.getRequestInstance().add(this, 0x01, getCodeRequest, new HttpListenerCallback() {
           @Override
           public void onSucceed(int what, NetBaseBean netBaseBean) {
               if (netBaseBean.isSuccess()) {
                   btn_getcode.setClickable(false);
                   new CountDownTimer(60000, 1000) {
                       @Override
                       public void onTick(long millisUntilFinished) {
                           btn_getcode.setText("已发送" + millisUntilFinished / 1000 + "s");
                       }

                       @Override
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
