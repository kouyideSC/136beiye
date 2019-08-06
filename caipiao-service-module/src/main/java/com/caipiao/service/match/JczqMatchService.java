package com.caipiao.service.match;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.lottery.JczqUtils;
import com.caipiao.common.util.BeanUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.match.MatchFootBallMapper;
import com.caipiao.domain.base.MatchBean;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.match.MatchFootBall;
import com.caipiao.domain.vo.JczqAwardInfo;
import com.caipiao.domain.vo.JczqMatchVo;
import com.caipiao.memcache.MemCached;
import com.caipiao.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 竞彩足球相关业务处理服务
 * Created by kouyi on 2017/11/07.
 */
@Service("jczqMatchService")
public class JczqMatchService {
    private static Logger logger = LoggerFactory.getLogger(JczqMatchService.class);
    @Autowired
    private MemCached memcache;
    @Autowired
    private MatchFootBallMapper matchFootBallMapper;

    /**
     * 根据竞彩场次号-查询竞彩足球对阵信息-前端接口使用
     * @param bean
     * @return
     */
    public void queryMatchFootBallByMatchCode(MatchBean bean, ResultBean result) throws ServiceException, Exception {
        try {
            //用户编号
            if (StringUtil.isEmpty(bean.getMatchCode())) {
                throw new ServiceException(ErrorCode_API.ERROR_100001);
            }

            MatchFootBall match = matchFootBallMapper.queryMatchFootBallByMatchCode(bean.getMatchCode());
            result.setData(match);
        } catch (Exception e) {
            logger.error("[查询竞彩足球对阵信息异常] bean=" + bean.getMatchCode() + " errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 根据参数-查询竞彩足球可售对阵列表-前端接口展示使用
     * @return
     */
    public List<JczqMatchVo> queryJczqSaleMatchList() throws ServiceException, Exception {
        try {
            return matchFootBallMapper.queryJczqSaleMatchList();
        } catch (Exception e) {
            logger.error("[查询竞彩足球可售对阵列表异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 根据参数-查询竞彩足球对阵列表
     * @param match
     * @return
     */
    public List<MatchFootBall> queryMatchFootBallList(MatchFootBall match) throws ServiceException, Exception {
        try {
            return matchFootBallMapper.queryMatchFootBallList(match);
        } catch (Exception e) {
            logger.error("[查询竞彩足球对阵列表异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 查询开赛时间在3天内且至少105分钟前，无赛果和等待抓取赛果的-竞彩足球对阵列表
     * @return
     */
    public List<MatchFootBall> queryMatchFootBallNoResultList() throws ServiceException, Exception {
        try {
            return matchFootBallMapper.queryMatchFootBallNoResultList();
        } catch (Exception e) {
            logger.error("[查询开赛时间在3天内且至少105分钟前的竞彩足球对阵列表异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 查询计奖状态为未处理的比赛-截止前5分钟
     * @return
     */
    public List<MatchFootBall> queryJczqStatusNoHandlerList() throws ServiceException, Exception {
        try {
            return matchFootBallMapper.queryJczqStatusNoHandlerList();
        } catch (Exception e) {
            logger.error("[查询竞彩足球计奖状态为未处理的比赛列表异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 根据比赛唯一编号-更新比赛状态
     * @param match
     */
    public void updateMatchStatusById(JczqAwardInfo match) throws ServiceException, Exception {
        try {
            matchFootBallMapper.updateMatchStatusById(match);
        } catch (Exception e){
            logger.error("[更新竞彩足球场次状态异常] matchCode=" + match.getMatchCode(), e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 保存或更新竞彩足球对阵信息
     * @param matchFootBall
     */
    public boolean saveOrUpdateMatch(MatchFootBall matchFootBall) throws ServiceException, Exception {
        try {
            MatchFootBall match = matchFootBallMapper.queryMatchFootBallByMatchCode(matchFootBall.getMatchCode());
            //对阵更新
            if(StringUtil.isNotEmpty(match)) {
                //取消|截止|数据没变化|后台手工设置的场次不自动更新
                if(match.getStatus().intValue() == LotteryConstants.STATUS_EXPIRE
                        || match.getStatus().intValue() == LotteryConstants.STATUS_CANCEL
                        //|| (match.getStatus().intValue() == LotteryConstants.STATUS_STOP && match.getUpdateFlag())
                        || match.getMatchInfo().equals(matchFootBall.getMatchInfo()) || match.getUpdateFlag()) {
                    return false;
                }
                match.setMatchTime(matchFootBall.getMatchTime());
                match.setEndTime(matchFootBall.getEndTime());
                match.setHostName(matchFootBall.getHostName());
                match.setGuestName(matchFootBall.getGuestName());
                match.setLeagueName(matchFootBall.getLeagueName());
                match.setPeriod(matchFootBall.getPeriod());
                match.setJcWebId(matchFootBall.getJcWebId());
                if(match.getSingleSpfStatus() != LotteryConstants.STATUS_EXPIRE || match.getSingleSpfStatus() != LotteryConstants.STATUS_CANCEL) {
                    match.setSingleSpfStatus(matchFootBall.getSingleSpfStatus());
                }
                if(match.getSpfStatus() != LotteryConstants.STATUS_EXPIRE || match.getSpfStatus() != LotteryConstants.STATUS_CANCEL) {
                    match.setSpfStatus(matchFootBall.getSpfStatus());
                }
                if(match.getSingleRqspfStatus() != LotteryConstants.STATUS_EXPIRE || match.getSingleRqspfStatus() != LotteryConstants.STATUS_CANCEL) {
                    match.setSingleRqspfStatus(matchFootBall.getSingleRqspfStatus());
                }
                if(match.getRqspfStatus() != LotteryConstants.STATUS_EXPIRE || match.getRqspfStatus() != LotteryConstants.STATUS_CANCEL) {
                    match.setRqspfStatus(matchFootBall.getRqspfStatus());
                }
                if(match.getSingleBfStatus() != LotteryConstants.STATUS_EXPIRE || match.getSingleBfStatus() != LotteryConstants.STATUS_CANCEL) {
                    match.setSingleBfStatus(matchFootBall.getSingleBfStatus());
                }
                if(match.getBfStatus() != LotteryConstants.STATUS_EXPIRE || match.getBfStatus() != LotteryConstants.STATUS_CANCEL) {
                    match.setBfStatus(matchFootBall.getBfStatus());
                }
                if(match.getSingleZjqStatus() != LotteryConstants.STATUS_EXPIRE || match.getSingleZjqStatus() != LotteryConstants.STATUS_CANCEL) {
                    match.setSingleZjqStatus(matchFootBall.getSingleZjqStatus());
                }
                if(match.getZjqStatus() != LotteryConstants.STATUS_EXPIRE || match.getZjqStatus() != LotteryConstants.STATUS_CANCEL) {
                    match.setZjqStatus(matchFootBall.getZjqStatus());
                }
                if(match.getSingleBqcStatus() != LotteryConstants.STATUS_EXPIRE || match.getSingleBqcStatus() != LotteryConstants.STATUS_CANCEL) {
                    match.setSingleBqcStatus(matchFootBall.getSingleBqcStatus());
                }
                if(match.getBqcStatus() != LotteryConstants.STATUS_EXPIRE || match.getBqcStatus() != LotteryConstants.STATUS_CANCEL) {
                    match.setBqcStatus(matchFootBall.getBqcStatus());
                }
                matchFootBallMapper.updateMatchFootBall(match);
            }
            //该场次第一次抓取 直接入库
            else {
                matchFootBallMapper.insertMatchFootBall(matchFootBall);
            }
            return true;
        } catch (Exception e){
            logger.error("[保存或更新竞彩足球对阵异常] matchCode=" + matchFootBall.getMatchCode(), e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 根据竞彩场次号-查询竞彩足球对阵信息
     * @param matchCode
     * @return
     */
    public MatchFootBall queryMatchFootBallByMatchCode(String matchCode) throws Exception {
        try {
            return matchFootBallMapper.queryMatchFootBallByMatchCode(matchCode);
        } catch (Exception e) {
            logger.error("[查询竞彩足球对阵信息异常] bean=" + matchCode + " errorDesc=" + e.getMessage());
            return null;
        }
    }
}
