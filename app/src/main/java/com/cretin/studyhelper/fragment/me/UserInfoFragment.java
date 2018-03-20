package com.cretin.studyhelper.fragment.me;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
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
import com.cretin.studyhelper.base.BaseFragment;
import com.cretin.studyhelper.model.CusUser;
import com.cretin.studyhelper.ui.PermissionsActivity;
import com.cretin.studyhelper.utils.KV;
import com.cretin.studyhelper.utils.PermissionsChecker;
import com.cretin.studyhelper.utils.UiUtils;
import com.cretin.studyhelper.view.CircleImageView;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.UploadFileListener;

import static android.app.Activity.RESULT_OK;
import static com.luck.picture.lib.config.PictureConfig.LUBAN_COMPRESS_MODE;

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
    }

    private void startPermissionsActivity() {
        Intent intent = new Intent(getActivity(), PermissionsActivity.class);
        intent.putExtra(PermissionsActivity.EXTRA_PERMISSIONS, PERMISSIONS);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @OnClick( {R.id.rl_me, R.id.ll_phone, R.id.ll_nickname, R.id.ll_qianming, R.id.ll_sex, R.id.ll_birthday, R.id.ll_qq, R.id.ll_weixin, R.id.ll_weibo} )
    public void onViewClicked(View view) {
        switch ( view.getId() ) {
            case R.id.rl_me:
                if ( mPermissionsChecker.lacksPermissions(PERMISSIONS) ) {
                    startPermissionsActivity();
                } else {
                    openDialog();
                }
                break;
            case R.id.ll_phone:

                break;
            case R.id.ll_nickname:

                break;
            case R.id.ll_qianming:

                break;
            case R.id.ll_sex:

                break;
            case R.id.ll_birthday:

                break;
            case R.id.ll_qq:

                break;
            case R.id.ll_weixin:

                break;
            case R.id.ll_weibo:

                break;
        }
    }

    public void openDialog() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .isCamera(true)// 是否显示拍照按钮 true or false
                .enableCrop(true)// 是否裁剪 true or false
                .withAspectRatio(1, 1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .imageSpanCount(3)// 每行显示个数 int
                .compress(true)// 是否压缩
                .compressMode(LUBAN_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
                .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .compressMaxKB(100)//压缩最大值kb compressGrade()为Luban.CUSTOM_GEAR有效 int
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    private String currImageUrl;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED ) {
            Toast.makeText(getActivity(), "授权失败", Toast.LENGTH_SHORT).show();
        }
        if ( requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_GRANTED ) {
            Toast.makeText(getActivity(), "授权成功", Toast.LENGTH_SHORT).show();
            openDialog();
        }
        if ( resultCode != getActivity().RESULT_OK ) {
            return;
        }

        if ( resultCode == RESULT_OK ) {
            switch ( requestCode ) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    if ( !selectList.isEmpty() ) {
                        LocalMedia localMedia = selectList.get(0);
                        if ( localMedia.isCut() )
                            currImageUrl = localMedia.getCutPath();
                        else
                            currImageUrl = localMedia.getPath();
                        if ( localMedia.isCompressed() ) {
                            currImageUrl = localMedia.getCompressPath();
                        }
                        uploadAvatar(currImageUrl);
                    }
                    break;
            }
        }
    }

    //上传图片
    private void uploadAvatar(String currImageUrl) {
        showDialog("上传中...");
        final BmobFile bmobFile = new BmobFile(new File(currImageUrl));
        bmobFile.uploadblock(new UploadFileListener() {

            @Override
            public void done(BmobException e) {
                if ( e == null ) {
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    Glide.with(mActivity).load(bmobFile.getFileUrl()).into(ivPortrait);
                    UiUtils.showToastInAnyThread();
                } else {
                    UiUtils.showToastInAnyThreadFail();
                }
                stopDialog();
            }

            @Override
            public void onProgress(Integer value) {
            }
        });
    }
}
