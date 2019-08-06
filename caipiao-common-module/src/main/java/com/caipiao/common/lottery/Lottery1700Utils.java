package com.caipiao.common.lottery;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.lottery.Lottery;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.ticket.SchemeTicket;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * 竞彩足球工具类
 * @author  mcdog
 */
public class Lottery1700Utils extends LotteryUtils
{
    /**
     * 获取投注选项集合
     * @author  mcdog
     * @param   scheme      方案对象
     * @param   matchMaps   方案对阵Map
     * @return  tzxxList    投注选项集合
     */
    @Override
    public List<Dto> getJcTzxxList(Scheme scheme,Map<String,Dto> matchMaps,Dto params)
    {
        String[] tzspContent = scheme.getSchemeSpContent().split("\\|");//提取带sp的投注内容

        /**
         * 设置投注选项
         */
        Dto tzxxDto = null;//投注选项对象
        List<Dto> tzxxList = new ArrayList<Dto>();//投注选项集合
        Dto ccxxDto = null;//场次选项对象
        List<Dto> ccxxList = null;//场次选项集合
        Dto ccinfoDto = null;//场次信息对象
        boolean hasD = tzspContent[1].indexOf("$") > -1? true : false;//是否有设胆,true表示有
        boolean ishh = tzspContent[0].indexOf(LotteryConstants.JCWF_PREFIX_HH) > -1? true : false;//是否混投,true表示是混投

        //设置非混投玩法名称及盘口
        String tzprefix = tzspContent[0].indexOf("(") > -1? (tzspContent[0].substring(0,tzspContent[0].indexOf("("))) : tzspContent[0];//投注项前缀
        String tzwfname = LotteryConstants.playMethodMaps.get(tzprefix);//根据玩法前缀获取玩法名称

        //重新排序,按场次号的大小从小到大排序(场次选项也需要按从小到大排序)
        Map<String,Dto> ccxxMaps = new TreeMap<String,Dto>();
        Dto dccxxDto = null;
        String[] tzcodes = tzspContent[1].split("\\$");//提取投注选项(考虑有设胆的情况,所以用$分割)
        if(hasD)
        {
            String[] dccxxcodes = tzcodes[0].split(",");
            if(ishh)
            {
                for(String ccxxcode : dccxxcodes)
                {
                    String[] tempcodes = ccxxcode.split("\\>");//提取场次投注选项
                    dccxxDto = new BaseDto("mcode",tempcodes[0]);
                    dccxxDto.put("isd",1);//是否为胆(0-不是 1-是)
                    dccxxDto.put("tzcodes",ccxxcode);//设置场次选项
                    ccxxMaps.put(tempcodes[0],dccxxDto);
                }
            }
            else
            {
                for(String ccxxcode : dccxxcodes)
                {
                    String[] tempcodes = ccxxcode.split("\\=");//提取场次投注选项
                    dccxxDto = new BaseDto("mcode",tempcodes[0]);
                    dccxxDto.put("isd",1);//是否为胆(0-不是 1-是)
                    dccxxDto.put("tzcodes",tempcodes[1]);//设置场次选项
                    String mcode = tempcodes[0];
                    if(mcode.indexOf("(") > -1)
                    {
                        String pankou = mcode.substring(mcode.indexOf("(") + 1,mcode.lastIndexOf(")"));
                        dccxxDto.put("pankou",pankou);
                        dccxxDto.put("mcode",mcode.substring(0,mcode.indexOf("(")));
                    }
                    ccxxMaps.put(dccxxDto.getAsString("mcode"),dccxxDto);
                }
            }
        }
        String[] ccxxcodes = tzcodes[tzcodes.length - 1].split(",");
        if(ishh)
        {
            for(String ccxxcode : ccxxcodes)
            {
                String[] tempcodes = ccxxcode.split("\\>");//提取场次投注选项
                dccxxDto = new BaseDto("mcode",tempcodes[0]);
                dccxxDto.put("isd",0);//是否为胆(0-不是 1-是)
                dccxxDto.put("tzcodes",ccxxcode);//设置场次选项
                ccxxMaps.put(tempcodes[0],dccxxDto);
            }
        }
        else
        {
            for(String ccxxcode : ccxxcodes)
            {
                String[] tempcodes = ccxxcode.split("\\=");//提取场次投注选项
                dccxxDto = new BaseDto("mcode",tempcodes[0]);
                dccxxDto.put("isd",0);//是否为胆(0-不是 1-是)
                dccxxDto.put("tzcodes",tempcodes[1]);//设置场次选项
                String mcode = tempcodes[0];
                if(mcode.indexOf("(") > -1)
                {
                    String pankou = mcode.substring(mcode.indexOf("(") + 1,mcode.lastIndexOf(")"));
                    dccxxDto.put("pankou",pankou);
                    dccxxDto.put("mcode",mcode.substring(0,mcode.indexOf("(")));
                }
                ccxxMaps.put(dccxxDto.getAsString("mcode"),dccxxDto);
            }
        }

        //解析投注选项
        for(String key : ccxxMaps.keySet())
        {
            //设置投注场次信息
            tzxxDto = new BaseDto();
            Dto ccxxData = ccxxMaps.get(key);//获取单场次投注数据对象
            ccinfoDto = matchMaps.get(key);//获取场次信息
            tzxxDto.put("isd",ccxxData.getAsInteger("isd"));//设置是否为胆 0-不是 1-是
            tzxxDto.put("week",ccinfoDto.getAsString("weekday") + ccinfoDto.getAsString("jcId"));//设置周信息

            //设置联赛名称
            String lname = ccinfoDto.getAsString("leagueName");
            tzxxDto.put("lname",StringUtil.isEmpty(lname)? "" : (lname.length() > 4? lname.substring(0,4) : lname));

            //设置主客队
            String hname = ccinfoDto.getAsString("hostName");
            String gname = ccinfoDto.getAsString("guestName");
            hname = StringUtil.isEmpty(hname)? "" : (hname.length() > 4? hname.substring(0,4) : hname);
            gname = StringUtil.isEmpty(gname)? "" : (gname.length() > 4? gname.substring(0,4) : gname);
            tzxxDto.put("hname",hname);//设置主队名
            tzxxDto.put("gname",gname);//设置客队名

            //设置比赛时间
            tzxxDto.put("mtime",ccinfoDto.getAsString("matchTime"));
            if(StringUtil.isNotEmpty(tzxxDto.get("mtime")) && tzxxDto.getAsString("mtime").length() >= 19)
            {
                tzxxDto.put("mhour",tzxxDto.getAsString("mtime").substring(11,16));//设置比赛时间简称(只保留时和分)
                tzxxDto.put("mtime",tzxxDto.getAsString("mtime").substring(5,16));//设置比赛时间(去掉年,去掉秒)
            }
            //设置比赛结果
            String hscore = ccinfoDto.getAsString("halfScore");
            String score = ccinfoDto.getAsString("score");
            tzxxDto.put("hscore",hscore);//设置半场比分
            if(StringUtil.isEmpty(score))
            {
                tzxxDto.put("score","VS");//设置全场比分
            }
            else
            {
                tzxxDto.put("score",score + (StringUtil.isEmpty(hscore)? "" : ("（" + hscore + "）")));//设置比分
            }
            tzxxDto.put("isend",StringUtil.isEmpty(tzxxDto.get("score"))? 0 : 1);//设置比赛是否结束 0-未结束 1-已结束

            /**
             * 设置即时比分
             */
            if(StringUtil.isNotEmpty(score))
            {
                tzxxDto.put("bscore",score + (StringUtil.isEmpty(hscore)? "" : ("(" + hscore + ")")));
                tzxxDto.put("bstime","");
            }
            else
            {
                Integer matchState = ccinfoDto.getAsInteger("matchState");//提取场次比赛状态
                if(matchState == null)
                {
                    tzxxDto.put("bscore",score + (StringUtil.isEmpty(hscore)? "" : ("(" + hscore + ")")));
                    tzxxDto.put("bstime","");
                }
                else
                {
                    //未开
                    if(matchState == 0)
                    {
                        tzxxDto.put("bscore","未开赛");
                        tzxxDto.put("bstime","");//设置比赛进行时间
                    }
                    //上半场
                    else if(matchState == 1)
                    {
                        tzxxDto.put("bscore",ccinfoDto.get("homeScore") + ":" + ccinfoDto.get("guestScore"));
                        String bstime = "";
                        String beginTime = ccinfoDto.getAsString("beginTime");
                        if(StringUtil.isNotEmpty(beginTime))
                        {
                            Calendar beginCalendar = DateUtil.parseCalendar(beginTime,DateUtil.DEFAULT_DATE_TIME);//比赛开始时间
                            Calendar currentCalendar = Calendar.getInstance();
                            int minute = DateUtil.minutesBetween(beginCalendar.getTime(),currentCalendar.getTime());//计算开赛时间与当前时间的分差数
                            bstime = minute > 120? "" : (minute > 45? "45" : (minute + ""));
                        }
                        tzxxDto.put("bstime",bstime);//设置比赛进行时间
                    }
                    //中场
                    else if(matchState == 2)
                    {
                        tzxxDto.put("bscore",(ccinfoDto.get("homeScore") + ":" + ccinfoDto.get("guestScore") + "(" + (ccinfoDto.get("homeHalfScore") + ":" + ccinfoDto.get("guestHalfScore")) + ")"));
                        String bstime = "中";
                        String beginTime = ccinfoDto.getAsString("beginTime");
                        if(StringUtil.isNotEmpty(beginTime))
                        {
                            Calendar beginCalendar = DateUtil.parseCalendar(beginTime,DateUtil.DEFAULT_DATE_TIME);//比赛开始时间
                            Calendar currentCalendar = Calendar.getInstance();
                            int minute = DateUtil.minutesBetween(beginCalendar.getTime(),currentCalendar.getTime());//计算开赛时间与当前时间的分差数
                            bstime = minute > 120? "" : "45";
                        }
                        tzxxDto.put("bstime",bstime);//设置比赛进行时间
                    }
                    //下半场
                    else if(matchState == 3)
                    {
                        tzxxDto.put("bscore",(ccinfoDto.get("homeScore") + ":" + ccinfoDto.get("guestScore") + "(" + (ccinfoDto.get("homeHalfScore") + ":" + ccinfoDto.get("guestHalfScore")) + ")"));
                        String bstime = "";
                        String beginTime = ccinfoDto.getAsString("beginTime");
                        if(StringUtil.isNotEmpty(beginTime))
                        {
                            Calendar beginCalendar = DateUtil.parseCalendar(beginTime,DateUtil.DEFAULT_DATE_TIME);//比赛开始时间
                            Calendar currentCalendar = Calendar.getInstance();
                            int minute = DateUtil.minutesBetween(beginCalendar.getTime(),currentCalendar.getTime());//计算开赛时间与当前时间的分差数
                            minute = minute + 45;
                            bstime = minute > 120? "" : (minute > 90? "90" : (minute + ""));
                        }
                        tzxxDto.put("bstime",bstime);//设置比赛进行时间
                    }
                    //加时
                    else if(matchState == 4)
                    {
                        tzxxDto.put("bscore",(ccinfoDto.get("homeScore") + ":" + ccinfoDto.get("guestScore") + "(" + (ccinfoDto.get("homeHalfScore") + ":" + ccinfoDto.get("guestHalfScore")) + ")"));
                        tzxxDto.put("bstime","");
                    }
                    //待定
                    else if(matchState == -11)
                    {
                        tzxxDto.put("bscore","<font color='#FF0000'>待定</font>");
                        tzxxDto.put("bscore","待定");
                        tzxxDto.put("bstime","");
                    }
                    //腰斩
                    else if(matchState == -12)
                    {
                        tzxxDto.put("bscore","<font color='#FF0000'>腰斩</font>");
                        tzxxDto.put("bscore","腰斩");
                        tzxxDto.put("bstime","");
                    }
                    //中断
                    else if(matchState == -13)
                    {
                        tzxxDto.put("bscore","<font color='#FF0000'>中断</font>");
                        tzxxDto.put("bscore","中断");
                        tzxxDto.put("bstime","");
                    }
                    //推迟
                    else if(matchState == -14)
                    {
                        tzxxDto.put("bscore","<font color='#FF0000'>推迟</font>");
                        tzxxDto.put("bscore","推迟");
                        tzxxDto.put("bstime","");
                    }
                    //完场
                    else if(matchState == -1)
                    {
                        tzxxDto.put("bscore",(ccinfoDto.get("homeScore") + ":" + ccinfoDto.get("guestScore") + "(" + (ccinfoDto.get("homeHalfScore") + ":" + ccinfoDto.get("guestHalfScore")) + ")"));
                        tzxxDto.put("bstime","");
                    }
                    //取消
                    else if(matchState == -10)
                    {
                        tzxxDto.put("bscore","<font color='#FF0000'>取消</font>");
                        tzxxDto.put("bscore","取消");
                        tzxxDto.put("bstime","");
                    }
                    else
                    {
                        tzxxDto.put("bscore","--");
                        tzxxDto.put("bstime","");//设置比赛进行时间为空
                    }
                }
            }

            /**
             * 设置投注选项信息
             */
            //设置混投投注选项信息
            if(ishh)
            {
                ccxxList = new ArrayList<Dto>();
                String[] ccxxs = ccxxData.getAsString("tzcodes").split(">");//获取单场次投注选项
                String[] xxs = ccxxs[1].split("\\+");
                for(String xx : xxs)
                {
                    //设置玩法名称及盘口
                    String[] wfxx = xx.split("=");//提取玩法及选项
                    ccxxDto = new BaseDto();
                    String wfprefix = wfxx[0].indexOf("(") > -1? (wfxx[0].substring(0,wfxx[0].indexOf("("))) : wfxx[0];
                    String wfname = LotteryConstants.playMethodMaps.get(wfprefix);//根据玩法前缀获取玩法名称
                    if(wfxx[0].indexOf("(") > -1)
                    {
                        String pankou = wfxx[0].substring((wfxx[0].indexOf("(") + 1),wfxx[0].indexOf(")"));
                        if(!LotteryConstants.JCWF_PREFIX_DXF.equals(wfprefix))
                        {
                            pankou = pankou.indexOf("-") > -1? pankou : ("+" + pankou);
                        }
                        wfname += "(" + pankou + ")";
                    }
                    ccxxDto.put("wfname",wfname);//设置玩法名称

                    //设置选项
                    StringBuilder xxsBuilder = new StringBuilder();//选项字符串
                    String[] tempxxs = wfxx[1].split("/");
                    if(wfprefix.equals(LotteryConstants.JCWF_PREFIX_JQS)
                            || wfprefix.equals(LotteryConstants.JCWF_PREFIX_CBF))
                    {
                        sortArrayWithSpByAsc(tempxxs);//进球数/猜比分玩法,选项升序排列
                    }
                    else
                    {
                        sortArrayWithSpByDesc(tempxxs);//其它玩法,选项降序排列
                    }
                    int mzcs = 0;//命中次数
                    for(String tempxx : tempxxs)
                    {
                        String realxx = tempxx.indexOf("(") > -1? (tempxx.substring(0,tempxx.indexOf("("))) : tempxx;
                        String xname = LotteryConstants.jcXxNameMaps.get(wfprefix + realxx);
                        if(tempxx.indexOf("(") > -1)
                        {
                            String sp = tempxx.substring(tempxx.indexOf("("));
                            xname += sp;
                        }
                        //拼接选项
                        int zstatus = getJcMzStatus(ccinfoDto,wfxx[0],realxx);//获取选项命中状态(0-未命中 1-命中)
                        if(zstatus == 0)
                        {
                            xxsBuilder.append(xname + " ");
                        }
                        else
                        {
                            xxsBuilder.append("<font color='#FF0000'>" + xname + "</font>" + " ");
                        }
                        mzcs += zstatus;

                        //保存玩法选项赔率信息
                        String realwf = wfxx[0];
                        realwf = realwf.indexOf("(") > -1? realwf.substring(0,realwf.indexOf("(")) : realwf;
                        params.put((ccinfoDto.getAsString("matchCode") + realwf + realxx),tempxx.substring(tempxx.indexOf("(") + 1,tempxx.lastIndexOf(")")));
                    }
                    ccxxDto.put("xxs",xxsBuilder.toString());//设置选项信息
                    String wfcg = getJcWfcg(ccinfoDto,wfxx[0]);//获取玩法彩果
                    ccxxDto.put("xxcg",StringUtil.isEmpty(wfcg)? "" : (mzcs > 0? ("<font color='#FF0000'>" + wfcg + "</font>") : wfcg));//设置玩法彩果
                    ccxxList.add(ccxxDto);
                }
            }
            //设置非混投投注选项信息
            else
            {
                //设置玩法及盘口
                ccxxList = new ArrayList<Dto>();
                ccxxDto = new BaseDto();
                String realWfname = tzwfname;
                String pankou = ccxxData.getAsString("pankou");
                if(StringUtil.isNotEmpty(pankou))
                {
                    pankou = pankou.indexOf("-") > -1? pankou : ("+" + pankou);
                    pankou = "(" + pankou + ")";
                    realWfname += pankou;
                }
                ccxxDto.put("wfname",realWfname);//设置玩法名称

                //设置选项
                StringBuilder xxsBuilder = new StringBuilder();//选项字符串
                String[] tempxxs = ccxxData.getAsString("tzcodes").split("/");
                if(tzprefix.equals(LotteryConstants.JCWF_PREFIX_JQS)
                        || tzprefix.equals(LotteryConstants.JCWF_PREFIX_CBF))
                {
                    sortArrayWithSpByAsc(tempxxs);//进球数/猜比分玩法,选项升序排列
                }
                else
                {
                    sortArrayWithSpByDesc(tempxxs);//其它玩法,选项降序排列
                }
                int mzcs = 0;//命中次数
                for(String tempxx : tempxxs)
                {
                    String realxx = tempxx.indexOf("(") > -1? (tempxx.substring(0,tempxx.indexOf("("))) : tempxx;
                    String xname = LotteryConstants.jcXxNameMaps.get(tzprefix + realxx);
                    if(tempxx.indexOf("(") > -1)
                    {
                        xname += tempxx.substring(tempxx.indexOf("("));
                    }
                    //拼接选项
                    int zstatus = getJcMzStatus(ccinfoDto,(tzprefix + pankou),realxx);//获取选项命中状态(0-未命中 1-命中)
                    if(zstatus == 0)
                    {
                        xxsBuilder.append(xname + " ");
                    }
                    else
                    {
                        xxsBuilder.append("<font color='#FF0000'>" + xname + "</font>" + " ");
                    }
                    mzcs += zstatus;

                    //保存玩法选项赔率信息
                    String realwf = tzprefix;
                    realwf = realwf.indexOf("(") > -1? realwf.substring(0,realwf.indexOf("(")) : realwf;
                    params.put((ccinfoDto.getAsString("matchCode") + realwf + realxx),tempxx.substring(tempxx.indexOf("(") + 1,tempxx.lastIndexOf(")")));
                }
                ccxxDto.put("xxs",xxsBuilder.toString());//设置选项信息
                String wfcg = getJcWfcg(ccinfoDto,(tzprefix + pankou));//获取玩法彩果
                ccxxDto.put("xxcg",StringUtil.isEmpty(wfcg)? "" : (mzcs > 0? ("<font color='#FF0000'>" + wfcg + "</font>") : wfcg));//设置玩法彩果
                ccxxList.add(ccxxDto);
            }
            tzxxDto.put("ccxxs",ccxxList);//设置场次选项信息
            tzxxList.add(tzxxDto);
        }
        return tzxxList;
    }

