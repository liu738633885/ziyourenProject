package com.huaqie.wubingjie.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.activity.BaseActivity;
import com.huaqie.wubingjie.model.UpYun;
import com.huaqie.wubingjie.model.event.EventRefresh;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.model.user.MemberCard;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;
import com.huaqie.wubingjie.setting.MyUserInfoActivity;
import com.huaqie.wubingjie.utils.T;
import com.huaqie.wubingjie.utils.UpLoadManager;
import com.huaqie.wubingjie.utils.imageloader.ImageLoader;
import com.huaqie.wubingjie.widgets.dialog.WaitDialog;
import com.orhanobut.logger.Logger;
import com.upyun.library.listener.UpCompleteListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;

public class CheckCardActivity extends BaseActivity {
    public final static int REQUEST_CODE = 1;
    List<String> pic_list;
    private MemberCard memberCard;
    private LinearLayout ll_type1, ll_type2;
    private EditText edt_card_name, edt_card_number;
    private ImageView imv1, imv2;
    private int backCode;
    private Button btn_commit;
    private WaitDialog waitDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_check_card;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        memberCard = new MemberCard();
        pic_list = new ArrayList<>();
        pic_list.add("");
        pic_list.add("");
        waitDialog = new WaitDialog(this);
        ll_type1 = (LinearLayout) findViewById(R.id.ll_type1);
        ll_type2 = (LinearLayout) findViewById(R.id.ll_type2);
        edt_card_name = (EditText) findViewById(R.id.edt_card_name);
        edt_card_number = (EditText) findViewById(R.id.edt_card_number);
        imv1 = (ImageView) findViewById(R.id.imv1);
        imv2 = (ImageView) findViewById(R.id.imv2);
        btn_commit = (Button) findViewById(R.id.btn_commit);
        ll_type1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                memberCard.setType(1);
                updateUI();
            }
        });
        ll_type2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                memberCard.setType(2);
                updateUI();
            }
        });
        imv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backCode = 1;
                PhotoPickerIntent intent = new PhotoPickerIntent(CheckCardActivity.this);
                intent.setPhotoCount(1);
                intent.setShowCamera(true);
                intent.setShowGif(true);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        imv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backCode = 2;
                PhotoPickerIntent intent = new PhotoPickerIntent(CheckCardActivity.this);
                intent.setPhotoCount(1);
                intent.setShowCamera(true);
                intent.setShowGif(true);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                memberCheckCard();
            }
        });
        getMemberCard();
    }


    private void updateUI() {
        try {
            setChooseLayout(ll_type1, false);
            setChooseLayout(ll_type2, false);
            edt_card_name.setEnabled(false);
            edt_card_number.setEnabled(false);
            imv1.setEnabled(false);
            imv2.setEnabled(false);
            if (memberCard.getType() == 1) {
                setChooseLayout(ll_type1, true);
                edt_card_name.setEnabled(true);
                edt_card_number.setEnabled(true);
                btn_commit.setEnabled(true);
            } else if (memberCard.getType() == 2) {
                setChooseLayout(ll_type2, true);
                imv1.setEnabled(true);
                imv2.setEnabled(true);
                btn_commit.setEnabled(true);
            } else {
                btn_commit.setEnabled(false);
            }
            if (!TextUtils.isEmpty(memberCard.getCard_status()) && !TextUtils.isEmpty(memberCard.getCheck_status())) {
                if (memberCard.getCard_status().equals("1") || memberCard.getCheck_status().equals("1")) {
                    btn_commit.setEnabled(false);
                } else {
                    btn_commit.setEnabled(true);
                }
            } else {
                btn_commit.setEnabled(true);
            }
            //设置图片
            if (memberCard.getCard_pic_list() != null) {
                ImageLoader.load(this, memberCard.getCard_pic_list().get(0), imv1);
                ImageLoader.load(this, memberCard.getCard_pic_list().get(1), imv2);
            }
            edt_card_name.setText(memberCard.getCard_name());
            edt_card_number.setText(memberCard.getCard_number());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void getMemberCard() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(this, Constants.GET_MEMBER_CARD);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    memberCard = netBaseBean.parseObject(MemberCard.class);
                    updateUI();

                } else {
                    T.showShort(CheckCardActivity.this, netBaseBean.getMessage());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

    private void memberCheckCard() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(this, Constants.MEMBER_CHECK_CARD);
        if (memberCard.getType() == 1) {
            if (TextUtils.isEmpty(edt_card_name.getText().toString())) {
                T.showShort(this, "请输入姓名");
                return;
            }
            request.add("card_name", edt_card_name.getText().toString());
            if (TextUtils.isEmpty(edt_card_number.getText().toString())) {
                T.showShort(this, "请输入身份证号码");
                return;
            }
            request.add("card_number", edt_card_number.getText().toString());
        } else if (memberCard.getType() == 2) {
            if (memberCard.getCard_pic_list() == null || memberCard.getCard_pic_list().size() != 2) {
                T.showShort(this, "图片不完整");
                return;
            }
            request.addJsonArray("card_pic_list", memberCard.getCard_pic_list());
        } else {
            T.showShort(this, "请选择验证方式");
        }
        request.add("type", memberCard.getType());
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.getStatus() == 200 || netBaseBean.getStatus() == 2001) {
                    EventBus.getDefault().post(new EventRefresh(EventRefresh.ACTION_REFRESH, null, MyUserInfoActivity.class.getSimpleName()));
                    finish();
                }
                T.showShort(CheckCardActivity.this, netBaseBean.getMessage());
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

    private void setChooseLayout(LinearLayout layout, boolean choose) {
        try {
            if (layout == null) {
                return;
            }
            TextView tv = (TextView) layout.getChildAt(0);
            tv.setTextColor(choose ? ContextCompat.getColor(this, R.color.super_green) : ContextCompat.getColor(this, R.color.gray09));
            CheckBox checkBox = (CheckBox) layout.getChildAt(2);
            checkBox.setChecked(choose);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos =
                        data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                waitDialog.setMessage("上传图片中");
                waitDialog.setCancelable(false);
                waitDialog.setCanceledOnTouchOutside(false);
                waitDialog.show();
                upImg(photos.get(0));
            }
        }
    }

    private void upImg(String path) {
        UpLoadManager.uploadImg(path, new UpCompleteListener() {
            @Override
            public void onComplete(boolean isSuccess, String result) {
                if (isSuccess) {
                    waitDialog.setMessage("上传成功");
                    waitDialog.setCancelable(true);
                    waitDialog.setCanceledOnTouchOutside(true);
                    waitDialog.dismiss();
                    UpYun upYu = JSON.parseObject(result, UpYun.class);
                    Logger.d("上传的图片" + Constants.IMG__HEAD + upYu.getUrl());
                    if (backCode == 1) {
                        if (memberCard.getCard_pic_list() == null || memberCard.getCard_pic_list().size() != 2) {
                            memberCard.setCard_pic_list(pic_list);
                        }
                        memberCard.getCard_pic_list().remove(0);
                        memberCard.getCard_pic_list().add(0, upYu.getUrl());
                    } else if (backCode == 2) {
                        if (memberCard.getCard_pic_list() == null || memberCard.getCard_pic_list().size() != 2) {
                            memberCard.setCard_pic_list(pic_list);
                        }
                        memberCard.getCard_pic_list().remove(1);
                        memberCard.getCard_pic_list().add(1, upYu.getUrl());
                    }
                    updateUI();
                } else {
                    waitDialog.setMessage("上传失败");
                    waitDialog.setCancelable(true);
                    waitDialog.setCanceledOnTouchOutside(true);
                    waitDialog.dismiss();
                }
            }
        }, UpLoadManager.STATIC);
    }

}
