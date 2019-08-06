package com.caipiao.common.pay.wlpay;

import com.caipiao.common.constants.PayConstants;
import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.json.JsonUtil;
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
 * 万两支付工具类
 * @author mcdog
 */
public class WlPayUtils
{
    private static final Logger logger = LoggerFactory.getLogger(WlPayUtils.class);
    public static final String BANKCODE_UNIONPAYWAP = "UNIONPAYWAP";
    public static final String merchantNo = "3108";//商户号/商户ID
    public static final String apiUrl = "http://open.6wlpay.com:8080/";//api地址
    public static final String secretKey = "0eb94606862ac2b34680e7a4876af46d";//签名密钥

    /**
     * 万两支付-代付请求
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
         * 参数校验
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
        //商户密钥校验
        String secretKey = params.getAsString("secretKey");
        if(StringUtil.isEmpty(secretKey))
        {
            params.put("dmsg","商户密钥secretKey不能为空");
            return;
        }
        //银行类型校验
        String bankcode = params.getAsString("bankcode");
        if(StringUtil.isEmpty(bankcode))
        {
            params.put("dmsg","银行类型bankcode不能为空");
            return;
        }
        //代付金额校验
        String paymoneyStr = params.getAsString("paymoney");
        Double paymoney = params.getAsDouble("paymoney");
        if(paymoney == null || paymoney.doubleValue() <= 0)
        {
            params.put("dmsg","代付金额paymoney校验不合法,金额不能为空且必须大于0");
            return;
        }
        else if(paymoneyStr.indexOf(".") > 0 && paymoneyStr.substring(paymoneyStr.indexOf(".")).length() > 3)
        {
            params.put("dmsg","订单总金额paymoney不合法,金额最多只能有2位小数");
            return;
        }
        //批次号(商户订单号)校验
        String payId = params.getAsString("payId");
        if(StringUtil.isEmpty(payId))
        {
            params.put("dmsg","批次号(商户订单号)payId不能为空");
            return;
        }
        //收款账号校验
        String bankaccount = params.getAsString("bankaccount");
        if(StringUtil.isEmpty(bankaccount))
        {
            params.put("dmsg","收款人帐号bankaccount不能为空");
            return;
        }
        //收款人校验
        String bankusername = params.getAsString("bankusername");
        if(StringUtil.isEmpty(bankusername))
        {
            params.put("dmsg","收款人bankusername不能为空");
            return;
        }
        //收款银行所在省份校验
        String bankprovince = params.getAsString("bankprovince");
        if(StringUtil.isEmpty(bankprovince))
        {
            params.put("dmsg","收款银行所在省份bankprovince不能为空");
            return;
        }
        //收款银行所在城市校验
        String bankcity = params.getAsString("bankcity");
        if(StringUtil.isEmpty(bankcity))
        {
            params.put("dmsg","收款银行所在城市bankcity不能为空");
            return;
        }
        //收款银行支行校验
        String banksubname = params.getAsString("banksubname");
        if(StringUtil.isEmpty(banksubname))
        {
            params.put("dmsg","收款银行支行banksubname不能为空");
            return;
        }
        //收款银行账户类型校验
        String bankaccounttype = params.getAsString("bankaccounttype");
        if(StringUtil.isEmpty(bankaccounttype))
        {
            params.put("dmsg","收款银行账户类型bankaccounttype不能为空");
            return;
        }

        /**
         * 设置请求参数
         */
        //设置基础参数
        Dto requestDto = new BaseDto();
        requestDto.put("version","3.0");//设置版本号
        requestDto.put("method","wlapp.online.pay");//设置接口名称
        requestDto.put("partner",merchantNo);//设置商户ID
        requestDto.put("bankcode",bankcode);//设置银行代码
        requestDto.put("paymoney",String.format("%.2f",paymoney));//设置代付金额,保留2位小数
        requestDto.put("batchnumber",payId);//设置商户批次号
        requestDto.put("bankaccount",bankaccount);//设置收款账号
        requestDto.put("bankusername",bankusername);//设置收款人
        requestDto.put("bankprovince",bankprovince);//设置收款银行所在省份
        requestDto.put("bankcity",bankcity);//设置收款银行所在城市
        requestDto.put("banksubname",banksubname);//设置收款银行支行
        requestDto.put("bankaccounttype",bankaccounttype);//设置收款银行账户类型
        requestDto.put("subtime",DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME2));//设置请求提交时间
        requestDto.put("remarks",StringUtil.isEmpty(params.get("remarks"))? "付款" : params.getAsString("remarks"));//设置备注

        /**
         * 设置签名
         */
        //拼接签名原始字符串(顺序不能变)
        StringBuilder signBuilder = new StringBuilder();
        signBuilder.append("version=" + requestDto.getAsString("version"));
        signBuilder.append("&method=" + requestDto.getAsString("method"));
        signBuilder.append("&partner=" + requestDto.getAsString("partner"));
        signBuilder.append("&batchnumber=" + requestDto.getAsString("batchnumber"));
        signBuilder.append("&bankcode=" + requestDto.getAsString("bankcode"));
        signBuilder.append("&bankaccount=" + requestDto.getAsString("bankaccount"));
        signBuilder.append("&paymoney=" + requestDto.getAsString("paymoney"));
        signBuilder.append("&subtime=" + requestDto.getAsString("subtime"));
        signBuilder.append("&key=" + secretKey);
        String sign = MD5.md5(new String(signBuilder.toString().getBytes(),"GB2312")).toLowerCase();
        requestDto.put("sign",sign);

        /**
         * 发送请求
         */
        logger.info("[万两支付-代付请求]请求参数=" + requestDto.toString());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/online/gateway",requestDto);
        logger.info("[万两支付-代付请求]响应结果=" + resp);
        params.put("resp",resp);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(resp);
        if(StringUtil.isEmpty(resp) || StringUtil.isEmpty(respDto))
        {
            params.put("dcode",1000);
            params.put("dmsg","代付请求响应结果="  + resp);
        }
        else
        {
            String status = respDto.getAsString("status");//提取付款申请状态码
            if("1".equals(status))
            {
                //校验签名
                String respSign = respDto.getAsString("sign");//请求响应的sign
                signBuilder = new StringBuilder();
                signBuilder.append("version=" + respDto.getAsString("version"));
                signBuilder.append("&partner=" + merchantNo);
                signBuilder.append("&batchnumber=" + respDto.getAsString("batchnumber"));
                signBuilder.append("&paymoney=" + String.format("%.2f",respDto.getAsDoubleValue("paymoney")));
                signBuilder.append("&key=" + secretKey);
                String realSign = MD5.md5(new String(signBuilder.toString().getBytes(),"GB2312")).toLowerCase();
                if(respSign.equals(realSign))
                {
                    params.put("dcode",1000);
                    params.put("dmsg","代付请求发送成功");
                }
                else
                {
                    params.put("dcode",1001);
                    params.put("dmsg","代付请求响应签名验证不通过");
                }
            }
            else
            {
                params.put("dcode",-1000);
                params.put("dmsg",respDto.getAsString("message"));
            }
        }
    }

    /**
     * 万两支付-银联预下单
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
        //充值银行编号校验
        String bankCode = params.getAsString("bankCode");
        if(StringUtil.isEmpty(bankCode))
        {
            params.put("dmsg","充值银行编号bankCode不能为空");
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
        //支付结果通知地址校验
        String notifyUrl = params.getAsString("notifyUrl");
        if(StringUtil.isEmpty(notifyUrl))
        {
            params.put("dmsg","通知地址notifyUrl不能为空");
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
        //判断金额是否最多只有2位小数
        else if(smoney.indexOf(".") > -1 && smoney.substring(smoney.indexOf(".") + 1).length() > 2)
        {
            params.put("dmsg","充值金额smoney最多只能有2位小数");
            return;
        }

        /**
         * 设置请求参数
         */
        Dto requestDto = new BaseDto();
        requestDto.put("version","3.0");//版本号,固定值3.0
        requestDto.put("method","wlapp.online.interface");//接口名称
        requestDto.put("partner",merchantNo);//商户ID
        requestDto.put("banktype",bankCode);//银行类型
        requestDto.put("paymoney",String.format("%.2f",smoney1));//订单金额(单位:元)
        requestDto.put("ordernumber",payId);//商户订单号
        requestDto.put("callbackurl",notifyUrl);//下行异步通知地址
        if(StringUtil.isNotEmpty(params.get("returnUrl")))
        {
            requestDto.put("hrefbackurl",params.getAsString("returnUrl"));//下行同步通知地址
        }
        //设置签名(顺序不能变)
        StringBuilder signBuilder = new StringBuilder();
        signBuilder.append("version=" + requestDto.getAsString("version"));
        signBuilder.append("&method=" + requestDto.getAsString("method"));
        signBuilder.append("&partner=" + requestDto.getAsString("partner"));
        signBuilder.append("&banktype=" + requestDto.getAsString("banktype"));
        signBuilder.append("&paymoney=" + requestDto.getAsString("paymoney"));
        signBuilder.append("&ordernumber=" + requestDto.getAsString("ordernumber"));
        signBuilder.append("&callbackurl=" + requestDto.getAsString("callbackurl"));
        signBuilder.append(secretKey);
        String sign = MD5.md5(new String(signBuilder.toString().getBytes(),"GB2312")).toLowerCase();
        requestDto.put("sign",sign);

        //设置表单提交地址及渠道充值渠道编号
        requestDto.put("action",apiUrl + "/online/gateway");//设置表单提交地址
        requestDto.put("pccode", PayConstants.PAYCHANNEL_CODE_WLPAY);//设置渠道编号

        /**
         * 设置表单参数
         */
        String urlparams = requestDto.toSeparatorString("&");
        logger.info("[万两支付-银联预下单]请求表单参数=" + urlparams);
        params.put("dcode",1000);//设置预下单状态码
        params.put("dmsg","success");//设置预下单状态码描述
        Dto resultsDto = new BaseDto();
        resultsDto.put("payInfo", params.getAsString("mobilehost") + PayConstants.unionpayWapPage + "?" + urlparams);//设置支付链接
        params.put("results",resultsDto);
    }

    /**
     * 万两支付-代付订单查询
     * @author  mcdog
     * @param   params  参数对象,订单信息也保存在该对象中(dcode-订单查询状态 dmsg-订单查询状态描述 resp-原始的响应字符串 results-订单交易状态信息)具体如下:
     *                  dcode=1000,查询成功
     *                  dcode=-1000,查询失败
     *                  results:{status:1000,msg:'success'},具体含义如下:
     *                  status=1000,订单交易成功
     *                  status=1001,订单交易中
     *                  status=-1000,订单交易失败/关闭
     */
    public static void queryDaifuOrder(Dto params) throws Exception
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
        requestDto.put("version","3.0");//版本号,固定值3.0
        requestDto.put("method","wlapp.online.payquery");//接口名称
        requestDto.put("partner",merchantNo);//商户ID
        requestDto.put("batchnumber",payId);//商户订单号

        //设置签名(顺序不能变)
        StringBuilder signBuilder = new StringBuilder();
        signBuilder.append("version=" + requestDto.getAsString("version"));
        signBuilder.append("&method=" + requestDto.getAsString("method"));
        signBuilder.append("&partner=" + requestDto.getAsString("partner"));
        signBuilder.append("&batchnumber=" + requestDto.getAsString("batchnumber"));
        signBuilder.append("&key=" + secretKey);
        String sign = MD5.md5(new String(signBuilder.toString().getBytes(),"GB2312")).toLowerCase();
        requestDto.put("sign",sign);

        /**
         * 发送请求
         */
        logger.info("[万两支付-代付订单查询]请求参数=" + requestDto.toString());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/online/gateway",requestDto);
        params.put("resp",resp);
        logger.info("[万两支付-代付订单查询]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        if(StringUtil.isNotEmpty(resp))
        {
            //校验签名
            Dto respDto = JsonUtil.jsonToDto(resp);
            String respSign = respDto.getAsString("sign");//请求响应的sign
            signBuilder = new StringBuilder();
            signBuilder.append("version=" + respDto.getAsString("version"));
            signBuilder.append("&partner=" + merchantNo);
            signBuilder.append("&batchnumber=" + respDto.getAsString("batchnumber"));
            signBuilder.append("&paymoney=" + respDto.getAsString("paymoney"));
            signBuilder.append("&status=" + respDto.getAsString("status"));
            signBuilder.append("&key=" + secretKey);
            String realSign = MD5.md5(new String(signBuilder.toString().getBytes(),"GB2312")).toLowerCase();
            if(realSign.equals(respSign))
            {
                //设置响应结果
                params.put("dcode",1000);//设置查询状态码
                params.put("dmsg","success");//设置查询状态码描述
                String status = respDto.getAsString("status");//提取订单状态码
                if("1".equals(status))
                {
                    //设置订单交易信息
                    Dto resultsDto = new BaseDto();
                    resultsDto.put("status",1000);//设置交易状态为成功
                    resultsDto.put("msg","success");
                    params.put("results",resultsDto);//设置查询响应结果
                }
                else if("0".equals(status) && "付款失败".equals(respDto.getAsString("message")))
                {
                    Dto resultsDto = new BaseDto();
                    resultsDto.put("status",-1000);//设置交易状态为处理失败
                    resultsDto.put("msg",respDto.getAsString("message"));
                    params.put("results",resultsDto);//设置查询响应结果
                }
                else
                {
                    Dto resultsDto = new BaseDto();
                    resultsDto.put("status",1001);//设置交易状态为处理中
                    resultsDto.put("msg",respDto.getAsString("message"));
                    params.put("results",resultsDto);//设置查询响应结果
                }
            }
            else
            {
                logger.error("[万两支付-代付订单查询]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                params.put("dcode",-1000);
                params.put("dmsg","响应结果签名不通过");
            }
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg","订单查询失败!resp=" + resp);
        }
    }

    /**
     * 万两支付-充值订单查询
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
        requestDto.put("version","3.0");//版本号,固定值3.0
        requestDto.put("method","wlapp.online.query");//接口名称
        requestDto.put("partner",merchantNo);//商户ID
        requestDto.put("ordernumber",payId);//商户订单号
        requestDto.put("sysnumber",params.getAsString("cpayId"));//渠道流水号

        //设置签名(顺序不能变)
        StringBuilder signBuilder = new StringBuilder();
        signBuilder.append("version=" + requestDto.getAsString("version"));
        signBuilder.append("&method=" + requestDto.getAsString("method"));
        signBuilder.append("&partner=" + requestDto.getAsString("partner"));
        signBuilder.append("&ordernumber=" + requestDto.getAsString("ordernumber"));
        signBuilder.append("&sysnumber=" + requestDto.getAsString("sysnumber"));
        signBuilder.append("&key=" + secretKey);
        String sign = MD5.md5(new String(signBuilder.toString().getBytes(),"GB2312")).toLowerCase();
        requestDto.put("sign",sign);

        /**
         * 发送请求
         */
        logger.info("[万两支付-充值订单查询]请求参数=" + requestDto.toString());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/online/gateway",requestDto);
        params.put("resp",resp);
        logger.info("[万两支付-充值订单查询]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        if(StringUtil.isNotEmpty(resp))
        {
            Dto respDto = JsonUtil.jsonToDto(resp);
            String status = respDto.getAsString("status");//提取订单状态码
            if("1".equals(status))
            {
                //校验签名
                String respSign = respDto.getAsString("sign");//请求响应的sign
                signBuilder = new StringBuilder();
                signBuilder.append("version=" + respDto.getAsString("version"));
                signBuilder.append("&partner=" + respDto.getAsString("partner"));
                signBuilder.append("&ordernumber=" + respDto.getAsString("ordernumber"));
                signBuilder.append("&sysnumber=" + respDto.getAsString("sysnumber"));
                signBuilder.append("&status=" + respDto.getAsString("status"));
                signBuilder.append("&tradestate=" + respDto.getAsString("tradestate"));
                signBuilder.append("&paymoney=" + respDto.getAsString("paymoney"));
                signBuilder.append("&banktype=" + respDto.getAsString("banktype"));
                signBuilder.append("&paytime=" + respDto.getAsString("paytime"));
                signBuilder.append("&endtime=" + respDto.getAsString("endtime"));
                signBuilder.append("&key=" + secretKey);
                String realSign = MD5.md5(new String(signBuilder.toString().getBytes(),"GB2312")).toLowerCase();
                if(realSign.equals(respSign))
                {
                    //设置响应结果
                    params.put("dcode",1000);//设置查询状态码
                    params.put("dmsg","success");//设置查询状态码描述

                    //设置订单交易信息
                    Dto resultsDto = new BaseDto();
                    resultsDto.put("tradeNo",respDto.getAsString("sysnumber"));//设置渠道流水号
                    resultsDto.put("smoney",respDto.getAsDoubleValue("paymoney"));//设置订单金额

                    //设置订单交易状态
                    String tradeState = respDto.getAsString("tradestate");//提取订单交易状态
                    if("1".equals(tradeState))
                    {
                        resultsDto.put("status",1000);//设置交易状态为成功
                        resultsDto.put("bankType",respDto.getAsString("banktype"));
                        resultsDto.put("msg","success");
                    }
                    else if("2".equals(tradeState))
                    {
                        resultsDto.put("status",-1000);//设置交易状态为失败
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
                    logger.error("[万两支付-充值订单查询]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                    params.put("dcode",-1000);
                    params.put("dmsg","响应结果签名不通过");
                }
            }
            else
            {
                params.put("dcode",-1000);
                params.put("dmsg","订单查询失败!message=" + respDto.getAsString("message"));
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
            String payId = "TX" + DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME) + new Random().nextInt(10) + new Random().nextInt(10);
            Dto params = new BaseDto();
            params.put("merchantNo",merchantNo);//设置商户ID
            params.put("secretKey",secretKey);
            params.put("apiUrl",apiUrl);
            params.put("bankcode",7);//设置银行代码
            params.put("paymoney",15);//设置代付金额,保留2位小数
            params.put("payId",payId);//设置商户批次号
            params.put("bankaccount","6214850210345952");//设置收款账号
            params.put("bankusername","孙俊奇");//设置收款人
            params.put("bankprovince","上海市");//设置收款银行所在省份
            params.put("bankcity","上海市");//设置收款银行所在城市
            params.put("banksubname","招行银行上海中远两湾城支行");//设置收款银行支行
            params.put("bankaccounttype",2);//设置收款银行账户类型(1-对公 2-对私)
            params.put("remarks","测试代付");//设置备注
            //sendDaiFuRequest(params);
            //System.out.println("resp:" + params.getAsString("resp"));
            params.put("payId","TX201811020731069074");
            queryOrder(params);
            queryDaifuOrder(params);
            System.out.println("resp:" + params.getAsString("resp"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
