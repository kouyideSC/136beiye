package com.caipiao.common.lottery;

import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.ticket.SchemeTicket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 任选九工具类
 * @author  mcdog
 */
public class Lottery1810Utils extends LotteryUtils
{
    /**
     * 获取投注选项集合
     * @author  mcdog
     * @param   scheme      方案对象
     * @param   matchMaps   方案所属期次的对阵
     * @return  tzxxList    投注选项集合
     */
    @Override
    public List<Dto> getZcTzxxList(Scheme scheme, Map<String,Object> matchMaps)
    {
        //提取场次投注选项
        List<Dto> tzxxList = new ArrayList<Dto>();//用来保存场次信息和投注选项
        String[] tzContents = scheme.getSchemeContent().split(":")[0].split("\\$");//默认按有胆拖的情况提取
        String[] dcodes = new String[]{"#","#","#","#","#","#","#","#","#","#","#","#","#","#"};//胆投注选项
        String[] tcodes = tzContents[tzContents.length - 1].split(",");//拖投注选项
        if(tzContents.length >= 2)
        {
            dcodes = tzContents[0].split(",");
        }
        Dto tzxxDto = null;//用来保存单场次信息和投注选项
        if(matchMaps != null && matchMaps.size() > 0)
        {
            //循环读取场次信息
            Map<String,String> matchMap = null;
            for(int i = 1; i <= 14; i ++)
            {
                StringBuilder ccxxsBuilder = new StringBuilder();//用来保存单场次的投注选项
                matchMap = (Map<String,String>)matchMaps.get("" + i);//提取单场次信息
                tzxxDto = new BaseDto();
                tzxxDto.put("xh",matchMap.get("index"));//设置对阵序号
                tzxxDto.put("mname",matchMap.get("matchname"));//设置赛事名称
                tzxxDto.put("score",matchMap.get("score"));//设置比分

                //设置主客队(2个字的球队名称添加空格)
                String hname = matchMap.get("homeTeamView");
                String gname = matchMap.get("awayTeamView");
                hname = StringUtil.isEmpty(hname)? "" : (hname.length() > 4? hname.substring(0,4) :
                        (hname.length() == 2? (hname.substring(0,1) + "　" + hname.substring(1,2)) : hname));
                gname = StringUtil.isEmpty(gname)? "" : (gname.length() > 4? gname.substring(0,4) :
                        (gname.length() == 2? (gname.substring(0,1) + "　" + gname.substring(1,2)) : gname));
                tzxxDto.put("hname",hname);//设置主队名
                tzxxDto.put("gname",gname);//设置客队名

                //设置比赛时间
                tzxxDto.put("mtime",matchMap.get("matchTime"));//设置比赛时间
                if(StringUtil.isNotEmpty(tzxxDto.get("mtime")) && tzxxDto.getAsString("mtime").length() >= 16)
                {
                    tzxxDto.put("mdate",tzxxDto.getAsString("mtime").substring(5,10));//设置比赛时间-只取月日
                    tzxxDto.put("mhour",tzxxDto.getAsString("mtime").substring(11,16));//设置比赛时间-只取时分
                    tzxxDto.put("mtime",tzxxDto.getAsString("mtime").substring(0,16));//设置比赛时间-去掉秒
                }
                //设置比分和比赛状态
                if(StringUtil.isNotEmpty(tzxxDto.get("score")) && tzxxDto.getAsString("score").length() > 1)
                {
                    tzxxDto.put("isend",1);//设置比赛为已结束 0-未结束 1-已结束
                }
                else
                {
                    tzxxDto.put("isend",0);//设置比赛为未结束
                    tzxxDto.put("score","VS");//设置比分为VS
                }
                //设置场次选项

                String xxs = dcodes[i - 1];//默认取胆投注项
                boolean isD = true;//是否为胆,默认是
                if(xxs.equals("#"))
                {
                    isD = false;
                    xxs = tcodes[i - 1];//该场次没设胆则取拖投注项
                }
                int mzcs = 0;
                if(!xxs.equals("#"))
                {
                    for(int j = 0; j < xxs.length(); j ++)
                    {
                        String xname = xxs.substring(j,j + 1);
                        int zstatus = getZcMzStatus(tzxxDto,xname);//获取选项是否命中(0-未命中 1-命中)
                        if(zstatus == 0)
                        {
                            ccxxsBuilder.append(xname);
                        }
                        else
                        {
                            mzcs ++;
                            ccxxsBuilder.append("<font color='#FF0000'>" + xname + "</font>");
                        }
                    }
                }
                ccxxsBuilder.append(isD? ("<font color='#FF0000'>(胆)</font>") : "");
                tzxxDto.put("ccxxs",ccxxsBuilder.toString());//设置场次投注选项
                String cg = "1".equals(tzxxDto.getAsString("isend"))? getCg(tzxxDto) : "";//获取彩果
                tzxxDto.put("xxcg",mzcs > 0? ("<font color='#FF0000'>" + cg + "</font>") : cg);//设置彩果
                tzxxList.add(tzxxDto);
            }
        }
        return tzxxList;
    }