    /**
     * 获取投注选项集合
     * @author  mcdog
     * @param   scheme      方案Dto
     * @param   matchMaps   方案对阵Map
     * @return  tzxxList    投注选项集合
     */
    @Override
    public List<Dto> getJcTzxxList(Dto scheme,Map<String,Dto> matchMaps,Dto params)
    {
        String[] tzspContent = scheme.getAsString("schemeSpContent").split("\\|");//提取带sp的投注内容

        /**
         * 设置投注选项
         */
        Dto tzxxDto = null;//投注选项对象
        List<Dto> tzxxList = new ArrayList<Dto>();//投注选项集合
        Dto ccxxDto = null;//场次选项对象
        List<Dto> ccxxList = null;//场次选项集合
        Dto ccinfoDto = null;//场次信息对象
        boolean hasD = tzspContent[1].indexOf("$") > -1? true : false;//是否有设胆,true表示有
        boolean ishh = tzspContent[0].indexOf(LotteryConstants.JCWF_PREFIX_HH) > -1? true : false;//是否混投,true表示是混投

        //设置非混投玩法名称及盘口
        String tzprefix = tzspContent[0].indexOf("(") > -1? (tzspContent[0].substring(0,tzspContent[0].indexOf("("))) : tzspContent[0];//投注项前缀
        String tzwfname = LotteryConstants.playMethodMaps.get(tzprefix);//根据玩法前缀获取玩法名称

        //重新排序,按场次号的大小从小到大排序(场次选项也需要按从小到大排序)
        Map<String,Dto> ccxxMaps = new TreeMap<String,Dto>();
        Dto dccxxDto = null;
        String[] tzcodes = tzspContent[1].split("\\$");//提取投注选项(考虑有设胆的情况,所以用$分割)
        if(hasD)
        {
            String[] dccxxcodes = tzcodes[0].split(",");
            if(ishh)
            {
                for(String ccxxcode : dccxxcodes)
                {
                    String[] tempcodes = ccxxcode.split("\\>");//提取场次投注选项
                    dccxxDto = new BaseDto("mcode",tempcodes[0]);
                    dccxxDto.put("isd",1);//是否为胆(0-不是 1-是)
                    dccxxDto.put("tzcodes",ccxxcode);//设置场次选项
                    ccxxMaps.put(tempcodes[0],dccxxDto);
                }
            }
            else
            {
                for(String ccxxcode : dccxxcodes)
                {
                    String[] tempcodes = ccxxcode.split("\\=");//提取场次投注选项
                    dccxxDto = new BaseDto("mcode",tempcodes[0]);
                    dccxxDto.put("isd",1);//是否为胆(0-不是 1-是)
                    dccxxDto.put("tzcodes",tempcodes[1]);//设置场次选项
                    String mcode = tempcodes[0];
                    if(mcode.indexOf("(") > -1)
                    {
                        String pankou = mcode.substring(mcode.indexOf("(") + 1,mcode.lastIndexOf(")"));
                        dccxxDto.put("pankou",pankou);
                        dccxxDto.put("mcode",mcode.substring(0,mcode.indexOf("(")));
                    }
                    ccxxMaps.put(dccxxDto.getAsString("mcode"),dccxxDto);
                }
            }
        }
        String[] ccxxcodes = tzcodes[tzcodes.length - 1].split(",");
        if(ishh)
        {
            for(String ccxxcode : ccxxcodes)
            {
                String[] tempcodes = ccxxcode.split("\\>");//提取场次投注选项
                dccxxDto = new BaseDto("mcode",tempcodes[0]);
                dccxxDto.put("isd",0);//是否为胆(0-不是 1-是)
                dccxxDto.put("tzcodes",ccxxcode);//设置场次选项
                ccxxMaps.put(tempcodes[0],dccxxDto);
            }
        }
        else
        {
            for(String ccxxcode : ccxxcodes)
            {
                String[] tempcodes = ccxxcode.split("\\=");//提取场次投注选项
                dccxxDto = new BaseDto("mcode",tempcodes[0]);
                dccxxDto.put("isd",0);//是否为胆(0-不是 1-是)
                dccxxDto.put("tzcodes",tempcodes[1]);//设置场次选项
                String mcode = tempcodes[0];
                if(mcode.indexOf("(") > -1)
                {
                    String pankou = mcode.substring(mcode.indexOf("(") + 1,mcode.lastIndexOf(")"));
                    dccxxDto.put("pankou",pankou);
                    dccxxDto.put("mcode",mcode.substring(0,mcode.indexOf("(")));
                }
                ccxxMaps.put(dccxxDto.getAsString("mcode"),dccxxDto);
            }
        }

        //解析投注选项
        for(String key : ccxxMaps.keySet())
        {
            //设置投注场次信息
            tzxxDto = new BaseDto();
            Dto ccxxData = ccxxMaps.get(key);//获取单场次投注数据对象
            ccinfoDto = matchMaps.get(key);//获取场次信息
            tzxxDto.put("isd",ccxxData.getAsInteger("isd"));//设置是否为胆 0-不是 1-是
            tzxxDto.put("week",ccinfoDto.getAsString("weekday") + ccinfoDto.getAsString("jcId"));//设置周信息

            //设置联赛名称
            String lname = ccinfoDto.getAsString("leagueName");
            tzxxDto.put("lname",StringUtil.isEmpty(lname)? "" : (lname.length() > 4? lname.substring(0,4) : lname));

            //设置主客队
            String hname = ccinfoDto.getAsString("hostName");
            String gname = ccinfoDto.getAsString("guestName");
            hname = StringUtil.isEmpty(hname)? "" : (hname.length() > 4? hname.substring(0,4) : hname);
            gname = StringUtil.isEmpty(gname)? "" : (gname.length() > 4? gname.substring(0,4) : gname);
            tzxxDto.put("hname",hname);//设置主队名
            tzxxDto.put("gname",gname);//设置客队名

            //设置比赛时间
            tzxxDto.put("mtime",ccinfoDto.getAsString("matchTime"));
            if(StringUtil.isNotEmpty(tzxxDto.get("mtime")) && tzxxDto.getAsString("mtime").length() >= 19)
            {
                tzxxDto.put("mhour",tzxxDto.getAsString("mtime").substring(11,16));//设置比赛时间简称(只保留时和分)
                tzxxDto.put("mtime",tzxxDto.getAsString("mtime").substring(5,16));//设置比赛时间(去掉年,去掉秒)
            }
            //设置比赛结果
            String hscore = ccinfoDto.getAsString("halfScore");
            String score = ccinfoDto.getAsString("score");
            tzxxDto.put("hscore",hscore);//设置半场比分
            if(StringUtil.isEmpty(score))
            {
                tzxxDto.put("score","");//设置全场比分
            }
            else
            {
                tzxxDto.put("score",score + (StringUtil.isEmpty(hscore)? "" : ("（" + hscore + "）")));//设置比分
            }
            tzxxDto.put("isend",StringUtil.isEmpty(tzxxDto.get("score"))? 0 : 1);//设置比赛是否结束 0-未结束 1-已结束

            /**
             * 设置即时比分
             */
            if(StringUtil.isNotEmpty(score))
            {
                tzxxDto.put("bscore",score + (StringUtil.isEmpty(hscore)? "" : ("(" + hscore + ")")));
                tzxxDto.put("bstime","");
            }
            else
            {
                Integer matchState = ccinfoDto.getAsInteger("matchState");//提取场次比赛状态
                if(matchState == null)
                {
                    tzxxDto.put("bscore",score + (StringUtil.isEmpty(hscore)? "" : ("(" + hscore + ")")));
                    tzxxDto.put("bstime","");
                }
                else
                {
                    //未开
                    if(matchState == 0)
                    {
                        tzxxDto.put("bscore","未开赛");
                        tzxxDto.put("bstime","");//设置比赛进行时间
                    }
                    //上半场
                    else if(matchState == 1)
                    {
                        tzxxDto.put("bscore",ccinfoDto.get("homeScore") + ":" + ccinfoDto.get("guestScore"));
                        String bstime = "";
                        String beginTime = ccinfoDto.getAsString("beginTime");
                        if(StringUtil.isNotEmpty(beginTime))
                        {
                            Calendar beginCalendar = DateUtil.parseCalendar(beginTime,DateUtil.DEFAULT_DATE_TIME);//比赛开始时间
                            Calendar currentCalendar = Calendar.getInstance();
                            int minute = DateUtil.minutesBetween(beginCalendar.getTime(),currentCalendar.getTime());//计算开赛时间与当前时间的分差数
                            bstime = minute > 120? "" : (minute > 45? "45" : (minute + ""));
                        }
                        tzxxDto.put("bstime",bstime);//设置比赛进行时间
                    }
                    //中场
                    else if(matchState == 2)
                    {
                        tzxxDto.put("bscore",(ccinfoDto.get("homeScore") + ":" + ccinfoDto.get("guestScore") + "(" + (ccinfoDto.get("homeHalfScore") + ":" + ccinfoDto.get("guestHalfScore")) + ")"));
                        String bstime = "中";
                        String beginTime = ccinfoDto.getAsString("beginTime");
                        if(StringUtil.isNotEmpty(beginTime))
                        {
                            Calendar beginCalendar = DateUtil.parseCalendar(beginTime,DateUtil.DEFAULT_DATE_TIME);//比赛开始时间
                            Calendar currentCalendar = Calendar.getInstance();
                            int minute = DateUtil.minutesBetween(beginCalendar.getTime(),currentCalendar.getTime());//计算开赛时间与当前时间的分差数
                            bstime = minute > 120? "" : "45";
                        }
                        tzxxDto.put("bstime",bstime);//设置比赛进行时间
                    }
                    //下半场
                    else if(matchState == 3)
                    {
                        tzxxDto.put("bscore",(ccinfoDto.get("homeScore") + ":" + ccinfoDto.get("guestScore") + "(" + (ccinfoDto.get("homeHalfScore") + ":" + ccinfoDto.get("guestHalfScore")) + ")"));
                        String bstime = "";
                        String beginTime = ccinfoDto.getAsString("beginTime");
                        if(StringUtil.isNotEmpty(beginTime))
                        {
                            Calendar beginCalendar = DateUtil.parseCalendar(beginTime,DateUtil.DEFAULT_DATE_TIME);//比赛开始时间
                            Calendar currentCalendar = Calendar.getInstance();
                            int minute = DateUtil.minutesBetween(beginCalendar.getTime(),currentCalendar.getTime());//计算开赛时间与当前时间的分差数
                            minute = minute + 45;
                            bstime = minute > 120? "" : (minute > 90? "90" : (minute + ""));
                        }
                        tzxxDto.put("bstime",bstime);//设置比赛进行时间
                    }
                    //加时
                    else if(matchState == 4)
                    {
                        tzxxDto.put("bscore",(ccinfoDto.get("homeScore") + ":" + ccinfoDto.get("guestScore") + "(" + (ccinfoDto.get("homeHalfScore") + ":" + ccinfoDto.get("guestHalfScore")) + ")"));
                        tzxxDto.put("bstime","");
                    }
                    //待定
                    else if(matchState == -11)
                    {
                        tzxxDto.put("bscore","<font color='#FF0000'>待定</font>");
                        tzxxDto.put("bstime","");
                    }
                    //腰斩
                    else if(matchState == -12)
                    {
                        tzxxDto.put("bscore","<font color='#FF0000'>腰斩</font>");
                        tzxxDto.put("bstime","");
                    }
                    //中断
                    else if(matchState == -13)
                    {
                        tzxxDto.put("bscore","<font color='#FF0000'>中断</font>");
                        tzxxDto.put("bscore","中断");
                        tzxxDto.put("bstime","");
                    }
                    //推迟
                    else if(matchState == -14)
                    {
                        tzxxDto.put("bscore","<font color='#FF0000'>推迟</font>");
                        tzxxDto.put("bstime","");
                    }
                    //完场
                    else if(matchState == -1)
                    {
                        tzxxDto.put("bscore",(ccinfoDto.get("homeScore") + ":" + ccinfoDto.get("guestScore") + "(" + (ccinfoDto.get("homeHalfScore") + ":" + ccinfoDto.get("guestHalfScore")) + ")"));
                        tzxxDto.put("bstime","");
                    }
                    //取消
                    else if(matchState == -10)
                    {
                        tzxxDto.put("bscore","<font color='#FF0000'>取消</font>");
                        tzxxDto.put("bstime","");
                    }
                    else
                    {
                        tzxxDto.put("bscore","--");
                        tzxxDto.put("bstime","");//设置比赛进行时间为空
                    }
                }
            }

            /**
             * 设置投注选项信息
             */
            //设置混投投注选项信息
            if(ishh)
            {
                ccxxList = new ArrayList<Dto>();
                String[] ccxxs = ccxxData.getAsString("tzcodes").split(">");//获取单场次投注选项
                String[] xxs = ccxxs[1].split("\\+");
                for(String xx : xxs)
                {
                    //设置玩法名称及盘口
                    String[] wfxx = xx.split("=");//提取玩法及选项
                    ccxxDto = new BaseDto();
                    String wfprefix = wfxx[0].indexOf("(") > -1? (wfxx[0].substring(0,wfxx[0].indexOf("("))) : wfxx[0];
                    String wfname = LotteryConstants.playMethodMaps.get(wfprefix);//根据玩法前缀获取玩法名称
                    if(wfxx[0].indexOf("(") > -1)
                    {
                        String pankou = wfxx[0].substring((wfxx[0].indexOf("(") + 1),wfxx[0].indexOf(")"));
                        if(!LotteryConstants.JCWF_PREFIX_DXF.equals(wfprefix))
                        {
                            pankou = pankou.indexOf("-") > -1? pankou : ("+" + pankou);
                        }
                        wfname += "(" + pankou + ")";
                    }
                    ccxxDto.put("wfname",wfname);//设置玩法名称

                    //设置选项
                    StringBuilder xxsBuilder = new StringBuilder();//选项字符串
                    String[] tempxxs = wfxx[1].split("/");
                    if(wfprefix.equals(LotteryConstants.JCWF_PREFIX_JQS)
                            || wfprefix.equals(LotteryConstants.JCWF_PREFIX_CBF))
                    {
                        sortArrayWithSpByAsc(tempxxs);//进球数/猜比分玩法,选项升序排列
                    }
                    else
                    {
                        sortArrayWithSpByDesc(tempxxs);//其它玩法,选项降序排列
                    }
                    int mzcs = 0;//命中次数
                    for(String tempxx : tempxxs)
                    {
                        String realxx = tempxx.indexOf("(") > -1? (tempxx.substring(0,tempxx.indexOf("("))) : tempxx;
                        String xname = LotteryConstants.jcXxNameMaps.get(wfprefix + realxx);
                        if(tempxx.indexOf("(") > -1)
                        {
                            String sp = tempxx.substring(tempxx.indexOf("("));
                            xname += sp;
                        }
                        //拼接选项
                        int zstatus = getJcMzStatus(ccinfoDto,wfxx[0],realxx);//获取选项命中状态(0-未命中 1-命中)
                        if(zstatus == 0)
                        {
                            xxsBuilder.append(xname + " ");
                        }
                        else
                        {
                            xxsBuilder.append("<font color='#FF0000'>" + xname + "</font>" + " ");
                        }
                        mzcs += zstatus;

                        //保存玩法选项赔率信息
                        String realwf = wfxx[0];
                        realwf = realwf.indexOf("(") > -1? realwf.substring(0,realwf.indexOf("(")) : realwf;
                        params.put((ccinfoDto.getAsString("matchCode") + realwf + realxx),tempxx.substring(tempxx.indexOf("(") + 1,tempxx.lastIndexOf(")")));
                    }
                    ccxxDto.put("xxs",xxsBuilder.toString());//设置选项信息
                    String wfcg = getJcWfcg(ccinfoDto,wfxx[0]);//获取玩法彩果
                    ccxxDto.put("xxcg",StringUtil.isEmpty(wfcg)? "" : (mzcs > 0? ("<font color='#FF0000'>" + wfcg + "</font>") : wfcg));//设置玩法彩果
                    ccxxList.add(ccxxDto);
                }
            }
            //设置非混投投注选项信息
            else
            {
                //设置玩法及盘口
                ccxxList = new ArrayList<Dto>();
                ccxxDto = new BaseDto();
                String realWfname = tzwfname;
                String pankou = ccxxData.getAsString("pankou");
                if(StringUtil.isNotEmpty(pankou))
                {
                    pankou = pankou.indexOf("-") > -1? pankou : ("+" + pankou);
                    pankou = "(" + pankou + ")";
                    realWfname += pankou;
                }
                ccxxDto.put("wfname",realWfname);//设置玩法名称

                //设置选项
                StringBuilder xxsBuilder = new StringBuilder();//选项字符串
                String[] tempxxs = ccxxData.getAsString("tzcodes").split("/");
                if(tzprefix.equals(LotteryConstants.JCWF_PREFIX_JQS)
                        || tzprefix.equals(LotteryConstants.JCWF_PREFIX_CBF))
                {
                    sortArrayWithSpByAsc(tempxxs);//进球数/猜比分玩法,选项升序排列
                }
                else
                {
                    sortArrayWithSpByDesc(tempxxs);//其它玩法,选项降序排列
                }
                int mzcs = 0;//命中次数
                for(String tempxx : tempxxs)
                {
                    String realxx = tempxx.indexOf("(") > -1? (tempxx.substring(0,tempxx.indexOf("("))) : tempxx;
                    String xname = LotteryConstants.jcXxNameMaps.get(tzprefix + realxx);
                    if(tempxx.indexOf("(") > -1)
                    {
                        xname += tempxx.substring(tempxx.indexOf("("));
                    }
                    //拼接选项
                    int zstatus = getJcMzStatus(ccinfoDto,(tzprefix + pankou),realxx);//获取选项命中状态(0-未命中 1-命中)
                    if(zstatus == 0)
                    {
                        xxsBuilder.append(xname + " ");
                    }
                    else
                    {
                        xxsBuilder.append("<font color='#FF0000'>" + xname + "</font>" + " ");
                    }
                    mzcs += zstatus;

                    //保存玩法选项赔率信息
                    String realwf = tzprefix;
                    realwf = realwf.indexOf("(") > -1? realwf.substring(0,realwf.indexOf("(")) : realwf;
                    params.put((ccinfoDto.getAsString("matchCode") + realwf + realxx),tempxx.substring(tempxx.indexOf("(") + 1,tempxx.lastIndexOf(")")));
                }
                ccxxDto.put("xxs",xxsBuilder.toString());//设置选项信息
                String wfcg = getJcWfcg(ccinfoDto,(tzprefix + pankou));//获取玩法彩果
                ccxxDto.put("xxcg",StringUtil.isEmpty(wfcg)? "" : (mzcs > 0? ("<font color='#FF0000'>" + wfcg + "</font>") : wfcg));//设置玩法彩果
                ccxxList.add(ccxxDto);
            }
            tzxxDto.put("ccxxs",ccxxList);//设置场次选项信息
            tzxxList.add(tzxxDto);
        }
        return tzxxList;
    }

