package com.huaqie.wubingjie;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.baidu.mapapi.SDKInitializer;
import com.hyphenate.chatuidemo.DemoHelper;
import com.pgyersdk.crash.PgyCrashManager;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;
import com.huaqie.wubingjie.service.LocationService;

/**
 * Created by lewis on 16/6/16.
 */
public class MainApplication extends MultiDexApplication {
    private static MainApplication instance;
    public Vibrator mVibrator;
    public static String currentUserNick = "";
    public LocationService locationService;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        NoHttp.initialize(this);
        Logger.setTag("NoHttpSample");
        Logger.setDebug(true);// 开始NoHttp的调试模式, 这样就能看到请求过程和日志
        SDKInitializer.initialize(this);
        // 环信
        DemoHelper.getInstance().init(this);
        // 百度地图
        locationService = new LocationService(this);
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        //bug跟踪
        PgyCrashManager.register(this);
    }

    public static MainApplication getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
