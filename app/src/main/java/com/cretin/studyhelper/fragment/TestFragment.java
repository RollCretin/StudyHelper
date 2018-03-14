package com.cretin.studyhelper.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cetin.studyhelper.R;
import com.cretin.studyhelper.base.BaseFragment;

import butterknife.Bind;

public class TestFragment extends BaseFragment {
    @Bind( R.id.textview )
    TextView textview;
    private int code = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_test;
    }

    @Override
    protected void initView(View contentView, Bundle savedInstanceState) {
    }

    @Override
    protected void initData() {

    }
}
