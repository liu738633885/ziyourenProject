package com.huaqie.wubingjie.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaqie.wubingjie.setting.UserInfoActivity;
import com.huaqie.wubingjie.utils.imageloader.ImageLoader;

/**
 * Created by lewis on 2016/11/8.
 */

public class ViewUtils {
    public static void setAvatar(final Context context, String url, ImageView imv, final String uid) {
        ImageLoader.loadHeadImage(context, url, imv, 0);
        if (!TextUtils.isEmpty(uid)) {
            imv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserInfoActivity.goTo(context, uid);
                }
            });
        }
    }

    public static void setPhone(final Context context, TextView tv, final String phoneText, final String phonenum) {
        tv.setText(phoneText + phonenum);
        try {
            if (!phonenum.contains("****")) {
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        Uri data = Uri.parse("tel:" + phonenum);
                        intent.setData(data);
                        context.startActivity(intent);
                    }
                });
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
