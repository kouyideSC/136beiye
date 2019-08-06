package com.caipiao.service.kaijiang;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.lottery.PeriodMapper;
import com.caipiao.dao.match.MatchBasketBallMapper;
import com.caipiao.dao.match.MatchFootBallMapper;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.code.ErrorCode;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.lottery.Period;
import com.caipiao.domain.vo.KaiJiangVo;
import com.caipiao.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.util.resources.CalendarData_el;

import java.util.*;

/**
 * 开奖业务处理类
 * @author  mcdog
 */
@Service("kaiJiangService")
public class KaiJiangService
{
    private static Logger logger = LoggerFactory.getLogger(KaiJiangService.class);

    @Autowired
    private PeriodMapper periodMapper;

    @Autowired
    private MatchFootBallMapper footBallMapper;

    @Autowired
    private MatchBasketBallMapper basketBallMapper;

    /**
     * 查询所有在售彩种的开奖信息
     * @author  mcdog
     */
    public List<KaiJiangVo> getLatestKjInfos() throws ServiceException
    {
        //循环读取彩种信息,依次查询开奖信息
        List<KaiJiangVo> kjList = new ArrayList<KaiJiangVo>();
        Dto queryDto = new BaseDto("appStatus",1);
        for(Map.Entry<String,String> entry : LotteryConstants.lotteryMap.entrySet())
        {
            //任九/四场进球/六场半全场不查询开奖信息
            if(LotteryConstants.RXJ.equals(entry.getKey())
                    || LotteryConstants.JQC.equals(entry.getKey())
                    || LotteryConstants.BQC.equals(entry.getKey()))
            {
                continue;
            }
            //快频/慢频/足彩
            queryDto.put("lotteryId",entry.getKey());
            KaiJiangVo kaiJiangVo = null;
            if(LotteryUtils.isKp(entry.getKey()) || LotteryUtils.isMp(entry.getKey()) || LotteryUtils.isZC(entry.getKey()))
            {
                //根据彩种id查询最新一期开奖信息
                kaiJiangVo = periodMapper.queryLatestKjByLotteryId(queryDto);
                if(kaiJiangVo != null)
                {
                    kaiJiangVo.setLid(entry.getKey());//将彩种id设置为前端显示模式
                    Date kjDate = DateUtil.parseDate(kaiJiangVo.getKtime(),DateUtil.DEFAULT_DATE_TIME);
                    kaiJiangVo.setPname(kaiJiangVo.getPid() + "期");
                    kaiJiangVo.setKtime(DateUtil.formatDate(kjDate,DateUtil.DEFAULT_DATE) + " " + DateUtil.getWeekStr(kjDate));
                    if(LotteryConstants.SFC.equals(entry.getKey()))
                    {
                        kaiJiangVo.setLname("胜负彩/任九");
                    }
                }
            }
            //竞彩足球
            else if(LotteryConstants.JCZQ.equals(entry.getKey()))
            {
                kaiJiangVo = footBallMapper.queryLatestKjMatch(queryDto);
                if(kaiJiangVo != null)
                {
                    kaiJiangVo.setLid(entry.getKey());//将彩种id设置为前端显示模式
                    kaiJiangVo.setPname(kaiJiangVo.getPid() + "期");
                    Date kjDate = DateUtil.parseDate(
                            (kaiJiangVo.getPid().substring(0,4) + "-" + kaiJiangVo.getPid().substring(4,6) + "-" + kaiJiangVo.getPid().substring(6)),
                            DateUtil.DEFAULT_DATE);
                    kaiJiangVo.setKtime(DateUtil.formatDate(kjDate,DateUtil.DEFAULT_DATE) + " " + DateUtil.getWeekStr(kjDate));
                }
            }
            //竞彩篮球需要查询最新开奖的一场对阵
            else if(LotteryConstants.JCLQ.equals(entry.getKey()))
            {
                kaiJiangVo = basketBallMapper.queryLatestKjMatch(queryDto);
                if(kaiJiangVo != null)
                {
                    kaiJiangVo.setLid(entry.getKey());//将彩种id设置为前端显示模式
                    kaiJiangVo.setPname(kaiJiangVo.getPid() + "期");
                    Date kjDate = DateUtil.parseDate(
                            (kaiJiangVo.getPid().substring(0,4) + "-" + kaiJiangVo.getPid().substring(4,6) + "-" + kaiJiangVo.getPid().substring(6)),
                            DateUtil.DEFAULT_DATE);
                    kaiJiangVo.setKtime(DateUtil.formatDate(kjDate,DateUtil.DEFAULT_DATE) + " " + DateUtil.getWeekStr(kjDate));
                }
            }
            if(kaiJiangVo != null)
            {
                kjList.add(kaiJiangVo);
            }
        }
        //按彩种的显示顺序进行排序
        Collections.sort(kjList, new Comparator<KaiJiangVo>()
        {
            @Override
            public int compare(KaiJiangVo o1, KaiJiangVo o2)
            {
                return o1.getXh() > o2.getXh()? 1 : -1;
            }
        });
        return  kjList;
    }

    /**
     * 查询指定数字彩的当前在售期次的开奖时间
     * @author  mcdog
     */
    public void getLotteryPeriodKjTime(Dto params, ResultBean result) throws Exception
    {
        if(StringUtil.isEmpty(params.get("lid")))
        {
            params.put("lid",LotteryConstants.DLT);//默认查询大乐透当前期的开奖时间
        }
        Period period = periodMapper.queryCurrentSellPeriod(params.getAsString("lid"));//查询当前在售期次
        if(period != null)
        {
            result.setErrorCode(ErrorCode.SUCCESS);
            Dto dataDto = new BaseDto("lid",params.get("lid"));//设置彩种编号
            dataDto.put("pid",period.getPeriod());//设置期次号

            //设置开奖时间
            String timestr = "--";
            if(period.getDrawNumberTime() != null)
            {
                Calendar currentCalendar = Calendar.getInstance();
                Calendar periodKjCalendar = Calendar.getInstance();
                periodKjCalendar.setTime(period.getDrawNumberTime());
                if(!LotteryUtils.isZC(params.getAsString("lid")))
                {
                    periodKjCalendar.set(Calendar.HOUR_OF_DAY,21);
                    periodKjCalendar.set(Calendar.MINUTE,30);
                }
                int days = periodKjCalendar.get(Calendar.DAY_OF_YEAR) - currentCalendar.get(Calendar.DAY_OF_YEAR);
                if(days == 0)
                {
                    timestr = "今天" + DateUtil.formatDate(periodKjCalendar.getTime(),DateUtil.HM_FORMAT);
                }
                else if(days == 1)
                {
                    timestr = "明天" + DateUtil.formatDate(periodKjCalendar.getTime(),DateUtil.HM_FORMAT);
                }
                else if(days == -1)
                {
                    timestr = "昨天" + DateUtil.formatDate(periodKjCalendar.getTime(),DateUtil.HM_FORMAT);
                }
                else
                {
                    timestr = DateUtil.formatDate(periodKjCalendar.getTime(),DateUtil.MDHM_FORMAT);
                }
                timestr += "开奖";
            }
            dataDto.put("kjtime",timestr);
            result.setData(dataDto);
        }
        else
        {
            result.setErrorCode(ErrorCode.SERVER_ERROR);
            result.setErrorDesc("无符合条件的期次开奖时间");
        }
    }
}