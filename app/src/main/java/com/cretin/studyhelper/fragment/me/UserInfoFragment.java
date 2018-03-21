package com.cretin.studyhelper.fragment.me;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cetin.studyhelper.R;
import com.cretin.studyhelper.app.LocalStorageKeys;
import com.cretin.studyhelper.base.BackFragmentActivity;
import com.cretin.studyhelper.base.BaseFragment;
import com.cretin.studyhelper.eventbus.NotifyMeChange;
import com.cretin.studyhelper.model.CusUser;
import com.cretin.studyhelper.ui.PermissionsActivity;
import com.cretin.studyhelper.utils.KV;
import com.cretin.studyhelper.utils.PermissionsChecker;
import com.cretin.studyhelper.utils.UiUtils;
import com.cretin.studyhelper.view.CircleImageView;
import com.google.gson.Gson;
import com.yongchun.library.view.ImageSelectorActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.SinglePicker;

import static android.app.Activity.RESULT_OK;
import static com.yongchun.library.view.ImageSelectorActivity.EXTRA_ENABLE_CROP;
import static com.yongchun.library.view.ImageSelectorActivity.EXTRA_ENABLE_PREVIEW;
import static com.yongchun.library.view.ImageSelectorActivity.EXTRA_MAX_SELECT_NUM;
import static com.yongchun.library.view.ImageSelectorActivity.EXTRA_SELECT_MODE;
import static com.yongchun.library.view.ImageSelectorActivity.EXTRA_SHOW_CAMERA;
import static com.yongchun.library.view.ImageSelectorActivity.REQUEST_IMAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserInfoFragment extends BaseFragment {
    public static final String TAG = "UserInfoFragment";
    @Bind( R.id.iv_portrait )
    CircleImageView ivPortrait;
    @Bind( R.id.rl_me )
    RelativeLayout rlMe;
    @Bind( R.id.tv_phone )
    TextView tvPhone;
    @Bind( R.id.ll_phone )
    LinearLayout llPhone;
    @Bind( R.id.tv_nickname )
    TextView tvNickname;
    @Bind( R.id.ll_nickname )
    LinearLayout llNickname;
    @Bind( R.id.tv_qianming )
    TextView tvQianming;
    @Bind( R.id.ll_qianming )
    LinearLayout llQianming;
    @Bind( R.id.tv_sex )
    TextView tvSex;
    @Bind( R.id.ll_sex )
    LinearLayout llSex;
    @Bind( R.id.tv_birthday )
    TextView tvBirthday;
    @Bind( R.id.ll_birthday )
    LinearLayout llBirthday;
    @Bind( R.id.tv_qq )
    TextView tvQq;
    @Bind( R.id.ll_qq )
    LinearLayout llQq;
    @Bind( R.id.tv_weixin )
    TextView tvWeixin;
    @Bind( R.id.ll_weixin )
    LinearLayout llWeixin;
    @Bind( R.id.tv_weibo )
    TextView tvWeibo;
    @Bind( R.id.ll_weibo )
    LinearLayout llWeibo;
    private CusUser mUserModel;
    private PermissionsChecker mPermissionsChecker; // 权限检测器
    private static final int REQUEST_CODE = 2; // 请求码
    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_info;
    }

    @Override
    protected void initView(View contentView, Bundle savedInstanceState) {
        setMainTitle("用户信息");
    }

    @Override
    protected void initData() {
        mPermissionsChecker = new PermissionsChecker(mActivity);
        getDate();
    }

    private void getDate() {
        showDialog("加载中...");
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
                } else {
                    UiUtils.showToastInAnyThread("用户信息更新失败");
                    mUserModel = KV.get(LocalStorageKeys.USER_INFO);
                }
                showData();
            }
        });
    }

    private void showData() {
        //显示头像
        if ( !TextUtils.isEmpty(mUserModel.getAvatar()) ) {
            Glide.with(mActivity).load(mUserModel.getAvatar()).into(ivPortrait);
        }

        //手机号
        tvPhone.setText(mUserModel.getMobilePhoneNumber());

        //昵称
        if ( !TextUtils.isEmpty(mUserModel.getNickname()) ) {
            tvNickname.setText(mUserModel.getNickname());
        }

        //签名
        if ( !TextUtils.isEmpty(mUserModel.getSignature()) ) {
            tvQianming.setText(mUserModel.getSignature());
        }

        //性别
        if ( !TextUtils.isEmpty(mUserModel.getSex()) ) {
            tvSex.setText(mUserModel.getSex());
        }

        //生日
        if ( !TextUtils.isEmpty(mUserModel.getBirthday()) ) {
            tvBirthday.setText(mUserModel.getBirthday());
        }

        //QQ
        if ( !TextUtils.isEmpty(mUserModel.getQq()) ) {
            tvQq.setText(mUserModel.getQq());
        }

        //微信
        if ( !TextUtils.isEmpty(mUserModel.getWeixin()) ) {
            tvWeixin.setText(mUserModel.getWeixin());
        }

        //微博
        if ( !TextUtils.isEmpty(mUserModel.getWeibo()) ) {
            tvWeibo.setText(mUserModel.getWeibo());
        }

        stopDialog();
    }

    private void startPermissionsActivity() {
        Intent intent = new Intent(getActivity(), PermissionsActivity.class);
        intent.putExtra(PermissionsActivity.EXTRA_PERMISSIONS, PERMISSIONS);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @OnClick( {R.id.rl_me, R.id.ll_phone, R.id.ll_nickname, R.id.ll_qianming,
            R.id.ll_sex, R.id.ll_birthday, R.id.ll_qq, R.id.ll_weixin, R.id.ll_weibo} )
    public void onViewClicked(View view) {
        switch ( view.getId() ) {
            case R.id.rl_me:
                if ( mPermissionsChecker.lacksPermissions(PERMISSIONS) ) {
                    startPermissionsActivity();
                } else {
                    Intent intent = new Intent(getActivity(), ImageSelectorActivity.class);
                    intent.putExtra(EXTRA_MAX_SELECT_NUM, 1);
                    intent.putExtra(EXTRA_SELECT_MODE, ImageSelectorActivity.MODE_SINGLE);
                    intent.putExtra(EXTRA_SHOW_CAMERA, true);
                    intent.putExtra(EXTRA_ENABLE_PREVIEW, true);
                    intent.putExtra(EXTRA_ENABLE_CROP, true);
                    startActivityForResult(intent, REQUEST_IMAGE);
                }
                break;
            case R.id.ll_phone:

                break;
            case R.id.ll_nickname:
                (( BackFragmentActivity ) mActivity).
                        addFragment(AddCommonInfoFragment.newInstance(
                                AddCommonInfoFragment.TYPE_NICKNAME,
                                tvQq.getText().toString()), true, true);
                break;
            case R.id.ll_qianming:
                (( BackFragmentActivity ) mActivity).
                        addFragment(AddCommonInfoFragment.newInstance(
                                AddCommonInfoFragment.TYPE_SIGNATURE,
                                tvQq.getText().toString()), true, true);
                break;
            case R.id.ll_sex:
                setSex();
                break;
            case R.id.ll_birthday:
                setBirthday();
                break;
            case R.id.ll_qq:
                (( BackFragmentActivity ) mActivity).
                        addFragment(AddCommonInfoFragment.newInstance(
                                AddCommonInfoFragment.TYPE_QQ,
                                tvQq.getText().toString()), true, true);
                break;
            case R.id.ll_weixin:
                (( BackFragmentActivity ) mActivity).
                        addFragment(AddCommonInfoFragment.newInstance(
                                AddCommonInfoFragment.TYPE_WEIXIN,
                                tvQq.getText().toString()), true, true);
                break;
            case R.id.ll_weibo:
                (( BackFragmentActivity ) mActivity).
                        addFragment(AddCommonInfoFragment.newInstance(
                                AddCommonInfoFragment.TYPE_WEIBO,
                                tvQq.getText().toString()), true, true);
                break;
        }
    }

    //设置生日
    private void setBirthday() {
        final DatePicker picker = new DatePicker(mActivity);
        Calendar now = new GregorianCalendar();
        now.setTime(new Date());
        now.add(Calendar.DAY_OF_MONTH, 1);
        int year = now.get(Calendar.YEAR);
        int month = (now.get(Calendar.MONTH) + 1);
        int day = now.get(Calendar.DAY_OF_MONTH);
        picker.setTopPadding(15);
        picker.setRangeStart(year, month, day);
        picker.setRangeEnd(year + 1, month, day);
        picker.setSelectedItem(year, month, day);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(final String year, final String month, final String day) {
                showDialog("修改中...");
                mUserModel.setBirthday(year + "-" + month + "-" + day);
                mUserModel.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if ( e == null ) {
                            KV.put(LocalStorageKeys.USER_INFO, mUserModel);
                            UiUtils.showToastInAnyThread();
                            tvBirthday.setText(year + "-" + month + "-" + day);
                        } else {
                            if ( e.getErrorCode() == 206 ) {
                                UiUtils.showToastInAnyThread("登录信息失效，请重新登录后再试");
                            }else{
                                UiUtils.showToastInAnyThreadFail();
                            }
                        }
                        stopDialog();
                    }
                });
            }
        });
        picker.show();
    }

    //上传头像
    private void uploadFile(final ArrayList<String> images) {
        if ( images != null && !images.isEmpty() ) {
            showDialog("正在上传...");
            final BmobFile bmobFile = new BmobFile(new File(images.get(0)));
            bmobFile.uploadblock(new UploadFileListener() {

                @Override
                public void done(BmobException e) {
                    if ( e == null ) {
                        mUserModel.setAvatar(bmobFile.getFileUrl());
                        mUserModel.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                stopDialog();
                                if ( e == null ) {
                                    KV.put(LocalStorageKeys.USER_INFO, mUserModel);
                                    if ( !TextUtils.isEmpty(bmobFile.getFileUrl()) )
                                        Glide.with(mActivity)
                                                .load(new File(images.get(0)))
                                                .thumbnail(0.5f)
                                                .into(ivPortrait);
                                    Toast.makeText(mActivity, "头像上传成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    if ( e.getErrorCode() == 206 ) {
                                        UiUtils.showToastInAnyThread("登录信息失效，请重新登录后再试");
                                    }else{
                                        Toast.makeText(mActivity, "头像上传失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    } else {
                        stopDialog();
                        Toast.makeText(mActivity, "头像上传失败", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onProgress(Integer value) {
                    // 返回的上传进度（百分比）
                }
            });
        }
    }

    //打开选择器
    private void setSex() {
        final ArrayList<String> list = new ArrayList<>();
        list.add("男");
        list.add("女");
        SinglePicker<String> picker = new SinglePicker<>(mActivity, list);
        picker.setLineVisible(true);
        picker.setTextSize(18);
        picker.setSelectedIndex(0);
        picker.setOnItemPickListener(new SinglePicker.OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, final String item) {
                showDialog("修改中...");
                mUserModel.setSex(item);
                mUserModel.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if ( e == null ) {
                            KV.put(LocalStorageKeys.USER_INFO, mUserModel);
                            UiUtils.showToastInAnyThread();
                            tvSex.setText(item);
                        } else {
                            if ( e.getErrorCode() == 206 ) {
                                UiUtils.showToastInAnyThread("登录信息失效，请重新登录后再试");
                            }else{
                                UiUtils.showToastInAnyThreadFail();
                            }
                        }
                        stopDialog();
                    }
                });
            }
        });
        picker.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED ) {
            Toast.makeText(getActivity(), "授权失败", Toast.LENGTH_SHORT).show();
        }
        if ( requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_GRANTED ) {
            Toast.makeText(getActivity(), "授权成功", Toast.LENGTH_SHORT).show();
        }

        if ( resultCode == RESULT_OK && requestCode == REQUEST_IMAGE ) {
            ArrayList<String> images = ( ArrayList<String> ) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
            uploadFile(images);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void notifyMeChange(NotifyMeChange event) {
        getDate();
    }
}