    /**
     * 获取选项命中状态
     * @author  mcdog
     * @param   ccinfoDto   场次信息(包含赛果)
     * @param   wf          玩法
     * @param   xx          选项
     * @return  zstatus     命中状态 0-未命中 1-命中
     */
    @Override
    public int getJcMzStatus(Dto ccinfoDto, String wf, String xx)
    {
        int zstatus = 0;
        try
        {
            if(StringUtil.isEmpty(ccinfoDto.get("halfScore")) || StringUtil.isEmpty(ccinfoDto.get("score")))
            {
                return 0;
            }
            //胜平负
            if(wf.startsWith(LotteryConstants.JCWF_PREFIX_SPF))
            {
                String[] scores = ccinfoDto.getAsString("score").split(":");
                int hscore = Integer.parseInt(scores[0]);//主队得分
                int gscore = Integer.parseInt(scores[1]);//客队得分
                int result = hscore > gscore? 3 : (hscore == gscore? 1 : 0);//比赛结果 3-主胜 1-平 0-主负
                zstatus = (result == Integer.parseInt(xx))? 1 : 0;
            }
            //让球胜平负
            else if(wf.startsWith(LotteryConstants.JCWF_PREFIX_RQSPF))
            {
                String[] scores = ccinfoDto.getAsString("score").split(":");
                double hscore = Double.parseDouble(scores[0]);//主队得分
                double gscore = Integer.parseInt(scores[1]);//客队得分
                if(wf.indexOf("(") > -1)
                {
                    hscore += Double.parseDouble(wf.substring(wf.indexOf("(") + 1,wf.indexOf(")")));//主队得分加上让球数
                }
                int result = hscore > gscore? 3 : (hscore == gscore? 1 : 0);//比赛结果 3-主胜 1-平 0-主负
                zstatus = (result == Integer.parseInt(xx))? 1 : 0;
            }
            //总进球
            else if(wf.startsWith(LotteryConstants.JCWF_PREFIX_JQS))
            {
                String[] scores = ccinfoDto.getAsString("score").split(":");
                int hscore = Integer.parseInt(scores[0]);//主队得分
                int gscore = Integer.parseInt(scores[1]);//客队得分
                int result = hscore + gscore;
                result = result > 7? 7 : result;//7+球当做7球处理(因为总进球选项最大值为7)
                zstatus = (result == Integer.parseInt(xx))? 1 : 0;
            }
            //半全场
            else if(wf.startsWith(LotteryConstants.JCWF_PREFIX_BQC))
            {
                String[] hscores = ccinfoDto.getAsString("halfScore").split(":");
                String[] scores = ccinfoDto.getAsString("score").split(":");
                int hhscore = Integer.parseInt(hscores[0]);//主队半场得分
                int ghscore = Integer.parseInt(hscores[1]);//客队半场得分
                int hscore = Integer.parseInt(scores[0]);//主队得分
                int gscore = Integer.parseInt(scores[1]);//客队得分
                int hresult = hhscore > ghscore? 3 : (hhscore == ghscore? 1 : 0);//半场比赛结果 3-主胜 1-平 0-主负
                int result = hscore > gscore? 3 : (hscore == gscore? 1 : 0);//全场比赛结果 3-主胜 1-平 0-主负
                String realResult = hresult + "-" + result;
                zstatus = realResult.equals(xx)? 1 : 0;
            }
            //比分
            else if(wf.startsWith(LotteryConstants.JCWF_PREFIX_CBF))
            {
                String[] scores = ccinfoDto.getAsString("score").split(":");
                int hscore = Integer.parseInt(scores[0]);//主队得分
                int gscore = Integer.parseInt(scores[1]);//客队得分
                String result = ccinfoDto.getAsString("score");//默认取赛果

                //处理胜其它的赛果
                if(hscore > gscore && (hscore > 5 || (hscore + gscore > 7)))
                {
                    result = "9:0";
                }
                //处理平其它的赛果
                else if(hscore == gscore && hscore > 3)
                {
                    result = "9:9";
                }
                //处理负其它的赛果
                else if(hscore < gscore && (gscore > 5 || (hscore + gscore > 7)))
                {
                    result = "0:9";
                }
                zstatus = result.equals(xx)? 1 : 0;
            }
        }
        catch (Exception e)
        {
            logger.error("获取竞彩足球选项命中状态发生异常,场次信息:" + ccinfoDto.toString() + ",玩法:" + wf + ",选项:" + xx + ",异常信息:" + e);
            zstatus = 0;
        }
        return zstatus;
    }

