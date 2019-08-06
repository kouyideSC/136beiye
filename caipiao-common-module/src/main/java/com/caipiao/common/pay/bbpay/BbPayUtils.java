package com.caipiao.common.pay.bbpay;

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

import java.util.Date;
import java.util.Random;

/**
 * BB支付工具类
 * @author sjq
 */
public class BbPayUtils
{
    private static final Logger logger = LoggerFactory.getLogger(BbPayUtils.class);
    public static final String merchantNo = "10096";//商户号
    public static final String apiUrl = "http://www.bbpay6.com";//api地址
    public static final String secretKey = "oe2qbugsqoox0jxg5s1n51et11y844fh";//签名密钥

    /**
     * BB支付-微信H5预下单
     * @author  mcdog
     * @param   params  参数对象,统一下单(dcode-下单状态 dmsg-下单状态描述 resp-原始的统一下单响应字符串 results-响应结果)结果也保存在该对象中,具体如下:
     *                  dcode=1000,下单成功
     *                  dcode=-1000,下单失败
     *                  dcode=1001,下单失败(签名错误)
     */
    public static void createWeixinWapPay(Dto params) throws Exception
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
        //客户端校验
        String clientIp = params.getAsString("clientIp");
        if(StringUtil.isEmpty(clientIp))
        {
            params.put("dmsg","客户端ip-clientIp不能为空");
            return;
        }

        /**
         * 设置请求参数
         */
        Dto requestDto = new BaseDto();
        requestDto.put("pay_memberid",merchantNo);//商户号
        requestDto.put("pay_orderid",payId);//商户订单号
        requestDto.put("pay_amount",String.format("%.2f",smoney1));//订单金额(单位:元)
        requestDto.put("pay_applydate",DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//支付时间(时间格式:yyyy-MM-dd HH:mm:ss)
        requestDto.put("pay_notifyurl",notifyUrl);//异步通知地址
        requestDto.put("pay_callbackurl",returnUrl);//支付成功跳转地址
        requestDto.put("pay_bankcode","901");//支付接口方式,微信H5-901
        requestDto.put("pay_attach","");//设置附加信息
        requestDto.put("pay_productname",appName);//产品名称/商品名称
        requestDto.put("client_ip",clientIp);//客户端IP地址

        //设置签名
        Dto signDto = new BaseDto();
        signDto.putAll(requestDto);
        signDto.remove("pay_productname");
        signDto.remove("pay_attach");
        signDto.remove("client_ip");
        String sign = SortUtils.getOrderByAsciiAscFromDto(signDto,false);
        sign += "&key=" + secretKey;
        sign = MD5.md5(sign).toUpperCase();
        requestDto.put("pay_md5sign",sign);

        requestDto.put("action",apiUrl + "/Pay_Index.html");//设置表单提交地址
        requestDto.put("pcode",PayConstants.PAYCHANNEL_CODE_BBPAY);//设置渠道编号

        /**
         * 设置表单参数
         */
        String urlparams = requestDto.toSeparatorString("&");
        logger.info("[BB支付-微信H5预下单]请求表单参数=" + urlparams);
        params.put("dcode",1000);//设置预下单状态码
        params.put("dmsg","success");//设置预下单状态码描述
        Dto resultsDto = new BaseDto();
        resultsDto.put("payInfo", params.getAsString("mobilehost") + PayConstants.weixinH5Page + "?" + urlparams);//设置支付链接
        params.put("results",resultsDto);
    }

    /**
     * BB支付-支付宝H5预下单
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
        //客户端校验
        String clientIp = params.getAsString("clientIp");
        if(StringUtil.isEmpty(clientIp))
        {
            params.put("dmsg","客户端ip-clientIp不能为空");
            return;
        }

        /**
         * 设置请求参数
         */
        Dto requestDto = new BaseDto();
        requestDto.put("pay_memberid",merchantNo);//商户号
        requestDto.put("pay_orderid",payId);//商户订单号
        requestDto.put("pay_amount",smoney);//订单金额(单位:元)
        requestDto.put("pay_applydate",DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//支付时间(时间格式:yyyy-MM-dd HH:mm:ss)
        requestDto.put("pay_notifyurl",notifyUrl);//异步通知地址
        requestDto.put("pay_callbackurl",returnUrl);//支付成功跳转地址
        requestDto.put("pay_bankcode","904");//支付接口方式,支付宝H5-904
        requestDto.put("pay_attach","");//设置附加信息
        requestDto.put("pay_productname",appName);//产品名称/商品名称
        requestDto.put("client_ip",clientIp);//客户端IP地址

        //设置签名
        Dto signDto = new BaseDto();
        signDto.putAll(requestDto);
        signDto.remove("pay_productname");
        signDto.remove("pay_attach");
        signDto.remove("client_ip");
        String sign = SortUtils.getOrderByAsciiAscFromDto(signDto,false);
        sign += "&key=" + secretKey;
        sign = MD5.md5(sign).toUpperCase();
        requestDto.put("pay_md5sign",sign);

        requestDto.put("action",apiUrl + "/Pay_Index.html");//设置表单提交地址
        requestDto.put("pcode",PayConstants.PAYCHANNEL_CODE_BBPAY);//设置渠道编号

        /**
         * 设置表单参数
         */
        String urlparams = requestDto.toSeparatorString("&");
        logger.info("[BB支付-支付宝H5预下单]请求表单参数=" + urlparams);
        params.put("dcode",1000);//设置预下单状态码
        params.put("dmsg","success");//设置预下单状态码描述
        Dto resultsDto = new BaseDto();
        resultsDto.put("payInfo", params.getAsString("mobilehost") + PayConstants.alipayH5Page + "?" + urlparams);//设置支付链接
        params.put("results",resultsDto);
    }

