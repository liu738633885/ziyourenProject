package com.huaqie.wubingjie.setting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.activity.BaseActivity;
import com.huaqie.wubingjie.activity.WebViewActivity;
import com.huaqie.wubingjie.utils.BitmapUtils;
import com.huaqie.wubingjie.utils.FileUtil;
import com.huaqie.wubingjie.utils.SettingsManager;
import com.huaqie.wubingjie.utils.SystemUtils;
import com.huaqie.wubingjie.utils.T;
import com.huaqie.wubingjie.utils.UpdataAppManager;
import com.huaqie.wubingjie.utils.UserManager;
import com.huaqie.wubingjie.widgets.ChooseLayout;
import com.hyphenate.EMCallBack;
import com.hyphenate.chatuidemo.DemoHelper;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

import java.io.File;


public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_logout;
    private ChooseLayout cl_userinfo, cl_zhanghu, cl_feedback, cl_clean, cl_appinfo, cl_updata, cl_clean_setting,cl_xieyi;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    showCacheSize();

            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected int getContentViewId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        cl_userinfo = (ChooseLayout) findViewById(R.id.cl_userinfo);
        cl_zhanghu = (ChooseLayout) findViewById(R.id.cl_zhanghu);
        cl_feedback = (ChooseLayout) findViewById(R.id.cl_feedback);
        cl_clean = (ChooseLayout) findViewById(R.id.cl_clean);
        cl_appinfo = (ChooseLayout) findViewById(R.id.cl_appinfo);
        cl_xieyi = (ChooseLayout) findViewById(R.id.cl_xieyi);
        cl_updata = (ChooseLayout) findViewById(R.id.cl_updata);
        cl_clean_setting = (ChooseLayout) findViewById(R.id.cl_clean_setting);
        cl_userinfo.setOnClickListener(this);
        cl_zhanghu.setOnClickListener(this);
        cl_feedback.setOnClickListener(this);
        cl_clean_setting.setOnClickListener(this);
        cl_updata.setOnClickListener(this);
        cl_appinfo.setOnClickListener(this);
        cl_xieyi.setOnClickListener(this);
        cl_clean.setOnClickListener(this);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(this);
        showCacheSize();
        if (UserManager.isLogin()) {
            btn_logout.setVisibility(View.VISIBLE);
        } else {
            btn_logout.setVisibility(View.GONE);
        }
        updata();
    }

    private void showCacheSize() {
        double size = FileUtil.getFolderSize(new File(getCacheDir(), BitmapUtils.SAVE_IMG));
        size += FileUtil.getFolderSize(new File(getCacheDir(), DiskCache.Factory.DEFAULT_DISK_CACHE_DIR));
        cl_clean.setText2(FileUtil.getFormatSize(size));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_logout:
                DemoHelper.getInstance().logout(true, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                T.showShort(getApplicationContext(), "--退出成功--");
                            }
                        });
                        finish();
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }

                    @Override
                    public void onError(int code, String message) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                T.showShort(getApplicationContext(), "环信退出失败");
                            }
                        });
                    }
                });
                break;
            case R.id.cl_clean:
                showWarningDialog("是否清除缓存?", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Glide.get(getApplicationContext()).clearDiskCache();
                                FileUtil.deleteFolderFile(getCacheDir() + BitmapUtils.SAVE_IMG, false);
                                handler.sendEmptyMessage(1);
                            }
                        }).start();
                        T.showShort(SettingActivity.this, "清除缓存成功");
                        dialog.myDismiss();
                    }
                });

                break;
            case R.id.cl_userinfo:
                MyUserInfoActivity.goTo(SettingActivity.this);
                break;
            case R.id.cl_zhanghu:
                AccountSecurityActivity.goTo(SettingActivity.this);
                break;
            case R.id.cl_feedback:
                FeedbackActivity.goTo(SettingActivity.this);
                break;
            case R.id.cl_appinfo:
                WebViewActivity.goTo(this, Constants.ABOUT+ SystemUtils.getAppVersionName(this),true);
                break;
            case R.id.cl_updata:
                UpdataAppManager.updata(this, true);
                break;
            case R.id.cl_clean_setting:
                showWarningDialog("是否重置设置?", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SettingsManager.clean();
                        T.showShort(SettingActivity.this, "重置设置成功");
                        dialog.myDismiss();
                    }
                });
                break;
            case R.id.cl_xieyi:
                WebViewActivity.goTo(this, Constants.DEAL,true);
                break;
            default:
        }
    }

    private void updata() {
        PgyUpdateManager.register(this,
                new UpdateManagerListener() {

                    @Override
                    public void onNoUpdateAvailable() {
                        cl_updata.setText("版本更新:当前已是最新版本");
                    }

                    @Override
                    public void onUpdateAvailable(String result) {
                        final AppBean appBean = getAppBeanFromString(result);
                        cl_updata.setText("有新版本: ");
                        cl_updata.setText2(appBean.getVersionName());
                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PgyUpdateManager.unregister();
    }
}
