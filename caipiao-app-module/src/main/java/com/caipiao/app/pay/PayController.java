package com.caipiao.app.pay;

import com.caipiao.app.base.BaseController;
import com.caipiao.app.utils.WebUtils;
import com.caipiao.common.constants.PayConstants;
import com.caipiao.common.encrypt.AuthSign;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.base.UserBean;
import com.caipiao.domain.code.ErrorCode;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.service.config.SysConfig;
import com.caipiao.service.exception.ServiceException;
import com.caipiao.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.caipiao.service.pay.PayService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 支付接口控制类
 */
@Controller
@RequestMapping("/pay")
public class PayController extends BaseController
{
    private static Logger logger = LoggerFactory.getLogger(PayController.class);

    @Autowired
    private PayService payService;

    @Autowired
    private UserService userService;

    /**
     * 获取充值方式
     * @author  mcdog
     */
    @RequestMapping(value="/getPayWays")
    public void getRechargeMethods(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//获取参数
            logger.info("[获取充值方式]用户编号=" + getLoginUserId(request) + ",接收原始参数=" + params.toString());
            params.put("userId",getLoginUserId(request));//获取用户编号
            payService.getNewPayways(params,result);//获取支付方式
        }
        catch (ServiceException e1)
        {
            logger.error("[获取充值方式]服务异常!用户编号=" + getLoginUserId(request) + ",异常信息：" + e1);
            result.setErrorCode(StringUtil.isEmpty(e1.getErrorCode())? ErrorCode.SERVER_ERROR : e1.getErrorCode());
            result.setErrorDesc(StringUtil.isEmpty(e1.getMessage())? ErrorCode.SERVER_ERROR_MSG : e1.getMessage());
        }
        catch (Exception e)
        {
            logger.error("[获取充值方式]系统异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 获取充值方式
     * @author  mcdog
     */
    @RequestMapping(value="/way/get")
    public void getPayways(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//获取参数
            logger.info("[获取充值方式]用户编号=" + getLoginUserId(request) + ",接收原始参数=" + params.toString());
            params.put("userId",getLoginUserId(request));//获取用户编号
            payService.getPayways(params,result);//获取支付方式
        }
        catch (ServiceException e1)
        {
            logger.error("[获取充值方式]服务异常!用户编号=" + getLoginUserId(request) + ",异常信息：" + e1);
            result.setErrorCode(StringUtil.isEmpty(e1.getErrorCode())? ErrorCode.SERVER_ERROR : e1.getErrorCode());
            result.setErrorDesc(StringUtil.isEmpty(e1.getMessage())? ErrorCode.SERVER_ERROR_MSG : e1.getMessage());
        }
        catch (Exception e)
        {
            logger.error("[获取充值方式]系统异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 获取充值描述
     * @author  mcdog
     */
    @RequestMapping(value="/getdesc")
    public void getRechargeDesc(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//获取参数
            payService.getRechargeDesc(params,result);//获取充值描述
        }
        catch (ServiceException e1)
        {
            logger.error("[获取充值描述]服务异常! 异常信息：" + e1);
            result.setErrorCode(ErrorCode.SERVER_ERROR);
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[获取充值描述]系统异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 获取充值交易参数(订单/签名等信息)
     * @author  mcdog
     */
    @RequestMapping(value="/getPayParams")
    public void getPayParams(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//获取参数
            logger.info("[获取充值交易参数]用户编号=" + getLoginUserId(request) + ",接收原始参数=" + params.toString());
            params.put("userId",getLoginUserId(request));//获取用户编号
            params.put("requestIp",getRequestIP(request));//获取客户端ip
            payService.getPayParams(params,result);//获取支付参数
        }
        catch (ServiceException e1)
        {
            logger.error("[获取充值交易参数]服务异常!用户编号=" + getLoginUserId(request) + ",异常信息：" + e1);
            result.setErrorCode(StringUtil.isEmpty(e1.getErrorCode())? ErrorCode.SERVER_ERROR : e1.getErrorCode());
            result.setErrorDesc(StringUtil.isEmpty(e1.getMessage())? ErrorCode.SERVER_ERROR_MSG : e1.getMessage());
        }
        catch (Exception e)
        {
            logger.error("[获取充值交易参数]系统异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 获取充值结果
     * @author  mcdog
     */
    @RequestMapping(value="/getPayResult")
    public void getPayResult(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//获取参数
            logger.info("[获取充值结果]用户编号=" + getLoginUserId(request) + ",接收原始参数=" + params.toString());
            params.put("userId",getLoginUserId(request));//获取用户编号
            payService.getPayResult(params,result);//获取支付参数
        }
        catch (ServiceException e1)
        {
            logger.error("[获取充值结果]服务异常!用户编号=" + getLoginUserId(request) + ",异常信息：" + e1);
            result.setErrorCode(ErrorCode.SERVER_ERROR);
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[获取充值结果]系统异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 银联预下单
     * @author  mcdog
     */
    @RequestMapping(value="/unionpay/create")
    public void createUnionpayOrder(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//获取参数
            settingLoginUser(request);
            logger.info("[银联预下单]用户编号=" + getLoginUserId(request) + ",接收原始参数:" + params.toString());
            params.put("userId",getLoginUserId(request));//获取用户编号
            params.put("requestIp",getRequestIP(request));//获取客户端ip
            payService.createUnionpayOrder(params,result);//创建银联订单
        }
        catch (ServiceException e1)
        {
            logger.error("[银联预下单]服务异常!用户编号=" + getLoginUserId(request) + ",异常信息：" + e1);
            result.setErrorCode(ErrorCode.SERVER_ERROR);
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[银联预下单]系统异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 设置登录用户
     * @author	mcdog
     * @param 	request
     */
    protected void settingLoginUser(HttpServletRequest request)
    {
        try
        {
            UserBean bean = new UserBean();
            String loginType = request.getParameter("loginType");
            if(StringUtil.isEmpty(loginType))
            {
                bean.setLoginType(1);//默认令牌登录
            }
            else
            {
                bean.setLoginType(Integer.parseInt(loginType));
            }
            String mobile = request.getParameter("mobile");
            if(StringUtil.isNotEmpty(mobile))
            {
                bean.setMobile(mobile);
            }
            String password = request.getParameter("password");
            if(StringUtil.isNotEmpty(password))
            {
                bean.setPassword(password);
            }
            String token = request.getParameter("token");
            if(StringUtil.isNotEmpty(token))
            {
                bean.setToken(token);
            }
            String key = request.getParameter("key");
            if (StringUtil.isNotEmpty(key))
            {
                bean.setKey(key);
            }
            String device = request.getParameter("device");
            if(StringUtil.isNotEmpty(device))
            {
                bean.setDevice(device);
            }
            //验证登录
            int login = bean.getLoginType();
            boolean flag = false;
            if(1 == login)
            {
                //令牌登录
                flag = userService.tokenAuthLogin(bean);
            }
            else if(2 == login)
            {
                //微信联合登录
                flag = userService.weixinQqAuthLogin(bean);
            }
            else if(3 == login)
            {
                flag = userService.weixinQqAuthLogin(bean);//QQ联合登录
            }
            else if(4 == login)
            {
            }
            else if(5 == login)
            {
            }
            else
            {
                flag = userService.passwordAuthLogin(bean);
            }
            if(flag)
            {
                request.setAttribute(USER_LOGIN_OBJ, bean.getObj());//透传用户登录信息
            }
        }
        catch (Exception e)
        {
            logger.error("[设置登录用户]发生异常!异常信息:",e);
        }
    }
}