package com.cretin.studyhelper.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.cetin.studyhelper.R;

/**
 * Created by cretin on 16/3/4.
 */
public class SelectPopupWindow extends PopupWindow implements View.OnClickListener {
    public static final int TYPE_INPUT_COMMENT = 1;
    public static final int TYPE_DELETE_UPDATE = 2;
    public static final int TYPE_SHARE = 3;
    private View mMenuView;
    private OnPopWindowClickListener listener;

    public SelectPopupWindow(Activity context, int type) {
        initView(context, null, type);
    }

    public SelectPopupWindow(Activity context, OnPopWindowClickListener listener, int type) {
        initView(context, listener, type);
    }

    private void initView(Activity context, OnPopWindowClickListener listener, int type) {
        this.listener = listener;
        //设置按钮监听
        if ( type == TYPE_INPUT_COMMENT ) {
            initViewInputComment(context);
        } else {
            throw new RuntimeException("type 类型有误");
        }

        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        if ( type == TYPE_INPUT_COMMENT )
            this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        this.setAnimationStyle(R.style.dialog_style);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        if ( type != TYPE_INPUT_COMMENT )
            //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
            mMenuView.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                    int y = ( int ) event.getY();
                    if ( event.getAction() == MotionEvent.ACTION_UP ) {
                        if ( y < height ) {
                            dismiss();
                        }
                    }
                    return true;
                }
            });
    }

    //评论输入对话框
    private void initViewInputComment(Activity context) {
        LayoutInflater inflater = ( LayoutInflater ) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.layout_input_dialog, null);
    }

    public View getmMenuView() {
        return mMenuView;
    }

    @Override
    public void onClick(View v) {
        if ( listener != null )
            listener.onPopWindowClickListener(v);
        dismiss();
    }

    public interface OnPopWindowClickListener {
        void onPopWindowClickListener(View view);
    }
}
