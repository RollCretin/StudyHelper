package com.cretin.studyhelper.fragment.study.daily;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cetin.studyhelper.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cretin.studyhelper.app.LocalStorageKeys;
import com.cretin.studyhelper.base.BackFragmentActivity;
import com.cretin.studyhelper.base.BaseFragment;
import com.cretin.studyhelper.base.BaseFragmentActivity;
import com.cretin.studyhelper.fragment.me.CommonUserInfoFragment;
import com.cretin.studyhelper.model.CusUser;
import com.cretin.studyhelper.model.DailyModel;
import com.cretin.studyhelper.ui.manager.CommonBackActivityManager;
import com.cretin.studyhelper.ui.manager.MeActivityManager;
import com.cretin.studyhelper.ui.manager.StudyActivityManager;
import com.cretin.studyhelper.utils.KV;
import com.cretin.studyhelper.utils.UiUtils;
import com.cretin.studyhelper.view.ItemButtomDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class DailyFragment extends BaseFragment {
    public static final String TAG = "DailyFragment";
    @Bind( R.id.recyclerview )
    RecyclerView recyclerview;
    @Bind( R.id.swipe_refresh )
    SwipeRefreshLayout swipeRefresh;
    @Bind( R.id.fab )
    FloatingActionButton fab;
    private List<DailyModel> list;

    private ListAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_daily;
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
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(mActivity, StudyActivityManager.class);
                intent.putExtra(BackFragmentActivity.TAG_FRAGMENT, DailyDetailsFragment.TAG);
                Bundle bundle = new Bundle();
                bundle.putString("id", list.get(position).getObjectId());
                intent.putExtra(BaseFragmentActivity.ARGS, bundle);
                mActivity.startActivity(intent);
            }
        });
        adapter.setEmptyView(R.layout.empty_view);
        recyclerview.addItemDecoration(new ItemButtomDecoration(mActivity, 10));
        recyclerview.setAdapter(adapter);

        showDialog("加载中...");
        getData(0);
    }

    @OnClick( R.id.fab )
    public void onViewClicked() {
        Intent intent = new Intent(mActivity, CommonBackActivityManager.class);
        intent.putExtra(BackFragmentActivity.TAG_FRAGMENT, AddDailyFragment.TAG);
        Bundle bundle = new Bundle();
        bundle.putInt("type", AddDailyFragment.TYPE_ADD);
        bundle.putString("id", "");
        intent.putExtra(BaseFragmentActivity.ARGS, bundle);
        mActivity.startActivity(intent);
    }

    private int currPage;

    //获取数据
    private void getData(final int page) {
        currPage = page + 1;
        cusUser = KV.get(LocalStorageKeys.USER_INFO);
        if ( cusUser != null ) {
            BmobQuery<DailyModel> query = new BmobQuery<DailyModel>();
            query.addWhereEqualTo("userId", cusUser.getObjectId());
            query.setLimit(10);
            query.setSkip(page * 10);
            query.include("cusUser");
            query.order("-createdAt");
            query.findObjects(new FindListener<DailyModel>() {
                @Override
                public void done(List<DailyModel> object, BmobException e) {
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

    private CusUser cusUser = null;

    class ListAdapter extends BaseQuickAdapter<DailyModel, BaseViewHolder> {
        public ListAdapter(List list) {
            super(R.layout.item_recycler_daily, list);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final DailyModel item) {
            helper.setText(R.id.tv_time, item.getCreatedAt());
            final CusUser cusUser = item.getCusUser();
            if ( cusUser != null ) {
                String nick = cusUser.getNickname();
                if ( TextUtils.isEmpty(nick) ) {
                    nick = cusUser.getUsername();
                }
                helper.setText(R.id.tv_name, nick);

                if ( !TextUtils.isEmpty(cusUser.getAvatar()) ) {
                    Glide.with(mActivity).load(cusUser.getAvatar()).into(
                            ( ImageView ) helper.getView(R.id.iv_avatar)
                    );
                } else {
                    helper.setImageResource(R.id.iv_avatar, R.mipmap.avatar);
                }
                helper.getView(R.id.iv_avatar).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mActivity, MeActivityManager.class);
                        intent.putExtra(BackFragmentActivity.TAG_FRAGMENT, CommonUserInfoFragment.TAG);
                        Bundle bundle = new Bundle();
                        bundle.putString("userId", cusUser.getObjectId());
                        intent.putExtra(BaseFragmentActivity.ARGS, bundle);
                        mActivity.startActivity(intent);
                    }
                });
            } else {
                helper.setText(R.id.tv_name, "未设置昵称");
                helper.setImageResource(R.id.iv_avatar, R.mipmap.avatar);
            }

            String remark = "无备注";
            if ( !TextUtils.isEmpty(item.getRemark()) ) {
                remark = item.getRemark();
            }
            //拼接数据
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("学习日期：" + item.getDate() + "\n");
            stringBuffer.append("学习科目：" + item.getSubject() + "\n");
            stringBuffer.append("今日完成任务：" + item.getFinish() + "\n");
            stringBuffer.append("未完成任务：" + item.getUnfinish() + "\n");
            stringBuffer.append("学习时长：" + item.getTime() + "小时\n");
            stringBuffer.append("备注：" + remark);
            String result = stringBuffer.toString();
            SpannableString spannableString = new SpannableString(result);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#000000"))
                    , result.indexOf(item.getDate()), result.indexOf(item.getDate()) + item.getDate().length()
                    , Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#000000"))
                    , result.indexOf(item.getSubject()), result.indexOf(item.getSubject()) + item.getSubject().length()
                    , Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#000000"))
                    , result.indexOf(item.getFinish()), result.indexOf(item.getFinish()) + item.getFinish().length()
                    , Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#000000"))
                    , result.indexOf(item.getUnfinish()), result.indexOf(item.getUnfinish()) + item.getUnfinish().length()
                    , Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#000000"))
                    , result.indexOf(item.getTime() + "小时"), result.indexOf(item.getTime() + "小时") + (item.getTime() + "小时").length()
                    , Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#000000"))
                    , result.indexOf(remark), result.indexOf(remark) + remark.length()
                    , Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            helper.setText(R.id.tv_content, spannableString);
        }
    }
}
