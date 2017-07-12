package com.huaqie.wubingjie.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.MainApplication;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.chat.MessageActivity;
import com.huaqie.wubingjie.city.HomeCityActivity;
import com.huaqie.wubingjie.home.ChooseFilterActivity;
import com.huaqie.wubingjie.mine.AccountActivity;
import com.huaqie.wubingjie.model.city.CityInfo;
import com.huaqie.wubingjie.model.event.EventRefresh;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.model.task.TaskItem;
import com.huaqie.wubingjie.model.task.TaskItemList;
import com.huaqie.wubingjie.model.user.UserInfo;
import com.huaqie.wubingjie.model.user.UserInfoData;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;
import com.huaqie.wubingjie.notice.NoticeListActivity;
import com.huaqie.wubingjie.order.MyOrderActivity;
import com.huaqie.wubingjie.order.MyServeTaskActivity;
import com.huaqie.wubingjie.service.LocationService;
import com.huaqie.wubingjie.setting.MyUserInfoActivity;
import com.huaqie.wubingjie.setting.SettingActivity;
import com.huaqie.wubingjie.task.EditServeTaskActivity;
import com.huaqie.wubingjie.task.HomeTaskAdapter;
import com.huaqie.wubingjie.utils.DensityUtil;
import com.huaqie.wubingjie.utils.SettingsManager;
import com.huaqie.wubingjie.utils.SwlUtils;
import com.huaqie.wubingjie.utils.T;
import com.huaqie.wubingjie.utils.UpdataAppManager;
import com.huaqie.wubingjie.utils.UserManager;
import com.huaqie.wubingjie.utils.imageloader.ImageLoader;
import com.hyphenate.chatuidemo.DemoHelper;
import com.pgyersdk.update.PgyUpdateManager;
import com.yolanda.nohttp.rest.CacheMode;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;