    /**
     * 获取投注选项集合
     * @author  mcdog
     * @param   scheme      方案Dto
     * @param   matchMaps   方案所属期次的对阵
     * @return  tzxxList    投注选项集合
     */
    @Override
    public List<Dto> getZcTzxxList(Dto scheme, Map<String,Object> matchMaps)
    {
        //提取场次投注选项
        List<Dto> tzxxList = new ArrayList<Dto>();//用来保存场次信息和投注选项
        String[] tzContents = scheme.getAsString("schemeContent").split(":")[0].split("\\$");//默认按有胆拖的情况提取
        String[] dcodes = new String[]{"#","#","#","#","#","#","#","#","#","#","#","#","#","#"};//胆投注选项
        String[] tcodes = tzContents[tzContents.length - 1].split(",");//拖投注选项
        if(tzContents.length >= 2)
        {
            dcodes = tzContents[0].split(",");
        }
        Dto tzxxDto = null;//用来保存单场次信息和投注选项
        if(matchMaps != null && matchMaps.size() > 0)
        {
            //循环读取场次信息
            Map<String,String> matchMap = null;
            for(int i = 1; i <= 14; i ++)
            {
                StringBuilder ccxxsBuilder = new StringBuilder();//用来保存单场次的投注选项
                matchMap = (Map<String,String>)matchMaps.get("" + i);//提取单场次信息
                tzxxDto = new BaseDto();
                tzxxDto.put("xh",matchMap.get("index"));//设置对阵序号
                tzxxDto.put("mname",matchMap.get("matchname"));//设置赛事名称
                tzxxDto.put("score",matchMap.get("score"));//设置比分

                //设置主客队(2个字的球队名称添加空格)
                String hname = matchMap.get("homeTeamView");
                String gname = matchMap.get("awayTeamView");
                hname = StringUtil.isEmpty(hname)? "" : (hname.length() > 4? hname.substring(0,4) :
                        (hname.length() == 2? (hname.substring(0,1) + "　" + hname.substring(1,2)) : hname));
                gname = StringUtil.isEmpty(gname)? "" : (gname.length() > 4? gname.substring(0,4) :
                        (gname.length() == 2? (gname.substring(0,1) + "　" + gname.substring(1,2)) : gname));
                tzxxDto.put("hname",hname);//设置主队名
                tzxxDto.put("gname",gname);//设置客队名

                //设置比赛时间
                tzxxDto.put("mtime",matchMap.get("matchTime"));//设置比赛时间
                if(StringUtil.isNotEmpty(tzxxDto.get("mtime")) && tzxxDto.getAsString("mtime").length() >= 16)
                {
                    tzxxDto.put("mdate",tzxxDto.getAsString("mtime").substring(5,10));//设置比赛时间-只取月日
                    tzxxDto.put("mhour",tzxxDto.getAsString("mtime").substring(11,16));//设置比赛时间-只取时分
                    tzxxDto.put("mtime",tzxxDto.getAsString("mtime").substring(0,16));//设置比赛时间-去掉秒
                }
                //设置比分和比赛状态
                if(StringUtil.isNotEmpty(tzxxDto.get("score")) && tzxxDto.getAsString("score").length() > 1)
                {
                    tzxxDto.put("isend",1);//设置比赛为已结束 0-未结束 1-已结束
                }
                else
                {
                    tzxxDto.put("isend",0);//设置比赛为未结束
                    tzxxDto.put("score","--");//设置比分为VS
                }
                //设置场次选项

                String xxs = dcodes[i - 1];//默认取胆投注项
                boolean isD = true;//是否为胆,默认是
                if(xxs.equals("#"))
                {
                    isD = false;
                    xxs = tcodes[i - 1];//该场次没设胆则取拖投注项
                }
                int mzcs = 0;
                if(!xxs.equals("#"))
                {
                    for(int j = 0; j < xxs.length(); j ++)
                    {
                        String xname = xxs.substring(j,j + 1);
                        int zstatus = getZcMzStatus(tzxxDto,xname);//获取选项是否命中(0-未命中 1-命中)
                        if(zstatus == 0)
                        {
                            ccxxsBuilder.append(xname);
                        }
                        else
                        {
                            mzcs ++;
                            ccxxsBuilder.append("<font color='#FF0000'>" + xname + "</font>");
                        }
                    }
                }
                ccxxsBuilder.append(isD? ("<font color='#FF0000'>(胆)</font>") : "");
                tzxxDto.put("ccxxs",ccxxsBuilder.toString());//设置场次投注选项
                String cg = "1".equals(tzxxDto.getAsString("isend"))? getCg(tzxxDto) : "--";//获取彩果
                tzxxDto.put("xxcg",mzcs > 0? ("<font color='#FF0000'>" + cg + "</font>") : cg);//设置彩果
                tzxxList.add(tzxxDto);
            }
        }
        return tzxxList;
    }

