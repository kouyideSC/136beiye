package com.caipiao.taskcenter.Test;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.UserConstants;
import com.caipiao.common.util.CalculationUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.common.ActivityAddBonus;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.scheme.SchemeMatches;
import com.caipiao.domain.user.User;
import com.caipiao.domain.user.UserAddBonusDetail;
import com.caipiao.memcache.MemCached;
import com.caipiao.service.common.ActivityService;
import com.caipiao.service.scheme.SchemeService;
import com.caipiao.service.user.UserService;
import com.caipiao.taskcenter.award.*;
import com.caipiao.taskcenter.award.util.JczqAwardUtil;
import com.caipiao.taskcenter.award.util.LoggerUtil;
import com.caipiao.taskcenter.award.util.PrizesUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 执行抓取开奖号码任务测试类
 * Created by Kouyi on 2017/11/11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring/spring-*.xml" })
public class AwardTest {
    @Autowired
    private KpPeriodTask kpNumberPeriodTask;
    @Autowired
    private MpPeriodTask mpPeriodTask;
    @Autowired
    private JczqMatchTask jczqMatchTask;
    @Autowired
    private JclqMatchTask jclqMatchTask;
    @Autowired
    private SchemeService schemeService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private UserService userService;
    @Autowired
    private GyjMatchTask gyjMatchTask;
    @Autowired
    private MemCached memCached;

    @Test
    public void testCreateFileData() throws Exception {
        //kpNumberPeriodTask.kpPeriodProcessTask();
        jczqMatchTask.jinCaiZqMatchTask();
        //jclqMatchTask.jinCaiLqMatchTask();
        //mpPeriodTask.mpPeriodProcessTask();
        //gyjMatchTask.gyjProcessTask();
        /*Scheme scheme = new Scheme();
        scheme.setSchemeUserId(87l);
        scheme.setId(331204l);
        scheme.setSchemeContent("HH|20180708104>RQSPF=0,20180708103>RQSPF=3|2*1");
        scheme.setPrizeTax(999.46d);
        addPrizeSummary(schemeService, activityService, userService, scheme, "", memCached);*/

    }