    /**
     * 获取竞彩足球玩法彩果
     * @author  mcdog
     * @param   ccinfoDto   场次信息(包含赛果)
     * @param   wf          玩法
     * @return  wfcg        玩法彩果
     */
    @Override
    public String getJcWfcg(Dto ccinfoDto,String wf)
    {
        String wfcg = "";
        try
        {
            if(StringUtil.isEmpty(ccinfoDto.get("halfScore")) || StringUtil.isEmpty(ccinfoDto.get("score")))
            {
                return "";
            }
            //胜平负
            if(wf.startsWith(LotteryConstants.JCWF_PREFIX_SPF))
            {
                String[] scores = ccinfoDto.getAsString("score").split(":");
                int hscore = Integer.parseInt(scores[0]);//主队得分
                int gscore = Integer.parseInt(scores[1]);//客队得分
                int result = hscore > gscore? 3 : (hscore == gscore? 1 : 0);//比赛结果 3-主胜 1-平 0-主负
                wfcg = LotteryConstants.jcXxNameMaps.get(wf + result);//玩法彩果
            }
            //让球胜平负
            else if(wf.startsWith(LotteryConstants.JCWF_PREFIX_RQSPF))
            {
                String[] scores = ccinfoDto.getAsString("score").split(":");
                double hscore = Double.parseDouble(scores[0]);//主队得分
                double gscore = Integer.parseInt(scores[1]);//客队得分
                String newwf = wf;
                if(wf.indexOf("(") > -1)
                {
                    hscore += Double.parseDouble(wf.substring(wf.indexOf("(") + 1,wf.indexOf(")")));//主队得分加上让球数
                    newwf = wf.substring(0,wf.indexOf("("));
                }
                int result = hscore > gscore? 3 : (hscore == gscore? 1 : 0);//比赛结果 3-主胜 1-平 0-主负
                wfcg = LotteryConstants.jcXxNameMaps.get(newwf + result);//玩法彩果
            }
            //总进球
            else if(wf.startsWith(LotteryConstants.JCWF_PREFIX_JQS))
            {
                String[] scores = ccinfoDto.getAsString("score").split(":");
                int hscore = Integer.parseInt(scores[0]);//主队得分
                int gscore = Integer.parseInt(scores[1]);//客队得分
                int result = hscore + gscore;//比赛结果
                wfcg = LotteryConstants.jcXxNameMaps.get(wf + result);//玩法彩果
            }
            //半全场
            else if(wf.startsWith(LotteryConstants.JCWF_PREFIX_BQC))
            {
                String[] hscores = ccinfoDto.getAsString("halfScore").split(":");
                String[] scores = ccinfoDto.getAsString("score").split(":");
                int hhscore = Integer.parseInt(hscores[0]);//主队半场得分
                int ghscore = Integer.parseInt(hscores[1]);//客队半场得分
                int hscore = Integer.parseInt(scores[0]);//主队得分
                int gscore = Integer.parseInt(scores[1]);//客队得分
                int hresult = hhscore > ghscore? 3 : (hhscore == ghscore? 1 : 0);//半场比赛结果 3-主胜 1-平 0-主负
                int result = hscore > gscore? 3 : (hscore == gscore? 1 : 0);//全场比赛结果 3-主胜 1-平 0-主负
                String realResult = hresult + "-" + result;//比赛结果
                wfcg = LotteryConstants.jcXxNameMaps.get(wf + realResult);//玩法彩果
            }
            //比分
            else if(wf.startsWith(LotteryConstants.JCWF_PREFIX_CBF))
            {
                String[] scores = ccinfoDto.getAsString("score").split(":");
                int hscore = Integer.parseInt(scores[0]);//主队得分
                int gscore = Integer.parseInt(scores[1]);//客队得分
                String result = ccinfoDto.getAsString("score");//默认取赛果

                //处理胜其它的赛果
                if(hscore > gscore && (hscore > 5 || (hscore + gscore > 7)))
                {
                    result = "9:0";
                }
                //处理平其它的赛果
                else if(hscore == gscore && hscore > 3)
                {
                    result = "9:9";
                }
                //处理负其它的赛果
                else if(hscore < gscore && (gscore > 5 || (hscore + gscore > 7)))
                {
                    result = "0:9";
                }
                wfcg = LotteryConstants.jcXxNameMaps.get(wf + result);//玩法彩果
            }
        }
        catch (Exception e)
        {
            logger.error("[获取竞彩足球玩法彩果]发生异常!场次信息=" + ccinfoDto.toString() + ",玩法=" + wf + ",异常信息:" + e);
            wfcg = "";
        }
        return wfcg;
    }

