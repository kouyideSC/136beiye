package com.caipiao.common.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * 彩种相关变量定义
 * Created by kouyi on 2017/11/09.
 */
public final class LotteryConstants {
    //竞彩彩种
    public static final String JCZQ = "1700";//竞彩足球
    public static final String JCLQ = "1710";//竞彩篮球

    public static final String JCZQSPF = "1720";//竞彩足球-胜平负
    public static final String JCZQRQSPF = "1900";//竞彩足球-让球胜平负
    public static final String JCZQCBF = "1910";//竞彩足球-猜比分
    public static final String JCZQBQC = "1920";//竞彩足球-半全场
    public static final String JCZQJQS = "1930";//竞彩足球-进球数

    public static final String JCLQSF = "1940";//竞彩篮球-胜负
    public static final String JCLQRFSF = "1950";//竞彩篮球-让分胜负
    public static final String JCLQSFC = "1960";//竞彩篮球-胜分差
    public static final String JCLQDXF = "1970";//竞彩篮球-大小分

    public static final String GJ = "1980";//猜冠军
    public static final String GYJ = "1990";//冠亚军

    //老足彩种-前端用户使用
    public static final String SFC = "1800";//胜负彩
    public static final String RXJ = "1810";//任九
    public static final String JQC = "1820";//四场进球彩
    public static final String BQC = "1830";//六场半全场
    //慢频彩种-前端用户使用
    public static final String SSQ = "1010";//双色球
    public static final String FC3D = "1030";//福彩3D
    public static final String QLC = "1070";//七乐彩
    public static final String DLT = "1500";//超级大乐透
    public static final String DLT_LS = "15001";//超级大乐透乐善玩法
    public static final String QXC = "1510";//七星彩
    public static final String PL5 = "1520";//排列5
    public static final String PL3 = "1530";//排列3
    //快频彩种-前端用户使用
    public static final String K3_JL = "1050";//吉林快3
    public static final String K3_AH = "1060";//安徽快3
    public static final String K3_JS = "1090";//江苏快3
    public static final String SSC_CQ = "1040";//重庆时时彩
    public static final String SSC_JX = "1200";//江西时时彩
    public static final String X511_GD = "1550";//广东11选5
    public static final String X511_SD = "1560";//山东11选5
    public static final String X511_SH = "1570";//上海11选5

    public static final Map<String, String> lotteryMap = new HashMap<String, String>();//所有支持的彩种、用以判断彩种合法性
    public static final Map<Integer, String> periodStateMaps = new HashMap<Integer, String>();//期次状态/状态描述集合

    public static final Map<Integer, String> matchJjStateMaps = new HashMap<Integer, String>();//场次计奖状态/状态描述集合

    //销售状态
    public static final int STATUS_CANCEL = -1;//已取消
    public static final int STATUS_STOP = 0;//已停售
    public static final int STATUS_SELL = 1;//默认-销售中
    public static final int STATUS_EXPIRE = 2;//已截止
    public static final int STATUS_CLOSE = 3;//未开售

    //计奖状态定义
    public static final int STATE_DEFAULT = 0;//计奖状态_初始默认
    public static final int STATE_ONE = 1;//计奖状态_系统自动撤单
    public static final int STATE_TWO = 2;//计奖状态_抓取开奖号
    public static final int STATE_THREE = 3;//计奖状态_待审核开奖号
    public static final int STATE_FOUR = 4;//计奖状态_开奖号审核完成
    public static final int STATE_FILE = 5;//计奖状态_开奖号同步订单
    public static final int STATE_SIX = 6;//计奖状态_计奖匹配完成
    public static final int STATE_SEVEN = 7;//计奖状态_奖金计算完成
    public static final int STATE_EIGHT = 8;//计奖状态_奖金汇总完成
    public static final int STATE_NINE = 9;//计奖状态_奖金核对完成
    public static final int STATE_TEN = 10;//计奖状态_自动派奖完成
    public static final int STATE_ELEVEN = 11;//计奖状态_过关统计完成
    public static final int STATE_TWELVE = 12;//计奖状态_战绩统计完成
    public static final int STATE_THIRTEEN = 13;//计奖状态_派送返点完成
    public static final int STATE_END = 99;//计奖状态_期次处理完成

    //场次计奖状态
    public static final int MATCHJJ_STATE_DEFAULT = 0;//场次计奖状态_初始默认(待处理)
    public static final int MATCHJJ_STATE_ONE = 1;//场次计奖状态_系统自动撤单
    public static final int MATCHJJ_STATE_TWO = 2;//场次计奖状态_抓取赛果中
    public static final int MATCHJJ_STATE_THREE = 3;//场次计奖状态_待审核赛果
    public static final int MATCHJJ_STATE_FOUR = 4;//场次计奖状态_赛果审核完成
    public static final int MATCHJJ_STATE_FILE = 5;//场次计奖状态_系统审核完成
    public static final int MATCHJJ_STATE_SIX = 6;//场次计奖状态_奖金计算完成
    public static final int MATCHJJ_STATE_SEVEN = 7;//场次计奖状态_奖金汇总完成
    public static final int MATCHJJ_STATE_EIGHT = 8;//场次计奖状态_奖金核对完成
    public static final int MATCHJJ_STATE_NINE = 9;//场次计奖状态_自动派奖完成
    public static final int MATCHJJ_STATE_TEN = 10;//场次计奖状态_过关统计完成
    public static final int MATCHJJ_STATE_ELEVEN = 11;//场次计奖状态_战绩统计完成
    public static final int MATCHJJ_STATE_TWELVE = 12;//场次计奖状态_派送返点完成
    public static final int MATCHJJ_STATE_END = 99;//场次计奖状态_场次处理完成

