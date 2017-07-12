package com.huaqie.wubingjie.widgets.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.huaqie.wubingjie.R;


/**
 * Created by lewis on 16/6/29.
 */
public class BottomDialog extends Dialog {
    private Context context;

    public BottomDialog(Context context) {
        this(context, R.style.MyDialogStyleBottom);
    }

    public BottomDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }
    public void setContentView(int layoutResID) {
        setContentView(layoutResID,false);
    }

    public void setContentView(int layoutResID, boolean isfull) {
        super.setContentView(layoutResID);
        initView(isfull);
        if (findViewById(R.id.cancel) != null) {
            findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDismiss();
                }
            });
        }
    }

    private void initView(boolean isfull) {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        if (isfull) {
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        } else {
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        // lp.width = DensityUtil.getWidth(context);
        dialogWindow.setAttributes(lp);
    }

    public void myDismiss() {
        if (this != null && isShowing()) {
            dismiss();
        }
    }

    public void myShow() {
        if (this != null && !isShowing()) {
            show();
        }
    }
}