    /**
     * 获取选项命中状态
     * @author  mcdog
     * @param   ccinfoDto   场次信息(包含赛果)
     * @param   xx          选项
     * @return  zstatus     命中状态 0-未命中 1-命中
     */
    @Override
    public int getZcMzStatus(Dto ccinfoDto, String xx)
    {
        int zstatus = 0;
        try
        {
            if(StringUtil.isEmpty(ccinfoDto.get("score")) || ccinfoDto.getAsString("score").length() < 2)
            {
                return 0;
            }
            String[] scores = ccinfoDto.getAsString("score").split(":");
            int hscore = Integer.parseInt(scores[0]);//主队得分
            int gscore = Integer.parseInt(scores[1]);//客队得分
            int result = hscore > gscore? 3 : (hscore == gscore? 1 : 0);//比赛结果 3-主胜 1-平 0-主负
            zstatus = (result == Integer.parseInt(xx))? 1 : 0;
        }
        catch (Exception e)
        {
            logger.error("获取任选九选项命中状态发生异常,场次信息:" + ccinfoDto.toString() + ",选项:" + xx + ",异常信息:" + e);
            zstatus = 0;
        }
        return zstatus;
    }

    /**
     * 获取默认的开奖号
     * @author  mcdog
     */
    @Override
    public String getDefaultKcodes()
    {
        return "?,?,?,?,?,?,?,?,?,?,?,?,?,?";
    }

    /**
     * 获取彩果
     * @author  mcdog
     * @param   ccinfoDto   场次信息(包含赛果)
     */
    public String getCg(Dto ccinfoDto)
    {
        String cg = "";
        try
        {
            if(StringUtil.isEmpty(ccinfoDto.get("score")) || ccinfoDto.getAsString("score").length() < 2)
            {
                return cg;
            }
            String[] scores = ccinfoDto.getAsString("score").split(":");
            int hscore = Integer.parseInt(scores[0]);//主队得分
            int gscore = Integer.parseInt(scores[1]);//客队得分
            cg = hscore > gscore? "3" : (hscore == gscore? "1" : "0");//比赛结果 3-主胜 1-平 0-主负
        }
        catch (Exception e)
        {
            logger.error("获取胜负彩彩果发生异常!场次信息:" + ccinfoDto.toString() + ",异常信息:" + e);
        }
        return cg;
    }

    /**
     * 获取方案出票详细信息
     * @author  mcdog
     * @param   scheme        方案信息对象
     * @param   ticketList    方案出票信息
     * @return  tkinfoList    方案出票详细信息
     */
    @Override
    public List<Dto> getTicketList(Scheme scheme,List<SchemeTicket> ticketList)
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
}