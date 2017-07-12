package com.huaqie.wubingjie.chat;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chatuidemo.HuanXinConstants;
import com.orhanobut.logger.Logger;
import com.huaqie.wubingjie.utils.UserManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lewis on 16/6/4.
 */
public class SendMessageManager {
    /**
     * 发送定位透传消息
     *
     * @param toChatUsername 发送给谁
     * @param status         是否是群主
     * @param remove         是否发送删除定位
     * @param isGroup        是否为群组
     */
    public static void sendCmdLocationMessage(String toChatUsername, int status, boolean isGroup, double lng, double lat, boolean remove) {
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        //支持单聊和群聊，默认单聊，如果是群聊添加下面这行
        if (isGroup) {
            cmdMsg.setChatType(EMMessage.ChatType.GroupChat);
        } else {
            cmdMsg.setChatType(EMMessage.ChatType.Chat);
        }
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(HuanXinConstants.GPS_CMD_ACTION);
        cmdMsg.setReceipt(toChatUsername);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lng", lng);
            jsonObject.put("lat", lat);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        cmdMsg.setAttribute("remove", remove);
        cmdMsg.setAttribute("location", jsonObject);
        cmdMsg.setAttribute("isLeader", status);
        cmdMsg.addBody(cmdBody);
        EMClient.getInstance().chatManager().sendMessage(cmdMsg);
        Logger.d("发送定位透传信息" + cmdMsg.toString() + "-----" + jsonObject.toString() + "isLeader" + status + "remove" + remove);
    }

    public static void sendLuckyMessage(String receipt, String redId) {
        EMMessage eMessage = EMMessage.createSendMessage(EMMessage.Type.TXT);
        eMessage.setReceipt(receipt);// 设置发起人
        eMessage.setAttribute(HuanXinConstants.MESSAGE_ATTR_IS_LUCKY_MONEY, true);// 设置扩展属性
        eMessage.setAttribute(HuanXinConstants.EXTRA_LUCKY_ID, redId);// 设置扩展属性
        EMTextMessageBody txtBody = new EMTextMessageBody(UserManager.getKeyNickname() + "发了一个红包");
        eMessage.addBody(txtBody);
        eMessage.setChatType(EMMessage.ChatType.GroupChat);// 设置成群聊
        // 发送信息
        EMClient.getInstance().chatManager().sendMessage(eMessage);
    }

    public static void sendLuckyNoticeMessage(String receipt, String redId) {
        EMMessage shakeMessage = EMMessage.createTxtSendMessage(UserManager.getKeyNickname() + "抢了红包", receipt);
        shakeMessage.setChatType(EMMessage.ChatType.GroupChat);
        shakeMessage.setAttribute(HuanXinConstants.EXTRA_USERNAME, UserManager.getKeyNickname());
        shakeMessage.setAttribute(HuanXinConstants.EXTRA_LUCKY_ID, redId);
        shakeMessage.setAttribute(HuanXinConstants.MESSAGE_ATTR_IS_LUCKY_MONEY_NOTICE, true);
        EMClient.getInstance().chatManager().sendMessage(shakeMessage);
    }

    public static void sendShakeMessage(String receipt, String shakeId) {
        EMMessage shakeMessage = EMMessage.createTxtSendMessage("群主发起了摇一摇", receipt);
        shakeMessage.setAttribute(HuanXinConstants.EXTRA_SHAKE_ID, shakeId);
        shakeMessage.setChatType(EMMessage.ChatType.GroupChat);
        shakeMessage.setAttribute(HuanXinConstants.EXTRA_USERNAME, UserManager.getKeyNickname());
        shakeMessage.setAttribute(HuanXinConstants.MESSAGE_ATTR_IS_SHAKE, true);
        EMClient.getInstance().chatManager().sendMessage(shakeMessage);
    }

}
