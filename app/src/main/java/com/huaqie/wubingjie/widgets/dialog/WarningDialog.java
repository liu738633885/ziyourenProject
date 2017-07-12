package com.huaqie.wubingjie.widgets.dialog;

import android.app.Dialog;
import android.content.Context;

import com.huaqie.wubingjie.R;

/**
 * Created by lewis on 2016/11/8.
 */

public class WarningDialog extends Dialog {

    public WarningDialog(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        setContentView(R.layout.mydialog_warning);
    }
}
