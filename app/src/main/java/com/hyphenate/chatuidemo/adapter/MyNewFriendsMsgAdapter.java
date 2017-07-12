package com.hyphenate.chatuidemo.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.hyphenate.chatuidemo.domain.InviteMessage;

/**
 * Created by Administrator on 2016/7/14.
 */
public class MyNewFriendsMsgAdapter extends ArrayAdapter<InviteMessage>{
    public MyNewFriendsMsgAdapter(Context context, int resource) {
        super(context, resource);
    }
}
