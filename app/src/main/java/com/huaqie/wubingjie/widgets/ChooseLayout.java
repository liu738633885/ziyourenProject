package com.huaqie.wubingjie.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaqie.wubingjie.R;


/**
 * Created by lewis on 16/8/3.
 */
public class ChooseLayout extends FrameLayout {
    private int mode;
    public static final int MODE_TEXT_IMV = 0;
    public static final int MODE_TEXT_NULL = 1;
    public static final int MODE_EDT_NULL = 4;
    private TextView tv1, tv2;
    private EditText edt;
    private View bottomline;
    private String text1;
    private String text2;
    private String edt_text;
    private String edt_hint;
    private int tv_color;
    private int bottomline_color;
    private int tv_size;
    private int edt_color;
    private int edt_size;
    private int edt_hintcolor;
    private int edt_hintsize;
    private ImageView imv_next;

    public ChooseLayout(Context context) {
        this(context, null, 0);
    }

    public ChooseLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChooseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.choose_layout, this, true);
        getAttr(attrs);
        updateView();
    }

    private void getAttr(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.ChooseLayout);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.ChooseLayout_ChooseLayout_tv_text:
                    text1 = a.getString(attr);
                    break;
                case R.styleable.ChooseLayout_ChooseLayout_mode:
                    mode = a.getInteger(attr, 0);
                    break;
                case R.styleable.ChooseLayout_ChooseLayout_textSize:
                    tv_size = a.getDimensionPixelSize(attr, 14);
                    break;
                case R.styleable.ChooseLayout_ChooseLayout_tv_textColor:
                    tv_color = a.getInteger(attr, 0);
                    break;
                case R.styleable.ChooseLayout_ChooseLayout_tv_text2:
                    text2 = a.getString(attr);
                    break;
                case R.styleable.ChooseLayout_ChooseLayout_edt_hint:
                    edt_hint = a.getString(attr);
                    break;
                case R.styleable.ChooseLayout_ChooseLayout_bottomlineColor:
                    bottomline_color = a.getInteger(attr, 0);
                    break;
                default:
                    break;
            }
        }
        a.recycle();
    }

    private void updateView() {
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        edt = (EditText) findViewById(R.id.edt);
        imv_next = (ImageView) findViewById(R.id.imv_next);
        bottomline = findViewById(R.id.bottomline);
        if (!TextUtils.isEmpty(text1)) {
            tv1.setText(text1);
        }
        if (!TextUtils.isEmpty(text2)) {
            tv2.setText(text2);
        }
        if (!TextUtils.isEmpty(edt_hint)) {
            edt.setHint(edt_hint);
        }
        switch (mode) {
            case MODE_TEXT_NULL:
                tv2.setVisibility(View.VISIBLE);
                imv_next.setVisibility(View.GONE);
                break;
            case MODE_TEXT_IMV:
                tv2.setVisibility(View.VISIBLE);
                imv_next.setVisibility(View.VISIBLE);
                break;
            case MODE_EDT_NULL:
                tv2.setVisibility(View.GONE);
                imv_next.setVisibility(View.GONE);
                edt.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        if (tv_color != 0) {
            tv1.setTextColor(tv_color);
        }
        if (bottomline_color != 0) {
            bottomline.setBackgroundColor(bottomline_color);
        }
    }

    public void setText(String string) {
        if (tv1 != null && tv1.getVisibility() == View.VISIBLE) {
            tv1.setText(string);
        }
    }

    public void setText2(String string) {
        if (TextUtils.isEmpty(string)) {
            return;
        }
        if (tv2 != null && tv2.getVisibility() == View.VISIBLE) {
            tv2.setText(string);
        }
    }

    public String getText2() {
        if (tv2 != null && tv2.getVisibility() == View.VISIBLE) {
            return tv2.getText().toString();
        }
        return "";
    }

    public String getText() {
        if (tv1 != null && tv1.getVisibility() == View.VISIBLE) {
            return tv1.getText().toString();
        }
        return "";
    }

    public EditText getEdt() {
        return edt;
    }
}
