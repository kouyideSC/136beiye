package com.caipiao.common.pay.juhe;

import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.pay.PayUtils;
import com.caipiao.common.pay.kuaijie.KuaiJieUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.HttpClientUtil;
import com.caipiao.common.util.SortUtils;
import com.caipiao.common.util.StringUtil;
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
 * 聚合支付10381-工具类
 * @author sjq
 */
public class JuHe10381Utils
{
    private static final Logger logger = LoggerFactory.getLogger(JuHe10381Utils.class);
    public static final String merchantNo = "10381";//商户号
    public static final String apiUrl = "http://47.104.201.85:2080";//api地址
    public static final String secretKey = "6a9ccc96c0e479b5540bc5839ff46105";//交易密钥/签名密钥
    public static final String dfSecretKey = "dbae75a7c6f8ec07507ba8814ce05640";//代付签名密钥
    public static final String dfpassword = "80a915b8a7638cefe3786062fa5c1f64";//代付密码/提现密码
    public static final String puserid = "10381";//用户ID
    public static final String ckaccount = "SYZFBS0001";//出款账户

    /**
     * 聚合支付10381-付款到银行账户
     * @author  mcdog
     * @param   params  参数对象,付款到银行账户结果也保存在该对象中(dcode-付款申请状态 dmsg-付款申请状态描述 resp-原始的付款响应字符串),具体如下:
     *                  dcode=1000,付款成功
     *                  dcode=-1000,付款失败
     */
    public static void transBatchBank(Dto params) throws Exception
    {
        params.put("dcode",-1000);//设置默认状态码
        params.put("dmsg","付款申请失败");//设置默认状态码描述
        params.put("resp","");

        /**
         * 参数校验
         */
        //非空校验
        if(params == null || params.size() == 0)
        {
            params.put("dmsg","参数不能为空");
            return;
        }
        //收款方银行编号校验
        String bankCode = params.getAsString("bankCode");//获取收款方银行编号
        if(StringUtil.isEmpty(bankCode))
        {
            params.put("dmsg","收款方银行编号bankCode不能为空");
            return;
        }
        //收款方银行卡号校验
        String bankAccount = params.getAsString("bankAccount");//获取收款方银行卡号
        if(StringUtil.isEmpty(bankAccount))
        {
            params.put("dmsg","收款方银行卡号bankAccount不能为空");
            return;
        }
        //收款方银行开户名校验
        String bankUserName = params.getAsString("bankUserName");//获取收款方银行开户名
        if(StringUtil.isEmpty(bankUserName))
        {
            params.put("dmsg","收款方银行开户名bankUserName不能为空");
            return;
        }
        //收款方银行开户所在省份代码校验
        String provinceCode = params.getAsString("provinceCode");//获取收款方银行开户所在省份代码
        if(StringUtil.isEmpty(provinceCode))
        {
            params.put("dmsg","收款方银行开户所在省份代码provinceCode不能为空");
            return;
        }
        //收款方银行开户所在城市代码校验
        String cityCode = params.getAsString("cityCode");//获取收款方银行开户所在城市代码
        if(StringUtil.isEmpty(cityCode))
        {
            params.put("dmsg","收款方银行开户所在城市代码cityCode不能为空");
            return;
        }
        //商户号校验
        String merchantNo = params.getAsString("merchantNo");
        if(StringUtil.isEmpty(merchantNo))
        {
            params.put("dmsg","商户号merchantNo不能为空");
            return;
        }
        //出款账号校验
        String ckaccount = params.getAsString("ckaccount");
        if(StringUtil.isEmpty(ckaccount))
        {
            params.put("dmsg","出款账号ckaccount不能为空");
            return;
        }
        //提现密码/代付密码校验
        String dfpassword = params.getAsString("dfpassword");
        if(StringUtil.isEmpty(dfpassword))
        {
            params.put("dmsg","提现密码dfpassword不能为空");
            return;
        }
        //代付签名秘钥校验
        String dfSecretKey = params.getAsString("secretKey");
        if(StringUtil.isEmpty(dfSecretKey))
        {
            params.put("dmsg","代付签名秘钥secretKey不能为空");
            return;
        }
        //商户订单号校验
        String payId = params.getAsString("payId");//获取商户流水号(订单号)
        if(StringUtil.isEmpty(payId))
        {
            params.put("dmsg","商户订单号payId不能为空");
            return;
        }
        //订单金额校验
        String amountStr = params.getAsString("amount");
        Double amount = params.getAsDouble("amount");//获取订金额
        if(amount == null || amount.doubleValue() <= 0)
        {
            params.put("dmsg","订单总金额amount不合法,金额不能为空且必须大于0");
            return;
        }
        else if(amountStr.indexOf(".") > 0 && amountStr.substring(amountStr.indexOf(".")).length() > 3)
        {
            params.put("dmsg","订单总金额amount不合法,金额最多只能有2位小数");
            return;
        }

        /**
         * 设置请求参数
         */
        //设置基础参数
        Dto requstDto = new BaseDto();
        requstDto.put("memberid",merchantNo);//商户号
        requstDto.put("account",ckaccount);//出款账号
        requstDto.put("bankName",bankCode);//收款方银行编号
        requstDto.put("bankCard",bankAccount);//收款方银行卡号
        requstDto.put("ownerName",bankUserName);//收款方银行开户名
        requstDto.put("provinceCode",provinceCode);//收款方开户行所在省份代码
        requstDto.put("cityCode",cityCode);//收款方开户行所在城市代码
        requstDto.put("amount", (int)(amount * 100));//订单金额
        requstDto.put("password",dfpassword);//提现密码
        requstDto.put("mem_order",payId);//商户订单号

        //设置签名
        String req = SortUtils.getOrderByAsciiAscFromDto(requstDto,false);
        String sign = MD5.md5(req + "&key=" + dfSecretKey).toUpperCase();
        requstDto.put("signature",sign);

        /**
         * 发送请求
         */
        logger.info("[聚合支付10381-付款到银行账户]请求参数=" + requstDto.toString());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/pay_DaiFu",requstDto);
        logger.info("[聚合支付10381-付款到银行账户]响应结果=" + resp);
        params.put("resp",resp);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(resp);
        String code = respDto.getAsString("code");//提取状态码
        if("11".equals(code))
        {
            //校验签名
            String respSign = respDto.getAsString("signature");//请求响应的sign
            respDto.remove("signature");
            String realSign = MD5.md5(SortUtils.getOrderByAsciiAscFromDto(respDto,false) + "&key=" + dfSecretKey).toUpperCase();
            if(realSign.equals(respSign))
            {
                //设置响应结果
                params.put("dcode",1000);//设置预下单状态码
                params.put("dmsg"," 付款请求发送成功");//设置预下单状态码描述
                params.put("tradeNo",respDto.getAsString("order"));//设置渠道流水号
            }
            else
            {
                logger.error("[聚合支付10381-付款到银行账户]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                params.put("dcode",1001);
                params.put("dmsg","响应结果签名不通过");
            }
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg",respDto.get("msg"));
        }
    }

    /**
     * 聚合支付10381-支付宝H5预下单
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
        requstDto.put("pay_memberid",merchantNo);//商户号
        requstDto.put("pay_orderid",payId);//商户订单号
        requstDto.put("pay_amount",smoney1 * 100);//订单金额
        requstDto.put("pay_callbackurl",notifyUrl);//成功支付通知地址
        requstDto.put("pay_turnyurl",returnUrl);//支付成功后跳转的地址
        requstDto.put("pay_userid",puserid);//支付账户用户ID
        requstDto.put("pay_productname",appName);//商品名称
        requstDto.put("pay_tradetype","9005");//通道类型,支付宝H5为9005
        requstDto.put("clientIp",userIp);//用户端IP地址

        //设置签名
        String sign = SortUtils.getOrderByAsciiAscFromDto(requstDto,false);
        sign += "&key=" + secretKey;
        sign = MD5.md5(sign).toUpperCase();
        requstDto.put("signature",sign);

        /**
         * 发送请求
         */
        logger.info("[聚合支付10381-支付宝H5预下单]请求参数=" + requstDto.toString());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/Pay_Index.html",requstDto);
        params.put("resp",resp);
        logger.info("[聚合支付10381-支付宝H5预下单]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(resp);
        String code = respDto.getAsString("code");//提取状态码
        if("11".equals(code))
        {
            //校验签名
            String respSign = respDto.getAsString("signature");//请求响应的sign
            respDto.remove("signature");
            String realSign = MD5.md5(SortUtils.getOrderByAsciiAscFromDto(respDto,false) + "&key=" + secretKey).toUpperCase();
            if(realSign.equals(respSign))
            {
                //设置响应结果
                params.put("dcode",1000);//设置预下单状态码
                params.put("dmsg","success");//设置预下单状态码描述
                params.put("tradeNo",respDto.getAsString("requestNo"));//设置渠道流水号
                Dto resultsDto = new BaseDto();
                resultsDto.put("payInfo",respDto.getAsString("codeUrl"));//设置支付链接
                params.put("results",resultsDto);//设置预下单响应结果
            }
            else
            {
                logger.error("[聚合支付10381-支付宝H5预下单]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                params.put("dcode",1001);
                params.put("dmsg","响应结果签名不通过");
            }
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg",respDto.get("msg"));
        }
    }

    /**
     * 聚合支付-银联快捷预下单
     * @author  mcdog
     * @param   params  参数对象,统一下单(dcode-下单状态 dmsg-下单状态描述 resp-原始的统一下单响应字符串 results-响应结果)结果也保存在该对象中,具体如下:
     *                  dcode=1000,下单成功
     *                  dcode=-1000,下单失败
     *                  dcode=1001,下单失败(签名错误)
     */
    public static void createUnionPayQuickPay(Dto params) throws Exception
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
        //支付账户用户ID
        String puserid = params.getAsString("puserid");
        if(StringUtil.isEmpty(puserid))
        {
            params.put("dmsg","支付账户用户ID-puserid不能为空");
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
        requstDto.put("pay_memberid",merchantNo);//商户号
        requstDto.put("pay_orderid",payId);//商户订单号
        requstDto.put("pay_amount",smoney1 * 100);//订单金额
        requstDto.put("pay_callbackurl",notifyUrl);//成功支付通知地址
        requstDto.put("pay_turnyurl",returnUrl);//支付成功后跳转的地址
        requstDto.put("pay_userid",puserid);//支付账户用户ID
        requstDto.put("pay_productname",appName);//商品名称
        requstDto.put("pay_tradetype","9009");//通道类型,银联快捷为9009
        requstDto.put("clientIp",userIp);//用户端IP地址

        //设置签名
        String sign = SortUtils.getOrderByAsciiAscFromDto(requstDto,false);
        sign += "&key=" + secretKey;
        sign = MD5.md5(sign).toUpperCase();
        requstDto.put("signature",sign);

        /**
         * 发送请求
         */
        logger.info("[聚合支付10381-银联快捷预下单]请求参数=" + requstDto.toString());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/Pay_Index.html",requstDto);
        params.put("resp",resp);
        logger.info("[聚合支付10381-银联快捷预下单]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(resp);
        String code = respDto.getAsString("code");//提取状态码
        if("11".equals(code))
        {
            //校验签名
            String respSign = respDto.getAsString("signature");//请求响应的sign
            respDto.remove("signature");
            String realSign = MD5.md5(SortUtils.getOrderByAsciiAscFromDto(respDto,false) + "&key=" + secretKey).toUpperCase();
            if(realSign.equals(respSign))
            {
                //设置响应结果
                params.put("dcode",1000);//设置预下单状态码
                params.put("dmsg","success");//设置预下单状态码描述
                params.put("tradeNo",respDto.getAsString("requestNo"));//设置渠道流水号
                Dto resultsDto = new BaseDto();
                resultsDto.put("payInfo",respDto.getAsString("codeUrl"));//设置支付链接
                params.put("results",resultsDto);//设置预下单响应结果
            }
            else
            {
                logger.error("[聚合支付10381-银联快捷预下单]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                params.put("dcode",1001);
                params.put("dmsg","响应结果签名不通过");
            }
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg",respDto.get("msg"));
        }
    }

    /**
     * 聚合支付-银联网关预下单
     * @author  mcdog
     * @param   params  参数对象,统一下单(dcode-下单状态 dmsg-下单状态描述 resp-原始的统一下单响应字符串 results-响应结果)结果也保存在该对象中,具体如下:
     *                  dcode=1000,下单成功
     *                  dcode=-1000,下单失败
     *                  dcode=-1000,下单失败(签名错误)
     */
    public static void createUnionPayGatewayPay(Dto params) throws Exception
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
        //支付账户用户ID
        String puserid = params.getAsString("puserid");
        if(StringUtil.isEmpty(puserid))
        {
            params.put("dmsg","支付账户用户ID-puserid不能为空");
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
        requstDto.put("pay_memberid",merchantNo);//商户号
        requstDto.put("pay_orderid",payId);//商户订单号
        requstDto.put("pay_amount",smoney1 * 100);//订单金额
        requstDto.put("pay_callbackurl",notifyUrl);//成功支付通知地址
        requstDto.put("pay_turnyurl",returnUrl);//支付成功后跳转的地址
        requstDto.put("pay_userid",puserid);//支付帐户用户ID
        requstDto.put("pay_productname",appName);//商品名称
        requstDto.put("pay_tradetype","9007");//通道类型,银联网关为9007
        requstDto.put("clientIp",userIp);//用户端IP地址

        //设置签名
        String sign = SortUtils.getOrderByAsciiAscFromDto(requstDto,false);
        sign += "&key=" + secretKey;
        sign = MD5.md5(sign).toUpperCase();
        requstDto.put("signature",sign);

        /**
         * 发送请求
         */
        logger.info("[聚合支付10381-银联网关预下单]请求参数=" + requstDto.toString());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/Pay_Index.html",requstDto);
        params.put("resp",resp);
        logger.info("[聚合支付10381-银联网关预下单]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(resp);
        String code = respDto.getAsString("code");//提取状态码
        if("11".equals(code))
        {
            //校验签名
            String respSign = respDto.getAsString("signature");//请求响应的sign
            respDto.remove("signature");
            String realSign = MD5.md5(SortUtils.getOrderByAsciiAscFromDto(respDto,false) + "&key=" + secretKey).toUpperCase();
            if(realSign.equals(respSign))
            {
                //设置响应结果
                params.put("dcode",1000);//设置预下单状态码
                params.put("dmsg","success");//设置预下单状态码描述
                params.put("tradeNo",respDto.getAsString("requestNo"));//设置渠道流水号
                Dto resultsDto = new BaseDto();
                resultsDto.put("payInfo",respDto.getAsString("codeUrl"));//设置支付链接
                params.put("results",resultsDto);//设置预下单响应结果
            }
            else
            {
                logger.error("[聚合支付10381-银联网关预下单]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                params.put("dcode",1001);
                params.put("dmsg","响应结果签名不通过");
            }
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg",respDto.get("msg"));
        }
    }

    /**
     * 聚合支付-订单查询
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
        //签名密钥校验
        String secretKey = params.getAsString("secretKey");
        if(StringUtil.isEmpty(secretKey))
        {
            params.put("dmsg","签名密钥secretKey不能为空");
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
        requstDto.put("signature",sign);

        /**
         * 发送请求
         */
        logger.info("[聚合支付10381-订单查询]请求参数=" + requstDto.toString());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/Pay_Index_getOrderInfo.html",requstDto);
        params.put("resp",resp);
        logger.info("[聚合支付10381-订单查询]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(resp);
        String code = respDto.getAsString("code");//提取状态码
        if(StringUtil.isEmpty(code) || "11".equals(code))
        {
            //校验签名
            String respSign = respDto.getAsString("signature");//请求响应的sign
            respDto.remove("signature");
            String realSign = MD5.md5(SortUtils.getOrderByAsciiAscFromDto(respDto,"0",false) + "&key=" + secretKey).toUpperCase();
            if(realSign.equals(respSign))
            {
                //设置响应结果
                params.put("dcode",1000);//设置查询状态码
                params.put("dmsg","success");//设置查询状态码描述

                /**
                 * 设置订单交易信息
                 */
                Dto resultsDto = new BaseDto();
                resultsDto.put("tradeNo",respDto.getAsString("pay_orderid"));//设置渠道流水号
                resultsDto.put("smoney",respDto.getAsDoubleValue("pay_amount") / 100);//设置订单金额

                //设置订单交易信息
                String payStatus = respDto.getAsString("pay_status");//提取订单交易状态
                if("1".equals(payStatus))
                {
                    resultsDto.put("status",1000);//设置交易状态为成功
                    resultsDto.put("msg","success");
                }
                else
                {
                    resultsDto.put("status",1001);//设置交易状态为交易中/交易已完成
                    resultsDto.put("msg",respDto.get("failinfo"));//设置交易状态描述
                }
                params.put("results",resultsDto);//设置查询响应结果
            }
            else
            {
                logger.error("[聚合支付10381-订单查询]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                params.put("dcode",-1000);
                params.put("dmsg","响应结果签名不通过");
            }
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg",respDto.get("msg"));
        }
    }

    /**
     * 聚合支付-代付订单查询
     * @author  mcdog
     * @param   params  参数对象,订单信息也保存在该对象中(dcode-订单查询状态 dmsg-订单查询状态描述 resp-原始的响应字符串 results-订单交易状态信息)具体如下:
     *                  dcode=1000,查询成功
     *                  dcode=-1000,查询失败
     *                  results:{status:1000,msg:'success'},具体含义如下:
     *                  status=1000,订单交易成功
     *                  status=1001,订单交易中
     *                  status=-1000,订单交易失败/关闭
     */
    public static void queryDpayOrder(Dto params) throws Exception
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
        //出款账号校验
        String account = params.getAsString("account");//获取出款账户
        if(StringUtil.isEmpty(account))
        {
            params.put("dmsg","出款账号account不能为空");
            return;
        }
        //代付签名密钥校验
        String dfSecretKey = params.getAsString("secretKey");
        if(StringUtil.isEmpty(dfSecretKey))
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
        requstDto.put("memberid",merchantNo);//商户号
        requstDto.put("account",account);//出款账号
        requstDto.put("get_type",200);//固定值
        requstDto.put("mem_order",payId);//商户订单号

        //设置签名
        String sign = SortUtils.getOrderByAsciiAscFromDto(requstDto,false);
        sign += "&key=" + dfSecretKey;
        sign = MD5.md5(sign).toUpperCase();
        requstDto.put("signature",sign);

        /**
         * 发送请求
         */
        logger.info("[聚合支付10381-代付订单查询]请求参数=" + requstDto.toString());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/pay_DaiFu_daiFuSta",requstDto);
        params.put("resp",resp);
        logger.info("[聚合支付10381-代付订单查询]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(resp);
        String code = respDto.getAsString("code");//提取状态码
        if("11".equals(code))
        {
            //校验签名
            String respSign = respDto.getAsString("signature");//请求响应的sign
            respDto.remove("signature");
            String realSign = MD5.md5(SortUtils.getOrderByAsciiAscFromDto(respDto,false) + "&key=" + dfSecretKey);
            if(realSign.equals(respSign))
            {
                //设置响应结果
                params.put("dcode",1000);//设置查询状态码
                params.put("dmsg","success");//设置查询状态码描述

                /**
                 * 设置订单交易信息
                 */
                Dto resultsDto = new BaseDto();
                resultsDto.put("tradeNo",respDto.getAsString("order"));//设置渠道流水号
                resultsDto.put("msg",respDto.get("msg"));//设置交易状态描述

                //设置订单交易信息
                String status = respDto.getAsString("status");//提取订单交易状态
                if("2".equals(status))
                {
                    resultsDto.put("status",1000);//设置交易状态为成功
                    resultsDto.put("msg","success");
                }
                else if("3".equals(status))
                {
                    resultsDto.put("status",-1000);//设置交易状态为交易失败
                }
                else
                {
                    resultsDto.put("status",1001);//设置交易状态为交易中
                }
                params.put("results",resultsDto);//设置订单交易结果
            }
            else
            {
                logger.error("[聚合支付10381-代付订单查询]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                params.put("dcode",-1000);
                params.put("dmsg","响应结果签名不通过");
            }
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg",respDto.get("msg"));
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
            requstDto.put("puserid","10381");
            requstDto.put("appName","充值");
            requstDto.put("apiUrl",apiUrl);
            requstDto.put("secretKey",secretKey);
            requstDto.put("notifyUrl","http://api.szmpyd.com/api/juhe/alipay");
            requstDto.put("returnUrl","http://mobile.szmpyd.com/html/pay/notify/notify.html");
            requstDto.put("payId",payId);
            requstDto.put("smoney",100);
            requstDto.put("userIp","223.104.213.124");
            requstDto.put("bankCode","03080000");
            requstDto.put("provinceCode","31");
            requstDto.put("amount",1);
            requstDto.put("account",ckaccount);
            requstDto.put("bankAccount","6214850210345952");
            requstDto.put("bankUserName","孙俊奇");
            requstDto.put("cityCode","2900");
            requstDto.put("payId","TX" + DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME));

            //createAlipayWapPay(requstDto);
            //createUnionPayQuickPay(requstDto);
            createUnionPayGatewayPay(requstDto);
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
