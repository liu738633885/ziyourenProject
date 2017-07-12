package com.huaqie.wubingjie.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lewis.widgets.MultiLineRadioGroup;
import com.orhanobut.logger.Logger;
import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.activity.BaseActivity;
import com.huaqie.wubingjie.model.event.EventRefresh;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.model.task.TaskClass;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class ChooseFilterActivity extends BaseActivity implements MultiLineRadioGroup.OnCheckedChangedListener {
    public static final String KEY_FILTER = "filter";
    private int[] filter = {0, 0, 0, 0};
    private MultiLineRadioGroup mrg_peoplenum, mrg_sex, mrg_class, mrg_type;
    private List<TaskClass> listClass;

    public static void goTo(Context context, String where, int[] filter) {
        Intent intent = new Intent(context, ChooseFilterActivity.class);
        intent.putExtra(KEY_FILTER, filter);
        intent.putExtra(KEY_WHERE, where);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        try {
            filter = intent.getIntArrayExtra(KEY_FILTER);
            where = intent.getStringExtra(KEY_WHERE);
        } catch (Exception e) {
            e.printStackTrace();
            filter = new int[]{0, 0, 0, 0};
        }

    }

    public void save(View v) {
        EventBus.getDefault().post(new EventRefresh(KEY_FILTER, filter, where));
        finish();
    }

    public void reset(View v) {
        filter = new int[]{0, 0, 0, 0};
        updataUi();
    }

    private void updataUi() {
        try {
            if (filter == null) {
                filter = new int[]{0, 0, 0, 0};
            }
            mrg_peoplenum.setItemChecked(filter[0]);
            mrg_sex.setItemChecked(filter[1]);
            mrg_class.setItemChecked(filter[2]);
            mrg_type.setItemChecked(filter[3]);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_choose_filter;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        mrg_peoplenum = (MultiLineRadioGroup) findViewById(R.id.mrg_peoplenum);
        mrg_sex = (MultiLineRadioGroup) findViewById(R.id.mrg_sex);
        mrg_class = (MultiLineRadioGroup) findViewById(R.id.mrg_class);
        mrg_type = (MultiLineRadioGroup) findViewById(R.id.mrg_type);
        mrg_peoplenum.setOnCheckChangedListener(this);
        mrg_sex.setOnCheckChangedListener(this);
        mrg_class.setOnCheckChangedListener(this);
        mrg_type.setOnCheckChangedListener(this);
        callClass();
    }

    private void callClass() {
        NetBaseRequest netBaseRequest = new NetBaseRequest(Constants.GET_CLASS);
        CallServer.getRequestInstance().add(this, 0x01, netBaseRequest, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    listClass = netBaseBean.parseList(TaskClass.class);
                    listClass.add(0, new TaskClass("0", "不限"));
                    //mrg_class.append("不限");
                    for (TaskClass tclass : listClass) {
                        mrg_class.append(tclass.getClass_name());
                    }
                    updataUi();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

    @Override
    public void onItemChecked(MultiLineRadioGroup group, int position, boolean checked) {
        Logger.d("id" + group.getId() + "--position" + position + "--checked" + checked);
        switch (group.getId()) {
            case R.id.mrg_peoplenum:
                filter[0] = position;
                break;
            case R.id.mrg_sex:
                filter[1] = position;
                break;
            case R.id.mrg_class:
                filter[2] = listClass.get(position).getIntClass_id();
                break;
            case R.id.mrg_type:
                filter[3] = position;
                break;
            default:
        }
    }
}