    /**
     * 获取方案优化信息
     * @author  mcdog
     * @param   scheme          方案对象
     * @param   matchMaps       方案对阵Map
     * @param   params          参数对象,用来传递或接收额外的参数
     * @return  yhinfosList     方案优化信息
     */
    @Override
    public List<Dto> getYhinfos(Scheme scheme,Map<String,Dto> matchMaps,Dto params)
    {
        List<Dto> yhinfosList = new ArrayList<Dto>();
        if(StringUtil.isNotEmpty(scheme.getSchemeYhContent()))
        {
            //提取奖金优化方案投注信息
            JSONArray yhinfosArray = JSONArray.fromObject(scheme.getSchemeYhContent());
            if(yhinfosArray != null || yhinfosArray.size() > 0)
            {
                //解析奖金优化方案投注信息
                Dto yhinfoDto = null;
                for (Object object : yhinfosArray)
                {
                    yhinfoDto = new BaseDto();
                    JSONObject jsonObject = JSONObject.fromObject(object);
                    String[] tzcontents = jsonObject.getString("tzcontent").split("\\|");//提取投注选项
                    yhinfoDto.put("ggfs",tzcontents[2].replace("1*1","单关").replace("*","串"));//设置过关方式
                    yhinfoDto.put("smultiple",jsonObject.getInt("smultiple"));//设置倍数
                    yhinfoDto.put("lprize",jsonObject.getString("lprize"));//设置理论奖金

                    //设置投注选项信息
                    StringBuilder xxsBuilder = new StringBuilder();
                    if(tzcontents[0].startsWith(LotteryConstants.JCWF_PREFIX_HH))
                    {
                        String[] tzcodes = tzcontents[1].split(",");
                        StringBuilder tempbuilder = new StringBuilder();
                        for(String codes : tzcodes)
                        {
                            String[] tzxxs = codes.split("\\>");
                            Dto matchDto = matchMaps.get(tzxxs[0]);//提取场次信息
                            String[] xxs = tzxxs[1].split("\\=");
                            xxsBuilder.append("<br/>");
                            String hname = matchDto.getAsString("hostName");//提取主队名
                            hname = StringUtil.isEmpty(hname)? "" : (hname.length() > 4? hname.substring(0,4) : hname);//主队名最多取前面4个字
                            xxsBuilder.append(hname + "=");//拼接主队名
                            xxsBuilder.append(LotteryConstants.jcXxNameMaps.get(xxs[0] + xxs[1]));//拼接选项
                            xxsBuilder.append("(" + params.getAsString(tzxxs[0] + xxs[0] + xxs[1]) + ")");//拼接赔率
                        }
                    }
                    else
                    {
                        String[] tzcodes = tzcontents[1].split(",");
                        for(String codes : tzcodes)
                        {
                            String[] xxs = codes.split("\\=");//提取投注选项
                            Dto matchDto = matchMaps.get(xxs[0]);//提取场次信息
                            xxsBuilder.append("<br/>");
                            String hname = matchDto.getAsString("hostName");//提取主队名
                            hname = StringUtil.isEmpty(hname)? "" : (hname.length() > 4? hname.substring(0,4) : hname);//主队名最多取前面4个字
                            xxsBuilder.append(hname + "=");//拼接主队名
                            xxsBuilder.append(LotteryConstants.jcXxNameMaps.get(tzcontents[0] + xxs[1]));//拼接选项
                            xxsBuilder.append("(" + params.getAsString(xxs[0] + tzcontents[0] + xxs[1]) + ")");//拼接赔率
                        }
                    }
                    yhinfoDto.put("xxs",xxsBuilder.toString().substring(5));//设置投注选项信息
                    yhinfosList.add(yhinfoDto);
                }
            }
        }
        return yhinfosList;
    }

