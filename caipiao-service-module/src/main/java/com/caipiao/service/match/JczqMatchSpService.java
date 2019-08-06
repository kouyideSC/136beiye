package com.caipiao.service.match;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.lottery.JczqUtils;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.util.BeanUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.match.MatchFootBallMapper;
import com.caipiao.dao.match.MatchFootBallSpMapper;
import com.caipiao.domain.base.MatchBean;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.match.MatchFootBall;
import com.caipiao.domain.match.MatchFootBallSp;
import com.caipiao.memcache.MemCached;
import com.caipiao.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 竞彩足球赔率相关业务处理服务
 * Created by kouyi on 2017/11/07.
 */
@Service("jczqMatchSpService")
public class JczqMatchSpService {
    private static Logger logger = LoggerFactory.getLogger(JczqMatchSpService.class);
    @Autowired
    private MatchFootBallSpMapper matchFootBallSpMapper;
    @Autowired
    private MatchFootBallMapper matchFootBallMapper;
    @Autowired
    private MemCached memcache;

    /**
     * 保存或更新竞彩足球对阵赔率信息
     * @param matchFootBallSp
     */
    public boolean saveOrUpdateMatchSp(MatchFootBallSp matchFootBallSp, MatchFootBall match) throws ServiceException, Exception {
        try {
            MatchFootBallSp matchSp = matchFootBallSpMapper.queryMatchFootBallSpByMatchCode(matchFootBallSp.getMatchCode());
            matchFootBallSp.setMatchId(match.getId());
            matchFootBallSp.setJcId(match.getJcId());
            matchFootBallSp.setPeriod(match.getPeriod());
            //赔率更新
            if(StringUtil.isNotEmpty(matchSp)) {
                //数据没变化
                if(matchSp.getMatchSpInfo().equals(matchFootBallSp.getMatchSpInfo())) {
                    return false;
                }
                matchFootBallSp.setId(matchSp.getId());
                logger.info("竞彩足球赔率更新,最新赔率：" + matchFootBallSp.getMatchSpInfo());
                matchFootBallSpMapper.updateMatchFootBallSp(matchFootBallSp);
                memcache.delete(LotteryConstants.jczqSpPrefix + matchFootBallSp.getMatchCode());//删除投注时赔率缓存
            }
            //该场赔率第一次抓取 直接入库
            else {
                matchFootBallSpMapper.insertMatchFootBallSp(matchFootBallSp);
            }

            //更新对阵表让球数
            if(StringUtil.isNotEmpty(matchFootBallSp.getLose())) {
                matchFootBallMapper.updateMatchFootBallLose(matchFootBallSp.getLose(), match.getId());
            }
            return true;
        } catch (Exception e){
            logger.error("[保存或更新竞彩足球赔率异常] matchCode=" + matchFootBallSp.getMatchCode(), e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }
}
