package com.caipiao.service.match;

import com.caipiao.common.constants.*;
import com.caipiao.common.util.CalculationUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.match.*;
import com.caipiao.dao.scheme.SchemeMapper;
import com.caipiao.dao.user.UserAccountMapper;
import com.caipiao.dao.user.UserDetailMapper;
import com.caipiao.dao.user.UserMapper;
import com.caipiao.domain.common.Task;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.user.User;
import com.caipiao.domain.user.UserAccount;
import com.caipiao.domain.user.UserDetail;
import com.caipiao.service.common.TaskService;
import com.caipiao.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

/**
 * 赛事对阵-服务类
 */
@Service("matchService")
public class MatchService
{
    private static final Logger logger = LoggerFactory.getLogger(MatchService.class);

    @Autowired
    private MatchFootBallMapper matchFootBallMapper;

    @Autowired
    private MatchBasketBallMapper matchBasketBallMapper;

    @Autowired
    private MatchFootBallSpMapper matchFootBallSpMapper;

    @Autowired
    private MatchBasketBallSpMapper matchBasketBallSpMapper;

    @Autowired
    private MatchFootBallResultMapper matchFootBallResultMapper;

    @Autowired
    private MatchBasketBallResultMapper matchBasketBallResultMapper;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserMapper userMapper;

    private static Map<String,String> matchStatusMaps = new HashMap<String,String>();//场次销售状态

    private static Map<String,String> matchStateMaps = new HashMap<String,String>();//场次计奖状态

    static
    {
        matchStatusMaps.put("-1","已取消");
        matchStatusMaps.put("0","已停售");
        matchStatusMaps.put("1","销售中");
        matchStatusMaps.put("2","已截止");

        matchStateMaps.put("0","待处理");
        matchStateMaps.put("1","自动撤单中");
        matchStateMaps.put("2","赛果获取中");
        matchStateMaps.put("3","已有赛果待审核");
        matchStateMaps.put("4","赛果人工审核成功");
        matchStateMaps.put("5","系统审核成功");
        matchStateMaps.put("6","计算奖金成功");
        matchStateMaps.put("7","奖金汇总成功");
        matchStateMaps.put("8","奖金核对成功");
        matchStateMaps.put("9","自动派奖成功");
        matchStateMaps.put("10","过关统计完成");
        matchStateMaps.put("11","战绩统计完成");
        matchStateMaps.put("12","派送返点成功");
        matchStateMaps.put("99","场次处理结束");
    }

    /**
     * 查询赛事对阵信息(app)
     * @author	mcdog
     */
    public List<Dto> queryMatches(Dto params) throws Exception
    {
        /**
         * 用户身份校验
         */
        if(StringUtil.isEmpty(params.get("userId")))
        {
            throw new Exception("非法操作");
        }
        User user = userMapper.queryUserInfoById(params.getAsLong("userId"));//获取操作用户
        if(user == null || user.getIsAdmin() != 1)
        {
            throw new Exception("非法操作");
        }
        //校验参数
        if(StringUtil.isEmpty(params.get("lotteryId")))
        {
            throw new Exception("缺少必要参数");
        }
        //判断是否有分页标识,如果有,则设置分页查询参数
        if(StringUtil.isNotEmpty(params.get("psize")))
        {
            //pnum为空或值格式错误,则默认查询第一页
            if(params.get("pnum") == null || params.getAsInteger("pnum") <= 0)
            {
                params.put("pnum",1);
            }
            //设置读取起始位置
            long pstart = (params.getAsLong("pnum") - 1) * params.getAsLong("psize");
            params.put("pstart",pstart);//设置读取起始位置
        }
        //设置默认期次
        if(StringUtil.isEmpty(params.get("period")))
        {
            Calendar calendar = Calendar.getInstance();
            if(calendar.get(Calendar.HOUR_OF_DAY) < 11 || (calendar.get(Calendar.HOUR_OF_DAY) == 11 && calendar.get(Calendar.MINUTE) < 30))
            {
                calendar.add(Calendar.DAY_OF_MONTH,-1);
            }
            params.put("period", DateUtil.formatDate(calendar.getTime(),DateUtil.DEFAULT_DATE1));//设置默认期次
        }
        //查询数据
        //params.put("states","2,3,4");//只查询对阵处理状态为赛果获取中/已有赛果待审核/赛果人工审核成功的赛事
        List<Dto> matchList = new ArrayList<Dto>();
        if(LotteryConstants.JCZQ.equals(params.getAsString("lotteryId")))
        {
            matchList = matchFootBallMapper.queryFootBallAudit(params);
        }
        else if(LotteryConstants.JCLQ.equals(params.getAsString("lotteryId")))
        {
            matchList = matchBasketBallMapper.queryBasketAudit(params);
        }
        //处理数据
        List<Dto> dataList = new ArrayList<Dto>();
        if(matchList != null && matchList.size() > 0)
        {
            Dto dataDto = null;
            for(Dto match : matchList)
            {
                dataDto = new BaseDto("id",match.get("id"));//赛事id
                dataDto.put("weekinfo",match.getAsString("weekday") + match.getAsString("jcId"));//周信息

                //设置主队名
                String hname = match.getAsString("hostName");
                hname = hname.length() > 4? hname.substring(0,4) : hname;
                dataDto.put("hostName",hname);//主队名

                //设置客队名
                String gname = match.getAsString("guestName");
                gname = gname.length() > 4? gname.substring(0,4) : gname;
                dataDto.put("guestName",gname);//客队名

                dataDto.put("halfScore",StringUtil.isEmpty(match.get("halfScore"))? "--" : match.get("halfScore"));//半场比分
                dataDto.put("score",StringUtil.isEmpty(match.get("score"))? "--" : match.get("score"));//全场比分
                dataDto.put("statusDesc",matchStatusMaps.get(match.getAsString("status")));//销售状态描述
                String state = match.getAsString("state");
                dataDto.put("stateDesc",matchStateMaps.get(state));//计奖状态描述
                dataDto.put("aflag",("2".equals(state) || "3".equals(state) || "4".equals(state))? 1 : 0);//是否允许审核,0-不允许 1-允许
                dataList.add(dataDto);
            }
        }
        return dataList;
    }

