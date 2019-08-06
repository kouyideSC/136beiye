package com.caipiao.app.lottery;

import com.caipiao.app.base.BaseController;
import com.caipiao.common.constants.KeyConstants;
import com.caipiao.common.constants.UserConstants;
import com.caipiao.common.util.ReflectionToString;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.base.BaseBean;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.base.SchemeBean;
import com.caipiao.domain.code.ErrorCode;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.jjyh.JjyhTwo;
import com.caipiao.service.common.MarketVersionService;
import com.caipiao.service.exception.ServiceException;
import com.caipiao.service.scheme.SchemeService;
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
 * 奖金优化控制类
 * Created by kouyi on 2018/5/22.
 */
@Controller
@RequestMapping("/jjyh")
public class JjyhController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(JjyhController.class);

    @Autowired
    private SchemeService schemeService;

    /**
     * 竞彩奖金优化
     * @author kouyi
     */
    @RequestMapping(value="/jcjjyh", method= RequestMethod.POST)
    public void jcJJYH(SchemeBean schemeBean, HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        try
        {
            logger.info("[竞彩奖金优化] 接收原始参数:" + ReflectionToString.toString(schemeBean));
            schemeService.jcJJYHCalculate(schemeBean, result);//调用奖金优化方法
        }
        catch (ServiceException e1)
        {
            logger.error("[竞彩奖金优化]服务发生异常! 异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[竞彩奖金优化]系统发生异常! 异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result, JjyhTwo.filter, response);
    }
}