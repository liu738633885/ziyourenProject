package com.huaqie.wubingjie.task;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.MainApplication;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.activity.BaseActivity;
import com.huaqie.wubingjie.activity.ImagePagerActivity;
import com.huaqie.wubingjie.activity.MainActivity;
import com.huaqie.wubingjie.model.GrabOrder;
import com.huaqie.wubingjie.model.MyLatlng;
import com.huaqie.wubingjie.model.city.CityInfo;
import com.huaqie.wubingjie.model.event.EventRefresh;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.model.task.TaskClass;
import com.huaqie.wubingjie.model.task.TaskItem;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;
import com.huaqie.wubingjie.service.LocationService;
import com.huaqie.wubingjie.utils.EditInputFilter;
import com.huaqie.wubingjie.utils.T;
import com.huaqie.wubingjie.utils.UpLoadManager;
import com.huaqie.wubingjie.utils.UserManager;
import com.huaqie.wubingjie.widgets.MultiImageView2;
import com.huaqie.wubingjie.widgets.dialog.WaitDialog;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.lewis.utils.DateUtils;
import com.lewis.widgets.MultiLineRadioGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.utils.PhotoPickerIntent;

public class EditServeTaskActivity extends BaseActivity {
    public final static int REQUEST_CODE = 1;
    private EditText edt_title, edt_content, edt_people_num, edt_price, edt_phone;
    private TextView tv_address, tv_finish_time, tv_all_price;
    private RadioGroup rg_class, rg_type, rg_people_type, rg_sex;
    private TaskItem taskItem;
    private TimePickerDialog dateDialog;
    private MultiImageView2 multiImageView;
    private Handler handler = new Handler();
    private WaitDialog waitDialog;
    private RelativeLayout rl_child_class;
    private MultiLineRadioGroup mrg_class;
    private LocationService locationService;
    private Button btn_commit;
    private String cityId = "0";
    private TextWatcher priceWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            double all_price;
            try {
                if (!TextUtils.isEmpty(edt_price.getText().toString()) && !TextUtils.isEmpty(edt_people_num.getText().toString())) {
                    double price = Double.parseDouble(edt_price.getText().toString());
                    double num = Double.parseDouble(edt_people_num.getText().toString());
                    all_price = price * num;
                    all_price=Math.round( all_price * 100 ) / 100.0;
                    if (all_price > 10000&& all_price<1000000) {
                        tv_all_price.setText("合计:" + all_price / 10000 + "万");
                    } else if (all_price >= 1000000) {
                        tv_all_price.setText("合计:" + all_price / 1000000 + "百万");
                    } else {
                        tv_all_price.setText("合计:" + all_price);
                    }

                } else {
                    tv_all_price.setText("合计:" + 0.00);
                }
            } catch (Exception e) {
                e.printStackTrace();
                tv_all_price.setText("合计:" + 0.00);
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    @Override
    protected int getContentViewId() {
        return R.layout.activity_edit_serve_task;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        PERMISSIONS = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
        } ;
        needCheckerPermission();
        //initdata
        taskItem = new TaskItem();
        taskItem.setPic_list(new ArrayList<String>());

        waitDialog = new WaitDialog(this);
        //edittext
        edt_title = (EditText) findViewById(R.id.edt_title);
        edt_content = (EditText) findViewById(R.id.edt_content);
        /*edt_content.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    T.showShort(EditServeTaskActivity.this,"不能输入回车!");
                }
                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });*/
        edt_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int lines = edt_content.getLineCount();
                // 限制最大输入行数
                if (lines > 7) {
                    String str = s.toString();
                    int cursorStart = edt_content.getSelectionStart();
                    int cursorEnd = edt_content.getSelectionEnd();
                    if (cursorStart == cursorEnd && cursorStart < str.length() && cursorStart >= 1) {
                        str = str.substring(0, cursorStart-1) + str.substring(cursorStart);
                    } else {
                        str = str.substring(0, s.length()-1);
                    }
                    // setText会触发afterTextChanged的递归
                    edt_content.setText(str);
                    // setSelection用的索引不能使用str.length()否则会越界
                    edt_content.setSelection(edt_content.getText().length());
                }
            }
        });
        edt_people_num = (EditText) findViewById(R.id.edt_people_num);
        edt_price = (EditText) findViewById(R.id.edt_price);
        InputFilter[] filters = {new EditInputFilter()};
        edt_price.setFilters(filters);
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        //rg
        rg_class = (RadioGroup) findViewById(R.id.rg_class);
        rg_type = (RadioGroup) findViewById(R.id.rg_type);
        rg_people_type = (RadioGroup) findViewById(R.id.rg_people_type);
        rg_sex = (RadioGroup) findViewById(R.id.rg_sex);
        //tv
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_finish_time = (TextView) findViewById(R.id.tv_finish_time);
        tv_all_price = (TextView) findViewById(R.id.tv_all_price);
        //pic
        multiImageView = (MultiImageView2) findViewById(R.id.miv);
        rl_child_class = (RelativeLayout) findViewById(R.id.rl_child_class);
        rl_child_class.setVisibility(View.GONE);
        mrg_class = (MultiLineRadioGroup) findViewById(R.id.mrg_class);
        btn_commit= (Button) findViewById(R.id.btn_commit);


        rg_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_type1:
                        taskItem.setType("1");
                        break;
                    case R.id.rb_type2:
                        taskItem.setType("2");
                        break;
                    default:
                }
            }
        });
        rg_type.getChildAt(1).performClick();
        rg_people_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_people_type1:
                        taskItem.setPeople_type("1");
                        edt_people_num.setText("1");
                        edt_people_num.setEnabled(false);
                        break;
                    case R.id.rb_people_type2:
                        taskItem.setPeople_type("2");
                        edt_people_num.setEnabled(true);
                        break;
                    default:
                }
            }
        });
        rg_people_type.getChildAt(1).performClick();
        rg_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_sex_0:
                        taskItem.setMember_sex("0");
                        break;
                    case R.id.rb_sex_1:
                        taskItem.setMember_sex("1");
                        break;
                    case R.id.rb_sex_2:
                        taskItem.setMember_sex("2");
                        break;
                    default:
                }
            }
        });
        rg_sex.getChildAt(1).performClick();
        edt_price.addTextChangedListener(priceWatcher);
        edt_people_num.addTextChangedListener(priceWatcher);
        tv_finish_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFinishTime();
            }
        });
        tv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyLatlng myLatlng = new MyLatlng(taskItem.getAddress(), taskItem.getLat(), taskItem.getLng());
                ChooseAddressActivity.goTo(EditServeTaskActivity.this, myLatlng, EditServeTaskActivity.class.getSimpleName());
            }
        });
        multiImageView.setOnItemClickListener(new MultiImageView2.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());
                ImagePagerActivity.startImagePagerActivity(EditServeTaskActivity.this, taskItem.getPic_list(), position, imageSize);
            }

            @Override
            public void onLastItemClick(View view, int position) {
                if (taskItem.getPic_list().size() >=3) {
                    Toast.makeText(EditServeTaskActivity.this, "最多3张", Toast.LENGTH_SHORT).show();
                    return;
                }
                PhotoPickerIntent intent = new PhotoPickerIntent(EditServeTaskActivity.this);
                intent.setPhotoCount(3 - taskItem.getPic_list().size());
                intent.setShowCamera(true);
                intent.setShowGif(true);
                startActivityForResult(intent, REQUEST_CODE);
            }

            @Override
            public void onDeleteItemClick(View view, final int position) {
                new AlertDialog.Builder(EditServeTaskActivity.this).setTitle("删除这张?").setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        taskItem.getPic_list().remove(position);
                        multiImageView.setList(taskItem.getPic_list());
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
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addServeTask();
            }
        });
        edt_phone.setText(UserManager.getPhone());
        //call
        callGetClass();
    }

    private void chooseFinishTime() {
        long twoYears = 2L * 365 * 1000 * 60 * 60 * 24L;
        dateDialog = new TimePickerDialog.Builder()
                .setCallBack(
                        new OnDateSetListener() {
                            @Override
                            public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                                taskItem.setFinish_time(millseconds / 1000 + "");
                                updateUI();
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
                .setMinMillseconds(System.currentTimeMillis())
                .setMaxMillseconds(System.currentTimeMillis() + twoYears)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(ContextCompat.getColor(EditServeTaskActivity.this, R.color.colorPrimary))
                .setType(Type.ALL)
                .setWheelItemTextNormalColor(ContextCompat.getColor(EditServeTaskActivity.this, R.color.gray01))
                .setWheelItemTextSelectorColor(ContextCompat.getColor(EditServeTaskActivity.this, R.color.colorPrimaryDark))
                .setWheelItemTextSize(12)
                .build();
        dateDialog.show(getSupportFragmentManager(), "all");
    }

    private void updateUI() {
        try {
            if (!TextUtils.isEmpty(taskItem.getFinish_time())) {
                tv_finish_time.setText(DateUtils.stringToString(taskItem.getFinish_time(), DateUtils.yyyyMMddHHmmss));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callGetClass() {
        NetBaseRequest request = new NetBaseRequest(Constants.GET_CLASS);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<TaskClass> listClass = netBaseBean.parseList(TaskClass.class);
                    for (final TaskClass tclass : listClass) {
                        final RadioButton rb = (RadioButton) LayoutInflater.from(EditServeTaskActivity.this).inflate(R.layout.item_class, rg_class, false);
                        rb.setId(tclass.getIntClass_id());
                        rb.setText(tclass.getClass_name());
                        rg_class.addView(rb);
                        rb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                rg_class.clearCheck();
                                rb.setChecked(true);
                                taskItem.setClass_id(rb.getId() + "");
                                initClassUi(tclass);
                            }
                        });
                    }
                    //rg_class.check(listClass.get(0).getIntClass_id());
                    rg_class.getChildAt(0).performClick();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, false, true);
    }

    private void initClassUi(final TaskClass taskClass) {
        if (taskClass.getChlid() != null && taskClass.getChlid().size() > 0) {
            rl_child_class.setVisibility(View.VISIBLE);
            mrg_class.removeAllViews();
            for (TaskClass childClass : taskClass.getChlid()) {
                mrg_class.append(childClass.getClass_name());
            }
            mrg_class.setOnCheckChangedListener(new MultiLineRadioGroup.OnCheckedChangedListener() {
                @Override
                public void onItemChecked(MultiLineRadioGroup group, int position, boolean checked) {
                    taskItem.setClass_id(taskClass.getChlid().get(position).getClass_id());
                }
            });
            mrg_class.getChildAt(0).performClick();
        } else {
            rl_child_class.setVisibility(View.GONE);
            mrg_class.removeAllViews();
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
                upImg(photos);
            }
        }
    }

    private void upImg(List<String> list) {

        UpLoadManager.uploadImgS(list, new UpLoadManager.UpLoadListener() {
            @Override
            public void Success(List<String> urls) {
                taskItem.getPic_list().addAll(urls);
                multiImageView.setList(taskItem.getPic_list());
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
        }, UpLoadManager.GOODS);
    }

    private void addServeTask() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(this, Constants.ADD_SERVE_TASK);
        //添加类型
        if (TextUtils.isEmpty(taskItem.getClass_id())) {
            T.showShort(this, "请选择类型");
            return;
        }
        request.add("class_id", taskItem.getClass_id());

        //添加标题
        if (TextUtils.isEmpty(edt_title.getText().toString())) {
            //T.showShort(this, "请输入标题");
            showErrorDialog("请输入标题");
            return;
        }
        request.add("title", edt_title.getText().toString());
        //添加内容
        if (TextUtils.isEmpty(edt_content.getText().toString())) {
            //T.showShort(this, "请输入内容");
            showErrorDialog("请输入内容");
            return;
        }
        request.add("content", edt_content.getText().toString());
        //添加图片
        if (taskItem.getPic_list().size() > 0) {
            request.addJsonArray("pic_list", taskItem.getPic_list());
        }
        //添加任务类型
        if (TextUtils.isEmpty(taskItem.getType())) {
            //T.showShort(this, "请选择是服务还是任务");
            showErrorDialog("请选择是服务还是任务");
            return;
        }
        request.add("type", taskItem.getType());
        //添加人数类型
        if (TextUtils.isEmpty(taskItem.getPeople_type())) {
            //T.showShort(this, "请选择单人还是多人");
            showErrorDialog("请选择单人还是多人");
            return;
        }
        request.add("people_type", taskItem.getPeople_type());
        //添加人数
        if (TextUtils.isEmpty(edt_people_num.getText().toString())) {
            return;
        } else {
            int people_num = Integer.parseInt(edt_people_num.getText().toString());
            if (taskItem.getPeople_type().equals("2") && people_num < 2) {
                //T.showShort(this, "人数模式为多人时,人数不能小于2");
                showErrorDialog("人数模式为多人时,人数不能小于2");
                return;
            } else {
                request.add("people_num", people_num + "");
            }
        }
        //添加城市
        request.add("city_id", cityId);
        //添加人数类型
        if (TextUtils.isEmpty(taskItem.getMember_sex())) {
            //T.showShort(this, "请选择性别");
            showErrorDialog("请选择性别");
            return;
        }
        request.add("member_sex", taskItem.getMember_sex());
        //添加地址
        if (TextUtils.isEmpty(taskItem.getAddress()) || TextUtils.isEmpty(taskItem.getLat()) || TextUtils.isEmpty(taskItem.getLng())) {
            //T.showShort(this, "请选择地址");
            showErrorDialog("请选择地址");
            return;
        }
        request.add("address", taskItem.getAddress());
        request.add("lat", taskItem.getLat());
        request.add("lng", taskItem.getLng());
        //添加完成时间
        if (TextUtils.isEmpty(taskItem.getFinish_time())) {
            //T.showShort(this, "请选择完成时间");
            showErrorDialog("请选择完成时间");
            return;
        }
        request.add("finish_time", taskItem.getFinish_time());

        //添加单价
        if (TextUtils.isEmpty(edt_price.getText().toString())) {
            //T.showShort(this, "请输入单价");
            showErrorDialog("请输入单价");
            return;
        }
        if(Double.parseDouble(edt_price.getText().toString())<0.5){
            //T.showShort(this, "单价不小于0.5元");
            showErrorDialog("单价不小于0.5元");
            return;
        }
        request.add("price", edt_price.getText().toString());
        //添加电话号码
        if (TextUtils.isEmpty(edt_phone.getText().toString()) || edt_phone.getText().toString().length() != 11 || !edt_phone.getText().toString().startsWith("1")) {
            //T.showShort(this, "请输入正确的手机号");
            showErrorDialog("请输入正确的手机号");
            return;
        }
        request.add("member_phone", edt_phone.getText().toString());

        //完成
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    if (TextUtils.isEmpty(netBaseBean.getBody())) {
                        showFinishDialog();
                    } else {
                        GrabOrder grabOrder = netBaseBean.parseObject(GrabOrder.class);
                        if (grabOrder.getIs_pay() == 0) {
                            showFinishDialog();
                        } else {
                            //跳转支付
                            PayActivity.goTo(EditServeTaskActivity.this, grabOrder.getPay_id(), taskItem.getType(), EditServeTaskActivity.class.getSimpleName());
                        }
                    }
                    //T.showShort(EditServeTaskActivity.this, "创建成功");

                } else {
                    T.showShort(EditServeTaskActivity.this, netBaseBean.getMessage());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, false, true);

    }
    private void showFinishDialog(){
        showSuccessDialog("创建成功", null);
        new Handler().postDelayed(new TimerTask() {
            @Override
            public void run() {
                dialog.myDismiss();
                EventBus.getDefault().post(new EventRefresh(EventRefresh.ACTION_REFRESH, null, MainActivity.class.getSimpleName()));
                //UserManager.addSendNum();//侧边栏+1
                finish();
            }
        },1500);

    }


    public BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            try {
                if (null != location && location.getLocType() != BDLocation.TypeServerError && !TextUtils.isEmpty(location.getCityCode())) {
                    if (location.getCityCode().equals("0")) {
                        callCityInfo(location.getDistrict());
                    } else {
                        callCityInfo(location.getCity());
                    }
                }else {
                    setCity(false, null, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                setCity(false, null, null);
            }
        }
    };

    private void callCityInfo(String city_name) {
        NetBaseRequest request = new NetBaseRequest(Constants.GET_CITY_INFO);
        request.add("city_name", city_name);
        CallServer.getRequestInstance().add(this, 0x02, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(final int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    final CityInfo cityInfo = netBaseBean.parseObject(CityInfo.class);
                    setCity(true, cityInfo.getId(), cityInfo.getName());
                } else {
                    setCity(false, null, null);
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                setCity(false, null, null);
            }
        }, true, false);
    }

    private void setCity(boolean isok, String cityId, String cityName) {
        if(isok){
            this.cityId = cityId;
            //T.showShort(EditServeTaskActivity.this, "定位城市为:" + cityName);
        } else {
            this.cityId = "0";
            T.showShort(EditServeTaskActivity.this, "定位城市失败,默认为全国");
        }
        btn_commit.setEnabled(true);
        btn_commit.setText("确认");
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }


    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        locationService = ((MainApplication) getApplication()).locationService;
        locationService.registerListener(mListener);
        LocationClientOption option = locationService.getDefaultLocationClientOption();
        option.setScanSpan(0);
        locationService.setLocationOption(option);
        locationService.start();
        btn_commit.setEnabled(false);
        btn_commit.setText("定位当前城市...");
    }


    @Subscribe
    public void onEventMainThread(EventRefresh refresh) {
        if (refresh == null) {
            return;
        }
        if (refresh.getWhere().equals(EditServeTaskActivity.class.getSimpleName())) {
            if (refresh.getAction().equals(EventRefresh.ACTION_ADDRESS) && refresh.getData() != null) {
                MyLatlng myLatlng = (MyLatlng) refresh.getData();
                tv_address.setText(myLatlng.getAddress());
                taskItem.setAddress(myLatlng.getAddress());
                taskItem.setLat(myLatlng.getLat());
                taskItem.setLng(myLatlng.getLng());
            } else if (refresh.getAction().equals(EventRefresh.ACTION_REFRESH)) {
                T.showShort(this, "支付成功");
                showFinishDialog();
            }
        }
    }

}