    public static final String LOTTERY_CLOSE_TIME_KEY = "LOTTERY_CLOSE_TIME";//彩票休市时间在参数配置表中的key
    public static final Map<String,String> lotteryEarliestKjTimeMaps = new HashMap<String, String>();//彩种早开奖时间集合
    public static final Map<String,String> lotteryLatestKjTimeMaps = new HashMap<String, String>();//彩种最晚开奖时间集合

    public static final int mpSellEndHour = 19;//慢频销售截止时间-时
    public static final int mpSellEndMinute = 30;//慢频销售截止-分
    public static final double maxSchemeZs = 2000;//方案最大注数

    public static final Map<Integer,String> castMethodMaps = new HashMap<Integer,String>();//投注方式集合
    public static final Map<String,String> playMethodMaps = new HashMap<String,String>();//玩法集合(以彩种编号+ "-" + 玩法标识为key,玩法名称为值)
    public static final Map<String,String> jcWfPrefixMaps = new HashMap<String,String>();//竞彩玩法前缀集合
    public static final Map<String,String> jcXxNameMaps = new HashMap<String,String>();//竞彩选项名称集合
    public static final Map<String,String> jcWfPrefixPlayIdMaps = new HashMap<String,String>();//竞彩玩法前缀-玩法类型编号集合
    public static final Map<String,String> jcPlayNameMaps = new HashMap<String,String>();//竞彩玩法名称集合
    public static HashMap<String, String> playTypeName = new HashMap<>();//竞彩玩法选项对应名称
    public static Map<String, String> playMap = new HashMap<>();//竞彩玩法代表胜平负归属
	
    public static final String JCWF_PREFIX_HH = "HH";//竞彩玩法前缀-混投
    public static final String JCWF_PREFIX_SPF = "SPF";//竞彩玩法前缀-胜平负
    public static final String JCWF_PREFIX_RQSPF = "RQSPF";//竞彩玩法前缀-让球胜平负
    public static final String JCWF_PREFIX_JQS = "JQS";//竞彩玩法前缀-总进球
    public static final String JCWF_PREFIX_BQC = "BQC";//竞彩玩法前缀-半全场
    public static final String JCWF_PREFIX_CBF = "CBF";//竞彩玩法前缀-比分
    public static final String JCWF_PREFIX_SF = "SF";//竞彩玩法前缀-胜负
    public static final String JCWF_PREFIX_RFSF = "RFSF";//竞彩玩法前缀-让分胜负
    public static final String JCWF_PREFIX_DXF = "DXF";//竞彩玩法前缀-大小分
    public static final String JCWF_PREFIX_SFC = "SFC";//竞彩玩法前缀-胜分差
    public static final String JCWF_PREFIX_GJ = "GJ";//猜冠军
    public static final String JCWF_PREFIX_GYJ = "GYJ";//冠亚军

    public static final String jczqMatchPrefix = "jczq_match_";//竞彩足球对阵信息-前缀(缓存key)

    public static final String jclqMatchPrefix = "jclq_match_";//竞彩篮球对阵信息-前缀(缓存key)

    public static final String jczqSpPrefix = "jczq_sp_";//竞彩足球赔率信息-前缀(缓存key)

    public static final String jclqSpPrefix = "jclq_sp_";//竞彩篮球赔率信息-前缀(缓存key)

    public static final String zcPeriodPrefix = "zc_period_";//足彩期次信息-前缀(缓存key)

    public static final String jczqSchemeMatchPrefix = "jczq_scheme_match_";//竞彩足球方案对阵-前缀(缓存key)

    public static final String jclqSchemeMatchPrefix = "jclq_scheme_match_";//竞彩篮球方案对阵-前缀(缓存key)

    public static final String jczqSchemeMatchPrefix_short = "jczq_scheme_match_short_";//竞彩足球方案对阵-前缀(缓存key)

    public static final String jclqSchemeMatchPrefix_short = "jclq_scheme_match_short_";//竞彩篮球方案对阵-前缀(缓存key)

    public static final String lotteryPrefix = "lottery_";//彩种信息-前缀(缓存key)

    public static final String jczqMatchJsbfPrefix = "jczq_match_jsbf_";//竞彩足球对阵比分信息-前缀(缓存key)

    public static final String jclqMatchJsbfPrefix = "jclq_match_jsbf";//竞彩篮球对阵比分信息-前缀(缓存key)

