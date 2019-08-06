package com.caipiao.common.pay.swiftpass;

import com.caipiao.common.constants.KeyConstants;
import com.caipiao.common.constants.UserConstants;
import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.pay.PayUtils;
import com.caipiao.common.pay.weixin.WeixinUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.HttpClientUtil;
import com.caipiao.common.util.SortUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 威富通支付-工具类
 * @author  mcdog
 */
public class SwiftpassUtils
{
    public static final Logger logger = LoggerFactory.getLogger(SwiftpassUtils.class);

    public static final String merchantNo = "150500112823";//商户号
    public static final String secretKey = "3e799b172e77c674f43d895a5b1b878e";//商户密钥
    public static final String appName = "红日超市";//应用名
    public static final String deviceInfo = "AND_WAP";//微信终端设备号(门店号或收银设备ID),默认为WEB
    public static final String apiUrl = "https://pay.swiftpass.cn/pay/gateway";//api地址
    public static final String response_success = "0";//成功状态代码

    /**
     * 根据参数获取威富通MD5签名
     * @author  mcdog
     * @param   params     参数对象
     */
    public static String getMd5Sign(Dto params)
    {
        String sign = null;
        if(params != null && params.size() > 0)
        {
            Dto signParmas = new BaseDto();
            signParmas.putAll(params);
            String secretKey = signParmas.getAsString("secretKey");//提取商户签名密钥
            signParmas.remove("secretKey");
            String sourceStr = SortUtils.getOrderByAsciiAscFromDto(signParmas,false);//获取按参数名ASCII码从小到大排序的字符串
            sourceStr += "&key=" + secretKey;//添加API秘钥
            sign = MD5.md5(sourceStr);//生成MD5签名
            sign = sign.toUpperCase();//转换成大写
        }
        return sign;
    }

    /**
     * 威富通-微信wap(h5)预下单
     * @author  mcdog
     * @param   params  参数对象,统一下单(dcode-下单状态 dmsg-下单状态描述 resp-原始的统一下单响应字符串 results-响应结果)结果也保存在该对象中,具体如下:
     *                  dcode=1000,下单成功
     *                  dcode=-1000,下单失败
     *                  dcode=1001,下单失败(签名错误)
     */
    public static void createWeiXinWapPay(Dto params) throws Exception
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
        requstDto.put("service","pay.weixin.wappay");//设置接口类型
        requstDto.put("sign_type",PayUtils.signTypeMaps.get(signType));//设置签名方式
        requstDto.put("mch_id",merchantNo);//设置商户号
        requstDto.put("out_trade_no",payId);//设置商户订单号(平台支付流水号)
        requstDto.put("body",appName);//设置商品交易描述
        requstDto.put("total_fee",Math.round(smoney1 * 100));//设置订单金额(单位为分)
        requstDto.put("mch_create_ip",userIp);//设置终端IP
        requstDto.put("notify_url",notifyUrl);//设置通知地址
        requstDto.put("device_info",deviceInfo);//设置设备号
        requstDto.put("mch_app_name",appName);//设置应用名
        requstDto.put("nonce_str",PayUtils.getRandomStr(20));//设置长度为20位的随机字符串(不长于32位)

        //设置应用标识
        if(UserConstants.USER_SOURCE_IOS == clientFrom)
        {
            requstDto.put("mch_app_id",PayUtils.iosAppFlag);
        }
        else if(UserConstants.USER_SOURCE_ANDROID == clientFrom)
        {
            requstDto.put("mch_app_id",PayUtils.androidAppPackage);
        }

