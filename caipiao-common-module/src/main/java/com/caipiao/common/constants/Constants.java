package com.caipiao.common.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * 常量工具类
 * Created by kouyi on 2017/10/25.
 */
public class Constants {
    public static final String SENSITIVEWORD_KEY = "SENSITIVEWORD_KEY";//敏感词缓存KEY
    public final static String AUTHCODE = "REGISTER_AUTHCODE";//验证码缓存KEY
    public final static String BACKPWD_AUTHCODE = "BACKPASSWD_AUTHCODE";//找回密码验证码缓存KEY
    public final static String TICKET_CONFIG = "TICKET_CONFIG";//出票参数配置键值
    public final static String IDENTITYRZ_AUTHCODE = "IDENTITYRZ_AUTHCODE";//实名认证验证码缓存KEY
    public final static String UNION_BINDUSERINFO_AUTHCODE = "UNION_BINDUSERINFO_AUTHCODE";//第三方联合登录绑定用户信息验证码缓存KEY

    public final static String BANNER_HOME_UPDATE_TASK = "BANNER_HOME_UPDATE_TASK";//首页banner图任务文件更新
    public final static String NOTICE_HOME_UPDATE_TASK = "NOTICE_HOME_UPDATE_TASK";//首页公告任务文件更新
    public final static String LOTTERY_HOME_UPDATE_TASK = "LOTTERY_HOME_UPDATE_TASK";//首页彩种任务文件更新
    public final static String BANNER_APPSTART_UPDATE_TASK = "BANNER_APPSTART_UPDATE_TASK";//app启动页文件更新

    public final static String JCZQ_MATCH_UPDATE_TASK = "JCZQ_MATCH_UPDATE_TASK";//竞彩足球对阵任务文件更新
    public final static String JCZQ_RESULT_UPDATE_TASK = "JCZQ_RESULT_UPDATE_TASK";//竞彩足球赛果任务文件更新
    public final static String JCLQ_MATCH_UPDATE_TASK = "JCLQ_MATCH_UPDATE_TASK";//竞彩篮球对阵任务文件更新
    public final static String JCLQ_RESULT_UPDATE_TASK = "JCLQ_RESULT_UPDATE_TASK";//竞彩篮球赛果任务文件更新

    public final static String GJ_MATCH_UPDATE_TASK = "GJ_MATCH_UPDATE_TASK";//猜冠军对阵任务文件更新
    public final static String GYJ_MATCH_UPDATE_TASK = "GYJ_MATCH_UPDATE_TASK";//冠亚军对阵任务文件更新

