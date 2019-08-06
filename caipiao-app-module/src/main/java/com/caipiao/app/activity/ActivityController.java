package com.caipiao.app.activity;

import com.caipiao.app.base.BaseController;
import com.caipiao.app.utils.WebUtils;
import com.caipiao.common.constants.KeyConstants;
import com.caipiao.common.constants.UserConstants;
import com.caipiao.domain.base.BaseBean;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.code.ErrorCode;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.common.ActivityUser;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.service.common.ActivityService;
import com.caipiao.service.common.MarketVersionService;
import com.caipiao.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 活动控制管理接口
 * Created by kouyi on 2018/04/12.
 */
@Controller
public class ActivityController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(ActivityController.class);
    @Autowired
    private ActivityService activityService;

    /**
     * 用户申请参与加奖活动
     * @param
     * @param request
     * @param response
     */
    @RequestMapping(value="/acp/applyBonus", method= RequestMethod.POST)
    public void addPrizeApply(HttpServletRequest request, HttpServletResponse response) {
        ResultBean result = new ResultBean();
        try {
            Dto params = WebUtils.getParamsAsDto(request);
            logger.info("[用户申请参与加奖活动] 接收原始参数=" + params.toString());
            ActivityUser activityUser = new ActivityUser();
            activityUser.setActivityType(1);//默认加奖活动
            activityUser.setActivityId(params.getAsInteger("aid"));//活动编号
            activityUser.setUserId(params.getAsLong("uid"));//用户编号
            activityService.insertActivityUser(activityUser, result);
            logger.info("[用户申请参与加奖活动] 返回处理结果=" + result.getErrorDesc());
        } catch (ServiceException e) {
            logger.error("[用户申请参与加奖活动] 服务异常! userId=" + getLoginUserId(request) + " errorDesc=" + e.getMessage());
            result.setErrorCode(e.getErrorCode());
            result.setErrorDesc(e.getMessage());
        } catch (Exception e) {
            logger.error("[用户申请参与加奖活动] 系统异常! userId=" + getLoginUserId(request) + " errorDesc=" + e.getMessage());
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result, response);
    }
}