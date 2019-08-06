package com.caipiao.common.pay.kuaijie;

import com.caipiao.common.constants.UserConstants;
import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.pay.PayUtils;
import com.caipiao.common.pay.weixin.WeixinUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.HttpClientUtil;
import com.caipiao.common.util.SortUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * 快接支付-工具类
 * @author  mcdog
 */
public class KuaiJieUtils
{
    private static final Logger logger = LoggerFactory.getLogger(KuaiJieUtils.class);
    public static final String merchantNo = "2019118319";//商户号
    public static final String apiUrl = "http://api.kj-pay.com";//api地址
    public static final String secretKey = "68e51d376ccd06812ca10fb5e085ff74";//签名密钥
    public static final String appNo = "201904161451149481";//应用编号
    public static final String appName = "思岂网络";//应用名称

    public static Map<String,String> ddclStatusMap = new HashMap<String,String>();//等待交易状态码集合

    static
    {
        ddclStatusMap.put("-1","待支付");
    }

    /**
     * 快接支付-微信app下单
     * @author  mcdog
     * @param   params  参数对象,统一下单(dcode-下单状态 dmsg-下单状态描述 resp-原始的统一下单响应字符串 results-响应结果)结果也保存在该对象中,具体如下:
     *                  dcode=1000,下单成功
     *                  dcode=-1000,下单失败
     *                  dcode=1001,下单失败(签名错误)
     */
    public static void createWeiXinAppPay(Dto params) throws Exception
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
        //签名方式校验
        Integer signType = params.getAsInteger("signType");
        if(StringUtil.isEmpty(signType))
        {
            params.put("dmsg","签名方式signType不能为空");
            return;
        }
        else if(PayUtils.signTypeMaps.get(signType) == null)
        {
            params.put("dmsg","签名方式不合法");
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
            params.put("dmsg","充值金额格式错误");
            return;
        }
        //判断金额是否超过系统设置的最大金额
        else if(maxSmoney != null && smoney1 > maxSmoney)
        {
            params.put("dmsg","充值金额不能超过系统设置的单次最多充值金额");
            return;
        }
        //判断金额是否最多只有2位小数
        else if(smoney.indexOf(".") > -1 && smoney.substring(smoney.indexOf(".") + 1).length() > 2)
        {
            params.put("dmsg","充值金额最多只能有2位小数");
            return;
        }

