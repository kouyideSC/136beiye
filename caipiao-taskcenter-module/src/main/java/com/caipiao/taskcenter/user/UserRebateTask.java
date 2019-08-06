package com.caipiao.taskcenter.user;

import com.caipiao.common.constants.UserConstants;
import com.caipiao.common.util.CalculationUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.user.UserAccount;
import com.caipiao.domain.user.UserRebate;
import com.caipiao.domain.user.UserRebateDetail;
import com.caipiao.service.exception.ServiceException;
import com.caipiao.service.scheme.SchemeService;
import com.caipiao.service.user.UserService;
import com.util.math.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 用户返利任务
 * Created by Kouyi on 2018/1/12
 */
@Component("userRebateTask")
public class UserRebateTask {
    private static Logger logger = LoggerFactory.getLogger(UserRebateTask.class);
    @Autowired
    private UserService userService;
    @Autowired
    private SchemeService schemeService;

    /**
     * 用户返利业务处理
     * @author kouyi
     */
    public void userRebateHandle() {
        try {
            List<Scheme> rebateSchemeList = schemeService.queryRebateSchemeList(1);
            if(StringUtil.isEmpty(rebateSchemeList)) {
                return;
            }
            for(Scheme scheme : rebateSchemeList) {
                try {
                    //判断方案是否需要返利 只有销售员和合作商户以及虚拟用户不返点
                    if (scheme.getClientSource() == UserConstants.USER_PROXY_SALE
                            || scheme.getChannelCode() == UserConstants.USER_TYPE_MOBILE
                            || scheme.getChannelCode() == UserConstants.USER_TYPE_OUTMONEY) {
                        continue;
                    }
                    userService.updateUserRebateAccount(scheme,logger);
                } catch (ServiceException e) {//返现失败
                    scheme.setBackStatus(-1);
                    userService.updateSchemeBackStatus(scheme);
                }
            }
        } catch (Exception e) {
            logger.error("[用户返利任务处理] 异常", e);
        }
    }

}
