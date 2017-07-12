package com.huaqie.wubingjie.utils.imageloader;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.utils.DensityUtil;


/**
 * Created by lewis on 16/7/6.
 */
public class ImageLoader {
    private static DrawableRequestBuilder getBuilder(Context context, String url, boolean title, boolean isheadicon, int type, int denWith, int denHeight,int round) {
        if (title) {
            url = Constants.IMG__HEAD + url;
        }
        DrawableRequestBuilder builder = Glide.with(context).load(url);
        int placehoder;
        if (isheadicon) {
            placehoder = R.mipmap.default_user_icon;
        } else {
            //placehoder = R.color.loading;
            placehoder = R.mipmap.icon_loading;
        }
        builder.placeholder(placehoder);
        if (denWith > 0 && denHeight > 0) {
            builder.override(DensityUtil.getWidth(context) / denWith, DensityUtil.getWidth(context) / denHeight);
        }
        if (type == 1) {
            builder.centerCrop();
        } else if (type == 2) {
            builder.fitCenter();
        }
        if(round==0){
            builder.transform(new GlideCircleTransform(context));
        }else if (round!=-1){
            builder.transform(new GlideRoundTransform(context,round));
        }
        return builder;
    }

    public static void load(Context context, String url, boolean title, boolean isheadicon, int type, int denWith, int denHeight, ImageView imv) {
        try {
            getBuilder(context, url, title, isheadicon, type, denWith, denHeight, -1).into(imv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load(Context context, String url, boolean isheadicon, int type, int denWith, int denHeight, ImageView imv) {
        try {
            if (TextUtils.isEmpty(url)) {
                return;
            }
            if (url.startsWith("http")) {
                getBuilder(context, url, false, isheadicon, type, denWith, denHeight, -1).into(imv);
            } else {
                getBuilder(context, url, true, isheadicon, type, denWith, denHeight, -1).into(imv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load(Context context, String url, ImageView imv) {
        try {
            if (TextUtils.isEmpty(url)) {
                return;
            }
            if (url.startsWith("http")) {
                getBuilder(context, url, false, false, 0, 0, 0, -1).into(imv);
            } else {
                getBuilder(context, url, true, false, 0, 0, 0, -1).into(imv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void loadRound(Context context, String url, ImageView imv) {
        try {
            if (TextUtils.isEmpty(url)) {
                return;
            }
            if (url.startsWith("http")) {
                getBuilder(context, url, false, false, 0, 0, 0, 0).into(imv);
            } else {
                getBuilder(context, url, true, false, 0, 0, 0, 0).into(imv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void loadHeadImage(Context context, String url,boolean title,  ImageView imv, int round) {
        try {
            getBuilder(context, url, title, true, 1, 0, 0, round).into(imv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadHeadImage(Context context, String url, ImageView imv, int round) {
        try {
            if (TextUtils.isEmpty(url)) {
                getBuilder(context, url, false, true, 1, 0, 0, round).into(imv);
                return;
            }
            if (url.startsWith("http")) {
                getBuilder(context, url, false, true, 1, 0, 0, round).into(imv);
            } else {
                getBuilder(context, url, true, true, 1, 0, 0, round).into(imv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadHeadImageNoCache(Context context, String url, ImageView imv, int round) {
        try {
            if (TextUtils.isEmpty(url)) {
                getBuilder(context, url, false, true, 1, 0, 0, round).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(imv);
                return;
            }
            if (url.startsWith("http")) {
                getBuilder(context, url, false, true, 1, 0, 0, round).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(imv);
            } else {
                getBuilder(context, url, true, true, 1, 0, 0, round).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(imv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
