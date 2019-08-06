package com.caipiao.admin.service.weihu.match;

import com.caipiao.admin.service.task.TaskService;
import com.caipiao.common.constants.Constants;
import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.lottery.LotteryMapper;
import com.caipiao.dao.match.*;
import com.caipiao.domain.common.Task;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.match.MatchFootBallSp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 查询赛事对阵信息
     * @author	sjq
     */
    public List<Dto> queryMatches(Dto params)
    {
        List<Dto> matchList = new ArrayList<Dto>();
        if(LotteryConstants.JCZQ.equals(params.getAsString("lotteryId")))
        {
            matchList = matchFootBallMapper.queryFootBallAudit(params);
        }
        else if(LotteryConstants.JCLQ.equals(params.getAsString("lotteryId")))
        {
            matchList = matchBasketBallMapper.queryBasketAudit(params);
        }
        return matchList;
    }

    /**
     * 查询赛事对阵总记录条数
     * @author	sjq
     */
    public int queryMatchesCount(Dto params)
    {
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
     * 赛果审核
     * @author	sjq
     */
    public int eidtMatchResult(Dto params) throws Exception
    {
        //参数校验
        int result = 0;
        if(StringUtil.isEmpty(params.get("lotteryId"))
                || StringUtil.isEmpty(params.get("id")))
        {
            params.put("dmsg","缺少必要参数");
            return 0;
        }
        String status = params.getAsString("status");//场次销售状态
        if(!"-1".equals(status))
        {
            //全场比分格式校验
            String score = params.getAsString("score");
            if(StringUtil.isEmpty(score))
            {
                params.put("dmsg","全场比分不能为空");
                return 0;
            }
            else if(score.indexOf(":") <= 0 || score.split(":").length != 2)
            {
                params.put("dmsg","全场比分格式错误");
                return 0;
            }
        }
        boolean iszq = true;
        if(LotteryConstants.JCZQ.equals(params.getAsString("lotteryId")))
        {
            //半场比分格式校验
            String halfScore = params.getAsString("halfScore");
            if(!"-1".equals(status))
            {
                if(StringUtil.isEmpty(halfScore))
                {
                    params.put("dmsg","半场比分不能为空");
                    return 0;
                }
                else if(halfScore.indexOf(":") <= 0 || halfScore.split(":").length != 2)
                {
                    params.put("dmsg","半场比分格式错误");
                    return 0;
                }
            }
            result = matchFootBallMapper.editMatchResult(params);
        }
        else if(LotteryConstants.JCLQ.equals(params.getAsString("lotteryId")))
        {
            iszq = false;
            result = matchBasketBallMapper.editMatchResult(params);
        }
        if(result > 0)
        {
            logger.info("[赛果审核]审核成功!赛事id=" + params.getAsString("id") + ",操作帐户=" + params.getAsString("opaccountName"));
            try
            {
                /**
                 * 如果赛事对阵的销售状态为停售状态,则将赛事对阵的销售状态改为截止状态
                 */
                List<Dto> matchList = iszq? matchFootBallMapper.queryFootBallMatchs(params) : matchBasketBallMapper.queryBasketBallMatchs(params);
                if(matchList != null && matchList.size() > 0)
                {
                    Dto matchDto = matchList.get(0);
                    if("0".equals(matchDto.getAsString("status")))
                    {
                        Dto updateParams = new BaseDto("id",params.get("id"));
                        updateParams.put("status",2);
                        int count = iszq? matchFootBallMapper.editSellStatus(updateParams) : matchBasketBallMapper.editSellStatus(updateParams);
                        logger.info("[赛果审核][停售赛事改为截止]销售状态修改" + (count > 0? "成功" : "失败") + "!操作帐户=" + params.getAsString("opaccountName") + ",赛事id=" + params.getAsString("id"));
                    }
                }
                /**
                 * 添加期次文件更新任务
                 */
                Task task = new Task();
                task.setTaskName(Constants.periodHistoryUpdateTaskMaps.get(params.getAsString("lotteryId")));
                taskService.saveTask(task);
            }
            catch(Exception e)
            {
                result = 0;
                params.put("dmsg","审核失败！插入【文件更新任务】发生异常，异常信息：" + e.getMessage());
                throw  e;
            }
        }
        return result;
    }

    /**
     * 销售状态修改
     * @author	sjq
     */
    public int editSellStatus(Dto params) throws Exception
    {
        int result = 0;
        if(LotteryConstants.JCZQ.equals(params.getAsString("lotteryId")))
        {
            /**
             * 判断,如果修改后的销售状态为停售或开售,则校验当前是否允许进行此操作(销售状态为取消或截止,则不允许进行开售或停售的操作)
             */
            int status = params.getAsInteger("status");
            if(status == 0 || status == 1)
            {
                Dto queryDto = new BaseDto("id",params.get("id"));
                queryDto.put("ids",params.get("ids"));
                List<Dto> matchList = matchFootBallMapper.queryFootBallMatchs(queryDto);
                if(matchList != null && matchList.size() > 0)
                {
                    for(Dto match : matchList)
                    {
                        int matchStatus = match.getAsInteger("status");
                        if(matchStatus == -1 || matchStatus == 2)
                        {
                            params.put("dmsg","已经取消或截止的比赛无法进行停开售操作");
                            return result;
                        }
                    }
                }
            }
            result = matchFootBallMapper.editSellStatus(params);
            logger.info("[销售状态修改][竞彩足球]修改" + (result > 0? "成功" : "失败") + "!操作帐户=" + params.getAsString("opaccountName")
                    + "赛事id=" + params.getAsString("id") + ",销售状态被修改为" + status);
        }
        else if(LotteryConstants.JCLQ.equals(params.getAsString("lotteryId")))
        {
            /**
             * 判断,如果修改后的销售状态为停售或开售,则校验当前是否允许进行此操作(销售状态为取消或截止,则不允许进行开售或停售的操作)
             */
            int status = params.getAsInteger("status");
            if(status == 0 || status == 1)
            {
                Dto queryDto = new BaseDto("id",params.get("id"));
                queryDto.put("ids",params.get("ids"));
                List<Dto> matchList = matchBasketBallMapper.queryBasketBallMatchs(queryDto);
                if(matchList != null && matchList.size() > 0)
                {
                    for(Dto match : matchList)
                    {
                        int matchStatus = match.getAsInteger("status");
                        if(matchStatus == -1 || matchStatus == 2)
                        {
                            params.put("dmsg","已经取消或截止的比赛无法进行停开售操作");
                            return result;
                        }
                    }
                }
            }
            result = matchBasketBallMapper.editSellStatus(params);
            logger.info("[销售状态修改][竞彩篮球]修改" + (result > 0? "成功" : "失败") + "!操作帐户=" + params.getAsString("opaccountName")
                    + "赛事id=" + params.getAsString("id") + ",销售状态被修改为" + status);
        }
        try
        {
            Task task = new Task();
            task.setTaskName(Constants.periodUpdateTaskMaps.get(params.getAsString("lotteryId")));
            taskService.saveTask(task);
        }
        catch(Exception e)
        {
            result = 0;
            params.put("dmsg","删除失败！插入【文件更新任务】发生异常，异常信息：" + e.getMessage());
            throw  e;
        }
        return result;
    }

    /**
     * 玩法销售状态修改
     * @author	sjq
     */
    public int editPlaySellStatus(Dto params) throws Exception
    {
        int result = 0;
        if(LotteryConstants.JCZQ.equals(params.getAsString("lotteryId")))
        {
            result = matchFootBallMapper.editPlaySellStatus(params);
            logger.info("[玩法销售状态修改][竞彩足球]修改" + (result > 0? "成功" : "失败") + "!操作帐户=" + params.getAsString("opaccountName")
                    + "赛事id=" + params.getAsString("id") + ",接收原始参数=" + params.toString());
        }
        else if(LotteryConstants.JCLQ.equals(params.getAsString("lotteryId")))
        {
            result = matchBasketBallMapper.editPlaySellStatus(params);
            logger.info("[玩法销售状态修改][竞彩篮球]修改" + (result > 0? "成功" : "失败") + "!操作帐户=" + params.getAsString("opaccountName")
                    + "赛事id=" + params.getAsString("id") + ",接收原始参数=" + params.toString());
        }
        try
        {
            Task task = new Task();
            task.setTaskName(Constants.periodUpdateTaskMaps.get(params.getAsString("lotteryId")));
            taskService.saveTask(task);
        }
        catch(Exception e)
        {
            result = 0;
            params.put("dmsg","删除失败！插入【文件更新任务】发生异常，异常信息：" + e.getMessage());
            throw  e;
        }
        return result;
    }

    /**
     * sp修改
     * @author	sjq
     */
    public int editSp(Dto params) throws Exception
    {
        int result = 0;
        if(LotteryConstants.JCZQ.equals(params.getAsString("lotteryId")))
        {
            result = matchFootBallMapper.editSp(params);
            logger.info("[sp修改][竞彩足球]修改" + (result > 0? "成功" : "失败") + "!操作帐户=" + params.getAsString("opaccountName")
                    + "赛事id=" + params.getAsString("id") + ",接收原始参数=" + params.toString());
        }
        else if(LotteryConstants.JCLQ.equals(params.getAsString("lotteryId")))
        {
            result = matchBasketBallMapper.editSp(params);
            logger.info("[sp修改][竞彩篮球]修改" + (result > 0? "成功" : "失败") + "!操作帐户=" + params.getAsString("opaccountName")
                    + "赛事id=" + params.getAsString("id") + ",接收原始参数=" + params.toString());
        }
        try
        {
            Task task = new Task();
            task.setTaskName(Constants.periodUpdateTaskMaps.get(params.getAsString("lotteryId")));
            taskService.saveTask(task);
        }
        catch(Exception e)
        {
            result = 0;
            params.put("dmsg","删除失败！插入【文件更新任务】发生异常，异常信息：" + e.getMessage());
            throw  e;
        }
        return result;
    }

    /**
     * 设置热门
     * @author	sjq
     */
    public int editHot(Dto params) throws Exception
    {
        int result = 0;
        if(LotteryConstants.JCZQ.equals(params.getAsString("lotteryId")))
        {
            result = matchFootBallMapper.editHot(params);
            logger.info("[设置热门][竞彩足球]设置" + (result > 0? "成功" : "失败") + "!操作帐户=" + params.getAsString("opaccountName")
                    + "赛事id=" + params.getAsString("id") + ",接收原始参数=" + params.toString());
        }
        else if(LotteryConstants.JCLQ.equals(params.getAsString("lotteryId")))
        {
            result = matchBasketBallMapper.editHot(params);
            logger.info("[设置热门][竞彩篮球]设置" + (result > 0? "成功" : "失败") + "!操作帐户=" + params.getAsString("opaccountName")
                    + "赛事id=" + params.getAsString("id") + ",接收原始参数=" + params.toString());
        }
        if(result > 0)
        {
            try
            {
                Task task = new Task();
                task.setTaskName(Constants.periodUpdateTaskMaps.get(params.getAsString("lotteryId")));
                taskService.saveTask(task);
            }
            catch(Exception e)
            {
                result = 0;
                params.put("dmsg","删除失败！插入【文件更新任务】发生异常，异常信息：" + e.getMessage());
                throw  e;
            }
        }
        return result;
    }
}