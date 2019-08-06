package com.caipiao.common.pay.kj412;

import com.caipiao.common.constants.PayConstants;
import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.pay.kj412.util.AesUtils;
import com.caipiao.common.pay.kj412.util.HttpUtil;
import com.caipiao.common.pay.kj412.util.StringUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.HttpClientUtil;
import com.caipiao.common.util.SortUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.Random;

/**
 * kj412-支付工具类
 * @author mcdog
 */
public class Kj412PayUtils
{
    private static final Logger logger = LoggerFactory.getLogger(Kj412PayUtils.class);
    public static final String merchantNo = "201904120000009";//商户号
    public static final String apiUrl = "http://www.cardpower.cn/forward_jtsr/service";//api地址
    public static final String secretKey = "8A2C2B5878ACEAACC8611F8F191C412D";//签名密钥
    public static final String aesKeyPwd = "EFDBF5E74589154F";//aeskey密码

    /**
     * kj412-银联预下单
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
        //商户订单号校验
        String payId = params.getAsString("payId");
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
        //客户端ip校验
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
        requestDto.put("service","hc.createorder");//设置接口路由
        requestDto.put("merchantcode",merchantNo);//设置商户号
        requestDto.put("merchorder_no",payId);//设置商户订单号
        requestDto.put("money",String.format("%.2f",smoney1));//设置金额(必须有且只能有2位小数,单位:元)
        requestDto.put("paytype","3");//设置交易类型(3-快捷)
        requestDto.put("backurl",notifyUrl);//设置异步通知地址
        //requestDto.put("subject","");//设置商品标题
        requestDto.put("returnurl",returnUrl);//设置同步跳转地址
        requestDto.put("sendip",clientIp);//设置用户ip
        requestDto.put("transdate",DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME2));//设置订单发送时间(格式为年月日时分秒)

        /**
         * 设置签名
         */
        //报文加密
        requestDto.put("sign","");
        String sign = StringUtils.signSHA512(requestDto.toJson() + secretKey);
        requestDto.put("sign", sign);
        logger.info("[kj412-银联预下单]请求参数=" + requestDto.toJson());
        String resp = HttpUtil.httpPost(requestDto.toJson(),apiUrl);
        params.put("resp",resp);
        logger.info("[kj412-银联预下单]响应结果=" + resp);
        if(StringUtil.isNotEmpty(resp))
        {
            JSONObject respObject = JSONObject.fromObject(resp);
            if(StringUtil.isNotEmpty(respObject))
            {
                if(StringUtil.isNotEmpty(respObject.getString("transurl")))
                {
                    //验证签名
                    String respSign = respObject.getString("sign");
                    respObject.put("sign","");
                    String realSign = StringUtils.signSHA512(respObject.toString() + secretKey);
                    if(respSign.equals(realSign))
                    {
                        //设置表单提交地址及渠道充值渠道编号
                        requestDto.put("action",apiUrl);//设置表单提交地址
                        requestDto.put("pccode", PayConstants.PAYCHANNEL_CODE_KJ142PAY);//设置渠道编号

                        /**
                         * 设置表单参数
                         */
                        String urlparams = requestDto.toSeparatorString("&");
                        logger.info("[kj412-银联预下单]请求表单参数=" + urlparams);
                        params.put("dcode",1000);//设置预下单状态码
                        params.put("dmsg","success");//设置预下单状态码描述
                        Dto resultsDto = new BaseDto();
                        resultsDto.put("payInfo",respObject.getString("transurl"));//设置支付链接
                        params.put("results",resultsDto);
                    }
                    else
                    {
                        params.put("dmsg","预下单失败,respSign验证不通过!respSign=" + respSign + ",验证签名=" + realSign);
                    }
                }
                else
                {
                    params.put("dmsg","预下单失败,retcode=" + respObject.getString("retcode") + ",result=" + respObject.getString("result"));
                }
            }
            else
            {
                params.put("dmsg","预下单失败,resp转换失败!resp=" + resp);
            }
        }
        else
        {
            params.put("dmsg","预下单失败,resp=" + resp);
        }
    }

    /**
     * kj412-代付请求
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
        //商户订单号校验
        String payId = params.getAsString("payId");
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
        //异步通知地址校验
        String notifyUrl = params.getAsString("notifyUrl");
        if(StringUtil.isEmpty(notifyUrl))
        {
            params.put("dmsg","异步通知地址notifyUrl不能为空");
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
        requestDto.put("service","mb.payment");//设置接口路由
        requestDto.put("merchantcode",merchantNo);//设置商户号
        requestDto.put("merchorder_no",payId);//设置商户订单号
        requestDto.put("amount",String.format("%.2f",smoney1));//设置金额(必须有且只能有2位小数,单位:元)
        requestDto.put("baname", AesUtils.aesEn128(bankAccount,aesKeyPwd));//设置代付账号
        requestDto.put("realname",AesUtils.aesEn128(bankUserName,aesKeyPwd));//设置代付账号持卡人姓名
        requestDto.put("backurl",notifyUrl);//设置异步通知地址
        requestDto.put("transdate",DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME2));//设置订单发送时间(格式为年月日时分秒)

        /**
         * 设置签名
         */
        //报文加密
        requestDto.put("sign","");
        String sign = StringUtils.signSHA512(requestDto.toJson() + secretKey);
        requestDto.put("sign", sign);
        logger.info("[kj412-代付请求]请求参数=" + requestDto.toJson());
        String resp = HttpUtil.httpPost(requestDto.toJson(),apiUrl);
        params.put("resp",resp);
        logger.info("[kj412-代付请求]响应结果=" + resp);
        if(StringUtil.isNotEmpty(resp))
        {
            //验证签名
            JSONObject respObject = JSONObject.fromObject(resp);
            String respSign = respObject.getString("sign");
            respObject.put("sign","");
            String realSign = StringUtils.signSHA512(respObject.toString() + secretKey);
            if(respSign.equals(realSign))
            {
                String retcode = respObject.getString("retcode");//提取付款申请状态码
                if("00".equals(retcode) || "R9".equals(retcode))
                {
                    params.put("dcode",1000);
                    params.put("dmsg","代付请求发送成功");
                }
                else
                {
                    params.put("dcode",-1000);
                    params.put("dmsg","代付请求发送失败!retcode=" + retcode + ",result=" + respObject.getString("result"));
                }
            }
            else
            {
                params.put("dcode",1001);
                params.put("dmsg","代付请求响应签名验证不通过");
            }
        }
        else
        {
            params.put("dmsg","代付请求发送失败!resp=" + resp);
        }
    }

    /**
     * kj412-充值订单查询
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
        requestDto.put("service","hc.query");//接口路由
        requestDto.put("merchantcode",merchantNo);//商户号
        requestDto.put("merchorder_no",payId);//商户订单号
        requestDto.put("transdate",DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME2));//设置订单发送时间(格式为年月日时分秒)

        //设置签名
        requestDto.put("sign","");
        String sign = StringUtils.signSHA512(requestDto.toJson() + secretKey);
        requestDto.put("sign", sign);

        /**
         * 发送请求
         */
        logger.info("[kj412-充值订单查询]请求参数=" + requestDto.toString());
        String resp = HttpUtil.httpPost(requestDto.toJson(),apiUrl);
        params.put("resp",resp);
        logger.info("[kj412-充值订单查询]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        if(StringUtil.isNotEmpty(resp))
        {
            JSONObject respObject = JSONObject.fromObject(resp);
            if(StringUtil.isNotEmpty(respObject))
            {
                //验证签名
                String respSign = respObject.getString("sign");
                respObject.put("sign","");
                String realSign = StringUtils.signSHA512(respObject.toString() + secretKey);
                if(respSign.equals(realSign))
                {
                    //设置响应结果
                    params.put("dcode",1000);//设置查询状态码
                    params.put("dmsg","success");//设置查询状态码描述

                    //设置订单交易信息
                    Dto resultsDto = new BaseDto();
                    resultsDto.put("smoney",respObject.getDouble("money"));//设置订单金额

                    //设置订单交易状态
                    String retcode = respObject.getString("retcode");//提取订单交易状态
                    if("00".equals(retcode))
                    {
                        resultsDto.put("status",1000);//设置交易状态为成功
                        resultsDto.put("msg","success");
                    }
                    else
                    {
                        //resultsDto.put("tradeNo",respObject.getString("payorderno"));//设置渠道流水号
                        resultsDto.put("status",1001);//设置交易状态为交易中
                        resultsDto.put("msg","订单交易状态retcode=" + retcode);//设置交易状态描述
                    }
                    params.put("results",resultsDto);//设置查询响应结果
                }
                else
                {
                    logger.error("[kj412-充值订单查询]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                    params.put("dcode",-1000);
                    params.put("dmsg","响应结果签名不通过");
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

    /**
     * kj412-代付订单查询
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
        requestDto.put("service","mb.paymentquery");//接口路由
        requestDto.put("merchantcode",merchantNo);//商户号
        requestDto.put("merchorder_no",payId);//商户订单号
        requestDto.put("transdate",DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME2));//设置订单发送时间(格式为年月日时分秒)

        //设置签名
        requestDto.put("sign","");
        String sign = StringUtils.signSHA512(requestDto.toJson() + secretKey);
        requestDto.put("sign", sign);

        /**
         * 发送请求
         */
        logger.info("[kj412-代付订单查询]请求参数=" + requestDto.toString());
        String resp = HttpUtil.httpPost(requestDto.toJson(),apiUrl);
        params.put("resp",resp);
        logger.info("[kj412-代付订单查询]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        if(StringUtil.isNotEmpty(resp))
        {
            JSONObject respObject = JSONObject.fromObject(resp);
            if(StringUtil.isNotEmpty(respObject))
            {
                //验证签名
                String respSign = respObject.getString("sign");
                respObject.put("sign","");
                String realSign = StringUtils.signSHA512(respObject.toString() + secretKey);
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
                    if(StringUtil.isNotEmpty(respObject.get("payorderno")))
                    {
                        resultsDto.put("tradeNo",respObject.getString("payorderno"));//设置交易流水号
                    }
                    //设置订单交易状态
                    String retcode = respObject.getString("retcode");//提取订单交易状态
                    if("00".equals(retcode))
                    {
                        resultsDto.put("status",1000);//设置交易状态为成功
                        resultsDto.put("msg","success");
                    }
                    else if("S0".equals(retcode))
                    {
                        resultsDto.put("status",-1000);//设置交易状态为失败
                        resultsDto.put("msg",StringUtil.isNotEmpty(respObject.get("result"))? respObject.getString("result") : "交易失败");
                    }
                    else
                    {

                        resultsDto.put("status",1001);//设置交易状态为交易中
                        resultsDto.put("msg","订单交易状态retcode=" + retcode);//设置交易状态描述
                    }
                    params.put("results",resultsDto);//设置查询响应结果
                }
                else
                {
                    logger.error("[kj412-代付订单查询]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                    params.put("dcode",-1000);
                    params.put("dmsg","响应结果签名不通过");
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
            String payId = "CZ" + DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME);//生成商户订单号
            payId += new Random().nextInt(10);
            requestDto.put("merchantNo",merchantNo);
            requestDto.put("apiUrl",apiUrl);
            requestDto.put("secretKey",secretKey);
            requestDto.put("notifyUrl","http://api.sqgoing.com/api/notify/kj412");
            requestDto.put("returnUrl","http://mobile.sqgoing.com/html/pay/notify/notify.html");
            requestDto.put("payId",payId);
            requestDto.put("smoney",20);
            requestDto.put("clientIp","114.95.156.70");
            //createUnionWapPay(requestDto);
            //queryOrder(requstDto);
            //System.out.println(requestDto.toJson());

            //代付
            payId = "TX" + DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME);//生成商户订单号
            payId += new Random().nextInt(10);
            requestDto.clear();
            requestDto.put("merchantNo",merchantNo);
            requestDto.put("apiUrl",apiUrl);
            requestDto.put("secretKey",secretKey);
            requestDto.put("notifyUrl","http://api.sqgoing.com/api/notify/kj412/df");
            requestDto.put("bankUserName","孙俊奇");
            requestDto.put("bankAccount","6214850210345952");
            requestDto.put("smoney",20);
            requestDto.put("payId",payId);
            //sendDaiFuRequest(requestDto);
            //requestDto.put("payId","TX201904151155573392");
            //queryDfOrder(requestDto);
            //System.out.println(requestDto.toJson());
            //System.out.println(requestDto.toJson());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
