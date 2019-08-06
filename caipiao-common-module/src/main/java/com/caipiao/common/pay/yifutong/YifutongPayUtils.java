package com.caipiao.common.pay.yifutong;

import com.caipiao.common.constants.PayConstants;
import com.caipiao.common.constants.UserConstants;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 亿富通支付工具类
 * @author sjq
 */
public class YifutongPayUtils
{
    private static final Logger logger = LoggerFactory.getLogger(YifutongPayUtils.class);
    public static final String merchantNo = "70018108296801";//应用号/商户号
    public static final String apiUrl = "http://order.veppay.com";//api地址
    public static final String queryApiUrl = "http://cdjk.veppay.com";
    public static final String secretKey = "047009171028wmnrbcCe";//签名密钥
    public static final String aesSecretKey = "054b0f9216a25975d73e095b733d6518";//AES加密密钥
    public static Map<Integer,Integer> deviceTypeMaps = new HashMap<Integer,Integer>();//终端设备类型映射

    static
    {
        deviceTypeMaps.put(UserConstants.USER_SOURCE_WEB,1);
        deviceTypeMaps.put(UserConstants.USER_SOURCE_IOS,2);
        deviceTypeMaps.put(UserConstants.USER_SOURCE_ANDROID,3);
    }

    /**
     * 亿富通支付-支付宝H5预下单
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
        //客户端ip校验
        String clientIp = params.getAsString("clientIp");
        if(clientIp == null)
        {
            params.put("dmsg","客户端IPclientIp不能为空");
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
        Dto requestDto = new BaseDto();
        requestDto.put("p1_yingyongnum",merchantNo);//应用号
        requestDto.put("p2_ordernumber",payId);//商户订单号
        requestDto.put("p3_money",String.format("%.2f",smoney1));//订单金额(单位:元,保留2位小数)
        requestDto.put("p6_ordertime",DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME2));//订单创建时间(yyyyMMddHHmmss)
        requestDto.put("p7_productcode","ZFBZZWAP");//终端支付方式,固定值
        requestDto.put("p9_signtype",1);//签名方式,1-MD5
        requestDto.put("p14_customname",clientIp);//付款人在商户系统中的帐号
        requestDto.put("p16_customip",clientIp);//付款人ip地址
        requestDto.put("p25_terminal",deviceTypeMaps.get(clientFrom));//终端设置类型(1-pc 2-ios 3-android)

        /**
         * 设置签名
         */
        //拼接签名原始字符串(顺序不能变)
        StringBuilder signBuilder = new StringBuilder();
        signBuilder.append(requestDto.getAsString("p1_yingyongnum"));
        signBuilder.append("&" + requestDto.getAsString("p2_ordernumber"));
        signBuilder.append("&" + requestDto.getAsString("p3_money"));
        signBuilder.append("&" + requestDto.getAsString("p6_ordertime"));
        signBuilder.append("&" + requestDto.getAsString("p7_productcode"));
        signBuilder.append("&" + secretKey);
        String sign = MD5.md5(new String(signBuilder.toString().getBytes(),"UTF-8"));
        requestDto.put("p8_sign",sign);

        //设置表单提交地址及渠道充值渠道编号
        requestDto.put("action",apiUrl + "/jh-web-order/order/receiveOrder");//设置表单提交地址
        requestDto.put("pccode", PayConstants.PAYCHANNEL_CODE_YIFUTONGPAY);//设置渠道编号

        /**
         * 设置表单参数
         */
        String urlparams = requestDto.toSeparatorString("&");
        logger.info("[亿富通支付-支付宝H5预下单]请求表单参数=" + urlparams);
        params.put("dcode",1000);//设置预下单状态码
        params.put("dmsg","success");//设置预下单状态码描述
        Dto resultsDto = new BaseDto();
        resultsDto.put("payInfo", params.getAsString("mobilehost") + PayConstants.alipayH5Page + "?" + urlparams);//设置支付链接
        params.put("results",resultsDto);
    }

    /**
     * 亿富通支付-订单查询
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
        requestDto.put("p1_yingyongnum",merchantNo);//商户号
        requestDto.put("p2_ordernumber",payId);//商户订单号

        //设置签名
        StringBuilder signBuilder = new StringBuilder();
        signBuilder.append(requestDto.getAsString("p1_yingyongnum"));
        signBuilder.append("&" + requestDto.getAsString("p2_ordernumber"));
        signBuilder.append("&" + secretKey);
        String sign = MD5.md5(new String(signBuilder.toString().getBytes(),"UTF-8"));
        requestDto.put("p10_sign",sign);

        /**
         * 发送请求
         */
        logger.info("[亿富通支付-订单查询]请求参数=" + requestDto.toString());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/merchantOrderQuery/query",requestDto);
        params.put("resp",resp);
        logger.info("[亿富通支付-订单查询]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        if(StringUtil.isNotEmpty(resp))
        {
            //设置响应结果
            Dto respDto = JsonUtil.jsonToDto(resp);
            params.put("dcode",1000);//设置查询状态码
            params.put("dmsg","success");//设置查询状态码描述
            String state = respDto.getAsString("p4_zfstate");//提取订单状态码(1-成功 其它均为失败)
            if("1".equals(state))
            {
                //校验签名
                String respSign = respDto.getAsString("p10_sign");//请求响应的sign
                signBuilder = new StringBuilder();
                signBuilder.append(respDto.getAsString("p1_yingyongnum"));
                signBuilder.append("&" + respDto.getAsString("p2_ordernumber"));
                signBuilder.append("&" + respDto.getAsString("p3_money"));
                signBuilder.append("&" + respDto.getAsString("p4_zfstate"));
                signBuilder.append("&" + respDto.getAsString("p5_orderid"));
                signBuilder.append("&" + respDto.getAsString("p6_productcode"));
                signBuilder.append("&" + respDto.getAsString("p7_bank_card_code"));
                signBuilder.append("&" + respDto.getAsString("p8_charset"));
                signBuilder.append("&" + respDto.getAsString("p9_signtype"));
                signBuilder.append("&" + respDto.getAsString("p11_pdesc"));
                signBuilder.append("&" + secretKey);
                String realSign = MD5.md5(signBuilder.toString()).toUpperCase();
                if(realSign.equals(respSign))
                {
                    //设置订单交易信息
                    Dto resultsDto = new BaseDto();
                    resultsDto.put("tradeNo",respDto.getAsString("p5_orderid"));//设置渠道流水号
                    resultsDto.put("smoney",respDto.getAsDoubleValue("p3_money"));//设置订单金额
                    resultsDto.put("status",1000);//设置交易状态为成功
                    resultsDto.put("msg","success");
                    params.put("results",resultsDto);//设置查询响应结果
                }
                else
                {
                    logger.error("[亿富通支付-订单查询]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                    params.put("dcode",-1000);
                    params.put("dmsg","响应结果签名不通过");
                }
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
            requstDto.put("notifyUrl","http://api.szmpyd.com/api/notify/yifutong");
            requstDto.put("returnUrl","http://mobile.szmpyd.com/html/pay/notify/notify.html");
            requstDto.put("payId",payId);
            requstDto.put("smoney",10);
            requstDto.put("clientIp","218.83.114.92");
            requstDto.put("clientFrom",1);
            //createAlipayWapPay(requstDto);
            requstDto.put("payId","CZ201810291613149973");
            requstDto.put("apiUrl",queryApiUrl);
            queryOrder(requstDto);
            System.out.println(requstDto.getAsString("resp"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
