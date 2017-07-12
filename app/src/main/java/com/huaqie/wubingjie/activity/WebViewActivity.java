package com.huaqie.wubingjie.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.widgets.TitleBar;
import com.huaqie.wubingjie.widgets.dialog.LoadingDialog;
import com.orhanobut.logger.Logger;


public class WebViewActivity extends BaseActivity {

    SwipeRefreshLayout mSwipeRefreshLayout;
    private WebView webView;
    //private LoadingDialog loadingDialog;
    private Uri startUri;
    private TitleBar titleBar;
    private boolean showTitle=true;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_web_view;
    }


    public static void goTo(Context context, String url, boolean showTitle) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.setData(Uri.parse(url));
        intent.putExtra("showTitle", showTitle);
        context.startActivity(intent);
    }
    //获取Intent
    protected void handleIntent(Intent intent) {
        showTitle=intent.getBooleanExtra("showTitle",true);
        startUri = intent.getData();
    }
    @Override
    protected void initView(Bundle savedInstanceState) {
        try {
            initLayout();
            setWebView();
            webView.loadUrl(startUri.toString());
            Logger.d(startUri.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 初始化界面
     */
    private void initLayout() {
        //loadingDialog = new LoadingDialog(this);
        webView = (WebView) findViewById(R.id.webView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swl);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                webView.reload();
            }
        });
        titleBar= (TitleBar) findViewById(R.id.titlebar);
        titleBar.setVisibility(showTitle ? View.VISIBLE : View.GONE);
        titleBar.setLeftClike(new TitleBar.LeftClike() {
            @Override
            public void onClick(View view) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
            }
        });
    }

    /**
     * 设置webview
     */
    @SuppressLint({"NewApi", "SetJavaScriptEnabled"})
    private void setWebView() {
        WebSettings webSetting = webView.getSettings();
        // 告诉WebView先不要自动加载图片，等页面finish后再发起图片加载。
        if (Build.VERSION.SDK_INT >= 19) {
            webView.getSettings().setLoadsImagesAutomatically(true);
        } else {
            webView.getSettings().setLoadsImagesAutomatically(false);
        }
        webView.setWebChromeClient(defaultWebChromeClient);// 显示进度条等浏览器选项
        MyWebViewClient webViewClient = new MyWebViewClient();
        webView.setWebViewClient(webViewClient);// 设置在当前webview里面跳转
        webView.setLayerType(View.LAYER_TYPE_NONE, null);
        webSetting.setUseWideViewPort(true);// 可任意比例缩放
        webSetting.setJavaScriptEnabled(true);// 支持js
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setSupportZoom(false); // 支持缩放
        webSetting.setAllowFileAccess(true); // 设置可以访问文件
        webSetting.setLoadsImagesAutomatically(true); // 支持自动加载图片
        webSetting.setAppCacheEnabled(false);
        webSetting.setDomStorageEnabled(true);// 开启 DOM storage 功能
        webSetting.setCacheMode(webSetting.LOAD_DEFAULT);// 设置缓存
        webView.setSaveEnabled(false);
    }

    /**
     * 重写按键单击方法
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /* 希望浏览的网页回退而不是退出浏览器 */
        if (null != webView && webView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {
            webView.goBack();// 网页回退
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();// 退出应用
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    protected void onDestroy() {
        if (webView != null) {// 移除webview
            mSwipeRefreshLayout.removeView(webView);
            webView.removeAllViews();
            webView.destroy();
        }
        super.onDestroy();
    }

    private final WebChromeClient defaultWebChromeClient = new WebChromeClient() {
        public final static String W_TAG = "WebChromeClient";
        public void onProgressChanged(WebView view, int newProgress) {
            Log.i(W_TAG, "onProgressChanged newProgress = " + newProgress);
        }
        @Override
        public void onReceivedTitle(WebView view, String title) {
            titleBar.setCenterText(title);
        }
    };

    public class MyWebViewClient extends WebViewClient {
        private final static String TAG = "MyWebViewClient";

        public MyWebViewClient() {
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("tel:")) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                view.getContext().startActivity(intent);
                return true;
            } else {
                view.loadUrl(url); // 在当前的webview中跳转到新的url
                Log.i(TAG, "loadUrl = " + url);
                return true;
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            //loadingDialog.myShow();
            mSwipeRefreshLayout.setRefreshing(true);
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            // finish之后加载图片
            if (!view.getSettings().getLoadsImagesAutomatically()) {
                view.getSettings().setLoadsImagesAutomatically(true);
            }
            //loadingDialog.myDismiss();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
