package com.cretin.studyhelper.fragment.study.plan;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cetin.studyhelper.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cretin.studyhelper.app.LocalStorageKeys;
import com.cretin.studyhelper.base.BackFragmentActivity;
import com.cretin.studyhelper.base.BaseFragment;
import com.cretin.studyhelper.base.BaseFragmentActivity;
import com.cretin.studyhelper.eventbus.StydyDataRegreshNotify;
import com.cretin.studyhelper.model.CusUser;
import com.cretin.studyhelper.model.PlanSingleTimeModel;
import com.cretin.studyhelper.model.PlansModel;
import com.cretin.studyhelper.ui.manager.StudyActivityManager;
import com.cretin.studyhelper.utils.KV;
import com.cretin.studyhelper.utils.StringUtils;
import com.cretin.studyhelper.utils.UiUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * A simple {@link Fragment} subclass.
 * 学习-计划 页面
 */
public class PlanFragment extends BaseFragment {
    public static final String TAG = "PlanFragment";
    @Bind( R.id.recyclerview )
    RecyclerView recyclerview;
    @Bind( R.id.swipe_refresh )
    SwipeRefreshLayout swipeRefresh;
    @Bind( R.id.fab )
    FloatingActionButton fab;
    private List<PlansModel> list;

    private ListAdapter adapter;

    //计时器 轮询任务
    private List<String> taskStartTime;
    private Timer timer;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_plan;
    }

    @Override
    protected void initView(View contentView, Bundle savedInstanceState) {
        hidTitleView();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        taskStartTime = new ArrayList<>();
        swipeRefresh.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(0);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, StudyActivityManager.class);
                intent.putExtra(BackFragmentActivity.TAG_FRAGMENT, AddPlanFragment.TAG);
                Bundle bundle = new Bundle();
                bundle.putInt("type", AddPlanFragment.TYPE_ADD);
                bundle.putString("id", "");
                intent.putExtra(BaseFragmentActivity.ARGS, bundle);
                mActivity.startActivity(intent);
            }
        });
    }

    @Override
    protected void initData() {
        recyclerview.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        recyclerview.setLayoutManager(linearLayoutManager);
        list = new ArrayList();
        adapter = new ListAdapter(list);
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        adapter.setNotDoAnimationCount(2);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getData(currPage);
            }
        }, recyclerview);
        recyclerview.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                showDetailsDialog(position);
            }
        });

        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
//                showPopWindow(position, adapter);
                return false;
            }
        });

        showDialog("加载中...");
        getData(0);
    }

    /**
     * 显示选项
     * <p>
     * //     * @param position
     * //     * @param adapter
     */
//    private void showPopWindow(final int position, BaseQuickAdapter adapter) {
//        final PlanModel planModel = list.get(position);
//
//        if ( planModel.getCurrFlag() == 0 ) {
//            //进行中
//            PopWindow popWindow = new PopWindow.Builder(mActivity)
//                    .setStyle(PopWindow.PopWindowStyle.PopUp)
//                    .setTitle("请选择")
//                    .addItemAction(new PopItemAction("编辑计划", PopItemAction.PopItemStyle.Normal, new PopItemAction.OnClickListener() {
//                        @Override
//                        public void onClick() {
//                            //去编辑
//                            editPlan(planModel);
//                        }
//                    }))
//                    .addItemAction(new PopItemAction("删除计划", PopItemAction.PopItemStyle.Warning, new PopItemAction.OnClickListener() {
//                        @Override
//                        public void onClick() {
//                            delete(position, planModel);
//                        }
//                    }))
//                    .addItemAction(new PopItemAction("取消", PopItemAction.PopItemStyle.Cancel))
//                    .create();
//            popWindow.show();
//        } else {
//            PopWindow popWindow = new PopWindow.Builder(mActivity)
//                    .setStyle(PopWindow.PopWindowStyle.PopUp)
//                    .setTitle("请选择")
//                    .addItemAction(new PopItemAction("删除计划", PopItemAction.PopItemStyle.Warning, new PopItemAction.OnClickListener() {
//                        @Override
//                        public void onClick() {
//                            //去删除
//                            delete(position, planModel);
//                        }
//                    }))
//                    .addItemAction(new PopItemAction("取消", PopItemAction.PopItemStyle.Cancel))
//                    .create();
//            popWindow.show();
//        }
//    }

