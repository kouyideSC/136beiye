package com.caipiao.app.scheme;

import com.caipiao.app.base.BaseController;
import com.caipiao.app.utils.WebUtils;
import com.caipiao.common.encrypt.AuthSign;
import com.caipiao.common.util.ReflectionToString;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.base.SchemeBean;
import com.caipiao.domain.base.UserBean;
import com.caipiao.domain.code.ErrorCode;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.jjyh.JjyhTwo;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.service.exception.ServiceException;
import com.caipiao.service.scheme.SchemeService;
import com.caipiao.service.user.UserService;
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
 * 方案接口控制类
 */
@Controller
@RequestMapping("/scheme")
public class SchemeController extends BaseController
{
    private static Logger logger = LoggerFactory.getLogger(SchemeController.class);

    @Autowired
    private SchemeService schemeService;

    @Autowired
    private UserService userService;

    /**
     * 保存方案(投注)
     * @author  mcdog
     */
    @RequestMapping(value="/create", method= RequestMethod.POST)
    public void createScheme(SchemeBean schemeBean, HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        try
        {
            logger.info("[保存方案]用户编号=" + getLoginUserId(request) + ",接收原始参数:" + ReflectionToString.toString(schemeBean));
            schemeBean.setUserId(getLoginUserId(request));//设置当前登录用户编号
            schemeBean.setSource("00000");//标识官方渠道
            schemeService.saveScheme(schemeBean,result);//调用保存方案方法
        }
        catch (ServiceException e1)
        {
            logger.error("[保存方案]服务发生异常!用户编号=" + schemeBean.getUserId() + ",异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[保存方案]系统发生异常!用户编号=" + schemeBean.getUserId() + ",异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 方案确认购买(支付)
     * @author  mcdog
     */
    @RequestMapping(value="/confirm", method= RequestMethod.POST)
    public void schemeConfirm(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//获取请求参数
            logger.info("[方案确认购买]用户编号=" + getLoginUserId(request) + ",接收原始参数:" + params.toString());
            params.put("userId",getLoginUserId(request));//设置当前登录用户编号
            schemeService.schemeConfirm(params,result);//调用方案确认购(支付)买方法
        }
        catch (ServiceException e1)
        {
            logger.error("[方案确认购买]服务发生异常!用户编号=" + (params == null? "" : params.getAsString("userId")) + ",异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[方案确认购买]系统发生异常!用户编号=" + (params == null? "" : params.getAsString("userId")) + ",异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 获取用户方案
     * @author  mcdog
     */
    @RequestMapping(value="/get")
    public void getScheme(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//获取请求参数
            //logger.info("[获取用户方案]用户编号=" + getLoginUserId(request) + ",接收原始参数:" + params.toString());
            params.put("userId",getLoginUserId(request));//设置当前登录用户编号
            schemeService.getScheme(params,result);//查询方案信息
        }
        catch (ServiceException e1)
        {
            logger.error("[获取用户方案]服务发生异常!用户编号=" + (params == null? "" : params.getAsString("userId")) + ",异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[获取用户方案]系统发生异常!用户编号=" + (params == null? "" : params.getAsString("userId")) + ",异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 获取方案详情
     * @author  mcdog
     */
    @RequestMapping(value="/detail")
    public void getSchemeDetail(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//获取请求参数
            params.put("userId",getLoginUserId(request));//设置当前登录用户编号
            schemeService.getSchemeDetail(params,result);//查询方案详情
        }
        catch (ServiceException e1)
        {
            logger.error("[获取方案详情]服务发生异常!用户编号=" + (params == null? "" : params.getAsString("userId")) + ",异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[获取方案详情]系统发生异常!用户编号=" + (params == null? "" : params.getAsString("userId")) + ",异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 获取方案信息
     * @author  mcdog
     */
    @RequestMapping(value="/getInfo")
    public void getSchemeInfo(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//获取请求参数
            params.put("userId",getLoginUserId(request));//设置当前登录用户编号
            schemeService.getSchemeInfo(params,result);//获取方案信息
        }
        catch (ServiceException e1)
        {
            logger.error("[获取方案信息]服务发生异常!用户编号=" + (params == null? "" : params.getAsString("userId")) + ",异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[获取方案信息]系统发生异常!用户编号=" + (params == null? "" : params.getAsString("userId")) + ",异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 神单分享
     * @author  mcdog
     */
    @RequestMapping(value="/share")
    public void shareScheme(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//提取参数
            params.put("userId",getLoginUserId(request));//设置当前登录用户编号
            schemeService.shareScheme(params,result);//调用神单分享方法
        }
        catch (ServiceException e1)
        {
            logger.error("[神单分享]服务发生异常!用户编号=" + (params == null? "" : params.getAsString("userId")) + ",异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[神单分享]系统发生异常!用户编号=" + (params == null? "" : params.getAsString("userId")) + ",异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 查询神单方案(晒单达人)
     * @author  mcdog
     */
    @RequestMapping(value="/sd/get")
    public void getSdSchemes(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//提取参数
            settingLoginUser(request);
            params.put("userId",getLoginUserId(request));//设置当前登录用户编号
            schemeService.getSdSchemes(params,result);//查询用户神单方案
        }
        catch (ServiceException e1)
        {
            logger.error("[查询神单方案(晒单达人)]服务发生异常!异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[查询神单方案(晒单达人)]系统发生异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 用户晒单
     * @author  mcdog
     */
    @RequestMapping(value="/sd/gd")
    public void schemeGd(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//提取参数
            logger.info("[用户晒单]用户编号=" + getLoginUserId(request) + ",接收原始参数=" + params.toString());
            params.put("userId",getLoginUserId(request));//设置当前登录用户编号
            schemeService.shareScheme(params,result);//调用神单分享方法
        }
        catch (ServiceException e1)
        {
            logger.error("[用户晒单]服务发生异常!用户编号=" + (params == null? "" : params.getAsString("userId")) + ",异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[用户晒单]系统发生异常!用户编号=" + (params == null? "" : params.getAsString("userId")) + ",异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 查询神单命中榜
     * @author  mcdog
     */
    @RequestMapping(value="/sd/tj/mz")
    public void getSdTjForMz(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//提取参数
            schemeService.getSdTjForMz(params,result);//查询用户神单方案
        }
        catch (ServiceException e1)
        {
            logger.error("[查询神单命中榜]服务发生异常!异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[查询神单命中榜]系统发生异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 查询神单盈利榜
     * @author  mcdog
     */
    @RequestMapping(value="/sd/tj/yl")
    public void getSdTjForYl(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//提取参数
            schemeService.getSdTjForYl(params,result);//查询用户神单方案
        }
        catch (ServiceException e1)
        {
            logger.error("[查询神单盈利榜]服务发生异常!异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[查询神单盈利榜]系统发生异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 查询神单连红榜
     * @author  mcdog
     */
    @RequestMapping(value="/sd/tj/lh")
    public void getSdTjForLh(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//提取参数
            schemeService.getSdTjForLh(params,result);//查询用户神单方案
        }
        catch (ServiceException e1)
        {
            logger.error("[查询神单连红榜]服务发生异常!异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[查询神单连红榜]系统发生异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 跟单
     * @author  mcdog
     */
    @RequestMapping(value="/follow")
    public void schemeFollow(SchemeBean schemeBean, HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        try
        {
            logger.info("[跟单]用户编号=" + getLoginUserId(request) + ",接收原始参数:" + ReflectionToString.toString(schemeBean));
            schemeBean.setUserId(getLoginUserId(request));//设置当前登录用户编号
            schemeService.schemeFollow(schemeBean,result);//调用跟单方法
        }
        catch (ServiceException e1)
        {
            logger.error("[跟单]服务发生异常!用户编号=" + schemeBean.getUserId() + ",异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[跟单]系统发生异常!用户编号=" + schemeBean.getUserId() + ",异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 搜索神单达人
     * @author  mcdog
     */
    @RequestMapping(value="/sd/tj/search")
    public void searchSdUser(SchemeBean schemeBean, HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//提取参数
            schemeService.getSdUser(params,result);//调用获取神单用户方法
        }
        catch (ServiceException e1)
        {
            logger.error("[搜索神单达人]服务发生异常!异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[跟单]系统发生异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 神单详情
     * @author  mcdog
     */
    @RequestMapping(value="/sd/detail")
    public void getSdDetail(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//提取参数
            settingLoginUser(request);
            params.put("userId",getLoginUserId(request));//设置当前登录用户编号
            schemeService.getSdSchemeDetail(params,result);//获取神单详情
        }
        catch (ServiceException e1)
        {
            logger.error("[神单详情]服务发生异常!异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[神单详情]系统发生异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 获取我的晒单
     * @author  mcdog
     */
    @RequestMapping(value="/sd/user/usersd")
    public void getUserSd(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//提取参数
            params.put("userId",getLoginUserId(request));//设置当前登录用户编号
            schemeService.getUserSd(params,result);//获取用户晒单
        }
        catch (ServiceException e1)
        {
            logger.error("[获取用户晒单]服务发生异常!用户编号=" + getLoginUserId(request) + ",异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[获取用户晒单]系统发生异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 获取我的最近战绩
     * @author  mcdog
     */
    @RequestMapping(value="/sd/user/historysd")
    public void getUserHistorySd(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//提取参数
            params.put("userId",getLoginUserId(request));//设置当前登录用户编号
            params.put("suserId",params.get("userId"));
            params.put("hideType",0);//设置对阵隐藏模式为不隐藏
            schemeService.getUserHistorySd(params,result);//获取用户最近战绩
        }
        catch (ServiceException e1)
        {
            logger.error("[获取用户最近战绩]服务发生异常!用户编号=" + getLoginUserId(request) + ",异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[获取用户最近战绩]系统发生异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 获取我的关注
     * @author  mcdog
     */
    @RequestMapping(value="/sd/user/usergz")
    public void getUserGz(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//提取参数
            params.put("userId",getLoginUserId(request));//设置当前登录用户编号
            schemeService.getUserGz(params,result);//获取用户关注
        }
        catch (ServiceException e1)
        {
            logger.error("[获取用户关注]服务发生异常!用户编号=" + getLoginUserId(request) + ",异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[获取用户关注]系统发生异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 获取我的粉丝
     * @author  mcdog
     */
    @RequestMapping(value="/sd/user/userfans")
    public void getUserFans(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//提取参数
            params.put("userId",getLoginUserId(request));//设置当前登录用户编号
            schemeService.getUserFans(params,result);//获取用户粉丝
        }
        catch (ServiceException e1)
        {
            logger.error("[获取用户粉丝]服务发生异常!用户编号=" + getLoginUserId(request) + ",异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[获取用户粉丝]系统发生异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 达人主页
     * @author  mcdog
     */
    @RequestMapping(value="/sd/tj/uhome")
    public void getSdUserHome(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//提取参数
            settingLoginUser(request);
            params.put("userId",getLoginUserId(request));//设置当前登录用户编号
            schemeService.getSdUserHome(params,result);//获取达人主页信息
        }
        catch (ServiceException e1)
        {
            logger.error("[达人主页]服务发生异常!异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[达人主页]系统发生异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 获取达人最近战绩
     * @author  mcdog
     */
    @RequestMapping(value="/sd/tj/historysd")
    public void getSdUserHistorySd(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//提取参数
            params.put("userId",getLoginUserId(request));//设置当前登录用户编号
            params.put("suserId",params.get("uid"));
            params.put("hideType",1);//设置对阵隐藏模式以方案对阵及选项的隐藏模式为准
            schemeService.getUserHistorySd(params,result);//获取用户最近战绩
        }
        catch (ServiceException e1)
        {
            logger.error("[获取达人最近战绩]服务发生异常!异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[获取达人最近战绩]系统发生异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 关注达人
     * @author  mcdog
     */
    @RequestMapping(value="/sd/user/gz")
    public void followSdUser(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//提取参数
            params.put("userId",getLoginUserId(request));//设置当前登录用户编号
            schemeService.insertFollowSdUser(params,result);//调用关注达人方法
        }
        catch (ServiceException e1)
        {
            logger.error("[关注达人]服务发生异常!用户编号=" + getLoginUserId(request) + ",异常信息:" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[关注达人]系统发生异常!用户编号=" + getLoginUserId(request) + ",异常信息:" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 取消关注达人
     * @author  mcdog
     */
    @RequestMapping(value="/sd/user/qxgz")
    public void qxgzSdUser(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        result.setErrorCode(ErrorCode.SERVER_ERROR);
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//提取参数
            params.put("userId",getLoginUserId(request));//设置当前登录用户编号
            schemeService.deletetFollowSdUser(params,result);//调用取消关注达人方法
        }
        catch (ServiceException e1)
        {
            logger.error("[取消关注达人]服务发生异常!用户编号=" + getLoginUserId(request) + ",异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[取消关注达人]系统发生异常!用户编号=" + getLoginUserId(request) + ",异常信息：" + e);
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
            if(AuthSign.checkSign(request))
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
        }
        catch (Exception e)
        {
            logger.error("[设置登录用户]发生异常!异常信息:",e);
        }
    }
}