package com.caipiao.taskcenter.user;

import com.caipiao.common.constants.UserConstants;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.service.config.SysConfig;
import com.caipiao.service.exception.ServiceException;
import com.caipiao.service.scheme.SchemeService;
import com.caipiao.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

/**
 * 用户销量统计任务
 * Created by Kouyi on 2018/03/30
 */
@Component("userSellTask")
public class UserSellTask {
    private static Logger logger = LoggerFactory.getLogger(UserSellTask.class);

    @Autowired
    private UserService userService;

    /**
     * 用户月销量统计处理
     * @author kouyi
     */
    public void userSellMoneyCommissionStatis() {
        try {
            if(SysConfig.getCommissionRate() <= 0) {
                return;
            }
            Calendar calendarLast = Calendar.getInstance();
            calendarLast.add(Calendar.MONTH, -1);
            calendarLast.set(Calendar.DAY_OF_MONTH, 1);
            calendarLast.set(Calendar.HOUR_OF_DAY, 0);
            calendarLast.set(Calendar.MINUTE, 0);
            calendarLast.set(Calendar.SECOND, 0);

            Calendar calendarCurr = Calendar.getInstance();
            calendarCurr.set(Calendar.DAY_OF_MONTH, 1);
            calendarCurr.set(Calendar.HOUR_OF_DAY, 0);
            calendarCurr.set(Calendar.MINUTE, 0);
            calendarCurr.set(Calendar.SECOND, 0);

            userService.statisSellMoneyCommission(calendarLast.getTime(), calendarCurr.getTime(), logger);
        } catch (Exception e) {
            logger.error("[用户月销量统计任务] 异常", e);
        }
    }

}