    /**
     * 查询赛事对阵总记录条数(app)
     * @author	mcdog
     */
    public int queryMatchesCount(Dto params) throws Exception
    {
        //设置默认期次
        if(StringUtil.isEmpty(params.get("period")))
        {
            Calendar calendar = Calendar.getInstance();
            if(calendar.get(Calendar.HOUR_OF_DAY) < 11 || (calendar.get(Calendar.HOUR_OF_DAY) == 11 && calendar.get(Calendar.MINUTE) < 30))
            {
                calendar.add(Calendar.DAY_OF_MONTH,-1);
            }
            params.put("period", DateUtil.formatDate(calendar.getTime(),DateUtil.DEFAULT_DATE1));//设置默认期次
        }
        //params.put("states","2,3,4");//只查询对阵处理状态为赛果获取中/已有赛果待审核/赛果人工审核成功的赛事
        int count = 0;
        if(LotteryConstants.JCZQ.equals(params.getAsString("lotteryId")))
        {
            count = matchFootBallMapper.queryFootBallAuditCount(params);
        }
        else if(LotteryConstants.JCLQ.equals(params.getAsString("lotteryId")))
        {
            count = matchBasketBallMapper.queryBasketAuditCount(params);
        }
        return count;
    }

    /**
     * 赛果审核(app)
     * @author	mcdog
     */
    public int eidtMatchResult(Dto params) throws Exception
    {
        /**
         * 用户身份校验
         */
        if(StringUtil.isEmpty(params.get("userId")))
        {
            throw new Exception("非法操作");
        }
        User user = userMapper.queryUserInfoById(params.getAsLong("userId"));//获取操作用户
        if(user == null || user.getIsAdmin() != 1)
        {
            throw new Exception("非法操作");
        }
        //参数校验
        if(StringUtil.isEmpty(params.get("lotteryId"))
                || StringUtil.isEmpty(params.get("id"))
                || StringUtil.isEmpty(params.get("score")))
        {
            throw new Exception("缺少必要参数");
        }
        //全场比分格式校验
        String score = params.getAsString("score");
        if(score.indexOf(":") <= 0 || score.split(":").length != 2)
        {
            throw new Exception("全场比分格式错误");
        }
        //设置赛事默认销售状态为截止
        if(StringUtil.isEmpty(params.get("status")))
        {
            params.remove("status");
        }
        //查询赛事对阵
        params.put("opfullName",(user.getNickName() + "(" + user.getMobile() + ")"));
        List<Dto> matchList = new ArrayList<Dto>();
        boolean iszq = true;
        if(LotteryConstants.JCZQ.equals(params.getAsString("lotteryId")))
        {
            //半场比分非空校验
            if(StringUtil.isEmpty(params.get("halfScore")))
            {
                throw new Exception("缺少必要参数");
            }
            //半场比分格式校验
            String halfScore = params.getAsString("halfScore");
            if(halfScore.indexOf(":") <= 0 || halfScore.split(":").length != 2)
            {
                throw new Exception("半场比分格式错误");
            }
            matchList.addAll(matchFootBallMapper.queryFootBallMatchs(params));
        }
        else if(LotteryConstants.JCLQ.equals(params.getAsString("lotteryId")))
        {
            iszq = false;
            matchList.addAll(matchBasketBallMapper.queryBasketBallMatchs(params));

        }
        else
        {
            throw new Exception("未知彩种");
        }
        if(matchList.size() == 0)
        {
            throw new Exception("查询不到相关的赛事对阵");
        }
        //赛果审核
        Dto matchDto = matchList.get(0);
        String state = matchDto.getAsString("state");
        if(!"2".equals(state) && !"3".equals(state) && !"4".equals(state))
        {
            throw new Exception("对阵尚未满足审核条件");
        }
        int result = 0;
        if(iszq)
        {
            result = matchFootBallMapper.editMatchResult(params);
        }
        else
        {
            result = matchBasketBallMapper.editMatchResult(params);
        }
        if(result > 0)
        {
            /**
             * 如果赛事对阵的销售状态为停售状态,则将赛事对阵的销售状态改为截止状态
             */
            logger.info("[赛果审核(app)]审核成功!赛事id=" + params.getAsString("id") + ",操作帐户=" + params.getAsString("opfullName"));
            if("0".equals(matchDto.getAsString("status")))
            {
                Dto updateParams = new BaseDto("id",params.get("id"));
                updateParams.put("status","2");
                int count = iszq? matchFootBallMapper.editSellStatus(updateParams) : matchBasketBallMapper.editSellStatus(updateParams);
                logger.info("[赛果审核(app)][停售赛事]销售状态修改" + (count > 0? "成功" : "失败") + "!操作帐户=" + params.getAsString("opfullName") + ",赛事id=" + params.getAsString("id"));
            }
            /**
             * 添加期次文件更新任务
             */
            Task task = new Task();
            task.setTaskName(Constants.periodHistoryUpdateTaskMaps.get(params.getAsString("lotteryId")));
            taskService.saveTask(task);
        }
        return result;
    }
}