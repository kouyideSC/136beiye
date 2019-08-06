package com.caipiao.common.lottery;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.code.ErrorCode;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.match.GyjMatch;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.ticket.SchemeTicket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 猜冠军工具类
 * @author  mcdog
 */
public class Lottery1980Utils extends LotteryUtils
{
    /**
     * 获取投注选项集合
     * @author  mcdog
     * @param   scheme        方案对象
     * @param   matchMaps     方案对阵Map
     * @return  tzxxList      投注选项集合
     */
    @Override
    public List<Dto> getJcTzxxList(Scheme scheme,Map<String,Dto> matchMaps,Dto params)
    {
        /**
         * 解析投注串,并拼接带赔率的投注串
         */
        List<Dto> tzxxList = new ArrayList<Dto>();
        StringBuilder spTzContentBuilder = new StringBuilder();//用来存放带赔率的投注串
        String[] tzcodes = scheme.getSchemeSpContent().split("\\|")[1].split("\\=")[1].split("\\/");
        Dto tzxxDto = null;//投注选项对象
        List<Dto> ccxxList = new ArrayList<Dto>();
        for(String tzcode : tzcodes)
        {
            String mcode = tzcode;
            if(tzcode.indexOf("(") > -1)
            {
                mcode = tzcode.substring(0,tzcode.indexOf("("));
            }
            Dto ccinfoDto = matchMaps.get(mcode);//匹配场次信息
            if(ccinfoDto == null)
            {
                logger.error("[获取投注选项集合]匹配不到猜冠军场次信息!期次号=" + scheme.getPeriod() + ",场次号=" + tzcode);
                continue;
            }
            tzxxDto = new BaseDto();
            int status = getMzStatus(scheme.getDrawNumber(),mcode);//获取选项是否命中
            /*if(status == 1)
            {
                tzxxDto.put("xx","<font color='#FF0000'>" + ccinfoDto.getAsString("teamName") + "</font>");//设置投注选项
            }
            else
            {
                tzxxDto.put("xx",ccinfoDto.getAsString("teamName"));
            }*/
            tzxxDto.put("xx",ccinfoDto.getAsString("teamName"));

            String sp = tzcode.substring(tzcode.indexOf("(") + 1,tzcode.indexOf(")"));//提取赔率
            sp = StringUtil.isEmpty(sp)? sp : String.format("%.2f",Double.parseDouble(sp));
            tzxxDto.put("sp",sp);//设置赔率
            tzxxList.add(tzxxDto);
        }
        return tzxxList;
    }

    /**
     * 获取投注选项集合
     * @author  mcdog
     * @param   scheme        方案Dto
     * @param   matchMaps     方案对阵Map
     * @return  tzxxList      投注选项集合
     */
    @Override
    public List<Dto> getJcTzxxList(Dto scheme,Map<String,Dto> matchMaps,Dto params)
    {
        /**
         * 解析投注串,并拼接带赔率的投注串
         */
        List<Dto> tzxxList = new ArrayList<Dto>();
        StringBuilder spTzContentBuilder = new StringBuilder();//用来存放带赔率的投注串
        String[] tzcodes = scheme.getAsString("schemeSpContent").split("\\|")[1].split("\\=")[1].split("\\/");
        Dto tzxxDto = null;//投注选项对象
        List<Dto> ccxxList = new ArrayList<Dto>();
        for(String tzcode : tzcodes)
        {
            String mcode = tzcode;
            if(tzcode.indexOf("(") > -1)
            {
                mcode = tzcode.substring(0,tzcode.indexOf("("));
            }
            Dto ccinfoDto = matchMaps.get(mcode);//匹配场次信息
            if(ccinfoDto == null)
            {
                logger.error("[获取投注选项集合]匹配不到猜冠军场次信息!期次号=" + scheme.getAsString("period") + ",场次号=" + tzcode);
                continue;
            }
            tzxxDto = new BaseDto();
            int status = getMzStatus(scheme.getAsString("drawNumber"),mcode);//获取选项是否命中
            if(status == 1)
            {
                tzxxDto.put("xx","<font color='#FF0000'>" + ccinfoDto.getAsString("teamName") + "</font>");//设置投注选项
            }
            else
            {
                tzxxDto.put("xx",ccinfoDto.getAsString("teamName"));
            }
            String sp = tzcode.substring(tzcode.indexOf("(") + 1,tzcode.indexOf(")"));//提取赔率
            sp = StringUtil.isEmpty(sp)? sp : String.format("%.2f",Double.parseDouble(sp));
            tzxxDto.put("sp",sp);//设置赔率
            tzxxList.add(tzxxDto);
        }
        return tzxxList;
    }

