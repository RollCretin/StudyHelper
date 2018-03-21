package com.cretin.studyhelper.fragment.me;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cetin.studyhelper.R;
import com.cretin.studyhelper.app.LocalStorageKeys;
import com.cretin.studyhelper.base.BackFragmentActivity;
import com.cretin.studyhelper.base.BaseFragment;
import com.cretin.studyhelper.eventbus.NotifyMeChange;
import com.cretin.studyhelper.model.CusUser;
import com.cretin.studyhelper.utils.KV;
import com.cretin.studyhelper.utils.UiUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddCommonInfoFragment extends BaseFragment {
    public static final String TAG = "AddCommonInfoFragment";
    public static final int TYPE_NICKNAME = 1;
    public static final int TYPE_SIGNATURE = 2;
    public static final int TYPE_QQ = 3;
    public static final int TYPE_WEIBO = 4;
    public static final int TYPE_WEIXIN = 5;
    @Bind( R.id.tv_nickname )
    TextView tvNickname;
    @Bind( R.id.et_nickname )
    EditText etNickname;
    @Bind( R.id.bt_submit )
    TextView btSubmit;
    private int type;
    private String msg;

    public static AddCommonInfoFragment newInstance(int type, String msg) {
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putString("msg", msg);
        AddCommonInfoFragment fragment = new AddCommonInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_add_common_info;
    }

    @Override
    protected void initView(View v, Bundle savedInstanceState) {
        type = getArguments().getInt("type");
        msg = getArguments().getString("msg");
        if ( type == TYPE_NICKNAME ) {
            //修改昵称
            setMainTitle("修改昵称");
            tvNickname.setText("昵称");
            etNickname.setHint("请输入昵称(6个字符以内)");
            etNickname.setText(msg);
        } else if ( type == TYPE_SIGNATURE ) {
            //修改签名
            setMainTitle("修改签名");
            tvNickname.setText("签名");
            etNickname.setHint("请输入签名(15个字符以内)");
            etNickname.setText(msg);
        } else if ( type == TYPE_QQ ) {
            //修改QQ
            setMainTitle("修改QQ");
            tvNickname.setText("QQ");
            etNickname.setHint("请输入QQ");
            etNickname.setText(msg);
        } else if ( type == TYPE_WEIBO ) {
            //修改微博
            setMainTitle("修改微博");
            tvNickname.setText("微博");
            etNickname.setHint("请输入微博");
            etNickname.setText(msg);
        } else if ( type == TYPE_WEIXIN ) {
            //修改微信
            setMainTitle("修改微信");
            tvNickname.setText("微信");
            etNickname.setHint("请输入微信");
            etNickname.setText(msg);
        }
    }

    @Override
    protected void initData() {

    }

    @OnClick( R.id.bt_submit )
    public void onViewClicked() {
        // 修改的类型  3 修改昵称
        // 7 修改签名 // 8 修改qq // 9 修改微信 // 10 修改微博
        CusUser cusUser = KV.get(LocalStorageKeys.USER_INFO);
        if ( cusUser != null ) {
            int inputType = 0;
            String content = etNickname.getText().toString().trim();
            if ( type == TYPE_NICKNAME ) {
                //修改昵称
                inputType = 3;
                if ( content.length() > 8 ) {
                    UiUtils.showToastInAnyThread("昵称最长不能超过8个字符");
                    return;
                }

                cusUser.setNickname(content);
            } else if ( type == TYPE_SIGNATURE ) {
                //修改签名
                inputType = 7;
                if ( content.length() > 15 ) {
                    UiUtils.showToastInAnyThread("签名最长不能超过15个字符");
                    return;
                }

                cusUser.setSignature(content);
            } else if ( type == TYPE_QQ ) {
                //修改QQ
                inputType = 8;
                cusUser.setQq(content);
            } else if ( type == TYPE_WEIBO ) {
                //修改微博
                inputType = 10;
                cusUser.setWeibo(content);
            } else if ( type == TYPE_WEIXIN ) {
                //修改微信
                inputType = 9;
                cusUser.setWeixin(content);
            }
            showDialog("正在修改...");

            cusUser.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    stopDialog();
                    if ( e == null ) {
                        UiUtils.showToastInAnyThread();
                        EventBus.getDefault().post(new NotifyMeChange());
                        (( BackFragmentActivity ) mActivity).removeFragment();
                    } else {
                        if ( e.getErrorCode() == 206 ) {
                            UiUtils.showToastInAnyThread("登录信息失效，请重新登录后再试");
                        }else{
                            UiUtils.showToastInAnyThreadFail();
                        }
                    }
                }
            });
        }
    }
}
