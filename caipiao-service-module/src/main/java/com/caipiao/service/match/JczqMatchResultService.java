package com.caipiao.service.match;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.lottery.JczqUtils;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.match.MatchFootBallMapper;
import com.caipiao.dao.match.MatchFootBallResultMapper;
import com.caipiao.dao.match.MatchFootBallResultMapper;
import com.caipiao.dao.match.MatchFootBallSpMapper;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.match.MatchFootBall;
import com.caipiao.domain.match.MatchFootBallResult;
import com.caipiao.domain.match.MatchFootBallResult;
import com.caipiao.domain.match.MatchFootBallSp;
import com.caipiao.domain.vo.JczqMatchVo;
import com.caipiao.domain.vo.JczqResultVo;
import com.caipiao.memcache.MemCached;
import com.caipiao.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 竞彩足球赛果相关业务处理服务
 * Created by kouyi on 2017/11/07.
 */
@Service("jczqMatchResultService")
public class JczqMatchResultService {
    private static Logger logger = LoggerFactory.getLogger(JczqMatchResultService.class);
    @Autowired
    private MatchFootBallResultMapper matchFootBallResultMapper;
    @Autowired
    private MatchFootBallSpMapper matchFootBallSpMapper;
    @Autowired
    private MatchFootBallMapper matchFootBallMapper;

    /**
     * 根据参数-查询竞彩足球赛果列表-前端接口展示使用
     * @return
     */
    public List<JczqResultVo> queryJczqResultList(String beginPeriod, String endPeriod) throws ServiceException, Exception {
        try {
            return matchFootBallResultMapper.queryJczqResultList(beginPeriod, endPeriod);
        } catch (Exception e) {
            logger.error("[查询竞彩足球赛果列表异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 保存竞彩足球赛果信息
     * @param matchResult
     * @param match
     * @throws ServiceException
     * @throws Exception
     */
    public void saveMatchResult(MatchFootBallResult matchResult, MatchFootBall match) throws ServiceException, Exception {
        try {
            MatchFootBallSp matchSp = matchFootBallSpMapper.queryMatchFootBallSpByMatchCode(match.getMatchCode());
            if(StringUtil.isEmpty(matchSp)) {
                return;
            }

            //根据比分计算足球赛果
            JczqUtils.getFootballResult(matchResult, matchSp, match);
            //属性初始化
            matchResult.setMatchTime(match.getMatchTime());
            matchResult.setStatus(match.getStatus());
            matchResult.setLose(match.getLose());
            MatchFootBallResult result = matchFootBallResultMapper.queryJczqResultInfo(match.getMatchCode());
            if(StringUtil.isEmpty(result)) {
                matchResult.setLeagueName(match.getLeagueName());
                matchResult.setMatchId(match.getId());
                matchResult.setMatchCode(match.getMatchCode());
                matchResult.setJcId(match.getWeekDay()+match.getJcId());
                matchResult.setPeriod(match.getPeriod());
                matchResult.setHostName(match.getHostName());
                matchResult.setGuestName(match.getGuestName());
                matchFootBallResultMapper.insertFootBallResult(matchResult);
            } else {
                matchResult.setId(result.getId());
                matchFootBallResultMapper.updateJczqResult(matchResult);
            }
            //更新对阵表比分赛果
            match.setHalfScore(matchResult.getHalfScore());
            match.setScore(matchResult.getScore());
            match.setState(LotteryConstants.MATCHJJ_STATE_THREE);
            matchFootBallMapper.updateMatchFootBallResult(match);

        } catch (Exception e){
            logger.error("[保存竞彩足球赛果信息异常] matchCode=" + match.getMatchCode(), e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }
}
