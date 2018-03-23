package com.cretin.studyhelper.fragment.study.plan;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cetin.studyhelper.R;
import com.cretin.studyhelper.app.LocalStorageKeys;
import com.cretin.studyhelper.base.BackFragmentActivity;
import com.cretin.studyhelper.base.BaseFragment;
import com.cretin.studyhelper.eventbus.CommonCloseNotify;
import com.cretin.studyhelper.eventbus.StydyDataRegreshNotify;
import com.cretin.studyhelper.eventbus.TimeUpNotify;
import com.cretin.studyhelper.model.PlanSingleTimeModel;
import com.cretin.studyhelper.model.PlansModel;
import com.cretin.studyhelper.model.UnfinishedTaskModel;
import com.cretin.studyhelper.utils.KV;
import com.cretin.studyhelper.utils.ResourceUtil;
import com.cretin.studyhelper.utils.UiUtils;
import com.cretin.studyhelper.view.MyAlertDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaningFragment extends BaseFragment {
    public static final String TAG = "PlaningFragment";
    @Bind( R.id.iv_background )
    ImageView ivBackground;
    @Bind( R.id.tv_time )
    TextView tvTime;
    @Bind( R.id.tv_flag )
    TextView tvFlag;
    @Bind( R.id.tv_tips )
    TextView tvTips;
    @Bind( R.id.tv_saying )
    TextView tvSaying;
    @Bind( R.id.tv_title )
    TextView tvTitle;
    @Bind( R.id.tv_state )
    TextView tvState;
    @Bind( R.id.iv_stop )
    ImageView ivStop;

    private PlansModel currModel;
    private UnfinishedTaskModel currUnfinishedTaskModel;

    private long allTime;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_planing;
    }

    public static PlaningFragment newInstance(UnfinishedTaskModel p) {
        Bundle args = new Bundle();
        args.putSerializable("bean", p);
        PlaningFragment fragment = new PlaningFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(View contentView, Bundle savedInstanceState) {
        hidTitleView();

        currUnfinishedTaskModel = ( UnfinishedTaskModel ) getArguments().getSerializable("bean");
        if ( currUnfinishedTaskModel != null ) {
            currModel = currUnfinishedTaskModel.getAimItem();

            passTime = ( int ) currUnfinishedTaskModel.getPassedTime();
        }

        //设置屏幕常亮
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void initData() {
        if ( currModel != null ) {
            //设置其他信息
            if ( currModel.getPlanType() == PlansModel.PLAN_TYPE_AIM ) {
                //目标
                long hasPassedTime = 0;
                List<PlanSingleTimeModel> singleTimeModelList = currModel.getSingleTimeModelList();
                if ( singleTimeModelList != null )
                    for ( PlanSingleTimeModel p :
                            singleTimeModelList ) {
                        hasPassedTime += p.getTimes();
                    }

                String time = currModel.getAimTime() + (currModel.getAimTimeType() == PlansModel.TIME_TYPE_HOUR ? "小时" : "分钟");
                allTime = (currModel.getAimTimeType() == PlansModel.TIME_TYPE_HOUR ? (currModel.getAimTime() * 60 * 60) :
                        (currModel.getAimTime() * 60)) - currUnfinishedTaskModel.getPassedTime() - hasPassedTime;
                tvTitle.setText(currModel.getPlanName());
                tvTips.setVisibility(View.VISIBLE);
                tvTips.setText("目标-在 " + currModel.getAimEndTime() + " 前完成 " + time);
                //计算剩下的时间来显示
                setTimer();
            } else if ( currModel.getPlanType() == PlansModel.PLAN_TYPE_NORMAL ) {
                //正常
                if ( currModel.getNormalType() == PlansModel.NORMAL_TYPE_BJS ) {
                    //不计时  不会来到这个页面
                } else if ( currModel.getNormalType() == PlansModel.NORMAL_TYPE_DJS ) {
                    //倒计时
                    tvTitle.setText(currModel.getPlanName());
                    allTime = currModel.getNormalTime() * 60 - currUnfinishedTaskModel.getPassedTime();
                    setTimer();
                } else if ( currModel.getNormalType() == PlansModel.NORMAL_TYPE_ZJS ) {
                    //正计时
                    tvTitle.setText(currModel.getPlanName());
                    allTime = currUnfinishedTaskModel.getPassedTime();
                    setTimer();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //设置每日一句
        getSayings();
    }

    //处理TImer
    private void setTimer() {
        if ( mTimer == null ) {
            mTimer = new Timer();
        } else {
            mTimer.cancel();
            mTimer = null;
            mTimer = new Timer();
        }
        mTimer.schedule(timerTask, 0, 1000);
    }

    //显示时间
    private void showTime(long time) {
        //计算分钟
        int minute = ( int ) (time / 60);
        int second = ( int ) (time % 60);
        if ( tvTime != null )
            tvTime.setText((minute < 10 ? ("0" + minute) : minute) + ":" + (second < 10 ? ("0" + second) : second));

        if ( allTime == 0 ) {
            flag = true;
            //时间到了
            if ( mTimer != null ) {
                mTimer.cancel();
                mTimer = null;
            }
            finishTask(true);
        }
    }

    private Timer mTimer;

    //记录经过的时间
    private int passTime;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //每10s存储一次数据
            passTime++;
            if ( currModel != null ) {
                if ( currModel.getPlanType() == PlansModel.PLAN_TYPE_AIM ) {
                    //目标
                    allTime--;
                    showTime(allTime);
                } else if ( currModel.getPlanType() == PlansModel.PLAN_TYPE_NORMAL ) {
                    //正常
                    if ( currModel.getNormalType() == PlansModel.NORMAL_TYPE_DJS ) {
                        //倒计时
                        allTime--;
                        showTime(allTime);
                    } else if ( currModel.getNormalType() == PlansModel.NORMAL_TYPE_ZJS ) {
                        //正计时
                        allTime++;
                        showTime(allTime);
                    }
                }
                savaState();
            }
        }
    };

    private UnfinishedTaskModel unfinishedTaskModel;

    //保存现在的状态
    private void savaState() {
        if ( !flag )
            if ( passTime % 10 == 0 ) {
                if ( unfinishedTaskModel == null ) {
                    unfinishedTaskModel = new UnfinishedTaskModel();
                    unfinishedTaskModel.setState(0);
                    unfinishedTaskModel.setAimItem(currModel);
                }
                unfinishedTaskModel.setPassedTime(passTime);

                KV.put(LocalStorageKeys.UNFINISH_TASK, unfinishedTaskModel);
            }
    }

    private boolean flag;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if ( mTimer != null ) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private List<String> list;

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            handler.sendEmptyMessage(0);
        }
    };

    private void getSayings() {
        String[] reses = new String[]{"p01", "p02", "p03", "p04", "p05", "p06"};
        int resId = ResourceUtil.getResource(reses[new Random().nextInt(6)],
                mActivity, "mipmap");
        ivBackground.setImageResource(resId);

        if ( list != null && !list.isEmpty() ) {
            int num = new Random().nextInt(list.size());
            String text = list.get(num);
            if ( tvSaying != null )
                tvSaying.setText(text);
            return;
        }
        InputStream is = getResources().openRawResource(R.raw.sayings);
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(is, "utf-8");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            if ( list == null )
                list = new ArrayList<>();
            String line;
            try {
                while ( (line = reader.readLine()) != null ) {
                    list.add(line);
                }
            } catch ( IOException e ) {
                e.printStackTrace();
            }

            if ( !list.isEmpty() ) {
                int num = new Random().nextInt(list.size());
                String text = list.get(num);
                if ( tvSaying != null )
                    tvSaying.setText(text);
            }
        } catch ( UnsupportedEncodingException e1 ) {
            e1.printStackTrace();
        }
    }


    @OnClick( R.id.iv_stop )
    public void onViewClicked() {
        showCloseDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void closePlaningNotify(CommonCloseNotify event) {
        if ( passTime <= 5 ) {
            UiUtils.showToastInAnyThread("不记录5s以内的任务");
            (( BackFragmentActivity ) mActivity).removeFragment();
            return;
        }
        showCloseDialog();
    }

    //显示点击关闭按钮的提示信息
    private void showCloseDialog() {
        View view = View.inflate(mActivity, R.layout.layout_planing_dialog, null);
        final MyAlertDialog myAlertDialog = new MyAlertDialog(mActivity, view);
        //放弃
        view.findViewById(R.id.ll_fangqi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAlertDialog.dismiss();
                if ( mTimer != null ) {
                    mTimer.cancel();
                    mTimer = null;
                }
                //清空数据
                KV.put(LocalStorageKeys.UNFINISH_TASK, null);
                (( BackFragmentActivity ) mActivity).removeFragment();
            }
        });
        //取消
        view.findViewById(R.id.ll_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAlertDialog.dismiss();
            }
        });
        //提前
        view.findViewById(R.id.ll_tiqian).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAlertDialog.dismiss();
                if ( mTimer != null ) {
                    mTimer.cancel();
                    mTimer = null;
                }
                //提前结束任务
                finishTask(false);
            }
        });
        myAlertDialog.show();
    }

    //结束任务 flag true 时间到
    private void finishTask(final boolean flag) {
        //清空数据
        KV.put(LocalStorageKeys.UNFINISH_TASK, null);
        showDialog("请稍后...");
        if ( currModel.getPlanType() == PlansModel.PLAN_TYPE_AIM ) {
            //目标 把信息放进去
            List<PlanSingleTimeModel> singleTimeModelList =
                    currModel.getSingleTimeModelList();
            if ( singleTimeModelList == null ) {
                singleTimeModelList = new ArrayList<>();
            }
            PlanSingleTimeModel planSingleTimeModel = new PlanSingleTimeModel();
            planSingleTimeModel.setTimes(passTime);
            planSingleTimeModel.setCommitTimeLong(System.currentTimeMillis());
            singleTimeModelList.add(planSingleTimeModel);
            currModel.setSingleTimeModelList(singleTimeModelList);

            if ( flag ) {
                currModel.setCurrFlag(1);
            }

            //更新数据
            currModel.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    stopDialog();
                    if ( e == null ) {
                        //更新成功
                        EventBus.getDefault().post(new StydyDataRegreshNotify());
                        if ( flag ) {
                            String time = currModel.getAimTime() + (currModel.getAimTimeType() == PlansModel.TIME_TYPE_HOUR ? "小时" : "分钟");
                            EventBus.getDefault().post(new TimeUpNotify(currModel.getPlanName() + "-计划 " + time + " 已完成"));
                        }
                        (( BackFragmentActivity ) mActivity).removeFragment();
                    } else {
                        UiUtils.showToastInAnyThreadFail();
                    }
                }
            });
        } else if ( currModel.getPlanType() == PlansModel.PLAN_TYPE_NORMAL ) {
            //正常 直接设置成已完成
            currModel.setCurrFlag(1);
            currModel.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    stopDialog();
                    if ( e == null ) {
                        //更新成功
                        EventBus.getDefault().post(new StydyDataRegreshNotify());
                        if ( currModel.getNormalType() == PlansModel.NORMAL_TYPE_DJS ) {
                            //倒计时
                            String time = currModel.getNormalTime() + "分钟";
                            EventBus.getDefault().post(new TimeUpNotify(currModel.getPlanName() + "-倒计时 " + time + " 已完成"));
                        }
                        (( BackFragmentActivity ) mActivity).removeFragment();
                    } else {
                        UiUtils.showToastInAnyThreadFail();
                    }
                }
            });
        }
    }
}
