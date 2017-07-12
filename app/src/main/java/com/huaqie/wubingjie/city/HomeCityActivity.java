package com.huaqie.wubingjie.city;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.MainApplication;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.activity.BaseActivity;
import com.huaqie.wubingjie.model.city.CityInfo;
import com.huaqie.wubingjie.model.event.EventRefresh;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.service.LocationService;
import com.huaqie.wubingjie.utils.T;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.indexablelistview.IndexEntity;
import me.yokeyword.indexablelistview.IndexableStickyListView;

public class HomeCityActivity extends BaseActivity {
    private LocationService locationService;
    private IndexableStickyListView mIndexableStickyListView;
    private CityAdapter mAdapter;
    private View cityHeadView;
    //定位位置
    private TextView gps_btn;
    /*mode 为1为首页   2为全部城市*/
    private int mode = 1;
    public static final int MODE_HOME = 1;
    public static final int MODE_ALL = 2;
    private RecyclerView rv_hot_city;
    private HotCityAdapter hotCityAdapter;
    //下载的城市列表
    private List<CityInfo> cityList = new ArrayList<>();
    private List<CityInfo> cityHotList = new ArrayList<>();
    public static void goTo(Context context, String where, int mode) {
        Intent intent = new Intent(context, HomeCityActivity.class);
        intent.putExtra(KEY_WHERE, where);
        intent.putExtra("mode", mode);
        context.startActivity(intent);
    }

    //获取Intent
    @Override
    protected void handleIntent(Intent intent) {
        mode = getIntent().getIntExtra("mode", MODE_HOME);
        where = getIntent().getStringExtra(KEY_WHERE);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_home_city;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        //初始化控件
        initView();
    }

    private void initView() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //城市列表listview
        mIndexableStickyListView = (IndexableStickyListView) findViewById(R.id.home_city_listview);
        //设置适配器
        mAdapter = new CityAdapter(this);
        mIndexableStickyListView.setAdapter(mAdapter);
        cityHeadView = LayoutInflater.from(HomeCityActivity.this).inflate(R.layout.headview_homeactivity, null);
        //定位位置设置
        gps_btn = (TextView) cityHeadView.findViewById(R.id.gps_btn);
        mIndexableStickyListView.getListView().addHeaderView(cityHeadView);
        rv_hot_city= (RecyclerView) cityHeadView.findViewById(R.id.rv_hot_city);
        hotCityAdapter=new HotCityAdapter();
        rv_hot_city.setLayoutManager(new GridLayoutManager(this, 4));
        rv_hot_city.setAdapter(hotCityAdapter);
        //下载城市列表
        callCityList();
        //列表点击事件的监听
        setListViewClickListener();
    }

    //下载城市列表
    private void callCityList() {
        NetBaseRequest netBaseRequest = new NetBaseRequest(Constants.GET_CITY_LIST);
        CallServer.getRequestInstance().add(HomeCityActivity.this, 0x01, netBaseRequest, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    List<CityInfo> list = netBaseBean.parseList(CityInfo.class);
                    if (list != null && list.size() > 0) {
                        cityList = list;
                        mIndexableStickyListView.bindDatas(cityList);
                        cityHotList.clear();
                        cityHotList.add(new CityInfo("0", "", "全国", "", "", ""));
                        for (CityInfo cityInfo:cityList){
                            if (cityInfo.getIs_hot().equals("1")){
                                cityHotList.add(cityInfo);
                            }
                        }
                        hotCityAdapter.setNewData(cityHotList);
                    }

                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, false);
    }

    class HotCityAdapter extends BaseQuickAdapter<CityInfo> {

        public HotCityAdapter() {
            super(R.layout.item_city_hot, new ArrayList<CityInfo>());
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, final CityInfo cityInfo) {
            baseViewHolder.setText(R.id.tv_hot_city, cityInfo.getName());
            baseViewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new EventRefresh(EventRefresh.ACTION_CITY, cityInfo, where));
                    finish();
                }
            });
        }
    }

    /**
     * 列表点击事件的监听
     */
    private void setListViewClickListener() {

        //城市的点击事件
        mIndexableStickyListView.setOnItemContentClickListener(new IndexableStickyListView.OnItemContentClickListener() {
            @Override
            public void onItemClick(View v, IndexEntity indexEntity) {
                CityInfo cityInfo = (CityInfo) indexEntity;
                EventBus.getDefault().post(new EventRefresh(EventRefresh.ACTION_CITY, cityInfo, where));
                finish();
            }
        });

    }

    public BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            try {
                if (null != location && location.getLocType() != BDLocation.TypeServerError && !TextUtils.isEmpty(location.getCityCode())) {
                    Logger.d("定位成功"+location.getLatitude());
                    if (location.getCityCode().equals("0")) {
                        callCityInfo(location.getDistrict());
                    } else {
                        callCityInfo(location.getCity());
                    }
                } else {
                    setCity(false, null,"定位失败"+location.getLocType());
                }
            } catch (Exception e) {
                e.printStackTrace();
                setCity(false, null,"定位失败");
            }
        }
    };

    private void setCity(boolean isok, final CityInfo info, String erroText) {
        if(isok){
            gps_btn.setText(info.getName());
            gps_btn.setEnabled(true);
            gps_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new EventRefresh(EventRefresh.ACTION_CITY, info, where));
                    finish();
                }
            });

        }else {
            gps_btn.setText(erroText);
            gps_btn.setEnabled(false);
        }
    }

    private void callCityInfo(String city_name) {
        NetBaseRequest request = new NetBaseRequest(Constants.GET_CITY_INFO);
        request.add("city_name", city_name);
        CallServer.getRequestInstance().add(HomeCityActivity.this, 0x02, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(final int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    final CityInfo cityInfo = netBaseBean.parseObject(CityInfo.class);
                    setCity(true, cityInfo,"未开通");
                } else {
                    T.showShort(HomeCityActivity.this, netBaseBean.getMessage());
                    setCity(false, null,"未开通");
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                setCity(false, null, "未开通");
            }
        }, true, false);
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
    }
}
