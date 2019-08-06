package com.caipiao.common.pay.ypay;

import com.caipiao.common.constants.PayConstants;
import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.HttpClientUtil;
import com.caipiao.common.util.SortUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;

/**
 * ypay支付工具类
 * @author mcdog
 */
public class YPayUtils
{
    private static final Logger logger = LoggerFactory.getLogger(YPayUtils.class);
    public static final String merchantNo = "190420834";//商户号/商户ID
    public static final String apiUrl = "http://www.ypay.xin";//api地址
    public static final String secretKey = "bylbrcv661hvtz0eusx2a9yfavlg3v32";//签名密钥

    /**
     * ypay-银联预下单
     * @author  mcdog
     * @param   params  参数对象,统一下单(dcode-下单状态 dmsg-下单状态描述 resp-原始的统一下单响应字符串 results-响应结果)结果也保存在该对象中,具体如下:
     *                  dcode=1000,下单成功
     *                  dcode=-1000,下单失败
     *                  dcode=1001,下单失败(签名错误)
     */
    public static void createUnionWapPay(Dto params) throws Exception
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
        //支付结果异步通知地址校验
        String notifyUrl = params.getAsString("notifyUrl");
        if(StringUtil.isEmpty(notifyUrl))
        {
            params.put("dmsg","通知地址notifyUrl不能为空");
            return;
        }
        //支付结果同步通知地址校验
        String returnUrl = params.getAsString("returnUrl");
        if(StringUtil.isEmpty(returnUrl))
        {
            params.put("dmsg","通知地址returnUrl不能为空");
            return;
        }
        //商户订单号校验
        String payId = params.getAsString("payId");
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
        //判断金额是否最多只能有2位小数
        else if(smoney.indexOf(".") > -1 && smoney.substring(smoney.indexOf(".") + 1).length() > 2)
        {
            params.put("dmsg","充值金额smoney最多只能有2位小数");
            return;
        }

