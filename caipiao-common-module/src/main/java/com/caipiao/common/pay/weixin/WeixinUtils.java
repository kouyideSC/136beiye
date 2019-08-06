package com.caipiao.common.pay.weixin;

import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.pay.PayUtils;
import com.caipiao.common.util.*;
import com.caipiao.domain.code.ErrorCode;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.plugin.bjutil.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 微信官方支付-工具类
 * @author  mcdog
 */
public class WeixinUtils
{
    private static final Logger logger = LoggerFactory.getLogger(WeixinUtils.class);
    public static final String merchantNo = "1494383702";//商户号
    public static final String appNo = "wx9146e3d756b37943";//AppID(微信开放平台AppID)
    public static final String appSecret = "e14c4b98aacef3d0bc5a4f1d00ecc16b";//微信开放平台AppSecret)
    public static final String appName = "沐派商城";
    public static final String apiUrl = "https://api.mch.weixin.qq.com";
    public static final String notifyUrl = "http://api.szmpyd.com/api/notify/weixin";
    public static final String secretKey = "dgasdgafgherm34rmhfdgerepryw45ww";//商户API签名密钥
    public static final String success = "SUCCESS";//成功状态码
    public static final String refund = "REFUND";//转入退款状态码
    public static final String notpay = "NOTPAY";//未支付状态码
    public static final String closed = "CLOSED";//已关闭状态码
    public static final String revoked = "REVOKED";//已撤销状态码(刷卡支付)
    public static final String userpaying = "USERPAYING";//支付中状态码
    public static final String payerror = "PAYERROR";//支付失败状态码

    /**
     * 根据参数获取MD5签名
     * @author  mcdog
     * @param   params     参数对象
     */
    public static String getMd5Sign(Dto params) throws Exception
    {
        String sign = null;
        if(params != null && params.size() > 0)
        {
            Dto signParmas = new BaseDto();
            signParmas.putAll(params);
            String secretKey = signParmas.getAsString("secretKey");//提取商户签名密钥
            signParmas.remove("secretKey");
            String sourceStr = SortUtils.getOrderByAsciiAscFromDto(signParmas,false);//获取按参数名ASCII码从小到大排序的字符串
            sourceStr += "&key=" + secretKey;//添加API签名秘钥
            sign = MD5.md5(sourceStr);//生成MD5签名
            sign = sign.toUpperCase();//转换成大写
        }
        return sign;
    }

