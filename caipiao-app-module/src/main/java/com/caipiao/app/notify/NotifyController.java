package com.caipiao.app.notify;

import com.caipiao.app.base.BaseController;
import com.caipiao.app.utils.WebUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.service.exception.ServiceException;
import com.caipiao.service.pay.PayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 支付通知控制类
 */
@Controller
@RequestMapping("/notify")
public class NotifyController extends BaseController
{
    private static Logger logger = LoggerFactory.getLogger(NotifyController.class);

    @Autowired
    private PayService payService;

    /**
     * 微信官方充值结果通知
     * @author  mcdog
     */
    @RequestMapping(value="/weixin")
    public void wexinPayResultNotify(HttpServletRequest request, HttpServletResponse response)
    {
        String xmlResult = "";
        try
        {
            Dto params = WebUtils.getParamsAsDtoFromXml(request);//获取参数
            logger.info("[微信官方充值结果通知]接收原始参数=" + params.toString());
            payService.doWeixinPayResult(params);//处理通知
            xmlResult = "<xml>";
            int dcode = params.getAsInteger("dcode");//提取通知处理状态码
            if(1000 == dcode)
            {
                xmlResult += "<return_code>SUCCESS</return_code>";
                xmlResult += "<return_msg>OK</return_msg>";
            }
            else
            {
                xmlResult += "<return_code>FAIL</return_code>";
                xmlResult += "<return_msg>" + (StringUtil.isNotEmpty(params.get("dmsg"))? params.getAsString("dmsg") : "处理失败") + "</return_msg>";
            }
            xmlResult += "</xml>";
        }
        catch (ServiceException e1)
        {
            logger.error("[微信官方充值结果通知]服务异常!异常信息：" + e1);
            xmlResult = "<xml><return_code>FAIL</return_code><return_msg>" + (StringUtil.isEmpty(e1.getMessage())? "服务异常" : e1.getMessage()) + "</return_msg></xml>";
        }
        catch (Exception e)
        {
            logger.error("[微信官方充值结果通知]系统异常!异常信息：" + e);
            xmlResult = "<xml><return_code>FAIL</return_code><return_msg>系统异常</return_msg></xml>";
        }
        writeResponse(xmlResult,response);
    }

    /**
     * 威富通-微信-充值结果通知
     * @author  mcdog
     */
    @RequestMapping(value="/swiftpass/weixin")
    public void swiftpassWeixinPayResultNotify(HttpServletRequest request, HttpServletResponse response)
    {
        String resultStr = "fail";
        try
        {
            Dto params = WebUtils.getParamsAsDtoFromXml(request);//获取参数
            logger.info("[威富通-微信-充值结果通知]接收原始参数=" + params.toString());
            payService.doSwiftpassWeixinPayResult(params);//处理通知
            int dcode = params.getAsInteger("dcode");//提取通知处理状态码
            if(1000 == dcode)
            {
                resultStr = "success";
            }
        }
        catch (ServiceException e1)
        {
            logger.error("[威富通-微信-充值结果通知]服务异常!异常信息：" + e1);
        }
        catch (Exception e)
        {
            logger.error("[威富通-微信-充值结果通知]系统异常!异常信息：" + e);
        }
        writeResponse(resultStr,response);
    }

