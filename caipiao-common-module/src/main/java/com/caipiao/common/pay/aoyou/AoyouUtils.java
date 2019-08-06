package com.caipiao.common.pay.aoyou;

import com.caipiao.common.constants.PayConstants;
import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.HttpClientUtil;
import com.caipiao.common.util.SortUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Random;

/**
 * 傲游支付工具类
 * @author sjq
 */
public class AoyouUtils
{
    private static final Logger logger = LoggerFactory.getLogger(AoyouUtils.class);
    public static final String merchantNo = "10191";//商户号
    public static final String apiUrl = "http://pay.pk086.cn";//api地址
    public static final String secretKey = "x59yklc1f1vjyhhmz181dv5kw5wjrmi5";//签名密钥

    /**
     * 傲游支付-支付宝H5预下单
     * @author  mcdog
     * @param   params  参数对象,统一下单(dcode-下单状态 dmsg-下单状态描述 resp-原始的统一下单响应字符串 results-响应结果)结果也保存在该对象中,具体如下:
     *                  dcode=1000,下单成功
     *                  dcode=-1000,下单失败
     *                  dcode=1001,下单失败(签名错误)
     */
    public static void createAlipayWapPay(Dto params) throws Exception
    {
        params.put("dcode",-1000);//设置默认状态码
        params.put("dmsg","预下单失败");//设置默认状态码描述
        params.put("resp","");

        /**
         * 校验参数
         */
        //非空校验
        if(params == null || params.size() == 0)
        {
            params.put("dmsg","参数不能为空");
            return;
        }
        //商户号校验
        String merchantNo = params.getAsString("merchantNo");
        if(StringUtil.isEmpty(merchantNo))
        {
            params.put("dmsg","商户号merchantNo不能为空");
            return;
        }
        //应用名称/app名称/商品描述校验
        String appName = params.getAsString("appName");
        if(StringUtil.isEmpty(appName))
        {
            params.put("dmsg","应用名称/app名称/商品描述appName不能为空");
            return;
        }
        //api地址校验
        String apiUrl = params.getAsString("apiUrl");
        if(StringUtil.isEmpty(apiUrl))
        {
            params.put("dmsg","api地址apiUrl不能为空");
            return;
        }
        //签名密钥校验
        String secretKey = params.getAsString("secretKey");
        if(StringUtil.isEmpty(secretKey))
        {
            params.put("dmsg","签名密钥secretKey不能为空");
            return;
        }
        //支付结果通知地址校验
        String notifyUrl = params.getAsString("notifyUrl");
        if(StringUtil.isEmpty(notifyUrl))
        {
            params.put("dmsg","通知地址notifyUrl不能为空");
            return;
        }
        //支付成功后跳转的地址校验
        String returnUrl = params.getAsString("returnUrl");
        if(StringUtil.isEmpty(returnUrl))
        {
            params.put("dmsg","支付成功后跳转的地址returnUrl不能为空");
            return;
        }
        //商户订单号校验
        String payId = params.getAsString("payId");//获取商户订单号
        if(StringUtil.isEmpty(payId))
        {
            params.put("dmsg","商户订单号payId不能为空");
            return;
        }
        //判断金额是否大于0
        Double maxSmoney = params.getAsDouble("maxSmoney");//系统设置的单次充值最大金额
        Double smoney1 = params.getAsDouble("smoney");
        String smoney = params.getAsString("smoney");
        if(smoney1 == null || smoney1 < 0)
        {
            params.put("dmsg","充值金额smoney格式错误");
            return;
        }
        //判断金额是否超过系统设置的最大金额
        else if(maxSmoney != null && smoney1 > maxSmoney)
        {
            params.put("dmsg","充值金额smoney不能超过系统设置的单次最多充值金额");
            return;
        }
        //判断金额是否最多只有2位小数
        else if(smoney.indexOf(".") > -1 && smoney.substring(smoney.indexOf(".") + 1).length() > 2)
        {
            params.put("dmsg","充值金额smoney最多只能有2位小数");
            return;
        }

        /**
         * 拼接请求参数
         */
        Dto requestDto = new BaseDto();
        requestDto.put("pay_memberid",merchantNo);//设置商户号
        requestDto.put("pay_orderid",payId);//设置商户订单号
        requestDto.put("pay_amount",(int)(smoney1 * 100));//设置订单金额
        requestDto.put("pay_applydate",DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//设置订单时间
        requestDto.put("pay_bankcode","933");//设置网关代码,支付宝为933
        requestDto.put("pay_notifyurl",notifyUrl);//设置订单支付结果异步通知地址
        requestDto.put("pay_callbackurl",returnUrl);//设置订单支付成功跳转地址
        requestDto.put("pay_attach","");//设置附加信息
        requestDto.put("pay_productname",URLEncoder.encode(appName,"utf-8"));//设置商品名称

        //设置签名
        Dto signDto = new BaseDto();
        signDto.putAll(requestDto);
        signDto.remove("pay_productname");
        signDto.remove("pay_attach");
        String sign = SortUtils.getOrderByAsciiAscFromDto(signDto,false);
        sign += "&key=" + secretKey;
        sign = MD5.md5(sign).toUpperCase();
        requestDto.put("pay_md5sign",sign);

        requestDto.put("action",apiUrl + "/Pay_Index.html");//设置表单提交地址
        requestDto.put("pcode",PayConstants.PAYCHANNEL_CODE_AOYOUPAY);//设置渠道编号

        /**
         * 设置表单参数
         */
        String urlparams = requestDto.toSeparatorString("&");
        logger.info("[傲游支付-支付宝H5预下单]请求表单参数=" + urlparams);
        params.put("dcode",1000);//设置预下单状态码
        params.put("dmsg","success");//设置预下单状态码描述
        Dto resultsDto = new BaseDto();
        resultsDto.put("payInfo",params.getAsString("mobilehost") + PayConstants.alipayH5Page + "?" + urlparams);//设置支付链接
        params.put("results",resultsDto);
    }

    public static void main(String[] args)
    {
        try
        {
            Dto requstDto = new BaseDto();
            String payId = "CZ" + DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME);//生成商户订单号(平台流水号)
            payId += new Random().nextInt(10);
            requstDto.put("merchantNo",merchantNo);
            requstDto.put("puserid","10381");
            requstDto.put("appName","充值");
            requstDto.put("apiUrl",apiUrl);
            requstDto.put("secretKey",secretKey);
            requstDto.put("notifyUrl","http://api.szmpyd.com/api/notify/aoyou/alipay");
            requstDto.put("returnUrl","http://mobile.szmpyd.com/html/pay/notify/notify.html");
            requstDto.put("payId",payId);
            requstDto.put("smoney",100);
            requstDto.put("userIp","223.104.213.124");
            requstDto.put("bankCode","03080000");
            requstDto.put("provinceCode","31");
            requstDto.put("amount",1);

            createAlipayWapPay(requstDto);
            //createUnionPayQuickPay(requstDto);
            //createUnionPayGatewayPay(requstDto);
            //queryOrder(requstDto);
            //payToBank(requstDto);

            //requstDto.put("payId","CZ201806281536286347");
            //queryOrder(requstDto);
            //queryDpayOrder(requstDto);
            System.out.println(requstDto.getAsString("dmsg"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
