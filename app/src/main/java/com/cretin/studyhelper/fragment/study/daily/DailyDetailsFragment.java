package com.cretin.studyhelper.fragment.study.daily;


import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cetin.studyhelper.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cretin.studyhelper.app.LocalStorageKeys;
import com.cretin.studyhelper.base.BaseFragment;
import com.cretin.studyhelper.model.CommentModel;
import com.cretin.studyhelper.model.CusUser;
import com.cretin.studyhelper.model.DailyModel;
import com.cretin.studyhelper.utils.KV;
import com.cretin.studyhelper.utils.UiUtils;
import com.cretin.studyhelper.view.CircleImageView;
import com.cretin.studyhelper.view.ItemButtomDecoration;
import com.cretin.studyhelper.view.SelectPopupWindow;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class DailyDetailsFragment extends BaseFragment {
    public static final String TAG = "DailyDetailsFragment";
    @Bind( R.id.recyclerview )
    RecyclerView recyclerview;
    @Bind( R.id.swipe_refresh )
    SwipeRefreshLayout swipeRefresh;
    @Bind( R.id.ll_pinglin )
    LinearLayout llPinglin;
    private CircleImageView ivAvatar;
    private TextView tvName;
    private TextView tvTime;
    private TextView tvContent;
    private List<CommentModel> list;

    private ListAdapter adapter;
    private int currPage;

    private String id;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_daily_details;
    }

    public static DailyDetailsFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString("id", id);
        DailyDetailsFragment fragment = new DailyDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(View contentView, Bundle savedInstanceState) {
        setMainTitle("日报详情");
        id = getArguments().getString("id");

        initAlterDialog();
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
        initHeaderView();
        recyclerview.addItemDecoration(new ItemButtomDecoration(mActivity, 2));
        recyclerview.setAdapter(adapter);
        adapter.loadMoreEnd();
        adapter.notifyDataSetChanged();

        showDialog("加载中...");
        getData(0);
    }

    private void initHeaderView() {
        View view = View.inflate(mActivity, R.layout.header_daily_details, null);
        ivAvatar = ( CircleImageView ) view.findViewById(R.id.iv_avatar);
        tvContent = ( TextView ) view.findViewById(R.id.tv_content);
        tvTime = ( TextView ) view.findViewById(R.id.tv_time);
        tvName = ( TextView ) view.findViewById(R.id.tv_name);

        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtils.showToastInAnyThread("点我头像干啥");
            }
        });
        adapter.setHeaderView(view);
    }

    //获取数据
    private void getData(final int page) {
        getHeaderData();

        currPage = page + 1;
        BmobQuery<CommentModel> query = new BmobQuery<CommentModel>();
        query.addWhereEqualTo("id", id);
        query.setLimit(10);
        query.setSkip(page * 10);
        query.include("cusUser");
        query.order("-createdAt");
        query.findObjects(new FindListener<CommentModel>() {
            @Override
            public void done(List<CommentModel> object, BmobException e) {
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

    //获取头部数据
    private void getHeaderData() {
        BmobQuery<DailyModel> query = new BmobQuery<DailyModel>();
        query.include("cusUser");
        query.getObject(id, new QueryListener<DailyModel>() {

            @Override
            public void done(DailyModel item, BmobException e) {
                if ( e == null ) {
                    //显示数据
                    CusUser cusUser = item.getCusUser();
                    if ( cusUser != null ) {
                        String nick = cusUser.getNickname();
                        if ( TextUtils.isEmpty(nick) ) {
                            nick = cusUser.getUsername();
                        }
                        tvName.setText(nick);
                        if ( !TextUtils.isEmpty(cusUser.getAvatar()) ) {
                            Glide.with(mActivity).load(cusUser.getAvatar()).into(
                                    ivAvatar
                            );
                        } else {
                            ivAvatar.setImageResource(R.mipmap.avatar);
                        }
                    } else {
                        tvName.setText("未设置昵称");
                        ivAvatar.setImageResource(R.mipmap.avatar);
                    }
                    tvTime.setText(item.getCreatedAt());
                    String remark = "无备注";
                    if ( !TextUtils.isEmpty(item.getRemark()) ) {
                        remark = item.getRemark();
                    }
                    //拼接数据
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("学习日期：\n" + item.getDate() + "\n");
                    stringBuffer.append("学习科目：\n" + item.getSubject() + "\n");
                    stringBuffer.append("今日完成任务：\n" + item.getFinish() + "\n");
                    stringBuffer.append("未完成任务：\n" + item.getUnfinish() + "\n");
                    stringBuffer.append("学习时长：\n" + item.getTime() + "小时\n");
                    stringBuffer.append("备注：\n" + remark);
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
                    tvContent.setText(spannableString);
                } else {
                    UiUtils.showToastInAnyThread("日报详情获取失败");
                }
            }
        });
    }

    @OnClick( R.id.ll_pinglin )
    public void onViewClicked() {
        showAlertDialog();
    }

    //显示对话框
    private void showAlertDialog() {
        if ( menuWindow == null )
            menuWindow = new SelectPopupWindow(getActivity(), null, SelectPopupWindow.TYPE_INPUT_COMMENT);
        Rect rect = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int winHeight = getActivity().getWindow().getDecorView().getHeight();
        menuWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.BOTTOM, 0, winHeight - rect.bottom);
    }

    class ListAdapter extends BaseQuickAdapter<CommentModel, BaseViewHolder> {
        public ListAdapter(List list) {
            super(R.layout.item_recycler_comment, list);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final CommentModel item) {
            //头像
            if ( item.getCusUser() != null ) {
                CusUser cusUser = item.getCusUser();
                if ( !TextUtils.isEmpty(cusUser.getAvatar()) ) {
                    Glide.with(mActivity).load(cusUser.getAvatar()).into(( ImageView ) helper.getView(R.id.iv_avatar));
                } else {
                    (( ImageView ) helper.getView(R.id.iv_avatar)).setImageResource(R.mipmap.avatar);
                }

                //名字
                String name = cusUser.getNickname();
                if ( TextUtils.isEmpty(name) ) {
                    name = cusUser.getMobilePhoneNumber();
                }
                helper.setText(R.id.user_name, name);
            }

            //内容
            helper.setText(R.id.tv_content, item.getContent());
            //时间
            helper.setText(R.id.tv_time, item.getCreatedAt());
        }
    }

    private SelectPopupWindow menuWindow;

    private void initAlterDialog() {
        if ( menuWindow == null )
            menuWindow = new SelectPopupWindow(getActivity(), new SelectPopupWindow.OnPopWindowClickListener() {
                @Override
                public void onPopWindowClickListener(View view) {

                }
            }, SelectPopupWindow.TYPE_INPUT_COMMENT);
        View mMenuView = menuWindow.getmMenuView();
        ImageView iv_close = ( ImageView ) mMenuView.findViewById(R.id.iv_close);
        TextView tv_send = ( TextView ) mMenuView.findViewById(R.id.tv_send);
        final EditText ed_content = ( EditText ) mMenuView.findViewById(R.id.ed_content);
        final CheckBox cb_niming = ( CheckBox ) mMenuView.findViewById(R.id.cb_niming);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuWindow.dismiss();
            }
        });
        tv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = ed_content.getText().toString().trim();
                if ( TextUtils.isEmpty(content) ) {
                    UiUtils.showToastInAnyThread("评论内容不能为空");
                    return;
                }
                addComment(ed_content, cb_niming, content, cb_niming.isChecked());
            }
        });
    }

    //添加评论
    private void addComment(final EditText ed_content, CheckBox cb_niming, String content, boolean checked) {
        CusUser cusUser = KV.get(LocalStorageKeys.USER_INFO);
        if ( cusUser != null ) {
            showDialog("正在评论...");

            final CommentModel commentModel = new CommentModel();
            commentModel.setCusUser(cusUser);
            commentModel.setContent(content);
            commentModel.setId(id);
            commentModel.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if ( e == null ) {
                        //提交成功
                        ed_content.setText("");
                        if ( menuWindow != null )
                            menuWindow.dismiss();
                        UiUtils.showToastInAnyThread();
                        list.add(0, commentModel);
                        adapter.notifyDataSetChanged();
                    } else {
                        UiUtils.showToastInAnyThreadFail();
                    }
                    stopDialog();
                }
            });

        }


    }
}
