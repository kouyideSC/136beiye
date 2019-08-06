package com.caipiao.app.message;

import com.caipiao.app.base.BaseController;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.NumberUtil;
import com.caipiao.common.util.ReflectionToString;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.code.ErrorCode;
import com.caipiao.domain.common.MessageCode;
import com.caipiao.service.common.MessageCodeService;
import com.caipiao.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 消息业务控制接口
 * Created by kouyi on 2017/10/25.
 */
@Controller
public class MessageCodeController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(MessageCodeController.class);
    @Autowired
    private MessageCodeService messageService;

    /**
     * 获取用户注册验证码
     * @param
     * @param request
     * @param response
     */
    @RequestMapping(value="/message/getAuthCode", method= RequestMethod.POST)
    public void getAuthCode(MessageCode message, HttpServletRequest request, HttpServletResponse response) {
        ResultBean result = new ResultBean();
        try {
            logger.info("[获取用户注册验证码] 接收原始参数=" + ReflectionToString.toString(message));
            String code = NumberUtil.randomCode();//生成验证码
            message.setContent(code);
            message.setType(1);
            message.setExpireTime(DateUtil.addMinute(new Date(), 10));//有效期10分钟
            messageService.saveMessageCode(message, result);
            logger.info("[获取用户注册验证码] 返回处理结果=" + result.getErrorDesc());
        } catch (ServiceException e) {
            logger.error("[获取用户注册验证码] 服务异常! mobile=" + message.getMobile() + " errorDesc=" + e.getMessage());
            result.setErrorCode(e.getErrorCode());
        } catch (Exception e) {
            logger.error("[获取用户注册验证码] 系统异常! mobile=" + message.getMobile() + " errorDesc=" + e.getMessage());
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result.getErrorCode(), response);
    }

    /**
     * 获取找回密码验证码
     * @param
     * @param request
     * @param response
     */
    @RequestMapping(value="/message/getBackPwdAuthCode", method= RequestMethod.POST)
    public void getBackPasswordAuthCode(MessageCode message, HttpServletRequest request, HttpServletResponse response) {
        ResultBean result = new ResultBean();
        try {
            logger.info("[获取找回密码验证码] 接收原始参数=" + ReflectionToString.toString(message));
            String code = NumberUtil.randomCode();//生成验证码
            message.setContent(code);
            message.setType(3);
            message.setExpireTime(DateUtil.addMinute(new Date(), 10));//有效期10分钟
            messageService.backPasswordAuthCode(message, result);
            logger.info("[获取找回密码验证码] 返回处理结果=" + result.getErrorDesc());
        } catch (ServiceException e) {
            logger.error("[获取找回密码验证码] 服务异常! mobile=" + message.getMobile() + " errorDesc=" + e.getMessage());
            result.setErrorCode(e.getErrorCode());
        } catch (Exception e) {
            logger.error("[获取找回密码验证码] 系统异常! mobile=" + message.getMobile() + " errorDesc=" + e.getMessage());
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result.getErrorCode(), response);
    }

    /**
     * 获取实名认证验证码
     * @author  mcdog
     */
    @RequestMapping(value="/message/getRzAuthCode", method= RequestMethod.POST)
    public void getIdentityRzAuthCode(MessageCode message, HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        try
        {
            logger.info("[获取实名认证验证码]接收原始参数=" + ReflectionToString.toString(message));
            String code = NumberUtil.randomCode();//生成验证码
            message.setContent(code);
            message.setType(4);
            message.setExpireTime(DateUtil.addMinute(new Date(), 10));//有效期10分钟
            messageService.identityRzAuthCode(message, result);
            logger.info("[获取实名认证验证码]返回处理结果=" + result.getErrorDesc());
        }
        catch (ServiceException e)
        {
            logger.error("[获取实名认证验证码]服务异常!mobile=" + message.getMobile() + ",errorDesc=" + e.getMessage());
            result.setErrorCode(e.getErrorCode());
        }
        catch (Exception e)
        {
            logger.error("[获取实名认证验证码]系统异常!mobile=" + message.getMobile() + ",errorDesc=" + e.getMessage());
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result.getErrorCode(), response);
    }

    /**
     * 发送微信/QQ联合登录用户绑定用户信息验证码
     * @author  mcdog
     */
    @RequestMapping(value="/message/unionlogon/authcode")
    public void getBindWeixinQqUserInfoRzAuthCode(MessageCode message, HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        try
        {
            logger.info("[发送微信/QQ联合登录用户绑定用户信息验证码]接收原始参数=" + ReflectionToString.toString(message));
            String code = NumberUtil.randomCode();//生成验证码
            message.setContent(code);
            message.setType(5);
            message.setExpireTime(DateUtil.addMinute(new Date(), 10));//有效期10分钟
            messageService.unionBindUserInfoAuthCode(message, result);
            logger.info("[发送微信/QQ联合登录用户绑定用户信息验证码]返回处理结果=" + result.getErrorDesc());
        }
        catch (ServiceException e)
        {
            logger.error("[发送微信/QQ联合登录用户绑定用户信息验证码]服务异常!mobile=" + message.getMobile() + ",errorDesc=" + e.getMessage());
            result.setErrorCode(e.getErrorCode());
        }
        catch (Exception e)
        {
            logger.error("[发送微信/QQ联合登录用户绑定用户信息验证码]系统异常!mobile=" + message.getMobile() + ",errorDesc=" + e.getMessage());
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result.getErrorCode(), response);
    }
}
