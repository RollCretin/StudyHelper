package com.cretin.studyhelper.ui;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.cetin.studyhelper.R;
import com.cretin.studyhelper.BaseApplication;
import com.cretin.studyhelper.app.Console;
import com.cretin.studyhelper.base.BaseActivity;
import com.cretin.studyhelper.model.CusUser;
import com.cretin.studyhelper.utils.UiUtils;
import com.cretin.www.clearedittext.view.ClearEditText;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends BaseActivity {
    @Bind( R.id.ed_username )
    ClearEditText edUsername;
    @Bind( R.id.ed_password )
    ClearEditText edPassword;
    @Bind( R.id.ed_password_confirm )
    ClearEditText edPasswordConfirm;
    @Bind( R.id.ed_code )
    ClearEditText edCode;
    @Bind( R.id.tv_get_code )
    TextView tvGetCode;
    @Bind( R.id.tv_back_login )
    TextView tvBackLogin;
    @Bind( R.id.tv_register )
    TextView tvRegister;
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
        return R.layout.activity_register;
    }

    @Override
    protected void initView(View view) {
        if ( view != null )
            setMainTitle("新用户注册");
    }

    //获取验证码
    private void getCode() {
        final String phone = edUsername.getText().toString().trim();
        if ( TextUtils.isEmpty(phone) ) {
            UiUtils.showToastInAnyThread("手机号不能为空");
            return;
        }
        showDialog("发送验证码...");
        BmobQuery<CusUser> query = new BmobQuery<>();
        query.addWhereEqualTo("username", phone);
        query.findObjects(new FindListener<CusUser>() {
            @Override
            public void done(List<CusUser> object, BmobException e) {
                if ( e == null ) {
                    if ( object.size() == 0 ) {
                        //获取验证码
                        BmobSMS.requestSMSCode(phone, Console.SMS_TEMP_ID, new QueryListener<Integer>() {
                            @Override
                            public void done(Integer smsId, BmobException ex) {
                                stopDialog();
                                if ( ex == null ) {
                                    //验证码发送成功
                                    UiUtils.showToastInAnyThread("验证码发送成功");
                                    handlerCode.postDelayed(runnable, 1000);
                                } else {
                                    if ( ex.getErrorCode() == 10010 ) {
                                        UiUtils.showToastInAnyThread("验证码发送太快,请稍后再试");
                                    } else
                                        UiUtils.showToastInAnyThread(ex.getMessage());
                                }
                            }
                        });
                    } else {
                        stopDialog();
                        UiUtils.showToastInAnyThread("该手机号已经被注册,请直接登录");
                    }
                } else {
                    stopDialog();
                }
            }
        });
    }

    //注册操作
    private void register() {
        final String phone = edUsername.getText().toString().trim();
        final String password = edPassword.getText().toString();
        String password1 = edPasswordConfirm.getText().toString();
        final String code = edCode.getText().toString();
        if ( TextUtils.isEmpty(phone) ) {
            UiUtils.showToastInAnyThread("手机号不能为空");
            return;
        }
        if ( TextUtils.isEmpty(password) ) {
            UiUtils.showToastInAnyThread("密码不能为空");
            return;
        }
        if ( !password.equals(password1) ) {
            UiUtils.showToastInAnyThread("两次密码输入不一致");
        }
        if ( TextUtils.isEmpty(code) ) {
            UiUtils.showToastInAnyThread("验证码不能为空");
            return;
        }
        showDialog("正在注册...");
        doRegister(phone, password, code);
    }

    private void doRegister(String phone, String password, String code) {
        //用手机号进行注册
        CusUser user = new CusUser();
        user.setMobilePhoneNumber(phone);//设置手机号码（必填）
        user.setPassword(password);
        user.setAvatar("");
        user.setNickname("");
        //设置用户密码
        user.signOrLogin(code, new SaveListener<CusUser>() {

            @Override
            public void done(CusUser user, BmobException e) {
                stopDialog();
                if ( e == null ) {
                    UiUtils.showToastInAnyThread("注册成功");
                    handlerCode.removeCallbacks(runnable);
                    finish();
                } else {
                    UiUtils.showToastInAnyThread("注册失败,稍后再试");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerCode.removeCallbacks(runnable);
    }

    @OnClick( {R.id.tv_get_code, R.id.tv_back_login, R.id.tv_register} )
    public void onViewClicked(View view) {
        switch ( view.getId() ) {
            case R.id.tv_get_code:
                getCode();
                break;
            case R.id.tv_back_login:
                finish();
                break;
            case R.id.tv_register:
                register();
                break;
        }
    }
}
