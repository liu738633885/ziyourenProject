package com.huaqie.wubingjie.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.model.MyLatlng;
import com.orhanobut.logger.Logger;

import java.io.Serializable;

public class MapActivity extends BaseActivity {
    private MyLatlng myLatlng;
    private BDLocation mlocation;
    private TextView tv_address, tv_distance;
    /*百度地图*/
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationClient mLocClient;
    BitmapDescriptor mCurrentMarker;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_map;
    }

    public static void goTo(Context context, MyLatlng myLatlng) {
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra("latlng", (Serializable) myLatlng);
        context.startActivity(intent);
    }

    @Override
    protected void handleIntent(Intent intent) {
        myLatlng = (MyLatlng) intent.getSerializableExtra("latlng");
    }
    @Override
    protected void initView(Bundle savedInstanceState) {
        initMap();
        tv_address= (TextView) findViewById(R.id.tv_address);
        tv_distance= (TextView) findViewById(R.id.tv_distance);
        updateMapState();
    }

    private void initMap() {
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
        mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_gps_red);
        mBaiduMap
                .setMyLocationConfigeration(new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.NORMAL, true, mCurrentMarker,
                        0x00FFFF88, 0x0000FF00));
    }
    /**
     * 更新地图状态显示面板
     */
    private void updateMapState() {
        if(myLatlng==null){
            return;
        }
        if (!TextUtils.isEmpty(myLatlng.getAddress())) {
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
    }

    public void moveToLocation(LatLng ll) {
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(18.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
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
                    .direction(location.getRadius()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            double distance=DistanceUtil.getDistance(new LatLng(location.getLatitude(),
                    location.getLongitude()), myLatlng.parse());
            if(distance>1000){
                tv_distance.setText("距离"+(int)distance/1000+"千米");
            } else if (distance<1){
                tv_distance.setText("距离" + distance + "米");
            }else {
                tv_distance.setText("距离" + (int)distance + "米");
            }
           /* if (isFirstLoc && haveLatLng == false) {
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(new LatLng(location.getLatitude(),
                        location.getLongitude())).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                isFirstLoc = false;
            }*/
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
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
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        // MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }
}
