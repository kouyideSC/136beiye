package com.caipiao.app.interceptor;

import com.caipiao.app.base.BaseController;
import com.caipiao.common.encrypt.AuthSign;
import com.caipiao.common.util.ReflectionToString;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.base.UserBean;
import com.caipiao.domain.code.ErrorCode;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.cpadmin.JsonHelper;
import com.caipiao.service.exception.ServiceException;
import com.caipiao.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 验证用户登录拦截器
 * Created by kouyi on 2017/9/29.
 */
public class UserLoginInterceptor extends BaseController implements HandlerInterceptor {
    private static Logger logger = LoggerFactory.getLogger(UserLoginInterceptor.class);
    private static final String REGISTER_URI = "/user/register";

    @Autowired
    private UserService userService;

    /**
     * 控制层之前被执行-此处处理请求解密-并验证用户登录
     * @param request
     * @param response
     * @param o
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) {
        String mobile = null;
        String URI = request.getRequestURI();
        long startTime = System.currentTimeMillis();
        String params = ReflectionToString.getParams(request);
        try {
            //logger.info("[拦截器-验证登录 线程号:"+startTime+"] 接收原始参数=" + params);
            //除注册以外的用户行为一律验证签名
            if (!AuthSign.checkSign(request)) {
                logger.error("[拦截器-验证签名 线程号:"+startTime+"] 签名验证不通过 接收原始参数=" + params);
                writeResponse(ErrorCode.SERVER_ERROR, response);
                return false;
            }
            //拦截注册请求-不作处理
            if (URI.indexOf(REGISTER_URI) > -1) {
                request.setAttribute(START_TIME, startTime);
                return true;
            }
            //登录验证
            UserBean bean = new UserBean();
            String loginType = request.getParameter("loginType");
            if (StringUtil.isEmpty(loginType)) {
                bean.setLoginType(1);//默认令牌登录
            } else {
                bean.setLoginType(Integer.parseInt(loginType));
            }
            mobile = request.getParameter("mobile");
            if (StringUtil.isNotEmpty(mobile)) {
                bean.setMobile(mobile);
            }
            String password = request.getParameter("password");
            if (StringUtil.isNotEmpty(password)) {
                bean.setPassword(password);
            }
            String token = request.getParameter("token");
            if (StringUtil.isNotEmpty(token)) {
                bean.setToken(token);
            }
            String key = request.getParameter("key");
            if (StringUtil.isNotEmpty(key)) {
                bean.setKey(key);
            }
            String device = request.getParameter("device");
            if (StringUtil.isNotEmpty(device)) {
                bean.setDevice(device);
            }

            /**
             * 联合登录相关字段
             */
            //QQ用户唯一标识
            String openId = request.getParameter("openId");
            if (StringUtil.isNotEmpty(openId)) {
                bean.setOpenId(openId);
            }
            //获取QQ用户信息的票据
            String accessToken = request.getParameter("accessToken");
            if (StringUtil.isNotEmpty(accessToken)) {
                bean.setAccessToken(accessToken);
            }
            //用户换取access_token的code(微信)
            String wxcode = request.getParameter("wxcode");
            if (StringUtil.isNotEmpty(wxcode)) {
                bean.setWxcode(wxcode);
            }

            // 验证不通过、直接返回错误码
            if (userLoginAuth(bean)) {
                request.setAttribute(USER_LOGIN_OBJ, bean.getObj());//透传用户登录信息
                request.setAttribute(START_TIME, startTime);
                return true;
            }
            //第三方联合登录尚未绑定用户信息(首次登录)
            if(bean.getValidFlag() == 1)
            {
                Dto result = new BaseDto("errorCode",ErrorCode.ERROR_100008);//设置状态码
                result.put("errorDesc",ErrorCode.ERROR_100008_MSG);//设置状态描述
                result.put("nickName",bean.getNickName());//设置用户昵称
                writeResponse(JsonHelper.encodeObject2Json(result).toString(),response);
                return false;
            }
            else
            {
                writeResponse(ErrorCode.ERROR_100001, response);
            }
        } catch (ServiceException se) {
            logger.error("[拦截器-验证登录 线程号:"+startTime+"]服务异常!URI=" + URI + ",异常信息:", se);
            logger.error("[拦截器-验证登录 线程号:"+startTime+"]接收原始参数=" + params);
            writeResponse(se.getErrorCode(), response);
        } catch (Exception e) {
            logger.error("[拦截器-验证登录 线程号:"+startTime+"]系统异常!URI=" + URI + ",异常信息:", e);
            logger.error("[拦截器-验证登录 线程号:"+startTime+"]接收原始参数=" + params);
            writeResponse(ErrorCode.NETWORK_ERROR, response);
        }
        return false;
    }

    /**
     * 用户登录业务方法
     * @param bean
     * @return
     */
    public boolean userLoginAuth(UserBean bean) throws ServiceException, Exception {
        int login = bean.getLoginType();
        switch (login) {
            //令牌登录
            case 1: {
                return userService.tokenAuthLogin(bean);
            }
            //微信联合登录
            case 2:
            {
                return userService.weixinQqAuthLogin(bean);
            }
            //QQ联合登录
            case 3:
            {
                return userService.weixinQqAuthLogin(bean);
            }
            //支付宝联合登录
            case 4: {break;}
            //验证码登录
            case 5: {break;}
            //密码登录
            default: {
                return userService.passwordAuthLogin(bean);
            }
        }
        return false;
    }

    /**
     * 控制层之后被执行
     * @param request
     * @param response
     * @param o
     * @param modelAndView
     * @throws Exception
     */
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 渲染视图之后被执行
     * @param request
     * @param response
     * @param o
     * @param e
     * @throws Exception
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) throws Exception {

    }

}
