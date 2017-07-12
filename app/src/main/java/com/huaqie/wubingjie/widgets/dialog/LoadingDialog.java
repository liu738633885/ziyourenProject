/*
 * Copyright 2015 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huaqie.wubingjie.widgets.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.utils.DensityUtil;


/**
 * Created in Oct 23, 2015 1:19:04 PM.
 *
 * @author Yan Zhenjie.
 */
public class LoadingDialog extends Dialog {
    private Context context;

    public LoadingDialog(Context context) {
        super(context, R.style.LoadingDialog);
        this.context = context;
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_loading);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.pb);
    }

    public void setFull() {
        findViewById(R.id.tv).setVisibility(View.VISIBLE);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = DensityUtil.getHeight(context) - DensityUtil.getStatusBarHeight(context);
        lp.dimAmount = 0.0f;
        dialogWindow.setAttributes(lp);
        getWindow().setBackgroundDrawableResource(R.color.white);
    }

    public void myShow() {
        if (!isShowing()) {
            show();
        }
    }

    public void myDismiss() {
        if (isShowing()) {
            dismiss();
        }
    }

}
