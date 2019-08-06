package com.caipiao.common.pay.kuaifu;

import com.alibaba.fastjson.JSONObject;
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
import java.util.Map;
import java.util.Random;

/**
 * 快付支付工具类
 * @author mcdog
 */
public class KuaiFuPayUtils
{
    private static final Logger logger = LoggerFactory.getLogger(KuaiFuPayUtils.class);
    public static final String merchantNo = "";//商户号
    public static final String apiUrl = "http://gateway.fubaohe.com";//api地址
    public static final String merchantKey = "e00b0a65dfd64282adb1eb71f3fdf2ed";//商户key
    public static final String secretKey = "b2afdcd0fb2944228964e3b56cf8c606";//支付密钥


    /**
     * 快付支付-代付请求
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
        //商户key校验
        String merchantKey = params.getAsString("merchantKey");
        if(StringUtil.isEmpty(merchantKey))
        {
            params.put("dmsg","商户key:merchantKey不能为空");
            return;
        }
        //支付密钥校验
        String secretKey = params.getAsString("secretKey");
        if(StringUtil.isEmpty(secretKey))
        {
            params.put("dmsg","支付密钥secretKey不能为空");
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
        //银行编码
        String bankCode = params.getAsString("bankCode");
        if(StringUtil.isEmpty(bankCode))
        {
            params.put("dmsg","银行编码bankCode不能为空");
            return;
        }
        //收款银行所在省份校验
        String bankProvince = params.getAsString("bankProvince");
        if(StringUtil.isEmpty(bankProvince))
        {
            params.put("dmsg","收款银行所在省份bankProvince不能为空");
            return;
        }
        //收款银行所在城市校验
        String bankCity = params.getAsString("bankCity");
        if(StringUtil.isEmpty(bankCity))
        {
            params.put("dmsg","收款银行所在城市bankCity不能为空");
            return;
        }
        //收款银行名称校验
        String bankName = params.getAsString("bankName");
        if(StringUtil.isEmpty(bankName))
        {
            params.put("dmsg","收款银行名称bankName不能为空");
            return;
        }
        //代付金额校验
        String amountstr = params.getAsString("amount");
        Double amount = params.getAsDouble("amount");
        if(amount == null || amount.doubleValue() <= 0)
        {
            params.put("dmsg","代付金额amount不合法,金额不能为空且必须大于0");
            return;
        }
        else if(amountstr.indexOf(".") > 0 && amountstr.substring(amountstr.indexOf(".")).length() > 3)
        {
            params.put("dmsg","代付金额amount不合法,金额最多只能有2位小数");
            return;
        }
        //收款银行预留手机号校验
        String moblieNo = params.getAsString("moblieNo");
        if(StringUtil.isEmpty(moblieNo))
        {
            params.put("dmsg","收款银行预留手机号moblieNo不能为空");
            return;
        }
        //批次号(商户订单号)校验
        String payId = params.getAsString("payId");
        if(StringUtil.isEmpty(payId))
        {
            params.put("dmsg","批次号(商户订单号)payId不能为空");
            return;
        }

        /**
         * 设置请求参数
         */
        //设置基础参数
        Dto requestDto = new BaseDto();
        requestDto.put("merchantKey",merchantKey);//设置商户key
        requestDto.put("realname",bankUserName);//设置持卡人真实姓名
        requestDto.put("cardNo",bankAccount);//设置代付卡卡号
        requestDto.put("bankCode",bankCode);//设置银行编码
        requestDto.put("accType","02");//设置开户账号类型(01-对公 02-对私)
        requestDto.put("province",bankProvince);//设置开户行所在省份
        requestDto.put("city",bankCity);//设置开户行所在城市
        requestDto.put("bankAccountAddress",bankName);//设置开户行全称
        requestDto.put("amount",String.format("%.2f",amount));//设置代付金额
        requestDto.put("moblieNo",moblieNo);//设置银行预留手机号码
        requestDto.put("merchantOrderId",payId);//设置商户订单号

        /**
         * 设置签名
         */
        String sign = SortUtils.getOrderByAsciiAscFromDto(requestDto,false) + "&paySecret=" + secretKey;
        sign = MD5.md5(sign).toUpperCase();
        requestDto.put("sign",sign);

        /**
         * 发送请求
         */
        logger.info("[快付支付-代付请求]请求参数=" + requestDto.toString());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/gateway/remittance/pay",requestDto);
        logger.info("[快付支付-代付请求]响应结果=" + resp);
        params.put("resp",resp);

