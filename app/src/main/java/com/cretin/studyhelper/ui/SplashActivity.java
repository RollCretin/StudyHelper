package com.cretin.studyhelper.ui;

import android.content.Intent;
import android.view.View;

import com.cetin.studyhelper.R;
import com.cretin.studyhelper.base.ParentActivity;

public class SplashActivity extends ParentActivity {
    @Override
    protected void initView(View view) {
        setContentView(R.layout.activity_splash);

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

//        BaseApplication.getHandler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