    /**
     * BB支付-订单查询
     * @author  mcdog
     * @param   params  参数对象,订单信息也保存在该对象中(dcode-订单查询状态 dmsg-订单查询状态描述 resp-原始的响应字符串 results-订单交易状态信息)具体如下:
     *                  dcode=1000,查询成功
     *                  dcode=-1000,查询失败
     *                  results:{status:1000,msg:'success'},具体含义如下:
     *                  status=1000,订单交易成功
     *                  status=1001,订单交易中
     *                  status=-1000,订单交易失败/关闭
     */
    public static void queryOrder(Dto params) throws Exception
    {
        params.put("dcode",-1000);//设置默认状态码
        params.put("dmsg","查询失败");//设置默认状态码描述
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
        //商户订单号校验
        String payId = params.getAsString("payId");//获取商户订单号
        if(StringUtil.isEmpty(payId))
        {
            params.put("dmsg","商户订单号payId不能为空");
            return;
        }
        //api地址校验
        String apiUrl = params.getAsString("apiUrl");
        if(StringUtil.isEmpty(apiUrl))
        {
            params.put("dmsg","api地址apiUrl不能为空");
            return;
        }

        /**
         * 设置请求参数
         */
        Dto requstDto = new BaseDto();
        requstDto.put("pay_memberid",merchantNo);//商户号
        requstDto.put("pay_orderid",payId);//商户订单号

        //设置签名
        String sign = SortUtils.getOrderByAsciiAscFromDto(requstDto,false);
        sign += "&key=" + secretKey;
        sign = MD5.md5(sign).toUpperCase();
        requstDto.put("pay_md5sign",sign);

        /**
         * 发送请求
         */
        logger.info("[BB支付-订单查询]请求参数=" + requstDto.toString());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/Pay_Trade_Query.html",requstDto);
        params.put("resp",resp);
        logger.info("[BB支付-订单查询]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(resp);
        String returncode = respDto.getAsString("returncode");//提取状态码
        if("00".equals(returncode))
        {
            //校验签名
            String respSign = respDto.getAsString("sign");//请求响应的sign
            respDto.remove("sign");
            String realSign = MD5.md5(SortUtils.getOrderByAsciiAscFromDto(respDto,false) + "&key=" + secretKey).toUpperCase();
            if(realSign.equals(respSign))
            {
                //设置响应结果
                params.put("dcode",1000);//设置查询状态码
                params.put("dmsg","success");//设置查询状态码描述

                /**
                 * 设置订单交易信息
                 */
                Dto resultsDto = new BaseDto();
                resultsDto.put("tradeNo",respDto.getAsString("transaction_id"));//设置渠道流水号
                resultsDto.put("smoney",respDto.getAsDoubleValue("amount"));//设置订单金额

                //设置订单交易信息
                String tradeState = respDto.getAsString("trade_state");//提取订单交易状态
                if("SUCCESS".equals(tradeState))
                {
                    resultsDto.put("status",1000);//设置交易状态为成功
                    resultsDto.put("msg","success");
                }
                else
                {
                    resultsDto.put("status",1001);//设置交易状态为交易中/交易已完成
                    resultsDto.put("msg","订单交易状态tradeState=" + tradeState);//设置交易状态描述
                }
                params.put("results",resultsDto);//设置查询响应结果
            }
            else
            {
                logger.error("[BB支付-订单查询]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                params.put("dcode",-1000);
                params.put("dmsg","响应结果签名不通过");
            }
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg","订单查询失败!查询状态码returncode=" + returncode);
        }
    }

    public static void main(String[] args)
    {
        try
        {
            Dto requstDto = new BaseDto();
            String payId = "CZ" + DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME);//生成商户订单号(平台流水号)
            payId += new Random().nextInt(10);
            requstDto.put("merchantNo",merchantNo);
            requstDto.put("appName","广博网络");
            requstDto.put("apiUrl",apiUrl);
            requstDto.put("secretKey",secretKey);
            requstDto.put("notifyUrl","http://api.szmpyd.com/api/notify/bbpay");
            requstDto.put("returnUrl","http://mobile.szmpyd.com/html/pay/notify/notify.html");
            requstDto.put("payId",payId);
            requstDto.put("smoney",100);
            requstDto.put("userIp","218.83.113.221");
            requstDto.put("clientIp","218.83.113.221");

            //createWeixinWapPay(requstDto);

            payId = "CZ" + DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME);//生成商户订单号(平台流水号)
            payId += new Random().nextInt(10);
            requstDto.put("payId",payId);
            createAlipayWapPay(requstDto);

            /*payId = "CZ" + DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME);//生成商户订单号(平台流水号)
            payId += new Random().nextInt(10);
            requstDto.put("payId",payId);*/
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
