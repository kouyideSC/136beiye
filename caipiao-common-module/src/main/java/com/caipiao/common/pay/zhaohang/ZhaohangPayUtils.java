package com.caipiao.common.pay.zhaohang;

import com.caipiao.common.constants.PayConstants;
import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.pay.huichao.utils.HTTPClientUtils;
import com.caipiao.common.pay.huichao.utils.RsaUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.HttpClientUtil;
import com.caipiao.common.util.SortUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 兆行支付工具类
 * @author sjq
 */
public class ZhaohangPayUtils
{
    private static final Logger logger = LoggerFactory.getLogger(ZhaohangPayUtils.class);
    public static final String merchantNo = "1540194060OZlpLG";//商户号
    public static final String apiUrl = "http://www.lanjunshop.com/";//api地址
    public static final String secretKey = "c9bfe4a92191bab6fcf6cafa0eb47d14598aff0c";//签名密钥

    /**
     * 兆行支付-支付宝H5预下单
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
        String payId = params.getAsString("payId");//获取商户订单号
        if(StringUtil.isEmpty(payId))
        {
            params.put("dmsg","商户订单号payId不能为空");
            return;
        }
        else if(payId.length() != 20)
        {
            params.put("dmsg","商户订单号payId长度只能为20");
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
        requestDto.put("mchNo",merchantNo);//商户号
        requestDto.put("type","1");//支付类型,1-支付宝
        requestDto.put("account","");//手机设备号,可为空
        requestDto.put("price",String.format("%.2f",smoney1));//订单金额(单位:元,小数点后2位)
        requestDto.put("orderCode",payId);//商户订单号
        requestDto.put("ts",System.currentTimeMillis());//时间戳,单位为毫秒
        requestDto.put("notifyUrl",notifyUrl);//支付结果异步通知地址
        requestDto.put("succPage",StringUtil.isEmpty(params.get("returnUrl"))? "" : params.getAsString("returnUrl"));//支付成功跳转地址

        //设置签名
        String sign = SortUtils.getOrderByAsciiAscFromDto(requestDto,false) + "&token=" + secretKey;
        sign = MD5.md5(sign).toLowerCase();
        requestDto.put("sign",sign);

        //设置版本号
        requestDto.put("version",2);//设置版本号(不参与签名)

        /**
         * 发送请求
         */
        logger.info("[兆行支付-支付宝H5预下单]请求参数=" + requestDto.toString());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/api/getQrcode",requestDto);
        logger.info("[兆行支付-支付宝H5预下单]响应结果=" + resp);
        params.put("resp",resp);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(resp);
        if("0".equals(respDto.getAsString("code")) && respDto.getAsBoolean("success") == true)
        {
            //校验签名
            Dto respDataDto = (Dto) respDto.get("data");
            String respSign = respDataDto.getAsString("sign");//请求响应的sign
            respDataDto.remove("sign");
            respDataDto.put("price",String.format("%.2f",respDataDto.getAsDoubleValue("price")));
            respDataDto.put("realPrice",String.format("%.2f",respDataDto.getAsDoubleValue("realPrice")));
            String realSign = SortUtils.getOrderByAsciiAscFromDto(respDataDto,false) + "&token=" + secretKey;
            realSign = MD5.md5(realSign).toLowerCase();
            if(realSign.equals(respSign))
            {
                //设置响应结果
                params.put("dcode",1000);//设置预下单状态码
                params.put("dmsg","success");//设置预下单状态码描述
                params.put("rsmoney",respDataDto.getAsDoubleValue("realPrice"));//设置实际支付金额
                Dto resultsDto = new BaseDto("payInfo",respDataDto.getAsString("payUrl"));//设置支付链接地址
                params.put("results",resultsDto);//设置预下单响应结果
            }
            else
            {
                logger.error("[兆行支付-支付宝H5预下单]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
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
     * 兆行支付-充值订单查询
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
        requestDto.put("mchNo",merchantNo);//商户号
        requestDto.put("orderCode",payId);//商户订单号
        requestDto.put("ts",System.currentTimeMillis());//时间戳,单位为毫秒

        //设置签名
        String sign = SortUtils.getOrderByAsciiAscFromDto(requestDto,false) + "&token=" + secretKey;
        sign = MD5.md5(sign).toLowerCase();
        requestDto.put("sign",sign);

        /**
         * 发送请求
         */
        logger.info("[兆行支付-充值订单查询]请求参数=" + requestDto.toString());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/api/queryOrder",requestDto);
        params.put("resp",resp);
        logger.info("[兆行支付-充值订单查询]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        if(StringUtil.isNotEmpty(resp))
        {
            Dto respDto = JsonUtil.jsonToDto(resp);
            if("0".equals(respDto.getAsString("code")) && respDto.getAsBoolean("success") == true)
            {
                //校验签名
                Dto respDataDto = (Dto) respDto.get("data");
                String respSign = respDataDto.getAsString("sign");//请求响应的sign
                respDataDto.remove("sign");
                respDataDto.put("extraCost",String.format("%.2f",respDataDto.getAsDoubleValue("extraCost")));
                respDataDto.put("price",String.format("%.2f",respDataDto.getAsDoubleValue("price")));
                respDataDto.put("realPrice",String.format("%.2f",respDataDto.getAsDoubleValue("realPrice")));
                String realSign = SortUtils.getOrderByAsciiAscFromDto(respDataDto,false) + "&token=" + secretKey;
                realSign = MD5.md5(realSign).toLowerCase();
                if(realSign.equals(respSign))
                {
                    //设置响应结果
                    params.put("dcode",1000);//设置查询状态码
                    params.put("dmsg","success");//设置查询状态码描述

                    //设置订单交易信息
                    Dto resultsDto = new BaseDto();
                    resultsDto.put("tradeNo",respDataDto.getAsString("tradeNo"));//设置渠道流水号(支付宝交易订单号)
                    resultsDto.put("smoney",respDataDto.getAsDoubleValue("realPrice"));//设置订单金额(取实际支付金额)

                    //设置订单交易状态
                    String status = respDataDto.getAsString("status");//提取订单交易状态
                    if("2".equals(status) || "3".equals(status))
                    {
                        resultsDto.put("status",1000);//设置交易状态为成功
                        resultsDto.put("msg","success");
                    }
                    else if("0".equals(status))
                    {
                        resultsDto.put("status",-1000);//设置交易状态为交易失败
                        resultsDto.put("msg","订单交易状态status=" + status);//设置交易状态描述
                    }
                    else
                    {
                        resultsDto.put("status",1001);//设置交易状态为交易中
                        resultsDto.put("msg","订单交易状态status=" + status);//设置交易状态描述
                    }
                    params.put("results",resultsDto);//设置查询响应结果
                }
                else
                {
                    logger.error("[兆行支付-充值订单查询]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                    params.put("dcode",-1000);
                    params.put("dmsg","响应结果签名不通过");
                }
            }
            else
            {
                params.put("dcode",-1000);
                params.put("dmsg","订单查询失败!msg=" + respDto.getAsString("msg"));
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
            Dto requstDto = new BaseDto();
            String payId = "CZ" + DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME);//生成商户订单号(平台流水号)
            payId += new Random().nextInt(10);
            requstDto.put("merchantNo",merchantNo);
            requstDto.put("apiUrl",apiUrl);
            requstDto.put("secretKey",secretKey);
            requstDto.put("notifyUrl","http://api.szmpyd.com/api/notify/zhaohang");
            requstDto.put("returnUrl","http://mobile.szmpyd.com/html/pay/notify/notify.html");
            requstDto.put("payId",payId);
            requstDto.put("smoney",23);
            //createAlipayWapPay(requstDto);
            //requstDto.put("payId","CZ201810261443540808");
            //queryOrder(requstDto);
            //System.out.println(requstDto.getAsString("resp"));
            //requstDto.put("payId","TX201807060951048034");
            //requstDto.put("beginTime","2018-07-06 09:50:00");
            //requstDto.put("endTime","2018-07-06 09:55:00");
            //queryDfOrder(requstDto);
            System.out.println(requstDto.getAsString("resp"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
