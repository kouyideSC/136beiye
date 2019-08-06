package com.caipiao.common.lottery;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.plugin.InitPlugin;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.lottery.Lottery;
import com.caipiao.domain.match.GyjMatch;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.sturct.GameCastCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;

/**
 * 彩种常量工具类
 * Created by Kouyi on 2017/11/4.
 */
public class LotteryUtils
{
    protected static final Logger logger = LoggerFactory.getLogger(LotteryUtils.class);

    /**
     * 彩种是否为11选5
     * @param lotteryId
     * @return
     */
    public static boolean is11x5(String lotteryId) {
        if(StringUtil.isEmpty(lotteryId)) {
            return false;
        }
        return lotteryId.equals(LotteryConstants.X511_GD)
                || lotteryId.equals(LotteryConstants.X511_SD) || lotteryId.equals(LotteryConstants.X511_SH);
    }

    /**
     * 彩种是否为福彩
     * @param lotteryId
     * @return
     */
    public static boolean isFC(String lotteryId) {
        if(StringUtil.isEmpty(lotteryId)) {
            return false;
        }
        return lotteryId.equals(LotteryConstants.SSQ) || lotteryId.equals(LotteryConstants.QLC)
                || lotteryId.equals(LotteryConstants.FC3D);
    }

    /**
     * 彩种是否为体彩
     * @param lotteryId
     * @return
     */
    public static boolean isTC(String lotteryId) {
        if(StringUtil.isEmpty(lotteryId)) {
            return false;
        }
        return lotteryId.equals(LotteryConstants.DLT) || lotteryId.equals(LotteryConstants.PL3)
                || lotteryId.equals(LotteryConstants.PL5) || lotteryId.equals(LotteryConstants.QXC)
                || lotteryId.equals(LotteryConstants.SFC) || lotteryId.equals(LotteryConstants.RXJ);
    }

    /**
     * 彩种是否为快频
     * @param lotteryId
     * @return
     */
    public static boolean isKp(String lotteryId) {
        if(StringUtil.isEmpty(lotteryId)) {
            return false;
        }
        return lotteryId.equals(LotteryConstants.K3_JL) || lotteryId.equals(LotteryConstants.K3_AH)
                || lotteryId.equals(LotteryConstants.K3_JS) || lotteryId.equals(LotteryConstants.SSC_CQ)
                || lotteryId.equals(LotteryConstants.SSC_JX) || lotteryId.equals(LotteryConstants.X511_GD)
                || lotteryId.equals(LotteryConstants.X511_SD) || lotteryId.equals(LotteryConstants.X511_SH);
    }

    /**
     * 彩种是否为慢频
     * @param lotteryId
     * @return
     */
    public static boolean isMp(String lotteryId) {
        if(StringUtil.isEmpty(lotteryId)) {
            return false;
        }
        return lotteryId.equals(LotteryConstants.SSQ) || lotteryId.equals(LotteryConstants.FC3D)
                || lotteryId.equals(LotteryConstants.QLC) || lotteryId.equals(LotteryConstants.DLT)
                || lotteryId.equals(LotteryConstants.QXC) || lotteryId.equals(LotteryConstants.PL5)
                || lotteryId.equals(LotteryConstants.PL3);
    }

    /**
     * 彩种是否为竞彩
     * @param lotteryId
     * @return
     */
    public static boolean isJcht(String lotteryId) {
        if(StringUtil.isEmpty(lotteryId)) {
            return false;
        }
        return lotteryId.equals(LotteryConstants.JCZQ) || lotteryId.equals(LotteryConstants.JCLQ);
    }

    /**
     * 彩种是否为竞彩
     * @param lotteryId
     * @return
     */
    public static boolean isJc(String lotteryId) {
        if(StringUtil.isEmpty(lotteryId)) {
            return false;
        }
        return lotteryId.equals(LotteryConstants.JCZQ) || lotteryId.equals(LotteryConstants.JCLQ)
                || lotteryId.equals(LotteryConstants.JCZQSPF) || lotteryId.equals(LotteryConstants.JCZQRQSPF)
                || lotteryId.equals(LotteryConstants.JCZQCBF) || lotteryId.equals(LotteryConstants.JCZQBQC)
                || lotteryId.equals(LotteryConstants.JCZQJQS) || lotteryId.equals(LotteryConstants.JCLQSF)
                || lotteryId.equals(LotteryConstants.JCLQRFSF) || lotteryId.equals(LotteryConstants.JCLQSFC)
                || lotteryId.equals(LotteryConstants.JCLQDXF) || lotteryId.equals(LotteryConstants.GYJ)
                || lotteryId.equals(LotteryConstants.GJ);
    }