//    private void delete(final int position, PlanModel planModel) {
//        showDialog("请稍后...");
//        planModel.delete(new UpdateListener() {
//            @Override
//            public void done(BmobException e) {
//                if ( e == null ) {
//                    //去删除
//                    list.remove(position);
//                    handlerData(list);
//                    PlanFragment.this.adapter.notifyItemRemoved(position);
//                    PlanFragment.this.adapter.notifyItemRangeChanged(position, 1);
//                    UiUtils.showToastInAnyThread("删除成功");
//                } else {
//                    UiUtils.showToastInAnyThreadFail();
//                }
//                stopDialog();
//            }
//        });
//    }

//    //编辑计划
//    private void editPlan(PlanModel planModel) {
//        Intent intent = new Intent(mActivity, StudyActivityManager.class);
//        intent.putExtra(BackFragmentActivity.TAG_FRAGMENT, AddPlanFragment.TAG);
//        Bundle bundle = new Bundle();
//        bundle.putInt("type", AddPlanFragment.TYPE_EDIT);
//        bundle.putString("id", planModel.getObjectId());
//        intent.putExtra(BaseFragmentActivity.ARGS, bundle);
//        mActivity.startActivity(intent);
//    }

    private SimpleDateFormat simpleDateFormat;

//    private void showDetailsDialog(int position) {
//        if ( simpleDateFormat == null )
//            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        View view = View.inflate(mActivity, R.layout.layout_plan_details, null);
//        final MyAlertDialog myAlertDialog = new MyAlertDialog(mActivity, view);
//        myAlertDialog.show();
//        PlanModel planModel = list.get(position);
//        String flag = "";
//        if ( planModel.getCurrFlag() == 0 ) {
//            //进行中
//            //根据时间来判断当前状态
//            try {
//                long currTime = System.currentTimeMillis();
//                long startTime = simpleDateFormat.parse(planModel.getStartTime()).getTime();
//                long endTime = simpleDateFormat.parse(planModel.getEndTime()).getTime();
//                if ( currTime < startTime ) {
//                    flag = "未开始...";
//                } else if ( currTime < endTime ) {
//                    flag = "进行中...";
//                } else {
//                    flag = "已超时...";
//                }
//            } catch ( ParseException e ) {
//                e.printStackTrace();
//            }
//        } else {
//            //已完成
//            flag = "已完成...";
//        }
//        String content = "开始时间：" + planModel.getStartTime() + "\n结束时间：" + planModel.getEndTime()
//                + "\n当前状态：" + flag + "\n备注：\n" + (TextUtils.isEmpty(planModel.getRemark()) ? "无" :
//                planModel.getRemark());
//        (( TextView ) view.findViewById(R.id.tv_content)).setText(content);
//        (( TextView ) view.findViewById(R.id.tv_title)).setText(planModel.getTitle());
//
//        //给确定添加点击事件
//        view.findViewById(R.id.tv_iknow).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                myAlertDialog.dismiss();
//            }
//        });
//    }

    private int currPage;

    //获取数据
    private void getData(final int page) {
        currPage = page + 1;
        CusUser cusUser = KV.get(LocalStorageKeys.USER_INFO);
        if ( cusUser != null ) {
            BmobQuery<PlansModel> query = new BmobQuery<PlansModel>();
            query.addWhereEqualTo("userId", cusUser.getObjectId());
            query.setLimit(10);
            query.setSkip(page * 10);
            query.order("currFlag,-createdAt");
            query.findObjects(new FindListener<PlansModel>() {
                @Override
                public void done(List<PlansModel> object, BmobException e) {
                    if ( e == null ) {
                        if ( page == 0 ) {
                            list.clear();
                        }
                        list.addAll(object);
                        if ( object.size() < 10 ) {
                            adapter.loadMoreEnd();
                        } else {
                            adapter.loadMoreComplete();
                        }
//                        handlerData(list);
                        adapter.notifyDataSetChanged();
                    } else {
                        UiUtils.showToastInAnyThreadFail();
                    }
                    swipeRefresh.setRefreshing(false);
                    stopDialog();
                }
            });
        }
    }

    /**
     * 处理数据
     * <p>
     * //     * @param list
     */
