package com.caipiao.app.lottery;

import com.caipiao.app.base.BaseController;
import com.caipiao.app.utils.WebUtils;
import com.caipiao.common.constants.KeyConstants;
import com.caipiao.common.constants.UserConstants;
import com.caipiao.common.util.DateUtil;
import com.caipiao.domain.base.BaseBean;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.code.ErrorCode;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.common.AppMarket;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.service.common.MarketVersionService;
import com.caipiao.service.exception.ServiceException;
import com.caipiao.service.kaijiang.KaiJiangService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 市场版本更新控制类
 * Created by kouyi on 2018/1/5.
 */
@Controller
@RequestMapping("/market")
public class MarketVersionController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(MarketVersionController.class);

    @Autowired
    private MarketVersionService marketVersionService;

    /**
     * 获取市场安装包版本信息
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping(value="/getVersion")
    public void getMarketVersion(BaseBean bean, HttpServletRequest request, HttpServletResponse response) {
        ResultBean result = new ResultBean();
        try {
            if (KeyConstants.loginUserMap.containsKey(bean.getAppId())) {
                Integer clientType = KeyConstants.loginUserMap.get(bean.getAppId());
                if(clientType == UserConstants.USER_SOURCE_IOS) {
                    clientType = 0;
                } else if(clientType == UserConstants.USER_SOURCE_ANDROID) {
                    clientType = 1;
                } else {
                    clientType = 1;
                }
                Dto params = new BaseDto();//获取请求参数
                params.put("clientType", clientType + "");
                params.put("marketName", bean.getMarketFrom());
                params.put("buildVersion", bean.getVersion());
                marketVersionService.queryMarketVersionList(params, result);
            } else {
                logger.error("[获取市场安装包版本信息] appId不合法");
                result.setErrorCode(ErrorCode_API.ERROR_USER_110015);
            }
        } catch (ServiceException e) {
            logger.error("[获取市场安装包版本信息] 服务异常 marketName=" + bean.getMarketFrom() + " errorDesc=" + e.getMessage());
            result.setErrorCode(e.getErrorCode());
        } catch (Exception e) {
            logger.error("[获取市场安装包版本信息] 系统异常 marketName=" + bean.getMarketFrom() + " errorDesc=" + e.getMessage());
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        List<String> filters = new ArrayList<String>();
        filters.add("id");
        filters.add("appName");
        filters.add("clientType");
        filters.add("updateTime");
        filters.add("marketId");
        writeResponse(result, filters, response);
    }
}