package com.caipiao.common.lottery;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.ticket.SchemeTicket;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * 江西时时彩工具类
 * @author  mcdog
 */
public class Lottery1200Utils extends LotteryUtils
{
    /**
     * 一天最大期次数
     */
    public static final int maxPeriodOfDay = 84;
    /**
     * 每天最早开奖时间(小时)
     */
    public static final int earliestKjHour = 9;
    /**
     * 每天最早开奖时间(分钟)
     */
    public static final int earliestKjMinute = 10;

    /**
     * 生成期次(一年期次)
     * @author  mcdog
     * @param   lotteryCloseTime    彩票休市时间(多个时间段用";"连接)
     * @param   calendar            时间
     */
    @Override
    public List<Map<String,String>> createPeriodsOfYear(String lotteryCloseTime, Calendar calendar)
    {
        List<Map<String,String>> periodList = new ArrayList<Map<String, String>>();

        //设置彩票休市时间
        List<LinkedList<Calendar>> closeTimeList = new ArrayList<LinkedList<Calendar>>();
        if(StringUtils.isNotEmpty(lotteryCloseTime))
        {
            String[] closeTimes = lotteryCloseTime.split(";");
            if(closeTimes.length > 0)
            {
                for (String closeTime : closeTimes)
                {
                    String[] times = closeTime.split("~");
                    if (times.length == 2)
                    {
                        LinkedList<Calendar> linkedList = new LinkedList<Calendar>();
                        Calendar closeStartCalendar = DateUtil.parseCalendar(times[0],DateUtil.DEFAULT_DATE_TIME);
                        Calendar closeEndCalendar = DateUtil.parseCalendar(times[1],DateUtil.DEFAULT_DATE_TIME);
                        linkedList.add(closeStartCalendar);
                        linkedList.add(closeEndCalendar);
                        closeTimeList.add(linkedList);
                    }
                }
            }
        }

        //设置上一年度最后一次开奖详细时间
        Calendar lastYearLastKjCalendar = Calendar.getInstance();
        lastYearLastKjCalendar.set(Calendar.YEAR,calendar.get(Calendar.YEAR) - 1);
        lastYearLastKjCalendar.set(Calendar.MONTH,11);
        lastYearLastKjCalendar.set(Calendar.DAY_OF_MONTH,31);
        lastYearLastKjCalendar.setTime(DateUtil.parseDate(
                (DateUtil.formatDate(lastYearLastKjCalendar.getTime(),DateUtil.DEFAULT_DATE) + " " + LotteryConstants.lotteryLatestKjTimeMaps.get(LotteryConstants.SSC_JX)),
                DateUtil.DEFAULT_DATE_TIME));

        //获取一年中第一次开奖详细时间
        Calendar firstKjCalendar = getLotteryFirstKjDateOfYear(LotteryConstants.SSC_JX,calendar);
        firstKjCalendar.setTime(DateUtil.parseDate(
                (DateUtil.formatDate(firstKjCalendar.getTime(),DateUtil.DEFAULT_DATE) + " " + LotteryConstants.lotteryEarliestKjTimeMaps.get(LotteryConstants.SSC_JX)),
                DateUtil.DEFAULT_DATE_TIME));

        //生成期次
        Calendar lastKjCalendar = Calendar.getInstance();
        lastKjCalendar.setTime(lastYearLastKjCalendar.getTime());
        lastKjCalendar.add(Calendar.MINUTE,-3);//本期次开奖时间前3分钟开始销售下一期次
        int periodNum = 1;
        int dayOffset = DateUtil.getDayOfYear(calendar.get(Calendar.YEAR));
        for(int i = 0; i < dayOffset; i ++)
        {
            //遍历彩票休市时间集合,只有该时间不在彩票休市期间,才生成相应的期次
            boolean isCloseTime = false;
            for(LinkedList<Calendar> linkedList : closeTimeList)
            {
                if(firstKjCalendar.after(linkedList.get(0)) && firstKjCalendar.before(linkedList.get(1)))
                {
                    isCloseTime = true;
                    break;
                }
            }
            if(!isCloseTime)
            {
                firstKjCalendar.set(Calendar.HOUR_OF_DAY,earliestKjHour);
                firstKjCalendar.set(Calendar.MINUTE,earliestKjMinute);
                for(int j = 1; j <= maxPeriodOfDay; j ++)
                {
                    Map<String,String> periodMap = new HashMap<String, String>();
                    periodMap.put("lotteryId",LotteryConstants.SSC_JX);
                    String periodStr = j < 10? ("00" + j) : (j < 100? ("0" + j) : ("" + j));
                    periodMap.put("period",(DateUtil.formatDate(firstKjCalendar.getTime(),DateUtil.DEFAULT_DATE1) + periodStr));
                    periodMap.put("sellStartTime",DateUtil.formatDate(lastKjCalendar.getTime(),DateUtil.DEFAULT_DATE_TIME));
                    periodMap.put("sellEndTime",DateUtil.formatDate(getLotterySellEndTime(LotteryConstants.SSC_JX,firstKjCalendar).getTime(),DateUtil.DEFAULT_DATE_TIME));
                    periodMap.put("authorityEndTime",DateUtil.formatDate(firstKjCalendar.getTime(),DateUtil.DEFAULT_DATE_TIME));
                    periodList.add(periodMap);

                    //本期次的开奖时间作为下一期次的开售时间
                    lastKjCalendar.setTime(firstKjCalendar.getTime());
                    lastKjCalendar.add(Calendar.MINUTE,-3);//本期次开奖时间前3分钟开始销售下一期次
                    firstKjCalendar.add(Calendar.MINUTE,10);//10分钟每期,所以分钟加10
                }
            }
            firstKjCalendar.add(Calendar.DAY_OF_MONTH,1);
        }
        return periodList;
    }