    static{
        lotteryMap.put(JCZQ, JCZQ);
        lotteryMap.put(JCLQ, JCLQ);
        lotteryMap.put(SFC, SFC);
        lotteryMap.put(RXJ, RXJ);
        lotteryMap.put(JQC, JQC);
        lotteryMap.put(BQC, BQC);
        lotteryMap.put(SSQ, SSQ);
        lotteryMap.put(FC3D, FC3D);
        lotteryMap.put(QLC, QLC);
        lotteryMap.put(DLT, DLT);
        lotteryMap.put(QXC, QXC);
        lotteryMap.put(PL5, PL5);
        lotteryMap.put(PL3, PL3);
        lotteryMap.put(K3_JL, K3_JL);
        lotteryMap.put(K3_AH, K3_AH);
        lotteryMap.put(K3_JS, K3_JS);
        lotteryMap.put(SSC_CQ, SSC_CQ);
        lotteryMap.put(SSC_JX, SSC_JX);
        lotteryMap.put(X511_GD, X511_GD);
        lotteryMap.put(X511_SD, X511_SD);
        lotteryMap.put(X511_SH, X511_SH);
        lotteryMap.put(JCZQSPF, JCZQSPF);
        lotteryMap.put(JCZQRQSPF, JCZQRQSPF);
        lotteryMap.put(JCZQCBF, JCZQCBF);
        lotteryMap.put(JCZQBQC, JCZQBQC);
        lotteryMap.put(JCZQJQS, JCZQJQS);
        lotteryMap.put(JCLQSF, JCLQSF);
        lotteryMap.put(JCLQRFSF, JCLQRFSF);
        lotteryMap.put(JCLQSFC, JCLQSFC);
        lotteryMap.put(JCLQDXF, JCLQDXF);
        lotteryMap.put(GJ, GJ);
        lotteryMap.put(GYJ, GYJ);

        //初始化期次状态/状态描述集合
        periodStateMaps.put(STATE_DEFAULT,"待处理(默认)");
        periodStateMaps.put(STATE_ONE,"系统自动撤单");
        periodStateMaps.put(STATE_TWO,"抓取开奖号");
        periodStateMaps.put(STATE_THREE,"待审核开奖号");
        periodStateMaps.put(STATE_FOUR,"开奖号审核完成");
        periodStateMaps.put(STATE_FILE,"开奖号同步订单");
        periodStateMaps.put(STATE_SIX,"计奖匹配完成");
        periodStateMaps.put(STATE_SEVEN,"奖金计算完成");
        periodStateMaps.put(STATE_EIGHT,"奖金汇总完成");
        periodStateMaps.put(STATE_NINE,"奖金核对完成");
        periodStateMaps.put(STATE_TEN,"自动派奖完成");
        periodStateMaps.put(STATE_ELEVEN,"过关统计完成");
        periodStateMaps.put(STATE_TWELVE,"战绩统计完成");
        periodStateMaps.put(STATE_THIRTEEN,"派送返点完成");
        periodStateMaps.put(STATE_END,"期次处理完成");

        matchJjStateMaps.put(MATCHJJ_STATE_DEFAULT,"待处理(默认)");
        matchJjStateMaps.put(MATCHJJ_STATE_ONE,"系统自动撤单");
        matchJjStateMaps.put(MATCHJJ_STATE_TWO,"抓取赛果中");
        matchJjStateMaps.put(MATCHJJ_STATE_THREE,"待审核赛果");
        matchJjStateMaps.put(MATCHJJ_STATE_FOUR,"赛果审核完成");
        matchJjStateMaps.put(MATCHJJ_STATE_FILE,"系统审核完成");
        matchJjStateMaps.put(MATCHJJ_STATE_SIX,"奖金计算完成");
        matchJjStateMaps.put(MATCHJJ_STATE_SEVEN,"奖金汇总完成");
        matchJjStateMaps.put(MATCHJJ_STATE_EIGHT,"奖金核对完成");
        matchJjStateMaps.put(MATCHJJ_STATE_NINE,"自动派奖完成");
        matchJjStateMaps.put(MATCHJJ_STATE_TEN,"过关统计完成");
        matchJjStateMaps.put(MATCHJJ_STATE_ELEVEN,"战绩统计完成");
        matchJjStateMaps.put(MATCHJJ_STATE_TWELVE,"派送返点完成");
        matchJjStateMaps.put(MATCHJJ_STATE_END,"场次处理完成");

        lotteryLatestKjTimeMaps.put(SSQ,"21:15:00");//双色球
        lotteryLatestKjTimeMaps.put(FC3D,"21:15:00");//福彩3D
        lotteryLatestKjTimeMaps.put(SSC_CQ,"24:00:00");//重庆时时彩
        lotteryLatestKjTimeMaps.put(K3_JL,"22:50:00");//吉林快三
        lotteryLatestKjTimeMaps.put(K3_AH,"22:00:00");//安徽快三
        lotteryLatestKjTimeMaps.put(QLC,"21:15:00");//七乐彩
        lotteryLatestKjTimeMaps.put(K3_JS,"22:10:00");//江苏快三
        lotteryLatestKjTimeMaps.put(SSC_JX,"23:00:00");//江西时时彩
        lotteryLatestKjTimeMaps.put(DLT,"20:30:00");//大乐透
        lotteryLatestKjTimeMaps.put(QXC,"20:30:00");//七星彩
        lotteryLatestKjTimeMaps.put(PL5,"20:30:00");//排列五
        lotteryLatestKjTimeMaps.put(PL3,"20:30:00");//排列三
        lotteryLatestKjTimeMaps.put(X511_GD,"23:00:00");//广东11选5
        lotteryLatestKjTimeMaps.put(X511_SD,"22:56:00");//山东11选5
        lotteryLatestKjTimeMaps.put(X511_SH,"23:50:00");//上海11选5

        lotteryEarliestKjTimeMaps.put(SSQ,"21:15:00");//双色球
        lotteryEarliestKjTimeMaps.put(FC3D,"21:15:00");//福彩3D
        lotteryEarliestKjTimeMaps.put(SSC_CQ,"00:05:00");//重庆时时彩
        lotteryEarliestKjTimeMaps.put(K3_JL,"08:30:00");//吉林快三
        lotteryEarliestKjTimeMaps.put(K3_AH,"08:50:00");//安徽快三
        lotteryEarliestKjTimeMaps.put(QLC,"21:15:00");//七乐彩
        lotteryEarliestKjTimeMaps.put(K3_JS,"08:40:00");//江苏快三
        lotteryEarliestKjTimeMaps.put(SSC_JX,"09:00:00");//江西时时彩
        lotteryEarliestKjTimeMaps.put(DLT,"20:30:00");//大乐透
        lotteryEarliestKjTimeMaps.put(QXC,"20:30:00");//七星彩
        lotteryEarliestKjTimeMaps.put(PL5,"20:30:00");//排列五
        lotteryEarliestKjTimeMaps.put(PL3,"20:30:00");//排列三
        lotteryEarliestKjTimeMaps.put(X511_GD,"09:10:00");//广东11选5
        lotteryEarliestKjTimeMaps.put(X511_SD,"08:36:00");//山东11选5
        lotteryEarliestKjTimeMaps.put(X511_SH,"09:00:00");//上海11选5

        castMethodMaps.put(1,"单式");
        castMethodMaps.put(2,"复式");
        castMethodMaps.put(3,"包号");
        castMethodMaps.put(4,"和值");
        castMethodMaps.put(5,"胆拖");

        playMethodMaps.put("1010-1:1","单式");
        playMethodMaps.put("1010-1:2","复式");
        playMethodMaps.put("1010-1:5","胆拖");

        playMethodMaps.put("1030-1:1","直选单式");
        playMethodMaps.put("1030-1:2","直选复式");
        playMethodMaps.put("1030-1:3","直选包号");
        playMethodMaps.put("1030-2:1","组三单式");
        playMethodMaps.put("1030-2:3","组三复式");
        playMethodMaps.put("1030-3:1","组六单式");
        playMethodMaps.put("1030-3:3","组六复式");
        playMethodMaps.put("1030-1:4","直选和值");
        playMethodMaps.put("1030-2:4","组三和值");
        playMethodMaps.put("1030-3:4","组六和值");

        playMethodMaps.put("1050-1:4","和值");
        playMethodMaps.put("1050-2:1","三同号通选");
        playMethodMaps.put("1050-5:1","三连号通选");
        playMethodMaps.put("1050-3:1","三同号");
        playMethodMaps.put("1050-4:1","三不同号");
        playMethodMaps.put("1050-6:1","二同号复选");
        playMethodMaps.put("1050-7:1","二同号单选");
        playMethodMaps.put("1050-8:1","二不同号");

        playMethodMaps.put("1060-1:4","和值");
        playMethodMaps.put("1060-2:1","三同号通选");
        playMethodMaps.put("1060-5:1","三连号通选");
        playMethodMaps.put("1060-3:1","三同号");
        playMethodMaps.put("1060-4:1","三不同号");
        playMethodMaps.put("1060-6:1","二同号复选");
        playMethodMaps.put("1060-7:1","二同号单选");
        playMethodMaps.put("1060-8:1","二不同号");

        playMethodMaps.put("1090-1:4","和值");
        playMethodMaps.put("1090-2:1","三同号通选");
        playMethodMaps.put("1090-5:1","三连号通选");
        playMethodMaps.put("1090-3:1","三同号");
        playMethodMaps.put("1090-4:1","三不同号");
        playMethodMaps.put("1090-6:1","二同号复选");
        playMethodMaps.put("1090-7:1","二同号单选");
        playMethodMaps.put("1090-8:1","二不同号");

        playMethodMaps.put("1070-1:1","单式");
        playMethodMaps.put("1070-1:2","复式");
        playMethodMaps.put("1070-1:5","胆拖");

        playMethodMaps.put("1500-1:1","单式");
        playMethodMaps.put("1500-1:2","复式");
        playMethodMaps.put("1500-2:1","单式追加");
        playMethodMaps.put("1500-2:2","复式追加");
        playMethodMaps.put("1500-1:5","胆拖");
        playMethodMaps.put("1500-2:5","拖胆追加");

        playMethodMaps.put("1510-1:1","单式");
        playMethodMaps.put("1510-1:2","复式");

        playMethodMaps.put("1520-1:1","单式");
        playMethodMaps.put("1520-1:2","复式");

        playMethodMaps.put("1530-1:1","直选单式");
        playMethodMaps.put("1530-1:2","直选复式");
        playMethodMaps.put("1530-1:3","直选包号");
        playMethodMaps.put("1530-2:1","组三单式");
        playMethodMaps.put("1530-2:3","组三复式");
        playMethodMaps.put("1530-3:1","组六单式");
        playMethodMaps.put("1530-3:3","组六复式");
        playMethodMaps.put("1530-1:4","直选和值");
        playMethodMaps.put("1530-2:4","组三和值");
        playMethodMaps.put("1530-3:4","组六和值");

        playMethodMaps.put("1550-1:1","前一直选单式");
        playMethodMaps.put("1550-1:2","前一直选复式");
        playMethodMaps.put("1550-2:1","任选二单式");
        playMethodMaps.put("1550-2:2","任选二复式");
        playMethodMaps.put("1550-3:1","任选三单式");
        playMethodMaps.put("1550-3:2","任选三复式");
        playMethodMaps.put("1550-4:1","任选四单式");
        playMethodMaps.put("1550-4:2","任选四复式");
        playMethodMaps.put("1550-5:1","任选五单式");
        playMethodMaps.put("1550-5:2","任选五复式");
        playMethodMaps.put("1550-6:1","任选六单式");
        playMethodMaps.put("1550-6:2","任选六复式");
        playMethodMaps.put("1550-7:1","任选七单式");
        playMethodMaps.put("1550-7:2","任选七复式");
        playMethodMaps.put("1550-8:1","任选八单式");
        playMethodMaps.put("1550-8:2","任选八复式");
        playMethodMaps.put("1550-9:1","前二直选");
        playMethodMaps.put("1550-9:2","前二直选复式");
        playMethodMaps.put("1550-10:1","前三直选");
        playMethodMaps.put("1550-10:2","前三直选复式");
        playMethodMaps.put("1550-11:1","前二组选");
        playMethodMaps.put("1550-11:2","前二组选复式");
        playMethodMaps.put("1550-12:1","前三组选");
        playMethodMaps.put("1550-12:2","前三组选复式");

        playMethodMaps.put("1560-1:1","前一直选单式");
        playMethodMaps.put("1560-1:2","前一直选复式");
        playMethodMaps.put("1560-2:1","任选二单式");
        playMethodMaps.put("1560-2:2","任选二复式");
        playMethodMaps.put("1560-3:1","任选三单式");
        playMethodMaps.put("1560-3:2","任选三复式");
        playMethodMaps.put("1560-4:1","任选四单式");
        playMethodMaps.put("1560-4:2","任选四复式");
        playMethodMaps.put("1560-5:1","任选五单式");
        playMethodMaps.put("1560-5:2","任选五复式");
        playMethodMaps.put("1560-6:1","任选六单式");
        playMethodMaps.put("1560-6:2","任选六复式");
        playMethodMaps.put("1560-7:1","任选七单式");
        playMethodMaps.put("1560-7:2","任选七复式");
        playMethodMaps.put("1560-8:1","任选八单式");
        playMethodMaps.put("1560-8:2","任选八复式");
        playMethodMaps.put("1560-9:1","前二直选");
        playMethodMaps.put("1560-9:2","前二直选复式");
        playMethodMaps.put("1560-10:1","前三直选");
        playMethodMaps.put("1560-10:2","前三直选复式");
        playMethodMaps.put("1560-11:1","前二组选");
        playMethodMaps.put("1560-11:2","前二组选复式");
        playMethodMaps.put("1560-12:1","前三组选");
        playMethodMaps.put("1560-12:2","前三组选复式");

        playMethodMaps.put("1570-1:1","前一直选单式");
        playMethodMaps.put("1570-1:2","前一直选复式");
        playMethodMaps.put("1570-2:1","任选二单式");
        playMethodMaps.put("1570-2:2","任选二复式");
        playMethodMaps.put("1570-3:1","任选三单式");
        playMethodMaps.put("1570-3:2","任选三复式");
        playMethodMaps.put("1570-4:1","任选四单式");
        playMethodMaps.put("1570-4:2","任选四复式");
        playMethodMaps.put("1570-5:1","任选五单式");
        playMethodMaps.put("1570-5:2","任选五复式");
        playMethodMaps.put("1570-6:1","任选六单式");
        playMethodMaps.put("1570-6:2","任选六复式");
        playMethodMaps.put("1570-7:1","任选七单式");
        playMethodMaps.put("1570-7:2","任选七复式");
        playMethodMaps.put("1570-8:1","任选八单式");
        playMethodMaps.put("1570-8:2","任选八复式");
        playMethodMaps.put("1570-9:1","前二直选");
        playMethodMaps.put("1570-9:2","前二直选复式");
        playMethodMaps.put("1570-10:1","前三直选");
        playMethodMaps.put("1570-10:2","前三直选复式");
        playMethodMaps.put("1570-11:1","前二组选");
        playMethodMaps.put("1570-11:2","前二组选复式");
        playMethodMaps.put("1570-12:1","前三组选");
        playMethodMaps.put("1570-12:2","前三组选复式");
        playMethodMaps.put("1800-1:1","单式");
        playMethodMaps.put("1810-1:1","单式");
        playMethodMaps.put("1810-1:2","复式");
        playMethodMaps.put("1810-1:5","胆拖");
        playMethodMaps.put("1820-1:1","单式");
        playMethodMaps.put("1830-1:1","单式");

        playMethodMaps.put(JCWF_PREFIX_HH,"混投");
        playMethodMaps.put(JCWF_PREFIX_SPF,"胜平负");
        playMethodMaps.put(JCWF_PREFIX_RQSPF,"让球胜平负");
        playMethodMaps.put(JCWF_PREFIX_JQS,"总进球");
        playMethodMaps.put(JCWF_PREFIX_BQC,"半全场");
        playMethodMaps.put(JCWF_PREFIX_CBF,"比分");

        playMethodMaps.put(JCWF_PREFIX_SF,"胜负");
        playMethodMaps.put(JCWF_PREFIX_RFSF,"让分胜负");
        playMethodMaps.put(JCWF_PREFIX_DXF,"大小分");
        playMethodMaps.put(JCWF_PREFIX_SFC,"胜分差");

        playMethodMaps.put(JCWF_PREFIX_GJ,"猜冠军");
        playMethodMaps.put(JCWF_PREFIX_GYJ,"猜冠亚军");

        jcWfPrefixMaps.put(JCZQ,JCWF_PREFIX_HH);
        jcWfPrefixMaps.put(JCZQSPF,JCWF_PREFIX_SPF);
        jcWfPrefixMaps.put(JCZQRQSPF,JCWF_PREFIX_RQSPF);
        jcWfPrefixMaps.put(JCZQJQS,JCWF_PREFIX_JQS);
        jcWfPrefixMaps.put(JCZQBQC,JCWF_PREFIX_BQC);
        jcWfPrefixMaps.put(JCZQCBF,JCWF_PREFIX_CBF);

        jcWfPrefixMaps.put(JCLQ,JCWF_PREFIX_HH);
        jcWfPrefixMaps.put(JCLQSF,JCWF_PREFIX_SF);
        jcWfPrefixMaps.put(JCLQRFSF,JCWF_PREFIX_RFSF);
        jcWfPrefixMaps.put(JCLQDXF,JCWF_PREFIX_DXF);
        jcWfPrefixMaps.put(JCLQSFC,JCWF_PREFIX_SFC);

        jcWfPrefixMaps.put(GJ,JCWF_PREFIX_GJ);
        jcWfPrefixMaps.put(GYJ,JCWF_PREFIX_GYJ);

        jcXxNameMaps.put(JCWF_PREFIX_SPF + "3","主胜");
        jcXxNameMaps.put(JCWF_PREFIX_SPF + "1","平");
        jcXxNameMaps.put(JCWF_PREFIX_SPF + "0","主负");

        jcXxNameMaps.put(JCWF_PREFIX_RQSPF + "3","让球主胜");
        jcXxNameMaps.put(JCWF_PREFIX_RQSPF + "1","让球平");
        jcXxNameMaps.put(JCWF_PREFIX_RQSPF + "0","让球主负");

        jcXxNameMaps.put(JCWF_PREFIX_JQS + "0","0球");
        jcXxNameMaps.put(JCWF_PREFIX_JQS + "1","1球");
        jcXxNameMaps.put(JCWF_PREFIX_JQS + "2","2球");
        jcXxNameMaps.put(JCWF_PREFIX_JQS + "3","3球");
        jcXxNameMaps.put(JCWF_PREFIX_JQS + "4","4球");
        jcXxNameMaps.put(JCWF_PREFIX_JQS + "5","5球");
        jcXxNameMaps.put(JCWF_PREFIX_JQS + "6","6球");
        jcXxNameMaps.put(JCWF_PREFIX_JQS + "7","7+球");

        jcXxNameMaps.put(JCWF_PREFIX_BQC + "3-3","胜胜");
        jcXxNameMaps.put(JCWF_PREFIX_BQC + "3-1","胜平");
        jcXxNameMaps.put(JCWF_PREFIX_BQC + "3-0","胜负");
        jcXxNameMaps.put(JCWF_PREFIX_BQC + "1-3","平胜");
        jcXxNameMaps.put(JCWF_PREFIX_BQC + "1-1","平平");
        jcXxNameMaps.put(JCWF_PREFIX_BQC + "1-0","平负");
        jcXxNameMaps.put(JCWF_PREFIX_BQC + "0-3","负胜");
        jcXxNameMaps.put(JCWF_PREFIX_BQC + "0-1","负平");
        jcXxNameMaps.put(JCWF_PREFIX_BQC + "0-0","负负");

        jcXxNameMaps.put(JCWF_PREFIX_BQC + "33","胜胜");
        jcXxNameMaps.put(JCWF_PREFIX_BQC + "31","胜平");
        jcXxNameMaps.put(JCWF_PREFIX_BQC + "30","胜负");
        jcXxNameMaps.put(JCWF_PREFIX_BQC + "13","平胜");
        jcXxNameMaps.put(JCWF_PREFIX_BQC + "11","平平");
        jcXxNameMaps.put(JCWF_PREFIX_BQC + "10","平负");
        jcXxNameMaps.put(JCWF_PREFIX_BQC + "03","负胜");
        jcXxNameMaps.put(JCWF_PREFIX_BQC + "01","负平");
        jcXxNameMaps.put(JCWF_PREFIX_BQC + "00","负负");

        jcXxNameMaps.put(JCWF_PREFIX_CBF + "1:0","1:0");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "2:0","2:0");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "2:1","2:1");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "3:0","3:0");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "3:1","3:1");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "3:2","3:2");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "4:0","4:0");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "4:1","4:1");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "4:2","4:2");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "5:0","5:0");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "5:1","5:1");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "5:2","5:2");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "9:0","胜其它");

        jcXxNameMaps.put(JCWF_PREFIX_CBF + "0:0","0:0");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "1:1","1:1");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "2:2","2:2");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "3:3","3:3");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "9:9","平其它");

        jcXxNameMaps.put(JCWF_PREFIX_CBF + "0:1","0:1");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "0:2","0:2");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "1:2","1:2");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "0:3","0:3");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "1:3","1:3");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "2:3","2:3");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "0:4","0:4");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "1:4","1:4");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "2:4","2:4");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "0:5","0:5");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "1:5","1:5");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "2:5","2:5");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "0:9","负其它");

        jcXxNameMaps.put(JCWF_PREFIX_CBF + "10","1:0");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "20","2:0");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "21","2:1");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "30","3:0");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "31","3:1");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "32","3:2");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "40","4:0");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "41","4:1");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "42","4:2");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "50","5:0");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "51","5:1");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "52","5:2");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "90","胜其它");

        jcXxNameMaps.put(JCWF_PREFIX_CBF + "00","0:0");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "11","1:1");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "22","2:2");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "33","3:3");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "99","平其它");

        jcXxNameMaps.put(JCWF_PREFIX_CBF + "01","0:1");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "02","0:2");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "12","1:2");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "03","0:3");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "13","1:3");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "23","2:3");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "04","0:4");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "14","1:4");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "24","2:4");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "05","0:5");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "15","1:5");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "25","2:5");
        jcXxNameMaps.put(JCWF_PREFIX_CBF + "09","负其它");

        jcXxNameMaps.put(JCWF_PREFIX_SF + "3","主胜");
        jcXxNameMaps.put(JCWF_PREFIX_SF + "0","主负");
        jcXxNameMaps.put(JCWF_PREFIX_RFSF + "3","让分主胜");
        jcXxNameMaps.put(JCWF_PREFIX_RFSF + "0","让分主负");
        jcXxNameMaps.put(JCWF_PREFIX_DXF + "3","大分");
        jcXxNameMaps.put(JCWF_PREFIX_DXF + "0","小分");

        jcXxNameMaps.put(JCWF_PREFIX_SFC + "11","客胜1-5");
        jcXxNameMaps.put(JCWF_PREFIX_SFC + "12","客胜6-10");
        jcXxNameMaps.put(JCWF_PREFIX_SFC + "13","客胜11-15");
        jcXxNameMaps.put(JCWF_PREFIX_SFC + "14","客胜16-20");
        jcXxNameMaps.put(JCWF_PREFIX_SFC + "15","客胜21-25");
        jcXxNameMaps.put(JCWF_PREFIX_SFC + "16","客胜26+");
        jcXxNameMaps.put(JCWF_PREFIX_SFC + "01","主胜1-5");
        jcXxNameMaps.put(JCWF_PREFIX_SFC + "02","主胜6-10");
        jcXxNameMaps.put(JCWF_PREFIX_SFC + "03","主胜11-15");
        jcXxNameMaps.put(JCWF_PREFIX_SFC + "04","主胜16-20");
        jcXxNameMaps.put(JCWF_PREFIX_SFC + "05","主胜21-25");
        jcXxNameMaps.put(JCWF_PREFIX_SFC + "06","主胜26+");

        jcWfPrefixPlayIdMaps.put((JCZQ + JCWF_PREFIX_HH),JCZQ);
        jcWfPrefixPlayIdMaps.put((JCZQ + JCWF_PREFIX_SPF),JCZQSPF);
        jcWfPrefixPlayIdMaps.put((JCZQ + JCWF_PREFIX_RQSPF),JCZQRQSPF);
        jcWfPrefixPlayIdMaps.put((JCZQ + JCWF_PREFIX_JQS),JCZQJQS);
        jcWfPrefixPlayIdMaps.put((JCZQ + JCWF_PREFIX_BQC),JCZQBQC);
        jcWfPrefixPlayIdMaps.put((JCZQ + JCWF_PREFIX_CBF),JCZQCBF);
        jcWfPrefixPlayIdMaps.put((JCLQ + JCWF_PREFIX_HH),JCLQ);
        jcWfPrefixPlayIdMaps.put((JCLQ + JCWF_PREFIX_SF),JCLQSF);
        jcWfPrefixPlayIdMaps.put((JCLQ + JCWF_PREFIX_RFSF),JCLQRFSF);
        jcWfPrefixPlayIdMaps.put((JCLQ + JCWF_PREFIX_DXF),JCLQDXF);
        jcWfPrefixPlayIdMaps.put((JCLQ + JCWF_PREFIX_SFC),JCLQSFC);
        jcWfPrefixPlayIdMaps.put((GJ + JCWF_PREFIX_GJ),GJ);
        jcWfPrefixPlayIdMaps.put((GYJ + JCWF_PREFIX_GYJ),GYJ);

        jcPlayNameMaps.put(JCZQ,"足球混投");
        jcPlayNameMaps.put(JCZQSPF,"胜平负");
        jcPlayNameMaps.put(JCZQRQSPF,"让球胜平负");
        jcPlayNameMaps.put(JCZQJQS,"总进球");
        jcPlayNameMaps.put(JCZQBQC,"半全场");
        jcPlayNameMaps.put(JCZQCBF,"猜比分");
        jcPlayNameMaps.put(JCLQ,"篮球混投");
        jcPlayNameMaps.put(JCLQSF,"胜负");
        jcPlayNameMaps.put(JCLQRFSF,"让分胜负");
        jcPlayNameMaps.put(JCLQDXF,"大小分");
        jcPlayNameMaps.put(JCLQSFC,"胜分差");
        jcPlayNameMaps.put(GJ,"猜冠军");
        jcPlayNameMaps.put(GYJ,"冠亚军");

        playTypeName.put("BQC3-3", "胜胜");
        playTypeName.put("BQC3-1", "胜平");
        playTypeName.put("BQC3-0", "胜负");
        playTypeName.put("BQC1-3", "平胜");
        playTypeName.put("BQC1-1", "平平");
        playTypeName.put("BQC1-0", "平负");
        playTypeName.put("BQC0-3", "负胜");
        playTypeName.put("BQC0-1", "负平");
        playTypeName.put("BQC0-0", "负负");
        playTypeName.put("CBF9:0", "胜其它");
        playTypeName.put("CBF1:0", "1:0");
        playTypeName.put("CBF2:0", "2:0");
        playTypeName.put("CBF2:1", "2:1");
        playTypeName.put("CBF3:0", "3:0");
        playTypeName.put("CBF3:1", "3:1");
        playTypeName.put("CBF3:2", "3:2");
        playTypeName.put("CBF4:0", "4:0");
        playTypeName.put("CBF4:1", "4:1");
        playTypeName.put("CBF4:2", "4:2");
        playTypeName.put("CBF5:0", "5:0");
        playTypeName.put("CBF5:1", "5:1");
        playTypeName.put("CBF5:2", "5:2");
        playTypeName.put("CBF9:9", "平其它");
        playTypeName.put("CBF0:0", "0:0");
        playTypeName.put("CBF1:1", "1:1");
        playTypeName.put("CBF2:2", "2:2");
        playTypeName.put("CBF3:3", "3:3");
        playTypeName.put("CBF0:9", "负其它");
        playTypeName.put("CBF0:1", "0:1");
        playTypeName.put("CBF0:2", "0:2");
        playTypeName.put("CBF1:2", "1:2");
        playTypeName.put("CBF0:3", "0:3");
        playTypeName.put("CBF1:3", "1:3");
        playTypeName.put("CBF2:3", "2:3");
        playTypeName.put("CBF0:4", "0:4");
        playTypeName.put("CBF1:4", "1:4");
        playTypeName.put("CBF2:4", "2:4");
        playTypeName.put("CBF0:5", "0:5");
        playTypeName.put("CBF1:5", "1:5");
        playTypeName.put("CBF2:5", "2:5");
        playTypeName.put("JQS0", "0球");
        playTypeName.put("JQS1", "1球");
        playTypeName.put("JQS2", "2球");
        playTypeName.put("JQS3", "3球");
        playTypeName.put("JQS4", "4球");
        playTypeName.put("JQS5", "5球");
        playTypeName.put("JQS6", "6球");
        playTypeName.put("JQS7", "7球");
        playTypeName.put("RQSPF3", "让胜");
        playTypeName.put("RQSPF1", "让平");
        playTypeName.put("RQSPF0", "让负");
        playTypeName.put("SPF3", "胜");
        playTypeName.put("SPF1", "平");
        playTypeName.put("SPF0", "负");

        playTypeName.put("SF3", "胜");
        playTypeName.put("SF0", "负");
        playTypeName.put("RFSF3", "让胜");
        playTypeName.put("RFSF0", "让负");
        playTypeName.put("DXF3", "大分");
        playTypeName.put("DXF0", "小分");
        playTypeName.put("SFC01", "主1-5");
        playTypeName.put("SFC02", "主6-10");
        playTypeName.put("SFC03", "主11-15");
        playTypeName.put("SFC04", "主16-20");
        playTypeName.put("SFC05", "主21-25");
        playTypeName.put("SFC06", "主26+");
        playTypeName.put("SFC10", "客1-5");
        playTypeName.put("SFC11", "客6-10");
        playTypeName.put("SFC12", "客11-15");
        playTypeName.put("SFC13", "客16-20");
        playTypeName.put("SFC14", "客21-25");
        playTypeName.put("SFC15", "客26+");

        //足球定义
        playMap.put("SPF3", "3");//胜
        playMap.put("SPF1", "1");//平
        playMap.put("SPF0", "0");//负
        playMap.put("RQSPF3-", "3");//让球-1胜
        playMap.put("RQSPF3+", "31");//让球+1胜
        playMap.put("RQSPF1-", "3");//让球-1平
        playMap.put("RQSPF1+", "0");//让球+1平
        playMap.put("RQSPF0-", "10");//让球-1负
        playMap.put("RQSPF0+", "0");//让球+1负
        playMap.put("JQS0", "1");//进球数0
        playMap.put("JQS1", "30");//进球数1
        playMap.put("JQS2", "310");//进球数2
        playMap.put("JQS3", "30");//进球数3
        playMap.put("JQS4", "310");//进球数4
        playMap.put("JQS5", "30");//进球数5
        playMap.put("JQS6", "310");//进球数6
        playMap.put("JQS7", "30");//进球数7+
        playMap.put("BQC3-3", "3");//半全场胜胜
        playMap.put("BQC3-1", "1");//半全场胜平
        playMap.put("BQC3-0", "0");//半全场胜负
        playMap.put("BQC1-3", "3");//半全场平胜
        playMap.put("BQC1-1", "1");//半全场平平
        playMap.put("BQC1-0", "0");//半全场平负
        playMap.put("BQC0-3", "3");//半全场负胜
        playMap.put("BQC0-1", "1");//半全场负平
        playMap.put("BQC0-0", "0");//半全场负负
        playMap.put("CBF1:0", "3");//比分1:0
        playMap.put("CBF2:0", "3");//比分2:0
        playMap.put("CBF2:1", "3");//比分2:1
        playMap.put("CBF3:0", "3");//比分3:0
        playMap.put("CBF3:1", "3");//比分3:1
        playMap.put("CBF3:2", "3");//比分3:2
        playMap.put("CBF4:0", "3");//比分4:0
        playMap.put("CBF4:1", "3");//比分4:1
        playMap.put("CBF4:2", "3");//比分4:2
        playMap.put("CBF5:0", "3");//比分5:0
        playMap.put("CBF5:1", "3");//比分5:1
        playMap.put("CBF5:2", "3");//比分5:2
        playMap.put("CBF9:0", "3");//比分胜其他
        playMap.put("CBF0:0", "1");//比分0:0
        playMap.put("CBF1:1", "1");//比分1:1
        playMap.put("CBF2:2", "1");//比分2:2
        playMap.put("CBF3:3", "1");//比分3:3
        playMap.put("CBF9:9", "1");//比分平其他
        playMap.put("CBF0:1", "0");//比分0:1
        playMap.put("CBF0:2", "0");//比分0:2
        playMap.put("CBF1:2", "0");//比分1:2
        playMap.put("CBF0:3", "0");//比分0:3
        playMap.put("CBF1:3", "0");//比分1:3
        playMap.put("CBF2:3", "0");//比分2:3
        playMap.put("CBF0:4", "0");//比分0:4
        playMap.put("CBF1:4", "0");//比分1:4
        playMap.put("CBF2:4", "0");//比分2:4
        playMap.put("CBF0:5", "0");//比分0:5
        playMap.put("CBF1:5", "0");//比分1:5
        playMap.put("CBF2:5", "0");//比分2:5
        playMap.put("CBF0:9", "0");//比分负其他
        //篮球定义
        playMap.put("SF3", "3");//胜
        playMap.put("SF0", "0");//负
        playMap.put("RFSF3-", "3");//主-让分胜
        playMap.put("RFSF3+", "30");//主+让分胜
        playMap.put("RFSF0-", "30");//主-让分负
        playMap.put("RFSF0+", "0");//主+让分负
        playMap.put("DXF3", "30");//大分
        playMap.put("DXF0", "30");//小分
        playMap.put("SFC01", "3");//主胜1-5分
        playMap.put("SFC02", "3");//主胜6-10分
        playMap.put("SFC03", "3");//主胜11-15分
        playMap.put("SFC04", "3");//主胜16-20分
        playMap.put("SFC05", "3");//主胜21-25分
        playMap.put("SFC06", "3");//主胜26+分
        playMap.put("SFC10", "0");//客胜1-5分
        playMap.put("SFC11", "0");//客胜6-10分
        playMap.put("SFC12", "0");//客胜11-15分
        playMap.put("SFC13", "0");//客胜16-20分
        playMap.put("SFC14", "0");//客胜21-25分
        playMap.put("SFC15", "0");//客胜26+分
    }
}