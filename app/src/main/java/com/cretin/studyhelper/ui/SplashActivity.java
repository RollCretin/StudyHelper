package com.cretin.studyhelper.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.cetin.studyhelper.R;
import com.cretin.studyhelper.BaseApplication;
import com.cretin.studyhelper.app.LocalStorageKeys;
import com.cretin.studyhelper.base.ParentActivity;
import com.cretin.studyhelper.utils.KV;

public class SplashActivity extends ParentActivity {
    @Override
    protected void initView(View view) {
        setContentView(R.layout.activity_splash);

        ImageView iv_pic = ( ImageView ) findViewById(R.id.iv_pic);

        ObjectAnimator.ofFloat(iv_pic, "alpha", 0f, 1f).setDuration(1000).start();

        ValueAnimator bounceAnim = ObjectAnimator.ofFloat(iv_pic, "y",
                -200 * getScale(), 100);
        bounceAnim.setDuration(1000);
        // 加速器，小球会加速下落
        bounceAnim.setInterpolator(new AccelerateInterpolator());
        bounceAnim.start();

        BaseApplication.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if ( KV.get(LocalStorageKeys.USER_INFO) == null ) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private float getScale() {
        TextView textView = new TextView(this);
        textView.setTextSize(1);
        return textView.getTextSize();
    }
}
