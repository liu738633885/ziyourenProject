package com.huaqie.wubingjie.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.utils.PermissionsChecker;
import com.huaqie.wubingjie.widgets.dialog.CenterDialog;
import com.hyphenate.chatuidemo.DemoHelper;
import com.pgyersdk.crash.PgyCrashManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by lewis on 16/6/15.
 */
public abstract class BaseActivity extends AppCompatActivity {
   // private PromptDialog promptDialog;
    private boolean needCheck = false;
    public static final String KEY_WHERE="where";
    protected String where="";
    //布局文件ID
    protected abstract int getContentViewId();

    //获取Intent
    protected void handleIntent(Intent intent) {

    }

    //检验是否登录
    protected void checkLogin() {
       /* if (!UserManager.isLogin()) {
            startActivity(new Intent(this, LoginActivity.class));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 500);
        }*/
    }
    protected abstract void initView(Bundle savedInstanceState);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        EventBus.getDefault().register(this);
        PgyCrashManager.register(this);
        setContentView(getContentViewId());

        if (null != getIntent()) {
            handleIntent(getIntent());
        }
        initView(savedInstanceState);

    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker != null && mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
        }
        // 自定义摇一摇的灵敏度，默认为950，数值越小灵敏度越高。
        //PgyFeedbackShakeManager.setShakingThreshold(1000);

        // 以对话框的形式弹出
        //PgyFeedbackShakeManager.register(MainActivity.this);

        // 以Activity的形式打开，这种情况下必须在AndroidManifest.xml配置FeedbackActivity
        // 打开沉浸式,默认为false
        //FeedbackActivity.setBarImmersive(true);
        //PgyFeedbackShakeManager.register(this, false);
    }
    @Override
    protected void onPause() {
        //PgyFeedbackShakeManager.unregister();
        super.onPause();
    }
    @Override
    protected void onStop(){
        super.onStop();
        DemoHelper.getInstance().setAppBadge();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        PgyCrashManager.unregister();

    }
    @Subscribe
    public void onEventMainThread(Boolean b) {
    }
    public CenterDialog dialog;
    public void showInfoDialog(String text,DialogInterface.OnCancelListener onCancelListener) {
        if(dialog==null){
            dialog=new CenterDialog(this);
        }
        dialog.setContentView(R.layout.mydialog_info);
        dialog.setText1(text);
        if (onCancelListener != null) {
            dialog.setCancelable(true);
            dialog.setOnCancelListener(onCancelListener);
        }
        dialog.myShow();
    }
    public void showErrorDialog(String text) {
        showErrorDialog(text, null);
    }
    public void showErrorDialog(String text, View.OnClickListener clickListener) {
        if(dialog==null){
            dialog=new CenterDialog(this);
        }
        dialog.setContentView(R.layout.mydialog_error);
        dialog.setText1(text);
        if(clickListener!=null){
            dialog.findViewById(R.id.cancel).setOnClickListener(clickListener);
        }
        dialog.myShow();
    }


    public void showWarningDialog(String text, View.OnClickListener clickListener) {
        if(dialog==null){
            dialog=new CenterDialog(this);
        }
        dialog.setContentView(R.layout.mydialog_warning);
        dialog.setText1(text);
        try {
            Button btn_ok= (Button) dialog.findViewById(R.id.btn_ok);
            if(clickListener==null){
                btn_ok.setVisibility(View.GONE);
            }else {
                btn_ok.setVisibility(View.VISIBLE);
                btn_ok.setOnClickListener(clickListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.myShow();

    }
    public void showSuccessDialog(String text, DialogInterface.OnCancelListener onCancelListener) {
        if(dialog==null){
            dialog=new CenterDialog(this);
        }
        dialog.setContentView(R.layout.mydialog_success);
        dialog.setText1(text);
        if (onCancelListener != null) {
            dialog.setCancelable(true);
            dialog.setOnCancelListener(onCancelListener);
        }
        dialog.myShow();
    }
    private PermissionsChecker mPermissionsChecker; // 权限检测器
    protected String[] PERMISSIONS = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
    };
    protected void needCheckerPermission(){
        mPermissionsChecker = new PermissionsChecker(this);

    }
    private static final int REQUEST_CODE = 0; // 请求码
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }
    }
}
