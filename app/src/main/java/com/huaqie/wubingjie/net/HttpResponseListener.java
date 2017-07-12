/*
 * Copyright 2015 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huaqie.wubingjie.net;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.huaqie.wubingjie.activity.LoginActivity;
import com.huaqie.wubingjie.mine.CheckCardActivity;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.utils.UserManager;
import com.huaqie.wubingjie.widgets.dialog.LoadingDialog;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;


/**
 * Created in Nov 4, 2015 12:02:55 PM.
 *
 * @author Yan Zhenjie.
 */
public class HttpResponseListener<T> implements OnResponseListener<T> {

    private Context context;
    /**
     * Dialog.
     */
    private LoadingDialog mWaitDialog;

    private Request<?> mRequest;
    private NetBaseRequest mNetBaseRequest;

    /**
     * 结果回调.
     */
    private HttpListener<T> callback;
    private HttpListenerCallback callback2;

    /**
     * 是否显示dialog.
     */
    private boolean isLoading;

    /**
     * @param context      context用来实例化dialog.
     * @param request      请求对象.
     * @param httpCallback 回调对象.
     * @param canCancel    是否允许用户取消请求.
     * @param isLoading    是否显示dialog.
     */
    public HttpResponseListener(Context context, Request<?> request, HttpListener<T> httpCallback, boolean canCancel, boolean isLoading) {
        this.context = context;
        this.mRequest = request;
        if (context != null && isLoading) {
            mWaitDialog = new LoadingDialog(context);
            mWaitDialog.setCancelable(canCancel);
            mWaitDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mRequest.cancel();
                }
            });
        }
        this.callback = httpCallback;
        this.isLoading = isLoading;
    }

    public HttpResponseListener(Context context, Request<?> request, HttpListenerCallback httpCallback, boolean canCancel, boolean isLoading) {
        this.context = context;
        this.mRequest = request;
        if (context != null && isLoading) {
            mWaitDialog = new LoadingDialog(context);
            mWaitDialog.setCancelable(canCancel);
            mWaitDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mRequest.cancel();
                }
            });
        }
        this.callback2 = httpCallback;
        this.isLoading = isLoading;
    }

    /**
     * 开始请求, 这里显示一个dialog.
     */
    @Override
    public void onStart(int what) {
        if (isLoading && mWaitDialog != null && !mWaitDialog.isShowing())
            mWaitDialog.show();
    }

    /**
     * 结束请求, 这里关闭dialog.
     */
    @Override
    public void onFinish(int what) {
        try {
            if (isLoading && mWaitDialog != null && mWaitDialog.isShowing())
                mWaitDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 成功回调.
     */
    @Override
    public void onSucceed(int what, Response<T> response) {
        int responseCode = response.getHeaders().getResponseCode();
       /* if (responseCode > 400 && context != null) {
            if (responseCode == 405) {// 405表示服务器不支持这种请求方法，比如GET、POST、TRACE中的TRACE就很少有服务器支持。
                context.showMessageDialog(R.string.request_succeed, R.string.request_method_not_allow);
            } else {// 但是其它400+的响应码服务器一般会有流输出。
                context.showWebDialog(response);
            }
        }*/
        //验证 token 是否过期

        if (response.get() instanceof NetBaseBean) {
            NetBaseBean request = (NetBaseBean) response.get();
            try {
                if (request.getStatus() == 4001) {
                    if (UserManager.isLogin()) {
                        com.huaqie.wubingjie.utils.T.showShort(context, "登录信息过期");
                    } else {
                        com.huaqie.wubingjie.utils.T.showShort(context, "请登录");
                    }
                  /*  if (context instanceof SplashActivity) {
                        com.orhanobut.logger.Logger.e("首页判断");
                    } else {*/
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    //}
                }
                if (request.getStatus() == 4002) {
                    Intent intent = new Intent(context, CheckCardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (callback2 != null) {
                try {
                    callback2.onSucceed(what, request);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context,"服务器数据类型错误",Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (callback != null) {
            callback.onSucceed(what, response);
        }
    }

    /**
     * 失败回调.
     */
    @Override
    public void onFailed(int what, String url, Object tag, Exception exception,
                         int responseCode, long networkMillis) {
        HttpExceptionToast.showException(exception, responseCode);
        Logger.e("错误：" + exception.getMessage());
        if (callback != null) {
            callback.onFailed(what, url, tag, exception, responseCode, networkMillis);
        }
        if (callback2 != null) {
            try {
                callback2.onFailed(what, url, tag, exception, responseCode, networkMillis);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "服务器数据类型错误", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
