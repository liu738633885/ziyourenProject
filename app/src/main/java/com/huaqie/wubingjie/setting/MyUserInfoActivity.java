package com.huaqie.wubingjie.setting;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.activity.BaseActivity;
import com.huaqie.wubingjie.activity.LoginActivity;
import com.huaqie.wubingjie.mine.CheckCardActivity;
import com.huaqie.wubingjie.model.UpYun;
import com.huaqie.wubingjie.model.event.EventRefresh;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.model.user.UserInfo;
import com.huaqie.wubingjie.model.user.UserInfoData;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;
import com.huaqie.wubingjie.utils.BitmapUtils;
import com.huaqie.wubingjie.utils.T;
import com.huaqie.wubingjie.utils.UpLoadManager;
import com.huaqie.wubingjie.utils.UserManager;
import com.huaqie.wubingjie.utils.imageloader.ImageLoader;
import com.huaqie.wubingjie.widgets.TitleBar;
import com.huaqie.wubingjie.widgets.dialog.BottomDialog;
import com.huaqie.wubingjie.widgets.dialog.WaitDialog;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.lewis.utils.DateUtils;
import com.orhanobut.logger.Logger;
import com.soundcloud.android.crop.Crop;
import com.upyun.library.listener.UpCompleteListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;


public class MyUserInfoActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_nickname, tv_birthday, tv_school_isstudent, tv_yq_code, tv_iscard;
    private ImageView imv_avatar;
    private RadioButton rb_man, rb_woman;
    private LinearLayout ll_avatar, ll_nickname, ll_sex, ll_birthday, ll_school_isstudent, ll_iscard;
    private BottomDialog bottomDialog;
    private WaitDialog dialog;
    private TimePickerDialog dateDialog;
    private UserInfo userInfo;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_my_user_info;
    }

    public static void goTo(Context context) {
        Intent intent;
        if (UserManager.isLogin()) {
            intent = new Intent(context, MyUserInfoActivity.class);
        } else {
            intent = new Intent(context, LoginActivity.class);
        }
        context.startActivity(intent);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        bottomDialog = new BottomDialog(this);
        dialog = new WaitDialog(this);
        dialog.setMessage("图片上传中");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        tv_birthday = (TextView) findViewById(R.id.tv_birthday);
        tv_school_isstudent = (TextView) findViewById(R.id.tv_school_isstudent);
        tv_yq_code = (TextView) findViewById(R.id.tv_yq_code);
        tv_iscard = (TextView) findViewById(R.id.tv_iscard);
        imv_avatar = (ImageView) findViewById(R.id.imv_avatar);
        rb_man = (RadioButton) findViewById(R.id.rb_man);
        rb_woman = (RadioButton) findViewById(R.id.rb_woman);
        ll_avatar = (LinearLayout) findViewById(R.id.ll_avatar);
        ll_iscard = (LinearLayout) findViewById(R.id.ll_iscard);
        ll_avatar.setOnClickListener(this);
        ll_iscard.setOnClickListener(this);
        ll_nickname = (LinearLayout) findViewById(R.id.ll_nickname);
        ll_nickname.setOnClickListener(this);
        ll_sex = (LinearLayout) findViewById(R.id.ll_sex);
        ll_sex.setOnClickListener(this);
        ll_birthday = (LinearLayout) findViewById(R.id.ll_birthday);
        ll_birthday.setOnClickListener(this);
        ll_school_isstudent = (LinearLayout) findViewById(R.id.ll_school_isstudent);
        ll_school_isstudent.setOnClickListener(this);
        callUserInfo();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_avatar:
                chooseAvater();
                break;
            case R.id.ll_nickname:
                chooseNickName();
                break;
            case R.id.ll_sex:
                chooseSex();
                break;
            case R.id.ll_birthday:
                chooseBirthday();
                break;
            case R.id.ll_school_isstudent:
                chooseSchool();
                break;
            case R.id.ll_iscard:
                //if (!TextUtils.isEmpty(userInfo.getMember_is_card()) && userInfo.getMember_is_card().equals("1")) {
                    //T.showShort(this, "已验证");
                    startActivity(new Intent(this, CheckCardActivity.class));
                //}
                break;

        }
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

            ImageLoader.loadHeadImage(this, userInfo.getMember_avatar(), imv_avatar, 0);
            tv_nickname.setText(userInfo.getMember_nickname());
            String isstudent = "";
            if (userInfo.getMember_is_student().equals("1")) {
                isstudent = "在校生";
            } else if (userInfo.getMember_is_student().equals("2")) {
                isstudent = "毕业";
            } else {
                isstudent = "其他";
            }
            tv_school_isstudent.setText(userInfo.getMember_school() + "  " + isstudent);
            rb_man.setChecked(false);
            rb_woman.setChecked(false);
            if (userInfo.getMember_sex().equals(UserInfo.SEX_MAN)) {
                rb_man.setChecked(true);
            } else if (userInfo.getMember_sex().equals(UserInfo.SEX_WOMAN)) {
                rb_woman.setChecked(true);
            }
            if (!TextUtils.isEmpty(userInfo.getYq_code())) {
                tv_yq_code.setText(userInfo.getYq_code());
            }
            if (!TextUtils.isEmpty(userInfo.getMember_is_card())){
                switch (userInfo.getMember_is_card()) {
                    case "0":
                        tv_iscard.setText("未认证");
                        break;
                    case "1":
                        tv_iscard.setText("已认证");
                        break;
                    default:
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            tv_birthday.setText(DateUtils.stringToString(userInfo.getMember_birthday(), DateUtils.yyyyMMDD));
        } catch (Exception e) {
            tv_birthday.setText("");
            e.printStackTrace();
        }
    }

    private void chooseAvater() {
        bottomDialog.setContentView(R.layout.dialog_choose_pic);
        bottomDialog.findViewById(R.id.btn_pick_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Crop.pickImage(MyUserInfoActivity.this);
                bottomDialog.myDismiss();
            }
        });
        bottomDialog.show();
    }


    private void chooseNickName() {
        bottomDialog.setContentView(R.layout.dialog_edittext, true);
        TitleBar titleBar = (TitleBar) bottomDialog.findViewById(R.id.titlebar);
        titleBar.setMode(TitleBar.MODE_BACK_TV);
        titleBar.setCenterText("输入昵称");
        titleBar.setRightText("确认");
        final EditText edt = (EditText) bottomDialog.findViewById(R.id.edt);
        if (!TextUtils.isEmpty(userInfo.getMember_nickname())) {
            edt.setText(userInfo.getMember_nickname());
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
                String nickname = edt.getText().toString();
                if (TextUtils.isEmpty(nickname)) {
                    T.showShort(MyUserInfoActivity.this, "请输入昵称");
                } else if (nickname.length() > 10) {
                    T.showShort(MyUserInfoActivity.this, "请输入小于10位的昵称");
                } else {
                    editUserInfo("member_nickname", nickname);
                    bottomDialog.myDismiss();
                }
            }
        });
        bottomDialog.show();
    }

    private void chooseSex() {
        bottomDialog.setContentView(R.layout.dialog_choose_sex);
        bottomDialog.findViewById(R.id.btn_man).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editUserInfo("member_sex", "1");
                bottomDialog.myDismiss();
            }
        });
        bottomDialog.findViewById(R.id.btn_woman).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editUserInfo("member_sex", "2");
                bottomDialog.myDismiss();
            }
        });
        bottomDialog.show();
    }

    private void chooseSchool() {
        bottomDialog.setContentView(R.layout.dialog_choose_school, true);
        TitleBar titleBar = (TitleBar) bottomDialog.findViewById(R.id.titlebar);
        titleBar.setLeftClike(new TitleBar.LeftClike() {
            @Override
            public void onClick(View view) {
                bottomDialog.myDismiss();
            }
        });
        final EditText edt = (EditText) bottomDialog.findViewById(R.id.edt);
        final RadioButton rb1 = (RadioButton) bottomDialog.findViewById(R.id.rb1);
        final RadioButton rb2 = (RadioButton) bottomDialog.findViewById(R.id.rb2);
        final RadioButton rb3 = (RadioButton) bottomDialog.findViewById(R.id.rb3);
        if (!TextUtils.isEmpty(userInfo.getMember_school())) {
            edt.setHint(userInfo.getMember_school());
        }
        if (!TextUtils.isEmpty(userInfo.getMember_is_student())) {
            if (userInfo.getMember_is_student().equals("1")) {
                rb1.setChecked(true);
            } else if (userInfo.getMember_is_student().equals("2")) {
                rb2.setChecked(true);
            } else if (userInfo.getMember_is_student().equals("3")) {
                rb3.setChecked(true);
            } else {

            }
        }
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String schoolname = edt.getText().toString();
                if (TextUtils.isEmpty(schoolname)) {
                    T.showShort(MyUserInfoActivity.this, "请输入学校名称");
                    return;
                }
                if (!rb1.isChecked() && !rb2.isChecked() && !rb3.isChecked()) {
                    T.showShort(MyUserInfoActivity.this, "请选择状态");
                    return;
                }
                editUserInfo("member_school", schoolname);
                if (rb1.isChecked()) {
                    editUserInfo("member_is_student", "1");
                } else if (rb2.isChecked()) {
                    editUserInfo("member_is_student", "2");
                } else if (rb3.isChecked()) {
                    editUserInfo("member_is_student", "3");
                }
                bottomDialog.myDismiss();
            }
        });
        bottomDialog.show();
    }

    private void chooseBirthday() {
        long birthday = 0;
        if (!TextUtils.isEmpty(userInfo.getMember_birthday())) {
            birthday = Long.parseLong(userInfo.getMember_birthday()) * 1000;
        }
        long twoYears = 80L * 365 * 1000 * 60 * 60 * 24L;
        dateDialog = new TimePickerDialog.Builder()
                .setCallBack(
                        new OnDateSetListener() {
                            @Override
                            public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                                editUserInfo("member_birthday", "" + millseconds / 1000);
                            }
                        }
                )
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("选择时间")
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setHourText("时")
                .setMinuteText("分")
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis() - twoYears)
                .setMaxMillseconds(System.currentTimeMillis())
                .setCurrentMillseconds(birthday > 0 ? birthday : System.currentTimeMillis())
                .setThemeColor(ContextCompat.getColor(MyUserInfoActivity.this, R.color.colorPrimary))
                .setType(Type.YEAR_MONTH_DAY)
                .setWheelItemTextNormalColor(ContextCompat.getColor(MyUserInfoActivity.this, R.color.gray01))
                .setWheelItemTextSelectorColor(ContextCompat.getColor(MyUserInfoActivity.this, R.color.colorPrimaryDark))
                .setWheelItemTextSize(12)
                .build();
        dateDialog.show(getSupportFragmentManager(), "all");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }
        if (data == null) {
            return;
        }
        if (requestCode == Crop.REQUEST_PICK) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }

    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(BitmapUtils.getSaveRealPath(), System.currentTimeMillis() + "cropped.png"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            dialog.show();
            UpLoadManager.uploadImg(Crop.getOutput(result).getPath(), new UpCompleteListener() {
                @Override
                public void onComplete(boolean isSuccess, String result) {
                    dialog.dismiss();
                    if (isSuccess) {
                        UpYun upYu = JSON.parseObject(result, UpYun.class);
                        Logger.d("得到的 url" + upYu.getUrl());
                        editUserInfo("member_avatar", upYu.getUrl());
                        Glide.get(MyUserInfoActivity.this).clearMemory();
                    } else {
                        Toast.makeText(MyUserInfoActivity.this, "上传图片失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }, UpLoadManager.AVATAR);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
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
                    T.showShort(MyUserInfoActivity.this, "编辑用户失败");
                }

            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                T.showShort(MyUserInfoActivity.this, "编辑用户失败");
            }
        }, true, true);
    }
    @Subscribe
    public void onEventMainThread(EventRefresh refresh) {
        if(!TextUtils.isEmpty(refresh.getWhere())&&refresh.getWhere().equals(MyUserInfoActivity.class.getSimpleName())){
            if(refresh.getAction().equals(EventRefresh.ACTION_REFRESH)){
                callUserInfo();
            }
        }
    }
}
