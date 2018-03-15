package com.cretin.studyhelper.ui;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cetin.studyhelper.R;
import com.cretin.studyhelper.BaseApplication;
import com.cretin.studyhelper.app.Console;
import com.cretin.studyhelper.base.BaseActivity;
import com.cretin.studyhelper.utils.UiUtils;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class ResetPswActivity extends BaseActivity {
    @Bind( R.id.et_phone )
    EditText etPhone;
    @Bind( R.id.et_new_psw )
    EditText etNewPsw;
    @Bind( R.id.et_new_confirm )
    EditText etNewConfirm;
    @Bind( R.id.ed_code )
    EditText edCode;
    @Bind( R.id.tv_get_code )
    TextView tvGetCode;
    @Bind( R.id.tv_submit )
    TextView tvSubmit;
    private Handler handlerCode = BaseApplication.getHandler();
    private int countDown = 60;// 倒计时秒数
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if ( countDown != 1 ) {
                countDown--;
                tvGetCode.setText("还剩" + countDown + "S");
                tvGetCode.setEnabled(false);
                handlerCode.postDelayed(this, 1000);
            } else if ( countDown == 1 ) {
                countDown = 60;
                tvGetCode.setEnabled(true);
                tvGetCode.setText("重新发送");
            } else if ( countDown <= 0 ) {
                countDown = 60;
                tvGetCode.setEnabled(true);
                tvGetCode.setText("获取验证码");
            }
        }
    };

    @Override
    protected void initData() {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_reset_psw;
    }

    @Override
    protected void initView(View view) {
        if ( view != null ) {
            setMainTitle("重置密码");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerCode.removeCallbacks(runnable);
    }

    //重置密码
    private void resetPsw() {
        String phone = etPhone.getText().toString().trim();
        String newPsw = etNewPsw.getText().toString();
        String newPswConfirm = etNewConfirm.getText().toString();
        String code = edCode.getText().toString();
        if ( TextUtils.isEmpty(phone) || phone.length() != 11 ) {
            UiUtils.showToastInAnyThread("请输入11位手机号");
            return;
        }
        if ( TextUtils.isEmpty(newPsw) ) {
            UiUtils.showToastInAnyThread("请输入密码");
            return;
        }
        if ( !newPsw.equals(newPswConfirm) ) {
            UiUtils.showToastInAnyThread("两次密码输入不一致");
            return;
        }
        if ( TextUtils.isEmpty(code) ) {
            UiUtils.showToastInAnyThread("请输入验证码");
            return;
        }
        showDialog("重置中...");
        //校验成功 开始重置密码
        BmobUser.resetPasswordBySMSCode(code, newPsw, new UpdateListener() {
            @Override
            public void done(BmobException ex) {
                stopDialog();
                if ( ex == null ) {
                    UiUtils.showToastInAnyThread("密码重置成功");
                    handlerCode.removeCallbacks(runnable);
                    finish();
                } else {
                    UiUtils.showToastInAnyThread("请求超时,请稍后再试");
                }
            }
        });
    }

    //获取重置密码的验证码
    private void getCode() {
        String phone = etPhone.getText().toString().trim();
        if ( TextUtils.isEmpty(phone) || phone.length() != 11 ) {
            UiUtils.showToastInAnyThread("请输入11位手机号");
            return;
        }
        showDialog("发送验证码...");
        BmobSMS.requestSMSCode(phone, Console.SMS_TEMP_ID, new QueryListener<Integer>() {

            @Override
            public void done(Integer smsId, BmobException ex) {
                stopDialog();
                if ( ex == null ) {//验证码发送成功
                    UiUtils.showToastInAnyThread("验证码发送成功！");
                    handlerCode.postDelayed(runnable, 1000);
                } else {
                    UiUtils.showToastInAnyThread(ex.getMessage());
                }
            }
        });
    }

    @OnClick( {R.id.tv_get_code, R.id.tv_submit} )
    public void onViewClicked(View view) {
        switch ( view.getId() ) {
            case R.id.tv_get_code:
                getCode();
                break;
            case R.id.tv_submit:
                resetPsw();
                break;
        }
    }
}
