package com.huaqie.wubingjie.mine;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.model.user.AccountItem;
import com.huaqie.wubingjie.model.user.AccountItemChild;
import com.lewis.utils.DateUtils;

import java.util.ArrayList;

/**
 * Created by lewis on 2016/10/29.
 */

public class AccountAdapter extends BaseQuickAdapter<AccountItem> {
    public AccountAdapter() {
        super(R.layout.item_account, new ArrayList<AccountItem>());
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, AccountItem accountItem) {
        if (!TextUtils.isEmpty(accountItem.getDate())){
            baseViewHolder.setText(R.id.tv_date, accountItem.getDate() + " ( 支出:" + accountItem.getPay_total_money() + "元, 收入:" + accountItem.getIncome_total_money() + "元 )");
        }
        if (accountItem.getArr()!=null&&accountItem.getArr().size()>0){
            LinearLayout ll_content=baseViewHolder.getView(R.id.ll_content);
            ll_content.removeAllViews();
            for (AccountItemChild child:accountItem.getArr()){
                View childView= LayoutInflater.from(mContext).inflate(R.layout.item_account_child,ll_content,false);
                ll_content.addView(childView);
                initChildView(childView, child);
            }
        }

    }
    private void initChildView(View childView,AccountItemChild child){
        TextView tv_type_money= (TextView) childView.findViewById(R.id.tv_type_money);
        TextView tv_time = (TextView) childView.findViewById(R.id.tv_time);
        TextView tv_account_content = (TextView) childView.findViewById(R.id.tv_account_content);
        TextView tv_pay_type = (TextView) childView.findViewById(R.id.tv_pay_type);
        TextView tv_create_time = (TextView) childView.findViewById(R.id.tv_create_time);
        CheckBox cb_up_down = (CheckBox) childView.findViewById(R.id.cb_up_down);
        final LinearLayout ll_child_content = (LinearLayout) childView.findViewById(R.id.ll_child_content);
        if(!TextUtils.isEmpty(child.getAccount_type())&&!TextUtils.isEmpty(child.getMoney())){
            String text="";
            switch (child.getAccount_type()){
                case "1":
                    text = "收入:  ";
                    break;
                case "2":
                    text = "支出:  ";
                    break;
                case "3":
                    text = "退款:  ";
                    break;
                case "4":
                    text = "提现:  ";
                    break;
            }
            text+=child.getMoney();
            text+="元";
            tv_type_money.setText(text);
        }
        if(!TextUtils.isEmpty(child.getPay_type())){
            switch (child.getPay_type()){
                case "1":
                    tv_pay_type.setText("钱包");
                    break;
                case "2":
                    tv_pay_type.setText("支付宝");
                    break;
                case "3":
                    tv_pay_type.setText("微信");
                    break;
                case "4":
                    tv_pay_type.setText("取消任务");
                    break;
                case "5":
                    tv_pay_type.setText("钱包提现");
                    break;
            }
        }
        if (!TextUtils.isEmpty(child.getAccount_content())) {
            tv_account_content.setText(child.getAccount_content());
        }
        if(ll_child_content.getVisibility()==View.VISIBLE){
            cb_up_down.setChecked(true);
        }else {
            cb_up_down.setChecked(false);
        }
        cb_up_down.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    ll_child_content.setVisibility(View.VISIBLE);
                }else {
                    ll_child_content.setVisibility(View.GONE);
                }
            }
        });
        if(!TextUtils.isEmpty(child.getCreate_time())){
            try {
                tv_time.setText(DateUtils.stringToString(child.getCreate_time(),DateUtils.yyyyMMddHHmmss));
                tv_create_time.setText(DateUtils.stringToString(child.getCreate_time(), DateUtils.yyyyMMddHHmmss));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
