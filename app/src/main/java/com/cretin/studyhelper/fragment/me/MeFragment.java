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

import com.cetin.studyhelper.R;
import com.cretin.studyhelper.app.LocalStorageKeys;
import com.cretin.studyhelper.base.BaseFragment;
import com.cretin.studyhelper.model.CusUser;
import com.cretin.studyhelper.ui.LoginActivity;
import com.cretin.studyhelper.utils.DataCleanManager;
import com.cretin.studyhelper.utils.KV;
import com.cretin.studyhelper.view.CircleImageView;
import com.cretin.studyhelper.view.MyAlertDialog;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;


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
    @Bind( R.id.tv_forget_psw )
    TextView tvForgetPsw;
    @Bind( R.id.tv_exit )
    TextView tvExit;
    @Bind( R.id.tv_my_love )
    TextView tvMyLove;
    @Bind( R.id.ll_mylove )
    LinearLayout llMylove;
    @Bind( R.id.swipe_refresh )
    SwipeRefreshLayout swipeRefresh;
    @Bind( R.id.tv_wodedingdan )
    TextView tvWodedingdan;
    @Bind( R.id.tv_wodejiedan )
    TextView tvWodejiedan;
    @Bind( R.id.tv_pinglunwode )
    TextView tvPinglunwode;
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
        mUserModel = BmobUser.getCurrentUser(CusUser.class);
        if ( mUserModel != null ) {
            KV.put(LocalStorageKeys.USER_INFO, mUserModel);
        } else
            mUserModel = KV.get(LocalStorageKeys.USER_INFO);
        if ( mUserModel != null ) {
            tvUsername.setText("账号：" + mUserModel.getUsername());
            String nick = mUserModel.getNickname();
            if ( TextUtils.isEmpty(nick) )
                nick = "没有昵称的用户";
            tvName.setText(nick);
            tvForgetPsw.setVisibility(View.VISIBLE);
            tvExit.setVisibility(View.VISIBLE);
            tvUsername.setVisibility(View.VISIBLE);
            if ( !TextUtils.isEmpty(mUserModel.getAvatar()) )
                Picasso.with(mActivity)
                        .load(mUserModel.getAvatar())
                        .into(ivPortrait);
        }

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

    @OnClick( {R.id.rl_me, R.id.tv_pinglunwode, R.id.ll_clearcache, R.id.tv_wodedingdan, R.id.tv_wodejiedan, R.id.tv_yonghuxieyi, R.id.tv_my_love, R.id.tv_forget_psw, R.id.tv_exit} )
    public void onViewClicked(View view) {
//        Intent intent = null;
//        Bundle bundle = null;
        switch ( view.getId() ) {
//            case R.id.tv_pinglunwode:
//                intent = new Intent(mActivity, MeManager.class);
//                intent.putExtra(BackFragmentActivity.TAG_FRAGMENT, AllMyCommentFragment.TAG);
//                break;
//            case R.id.rl_me:
//                intent = new Intent(mActivity, MeManager.class);
//                intent.putExtra(BackFragmentActivity.TAG_FRAGMENT, UserInfoFragment.TAG);
//                break;
//            case R.id.tv_my_love:
//                //我的收藏
//                intent = new Intent(mActivity, MeManager.class);
//                intent.putExtra(BackFragmentActivity.TAG_FRAGMENT, GoodsListFragment.TAG);
//                bundle = new Bundle();
//                bundle.putInt("type", GoodsListFragment.TYPE_SHOUCANG);
//                intent.putExtra(BaseFragmentActivity.ARGS, bundle);
//                break;
//            case R.id.ll_clearcache:
//                //清除应用缓存
//                MyAlertDialog alertDialog = new MyAlertDialog(getActivity(), "温馨提示", "是否清除应用缓存？");
//                alertDialog.setOnClickListener(new MyAlertDialog.OnPositiveClickListener() {
//                    @Override
//                    public void onPositiveClickListener(View v) {
//                        DataCleanManager.clearAllCache(mActivity);
//                        UiUtils.showToastInAnyThread("缓存已清除");
//                        try {
//                            //获取缓存大小
//                            String ss = DataCleanManager.getTotalCacheSize(getActivity());
//                            tvCache.setText(ss);
//                        } catch ( Exception e ) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//                alertDialog.show();
//                break;
//            case R.id.tv_yonghuxieyi:
//                MyAlertDialog myAlertDialog = new MyAlertDialog(mActivity, "提示", "不准笑，这其实是个协议！");
//                myAlertDialog.setOnClickListener(new MyAlertDialog.OnPositiveClickListener() {
//                    @Override
//                    public void onPositiveClickListener(View v) {
//                    }
//                });
//                myAlertDialog.show();
//                break;
//            case R.id.tv_forget_psw:
//                intent = new Intent(mActivity, MeManager.class);
//                intent.putExtra(BackFragmentActivity.TAG_FRAGMENT, ChangeLoginPswFragment.TAG);
//                break;
            case R.id.tv_exit:
                exitLogin();
                break;
//            case R.id.tv_wodedingdan:
//                intent = new Intent(mActivity, MeManager.class);
//                intent.putExtra(BackFragmentActivity.TAG_FRAGMENT, OrderFragment.TAG);
//                bundle = new Bundle();
//                bundle.putInt("type", OrderFragment.TYPE_WODEDINGDAN);
//                intent.putExtra(BaseFragmentActivity.ARGS, bundle);
//                break;
//            case R.id.tv_wodejiedan:
//                intent = new Intent(mActivity, MeManager.class);
//                intent.putExtra(BackFragmentActivity.TAG_FRAGMENT, OrderFragment.TAG);
//                bundle = new Bundle();
//                bundle.putInt("type", OrderFragment.TYPE_WODEJIEDAN);
//                intent.putExtra(BaseFragmentActivity.ARGS, bundle);
//                break;
        }
//        if ( intent != null )
//            mActivity.startActivity(intent);
    }

    //
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