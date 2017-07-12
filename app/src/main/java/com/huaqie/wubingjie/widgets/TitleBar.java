package com.huaqie.wubingjie.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaqie.wubingjie.R;


/**
 * Created by lewis on 16/7/7.
 */
public class TitleBar extends FrameLayout {
    public static final int MODE_CUSTOM = -1;
    public static final int MODE_ONLY_BACK = 0;
    public static final int MODE_BACK_IMV = 1;
    public static final int MODE_BACK_TV = 2;
    public static final int MODE_CENTER_RIGHTTV = 3;
    public static final int MODE_CENTER_RIGHTIMV = 4;
    private int titleMode;
    private String centerText;
    private String left_text;
    private String right_text;
    private Drawable left_img;
    private Drawable right_img;
    private ViewGroup left, right;
    private ImageView left_imv, right_imv;
    private TextView left_tv, right_tv, center_tv;

    public TitleBar(Context context) {
        this(context, null, 0);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.titlebar, this, true);

        getAttr(attrs);
        updateView();
    }

    private void getAttr(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.TitleBar);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.TitleBar_mode:
                    titleMode = a.getInteger(attr, 0);
                    break;
                default:
                    break;
            }
        }

        initData();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.TitleBar_left_text:
                    if (!TextUtils.isEmpty(a.getString(attr))) {
                        left_text = a.getString(attr);
                    }
                    break;
                case R.styleable.TitleBar_left_img:
                    left_img = a.getDrawable(attr);
                    break;
                case R.styleable.TitleBar_right_text:
                    if (!TextUtils.isEmpty(a.getString(attr))) {
                        right_text = a.getString(attr);
                    }
                    break;
                case R.styleable.TitleBar_right_img:
                    right_img = a.getDrawable(attr);
                    break;
                case R.styleable.TitleBar_center_text:
                    if (!TextUtils.isEmpty(a.getString(attr))) {
                        centerText = a.getString(attr);
                    }
                    break;
                default:
                    break;
            }
        }
        a.recycle();
    }

    private void initData() {
        left_img = ContextCompat.getDrawable(getContext(), R.drawable.icon_back);
        switch (titleMode) {
            case MODE_ONLY_BACK:
                break;
            case MODE_BACK_IMV:
                break;
            case MODE_BACK_TV:
                break;
            case MODE_CENTER_RIGHTTV:
                break;
            case MODE_CENTER_RIGHTIMV:
                break;
            default:
                break;
        }
    }

    private void updateView() {
        left = (ViewGroup) findViewById(R.id.left);
        right = (ViewGroup) findViewById(R.id.right);
        left_imv = (ImageView) findViewById(R.id.left_imv);
        right_imv = (ImageView) findViewById(R.id.right_imv);
        left_tv = (TextView) findViewById(R.id.left_tv);
        right_tv = (TextView) findViewById(R.id.right_tv);
        center_tv = (TextView) findViewById(R.id.center_tv);
        switch (titleMode) {
            case MODE_ONLY_BACK:
                setViewsVisible(false, true, false, false);
                break;
            case MODE_BACK_IMV:
                setViewsVisible(false, true, false, true);
                break;
            case MODE_BACK_TV:
                setViewsVisible(false, true, true, false);
                break;
            case MODE_CENTER_RIGHTTV:
                setViewsVisible(false, false, true, false);
                break;
            case MODE_CENTER_RIGHTIMV:
                setViewsVisible(false, false, false, true);
                break;
            default:
                break;
        }
    }

    public void setMode(int mode) {
        this.titleMode = mode;
        updateView();
    }

    public ViewGroup getLeftGroup() {
        return left;
    }

    public ViewGroup getRightGroup() {
        return right;
    }

    public ImageView getLeft_imv() {
        return left_imv;
    }

    public ImageView getRight_imv() {
        return right_imv;
    }

    public TextView getLeft_tv() {
        return left_tv;
    }

    public TextView getRight_tv() {
        return right_tv;
    }

    public TextView getCenter_tv() {
        return center_tv;
    }

    public void setViewsVisible(boolean left_tvshow, boolean left_imvshow, boolean right_tvshow, boolean right_imvshow) {
        left_tv.setVisibility(left_tvshow ? View.VISIBLE : View.GONE);
        left_imv.setVisibility(left_imvshow ? View.VISIBLE : View.GONE);
        right_tv.setVisibility(right_tvshow ? View.VISIBLE : View.GONE);
        right_imv.setVisibility(right_imvshow ? View.VISIBLE : View.GONE);
        if (!left_tvshow && !left_imvshow) {
            getLeftGroup().setEnabled(false);
        } else {
            getLeftGroup().setEnabled(true);
        }
        if (!right_tvshow && !right_imvshow) {
            getRightGroup().setEnabled(false);
        } else {
            getRightGroup().setEnabled(true);
        }
        setTvText(left_tv, left_text);
        setTvText(right_tv, right_text);
        setTvText(center_tv, centerText);
        setImvDrawable(left_imv, left_img);
        setImvDrawable(right_imv, right_img);
        setDefaultLeft();
    }

    private void setTvText(TextView tv, String text) {
        if (tv.getVisibility() == View.GONE) {
            return;
        }
        if (TextUtils.isEmpty(text)) {
            return;
        }
        tv.setText(text);
    }

    private void setImvDrawable(ImageView imv, Drawable drawable) {
        if (imv.getVisibility() == View.GONE) {
            return;
        }
        if (drawable == null) {
            return;
        }
        imv.setImageDrawable(drawable);
    }

    private LeftClike leftClike;

    public interface LeftClike extends OnClickListener {
        void onClick(View view);
    }

    public void setLeftClike(LeftClike leftClike) {
        this.leftClike = leftClike;
        setDefaultLeft();
    }

    public void setCenterText(String text) {
        if (center_tv != null) {
            center_tv.setText(text);
        }
    }

    public void setLeftText(String text) {
        if (left_tv != null) {
            left_tv.setText(text);
        }
    }

    public void setRightText(String text) {
        if (right_tv != null) {
            right_tv.setText(text);
        }
    }

    private void setDefaultLeft() {
        if (leftClike != null) {
            left.setOnClickListener(leftClike);
        } else {
            if (left_imv != null && left_img != null && left_imv.getVisibility() == View.VISIBLE) {
                try {
                    final Activity activity = (Activity) getContext();
                    left.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            activity.finish();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    //说明是ApplicationContext
                }
            }
        }
    }
}