        /**
         * 处理请求结果
         */
        if(StringUtil.isEmpty(resp))
        {
            params.put("dcode",-1000);
            params.put("dmsg","代付请求响应结果="  + resp);
        }
        else
        {
            JSONObject jsonObject = JSONObject.parseObject(resp);
            String resultCode = jsonObject.getString("resultCode");//提取付款申请状态码
            if("00".equals(resultCode))
            {
                //校验签名
                String respSign = jsonObject.getString("sign");//请求响应的sign
                Dto respDto = new BaseDto();
                for(Map.Entry<String,Object> entry : jsonObject.entrySet())
                {
                    respDto.put(entry.getKey(),entry.getValue().toString());
                }
                respDto.remove("sign");
                String realSign = SortUtils.getOrderByAsciiAscFromDto(respDto,false) + "&paySecret=" + secretKey;
                realSign = MD5.md5(realSign).toUpperCase();
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
                params.put("dmsg",jsonObject.getString("resultMsg"));
            }
        }
    }

    /**
     * 快付支付-代付订单查询
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
        //商户订单号校验
        String payId = params.getAsString("payId");
        if(StringUtil.isEmpty(payId))
        {
            params.put("dmsg","商户订单号payId不能为空");
            return;
        }
        //商户key校验
        String merchantKey = params.getAsString("merchantKey");
        if(StringUtil.isEmpty(merchantKey))
        {
            params.put("dmsg","商户key:merchantKey不能为空");
            return;
        }
        //支付密钥校验
        String secretKey = params.getAsString("secretKey");
        if(StringUtil.isEmpty(secretKey))
        {
            params.put("dmsg","支付密钥secretKey不能为空");
            return;
        }

        /**
         * 设置请求参数
         */
        Dto requestDto = new BaseDto();
        requestDto.put("merchantKey",merchantKey);//商户key
        requestDto.put("timestamp",String.valueOf(System.currentTimeMillis() / 1000L));//unix时间戳
        requestDto.put("merchantOrderId",payId);//商户订单号

        //设置签名(顺序不能变)
        String sign = SortUtils.getOrderByAsciiAscFromDto(requestDto,false) + "&paySecret=" + secretKey;
        sign = MD5.md5(sign).toUpperCase();
        requestDto.put("sign",sign);

        /**
         * 发送请求
         */
        logger.info("[快付支付-代付订单查询]请求参数=" + requestDto.toString());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/gateway/remittance/query",requestDto);
        params.put("resp",resp);
        logger.info("[快付支付-代付订单查询]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        if(StringUtil.isNotEmpty(resp))
        {
            JSONObject jsonObject = JSONObject.parseObject(resp);
            String resultCode = jsonObject.getString("resultCode");//提取返回状态码
            if("00".equals(resultCode))
            {
                Dto respDto = new BaseDto();
                for(Map.Entry<String,Object> entry : jsonObject.entrySet())
                {
                    respDto.put(entry.getKey(),entry.getValue().toString());
                }
                respDto.remove("sign");
                String respSign = jsonObject.getString("sign");//请求响应的sign.
                String realSign = SortUtils.getOrderByAsciiAscFromDto(respDto,false) + "&paySecret=" + secretKey;
                realSign = MD5.md5(realSign).toUpperCase();
                if(respSign.equals(realSign))
                {
                    //设置响应结果
                    params.put("dcode",1000);//设置查询状态码
                    params.put("dmsg","success");//设置查询状态码描述

                    //设置订单交易信息
                    Dto resultsDto = new BaseDto();
                    String status = respDto.getAsString("settStatus");//提取订单状态码
                    if("00".equals(status))
                    {
                        resultsDto.put("status",1000);//设置交易状态为成功
                        resultsDto.put("msg","success");
                    }
                    else if("01".equals(status))
                    {
                        resultsDto.put("status",-1000);//设置交易状态为处理失败
                        resultsDto.put("msg","打款失败");
                    }
                    else
                    {
                        resultsDto.put("status",1001);//设置交易状态为处理中
                        resultsDto.put("msg","打款中");
                    }
                    params.put("results",resultsDto);//设置查询响应结果
                }
                else
                {
                    logger.error("[快付支付-代付订单查询]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                    params.put("dcode",-1000);
                    params.put("dmsg","响应结果签名不通过");
                }
            }
            else
            {
                params.put("dcode",-1000);
                params.put("dmsg",jsonObject.getString("resultMsg"));
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
            Dto requestDto = new BaseDto();
            requestDto.put("merchantKey",merchantKey);//设置商户key
            requestDto.put("bankUserName","孙俊奇");//设置持卡人真实姓名
            requestDto.put("bankAccount","6214850210345952");//设置代付卡卡号
            requestDto.put("bankCode","CMB");//设置银行编码
            requestDto.put("bankProvince","上海");//设置开户行所在省份
            requestDto.put("bankCity","上海");//设置开户行所在城市
            requestDto.put("bankName","招商银行");//设置开户行全称
            requestDto.put("amount","10");//设置代付金额
            requestDto.put("moblieNo","13524944828");//设置银行预留手机号码
            requestDto.put("payId",payId);//设置商户订单号
            requestDto.put("secretKey",secretKey);
            //sendDaiFuRequest(requestDto);
            requestDto.put("payId","TX2019041221510397159");//设置商户订单号
            queryDaifuOrder(requestDto);
            System.out.println(requestDto.getAsString("resp"));
            System.out.println(requestDto.getAsString("results"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
