package com.hyphenate.chatuidemo.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.huaqie.wubingjie.Constants;
import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.activity.MainActivity;
import com.huaqie.wubingjie.model.netmodel.NetBaseBean;
import com.huaqie.wubingjie.net.CallServer;
import com.huaqie.wubingjie.net.HttpListenerCallback;
import com.huaqie.wubingjie.net.NetBaseRequest;
import com.huaqie.wubingjie.net.RequsetFactory;
import com.huaqie.wubingjie.setting.UserInfoActivity;
import com.huaqie.wubingjie.utils.T;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.HuanXinConstants;
import com.hyphenate.chatuidemo.domain.EmojiconExampleGroupData;
import com.hyphenate.chatuidemo.domain.RobotUser;
import com.hyphenate.chatuidemo.widgets.chatrow.EaseChatRowLocation;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.ui.EaseChatFragment.EaseChatFragmentHelper;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenu;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EasyUtils;
import com.hyphenate.util.PathUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

public class ChatFragment extends EaseChatFragment implements EaseChatFragmentHelper, HttpListenerCallback {

    // constant start from 11 to avoid conflict with constant in base class
    protected static final int ITEM_VIDEO = 11;
    protected static final int ITEM_FILE = 12;
    protected static final int ITEM_VOICE_CALL = 13;
    protected static final int ITEM_VIDEO_CALL = 14;
    //    protected static final int ITEM_RED_PACKET = 16;
    protected static final int ITEM_CHECK_IN = 15;// 自定义：签到
    protected static final int ITEM_START_CHECK_IN = 16;// 自定义：发起签到
    protected static final int ITEM_LUCKY_MONEY = 17;// 自定义：红包
    protected static final int ITEM_SHAKE = 18;// 自定义：摇一摇
    protected static final int ITEM_SCHEDULE = 19;// 自定义：行程
    protected static final int ITEM_GUIDE = 20;// 自定义：指南
    protected static final int ITEM_CALL_GUIDE = 21;// 自定义：呼叫领队
    protected static final int ITEM_SHARE_LOCATION = 22;// 自定义：位置共享

    protected static final int REQUEST_CODE_SELECT_VIDEO = 11;
    protected static final int REQUEST_CODE_SELECT_FILE = 12;
    protected static final int REQUEST_CODE_GROUP_DETAIL = 13;
    protected static final int REQUEST_CODE_CONTEXT_MENU = 14;
    protected static final int REQUEST_CODE_SELECT_AT_USER = 15;

    protected static final int REQUEST_CODE_SEND_MONEY = 16;

    protected static final int MESSAGE_TYPE_SENT_VOICE_CALL = 1;
    protected static final int MESSAGE_TYPE_RECV_VOICE_CALL = 2;
    protected static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 3;
    protected static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 4;
    protected static final int MESSAGE_TYPE_SENT_CHECK_IN = 5;// 自定义；发起签到
    protected static final int MESSAGE_TYPE_RECV_CHECK_IN = 6;// 自定义：收到签到
    protected static final int MESSAGE_TYPE_SENT_LUCKY_MONEY = 7;// 自定义：发红包
    protected static final int MESSAGE_TYPE_RECV_LUCKY_MONEY = 8;// 自定义：收到红包
    protected static final int MESSAGE_TYPE_SENT_SHAKE = 9;// 自定义：发送摇一摇
    protected static final int MESSAGE_TYPE_RECV_SHAKE = 10;// 自定义：收到摇一摇
    protected static final int MESSAGE_TYPE_LUCKY_MONEY_NOTICE = 11;// 自定义：收到红包通知
    protected static final int MESSAGE_TYPE_CLUB_NOTICE = 12;// 自定义：收到俱乐部信息提醒
    protected static final int MESSAGE_TYPE_SENT_ADD_ACTIVITY = 13;// 自定义：发送摇一摇
    protected static final int MESSAGE_TYPE_RECV_ADD_ACTIVITY = 14;// 自定义：收到摇一摇

    protected static final int NET_SIGN = 0x11;// 网络
    protected static final int NET_RED = 0x12;
    protected static final int NET_GET_GROUP_INFO = 0x13;


    private double[] location = {0, 0};// 经纬度


    public int status;

