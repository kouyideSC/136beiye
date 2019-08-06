package com.caipiao.service.match;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.lottery.JclqUtils;
import com.caipiao.common.util.BeanUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.match.MatchBasketBallMapper;
import com.caipiao.domain.base.MatchBean;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.match.MatchBasketBall;
import com.caipiao.domain.vo.JclqAwardInfo;
import com.caipiao.domain.vo.JclqMatchVo;
import com.caipiao.memcache.MemCached;
import com.caipiao.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 竞彩篮球相关业务处理服务
 * Created by kouyi on 2017/11/07.
 */
@Service("jclqMatchService")
public class JclqMatchService {
    private static Logger logger = LoggerFactory.getLogger(JclqMatchService.class);
    @Autowired
    private MemCached memcache;
    @Autowired
    private MatchBasketBallMapper matchBasketBallMapper;

    /**
     * 根据竞彩场次号-查询竞彩篮球对阵信息-前端接口使用
     * @param bean
     * @return
     */
    public void queryMatchBasketBallByMatchCode(MatchBean bean, ResultBean result) throws ServiceException, Exception {
        try {
            //用户编号
            if (StringUtil.isEmpty(bean.getMatchCode())) {
                throw new ServiceException(ErrorCode_API.ERROR_100001);
            }

            MatchBasketBall match = matchBasketBallMapper.queryMatchBasketBallByMatchCode(bean.getMatchCode());
            result.setData(match);
        } catch (Exception e) {
            logger.error("[查询竞彩篮球对阵信息异常] bean=" + bean.getMatchCode() + " errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 根据参数-查询竞彩篮球可售对阵列表-前端接口展示使用
     * @return
     */
    public List<JclqMatchVo> queryJclqSaleMatchList() throws ServiceException, Exception {
        try {
            return matchBasketBallMapper.queryJclqSaleMatchList();
        } catch (Exception e) {
            logger.error("[查询竞彩篮球可售对阵列表异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }



    /**
     * 根据参数-查询竞彩篮球对阵列表
     * @param match
     * @return
     */
    public List<MatchBasketBall> queryMatchBasketBallList(MatchBasketBall match) throws ServiceException, Exception {
        try {
            return matchBasketBallMapper.queryMatchBasketBallList(match);
        } catch (Exception e) {
            logger.error("[查询竞彩篮球对阵列表异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 查询开赛时间在3天内且至少105分钟前，无赛果和等待抓取赛果的-竞彩篮球对阵列表
     * @return
     */
    public List<MatchBasketBall> queryMatchBasketBallNoResultList() throws ServiceException, Exception {
        try {
            return matchBasketBallMapper.queryMatchBasketBallNoResultList();
        } catch (Exception e) {
            logger.error("[查询开赛时间在3天内且至少100分钟前的竞彩篮球对阵列表异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 查询计奖状态为未处理的比赛-截止前5分钟
     * @return
     */
    public List<MatchBasketBall> queryJclqStatusNoHandlerList() throws ServiceException, Exception {
        try {
            return matchBasketBallMapper.queryJclqStatusNoHandlerList();
        } catch (Exception e) {
            logger.error("[查询竞彩篮球计奖状态为未处理的比赛列表异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 根据比赛唯一编号-更新比赛状态
     * @param match
     */
    public void updateMatchStatusById(JclqAwardInfo match) throws ServiceException, Exception {
        try {
            matchBasketBallMapper.updateMatchStatusById(match);
        } catch (Exception e){
            logger.error("[更新竞彩篮球场次状态异常] matchCode=" + match.getMatchCode(), e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 保存或更新竞彩篮球对阵信息
     * @param matchBasketBall
     */
    public boolean saveOrUpdateMatch(MatchBasketBall matchBasketBall) throws ServiceException, Exception {
        try {
            MatchBasketBall match = matchBasketBallMapper.queryMatchBasketBallByMatchCode(matchBasketBall.getMatchCode());
            //对阵更新
            if(StringUtil.isNotEmpty(match)) {
                //取消|截止|数据没变化|后台手工设置的场次不自动更新
                if(match.getStatus().intValue() == LotteryConstants.STATUS_EXPIRE
                        || match.getStatus().intValue() == LotteryConstants.STATUS_CANCEL
                        //|| match.getStatus().intValue() == LotteryConstants.STATUS_STOP
                        || match.getMatchInfo().equals(matchBasketBall.getMatchInfo()) || match.getUpdateFlag()) {
                    return false;
                }
                match.setMatchTime(matchBasketBall.getMatchTime());
                match.setEndTime(matchBasketBall.getEndTime());
                match.setHostName(matchBasketBall.getHostName());
                match.setGuestName(matchBasketBall.getGuestName());
                match.setLeagueName(matchBasketBall.getLeagueName());
                match.setPeriod(matchBasketBall.getPeriod());
                match.setJcWebId(matchBasketBall.getJcWebId());
                if(match.getSingleSfStatus() != LotteryConstants.STATUS_EXPIRE || match.getSingleSfStatus() != LotteryConstants.STATUS_CANCEL) {
                    match.setSingleSfStatus(matchBasketBall.getSingleSfStatus());
                }
                if(match.getSfStatus() != LotteryConstants.STATUS_EXPIRE || match.getSfStatus() != LotteryConstants.STATUS_CANCEL) {
                    match.setSfStatus(matchBasketBall.getSfStatus());
                }
                if(match.getSingleRfsfStatus() != LotteryConstants.STATUS_EXPIRE || match.getSingleRfsfStatus() != LotteryConstants.STATUS_CANCEL) {
                    match.setSingleRfsfStatus(matchBasketBall.getSingleRfsfStatus());
                }
                if(match.getRfsfStatus() != LotteryConstants.STATUS_EXPIRE || match.getRfsfStatus() != LotteryConstants.STATUS_CANCEL) {
                    match.setRfsfStatus(matchBasketBall.getRfsfStatus());
                }
                if(match.getSingleSfcStatus() != LotteryConstants.STATUS_EXPIRE || match.getSingleSfcStatus() != LotteryConstants.STATUS_CANCEL) {
                    match.setSingleSfcStatus(matchBasketBall.getSingleSfcStatus());
                }
                if(match.getSfcStatus() != LotteryConstants.STATUS_EXPIRE || match.getSfcStatus() != LotteryConstants.STATUS_CANCEL) {
                    match.setSfcStatus(matchBasketBall.getSfcStatus());
                }
                if(match.getSingleDxfStatus() != LotteryConstants.STATUS_EXPIRE || match.getSingleDxfStatus() != LotteryConstants.STATUS_CANCEL) {
                    match.setSingleDxfStatus(matchBasketBall.getSingleDxfStatus());
                }
                if(match.getDxfStatus() != LotteryConstants.STATUS_EXPIRE || match.getDxfStatus() != LotteryConstants.STATUS_CANCEL) {
                    match.setDxfStatus(matchBasketBall.getDxfStatus());
                }
                matchBasketBallMapper.updateMatchBasketBall(match);
            }
            //该场次第一次抓取 直接入库
            else {
                matchBasketBallMapper.insertMatchBasketBall(matchBasketBall);
            }
            return true;
        } catch (Exception e){
            logger.error("[保存或更新竞彩篮球对阵异常] matchCode=" + matchBasketBall.getMatchCode(), e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }
}
