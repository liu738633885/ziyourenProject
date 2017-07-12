package com.huaqie.wubingjie.task;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.activity.BaseActivity;
import com.huaqie.wubingjie.activity.CancelServeTaskActivity;
import com.huaqie.wubingjie.activity.ComplainActivity;
import com.huaqie.wubingjie.activity.FinishActivity;
import com.huaqie.wubingjie.activity.ImagePagerActivity;
import com.huaqie.wubingjie.activity.MapActivity;
import com.huaqie.wubingjie.model.GrabOrder;
import com.huaqie.wubingjie.model.MyLatlng;
import com.huaqie.wubingjie.model.event.EventRefresh;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.model.order.OrderInfo;
import com.huaqie.wubingjie.model.task.TaskItem;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;
import com.huaqie.wubingjie.utils.CommonUtils;
import com.huaqie.wubingjie.utils.DensityUtil;
import com.huaqie.wubingjie.utils.SwlUtils;
import com.huaqie.wubingjie.utils.T;
import com.huaqie.wubingjie.utils.ViewUtils;
import com.huaqie.wubingjie.utils.imageloader.ImageLoader;
import com.huaqie.wubingjie.widgets.MorePopupWindow;
import com.huaqie.wubingjie.widgets.ScrollableLayout;
import com.huaqie.wubingjie.widgets.TitleBar;
import com.lewis.utils.DateUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import static com.huaqie.wubingjie.R.id.editTextBodyLl;
import static com.huaqie.wubingjie.R.id.swl;

public class ServeTaskDetailActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{
    private ScrollableLayout mScrollLayout;
    private CommentOrderFragment fragment;
    private ImageView imv_avatar, imv_tag;
    private TextView tv_nickname, tv_school_isstudent, tv_create_time, tv_price, tv_title, tv_content, tv_member_sex, tv_classname, tv_address, tv_finish_time, tv_phonenum;
    private CheckBox cb_grab, cb_finish, cb_jiedan;
    private LinearLayout layout_pic, ll_bottom, mEditTextBody;
    //private TextView tv_one_comment;
    //private LinearLayout ll_one_order;
    private RadioGroup rg;
    public static final String KEY_ID = "key_id";
    public static final String KEY_TYPE = "key_type";
    private TaskItem taskItem;
    private String id;
    private String type;
    private Button btn1, btn2;
    private RadioButton rb_commment;
    private EditText mEditText;
    private TitleBar titleBar;
    private TextView tv_send;
    private MorePopupWindow morePopupWindow;
    private SwipeRefreshLayout swipeRefreshLayout;
   /* private TextView tv_order_nickname,tv_order_school_isstudent,tv_order_phonenum;
    private ImageView imv_order_avatar;
    private Button btn_order_statue;*/

    @Override
    protected int getContentViewId() {
        return R.layout.activity_serve_task_detail;
    }

    protected void handleIntent(Intent intent) {
        id = intent.getStringExtra(KEY_ID);
        type = intent.getStringExtra(KEY_TYPE);
    }

