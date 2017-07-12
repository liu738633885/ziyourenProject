package com.huaqie.wubingjie.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.utils.DensityUtil;
import com.orhanobut.logger.Logger;


/**
 * 朋友圈点赞评论的popupwindow
 * 
 * @author wei.yi
 * 
 */
public class MorePopupWindow extends PopupWindow {

	private ImageView btn_cancel,btn_tousu;
	// 实例化一个矩形
	private Rect mRect = new Rect();
	// 坐标的位置（x、y）
	private final int[] mLocation = new int[2];
	private Context context;

	public MorePopupWindow(Context context) {
		this.context=context;
		View view = LayoutInflater.from(context).inflate(R.layout.more_pop_upwindow, null);
		btn_cancel= (ImageView) view.findViewById(R.id.btn_cancel);
		btn_tousu = (ImageView) view.findViewById(R.id.btn_tousu);

		this.setContentView(view);
		this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
		this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		this.update();
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0000000000);
		// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
		this.setBackgroundDrawable(dw);
		this.setAnimationStyle(R.style.social_pop_anim);
	}

	public ImageView getBtn_cancel() {
		return btn_cancel;
	}

	public ImageView getBtn_tousu() {
		return btn_tousu;
	}

	public void showPopupWindow(View parent){
		parent.getLocationOnScreen(mLocation);
		Logger.e("---"+mLocation[0]+"---"+mLocation[1]);
		// 设置矩形的大小
		if(!this.isShowing()){
			showAtLocation(parent, Gravity.NO_GRAVITY, DensityUtil.getWidth(context)-DensityUtil.dip2px(context,61)
					, mLocation[1]+DensityUtil.dip2px(context,48));
		}else{
			dismiss();
		}
	}
}