    /**
     * 彩种是否为老足彩
     * @param lotteryId
     * @return
     */
    public static boolean isZC(String lotteryId) {
        if(StringUtil.isEmpty(lotteryId)) {
            return false;
        }
        return lotteryId.equals(LotteryConstants.SFC) || lotteryId.equals(LotteryConstants.RXJ)
                || lotteryId.equals(LotteryConstants.JQC) || lotteryId.equals(LotteryConstants.BQC);
    }

    /**
     * 彩种是否为竞彩足球
     * @author  mcdog
     * @param   lotteryId   彩种id
     */
    public static boolean isJczq(String lotteryId)
    {
        if(StringUtil.isEmpty(lotteryId)) {
            return false;
        }
        return lotteryId.equals(LotteryConstants.JCZQ) || lotteryId.equals(LotteryConstants.JCZQSPF)
                || lotteryId.equals(LotteryConstants.JCZQRQSPF) || lotteryId.equals(LotteryConstants.JCZQCBF)
                || lotteryId.equals(LotteryConstants.JCZQBQC) || lotteryId.equals(LotteryConstants.JCZQJQS);
    }

    /**
     * 彩种是否为竞彩篮球
     * @author  mcdog
     * @param   lotteryId   彩种id
     */
    public static boolean isJclq(String lotteryId)
    {
        if(StringUtil.isEmpty(lotteryId)) {
            return false;
        }
        return lotteryId.equals(LotteryConstants.JCLQ) || lotteryId.equals(LotteryConstants.JCLQSF)
                || lotteryId.equals(LotteryConstants.JCLQRFSF) || lotteryId.equals(LotteryConstants.JCLQSFC)
                || lotteryId.equals(LotteryConstants.JCLQDXF);
    }

    /**
     * 彩种是否为猜冠军/冠亚军
     * @author  mcdog
     * @param   lotteryId   彩种id
     */
    public static boolean isGyj(String lotteryId)
    {
        if(StringUtil.isEmpty(lotteryId))
        {
            return false;
        }
        return lotteryId.equals(LotteryConstants.GJ) || lotteryId.equals(LotteryConstants.GYJ);
    }


    /**
     * 根据彩种判断是否为数字彩
     * @author	sjq
     * @param 	lotteryId	彩种id
     */
    public static boolean isSzc(String lotteryId)
    {
        if(StringUtil.isEmpty(lotteryId)) {
            return false;
        }
        boolean flag = true;
        if(LotteryConstants.JCZQ.equals(lotteryId)
                || LotteryConstants.JCLQ.equals(lotteryId)
                || LotteryConstants.SFC.equals(lotteryId)
                || LotteryConstants.RXJ.equals(lotteryId)
                || LotteryConstants.JQC.equals(lotteryId)
                || LotteryConstants.BQC.equals(lotteryId)
                || LotteryConstants.GJ.equals(lotteryId)
                || LotteryConstants.GYJ.equals(lotteryId))
        {
            flag = false;
        }
        return flag;
    }


    /**
     * 彩种是否为奖金低的彩种
     * @param lotteryId
     * @return
     */
    public static boolean isPrizeLower(String lotteryId) {
        if(StringUtil.isEmpty(lotteryId)) {
            return false;
        }
        return lotteryId.equals(LotteryConstants.X511_GD)
                || lotteryId.equals(LotteryConstants.X511_SD) || lotteryId.equals(LotteryConstants.X511_SH)
                || lotteryId.equals(LotteryConstants.K3_JL) || lotteryId.equals(LotteryConstants.K3_AH)
                || lotteryId.equals(LotteryConstants.K3_JS) || lotteryId.equals(LotteryConstants.SSC_CQ)
                || lotteryId.equals(LotteryConstants.SSC_JX) || lotteryId.equals(LotteryConstants.SSC_JX);
    }

