package com.cretin.studyhelper.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cetin.studyhelper.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by masel on 2016/10/10.
 */

public class PopupMenuDialog {
    Button mBtnWifiSettings;
    @Bind( R.id.popup_menu_title )
    TextView popupMenuTitle;
    @Bind( R.id.shared_wifi_state )
    ImageView sharedWifiState;
    @Bind( R.id.shared_wifi_state_hint )
    TextView sharedWifiStateHint;
    @Bind( R.id.shared_wifi_address )
    TextView sharedWifiAddress;
    @Bind( R.id.shared_wifi_settings )
    Button sharedWifiSettings;
    private Context context;
    private Dialog dialog;
    private Display display;

    public PopupMenuDialog(Context context) {
        this.context = context;
        WindowManager windowManager = ( WindowManager ) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public PopupMenuDialog builder(String msg) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.layout_timeup, null);

        view.setMinimumWidth(display.getWidth());

        dialog = new Dialog(context, R.style.PopupMenuDialogStyle);
        dialog.setContentView(view);
        ButterKnife.bind(this, dialog);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        sharedWifiStateHint.setText(msg.split("-")[0]);
        sharedWifiAddress.setText(msg.split("-")[1]);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);

        return this;
    }

    public void show() {
        dialog.show();
    }

    public PopupMenuDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public PopupMenuDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    @OnClick( {R.id.shared_wifi_settings} )
    public void onClick(View view) {
        switch ( view.getId() ) {
            case R.id.shared_wifi_settings:
                dialog.dismiss();
                break;
        }
    }
}