    /**
     * 微信app预下单(统一下单)
     * @author  mcdog
     * @param   params  参数对象,统一下单结果也保存在该对象中(dcode-下单状态 dmsg-下单状态描述 resp-原始的统一下单响应字符串 results-响应结果),具体如下:
     *                  dcode=1000,下单成功
     *                  dcode=-1000,下单失败
     */
    public synchronized static void createAppPay(Dto params) throws Exception
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
        //应用编号/产品编号校验
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
        //签名方式校验
        Integer signType = params.getAsInteger("signType");
        if(StringUtil.isEmpty(signType))
        {
            params.put("dmsg","签名方式signType不能为空");
            return;
        }
        else if(PayUtils.signTypeMaps.get(signType) == null)
        {
            params.put("dmsg","签名方式不合法");
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
        //设备信息校验
        String deviceInfo = params.getAsString("deviceInfo");
        if(StringUtil.isEmpty(deviceInfo))
        {
            params.put("dmsg","设备信息deviceInfo不能为空");
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
        requstDto.put("appid",appNo);//设置微信应用ID
        requstDto.put("mch_id",merchantNo);//设置微信商户号
        requstDto.put("device_info",deviceInfo);//设置设备号
        requstDto.put("nonce_str",PayUtils.getRandomStr(20));//设置长度为20位的随机字符串(不长于32位)
        requstDto.put("sign_type",PayUtils.signTypeMaps.get(signType));//设置签名方式
        requstDto.put("body",appName);//设置商品交易描述
        requstDto.put("out_trade_no",payId);//设置商户订单号
        requstDto.put("fee_type","CNY");//设置货币类型(CNY-人民币)
        requstDto.put("total_fee",Math.round(smoney1 * 100));//设置订单金额(单位为分)
        requstDto.put("spbill_create_ip",userIp);//设置终端ip

        //设置交易起始时间和交易结束时间
        int valid = StringUtil.isEmpty(params.get("validTime"))? 15 : params.getAsInteger("validTime");//有效期,默认为15分钟
        Calendar calendar = Calendar.getInstance();
        String startTime = DateUtil.formatDate(calendar.getTime(),DateUtil.LOG_DATE_TIME2);//交易起始时间(格式为yyyyMMddHHmmss)
        calendar.add(Calendar.MINUTE,valid);//当前时间+有效期作为交易的结束时间
        String endTime = DateUtil.formatDate(calendar.getTime(),DateUtil.LOG_DATE_TIME2);//交易结束时间(格式为yyyyMMddHHmmss)
        requstDto.put("time_start",startTime);//设置交易起始时间
        requstDto.put("time_expire",endTime);//设置交易结束时间
        requstDto.put("notify_url",notifyUrl);//设置通知地址
        requstDto.put("trade_type",PayUtils.weixinTradeTypeMaps.get(clientFrom));//设置交易类型

        //设置签名
        requstDto.put("secretKey",secretKey);
        String sign = "";
        if(PayUtils.signType_md5 == signType)
        {
            sign = getMd5Sign(requstDto);
        }
        if(StringUtil.isEmpty(sign))
        {
            params.put("dmsg","生成签名为空!参数错误!");
            return;
        }
        requstDto.put("sign",sign);
        requstDto.remove("secretKey");

        /**
         * 发送请求
         */
        String reqXml = requstDto.toXml();//将参数转换为xml形式的字符串
        logger.info("[微信app预下单(统一下单)]请求参数=" + reqXml);
        String resp = HttpClientUtil.callHttpPost_String(apiUrl + "/pay/unifiedorder",reqXml);
        params.put("resp",resp);
        logger.info("[微信app预下单(统一下单)]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        Dto respDto = StringUtil.parseDtoFromXmlStr(resp);
        String returnCode = respDto.getAsString("return_code");//提取通信状态码
        String resultCode = respDto.getAsString("result_code");//提取业务处理状态码
        if(success.equals(returnCode) && success.equals(resultCode))
        {
            //响应签名校验
            String respSign = respDto.getAsString("sign");//请求响应的sign
            respDto.remove("sign");
            if(PayUtils.signType_md5 == signType)
            {
                respDto.put("secretKey",secretKey);
                String realSign = getMd5Sign(respDto);
                respDto.remove("secretKey");
                if(realSign.equals(respSign))
                {
                    //封装响应数据(用于调起支付所要传递的参数)
                    Dto resultsDto = new BaseDto();
                    resultsDto.put("appid",respDto.get("appid"));//设置应用ID
                    resultsDto.put("partnerid",respDto.get("mch_id"));//设置商户号
                    resultsDto.put("prepayid",respDto.get("prepay_id"));//设置预支付交易回话ID
                    resultsDto.put("package","Sign=WXPay");//设置扩展字段,暂填写固定值Sign=WXPay
                    resultsDto.put("noncestr",PayUtils.getRandomStr(20));//设置随机字符串,不长于32位
                    resultsDto.put("timestamp",System.currentTimeMillis() / 1000);//取自1970年1月1日 0点0分0秒以来的秒数
                    resultsDto.put("secretKey",secretKey);
                    String paySign = getMd5Sign(resultsDto);//获取签名
                    resultsDto.put("sign",paySign);//设置签名
                    resultsDto.remove("secretKey");

                    //设置响应结果
                    params.put("dcode",1000);//设置预下单状态码
                    params.put("dmsg","success");//设置预下单状态码描述
                    params.put("results",resultsDto);//设置预下单响应结果

                }
                else
                {
                    logger.error("[微信app预下单(统一下单)]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                    params.put("dcode",1001);
                    params.put("dmsg","响应结果签名不通过");
                }
            }
        }
        else
        {
            if(!success.equals(returnCode))
            {
                params.put("dcode",-1000);
                params.put("dmsg",respDto.getAsString("return_msg"));
            }
            else
            {
                params.put("dcode",-1000);
                params.put("dmsg",respDto.getAsString("err_code_des"));
            }
        }
    }

    /**
     * 微信-订单查询
     * @author  mcdog
     * @param   params  参数对象,订单信息也保存在该对象中(dcode-订单查询状态 dmsg-订单查询状态描述 resp-原始的响应字符串 results-订单交易状态信息)具体如下:
     *                  dcode=1000,查询成功
     *                  dcode=-1000,查询失败
     *                  results:{status:1000,msg:'success'},具体含义如下:
     *                  status=1000,订单交易成功
     *                  status=1001,订单交易中
     *                  status=-1000,订单交易失败/关闭
     */
    public synchronized static void queryOrder(Dto params) throws Exception
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
        //应用编号/产品编号校验
        String appNo = params.getAsString("appNo");
        if(StringUtil.isEmpty(appNo))
        {
            params.put("dmsg","应用编号/产品编号appNo不能为空");
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
        //校验订单号
        String cpayId = params.getAsString("cpayId");//提取渠道流水号
        String payId = params.getAsString("payId");//提取商户订单号
        if(StringUtil.isEmpty(cpayId) && StringUtil.isEmpty(payId))
        {
            params.put("dmsg","订单号不能为空!渠道流水号cpayId或者商户订单号payId至少一个不能为空!");
            return;
        }

        /**
         * 设置请求参数
         */
        Dto requstDto = new BaseDto();
        requstDto.put("mch_id",merchantNo);//设置商户号
        requstDto.put("appid",appNo);//设置应用编号
        requstDto.put("nonce_str", PayUtils.getRandomStr(20));//设置长度为20位的随机字符串(不长于32位)

        //设置订单号
        if(StringUtil.isNotEmpty(cpayId))
        {
            requstDto.put("transaction_id",cpayId);//设置渠道流水号
        }
        else
        {
            requstDto.put("out_trade_no",payId);//设置商户订单号
        }
        //设置签名
        requstDto.put("secretKey",secretKey);
        String sign = getMd5Sign(requstDto);
        requstDto.remove("secretKey");
        if(StringUtil.isEmpty(sign))
        {
            params.put("dmsg","生成签名为空!参数错误!");
            return;
        }
        requstDto.put("sign",sign);

        /**
         * 发送请求
         */
        String reqXml = requstDto.toXml();//将参数转换为xml形式的字符串
        logger.info("[微信-订单查询]请求参数=" + reqXml);
        String resp = HttpClientUtil.callHttpPost_String(apiUrl + "/pay/orderquery",reqXml);
        params.put("resp",resp);
        logger.info("[微信-订单查询]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        Dto respDto = StringUtil.parseDtoFromXmlStr(resp);
        String returnCode = respDto.getAsString("return_code");//提取返回状态码
        String resultCode = respDto.getAsString("result_code");//提取业务处理状态码
        if(success.equals(returnCode))
        {
            //响应签名校验
            String respSign = respDto.getAsString("sign");//请求响应的sign
            respDto.remove("sign");
            respDto.put("secretKey",secretKey);
            String realSign = getMd5Sign(respDto);
            respDto.remove("secretKey");
            if(realSign.equals(respSign))
            {
                //判断,如果业务结果状态为成功
                if(success.equals(resultCode))
                {
                    params.put("dcode",1000);//设置查询状态码
                    params.put("dmsg","success");//设置查询状态码描述

                    /**
                     * 设置订单交易信息
                     */
                    Dto resultsDto = new BaseDto();
                    resultsDto.put("tradeNo",respDto.getAsString("transaction_id"));//设置渠道流水号

                    //判断订单交易状态,如果状态为交易成功
                    String tradeState = respDto.getAsString("trade_state");//提取订单交易状态
                    if(success.equals(tradeState))
                    {
                        resultsDto.put("status",1000);//设置交易状态为成功
                        resultsDto.put("bankType",respDto.getAsString("bank_type"));//设置付款银行
                        resultsDto.put("smoney",respDto.getAsDoubleValue("total_fee") / 100);//设置交易金额
                    }
                    //如果状态为交易失败/关闭/退款
                    else if(closed.equals(tradeState)
                            || payerror.equals(tradeState)
                            || refund.equals(tradeState)
                            || revoked.equals(tradeState))
                    {
                        resultsDto.put("status",-1000);//设置交易状态为失败
                        resultsDto.put("msg",respDto.getAsString("trade_state_desc"));//设置交易状态描述
                    }
                    //如果状态为交易中
                    else if(notpay.equals(tradeState) || userpaying.equals(tradeState))
                    {
                        resultsDto.put("status",1001);//设置交易状态为交易中
                        resultsDto.put("msg",respDto.getAsString("trade_state_desc"));//设置交易状态描述
                    }
                    params.put("results",resultsDto);//设置查询响应结果
                }
                //业务结果状态失败
                else
                {
                    params.put("dcode",-1000);
                    params.put("dmsg",respDto.getAsString("err_code_des"));
                }
            }
            else
            {
                logger.error("[微信-订单查询]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                params.put("dcode",-1000);
                params.put("dmsg","响应结果签名不通过");
            }
        }
        else
        {
            if(!success.equals(returnCode))
            {
                logger.error("[微信-订单查询]");
                params.put("dcode",-1000);
                params.put("dmsg",respDto.getAsString("return_msg"));
            }
        }
    }

    public static void main(String[] args)
    {
        try
        {
            //支付接口测试(如下参数都是必传参数)
            Dto params = new BaseDto();
            params.put("merchantNo",merchantNo);
            params.put("appNo",appNo);
            params.put("appName",appName);
            params.put("secretKey",secretKey);
            params.put("apiUrl",apiUrl);

            //设置订单号
            String payId = "CZ" + DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME);//生成商户订单号(平台流水号)
            payId += new Random().nextInt(10);
            params.put("payId",payId);//商户系统内部的订单号 ,32个字符内、 可包含字母,确保在商户系统唯一

            params.put("smoney","1");//总金额，以分为单位，不允许包含任何字、符号
            params.put("userIp","114.92.14.194");//订单生成的机器 IP
            params.put("notifyUrl",notifyUrl);//接收通知的URL，需给绝对路径，255字符内格式，确保平台能通过互联网访问该地址
            params.put("deviceInfo","iOS_SDK");//应用类型,如果是用于苹果app应用里值为iOS_SDK；如果是用于安卓app应用里值为AND_SDK；如果是用于手机网站，值为iOS_WAP或AND_WAP均可
            //params.put("mch_app_name","红日超市");//应用名称,如果是用于苹果或安卓app应用中，传分别对应在AppStore和安桌分发市场中的应用名（如：王者荣耀）如果是用于手机网站，传对应的网站名(如：京东官网)
            //params.put("mch_app_id","cn.RedSunLottery");//应用标识,如果是用于苹果或安卓app应用中，苹果传IOS 应用唯一标识(如：com.tencent.wzryIOS)安卓传包名(如：com.tencent.tmgp.sgame)如果是用于手机网站，传网站首页URL地址,必须保证公网能正常访问(如：https://m.jd.com)
            params.put("signType",1);
            params.put("payId",DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME));
            params.put("smoney",1);
            params.put("clientFrom",1);
            //createAppPay(params);
            //System.out.println(params.getAsString("dmsg"));
            params.put("payId","CZ201803201410528030");
            queryOrder(params);
            System.out.println(params.getAsString("dmsg"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}