    /**
     * 根据彩种id判断是否为快3
     * @author	sjq
     * @param 	lotteryId	彩种id
     */
    public static boolean isK3(String lotteryId)
    {
        if(StringUtil.isEmpty(lotteryId))
        {
            return false;
        }
        return LotteryConstants.K3_JS.equals(lotteryId)
                || LotteryConstants.K3_AH.equals(lotteryId)
                || LotteryConstants.K3_JL.equals(lotteryId);
    }

    /**
     * 获取下拉列表工具类
     * @author  kouyi
     * @return  List<Map>   单个对象为Map,包含:id-select标签编号 value-select标签值)
     */
    public static List<Map<String,String>> getSelectUtil(Map<?, ?> paramMap)
    {
        List<Map<String,String>> selectList = new ArrayList<Map<String, String>>();
        if(StringUtil.isEmpty(paramMap)) {
            return selectList;
        }
        for(Map.Entry<?, ?> entry : paramMap.entrySet())
        {
            Map<String,String> periodStateMap = new HashMap<String, String>();
            periodStateMap.put("id",entry.getKey() + "");
            periodStateMap.put("value",entry.getValue() + "");
            selectList.add(periodStateMap);
        }
        return selectList;
    }

    /**
     * 获取期次状态列表
     * @author  mcdog
     * @return  List<Map>   期次状态集合(单个对象为Map,包含:state-状态值 description-状态描述)
     */
    public static List<Map<String,String>> getPeriodStates()
    {
        List<Map<String,String>> periodStateList = new ArrayList<Map<String, String>>();
        for(Map.Entry<Integer,String> entry : LotteryConstants.periodStateMaps.entrySet())
        {
            Map<String,String> periodStateMap = new HashMap<String, String>();
            periodStateMap.put("state",entry.getKey() + "");
            periodStateMap.put("description",entry.getValue());
            periodStateList.add(periodStateMap);
        }
        return periodStateList;
    }

    /**
     * 获取对阵场次计奖状态列表
     * @author  mcdog
     * @return  List<Map>   对阵场次计奖状态集合(单个对象为Map,包含:state-状态值 description-状态描述)
     */
    public static List<Map<String,String>> getMatchJJStates()
    {
        List<Map<String,String>> matchStateList = new ArrayList<Map<String, String>>();
        for(Map.Entry<Integer,String> entry : LotteryConstants.matchJjStateMaps.entrySet())
        {
            Map<String,String> matchStateMap = new HashMap<String, String>();
            matchStateMap.put("state",entry.getKey() + "");
            matchStateMap.put("description",entry.getValue());
            matchStateList.add(matchStateMap);
        }
        return matchStateList;
    }

