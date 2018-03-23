package com.cretin.studyhelper.fragment.me;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cetin.studyhelper.R;
import com.cretin.studyhelper.app.LocalStorageKeys;
import com.cretin.studyhelper.base.BackFragmentActivity;
import com.cretin.studyhelper.base.BaseFragment;
import com.cretin.studyhelper.base.BaseFragmentActivity;
import com.cretin.studyhelper.fragment.follow.evaluation.EvaluationFragment;
import com.cretin.studyhelper.fragment.follow.study.StudyBarFragment;
import com.cretin.studyhelper.fragment.home.HomeFragment;
import com.cretin.studyhelper.model.CusUser;
import com.cretin.studyhelper.ui.LoginActivity;
import com.cretin.studyhelper.ui.manager.HomeActivityManager;
import com.cretin.studyhelper.ui.manager.MeActivityManager;
import com.cretin.studyhelper.ui.manager.PostActivityManager;
import com.cretin.studyhelper.utils.DataCleanManager;
import com.cretin.studyhelper.utils.KV;
import com.cretin.studyhelper.utils.UiUtils;
import com.cretin.studyhelper.view.CircleImageView;
import com.cretin.studyhelper.view.MyAlertDialog;
import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends BaseFragment {
    public static final String TAG = "MeFragment";
    @Bind( R.id.iv_portrait )
    CircleImageView ivPortrait;
    @Bind( R.id.tv_username )
    TextView tvUsername;
    @Bind( R.id.tv_name )
    TextView tvName;
    @Bind( R.id.iv_arrow )
    ImageView ivArrow;
    @Bind( R.id.rl_me )
    RelativeLayout rlMe;
    @Bind( R.id.tv_cache )
    TextView tvCache;
    @Bind( R.id.ll_clearcache )
    LinearLayout llClearcache;
    @Bind( R.id.tv_yonghuxieyi )
    TextView tvYonghuxieyi;
    @Bind( R.id.tv_exit )
    TextView tvExit;
    @Bind( R.id.tv_my_love )
    TextView tvMyLove;
    @Bind( R.id.ll_mylove )
    LinearLayout llMylove;
    @Bind( R.id.swipe_refresh )
    SwipeRefreshLayout swipeRefresh;
    private CusUser mUserModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_me;
    }

    @Override
    protected void initView(View contentView, Bundle savedInstanceState) {
        setMainTitle("我");
        hidBackBtn();

        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
    }

    @Override
    protected void initData() {
        BmobUser.fetchUserJsonInfo(new FetchUserInfoListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if ( e == null ) {
                    mUserModel = new Gson().fromJson(s, CusUser.class);
                    if ( mUserModel != null ) {
                        KV.put(LocalStorageKeys.USER_INFO, mUserModel);
                    } else {
                        mUserModel = KV.get(LocalStorageKeys.USER_INFO);
                    }

                    if ( mUserModel != null ) {
                        tvUsername.setText("账号：" + mUserModel.getUsername());
                        String nick = mUserModel.getNickname();
                        if ( TextUtils.isEmpty(nick) )
                            nick = "没有昵称的用户";
                        tvName.setText(nick);
                        tvExit.setVisibility(View.VISIBLE);
                        tvUsername.setVisibility(View.VISIBLE);
                        if ( !TextUtils.isEmpty(mUserModel.getAvatar()) )
                            Glide.with(mActivity)
                                    .load(mUserModel.getAvatar())
                                    .into(ivPortrait);
                    }
                } else {
                    UiUtils.showToastInAnyThread("用户信息更新失败");
                    mUserModel = KV.get(LocalStorageKeys.USER_INFO);
                }
            }
        });

        try {
            //获取缓存大小
            String ss = DataCleanManager.getTotalCacheSize(getActivity());
            tvCache.setText(ss);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @OnClick( {R.id.rl_me, R.id.ll_clearcache, R.id.tv_yonghuxieyi, R.id.tv_canyu, R.id.tv_tiezi, R.id.tv_ceping, R.id.tv_fenxiang, R.id.tv_my_love, R.id.tv_exit} )
    public void onViewClicked(View view) {
        Intent intent = null;
        Bundle bundle = null;
        switch ( view.getId() ) {
            case R.id.rl_me:
                intent = new Intent(mActivity, MeActivityManager.class);
                intent.putExtra(BackFragmentActivity.TAG_FRAGMENT, UserInfoFragment.TAG);
                break;
            case R.id.tv_my_love:
//                //我的收藏
                intent = new Intent(mActivity, PostActivityManager.class);
                intent.putExtra(BackFragmentActivity.TAG_FRAGMENT, StudyBarFragment.TAG);
                bundle = new Bundle();
                bundle.putInt("type", StudyBarFragment.TYPE_SHOUCANG);
                intent.putExtra(BaseFragmentActivity.ARGS, bundle);
                break;
            case R.id.tv_tiezi:
//                //我发布的帖子
                intent = new Intent(mActivity, PostActivityManager.class);
                intent.putExtra(BackFragmentActivity.TAG_FRAGMENT, StudyBarFragment.TAG);
                bundle = new Bundle();
                bundle.putInt("type", StudyBarFragment.TYPE_SELF);
                intent.putExtra(BaseFragmentActivity.ARGS, bundle);
                break;
            case R.id.tv_ceping:
//                //我发布的测评
                intent = new Intent(mActivity, PostActivityManager.class);
                intent.putExtra(BackFragmentActivity.TAG_FRAGMENT, EvaluationFragment.TAG);
                bundle = new Bundle();
                bundle.putInt("type", EvaluationFragment.TYPE_ME_CREATE);
                intent.putExtra(BaseFragmentActivity.ARGS, bundle);
                break;
            case R.id.tv_canyu:
//                //我参与的测评
                intent = new Intent(mActivity, PostActivityManager.class);
                intent.putExtra(BackFragmentActivity.TAG_FRAGMENT, EvaluationFragment.TAG);
                bundle = new Bundle();
                bundle.putInt("type", EvaluationFragment.TYPE_ME_JOIN);
                intent.putExtra(BaseFragmentActivity.ARGS, bundle);
                break;
            case R.id.tv_fenxiang:
//                //我分享的内容
                intent = new Intent(mActivity, HomeActivityManager.class);
                intent.putExtra(BackFragmentActivity.TAG_FRAGMENT, HomeFragment.TAG);
                bundle = new Bundle();
                bundle.putInt("type", HomeFragment.TYPE_MESHARE);
                intent.putExtra(BaseFragmentActivity.ARGS, bundle);
                break;
            case R.id.ll_clearcache:
//                //清除应用缓存
                MyAlertDialog alertDialog = new MyAlertDialog(getActivity(), "温馨提示", "是否清除应用缓存？");
                alertDialog.setOnClickListener(new MyAlertDialog.OnPositiveClickListener() {
                    @Override
                    public void onPositiveClickListener(View v) {
                        DataCleanManager.clearAllCache(mActivity);
                        UiUtils.showToastInAnyThread("缓存已清除");
                        try {
                            //获取缓存大小
                            String ss = DataCleanManager.getTotalCacheSize(getActivity());
                            tvCache.setText(ss);
                        } catch ( Exception e ) {
                            e.printStackTrace();
                        }
                    }
                });
                alertDialog.show();
                break;
            case R.id.tv_yonghuxieyi:
                MyAlertDialog myAlertDialog = new MyAlertDialog(mActivity, "提示", "不准笑，这其实是个协议！");
                myAlertDialog.setOnClickListener(new MyAlertDialog.OnPositiveClickListener() {
                    @Override
                    public void onPositiveClickListener(View v) {
                    }
                });
                myAlertDialog.show();
                break;
            case R.id.tv_exit:
                exitLogin();
                break;
        }
        if ( intent != null )
            mActivity.startActivity(intent);
    }

    //    退出登录
    private void exitLogin() {
        final MyAlertDialog myAlertDialog = new MyAlertDialog(mActivity, "温馨提示", "确定退出登录吗?");
        myAlertDialog.setOnClickListener(new MyAlertDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClickListener(View v) {
                KV.put(LocalStorageKeys.USER_INFO, null);
                CusUser.logOut();   //清除缓存用户对象
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        myAlertDialog.show();
    }
}