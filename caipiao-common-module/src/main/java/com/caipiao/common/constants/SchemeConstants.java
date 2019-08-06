package com.caipiao.common.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * 方案相关变量定义
 * @author  mcdog
 */
public final class SchemeConstants
{
    public static final int SCHEME_TYPE_PT = 0;//方案类型-普通
    public static final int SCHEME_TYPE_ZH = 1;//方案类型-追号
    public static final int SCHEME_TYPE_YH = 2;//方案类型-优化
    public static final int SCHEME_TYPE_GD = 3;//方案类型-跟单
    public static final int SCHEME_TYPE_SD = 4;//方案类型-神单
    public static final int SCHEME_TYPE_ZNZH = 5;//方案类型-智能追号

    public static final int SCHEME_STATUS_WX = -1;//方案状态-无效
    public static final int SCHEME_STATUS_DZF = 0;//方案状态-待支付
    public static final int SCHEME_STATUS_ZFCG = 1;//方案状态-支付成功
    public static final int SCHEME_STATUS_CPZ = 2;//方案状态-出票中
    public static final int SCHEME_STATUS_CPCG = 3;//方案状态-出票成功
    public static final int SCHEME_STATUS_CPSB = 4;//方案状态-出票失败
    public static final int SCHEME_STATUS_CDCG = 5;//方案状态-撤单成功
    public static final int SCHEME_STATUS_TKF = 6;//方案状态-出票失败待撤单
    public static final int SCHEME_STATUS_ETF = 7;//方案状态-截止未出票待撤单

    public static final String SCHEME_STATUS_FAKJ_DESC = "方案开奖";//方案状态-方案开奖
    public static final String SCHEME_STATUS_FAPJ_DESC = "方案派奖";//方案状态-方案派奖
    public static final String SCHEME_STATUS_FATK_DESC = "方案退款";//方案状态-方案退款
    public static final String SCHEME_STATUS_YTK_DESC = "已退款";//方案状态-已退款

    public static final String SCHEME_SCODE_KP = "KP";//方案编号前缀-快频
    public static final String SCHEME_SCODE_JC = "JC";//方案编号前缀-竞彩
    public static final String SCHEME_SCODE_MP = "MP";//方案编号前缀-慢频
    public static final String SCHEME_SCODE_ZC = "ZC";//方案编号前缀-足彩
    public static final String SCHEME_SCODE_ZH = "ZH";//方案编号前缀-追号
    public static final String SCHEME_SCODE_ZHZN = "ZHZN";//方案编号前缀-智能追号
    public static final String SCHEME_SCODE_JJYH = "YH";//方案编号前缀-奖金优化

    public static final int SCHEME_QUERY_DATERANGE = 3;//客户端查询方案日期跨度(3个月)

    public static final int TICKET_STATUS_REOUT = -4;//废弃票-已重新出票
    public static final int TICKET_STATUS_AWARDFAIL = -3;//奖金核对失败
    public static final int TICKET_STATUS_CANCEL = -2;//系统撤单(任务不用处理该状态)
    public static final int TICKET_STATUS_FAIL = -1;//出票失败
    public static final int TICKET_STATUS_WAITING = 0;//待提票
    public static final int TICKET_STATUS_CAST = 1;//提票成功未出票
    public static final int TICKET_STATUS_OUTED = 2;//出票成功
    public static final int TICKET_STATUS_AWARD = 3;//奖金核对成功


    public static final HashMap<Integer, String> schemeTypesMap = new HashMap<Integer, String>();//方案类型/类型描述集合
    public static final HashMap<Integer, String> schemeStatusMap = new HashMap<Integer, String>();//方案状态/状态描述集合
    public static final HashMap<Integer, String> schemeStatusClientMap = new HashMap<Integer, String>();//前端展示-方案状态/状态描述集合
    public static final HashMap<Integer, String> ticketStatusMap = new HashMap<Integer, String>();//票状态/状态描述集合

    public static final String SCHEME_CKSP_DESC = "赔率为您购买时所显示赔率，最终计算奖金以出票成功时间点官方赔率为准";//方案投注项参考sp描述

    static
    {
        schemeTypesMap.put(SCHEME_TYPE_PT,"普通");
        schemeTypesMap.put(SCHEME_TYPE_ZH,"追号");
        schemeTypesMap.put(SCHEME_TYPE_YH,"优化");
        schemeTypesMap.put(SCHEME_TYPE_GD,"跟单");
        schemeTypesMap.put(SCHEME_TYPE_SD,"神单");
        schemeTypesMap.put(SCHEME_TYPE_ZNZH,"智能追号");

        schemeStatusMap.put(SCHEME_STATUS_WX,"无效");
        schemeStatusMap.put(SCHEME_STATUS_DZF,"待支付");
        schemeStatusMap.put(SCHEME_STATUS_ZFCG,"支付成功");
        schemeStatusMap.put(SCHEME_STATUS_CPZ,"出票中");
        schemeStatusMap.put(SCHEME_STATUS_CPCG,"出票成功");
        schemeStatusMap.put(SCHEME_STATUS_CPSB,"出票失败");
        schemeStatusMap.put(SCHEME_STATUS_CDCG,"撤单成功");
        schemeStatusMap.put(SCHEME_STATUS_TKF,"出票失败待撤单");
        schemeStatusMap.put(SCHEME_STATUS_ETF,"截止未出票待撤单");

        schemeStatusClientMap.put(SCHEME_STATUS_WX,"无效");
        schemeStatusClientMap.put(SCHEME_STATUS_DZF,"待支付");
        schemeStatusClientMap.put(SCHEME_STATUS_ZFCG,"预约中");
        schemeStatusClientMap.put(SCHEME_STATUS_CPZ,"预约中");
        schemeStatusClientMap.put(SCHEME_STATUS_CPCG,"预约成功");
        schemeStatusClientMap.put(SCHEME_STATUS_CPSB,"预约失败");
        schemeStatusClientMap.put(SCHEME_STATUS_CDCG,"撤单成功");

        ticketStatusMap.put(TICKET_STATUS_CANCEL,"系统撤单");
        ticketStatusMap.put(TICKET_STATUS_FAIL,"出票失败");
        ticketStatusMap.put(TICKET_STATUS_WAITING,"待提票");
        ticketStatusMap.put(TICKET_STATUS_CAST,"提票成功未出票");
        ticketStatusMap.put(TICKET_STATUS_OUTED,"出票成功");
        ticketStatusMap.put(TICKET_STATUS_AWARD,"奖金核对成功");
        ticketStatusMap.put(TICKET_STATUS_AWARDFAIL,"奖金核对失败");
        ticketStatusMap.put(TICKET_STATUS_REOUT,"废弃票-已手动重新出票");
    }
}