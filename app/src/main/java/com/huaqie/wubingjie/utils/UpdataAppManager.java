package com.huaqie.wubingjie.utils;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.widgets.dialog.CenterDialog;
import com.orhanobut.logger.Logger;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

/**
 * Created by lewis on 16/8/20.
 */
public class UpdataAppManager {
    public static void updata(final Activity activity, final boolean showNoUpdate) {
        PgyUpdateManager.register(activity,
                new UpdateManagerListener() {
                    @Override
                    public void onUpdateAvailable(final String result) {
                        Logger.e("appinf" + result);
                        // 将新版本信息封装到AppBean中
                        final AppBean appBean = getAppBeanFromString(result);
                        StringBuffer contentText = new StringBuffer();
                        contentText.append("版本:").append(appBean.getVersionName()).append("\n");
                        contentText.append("更新内容:\n");
                        contentText.append(appBean.getReleaseNote());
                        boolean canCancel = true;
                        try {
                            if (appBean.getReleaseNote().endsWith("请更新")) {
                                canCancel = false;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        CenterDialog centerDialog=new CenterDialog(activity);
                        centerDialog.setContentView(R.layout.mydialog_updata);
                        centerDialog.setText1(contentText.toString());
                        Button btn_updata_imposed= (Button) centerDialog.findViewById(R.id.btn_updata_imposed);
                        Button btn_updata = (Button) centerDialog.findViewById(R.id.btn_updata);
                        ((ViewGroup)btn_updata.getParent()).setVisibility(canCancel? View.VISIBLE:View.GONE);
                        btn_updata_imposed.setVisibility(canCancel ? View.GONE : View.VISIBLE);
                        btn_updata_imposed.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startDownloadTask(
                                        activity,
                                        appBean.getDownloadURL());
                            }
                        });
                        btn_updata.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startDownloadTask(
                                        activity,
                                        appBean.getDownloadURL());
                            }
                        });
                        centerDialog.setCancelable(canCancel);
                        centerDialog.setCanceledOnTouchOutside(canCancel);
                        centerDialog.myShow();
                    }

                    @Override
                    public void onNoUpdateAvailable() {
                        if (showNoUpdate) {
                            T.showShort(activity, "当前已是最新版本");
                        }
                    }
                });
    }

}