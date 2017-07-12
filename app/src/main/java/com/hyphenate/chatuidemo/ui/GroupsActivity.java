/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.chatuidemo.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chatuidemo.HuanXinConstants;
import com.hyphenate.chatuidemo.adapter.GroupAdapter;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EasyUtils;
import com.huaqie.wubingjie.activity.MainActivity;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.model.user.UserInfo;

import java.util.List;

public class GroupsActivity extends HxBaseActivity {
    public static final String TAG = "GroupsActivity";
    private ListView groupListView;
    protected List<EMGroup> grouplist;
    private GroupAdapter groupAdapter;
    private InputMethodManager inputMethodManager;
    public static GroupsActivity instance;
    private View progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    // mainAcitity
    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver broadcastReceiver;

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            swipeRefreshLayout.setRefreshing(false);
            switch (msg.what) {
                case 0:
                    refresh();
                    break;
                case 1:
                    // 更改显示的文字
                    Toast.makeText(GroupsActivity.this, R.string.Failed_to_get_activity_information, Toast.LENGTH_LONG).show();
                    break;

                default:
                    break;
            }
        }

        ;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_fragment_groups);

        // mainActivity 里面的
        registerBroadcastReceiver();
        EMClient.getInstance().contactManager().setContactListener(new MyContactListener());

        instance = this;
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        grouplist = EMClient.getInstance().groupManager().getAllGroups();
        if (grouplist.isEmpty()) {
            Toast.makeText(GroupsActivity.this, "您还没参加任何活动", Toast.LENGTH_SHORT).show();
        }
        groupListView = (ListView) findViewById(R.id.list);
        //show group list
        groupAdapter = new GroupAdapter(this, 1, grouplist);
        groupListView.setAdapter(groupAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                            handler.sendEmptyMessage(0);
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                            handler.sendEmptyMessage(1);
                        }
                    }
                }.start();
            }
        });

        groupListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 直接进入群聊。把其他的注释掉 chun 4.19
//				if (position == 1) {
//					// 新建群聊
//					startActivityForResult(new Intent(GroupsActivity.this, NewGroupActivity.class), 0);
//				} else if (position == 2) {
//					// 添加公开群
//					startActivityForResult(new Intent(GroupsActivity.this, PublicGroupsActivity.class), 0);
//				} else {
                // 进入群聊
                Intent intent = new Intent(GroupsActivity.this, ChatActivity.class);
                // it is group chat
                intent.putExtra("chatType", HuanXinConstants.CHATTYPE_GROUP);
//					intent.putExtra("userId", groupAdapter.getItem(position - 3).getGroupId());
                intent.putExtra("userId", groupAdapter.getItem(position).getGroupId());
                startActivityForResult(intent, 0);
//				}
            }

        });
        groupListView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                    if (getCurrentFocus() != null)
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
        // 获取群组成员
//        GetGroupMenber getGroupMenber = new GetGroupMenber();
//        getGroupMenber.start();

//        BeanJsonRequest<UserInfo> request = new BeanJsonRequest<UserInfo>("", RequestMethod.POST, UserInfo.class);
//        request.add("user_id", Integer.parseInt(EMClient.getInstance().getCurrentUser()));
//        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListener<UserInfo>() {
//            @Override
//            public void onSucceed(int what, Response<UserInfo> response) {
//                if (response.isSucceed() && response.get() != null) {
//                    SaveUserInfo(response.get());
//                }
//            }
//
//            @Override
//            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
//
//            }
//        }, false, false);

    }

    private void SaveUserInfo(UserInfo userInfo) {

        if (userInfo == null) return;

//        SPUtils.put(this, SPConfig.SHARED_KEY_HUANXIN_NICKNAME, userInfo.getNickname());
//        SPUtils.put(this, SPConfig.SHARED_KEY_HUANXIN_IMA_URL, userInfo.getUserImg());
//        SPUtilsHuanXin.put(this, EMClient.getInstance().getCurrentUser() + HuanXinConstants.ATTR_HEAD_IMG_KEY, userInfo.getUserImg());
//        SPUtilsHuanXin.put(this, EMClient.getInstance().getCurrentUser() + HuanXinConstants.ATTR_NICK_NAME_KEY, userInfo.getNickname());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        refresh();
        super.onResume();
    }

    private void refresh() {
        grouplist = EMClient.getInstance().groupManager().getAllGroups();
        groupAdapter = new GroupAdapter(this, 1, grouplist);
        groupListView.setAdapter(groupAdapter);
        groupAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // mainActivity
        unregisterBroadcastReceiver();

        instance = null;
    }


    /**
     * 返回
     *
     * @param view
     */
    public void back(View view) {
        finish();
        if (EasyUtils.isSingleActivity(this)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    // mainActivity
    private void registerBroadcastReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HuanXinConstants.ACTION_CONTACT_CHANAGED);
        intentFilter.addAction(HuanXinConstants.ACTION_GROUP_CHANAGED);
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
//                updateUnreadLabel();
//                updateUnreadAddressLable();
//                if (currentTabIndex == 0) {
//                    // 当前页面如果为聊天历史页面，刷新此页面
//                    if (conversationListFragment != null) {
//                        conversationListFragment.refresh();
//                    }
//                } else if (currentTabIndex == 1) {
//                    if (contactListFragment != null) {
//                        contactListFragment.refresh();
//                    }
//                }
                String action = intent.getAction();
                if (action.equals(HuanXinConstants.ACTION_GROUP_CHANAGED)) {
                    if (EaseCommonUtils.getTopActivity(GroupsActivity.this).equals(GroupsActivity.class.getName())) {
                        GroupsActivity.instance.onResume();
                    }
                }
            }
        };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void unregisterBroadcastReceiver() {
        broadcastManager.unregisterReceiver(broadcastReceiver);
    }

    public class MyContactListener implements EMContactListener {
        @Override
        public void onContactAdded(String username) {
        }

        @Override
        public void onContactDeleted(final String username) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (ChatActivity.activityInstance != null && ChatActivity.activityInstance.toChatUsername != null &&
                            username.equals(ChatActivity.activityInstance.toChatUsername)) {
                        String st10 = getResources().getString(R.string.have_you_removed);
                        Toast.makeText(GroupsActivity.this, ChatActivity.activityInstance.getToChatUsername() + st10, Toast.LENGTH_LONG)
                                .show();
                        ChatActivity.activityInstance.finish();
                    }
                }
            });
        }

        @Override
        public void onContactInvited(String username, String reason) {
        }

        @Override
        public void onContactAgreed(String username) {
        }

        @Override
        public void onContactRefused(String username) {
        }
    }

    /**
     * 获取所有群成员列表
     */
    class GetGroupMenber extends Thread {
        public void run() {
//            for (EMGroup s : grouplist) {
//                String params = "group_id=" + s.getGroupId() +
//                        "&" + AinanaConstants.POST_KEY_SECRET_2_4_0;
//                String result = NetWork.sendPost(AinanaConstants.GET_GROUP_MENBER, params);
//                Log.i("xyz", "group_id : " + s.getGroupId() + " menber : " + result);
////                JSONObject js =
//                SharedPreferences sharedPreferences = getSharedPreferences("huanxin_group", MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString(s.getGroupId(), result);
//                editor.apply();
//            }
        }
    }
}
