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
 * 江苏快三工具类
 * @author  mcdog
 */
public class Lottery1090Utils extends LotteryUtils
{
    /**
     * 一天最大期次数
     */
    public static final int maxPeriodOfDay = 82;
    /**
     * 每天最早开奖时间(小时)
     */
    public static final int earliestKjHour = 8;
    /**
     * 每天最早开奖时间(分钟)
     */
    public static final int earliestKjMinute = 40;

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
                (DateUtil.formatDate(lastYearLastKjCalendar.getTime(),DateUtil.DEFAULT_DATE)
                        + " " + LotteryConstants.lotteryLatestKjTimeMaps.get(LotteryConstants.K3_JS)),
                DateUtil.DEFAULT_DATE_TIME));
        //获取一年中第一次开奖详细时间
        Calendar firstKjCalendar = getLotteryFirstKjDateOfYear(LotteryConstants.K3_JS,calendar);
        firstKjCalendar.setTime(DateUtil.parseDate(
                (DateUtil.formatDate(firstKjCalendar.getTime(),DateUtil.DEFAULT_DATE)
                        + " " + LotteryConstants.lotteryEarliestKjTimeMaps.get(LotteryConstants.K3_JS)),
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
                    periodMap.put("lotteryId",LotteryConstants.K3_JS);
                    String periodStr = j < 10? ("00" + j) : (j < 100? ("0" + j) : ("" + j));
                    periodMap.put("period",(DateUtil.formatDate(firstKjCalendar.getTime(),DateUtil.DEFAULT_DATE1) + periodStr));
                    periodMap.put("sellStartTime",DateUtil.formatDate(lastKjCalendar.getTime(),DateUtil.DEFAULT_DATE_TIME));
                    periodMap.put("sellEndTime",DateUtil.formatDate(getLotterySellEndTime(LotteryConstants.K3_JS,firstKjCalendar).getTime(),DateUtil.DEFAULT_DATE_TIME));
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
     * 获取默认的开奖号
     * @author  mcdog
     */
    @Override
    public String getDefaultKcodes()
    {
        return "?,?,?";
    }

    /**
     * 获取彩投注选项集合
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
            String wf = tzcodes[1] + ":" + tzcodes[2];//提取玩法
            tzxxDto.put("wfname","[" + LotteryConstants.playMethodMaps.get(scheme.getLotteryId() + "-" + wf) + "]");//设置玩法名称

            //三同号通选
            int first = Integer.parseInt(tzcodes[1]);
            if(first == 2)
            {
                StringBuilder xxsBuilder = new StringBuilder();//用来保存单组投注选项
                for(int i = 1; i <= 6; i ++)
                {
                    xxsBuilder.append(" ");
                    String xxcodes = i + "," + i + "," + i;
                    String[] xxs = xxcodes.split(",");
                    StringBuilder tempxxsBuilder = new StringBuilder();
                    for(String xx : xxs)
                    {
                        int zstatus = scheme.getSchemeType() == 1? 0 : getSzcMzStatus(scheme.getDrawNumber(),wf,xx,0,xxcodes);//获取选项命中状态(0-未命中 1-命中)
                        if(zstatus == 0)
                        {
                            tempxxsBuilder.append(xx);
                        }
                        else
                        {
                            tempxxsBuilder.append("<font color='#FF0000'>" + xx + "</font>");
                        }
                    }
                    xxsBuilder.append(tempxxsBuilder.toString());
                }
                tzxxDto.put("xxs",xxsBuilder.toString().substring(1));//设置单组投注选项
            }
            //三连号通选
            else if(first == 5)
            {
                StringBuilder xxsBuilder = new StringBuilder();//用来保存单组投注选项
                for(int i = 1,j = 2,k = 3; k <= 6; i ++,j ++,k ++)
                {
                    xxsBuilder.append(" ");
                    String xxcodes = i + "," + j + "," + k;
                    String[] xxs = xxcodes.split(",");
                    StringBuilder tempxxsBuilder = new StringBuilder();
                    for(int z = 0; z < xxs.length; z ++)
                    {
                        int zstatus = scheme.getSchemeType() == 1? 0 : getSzcMzStatus(scheme.getDrawNumber(),wf,xxs[z],z,xxcodes);//获取选项命中状态(0-未命中 1-命中)
                        if(zstatus == 0)
                        {
                            tempxxsBuilder.append(xxs[z]);
                        }
                        else
                        {
                            tempxxsBuilder.append("<font color='#FF0000'>" + xxs[z] + "</font>");
                        }
                    }
                    xxsBuilder.append(tempxxsBuilder.toString());
                }
                tzxxDto.put("xxs",xxsBuilder.toString().substring(1));//设置单组投注选项
            }
            //两同号复选
            else if(first == 6)
            {
                StringBuilder xxsBuilder = new StringBuilder();//用来保存单组投注选项
                String[] xxs = tzcodes[0].split(",");
                sortArrayByAsc(xxs);
                for(String xx : xxs)
                {
                    int zstatus = scheme.getSchemeType() == 1? 0 : getSzcMzStatus(scheme.getDrawNumber(),wf,xx,0,tzcodes[0]);//获取选项命中状态(0-未命中 1-命中)
                    if(zstatus == 1)
                    {
                        xxsBuilder.append(" <font color='#FF0000'>" + xx + xx + "*</font>");
                    }
                    else
                    {
                        xxsBuilder.append(" " + xx + xx + "*");
                    }
                }
                tzxxDto.put("xxs",xxsBuilder.toString().substring(1));//设置单组投注选项
            }
            //两同号单选
            else if(first == 7)
            {
                StringBuilder xxsBuilder = new StringBuilder();//用来保存单组投注选项
                String[] xxcodes = tzcodes[0].split("\\|");
                String[] thxxs = xxcodes[0].split(",");//提取同号选项
                String[] bthxxs = xxcodes[1].split(",");//提取不同号选项
                sortArrayByAsc(thxxs);
                sortArrayByAsc(bthxxs);
                for(String thxx : thxxs)
                {
                    int zstatus = scheme.getSchemeType() == 1? 0 : getSzcMzStatus(scheme.getDrawNumber(),wf,thxx,0,tzcodes[0]);//获取选项命中状态(0-未命中 1-命中)
                    if(zstatus == 1)
                    {
                        thxx = "<font color='#FF0000'>" + thxx + thxx + "</font>";
                    }
                    else
                    {
                        thxx = thxx + thxx;
                    }
                    for(String bthxx : bthxxs)
                    {
                        zstatus = scheme.getSchemeType() == 1? 0 : getSzcMzStatus(scheme.getDrawNumber(),wf,thxx,1,tzcodes[0]);
                        if(zstatus == 1)
                        {
                            bthxx = "<font color='#FF0000'>" + bthxx + "</font>";
                        }
                        xxsBuilder.append(" " + thxx + bthxx);
                    }
                }
                tzxxDto.put("xxs",xxsBuilder.toString().substring(1));//设置单组投注选项
            }
            //其它玩法
            else
            {
                StringBuilder xxsBuilder = new StringBuilder();//用来保存单组投注选项
                String[] xxs = tzcodes[0].split(",");
                sortArrayByAsc(xxs);
                for(String xx : xxs)
                {
                    int zstatus = scheme.getSchemeType() == 1? 0 : getSzcMzStatus(scheme.getDrawNumber(),wf,xx,0,tzcodes[0]);//获取选项命中状态(0-未命中 1-命中)
                    if(zstatus == 0)
                    {
                        xxsBuilder.append(" " + xx);
                    }
                    else
                    {
                        xxsBuilder.append(" <font color='#FF0000'>" + xx + "</font>");
                    }
                }
                tzxxDto.put("xxs",xxsBuilder.toString().substring(1));//设置单组投注选项
            }
            tzxxList.add(tzxxDto);
        }
        return tzxxList;
    }

    /**
     * 获取彩投注选项集合
     * @author  mcdog
     * @param   schemeDto    方案对象
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
            String wf = tzcodes[1] + ":" + tzcodes[2];//提取玩法
            tzxxDto.put("wfname","[" + LotteryConstants.playMethodMaps.get(schemeDto.getAsString("lotteryId") + "-" + wf) + "]");//设置玩法名称

            //三同号通选
            int first = Integer.parseInt(tzcodes[1]);
            if(first == 2)
            {
                StringBuilder xxsBuilder = new StringBuilder();//用来保存单组投注选项
                for(int i = 1; i <= 6; i ++)
                {
                    xxsBuilder.append(" ");
                    String xxcodes = i + "," + i + "," + i;
                    String[] xxs = xxcodes.split(",");
                    StringBuilder tempxxsBuilder = new StringBuilder();
                    for(String xx : xxs)
                    {
                        int zstatus = schemeType == 1? 0 : getSzcMzStatus(drawNumber,wf,xx,0,xxcodes);//获取选项命中状态(0-未命中 1-命中)
                        if(zstatus == 0)
                        {
                            tempxxsBuilder.append(xx);
                        }
                        else
                        {
                            tempxxsBuilder.append("<font color='#FF0000'>" + xx + "</font>");
                        }
                    }
                    xxsBuilder.append(tempxxsBuilder.toString());
                }
                tzxxDto.put("xxs",xxsBuilder.toString().substring(1));//设置单组投注选项
            }
            //三连号通选
            else if(first == 5)
            {
                StringBuilder xxsBuilder = new StringBuilder();//用来保存单组投注选项
                for(int i = 1,j = 2,k = 3; k <= 6; i ++,j ++,k ++)
                {
                    xxsBuilder.append(" ");
                    String xxcodes = i + "," + j + "," + k;
                    String[] xxs = xxcodes.split(",");
                    StringBuilder tempxxsBuilder = new StringBuilder();
                    for(int z = 0; z < xxs.length; z ++)
                    {
                        int zstatus = schemeType == 1? 0 : getSzcMzStatus(drawNumber,wf,xxs[z],z,xxcodes);//获取选项命中状态(0-未命中 1-命中)
                        if(zstatus == 0)
                        {
                            tempxxsBuilder.append(xxs[z]);
                        }
                        else
                        {
                            tempxxsBuilder.append("<font color='#FF0000'>" + xxs[z] + "</font>");
                        }
                    }
                    xxsBuilder.append(tempxxsBuilder.toString());
                }
                tzxxDto.put("xxs",xxsBuilder.toString().substring(1));//设置单组投注选项
            }
            //两同号复选
            else if(first == 6)
            {
                StringBuilder xxsBuilder = new StringBuilder();//用来保存单组投注选项
                String[] xxs = tzcodes[0].split(",");
                sortArrayByAsc(xxs);
                for(String xx : xxs)
                {
                    int zstatus = schemeType == 1? 0 : getSzcMzStatus(drawNumber,wf,xx,0,tzcodes[0]);//获取选项命中状态(0-未命中 1-命中)
                    if(zstatus == 1)
                    {
                        xxsBuilder.append(" <font color='#FF0000'>" + xx + xx + "*</font>");
                    }
                    else
                    {
                        xxsBuilder.append(" " + xx + xx + "*");
                    }
                }
                tzxxDto.put("xxs",xxsBuilder.toString().substring(1));//设置单组投注选项
            }
            //两同号单选
            else if(first == 7)
            {
                StringBuilder xxsBuilder = new StringBuilder();//用来保存单组投注选项
                String[] xxcodes = tzcodes[0].split("\\|");
                String[] thxxs = xxcodes[0].split(",");//提取同号选项
                String[] bthxxs = xxcodes[1].split(",");//提取不同号选项
                sortArrayByAsc(thxxs);
                sortArrayByAsc(bthxxs);
                for(String thxx : thxxs)
                {
                    int zstatus = schemeType == 1? 0 : getSzcMzStatus(drawNumber,wf,thxx,0,tzcodes[0]);//获取选项命中状态(0-未命中 1-命中)
                    if(zstatus == 1)
                    {
                        thxx = "<font color='#FF0000'>" + thxx + thxx + "</font>";
                    }
                    else
                    {
                        thxx = thxx + thxx;
                    }
                    for(String bthxx : bthxxs)
                    {
                        zstatus = schemeType == 1? 0 : getSzcMzStatus(drawNumber,wf,thxx,1,tzcodes[0]);
                        if(zstatus == 1)
                        {
                            bthxx = "<font color='#FF0000'>" + bthxx + "</font>";
                        }
                        xxsBuilder.append(" " + thxx + bthxx);
                    }
                }
                tzxxDto.put("xxs",xxsBuilder.toString().substring(1));//设置单组投注选项
            }
            //其它玩法
            else
            {
                StringBuilder xxsBuilder = new StringBuilder();//用来保存单组投注选项
                String[] xxs = tzcodes[0].split(",");
                sortArrayByAsc(xxs);
                for(String xx : xxs)
                {
                    int zstatus = schemeType == 1? 0 : getSzcMzStatus(drawNumber,wf,xx,0,tzcodes[0]);//获取选项命中状态(0-未命中 1-命中)
                    if(zstatus == 0)
                    {
                        xxsBuilder.append(" " + xx);
                    }
                    else
                    {
                        xxsBuilder.append(" <font color='#FF0000'>" + xx + "</font>");
                    }
                }
                tzxxDto.put("xxs",xxsBuilder.toString().substring(1));//设置单组投注选项
            }
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
            String[] kcodes = kcode.split(",");
            String[] wfs = wf.split(":");
            int first = Integer.parseInt(wfs[0]);

            //和值
            if(first == 1)
            {
                int plus = 0;
                for(String code : kcodes)
                {
                    plus += Integer.parseInt(code);
                }
                zstatus = xx.indexOf(plus + "") > -1? 1 : 0;
            }
            //三同号通选
            if(first == 2)
            {
                zstatus = kcode.equals(xxcode)? 1 : 0;
            }
            //三同号单选
            else if(first == 3)
            {
                zstatus = kcode.equals(xxcode)? 1 : 0;
            }
            //三不同号
            else if(first == 4)
            {
                //只有开奖形态为三不同号才有命中的意义
                int firstKcode = Integer.parseInt(kcodes[0]);
                int secondKcode = Integer.parseInt(kcodes[1]);
                int thirdKcode = Integer.parseInt(kcodes[2]);
                if((firstKcode != secondKcode && secondKcode != thirdKcode))
                {
                    zstatus = kcode.indexOf(xx) > -1? 1 : 0;
                }
            }
            //三连号通选
            else if(first == 5)
            {
                //只有开奖形态为连号才有命中的意义
                int firstKcode = Integer.parseInt(kcodes[0]);
                int secondKcode = Integer.parseInt(kcodes[1]);
                int thirdKcode = Integer.parseInt(kcodes[2]);
                if((firstKcode + 1 == secondKcode) && (secondKcode + 1 == thirdKcode))
                {
                    zstatus = kcodes[index].indexOf(xx) > -1? 1 : 0;
                }
            }
            //两同号复选
            else if(first == 6)
            {
                //只有开奖形态为两同号才有命中的意义
                int firstKcode = Integer.parseInt(kcodes[0]);
                int secondKcode = Integer.parseInt(kcodes[1]);
                int thirdKcode = Integer.parseInt(kcodes[2]);
                if((firstKcode == secondKcode && firstKcode != thirdKcode)
                        || (secondKcode == thirdKcode && firstKcode != secondKcode))
                {
                    zstatus = kcode.indexOf(xx) > -1? 1 : 0;
                }
            }
            //两同号单选
            else if(first == 7)
            {
                //只有开奖形态为两同号才有命中的意义
                int firstKcode = Integer.parseInt(kcodes[0]);
                int secondKcode = Integer.parseInt(kcodes[1]);
                int thirdKcode = Integer.parseInt(kcodes[2]);
                if((firstKcode == secondKcode && firstKcode != thirdKcode)
                        || (secondKcode == thirdKcode && firstKcode != secondKcode))
                {
                    zstatus = kcode.indexOf(xx) > -1? 1 : 0;
                }
            }
            //两不同号
            else if(first == 8)
            {
                //只有开奖形态为两不同号才有命中的意义
                int firstKcode = Integer.parseInt(kcodes[0]);
                int secondKcode = Integer.parseInt(kcodes[1]);
                int thirdKcode = Integer.parseInt(kcodes[2]);
                if(firstKcode != secondKcode || secondKcode != thirdKcode)
                {
                    if(index == 0)
                    {
                        zstatus = kcode.indexOf(xx) > -1? 1 : 0;
                    }
                    else if(index == 1)
                    {
                        zstatus = kcode.endsWith(xx)? 1 : 0;
                    }
                }
            }
        }
        return zstatus;
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
            LotteryUtils lotteryUtils = new Lottery1090Utils();
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
