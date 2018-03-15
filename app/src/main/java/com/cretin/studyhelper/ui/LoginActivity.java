package com.cretin.studyhelper.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.cetin.studyhelper.R;
import com.cretin.studyhelper.app.LocalStorageKeys;
import com.cretin.studyhelper.base.BaseActivity;
import com.cretin.studyhelper.model.CusUser;
import com.cretin.studyhelper.utils.KV;
import com.cretin.studyhelper.utils.UiUtils;
import com.cretin.www.clearedittext.view.ClearEditText;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends BaseActivity {
    @Bind( R.id.ed_username )
    ClearEditText edUsername;
    @Bind( R.id.ed_password )
    ClearEditText edPassword;
    @Bind( R.id.tv_register )
    TextView tvRegister;
    @Bind( R.id.tv_get_password )
    TextView tvGetPassword;
    @Bind( R.id.tv_login )
    TextView tvLogin;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            edPassword.requestFocus();
        }
    };

    @Override
    protected void initData() {
        //检查有没有数据 有就显示
        String info = KV.get(LocalStorageKeys.USER_LOGIN_INFO);
        if ( !TextUtils.isEmpty(info) ) {
            String phone = info.split("#")[0];
            String psw = info.split("#")[1];
            edUsername.setText(phone);
            edPassword.setText(psw);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(1);
                }
            }, 100);
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView(View view) {
        hidTitleView();
    }

    //登录
    private void login() {
        final String phone = edUsername.getText().toString().trim();
        final String password = edPassword.getText().toString();
        if ( TextUtils.isEmpty(phone) || TextUtils.isEmpty(password) ) {
            UiUtils.showToastInAnyThread("用户名或密码不能为空！");
            return;
        }
        showDialog("正在登录...");
        CusUser bu2 = new CusUser();
        bu2.setUsername(phone);
        bu2.setPassword(password);
        bu2.login(new SaveListener<CusUser>() {
                      @Override
                      public void done(final CusUser bmobUser, BmobException e) {
                          if ( e == null ) {
                              UiUtils.showToastInAnyThread("登录成功");
                              //更新成功
                              KV.put(LocalStorageKeys.USER_INFO, bmobUser);
                              //保存账户和密码
                              String info = phone + "#" + password;
                              KV.put(LocalStorageKeys.USER_LOGIN_INFO, info);
                              startActivity(new Intent(LoginActivity.this, MainActivity.class));
                              stopDialog();
                              finish();
                          } else {

                              if ( e.getErrorCode() == 101 ) {
                                  UiUtils.showToastInAnyThread("用户名或密码错误");
                              } else
                                  UiUtils.showToastInAnyThread("网络异常");
                          }
                          stopDialog();
                      }
                  }
        );
    }

    @OnClick( {R.id.tv_register, R.id.tv_get_password, R.id.tv_login} )
    public void onViewClicked(View view) {
        switch ( view.getId() ) {
            case R.id.tv_register:
                //去注册
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_get_password:
                //重置密码
                startActivity(new Intent(this, ResetPswActivity.class));
                break;
            case R.id.tv_login:
                login();
                break;
        }
    }
}