    /**
     * 获取方案优化信息
     * @author  mcdog
     * @param   scheme          方案Dto
     * @param   matchMaps       方案对阵Map
     * @param   params          参数对象,用来传递或接收额外的参数
     * @return  yhinfosList     方案优化信息
     */
    @Override
    public List<Dto> getYhinfos(Dto scheme,Map<String,Dto> matchMaps,Dto params)
    {
        List<Dto> yhinfosList = new ArrayList<Dto>();
        if(StringUtil.isNotEmpty(scheme.getAsString("schemeYhContent")))
        {
            //提取奖金优化方案投注信息
            JSONArray yhinfosArray = JSONArray.fromObject(scheme.getAsString("schemeYhContent"));
            if(yhinfosArray != null || yhinfosArray.size() > 0)
            {
                //解析奖金优化方案投注信息
                Dto yhinfoDto = null;
                for (Object object : yhinfosArray)
                {
                    yhinfoDto = new BaseDto();
                    JSONObject jsonObject = JSONObject.fromObject(object);
                    String[] tzcontents = jsonObject.getString("tzcontent").split("\\|");//提取投注选项
                    yhinfoDto.put("ggfs",tzcontents[2].replace("1*1","单关").replace("*","串"));//设置过关方式
                    yhinfoDto.put("smultiple",jsonObject.getInt("smultiple"));//设置倍数
                    yhinfoDto.put("lprize",jsonObject.getString("lprize"));//设置理论奖金

                    //设置投注选项信息
                    StringBuilder xxsBuilder = new StringBuilder();
                    if(tzcontents[0].startsWith(LotteryConstants.JCWF_PREFIX_HH))
                    {
                        String[] tzcodes = tzcontents[1].split(",");
                        StringBuilder tempbuilder = new StringBuilder();
                        for(String codes : tzcodes)
                        {
                            String[] tzxxs = codes.split("\\>");
                            Dto matchDto = matchMaps.get(tzxxs[0]);//提取场次信息
                            String[] xxs = tzxxs[1].split("\\=");
                            xxsBuilder.append("<br/>");
                            String hname = matchDto.getAsString("hostName");//提取主队名
                            hname = StringUtil.isEmpty(hname)? "" : (hname.length() > 4? hname.substring(0,4) : hname);//主队名最多取前面4个字
                            xxsBuilder.append(hname + "=");//拼接主队名
                            xxsBuilder.append(LotteryConstants.jcXxNameMaps.get(xxs[0] + xxs[1]));//拼接选项
                            xxsBuilder.append("(" + params.getAsString(tzxxs[0] + xxs[0] + xxs[1]) + ")");//拼接赔率
                        }
                    }
                    else
                    {
                        String[] tzcodes = tzcontents[1].split(",");
                        for(String codes : tzcodes)
                        {
                            String[] xxs = codes.split("\\=");//提取投注选项
                            Dto matchDto = matchMaps.get(xxs[0]);//提取场次信息
                            xxsBuilder.append("<br/>");
                            String hname = matchDto.getAsString("hostName");//提取主队名
                            hname = StringUtil.isEmpty(hname)? "" : (hname.length() > 4? hname.substring(0,4) : hname);//主队名最多取前面4个字
                            xxsBuilder.append(hname + "=");//拼接主队名
                            xxsBuilder.append(LotteryConstants.jcXxNameMaps.get(tzcontents[0] + xxs[1]));//拼接选项
                            xxsBuilder.append("(" + params.getAsString(xxs[0] + tzcontents[0] + xxs[1]) + ")");//拼接赔率
                        }
                    }
                    yhinfoDto.put("xxs",xxsBuilder.toString().substring(5));//设置投注选项信息
                    yhinfosList.add(yhinfoDto);
                }
            }
        }
        return yhinfosList;
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
            for(SchemeTicket schemeTicket : ticketList)
            {
                ticketDto = new BaseDto();
                tempScheme.setSchemeContent(schemeTicket.getCodes());
                tempScheme.setSchemeSpContent(schemeTicket.getCodesSp());
                ticketDto.put("xxs",getTicketTzxxs(tempScheme,matchMaps,ticketDto));//设置票单选项
                ticketDto.put("smultiple",schemeTicket.getMultiple() + "倍/" + ticketDto.getAsString("ggfs"));//设置票单倍数/串关
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
            if(scheme.getSchemeContent().startsWith(LotteryConstants.JCWF_PREFIX_HH))
            {
                String[] tzspContents = scheme.getSchemeSpContent().split(",");//提取带sp的投注内容
                for(String tzspContent : tzspContents)
                {
                    String[] tzcodes = tzspContent.split("-\\>");
                    String[] wfxxs = tzcodes[1].split("\\=");
                    Dto ccinfoDto = matchMaps.get(tzcodes[0]);//提取场次信息
                    xxsBuilder.append("<br/>");
                    xxsBuilder.append(ccinfoDto.getAsString("weekday") + ccinfoDto.getAsString("jcId"));
                    xxsBuilder.append(">");
                    xxsBuilder.append(LotteryConstants.playMethodMaps.get(wfxxs[0]));
                    xxsBuilder.append("=");
                    String tempxxs = "";
                    String[] xxs = wfxxs[1].split("\\/");
                    for(String xx : xxs)
                    {
                        String[] x = xx.split("\\@");
                        tempxxs += "/" + LotteryConstants.jcXxNameMaps.get(wfxxs[0] + x[0]);
                        tempxxs += "(" + x[1] + ")";
                    }
                    xxsBuilder.append(tempxxs.substring(1));
                }
                xxsBuilder = new StringBuilder(xxsBuilder.toString().substring(5));
                ticketDto.put("ggfs",tzspContents.length + "串1");
            }
            else
            {
                String[] tzcontents = scheme.getSchemeContent().split("\\|");
                String wfname = LotteryConstants.playMethodMaps.get(tzcontents[0]);
                String[] tzspContents = scheme.getSchemeSpContent().split(",");//提取带sp的投注内容
                for(String tzspContent : tzspContents)
                {
                    String[] tzcodes = tzspContent.split("\\=");
                    Dto ccinfoDto = matchMaps.get(tzcodes[0]);//提取场次信息
                    xxsBuilder.append("<br/>");
                    xxsBuilder.append(ccinfoDto.getAsString("weekday") + ccinfoDto.getAsString("jcId"));
                    xxsBuilder.append(">");
                    xxsBuilder.append(wfname);
                    xxsBuilder.append("=");
                    String tempxxs = "";
                    String[] xxs = tzcodes[1].split("\\/");
                    for(String xx : xxs)
                    {
                        String[] x = xx.split("\\@");
                        tempxxs += "/" + LotteryConstants.jcXxNameMaps.get(tzcontents[0] + x[0]);
                        tempxxs += "(" + x[1] + ")";
                    }
                    xxsBuilder.append(tempxxs.substring(1));
                }
                xxsBuilder = new StringBuilder(xxsBuilder.toString().substring(5));
                ticketDto.put("ggfs",tzspContents.length + "串1");
            }
        }
        return xxsBuilder.toString();
    }
}