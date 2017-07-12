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

public class MyOrderActivity extends BaseActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Adapter adapter;
    private List<BaseFragment> list_fragment = new ArrayList<>();
    public static void goTo(Context context) {
        Intent intent;
        if (UserManager.isLogin()) {
            intent = new Intent(context, MyOrderActivity.class);
        } else {
            intent = new Intent(context, LoginActivity.class);
        }
        context.startActivity(intent);
    }
    @Override
    protected int getContentViewId() {
        return R.layout.activity_my_order;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("全部"));
        tabLayout.addTab(tabLayout.newTab().setText("进行中"));
        tabLayout.addTab(tabLayout.newTab().setText("已完成"));
        tabLayout.addTab(tabLayout.newTab().setText("已结单"));
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        list_fragment.add(MyOrderFragment.newInstance("0"));
        list_fragment.add(MyOrderFragment.newInstance("1"));
        list_fragment.add(MyOrderFragment.newInstance("2"));
        list_fragment.add(MyOrderFragment.newInstance("3"));
        adapter = new Adapter(getSupportFragmentManager(), list_fragment);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
    public class Adapter extends FragmentStatePagerAdapter {

        final int PAGE_COUNT = 4;
        private String tabTitles[] = new String[]{"全部", "进行中","已完成","已结单"};
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
