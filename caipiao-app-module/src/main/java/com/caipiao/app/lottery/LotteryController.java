package com.caipiao.app.lottery;

import com.caipiao.app.base.BaseController;
import com.caipiao.app.utils.WebUtils;
import com.caipiao.common.constants.KeyConstants;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.code.ErrorCode;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.vo.KaiJiangVo;
import com.caipiao.service.common.AreaService;
import com.caipiao.service.common.BankService;
import com.caipiao.service.exception.ServiceException;
import com.caipiao.service.kaijiang.KaiJiangService;
import com.caipiao.service.lottery.LotteryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 彩票接口控制类
 */
@Controller
@RequestMapping("/lottery")
public class LotteryController extends BaseController
{
    private static Logger logger = LoggerFactory.getLogger(LotteryController.class);

    @Autowired
    private KaiJiangService kaiJiangService;

    @Autowired
    private AreaService areaService;

    @Autowired
    private BankService bankService;

    @Autowired
    private LotteryService lotteryService;

    /**
     * 获取服务器当前时间
     * @author  mcdog
     */
    @RequestMapping(value="/now")
    public void getAllKj(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        try
        {
            Calendar calendar = Calendar.getInstance();
            result.setData(new BaseDto("ctime",DateUtil.formatDate(calendar.getTime(),DateUtil.DEFAULT_DATE_TIME)));
        }
        catch (Exception e)
        {
            logger.error("[获取服务器当前时间]系统异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 获取银行
     * @author  mcdog
     */
    @RequestMapping(value="/bank/get")
    public void getBanks(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//获取请求参数
            logger.info("[获取银行]接收原始参数:" + params.toString());
            bankService.getBanks(params,result);//获取银行信息
        }
        catch (ServiceException e1)
        {
            logger.error("[获取银行]服务发生异常!异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[获取银行]系统发生异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 获取支行
     * @author  mcdog
     */
    @RequestMapping(value="/bank/getSub")
    public void getBankSub(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//获取请求参数
            logger.info("[获取支行]接收原始参数:" + params.toString());
            bankService.getBankSubs(params,result);//查询支行信息
        }
        catch (ServiceException e1)
        {
            logger.error("[获取支行]服务发生异常!异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[获取支行]系统发生异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 获取省份
     * @author  mcdog
     */
    @RequestMapping(value="/area/getProvince")
    public void getProvinces(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//获取请求参数
            logger.info("[获取省份]接收原始参数:" + params.toString());
            areaService.getProvinces(params,result);//查询省份
        }
        catch (ServiceException e1)
        {
            logger.error("[获取省份]服务发生异常!异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[获取省份]系统发生异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 获取城市
     * @author  mcdog
     */
    @RequestMapping(value="/area/getCity")
    public void getCitys(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//获取请求参数
            logger.info("[获取城市]接收原始参数:" + params.toString());
            areaService.getCitys(params,result);//查询城市
        }
        catch (ServiceException e1)
        {
            logger.error("[获取城市]服务发生异常!异常信息：" + e1);
            result.setErrorCode(e1.getErrorCode());
            if(StringUtil.isNotEmpty(e1.getMessage()))
            {
                result.setErrorDesc(e1.getMessage());
            }
        }
        catch (Exception e)
        {
            logger.error("[获取城市]系统发生异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 获取关于我们
     * @author  mcdog
     */
    @RequestMapping(value="/aboutus")
    public void getAboutus(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        try
        {
            result.setData(new BaseDto("content",lotteryService.getAboutUs()));//设置关于我们
            result.setErrorCode(ErrorCode_API.SUCCESS);
        }
        catch (Exception e)
        {
            logger.error("[获取关于我们]系统发生异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 获取用户协议
     * @author  mcdog
     */
    @RequestMapping(value="/agreement")
    public void getUserAgreement(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        try
        {
            result.setData(new BaseDto("content",lotteryService.getUserAgreement()));//设置用户协议
            result.setErrorCode(ErrorCode_API.SUCCESS);
        }
        catch (Exception e)
        {
            logger.error("[获取用户协议]系统发生异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }

    /**
     * 获取指定数字彩的当前期的开奖时间
     * @author  mcdog
     */
    @RequestMapping(value="/sale/kjtime")
    public void getLotteryKjTime(HttpServletRequest request, HttpServletResponse response)
    {
        ResultBean result = new ResultBean();
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//获取请求参数
            kaiJiangService.getLotteryPeriodKjTime(params,result);
        }
        catch (Exception e)
        {
            logger.error("[获取彩种期次最新开奖时间]系统发生异常!异常信息：" + e);
            result.setErrorCode(ErrorCode.NETWORK_ERROR);
        }
        writeResponse(result,response);
    }
}