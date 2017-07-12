package com.huaqie.wubingjie;

/**
 * Created by lewis on 16/6/22.
 */
public class Constants {
    public static String getApiConfig() {
        return getConfig() + "/Api/V1";
    }

    public static String getConfig() {
        int httpConfig = MainApplication.getInstance().getResources().getInteger(R.integer.HTTP_CONFIG);
        if (httpConfig == 1) {
            return API_URL_RELEASE;
        } else if (httpConfig == 2) {
            return API_URL_DEBUG;
        } else {
            return API_URL_RELEASE;
        }
    }

    public static final String API_URL_RELEASE = "http://api.zyr18.com";
    public static final String API_URL_DEBUG = "http://118.178.93.154";
    public static final String IMG__HEAD = "http://ziyouren.b0.upaiyun.com";
    public static final String ADMIN_ID = "1";


    public static final String GET_GROUP_ICON = getApiConfig() + "/getGroupIcon";
    public static final String GET_GROUP_INFO_INFO = getApiConfig() + "/getGroupInfoInfo";//在环信聊天界面获取信息


    /**
     * ====用户业务接口=======
     */
    public static final String ADD_FRIEND = getApiConfig() + "/addFriend";
    public static final String DEL_FRIEND = getApiConfig() + "/delFriend";// 删除好友
    public static final String REGISTER = getApiConfig() + "/register";
    public static final String LOGIN = getApiConfig() + "/login";
    public static final String GET_USER_INFO = getApiConfig() + "/getUserInfo";
    public static final String EDIT_USER_INFO = getApiConfig() + "/editUserInfo";
    public static final String EDIT_PASSWORD = getApiConfig() + "/editPassword";
    public static final String UPDATE_PHOEN = getApiConfig() + "/updatePhone";
    public static final String GET_CODE = getApiConfig() + "/getCode";
    public static final String MY_ORDER = getApiConfig() + "/myOrder";
    public static final String UPDATE_LAT_LNG = getApiConfig() + "/updateLatLng";
    public static final String GET_USER_ACCOUNT = getApiConfig() + "/getUserAccount";
    public static final String FIND_PASSWORD = getApiConfig() + "/findPassword";
    public static final String SEARCH_FRIEND = getApiConfig() + "/searchFriend";
    public static final String USER_DEPOSIT = getApiConfig() + "/userDeposit";
    public static final String MY_SERVE_TASK = getApiConfig() + "/myServeTask";
    public static final String NEW_FRIEND = getApiConfig() + "/newFriend";
    public static final String CHECK_FRIEND = getApiConfig() + "/checkFriend";
    public static final String GET_MEMBER_CARD = getApiConfig() + "/getMemberCard";
    public static final String MEMBER_CHECK_CARD = getApiConfig() + "/memberCheckCard";
    /**
     * ====城市业务接口=======
     */
    public static final String GET_CITY_LIST = getApiConfig() + "/getCityList";
    public static final String GET_CITY_INFO = getApiConfig() + "/getCityInfo";
    /**
     * ====任务业务接口====
     */
    public static final String GET_HOME_DATA = getApiConfig() + "/getHomeData";
    public static final String GET_CLASS = getApiConfig() + "/getClass";
    public static final String SERVE_TASK_DETAIL = getApiConfig() + "/serveTaskDetail";
    public static final String SERVE_TASK_COMMENT = getApiConfig() + "/serveTaskComment";
    public static final String ADD_COMMENT = getApiConfig() + "/addComment";
    public static final String FINISH_SERVE_TASK = getApiConfig() + "/finishServeTask";
    public static final String GET_FINISH_MSG = getApiConfig() + "/getFinishMsg";
    public static final String SUSPEND_SERVE_TASK = getApiConfig() + "/suspendServeTask";
    public static final String GRAB_ORDER = getApiConfig() + "/grabOrder";
    public static final String GO_PAY_SERVE_ORDER = getApiConfig() + "/goPayServeOrder";
    public static final String GO_PAY = getApiConfig() + "/goPay";
    public static final String ADD_SERVE_TASK = getApiConfig() + "/addServeTask";
    public static final String SERVE_TASK_COMPLAIN = getApiConfig() + "/serveTaskComplain";
    public static final String CANCEL_SERVE_TASK = getApiConfig() + "/cancelServeTask";
    public static final String NOTICE_LIST = getApiConfig() + "/noticeList";
    public static final String NOTICE_DETAIL = getApiConfig() + "/noticeDetail";
    public static final String CLOSE_TASK_SERVE = getApiConfig() + "/closeTaskServe";
    public static final String READ_NOTICE = getApiConfig() + "/readNotice";
    public static final String DEL_NOTICE = getApiConfig() + "/delNotice";
    public static final String PAST_TASK = getApiConfig() + "/pastTask";

    /**
     * ====系统业务接口====
     */
    public static final String USER_FEED_BACK = getApiConfig() + "/userFeedback";
    /**
     * ====web====
     */
    public static final String HELP="http://www.zyr18.com/help/index.html";
    public static final String DEAL="http://www.zyr18.com/help/deal.html";
    public static final String ABOUT = "http://www.zyr18.com/help/about.html?version=";
    public static final String ACTIVITY = "http://zyr18.com/index/activity.html?uid=";
}