    /**
     * if it is chatBot
     */
    protected boolean isRobot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void registerExtendMenuItem() {
        //use the menu in base class
        super.registerExtendMenuItem();

        inputMenu.registerExtendMenuItem(R.string.attach_video, R.drawable.ease_chat_video_selector_svg, ITEM_VIDEO, extendMenuItemClickListener);
        // 这里判断是不是领队
        if (chatType == HuanXinConstants.CHATTYPE_GROUP) {
            try {
                EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
                if (group.getOwner().equals(EMClient.getInstance().getCurrentUser())) {
                    status = 1;
                } else {
                    status = 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
                T.showShort(getActivity(), "找不到该群，或已被解散");
                getActivity().finish();
            }
        }
    }

    @Override
    protected void setUpView() {
        // 判断是否系统消息
        if (toChatUsername.equals("1")) {
            inputMenu.setVisibility(View.GONE);
        }
        setChatFragmentListener(this);
        if (chatType == HuanXinConstants.CHATTYPE_SINGLE) {
            Map<String, RobotUser> robotMap = DemoHelper.getInstance().getRobotList();
            if (robotMap != null && robotMap.containsKey(toChatUsername)) {
                isRobot = true;
            }
        }
        super.setUpView();
        // set click listener
        titleBar.setLeftLayoutClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (EasyUtils.isSingleActivity(getActivity())) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }
                getActivity().finish();
            }
        });
        ((EaseEmojiconMenu) inputMenu.getEmojiconMenu()).addEmojiconGroup(EmojiconExampleGroupData.getData());
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            inputMenu.getPrimaryMenu().getEditText().addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (count == 1 && "@".equals(String.valueOf(s.charAt(start)))) {
                        startActivityForResult(new Intent(getActivity(), PickAtUserActivity.class).
                                putExtra("groupId", toChatUsername), REQUEST_CODE_SELECT_AT_USER);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
            if (group == null || group.getAffiliationsCount() <= 0) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            EMClient.getInstance().groupManager().getGroupFromServer(toChatUsername);
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
            //获取群组详情
            callGroupInfoinfo();
        } else if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            DemoHelper.getInstance().getUserInfo(toChatUsername, true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
            switch (resultCode) {
                case ContextMenuActivity.RESULT_CODE_COPY: // copy
                    clipboard.setPrimaryClip(ClipData.newPlainText(null,
                            ((EMTextMessageBody) contextMenuMessage.getBody()).getMessage()));
                    break;
                case ContextMenuActivity.RESULT_CODE_DELETE: // delete
                    conversation.removeMessage(contextMenuMessage.getMsgId());
                    messageList.refresh();
                    break;

                case ContextMenuActivity.RESULT_CODE_FORWARD: // forward
                    Intent intent = new Intent(getActivity(), ForwardMessageActivity.class);
                    intent.putExtra("forward_msg_id", contextMenuMessage.getMsgId());
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SELECT_VIDEO: //send the video
                    if (data != null) {
                        int duration = data.getIntExtra("dur", 0);
                        String videoPath = data.getStringExtra("path");
                        File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
                            ThumbBitmap.compress(CompressFormat.JPEG, 100, fos);
                            fos.close();
                            sendVideoMessage(videoPath, file.getAbsolutePath(), duration);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case REQUEST_CODE_SELECT_FILE: //send the file
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            sendFileByUri(uri);
                        }
                    }
                    break;
                case REQUEST_CODE_SELECT_AT_USER:
                    if (data != null) {
                        String username = data.getStringExtra("username");
                        inputAtUsername(username, false);
                    }
                    break;

                case REQUEST_CODE_SEND_MONEY:// 2016.7.7 dachun 红包 屏蔽
//                if (data != null){
//                    sendMessage(RedPacketUtil.createRPMessage(getActivity(), data, toChatUsername));
//                }
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void onSetMessageAttributes(EMMessage message) {
        if (isRobot) {
            //set message extension
            message.setAttribute("em_robot_message", isRobot);
        }
    }

    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        //设置自定义listview item提供者
        return new CustomChatRowProvider();
    }


    @Override
    public void onEnterToChatDetails() {
        if (chatType == HuanXinConstants.CHATTYPE_GROUP) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
            if (group == null) {
                Toast.makeText(getActivity(), R.string.gorup_not_found, Toast.LENGTH_SHORT).show();
                return;
            }
            startActivityForResult(
                    (new Intent(getActivity(), GroupDetailsActivity.class).putExtra("groupId", toChatUsername)),
                    REQUEST_CODE_GROUP_DETAIL);
//        }else if(chatType == HuanXinConstants.CHATTYPE_CHATROOM){
//        	startActivityForResult(new Intent(getActivity(), ChatRoomDetailsActivity.class).putExtra("roomId", toChatUsername), REQUEST_CODE_GROUP_DETAIL);
        }
    }

    @Override
    public void onAvatarClick(String username) {
        UserInfoActivity.goTo(getActivity(), username);
    }

    @Override
    public void onAvatarLongClick(String username) {
        inputAtUsername(username);
    }


    @Override
    public boolean onMessageBubbleClick(EMMessage message) {

        return false;
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        for (EMMessage message : messages) {
            // EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
        }
        super.onCmdMessageReceived(messages);
    }

    @Override
    protected void jumpBaiduMap() {
        startActivityForResult(new Intent(getActivity(), EaseBaiduMapActivity.class), REQUEST_CODE_MAP);
    }

    @Override
    public void onMessageBubbleLongClick(EMMessage message) {
        // no message forward when in chat room
        startActivityForResult((new Intent(getActivity(), ContextMenuActivity.class)).putExtra("message", message)
                        .putExtra("ischatroom", chatType == EaseConstant.CHATTYPE_CHATROOM),
                REQUEST_CODE_CONTEXT_MENU);
    }

    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        switch (itemId) {
            case ITEM_VIDEO:
                Intent intent = new Intent(getActivity(), ImageGridActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onSucceed(int what, NetBaseBean netBaseBean) {
    }


    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

    }


    /**
     * chat row provider
     */
    protected final class CustomChatRowProvider implements EaseCustomChatRowProvider {
        @Override
        public int getCustomChatRowTypeCount() {
            //音、视频通话发送、接收共4种
            //自定义：加上签到、红包、摇一摇共10种
            //自定义：红包提醒 共11种
            //自定义：系统消息提醒 共12种
            //自定义：俱乐部发布活动推送到群 共14种
            return 14;
        }

        @Override
        public int getCustomChatRowType(EMMessage message) {
            if (message.getType() == EMMessage.Type.TXT) {
                //语音通话类型
                if (message.getBooleanAttribute(HuanXinConstants.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL : MESSAGE_TYPE_SENT_VOICE_CALL;
                } else if (message.getBooleanAttribute(HuanXinConstants.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    //视频通话
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL : MESSAGE_TYPE_SENT_VIDEO_CALL;
                } else if (message.getBooleanAttribute(HuanXinConstants.MESSAGE_ATTR_IS_CHECK_IN, false)) {
                    //签到
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_CHECK_IN : MESSAGE_TYPE_SENT_CHECK_IN;
                } else if (message.getBooleanAttribute(HuanXinConstants.MESSAGE_ATTR_IS_LUCKY_MONEY, false)) {
                    //红包
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_LUCKY_MONEY : MESSAGE_TYPE_SENT_LUCKY_MONEY;
                } else if (message.getBooleanAttribute(HuanXinConstants.MESSAGE_ATTR_IS_SHAKE, false)) {
                    //摇一摇
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_SHAKE : MESSAGE_TYPE_SENT_SHAKE;
                } else if (message.getBooleanAttribute(HuanXinConstants.MESSAGE_ATTR_IS_LUCKY_MONEY_NOTICE, false)) {
                    //谁抢了红包
                    return MESSAGE_TYPE_LUCKY_MONEY_NOTICE;
                } else if (message.getBooleanAttribute(HuanXinConstants.MESSAGE_ATTR_IS_ADD_ACTIVITY, false)) {
                    //俱乐部发布活动推送到群
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_ADD_ACTIVITY : MESSAGE_TYPE_SENT_ADD_ACTIVITY;
                } else if (!message.getStringAttribute(HuanXinConstants.MESSAGE_ATTR_IS_MSG_STYLE, "").equals("")) {
                    //系统消息
                    return MESSAGE_TYPE_CLUB_NOTICE;
                }
            }
            return 0;
        }

        @Override
        public EaseChatRow getCustomChatRow(EMMessage message, int position, BaseAdapter adapter) {
            if (message.getType() == EMMessage.Type.TXT) {
                // 语音通话,  视频通话
                if (message.getBooleanAttribute(HuanXinConstants.MESSAGE_ATTR_IS_VOICE_CALL, false) ||
                        message.getBooleanAttribute(HuanXinConstants.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                }
            } else if (message.getType() == EMMessage.Type.LOCATION) {
                //把位置抽出来
                return new EaseChatRowLocation(getActivity(), message, position, adapter);
            }
            return null;
        }
    }

    @Override
    protected void initView() {
        super.initView();
    }


    private void callGroupInfoinfo() {
        NetBaseRequest request = RequsetFactory.creatNoUidRequest(Constants.GET_GROUP_INFO_INFO);
        if (TextUtils.isEmpty(toChatUsername)) {
            return;
        }
        request.add("group_id", toChatUsername);
        CallServer.getRequestInstance().add(getActivity(), NET_GET_GROUP_INFO, request, this, false, false);
    }
}