public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private ImageView imv_headicon;
    private DrawerLayout drawer;
    private RecyclerView rv;
    private SwipeRefreshLayout swl;
    private HomeTaskAdapter homeTaskAdapter;
    private int pagenum;
    private View notLoadingView;
    private TextView tv_city, drawer_tv_city;
    //时间,金额,距离
    int[] filter1 = {2, 0, 0};
    //人数,性别,类别,服务与需求
    int[] filter2 = {0, 0, 0, 0};
    private CheckBox cb_filter_time, cb_filter_price, cb_filter_distance;
    private ImageView left_imv;
    private LocationService locationService;
    private TextView tv_member_send_num,tv_member_receive_num;
    private TextView tv_count_message,tv_count_notice;
    private LinearLayout ll_home,ll_ziyouren;
    private LinearLayout ll_main;
    private LinearLayout ll_filter_time, ll_filter_price, ll_filter_distance;
    private int is_zyr=0;
    private RelativeLayout right;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        //drawer
        imv_headicon = (ImageView) findViewById(R.id.imv_headicon);
        imv_headicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUserInfoActivity.goTo(MainActivity.this);
            }
        });
        left_imv = (ImageView) findViewById(R.id.left_imv);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ll_main= (LinearLayout) findViewById(R.id.ll_main);
        findViewById(R.id.fl_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageActivity.goTo(MainActivity.this);
            }
        });
        findViewById(R.id.imv_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
        });
        findViewById(R.id.left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        right= (RelativeLayout) findViewById(R.id.right);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, EditServeTaskActivity.class));
            }
        });
        drawer_tv_city = (TextView) findViewById(R.id.drawer_tv_city);
        tv_member_send_num= (TextView) findViewById(R.id.tv_member_send_num);
        tv_member_receive_num = (TextView) findViewById(R.id.tv_member_receive_num);
        tv_count_message = (TextView) findViewById(R.id.tv_count_message);
        tv_count_notice = (TextView) findViewById(R.id.tv_count_notice);
        ((View)tv_member_send_num.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyServeTaskActivity.goTo(MainActivity.this);
            }
        });
        ((View)tv_member_receive_num.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyOrderActivity.goTo(MainActivity.this);
            }
        });
        findViewById(R.id.ll_qianbao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountActivity.goTo(MainActivity.this);
            }
        });
        findViewById(R.id.ll_notice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoticeListActivity.goTo(MainActivity.this);
            }
        });
        findViewById(R.id.ll_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebViewActivity.goTo(MainActivity.this,Constants.HELP,true);
            }
        });
        findViewById(R.id.ll_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserManager.isLogin()) {
                    WebViewActivity.goTo(MainActivity.this, Constants.ACTIVITY + UserManager.getUid(), true);
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        });
        ll_home= (LinearLayout) findViewById(R.id.ll_home);
        ll_ziyouren = (LinearLayout) findViewById(R.id.ll_ziyouren);
        ll_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setChooseLayout(ll_home, true);
                setChooseLayout(ll_ziyouren, false);
                is_zyr = 0;
                onRefresh();
                drawer.closeDrawers();
                right.setVisibility(View.VISIBLE);

            }
        });
        ll_ziyouren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setChooseLayout(ll_home, false);
                setChooseLayout(ll_ziyouren, true);
                is_zyr = 1;
                onRefresh();
                drawer.closeDrawers();
                right.setVisibility(View.INVISIBLE);
            }
        });



        //choose
        tv_city= (TextView) findViewById(R.id.tv_city);
        findViewById(R.id.imv_filter_other).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseFilterActivity.goTo(MainActivity.this, MainActivity.class.getSimpleName(), filter2);
            }
        });
        findViewById(R.id.ll_choose_city).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeCityActivity.goTo(MainActivity.this, MainActivity.class.getSimpleName(),0);
            }
        });
        cb_filter_time = (CheckBox) findViewById(R.id.cb_filter_time);
        cb_filter_price = (CheckBox) findViewById(R.id.cb_filter_price);
        cb_filter_distance = (CheckBox) findViewById(R.id.cb_filter_distance);
        ll_filter_time = (LinearLayout) findViewById(R.id.ll_filter_time);
        ll_filter_price = (LinearLayout) findViewById(R.id.ll_filter_price);
        ll_filter_distance = (LinearLayout) findViewById(R.id.ll_filter_distance);


        //rv
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        homeTaskAdapter = new HomeTaskAdapter();
        rv.setAdapter(homeTaskAdapter);
        notLoadingView = LayoutInflater.from(this).inflate(R.layout.not_loading, (ViewGroup) rv.getParent(), false);
        homeTaskAdapter.setOnLoadMoreListener(this);
        //swl
        swl = (SwipeRefreshLayout) findViewById(R.id.swl);
        swl.setOnRefreshListener(this);
        swl.setRefreshing(true);
        SwlUtils.initSwl(this,swl);
        onRefresh();
        updataUserUI();

        updateLocationUI();
        updataApp();
        startLoaction();
        //drawlistener
        mDrawerWidth= DensityUtil.dip2px(this,250);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                scrollWidth=slideOffset*mDrawerWidth;
                ll_main.setTranslationX((int)(1*scrollWidth));
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                callUserInfo();
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        drawer.setScrimColor(Color.TRANSPARENT);

    }
    float scrollWidth;
    int mDrawerWidth;
    private void updataApp() {
        UpdataAppManager.updata(this, false);
    }

    private void setChooseLayout(LinearLayout layout, boolean choose) {
        try {
            if (layout == null) {
                return;
            }
            layout.setBackgroundColor(choose ? ContextCompat.getColor(this, R.color.bg_black02) : ContextCompat.getColor(this, R.color.transparent));
            TextView tv = (TextView) layout.getChildAt(1);
            tv.setTextColor(choose ? ContextCompat.getColor(this, R.color.super_green) : ContextCompat.getColor(this, R.color.white));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setChooseLayout2(LinearLayout layout, boolean choose) {
        try {
            if (layout == null) {
                return;
            }
            TextView tv = (TextView) layout.getChildAt(0);
            tv.setTextColor(choose ? ContextCompat.getColor(this, R.color.super_green) : ContextCompat.getColor(this, R.color.gray06));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateCount();
        updataUserNumUI();
    }

    private void updataUserUI() {
        ImageLoader.loadHeadImage(this, UserManager.getKeyAvatar(), imv_headicon, 0);
        ImageLoader.loadHeadImage(this, UserManager.getKeyAvatar(), left_imv, 0);
        if (UserManager.getIsZYR() == 0) {
            ll_ziyouren.setVisibility(View.GONE);
        } else {
            ll_ziyouren.setVisibility(View.VISIBLE);
        }
        //
        updataUserNumUI();
    }
    private void updataUserNumUI(){
        tv_member_send_num.setText(UserManager.getSendNum());
        tv_member_receive_num.setText(UserManager.getReceiveNum());
    }

    private void updateLocationUI() {
        tv_city.setText(SettingsManager.getKeyCityName());
        drawer_tv_city.setText(SettingsManager.getKeyCityName());
        drawer_tv_city.setVisibility(View.GONE);
    }
    //环信消息提示
    public void updateCount() {
        int messageCount = DemoHelper.getInstance().getUnreadMsgCountTotal()+DemoHelper.getInstance().getUnreadAddressCountTotal();
        if (messageCount > 0) {
            tv_count_message.setText(String.valueOf(messageCount));
            tv_count_message.setVisibility(View.VISIBLE);
        } else {
            tv_count_message.setVisibility(View.INVISIBLE);
        }
        int noticeCount= DemoHelper.getInstance().getCountNotice();
        if (noticeCount > 0) {
            tv_count_notice.setText(String.valueOf(noticeCount));
            tv_count_notice.setVisibility(View.VISIBLE);
        } else {
            tv_count_notice.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setFilter(NetBaseRequest request, String key, int filter, boolean isString) {
        if (isString) {
            if (filter == 1) {
                request.add(key, "ASC");
            } else if (filter == 2) {
                request.add(key, "DESC");
            } else {
                request.add(key, "");
            }
        } else {
            request.add(key, filter);
        }
    }

    public void changeFilter1(View v) {
        cb_filter_time.setVisibility(View.INVISIBLE);
        cb_filter_price.setVisibility(View.INVISIBLE);
        cb_filter_distance.setVisibility(View.INVISIBLE);
        setChooseLayout2(ll_filter_distance, false);
        setChooseLayout2(ll_filter_price, false);
        setChooseLayout2(ll_filter_time, false);
        filter1 = new int[]{0, 0, 0};
        switch (v.getId()) {
            case R.id.ll_filter_time:
                cb_filter_time.setVisibility(View.VISIBLE);
                cb_filter_time.toggle();
                if (cb_filter_time.isChecked()) {
                    filter1[0] = 1;
                } else {
                    filter1[0] = 2;
                }
                break;
            case R.id.ll_filter_price:
                cb_filter_price.setVisibility(View.VISIBLE);
                cb_filter_price.toggle();
                if (cb_filter_price.isChecked()) {
                    filter1[1] = 1;
                } else {
                    filter1[1] = 2;
                }
                break;
            case R.id.ll_filter_distance:
                cb_filter_distance.setVisibility(View.VISIBLE);
                cb_filter_distance.toggle();
                if (cb_filter_distance.isChecked()) {
                    filter1[2] = 1;
                } else {
                    filter1[2] = 2;
                }
                break;
            default:
        }
        setChooseLayout2((LinearLayout) v, true);
        onRefresh();
    }

    private void callTasks(int num) {
        NetBaseRequest netBaseRequest = RequsetFactory.creatNoUidRequest(Constants.GET_HOME_DATA);
        netBaseRequest.add("pageno", num);
        netBaseRequest.add("lat", SettingsManager.getLat());
        netBaseRequest.add("lng", SettingsManager.getLng());
        if (num == 0) {
            netBaseRequest.setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE);
            swl.setRefreshing(true);
        }
        //单选
        setFilter(netBaseRequest, "finish_time", filter1[0], true);
        setFilter(netBaseRequest, "price", filter1[1], true);
        setFilter(netBaseRequest, "distance", filter1[2], true);
        //多选
        setFilter(netBaseRequest, "people_type", filter2[0], false);//人数
        setFilter(netBaseRequest, "sex_type", filter2[1], false);//性别
        setFilter(netBaseRequest, "class_id", filter2[2], false);//性别
        setFilter(netBaseRequest, "type", filter2[3], false);
        //是否自由人
        netBaseRequest.add("is_zyr", is_zyr);
        CallServer.getRequestInstance().add(this, num, netBaseRequest, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                swl.setRefreshing(false);
                if (netBaseBean.isSuccess()) {
                    TaskItemList data = netBaseBean.parseObject(TaskItemList.class);
                    List<TaskItem> list = data.getList();
                    pagenum = data.getPageno();
                    boolean isnodata = data.getPageno() == -1 | data.getPageno() == 0;
                    //添加没有更多 view
                    homeTaskAdapter.removeAllFooterView();
                    if (isnodata) {
                        homeTaskAdapter.addFooterView(notLoadingView);
                    }
                    if (what == 0) {
                        homeTaskAdapter.setNewData(list);
                        homeTaskAdapter.openLoadMore(list.size(), !isnodata);
                    } else {
                        homeTaskAdapter.notifyDataChangedAfterLoadMore(list, !isnodata);
                    }
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                swl.setRefreshing(false);
            }
        }, true, false);
    }

    @Override
    public void onRefresh() {
        callTasks(0);
    }

    @Override
    public void onLoadMoreRequested() {
        callTasks(pagenum);
    }

    @Subscribe
    public void onEventMainThread(EventRefresh refresh) {
        if (refresh.getAction().equals(EventRefresh.ACTION_LOGIN)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updataUserUI();
                }
            });
            return;
        }
        if (refresh.getAction().equals(EventRefresh.ACTION_CHAT)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCount();
                }
            });
            return;
        }
        if (!TextUtils.isEmpty(refresh.getWhere()) && refresh.getWhere().equals(MainActivity.class.getSimpleName())) {
            if (refresh.getAction().equals(ChooseFilterActivity.KEY_FILTER)) {
                filter2 = (int[]) refresh.getData();
                onRefresh();
            }else if(refresh.getAction().equals(EventRefresh.ACTION_CITY)){
                CityInfo city = (CityInfo) refresh.getData();
                SettingsManager.setKEY_CITY_ID(city.getId());
                SettingsManager.setKEY_CITY_NAME(city.getName());
                updateLocationUI();
                onRefresh();
            } else if (refresh.getAction().equals(EventRefresh.ACTION_REFRESH)) {
                onRefresh();
            }
        }
    }

    private void startLoaction() {
        locationService = ((MainApplication) getApplication()).locationService;
        locationService.registerListener(mListener);
        LocationClientOption option = locationService.getDefaultLocationClientOption();
        option.setScanSpan(0);
        locationService.setLocationOption(option);
        locationService.start();
    }
    public BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            try {
                if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                    SettingsManager.setLat(location.getLatitude() + "");
                    SettingsManager.setLng(location.getLongitude() + "");
                    if (UserManager.isLogin()) {
                        updateLatLng(location.getLatitude() + "", location.getLongitude() + "");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            onRefresh();
        }
    };

    private void updateLatLng(String lat, String lng) {
        NetBaseRequest netBaseRequest = RequsetFactory.creatNoUidRequest(Constants.UPDATE_LAT_LNG);
        netBaseRequest.add("lat", lat);
        netBaseRequest.add("lng", lng);
        CallServer.getRequestInstance().add(this, 0x01, netBaseRequest, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            }
        }, true, false);
    }
    private void callUserInfo() {
        if(!UserManager.isLogin()){
            return;
        }
        NetBaseRequest getUserRequest = RequsetFactory.creatBaseRequest(this, Constants.GET_USER_INFO);
        CallServer.getRequestInstance().addWithLogin(this, 0x01, getUserRequest, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    UserInfoData data = netBaseBean.parseObject(UserInfoData.class);
                    UserInfo userInfo = data.getInfo();
                    UserManager.saveUserInfo(userInfo);
                    updataUserNumUI();
                }

            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, false);
    }

    @Override
    protected void onPause() {
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //自动更新解注册
        PgyUpdateManager.unregister();
    }
    long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if(drawer.isDrawerOpen(Gravity.LEFT)){
                drawer.closeDrawers();
            }
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                T.showShort(getApplicationContext(), "再按一次退出自由人");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                //System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
