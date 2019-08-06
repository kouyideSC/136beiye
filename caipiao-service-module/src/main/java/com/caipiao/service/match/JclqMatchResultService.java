package com.caipiao.service.match;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.lottery.JclqUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.match.MatchBasketBallMapper;
import com.caipiao.dao.match.MatchBasketBallResultMapper;
import com.caipiao.dao.match.MatchBasketBallSpMapper;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.match.MatchBasketBall;
import com.caipiao.domain.match.MatchBasketBallResult;
import com.caipiao.domain.match.MatchBasketBallSp;
import com.caipiao.domain.vo.JclqResultVo;
import com.caipiao.domain.vo.JczqResultVo;
import com.caipiao.memcache.MemCached;
import com.caipiao.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 竞彩篮球赛果相关业务处理服务
 * Created by kouyi on 2017/11/07.
 */
@Service("jclqMatchResultService")
public class JclqMatchResultService {
    private static Logger logger = LoggerFactory.getLogger(JclqMatchResultService.class);
    @Autowired
    private MatchBasketBallResultMapper matchBasketBallResultMapper;
    @Autowired
    private MatchBasketBallSpMapper matchBasketBallSpMapper;
    @Autowired
    private MatchBasketBallMapper matchBasketBallMapper;

    /**
     * 根据参数-查询竞彩篮球赛果列表-前端接口展示使用
     * @return
     */
    public List<JclqResultVo> queryJclqResultList(String beginPeriod, String endPeriod) throws ServiceException, Exception {
        try {
            return matchBasketBallResultMapper.queryJclqResultList(beginPeriod, endPeriod);
        } catch (Exception e) {
            logger.error("[查询竞彩篮球赛果列表异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 保存竞彩篮球赛果信息
     * @param matchResult
     * @param match
     * @throws ServiceException
     * @throws Exception
     */
    public void saveMatchResult(MatchBasketBallResult matchResult, MatchBasketBall match) throws ServiceException, Exception {
        try {
            MatchBasketBallSp matchSp = matchBasketBallSpMapper.queryMatchBasketBallSpByMatchCode(match.getMatchCode());
            if(StringUtil.isEmpty(matchSp)) {
                return;
            }

            //根据比分计算篮球赛果
            JclqUtils.getBasketballResult(matchResult, matchSp, match);
            //属性初始化
            matchResult.setMatchTime(match.getMatchTime());
            matchResult.setStatus(match.getStatus());
            matchResult.setLose(match.getLose());
            MatchBasketBallResult result = matchBasketBallResultMapper.queryJclqResultInfo(match.getMatchCode());
            if(StringUtil.isEmpty(result)) {
                matchResult.setLeagueName(match.getLeagueName());
                matchResult.setMatchId(match.getId());
                matchResult.setMatchCode(match.getMatchCode());
                matchResult.setJcId(match.getWeekDay()+match.getJcId());
                matchResult.setPeriod(match.getPeriod());
                matchResult.setHostName(match.getHostName());
                matchResult.setGuestName(match.getGuestName());
                matchBasketBallResultMapper.insertBasketBallResult(matchResult);
            } else {
                matchResult.setId(result.getId());
                matchBasketBallResultMapper.updateJclqResult(matchResult);
            }
            //更新对阵表比分赛果
            match.setHalfScore(matchResult.getHalfScore());
            match.setScore(matchResult.getScore());
            match.setState(LotteryConstants.MATCHJJ_STATE_THREE);
            matchBasketBallMapper.updateMatchBasketBallResult(match);
        } catch (Exception e){
            logger.error("[保存竞彩篮球赛果信息异常] matchCode=" + match.getMatchCode(), e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }
}
