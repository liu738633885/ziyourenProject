package com.huaqie.wubingjie.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.model.event.EventRefresh;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.model.order.OrderInfo;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;
import com.huaqie.wubingjie.utils.T;
import com.huaqie.wubingjie.utils.UpLoadManager;
import com.huaqie.wubingjie.utils.UserManager;
import com.huaqie.wubingjie.widgets.MultiImageView;
import com.huaqie.wubingjie.widgets.MultiImageView2;
import com.huaqie.wubingjie.widgets.TitleBar;
import com.huaqie.wubingjie.widgets.dialog.WaitDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;

public class FinishActivity extends BaseActivity {
    private ArrayList<String> imglist = new ArrayList<String>();
    public final static int REQUEST_CODE = 1;
    private String orderID;
    private String title;
    private EditText edt;
    private MultiImageView2 multiImageView;
    private MultiImageView miv2;
    private TitleBar titleBar;
    private Handler handler = new Handler();
    private WaitDialog waitDialog;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_finish;
    }

    public static void goTo(Context context,String orderID, String title,String where) {
        Intent intent;
        if (UserManager.isLogin()) {
            intent = new Intent(context, FinishActivity.class);
            intent.putExtra("orderID", orderID);
            intent.putExtra("title", title);
            intent.putExtra(KEY_WHERE, where);
        } else {
            intent = new Intent(context, LoginActivity.class);
        }
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        orderID = intent.getStringExtra("orderID");
        title = intent.getStringExtra("title");
        where = intent.getStringExtra(KEY_WHERE);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titlebar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callFinish();
            }
        });
        if(!TextUtils.isEmpty(title)){
            titleBar.setCenterText(title);
        }
        if(TextUtils.isEmpty(orderID)){
            T.showShort(getApplicationContext(), "order_id为 nulll");
            finish();
        }
        waitDialog = new WaitDialog(this);
        waitDialog.setCancelable(false);
        waitDialog.setCanceledOnTouchOutside(false);
        edt = (EditText) findViewById(R.id.edt);
        multiImageView = (MultiImageView2) findViewById(R.id.miv);
        miv2 = (MultiImageView) findViewById(R.id.miv2);

        //监听
        multiImageView.setOnItemClickListener(new MultiImageView2.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());
                ImagePagerActivity.startImagePagerActivity(FinishActivity.this, imglist, position, imageSize);
            }

            @Override
            public void onLastItemClick(View view, int position) {
                if (imglist.size() >= 10) {
                    Toast.makeText(FinishActivity.this, "最多9张", Toast.LENGTH_SHORT).show();
                    return;
                }
                PhotoPickerIntent intent = new PhotoPickerIntent(FinishActivity.this);
                intent.setPhotoCount(9 - imglist.size());
                intent.setShowCamera(true);
                intent.setShowGif(true);
                startActivityForResult(intent, REQUEST_CODE);


            }

            @Override
            public void onDeleteItemClick(View view, final int position) {
                new AlertDialog.Builder(FinishActivity.this).setTitle("删除这张?").setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imglist.remove(position);
                        multiImageView.setList(imglist);
                    }
                }).setNegativeButton("否", null).show();
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                multiImageView.setList(new ArrayList<String>());
            }
        }, 500);
        getFinishMsg();
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
                upImg(photos);
            }
        }
    }


    private void upImg(List<String> list) {

        UpLoadManager.uploadImgS(list, new UpLoadManager.UpLoadListener() {
            @Override
            public void Success(List<String> urls) {
                imglist.addAll(urls);
                multiImageView.setList(imglist);
                waitDialog.dismiss();
                waitDialog.setCancelable(true);
                waitDialog.setCanceledOnTouchOutside(true);
            }

            @Override
            public void Failed(int whereFailed) {
                waitDialog.setMessage("第" + (whereFailed + 1) + "张上传失败");
                waitDialog.setCancelable(true);
                waitDialog.setCanceledOnTouchOutside(true);
                waitDialog.dismiss();
            }
        }, UpLoadManager.STATIC);
    }

    private void callFinish() {
        NetBaseRequest netBaseRequest = RequsetFactory.creatBaseRequest(this, Constants.FINISH_SERVE_TASK);
        netBaseRequest.add("order_id", orderID);
        if (imglist.size() > 0) {
            netBaseRequest.addJsonArray("pic_list", imglist);
        }
        /*if (edt.getText().toString().length() < 5) {
            T.showShort(this, "最少5个字");
            return;
        }*/
        netBaseRequest.add("content", edt.getText().toString());
        CallServer.getRequestInstance().add(this, 0x01, netBaseRequest, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if(netBaseBean.isSuccess()){
                    EventBus.getDefault().post(new EventRefresh(EventRefresh.ACTION_REFRESH, null, where));
                    FinishActivity.this.finish();
                }else {
                    showErrorDialog(netBaseBean.getMessage());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                showErrorDialog("请求失败");
            }
        }, false, true);
    }
    private void getFinishMsg() {
        NetBaseRequest netBaseRequest = RequsetFactory.creatBaseRequest(this, Constants.GET_FINISH_MSG);
        netBaseRequest.add("order_id", orderID);
        CallServer.getRequestInstance().add(this, 0x01, netBaseRequest, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if(netBaseBean.isSuccess()){
                    OrderInfo orderInfo = netBaseBean.parseObject(OrderInfo.class);
                    updateMsgUI(orderInfo);
                }else {
                    showErrorDialog(netBaseBean.getMessage());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                showErrorDialog("请求失败");
            }
        }, false, true);
    }

    private void updateMsgUI(OrderInfo orderInfo) {
        if(!TextUtils.isEmpty(orderInfo.getContent())){
            edt.setText(orderInfo.getContent());
            edt.setEnabled(false);
            titleBar.getRight_tv().setText("完成");
            multiImageView.setVisibility(View.GONE);
        } else {
            titleBar.getRight_tv().setText("发送");
            multiImageView.setVisibility(View.VISIBLE);
        }
        if (orderInfo.getPic_list() != null && orderInfo.getPic_list().size() > 0) {
            miv2.setList(orderInfo.getPic_list());
        } else {

        }
    }

}
