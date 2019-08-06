package com.caipiao.common.pay.xunyoutong;

import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.pay.PayUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.HttpClientUtil;
import com.caipiao.common.util.SortUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.util.Date;
import java.util.Random;

/**
 * 迅游通-工具类
 * @author  mcdog
 */
public class XunYouTongUtils
{
    public static final Logger logger = LoggerFactory.getLogger(XunYouTongUtils.class);
    public static final String merchantNo = "325a626095754f7c9dcf459fdeb8e9a9";//商户支付key(商户号)
    public static final String secretKey = "64b277be2bd745b3b0790638dae6daeb";//签名密钥(签名用)
    public static final String apiUrl = "https://gateway.ioo8.com";
    public static final String notifyUrl = "http://api.szmpyd.com/api/notify/xunyoutong/qqwallet";//支付结果异步通知地址
    public static final String returnUrl = "http://mobile.szmpyd.com/html/pay/notify/notify.html";//支付结果页面通知地址
    public static final String appName = "广博网络";//支付产品名称

    /**
     * 迅游通-银联预下单
     * @author  mcdog
     * @param   params  参数对象,统一下单(dcode-下单状态 dmsg-下单状态描述 resp-原始的统一下单响应字符串 results-响应结果)结果也保存在该对象中,具体如下:
     *                  dcode=1000,下单成功
     *                  dcode=-1000,下单失败
     */
    public static void createUnionpayOrder(Dto params) throws Exception
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
        //银行编号校验
        String bankCode = params.getAsString("bankCode");
        if(StringUtil.isEmpty(bankCode))
        {
            params.put("dmsg","银行编号bankCode不能为空");
            return;
        }
        //终端ip校验
        String userIp = params.getAsString("userIp");
        if(StringUtil.isEmpty(userIp))
        {
            params.put("dmsg","终端ip-userIp不能为空");
            return;
        }