        //设置交易起始时间和交易结束时间
        int valid = StringUtil.isEmpty(params.get("validTime"))? 15 : params.getAsInteger("validTime");//有效期,默认为15分钟
        Calendar calendar = Calendar.getInstance();
        String startTime = DateUtil.formatDate(calendar.getTime(),DateUtil.LOG_DATE_TIME2);//交易起始时间(格式为yyyyMMddHHmmss)
        calendar.add(Calendar.MINUTE,valid);//当前时间+有效期作为交易的结束时间
        String endTime = DateUtil.formatDate(calendar.getTime(),DateUtil.LOG_DATE_TIME2);//交易结束时间(格式为yyyyMMddHHmmss)
        requstDto.put("time_start",startTime);//设置交易起始时间
        requstDto.put("time_expire",endTime);//设置交易结束时间

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
        logger.info("[威富通-微信wap(h5)预下单]请求参数=" + reqXml);
        String resp = HttpClientUtil.callHttpPost_String(apiUrl,reqXml);
        params.put("resp",resp);
        logger.info("[威富通-微信wap(h5)预下单]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        Dto respDto = StringUtil.parseDtoFromXmlStr(resp);
        String status = respDto.getAsString("status");//提取通信状态码
        String resultCode = respDto.getAsString("result_code");//提取业务处理状态码
        if(response_success.equals(status) && response_success.equals(resultCode))
        {
            //响应签名校验
            String respSign = respDto.getAsString("sign");//请求响应的sign
            respDto.remove("sign");
            if(PayUtils.signType_md5 == signType)
            {
                respDto.put("secretKey",params.get("secretKey"));
                String realSign = getMd5Sign(respDto);
                respDto.remove("secretKey");
                if(realSign.equals(respSign))
                {
                    //设置响应结果
                    params.put("dcode",1000);//设置预下单状态码
                    params.put("dmsg","success");//设置预下单状态码描述
                    Dto resultsDto = new BaseDto();
                    resultsDto.put("payInfo",respDto.getAsString("pay_info"));
                    params.put("results",resultsDto);//设置预下单响应结果
                }
                else
                {
                    logger.error("[威富通-微信wap(h5)预下单]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                    params.put("dcode",1001);
                    params.put("dmsg","响应结果签名不通过");
                }
            }
        }
        else
        {
            params.put("dcode",-1000);
            if(!response_success.equals(status))
            {
                params.put("dmsg",respDto.getAsString("message"));
            }
            else
            {
                params.put("dmsg",respDto.getAsString("err_msg"));
            }
        }
    }

    public static void main(String[] args)
    {
        try
        {
            //支付接口测试(如下参数都是必传参数)
            Dto params = new BaseDto();
            String out_trade_no = DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME);//商户订单号(平台支付流水号)
            params.put("merchantNo",merchantNo);
            params.put("appName",appName);
            params.put("secretKey",secretKey);
            params.put("apiUrl",apiUrl);
            params.put("payId",out_trade_no);//商户系统内部的订单号 ,32个字符内、 可包含字母,确保在商户系统唯一
            params.put("smoney","1");//总金额，以分为单位，不允许包含任何字、符号
            params.put("userIp","114.92.14.194");//订单生成的机器 IP
            params.put("notifyUrl","http://api.szmpyd.com/api/notify/swiftpass/weixin");//接收通知的URL，需给绝对路径，255字符内格式，确保平台能通过互联网访问该地址
            params.put("deviceInfo","AND_WAP");//应用类型,如果是用于苹果app应用里值为iOS_SDK；如果是用于安卓app应用里值为AND_SDK；如果是用于手机网站，值为iOS_WAP或AND_WAP均可
            //params.put("mch_app_name","红日超市");//应用名称,如果是用于苹果或安卓app应用中，传分别对应在AppStore和安桌分发市场中的应用名（如：王者荣耀）如果是用于手机网站，传对应的网站名(如：京东官网)
            //params.put("mch_app_id","cn.RedSunLottery");//应用标识,如果是用于苹果或安卓app应用中，苹果传IOS 应用唯一标识(如：com.tencent.wzryIOS)安卓传包名(如：com.tencent.tmgp.sgame)如果是用于手机网站，传网站首页URL地址,必须保证公网能正常访问(如：https://m.jd.com)
            params.put("signType",1);
            params.put("payId",DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME));
            params.put("smoney",1);
            params.put("clientFrom",1);
            createWeiXinWapPay(params);
            System.out.println(params.getAsString("dmsg"));

           /* Dto userParams = new BaseDto();
            userParams.put("device","C66A7C24-282E-4A09-8BC2-3EB4CA6D3F56");
            userParams.put("appId","100220");
            userParams.put("key","UK2PWIO0N1E8010ZD4F2033D546FD7V5");
            userParams.put("token","DA6jtEOSEYx314dE0XRslwsbO2qxQWHzrg1d/iHwgAwD5c0akLhHGXIiwDWUkTASErFBV4XjNFiuQvVlMucJdplYH+CnyhfP99Kb1Mx8Z6XRsEV0WbAnj7hosfbo82RNB6+wzRtoYpLjoY9eF1N5KzQ/2+wxeKdL4QEfl8KqX8hbMDrO0tCfmQvYLrimYPJj7EHfmERxDy70TT6st6z3k3+6pok1T/Zb/2ofZwry5GJxYTquxubNUB3v76TfPDV9vBGPnDRETqrIlCjEe/Q+00pHoVeBC8TTDW6BeI+6IabasRKoAeTZ2ItkLs8VcXuOo0SSV9o+rRgYg+rmzVSl7w==");
            userParams.put("qtype",1);
            userParams.put("psize",10);*/
            //userParams.put("pid",1);
            //userParams.put("smoney",0.001);
            //sign = SortUtils.getOrderByAsciiDescFromDto(userParams,false);
            //sign += "&skey=" + KeyConstants.loginKeys.get(userParams.get("appId").toString());
            //sign = URLEncoder.encode(sign,"UTF-8");
            //System.out.println(sign);
            //System.out.println(MD5.md5(sign).toUpperCase());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
