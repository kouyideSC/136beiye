package com.caipiao.service.common;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.common.ActivityMapper;
import com.caipiao.dao.scheme.SchemeMapper;
import com.caipiao.dao.user.UserMapper;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.base.UserBean;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.common.Activity;
import com.caipiao.domain.common.ActivityAddBonus;
import com.caipiao.domain.common.ActivityUser;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.scheme.SchemeMatches;
import com.caipiao.domain.user.UserAddBonusDetail;
import com.caipiao.domain.vo.UserVo;
import com.caipiao.memcache.MemCached;
import com.caipiao.service.exception.ServiceException;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * 活动相关业务处理服务
 * Created by kouyi on 2017/11/15.
 */
@Service("activityService")
public class ActivityService {
    private static Logger logger = LoggerFactory.getLogger(ActivityService.class);
    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private SchemeMapper schemeMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MemCached memCached;

    /**
     * 根据条件查询活动列表
     * @param activity
     * @return
     */
    public List<Activity> queryActivityList(Activity activity) throws ServiceException {
        try {
            return activityMapper.queryActivityList(activity);
        } catch (Exception e) {
            logger.error("[查询活动列表异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 根据条件查询活动列表
     * @param lotteryId
     * @param passType
     * @param userId
     * @return
     * @throws ServiceException
     */
    public List<ActivityAddBonus> queryAddPrizeActivityList(String lotteryId, String passType, Long userId) throws ServiceException {
        try {
            return activityMapper.queryAddPrizeActivityList(lotteryId, passType, userId);
        } catch (Exception e) {
            logger.error("[查询加奖活动列表异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 根据彩种查询加奖活动列表
     * @param lotteryId
     * @return
     * @throws ServiceException
     */
    public List<ActivityAddBonus> queryLotteryAddActivityList(String lotteryId) throws ServiceException {
        try {
            return activityMapper.queryLotteryAddActivityList(lotteryId);
        } catch (Exception e) {
            logger.error("[根据彩种查询加奖活动列表异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 查询活动参与的所有用户
     * @param activityId
     * @return
     * @throws ServiceException
     */
    public List<Long> queryActivityJoinUser(Integer activityId) throws ServiceException {
        try {
            return activityMapper.queryActivityJoinUser(activityId);
        } catch (Exception e) {
            logger.error("[查询活动参与的所有用户列表异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 查询用户当日已经加奖金额
     * @author kouyi
     */
    public double queryUserDayAddprizeSum(Long userId, Integer activityId, String matchCode) throws ServiceException {
        try {
            return activityMapper.queryUserDayAddprizeSum(userId,activityId,matchCode);
        } catch (Exception e) {
            logger.error("[查询用户当日已经加奖金额异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 新增用户加奖流水
     * @author kouyi
     */
    public int insertUserAddBounsDetail(UserAddBonusDetail detail) throws ServiceException {
        try {
            return activityMapper.insertUserAddBounsDetail(detail);
        } catch (Exception e) {
            logger.error("[新增用户加奖流水异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 更新活动已使用额度
     * @author kouyi
     */
    public int updateAddBounsBalance(double balance, Integer id) throws ServiceException {
        try {
            return activityMapper.updateAddBounsBalance(balance, id);
        } catch (Exception e) {
            logger.error("[更新活动已使用额度异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 用户领取活动
     * @author kouyi
     */
    public void insertActivityUser(ActivityUser activityUser, ResultBean result) throws ServiceException {
        try {
            if(StringUtil.isEmpty(activityUser.getActivityId()) || StringUtil.isEmpty(activityUser.getUserId())) {
                throw new ServiceException(ErrorCode_API.ERROR_USER_APPLY_BONUS, MessageFormat.format(ErrorCode_API.ERROR_USER_APPLY_BONUS_MSG, new String[]{"非法活动编号"}));
            }
            String mkey = "USER_APPLY_ADDACTIVITY_"+ activityUser.getUserId() + "_"+activityUser.getActivityId();
            if(memCached.contains(mkey)) {
                throw new ServiceException(ErrorCode_API.ERROR_USER_APPLY_BONUS, MessageFormat.format(ErrorCode_API.ERROR_USER_APPLY_BONUS_MSG, new String[]{"重复领取活动资格"}));
            }
            UserVo userInfo = userMapper.queryUserInfoBalaceById(activityUser.getUserId());
            if (StringUtil.isEmpty(userInfo)) {
                throw new ServiceException(ErrorCode_API.ERROR_USER_APPLY_BONUS, MessageFormat.format(ErrorCode_API.ERROR_USER_APPLY_BONUS_MSG, new String[]{"用户信息不完整"}));
            }
            if (userInfo.getStatus().intValue() != 1) {//用户状态
                throw new ServiceException(ErrorCode_API.ERROR_USER_APPLY_BONUS, MessageFormat.format(ErrorCode_API.ERROR_USER_APPLY_BONUS_MSG, new String[]{"用户已被冻结或注销,请联系客服"}));
            }
            List<Dto> dtoList = activityMapper.queryAddBounsList(new BaseDto("id", activityUser.getActivityId()));
            if(StringUtil.isEmpty(dtoList)) {
                throw new ServiceException(ErrorCode_API.ERROR_USER_APPLY_BONUS, MessageFormat.format(ErrorCode_API.ERROR_USER_APPLY_BONUS_MSG, new String[]{"活动已过期或无效"}));
            }
            Dto activity = dtoList.get(0);
            if(activity.getAsInteger("status")==0) {
                throw new ServiceException(ErrorCode_API.ERROR_USER_APPLY_BONUS, MessageFormat.format(ErrorCode_API.ERROR_USER_APPLY_BONUS_MSG, new String[]{"活动已过期或无效"}));
            }
            //不再有效时间内<开始时间
            /*Date begin = activity.getAsDate("beginTime", DateUtil.DEFAULT_DATE_TIME);
            if(StringUtil.isNotEmpty(begin) && begin.getTime() > new Date().getTime()) {
                throw new ServiceException(ErrorCode_API.ERROR_USER_APPLY_BONUS, MessageFormat.format(ErrorCode_API.ERROR_USER_APPLY_BONUS_MSG, new String[]{"活动已过期或无效"}));
            }*/
            //不再有效时间内>结束时间
            Date end = activity.getAsDate("endTime", DateUtil.DEFAULT_DATE_TIME);
            if(StringUtil.isNotEmpty(end) && end.getTime() < new Date().getTime()) {
                throw new ServiceException(ErrorCode_API.ERROR_USER_APPLY_BONUS, MessageFormat.format(ErrorCode_API.ERROR_USER_APPLY_BONUS_MSG, new String[]{"活动已过期或无效"}));
            }
            //判断用户是否已经领取过活动资格,如果没领取过,则新增用户活动资格,否则不再增加用户的活动资格(仍提示领取成功)
            if(activityMapper.isUserJoinActivity(activityUser) <= 0) {
                //throw new ServiceException(ErrorCode_API.ERROR_USER_APPLY_BONUS, MessageFormat.format(ErrorCode_API.ERROR_USER_APPLY_BONUS_MSG, new String[]{"重复领取活动资格"}));
                activityMapper.insertActivityUser(activityUser);
                memCached.set(mkey, 1, 24*60*60);//领取成功用户 缓存一天
            }
        } catch (Exception e) {
            logger.error("[用户领取加奖活动异常] errorDesc=" + e.getMessage());
            int code = ErrorCode_API.SERVER_ERROR;
            if(e instanceof ServiceException) {
                code = ((ServiceException) e).getErrorCode();
            }
            throw new ServiceException(code, e.getMessage());
        }
    }

    /**
     * 查询用户是否参与任何活动
     * @param scheme
     * @return
     */
    public int isUserJoinActivityByUserId(Scheme scheme) {
        try {
            List<SchemeMatches> schemeMatches = null;
            if(scheme.getLotteryId().equals(LotteryConstants.JCZQ)) {
                schemeMatches = schemeMapper.querySchemeInfoByZqMatches(scheme.getId());
            }
            if(scheme.getLotteryId().equals(LotteryConstants.JCLQ)) {
                schemeMatches = schemeMapper.querySchemeInfoByLqMatches(scheme.getId());
            }
            if(StringUtil.isEmpty(schemeMatches)) {
                return 0;
            }

            List<ActivityAddBonus> activityAddBonusList = activityMapper.queryAddPrizeActivityList(scheme.getLotteryId(), schemeMatches.size()+"*1", scheme.getSchemeUserId());
            if(StringUtil.isEmpty(activityAddBonusList) || (StringUtil.isNotEmpty(activityAddBonusList) && activityAddBonusList.size() > 1)){
                return 0;//该方案用户没有参与对应活动||有多个对应活动[多个活动不加奖，所以需要返利]
            }
            ActivityAddBonus activityAddBonus = activityAddBonusList.get(0);
            String[] contents = scheme.getSchemeContent().split("\\|");
            String maxMatchCode = schemeMatches.get(0).getMatchCode();//取出最大场次(数据库查询倒序)确定订单归属日期
            //单关加奖方案验证购买场次必须一致
            if(schemeMatches.size() == 1 && contents[2].equals("1*1"))
            {
                if(!activityAddBonus.getMatchCode().equals(maxMatchCode)) {
                    return 0;
                } else{
                    return 1;
                }
            }
            //串关加奖验证
            else if(schemeMatches.size() > 1 && contents[2].equals((schemeMatches.size()+"*1")))
            {
                Date schemeAddTime = DateUtil.dateFormat(maxMatchCode+"235959", DateUtil.LOG_DATE_TIME2);
                int joinNum = 0;
                if((StringUtil.isNotEmpty(activityAddBonus.getBeginTime()) && activityAddBonus.getBeginTime().getTime() < schemeAddTime.getTime())
                        && (StringUtil.isNotEmpty(activityAddBonus.getEndTime()) && activityAddBonus.getEndTime().getTime() > schemeAddTime.getTime())) {
                    joinNum++;
                } else if(StringUtil.isEmpty(activityAddBonus.getBeginTime()) && (StringUtil.isNotEmpty(activityAddBonus.getEndTime()) && activityAddBonus.getEndTime().getTime() > schemeAddTime.getTime())) {
                    joinNum++;
                } else if(StringUtil.isEmpty(activityAddBonus.getEndTime()) && (StringUtil.isNotEmpty(activityAddBonus.getBeginTime()) && activityAddBonus.getBeginTime().getTime() < schemeAddTime.getTime())) {
                    joinNum++;
                } else if(StringUtil.isEmpty(activityAddBonus.getBeginTime()) && StringUtil.isEmpty(activityAddBonus.getEndTime())) {
                    joinNum++;
                } else {
                    return joinNum;//活动已过期
                }
                //星期限制
                if(StringUtil.isNotEmpty(activityAddBonus.getWeekLimit()) && activityAddBonus.getWeekLimit().indexOf(DateUtil.getWeekInt(schemeAddTime)+"") == -1) {
                    return 0;
                }
                //赛事限制
                if(StringUtil.isNotEmpty(activityAddBonus.getLeagueNameLimit())) {
                    for (SchemeMatches match : schemeMatches) {
                        if (activityAddBonus.getLeagueNameLimit().indexOf(match.getLotteryId()) == -1){
                            return 0;
                        }
                    }
                }
                return joinNum;
            }
            else
            {
                return 0;
            }
        } catch (Exception e) {
            logger.error("[查询用户是否参与活动异常] errorDesc=" + e.getMessage());
            return 0;
        }
    }
}
