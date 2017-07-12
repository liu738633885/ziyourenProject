package com.huaqie.wubingjie.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.model.event.EventRefresh;
import com.orhanobut.logger.Logger;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this,WXConstants.APP_ID);

        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Logger.d("onPayFinish, errCode = " + resp.errCode + resp.errStr);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_COMM:
                    // 一般错误
                    EventBus.getDefault().post(new EventRefresh(WXPayEntryActivity.class.getName()+"ERR_COMM"));
                    break;
                case BaseResp.ErrCode.ERR_OK:
                    // 正确返回
                    EventBus.getDefault().post(new EventRefresh(WXPayEntryActivity.class.getName()+"ERR_OK"));
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    // 用户取消
                    EventBus.getDefault().post(new EventRefresh(WXPayEntryActivity.class.getName()+"ERR_USER_CANCEL"));
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    // 认证被否决
                    break;
                case BaseResp.ErrCode.ERR_UNSUPPORT:
                    // 不支持错误
                    break;
                case BaseResp.ErrCode.ERR_SENT_FAILED:
                    // 发送失败
                    break;
            }
        }
        finish();
    }


}