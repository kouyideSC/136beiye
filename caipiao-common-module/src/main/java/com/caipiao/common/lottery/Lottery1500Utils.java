package com.caipiao.common.lottery;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.plugin.InitPlugin;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.plugin.helper.GamePluginAdapter;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * 大乐透工具类
 * @author  mcdog
 */
public class Lottery1500Utils extends LotteryUtils
{
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

        //获取上一年度最后一次开奖日期
        Calendar lastYearLastKjCalendar = Calendar.getInstance();
        lastYearLastKjCalendar.set(Calendar.YEAR,calendar.get(Calendar.YEAR) - 1);
        lastYearLastKjCalendar.set(Calendar.MONTH,11);
        lastYearLastKjCalendar.set(Calendar.DAY_OF_MONTH,31);
        for(int i = 0; i < 7; i ++)
        {
            if(lastYearLastKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
                    || lastYearLastKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY
                    || lastYearLastKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
            {
                break;
            }
            lastYearLastKjCalendar.add(Calendar.DAY_OF_MONTH,-1);
        }

        //设置上一年度最后一期次的销售截止时间
        lastYearLastKjCalendar.set(Calendar.HOUR_OF_DAY,LotteryConstants.mpSellEndHour);
        lastYearLastKjCalendar.set(Calendar.MINUTE,LotteryConstants.mpSellEndMinute);
        lastYearLastKjCalendar.set(Calendar.SECOND,0);

        //获取本年第一期次开奖的详细时间
        Calendar firstKjCalendar = getLotteryFirstKjDateOfYear(LotteryConstants.DLT,calendar);
        firstKjCalendar.setTime(DateUtil.parseDate(
                (DateUtil.formatDate(firstKjCalendar.getTime(),DateUtil.DEFAULT_DATE) + " " + LotteryConstants.lotteryEarliestKjTimeMaps.get(LotteryConstants.DLT)),
                DateUtil.DEFAULT_DATE_TIME));

        //生成期次
        Calendar lastKjCalendar = Calendar.getInstance();
        lastKjCalendar.setTime(lastYearLastKjCalendar.getTime());
        int periodNum = 1;
        int dayOffset = DateUtil.getDayOfYear(calendar.get(Calendar.YEAR));
        dayOffset = dayOffset - firstKjCalendar.get(Calendar.DAY_OF_YEAR);
        for(int i = 0; i < dayOffset; i ++)
        {
            if(firstKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
                    || firstKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY
                    || firstKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
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
                    Map<String,String> periodMap = new HashMap<String, String>();
                    periodMap.put("lotteryId",LotteryConstants.DLT);
                    String periodStr = periodNum < 10? ("00" + periodNum) : (periodNum < 100? ("0" + periodNum) : ("" + periodNum));
                    periodMap.put("period",(firstKjCalendar.get(Calendar.YEAR) + periodStr));
                    periodMap.put("sellStartTime",DateUtil.formatDate(lastKjCalendar.getTime(),DateUtil.DEFAULT_DATE_TIME));//开售时间
                    periodMap.put("sellEndTime",DateUtil.formatDate(getLotterySellEndTime(
                            LotteryConstants.DLT,firstKjCalendar).getTime(),DateUtil.DEFAULT_DATE_TIME));//截止时间
                    periodMap.put("drawNumberTime",DateUtil.formatDate(firstKjCalendar.getTime(),DateUtil.DEFAULT_DATE_TIME));//理论开奖时间

                    //官方截止时间,理论开奖时间 - 30分钟
                    Calendar authorityEndCalendar = Calendar.getInstance();
                    authorityEndCalendar.setTime(firstKjCalendar.getTime());
                    authorityEndCalendar.add(Calendar.MINUTE,-30);
                    periodMap.put("authorityEndTime", DateUtil.formatDate(authorityEndCalendar.getTime(),DateUtil.DEFAULT_DATE_TIME));

                    periodList.add(periodMap);
                    periodNum ++;

                    //本期次的开奖日期作为下一期次的开售日期(时间设置为本期次的销售截止时间)
                    lastKjCalendar.setTime(firstKjCalendar.getTime());
                    lastKjCalendar.set(Calendar.HOUR_OF_DAY,LotteryConstants.mpSellEndHour);
                    lastKjCalendar.set(Calendar.MINUTE,LotteryConstants.mpSellEndMinute);
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

        //设置彩票休市时间
        List<LinkedList<Calendar>> closeTimeList = new ArrayList<LinkedList<Calendar>>();
        if(StringUtils.isNotEmpty(lotteryCloseTime) && StringUtils.isNotEmpty(startPeriodStr) && periodNum > 0)
        {
            String[] closeTimes = lotteryCloseTime.split(";");
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

        //获取上一年度最后一次开奖日期
        int startPeriodYear = Integer.parseInt(startPeriodStr.substring(0,4));//截取起始期次所在的年份
        Calendar calendar = Calendar.getInstance();//起始期次所在的日期
        calendar.set(Calendar.YEAR,startPeriodYear);
        Calendar lastYearLastKjCalendar = Calendar.getInstance();
        lastYearLastKjCalendar.set(Calendar.YEAR,calendar.get(Calendar.YEAR) - 1);
        lastYearLastKjCalendar.set(Calendar.MONTH,11);
        lastYearLastKjCalendar.set(Calendar.DAY_OF_MONTH,31);
        for(int i = 0; i < 7; i ++)
        {
            if(lastYearLastKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
                    || lastYearLastKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY
                    || lastYearLastKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
            {
                break;
            }
            lastYearLastKjCalendar.add(Calendar.DAY_OF_MONTH,-1);
        }

        //设置上一年度最后一期次的销售截止时间
        lastYearLastKjCalendar.set(Calendar.HOUR_OF_DAY,LotteryConstants.mpSellEndHour);
        lastYearLastKjCalendar.set(Calendar.MINUTE,LotteryConstants.mpSellEndMinute);
        lastYearLastKjCalendar.set(Calendar.SECOND,0);

        //获取本年第一期次开奖的详细时间
        Calendar firstKjCalendar = getLotteryFirstKjDateOfYear(LotteryConstants.DLT,calendar);
        firstKjCalendar.setTime(DateUtil.parseDate(
                (DateUtil.formatDate(firstKjCalendar.getTime(),DateUtil.DEFAULT_DATE) + " " + LotteryConstants.lotteryEarliestKjTimeMaps.get(LotteryConstants.DLT)),
                DateUtil.DEFAULT_DATE_TIME));

        //获取起始期次的截止时间
        Calendar lastKjCalendar = Calendar.getInstance();
        lastKjCalendar.setTime(lastYearLastKjCalendar.getTime());
        int startPeriod = Integer.parseInt(startPeriodStr.substring(4));
        do
        {
            if(firstKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
                    || firstKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY
                    || firstKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
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
                    startPeriod --;

                    //本期次的开奖日期作为下一期次的开售日期(时间设置为本期次的销售截止时间)
                    lastKjCalendar.setTime(firstKjCalendar.getTime());
                    lastKjCalendar.set(Calendar.HOUR_OF_DAY,LotteryConstants.mpSellEndHour);
                    lastKjCalendar.set(Calendar.MINUTE,LotteryConstants.mpSellEndMinute);
                }
            }
            firstKjCalendar.add(Calendar.DAY_OF_MONTH,1);
        }
        while (startPeriod > 0);

        //生成期次
        int newPeriod = Integer.parseInt(startPeriodStr.substring(4));
        do
        {
            if(firstKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY
                    || firstKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY
                    || firstKjCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
            {
                //遍历彩票休市时间集合,只有该时间不在彩票休市期间,才生成相应的期次
                boolean isCloseTime = false;
                for (LinkedList<Calendar> linkedList : closeTimeList)
                {
                    if (firstKjCalendar.after(linkedList.get(0)) && firstKjCalendar.before(linkedList.get(1)))
                    {
                        isCloseTime = true;
                        break;
                    }
                }
                if (!isCloseTime)
                {
                    Map<String, String> periodMap = new HashMap<String, String>();
                    periodMap.put("lotteryId", LotteryConstants.DLT);

                    //跨年的期次,需要将期次号设置为从1开始
                    if(firstKjCalendar.get(Calendar.YEAR) != startPeriodYear && newPeriod != 1)
                    {
                        startPeriodYear ++;
                        newPeriod = 1;
                    }
                    else
                    {
                        newPeriod ++;
                    }
                    String periodStr = newPeriod < 10 ? ("00" + newPeriod) : (newPeriod < 100 ? ("0" + newPeriod) : ("" + newPeriod));
                    periodMap.put("period", (firstKjCalendar.get(Calendar.YEAR) + periodStr));
                    periodMap.put("sellStartTime", DateUtil.formatDate(lastKjCalendar.getTime(), DateUtil.DEFAULT_DATE_TIME));//开售时间
                    periodMap.put("sellEndTime", DateUtil.formatDate(getLotterySellEndTime(
                            LotteryConstants.DLT, firstKjCalendar).getTime(), DateUtil.DEFAULT_DATE_TIME));//截止时间
                    periodMap.put("drawNumberTime",DateUtil.formatDate(firstKjCalendar.getTime(),DateUtil.DEFAULT_DATE_TIME));//理论开奖时间

                    //官方截止时间,理论开奖时间 - 30分钟
                    Calendar authorityEndCalendar = Calendar.getInstance();
                    authorityEndCalendar.setTime(firstKjCalendar.getTime());
                    authorityEndCalendar.add(Calendar.MINUTE,-30);
                    periodMap.put("authorityEndTime", DateUtil.formatDate(authorityEndCalendar.getTime(),DateUtil.DEFAULT_DATE_TIME));

                    periodList.add(periodMap);
                    periodNum --;

                    //本期次的开奖日期作为下一期次的开售日期(时间设置为本期次的销售截止时间)
                    lastKjCalendar.setTime(firstKjCalendar.getTime());
                    lastKjCalendar.set(Calendar.HOUR_OF_DAY,LotteryConstants.mpSellEndHour);
                    lastKjCalendar.set(Calendar.MINUTE,LotteryConstants.mpSellEndMinute);
                }
            }
            firstKjCalendar.add(Calendar.DAY_OF_MONTH,1);
        }
        while (periodNum > 0);

        return periodList;
    }

    /**
     * 获取默认的开奖号
     * @author  mcdog
     */
    @Override
    public String getDefaultKcodes()
    {
        return "?,?,?,?,?|?,?";
    }

    /**
     * 获取投注选项集合
     * @author  mcdog
     * @param   scheme      方案对象
     * @return  tzxxList    投注选项集合
     */
    @Override
    public List<Dto> getSzcTzxxList(Scheme scheme)
    {
        //解析投注选项
        List<Dto> tzxxList = new ArrayList<Dto>();//用来保存投注选项
        Dto tzxxDto = null;//用来保存单组投注的投注项
        String[] tzContents = scheme.getSchemeContent().split(";");//提取投注内容(可能会有多组投注,所以按";"截取)
        for(String tzContent : tzContents)
        {
            tzxxDto = new BaseDto();
            String[] tzcodes = tzContent.split(":");//提取投注选项

            //设置玩法名称
            String wf = tzcodes[1] + ":" + tzcodes[2];//提取玩法
            tzxxDto.put("wfname","[" + LotteryConstants.playMethodMaps.get(scheme.getLotteryId() + "-" + wf) + "]");//设置玩法名称

            String[] xxcodes = tzcodes[0].split("\\|");
            StringBuilder xxsBuilder = new StringBuilder();//用来保存单组投注选项

            //保存前区胆选项
            String[] qxxcodes = xxcodes[0].split("\\$");
            boolean hasqD = xxcodes[0].indexOf("$") > -1? true : false;//前区是否有胆 true-有 false-无
            StringBuilder tempXxsBuilder = new StringBuilder();
            if(hasqD)
            {
                String[] qdxxcodes = qxxcodes[0].split(",");//提取前区胆选项
                xxsBuilder.append(qdxxcodes.length > 0? "(" : "");
                sortArrayByAsc(qdxxcodes);
                for(String qdxxcode : qdxxcodes)
                {
                    int zstatus = scheme.getSchemeType() == 1? 0 : getSzcMzStatus(scheme.getDrawNumber(),wf,qdxxcode,0,tzcodes[0]);//获取选项命中状态(0-未命中 1-命中)
                    if(zstatus == 0)
                    {
                        tempXxsBuilder.append(" " + qdxxcode);
                    }
                    else
                    {
                        tempXxsBuilder.append(" <font color='#FF0000'>" + qdxxcode + "</font>");
                    }
                }
                xxsBuilder.append(tempXxsBuilder.toString().substring(1));
                xxsBuilder.append(qdxxcodes.length > 0? ") " : "");
            }
            //保存前区拖选项
            tempXxsBuilder = new StringBuilder();
            String[] qtxxcodes = qxxcodes[qxxcodes.length - 1].split(",");//提取前区拖选项
            sortArrayByAsc(qtxxcodes);
            for(String qtxxcode : qtxxcodes)
            {
                int zstatus = scheme.getSchemeType() == 1? 0 : getSzcMzStatus(scheme.getDrawNumber(),wf,qtxxcode,0,tzcodes[0]);//获取选项命中状态(0-未命中 1-命中)
                if(zstatus == 0)
                {
                    tempXxsBuilder.append(" " + qtxxcode);
                }
                else
                {
                    tempXxsBuilder.append(" <font color='#FF0000'>" + qtxxcode + "</font>");
                }
            }
            xxsBuilder.append(tempXxsBuilder.toString().substring(1));

            //保存后区胆选项
            tempXxsBuilder = new StringBuilder();
            xxsBuilder.append(" | ");
            String[] hxxcodes = xxcodes[1].split("\\$");
            boolean hashD = xxcodes[1].indexOf("$") > -1? true : false;//后区是否有胆 true-有 false-无
            if(hashD)
            {
                String[] hdxxcodes = hxxcodes[0].split(",");//提取前区胆选项
                xxsBuilder.append(hdxxcodes.length > 0? "(" : "");
                sortArrayByAsc(hdxxcodes);
                for(String hdxxcode : hdxxcodes)
                {
                    int zstatus = scheme.getSchemeType() == 1? 0 : getSzcMzStatus(scheme.getDrawNumber(),wf,hdxxcode,1,tzcodes[0]);//获取选项命中状态(0-未命中 1-命中)
                    if(zstatus == 0)
                    {
                        tempXxsBuilder.append(" " + hdxxcode);
                    }
                    else
                    {
                        tempXxsBuilder.append(" <font color='#FF0000'>" + hdxxcode + "</font>");
                    }
                }
                xxsBuilder.append(tempXxsBuilder.toString().substring(1));
                xxsBuilder.append(hdxxcodes.length > 0? ") " : "");
            }
            //保存后区拖选项
            tempXxsBuilder = new StringBuilder();
            String[] htxxcodes = hxxcodes[hxxcodes.length - 1].split(",");//提取后区拖选项
            sortArrayByAsc(htxxcodes);
            for(String htxxcode : htxxcodes)
            {
                int zstatus = scheme.getSchemeType() == 1? 0 : getSzcMzStatus(scheme.getDrawNumber(),wf,htxxcode,1,tzcodes[0]);//获取选项命中状态(0-未命中 1-命中)
                if(zstatus == 0)
                {
                    tempXxsBuilder.append(" " + htxxcode);
                }
                else
                {
                    tempXxsBuilder.append(" <font color='#FF0000'>" + htxxcode + "</font>");
                }
            }
            xxsBuilder.append(tempXxsBuilder.toString().substring(1));
            tzxxDto.put("xxs",xxsBuilder.toString());//设置单组投注选项内容
            tzxxList.add(tzxxDto);
        }
        return tzxxList;
    }

    /**
     * 获取投注选项集合
     * @author  mcdog
     * @param   schemeDto   方案对象
     * @return  tzxxList    投注选项集合
     */
    @Override
    public List<Dto> getSzcTzxxList(Dto schemeDto)
    {
        //解析投注选项
        List<Dto> tzxxList = new ArrayList<Dto>();//用来保存投注选项
        Dto tzxxDto = null;//用来保存单组投注的投注项
        int schemeType = schemeDto.getAsInteger("schemeType");//方案类型
        String drawNumber = schemeDto.getAsString("drawNumber");//开奖号
        String[] tzContents = schemeDto.getAsString("schemeContent").split(";");//提取投注内容(可能会有多组投注,所以按";"截取)
        for(String tzContent : tzContents)
        {
            tzxxDto = new BaseDto();
            String[] tzcodes = tzContent.split(":");//提取投注选项

            //设置玩法名称
            String wf = tzcodes[1] + ":" + tzcodes[2];//提取玩法
            tzxxDto.put("wfname","[" + LotteryConstants.playMethodMaps.get(schemeDto.getAsString("lotteryId") + "-" + wf) + "]");//设置玩法名称

            String[] xxcodes = tzcodes[0].split("\\|");
            StringBuilder xxsBuilder = new StringBuilder();//用来保存单组投注选项

            //保存前区胆选项
            String[] qxxcodes = xxcodes[0].split("\\$");
            boolean hasqD = xxcodes[0].indexOf("$") > -1? true : false;//前区是否有胆 true-有 false-无
            StringBuilder tempXxsBuilder = new StringBuilder();
            if(hasqD)
            {
                String[] qdxxcodes = qxxcodes[0].split(",");//提取前区胆选项
                xxsBuilder.append(qdxxcodes.length > 0? "(" : "");
                sortArrayByAsc(qdxxcodes);
                for(String qdxxcode : qdxxcodes)
                {
                    int zstatus = schemeType == 1? 0 : getSzcMzStatus(drawNumber,wf,qdxxcode,0,tzcodes[0]);//获取选项命中状态(0-未命中 1-命中)
                    if(zstatus == 0)
                    {
                        tempXxsBuilder.append(" " + qdxxcode);
                    }
                    else
                    {
                        tempXxsBuilder.append(" <font color='#FF0000'>" + qdxxcode + "</font>");
                    }
                }
                xxsBuilder.append(tempXxsBuilder.toString().substring(1));
                xxsBuilder.append(qdxxcodes.length > 0? ") " : "");
            }
            //保存前区拖选项
            tempXxsBuilder = new StringBuilder();
            String[] qtxxcodes = qxxcodes[qxxcodes.length - 1].split(",");//提取前区拖选项
            sortArrayByAsc(qtxxcodes);
            for(String qtxxcode : qtxxcodes)
            {
                int zstatus = schemeType == 1? 0 : getSzcMzStatus(drawNumber,wf,qtxxcode,0,tzcodes[0]);//获取选项命中状态(0-未命中 1-命中)
                if(zstatus == 0)
                {
                    tempXxsBuilder.append(" " + qtxxcode);
                }
                else
                {
                    tempXxsBuilder.append(" <font color='#FF0000'>" + qtxxcode + "</font>");
                }
            }
            xxsBuilder.append(tempXxsBuilder.toString().substring(1));

            //保存后区胆选项
            tempXxsBuilder = new StringBuilder();
            xxsBuilder.append(" | ");
            String[] hxxcodes = xxcodes[1].split("\\$");
            boolean hashD = xxcodes[1].indexOf("$") > -1? true : false;//后区是否有胆 true-有 false-无
            if(hashD)
            {
                String[] hdxxcodes = hxxcodes[0].split(",");//提取前区胆选项
                xxsBuilder.append(hdxxcodes.length > 0? "(" : "");
                sortArrayByAsc(hdxxcodes);
                for(String hdxxcode : hdxxcodes)
                {
                    int zstatus = schemeType == 1? 0 : getSzcMzStatus(drawNumber,wf,hdxxcode,1,tzcodes[0]);//获取选项命中状态(0-未命中 1-命中)
                    if(zstatus == 0)
                    {
                        tempXxsBuilder.append(" " + hdxxcode);
                    }
                    else
                    {
                        tempXxsBuilder.append(" <font color='#FF0000'>" + hdxxcode + "</font>");
                    }
                }
                xxsBuilder.append(tempXxsBuilder.toString().substring(1));
                xxsBuilder.append(hdxxcodes.length > 0? ") " : "");
            }
            //保存后区拖选项
            tempXxsBuilder = new StringBuilder();
            String[] htxxcodes = hxxcodes[hxxcodes.length - 1].split(",");//提取后区拖选项
            sortArrayByAsc(htxxcodes);
            for(String htxxcode : htxxcodes)
            {
                int zstatus = schemeType == 1? 0 : getSzcMzStatus(drawNumber,wf,htxxcode,1,tzcodes[0]);//获取选项命中状态(0-未命中 1-命中)
                if(zstatus == 0)
                {
                    tempXxsBuilder.append(" " + htxxcode);
                }
                else
                {
                    tempXxsBuilder.append(" <font color='#FF0000'>" + htxxcode + "</font>");
                }
            }
            xxsBuilder.append(tempXxsBuilder.toString().substring(1));
            tzxxDto.put("xxs",xxsBuilder.toString());//设置单组投注选项内容
            tzxxList.add(tzxxDto);
        }
        return tzxxList;
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
        if(StringUtil.isNotEmpty(kcode))
        {
            String[] kcodes = kcode.split("\\|");//截取开奖号
            zstatus = kcodes[index].indexOf(xx) > -1? 1 : 0;
        }
        return zstatus;
    }

    /**
     * 获取方案出票详细信息
     * @author  mcdog
     * @param   scheme        方案信息对象
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
            for(SchemeTicket schemeTicket : ticketList)
            {
                //设置乐善码
                ticketDto = new BaseDto();
                if(StringUtil.isNotEmpty(schemeTicket.getDrawNumber()))
                {
                    if(schemeTicket.getDrawNumber().indexOf("|") < 0)
                    {
                        String[] newlscodes = schemeTicket.getDrawNumber().split(",");
                        schemeTicket.setDrawNumber(newlscodes[0] + "," + newlscodes[1] + ","
                                + newlscodes[2] + "," + newlscodes[3] + "," + newlscodes[4] + "|" + newlscodes[5] + "," + newlscodes[6]);
                    }
                    StringBuilder lscodeBuilder = new StringBuilder();
                    String[] lscodes = schemeTicket.getDrawNumber().split("\\|");
                    String[] lsqxcodes = lscodes[0].split(",");
                    String[] lshxcodes = lscodes[1].split(",");
                    for(String lsqxcode : lsqxcodes)
                    {
                        lscodeBuilder.append(" " + lsqxcode);
                    }
                    lscodeBuilder.append(" |");
                    for(String lshxcode : lshxcodes)
                    {
                        lscodeBuilder.append(" " + lshxcode);
                    }
                    ticketDto.put("lscodes",lscodeBuilder.toString().substring(1));
                }
                else
                {
                    ticketDto.put("lscodes","");
                }
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
            /*String lotteryCloseTime = "2016-02-07 00:00:00~2016-02-13 24:00:00";
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR,2016);
            LotteryUtils lotteryUtils = new Lottery1500Utils();
            List<Map<String,String>> periodList = lotteryUtils.createPeriodsOfYear(lotteryCloseTime,calendar);
            //periodList = createPeriodsByPeriodNum(lotteryCloseTime,"2016150",10);
            for(Map<String,String> periodMap : periodList)
            {
                System.out.println("期次号：" + periodMap.get("period")
                        + "，开售时间：" + periodMap.get("sellStartTime")
                        + "，截止时间：" + periodMap.get("sellEndTime")
                        + "，官方截止时间：" + periodMap.get("authorityEndTime")
                        + "，理论开奖时间：" + periodMap.get("drawNumberTime"));
            }*/
            LotteryUtils lotteryUtils = new Lottery1500Utils();
            Scheme scheme = new Scheme();
            scheme.setDrawNumber("03,04,05,06,07|01,02");
            scheme.setSchemeContent("03,01$04,05,13,18,22,28|05,01:1:2");
            scheme.setSchemeType(0);
            List<Dto> tzxxList = lotteryUtils.getSzcTzxxList(scheme);
            for(Dto dto : tzxxList)
            {
                System.out.print(dto.getAsString("xxs"));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}