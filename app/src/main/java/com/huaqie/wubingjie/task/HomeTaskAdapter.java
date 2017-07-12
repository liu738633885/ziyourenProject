package com.huaqie.wubingjie.task;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.model.task.TaskItem;
import com.huaqie.wubingjie.utils.DensityUtil;
import com.huaqie.wubingjie.utils.ViewUtils;
import com.huaqie.wubingjie.utils.imageloader.ImageLoader;
import com.lewis.utils.DateUtils;

import java.util.ArrayList;

/**
 * Created by lewis on 2016/10/7.
 */

public class HomeTaskAdapter extends BaseQuickAdapter<TaskItem> {
    public HomeTaskAdapter() {
        super(R.layout.item_task, new ArrayList<TaskItem>());
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, final TaskItem taskItem) {
        ImageView imv_tag = baseViewHolder.getView(R.id.imv_tag);
        if (taskItem.getType().equals("1")) {
            imv_tag.setImageResource(R.mipmap.icon_tag01);
        } else {
            imv_tag.setImageResource(R.mipmap.icon_tag02);
        }
        ImageView imv_avatar = baseViewHolder.getView(R.id.imv_avatar);
        ViewUtils.setAvatar(mContext, taskItem.getMember_avatar(), imv_avatar, taskItem.getUid());
        baseViewHolder.setText(R.id.tv_nickname, taskItem.getMember_nickname());
        String isstudent = "";
        if (taskItem.getMember_is_student().equals("1")) {
            isstudent = "在校生";
        } else if (taskItem.getMember_is_student().equals("2")) {
            isstudent = "毕业";
        } else {
            isstudent = "其他";
        }
        baseViewHolder.setText(R.id.tv_school_isstudent, taskItem.getMember_school() + "   " + isstudent);
        baseViewHolder.setText(R.id.tv_title, taskItem.getTitle());
        baseViewHolder.setText(R.id.tv_content, taskItem.getContent());
        baseViewHolder.setText(R.id.tv_price, taskItem.getDoublePrice() > 0 ? "￥  " + taskItem.getPrice() + "元" : "免费");
        baseViewHolder.setText(R.id.tv_address, taskItem.getAddress());
        baseViewHolder.setText(R.id.tv_distance, taskItem.getDistance());
        baseViewHolder.setText(R.id.tv_create_time, DateUtils.translateDate2(taskItem.getLongCreate_time(), System.currentTimeMillis() / 1000));
        //渲染图片
        if (taskItem.getPic_list().size() > 0) {
            baseViewHolder.setVisible(R.id.layout_pic, true);
            LinearLayout layout_pic = baseViewHolder.getView(R.id.layout_pic);
            layout_pic.removeAllViews();
            for (int i = 0; i < taskItem.getPic_list().size(); i++) {
                ImageView imvpic = new ImageView(mContext);
                int width = DensityUtil.dip2px(mContext, 50);
                LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(width, width);
                ll.setMargins(0, 0, DensityUtil.dip2px(mContext, 8), 0);
                imvpic.setLayoutParams(ll);
                imvpic.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imvpic.setAdjustViewBounds(true);
                imvpic.setBackgroundResource(R.color.loading);
                layout_pic.addView(imvpic);
                ImageLoader.load(mContext, taskItem.getPic_list().get(i), false, 1, 0, 0, imvpic);
            }
        } else {
            baseViewHolder.setVisible(R.id.layout_pic, false);
        }

        if (taskItem.getIntPeople_num() > 1) {
            baseViewHolder.setText(R.id.tv_people_type, "多人");
        } else {
            baseViewHolder.setText(R.id.tv_people_type, "单人");
        }
        if (taskItem.getIntGrab_num() > 0) {
            baseViewHolder.setText(R.id.tv_grab, "被抢" +(taskItem.getIntPeople_num()>1? taskItem.getIntGrab_num() + "/" + taskItem.getIntPeople_num():""));
        } else {
            baseViewHolder.setText(R.id.tv_grab, "新发布");
        }

        baseViewHolder.setText(R.id.tv_classname, taskItem.getClass_name());
        baseViewHolder.setVisible(R.id.tv_classname, !TextUtils.isEmpty(taskItem.getClass_name()));
        TextView tv_member_sex = baseViewHolder.getView(R.id.tv_member_sex);
        if (taskItem.getMember_sex().equals("1")) {
            tv_member_sex.setText("男生");
            tv_member_sex.setTextColor(ContextCompat.getColor(mContext, R.color.super_blue));
            tv_member_sex.setBackgroundResource(R.drawable.shape_bg_circular_only_stroke_blue);
        } else if (taskItem.getMember_sex().equals("2")) {
            tv_member_sex.setText("女生");
            tv_member_sex.setTextColor(ContextCompat.getColor(mContext, R.color.super_pink));
            tv_member_sex.setBackgroundResource(R.drawable.shape_bg_circular_only_stroke_pink);
        } else {
            tv_member_sex.setText("男/女");
            tv_member_sex.setTextColor(ContextCompat.getColor(mContext, R.color.super_green));
            tv_member_sex.setBackgroundResource(R.drawable.shape_bg_circular_only_stroke_green);
        }
        /*CardView cardView = baseViewHolder.getView(R.id.cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServeTaskDetailActivity2.goTo(mContext, taskItem.getServe_task_id(), taskItem.getType());
            }
        });*/
        baseViewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServeTaskDetailActivity2.goTo(mContext, taskItem.getServe_task_id(), taskItem.getType());
            }
        });
    }
}
