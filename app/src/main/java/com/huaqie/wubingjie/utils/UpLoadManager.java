package com.huaqie.wubingjie.utils;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.model.UpYun;
import com.lewis.utils.DateUtils;
import com.orhanobut.logger.Logger;
import com.upyun.library.common.Params;
import com.upyun.library.common.UploadManager;
import com.upyun.library.listener.SignatureListener;
import com.upyun.library.listener.UpCompleteListener;
import com.upyun.library.listener.UpProgressListener;
import com.upyun.library.utils.UpYunUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lewis on 16/6/24.
 */
public class UpLoadManager {

    /**
     * 压缩的最大大小(粗略)
     */
    final static int compressWidth = 1000;
    final static int compressHeight = 1000;

    private static final String TAG = "UpLoadManager";
    public static String KEY = "+KB4iypkrI6RWmXCvIapO7SkWe4=";
    public static String SPACE = "ziyouren";
    public static final String GOODS = "goods/";
    public static final String STATIC = "static/";
    public static final String AVATAR = "avatar/";

    private static String creatSavePath(String filePath) {
        String suffix = filePath.substring(filePath.lastIndexOf("."));
        String savePath = "/" + DateUtils.getNowDate(DateUtils.YYYYMMDD) + "/" + System.currentTimeMillis();
        savePath += suffix;
        Logger.d("生成的网络 path:" + savePath);
        return savePath;
    }

    private static String creatSavePath(String filePath, String topPath) {
        String suffix = filePath.substring(filePath.lastIndexOf("."));
        String savePath = "";
        savePath = "/uploads/" + topPath + System.currentTimeMillis();
        savePath += suffix;
        Logger.d("生成的网络 path:" + savePath);
        return savePath;
    }
    public static void uploadImg(String filePath, UpCompleteListener completeListener) {
        uploadImg(filePath, completeListener, "");
    }

    public static void uploadImg(String filePath, UpCompleteListener completeListener, String topPath) {
        final Map<String, Object> paramsMap = new HashMap<>();
        //上传空间
        paramsMap.put(Params.BUCKET, SPACE);
        //保存路径，任选其中一个
        paramsMap.put(Params.SAVE_KEY, creatSavePath(filePath, topPath));
        //paramsMap.put(Params.PATH, savePath);
        //可选参数（详情见api文档介绍）
        //paramsMap.put(Params.RETURN_URL, "httpbin.org/post");
        paramsMap.put("x-gmkerl-thumb", "/max/1000");
        //进度回调，可为空
        UpProgressListener progressListener = new UpProgressListener() {
            @Override
            public void onRequestProgress(final long bytesWrite, final long contentLength) {
                Log.d(TAG, (100 * bytesWrite) / contentLength + "%");
            }
        };

        //结束回调，不可为空
        if (completeListener == null) {
            return;
        }

        SignatureListener signatureListener = new SignatureListener() {
            @Override
            public String getSignature(String raw) {
                String sign = UpYunUtils.md5(raw + KEY);
                return sign;
            }
        };

        //压缩图片
        String newFilePath = BitmapUtils.compressImg(filePath, compressWidth, compressHeight);
        Logger.d("压缩过后的path:" + newFilePath);


        //UploadManager.getInstance().formUpload(temp, paramsMap, KEY, completeListener, progressListener);
        UploadManager.getInstance().formUpload(new File(newFilePath), paramsMap, signatureListener, completeListener, progressListener);
        //UploadManager.getInstance().blockUpload(temp, paramsMap, KEY, completeListener, progressListener);
        //UploadManager.getInstance().blockUpload(temp, paramsMap, signatureListener, completeListener, progressListener);

    }
    public static void uploadImgS(final List<String> paths, final UpLoadListener upLoadListener) {
        uploadImgS(paths, upLoadListener, "");
    }

    public static void uploadImgS(final List<String> paths, final UpLoadListener upLoadListener, final String topPath) {
        if (paths.size() < 1) {
            return;
        }
        if (upLoadListener == null) {
            return;
        }
        final List<String> urls = new ArrayList<String>();
        final int[] i = {0};
        UpCompleteListener upCompleteListener = new UpCompleteListener() {
            @Override
            public void onComplete(boolean isSuccess, String result) {

                if (isSuccess) {
                    UpYun upYu = JSON.parseObject(result, UpYun.class);
                    Logger.d("上传完第" + (i[0] + 1) + "张" + "url" + Constants.IMG__HEAD + upYu.getUrl());
                    //urls.add(Constants.IMG__HEAD+upYu.getUrl());
                    urls.add(upYu.getUrl());
                    if (urls.size() >= paths.size()) {
                        upLoadListener.Success(urls);
                    } else {
                        i[0]++;
                        uploadImg(paths.get(i[0]), this, topPath);
                        Logger.d("开始上传第" + (i[0] + 1) + "张");
                    }
                } else {
                    upLoadListener.Failed(i[0]);
                    Logger.d("失败信息" + result);
                }
            }
        };
        uploadImg(paths.get(i[0]), upCompleteListener, topPath);

    }


    public interface UpLoadListener {
        public void Success(List<String> urls);

        public void Failed(int whereFailed);
    }
}