    /**
     * 获取选项命中状态
     * @author  mcdog
     * @param   kcodes      玩法
     * @param   xx          选项
     * @return  zstatus     命中状态 0-未命中 1-命中
     */
    public int getMzStatus(String kcodes,String xx)
    {
        int zstatus = 0;
        try
        {
           if(StringUtil.isNotEmpty(kcodes))
           {
               return kcodes.equals(xx)? 1 : 0;
           }
        }
        catch (Exception e)
        {
            logger.error("[获取猜冠军选项命中状态]发生异常!选项=" + xx + ",异常信息:" + e);
            zstatus = 0;
        }
        return zstatus;
    }

    /**
     * 获取方案出票详细信息
     * @author  mcdog
     * @param   scheme        方案信息
     * @param   matchMaps     方案对阵Map
     * @param   ticketList    方案出票信息
     * @return  tkinfoList    方案出票详细信息
     */
    @Override
    public List<Dto> getTicketList(Scheme scheme,Map<String,Dto> matchMaps,List<SchemeTicket> ticketList)
    {
        List<Dto> tkinfoList = new ArrayList<Dto>();//用来保存方案出票详细信息
        Dto ticketDto = null;//用来保存单张出票信息
        if(ticketList != null && ticketList.size() > 0)
        {
            Scheme tempScheme = new Scheme();
            tempScheme.setLotteryId(scheme.getLotteryId());
            tempScheme.setLotteryName(scheme.getLotteryName());
            tempScheme.setSchemeType(SchemeConstants.SCHEME_TYPE_PT);
            for(SchemeTicket schemeTicket : ticketList)
            {
                ticketDto = new BaseDto();
                tempScheme.setSchemeContent(schemeTicket.getCodes());
                tempScheme.setSchemeSpContent(schemeTicket.getCodesSp());
                ticketDto.put("xxs",getTicketTzxxs(tempScheme,matchMaps,ticketDto));//设置票单选项
                ticketDto.put("smultiple",schemeTicket.getMultiple() + "倍/单关");//设置票单倍数
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

    /**
     * 获取票单投注选项
     * @author  mcdog
     * @param   scheme      方案对象
     * @param   matchMaps   方案对阵Map
     * @param   ticketDto   票单Dto
     * @return  String      票单投注选项
     */
    public String getTicketTzxxs(Scheme scheme,Map<String,Dto> matchMaps,Dto ticketDto)
    {
        StringBuilder xxsBuilder = new StringBuilder();
        if(StringUtil.isNotEmpty(scheme.getSchemeContent()) && StringUtil.isNotEmpty(scheme.getSchemeSpContent()))
        {
            xxsBuilder.append(scheme.getLotteryName() + "=");
            StringBuilder tempxxsBuilder = new StringBuilder();
            String[] tzcodes = scheme.getSchemeSpContent().split("\\=")[1].split("\\/");//提取带sp的投注内容
            for(String tzcode : tzcodes)
            {
                String[] wfxxs = tzcode.split("\\@");
                Dto ccinfoDto = matchMaps.get(wfxxs[0]);//提取场次信息
                tempxxsBuilder.append("/");
                tempxxsBuilder.append(ccinfoDto.getAsString("teamName"));
                tempxxsBuilder.append("(" + wfxxs[1] + ")");
            }
            xxsBuilder.append(tempxxsBuilder.toString().substring(1));
        }
        return xxsBuilder.toString();
    }
}