    /**
     * 获取一年中第一次开奖日期(双色球/七乐彩/大乐透/七星彩)
     * @author  mcdog
     * @param   lotteryId           彩种id
     * @param   calendar            日期
     */
    public static Calendar getLotteryFirstKjDateOfYear(String lotteryId,Calendar calendar)
    {
        //获取当前年份的第一期次开奖日期
        Calendar firstKjCalendar = Calendar.getInstance();
        firstKjCalendar.set(Calendar.YEAR,calendar.get(Calendar.YEAR));
        firstKjCalendar.set(Calendar.MONTH,0);
        firstKjCalendar.set(Calendar.DAY_OF_MONTH,1);
        if(LotteryConstants.SSQ.equals(lotteryId) || LotteryConstants.QLC.equals(lotteryId) || LotteryConstants.DLT.equals(lotteryId) || LotteryConstants.QXC.equals(lotteryId))
        {
            for(int i = 0; i < 7; i ++)
            {
                //双色球
                if(LotteryConstants.SSQ.equals(lotteryId))
                {
                    if(firstKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY
                            || firstKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY
                            || firstKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                    {
                        break;
                    }
                }
                //七乐彩
                else if(LotteryConstants.QLC.equals(lotteryId))
                {
                    if(firstKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
                            || firstKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY
                            || firstKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
                    {
                        break;
                    }
                }
                //大乐透
                else if(LotteryConstants.DLT.equals(lotteryId))
                {
                    if(firstKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
                            || firstKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY
                            || firstKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
                    {
                        break;
                    }
                }
                //七星彩
                else if(LotteryConstants.QXC.equals(lotteryId))
                {
                    if(firstKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY
                            || firstKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
                            || firstKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                    {
                        break;
                    }
                }
                firstKjCalendar.add(Calendar.DAY_OF_MONTH,1);
            }
        }
        return firstKjCalendar;
    }

    /**
     * 根据彩种和期次开奖时间(官方截止时间)获取期次销售截止时间
     * @author  mcdog
     * @param   lotteryId   彩种id
     * @param   calendar    开奖时间(官方截止时间)
     */
    public static Calendar getLotterySellEndTime(String lotteryId,Calendar calendar)
    {
        Calendar sellEndCalendar = Calendar.getInstance();
        sellEndCalendar.setTime(calendar.getTime());
        if(LotteryConstants.SSQ.equals(lotteryId)
                || LotteryConstants.FC3D.equals(lotteryId)
                || LotteryConstants.QLC.equals(lotteryId)
                || LotteryConstants.DLT.equals(lotteryId)
                || LotteryConstants.QXC.equals(lotteryId)
                || LotteryConstants.PL5.equals(lotteryId)
                || LotteryConstants.PL3.equals(lotteryId))
        {
            sellEndCalendar.set(Calendar.HOUR_OF_DAY,19);
            sellEndCalendar.set(Calendar.MINUTE,30);
        }
        else
        {
            sellEndCalendar.add(Calendar.MINUTE,-3);//默认开奖前3分钟截止
        }
        return sellEndCalendar;
    }

    /**
     * 根据彩种编号获取玩法-后台使用
     * @param lotteryId
     * @return
     */
    public static Map<String, String> getLotteryPlay(String lotteryId) {
        Map<String, String> playMap = new LinkedHashMap<String, String>();
        if(LotteryConstants.JCLQ.equals(lotteryId)) {
            playMap.put(LotteryConstants.JCLQ, "混投");
            playMap.put(LotteryConstants.JCLQSF, "胜负");
            playMap.put(LotteryConstants.JCLQRFSF, "让分胜负");
            playMap.put(LotteryConstants.JCLQSFC, "胜分差");
            playMap.put(LotteryConstants.JCLQDXF, "大小分");
        }
        else if(LotteryConstants.JCZQ.equals(lotteryId)) {
            playMap.put(LotteryConstants.JCZQ, "混投");
            playMap.put(LotteryConstants.JCZQSPF, "胜平负");
            playMap.put(LotteryConstants.JCZQRQSPF, "让球胜平负");
            playMap.put(LotteryConstants.JCZQCBF, "猜比分");
            playMap.put(LotteryConstants.JCZQBQC, "半全场");
            playMap.put(LotteryConstants.JCZQJQS, "总进球");
        }
        else if(LotteryConstants.SFC.equals(lotteryId)) {
            playMap.put(LotteryConstants.SFC, "胜负彩");
        }
        else if(LotteryConstants.RXJ.equals(lotteryId)) {
            playMap.put(LotteryConstants.RXJ, "任九");
        }
        else if(LotteryConstants.BQC.equals(lotteryId)) {
            playMap.put(LotteryConstants.BQC, "半全场");
        }
        else if(LotteryConstants.JQC.equals(lotteryId)) {
            playMap.put(LotteryConstants.JQC, "进球彩");
        }
        else if(LotteryConstants.SSQ.equals(lotteryId)) {
            playMap.put(LotteryConstants.SSQ, "双色球");
        }
        else if(LotteryConstants.DLT.equals(lotteryId)) {
            playMap.put(LotteryConstants.DLT, "大乐透");
        }
        else if(LotteryConstants.QXC.equals(lotteryId)) {
            playMap.put(LotteryConstants.QXC, "七星彩");
        }
        else if(LotteryConstants.QLC.equals(lotteryId)) {
            playMap.put(LotteryConstants.QLC, "七乐彩");
        }
        else if(LotteryConstants.FC3D.equals(lotteryId)) {
            playMap.put(LotteryConstants.FC3D, "福彩3D");
        }
        else if(LotteryConstants.PL3.equals(lotteryId)) {
            playMap.put(LotteryConstants.PL3, "排列3");
        }
        else if(LotteryConstants.PL5.equals(lotteryId)) {
            playMap.put(LotteryConstants.PL5, "排列5");
        }
        else if(LotteryConstants.K3_JL.equals(lotteryId)) {
            playMap.put(LotteryConstants.K3_JL, "吉林快3");
        }
        else if(LotteryConstants.K3_AH.equals(lotteryId)) {
            playMap.put(LotteryConstants.K3_AH, "安徽快3");
        }
        else if(LotteryConstants.K3_JS.equals(lotteryId)) {
            playMap.put(LotteryConstants.K3_JS, "江苏快3");
        }
        else if(LotteryConstants.SSC_CQ.equals(lotteryId)) {
            playMap.put(LotteryConstants.SSC_CQ, "重庆时时彩");
        }
        else if(LotteryConstants.SSC_JX.equals(lotteryId)) {
            playMap.put(LotteryConstants.SSC_JX, "江西时时彩");
        }
        else if(LotteryConstants.X511_GD.equals(lotteryId)) {
            playMap.put(LotteryConstants.X511_GD, "广东11选5");
        }
        else if(LotteryConstants.X511_SD.equals(lotteryId)) {
            playMap.put(LotteryConstants.X511_SD, "山东11选5");
        }
        else if(LotteryConstants.X511_SH.equals(lotteryId)) {
            playMap.put(LotteryConstants.X511_SH, "上海11选5");
        }
        else if(LotteryConstants.GYJ.equals(lotteryId)) {
            playMap.put(LotteryConstants.GYJ, "冠亚军");
        }
        else if(LotteryConstants.GJ.equals(lotteryId)) {
            playMap.put(LotteryConstants.GJ, "猜冠军");
        }
        else {}
        return playMap;
    }

    /**
     * 生成期次(一年期次)
     * @author  mcdog
     * @param   lotteryCloseTime    彩票休市时间(多个时间段用";"连接)
     * @param   calendar            时间
     */
    public List<Map<String,String>> createPeriodsOfYear(String lotteryCloseTime, Calendar calendar) throws Exception
    {
        return new ArrayList<Map<String,String>>();
    }

    /**
     * 生成期次(依据期次数)
     * @author  mcdog
     * @param   lotteryCloseTime    彩票休市时间(多个时间段用";"连接)
     * @param   startPeriodStr      起始期次(生成的期次从起始期次的下一期次开始)
     * @param   periodNum           生成期次数
     */
    public List<Map<String,String>> createPeriodsByPeriodNum(String lotteryCloseTime,String startPeriodStr,int periodNum)
    {
        return new ArrayList<Map<String,String>>();
    }

    /**
     * 生成期次(一年期次)
     * @author  mcdog
     * @param   lotteryId           彩种id
     * @param   lotteryCloseTime    彩票休市时间(多个时间段用";"连接)
     * @param   calendar            时间
     */
    public static List<Map<String,String>> createPeriodsOfYear(String lotteryId,String lotteryCloseTime, Calendar calendar) throws Exception
    {
        List<Map<String,String>> periodList = new ArrayList<Map<String, String>>();
        LotteryUtils lotteryUtils = (LotteryUtils)Thread.currentThread().getContextClassLoader().loadClass("com.caipiao.common.lottery.Lottery" + lotteryId + "Utils").newInstance();
        periodList = lotteryUtils.createPeriodsOfYear(lotteryCloseTime,calendar);
        return periodList;
    }

    /**
     * 生成期次(依据期次数)
     * @author  mcdog
     * @param   lotteryCloseTime    彩票休市时间(多个时间段用";"连接)
     * @param   startPeriodStr      起始期次(生成的期次从起始期次的下一期次开始)
     * @param   periodNum           生成期次数
     */
    public static List<Map<String,String>> createPeriodsByPeriodNum(String lotteryId,String lotteryCloseTime,String startPeriodStr,int periodNum) throws Exception
    {
        List<Map<String,String>> periodList = new ArrayList<Map<String, String>>();
        LotteryUtils lotteryUtils = (LotteryUtils)Thread.currentThread().getContextClassLoader().loadClass("com.caipiao.common.lottery.Lottery" + lotteryId + "Utils").newInstance();
        periodList = lotteryUtils.createPeriodsByPeriodNum(lotteryCloseTime,startPeriodStr,periodNum);
        return periodList;
    }

    /**
     * 获取竞彩足球选项命中状态
     * @author  mcdog
     * @param   lotteryId   彩种id
     * @param   ccinfoDto   场次信息(包含赛果)
     * @param   wf          玩法
     * @param   xx          选项
     * @return  zstatus     命中状态 0-未命中 1-命中
     */
    public static int getJcMzStatus(String lotteryId,Dto ccinfoDto, String wf, String xx) throws Exception
    {
        LotteryUtils lotteryUtils = (LotteryUtils)Thread.currentThread().getContextClassLoader().loadClass("com.caipiao.common.lottery.Lottery" + lotteryId + "Utils").newInstance();
        int zstatus = lotteryUtils.getJcMzStatus(ccinfoDto,wf,xx);
        return zstatus;
    }

    /**
     * 获取足彩选项命中状态
     * @author  mcdog
     * @param   lotteryId   彩种id
     * @param   ccinfoDto   场次信息(包含赛果)
     * @param   xx          选项
     * @return  zstatus     命中状态 0-未命中 1-命中
     */
    public static int getZcMzStatus(String lotteryId,Dto ccinfoDto,String xx) throws Exception
    {
        LotteryUtils lotteryUtils = (LotteryUtils)Thread.currentThread().getContextClassLoader().loadClass("com.caipiao.common.lottery.Lottery" + lotteryId + "Utils").newInstance();
        int zstatus = lotteryUtils.getZcMzStatus(ccinfoDto,xx);
        return zstatus;
    }

    /**
     * 获取数字彩选项命中状态
     * @author  mcdog
     * @param   kcode       开奖号码
     * @param   wf          玩法
     * @param   xx          选项
     * @param   index       选项位置,从0开始(针对有前后区或者位置顺序的玩法)
     * @param   xxcode      完整的单组投注选项
     * @return  zstatus     命中状态 0-未命中 1-命中
     */
    public int getSzcMzStatus(String lotteryId,String kcode,String wf,String xx,int index,String xxcode) throws Exception
    {
        LotteryUtils lotteryUtils = (LotteryUtils)Thread.currentThread().getContextClassLoader().loadClass("com.caipiao.common.lottery.Lottery" + lotteryId + "Utils").newInstance();
        int zstatus = lotteryUtils.getSzcMzStatus(kcode,wf,xx,index,xxcode);
        return zstatus;
    }

    /**
     * 获取竞彩足球选项命中状态
     * @author  mcdog
     * @param   ccinfoDto   场次信息(包含赛果)
     * @param   wf          玩法
     * @param   xx          选项
     * @return  zstatus     命中状态 0-未命中 1-命中
     */
    public int getJcMzStatus(Dto ccinfoDto, String wf, String xx)
    {
        return 0;
    }

    /**
     * 获取足彩选项命中状态
     * @author  mcdog
     * @param   ccinfoDto   场次信息(包含赛果)
     * @param   xx          选项
     * @return  zstatus     命中状态 0-未命中 1-命中
     */
    public int getZcMzStatus(Dto ccinfoDto,String xx)
    {
        return 0;
    }

    /**
     * 获取选项命中状态
     * @author  mcdog
     * @param   kcode       开奖号码
     * @param   wf          玩法
     * @param   xx          选项
     * @param   index       选项位置,从0开始(针对有前后区或者位置顺序的玩法)
     * @param   xxcode      完整的单组投注选项
     * @return  zstatus     命中状态 0-未命中 1-命中
     */
    public int getSzcMzStatus(String kcode,String wf,String xx,int index,String xxcode)
    {
        return 0;
    }

    /**
     * 获取竞彩玩法彩果
     * @author  mcdog
     * @param   ccinfoDto   场次信息(包含赛果)
     * @param   wf          玩法
     * @return  wfcg        玩法彩果
     */
    public String getJcWfcg(Dto ccinfoDto,String wf)
    {
        return "";
    }

    /**
     * 获取默认的开奖号
     * @author  mcdog
     */
    public String getDefaultKcodes()
    {
        return "";
    }

    /**
     * 获取数字彩投注选项集合
     * @author  mcdog
     * @param   scheme      方案对象
     * @return  tzxxList    投注选项集合
     */
    public List<Dto> getSzcTzxxList(Scheme scheme)
    {
        return new ArrayList<Dto>();
    }

    /**
     * 获取数字彩投注选项集合
     * @author  mcdog
     * @param   schemeDto   方案对象
     * @return  tzxxList    投注选项集合
     */
    public List<Dto> getSzcTzxxList(Dto schemeDto)
    {
        return new ArrayList<Dto>();
    }

    /**
     * 获取竞彩投注选项集合
     * @author  mcdog
     * @param   scheme          方案对象
     * @param   matchMaps       方案对阵Map
     * @param   params          参数对象,用来传递或接收额外的参数
     * @return  tzxxList        投注选项集合
     */
    public List<Dto> getJcTzxxList(Scheme scheme,Map<String,Dto> matchMaps,Dto params)
    {
        return new ArrayList<Dto>();
    }

    /**
     * 获取竞彩投注选项集合
     * @author  mcdog
     * @param   scheme      方案Dto
     * @param   matchMaps   方案对阵Map
     * @return  List        投注选项集合
     */
    public List<Dto> getJcTzxxList(Dto scheme,Map<String,Dto> matchMaps,Dto params)
    {
        return new ArrayList<Dto>();
    }

    /**
     * 获取足彩投注选项集合
     * @author  mcdog
     * @param   scheme      方案Dto
     * @param   matchMaps   方案所属期次的对阵
     * @return  List        投注选项集合
     */
    public List<Dto> getZcTzxxList(Dto scheme, Map<String,Object> matchMaps)
    {
        return new ArrayList<Dto>();
    }

    /**
     * 获取足彩投注选项集合
     * @author  mcdog
     * @param   scheme      方案对象
     * @param   matchMaps   方案所属期次的对阵
     * @return  List        投注选项集合
     */
    public List<Dto> getZcTzxxList(Scheme scheme, Map<String,Object> matchMaps)
    {
        return new ArrayList<Dto>();
    }

    /**
     * 获取方案出票详细信息
     * @author  mcdog
     * @param   scheme        方案信息Dto
     * @param   matchMaps     方案对阵Map
     * @param   ticketList    方案出票信息
     * @return  List          方案出票详细信息
     */
    public List<Dto> getTicketList(Scheme scheme,Map<String,Dto> matchMaps,List<SchemeTicket> ticketList)
    {
        return new ArrayList<Dto>();
    }

    /**
     * 获取方案出票详细信息
     * @author  mcdog
     * @param   scheme        方案信息对象
     * @param   ticketList    方案出票信息
     * @return  List          方案出票详细信息
     */
    public List<Dto> getTicketList(Scheme scheme, List<SchemeTicket> ticketList)
    {
        return new ArrayList<Dto>();
    }

    /**
     * 获取方案优化信息
     * @author  mcdog
     * @param   scheme      方案对象
     * @param   matchMaps   方案对阵Map
     * @param   params      参数对象,用来传递或接收额外的参数
     * @return  List        投注选项集合
     */
    public List<Dto> getYhinfos(Scheme scheme,Map<String,Dto> matchMaps,Dto params)
    {
        return new ArrayList<Dto>();
    }

    /**
     * 获取方案优化信息
     * @author  mcdog
     * @param   scheme      方案Dto
     * @param   matchMaps   方案对阵Map
     * @param   params      参数对象,用来传递或接收额外的参数
     * @return  List        投注选项集合
     */
    public List<Dto> getYhinfos(Dto scheme,Map<String,Dto> matchMaps,Dto params)
    {
        return new ArrayList<Dto>();
    }

    /**
     * 将选项号码组按从小到大的顺序排列
     * @author  mcdog
     * @param   codes   选项号码
     */
    public static void sortArrayByAsc(String[] codes)
    {
        if(codes != null && codes.length > 0)
        {
            Arrays.sort(codes, new Comparator<String>()
            {
                @Override
                public int compare(String o1, String o2)
                {
                    return Integer.parseInt(o1) > Integer.parseInt(o2)? 1 : -1;
                }
            });
        }
    }

    /**
     * 将选项过关方式按从小到大的顺序排列
     * @author  mcdog
     * @param   codes   选项号码
     */
    public static void sortArrayWithGgfsByAsc(String[] codes)
    {
        if(codes != null && codes.length > 0)
        {
            Arrays.sort(codes, new Comparator<String>()
            {
                @Override
                public int compare(String o1, String o2)
                {
                    if(o1.indexOf("*") < 0 || o2.indexOf("*") < 0)
                    {
                        return 1;
                    }
                    int g1 = Integer.parseInt(o1.substring(0,o1.indexOf("*")));
                    int g2 = Integer.parseInt(o2.substring(0,o2.indexOf("*")));
                    return g1 > g2? 1 : -1;
                }
            });
        }
    }

    /**
     * 将选项号码字符串按从小到大的顺序排列
     * @author  mcdog
     * @param   codes       选项号码字符串
     * @return  newcodes    排序后的字符串
     */
    public static String sortStrByAsc(String codes)
    {
        String newcodes = codes;
        if(StringUtil.isNotEmpty(codes))
        {
            String[] arrays = new String[codes.length()];
            for(int i = 0; i < arrays.length; i ++)
            {
                arrays[i] = codes.substring(i,i + 1);
            }
            sortArrayByAsc(arrays);
            newcodes = "";
            for(String code : arrays)
            {
                newcodes += code;
            }
            codes = new String(newcodes);
        }
        return newcodes;
    }

    /**
     * 将带赔率的选项按从小到大的顺序排列
     * @author  mcdog
     * @param   codes   选项号码
     */
    public static void sortArrayWithSpByAsc(String[] codes)
    {
        if(codes != null && codes.length > 0)
        {
            Arrays.sort(codes, new Comparator<String>()
            {
                @Override
                public int compare(String o1, String o2)
                {
                    Integer xx1 = 0;
                    Integer xx2 = 0;
                    if(o1.indexOf("(") > -1)
                    {
                        o1 = o1.substring(0,o1.indexOf("("));
                    }
                    if(o1.indexOf(":") > -1)
                    {
                        String[] temp = o1.split(":");
                        xx1 = Integer.parseInt(temp[0] + temp[1]);
                    }
                    else if(o1.indexOf("-") > -1)
                    {
                        String[] temp = o1.split("-");
                        xx1 = Integer.parseInt(temp[0] + temp[1]);
                    }
                    else
                    {
                        xx1 = Integer.parseInt(o1);
                    }
                    if(o2.indexOf("(") > -1)
                    {
                        o2 = o2.substring(0,o2.indexOf("("));
                    }
                    if(o2.indexOf(":") > -1)
                    {
                        String[] temp = o2.split(":");
                        xx2 = Integer.parseInt(temp[0] + temp[1]);
                    }
                    else if(o2.indexOf("-") > -1)
                    {
                        String[] temp = o2.split("-");
                        xx2 = Integer.parseInt(temp[0] + temp[1]);
                    }
                    else
                    {
                        xx2 = Integer.parseInt(o2);
                    }
                    return xx1 > xx2? 1 : -1;
                }
            });
        }
    }

    /**
     * 将带赔率的选项按从大到小的顺序排列
     * @author  mcdog
     * @param   codes   选项号码
     */
    public static void sortArrayWithSpByDesc(String[] codes)
    {
        if(codes != null && codes.length > 0)
        {
            Arrays.sort(codes, new Comparator<String>()
            {
                @Override
                public int compare(String o1, String o2)
                {
                    Integer xx1 = 0;
                    Integer xx2 = 0;
                    if(o1.indexOf("(") > -1)
                    {
                        o1 = o1.substring(0,o1.indexOf("("));
                    }
                    if(o1.indexOf(":") > -1)
                    {
                        String[] temp = o1.split(":");
                        xx1 = Integer.parseInt(temp[0] + temp[1]);
                    }
                    else if(o1.indexOf("-") > -1)
                    {
                        String[] temp = o1.split("-");
                        xx1 = Integer.parseInt(temp[0] + temp[1]);
                    }
                    else
                    {
                        xx1 = Integer.parseInt(o1);
                    }
                    if(o2.indexOf("(") > -1)
                    {
                        o2 = o2.substring(0,o2.indexOf("("));
                    }
                    if(o2.indexOf(":") > -1)
                    {
                        String[] temp = o2.split(":");
                        xx2 = Integer.parseInt(temp[0] + temp[1]);
                    }
                    else if(o2.indexOf("-") > -1)
                    {
                        String[] temp = o2.split("-");
                        xx2 = Integer.parseInt(temp[0] + temp[1]);
                    }
                    else
                    {
                        xx2 = Integer.parseInt(o2);
                    }
                    return xx1 < xx2? 1 : -1;
                }
            });
        }
    }
}