package com.huaqie.wubingjie.task;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.activity.BaseActivity;
import com.huaqie.wubingjie.aliPay.PayResult;
import com.huaqie.wubingjie.model.AliPayPost;
import com.huaqie.wubingjie.model.WeChatPost;
import com.huaqie.wubingjie.model.WeChatPostBody;
import com.huaqie.wubingjie.model.event.EventRefresh;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.model.user.Account;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;
import com.huaqie.wubingjie.utils.T;
import com.huaqie.wubingjie.widgets.dialog.LoadingDialog;
import com.huaqie.wubingjie.wxapi.WXConstants;
import com.huaqie.wubingjie.wxapi.WXPayEntryActivity;
import com.orhanobut.logger.Logger;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PayActivity extends BaseActivity {
    public static final String KEY_PAY_ID = "key_pay_id";
    public static final String KEY_TYPE = "key_type";
    private RadioButton rb3;
    private RadioGroup activity_pay_grab;
    private Button btn_pay;
    private int pay_type = 0;
    private String pay_id;
    private String type;
    public static final int BALANCR = 1;
    public static final int ALIPAY = 2;
    public static final int WECHAT = 3;
    private LoadingDialog loadingDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_pay;
    }

    protected void handleIntent(Intent intent) {
        pay_id = intent.getStringExtra(KEY_PAY_ID);
        type = intent.getStringExtra(KEY_TYPE);
        where = intent.getStringExtra(KEY_WHERE);
    }

    public static void goTo(Context context, String pay_id, String type, String where) {
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra(KEY_PAY_ID, pay_id);
        intent.putExtra(KEY_TYPE, type);
        intent.putExtra(KEY_WHERE, where);
        context.startActivity(intent);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        loadingDialog=new LoadingDialog(this);
        rb3 = (RadioButton) findViewById(R.id.rb3);
        activity_pay_grab = (RadioGroup) findViewById(R.id.activity_pay);
        btn_pay = (Button) findViewById(R.id.btn_pay);
        activity_pay_grab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb1:
                        pay_type = WECHAT;
                        break;
                    case R.id.rb2:
                        pay_type = ALIPAY;
                        break;
                    case R.id.rb3:
                        pay_type = BALANCR;
                        break;
                    default:
                }
            }
        });
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPay();
            }
        });
        getUserAccount();
    }

    private void callPay() {
        if (pay_type == 0) {
            T.showShort(this, "请选择支付方式");
            return;
        }
        if (TextUtils.isEmpty(type)) {
            T.showShort(this, "类型无法判断");
            return;
        }
        NetBaseRequest request;
        if (type.equals("1")) {
            request = RequsetFactory.creatBaseRequest(this, Constants.GO_PAY_SERVE_ORDER);
        } else if (type.equals("2")) {
            request = RequsetFactory.creatBaseRequest(this, Constants.GO_PAY);
        } else {
            T.showShort(this, "类型无法判断");
            return;
        }
        request.add("pay_type", pay_type);
        request.add("pay_id", pay_id);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    if (pay_type == BALANCR) {
                        //钱包
                        EventBus.getDefault().post(new EventRefresh(EventRefresh.ACTION_REFRESH, null, where));
                        finish();
                    }else if(pay_type==ALIPAY){
                        //支付宝
                        AliPayPost aliData = netBaseBean.parseObject(AliPayPost.class);
                        if (aliData != null) {
                            String return_str = aliData.getReturn_str();
                            payToALi(return_str);
                        }
                    } else if (pay_type == WECHAT) {
                        //微信
                        WeChatPostBody post = netBaseBean.parseObject(WeChatPostBody.class);
                        if (post != null) {
                            weChatPay(post);
                        }
                    }
                } else {
                    showErrorDialog(netBaseBean.getMessage());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

    private void getUserAccount() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(this, Constants.GET_USER_ACCOUNT);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    Account account = netBaseBean.parseObject(Account.class);
                    rb3.setText("钱包(" + account.getInfo().getMember_surplus_money() + ")");
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }
    public void payToALi(String return_str) {

        /**
         * 完整的符合支付宝参数规范的订单信息
         */
        final String payInfo = return_str;
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(PayActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);
                // 处理支付结果
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                nHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private static final int SDK_PAY_FLAG = 11;
    private static final int SDK_CHECK_FLAG = 12;
    private Handler nHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(getApplication(), "支付成功", Toast.LENGTH_SHORT).show();
                        /**
                         * 支付宝支付成功后调接口
                         */
                        EventBus.getDefault().post(new EventRefresh(EventRefresh.ACTION_REFRESH, null, where));
                        finish();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(getApplication(), "支付结果确认中", Toast.LENGTH_SHORT).show();
                        /* 发送信息到服务器说支付结果确认中 */
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(getApplication(), "支付失败", Toast.LENGTH_SHORT).show();
                        /* 发送信息到服务器说支付失败 */
                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    Toast.makeText(getApplication(), "检查结果为：" + msg.obj, Toast.LENGTH_LONG);
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    private void weChatPay(WeChatPostBody postBody) {
        WeChatPost post = postBody.getReturn_str();
        if (TextUtils.isEmpty(post.getAppid()) || TextUtils.isEmpty(post.getNoncestr()) || TextUtils.isEmpty(post.getPartnerid()) || TextUtils.isEmpty(post.getPrepayid())
                || TextUtils.isEmpty(post.getSign()) || post.getTimestamp()==0) {
            Logger.d("微信支付" + post.toString());
            T.showShort(this, "数据有误，不能支付！！");
            return;
        }
        loadingDialog.myShow();
        IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
        //IWXAPI msgApi = WXAPIFactory.createWXAPI(this, "wx906e5f58b03a2345");
        //msgApi.registerApp(post.getAppid());
        msgApi.registerApp(WXConstants.APP_ID);
        PayReq req = new PayReq();
        req.appId = post.getAppid();// 微信提供的APPID
        req.partnerId = post.getPartnerid();
        req.prepayId = post.getPrepayid();
        req.packageValue = "Sign=WXPay";
        req.nonceStr = post.getNoncestr();
        req.timeStamp = String.valueOf(post.getTimestamp());
        req.sign = post.getSign();
        /*req.appId = "wx5bb1152c4f7a21d4";// 微信提供的APPID
        req.partnerId = "1375015502";
        req.prepayId = "wx2016111016241704d3ed9b950136100000";
        req.packageValue = "Sign=WXPay";
        req.nonceStr = "ZrlkZCkPGilUdLfrObpj1QbRHal3XOIn";
        req.timeStamp = "1478766257";
        req.sign = "0F19BDD510E1D1250E5D44A1011575AC";*/
        msgApi.sendReq(req);
    }

    @Subscribe
    public void onEventMainThread(EventRefresh refresh) {
        if (refresh.getAction().equals(WXPayEntryActivity.class.getName() + "ERR_OK")) {
            T.showShort(this, "支付成功");
            loadingDialog.myDismiss();
            EventBus.getDefault().post(new EventRefresh(EventRefresh.ACTION_REFRESH, null, where));
            finish();
        }
        if (refresh.getAction().equals(WXPayEntryActivity.class.getName() + "ERR_USER_CANCEL")) {
            T.showShort(this, "支付取消");
            loadingDialog.myDismiss();
        }
        if (refresh.getAction().equals(WXPayEntryActivity.class.getName() + "ERR_COMM")) {
            T.showShort(this, "支付失败");
            loadingDialog.myDismiss();
        }

    }
}
