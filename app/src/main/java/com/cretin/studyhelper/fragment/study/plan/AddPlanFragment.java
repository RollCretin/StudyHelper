package com.cretin.studyhelper.fragment.study.plan;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cetin.studyhelper.R;
import com.cretin.studyhelper.app.LocalStorageKeys;
import com.cretin.studyhelper.base.BackFragmentActivity;
import com.cretin.studyhelper.base.BaseFragment;
import com.cretin.studyhelper.model.CusUser;
import com.cretin.studyhelper.model.PlanModel;
import com.cretin.studyhelper.utils.KV;
import com.cretin.studyhelper.utils.UiUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.qqtheme.framework.picker.DateTimePicker;
import cn.qqtheme.framework.picker.SinglePicker;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddPlanFragment extends BaseFragment {
    public static final String TAG = "AddPlanFragment";
    @Bind( R.id.ed_name )
    EditText edName;
    @Bind( R.id.tv_start )
    TextView tvStart;
    @Bind( R.id.tv_stop )
    TextView tvStop;
    @Bind( R.id.tv_tixing )
    TextView tvTixing;
    @Bind( R.id.ed_beizhu )
    EditText edBeizhu;
    @Bind( R.id.iv_ok )
    ImageView ivOk;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_add_plan;
    }

    @Override
    protected void initView(View contentView, Bundle savedInstanceState) {
        setMainTitle("添加计划");
        hidProgressView();
    }

    @Override
    protected void initData() {

    }

    private String selectTime;
    private String selectTimeValue;
    private String endTime;
    private String endTimeValue;

    @OnClick( {R.id.tv_start, R.id.tv_stop, R.id.tv_tixing, R.id.iv_ok} )
    public void onViewClicked(View view) {
        switch ( view.getId() ) {
            case R.id.tv_start:
                openTimePicker(new DateTimePicker.OnYearMonthDayTimePickListener() {
                    @Override
                    public void onDateTimePicked(String year, String month, String day, String hour, String minute) {
                        selectTime = year + "-" + month + "-" + day + " " + hour + ":" + minute;
                        selectTimeValue = (Integer.parseInt(year) % 100) + "/" + month + "/" + day + " " + hour + ":" + minute;
                        tvStart.setText((Integer.parseInt(year) % 100) + "/" + month + "/" + day + " " + hour + ":" + minute);
                    }
                }, "");
                break;
            case R.id.tv_stop:
                if ( TextUtils.isEmpty(selectTime) ) {
                    UiUtils.showToastInAnyThread("请先选择开始时间");
                    return;
                }
                openTimePicker(new DateTimePicker.OnYearMonthDayTimePickListener() {
                    @Override
                    public void onDateTimePicked(String year, String month, String day, String hour, String minute) {
                        endTime = year + "-" + month + "-" + day + " " + hour + ":" + (minute);
                        endTimeValue = (Integer.parseInt(year) % 100) + "/" + month + "/" + day + " " + hour + ":" + minute;
                        tvStop.setText((Integer.parseInt(year) % 100) + "/" + month + "/" + day + " " + hour + ":" + minute);
                    }
                }, selectTime);
                break;
            case R.id.tv_tixing:
                openTixingTime();
                break;
            case R.id.iv_ok:
                commit();
                break;
        }
    }

    //提交
    private void commit() {
        CusUser user = KV.get(LocalStorageKeys.USER_INFO);
        if ( user != null ) {
            //检查数据
            String title = edName.getText().toString().trim();
            if ( TextUtils.isEmpty(title) ) {
                UiUtils.showToastInAnyThread("请填写标题");
                return;
            }
            //检查起始和结束时间
            if ( TextUtils.isEmpty(selectTime) ) {
                UiUtils.showToastInAnyThread("请选择起始时间");
                return;
            }
            if ( TextUtils.isEmpty(endTime) ) {
                UiUtils.showToastInAnyThread("请选择结束时间");
                return;
            }

            showDialog("提交中...");
            String remark = edBeizhu.getText().toString().trim();
            //封装数据
            PlanModel planModel = new PlanModel();
            planModel.setCurrFlag(0);
            planModel.setEndTime(endTime);
            planModel.setEndTimeValue(endTimeValue);
            planModel.setStartTime(selectTime);
            planModel.setStartTimeValue(selectTimeValue);
            planModel.setRemark(remark);
            planModel.setRemindFlag(selectMode);
            planModel.setTitle(title);
            planModel.setUserId(user.getObjectId());
            planModel.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if ( e == null ) {
                        UiUtils.showToastInAnyThread();
                        (( BackFragmentActivity ) mActivity).removeFragment();
                    } else {
                        UiUtils.showToastInAnyThreadFail();
                    }
                    stopDialog();
                }
            });
        }
    }

    /**
     * 打开选择器
     */
    private void openTimePicker(DateTimePicker.OnYearMonthDayTimePickListener listener, String time) {
        DateTimePicker picker = new DateTimePicker(mActivity, DateTimePicker.HOUR_24);
        Calendar now = null;
        if ( !TextUtils.isEmpty(time) ) {
            now = new GregorianCalendar();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date daystart = null;
            try {
                daystart = df.parse(time);
            } catch ( ParseException e ) {
                e.printStackTrace();
            }
            now.setTime(daystart);     //得到的dayc1就是你需要的calendar了
        } else {
            //获取当前时间
            now = Calendar.getInstance();
        }
        int year = now.get(Calendar.YEAR);
        int month = (now.get(Calendar.MONTH) + 1);
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        picker.setDateRangeStart(year,
                month, day);
        picker.setDateRangeEnd(year + 2, 12, 31);
        picker.setTimeRangeStart(hour, minute);
        picker.setTimeRangeEnd(23, 59);
        picker.setTopLineColor(0xFF333333);
        picker.setLabelTextColor(0xFF333333);
        picker.setDividerColor(0xFF333333);
        picker.setOnDateTimePickListener(listener);
        picker.show();
    }

    private int selectMode = 0;

    //打开提醒时间的选择器
    private void openTixingTime() {
        ArrayList<String> list = new ArrayList<>();
//         0 不提醒 1 提前5分钟 2 提前10分钟 3 提前半小时 4 提前一小时
        list.add("不提醒");
        list.add("提前5分钟提醒");
        list.add("提前10分钟提醒");
        list.add("提前半小时提醒");
        list.add("提前一小时提醒");
        SinglePicker<String> picker = new SinglePicker<>(mActivity, list);
        picker.setLineVisible(true);
        picker.setTextSize(18);
        picker.setSelectedIndex(8);
        picker.setOnItemPickListener(new SinglePicker.OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
                selectMode = index;
                tvTixing.setText(item);
            }
        });
        picker.show();
    }
}