    public final static String PERIOD_SSQ_UPDATE_TASK = "PERIOD_" + LotteryConstants.SSQ + "_UPDATE_TASK";//双色球期次文件更新任务
    public final static String PERIOD_FC3D_UPDATE_TASK = "PERIOD_" + LotteryConstants.FC3D + "_UPDATE_TASK";//福彩3D期次文件更新任务
    public final static String PERIOD_CQSSC_UPDATE_TASK = "PERIOD_" + LotteryConstants.SSC_CQ + "_UPDATE_TASK";//重庆时时彩期次文件更新任务
    public final static String PERIOD_JLK3_UPDATE_TASK = "PERIOD_" + LotteryConstants.K3_JL + "_UPDATE_TASK";//吉林快3期次文件更新任务
    public final static String PERIOD_AHK3_UPDATE_TASK = "PERIOD_" + LotteryConstants.K3_AH + "_UPDATE_TASK";//安徽快3期次文件更新任务
    public final static String PERIOD_QLC_UPDATE_TASK = "PERIOD_" + LotteryConstants.QLC + "_UPDATE_TASK";//七乐彩期次文件更新任务
    public final static String PERIOD_JSK3_UPDATE_TASK = "PERIOD_" + LotteryConstants.K3_JS + "_UPDATE_TASK";//江苏快3期次文件更新任务
    public final static String PERIOD_JXSSC_UPDATE_TASK = "PERIOD_" + LotteryConstants.SSC_JX + "_UPDATE_TASK";//江西时时彩期次文件更新任务
    public final static String PERIOD_DLT_UPDATE_TASK = "PERIOD_" + LotteryConstants.DLT + "_UPDATE_TASK";//超级大乐透期次文件更新任务
    public final static String PERIOD_QXC_UPDATE_TASK = "PERIOD_" + LotteryConstants.QXC + "_UPDATE_TASK";//七星彩期次文件更新任务
    public final static String PERIOD_PL5_UPDATE_TASK = "PERIOD_" + LotteryConstants.PL5 + "_UPDATE_TASK";//排列5期次文件更新任务
    public final static String PERIOD_PL3_UPDATE_TASK = "PERIOD_" + LotteryConstants.PL3 + "_UPDATE_TASK";//排列3期次文件更新任务
    public final static String PERIOD_GD11X5_UPDATE_TASK = "PERIOD_" + LotteryConstants.X511_GD + "_UPDATE_TASK";//广东11选5期次文件更新任务
    public final static String PERIOD_SD11X5_UPDATE_TASK = "PERIOD_" + LotteryConstants.X511_SD + "_UPDATE_TASK";//山东11选5期次文件更新任务
    public final static String PERIOD_SH11X5_UPDATE_TASK = "PERIOD_" + LotteryConstants.X511_SH + "_UPDATE_TASK";//上海11选5期次文件更新任务
    public final static String PERIOD_SFC_UPDATE_TASK = "PERIOD_" + LotteryConstants.SFC + "_UPDATE_TASK";//胜负彩期次文件更新任务
    public final static String PERIOD_RXJ_UPDATE_TASK = "PERIOD_" + LotteryConstants.RXJ + "_UPDATE_TASK";//任九期次文件更新任务
    public final static String PERIOD_JQC_UPDATE_TASK = "PERIOD_" + LotteryConstants.JQC + "_UPDATE_TASK";//四场进球彩期次文件更新任务
    public final static String PERIOD_BQC_UPDATE_TASK = "PERIOD_" + LotteryConstants.BQC + "_UPDATE_TASK";//六场半全场期次文件更新任务

    public final static String PERIOD_SSQ_HISTORY_UPDATE_TASK = "PERIOD_" + LotteryConstants.SSQ + "_HISTORY_UPDATE_TASK";//双色球历史期次文件更新任务
    public final static String PERIOD_FC3D_HISTORY_UPDATE_TASK = "PERIOD_" + LotteryConstants.FC3D + "_HISTORY_UPDATE_TASK";//福彩3D历史期次文件更新任务
    public final static String PERIOD_CQSSC_HISTORY_UPDATE_TASK = "PERIOD_" + LotteryConstants.SSC_CQ + "_HISTORY_UPDATE_TASK";//重庆时时彩历史期次文件更新任务
    public final static String PERIOD_JLK3_HISTORY_UPDATE_TASK = "PERIOD_" + LotteryConstants.K3_JL + "_HISTORY_UPDATE_TASK";//吉林快3历史期次文件更新任务
    public final static String PERIOD_AHK3_HISTORY_UPDATE_TASK = "PERIOD_" + LotteryConstants.K3_AH + "_HISTORY_UPDATE_TASK";//安徽快3历史期次文件更新任务
    public final static String PERIOD_QLC_HISTORY_UPDATE_TASK = "PERIOD_" + LotteryConstants.QLC + "_HISTORY_UPDATE_TASK";//七乐彩历史期次文件更新任务
    public final static String PERIOD_JSK3_HISTORY_UPDATE_TASK = "PERIOD_" + LotteryConstants.K3_JS + "_HISTORY_UPDATE_TASK";//江苏快3历史期次文件更新任务
    public final static String PERIOD_JXSSC_HISTORY_UPDATE_TASK = "PERIOD_" + LotteryConstants.SSC_JX + "_HISTORY_UPDATE_TASK";//江西时时彩历史期次文件更新任务
    public final static String PERIOD_DLT_HISTORY_UPDATE_TASK = "PERIOD_" + LotteryConstants.DLT + "_HISTORY_UPDATE_TASK";//超级大乐透历史期次文件更新任务
    public final static String PERIOD_QXC_HISTORY_UPDATE_TASK = "PERIOD_" + LotteryConstants.QXC + "_HISTORY_UPDATE_TASK";//七星彩历史期次文件更新任务
    public final static String PERIOD_PL5_HISTORY_UPDATE_TASK = "PERIOD_" + LotteryConstants.PL5 + "_HISTORY_UPDATE_TASK";//排列5历史期次文件更新任务
    public final static String PERIOD_PL3_HISTORY_UPDATE_TASK = "PERIOD_" + LotteryConstants.PL3 + "_HISTORY_UPDATE_TASK";//排列3历史期次文件更新任务
    public final static String PERIOD_GD11X5_HISTORY_UPDATE_TASK = "PERIOD_" + LotteryConstants.X511_GD + "_HISTORY_UPDATE_TASK";//广东11选5历史期次文件更新任务
    public final static String PERIOD_SD11X5_HISTORY_UPDATE_TASK = "PERIOD_" + LotteryConstants.X511_SD + "_HISTORY_UPDATE_TASK";//山东11选5历史期次文件更新任务
    public final static String PERIOD_SH11X5_HISTORY_UPDATE_TASK = "PERIOD_" + LotteryConstants.X511_SH + "_HISTORY_UPDATE_TASK";//上海11选5历史期次文件更新任务
    public final static String PERIOD_SFC_HISTORY_UPDATE_TASK = "PERIOD_" + LotteryConstants.SFC + "_HISTORY_UPDATE_TASK";//胜负彩期历史次文件更新任务
    public final static String PERIOD_RXJ_HISTORY_UPDATE_TASK = "PERIOD_" + LotteryConstants.RXJ + "_HISTORY_UPDATE_TASK";//任九历史期次文件更新任务
    public final static String PERIOD_JQC_HISTORY_UPDATE_TASK = "PERIOD_" + LotteryConstants.JQC + "_HISTORY_UPDATE_TASK";//四场进球彩历史期次文件更新任务
    public final static String PERIOD_BQC_HISTORY_UPDATE_TASK = "PERIOD_" + LotteryConstants.BQC + "_HISTORY_UPDATE_TASK";//六场半全场历史期次文件更新任务

