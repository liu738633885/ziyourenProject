package com.huaqie.wubingjie.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.activity.BaseActivity;
import com.huaqie.wubingjie.activity.LoginActivity;
import com.huaqie.wubingjie.model.event.EventRefresh;
import com.huaqie.wubingjie.order.MyServeTaskActivity;
import com.huaqie.wubingjie.utils.UserManager;
import com.huaqie.wubingjie.widgets.TitleBar;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.ui.AddContactActivity;
import com.hyphenate.chatuidemo.ui.ContactListFragment;
import com.hyphenate.chatuidemo.ui.ConversationListFragment;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends BaseActivity {
    private RadioGroup rg_bottom;
    private TitleBar titleBar;
    private List<Fragment> fragments=new ArrayList<>();
    private int fragment_now_num;
    private TextView unread_msg_number,unread_friend_number;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_message;
    }

    public static void goTo(Context context) {
        Intent intent;
        if (UserManager.isLogin()) {
            intent = new Intent(context, MessageActivity.class);
        } else {
            intent = new Intent(context, LoginActivity.class);
        }
        context.startActivity(intent);
    }
    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar= (TitleBar) findViewById(R.id.titlebar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessageActivity.this, AddContactActivity.class));
            }
        });
        fragments.add(new ConversationListFragment());
        fragments.add(new ContactListFragment());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragments.get(0)).commit();
        fragment_now_num=0;
        rg_bottom = (RadioGroup) findViewById(R.id.rg_bottom);
        rg_bottom.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_1:
                        titleBar.setCenterText("消息");
                        changeFragment(0);
                        break;
                    case R.id.rb_2:
                        titleBar.setCenterText("通讯录");
                        changeFragment(1);
                        break;
                    default:
                }
            }
        });
        unread_msg_number= (TextView) findViewById(R.id.unread_msg_number);
        unread_friend_number = (TextView) findViewById(R.id.unread_friend_number);
    }
    private void changeFragment(int num){
        if (num != fragment_now_num) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments.get(fragment_now_num));
            if (!fragments.get(num).isAdded()) {
                trx.add(R.id.fragment_container, fragments.get(num));
            }
            trx.show(fragments.get(num)).commit();
            fragment_now_num = num;
        }
    }
    public void updateUnreadLabel() {
        int count = DemoHelper.getInstance().getUnreadMsgCountTotal();
        int friendCount=DemoHelper.getInstance().getUnreadAddressCountTotal();
        if (count > 0) {
            unread_msg_number.setText(String.valueOf(count));
            unread_msg_number.setVisibility(View.VISIBLE);
        } else {
            unread_msg_number.setVisibility(View.INVISIBLE);
        }
        if (friendCount > 0) {
            unread_friend_number.setText(String.valueOf(friendCount));
            unread_friend_number.setVisibility(View.VISIBLE);
        } else {
            unread_friend_number.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateUnreadLabel();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Subscribe
    public void onEventMainThread(EventRefresh refresh) {
        if (refresh.getAction().equals(EventRefresh.ACTION_CHAT)) {
            runOnUiThread(new Runnable() {
                public void run() {
                    updateUnreadLabel();
                    if(fragment_now_num==0&&fragments.get(0)!=null){
                        ((ConversationListFragment) fragments.get(0)).refresh();
                    }else if (fragment_now_num==1&&fragments.get(1)!=null){
                        ((ContactListFragment) fragments.get(1)).refresh();
                    }
                }
            });
            return;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