        /**
         * 设置请求参数
         */
        Dto requestDto = new BaseDto();
        requestDto.put("pay_memberid",merchantNo);//商户号
        requestDto.put("pay_orderid",payId);//商户订单号,长度为20
        requestDto.put("pay_applydate",DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));
        requestDto.put("pay_bankcode","911");//银行编码,911-快捷支付
        requestDto.put("pay_notifyurl",notifyUrl);//异步通知地址
        requestDto.put("pay_callbackurl",returnUrl);//同步通知地址
        requestDto.put("pay_amount",String.format("%.2f",smoney1));//订单金额

        //设置签名
        String sign = SortUtils.getOrderByAsciiAscFromDto(requestDto,false);
        sign += "&key=" + secretKey;
        sign = MD5.md5(sign).toUpperCase();
        requestDto.put("pay_md5sign",sign);

        requestDto.put("pay_productname",appName);//商品名称

        //设置表单提交地址及渠道充值渠道编号
        requestDto.put("action",apiUrl + "/Pay_Index.html");//设置表单提交地址
        requestDto.put("pccode", PayConstants.PAYCHANNEL_CODE_YPAY);//设置渠道编号

        /**
         * 设置表单参数
         */
        String urlparams = requestDto.toSeparatorString("&");
        logger.info("[ypay-银联预下单]请求表单参数=" + urlparams);
        params.put("dcode",1000);//设置预下单状态码
        params.put("dmsg","success");//设置预下单状态码描述
        Dto resultsDto = new BaseDto();
        resultsDto.put("payInfo", params.getAsString("mobilehost") + PayConstants.unionpayWapPage + "?" + urlparams);//设置支付链接
        params.put("results",resultsDto);
    }

    /**
     * ypay-代付请求
     * @author  mcdog
     * @param   params  参数对象,付款到银行账户结果也保存在该对象中(dcode-付款申请状态 dmsg-付款申请状态描述 resp-原始的付款响应字符串),具体如下:
     *                  dcode=1000,请求成功
     *                  dcode=-1000,请求失败
     *                  dcode=1001,请求失败(签名错误)
     */
    public static void sendDaiFuRequest(Dto params) throws Exception
    {
        params.put("dcode",-1000);//设置默认状态码
        params.put("dmsg","付款申请失败");//设置默认状态码描述
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
        //签名密钥校验
        String secretKey = params.getAsString("secretKey");
        if(StringUtil.isEmpty(secretKey))
        {
            params.put("dmsg","签名密钥secretKey不能为空");
            return;
        }
        //商户订单号校验
        String payId = params.getAsString("payId");
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
        //收款人校验
        String bankUserName = params.getAsString("bankUserName");
        if(StringUtil.isEmpty(bankUserName))
        {
            params.put("dmsg","收款人bankUserName不能为空");
            return;
        }
        //收款账号校验
        String bankAccount = params.getAsString("bankAccount");
        if(StringUtil.isEmpty(bankAccount))
        {
            params.put("dmsg","收款人帐号bankAccount不能为空");
            return;
        }
        //收款银行名称校验
        String bankName = params.getAsString("bankName");
        if(StringUtil.isEmpty(bankName))
        {
            params.put("dmsg","收款银行名称bankName不能为空");
            return;
        }
        //收款银行支行校验
        String branchName = params.getAsString("branchName");
        if(StringUtil.isEmpty(branchName))
        {
            params.put("dmsg","收款银行支行branchName不能为空");
            return;
        }
        //收款银行所在省份校验
        String province = params.getAsString("province");
        if(StringUtil.isEmpty(province))
        {
            params.put("dmsg","收款银行所在省份province不能为空");
            return;
        }
        //收款银行所在城市校验
        String city = params.getAsString("city");
        if(StringUtil.isEmpty(city))
        {
            params.put("dmsg","收款银行所在城市city不能为空");
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
        //判断金额是否最多只能有2位小数
        else if(smoney.indexOf(".") > -1 && smoney.substring(smoney.indexOf(".") + 1).length() > 2)
        {
            params.put("dmsg","充值金额smoney最多只能有2位小数");
            return;
        }

        /**
         * 设置请求参数
         */
        Dto requestDto = new BaseDto();
        requestDto.put("mchid",merchantNo);//设置商户号
        requestDto.put("out_trade_no",payId);//设置商户订单号
        requestDto.put("money",String.format("%.2f",smoney1));//设置金额(单位:元)
        requestDto.put("bankname",bankName);//设置开户行名称
        requestDto.put("subbranch",branchName);//设置银支行名称
        requestDto.put("accountname",bankUserName);//设置开户名
        requestDto.put("cardnumber",bankAccount);//设置银行卡号
        requestDto.put("province",province);//设置省份
        requestDto.put("city",city);//设置城市

        //设置签名
        String sign = SortUtils.getOrderByAsciiAscFromDto(requestDto,false);
        sign += "&key=" + secretKey;
        sign = MD5.md5(sign).toUpperCase();
        requestDto.put("pay_md5sign",sign);

        /**
         * 发送请求
         */
        logger.info("[ypay-代付请求]请求参数=" + requestDto.toJson());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/Payment_Dfpay_add.html",requestDto);
        params.put("resp",resp);
        logger.info("[ypay-代付请求]响应结果=" + resp);
        if(StringUtil.isNotEmpty(resp))
        {
            Dto respDto = JsonUtil.jsonToDto(resp);
            if(respDto != null)
            {
                String status = respDto.getAsString("status");//提取应答码
                if("success".equals(status))
                {
                    params.put("dcode",1000);
                    params.put("tradeNo",respDto.getAsString("transaction_id"));//设置平台流水号
                    params.put("dmsg","代付请求发送成功");
                }
                else
                {
                    params.put("dcode",-1000);
                    params.put("dmsg","代付请求发送失败!status=" + status + ",msg=" + respDto.getAsString("msg"));
                }
            }
            else
            {
                params.put("dmsg","代付请求发送失败!resp转换失败,resp=" + resp);
            }
        }
        else
        {
            params.put("dmsg","代付请求发送失败!resp=" + resp);
        }
    }

    /**
     * ypay-充值订单查询
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
        String payId = params.getAsString("payId");
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
        //签名密钥校验
        String secretKey = params.getAsString("secretKey");
        if(StringUtil.isEmpty(secretKey))
        {
            params.put("dmsg","签名密钥secretKey不能为空");
            return;
        }

        /**
         * 设置请求参数
         */
        Dto requestDto = new BaseDto();
        requestDto.put("pay_memberid",merchantNo);//商户号
        requestDto.put("pay_orderid",payId);//商户订单号

        //设置签名
        String sign = SortUtils.getOrderByAsciiAscFromDto(requestDto,false);
        sign += "&key=" + secretKey;
        sign = MD5.md5(sign).toUpperCase();
        requestDto.put("pay_md5sign",sign);

        /**
         * 发送请求
         */
        logger.info("[ypay-充值订单查询]请求参数=" + requestDto.toString());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/Pay_Trade_query.html",requestDto);
        params.put("resp",resp);
        logger.info("[ypay-充值订单查询]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        if(StringUtil.isNotEmpty(resp))
        {
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

                    //设置订单交易信息
                    Dto resultsDto = new BaseDto();
                    resultsDto.put("tradeNo",respDto.getAsString("transaction_id"));//设置渠道流水号
                    resultsDto.put("smoney",respDto.getAsDoubleValue("amount"));//设置订单金额

                    //设置订单交易状态
                    String tradeState = respDto.getAsString("trade_state");//提取订单交易状态
                    if("SUCCESS".equals(tradeState))
                    {
                        resultsDto.put("status",1000);//设置交易状态为成功
                        resultsDto.put("msg","success");
                    }
                    else
                    {
                        resultsDto.put("status",1001);//设置交易状态为交易中
                        resultsDto.put("msg","订单交易状态tradeState=" + tradeState);//设置交易状态描述
                    }
                    params.put("results",resultsDto);//设置查询响应结果
                }
                else
                {
                    logger.error("[ypay-充值订单查询]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                    params.put("dcode",-1000);
                    params.put("dmsg","响应结果签名不通过");
                }
            }
            else
            {
                params.put("dcode",-1000);
                params.put("dmsg","订单查询失败!returncode=" + respDto.getAsString("returncode"));
            }
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg","订单查询失败!resp=" + resp);
        }
    }

    /**
     * ypay-代付订单查询
     * @author  mcdog
     * @param   params  参数对象,订单信息也保存在该对象中(dcode-订单查询状态 dmsg-订单查询状态描述 resp-原始的响应字符串 results-订单交易状态信息)具体如下:
     *                  dcode=1000,查询成功
     *                  dcode=-1000,查询失败
     *                  results:{status:1000,msg:'success'},具体含义如下:
     *                  status=1000,订单交易成功
     *                  status=1001,订单交易中
     *                  status=-1000,订单交易失败/关闭
     */
    public static void queryDfOrder(Dto params) throws Exception
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
        //商户订单号校验
        String payId = params.getAsString("payId");
        if(StringUtil.isEmpty(payId))
        {
            params.put("dmsg","商户订单号payId不能为空");
            return;
        }

        /**
         * 设置请求参数
         */
        Dto requestDto = new BaseDto();
        requestDto.put("out_trade_no",payId);//商户订单号
        requestDto.put("mchid",merchantNo);//商户号

        //设置签名
        String sign = SortUtils.getOrderByAsciiAscFromDto(requestDto,false);
        sign += "&key=" + secretKey;
        sign = MD5.md5(sign).toUpperCase();
        requestDto.put("pay_md5sign",sign);

        /**
         * 发送请求
         */
        logger.info("[ypay-代付订单查询]请求参数=" + requestDto.toJson());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/Payment_Dfpay_query.html",requestDto);
        params.put("resp",resp);
        logger.info("[ypay-代付订单查询]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        if(StringUtil.isNotEmpty(resp))
        {
            JSONObject respObject = JSONObject.fromObject(resp);
            if(respObject != null)
            {
                String status = respObject.getString("status");//提取响应码
                if("success".equals(status))
                {
                    //验证签名
                    String respSign = respObject.getString("sign");//提取响应签名
                    respObject.remove("sign");
                    Object[] keys = respObject.keySet().toArray();
                    Arrays.sort(keys);
                    StringBuilder originStr = new StringBuilder();
                    for(Object key : keys)
                    {
                        if(StringUtil.isNotEmpty(respObject.get(key)))
                        {
                            originStr.append(key).append("=").append(respObject.get(key)).append("&");
                        }
                    }
                    originStr.append("key=" + secretKey);
                    String realSign = MD5.md5(originStr.toString()).toUpperCase();
                    if(respSign.equals(realSign))
                    {
                        //设置响应结果
                        params.put("dcode",1000);//设置查询状态码
                        params.put("dmsg","success");//设置查询状态码描述

                        /**
                         * 设置订单交易信息
                         */
                        Dto resultsDto = new BaseDto();
                        resultsDto.put("smoney",respObject.getDouble("amount"));//设置订单金额
                        resultsDto.put("tradeNo",respObject.getString("transaction_id"));//设置交易流水号

                        //设置订单交易状态
                        String refCode = respObject.getString("refCode");//提取订单交易状态
                        if("1".equals(refCode))
                        {
                            resultsDto.put("status",1000);//设置交易状态为成功
                            resultsDto.put("msg","success");
                        }
                        else if("2".equals(refCode))
                        {
                            resultsDto.put("status",-1000);//设置交易状态为失败
                            resultsDto.put("msg",StringUtil.isNotEmpty(respObject.get("refMsg"))? respObject.getString("refMsg") : "代付失败");
                        }
                        else
                        {
                            resultsDto.put("status",1001);//设置交易状态为交易中
                            resultsDto.put("msg","订单交易状态refCode=" + refCode);//设置交易状态描述
                        }
                        params.put("results",resultsDto);//设置查询响应结果
                    }
                    else
                    {
                        logger.error("[ypay-代付订单查询]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                        params.put("dcode",-1000);
                        params.put("dmsg","响应结果签名不通过");
                    }
                }
                else
                {
                    params.put("dcode",-1000);
                    params.put("dmsg","查询失败!status=" + status + ",msg=" + respObject.get("msg") + ",refCode=" + respObject.get("refCode") + ",refMsg=" + respObject.get("refMsg"));
                }
            }
            else
            {
                params.put("dmsg","订单查询失败,resp转换失败!resp=" + resp);
            }
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg","订单查询失败!resp=" + resp);
        }
    }

    public static void main(String[] args)
    {
        try
        {
            Dto requestDto = new BaseDto();
            String payId = "CZ" + DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME);//生成商户订单号(平台流水号)
            payId += new Random().nextInt(10);
            requestDto.put("merchantNo",merchantNo);
            requestDto.put("apiUrl",apiUrl);
            requestDto.put("secretKey",secretKey);
            requestDto.put("notifyUrl","http://api.sqgoing.com/api/notify/ypay");
            requestDto.put("returnUrl","http://mobile.sqgoing.com/html/pay/notify/notify.html");
            requestDto.put("payId",payId);
            requestDto.put("smoney",10.58);
            requestDto.put("appName","思岂网络");
            //createUnionWapPay(requestDto);
            requestDto.clear();
            payId = "TX" + DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME);//生成商户订单号
            payId += new Random().nextInt(10);
            requestDto.put("merchantNo",merchantNo);//设置商户编号
            requestDto.put("secretKey",secretKey);
            requestDto.put("apiUrl",apiUrl);//设置api地址
            requestDto.put("bankUserName","孙俊奇");//设置收款人
            requestDto.put("bankAccount","6214850210345952");//设置收款账户
            requestDto.put("bankName","招商银行");//设置收款银行简称
            requestDto.put("branchName","招商银行中远两湾城支行");//设置支行
            requestDto.put("province","上海");//设置开户行所在省份
            requestDto.put("city","上海");//设置开户行所在城市
            requestDto.put("smoney","100");//设置付款金额
            requestDto.put("payId",payId);//设置商户订单号
            //sendDaiFuRequest(requestDto);
            requestDto.put("payId","TX201904211332159933");
            queryDfOrder(requestDto);
            System.out.println("resp:" + requestDto.getAsString("resp"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