        /**
         * 设置请求参数
         */
        Dto requstDto = new BaseDto();
        requstDto.put("merchant_no",merchantNo);//设置商户号
        requstDto.put("merchant_order_no",payId);//设置商户订单号
        requstDto.put("notify_url",notifyUrl);//设置通知地址
        requstDto.put("start_time",DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME));//设置订单开始时间
        requstDto.put("trade_amount",smoney);//设置交易金额,单位为元,保留两位小数
        requstDto.put("goods_name",URLEncoder.encode(appName,"UTF-8"));//设置商品名称
        requstDto.put("goods_desc",URLEncoder.encode(appName,"UTF-8"));//设置商品描述
        requstDto.put("sign_type",signType);//设置签名方式 1-MD5

        //设置签名
        String req = SortUtils.getOrderByAsciiAscFromDto(requstDto,false);
        String sign = MD5.md5(URLDecoder.decode(req,"UTF-8") + "&key=" + secretKey);
        if(StringUtil.isEmpty(sign))
        {
            params.put("dmsg","生成签名为空!参数错误!");
            return;
        }

        /**
         * 发送请求
         */
        req += "&sign=" + sign;
        logger.info("[快接支付-微信app下单]请求参数=" + req);
        String resp = sendRequest(apiUrl + "/wechar/app_pay",req);
        params.put("resp",resp);
        logger.info("[快接支付-微信app下单]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(resp);
        String status = respDto.getAsString("status");//提取状态码
        if("1".equals(status))
        {
            //校验签名
            Dto respData = JsonUtil.jsonToDto(JSONObject.fromObject(respDto.get("data")));
            String respSign = respData.getAsString("sign");//请求响应的sign
            respData.remove("sign");
            String realSign = MD5.md5(URLDecoder.decode(SortUtils.getOrderByAsciiAscFromDto(respData,false),"UTF-8") + "&key=" + secretKey);
            if(realSign.equals(respSign))
            {
                //设置响应结果
                params.put("dcode",1000);//设置预下单状态码
                params.put("dmsg","success");//设置预下单状态码描述
                params.put("tradeNo",respData.getAsString("trade_no"));//设置渠道流水号
                Dto resultsDto = JsonUtil.jsonToDto(respData.getAsString("pay_url"));//设置支付参数
                params.put("results",resultsDto);//设置预下单响应结果
            }
            else
            {
                logger.error("[快接支付-微信app下单]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                params.put("dcode",1001);
                params.put("dmsg","响应结果签名不通过");
            }
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg",respDto.get("info"));
        }
    }

    /**
     * 快接支付-微信wap(h5)预下单
     * @author  mcdog
     * @param   params  参数对象,统一下单(dcode-下单状态 dmsg-下单状态描述 resp-原始的统一下单响应字符串 results-响应结果)结果也保存在该对象中,具体如下:
     *                  dcode=1000,下单成功
     *                  dcode=-1000,下单失败
     *                  dcode=1001,下单失败(签名错误)
     */
    public static void createWeiXinWapPay(Dto params) throws Exception
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
        //应用编号/产品编号校验
        String appNo = params.getAsString("appNo");
        if(StringUtil.isEmpty(appNo))
        {
            params.put("dmsg","应用编号/产品号appNo不能为空");
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
        //签名方式校验
        Integer signType = params.getAsInteger("signType");
        if(StringUtil.isEmpty(signType))
        {
            params.put("dmsg","签名方式signType不能为空");
            return;
        }
        else if(PayUtils.signTypeMaps.get(signType) == null)
        {
            params.put("dmsg","签名方式不合法");
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
        //wap地址校验
        String webAddress = params.getAsString("webAddress");
        if(StringUtil.isEmpty(webAddress))
        {
            params.put("dmsg","wap地址webAddress不能为空");
            return;
        }
        //终端IP校验
        String userIp = params.getAsString("userIp");
        if(StringUtil.isEmpty(userIp))
        {
            params.put("dmsg","终端IP不能为空");
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
            params.put("dmsg","充值金额格式错误");
            return;
        }
        //判断金额是否超过系统设置的最大金额
        else if(maxSmoney != null && smoney1 > maxSmoney)
        {
            params.put("dmsg","充值金额不能超过系统设置的单次最多充值金额");
            return;
        }
        //判断金额是否最多只有2位小数
        else if(smoney.indexOf(".") > -1 && smoney.substring(smoney.indexOf(".") + 1).length() > 2)
        {
            params.put("dmsg","充值金额最多只能有2位小数");
            return;
        }
        //客户端来源
        Integer clientFrom = params.getAsInteger("clientFrom");
        if(clientFrom == null)
        {
            params.put("dmsg","客户端来源clientFrom不能为空");
            return;
        }

        /**
         * 设置请求参数
         */
        Dto requstDto = new BaseDto();
        requstDto.put("merchant_no",merchantNo);//设置商户号
        requstDto.put("merchant_order_no",payId);//设置商户订单号
        requstDto.put("notify_url",notifyUrl);//设置通知地址
        requstDto.put("start_time",DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME2));//设置订单开始时间
        requstDto.put("trade_amount",smoney);//设置交易金额,单位为元,保留两位小数
        requstDto.put("goods_name",URLEncoder.encode(appName,"UTF-8"));//设置商品名称
        requstDto.put("goods_desc",URLEncoder.encode(appName,"UTF-8"));//设置商品描述

        //判断客户端来源,如果是web或者h5,则设置同步跳转地址
        if(UserConstants.USER_SOURCE_WEB == clientFrom
                || UserConstants.USER_SOURCE_H5 == clientFrom)
        {
            String returnUrl = params.getAsString("returnUrl");
            if(StringUtil.isNotEmpty(returnUrl))
            {
                requstDto.put("return_url",returnUrl);//设置同步跳转地址
                requstDto.put("pay_mode",2);//设置同步跳转类型(1-同步不跳转 2-同步跳转)
            }
        }
        requstDto.put("user_ip",userIp);//设置终端ip
        requstDto.put("app_no",appNo);//设置应用编号
        requstDto.put("sign_type",signType);//设置签名方式 1-MD5

        //设置支付场景
        String paySence = "";//支付场景参数
        if(UserConstants.USER_SOURCE_WEB == clientFrom
                || UserConstants.USER_SOURCE_H5 == clientFrom)
        {
            paySence = "{\"type\":\"Wap\",\"wap_url\":\"" + webAddress + "\",\"wap_name\":\"" + appName + "\"}";
        }
        else if(UserConstants.USER_SOURCE_IOS == clientFrom)
        {
            paySence = "{\"type\":\"IOS\",\"bundle_id\":\"" + PayUtils.iosAppFlag + "\",\"app_name\":\"" + appName + "\"}";
        }
        else if(UserConstants.USER_SOURCE_ANDROID == clientFrom)
        {
            paySence = "{\"type\":\"Android\",\"package_name\":\"" + PayUtils.androidAppPackage + "\",\"app_name\":\"" + appName + "\"}";
        }
        requstDto.put("pay_sence",URLEncoder.encode(paySence,"UTF-8"));//设置支付场景

        //设置签名
        String req = SortUtils.getOrderByAsciiAscFromDto(requstDto,false);
        String sign = "";
        if(PayUtils.signType_md5 == signType)
        {
            sign = MD5.md5(URLDecoder.decode(req,"UTF-8") + "&key=" + secretKey);
        }
        if(StringUtil.isEmpty(sign))
        {
            params.put("dmsg","生成签名为空!参数错误!");
            return;
        }

        /**
         * 发送请求
         */
        req += "&sign=" + sign;
        logger.info("[快接支付-微信wap(h5)预下单]请求参数=" + req);
        String resp = sendRequest(apiUrl + "/wechar/wap_pay",req);
        params.put("resp",resp);
        logger.info("[快接支付-微信wap(h5)预下单]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(resp);
        String status = respDto.getAsString("status");//提取状态码
        if("1".equals(status))
        {
            //校验签名
            Dto respData = JsonUtil.jsonToDto(JSONObject.fromObject(respDto.get("data")));
            String respSign = respData.getAsString("sign");//请求响应的sign
            respData.remove("sign");
            String realSign = MD5.md5(URLDecoder.decode(SortUtils.getOrderByAsciiAscFromDto(respData,false),"UTF-8") + "&key=" + secretKey);
            if(realSign.equals(respSign))
            {
                //设置响应结果
                params.put("dcode",1000);//设置预下单状态码
                params.put("dmsg","success");//设置预下单状态码描述
                params.put("tradeNo",respData.getAsString("trade_no"));//设置渠道流水号
                Dto resultsDto = new BaseDto();
                resultsDto.put("payInfo",respData.getAsString("pay_url"));//设置支付链接
                params.put("results",resultsDto);//设置预下单响应结果
            }
            else
            {
                logger.error("[快接支付-微信wap(h5)预下单]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                params.put("dcode",1001);
                params.put("dmsg","响应结果签名不通过");
            }
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg",respDto.get("info"));
        }
    }

    /**
     * 快接支付-支付宝app预下单
     * @author  mcdog
     * @param   params  参数对象,统一下单(dcode-下单状态 dmsg-下单状态描述 resp-原始的统一下单响应字符串 results-响应结果)结果也保存在该对象中,具体如下:
     *                  dcode=1000,下单成功
     *                  dcode=-1000,下单失败
     *                  dcode=1001,下单失败(签名错误)
     */
    public static void createAlipayAppPay(Dto params) throws Exception
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
        //签名方式校验
        Integer signType = params.getAsInteger("signType");
        if(StringUtil.isEmpty(signType))
        {
            params.put("dmsg","签名方式signType不能为空");
            return;
        }
        else if(PayUtils.signTypeMaps.get(signType) == null)
        {
            params.put("dmsg","签名方式不合法");
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
            params.put("dmsg","充值金额格式错误");
            return;
        }
        //判断金额是否超过系统设置的最大金额
        else if(maxSmoney != null && smoney1 > maxSmoney)
        {
            params.put("dmsg","充值金额不能超过系统设置的单次最多充值金额");
            return;
        }
        //判断金额是否最多只有2位小数
        else if(smoney.indexOf(".") > -1 && smoney.substring(smoney.indexOf(".") + 1).length() > 2)
        {
            params.put("dmsg","充值金额最多只能有2位小数");
            return;
        }

        /**
         * 设置请求参数
         */
        Dto requstDto = new BaseDto();
        requstDto.put("merchant_no",merchantNo);//设置商户号
        requstDto.put("merchant_order_no",payId);//设置商户订单号
        requstDto.put("notify_url",notifyUrl);//设置通知地址
        requstDto.put("start_time",DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME2));//设置订单开始时间
        requstDto.put("trade_amount",smoney);//设置交易金额,单位为元,保留两位小数
        requstDto.put("goods_name",URLEncoder.encode(appName,"UTF-8"));//设置商品名称
        requstDto.put("goods_desc",URLEncoder.encode(appName,"UTF-8"));//设置商品描述
        requstDto.put("sign_type",signType);//设置签名方式 1-MD5

        //设置签名
        String req = SortUtils.getOrderByAsciiAscFromDto(requstDto,false);
        String sign = MD5.md5(URLDecoder.decode(req,"UTF-8") + "&key=" + secretKey);
        if(StringUtil.isEmpty(sign))
        {
            params.put("dmsg","生成签名为空!参数错误!");
            return;
        }
        requstDto.put("sign",sign);//设置签名

        /**
         * 发送请求
         */
        req += "&sign=" + sign;
        logger.info("[快接支付-支付宝app预下单]请求参数=" + req);
        String resp = sendRequest(apiUrl + "/alipay/app_pay",req);
        params.put("resp",resp);
        logger.info("[快接支付-支付宝app预下单]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(resp);
        String status = respDto.getAsString("status");//提取状态码
        if("1".equals(status))
        {
            //校验签名
            Dto respData = JsonUtil.jsonToDto(JSONObject.fromObject(respDto.get("data")));
            /*String respSign = respData.getAsString("sign");//请求响应的sign
            respData.remove("sign");
            String realSign = MD5.md5(URLDecoder.decode(SortUtils.getOrderByAsciiAscFromDto(respData,false),"UTF-8") + "&key=" + secretKey);
            if(realSign.equals(respSign))
            {
                //设置响应结果
                params.put("dcode",1000);//设置预下单状态码
                params.put("dmsg","success");//设置预下单状态码描述
                params.put("tradeNo",respData.getAsString("trade_no"));//设置渠道流水号
                Dto resultsDto = JsonUtil.jsonToDto(respData.getAsString("pay_url"));//设置支付参数
                params.put("results",resultsDto);//设置预下单响应结果
            }
            else
            {
                logger.error("[快接支付-支付宝app预下单]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                params.put("dcode",1001);
                params.put("dmsg","响应结果签名不通过");
            }*/
            //设置响应结果
            params.put("dcode",1000);//设置预下单状态码
            params.put("dmsg","success");//设置预下单状态码描述
            params.put("tradeNo",respData.getAsString("trade_no"));//设置渠道流水号
            String[] payUrl = respData.getAsString("pay_url").split("\\&");
            Dto resultsDto = new BaseDto();//用来存放支付参数
            for(String str : payUrl)
            {
                String[] p = str.split("=");
                resultsDto.put(p[0],p[1]);
            }
            params.put("results",resultsDto);//设置预下单响应结果
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg",respDto.get("info"));
        }
    }

    /**
     * 快接支付-支付宝wap(h5)预下单
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
        //应用编号校验
        String appNo = params.getAsString("appNo");
        if(StringUtil.isEmpty(appNo))
        {
            params.put("dmsg","应用编号appNo不能为空");
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
        //签名方式校验
        Integer signType = params.getAsInteger("signType");
        if(StringUtil.isEmpty(signType))
        {
            params.put("dmsg","签名方式signType不能为空");
            return;
        }
        else if(PayUtils.signTypeMaps.get(signType) == null)
        {
            params.put("dmsg","签名方式不合法");
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
            params.put("dmsg","充值金额格式错误");
            return;
        }
        //判断金额是否超过系统设置的最大金额
        else if(maxSmoney != null && smoney1 > maxSmoney)
        {
            params.put("dmsg","充值金额不能超过系统设置的单次最多充值金额");
            return;
        }
        //判断金额是否最多只有2位小数
        else if(smoney.indexOf(".") > -1 && smoney.substring(smoney.indexOf(".") + 1).length() > 2)
        {
            params.put("dmsg","充值金额最多只能有2位小数");
            return;
        }

        /**
         * 设置请求参数
         */
        Dto requstDto = new BaseDto();
        requstDto.put("merchant_no",merchantNo);//设置商户号
        requstDto.put("merchant_order_no",payId);//设置商户订单号
        requstDto.put("notify_url",notifyUrl);//设置通知地址

        //判断客户端来源,如果是web或者h5,则设置同步跳转地址
        if(StringUtil.isNotEmpty(params.get("returnUrl")))
        {
            requstDto.put("return_url",params.get("returnUrl"));//设置同步跳转地址
        }
        requstDto.put("start_time",DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME2));//设置订单开始时间
        requstDto.put("trade_amount",String.format("%.2f",smoney1));//设置交易金额,单位为元,保留两位小数
        requstDto.put("goods_name",URLEncoder.encode(appName,"UTF-8"));//设置商品名称
        requstDto.put("goods_desc",URLEncoder.encode(appName,"UTF-8"));//设置商品描述
        requstDto.put("app_no",appNo);//设置应用编号
        requstDto.put("sign_type",signType);//设置签名方式 1-MD5

        //设置签名
        String req = SortUtils.getOrderByAsciiAscFromDto(requstDto,false);
        String sign = MD5.md5(URLDecoder.decode(req,"UTF-8") + "&key=" + secretKey);
        if(StringUtil.isEmpty(sign))
        {
            params.put("dmsg","生成签名为空!参数错误!");
            return;
        }

        /**
         * 发送请求
         */
        req += "&sign=" + sign;
        logger.info("[快接支付-支付宝wap(h5)预下单]请求参数=" + req);
        String resp = sendRequest(apiUrl + "/alipay/wap_pay",req);
        params.put("resp",resp);
        logger.info("[快接支付-支付宝wap(h5)预下单]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(resp);
        String status = respDto.getAsString("status");//提取状态码
        if("1".equals(status))
        {
            //校验签名
            Dto respData = JsonUtil.jsonToDto(JSONObject.fromObject(respDto.get("data")));
            String respSign = respData.getAsString("sign");//请求响应的sign
            respData.remove("sign");
            String realSign = MD5.md5(URLDecoder.decode(SortUtils.getOrderByAsciiAscFromDto(respData,false),"UTF-8") + "&key=" + secretKey);
            if(realSign.equals(respSign))
            {
                //设置响应结果
                params.put("dcode",1000);//设置预下单状态码
                params.put("dmsg","success");//设置预下单状态码描述
                params.put("tradeNo",respData.getAsString("trade_no"));//设置渠道流水号
                Dto resultsDto = new BaseDto();
                resultsDto.put("payInfo",respData.getAsString("pay_url"));//设置支付链接
                params.put("results",resultsDto);//设置预下单响应结果
            }
            else
            {
                logger.error("[快接支付-支付宝wap(h5)预下单]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                params.put("dcode",1001);
                params.put("dmsg","响应结果签名不通过");
            }
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg",respDto.get("info"));
        }
    }

    /**
     * 快接支付-QQ钱包wap(h5)预下单
     * @author  mcdog
     * @param   params  参数对象,统一下单(dcode-下单状态 dmsg-下单状态描述 resp-原始的统一下单响应字符串 results-响应结果)结果也保存在该对象中,具体如下:
     *                  dcode=1000,下单成功
     *                  dcode=-1000,下单失败
     *                  dcode=1001,下单失败(签名错误)
     */
    public static void createQqWalletWapPay(Dto params) throws Exception
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
        //签名方式校验
        Integer signType = params.getAsInteger("signType");
        if(StringUtil.isEmpty(signType))
        {
            params.put("dmsg","签名方式signType不能为空");
            return;
        }
        else if(PayUtils.signTypeMaps.get(signType) == null)
        {
            params.put("dmsg","签名方式不合法");
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
            params.put("dmsg","充值金额格式错误");
            return;
        }
        //判断金额是否超过系统设置的最大金额
        else if(maxSmoney != null && smoney1 > maxSmoney)
        {
            params.put("dmsg","充值金额不能超过系统设置的单次最多充值金额");
            return;
        }
        //判断金额是否最多只有2位小数
        else if(smoney.indexOf(".") > -1 && smoney.substring(smoney.indexOf(".") + 1).length() > 2)
        {
            params.put("dmsg","充值金额最多只能有2位小数");
            return;
        }

        /**
         * 设置请求参数
         */
        Dto requstDto = new BaseDto();
        requstDto.put("merchant_no",merchantNo);//设置商户号
        requstDto.put("merchant_order_no",payId);//设置商户订单号
        requstDto.put("notify_url",notifyUrl);//设置通知地址
        requstDto.put("start_time",DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME2));//设置订单开始时间
        requstDto.put("trade_amount",smoney);//设置交易金额,单位为元,保留两位小数
        requstDto.put("goods_name",URLEncoder.encode(appName,"UTF-8"));//设置商品名称
        requstDto.put("goods_desc",URLEncoder.encode(appName,"UTF-8"));//设置商品描述
        requstDto.put("sign_type",signType);//设置签名方式 1-MD5

        //设置签名
        String req = SortUtils.getOrderByAsciiAscFromDto(requstDto,false);
        String sign = MD5.md5(URLDecoder.decode(req,"UTF-8") + "&key=" + secretKey);
        if(StringUtil.isEmpty(sign))
        {
            params.put("dmsg","生成签名为空!参数错误!");
            return;
        }
        requstDto.put("sign",sign);//设置签名

        /**
         * 发送请求
         */
        req += "&sign=" + sign;
        logger.info("[快接支付-QQ钱包wap(h5)预下单]请求参数=" + req);
        String resp = sendRequest(apiUrl + "/qqpay/wap_pay",req);
        params.put("resp",resp);
        logger.info("[快接支付-QQ钱包wap(h5)预下单]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(resp);
        String status = respDto.getAsString("status");//提取状态码
        if("1".equals(status))
        {
            //校验签名
            Dto respData = JsonUtil.jsonToDto(JSONObject.fromObject(respDto.get("data")));
            String respSign = respData.getAsString("sign");//请求响应的sign
            respData.remove("sign");
            String realSign = MD5.md5(URLDecoder.decode(SortUtils.getOrderByAsciiAscFromDto(respData,false),"UTF-8") + "&key=" + secretKey);
            if(realSign.equals(respSign))
            {
                //设置响应结果
                params.put("dcode",1000);//设置预下单状态码
                params.put("dmsg","success");//设置预下单状态码描述
                params.put("tradeNo",respData.getAsString("trade_no"));//设置渠道流水号
                Dto resultsDto = new BaseDto();
                resultsDto.put("payInfo",respData.getAsString("pay_url"));//设置支付链接
                params.put("results",resultsDto);//设置预下单响应结果
            }
            else
            {
                logger.error("[快接支付-QQ钱包wap(h5)预下单]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                params.put("dcode",1001);
                params.put("dmsg","响应结果签名不通过");
            }
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg",respDto.get("info"));
        }
    }

    /**
     * 快接支付-京东钱包wap(h5)预下单
     * @author  mcdog
     * @param   params  参数对象,统一下单(dcode-下单状态 dmsg-下单状态描述 resp-原始的统一下单响应字符串 results-响应结果)结果也保存在该对象中,具体如下:
     *                  dcode=1000,下单成功
     *                  dcode=-1000,下单失败
     *                  dcode=1001,下单失败(签名错误)
     */
    public static void createJdWalletWapPay(Dto params) throws Exception
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
        //签名方式校验
        Integer signType = params.getAsInteger("signType");
        if(StringUtil.isEmpty(signType))
        {
            params.put("dmsg","签名方式signType不能为空");
            return;
        }
        else if(PayUtils.signTypeMaps.get(signType) == null)
        {
            params.put("dmsg","签名方式不合法");
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
            params.put("dmsg","充值金额格式错误");
            return;
        }
        //判断金额是否超过系统设置的最大金额
        else if(maxSmoney != null && smoney1 > maxSmoney)
        {
            params.put("dmsg","充值金额不能超过系统设置的单次最多充值金额");
            return;
        }
        //判断金额是否最多只有2位小数
        else if(smoney.indexOf(".") > -1 && smoney.substring(smoney.indexOf(".") + 1).length() > 2)
        {
            params.put("dmsg","充值金额最多只能有2位小数");
            return;
        }

        /**
         * 设置请求参数
         */
        Dto requstDto = new BaseDto();
        requstDto.put("merchant_no",merchantNo);//设置商户号
        requstDto.put("merchant_order_no",payId);//设置商户订单号
        requstDto.put("notify_url",notifyUrl);//设置通知地址
        requstDto.put("start_time",DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME2));//设置订单开始时间
        requstDto.put("trade_amount",smoney);//设置交易金额,单位为元,保留两位小数
        requstDto.put("goods_name",URLEncoder.encode(appName,"UTF-8"));//设置商品名称
        requstDto.put("goods_desc",URLEncoder.encode(appName,"UTF-8"));//设置商品描述
        requstDto.put("sign_type",signType);//设置签名方式 1-MD5

        //设置签名
        String req = SortUtils.getOrderByAsciiAscFromDto(requstDto,false);
        String sign = MD5.md5(URLDecoder.decode(req,"UTF-8") + "&key=" + secretKey);
        if(StringUtil.isEmpty(sign))
        {
            params.put("dmsg","生成签名为空!参数错误!");
            return;
        }
        requstDto.put("sign",sign);//设置签名

        /**
         * 发送请求
         */
        req += "&sign=" + sign;
        logger.info("[快接支付-京东钱包wap(h5)预下单]请求参数=" + req);
        String resp = sendRequest(apiUrl + "/jdpay/wap_pay",req);
        params.put("resp",resp);
        logger.info("[快接支付-京东钱包wap(h5)预下单]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(resp);
        String status = respDto.getAsString("status");//提取状态码
        if("1".equals(status))
        {
            //校验签名
            Dto respData = JsonUtil.jsonToDto(JSONObject.fromObject(respDto.get("data")));
            String respSign = respData.getAsString("sign");//请求响应的sign
            respData.remove("sign");
            String realSign = MD5.md5(URLDecoder.decode(SortUtils.getOrderByAsciiAscFromDto(respData,false),"UTF-8") + "&key=" + secretKey);
            if(realSign.equals(respSign))
            {
                //设置响应结果
                params.put("dcode",1000);//设置预下单状态码
                params.put("dmsg","success");//设置预下单状态码描述
                params.put("tradeNo",respData.getAsString("trade_no"));//设置渠道流水号
                Dto resultsDto = new BaseDto();
                resultsDto.put("payInfo",respData.getAsString("pay_url"));//设置支付链接
                params.put("results",resultsDto);//设置预下单响应结果
            }
            else
            {
                logger.error("[快接支付-京东钱包wap(h5)预下单]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                params.put("dcode",1001);
                params.put("dmsg","响应结果签名不通过");
            }
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg",respDto.get("info"));
        }
    }

    /**
     * 快接支付-订单查询
     * @author  mcdog
     * @param   params  参数对象,订单信息也保存在该对象中(dcode-订单查询状态 dmsg-订单查询状态描述 resp-原始的响应字符串 results-订单交易状态信息)具体如下:
     *                  dcode=1000,查询成功
     *                  dcode=-1000,查询失败
     *                  results:{status:1000,msg:'success'},具体含义如下:
     *                  status=1000,订单交易成功
     *                  status=1001,订单交易中
     *                  status=-1000,订单交易失败/关闭
     */
    public synchronized static void queryOrder(Dto params) throws Exception
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
        //api地址校验
        String apiUrl = params.getAsString("apiUrl");
        if(StringUtil.isEmpty(apiUrl))
        {
            params.put("dmsg","api地址apiUrl不能为空");
            return;
        }
        //签名方式校验
        Integer signType = params.getAsInteger("signType");
        if(StringUtil.isEmpty(signType))
        {
            params.put("dmsg","签名方式signType不能为空");
            return;
        }
        else if(PayUtils.signTypeMaps.get(signType) == null)
        {
            params.put("dmsg","签名方式不合法");
            return;
        }
        //签名密钥校验
        String secretKey = params.getAsString("secretKey");
        if(StringUtil.isEmpty(secretKey))
        {
            params.put("dmsg","签名密钥secretKey不能为空");
            return;
        }
        //渠道流水号
        String cpayId = params.getAsString("cpayId");//获取渠道流水号
        if(StringUtil.isEmpty(cpayId))
        {
            params.put("dmsg","渠道流水号cpayId不能为空");
            return;
        }

        /**
         * 设置请求参数
         */
        Dto requstDto = new BaseDto();
        requstDto.put("merchant_no",merchantNo);//设置商户号
        requstDto.put("trade_no",cpayId);//设置渠道流水号
        requstDto.put("sign_type",signType);//设置签名方式 1-MD5

        //设置签名
        String req = SortUtils.getOrderByAsciiAscFromDto(requstDto,false);
        String sign = "";
        if(PayUtils.signType_md5 == signType)
        {
            sign = MD5.md5(URLDecoder.decode(req,"UTF-8") + "&key=" + secretKey);
        }
        if(StringUtil.isEmpty(sign))
        {
            params.put("dmsg","生成签名为空!参数错误!");
            return;
        }

        /**
         * 发送请求
         */
        req += "&sign=" + sign;
        logger.info("[快接支付-支付宝订单查询]请求参数=" + req);
        String resp = sendRequest(apiUrl + "/alipay/query_pay",req);
        params.put("resp",resp);
        logger.info("[快接支付-支付宝订单查询]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(resp);
        String status = respDto.getAsString("status");//提取状态码
        if("1".equals(status))
        {
            //校验签名
            Dto respData = JsonUtil.jsonToDto(JSONObject.fromObject(respDto.get("data")));
            String respSign = respData.getAsString("sign");//请求响应的sign
            respData.remove("sign");
            String realSign = MD5.md5(URLDecoder.decode(SortUtils.getOrderByAsciiAscFromDto(respData,false),"UTF-8") + "&key=" + secretKey);
            if(realSign.equals(respSign))
            {
                //设置响应结果
                params.put("dcode",1000);//设置查询状态码
                params.put("dmsg","success");//设置查询状态码描述

                /**
                 * 设置订单交易信息
                 */
                Dto resultsDto = new BaseDto();
                resultsDto.put("tradeNo",respData.getAsString("trade_no"));//设置渠道流水号

                //设置订单交易信息
                String tradeState = respData.getAsString("status");//提取订单交易状态
                if("1".equals(tradeState))
                {
                    resultsDto.put("status",1000);//设置交易状态为成功
                    resultsDto.put("msg","success");
                    resultsDto.put("smoney",respData.getAsString("amount"));//设置订单金额
                }
                //未交易/交易处理中/等待交易
                else if(ddclStatusMap.containsKey(tradeState))
                {
                    resultsDto.put("status",1001);//设置交易状态为交易中
                    resultsDto.put("msg",ddclStatusMap.get(tradeState));//设置交易状态描述
                }
                //交易失败
                else if("2".equals(tradeState))
                {
                    resultsDto.put("status",-1000);//设置交易状态为交易失败
                    resultsDto.put("msg","交易失败");//设置交易状态描述
                }
                params.put("results",resultsDto);//设置查询响应结果
            }
            else
            {
                logger.error("[快接支付-支付宝订单查询]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                params.put("dcode",-1000);
                params.put("dmsg","响应结果签名不通过");
            }
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg",respDto.get("info"));
        }
    }

    /**
     * 发送快接支付请求
     * @author  mcdog
     * @return  响应结果字符串
     */
    public static String sendRequest(String url,String reqParams)
    {
        String result = "";
        PrintWriter out = null;
        BufferedReader in = null;
        try
        {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();

            //设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");

            //发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());//获取URLConnection对象对应的输出流
            out.print(reqParams);//发送请求参数
            out.flush();//flush输出流的缓冲

            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));//定义BufferedReader输入流来读取URL的响应
            String line;
            while((line = in.readLine()) != null)
            {
                result += line;
            }
        }
        catch (Exception e)
        {
            logger.error("[发送快接支付请求]发生异常!异常信息:" + e);
        }
        finally
        {
            try
            {
                if(out != null)
                {
                    out.close();
                }
                if(in != null)
                {
                    in.close();
                }
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public static void main(String[] args)
    {
        try
        {
            Dto params = new BaseDto();
            params.put("merchantNo",merchantNo);//设置商户号
            params.put("appNo",appNo);//设置应编号
            params.put("appName",appName);//设置应用名
            params.put("apiUrl",apiUrl);//设置商户号
            params.put("secretKey",secretKey);//设置签名密钥

            params.put("signType",1);//设置签名类型
            params.put("webAddress","http://www.sqgoing.com/");//设置应用web官网

            //设置订单号
            String payId = "CZ" + DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME);//生成商户订单号(平台流水号)
            payId += new Random().nextInt(10);
            params.put("payId",payId);//设置商户订单号
            params.put("smoney",306);//设置订单金额
            params.put("notifyUrl","http://api.sqgoing.com/api/notify/kuaijie");
            params.put("returnUrl","http://mobile.sqgoing.com/html/pay/notify/notify.html");
            params.put("userIp","114.95.156.70");//设置ip
            params.put("clientFrom",1);//客户端来源
            //createWeiXinWapPay(params);
            //System.out.println(params.getAsString("dmsg"));
            //createWeiXinAppPay(params);
            //System.out.println(params.getAsString("dmsg"));
            createAlipayWapPay(params);
            //System.out.println(params.getAsString("dmsg"));
            //createAlipayAppPay(params);
            //System.out.println(params.getAsString("dmsg"));
            //Dto results = (Dto)params.get("results");
            //System.out.println(results.toString());
            //createQqWalletWapPay(params);
            //System.out.println(params.getAsString("dmsg"));
            //createJdWalletWapPay(params);
            //System.out.println(params.getAsString("dmsg"));

            //params.put("cpayId","K201804012317528626283383");
            //queryWeixinOrder(params);
            //queryAlipayOrder(params);
            //queryQqWalletOrder(params);
            System.out.println(params.getAsString("dmsg"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
