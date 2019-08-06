package com.caipiao.common.constants;

import java.util.HashMap;
import java.util.Map;

import static com.caipiao.common.constants.UserConstants.*;

/**
 * 系统KEY相关常量类
 * Created by kouyi on 2017/9/30.
 */
public class KeyConstants {
    public final static String APPKEY = "skey";//签名key
    public final static String APPID = "appId";//为接入端分配的应用编号参数 用来签名
    private final static String APPID_WEB = "100219";//pc端接入
    private final static String APPID_IOS = "100220";//ios端接入
    private final static String APPID_ANDROID = "100221";//安卓端接入
    private final static String APPID_HR = "100222";//h5接入
    private final static String APPID_OTHER = "100223";//其他接入
    public final static String FIXED_VERSION = "20180618";//固定版本号

    //登录签名KEY
    public static Map<String, String> loginKeys = new HashMap<String,String>();
    static {
        loginKeys.put(APPID_WEB, "BB22644WEB10923");//web
        loginKeys.put(APPID_IOS, "BB432ANDROID998");//IOS
        loginKeys.put(APPID_ANDROID, "BB3485IOS038439");//ANDROID
        loginKeys.put(APPID_HR, "BB1038H52983433");//h5
        loginKeys.put(APPID_OTHER, "BB9485OTHER2873");//其他
    }

    public static Map<String, Integer> loginUserMap = new HashMap<String, Integer>();
    static {
        loginUserMap.put(APPID_WEB, USER_SOURCE_WEB);
        loginUserMap.put(APPID_IOS, USER_SOURCE_IOS);
        loginUserMap.put(APPID_ANDROID, USER_SOURCE_ANDROID);
        loginUserMap.put(APPID_HR, USER_SOURCE_H5);
        loginUserMap.put(APPID_OTHER, USER_SOURCE_OTHER);
    }

    public static String getAPPID() {
        return APPID;
    }

    public static String getAppidWeb() {
        return APPID_WEB;
    }

    public static String getAppidIos() {
        return APPID_IOS;
    }

    public static String getAppidAndroid() {
        return APPID_ANDROID;
    }

    public static String getAppidHr() {
        return APPID_HR;
    }

    public static String getAppidOther() {
        return APPID_OTHER;
    }
}
