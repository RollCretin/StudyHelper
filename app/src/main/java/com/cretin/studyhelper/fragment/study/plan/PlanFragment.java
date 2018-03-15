package com.cretin.studyhelper.fragment.study.plan;


import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cetin.studyhelper.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cretin.studyhelper.base.BaseFragment;
import com.cretin.studyhelper.model.CusUser;
import com.cretin.studyhelper.model.PlanModel;
import com.cretin.studyhelper.utils.StringUtils;
import com.cretin.studyhelper.utils.UiUtils;
import com.cretin.studyhelper.view.ItemButtomDecoration;
import com.cretin.studyhelper.view.PopupMenuDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
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
    private List<PlanModel> list;

    private ListAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_plan;
    }

    @Override
    protected void initView(View contentView, Bundle savedInstanceState) {
        hidTitleView();
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
                new PopupMenuDialog(mActivity).builder().setCancelable(false)
                        .setCanceledOnTouchOutside(false).show();
//                Intent intent = new Intent(mActivity, StudyActivityManager.class);
//                intent.putExtra(BackFragmentActivity.TAG_FRAGMENT, AddPlanFragment.TAG);
//                mActivity.startActivity(intent);
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

        recyclerview.addItemDecoration(new ItemButtomDecoration(mActivity, 1));
        recyclerview.setAdapter(adapter);

        getData(0);
    }

    private int currPage;

    //获取数据
    private void getData(final int page) {
        currPage = page + 1;
        CusUser cusUser = BmobUser.getCurrentUser(CusUser.class);
        if ( cusUser != null ) {
            BmobQuery<PlanModel> query = new BmobQuery<PlanModel>();
            query.addWhereEqualTo("userId", cusUser.getObjectId());
            query.setLimit(10);
            query.setSkip(page * 10);
            query.order("currFlag,-createdAt");
            query.findObjects(new FindListener<PlanModel>() {
                @Override
                public void done(List<PlanModel> object, BmobException e) {
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
                }
            });
        }
    }

    class ListAdapter extends BaseQuickAdapter<PlanModel, BaseViewHolder> {

        public ListAdapter(List list) {
            super(R.layout.item_recyclerview_plan, list);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final PlanModel item) {
            helper.setText(R.id.tv_title, item.getTitle());
            TextView tv_flag = helper.getView(R.id.tv_flag);
            String flag = "";
            ////当前状态 0 进行中 1 已超时 2 已完成
            switch ( item.getCurrFlag() ) {
                case 0:
                    flag = "进行中...";
                    tv_flag.setTextColor(Color.parseColor("#2aa515"));
                    break;
                case 1:
                    flag = "已超时...";
                    tv_flag.setTextColor(Color.parseColor("#d81e06"));
                    break;
                case 2:
                    flag = "已完成...";
                    tv_flag.setTextColor(Color.parseColor("#9b9b9b"));
                    break;
            }
            helper.setText(R.id.tv_time, StringUtils.formatTimeStr(item.getStartTimeValue(), "yy/MM/dd HH:mm"));
            helper.setText(R.id.tv_flag, flag);

        }
    }
}
