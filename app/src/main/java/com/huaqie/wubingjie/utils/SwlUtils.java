package com.huaqie.wubingjie.utils;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;

import com.huaqie.wubingjie.R;

/**
 * Created by lewis on 2016/11/1.
 */

public class SwlUtils {
    public static void initSwl(Activity activity, SwipeRefreshLayout swl){
        swl.setProgressViewOffset(false, 0, DensityUtil.dip2px(activity, 64));
        swl.setColorSchemeResources(R.color.colorPrimary);
    }
}