    public static final Map<String,String> periodUpdateTaskMaps = new HashMap<String, String>();//期次文件更新任务(彩种id为key,任务名称为值)
    public static final Map<String,String> periodHistoryUpdateTaskMaps = new HashMap<String, String>();//历史期次文件更新任务(彩种id为key,任务名称为值)

    static
    {
        periodUpdateTaskMaps.put(LotteryConstants.SSQ,PERIOD_SSQ_UPDATE_TASK);
        periodUpdateTaskMaps.put(LotteryConstants.FC3D,PERIOD_FC3D_UPDATE_TASK);
        periodUpdateTaskMaps.put(LotteryConstants.SSC_CQ,PERIOD_CQSSC_UPDATE_TASK);
        periodUpdateTaskMaps.put(LotteryConstants.K3_JL,PERIOD_JLK3_UPDATE_TASK);
        periodUpdateTaskMaps.put(LotteryConstants.K3_AH,PERIOD_AHK3_UPDATE_TASK);
        periodUpdateTaskMaps.put(LotteryConstants.QLC,PERIOD_QLC_UPDATE_TASK);
        periodUpdateTaskMaps.put(LotteryConstants.K3_JS,PERIOD_JSK3_UPDATE_TASK);
        periodUpdateTaskMaps.put(LotteryConstants.SSC_JX,PERIOD_JXSSC_UPDATE_TASK);
        periodUpdateTaskMaps.put(LotteryConstants.DLT,PERIOD_DLT_UPDATE_TASK);
        periodUpdateTaskMaps.put(LotteryConstants.QXC,PERIOD_QXC_UPDATE_TASK);
        periodUpdateTaskMaps.put(LotteryConstants.PL5,PERIOD_PL5_UPDATE_TASK);
        periodUpdateTaskMaps.put(LotteryConstants.PL3,PERIOD_PL3_UPDATE_TASK);
        periodUpdateTaskMaps.put(LotteryConstants.X511_GD,PERIOD_GD11X5_UPDATE_TASK);
        periodUpdateTaskMaps.put(LotteryConstants.X511_SD,PERIOD_SD11X5_UPDATE_TASK);
        periodUpdateTaskMaps.put(LotteryConstants.X511_SH,PERIOD_SH11X5_UPDATE_TASK);
        periodUpdateTaskMaps.put(LotteryConstants.JCZQ,JCZQ_MATCH_UPDATE_TASK);
        periodUpdateTaskMaps.put(LotteryConstants.JCLQ,JCLQ_MATCH_UPDATE_TASK);
        periodUpdateTaskMaps.put(LotteryConstants.SFC,PERIOD_SFC_UPDATE_TASK);
        periodUpdateTaskMaps.put(LotteryConstants.RXJ,PERIOD_RXJ_UPDATE_TASK);
        periodUpdateTaskMaps.put(LotteryConstants.JQC,PERIOD_JQC_UPDATE_TASK);
        periodUpdateTaskMaps.put(LotteryConstants.BQC,PERIOD_BQC_UPDATE_TASK);
        periodUpdateTaskMaps.put(LotteryConstants.GJ,GJ_MATCH_UPDATE_TASK);
        periodUpdateTaskMaps.put(LotteryConstants.GYJ,GYJ_MATCH_UPDATE_TASK);

        periodHistoryUpdateTaskMaps.put(LotteryConstants.SSQ,PERIOD_SSQ_HISTORY_UPDATE_TASK);
        periodHistoryUpdateTaskMaps.put(LotteryConstants.FC3D,PERIOD_FC3D_HISTORY_UPDATE_TASK);
        periodHistoryUpdateTaskMaps.put(LotteryConstants.SSC_CQ,PERIOD_CQSSC_HISTORY_UPDATE_TASK);
        periodHistoryUpdateTaskMaps.put(LotteryConstants.K3_JL,PERIOD_JLK3_HISTORY_UPDATE_TASK);
        periodHistoryUpdateTaskMaps.put(LotteryConstants.K3_AH,PERIOD_AHK3_HISTORY_UPDATE_TASK);
        periodHistoryUpdateTaskMaps.put(LotteryConstants.QLC,PERIOD_QLC_HISTORY_UPDATE_TASK);
        periodHistoryUpdateTaskMaps.put(LotteryConstants.K3_JS,PERIOD_JSK3_HISTORY_UPDATE_TASK);
        periodHistoryUpdateTaskMaps.put(LotteryConstants.SSC_JX,PERIOD_JXSSC_HISTORY_UPDATE_TASK);
        periodHistoryUpdateTaskMaps.put(LotteryConstants.DLT,PERIOD_DLT_HISTORY_UPDATE_TASK);
        periodHistoryUpdateTaskMaps.put(LotteryConstants.QXC,PERIOD_QXC_HISTORY_UPDATE_TASK);
        periodHistoryUpdateTaskMaps.put(LotteryConstants.PL5,PERIOD_PL5_HISTORY_UPDATE_TASK);
        periodHistoryUpdateTaskMaps.put(LotteryConstants.PL3,PERIOD_PL3_HISTORY_UPDATE_TASK);
        periodHistoryUpdateTaskMaps.put(LotteryConstants.X511_GD,PERIOD_GD11X5_HISTORY_UPDATE_TASK);
        periodHistoryUpdateTaskMaps.put(LotteryConstants.X511_SD,PERIOD_SD11X5_HISTORY_UPDATE_TASK);
        periodHistoryUpdateTaskMaps.put(LotteryConstants.X511_SH,PERIOD_SH11X5_HISTORY_UPDATE_TASK);
        periodHistoryUpdateTaskMaps.put(LotteryConstants.JCZQ,JCZQ_RESULT_UPDATE_TASK);
        periodHistoryUpdateTaskMaps.put(LotteryConstants.JCLQ,JCLQ_RESULT_UPDATE_TASK);
        periodHistoryUpdateTaskMaps.put(LotteryConstants.SFC,PERIOD_SFC_HISTORY_UPDATE_TASK);
        periodHistoryUpdateTaskMaps.put(LotteryConstants.RXJ,PERIOD_RXJ_HISTORY_UPDATE_TASK);
        periodHistoryUpdateTaskMaps.put(LotteryConstants.JQC,PERIOD_JQC_HISTORY_UPDATE_TASK);
        periodHistoryUpdateTaskMaps.put(LotteryConstants.BQC,PERIOD_BQC_HISTORY_UPDATE_TASK);
    }
}