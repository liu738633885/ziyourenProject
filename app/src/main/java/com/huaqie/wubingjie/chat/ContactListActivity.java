package com.huaqie.wubingjie.chat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.hyphenate.chatuidemo.ui.ContactListFragment;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;


public class ContactListActivity extends BaseActivity {
    private List<Fragment> fragment_list = new ArrayList<Fragment>();
    private ContactListFragment contactListFragment;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_contact_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        contactListFragment = new ContactListFragment();
//        contactListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, contactListFragment).commit();
    }

    public void back(View view){
        finish();
    }
}