        /**
         * 设置请求参数
         */
        Dto requstDto = new BaseDto();
        requstDto.put("payKey", merchantNo);//设置商户支付key(商户号)
        requstDto.put("outTradeNo",payId);//设置商户订单号
        requstDto.put("orderPrice",smoney);//设置订单金额
        requstDto.put("productType","50000103");//设置产品类型
        requstDto.put("orderTime",DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME2));//设置下单时间
        requstDto.put("productName",appName);//设置支付产品名称
        requstDto.put("orderIp",userIp);//设置产生订单的机器ip
        requstDto.put("bankCode",bankCode);//设置银行编码
        requstDto.put("bankAccountType","PRIVATE_DEBIT_ACCOUNT");//支付银行卡类型
        requstDto.put("returnUrl",returnUrl);//页面通知地址
        requstDto.put("notifyUrl",notifyUrl);//设置后台异步通知地址

        //设置签名
        String source = SortUtils.getOrderByAsciiAscFromDto(requstDto,false) + "&paySecret=" + secretKey;
        String sign = MD5.md5(source).toUpperCase();
        requstDto.put("sign",sign);//设置签名

        /**
         * 发送请求
         */
        logger.info("[迅游通-银联预下单]请求参数=" + requstDto.toString());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/b2cPay/initPay",requstDto);
        params.put("resp",resp);
        logger.info("[迅游通-银联预下单]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        if(resp.indexOf("html") > -1)
        {
            params.put("dmsg",resp);
        }
        else
        {
            params.put("dcode",1000);
            params.put("payInfo",resp);
        }
    }

    /**
     * 迅游通-QQ钱包预下单
     * @author  mcdog
     * @param   params  参数对象,统一下单(dcode-下单状态 dmsg-下单状态描述 resp-原始的统一下单响应字符串 results-响应结果)结果也保存在该对象中,具体如下:
     *                  dcode=1000,下单成功
     *                  dcode=-1000,下单失败
     */
    public static void createQqWalletWapPay(Dto params) throws Exception
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

        /**
         * 设置请求参数
         */
        Dto requstDto = new BaseDto();
        requstDto.put("payKey", merchantNo);//设置商户支付key(商户号)
        requstDto.put("outTradeNo",payId);//设置商户订单号
        requstDto.put("orderPrice",smoney);//设置订单金额
        requstDto.put("productType","70000203");//设置产品类型(微信WAP/H5(T0))
        requstDto.put("orderTime",DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME2));//设置下单时间
        requstDto.put("productName",appName);//设置支付产品名称
        requstDto.put("orderIp",userIp);//设置产生订单的机器ip
        requstDto.put("returnUrl",returnUrl);//页面通知地址
        requstDto.put("notifyUrl",notifyUrl);//设置后台异步通知地址
        requstDto.put("remark",appName);

        //设置签名
        String source = SortUtils.getOrderByAsciiAscFromDto(requstDto,false) + "&paySecret=" + secretKey;
        String sign = MD5.md5(source).toUpperCase();
        requstDto.put("sign",sign);//设置签名

        /**
         * 发送请求
         */
        logger.info("[迅游通-QQ钱包预下单]请求参数=" + requstDto.toString());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/scanPay/initPay",requstDto);
        params.put("resp",resp);
        logger.info("[迅游通-QQ钱包预下单]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(resp);
        String status = respDto.getAsString("resultCode");//提取状态码
        if("0000".equals(status))
        {
            //校验签名
            String respSign = respDto.getAsString("sign");//请求响应的sign
            respDto.remove("sign");
            String realSign = SortUtils.getOrderByAsciiAscFromDto(respDto,false) + "&paySecret=" + secretKey;
            realSign = MD5.md5(realSign).toUpperCase();
            if(realSign.equals(respSign))
            {
                //设置响应结果
                params.put("dcode",1000);//设置预下单状态码
                params.put("dmsg","success");//设置预下单状态码描述
                Dto resultsDto = new BaseDto();
                resultsDto.put("payInfo",respDto.get("payMessage"));//设置支付链接
                params.put("results",resultsDto);//设置预下单响应结果
            }
            else
            {
                logger.error("[迅游通-QQ钱包预下单]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                params.put("dcode",1001);
                params.put("dmsg","响应结果签名不通过");
            }
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg",respDto.get("errMsg"));
        }
    }

    /**
     * 迅游通-京东钱包预下单
     * @author  mcdog
     * @param   params  参数对象,统一下单(dcode-下单状态 dmsg-下单状态描述 resp-原始的统一下单响应字符串 results-响应结果)结果也保存在该对象中,具体如下:
     *                  dcode=1000,下单成功
     *                  dcode=-1000,下单失败
     */
    public static void createJdWalletOrder(Dto params) throws Exception
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

        /**
         * 设置请求参数
         */
        Dto requstDto = new BaseDto();
        requstDto.put("payKey", merchantNo);//设置商户支付key(商户号)
        requstDto.put("outTradeNo",payId);//设置商户订单号
        requstDto.put("orderPrice",smoney);//设置订单金额
        requstDto.put("productType","80000203");//设置产品类型(京东WAP支付(T0))
        requstDto.put("orderTime",DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME2));//设置下单时间
        requstDto.put("productName",appName);//设置支付产品名称
        requstDto.put("orderIp",userIp);//设置产生订单的机器ip
        requstDto.put("returnUrl",returnUrl);//页面通知地址
        requstDto.put("notifyUrl",notifyUrl);//设置后台异步通知地址
        requstDto.put("remark",appName);

        //设置签名
        String source = SortUtils.getOrderByAsciiAscFromDto(requstDto,false) + "&paySecret=" + secretKey;
        String sign = MD5.md5(source).toUpperCase();
        requstDto.put("sign",sign);//设置签名

        /**
         * 发送请求
         */
        logger.info("[迅游通-京东钱包预下单]请求参数=" + requstDto.toString());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/scanPay/initPay",requstDto);
        params.put("resp",resp);
        logger.info("[迅游通-京东钱包预下单]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(resp);
        String status = respDto.getAsString("resultCode");//提取状态码
        if("0000".equals(status))
        {
            //校验签名
            String respSign = respDto.getAsString("sign");//请求响应的sign
            respDto.remove("sign");
            String realSign = SortUtils.getOrderByAsciiAscFromDto(respDto,false) + "&paySecret=" + secretKey;
            realSign = MD5.md5(realSign).toUpperCase();
            if(realSign.equals(respSign))
            {
                //设置响应结果
                params.put("dcode",1000);//设置预下单状态码
                params.put("dmsg","success");//设置预下单状态码描述
                Dto resultsDto = new BaseDto();
                resultsDto.put("payInfo",respDto.get("payMessage"));//设置支付链接
                params.put("results",resultsDto);//设置预下单响应结果
            }
            else
            {
                logger.error("[迅游通-京东钱包预下单]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                params.put("dcode",1001);
                params.put("dmsg","响应结果签名不通过");
            }
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg",respDto.get("errMsg"));
        }
    }

    /**
     * 迅游通-订单查询
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
        //商户订单号校验
        String payId = params.getAsString("payId");//获取商户订单号
        if(StringUtil.isEmpty(payId))
        {
            params.put("dmsg","商户订单号payId不能为空");
            return;
        }

        /**
         * 设置请求参数
         */
        Dto requstDto = new BaseDto();
        requstDto.put("payKey", merchantNo);//设置商户支付key(商户号)
        requstDto.put("outTradeNo",payId);//设置商户订单号

        //设置签名
        String source = SortUtils.getOrderByAsciiAscFromDto(requstDto,false) + "&paySecret=" + secretKey;
        String sign = MD5.md5(source).toUpperCase();
        requstDto.put("sign",sign);//设置签名

        /**
         * 发送请求
         */
        logger.info("[迅游通-订单查询]请求参数=" + requstDto.toString());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/query/singleOrder",requstDto);
        params.put("resp",resp);
        logger.info("[迅游通-订单查询]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(resp);
        String resultCode = respDto.getAsString("resultCode");//提取状态码
        if("0000".equals(resultCode))
        {
            //校验签名
            String respSign = respDto.getAsString("sign");//请求响应的sign
            respDto.remove("sign");
            String realSign = SortUtils.getOrderByAsciiAscFromDto(respDto,false) + "&paySecret=" + secretKey;
            realSign = MD5.md5(realSign).toUpperCase();
            if(realSign.equals(respSign))
            {
                //设置响应结果
                params.put("dcode",1000);//设置查询状态码
                params.put("dmsg","success");//设置查询状态码描述

                /**
                 * 设置订单交易信息
                 */
                Dto resultsDto = new BaseDto();
                String orderStatus = respDto.getAsString("orderStatus");
                if("SUCCESS".equals(orderStatus))
                {
                    resultsDto.put("tradeNo",respDto.getAsString("trxNo"));//设置渠道流水号
                    resultsDto.put("smoney",respDto.getAsDoubleValue("orderPrice"));//设置交易金额
                    resultsDto.put("status",1000);//设置交易状态
                    resultsDto.put("msg","success");//设置交易状态描述
                }
                else if("FAILED".equals(orderStatus))
                {
                    resultsDto.put("status",-1000);//设置交易状态失败
                }
                else
                {
                    resultsDto.put("status",1001);//设置交易状态为交易中
                }
                params.put("results",resultsDto);//设置订单交易结果
            }
            else
            {
                logger.error("[迅游通-订单查询]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                params.put("dcode",1001);
                params.put("dmsg","响应结果签名不通过");
            }
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg",respDto.get("errMsg"));
        }
    }

    public static void main(String[] args)
    {
        try
        {
            Dto requstDto = new BaseDto();
            requstDto.put("merchantNo", merchantNo);//设置商户支付key(商户号)
            requstDto.put("smoney",2.58);//设置订单金额
            requstDto.put("appName",appName);//设置支付产品名称
            requstDto.put("apiUrl",apiUrl);//接口地址
            requstDto.put("secretKey",secretKey);//签名密钥
            requstDto.put("notifyUrl",notifyUrl);//设置后台异步通知地址
            requstDto.put("userIp","218.79.44.202");//设置产生订单的机器ip
            requstDto.put("returnUrl",returnUrl);//页面通知地址
            requstDto.put("notifyUrl",notifyUrl);//设置后台异步通知地址
            requstDto.put("remark","测试");

            //设置商户订单号
            String payId = "CZ" + DateUtil.formatDate(new Date(), DateUtil.LOG_DATE_TIME);//生成商户订单号(平台流水号)
            payId += new Random().nextInt(10);
            requstDto.put("payId", payId);//设置商户订单号

            String source = SortUtils.getOrderByAsciiAscFromDto(requstDto,false) + "&paySecret=" + secretKey;
            String sign = MD5.md5(source).toUpperCase();
            requstDto.put("sign",sign);//设置签名
            //createUnionpayOrder(requstDto);//银联下单
            //createQqWalletWapPay(requstDto);//QQ钱包下单
            createJdWalletOrder(requstDto);//京东钱包下单
            System.out.println(333);
            Dto results = (Dto)requstDto.get("results");
            System.out.println(results.getAsString("payInfo"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
