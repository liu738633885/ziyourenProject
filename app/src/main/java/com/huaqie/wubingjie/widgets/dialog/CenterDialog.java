package com.huaqie.wubingjie.widgets.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.huaqie.wubingjie.R;


/**
 * Created by lewis on 16/6/29.
 */
public class CenterDialog extends Dialog {
    private Context context;

    public CenterDialog(Context context) {
        this(context, R.style.MyDialogStyleBottom);
    }

    public CenterDialog(Context context, int themeResId) {
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
        dialogWindow.setGravity(Gravity.CENTER);
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
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

    public void setText1(String text) {
        if (findViewById(R.id.tv) != null && !TextUtils.isEmpty(text)) {
            ((TextView) findViewById(R.id.tv)).setText(text);
        }
    }

    public void myShow() {
        if (this != null && !isShowing()) {
            show();
        }
    }
    public interface OnPositiveListener {
        void onClick(CenterDialog dialog);
    }
    private OnPositiveListener mOnPositiveListener;
    public CenterDialog setPositiveListener(OnPositiveListener l) {
        this.mOnPositiveListener = l;
        return this;
    }
}
