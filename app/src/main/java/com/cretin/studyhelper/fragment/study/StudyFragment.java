package com.cretin.studyhelper.fragment.study;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.cetin.studyhelper.R;
import com.cretin.studyhelper.base.BaseFragment;
import com.cretin.studyhelper.fragment.study.daily.DailyFragment;
import com.cretin.studyhelper.fragment.study.plan.PlanFragment;
import com.cretin.studyhelper.utils.UiUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudyFragment extends BaseFragment {
    public static final String TAG = "StudyFragment";
    private Map<Integer, BaseFragment> mFragments = new HashMap();
    @Bind( R.id.sliding_tabs )
    TabLayout slidingTabs;
    @Bind( R.id.view_pager )
    ViewPager viewPager;
    @Bind( R.id.ll_root )
    LinearLayout llRoot;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_study;
    }

    @Override
    protected void initView(View contentView, Bundle savedInstanceState) {
        hidTitleView();
        hidProgressView();

        if ( mActivity.isKitkat ) {
            llRoot.setPadding(0, UiUtils.getStatusBarHeight(), 0, 0);
        }

        slidingTabs.addTab(slidingTabs.newTab().setText("计划"), true);
        slidingTabs.addTab(slidingTabs.newTab().setText("日报"), false);
        slidingTabs.setTabMode(TabLayout.MODE_SCROLLABLE);

        slidingTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setAdapter(new MainAdapter(getChildFragmentManager()));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                slidingTabs.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setOffscreenPageLimit(2);
    }

    @Override
    protected void initData() {

    }

    private class MainAdapter extends FragmentStatePagerAdapter {
        public MainAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        // 每个条目返回的fragment
        //  0
        @Override
        public Fragment getItem(int position) {
            //  通过Fragment工厂  生产Fragment
            // BaseFragment baseFragment = FragmentFactory.createFragment(position);
            return createFragment(position);
        }

        // 一共有几个条目
        @Override
        public int getCount() {
            return 2;
        }

        // 返回每个条目的标题
        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
    }

    public BaseFragment createFragment(int position) {
        BaseFragment fragment;
        fragment = mFragments.get(position);
        //在集合中取出来Fragment
        if ( fragment == null ) {  //如果再集合中没有取出来 需要重新创建
            if ( position == 0 ) {
                fragment = new PlanFragment();
            } else if ( position == 1 ) {
                fragment = new DailyFragment();
            }
            if ( fragment != null ) {
                mFragments.put(position, fragment);// 把创建好的Fragment存放到集合中缓存起来
            }
            return fragment;
        } else {
            return fragment;
        }
    }
}
