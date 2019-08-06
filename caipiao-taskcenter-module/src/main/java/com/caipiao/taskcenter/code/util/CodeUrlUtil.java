package com.caipiao.taskcenter.code.util;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.http.Grab;
import com.caipiao.common.util.CalculationUtils;
import com.caipiao.taskcenter.code.mp.*;

/**
 * 抓取开奖号码URL地址
 * Created by kouyi on 2017/11/16.
 */
public class CodeUrlUtil extends CalculationUtils {
    protected static final int DEFAULT_TIMEOUT = 30000;
    protected static final String HOST_TC = "www.lottery.gov.cn";//体彩网域名
    protected static final String HOST_FC = "www.cwl.gov.cn";//福彩官网域名
    protected static final String HOST_ZC = "kaijiang.zhcw.com";//中彩网域名
    protected static final String HOST_CQ = "www.cqcp.net";//重庆福彩官网域名
    protected static final String HOST_GD = "www.gdlottery.cn";//广东体彩官网域名
    protected static final String HOST_CLL = "kjh.cailele.com";//彩乐乐网站域名
    protected static final String HOST_SH = "caipiao.gooooal.com";//上海体彩网域名
    protected static final String HOST_CJW = "kj.cjcp.com.cn";//彩经网域名
    protected static final String REFERER_FC = "http://www.cwl.gov.cn/kjxx/ssq/kjgg/";//福彩官网
    protected static final String AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36";

    // ****************************中国体彩官网开奖号码和开奖公告抓取*******************************
    //大乐透
    public final static String DLT_TCW_PERIOD_URL = "http://www.lottery.gov.cn/api/lottery_kj_detail_new.jspx?_ltype=4&_term=";
    //七星彩
    public final static String QXC_TCW_PERIOD_URL = "http://www.lottery.gov.cn/api/lottery_kj_detail_new.jspx?_ltype=8&_term=";
    //排列3
    public final static String PL3_TCW_PERIOD_URL = "http://www.lottery.gov.cn/api/lottery_kj_detail_new.jspx?_ltype=5&_term=";
    //排列5
    public final static String PL5_TCW_PERIOD_URL = "http://www.lottery.gov.cn/kjplw/{0}.html";
    //胜负彩
    public final static String SFC_TCW_PERIOD_URL = "http://www.lottery.gov.cn/api/lottery_kj_detail.jspx?_ltype=9&&_term=";
    //任九
    public final static String RXJ_TCW_PERIOD_URL = "http://www.lottery.gov.cn/kjsfc/{0}.html";

    // ****************************中彩网开奖号码和开奖公告抓取*******************************
    //福彩3D
    public final static String FC3D_ZCW_PERIOD_URL = "http://kaijiang.zhcw.com/zhcw/html/3d/list.html";

    // ****************************福彩官网开奖号码和开奖公告抓取*******************************
    //双色球
    public final static String SSQ_FCW_PERIOD_URL = "http://www.cwl.gov.cn/cwl_admin/kjxx/findDrawNotice?name=ssq&issueCount=30";
    //七乐彩
    public final static String QLC_FCW_PERIOD_URL = "http://www.cwl.gov.cn/kjxx/qlc/hmhz/";

    // ****************************彩经网开奖号码和开奖公告抓取*******************************
    //江苏快三
    public final static String K3JS_CJW_PERIOD_URL = "http://kj.cjcp.com.cn/gaopin/kuai3/js/";
    //安徽快三
    public final static String K3AH_CJW_PERIOD_URL = "http://kj.cjcp.com.cn/gaopin/kuai3/ah/";
    //吉林快三
    public final static String K3JL_CJW_PERIOD_URL = "https://kj.cjcp.com.cn/gaopin/kuai3/jl/";
    //11运夺金
    public final static String X115SD_CJW_PERIOD_URL = "http://kj.cjcp.com.cn/gaopin/11x5/11ydj/";

    // ****************************重庆福彩官网开奖号码和开奖公告抓取*******************************
    //重庆时时彩
    public final static String SSC_CQGW_PERIOD_URL = "http://www.cqcp.net/game/ssc/";

    // ****************************广东体彩官网开奖号码和开奖公告抓取*******************************
    //广东11选5
    public final static String X115GD_GDTC_PERIOD_URL = "http://www.gdlottery.cn/odata/zst11xuan5.jspx";

    // ****************************彩乐乐开奖号码和开奖公告抓取*******************************
    //11运夺金
    public final static String X115SD_CLL_PERIOD_URL = "http://kjh.cailele.com/kj_11yun.shtml";
    //吉林快三
    public final static String K3JL_CLL_PERIOD_URL = "http://kjh.cailele.com/kj_jlk3.shtml";

    // ****************************上海体彩官网开奖号码和开奖公告抓取*******************************
    //上海11选5
    public final static String X115SH_SHTC_PERIOD_URL = "http://caipiao.gooooal.com/shtc!bc115.action";


}
