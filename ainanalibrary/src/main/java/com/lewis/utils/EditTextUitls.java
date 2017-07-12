package com.lewis.utils;

import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by lewis on 2016/9/27.
 */

public class EditTextUitls {
    public static void bindLookPasswordToImageView(final EditText edt, final ImageView imv, final int lookId, final int nolookId) {
        edt.setInputType(0x81);
        imv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (edt.getInputType() == 0x81) {
                        edt.setInputType(0x90);
                        imv.setImageResource(nolookId);
                    } else {
                        edt.setInputType(0x81);
                        imv.setImageResource(lookId);
                    }
                    // 切换后将EditText光标置于末尾
                    CharSequence charSequence = edt.getText();
                    if (charSequence instanceof Spannable) {
                        Spannable spanText = (Spannable) charSequence;
                        Selection.setSelection(spanText,
                                charSequence.length());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public static void bindCleanToView(final EditText edt, final View v) {
        edt.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    v.setVisibility(View.VISIBLE);
                } else {
                    v.setVisibility(View.INVISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt.getText().clear();
            }
        });
    }
}