    /**
     * 生成期次(依据期次数)
     * @author  mcdog
     * @param   lotteryCloseTime    彩票休市时间(多个时间段用";"连接)
     * @param   startPeriodStr      起始期次(生成的期次从起始期次的下一期次开始)
     * @param   periodNum           生成期次数
     */
    @Override
    public List<Map<String,String>> createPeriodsByPeriodNum(String lotteryCloseTime,String startPeriodStr,int periodNum)
    {
        List<Map<String,String>> periodList = new ArrayList<Map<String, String>>();
        return periodList;
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
    @Override
    public int getSzcMzStatus(String kcode,String wf,String xx,int index,String xxcode)
    {
        int zstatus = 0;
        return zstatus;
    }

    /**
     * 获取默认的开奖号
     * @author  mcdog
     */
    @Override
    public String getDefaultKcodes()
    {
        return "?,?,?,?,?";
    }

    /**
     * 获取方案出票详细信息
     * @author  mcdog
     * @param   scheme        方案信息
     * @param   ticketList    方案出票信息
     * @return  tkinfoList    方案出票详细信息
     */
    @Override
    public List<Dto> getTicketList(Scheme scheme, List<SchemeTicket> ticketList)
    {
        List<Dto> tkinfoList = new ArrayList<Dto>();//用来保存方案出票详细信息
        Dto ticketDto = null;//用来保存单张出票信息
        if(ticketList != null && ticketList.size() > 0)
        {
            Scheme tempScheme = new Scheme();
            tempScheme.setLotteryId(scheme.getLotteryId());
            tempScheme.setSchemeType(SchemeConstants.SCHEME_TYPE_PT);
            for(SchemeTicket schemeTicket : ticketList)
            {
                ticketDto = new BaseDto();
                tempScheme.setSchemeContent(schemeTicket.getCodes());//将票单选项作为投注选项
                ticketDto.put("xxs",schemeTicket.getCodes().replace(";","<br/>"));//设置票单选项
                ticketDto.put("smultiple",schemeTicket.getMultiple());//设置票单倍数
                //设置票单奖金
                double prize = 0d;
                if(StringUtil.isNotEmpty(schemeTicket.getTicketPrizeTax()))
                {
                    prize += schemeTicket.getTicketPrizeTax();
                }
                if(StringUtil.isNotEmpty(schemeTicket.getTicketSubjoinPrizeTax()))
                {
                    prize += schemeTicket.getTicketSubjoinPrizeTax();
                }
                ticketDto.put("lprize",prize + "");//设置票单奖金
                tkinfoList.add(ticketDto);
            }
        }
        return tkinfoList;
    }

    public static void main(String[] args)
    {
        try
        {
            String lotteryCloseTime = "2016-02-07 00:00:00~2016-02-13 24:00:00";
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR,2016);
            LotteryUtils lotteryUtils = new Lottery1200Utils();
            List<Map<String,String>> periodList = lotteryUtils.createPeriodsOfYear(lotteryCloseTime,calendar);
            for(Map<String,String> periodMap : periodList)
            {
                System.out.println("期次号：" + periodMap.get("period")
                        + "，开售时间：" + periodMap.get("sellStartTime")
                        + "，截止时间：" + periodMap.get("sellEndTime")
                        + "，官方截止时间：" + periodMap.get("authorityEndTime"));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
