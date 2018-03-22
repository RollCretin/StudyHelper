package com.cretin.studyhelper.fragment.study.plan;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cetin.studyhelper.R;
import com.cretin.studyhelper.app.LocalStorageKeys;
import com.cretin.studyhelper.base.BackFragmentActivity;
import com.cretin.studyhelper.base.BaseFragment;
import com.cretin.studyhelper.eventbus.CommonCloseNotify;
import com.cretin.studyhelper.eventbus.StydyDataRegreshNotify;
import com.cretin.studyhelper.model.CusUser;
import com.cretin.studyhelper.model.PlansModel;
import com.cretin.studyhelper.utils.KV;
import com.cretin.studyhelper.utils.UiUtils;
import com.cretin.studyhelper.view.MyAlertDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.SinglePicker;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddPlanFragment extends BaseFragment {
    public static final String TAG = "AddPlanFragment";
    public static final int TYPE_ADD = 0;
    public static final int TYPE_EDIT = 1;
    @Bind( R.id.tv_normal )
    TextView tvNormal;
    @Bind( R.id.tv_aim )
    TextView tvAim;
    @Bind( R.id.tv_habit )
    TextView tvHabit;
    @Bind( R.id.ed_name )
    EditText edName;
    @Bind( R.id.tv_settime_aim )
    TextView tvSettimeAim;
    @Bind( R.id.ed_sc_aim )
    EditText edScAim;
    @Bind( R.id.tv_timetype_aim )
    TextView tvTimetypeAim;
    @Bind( R.id.tv_type_habit )
    TextView tvTypeHabit;
    @Bind( R.id.ed_sc_habit )
    EditText edScHabit;
    @Bind( R.id.tv_timetype_habit )
    TextView tvTimetypeHabit;
    @Bind( R.id.tv_type_djs )
    TextView tvTypeDjs;
    @Bind( R.id.tv_type_zjs )
    TextView tvTypeZjs;
    @Bind( R.id.tv_type_bjs )
    TextView tvTypeBjs;
    @Bind( R.id.tv_time_25 )
    TextView tvTime25;
    @Bind( R.id.tv_time_35 )
    TextView tvTime35;
    @Bind( R.id.tv_time_zdy )
    TextView tvTimeZdy;
    @Bind( R.id.ed_beizhu )
    EditText edBeizhu;
    @Bind( R.id.ed_normal_sc )
    EditText edNormalSc;
    @Bind( R.id.tv_tips )
    TextView tvTips;
    @Bind( R.id.iv_ok )
    ImageView ivOk;
    @Bind( R.id.ll_shichang )
    LinearLayout llShichang;
    @Bind( R.id.ll_aim )
    LinearLayout llAim;
    @Bind( R.id.ll_habit )
    LinearLayout llHabit;
    @Bind( R.id.ll_normal )
    LinearLayout llNormal;
    private int type;
    private String id;

    private PlansModel currModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_add_plan;
    }

    @Override
    protected void initView(View contentView, Bundle savedInstanceState) {
        type = getArguments().getInt("type");
        id = getArguments().getString("id");
        removeLeftListener();
        setOnTitleAreaCliclkListener(new OnTitleAreaCliclkListener() {
            @Override
            public void onTitleAreaClickListener(View view) {
                if ( view.getId() == R.id.iv_back ) {
                    showCLoseDialog();
                }
            }
        });

        if ( type == TYPE_ADD ) {
            setMainTitle("添加计划");
            hidProgressView();

            //初始状态
            tvNormal.setBackgroundResource(R.drawable.bg_button_round);
            tvNormal.setTextColor(Color.parseColor("#ffffff"));
            llNormal.setVisibility(View.VISIBLE);

            tvTypeDjs.setBackgroundResource(R.drawable.bg_button_round);
            tvTypeDjs.setTextColor(Color.parseColor("#ffffff"));

            tvTime25.setBackgroundResource(R.drawable.bg_button_round);
            tvTime25.setTextColor(Color.parseColor("#ffffff"));
        } else if ( type == TYPE_EDIT ) {
            setMainTitle("编辑计划");
            tvTips.setText("编辑计划");
            //获取信息并添加
            showDialog("加载中...");
            BmobQuery<PlansModel> query = new BmobQuery<PlansModel>();
            query.getObject(id, new QueryListener<PlansModel>() {

                @Override
                public void done(PlansModel object, BmobException e) {
                    if ( e == null ) {
                        currModel = object;
                        //不能换类型
                        if ( object.getPlanType() == PlansModel.PLAN_TYPE_NORMAL ) {
                            tvNormal.setEnabled(true);
                            tvAim.setEnabled(false);
                            tvNormal.setBackgroundResource(R.drawable.bg_button_round);
                            tvNormal.setTextColor(Color.parseColor("#ffffff"));

                            llNormal.setVisibility(View.VISIBLE);
                            //类型
                            if ( object.getNormalType() == PlansModel.NORMAL_TYPE_DJS ) {
                                tvTypeDjs.performClick();
                            } else if ( object.getNormalType() == PlansModel.NORMAL_TYPE_ZJS ) {
                                tvTypeZjs.performClick();
                                currNormalTime = 25;
                                tvTime25.setBackgroundResource(R.drawable.bg_button_round);
                                tvTime25.setTextColor(Color.parseColor("#ffffff"));
                            } else if ( object.getNormalType() == PlansModel.NORMAL_TYPE_BJS ) {
                                tvTypeBjs.performClick();
                                currNormalTime = 25;
                                tvTime25.setBackgroundResource(R.drawable.bg_button_round);
                                tvTime25.setTextColor(Color.parseColor("#ffffff"));
                            }
                            //时间
                            if ( object.getNormalTime() == 25 ) {
                                tvTime25.setBackgroundResource(R.drawable.bg_button_round);
                                tvTime25.setTextColor(Color.parseColor("#ffffff"));
                            } else if ( object.getNormalTime() == 35 ) {
                                tvTime35.setBackgroundResource(R.drawable.bg_button_round);
                                tvTime35.setTextColor(Color.parseColor("#ffffff"));
                            } else {
                                tvTimeZdy.setBackgroundResource(R.drawable.bg_button_round);
                                tvTimeZdy.setTextColor(Color.parseColor("#ffffff"));
                                edNormalSc.setVisibility(View.VISIBLE);
                                edNormalSc.setText(object.getNormalTime() + "");
                            }
                        } else {
                            tvAim.setEnabled(true);
                            tvNormal.setEnabled(false);
                            tvAim.setBackgroundResource(R.drawable.bg_button_round);
                            tvAim.setTextColor(Color.parseColor("#ffffff"));

                            llAim.setVisibility(View.VISIBLE);

                            tvSettimeAim.setText(object.getAimEndTime());
                            tvTimetypeAim.setText(object.getAimTimeType() == PlansModel.TIME_TYPE_HOUR ? "小时" : "分钟");

                            llShichang.setVisibility(View.GONE);
                        }

                        edName.setText(object.getPlanName());
                        edBeizhu.setText(object.getRemark());

                        currPlanType = object.getPlanType();
                        currNromalType = object.getNormalType();
                        //计时时长 默认25分钟
                        currNormalTime = object.getNormalTime();
                        //记录目标模式下的最后时间
                        currAimEndTime = object.getAimEndTime();
                        //记录目标模式下的时长类型
                        currAimTimeType = object.getAimTimeType();

                        hidProgressView();
                    } else {
                        UiUtils.showToastInAnyThreadFail();
                        showErrorView();
                    }
                    stopDialog();
                }
            });
        }
    }

    public static AddPlanFragment newInstance(int type, String id) {
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putString("id", id);
        AddPlanFragment fragment = new AddPlanFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initData() {

    }


    //先重置一下按钮状态 0 重置计划类型 1 重置普通计划的类型 2重置普通计划的市场
    private void resetBtn(int type) {
        if ( type == 0 ) {
            llNormal.setVisibility(View.GONE);
            llAim.setVisibility(View.GONE);
            llHabit.setVisibility(View.GONE);
            tvAim.setBackgroundResource(R.drawable.bg_button_round_shallow);
            tvAim.setTextColor(Color.parseColor("#B19873"));
            tvNormal.setBackgroundResource(R.drawable.bg_button_round_shallow);
            tvNormal.setTextColor(Color.parseColor("#B19873"));
            tvHabit.setBackgroundResource(R.drawable.bg_button_round_shallow);
            tvHabit.setTextColor(Color.parseColor("#B19873"));
        } else if ( type == 1 ) {
            tvTypeDjs.setBackgroundResource(R.drawable.bg_button_round_shallow);
            tvTypeDjs.setTextColor(Color.parseColor("#B19873"));
            tvTypeZjs.setBackgroundResource(R.drawable.bg_button_round_shallow);
            tvTypeZjs.setTextColor(Color.parseColor("#B19873"));
            tvTypeBjs.setBackgroundResource(R.drawable.bg_button_round_shallow);
            tvTypeBjs.setTextColor(Color.parseColor("#B19873"));
        } else if ( type == 2 ) {
            tvTime25.setBackgroundResource(R.drawable.bg_button_round_shallow);
            tvTime25.setTextColor(Color.parseColor("#B19873"));
            tvTime35.setBackgroundResource(R.drawable.bg_button_round_shallow);
            tvTime35.setTextColor(Color.parseColor("#B19873"));
            tvTimeZdy.setBackgroundResource(R.drawable.bg_button_round_shallow);
            tvTimeZdy.setTextColor(Color.parseColor("#B19873"));
        }
    }

    //当前选择的计划类型
    private int currPlanType = PlansModel.PLAN_TYPE_NORMAL;
    //当前选择的正常模式的计时类型
    private int currNromalType = PlansModel.NORMAL_TYPE_DJS;
    //计时时长 默认25分钟
    private int currNormalTime = 25;
    //记录目标模式下的最后时间
    private String currAimEndTime;
    //记录目标模式下的时长类型
    private int currAimTimeType = PlansModel.TIME_TYPE_HOUR;
    //记录习惯模式下的类型
    private int currHabitType = PlansModel.HABIT_TYPE_DAY;
    //记录习惯模式下的时长类型
    private int currHabitTimeType = PlansModel.TIME_TYPE_HOUR;

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

            if ( currPlanType == PlansModel.PLAN_TYPE_NORMAL ) {
                //普通计划
                PlansModel plansModel = new PlansModel();
                plansModel.setPlanType(PlansModel.PLAN_TYPE_NORMAL);
                int time = currNormalTime;
                if ( time == -1 ) {
                    String times = edNormalSc.getText().toString().trim();
                    if ( TextUtils.isEmpty(times) ) {
                        UiUtils.showToastInAnyThread("请输入时间");
                        return;
                    }
                    time = Integer.parseInt(times);
                }
                plansModel.setNormalTime(time);
                plansModel.setPlanName(title);
                plansModel.setNormalType(currNromalType);
                plansModel.setRemark(edBeizhu.getText().toString().trim());
                plansModel.setUserId(user.getObjectId());
                plansModel.setCurrFlag(0);
                showDialog("提交中...");
                if ( type == TYPE_ADD ) {
                    plansModel.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if ( e == null ) {
                                UiUtils.showToastInAnyThread();
                                EventBus.getDefault().post(new StydyDataRegreshNotify());
                                (( BackFragmentActivity ) mActivity).removeFragment();
                            } else {
                                UiUtils.showToastInAnyThreadFail();
                            }
                            stopDialog();
                        }
                    });
                } else {
                    plansModel.update(id, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if ( e == null ) {
                                UiUtils.showToastInAnyThread();
                                EventBus.getDefault().post(new StydyDataRegreshNotify());
                                (( BackFragmentActivity ) mActivity).removeFragment();
                            } else {
                                UiUtils.showToastInAnyThreadFail();
                            }
                            stopDialog();
                        }
                    });
                }
                return;
            } else if ( currPlanType == PlansModel.PLAN_TYPE_AIM ) {
                //目标
                if ( TextUtils.isEmpty(currAimEndTime) ) {
                    UiUtils.showToastInAnyThread("请设置时间");
                    return;
                }
                String time = edScAim.getText().toString().trim();
                if ( TextUtils.isEmpty(time) ) {
                    UiUtils.showToastInAnyThread("请输入时长");
                    return;
                }
                showDialog("提交中...");
                PlansModel plansModel = new PlansModel();
                plansModel.setPlanType(PlansModel.PLAN_TYPE_AIM);
                plansModel.setUserId(user.getObjectId());
                plansModel.setAimEndTime(currAimEndTime);
                plansModel.setPlanName(title);
                plansModel.setRemark(edBeizhu.getText().toString().trim());
                plansModel.setAimTimeType(currAimTimeType);
                plansModel.setAimTime(Integer.parseInt(time));
                plansModel.setCurrFlag(0);
                if ( type == TYPE_ADD ) {
                    plansModel.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if ( e == null ) {
                                UiUtils.showToastInAnyThread();
                                EventBus.getDefault().post(new StydyDataRegreshNotify());
                                (( BackFragmentActivity ) mActivity).removeFragment();
                            } else {
                                UiUtils.showToastInAnyThreadFail();
                            }
                            stopDialog();
                        }
                    });
                } else {
                    plansModel.update(id, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if ( e == null ) {
                                UiUtils.showToastInAnyThread();
                                EventBus.getDefault().post(new StydyDataRegreshNotify());
                                (( BackFragmentActivity ) mActivity).removeFragment();
                            } else {
                                UiUtils.showToastInAnyThreadFail();
                            }
                            stopDialog();
                        }
                    });
                }
                return;
            } else if ( currPlanType == PlansModel.PLAN_TYPE_HABIT ) {
                //习惯模式
                String time = edScHabit.getText().toString().trim();
                if ( TextUtils.isEmpty(time) ) {
                    UiUtils.showToastInAnyThread("请输入时长");
                    return;
                }
                showDialog("提交中...");
                PlansModel plansModel = new PlansModel();
                plansModel.setUserId(user.getObjectId());
                plansModel.setPlanType(PlansModel.PLAN_TYPE_HABIT);
                plansModel.setRemark(edBeizhu.getText().toString().trim());
                plansModel.setHabitType(currHabitType);
                plansModel.setPlanName(title);
                plansModel.setHabitTimeType(currHabitTimeType);
                plansModel.setCurrFlag(0);
                plansModel.setHabitTime(Integer.parseInt(time));
                if ( type == TYPE_ADD ) {
                    plansModel.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if ( e == null ) {
                                UiUtils.showToastInAnyThread();
                                EventBus.getDefault().post(new StydyDataRegreshNotify());
                                (( BackFragmentActivity ) mActivity).removeFragment();
                            } else {
                                UiUtils.showToastInAnyThreadFail();
                            }
                            stopDialog();
                        }
                    });
                } else {
                    plansModel.update(id, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if ( e == null ) {
                                UiUtils.showToastInAnyThread();
                                EventBus.getDefault().post(new StydyDataRegreshNotify());
                                (( BackFragmentActivity ) mActivity).removeFragment();
                            } else {
                                UiUtils.showToastInAnyThreadFail();
                            }
                            stopDialog();
                        }
                    });
                }
                return;
            }
        }
    }

    //打开提醒时间的选择器 0 目标模式下的时长类型 1 习惯下的类型 2 习惯下的时长类型
    private void openTimeSelect(final int type) {
        final ArrayList<String> list = new ArrayList<>();
//         0 不提醒 1 提前5分钟 2 提前10分钟 3 提前半小时 4 提前一小时
        if ( type == 0 || type == 2 ) {
            //目标
            list.add("小时");
            list.add("分钟");
        } else {
            list.add("每天");
            list.add("每周");
            list.add("每月");
        }
        SinglePicker<String> picker = new SinglePicker<>(mActivity, list);
        picker.setLineVisible(true);
        picker.setTextSize(18);
        picker.setSelectedIndex(8);
        picker.setOnItemPickListener(new SinglePicker.OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
                if ( type == 0 ) {
                    //目标
                    currAimTimeType = index;
                    tvTimetypeAim.setText(item);
                } else if ( type == 1 ) {
                    //习惯下的类型
                    currHabitType = index;
                    tvTypeHabit.setText(item);
                } else if ( type == 2 ) {
                    //习惯下的时长类型
                    currHabitTimeType = index;
                    tvTimetypeHabit.setText(item);
                }
            }
        });
        picker.show();
    }

    @OnClick( {R.id.tv_normal, R.id.tv_aim, R.id.tv_habit, R.id.tv_settime_aim,
            R.id.tv_timetype_aim, R.id.tv_type_habit, R.id.tv_timetype_habit,
            R.id.tv_type_djs, R.id.tv_type_zjs, R.id.tv_type_bjs,
            R.id.tv_time_25, R.id.tv_time_35, R.id.tv_time_zdy, R.id.tv_tips, R.id.iv_ok} )
    public void onViewClicked(View view) {
        switch ( view.getId() ) {
            case R.id.tv_normal:
                currPlanType = PlansModel.PLAN_TYPE_NORMAL;
                resetBtn(0);
                tvNormal.setBackgroundResource(R.drawable.bg_button_round);
                tvNormal.setTextColor(Color.parseColor("#ffffff"));
                llNormal.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_aim:
                currPlanType = PlansModel.PLAN_TYPE_AIM;
                resetBtn(0);
                tvAim.setBackgroundResource(R.drawable.bg_button_round);
                tvAim.setTextColor(Color.parseColor("#ffffff"));
                llAim.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_habit:
                currPlanType = PlansModel.PLAN_TYPE_HABIT;
                resetBtn(0);
                tvHabit.setBackgroundResource(R.drawable.bg_button_round);
                tvHabit.setTextColor(Color.parseColor("#ffffff"));
                llHabit.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_settime_aim:
                onYearMonthDayPicker();
                break;
            case R.id.tv_timetype_aim:
                openTimeSelect(0);
                break;
            case R.id.tv_type_habit:
                openTimeSelect(1);
                break;
            case R.id.tv_timetype_habit:
                openTimeSelect(2);
                break;
            case R.id.tv_type_djs:
                currNromalType = PlansModel.NORMAL_TYPE_DJS;
                resetBtn(1);
                llShichang.setVisibility(View.VISIBLE);
                tvTypeDjs.setBackgroundResource(R.drawable.bg_button_round);
                tvTypeDjs.setTextColor(Color.parseColor("#ffffff"));
                break;
            case R.id.tv_type_zjs:
                currNromalType = PlansModel.NORMAL_TYPE_ZJS;
                resetBtn(1);
                llShichang.setVisibility(View.GONE);
                tvTypeZjs.setBackgroundResource(R.drawable.bg_button_round);
                tvTypeZjs.setTextColor(Color.parseColor("#ffffff"));
                break;
            case R.id.tv_type_bjs:
                currNromalType = PlansModel.NORMAL_TYPE_BJS;
                resetBtn(1);
                tvTypeBjs.setBackgroundResource(R.drawable.bg_button_round);
                tvTypeBjs.setTextColor(Color.parseColor("#ffffff"));
                llShichang.setVisibility(View.GONE);
                break;
            case R.id.tv_time_25:
                currNormalTime = 25;
                resetBtn(2);
                edNormalSc.setVisibility(View.GONE);
                tvTime25.setBackgroundResource(R.drawable.bg_button_round);
                tvTime25.setTextColor(Color.parseColor("#ffffff"));
                break;
            case R.id.tv_time_35:
                currNormalTime = 35;
                resetBtn(2);
                edNormalSc.setVisibility(View.GONE);
                tvTime35.setBackgroundResource(R.drawable.bg_button_round);
                tvTime35.setTextColor(Color.parseColor("#ffffff"));
                break;
            case R.id.tv_time_zdy:
                currNormalTime = -1;
                edNormalSc.setVisibility(View.VISIBLE);
                resetBtn(2);
                tvTimeZdy.setBackgroundResource(R.drawable.bg_button_round);
                tvTimeZdy.setTextColor(Color.parseColor("#ffffff"));
                break;
            case R.id.tv_tips:
                break;
            case R.id.iv_ok:
                commit();
                break;
        }
    }

    /**
     * 选择时期
     */
    public void onYearMonthDayPicker() {
        final DatePicker picker = new DatePicker(mActivity);
        Calendar now = new GregorianCalendar();
        now.setTime(new Date());
        now.add(Calendar.DAY_OF_MONTH, 1);
        int year = now.get(Calendar.YEAR);
        int month = (now.get(Calendar.MONTH) + 1);
        int day = now.get(Calendar.DAY_OF_MONTH);
        picker.setTopPadding(15);
        picker.setRangeStart(year, month, day);
        picker.setRangeEnd(year + 1, month, day);
        picker.setSelectedItem(year, month, day);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                tvSettimeAim.setText(year + "-" + month + "-" + day);
                currAimEndTime = year + "-" + month + "-" + day;
            }
        });
        picker.show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void commonCloseNotify(CommonCloseNotify event) {
        showCLoseDialog();
    }

    private void showCLoseDialog() {
        MyAlertDialog myAlertDialog = new MyAlertDialog(mActivity, "提示", "确定放弃已编辑的内容？");
        myAlertDialog.setOnClickListener(new MyAlertDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClickListener(View v) {
                (( BackFragmentActivity ) mActivity).closeAllFragment();
            }
        });
        myAlertDialog.show();
    }
}
