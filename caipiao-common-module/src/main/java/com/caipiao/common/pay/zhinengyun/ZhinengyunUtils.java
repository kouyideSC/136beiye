package com.caipiao.common.pay.zhinengyun;

import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.pay.PayUtils;
import com.caipiao.common.pay.huichao.HuiChaoUtils;
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
import java.net.URLEncoder;
import java.util.Date;
import java.util.Random;

/**
 * 智能云收银工具类
 * @author  mcdog
 */
public class ZhinengyunUtils
{
    private static final Logger logger = LoggerFactory.getLogger(ZhinengyunUtils.class);
    public static final String merchantNo = "306169098268";//商户号
    public static final String apiUrl = "http://zny.39n6.cn";//api地址
    public static final String secretKey = "9f3d8e3d38ace5d5f15bb8dc6ac3c84b";//签名密钥

    /**
     * 智能云收银-支付宝H5下单
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
        //支付结果异步通知地址校验
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
        //设置基础参数
        Dto requestDto = new BaseDto();
        requestDto.put("uid",merchantNo);//设置商户号
        requestDto.put("price",smoney);//设置金额
        requestDto.put("istype","10001");//支付渠道,10001-支付宝,20001-微信支付
        requestDto.put("notify_url",notifyUrl);//异步通知地址
        requestDto.put("return_url",returnUrl);//页面跳转地址
        requestDto.put("format","json");//web跳转我们的支付页,json(默认),获取json页支付信息,可自定义支付页面,return_url参数无效
        requestDto.put("orderid",payId);//商户订单号
        requestDto.put("goodsname",appName);//商品名称

        //设置密钥(拼接顺序:goodsname + istype + notify_url + orderid + orderuid + price + return_url + token + uid)
        String key = requestDto.getAsString("goodsname") + requestDto.getAsString("istype")
                + requestDto.getAsString("notify_url") + requestDto.getAsString("orderid")
                + requestDto.getAsString("price") + requestDto.getAsString("return_url") + secretKey + merchantNo;
        requestDto.put("key",MD5.md5(key).toLowerCase());

        /**
         * 发送请求
         */
        logger.info("[快智能云收银-支付宝H5下单]请求参数=" + requestDto.toString());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/pay/action",requestDto);
        params.put("resp",resp);
        logger.info("[快智能云收银-支付宝H5下单]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(resp);
        int code = respDto.getAsInteger("code");//提取状态码
        if(code > 0)
        {
            //设置响应结果
            params.put("dcode",1000);//设置预下单状态码
            params.put("dmsg","success");//设置预下单状态码描述
            Dto dataDto = (Dto)respDto.get("data");
            params.put("rsmoney",dataDto.get("realprice"));//设置订单实际金额
            Dto resultsDto = new BaseDto();
            resultsDto.put("payInfo",dataDto.get("qrcode"));//设置支付链接
            params.put("results",resultsDto);//设置预下单响应结果
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg",respDto.get("msg"));
        }
    }

    /**
     * 智能云收银-订单查询
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
        params.put("dmsg","订单查询失败");//设置默认状态码描述
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
        String payId = params.getAsString("payId");
        if(StringUtil.isEmpty(payId))
        {
            params.put("dmsg","商户订单号payId不能为空");
            return;
        }
        //订单金额校验
        Double smoney = params.getAsDouble("smoney");
        if(StringUtil.isEmpty(smoney))
        {
            params.put("dmsg","订单金额smoney不能为空");
            return;
        }

        /**
         * 设置请求参数
         */
        //设置基础参数
        Dto requestDto = new BaseDto();
        requestDto.put("uid",merchantNo);//设置商户号
        requestDto.put("price",smoney);//设置金额
        requestDto.put("orderid",payId);//商户订单号

        //设置密钥(拼接顺序:uid + orderid + price + orderuid + token)
        String key = merchantNo + requestDto.getAsString("orderid") + requestDto.getAsString("price") + secretKey;
        requestDto.put("key",MD5.md5(key).toLowerCase());

        /**
         * 发送请求
         */
        logger.info("[智能云收银-订单查询]请求参数=" + requestDto.toString());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/pay/select",requestDto);
        logger.info("[智能云收银-订单查询]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(resp);
        String code = respDto.getAsString("code");//提取状态码
        if("200".equals(code))
        {
            //校验秘钥
            String respSign = respDto.getAsString("key");//请求响应的key
            //key拼接顺序:uid + orderid + price + realprice + orderuid + ordno + status + token
            String realSign = merchantNo + respDto.getAsString("orderid") + respDto.getAsString("price")
                    + respDto.getAsString("realprice") + respDto.getAsString("ordno") + respDto.getAsString("status") + secretKey;
            realSign = MD5.md5(realSign).toLowerCase();
            if(realSign.equals(respSign))
            {
                //设置响应结果
                params.put("dcode",1000);//设置查询状态码
                params.put("dmsg","success");//设置查询状态码描述

                /**
                 * 设置订单交易信息
                 */
                Dto resultsDto = new BaseDto();
                resultsDto.put("tradeNo",respDto.getAsString("ordno"));//设置渠道流水号

                //设置订单交易信息
                String status = respDto.getAsString("status");//提取订单交易状态
                if("2".equals(status))
                {
                    resultsDto.put("status",1000);//设置交易状态为成功
                    resultsDto.put("msg","success");
                    resultsDto.put("smoney",respDto.getAsString("realprice"));//设置订单金额
                }
                //支付成功,回调成功/未支付/订单超时(下单4分钟未支付视为订单超时)
                else if("1".equals(status) || "3".equals(status) || "4".equals(status))
                {
                    resultsDto.put("status",1001);//设置交易状态为交易中
                }
                params.put("results",resultsDto);//设置查询响应结果
            }
            else
            {
                logger.error("[智能云收银-订单查询]响应结果秘钥验证不通过!响应秘钥=" + respSign + ",验证签名=" + realSign);
                params.put("dcode",-1000);
                params.put("dmsg","响应结果秘钥不匹配");
            }
        }
        else if("-7".equals(code))
        {
            params.put("dcode",-1000);
            params.put("dmsg","签名错误");
        }
        else if("-12".equals(code))
        {
            params.put("dcode",-1000);
            params.put("dmsg","参数错误");
        }
        else if("-13".equals(code))
        {
            params.put("dcode",-1000);
            params.put("dmsg","订单号不存在");
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg","订单查询失败!状态码=" + code);
        }
    }

    public static void main(String[] args)
    {
        try
        {
            Dto requestDto = new BaseDto();
            String payId = "CZ" + DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME);//生成商户订单号(平台流水号)
            payId += new Random().nextInt(10);
            requestDto.put("merchantNo",merchantNo);//商户号
            requestDto.put("secretKey",secretKey);//签名密钥
            requestDto.put("apiUrl",apiUrl);//接口地址
            requestDto.put("smoney","100");//金额
            requestDto.put("notifyUrl","http://api.szmpyd.com/api/notify/zhinengyun/alipay");//异步通知地址
            requestDto.put("returnUrl","http://mobile.szmpyd.com/html/pay/notify/notify.html");//页面跳转地址
            requestDto.put("payId",payId);
            requestDto.put("appName","广博网络");
            createAlipayWapPay(requestDto);
            System.out.println(3333);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
