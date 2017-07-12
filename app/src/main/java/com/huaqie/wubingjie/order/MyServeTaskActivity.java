package com.huaqie.wubingjie.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.activity.BaseActivity;
import com.huaqie.wubingjie.activity.LoginActivity;
import com.huaqie.wubingjie.fragment.BaseFragment;
import com.huaqie.wubingjie.utils.UserManager;

import java.util.ArrayList;
import java.util.List;

public class MyServeTaskActivity extends BaseActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Adapter adapter;
    private List<BaseFragment> list_fragment = new ArrayList<>();

    public static void goTo(Context context) {
        Intent intent;
        if (UserManager.isLogin()) {
            intent = new Intent(context, MyServeTaskActivity.class);
        } else {
            intent = new Intent(context, LoginActivity.class);
        }
        context.startActivity(intent);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_my_serve_task;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.addTab(tabLayout.newTab().setText("全部"));
        tabLayout.addTab(tabLayout.newTab().setText("新发布"));
        tabLayout.addTab(tabLayout.newTab().setText("被抢了"));
        tabLayout.addTab(tabLayout.newTab().setText("待收,待付"));
        tabLayout.addTab(tabLayout.newTab().setText("结单"));
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        list_fragment.add(MySendOrderFragment.newInstance("all"));
        list_fragment.add(MySendOrderFragment.newInstance("new_publish"));
        list_fragment.add(MySendOrderFragment.newInstance("grab"));
        list_fragment.add(MySendOrderFragment.newInstance("pay_collection"));
        list_fragment.add(MySendOrderFragment.newInstance("finish"));
        adapter = new Adapter(getSupportFragmentManager(), list_fragment);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
    public class Adapter extends FragmentStatePagerAdapter {

        final int PAGE_COUNT = 5;
        private String tabTitles[] = new String[]{"全部", "新发布", "被抢了", "待收,待付", "结单"};
        private List<BaseFragment> list_fragment;

        public Adapter(FragmentManager fm, List<BaseFragment> list_fragment) {
            super(fm);
            this.list_fragment = list_fragment;
        }

        @Override
        public Fragment getItem(int position) {
            return list_fragment.get(position);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

}