//    private void handlerData(final List<PlanModel> list) {
//        if ( simpleDateFormat == null ) {
//            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        }
//        if ( !list.isEmpty() ) {
//            //根据状态先排序
//            Collections.sort(list);
//            ////当前状态 0 进行中 1 已完成
//            int state = -1;
//            taskStartTime.clear();
//            if ( timer != null ) {
//                timer.cancel();
//                timer = null;
//            }
//            for ( PlanModel p :
//                    list ) {
//                if ( p.getRemindFlag() != 0 ) {
//                    long time = 0;
//                    //开启了提醒 0 不提醒 1 提前5分钟 2 提前10分钟 3 提前半小时 4 提前一小时
//                    switch ( p.getRemindFlag() ) {
//                        case 1:
//                            time = 5 * 60;
//                            break;
//                        case 2:
//                            time = 10 * 60;
//                            break;
//                        case 3:
//                            time = 30 * 60;
//                            break;
//                        case 4:
//                            time = 60 * 60;
//                            break;
//                    }
//
//                    try {
//                        //计算本来应该开始提醒的时间 s 为单位
//                        long startTime = simpleDateFormat.parse(p.getStartTime()).getTime() / 1000 - time;
//                        if ( startTime > (System.currentTimeMillis() + 10 * 1000) / 1000 ) {
//                            String msg = p.getTitle() + "-" + StringUtils.formatTimeStr(p.getStartTimeValue(),
//                                    "yy/MM/dd HH:mm") + "~" +
//                                    StringUtils.formatTimeStr(p.getEndTimeValue(), "yy/MM/dd HH:mm");
//                            taskStartTime.add(startTime + "#" + msg);
//                        }
//                    } catch ( ParseException e ) {
//
//                    }
//                }
//                if ( p.getCurrFlag() != state ) {
//                    p.setFirst(true);
//                    state = p.getCurrFlag();
//                } else {
//                    p.setFirst(false);
//                }
//            }
//
//            if ( !taskStartTime.isEmpty() ) {
//                Collections.sort(taskStartTime);
//                //有任务
//                timer = new Timer();
//                timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        long currTime = System.currentTimeMillis() / 1000;
//                        Log.e("HHHHHHH", "" + currTime + "    " + taskStartTime.get(0).split("#")[0]);
//                        if ( currTime == Long.parseLong(taskStartTime.get(0).split("#")[0]) ) {
//                            //发起通知
//                            EventBus.getDefault().post(new TimeUpNotify(taskStartTime.get(0).split("#")[1]));
//                            taskStartTime.remove(0);
//                            if ( taskStartTime.isEmpty() ) {
//                                timer.cancel();
//                                timer = null;
//                                getData(0);
//                            }
//                        }
//                    }
//                }, 0, 1000);
//            }
//        }
//    }

    class ListAdapter extends BaseQuickAdapter<PlansModel, BaseViewHolder> {

        public ListAdapter(List list) {
            super(R.layout.item_recyclerview_plan, list);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final PlansModel item) {
            helper.setText(R.id.tv_title, item.getPlanName());

            if ( item.getPlanType() == PlansModel.PLAN_TYPE_AIM ) {
                //目标
                String time = item.getAimTime() + (item.getAimTimeType() == PlansModel.TIME_TYPE_HOUR ? "小时" : "分钟");
                int timeValue = item.getAimTimeType() == PlansModel.TIME_TYPE_HOUR ? (item.getAimTime() * 60) :
                        item.getAimTime();
                helper.setText(R.id.tv_tag_01, time + " - 目标");
                helper.setVisible(R.id.tv_tag_02, true);
                helper.setVisible(R.id.tv_tag_03, true);
                helper.setVisible(R.id.tv_tag_04, true);

                if ( simpleDateFormat == null ) {
                    simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                }
                try {
                    helper.setText(R.id.tv_tag_04, "离计划结束:" +
                            StringUtils.differentDays(new Date(), simpleDateFormat.parse(item.getAimEndTime())) + "天");
                } catch ( ParseException e ) {
                    e.printStackTrace();
                }
                //获取时间
                List<PlanSingleTimeModel> singleTimeModelList =
                        item.getSingleTimeModelList();
                if ( singleTimeModelList != null ) {
                    int allTimes = 0;
                    for ( PlanSingleTimeModel p :
                            singleTimeModelList ) {
                        allTimes += p.getTimes();
                    }
                    int minute = allTimes / 60;
                    int posotion = minute * 100 / timeValue;
                    helper.setText(R.id.tv_tag_02, posotion + "%");
                    helper.setText(R.id.tv_tag_03, minute + "/" + timeValue + "分钟");
                } else {
                    helper.setText(R.id.tv_tag_02, "0%");
                    helper.setText(R.id.tv_tag_03, "0/" + timeValue + "分钟");
                }
            } else if ( item.getPlanType() == PlansModel.PLAN_TYPE_NORMAL ) {
                helper.setVisible(R.id.tv_tag_02, false);
                helper.setVisible(R.id.tv_tag_03, false);
                helper.setVisible(R.id.tv_tag_04, false);
                //正常
                if ( item.getNormalType() == PlansModel.NORMAL_TYPE_BJS ) {
                    //不计时
                    helper.setText(R.id.tv_tag_01, "普通待办");


                } else if ( item.getNormalType() == PlansModel.NORMAL_TYPE_DJS ) {
                    //倒计时
                    String time = item.getNormalTime() + "分钟";
                    helper.setText(R.id.tv_tag_01, time + " - 倒计时");
                } else if ( item.getNormalType() == PlansModel.NORMAL_TYPE_ZJS ) {
                    //正计时
                    String time = item.getNormalTime() + "分钟";
                    helper.setText(R.id.tv_tag_01, time + " - 正计时");
                }
            }


//            TextView tv_flag = helper.getView(R.id.tv_flag);
//
//            String flag = "";
//            helper.setVisible(R.id.iv_naozhong, false);
//            if ( item.getCurrFlag() == 0 ) {
//                //进行中
//                //根据时间来判断当前状态
//                try {
//                    long currTime = System.currentTimeMillis();
//                    long startTime = simpleDateFormat.parse(item.getStartTime()).getTime();
//                    long endTime = simpleDateFormat.parse(item.getEndTime()).getTime();
//                    if ( currTime < startTime ) {
//                        flag = "未开始...";
//                        tv_flag.setTextColor(Color.parseColor("#03A9F4"));
//                        checkTime(helper, item);
//                    } else if ( currTime < endTime ) {
//                        flag = "进行中...";
//                        tv_flag.setTextColor(Color.parseColor("#2aa515"));
//                    } else {
//                        flag = "已超时...";
//                        tv_flag.setTextColor(Color.parseColor("#F44336"));
//                    }
//                } catch ( ParseException e ) {
//                    e.printStackTrace();
//                }
//                helper.setImageResource(R.id.tv_state, R.mipmap.select);
//                helper.getView(R.id.tv_state).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        MyAlertDialog myAlertDialog = new MyAlertDialog(mActivity, "温馨提示",
//                                "是否将《" + item.getTitle() + "》设置成已完成？");
//                        myAlertDialog.setOnClickListener(new MyAlertDialog.OnPositiveClickListener() {
//                            @Override
//                            public void onPositiveClickListener(View v) {
//                                showDialog("请稍后...");
//                                //设置成已完成
//                                item.setCurrFlag(1);
//                                item.update(new UpdateListener() {
//                                    @Override
//                                    public void done(BmobException e) {
//                                        stopDialog();
//                                        if ( e == null ) {
//                                            UiUtils.showToastInAnyThread("操作成功");
//                                            handlerData(list);
//                                            adapter.notifyDataSetChanged();
//                                        } else {
//                                            UiUtils.showToastInAnyThreadFail();
//                                            helper.setImageResource(R.id.tv_state, R.mipmap.select);
//                                        }
//                                    }
//                                });
//                            }
//                        });
//                        myAlertDialog.setOnNegativeListener(new MyAlertDialog.OnNegativeClickListener() {
//                            @Override
//                            public void onNegativeClickListener(View v) {
//                            }
//                        });
//                        myAlertDialog.show();
//                    }
//                });
//            } else {
//                //已完成
//                flag = "已完成...";
//                tv_flag.setTextColor(Color.parseColor("#9b9b9b"));
//                helper.setImageResource(R.id.tv_state, R.mipmap.select_done);
//            }
//            if ( item.isFirst() ) {
//                helper.getView(R.id.tv_show).setVisibility(View.VISIBLE);
//                if ( item.getCurrFlag() == 0 ) {
//                    helper.setText(R.id.tv_show, "进行中");
//                } else {
//                    helper.setText(R.id.tv_show, "已完成");
//                }
//            } else {
//                helper.getView(R.id.tv_show).setVisibility(View.GONE);
//            }
//
//            helper.setText(R.id.tv_time, StringUtils.formatTimeStr(item.getStartTimeValue(), "yy/MM/dd HH:mm"));
//            helper.setText(R.id.tv_flag, flag);
        }
    }

    @Subscribe
    public void stydyDataRegreshNotify(StydyDataRegreshNotify event) {
        getData(0);
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
}