    public static void goTo(Context context, String id, String type) {
        Intent intent = new Intent(context, ServeTaskDetailActivity.class);
        intent.putExtra(KEY_ID, id);
        intent.putExtra(KEY_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar= (TitleBar) findViewById(R.id.titlebar);
        morePopupWindow = new MorePopupWindow(ServeTaskDetailActivity.this);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(swl);
        mScrollLayout = (ScrollableLayout) findViewById(R.id.scrollableLayout);
        //初始化 headview 数据
        imv_avatar = (ImageView) findViewById(R.id.imv_avatar);
        imv_tag = (ImageView) findViewById(R.id.imv_tag);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        tv_school_isstudent = (TextView) findViewById(R.id.tv_school_isstudent);
        tv_create_time = (TextView) findViewById(R.id.tv_create_time);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_member_sex = (TextView) findViewById(R.id.tv_member_sex);
        tv_classname = (TextView) findViewById(R.id.tv_classname);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_finish_time = (TextView) findViewById(R.id.tv_finish_time);
        tv_phonenum = (TextView) findViewById(R.id.tv_phonenum);
        cb_grab = (CheckBox) findViewById(R.id.cb_grab);
        cb_finish = (CheckBox) findViewById(R.id.cb_finish);
        cb_jiedan = (CheckBox) findViewById(R.id.cb_jiedan);
        layout_pic = (LinearLayout) findViewById(R.id.layout_pic);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
        mEditTextBody = (LinearLayout) findViewById(editTextBodyLl);
        mEditText = (EditText) findViewById(R.id.circleEt);
        tv_send = (TextView) findViewById(R.id.tv_send);
        tv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment();
            }
        });
        //tv_one_comment = (TextView) findViewById(R.id.tv_one_comment);
        //ll_one_order = (LinearLayout) findViewById(R.id.ll_one_order);
        rb_commment = (RadioButton) findViewById(R.id.rb_commment);

        //初始化 fragemnt
        fragment = CommentOrderFragment.newInstance(id, type);
        getSupportFragmentManager().beginTransaction().add(R.id.content, fragment).commit();
        mScrollLayout.getHelper().setCurrentScrollableContainer(fragment);
        rg = (RadioGroup) findViewById(R.id.rg);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_order:
                        if (fragment != null) {
                            fragment.changeAdapter(0);
                        }
                        break;
                    case R.id.rb_commment:
                        if (fragment != null) {
                            fragment.changeAdapter(1);
                        }
                        break;
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);
        SwlUtils.initSwl(this,swipeRefreshLayout);
        mScrollLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mEditTextBody.getVisibility() == View.VISIBLE) {
                    updateEditTextBodyVisible(View.GONE);
                    return true;
                }
                return false;
            }
        });
        mScrollLayout.setOnScrollListener(new ScrollableLayout.OnScrollListener() {
            @Override
            public void onScroll(int currentY, int maxY) {
                //ViewHelper.setTranslationY(headView, (float) (currentY * 0.5));
                if (currentY == 0) {
                    if (!swipeRefreshLayout.isRefreshing() && !swipeRefreshLayout.isEnabled()) {
                        swipeRefreshLayout.setEnabled(true);
                    }
                } else {
                    if (!swipeRefreshLayout.isRefreshing() && swipeRefreshLayout.isEnabled()) {
                        swipeRefreshLayout.setEnabled(false);
                    }
                }
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEditTextBodyVisible(View.VISIBLE);
            }
        });
       onRefresh();
    }

    private void addComment() {
        if (TextUtils.isEmpty(mEditText.getText().toString())) {
            T.showShort(this, "评论字数不能为空");
            return;
        }
        NetBaseRequest request = RequsetFactory.creatBaseRequest(this, Constants.ADD_COMMENT);
        request.add("serve_task_id", id);
        request.add("type", type);
        request.add("comment_content", mEditText.getText().toString());
        CallServer.getRequestInstance().add(this, 0x03, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    T.showShort(ServeTaskDetailActivity.this, "评论成功");
                    updateEditTextBodyVisible(View.GONE);
                    mEditText.getText().clear();
                    if (fragment != null) {
                        fragment.changeAdapter(1);
                        fragment.callComment(0);
                    }
                    //评论+1
                    taskItem.setComment_num(taskItem.getIntComment_num() + 1);
                    updataCommentUI(taskItem.getIntComment_num());
                } else {
                    T.showShort(ServeTaskDetailActivity.this, netBaseBean.getMessage());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                T.showShort(ServeTaskDetailActivity.this, "评论失败");
            }
        }, true, true);

    }


    private void callDetail() {
        swipeRefreshLayout.setRefreshing(true);
        NetBaseRequest netBaseRequest = RequsetFactory.creatBaseRequest(this, Constants.SERVE_TASK_DETAIL);
        netBaseRequest.add("serve_task_id", id);
        netBaseRequest.add("type", type);
        CallServer.getRequestInstance().add(this, 0x01, netBaseRequest, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    taskItem = netBaseBean.parseObject(TaskItem.class);
                    updataUI();
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, false, true);
    }

    private void updataUI() {
        try {
            updataTitleBarUI();
            ViewUtils.setAvatar(this, taskItem.getMember_avatar(), imv_avatar, taskItem.getUid());
            if (taskItem.getType().equals("1")) {
                imv_tag.setImageResource(R.mipmap.icon_tag01);
            } else {
                imv_tag.setImageResource(R.mipmap.icon_tag02);
            }
            tv_nickname.setText(taskItem.getMember_nickname());
            String isstudent = "";
            if (taskItem.getMember_is_student().equals("1")) {
                isstudent = "在校生";
            } else if (taskItem.getMember_is_student().equals("2")) {
                isstudent = "毕业";
            } else {
                isstudent = "其他";
            }
            tv_school_isstudent.setText(taskItem.getMember_school() + "   " + isstudent);
            tv_create_time.setText(DateUtils.translateDate2(taskItem.getLongCreate_time(), System.currentTimeMillis() / 1000) + "  发布");
            tv_price.setText(taskItem.getDoublePrice() > 0 ? "￥  " + taskItem.getPrice() + "元" : "免费");
            tv_title.setText(taskItem.getTitle());
            tv_content.setText(taskItem.getContent());
            //渲染图片
            if (taskItem.getPic_list().size() > 0) {
                layout_pic.setVisibility(View.VISIBLE);
                layout_pic.removeAllViews();
                for (int i = 0; i < taskItem.getPic_list().size(); i++) {
                    ImageView imvpic = new ImageView(this);
                    final int width = DensityUtil.dip2px(this, 75);
                    LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(width, width);
                    ll.setMargins(0, 0, DensityUtil.dip2px(this, 10), 0);
                    imvpic.setLayoutParams(ll);
                    imvpic.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imvpic.setAdjustViewBounds(true);
                    imvpic.setBackgroundResource(R.color.loading);
                    layout_pic.addView(imvpic);
                    ImageLoader.load(this, taskItem.getPic_list().get(i), false, 1, 0, 0, imvpic);
                    final int finalI = i;
                    imvpic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ImagePagerActivity.startImagePagerActivity(ServeTaskDetailActivity.this,taskItem.getPic_list(), finalI,new ImagePagerActivity.ImageSize(width,width));
                        }
                    });
                }
            } else {
                layout_pic.setVisibility(View.GONE);
                layout_pic.removeAllViews();
            }
            updataStatusUI();
            if (taskItem.getMember_sex().equals("1")) {
                tv_member_sex.setText("性别:  " + "男生");
            } else if (taskItem.getMember_sex().equals("2")) {
                tv_member_sex.setText("性别:  " + "女生");
            } else {
                tv_member_sex.setText("性别:  " + "不限男女");
            }

            tv_classname.setText("类型:  " + taskItem.getClass_name());
            tv_address.setText(taskItem.getAddress());
            ((ViewGroup)tv_address.getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MapActivity.goTo(ServeTaskDetailActivity.this, new MyLatlng(taskItem.getAddress(), taskItem.getLat(), taskItem.getLng()));
                }
            });
            tv_finish_time.setText("完成时间:  " + DateUtils.translateDate2(taskItem.getLongFinish_time(), System.currentTimeMillis() / 1000));
            //tv_phonenum.setText("发布人电话:  " + taskItem.getMember_phone());
            ViewUtils.setPhone(this,tv_phonenum,"发布人电话:  ",taskItem.getMember_phone());
            updataOrderUI(taskItem.getOrder_info());
            updataCommentUI(taskItem.getIntComment_num());
            setBottomLayout(taskItem.getOrder_finish_status());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updataStatusUI() {
        cb_grab.setChecked(false);
        cb_finish.setChecked(false);
        cb_jiedan.setChecked(false);
        if (taskItem.getPeopleType() == 2) {
            //多人
            cb_grab.setText("被抢了 " + taskItem.getIntGrab_num() + "/" + taskItem.getIntPeople_num());
        }
        switch (taskItem.getStatus()) {
            case 1:
                break;
            case 2:
                cb_grab.setChecked(true);
                break;
            case 3:
                cb_grab.setChecked(true);
                cb_finish.setChecked(true);
                break;
            case 4:
                cb_grab.setChecked(true);
                cb_finish.setChecked(true);
                cb_jiedan.setChecked(true);
                break;
            default:
        }
    }

    private void updataTitleBarUI() {
        if (!TextUtils.isEmpty(taskItem.getRole()) && taskItem.getRole().equals("2") && taskItem.getStatus()!= 4) {
            morePopupWindow.getBtn_cancel().setVisibility(View.VISIBLE);
        }else {
            morePopupWindow.getBtn_cancel().setVisibility(View.GONE);
        }
        morePopupWindow.getBtn_cancel().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(taskItem.getOrder_id())) {
                    CancelServeTaskActivity.goTo(ServeTaskDetailActivity.this, taskItem.getOrder_id(), CancelServeTaskActivity.CANCEL, ServeTaskDetailActivity.class.getSimpleName());
                } else {
                    T.showShort(ServeTaskDetailActivity.this, "order_id为 nulll");
                }
                morePopupWindow.dismiss();
            }
        });
        morePopupWindow.getBtn_tousu().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ComplainActivity.goTo(ServeTaskDetailActivity.this, taskItem.getType(), taskItem.getServe_task_id());
                morePopupWindow.dismiss();
            }
        });
        if (taskItem.getRole().equals("1")) {
            titleBar.getRight_imv().setImageResource(R.mipmap.icon_clean);
            titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showWarningDialog("是否关闭任务?", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            closeTaskServe();
                            dialog.myDismiss();
                        }
                    });
                }
            });
        } else {
            titleBar.getRight_imv().setImageResource(R.mipmap.icon_more);
            titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    morePopupWindow.showPopupWindow(view);
                }
            });
        }
    }

    private void updataOrderUI(List<OrderInfo> list) {
        // ll_one_order.setVisibility(View.GONE);
        if (list == null || list.size() < 1) {
            return;
        }
       /* if (taskItem.getPeopleType() == 1) {
            ll_one_order.setVisibility(View.VISIBLE);

        } else {
            ll_one_order.setVisibility(View.GONE);*/
        //}
        fragment.setOrderData(list);
    }

    private void updataCommentUI(int num) {
        /*if (taskItem.getPeopleType() == 1) {
            tv_one_comment.setVisibility(View.VISIBLE);
            rg.setVisibility(View.GONE);
            tv_one_comment.setText("评论  (" + num + ")");
        } else {*/
        //tv_one_comment.setVisibility(View.GONE);
            rg.setVisibility(View.VISIBLE);
            rb_commment.setText("评论  (" + num + ")");
        //}
    }

    private void setBottomLayout(String status) {
        btn1.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(taskItem.getFinish_msg()) || TextUtils.isEmpty(status) || status.equals("0")) {
            btn2.setVisibility(View.GONE);
            btn1.setText("发布评论");
        } else {
            btn2.setVisibility(View.VISIBLE);
            btn2.setText(taskItem.getFinish_msg());
            btn1.setText("评论");
        }
        btn2.setEnabled(true);
        switch (status) {
            case "1":
                //暂停接单
                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        suspendServeTask();
                    }
                });
                break;
            case "2":
                //我要接单
                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        grabOrder();
                    }
                });
                break;
            case "3":
                //确认完成
                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FinishActivity.goTo(ServeTaskDetailActivity.this, taskItem.getOrder_id(), taskItem.getFinish_msg(), ServeTaskDetailActivity.class.getSimpleName());
                    }
                });
                break;
            case "4":
                //确认完成（灰）
                btn2.setEnabled(false);
                break;
            case "5":
                //取消进行中(灰)
                btn2.setEnabled(false);
                break;
            case "6":
                //订单已取消(灰)
                btn2.setEnabled(false);
                break;
            case "7":
                //开始接单
                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        suspendServeTask();
                    }
                });
                break;
            default:
        }
    }

    public void updateEditTextBodyVisible(int visibility) {
        mEditTextBody.setVisibility(visibility);
        if (View.VISIBLE == visibility) {
            mEditText.requestFocus();
            //弹出键盘
            CommonUtils.showSoftInput(mEditText.getContext(), mEditText);
            //ll_bottom.setVisibility(View.GONE);
        } else if (View.GONE == visibility) {
            //隐藏键盘
            CommonUtils.hideSoftInput(mEditText.getContext(), mEditText);
            //ll_bottom.setVisibility(View.VISIBLE);
        }

    }
    private void suspendServeTask() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(this, Constants.SUSPEND_SERVE_TASK);
        request.add("serve_task_id", id);
        request.add("type", type);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    callDetail();
                } else {
                    T.showShort(ServeTaskDetailActivity.this, netBaseBean.getMessage());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

    private void grabOrder() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(this, Constants.GRAB_ORDER);
        request.add("serve_task_id", id);
        request.add("type", type);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    if (TextUtils.isEmpty(netBaseBean.getBody())) {
                        callDetail();
                    } else {
                        GrabOrder grabOrder = netBaseBean.parseObject(GrabOrder.class);
                        if (grabOrder.getIs_pay() == 0) {
                            //不需要支付
                            callDetail();
                        } else {
                            //跳转支付
                            PayActivity.goTo(ServeTaskDetailActivity.this, grabOrder.getPay_id(), taskItem.getType(), ServeTaskDetailActivity.class.getSimpleName());
                        }
                    }
                } else {
                    T.showShort(ServeTaskDetailActivity.this, netBaseBean.getMessage());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }
    private void closeTaskServe() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(this, Constants.CLOSE_TASK_SERVE);
        request.add("serve_task_id", id);
        request.add("type", type);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                T.showShort(ServeTaskDetailActivity.this, netBaseBean.getMessage());
                if (netBaseBean.isSuccess()) {
                    finish();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }


    @Subscribe
    public void onEventMainThread(EventRefresh eventRefresh) {
        if (eventRefresh.getAction().equals(EventRefresh.ACTION_REFRESH) && eventRefresh.getWhere().equals(ServeTaskDetailActivity.class.getSimpleName())) {
            callDetail();
        }
    }

    @Override
    public void onRefresh() {
        callDetail();
    }
}
