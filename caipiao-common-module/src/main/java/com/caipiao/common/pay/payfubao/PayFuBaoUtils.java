package com.caipiao.common.pay.payfubao;

import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.pay.PayUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.HttpClientUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Random;

/**
 * 贝付宝支付-工具类
 * @author  mcdog
 */
public class PayFuBaoUtils
{
    public static final Logger logger = LoggerFactory.getLogger(PayFuBaoUtils.class);
    public static final String merchantNo = "60100064";//商户号
    public static final String secretKey = "732de6f61d5769b9302a9c86d082bd7c";//签名密钥(签名用)
    public static final String apiUrl = "http://pay.payfubao.com";//api地址
    public static final String notifyUrl = "http://api.szmpyd.com/api/notify/payfubao/weixin";//支付结果异步通知地址
    public static final String returnUrl = "http://mobile.szmpyd.com/html/notify/payfubao/notify.html";//支付结果页面通知地址
    public static final String appNo = "11950";//应用编号/产品编号
    public static final String appName = "广博网络";//支付产品名称
    public static final String webAddress = "http://www.redsun188.com";

    /**
     * 贝付宝-微信H5预下单
     * @author  mcdog
     * @param   params  参数对象,统一下单(dcode-下单状态 dmsg-下单状态描述 resp-原始的统一下单响应字符串 results-响应结果)结果也保存在该对象中,具体如下:
     *                  dcode=1000,下单成功
     *                  dcode=-1000,下单失败
     */
    public static void createWeixinWapPay(Dto params) throws Exception
    {
        params.put("dcode",-1000);//设置默认状态码
        params.put("dmsg","下单失败");//设置默认状态码描述
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
        //应用编号/产品编号
        String appNo = params.getAsString("appNo");
        if(StringUtil.isEmpty(appNo))
        {
            params.put("dmsg","应用编号/产品编号appNo不能为空");
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
            params.put("dmsg","异步通知地址notifyUrl不能为空");
            return;
        }
        //支付结果页面通知地址校验
        String returnUrl = params.getAsString("returnUrl");
        if(StringUtil.isEmpty(notifyUrl))
        {
            params.put("dmsg","页面通知地址returnUrl不能为空");
            return;
        }
        //应用首页URL地址校验
        String webAddress = params.getAsString("webAddress");
        if(StringUtil.isEmpty(webAddress))
        {
            params.put("dmsg","应用首页URL地址webAddress不能为空");
            return;
        }
        //设备信息校验
        String deviceInfo = params.getAsString("deviceInfo");
        if(StringUtil.isEmpty(deviceInfo))
        {
            params.put("dmsg","设备信息deviceInfo不能为空");
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
        //终端ip校验
        String userIp = params.getAsString("userIp");
        if(StringUtil.isEmpty(userIp))
        {
            params.put("dmsg","终端ip-userIp不能为空");
            return;
        }
        //客户端来源校验
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
        requstDto.put("body",appName);//设置商品详情
        requstDto.put("total_fee",Math.round(smoney1 * 100));//设置订单金额(单位:分)
        requstDto.put("version","Pa2.5");//设置版本(固定值)
        requstDto.put("para_id",merchantNo);//设置商户号
        requstDto.put("app_id",appNo);//设置产品编号
        requstDto.put("notify_url",notifyUrl);//设置后台异步通知地址
        requstDto.put("pay_type",0);//设置下单类型(0-微信 1-支付宝)
        requstDto.put("userIdentity", PayUtils.getRandomStr(16));//设置唯一标识/字符串
        requstDto.put("child_para_id",1);//设置字渠道,传固定值1
        requstDto.put("mch_create_ip",userIp);//设置客户端ip
        requstDto.put("device_id",clientFrom == 1? 2 : 1);//设置设备ID(1-安卓 2-IOS)
        requstDto.put("device_info",deviceInfo);//设置设备唯一标识
        requstDto.put("mch_app_id",webAddress);//设置应用首页URl地址
        requstDto.put("mch_app_name",appName);//设置应用标识/网站名
        requstDto.put("attach","xuzhouguangbo");//设置自定义字段,不能超过32位,且不能为中文
        requstDto.put("order_no", payId);//设置商户订单号

        //设置签名(md5(para_id + app_id + order_no + total_fee + key))
        String source = merchantNo + appNo + payId + requstDto.getAsString("total_fee") + secretKey;
        String sign = MD5.md5(source).toLowerCase();
        requstDto.put("sign",sign);//设置签名

        /**
         * 发送请求
         */
        logger.info("[贝付宝-微信H5预下单]请求参数=" + requstDto.toSeparatorString("&"));
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/sdk_transform/Pay_api",requstDto);
        requstDto.put("resp",resp);
        logger.info("[贝付宝-微信H5预下单]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(resp);
        String status = respDto.getAsString("status");
        if("0".equals(status))
        {
            //设置响应结果
            params.put("dcode",1000);//设置预下单状态码
            params.put("dmsg","success");//设置预下单状态码描述
            params.put("tradeNo",respDto.getAsString("out_order_no"));//设置渠道流水号
            Dto resultsDto = new BaseDto();
            resultsDto.put("payInfo",respDto.getAsString("pay_url"));//设置支付链接
            params.put("results",resultsDto);//设置预下单响应结果
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg","预下单失败,status=" + status);
        }
    }

    /**
     * 贝付宝-支付宝H5预下单
     * @author  mcdog
     * @param   params  参数对象,统一下单(dcode-下单状态 dmsg-下单状态描述 resp-原始的统一下单响应字符串 results-响应结果)结果也保存在该对象中,具体如下:
     *                  dcode=1000,下单成功
     *                  dcode=-1000,下单失败
     */
    public static void createAlipayWapPay(Dto params) throws Exception
    {
        params.put("dcode",-1000);//设置默认状态码
        params.put("dmsg","下单失败");//设置默认状态码描述
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
        //应用编号/产品编号
        String appNo = params.getAsString("appNo");
        if(StringUtil.isEmpty(appNo))
        {
            params.put("dmsg","应用编号/产品编号appNo不能为空");
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
            params.put("dmsg","异步通知地址notifyUrl不能为空");
            return;
        }
        //支付结果页面通知地址校验
        String returnUrl = params.getAsString("returnUrl");
        if(StringUtil.isEmpty(notifyUrl))
        {
            params.put("dmsg","页面通知地址returnUrl不能为空");
            return;
        }
        //应用首页URL地址校验
        String webAddress = params.getAsString("webAddress");
        if(StringUtil.isEmpty(webAddress))
        {
            params.put("dmsg","应用首页URL地址webAddress不能为空");
            return;
        }
        //设备信息校验
        String deviceInfo = params.getAsString("deviceInfo");
        if(StringUtil.isEmpty(deviceInfo))
        {
            params.put("dmsg","设备信息deviceInfo不能为空");
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
        //终端ip校验
        String userIp = params.getAsString("userIp");
        if(StringUtil.isEmpty(userIp))
        {
            params.put("dmsg","终端ip-userIp不能为空");
            return;
        }
        //客户端来源校验
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
        requstDto.put("body",appName);//设置商品详情
        requstDto.put("total_fee",Math.round(smoney1 * 100));//设置订单金额(单位:分)
        requstDto.put("version","Pa2.5");//设置版本(固定值)
        requstDto.put("para_id",merchantNo);//设置商户号
        requstDto.put("app_id",appNo);//设置产品编号
        requstDto.put("notify_url",notifyUrl);//设置后台异步通知地址
        requstDto.put("pay_type",1);//设置下单类型(0-微信 1-支付宝)
        requstDto.put("userIdentity", PayUtils.getRandomStr(16));//设置唯一标识/字符串
        requstDto.put("child_para_id",1);//设置字渠道,传固定值1
        requstDto.put("mch_create_ip",userIp);//设置客户端ip
        requstDto.put("device_id",clientFrom == 1? 2 : 1);//设置设备ID(1-安卓 2-IOS)
        requstDto.put("device_info",deviceInfo);//设置设备唯一标识
        requstDto.put("mch_app_id",webAddress);//设置应用首页URl地址
        requstDto.put("mch_app_name",appName);//设置应用标识/网站名
        requstDto.put("attach","xuzhouguangbo");//设置自定义字段,不能超过32位,且不能为中文
        requstDto.put("order_no", payId);//设置商户订单号

        //设置签名(md5(para_id + app_id + order_no + total_fee + key))
        String source = merchantNo + appNo + payId + requstDto.getAsString("total_fee") + secretKey;
        String sign = MD5.md5(source).toLowerCase();
        requstDto.put("sign",sign);//设置签名

        /**
         * 发送请求
         */
        logger.info("[贝付宝-支付宝H5预下单]请求参数=" + requstDto.toSeparatorString("&"));
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/sdk_transform/Pay_api",requstDto);
        requstDto.put("resp",resp);
        logger.info("[贝付宝-支付宝H5预下单]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(resp);
        String status = respDto.getAsString("status");
        if("0".equals(status))
        {
            //设置响应结果
            params.put("dcode",1000);//设置预下单状态码
            params.put("dmsg","success");//设置预下单状态码描述
            params.put("tradeNo",respDto.getAsString("out_order_no"));//设置渠道流水号
            Dto resultsDto = new BaseDto();
            resultsDto.put("payInfo",respDto.getAsString("pay_url"));//设置支付链接
            params.put("results",resultsDto);//设置预下单响应结果
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg","预下单失败,status=" + status);
        }
    }

    /**
     * 贝付宝支付-微信订单查询
     * @author  mcdog
     * @param   params  参数对象,订单信息也保存在该对象中(dcode-订单查询状态 dmsg-订单查询状态描述 resp-原始的响应字符串 results-订单交易状态信息)具体如下:
     *                  dcode=1000,查询成功
     *                  dcode=-1000,查询失败
     *                  results:{status:1000,msg:'success'},具体含义如下:
     *                  status=1000,订单交易成功
     *                  status=1001,订单交易中
     *                  status=-1000,订单交易失败/关闭
     */
    public static void queryWeixinWapOrder(Dto params) throws Exception
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
        //应用编号/产品编号
        String appNo = params.getAsString("appNo");
        if(StringUtil.isEmpty(appNo))
        {
            params.put("dmsg","应用编号/产品编号appNo不能为空");
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
        //设备信息校验
        String deviceInfo = params.getAsString("deviceInfo");
        if(StringUtil.isEmpty(deviceInfo))
        {
            params.put("dmsg","设备信息deviceInfo不能为空");
            return;
        }
        //商户订单号校验
        String payId = params.getAsString("payId");//获取商户订单号
        if(StringUtil.isEmpty(payId))
        {
            params.put("dmsg","商户订单号payId不能为空");
            return;
        }
        //客户端来源校验
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
        requstDto.put("para_id",merchantNo);//设置商户号
        requstDto.put("app_id",appNo);//设置产品编号
        requstDto.put("order_no", payId);//设置商户订单号
        requstDto.put("pay_type",1);//设置下单类型(1-微信 2-支付宝)
        requstDto.put("device_id",clientFrom == 1? 2 : 1);//设置设备ID(1-安卓 2-IOS)

        //设置签名(md5(para_id + app_id + order_no + key))
        String source = merchantNo + appNo + payId + secretKey;
        String sign = MD5.md5(source).toLowerCase();
        requstDto.put("sign",sign);//设置签名

        /**
         * 发送请求
         */
        logger.info("[贝付宝支付-微信订单查询]请求参数=" + requstDto.toSeparatorString("&"));
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/ali_pay/merchantsearch",requstDto);
        requstDto.put("resp",resp);
        logger.info("[贝付宝支付-微信订单查询]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        //响应签名校验
        Dto respDto = JsonUtil.jsonToDto(resp);
        String status = respDto.getAsString("status");//提取订单状态
        if("1".equals(status) || "2".equals(status))
        {
            //设置响应结果
            params.put("dcode",1000);//设置查询状态码
            params.put("dmsg","success");//设置查询状态码描述

            /**
             * 设置订单交易信息
             */
            Dto resultsDto = new BaseDto();
            if("1".equals(status))
            {
                resultsDto.put("tradeNo",respDto.getAsString("ali_trade_no"));//设置渠道流水号
                resultsDto.put("smoney",respDto.getAsDoubleValue("total_fee") / 100);//设置交易金额
                resultsDto.put("status",1000);//设置交易状态为交易成功
                resultsDto.put("msg","订单支付成功");//设置交易状态描述
            }
            else
            {
                resultsDto.put("status",1001);//设置交易状态为交易中
            }
            params.put("results",resultsDto);//设置订单交易结果
        }
        else if("3".equals(status))
        {
            params.put("dcode",-1000);
            params.put("dmsg","签名失败");
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg","订单为空或无此订单类型,status=" + status);
        }
    }

    /**
     * 贝付宝支付-支付宝订单查询
     * @author  mcdog
     * @param   params  参数对象,订单信息也保存在该对象中(dcode-订单查询状态 dmsg-订单查询状态描述 resp-原始的响应字符串 results-订单交易状态信息)具体如下:
     *                  dcode=1000,查询成功
     *                  dcode=-1000,查询失败
     *                  results:{status:1000,msg:'success'},具体含义如下:
     *                  status=1000,订单交易成功
     *                  status=1001,订单交易中
     *                  status=-1000,订单交易失败/关闭
     */
    public static void queryAlipayWapOrder(Dto params) throws Exception
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
        //应用编号/产品编号
        String appNo = params.getAsString("appNo");
        if(StringUtil.isEmpty(appNo))
        {
            params.put("dmsg","应用编号/产品编号appNo不能为空");
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
        //设备信息校验
        String deviceInfo = params.getAsString("deviceInfo");
        if(StringUtil.isEmpty(deviceInfo))
        {
            params.put("dmsg","设备信息deviceInfo不能为空");
            return;
        }
        //商户订单号校验
        String payId = params.getAsString("payId");//获取商户订单号
        if(StringUtil.isEmpty(payId))
        {
            params.put("dmsg","商户订单号payId不能为空");
            return;
        }
        //客户端来源校验
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
        requstDto.put("para_id",merchantNo);//设置商户号
        requstDto.put("app_id",appNo);//设置产品编号
        requstDto.put("order_no", payId);//设置商户订单号
        requstDto.put("pay_type",2);//设置下单类型(1-微信 2-支付宝)
        requstDto.put("device_id",clientFrom == 1? 2 : 1);//设置设备ID(1-安卓 2-IOS)

        //设置签名(md5(para_id + app_id + order_no + key))
        String source = merchantNo + appNo + payId + secretKey;
        String sign = MD5.md5(source).toLowerCase();
        requstDto.put("sign",sign);//设置签名

        /**
         * 发送请求
         */
        logger.info("[贝付宝支付-支付宝订单查询]请求参数=" + requstDto.toSeparatorString("&"));
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/ali_pay/merchantsearch",requstDto);
        requstDto.put("resp",resp);
        logger.info("[贝付宝支付-支付宝订单查询]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        //响应签名校验
        Dto respDto = JsonUtil.jsonToDto(resp);
        String status = respDto.getAsString("status");//提取订单状态
        if("1".equals(status) || "2".equals(status))
        {
            //设置响应结果
            params.put("dcode",1000);//设置查询状态码
            params.put("dmsg","success");//设置查询状态码描述

            /**
             * 设置订单交易信息
             */
            Dto resultsDto = new BaseDto();
            if("1".equals(status))
            {
                resultsDto.put("tradeNo",respDto.getAsString("ali_trade_no"));//设置渠道流水号
                resultsDto.put("smoney",respDto.getAsDoubleValue("total_fee") / 100);//设置交易金额
                resultsDto.put("status",1000);//设置交易状态为成功
                resultsDto.put("msg","订单支付成功");//设置交易状态描述
            }
            else
            {
                resultsDto.put("status",1001);//设置交易状态为交易中
            }
            params.put("results",resultsDto);//设置订单交易结果
        }
        else if("3".equals(status))
        {
            params.put("dcode",-1000);
            params.put("dmsg","签名失败");
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg","订单为空或无此订单类型,status=" + status);
        }
    }

    public static void main(String[] args)
    {
        try
        {
            Dto requstDto = new BaseDto();
            requstDto.put("appName",appName);//设置商品详情
            requstDto.put("smoney",10);//设置订单金额
            requstDto.put("merchantNo",merchantNo);//设置商户号
            requstDto.put("appNo",appNo);//设置产品编号
            requstDto.put("apiUrl",apiUrl);//接口地址
            requstDto.put("notifyUrl",notifyUrl);//设置后台异步通知地址
            requstDto.put("secretKey",secretKey);//设置密钥
            requstDto.put("userIp","218.79.42.175");//设置客户端ip
            requstDto.put("device_id",2);//设置设备ID(1-安卓 2-IOS)
            requstDto.put("deviceInfo","WEB");//设置设备唯一标识
            requstDto.put("webAddress",webAddress);//设置应用首页URl地址
            requstDto.put("clientFrom",1);//设置客户端来源(0-ios 1-安卓)

            //设置商户订单号
            String payId = "CZ" + DateUtil.formatDate(new Date(), DateUtil.LOG_DATE_TIME);//生成商户订单号(平台流水号)
            payId += new Random().nextInt(10);
            requstDto.put("payId", payId);//设置商户订单号
            //createWeixinWapPay(requstDto);
            //createAlipayWapPay(requstDto);
            requstDto.put("payId","CZ201805021019592466");
            queryAlipayWapOrder(requstDto);
            System.out.println(33333);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}