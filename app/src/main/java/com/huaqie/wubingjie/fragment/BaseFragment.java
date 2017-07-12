package com.huaqie.wubingjie.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by lewis on 16/6/15.
 */
public abstract class BaseFragment extends Fragment {
    protected View parentView;

    //获取fragment布局文件ID
    protected abstract int getLayoutId();

    //获取Bundle
    protected void handleBundle(Bundle bundle) {
    }

    //初始化控件
    protected abstract void initView(View view, Bundle savedInstanceState);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(getArguments()!=null){
            handleBundle(getArguments());
        }
        parentView = inflater.inflate(getLayoutId(), container, false);
        initView(parentView, savedInstanceState);
        return parentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(Boolean b) {
    }

    public void baseRefresh() {
    }
}
