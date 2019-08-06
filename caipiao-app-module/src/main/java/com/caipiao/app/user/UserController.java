package com.caipiao.app.user;

import com.caipiao.app.base.BaseController;
import com.caipiao.app.utils.WebUtils;
import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.util.*;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.base.UserBean;
import com.caipiao.domain.code.ErrorCode;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.common.ActivityUser;
import com.caipiao.domain.common.MessageCode;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.cpadmin.JsonHelper;
import com.caipiao.domain.user.UserToken;
import com.caipiao.service.common.ActivityService;
import com.caipiao.service.common.MessageCodeService;
import com.caipiao.service.config.SysConfig;
import com.caipiao.service.exception.ServiceException;
import com.caipiao.service.match.MatchService;
import com.caipiao.service.scheme.SchemeService;
import com.caipiao.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

/**
 * 用户业务控制接口
 * Created by kouyi on 2017/9/20.
 */
@Controller
public class UserController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private MessageCodeService messageService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private SchemeService schemeService;

    /**
     * 新用户注册接口
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping(value="/user/register", method= RequestMethod.POST)
    public void userRegister(UserBean bean, HttpServletRequest request, HttpServletResponse response) {
        ResultBean result = new ResultBean();
        try {
            bean.setIpAddress(getRequestIP(request));//设置IP地址
            logger.info("[新用户注册] 接收原始参数=" + ReflectionToString.toString(bean));
            userService.saveUser(bean, result);
            logger.info("[新用户注册] 返回处理结果=" + result.getErrorDesc());
        } catch (ServiceException e) {
            logger.error("[新用户注册] 服务异常 mobile=" + bean.getMobile() + " errorDesc=" + e.getMessage());
            result.setErrorCode(e.getErrorCode());
        } catch (Exception e) {
            logger.error("[新用户注册] 系统异常 mobile=" + bean.getMobile() + " errorDesc=" + e.getMessage());
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result, response);
    }

    /**
     * 用户登录接口
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping(value="/user/login", method= RequestMethod.POST)
    public void userLogin(UserBean bean, HttpServletRequest request, HttpServletResponse response) {
        ResultBean result = new ResultBean();
        try {
            logger.info("[用户登录] 接收原始参数=" + ReflectionToString.toString(bean));
            getLoginUserInfo(bean, request);
            userService.userLoginResult(bean, result);
            long endTime = System.currentTimeMillis();
            logger.info("[用户登录] 返回处理结果=" + result.getErrorDesc() + " 处理耗时=" + (endTime-bean.getStartTime()) + "ms");
        } catch (ServiceException e) {
            logger.error("[用户登录] 服务异常 mobile=" + bean.getMobile() + " errorDesc=" + e.getMessage());
            result.setErrorCode(e.getErrorCode());
        } catch (Exception e) {
            logger.error("[用户登录] 系统异常 mobile=" + bean.getMobile() + " errorDesc=" + e.getMessage());
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result, response);
    }

    /**
     * 微信/QQ联合登录-绑定用户信息
     * @author  mcdog
     */
    @RequestMapping(value="/user/register/wxqq/bind", method= RequestMethod.POST)
    public void bindWeixinQqUserInfo(UserBean bean,HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);
            BeanUtils.copyProperties(bean,params);//拷贝属性
            bean.setIpAddress(getRequestIP(request));//设置IP地址
            logger.info("[微信/QQ联合登录-绑定用户信息]线程号=" + request.getAttribute(START_TIME) + ",接收原始参数=" + ReflectionToString.toString(bean));
            userService.bindWeixinQqUser(bean, result);
            if(result.getErrorCode() == ErrorCode.SUCCESS)
            {
                request.setAttribute(USER_LOGIN_OBJ, bean.getObj());//在request中保存用户登录信息
                userService.userLoginResult(bean, result);//获取登录用户信息
            }
        }
        catch (ServiceException e)
        {
            logger.error("[微信/QQ联合登录-绑定用户信息]服务异常!异常信息:" + e.getMessage());
            result.setErrorCode(e.getErrorCode());
        }
        catch (Exception e)
        {
            logger.error("[微信/QQ联合登录-绑定用户信息]系统异常!异常信息:",e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result, response);
    }

    /**
     * 获取用户基本信息
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping(value="/user/getInfo")
    public void getUserInfo(UserBean bean, HttpServletRequest request, HttpServletResponse response) {
        ResultBean result = new ResultBean();
        try {
            //logger.info("[查询用户信息] 接收原始参数=" + ReflectionToString.toString(bean));
            getLoginUserId(bean, request);
            userService.queryUserInfo(bean, result);
            long endTime = System.currentTimeMillis();
            //logger.info("[查询用户信息] 返回处理结果=" + result.getErrorDesc() + " 处理耗时=" + (endTime-bean.getStartTime()) + "ms");
        } catch (ServiceException e) {
            logger.error("[查询用户信息] 服务异常 userId=" + bean.getUserId() + " errorDesc=" + e.getMessage());
            result.setErrorCode(e.getErrorCode());
        } catch (Exception e) {
            logger.error("[查询用户信息] 系统异常 userId=" + bean.getMobile() + " errorDesc=" + e.getMessage());
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result, response);
    }

    /**
     * 获取用户返利账户余额
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping(value="/user/getBackBalance")
    public void getUserBackBalance(UserBean bean, HttpServletRequest request, HttpServletResponse response) {
        ResultBean result = new ResultBean();
        try {
            logger.info("[查询用户返利余额] 接收原始参数=" + ReflectionToString.toString(bean));
            getLoginUserId(bean, request);
            userService.queryUserBackBalance(bean, result);
            logger.info("[查询用户返利余额] 返回处理结果=" + result.getErrorDesc());
        } catch (ServiceException e) {
            logger.error("[查询用户返利余额] 服务异常 userId=" + bean.getUserId() + " errorDesc=" + e.getMessage());
            result.setErrorCode(e.getErrorCode());
        } catch (Exception e) {
            logger.error("[查询用户返利余额] 系统异常 userId=" + bean.getMobile() + " errorDesc=" + e.getMessage());
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result, response);
    }

    /**
     * 用户返利转出到账户余额
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping(value="/user/backRollOut")
    public void getUserBackRollOut(UserBean bean, HttpServletRequest request, HttpServletResponse response) {
        ResultBean result = new ResultBean();
        try {
            logger.info("[用户返利转出到账户余额] 接收原始参数=" + ReflectionToString.toString(bean));
            getLoginUserId(bean, request);
            userService.updateUserBackRollOut(bean, result);
            logger.info("[用户返利转出到账户余额] 返回处理结果=" + result.getErrorDesc());
        } catch (ServiceException e) {
            logger.error("[用户返利转出到账户余额] 服务异常 userId=" + bean.getUserId() + " errorDesc=" + e.getMessage());
            result.setErrorCode(e.getErrorCode());
        } catch (Exception e) {
            logger.error("[用户返利转出到账户余额] 系统异常 userId=" + bean.getMobile() + " errorDesc=" + e.getMessage());
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result.getErrorCode(), response);
    }

    /**
     * 获取销售或代理的邀请码
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping(value="/user/getCode")
    public void getUserCode(UserBean bean, HttpServletRequest request, HttpServletResponse response) {
        ResultBean result = new ResultBean();
        try {
            //logger.info("[查询用户邀请码] 接收原始参数=" + ReflectionToString.toString(bean));
            getLoginUserId(bean, request);
            userService.getUserCode(bean, result);
            //logger.info("[查询用户邀请码] 返回处理结果=" + result.getErrorDesc());
        } catch (ServiceException e) {
            logger.error("[查询用户邀请码] 服务异常 userId=" + bean.getUserId() + " errorDesc=" + e.getMessage());
            result.setErrorCode(e.getErrorCode());
        } catch (Exception e) {
            logger.error("[查询用户邀请码] 系统异常 userId=" + bean.getMobile() + " errorDesc=" + e.getMessage());
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result, response);
    }

    /**
     * 用户密码重置找回
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping(value="/reset/userPasswd", method= RequestMethod.POST)
    public void resetUserPassword(UserBean bean, HttpServletRequest request, HttpServletResponse response) {
        ResultBean result = new ResultBean();
        try {
            logger.info("[用户密码找回] 接收原始参数=" + ReflectionToString.toString(bean));
            userService.resetUserPasswd(bean, result);
            logger.info("[用户密码找回] 返回处理结果=" + result.getErrorDesc());
        } catch (ServiceException e) {
            logger.error("[用户密码找回] 服务异常 mobile=" + bean.getUserId() + " errorDesc=" + e.getMessage());
            result.setErrorCode(e.getErrorCode());
        } catch (Exception e) {
            logger.error("[用户密码找回] 系统异常 mobile=" + bean.getMobile() + " errorDesc=" + e.getMessage());
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result, response);
    }

    /**
     * 用户密码修改
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping(value="/user/modifyPasswd", method= RequestMethod.POST)
    public void updatePassword(UserBean bean,HttpServletRequest request, HttpServletResponse response) {
        ResultBean result = new ResultBean();
        try {
            logger.info("[修改用户密码] 接收原始参数=" + ReflectionToString.toString(bean));
            getLoginUserId(bean, request);
            userService.updatePasswd(bean, result);
            logger.info("[修改用户密码] 返回处理结果=" + result.getErrorDesc());
        } catch (ServiceException e) {
            logger.error("[修改用户密码] 服务异常 userId=" + bean.getUserId() + " errorDesc=" + e.getMessage());
            result.setErrorCode(e.getErrorCode());
        } catch (Exception e) {
            logger.error("[修改用户密码] 系统异常 userId=" + bean.getUserId() + " errorDesc=" + e.getMessage());
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result, response);
    }

    /**
     * 用户上传头像
     * @author  mcdog
     */
    @RequestMapping(value="/user/avatar/upload")
    public void upload(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        Dto params = new BaseDto();
        try
        {
            params.putAll(WebUtils.getParamsAsDto(request));//获取参数
            logger.info("[用户上传头像]用户编号=" + getLoginUserId(request) + ",接收原始参数=" + params.toString());
            params.put("userId",getLoginUserId(request));//获取用户编号

            //提取上传文件信息并上传
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            Map<String, MultipartFile> mfileMap = multipartRequest.getFileMap();
            if(mfileMap != null && mfileMap.size() > 0 && mfileMap.get("avatar") != null)
            {
                userService.updateUserAvatar(params, mfileMap.get("avatar"), result);//上传并更新用户头像
            }
            else
            {
                logger.error("[用户上传头像]用户编号=" + params.getAsString("userId") + ",提取不到有效的头像文件");
                throw new ServiceException(ErrorCode_API.SERVER_ERROR);
            }
        }
        catch (ServiceException e1)
        {
            logger.error("[用户上传头像]服务异常!用户编号=" + params.getAsString("userId") + ",异常信息:" + e1);
            result.setErrorCode(e1.getErrorCode());
            if (StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[用户上传头像]系统异常!用户编号=" + params.getAsString("userId") + ",异常信息:" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result, response);
    }

    /**
     * 用户绑定银行卡
     * @author  mcdog
     */
    @RequestMapping(value="/user/bankBd")
    public void bindingBank(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        Dto params = new BaseDto();
        try
        {
            params.putAll(WebUtils.getParamsAsDto(request));//获取参数
            logger.info("[用户绑定银行卡]用户编号=" + getLoginUserId(request) + ",接收原始参数=" + params.toString());
            params.put("userId",getLoginUserId(request));//获取用户编号
            userService.updateUserBank(params,result);//更新用户银行卡信息
        }
        catch (ServiceException e1)
        {
            logger.error("[用户绑定银行卡]服务异常!用户编号=" + params.getAsString("userId") + ",异常信息:" + e1);
            result.setErrorCode(e1.getErrorCode());
            if (StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[绑定银行卡]系统异常!用户编号=" + params.getAsString("userId") + ",异常信息:" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result, response);
    }

    /**
     * 用户实名认证
     * @author  mcdog
     */
    @RequestMapping(value="/user/identityRz")
    public void identityCertification(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        Dto params = new BaseDto();
        try
        {
            params.putAll(WebUtils.getParamsAsDto(request));//获取参数
            logger.info("[用户实名认证]用户编号=" + getLoginUserId(request) + ",接收原始参数=" + params.toString());
            params.put("userId",getLoginUserId(request));//获取用户编号
            userService.updateUserIdentity(params,result);//更新用户实名信息
        }
        catch (ServiceException e1)
        {
            logger.error("[用户实名认证]服务异常!用户编号=" + params.getAsString("userId") + ",异常信息:" + e1);
            result.setErrorCode(e1.getErrorCode());
            if (StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[用户实名认证]系统异常!用户编号=" + params.getAsString("userId") + ",异常信息:" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result, response);
    }

    /**
     * 发送实名认证验证码
     * @author  mcdog
     */
    @RequestMapping(value="/user/getSmRzAuthCode")
    public void getIdentityRzAuthCode(MessageCode message, HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        try
        {
            logger.info("[发送实名认证验证码]接收原始参数=" + ReflectionToString.toString(message));
            String code = NumberUtil.randomCode();//生成验证码
            message.setContent(code);
            message.setType(4);
            message.setExpireTime(DateUtil.addMinute(new Date(), 10));//有效期10分钟
            message.setUserId(getLoginUserId(request));//获取用户编号
            messageService.identityRzAuthCode(message, result);
            logger.info("[发送实名认证验证码]返回处理结果=" + result.getErrorDesc());
        }
        catch (ServiceException e)
        {
            logger.error("[发送实名认证验证码]服务异常!mobile=" + message.getMobile() + ",errorDesc=" + e.getMessage());
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
     * 用户提现
     * @author  mcdog
     */
    @RequestMapping(value="/user/withdraw")
    public void userWithdraw(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        Dto params = new BaseDto();
        try
        {
            params.putAll(WebUtils.getParamsAsDto(request));//获取参数
            logger.info("[用户提现]用户编号=" + getLoginUserId(request) + ",接收原始参数=" + params.toString());
            params.put("userId",getLoginUserId(request));//获取用户编号
            params.put("requestIp",getRequestIP(request));//获取客户端ip
            userService.updateUserForWithdraw(params,result);//发起付款并更新用户账户等信息
        }
        catch (ServiceException e1)
        {
            logger.error("[用户提现]服务异常!用户编号=" + params.getAsString("userId") + ",异常信息:" + e1);
            result.setErrorCode(e1.getErrorCode());
            if (StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[用户提现]系统异常!用户编号=" + params.getAsString("userId") + ",异常信息:" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result, response);
    }

    /**
     * 用户账户流水
     * @author  mcdog
     */
    @RequestMapping(value="/user/acdetail")
    public void balanceDetail(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        Dto params = new BaseDto();
        try
        {
            params.putAll(WebUtils.getParamsAsDto(request));//获取参数
            //logger.info("[用户账户流水]用户编号=" + getLoginUserId(request) + ",接收原始参数=" + params.toString());
            params.put("userId",getLoginUserId(request));//获取用户编号
            userService.getUserBalanceDetail(params,result);//获取用户账户流水明细
        }
        catch (ServiceException e1)
        {
            logger.error("[用户账户流水]服务异常!用户编号=" + params.getAsString("userId") + ",异常信息:" + e1);
            result.setErrorCode(e1.getErrorCode());
            if (StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[用户账户流水]系统异常!用户编号=" + params.getAsString("userId") + ",异常信息:" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result, response);
    }

    /**
     * 获取用户绑定的银行卡信息
     * @author  mcdog
     */
    @RequestMapping(value="/user/getBankInfo")
    public void getUserBankInfo(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        Dto params = new BaseDto();
        try
        {
            params.putAll(WebUtils.getParamsAsDto(request));//获取参数
            logger.info("[获取用户绑定的银行卡信息]用户编号=" + getLoginUserId(request) + ",接收原始参数=" + params.toString());
            params.put("userId",getLoginUserId(request));//获取用户编号
            userService.getUserBankInfo(params,result);//获取用户绑定的银行卡信息
        }
        catch (ServiceException e1)
        {
            logger.error("[获取用户绑定的银行卡信息]服务异常!用户编号=" + params.getAsString("userId") + ",异常信息:" + e1);
            result.setErrorCode(e1.getErrorCode());
            if (StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[获取用户绑定的银行卡信息]系统异常!用户编号=" + params.getAsString("userId") + ",异常信息:" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result, response);
    }

    /**
     * 获取用户的实名信息
     * @author  mcdog
     */
    @RequestMapping(value="/user/getIdentity")
    public void getUserIdentity(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        Dto params = new BaseDto();
        try
        {
            params.putAll(WebUtils.getParamsAsDto(request));//获取参数
            logger.info("[获取用户的实名信息]用户编号=" + getLoginUserId(request) + ",接收原始参数=" + params.toString());
            params.put("userId",getLoginUserId(request));//获取用户编号
            userService.getUserIdentity(params,result);//获取用户的实名信息
        }
        catch (ServiceException e1)
        {
            logger.error("[获取用户的实名信息]服务异常!用户编号=" + params.getAsString("userId") + ",异常信息:" + e1);
            result.setErrorCode(e1.getErrorCode());
            if (StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[获取用户的实名信息]系统异常!用户编号=" + params.getAsString("userId") + ",异常信息:" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result, response);
    }

    /**
     * 获取用户提现信息
     * @author  mcdog
     */
    @RequestMapping(value="/user/getTxinfo")
    public void getUserTxInfo(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        Dto params = new BaseDto();
        try
        {
            params.putAll(WebUtils.getParamsAsDto(request));//获取参数
            logger.info("[获取用户当前的提现信息]用户编号=" + getLoginUserId(request) + ",接收原始参数=" + params.toString());
            params.put("userId",getLoginUserId(request));//获取用户编号
            userService.getUserTxInfo(params,result);//获取用户当前的提现信息
        }
        catch (ServiceException e1)
        {
            logger.error("[获取用户当前的提现信息]服务异常!用户编号=" + params.getAsString("userId") + ",异常信息:" + e1);
            result.setErrorCode(e1.getErrorCode());
            if (StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[获取用户当前的提现信息]系统异常!用户编号=" + params.getAsString("userId") + ",异常信息:" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result, response);
    }

    /**
     * 获取用户优惠券信息
     * @author  mcdog
     */
    @RequestMapping(value="/user/coupon/get")
    public void getUserCoupons(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        Dto params = new BaseDto();
        try
        {
            params.putAll(WebUtils.getParamsAsDto(request));//获取参数
            logger.info("[获取用户优惠券信息]用户编号=" + getLoginUserId(request) + ",接收原始参数=" + params.toString());
            params.put("userId",getLoginUserId(request));//获取用户编号
            userService.getUserCoupons(params,result);//获取用户优惠券信息
        }
        catch (ServiceException e1)
        {
            logger.error("[获取用户优惠券信息]服务异常!用户编号=" + params.getAsString("userId") + ",异常信息:" + e1);
            result.setErrorCode(e1.getErrorCode());
            if (StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[获取用户优惠券信息]系统异常!用户编号=" + params.getAsString("userId") + ",异常信息:" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result, response);
    }

    /**
     * 查询赛事对阵(app)
     * @author  mcdog
     */
    @RequestMapping(value="/user/match/get")
    public void getMatchs(HttpServletRequest request, HttpServletResponse response)
    {
        Dto resultDto = new BaseDto("errorCode",ErrorCode.SERVER_ERROR);//返回数据对象
        Dto params = new BaseDto();
        try
        {
            params.putAll(WebUtils.getParamsAsDto(request));
            params.put("userId",getLoginUserId(request));

            //设置分页查询参数默认30条一页
            params.put("psize", "30");

            Dto dataDto = new BaseDto("list",matchService.queryMatches(params));//查询赛事对阵信息
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                int tsize = matchService.queryMatchesCount(params);//查询总记录条数
                int psize = params.getAsInteger("psize");
                dataDto.put("tsize",tsize);//设置总记录条数
                dataDto.put("tpage",(tsize % psize == 0? (tsize / psize) : ((tsize / psize) + 1)));//设置总页数
            }
            resultDto.put("data",dataDto);
            resultDto.put("errorCode",ErrorCode.SUCCESS);
            resultDto.put("errorDesc",ErrorCode.SUCCESS_MSG);
        }
        catch(Exception e)
        {
            logger.error("[查询赛事对阵(app)]发生异常!异常信息:",e);
            resultDto.put("errorDesc",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 赛果审核(app)
     * @author  mcdog
     */
    @RequestMapping(value="/user/match/audit")
    public void auditMatchs(HttpServletRequest request, HttpServletResponse response)
    {
        Dto resultDto = new BaseDto("errorCode",ErrorCode.SERVER_ERROR);//返回数据对象
        Dto params = new BaseDto();
        try
        {
            params.putAll(WebUtils.getParamsAsDto(request));
            params.put("userId",getLoginUserId(request));
            if(matchService.eidtMatchResult(params) > 0)
            {
                resultDto.put("errorCode",ErrorCode.SUCCESS);
                resultDto.put("errorDesc","赛果审核成功");
            }
            else
            {
                resultDto.put("errorDesc",StringUtil.isEmpty(params.get("dmsg"))? "赛果审核失败" : params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("[赛果审核(app)]发生异常!异常信息:",e);
            resultDto.put("errorDesc",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 查询派奖审核方案(app)
     * @author  mcdog
     */
    @RequestMapping(value="/user/pjsh/get")
    public void getPjshSchemes(HttpServletRequest request, HttpServletResponse response)
    {
        Dto resultDto = new BaseDto("errorCode",ErrorCode.SERVER_ERROR);//返回数据对象
        Dto params = new BaseDto();
        try
        {
            params.putAll(WebUtils.getParamsAsDto(request));
            params.put("userId",getLoginUserId(request));
            Dto dataDto = new BaseDto("list",schemeService.queryPjshSchemes(params));//查询派奖审核方案
            if(StringUtil.isNotEmpty(params.get("psize")))
            {
                Dto countDto = schemeService.queryPjshSchemesCount(params);//查询派奖审核方案总计
                int tsize = countDto.getAsInteger("tsize");
                int psize = params.getAsInteger("psize");
                dataDto.put("tsize",tsize);//设置总记录条数
                dataDto.put("tpage",(tsize % psize == 0? (tsize / psize) : ((tsize / psize) + 1)));//设置总页数
            }
            resultDto.put("data",dataDto);
            resultDto.put("errorCode",ErrorCode.SUCCESS);
            resultDto.put("errorDesc",ErrorCode.SUCCESS_MSG);
        }
        catch(Exception e)
        {
            logger.error("[查询派奖审核方案(app)]发生异常!异常信息:",e);
            resultDto.put("errorDesc",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }

    /**
     * 方案确认派奖(app)
     * @author  mcdog
     */
    @RequestMapping(value="/user/pjsh/qrpj")
    public void schemeQrpj(HttpServletRequest request, HttpServletResponse response)
    {
        Dto resultDto = new BaseDto("errorCode",ErrorCode.SERVER_ERROR);//返回数据对象
        Dto params = new BaseDto();
        try
        {
            params.putAll(WebUtils.getParamsAsDto(request));
            params.put("userId",getLoginUserId(request));
            if(schemeService.updateSchemeForQrPj(params) > 0)
            {
                resultDto.put("errorCode",ErrorCode.SUCCESS);
                resultDto.put("errorDesc","方案派奖成功");
            }
            else
            {
                resultDto.put("errorDesc",StringUtil.isEmpty(params.get("dmsg"))? "方案派奖失败" : params.get("dmsg"));
            }
        }
        catch(Exception e)
        {
            logger.error("[方案确认派奖(app)]发生异常!异常信息:",e);
            resultDto.put("errorDesc",e.getMessage());
        }
        WebUtils.write(JsonHelper.encodeObject2Json(resultDto).toString(),response);
    }
}