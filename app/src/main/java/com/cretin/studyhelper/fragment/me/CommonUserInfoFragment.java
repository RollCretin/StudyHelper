package com.cretin.studyhelper.fragment.me;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cetin.studyhelper.R;
import com.cretin.studyhelper.base.BackFragmentActivity;
import com.cretin.studyhelper.base.BaseFragment;
import com.cretin.studyhelper.base.BaseFragmentActivity;
import com.cretin.studyhelper.fragment.follow.evaluation.EvaluationFragment;
import com.cretin.studyhelper.fragment.follow.study.StudyBarFragment;
import com.cretin.studyhelper.fragment.home.HomeFragment;
import com.cretin.studyhelper.model.CusUser;
import com.cretin.studyhelper.ui.ShowBigPicActivity;
import com.cretin.studyhelper.ui.manager.HomeActivityManager;
import com.cretin.studyhelper.ui.manager.PostActivityManager;
import com.cretin.studyhelper.utils.UiUtils;
import com.cretin.studyhelper.view.CircleImageView;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommonUserInfoFragment extends BaseFragment {
    public static final String TAG = "CommonUserInfoFragment";
    @Bind( R.id.iv_avatar )
    CircleImageView ivAvatar;
    @Bind( R.id.tv_yonghuming )
    TextView tvYonghuming;
    @Bind( R.id.tv_qianming )
    TextView tvQianming;
    @Bind( R.id.tv_tiezi )
    TextView tvTiezi;
    @Bind( R.id.tv_ceping )
    TextView tvCeping;
    @Bind( R.id.tv_fenxiang )
    TextView tvFenxiang;
    private String userId;
    private CusUser cusUser;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_common_user_info;
    }

    public static CommonUserInfoFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString("id", id);
        CommonUserInfoFragment fragment = new CommonUserInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(View contentView, Bundle savedInstanceState) {
        setMainTitle("用户信息");
        userId = getArguments().getString("id");
    }

    @Override
    protected void initData() {
        showDialog("加载中...");
        BmobQuery<CusUser> query = new BmobQuery<CusUser>();
        query.getObject(userId, new QueryListener<CusUser>() {

            @Override
            public void done(CusUser object, BmobException e) {
                if ( e == null ) {
                    cusUser = object;
                    showData(cusUser);
                } else {
                    UiUtils.showToastInAnyThreadFail();
                }
                stopDialog();
            }
        });
    }

    //显示数据
    private void showData(CusUser cusUser) {
        //头像
        if ( TextUtils.isEmpty(cusUser.getAvatar()) ) {
            ivAvatar.setImageResource(R.mipmap.avatar);
        } else {
            Glide.with(mActivity).load(cusUser.getAvatar()).into(ivAvatar);
        }
        //昵称
        String nick = cusUser.getNickname();
        if ( TextUtils.isEmpty(nick) )
            nick = cusUser.getMobilePhoneNumber();
        tvYonghuming.setText(nick);
        //签名
        tvQianming.setText(cusUser.getSignature());
    }

    @OnClick( {R.id.iv_avatar, R.id.tv_tiezi, R.id.tv_ceping, R.id.tv_fenxiang} )
    public void onViewClicked(View view) {
        if ( cusUser != null ) {
            Intent intent = null;
            Bundle bundle = null;
            switch ( view.getId() ) {
                case R.id.iv_avatar:
                    if ( !TextUtils.isEmpty(cusUser.getAvatar()) ) {
                        intent = new Intent(mActivity, ShowBigPicActivity.class);
                        intent.putExtra("image_url", cusUser.getAvatar());
                        intent.putExtra("details", "用户 " + tvYonghuming.getText().toString() + " 的头像");
                    }
                    break;
                case R.id.tv_tiezi:
                    intent = new Intent(mActivity, PostActivityManager.class);
                    intent.putExtra(BackFragmentActivity.TAG_FRAGMENT, StudyBarFragment.TAG);
                    bundle = new Bundle();
                    bundle.putInt("type", StudyBarFragment.TYPE_OTHER);
                    bundle.putString("userId", cusUser.getObjectId());
                    intent.putExtra(BaseFragmentActivity.ARGS, bundle);
                    break;
                case R.id.tv_ceping:
                    intent = new Intent(mActivity, PostActivityManager.class);
                    intent.putExtra(BackFragmentActivity.TAG_FRAGMENT, EvaluationFragment.TAG);
                    bundle = new Bundle();
                    bundle.putInt("type", EvaluationFragment.TYPE_OTHER_CREATE);
                    bundle.putString("userId", cusUser.getObjectId());
                    intent.putExtra(BaseFragmentActivity.ARGS, bundle);
                    break;
                case R.id.tv_fenxiang:
                    intent = new Intent(mActivity, HomeActivityManager.class);
                    intent.putExtra(BackFragmentActivity.TAG_FRAGMENT, HomeFragment.TAG);
                    bundle = new Bundle();
                    bundle.putInt("type", HomeFragment.TYPE_OTHER_SHARE);
                    bundle.putString("userId", cusUser.getObjectId());
                    intent.putExtra(BaseFragmentActivity.ARGS, bundle);
                    break;
            }
            if ( intent != null )
                mActivity.startActivity(intent);
        } else {
            UiUtils.showToastInAnyThreadFail();
        }
    }
}
