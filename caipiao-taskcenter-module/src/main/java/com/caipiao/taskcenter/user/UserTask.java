package com.caipiao.taskcenter.user;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.util.CalculationUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.service.scheme.SchemeService;
import com.caipiao.service.ticket.TicketService;
import com.caipiao.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 用户统计任务
 * Created by Kouyi on 2018/1/12
 */
@Component("userTask")
public class UserTask {
    private static Logger logger = LoggerFactory.getLogger(UserTask.class);
    @Autowired
    private UserService userService;
    @Autowired
    private SchemeService schemeService;

    /**
     * 用户日报表数据统计
     * @author kouyi
     */
    public void userDayDateStatis() {
        try {
            String date = DateUtil.formatDate(new Date(), DateUtil.DEFAULT_DATE);
            if(DateUtil.getCurHour() == 0 && DateUtil.getCurMinute() < 10) {
                date = DateUtil.formatDate(DateUtil.addDay(new Date(), -1), DateUtil.DEFAULT_DATE);
            }
            userService.userDayDateStatis(date);
        } catch (Exception e) {
            logger.error("[用户日报表数据统计] 异常", e);
        }
    }

    /**
     * 更新有周榜数据但已经一周没有发神单的用户统计数据
     * @author kouyi
     */
    public void userFollowWeekDateStatis() {
        try {
            List<Long> userList = schemeService.queryWeekNoFollowUserList("1700");
            if(StringUtil.isEmpty(userList)) {
                return;
            }
            for(Long userId : userList) {
                Dto followStatis = new BaseDto();
                //***************************近一周数据统计********************************
                //查询最近一周用户神单数据
                followStatis.put("weekOrderSums", 0);
                followStatis.put("weekHitSums", 0);
                followStatis.put("weekHitRate", 0);
                followStatis.put("weekBuyMoney", 0);
                followStatis.put("weekHitMoney", 0);
                followStatis.put("weekWinRate", 0);
                followStatis.put("weekHitDescribe", 0 + "中" + 0);
                followStatis.put("weekRunRedSums", 0);
                followStatis.put("userId", userId);
                followStatis.put("lotteryId", LotteryConstants.JCZQ);
                //更新数据
                schemeService.updateUserFollowStatisInfo(followStatis);
            }
        } catch (Exception e) {
            logger.error("[更新有周榜数据但已经一周没有发神单的用户统计数据] 异常", e);
        }
    }

    /**
     * 查询有月榜数据但已经一月没有发神单的用户列表
     * @author kouyi
     */
    public void userFollowMonthDateStatis() {
        try {
            List<Long> userList = schemeService.queryMonthNoFollowUserList("1700");
            if(StringUtil.isEmpty(userList)) {
                return;
            }
            for(Long userId : userList) {
                Dto followStatis = new BaseDto();
                //***************************近一月数据统计********************************
                //查询最近一月用户神单数据
                followStatis.put("monthOrderSums", 0);
                followStatis.put("monthHitSums", 0);
                followStatis.put("monthHitRate", 0);
                followStatis.put("monthBuyMoney", 0);
                followStatis.put("monthHitMoney", 0);
                followStatis.put("monthWinRate", 0);
                followStatis.put("monthHitDescribe", 0 + "中" + 0);
                followStatis.put("monthRunRedSums", 0);//月连红不统计
                followStatis.put("userId", userId);
                followStatis.put("lotteryId", LotteryConstants.JCZQ);
                //更新数据
                schemeService.updateUserFollowStatisInfo(followStatis);
            }
        } catch (Exception e) {
            logger.error("[查询有月榜数据但已经一月没有发神单的用户列表] 异常", e);
        }
    }
}
