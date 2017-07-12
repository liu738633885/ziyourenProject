package com.huaqie.wubingjie.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.fragment.ProductTourFragment;
import com.huaqie.wubingjie.utils.SettingsManager;

public class WelcomeActivity extends BaseActivity {
    int NUM_PAGES = 3;
    PagerAdapter pagerAdapter;
    ViewPager pager;
    TextView tv_go;
    RadioGroup circles;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        tv_go = (TextView) findViewById(R.id.tv_go);
        tv_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTutorial();
            }
        });
        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        circles = (RadioGroup) findViewById(R.id.circles);
        for (int i=0;i<NUM_PAGES;i++){
            LayoutInflater.from(this).inflate(R.layout.item_welcome_point, circles, true);
        }
        setIndicator(0);
        pager.setPageTransformer(true, new CrossfadePageTransformer());
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //pager.setBackgroundColor(ContextCompat.getColor(WelcomeActivity.this, R.color.colorPrimary));
            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
                if (position == NUM_PAGES - 1) {
                    tv_go.setVisibility(View.VISIBLE);
                } else {
                    tv_go.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void setIndicator(int index){
        if(index < NUM_PAGES){
           circles.check(circles.getChildAt(index).getId());
        }
    }
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ProductTourFragment tp = null;
            switch (position) {
                case 0:
                    tp = ProductTourFragment.newInstance(R.layout.welcome_fragment01);
                    break;
                case 1:
                    tp = ProductTourFragment.newInstance(R.layout.welcome_fragment02);
                    break;
                case 2:
                    tp = ProductTourFragment.newInstance(R.layout.welcome_fragment03);
                    break;
            }

            return tp;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
    public class CrossfadePageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View page, float position) {};
    }

    private void endTutorial() {

        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        startActivity(new Intent(this, MainActivity.class));
        SettingsManager.saveAppCode();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pager != null) {
            pager.clearOnPageChangeListeners();
        }
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }

}
