package com.huaqie.wubingjie.task;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.activity.BaseActivity;
import com.huaqie.wubingjie.model.MyLatlng;
import com.huaqie.wubingjie.model.event.EventRefresh;
import com.huaqie.wubingjie.utils.CommonUtils;
import com.huaqie.wubingjie.utils.SettingsManager;
import com.huaqie.wubingjie.utils.T;
import com.huaqie.wubingjie.widgets.TitleBar;
import com.huaqie.wubingjie.widgets.dialog.BottomDialog;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;

public class ChooseAddressActivity extends BaseActivity {
    public static final int MODE_CHOOSE = 0;
    public static final int MODE_LOOK = 1;
    //mode  0编辑 有保存键  1查看
    private int mode;
    private RecyclerView rv_search, rv_city;
    boolean isFirstLoc = true; // 是否首次定位
    boolean haveLatLng = false; // 是否已经有点了
    /*百度地图*/
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationClient mLocClient;
    private GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    private EditText keyWorldsView = null;
    private TitleBar titleBar;
    private BottomDialog dialog, dialogChangeCity;
    private PoiSearch mPoiSearch = null;
    private BaseQuickAdapter<PoiInfo> search_adapter;
    private BaseQuickAdapter<CityInfo> city_adapter;
    private TextView tv_city, tv_address;
    //数据源
    private MyLatlng myLatlng;
    private BDLocation mlocation;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_choose_address;
    }

    public static void goTo(Context context, MyLatlng myLatlngs, String where) {
        goTo(context, myLatlngs, MODE_CHOOSE, where);
    }

    public static void goTo(Context context, MyLatlng myLatlng, int mode, String where) {
        Intent intent = new Intent(context, ChooseAddressActivity.class);
        intent.putExtra("latlng", (Serializable) myLatlng);
        intent.putExtra("mode", mode);
        intent.putExtra(KEY_WHERE, where);
        context.startActivity(intent);
    }

    @Override
    protected void handleIntent(Intent intent) {
        where = getIntent().getStringExtra(KEY_WHERE);
        mode = getIntent().getIntExtra("mode", 0);
        myLatlng = (MyLatlng) intent.getSerializableExtra("latlng");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titlebar);
        titleBar.setLeftClike(new TitleBar.LeftClike() {
            @Override
            public void onClick(View view) {
                showFinshDialog();
            }
        });
        if (mode == MODE_CHOOSE) {
            titleBar.setCenterText("选择地点位置");
            titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (myLatlng == null || TextUtils.isEmpty(myLatlng.getAddress()) || TextUtils.isEmpty(myLatlng.getLat()) || TextUtils.isEmpty(myLatlng.getLng())) {
                        T.showShort(ChooseAddressActivity.this, "地址不完整");
                        return;
                    }
                    EventBus.getDefault().post(new EventRefresh(EventRefresh.ACTION_ADDRESS, myLatlng, where));
                    finish();
                }
            });
        } else if (mode == MODE_LOOK) {
            titleBar.setCenterText("查看位置");
            titleBar.getRightGroup().setVisibility(View.INVISIBLE);
        }
        findViewById(R.id.ll_choose_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.myShow();
            }
        });
        tv_address = (TextView) findViewById(R.id.tv_address);
        initDialog();
        initMap();
        firstShowAddress();
    }

    private void initDialog() {
        dialog = new BottomDialog(this);
        dialog.setContentView(R.layout.dialog_search_address, true);
        tv_city = (TextView) dialog.findViewById(R.id.tv_city);
        dialogChangeCity = new BottomDialog(this);
        dialogChangeCity.setContentView(R.layout.dialog_search_address_changecity);
        keyWorldsView = (EditText) dialog.findViewById(R.id.searchkey);
        /**
         * 当输入关键字变化时，动态更新建议列表
         */
        keyWorldsView.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                if (cs.length() <= 0) {
                    return;
                }
                String citystr = TextUtils.isEmpty(mlocation.getCity()) ? SettingsManager.getKeyCityName() : mlocation.getCity();
                String keystr = keyWorldsView.getText().toString();
                Logger.d("搜索时的参数" + mlocation.getCity() + " city" + citystr + ",keyword=" + keystr);
                mPoiSearch.searchInCity((new PoiCitySearchOption())
                        .city(citystr).keyword(keystr).pageNum(0));
            }
        });
        rv_search = (RecyclerView) dialog.findViewById(R.id.rv_search);
        rv_city = (RecyclerView) dialogChangeCity.findViewById(R.id.rv_city);
        search_adapter = new BaseQuickAdapter<PoiInfo>(android.R.layout.simple_expandable_list_item_2, new ArrayList<PoiInfo>()) {
            @Override
            protected void convert(BaseViewHolder baseViewHolder, final PoiInfo o) {
                baseViewHolder.setText(android.R.id.text1, o.name);
                baseViewHolder.setText(android.R.id.text2, o.address);
                baseViewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //T.showShort(mContext, "name" + o.name + "__address" + o.address + "__city" + o.city + "__phoneNum" + o.phoneNum + "__type" + o.type);
                        if (o == null) {
                            return;
                        }
                        myLatlng = new MyLatlng(o.name, o.location.latitude + "", o.location.longitude + "");
                        updateMapState();
                        dialog.dismiss();
                    }
                });
            }
        };
        city_adapter = new BaseQuickAdapter<CityInfo>(android.R.layout.simple_expandable_list_item_1, new ArrayList<CityInfo>()) {
            @Override
            protected void convert(BaseViewHolder baseViewHolder, final CityInfo cityInfo) {
                baseViewHolder.setText(android.R.id.text1, cityInfo.city);
                baseViewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String keystr = keyWorldsView.getText().toString();
                        mPoiSearch.searchInCity((new PoiCitySearchOption())
                                .city(cityInfo.city).keyword(keystr).pageNum(0));
                        dialogChangeCity.myDismiss();
                        tv_city.setVisibility(View.VISIBLE);
                        tv_city.setText(cityInfo.city);
                    }
                });
            }
        };
        rv_search.setLayoutManager(new LinearLayoutManager(this));
        rv_search.setAdapter(search_adapter);
        rv_city.setLayoutManager(new LinearLayoutManager(this));
        rv_city.setAdapter(city_adapter);
    }

    private void initMap() {
        //poi 搜索
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult result) {
                {
                    if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                        Toast.makeText(ChooseAddressActivity.this, "未找到结果", Toast.LENGTH_LONG)
                                .show();
                        return;
                    }
                    if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

                        // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
                        /*String strInfo = "在";
                        for (CityInfo cityInfo : result.getSuggestCityList()) {
                            strInfo += cityInfo.city;
                            strInfo += ",";
                        }
                        strInfo += "找到结果";
                        Toast.makeText(ChosseAddressActivity.this, strInfo, Toast.LENGTH_LONG)
                                .show();*/
                        if (result.getSuggestCityList() != null && result.getSuggestCityList().size() > 0) {
                            dialogChangeCity.myShow();
                            city_adapter.setNewData(result.getSuggestCityList());
                        }
                    }
                    if (result.getAllPoi() == null || result.getAllPoi().size() < 1) {
                        Toast.makeText(ChooseAddressActivity.this, "当前城市未找到结果", Toast.LENGTH_LONG)
                                .show();
                        return;
                    }
                    if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                        search_adapter.setNewData(result.getAllPoi());
                        return;
                    }

                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    T.showLong(ChooseAddressActivity.this, "抱歉，未能找到结果");
                    return;
                }
                LatLng ll = result.getLocation();
                myLatlng = new MyLatlng(result.getAddress(), String.valueOf(ll.latitude), String.valueOf(ll.longitude));
                updateMapState();
            }
        });
        mlocation = new BDLocation();
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(new MyLocationListenner());
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(3000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            /**
             * 单击地图
             */
            public void onMapClick(LatLng point) {
                Logger.d("单击地图");
                myLatlng = new MyLatlng("", point.latitude + "", point.longitude + "");
                updateMapState();
            }

            /**
             * 单击地图中的POI点
             */
            public boolean onMapPoiClick(MapPoi poi) {
                myLatlng = new MyLatlng(poi.getName(), poi.getPosition().latitude + "", poi.getPosition().longitude + "");
                updateMapState();
                return false;
            }
        });
    }


    private void firstShowAddress() {
        if (myLatlng == null || TextUtils.isEmpty(myLatlng.getAddress()) || TextUtils.isEmpty(myLatlng.getLat()) || TextUtils.isEmpty(myLatlng.getLng())) {
            return;
        }
        haveLatLng = true;
        mBaiduMap.clear();
        updateMapState();
    }

    public void moveToLocation(LatLng ll) {
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(18.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    /**
     * 更新地图状态显示面板
     */
    private void updateMapState() {
        String state = "";
        if (myLatlng == null) {
            state = "点击、长按、双击地图以获取经纬度和地图状态";
        } else {
            state = "touchType" + ",当前经度:" + myLatlng.getLng() + ",当前纬度：%f" +
                    myLatlng.getLat();
        }
        state += "\n";
        MapStatus ms = mBaiduMap.getMapStatus();
        state += String.format(
                "zoom=%.1f rotate=%d overlook=%d",
                ms.zoom, (int) ms.rotate, (int) ms.overlook);
        Logger.d(state);

        if (TextUtils.isEmpty(myLatlng.getAddress())) {
            mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                    .location(myLatlng.parse()));
        } else {
            tv_address.setText(myLatlng.getAddress());
        }
        //定位到地图
        mBaiduMap.clear();
        OverlayOptions option = new MarkerOptions()
                .position(myLatlng.parse())
                .icon(BitmapDescriptorFactory
                        .fromResource(R.mipmap.icon_gps_green));
        mBaiduMap.addOverlay(option);
        moveToLocation(myLatlng.parse());
        CommonUtils.hideSoftInput(keyWorldsView.getContext(), keyWorldsView);

    }


    /*回收处理*/
    @Override
    protected void onPause() {
        // MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        //poi检索释放
        mPoiSearch.destroy();
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        // MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        mMapView.onDestroy();
        mSearch.destroy();
        //mSuggestionSearch.destroy();
        mMapView = null;
        super.onDestroy();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mlocation = location;
            Logger.d("我的定位mlocation" + mlocation.getLatitude() + "--" + mlocation.getLongitude() + mlocation.getAddress().toString() + "--" + mlocation.getCity());
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc && haveLatLng == false) {
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(new LatLng(location.getLatitude(),
                        location.getLongitude())).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                isFirstLoc = false;
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    private void showFinshDialog() {
        if (mode == MODE_CHOOSE) {
            new AlertDialog.Builder(this).setMessage("地址还没有保存,确定要退出么?").setPositiveButton("继续编辑", null).setNegativeButton("不选择了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ChooseAddressActivity.this.finish();
                }
            }).show();
        } else if (mode == MODE_LOOK) {
            ChooseAddressActivity.this.finish();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            showFinshDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
