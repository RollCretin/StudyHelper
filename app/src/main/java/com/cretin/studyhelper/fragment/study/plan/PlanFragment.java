package com.cretin.studyhelper.fragment.study.plan;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

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
import com.cretin.studyhelper.model.UnfinishedTaskModel;
import com.cretin.studyhelper.ui.manager.PlaningActivityManager;
import com.cretin.studyhelper.ui.manager.StudyActivityManager;
import com.cretin.studyhelper.utils.KV;
import com.cretin.studyhelper.utils.StringUtils;
import com.cretin.studyhelper.utils.UiUtils;
import com.cretin.studyhelper.view.MyAlertDialog;
import com.hmy.popwindow.PopItemAction;
import com.hmy.popwindow.PopWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

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

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_plan;
    }

    @Override
    protected void initView(View contentView, Bundle savedInstanceState) {
        hidTitleView();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
        adapter.setEmptyView(R.layout.empty_view);
        recyclerview.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                showDetailsDialog(position);
            }
        });

        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                showPopWindow(position, adapter);
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
    private void showPopWindow(final int position, BaseQuickAdapter adapter) {
        final PlansModel planModel = list.get(position);
        PopWindow.Builder builder = new PopWindow.Builder(mActivity);
        builder.setStyle(PopWindow.PopWindowStyle.PopUp);
        builder.addItemAction(new PopItemAction("查看详细数据", PopItemAction.PopItemStyle.Normal, new PopItemAction.OnClickListener() {
            @Override
            public void onClick() {
                showDetailsDialog(position);
            }
        }));
        if ( planModel.getCurrFlag() == 0 ) {
            //进行中
            if ( planModel.getPlanType() == PlansModel.PLAN_TYPE_AIM ) {
                //目标
                try {
                    int left = StringUtils.differentDays(new Date(), simpleDateFormat.parse(planModel.getAimEndTime()));
                    if ( left <= 0 ) {
                        //已过期
                    } else {
                        //未过期
                        builder.addItemAction(new PopItemAction("编辑计划", PopItemAction.PopItemStyle.Normal, new PopItemAction.OnClickListener() {
                            @Override
                            public void onClick() {
                                //去编辑
                                editPlan(planModel);
                            }
                        }));
                    }
                } catch ( ParseException e ) {
                    e.printStackTrace();
                }
            } else {
                builder.addItemAction(new PopItemAction("编辑计划", PopItemAction.PopItemStyle.Normal, new PopItemAction.OnClickListener() {
                    @Override
                    public void onClick() {
                        //去编辑
                        editPlan(planModel);
                    }
                }));
            }
        }
        builder.addItemAction(new PopItemAction("删除计划", PopItemAction.PopItemStyle.Warning, new PopItemAction.OnClickListener() {
            @Override
            public void onClick() {
                delete(position, planModel);
            }
        })).addItemAction(new PopItemAction("取消", PopItemAction.PopItemStyle.Cancel));
        builder.create().show();
    }

    private void delete(final int position, PlansModel planModel) {
        showDialog("请稍后...");
        planModel.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if ( e == null ) {
                    //去删除
                    list.remove(position);
                    PlanFragment.this.adapter.notifyItemRemoved(position);
                    PlanFragment.this.adapter.notifyItemRangeChanged(position, 1);
                    UiUtils.showToastInAnyThread("删除成功");
                } else {
                    UiUtils.showToastInAnyThreadFail();
                }
                stopDialog();
            }
        });
    }

    //编辑计划
    private void editPlan(PlansModel planModel) {
        Intent intent = new Intent(mActivity, StudyActivityManager.class);
        intent.putExtra(BackFragmentActivity.TAG_FRAGMENT, AddPlanFragment.TAG);
        Bundle bundle = new Bundle();
        bundle.putInt("type", AddPlanFragment.TYPE_EDIT);
        bundle.putString("id", planModel.getObjectId());
        intent.putExtra(BaseFragmentActivity.ARGS, bundle);
        mActivity.startActivity(intent);
    }

    private SimpleDateFormat simpleDateFormat;

    private void showDetailsDialog(int position) {
        if ( simpleDateFormat == null )
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        View view = View.inflate(mActivity, R.layout.layout_plan_details, null);
        final MyAlertDialog myAlertDialog = new MyAlertDialog(mActivity, view);
        myAlertDialog.show();
        PlansModel planModel = list.get(position);
        String flag = "";
        if ( planModel.getCurrFlag() == 0 ) {
            //进行中
            if ( planModel.getPlanType() == PlansModel.PLAN_TYPE_AIM ) {
                //目标
                try {
                    int left = StringUtils.differentDays(new Date(), simpleDateFormat.parse(planModel.getAimEndTime()));
                    if ( left <= 0 ) {
                        //已过期
                        if ( planModel.getSingleTimeModelList() != null && !planModel.getSingleTimeModelList().isEmpty() ) {
                            //已执行了  为执行完
                            flag = "未执行完成，已过期\n过期时间：" + planModel.getAimEndTime();
                        } else {
                            //压根未执行
                            flag = "未执行，已过期\n过期时间：" + planModel.getAimEndTime();
                        }

                    } else {
                        //未过期
                        flag = "执行中，未过期\n过期时间：" + planModel.getAimEndTime();
                    }
                } catch ( ParseException e ) {
                    e.printStackTrace();
                }
            } else {
                //普通
                flag = "未执行，等待执行";
            }
        } else {
            //已完成
            flag = "已完成...\n完成时间：" + planModel.getUpdatedAt();
        }
        String content = "当前状态：" + flag + "\n类型：" +
                (planModel.getPlanType() == PlansModel.PLAN_TYPE_AIM ? "目标计划" : "普通计划") +
                "\n备注：\n" + (TextUtils.isEmpty(planModel.getRemark()) ? "无" :
                planModel.getRemark());
        (( TextView ) view.findViewById(R.id.tv_content)).setText(content);
        (( TextView ) view.findViewById(R.id.tv_title)).setText(planModel.getPlanName());

        //给确定添加点击事件
        view.findViewById(R.id.tv_iknow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAlertDialog.dismiss();
            }
        });
    }

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

    class ListAdapter extends BaseQuickAdapter<PlansModel, BaseViewHolder> {

        public ListAdapter(List list) {
            super(R.layout.item_recyclerview_plan, list);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final PlansModel item) {
            helper.setText(R.id.tv_title, item.getPlanName());

            if ( item.getCurrFlag() == 0 ) {
                //未过期
                helper.setBackgroundRes(R.id.ll_container, R.drawable.bg_button_round);
                helper.setText(R.id.tv_start, "开始");
                helper.getView(R.id.tv_start).setEnabled(true);
            } else {
                //已完成
                helper.setBackgroundRes(R.id.ll_container, R.drawable.bg_button_round_gray);
                helper.setText(R.id.tv_start, "已完成");
                helper.getView(R.id.tv_start).setEnabled(false);
            }

            if ( item.getPlanType() == PlansModel.PLAN_TYPE_AIM ) {
                //目标
                String time = item.getAimTime() + (item.getAimTimeType() == PlansModel.TIME_TYPE_HOUR ? "小时" : "分钟");
                int timeValue = item.getAimTimeType() == PlansModel.TIME_TYPE_HOUR ? (item.getAimTime() * 60) :
                        item.getAimTime();
                helper.setText(R.id.tv_tag_01, time + " - 目标");

                helper.setVisible(R.id.tv_tag_02, true);
                helper.setVisible(R.id.tv_tag_03, true);
                helper.setVisible(R.id.tv_tag_04, true);

                if ( item.getCurrFlag() == 0 ) {
                    try {
                        int left = StringUtils.differentDays(new Date(), simpleDateFormat.parse(item.getAimEndTime()));
                        if ( left <= 0 ) {
                            //已过期
                            helper.setBackgroundRes(R.id.ll_container, R.drawable.bg_button_round_orange);
                            helper.setText(R.id.tv_tag_04, "已过期,不可用");
                            helper.setText(R.id.tv_start, "已过期");
                            helper.getView(R.id.tv_start).setEnabled(false);
                        } else {
                            //未过期
                            helper.setText(R.id.tv_tag_04, "离计划结束:" +
                                    left + "天");
                        }

                    } catch ( ParseException e ) {
                        e.printStackTrace();
                    }
                }

                if ( simpleDateFormat == null ) {
                    simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
                    helper.setText(R.id.tv_tag_01, "正计时");
                }
            }

            helper.getView(R.id.tv_start).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, PlaningActivityManager.class);
                    intent.putExtra(BackFragmentActivity.TAG_FRAGMENT, PlaningFragment.TAG);
                    UnfinishedTaskModel u = new UnfinishedTaskModel();
                    u.setState(2);
                    u.setAimItem(item);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("bean", u);
                    intent.putExtra(BaseFragmentActivity.ARGS, bundle);
                    mActivity.startActivity(intent);
                }
            });
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
