package com.caipiao.service.match;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.lottery.JclqUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.match.MatchBasketBallMapper;
import com.caipiao.dao.match.MatchBasketBallSpMapper;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.match.MatchBasketBall;
import com.caipiao.domain.match.MatchBasketBallSp;
import com.caipiao.memcache.MemCached;
import com.caipiao.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 竞彩篮球赔率相关业务处理服务
 * Created by kouyi on 2017/11/07.
 */
@Service("jclqMatchSpService")
public class JclqMatchSpService {
    private static Logger logger = LoggerFactory.getLogger(JclqMatchSpService.class);
    @Autowired
    private MatchBasketBallSpMapper matchBasketBallSpMapper;
    @Autowired
    private MatchBasketBallMapper matchBasketBallMapper;
    @Autowired
    private MemCached memcache;

    /**
     * 保存或更新竞彩篮球对阵赔率信息
     * @param matchBasketBallSp
     */
    public boolean saveOrUpdateMatchSp(MatchBasketBallSp matchBasketBallSp, MatchBasketBall match) throws ServiceException, Exception {
        try {
            MatchBasketBallSp matchSp = matchBasketBallSpMapper.queryMatchBasketBallSpByMatchCode(matchBasketBallSp.getMatchCode());
            matchBasketBallSp.setMatchId(match.getId());
            matchBasketBallSp.setJcId(match.getJcId());
            matchBasketBallSp.setPeriod(match.getPeriod());
            //赔率更新
            if(StringUtil.isNotEmpty(matchSp)) {
                //数据没变化
                if(matchSp.getMatchSpInfo().equals(matchBasketBallSp.getMatchSpInfo())) {
                    return false;
                }
                logger.info("竞彩篮球赔率更新,最新赔率为：" + matchBasketBallSp.getMatchSpInfo());
                matchBasketBallSp.setId(matchSp.getId());
                matchBasketBallSpMapper.updateMatchBasketBallSp(matchBasketBallSp);
                memcache.delete(LotteryConstants.jclqSpPrefix + matchBasketBallSp.getMatchCode());//删除投注时赔率缓存
            }
            //该场赔率第一次抓取 直接入库
            else {
                matchBasketBallSpMapper.insertMatchBasketBallSp(matchBasketBallSp);
            }

            //更新对阵表让分和大小分
            if(StringUtil.isNotEmpty(matchBasketBallSp.getLose()) || StringUtil.isNotEmpty(matchBasketBallSp.getDxf())) {
                matchBasketBallMapper.updateMatchBasketBallLose(matchBasketBallSp.getLose(), matchBasketBallSp.getDxf(), match.getId());
            }
            return true;
        } catch (Exception e){
            logger.error("[保存或更新竞彩篮球赔率异常] matchCode=" + matchBasketBallSp.getMatchCode(), e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }
}