    public static synchronized double addPrizeSummary(SchemeService schemeService, ActivityService activityService,
                                                      UserService userService, Scheme scheme, String matchCode, MemCached memCached) {
        try{
            double addPrize = 0;
            User user = null;
            String usKey = "award_us_" + scheme.getSchemeUserId();
            //出款账户不加奖
            if(memCached.contains(usKey)) {
                user = (User) memCached.get(usKey);
            } else {
                user = userService.queryUserInfoByAward(scheme.getSchemeUserId());
                if(StringUtil.isEmpty(user)){
                    return addPrize;
                }
                memCached.set(usKey, user, 12 * 60 * 60);//缓存12小时
            }
            if(user.getUserType() == UserConstants.USER_TYPE_OUTMONEY) {
                return addPrize;
            }
            List<SchemeMatches> matchesList = schemeService.querySchemeInfoByMatches(LotteryConstants.JCZQ, scheme.getId());
            if(StringUtil.isEmpty(matchesList)) {
                return addPrize;
            }

            //查询彩种相关的加奖活动
            List<ActivityAddBonus> activityAddBonusList = null;
            String acKey = "addbonus_ac_" + LotteryConstants.JCZQ;
            if(memCached.contains(acKey)) {
                activityAddBonusList = (List<ActivityAddBonus>) memCached.get(acKey);
            } else {
                activityAddBonusList = activityService.queryLotteryAddActivityList(LotteryConstants.JCZQ);
                if(StringUtil.isEmpty(activityAddBonusList)){
                    return addPrize;
                }
                memCached.set(acKey, activityAddBonusList, 10 * 60);//缓存10分钟
            }

            for(ActivityAddBonus activityAddBonus : activityAddBonusList) {
                if(!activityAddBonus.getPassType().equalsIgnoreCase(matchesList.size()+"*1")) {//串关方式匹配
                    continue;
                }
                if (StringUtil.isEmpty(activityAddBonus.getAddBonusRate())) {
                    continue;//未正确配置加奖比例
                }
                //查询参与活动的所有用户
                List<Long> joinUserList = null;
                String acJoinKey = "userid_join_" + activityAddBonus.getId();
                if(memCached.contains(acJoinKey)) {
                    joinUserList = (List<Long>) memCached.get(acJoinKey);
                } else {
                    joinUserList = activityService.queryActivityJoinUser(activityAddBonus.getId());
                    if(StringUtil.isEmpty(joinUserList)){
                        continue;
                    }
                    memCached.set(acJoinKey, joinUserList, 10 * 60);//缓存10分钟
                }
                if(!joinUserList.contains(scheme.getSchemeUserId())) {
                    continue;//用户未参与该加奖活动
                }
                String[] contents = scheme.getSchemeContent().split("\\|");
                if (contents.length != 3) {
                    continue;
                }

                String maxMatchCode = matchesList.get(0).getMatchCode();//取出最大场次(数据库查询倒序)确定订单归属日期
                //单关加奖方案验证购买场次必须一致
                if (matchesList.size() == 1 && contents[2].equals("1*1")) {
                    if (!activityAddBonus.getMatchCode().equals(maxMatchCode)) {
                        continue;
                    }
                }
                //串关加奖验证
                else if (matchesList.size() > 1 && contents[2].equals((matchesList.size() + "*1"))) {
                    maxMatchCode = maxMatchCode.substring(0, 8);//串关加奖订单归属必须使用日期
                    Date schemeAddTime = DateUtil.dateFormat(maxMatchCode + "235959", DateUtil.LOG_DATE_TIME2);
                    //不再有效时间内<开始时间
                    if (StringUtil.isNotEmpty(activityAddBonus.getBeginTime()) && activityAddBonus.getBeginTime().getTime() > schemeAddTime.getTime()) {
                        continue;
                    }
                    //不再有效时间内>结束时间
                    if (StringUtil.isNotEmpty(activityAddBonus.getEndTime()) && activityAddBonus.getEndTime().getTime() < schemeAddTime.getTime()) {
                        continue;
                    }
                    //星期限制
                    if (StringUtil.isNotEmpty(activityAddBonus.getWeekLimit()) && activityAddBonus.getWeekLimit().indexOf(DateUtil.getWeekInt(schemeAddTime) + "") == -1) {
                        continue;
                    }
                    //赛事限制
                    if (StringUtil.isNotEmpty(activityAddBonus.getLeagueNameLimit())) {
                        for (SchemeMatches match : matchesList) {
                            if (activityAddBonus.getLeagueNameLimit().indexOf(match.getLotteryId()) == -1) {
                                continue;
                            }
                        }
                    }
                } else {
                    continue;//其他订单不满足加奖条件
                }

                if (activityAddBonus.getBalance() >= activityAddBonus.getMaxMoney()) {
                    continue;//可用额度不足
                }
                Map<String, Double> rateMap = PrizesUtil.initRateMap(activityAddBonus.getAddBonusRate());//格式化加奖比例
                //查询用户当前期已经加奖金额
                double userDayAddPrizeSum = activityService.queryUserDayAddprizeSum(scheme.getSchemeUserId(), activityAddBonus.getId(), maxMatchCode);
                if (userDayAddPrizeSum >= activityAddBonus.getUserDayLimit()) {
                    continue;
                }
                //计算加奖奖金
                double rate = PrizesUtil.getAddBonusRate(rateMap, scheme.getPrizeTax());
                addPrize = CalculationUtils.muld(rate, scheme.getPrizeTax());
                //当前加奖金额+用户单日已加奖金额>用户单日加奖限制，则计算差值
                if (CalculationUtils.add(addPrize, userDayAddPrizeSum) > activityAddBonus.getUserDayLimit()) {
                    addPrize = CalculationUtils.sub(activityAddBonus.getUserDayLimit(), userDayAddPrizeSum);
                }
                //当前加奖金额+活动已使用额度>活动总额度，则计算差值
                if (CalculationUtils.add(addPrize, activityAddBonus.getBalance()) > activityAddBonus.getMaxMoney()) {
                    addPrize = CalculationUtils.sub(activityAddBonus.getMaxMoney(), activityAddBonus.getBalance());
                }
                //满足加奖条件
                if (addPrize > 0) {
                    addPrize = CalculationUtils.spValue(addPrize);
                    //记录订单加奖流水
                    UserAddBonusDetail detail = new UserAddBonusDetail();
                    detail.setUserId(scheme.getSchemeUserId());
                    detail.setSchemeOrderId(scheme.getSchemeOrderId());
                    detail.setSchemeMoney(scheme.getSchemeMoney());
                    detail.setActivityId(activityAddBonus.getId());
                    detail.setLotteryId(scheme.getLotteryId());
                    detail.setRateRange(rate + "");
                    detail.setAddPrizeTax(addPrize);
                    detail.setPrizeTax(scheme.getPrizeTax());
                    detail.setAddPrizeDateStr(maxMatchCode);
                    detail.setLastBalance(activityAddBonus.getBalance());
                    detail.setCurrBalance(CalculationUtils.add(activityAddBonus.getBalance(), addPrize));
                    int row = activityService.insertUserAddBounsDetail(detail);
                    if (row > 0) {//流水添加成功
                        activityService.updateAddBounsBalance(addPrize, activityAddBonus.getId());//更新使用额度
                    }
                    break;//同一订单不能参与多个加奖活动
                }
            }
            return addPrize;
        } catch (Exception e){
            return 0;
        }
    }

}
