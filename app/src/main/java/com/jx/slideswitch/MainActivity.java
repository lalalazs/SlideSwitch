package com.jx.slideswitch;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jx.slideswitchlib.SlideSwitch;

public class MainActivity extends AppCompatActivity {
    private String[] mTitles;
    private ViewPager mViewPager;
    private SlideSwitch mSlideSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTitles=getResources().getStringArray(R.array.title);
        mViewPager=(ViewPager)findViewById(R.id.mViewPager);
        mViewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        mSlideSwitch=(SlideSwitch)findViewById(R.id.mPagerSlidingTabStrip);
        mSlideSwitch.setViewPager(mViewPager);
    }

    class MyAdapter extends FragmentStatePagerAdapter{

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            if (position==0){
                return  new Test1Fragment();
            }
            return new Test2Fragment();
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }
    }
}
