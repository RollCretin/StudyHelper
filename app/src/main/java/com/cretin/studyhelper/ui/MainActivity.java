package com.cretin.studyhelper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cetin.studyhelper.R;
import com.cretin.studyhelper.app.LocalStorageKeys;
import com.cretin.studyhelper.base.BackFragmentActivity;
import com.cretin.studyhelper.base.BaseActivity;
import com.cretin.studyhelper.base.BaseFragment;
import com.cretin.studyhelper.base.BaseFragmentActivity;
import com.cretin.studyhelper.eventbus.TimeUpNotify;
import com.cretin.studyhelper.fragment.follow.FollowFragment;
import com.cretin.studyhelper.fragment.home.HomeFragment;
import com.cretin.studyhelper.fragment.me.MeFragment;
import com.cretin.studyhelper.fragment.study.StudyFragment;
import com.cretin.studyhelper.fragment.study.plan.PlaningFragment;
import com.cretin.studyhelper.model.UnfinishedTaskModel;
import com.cretin.studyhelper.ui.manager.CommonBackActivityManager;
import com.cretin.studyhelper.utils.KV;
import com.cretin.studyhelper.utils.NotifyHelper;
import com.cretin.studyhelper.utils.UiUtils;
import com.cretin.studyhelper.view.NoScrollViewPager;
import com.cretin.studyhelper.view.PopupMenuDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

public class MainActivity extends BaseActivity {
    @Bind( R.id.view_pager )
    NoScrollViewPager viewPager;
    @Bind( R.id.rb_01 )
    RadioButton rb01;
    @Bind( R.id.rb_02 )
    RadioButton rb02;
    @Bind( R.id.rb_03 )
    RadioButton rb03;
    @Bind( R.id.rb_04 )
    RadioButton rb04;
    @Bind( R.id.rg_group )
    RadioGroup rgGroup;
    private Map<Integer, BaseFragment> mFragments = new HashMap();
    private int currentPage = 0;
    private int lastPage;

    @Override
    protected void initView(View view) {
        if ( view != null ) {
            hidTitleView();
        }
    }

    @Override
    protected void initData() {
        //检查有没有数据没有处理完
        UnfinishedTaskModel unfinishedTaskModel = KV.get(LocalStorageKeys.UNFINISH_TASK);
        if ( unfinishedTaskModel != null ) {
            Intent intent = new Intent(this, CommonBackActivityManager.class);
            intent.putExtra(BackFragmentActivity.TAG_FRAGMENT, PlaningFragment.TAG);
            Bundle bundle = new Bundle();
            bundle.putSerializable("bean", unfinishedTaskModel);
            intent.putExtra(BaseFragmentActivity.ARGS, bundle);
            startActivity(intent);
        }

        viewPager.setAdapter(new MainAdapter(getSupportFragmentManager()));
        //ViewPager缓存4个界面
        viewPager.setOffscreenPageLimit(4);
        rgGroup.check(R.id.rb_01);

        // 监听RadioGroup的选择事件
        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                lastPage = currentPage;
                switch ( checkedId ) {
                    case R.id.rb_01:
                        currentPage = 0;
                        viewPager.setCurrentItem(currentPage, false);
                        break;
                    case R.id.rb_02:
                        currentPage = 1;
                        viewPager.setCurrentItem(currentPage, false);
                        break;
                    case R.id.rb_03:
                        currentPage = 2;
                        viewPager.setCurrentItem(currentPage, false);
                        break;
                    case R.id.rb_04:
                        currentPage = 3;
                        viewPager.setCurrentItem(currentPage, false);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }


    private long lastBackTime;

    //在需要监听的activity中重写onKeyDown()。
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0 ) {
            long currentTime = System.currentTimeMillis();
            if ( currentTime - lastBackTime > 1 * 1000 ) {
                lastBackTime = currentTime;
                UiUtils.showToastInAnyThread("再按一次退出程序");
            } else {
                MainActivity.this.finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
            return createFragment(position);
        }

        // 一共有几个条目
        @Override
        public int getCount() {
            return 4;
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
                fragment = HomeFragment.newInstance(HomeFragment.TYPE_HOME, "");
            } else if ( position == 1 ) {
                fragment = new FollowFragment();
            } else if ( position == 2 ) {
                fragment = new StudyFragment();
            } else if ( position == 3 ) {
                fragment = new MeFragment();
            }
            if ( fragment != null ) {
                mFragments.put(position, fragment);// 把创建好的Fragment存放到集合中缓存起来
            }
            return fragment;
        } else {
            return fragment;
        }
    }

    @Subscribe
    public void timeUpNotify(TimeUpNotify event) {
        Message message = handler.obtainMessage();
        message.obj = event.getMsg();
        handler.sendMessage(message);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            NotifyHelper.vibrator(MainActivity.this);
            NotifyHelper.sound(MainActivity.this);
            new PopupMenuDialog(MainActivity.this).builder(( String ) msg.obj).setCancelable(false)
                    .setCanceledOnTouchOutside(false).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
