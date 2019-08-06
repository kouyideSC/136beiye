package com.caipiao.service.lottery;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.check.CheckMapper;
import com.caipiao.dao.lottery.LotteryMapper;
import com.caipiao.dao.lottery.PeriodMapper;
import com.caipiao.dao.user.UserFollowMapper;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.lottery.Lottery;
import com.caipiao.domain.lottery.Period;
import com.caipiao.domain.vo.LotteryVo;
import com.caipiao.service.config.SysConfig;
import com.caipiao.service.exception.ServiceException;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 彩种相关业务处理服务
 * Created by kouyi on 2017/11/10.
 */
@Service("lotteryService")
public class LotteryService {
    private static Logger logger = LoggerFactory.getLogger(LotteryService.class);
    @Autowired
    private LotteryMapper lotteryMapper;
    @Autowired
    private CheckMapper checkMapper;
    @Autowired
    private UserFollowMapper userFollowMapper;

    /**
     * 查询在售彩种列表
     * @return
     */
    public List<LotteryVo> queryLotterySaleList(Dto params) throws Exception {
        return lotteryMapper.queryLotterySaleList(params);
    }

    /**
     * 查询彩种编号查询彩种信息
     * @param lotteryId
     * @return
     */
    public Lottery queryLotteryInfo(String lotteryId) throws Exception {
        return lotteryMapper.queryLotteryInfo(lotteryId);
    }

    /**
     * 获取关于我们
     * @author  mcdog
     */
    public String getAboutUs() throws Exception
    {
        return SysConfig.getString("ABOUT_US");
    }

    /**
     * 获取用户协议
     * @author  mcdog
     */
    public String getUserAgreement() throws Exception
    {
        return SysConfig.getString("USERS_AGREEMENT");
    }

    /**
     * 查询世界杯期间某天的用户购彩榜单
     * @param dateStr
     * @return
     * @throws ServiceException
     */
    public List<Dto> getWorldCupRankList(String dateStr) throws ServiceException {
        try {
            //日期
            if (StringUtil.isEmpty(dateStr)) {
                return null;
            }
            return checkMapper.getWorldCupRankList(dateStr);
        } catch (Exception e) {
            logger.error("[查询世界杯期间某天的用户购彩榜单异常] dateStr=" + dateStr + " errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

}
