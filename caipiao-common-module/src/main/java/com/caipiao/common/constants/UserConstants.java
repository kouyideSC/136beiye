package com.caipiao.common.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户相关变量定义
 * Created by kouyi on 2017/9/22.
 */
public final class UserConstants {
    public static final int USER_STATUS_TRUE = 1;//会员状态_正常
    public static final int USER_STATUS_LOCK = 0;//会员状态_锁定
    public static final int USER_STATUS_CANCEL = -1;//会员状态_禁用
    public static Map<Integer,String> userStatusMap = new HashMap<Integer,String>();
    static {
        userStatusMap.put(USER_STATUS_TRUE, "正常");
        userStatusMap.put(USER_STATUS_LOCK, "锁定");
        userStatusMap.put(USER_STATUS_CANCEL, "禁用");
    }

    public static final int USER_SOURCE_WEB=0;//客户端来源_WEB站点
    public static final int USER_SOURCE_IOS=1;//客户端来源_IOS
    public static final int USER_SOURCE_ANDROID=2;//客户端来源_ANDROID
    public static final int USER_SOURCE_H5 =3;//客户端来源_H5
    public static final int USER_SOURCE_OTHER = 4;//客户端_其他
    public static Map<Integer,String> userSourceMap = new HashMap<Integer,String>();
    static {
        userSourceMap.put(USER_SOURCE_WEB, "网站");
        userSourceMap.put(USER_SOURCE_IOS, "苹果");
        userSourceMap.put(USER_SOURCE_ANDROID, "安卓");
        userSourceMap.put(USER_SOURCE_H5, "H5");
    }

    public static final int USER_TYPE_GENERAL=0;//用户类型_普通
    public static final int USER_TYPE_MOBILE=1;//用户类型_如商户合作(待扩展定义)
    public static final int USER_TYPE_VIRTUAL=8888;//用户类型_虚拟用户
    public static final int USER_TYPE_OUTMONEY=9999;//用户类型_出款账户
    public static Map<Integer,String> userTypeMap = new HashMap<Integer,String>();
    static {
        userTypeMap.put(USER_TYPE_GENERAL, "普通用户");
        userTypeMap.put(USER_TYPE_MOBILE, "合作用户");
        userTypeMap.put(USER_TYPE_VIRTUAL, "虚拟用户");
        userTypeMap.put(USER_TYPE_OUTMONEY, "出款账户");
    }

    public static final int USER_VIP_1 = 1;//用户VIP等级1游侠V1[0-999]
    public static final int USER_VIP_2 = 2;//用户VIP等级2骑士V2[1000-9999]
    public static final int USER_VIP_3 = 3;//用户VIP等级3子爵V3[10000-49999]
    public static final int USER_VIP_4 = 4;//用户VIP等级4伯爵V4[50000-99999]
    public static final int USER_VIP_5 = 5;//用户VIP等级5公爵V5[100000-499999]
    public static final int USER_VIP_6 = 6;//用户VIP等级6国王V6[500000-999999]
    public static final int USER_VIP_7 = 7;//用户VIP等级7皇帝V7[1000000以上]
    public static Map<Integer,String> userVipLevelMap = new HashMap<Integer,String>();
    static {
        userVipLevelMap.put(USER_VIP_1, "V1游侠");
        userVipLevelMap.put(USER_VIP_2, "V2骑士");
        userVipLevelMap.put(USER_VIP_3, "V3子爵");
        userVipLevelMap.put(USER_VIP_4, "V4伯爵");
        userVipLevelMap.put(USER_VIP_5, "V5公爵");
        userVipLevelMap.put(USER_VIP_6, "V6国王");
        userVipLevelMap.put(USER_VIP_7, "V7皇帝");
    }

    public static final int USER_PROXY_GENERAL = 0;//代理头衔-普通用户
    public static final int USER_PROXY_SALE = 1;//代理头衔-销售员
    public static final int USER_STATUS_AGENT = 2;//代理头衔-代理员
    public static Map<Integer,String> userProxyMap = new HashMap<Integer,String>();
    static {
        userProxyMap.put(USER_PROXY_GENERAL, "会员");
        userProxyMap.put(USER_PROXY_SALE, "销售员");
        userProxyMap.put(USER_STATUS_AGENT, "代理员");
    }

    //内部虚拟用户投注金额
    public static final Integer[] virtualPay = new Integer[]{1000,1000,1200,1200,1400,1400,1600,1600};
}
