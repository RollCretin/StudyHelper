package com.cretin.studyhelper.fragment.study.daily;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cetin.studyhelper.R;
import com.cretin.studyhelper.app.LocalStorageKeys;
import com.cretin.studyhelper.base.BackFragmentActivity;
import com.cretin.studyhelper.base.BaseFragment;
import com.cretin.studyhelper.model.CusUser;
import com.cretin.studyhelper.model.DailyModel;
import com.cretin.studyhelper.model.StudyDataModel;
import com.cretin.studyhelper.utils.KV;
import com.cretin.studyhelper.utils.UiUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.SinglePicker;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddDailyFragment extends BaseFragment {
    public static final String TAG = "AddDailyFragment";
    public static final int TYPE_ADD = 0;
    public static final int TYPE_EDIT = 1;
    @Bind( R.id.tv_date )
    TextView tvDate;
    @Bind( R.id.ll_date )
    LinearLayout llDate;
    @Bind( R.id.tv_subject )
    TextView tvSubject;
    @Bind( R.id.ll_subject )
    LinearLayout llSubject;
    @Bind( R.id.ed_finish )
    EditText edFinish;
    @Bind( R.id.ed_unfinish )
    EditText edUnfinish;
    @Bind( R.id.ed_time )
    EditText edTime;
    @Bind( R.id.ed_beizhu )
    EditText edBeizhu;
    @Bind( R.id.tv_submit )
    TextView tvSubmit;
    private List<String> subjects;

    public static AddDailyFragment newInstance(int type, String id) {
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putString("id", id);
        AddDailyFragment fragment = new AddDailyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_add_daily;
    }

    @Override
    protected void initView(View contentView, Bundle savedInstanceState) {
        setMainTitle("写日报");
    }

    @Override
    protected void initData() {
        subjects = new ArrayList<>();
        getSubjectData(false);
    }

    //获取学科数据
    private void getSubjectData(final boolean flag) {
        showDialog("加载中...");
        BmobQuery<StudyDataModel> query = new BmobQuery<StudyDataModel>();
        query.order("subjectValue");
        query.findObjects(new FindListener<StudyDataModel>() {
            @Override
            public void done(List<StudyDataModel> object, BmobException e) {
                if ( e == null ) {
                    subjects.clear();
                    for ( StudyDataModel s :
                            object ) {
                        subjects.add(s.getSubjectValue(), s.getSubject());
                    }
                    if ( flag ) {
                        showCommonPopWindow(subjects);
                    }
                } else {
                    UiUtils.showToastInAnyThreadFail();
                }
                stopDialog();
            }
        });
    }

    @OnClick( {R.id.ll_date, R.id.ll_subject, R.id.tv_submit} )
    public void onViewClicked(View view) {
        switch ( view.getId() ) {
            case R.id.ll_date:
                onYearMonthDayPicker();
                break;
            case R.id.ll_subject:
                if ( subjects.isEmpty() ) {
                    getSubjectData(true);
                } else {
                    showCommonPopWindow(subjects);
                }
                break;
            case R.id.tv_submit:
                submit();
                break;
        }
    }

    //提交数据
    private void submit() {
        String finish = edFinish.getText().toString().trim();
        String unfinish = edUnfinish.getText().toString().trim();
        String time = edTime.getText().toString().trim();
        String remark = edBeizhu.getText().toString().trim();
        CusUser cusUser = KV.get(LocalStorageKeys.USER_INFO);
        if ( cusUser == null ) {
            UiUtils.showToastInAnyThread("登录信息失效");
            return;
        }
        //检查数据
        if ( TextUtils.isEmpty(currDate) ) {
            UiUtils.showToastInAnyThread("请选择日期");
            return;
        }
        if ( TextUtils.isEmpty(currSubject) ) {
            UiUtils.showToastInAnyThread("请选择学习科目");
            return;
        }
        if ( TextUtils.isEmpty(finish) ) {
            UiUtils.showToastInAnyThread("请输入今日完成任务");
            return;
        }
        if ( TextUtils.isEmpty(unfinish) ) {
            UiUtils.showToastInAnyThread("请输入未完成任务");
            return;
        }
        if ( TextUtils.isEmpty(time) ) {
            UiUtils.showToastInAnyThread("请输入学习时间");
            return;
        }
        //提交数据
        showDialog("提交中...");
        DailyModel dailyModel = new DailyModel();
        dailyModel.setDate(currDate);
        dailyModel.setFinish(finish);
        dailyModel.setRemark(remark);
        dailyModel.setUnfinish(unfinish);
        dailyModel.setSubject(currSubject.split(" ")[1]);
        dailyModel.setSubjectValue(Integer.parseInt(currSubject.split(" ")[0]));
        dailyModel.setUserId(cusUser.getObjectId());
        dailyModel.setTime(Integer.parseInt(time));
        dailyModel.setCusUser(cusUser);
        dailyModel.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                stopDialog();
                if ( e == null ) {
                    UiUtils.showToastInAnyThread();
                    (( BackFragmentActivity ) mActivity).removeFragment();
                } else {
                    UiUtils.showToastInAnyThreadFail();
                }
            }
        });
    }

    //已选择的数据
    private String currSubject;
    private String currDate;

    /**
     * 显示选择器
     *
     * @param list 数据
     */
    private void showCommonPopWindow(List<String> list) {
        if ( list == null || list.isEmpty() ) {
            return;
        }
        SinglePicker<String> picker = new SinglePicker<String>(mActivity, list);
        picker.setLineVisible(true);
        picker.setTextSize(18);
        picker.setSelectedIndex(8);
        picker.setOnItemPickListener(new SinglePicker.OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
                currSubject = index + " " + item;
                tvSubject.setText(item);
            }
        });
        picker.show();
    }

    /**
     * 选择时期
     */
    public void onYearMonthDayPicker() {
        final DatePicker picker = new DatePicker(mActivity);
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = (now.get(Calendar.MONTH) + 1);
        int day = now.get(Calendar.DAY_OF_MONTH);
        picker.setTopPadding(15);
        picker.setRangeStart(year - 1, month, day);
        picker.setRangeEnd(year + 1, month, day);
        picker.setSelectedItem(year, month, day);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                tvDate.setText(year + "-" + month + "-" + day);
                currDate = year + "-" + month + "-" + day;
            }
        });
        picker.show();
    }
}