    /**
     * 快接支付-充值结果通知
     * @author  mcdog
     */
    @RequestMapping(value="/kuaijie")
    public void kuaijieAlipayPayResultNotify(HttpServletRequest request, HttpServletResponse response)
    {
        String resultStr = "fail";
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//获取参数
            logger.info("[快接支付-充值结果通知]接收原始参数=" + params.toString());
            payService.doKuaijiePayResultNotify(params);//处理通知
            int dcode = params.getAsInteger("dcode");//提取通知处理状态码
            if(1000 == dcode)
            {
                resultStr = "success";
            }
        }
        catch (ServiceException e1)
        {
            logger.error("[快接支付-支付宝-充值结果通知]服务异常!异常信息：" + e1);
        }
        catch (Exception e)
        {
            logger.error("[快接支付-支付宝-充值结果通知]系统异常!异常信息：" + e);
        }
        writeResponse(resultStr,response);
    }

    /**
     * 贝付宝-微信-充值结果通知
     * @author  mcdog
     */
    @RequestMapping(value="/payfubao/weixin")
    public void payfubaoWeixinPayResultNotify(HttpServletRequest request, HttpServletResponse response)
    {
        String resultStr = "fail";
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//获取参数
            logger.info("[贝付宝-微信-充值结果通知]接收原始参数=" + params.toString());
            payService.doPayfubaoWeixinPayResultNotify(params);//处理通知
            int dcode = params.getAsInteger("dcode");//提取通知处理状态码
            if(1000 == dcode)
            {
                resultStr = "ok";
            }
        }
        catch (ServiceException e1)
        {
            logger.error("[贝付宝-微信-充值结果通知]服务异常!异常信息：" + e1);
        }
        catch (Exception e)
        {
            logger.error("[贝付宝-微信-充值结果通知]系统异常!异常信息：" + e);
        }
        writeResponse(resultStr,response);
    }

    /**
     * 贝付宝-支付宝-充值结果通知
     * @author  mcdog
     */
    @RequestMapping(value="/payfubao/alipay")
    public void payfubaoAlipayPayResultNotify(HttpServletRequest request, HttpServletResponse response)
    {
        String resultStr = "fail";
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//获取参数
            logger.info("[贝付宝-支付宝-充值结果通知]接收原始参数=" + params.toString());
            payService.doPayfubaoAlipayPayResultNotify(params);//处理通知
            int dcode = params.getAsInteger("dcode");//提取通知处理状态码
            if(1000 == dcode)
            {
                resultStr = "ok";
            }
        }
        catch (ServiceException e1)
        {
            logger.error("[贝付宝-支付宝-充值结果通知]服务异常!异常信息：" + e1);
        }
        catch (Exception e)
        {
            logger.error("[贝付宝-支付宝-充值结果通知]系统异常!异常信息：" + e);
        }
        writeResponse(resultStr,response);
    }

    /**
     * 迅游通-QQ钱包-充值结果通知
     * @author  mcdog
     */
    @RequestMapping(value="/xunyoutong/qqwallet")
    public void xunyoutongQqWalletPayResultNotify(HttpServletRequest request, HttpServletResponse response)
    {
        String resultStr = "FAIL";
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//获取参数
            logger.info("[迅游通-QQ钱包-充值结果通知]接收原始参数=" + params.toString());
            payService.doXunyoutongQqWalletPayResultNotify(params);//处理通知
            int dcode = params.getAsInteger("dcode");//提取通知处理状态码
            if(1000 == dcode)
            {
                resultStr = "SUCCESS";
            }
        }
        catch (ServiceException e1)
        {
            logger.error("[迅游通-QQ钱包-充值结果通知]服务异常!异常信息：" + e1);
        }
        catch (Exception e)
        {
            logger.error("[迅游通-QQ钱包-充值结果通知]系统异常!异常信息：" + e);
        }
        writeResponse(resultStr,response);
    }

    /**
     * 迅游通-京东钱包-充值结果通知
     * @author  mcdog
     */
    @RequestMapping(value="/xunyoutong/jdwallet")
    public void xunyoutongJdWalletPayResultNotify(HttpServletRequest request, HttpServletResponse response)
    {
        String resultStr = "FAIL";
        try
        {
            Dto params = WebUtils.getParamsAsDto(request);//获取参数
            logger.info("[迅游通-京东钱包-充值结果通知]接收原始参数=" + params.toString());
            payService.doXunyoutongJdWalletPayResultNotify(params);//处理通知
            int dcode = params.getAsInteger("dcode");//提取通知处理状态码
            if(1000 == dcode)
            {
                resultStr = "SUCCESS";
            }
        }
        catch (ServiceException e1)
        {
            logger.error("[迅游通-京东钱包-充值结果通知]服务异常!异常信息：" + e1);
        }
        catch (Exception e)
        {
            logger.error("[迅游通-京东钱包-充值结果通知]系统异常!异常信息：" + e);
        }
        writeResponse(resultStr,response);
    }

    /**
     * 盛付通-付款结果通知
     * @author  mcdog
     */
    @RequestMapping(value="/shengpay")
    public void shengpayPaymentResultNotify(HttpServletRequest request, HttpServletResponse response)
    {
        Dto resultDto = new BaseDto();
        try
        {
            Dto params = WebUtils.getParamsAsDtoFromJson(request);//获取参数
            logger.info("[盛付通付款结果通知]接收原始参数:" + params.toString());
            payService.doShengpayPaymentResult(params);//处理通知
            int dcode = params.getAsInteger("dcode");//提取通知处理状态码
            if(1000 == dcode)
            {
                resultDto.put("code","ok");
            }
            else
            {
                resultDto.put("code",params.getAsString("dmsg"));
            }
        }
        catch (ServiceException e1)
        {
            logger.error("[盛付通付款结果通知]服务异常!异常信息：" + e1);
            resultDto.put("code",StringUtil.isEmpty(e1.getMessage())? "failure" : e1.getMessage());
        }
        catch (Exception e)
        {
            logger.error("[盛付通付款结果通知]系统异常!异常信息：" + e);
            resultDto.put("code","failure");
        }
        writeResponse(resultDto.toJson(),response);
    }

    /**
     * 聚合支付-代付结果通知
     * @author  mcdog
     */
    @RequestMapping(value="/juhe/dpay")
    public void juheDpayResultNotify(HttpServletRequest request, HttpServletResponse response)
    {
        String resultStr = "fail";
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//获取参数
            logger.info("[聚合支付-代付结果通知]接收原始参数=" + params.toString());
            payService.doJuheDpayResult(params);//处理通知
            int dcode = params.getAsInteger("dcode");//提取通知处理状态码
            if(1000 == dcode)
            {
                resultStr = "success";
            }
        }
        catch (ServiceException e1)
        {
            logger.error("[聚合支付-代付结果通知]服务异常!接收原始参数=" + params.toString() + ",异常信息：" + e1);
        }
        catch (Exception e)
        {
            logger.error("[聚合支付-代付结果通知]系统异常!接收原始参数=" + params.toString() + ",异常信息：" + e);
        }
        writeResponse(resultStr,response);
    }

    /**
     * 聚合支付10381-支付宝-充值结果通知
     * @author  mcdog
     */
    @RequestMapping(value="/juhe10381/alipay")
    public void juheAlipayPayResultNotify(HttpServletRequest request, HttpServletResponse response)
    {
        String resultStr = "fail";
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDtoFromJson(request);//获取参数
            logger.info("[聚合支付-支付宝-充值结果通知]接收原始参数=" + params.toString());
            payService.doJuheAlipayPayResultNotify(params);//处理通知
            int dcode = params.getAsInteger("dcode");//提取通知处理状态码
            if(1000 == dcode)
            {
                resultStr = "success";
            }
        }
        catch (ServiceException e1)
        {
            logger.error("[聚合支付-支付宝充值结果通知]服务异常!接收原始参数=" + params.toString() + ",异常信息：" + e1);
        }
        catch (Exception e)
        {
            logger.error("[聚合支付-支付宝充值结果通知]系统异常!接收原始参数=" + params.toString() + ",异常信息：" + e);
        }
        writeResponse(resultStr,response);
    }

    /**
     * 直付支付-充值结果通知
     * @author  mcdog
     */
    @RequestMapping(value="/zhifu")
    public void zhifuAlipayPayResultNotify(HttpServletRequest request, HttpServletResponse response)
    {
        String resultStr = "FAIL";
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//获取参数
            logger.info("[直付支付-充值结果通知]接收原始参数=" + params.toString());
            payService.doZhifuPayResultNotify(params);//处理通知
            int dcode = params.getAsInteger("dcode");//提取通知处理状态码
            if(1000 == dcode)
            {
                resultStr = "SUCCESS";
            }
        }
        catch (ServiceException e1)
        {
            logger.error("[直付支付-充值结果通知]服务异常!接收原始参数=" + params.toString() + ",异常信息：" + e1);
        }
        catch (Exception e)
        {
            logger.error("[直付支付-充值结果通知]系统异常!接收原始参数=" + params.toString() + ",异常信息：" + e);
        }
        writeResponse(resultStr,response);
    }

    /**
     * 汇潮支付-代付结果通知
     * @author  mcdog
     */
    @RequestMapping(value="/huichao")
    public void huichaoDpayResultNotify(HttpServletRequest request, HttpServletResponse response)
    {
        String resultStr = "fail";
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//获取参数
            logger.info("[汇潮支付-代付结果通知]接收原始参数=" + params.toString());
            payService.doHuichaoDpayResult(params);//处理通知
            int dcode = params.getAsInteger("dcode");//提取通知处理状态码
            if(1000 == dcode)
            {
                resultStr = "success";
            }
        }
        catch (ServiceException e1)
        {
            logger.error("[汇潮支付-代付结果通知]服务异常!接收原始参数=" + params.toString() + ",异常信息：" + e1);
        }
        catch (Exception e)
        {
            logger.error("[汇潮支付-代付结果通知]系统异常!接收原始参数=" + params.toString() + ",异常信息：" + e);
        }
        writeResponse(resultStr,response);
    }

    /**
     * 智能云收银-充值结果通知
     * @author  mcdog
     */
    @RequestMapping(value="/zhinengyun")
    public void zhinengyunAlipayPayResultNotify(HttpServletRequest request, HttpServletResponse response)
    {
        String resultStr = "fail";
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//获取参数
            logger.info("[智能云收银-充值结果通知]接收原始参数=" + params.toString());
            payService.doZhinengyunPayResultNotify(params);//处理通知
            int dcode = params.getAsInteger("dcode");//提取通知处理状态码
            if(1000 == dcode)
            {
                resultStr = "success";
            }
        }
        catch (ServiceException e1)
        {
            logger.error("[智能云收银-充值结果通知]服务异常!接收原始参数=" + params.toString() + ",异常信息：" + e1);
        }
        catch (Exception e)
        {
            logger.error("[智能云收银-充值结果通知]系统异常!接收原始参数=" + params.toString() + ",异常信息：" + e);
        }
        writeResponse(resultStr,response);
    }

    /**
     * 傲游支付-充值结果通知
     * @author  mcdog
     */
    @RequestMapping(value="/aoyou")
    public void aoyouPayResultNotify(HttpServletRequest request, HttpServletResponse response)
    {
        String resultStr = "fail";
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//获取参数
            logger.info("[傲游支付-充值结果通知]接收原始参数=" + params.toString());
            payService.doAoyouPayResultNotify(params);//处理通知
            int dcode = params.getAsInteger("dcode");//提取通知处理状态码
            if(1000 == dcode)
            {
                resultStr = "ok";
            }
        }
        catch (ServiceException e1)
        {
            logger.error("[傲游支付-充值结果通知]服务异常!接收原始参数=" + params.toString() + ",异常信息：" + e1);
        }
        catch (Exception e)
        {
            logger.error("[傲游支付-充值结果通知]系统异常!接收原始参数=" + params.toString() + ",异常信息：" + e);
        }
        writeResponse(resultStr,response);
    }

    /**
     * BB支付-充值结果通知
     * @author  mcdog
     */
    @RequestMapping(value="/bbpay")
    public void bbPayResultNotify(HttpServletRequest request, HttpServletResponse response)
    {
        String resultStr = "fail";
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//获取参数
            logger.info("[BB支付-充值结果通知]接收原始参数=" + params.toString());
            payService.doBbPayResultNotify(params);//处理通知
            int dcode = params.getAsInteger("dcode");//提取通知处理状态码
            if(1000 == dcode)
            {
                resultStr = "ok";
            }
        }
        catch (ServiceException e1)
        {
            logger.error("[BB支付-充值结果通知]服务异常!接收原始参数=" + params.toString() + ",异常信息：" + e1);
        }
        catch (Exception e)
        {
            logger.error("[BB支付-充值结果通知]系统异常!接收原始参数=" + params.toString() + ",异常信息：" + e);
        }
        writeResponse(resultStr,response);
    }

    /**
     * 陌陌付-支付宝-充值结果通知
     * @author  mcdog
     */
    @RequestMapping(value="/momopay/alipay")
    public void momoAlipayPayResultNotify(HttpServletRequest request, HttpServletResponse response)
    {
        String resultStr = "FAIL";
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//获取参数
            logger.info("[陌陌付-支付宝-充值结果通知]接收原始参数=" + params.toString());
            payService.doMomoAlipayPayResultNotify(params);//处理通知
            int dcode = params.getAsInteger("dcode");//提取通知处理状态码
            if(1000 == dcode)
            {
                resultStr = "OK";
            }
        }
        catch (ServiceException e1)
        {
            logger.error("[陌陌付-支付宝充值结果通知]服务异常!接收原始参数=" + params.toString() + ",异常信息：" + e1);
        }
        catch (Exception e)
        {
            logger.error("[陌陌付-支付宝充值结果通知]系统异常!接收原始参数=" + params.toString() + ",异常信息：" + e);
        }
        writeResponse(resultStr,response);
    }

    /**
     * 万两支付-充值结果通知
     * @author  mcdog
     */
    @RequestMapping(value="/wlpay")
    public void wlpayPayResultNotify(HttpServletRequest request, HttpServletResponse response)
    {
        String resultStr = "fail";
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//获取参数
            logger.info("[万两支付-充值结果通知]接收原始参数=" + params.toString());
            payService.doWlpayPayResultNotify(params);//处理通知
            int dcode = params.getAsInteger("dcode");//提取通知处理状态码
            if(1000 == dcode)
            {
                resultStr = "ok";
            }
        }
        catch (ServiceException e1)
        {
            logger.error("[万两支付-充值结果通知]服务异常!接收原始参数=" + params.toString() + ",异常信息：" + e1);
        }
        catch (Exception e)
        {
            logger.error("[万两支付-充值结果通知]系统异常!接收原始参数=" + params.toString() + ",异常信息：" + e);
        }
        writeResponse(resultStr,response);
    }

    /**
     * 兆行支付-充值结果通知
     * @author  mcdog
     */
    @RequestMapping(value="/zhaohang")
    public void zhaohangPayResultNotify(HttpServletRequest request, HttpServletResponse response)
    {
        String resultStr = "FAIL";
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//获取参数
            logger.info("[兆行支付-充值结果通知]接收原始参数=" + params.toString());
            payService.doZhaohangPayResultNotify(params);//处理通知
            int dcode = params.getAsInteger("dcode");//提取通知处理状态码
            if(1000 == dcode)
            {
                resultStr = "SUCCESS";
            }
        }
        catch (ServiceException e1)
        {
            logger.error("[兆行支付-充值结果通知]服务异常!接收原始参数=" + params.toString() + ",异常信息：" + e1);
        }
        catch (Exception e)
        {
            logger.error("[兆行支付-充值结果通知]系统异常!接收原始参数=" + params.toString() + ",异常信息：" + e);
        }
        writeResponse(resultStr,response);
    }

    /**
     * 亿富通支付-充值结果通知
     * @author  mcdog
     */
    @RequestMapping(value="/yifutong")
    public void yifutongPayResultNotify(HttpServletRequest request, HttpServletResponse response)
    {
        String resultStr = "fail";
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//获取参数
            logger.info("[亿富通支付-充值结果通知]接收原始参数=" + params.toString());
            payService.doYifutongPayResultNotify(params);//处理通知
            int dcode = params.getAsInteger("dcode");//提取通知处理状态码
            if(1000 == dcode)
            {
                resultStr = "success";
            }
        }
        catch (ServiceException e1)
        {
            logger.error("[亿富通支付-充值结果通知]服务异常!接收原始参数=" + params.toString() + ",异常信息：" + e1);
        }
        catch (Exception e)
        {
            logger.error("[亿富通支付-充值结果通知]系统异常!接收原始参数=" + params.toString() + ",异常信息：" + e);
        }
        writeResponse(resultStr,response);
    }

    /**
     * 易旨支付(一麻袋)-代付结果通知
     * @author  mcdog
     */
    @RequestMapping(value="/yizhi/df")
    public void yizhiDpayResultNotify(HttpServletRequest request, HttpServletResponse response)
    {
        String resultStr = "fail";
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//获取参数
            logger.info("[易旨支付(一麻袋)-代付结果通知]接收原始参数=" + params.toString());
            payService.doYizhiDpayResult(params);//处理通知
            int dcode = params.getAsInteger("dcode");//提取通知处理状态码
            if(1000 == dcode)
            {
                resultStr = "ok";
            }
        }
        catch (ServiceException e1)
        {
            logger.error("[易旨支付(一麻袋)-代付结果通知]服务异常!接收原始参数=" + params.toString() + ",异常信息：" + e1);
        }
        catch (Exception e)
        {
            logger.error("[易旨支付(一麻袋)-代付结果通知]系统异常!接收原始参数=" + params.toString() + ",异常信息：" + e);
        }
        writeResponse(resultStr,response);
    }

    /**
     * 易旨支付-充值结果通知
     * @author  mcdog
     */
    @RequestMapping(value="/yizhi")
    public void yizhiPayResultNotify(HttpServletRequest request, HttpServletResponse response)
    {
        String resultStr = "fail";
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//获取参数
            logger.info("[易旨支付-充值结果通知]接收原始参数=" + params.toString());
            payService.doYizhiPayResultNotify(params);//处理通知
            int dcode = params.getAsInteger("dcode");//提取通知处理状态码
            if(1000 == dcode)
            {
                resultStr = "success";
            }
        }
        catch (ServiceException e1)
        {
            logger.error("[易旨支付-充值结果通知]服务异常!接收原始参数=" + params.toString() + ",异常信息：" + e1);
        }
        catch (Exception e)
        {
            logger.error("[易旨支付-充值结果通知]系统异常!接收原始参数=" + params.toString() + ",异常信息：" + e);
        }
        writeResponse(resultStr,response);
    }

    /**
     * ypay-充值结果通知
     * @author  mcdog
     */
    @RequestMapping(value="/ypay")
    public void ypayPayResultNotify(HttpServletRequest request, HttpServletResponse response)
    {
        String resultStr = "fail";
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//获取参数
            logger.info("[ypay-充值结果通知]接收原始参数=" + params.toString());
            payService.doYPayResultNotify(params);//处理通知
            int dcode = params.getAsInteger("dcode");//提取通知处理状态码
            if(1000 == dcode)
            {
                resultStr = "ok";
            }
        }
        catch (ServiceException e1)
        {
            logger.error("[ypay-充值结果通知]服务异常!接收原始参数=" + params.toString() + ",异常信息：" + e1);
        }
        catch (Exception e)
        {
            logger.error("[ypay-充值结果通知]系统异常!接收原始参数=" + params.toString() + ",异常信息：" + e);
        }
        writeResponse(resultStr,response);
    }

    /**
     * kj412-充值结果通知
     * @author  mcdog
     */
    @RequestMapping(value="/kj412")
    public void kj412PayResultNotify(HttpServletRequest request, HttpServletResponse response)
    {
        String resultStr = "fail";
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//获取参数
            logger.info("[kj412-充值结果通知]接收原始参数=" + params.toString());
            payService.doKj412PayResultNotify(params);//处理通知
            int dcode = params.getAsInteger("dcode");//提取通知处理状态码
            if(1000 == dcode)
            {
                resultStr = "0000";
            }
        }
        catch (ServiceException e1)
        {
            logger.error("[kj412-充值结果通知]服务异常!接收原始参数=" + params.toString() + ",异常信息：" + e1);
        }
        catch (Exception e)
        {
            logger.error("[kj412-充值结果通知]系统异常!接收原始参数=" + params.toString() + ",异常信息：" + e);
        }
        writeResponse(resultStr,response);
    }

    /**
     * kj412-代付结果通知
     * @author  mcdog
     */
    @RequestMapping(value="/kj412/df")
    public void kj412DPayResultNotify(HttpServletRequest request, HttpServletResponse response)
    {
        String resultStr = "fail";
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDto(request);//获取参数
            logger.info("[kj412-代付结果通知]接收原始参数=" + params.toString());
            payService.doKj412DpayResult(params);//处理通知
            int dcode = params.getAsInteger("dcode");//提取通知处理状态码
            if(1000 == dcode)
            {
                resultStr = "0000";
            }
        }
        catch (ServiceException e1)
        {
            logger.error("[kj412-代付结果通知]服务异常!接收原始参数=" + params.toString() + ",异常信息：" + e1);
        }
        catch (Exception e)
        {
            logger.error("[kj412-代付结果通知]系统异常!接收原始参数=" + params.toString() + ",异常信息：" + e);
        }
        writeResponse(resultStr,response);
    }

    /**
     * ttpay-充值结果通知
     * @author  mcdog
     */
    @RequestMapping(value="/ttpay")
    public void ttpayPayResultNotify(HttpServletRequest request, HttpServletResponse response)
    {
        Dto resultDto = new BaseDto("fail","fail");
        Dto params = null;
        try
        {
            params = WebUtils.getParamsAsDtoFromJson(request);//获取参数
            logger.info("[ttpay-充值结果通知]接收原始参数=" + params.toString());
            payService.doTtPayResultNotify(params);//处理通知
            int dcode = params.getAsInteger("dcode");//提取通知处理状态码
            if(1000 == dcode)
            {
                resultDto = new BaseDto("success","true");
            }
        }
        catch (ServiceException e1)
        {
            logger.error("[ttpay-充值结果通知]服务异常!接收原始参数=" + params.toString() + ",异常信息：" + e1);
        }
        catch (Exception e)
        {
            logger.error("[ttpay-充值结果通知]系统异常!接收原始参数=" + params.toString() + ",异常信息：" + e);
        }
        writeResponse(resultDto.toJson(),response);
    }
}