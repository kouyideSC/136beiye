package com.caipiao.app.lottery;

import com.caipiao.app.base.BaseController;
import com.caipiao.app.utils.WebUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.code.ErrorCode;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.service.common.AreaService;
import com.caipiao.service.common.BankService;
import com.caipiao.service.exception.ServiceException;
import com.caipiao.service.kaijiang.KaiJiangService;
import com.caipiao.service.lottery.LotteryService;
import com.caipiao.service.lottery.ZxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;

/**
 * 资讯接口控制类
 */
@Controller
@RequestMapping("/zx")
public class ZxController extends BaseController
{
    private static Logger logger = LoggerFactory.getLogger(ZxController.class);

    @Autowired
    private ZxService zxService;

    /**
     * 获取资讯
     * @author  mcdog
     */
    @RequestMapping(value="/get")
    public void getZx(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//获取参数
            zxService.getZx(params,result);//获取资讯数据
        }
        catch (ServiceException e1)
        {
            logger.error("[获取资讯]服务发生异常!异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[获取资讯]系统异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 获取资讯首页数据
     * @author  mcdog
     */
    @RequestMapping(value="/home/get")
    public void getHomeDatas(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//获取参数
            zxService.getHomeDatas(params,result);//获取首页数据
        }
        catch (ServiceException e1)
        {
            logger.error("[获取资讯首页数据]服务发生异常!异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[获取资讯首页数据]系统异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 获取推荐
     * @author  mcdog
     */
    @RequestMapping(value="/tj/get")
    public void getHotTj(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//获取参数
            zxService.getTj(params,result);//获取热门推荐
        }
        catch (ServiceException e1)
        {
            logger.error("[获取热门推荐]服务发生异常!异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[获取热门推荐]系统异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 获取推荐详情
     * @author  mcdog
     */
    @RequestMapping(value="/tj/detail")
    public void getTjDetail(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//获取参数
            logger.error("[获取推荐详情]接收原始参数=" + params.toString());
            zxService.getTjDetail(params,result);//获取推荐详情
        }
        catch (ServiceException e1)
        {
            logger.error("[获取推荐详情]服务发生异常!异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[获取推荐详情]系统异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 获取热门赛事
     * @author  mcdog
     */
    @RequestMapping(value="/match/hot")
    public void getHotMatch(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//获取请求参数
            zxService.getHotMatch(params,result);//获取热门赛事
        }
        catch (ServiceException e1)
        {
            logger.error("[获取热门赛事]服务发生异常!异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[获取热门赛事]系统发生异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 获取消息通知
     * @author  mcdog
     */
    @RequestMapping(value="/notice/get")
    public void getNotice(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//获取请求参数
            zxService.getNotice(params,result);//获取热门赛事
        }
        catch (ServiceException e1)
        {
            logger.error("[获取消息通知]服务发生异常!异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[获取消息通知]系统发生异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 意见反馈
     * @author  mcdog
     */
    @RequestMapping(value="/suggest")
    public void suggest(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        try
        {
            result.setErrorCode(ErrorCode_API.SUCCESS);
            result.setErrorDesc("意见反馈成功!感谢您的建议");
        }
        catch (Exception e)
        {
            logger.error("[意见反馈]系统发生异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }
}