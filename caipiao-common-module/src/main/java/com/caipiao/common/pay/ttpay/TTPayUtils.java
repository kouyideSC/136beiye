package com.caipiao.common.pay.ttpay;

import com.alibaba.fastjson.JSON;
import com.caipiao.common.encrypt.Base64Util;
import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.encrypt.RSA;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.HttpClientUtil;
import com.caipiao.common.util.SortUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

/**
 * ttpay-支付工具类
 * @author mcdog
 */
public class TTPayUtils
{
    private static final Logger logger = LoggerFactory.getLogger(TTPayUtils.class);
    public static final String apiUrl = "http://trans.palmf.cn";
    public static final String appNo = "0000003490";//应用编号
    public static final String secretKey = "c2dbe13818b13cb67725dac655a3f2af";//应用key/签名秘钥
    public static final String rsaPubkey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC0dYM3DXkVg9q+WcNjBPWaUwKoeRMrwdE4p4F6fiztv/Ys6F5AxGCbFW5UfbtbQavMp9Rrg3+8mJ5/Lp8sjf471NFe6EvbCcVwJ63Q6fA4xVyCAE7mQdfAlpCk9WKN7Qa/HqwO/OM6JDyOyycnjnNi3f3K2tK/JbWd/SHYOSMEDQIDAQAB";
    public static final String dfRsaPublickey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC+47pYW/2Gv4yYrIKqrsMX+ibHk65vCjDO7yWDbAnW/3VWjB1WPX/PA2/Nudlkz56SHmO4FeeJLsltNb44EdJbjKAxvXJUSsYSq9gAwPRuI/zYksrv736QZ8rV7GGJUAQFqm2sDg0bMR1DZXkN7S4TxE6Ek+Wxl7XOcxO/JmtMRQIDAQAB";//代付Rsa公钥
    public static final String dfRsaPrivateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAL7julhb/Ya/jJisgqquwxf6JseTrm8KMM7vJYNsCdb/dVaMHVY9f88Db8252WTPnpIeY7gV54kuyW01vjgR0luMoDG9clRKxhKr2ADA9G4j/NiSyu/vfpBnytXsYYlQBAWqbawODRsxHUNleQ3tLhPEToST5bGXtc5zE78ma0xFAgMBAAECgYBZS2wDI10QaJBbZu9k6oWGtNTpLTM+7DRyyLnypVphAXUhPEAI9JHSA9iMEvEX0GtsDN024sBBkupUtPw7fFK7AgUccyROeZuocUflM7GlYAWBXw7L1rR3gBZjYn1s6bi5gHVVnjtvHqnV2rqivEwGM1voOM+GB+F6ZjaltO8xDQJBAPxjQeWciKxwEvr7mpm5o53rBThkffo5G9JRW2T5+ciHVKroRDDGhkGvUkQ4UrPGjpYKgVbuR5Jn9qVSPrVO+98CQQDBnySISmvMNlVb1XBnFj2WYy5LE2Aq3KwRa9rnD4S21C+F0qnclQosxbWR2Kn/cAAJrnFXX2uhBOhhyLeZoLxbAkBNAI+LWV3NlogD8R2zBxNnS9wU33wcCq51VYMX5LiTuM5ZMnITm448nhvwWmrJFKxOFLhvaFWH0pZKzDP7pzRrAkEAgxJFe9HNaZ+6ZwF1JwPiS0T22LHUHw/ll6GIvXQ+5jl7tj7m6EEocyigAoxGgHoEPImPhpM82/LjkRZ8W455EwJBAPeO/L/0jfyWgdW32Q+ZqwX4/RGzuXdQ2b5RpsMVp6u6ZRKE8P29sVKT9BLk/s8icWyt6LR6306OnEZOqwkuIgk=";//代付RSA私钥
    public static final String dfmerId = "0000002565";//代付商户编号
    public static final String dfmerKey = "07d66bbcbd2d8995ee985e36c8ee61cd";//代付签名秘钥
    public static final String dfapiUrl = "http://api.palmf.cn";//代付api地址

    /**
     * ttpay-QQh5支付预下单
     * @author  mcdog
     * @param   params  参数对象,统一下单(dcode-下单状态 dmsg-下单状态描述 resp-原始的统一下单响应字符串 results-响应结果)结果也保存在该对象中,具体如下:
     *                  dcode=1000,下单成功
     *                  dcode=-1000,下单失败
     *                  dcode=1001,下单失败(签名错误)
     */
    public static void createQqWalletWapPay(Dto params) throws Exception
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
        //api地址校验
        String apiUrl = params.getAsString("apiUrl");
        if(StringUtil.isEmpty(apiUrl))
        {
            params.put("dmsg","api地址apiUrl不能为空");
            return;
        }
        //应用编号校验
        String appNo = params.getAsString("appNo");
        if(StringUtil.isEmpty(appNo))
        {
            params.put("dmsg","应用编号appNo不能为空");
            return;
        }
        //商品名称校验
        String appName = params.getAsString("appName");
        if(StringUtil.isEmpty(appName))
        {
            params.put("dmsg","商品名称appName不能为空");
            return;
        }
        //签名密钥校验
        String secretKey = params.getAsString("secretKey");
        if(StringUtil.isEmpty(secretKey))
        {
            params.put("dmsg","签名密钥secretKey不能为空");
            return;
        }
        //RSA公钥验证
        String rsaPublicKey = params.getAsString("rsaPublicKey");
        if(StringUtil.isEmpty(secretKey))
        {
            params.put("dmsg","RSA公钥rsaPublicKey不能为空");
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
        //终端ip校验
        String clientIp = params.getAsString("clientIp");
        if(StringUtil.isEmpty(clientIp))
        {
            params.put("dmsg","终端ip-clientIp不能为空");
            return;
        }

        /**
         * 设置请求参数
         */
        Dto requstDto = new BaseDto();
        requstDto.put("amount",(int)(smoney1 * 100));//设置交易金额,单位为分,必须为正整数
        requstDto.put("appid",appNo);//设置应用编号
        requstDto.put("subject",appName);//设置商品名称
        requstDto.put("mchntOrderNo",payId);//设置商户订单号
        requstDto.put("payChannelId","2000000003");//设置支付功能id(2000000003-QQ扫码支付)
        requstDto.put("clientIp",clientIp);//设置用户终端ip
        requstDto.put("version","api_NoEncrypt");//接口版本号(值为api_NoEncrypt时,表示平台返回商户参数时,不进行RSA加密)
        requstDto.put("notifyUrl",notifyUrl);//设置异步通知
        if(StringUtil.isNotEmpty(params.get("returnUrl")))
        {
            requstDto.put("returnUrl",params.getAsString("returnUrl"));
        }
        //设置签名
        Object[] keys = requstDto.keySet().toArray();
        Arrays.sort(keys);
        StringBuilder originStr = new StringBuilder();
        for(Object key : keys)
        {
            if(StringUtil.isNotEmpty(requstDto.get(key)))
            {
                originStr.append(key).append("=").append(requstDto.get(key)).append("&");
            }
        }
        originStr.append("key=").append(secretKey);
        String sign = DigestUtils.md5Hex(originStr.toString().getBytes("utf-8"));
        requstDto.put("signature",sign);//设置签名

        /**
         * 加密传输数据,发送请求
         */
        String jsonObject = JSON.toJSONString(requstDto);
        String orderInfo = Base64.encodeBase64String(RSAUtil.encryptByPublicKeyByPKCS1Padding(jsonObject.getBytes("utf-8"),rsaPublicKey));
        Dto orderInfoDto = new BaseDto("orderInfo",orderInfo);
        logger.info("[ttpay-QQh5支付预下单]请求参数=" + orderInfoDto.toJson());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/sdk/api/v1.0/cli/order_api/0",orderInfoDto);
        params.put("resp",resp);
        logger.info("[ttpay-QQh5支付预下单]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        if(StringUtil.isNotEmpty(resp))
        {
            Dto respDto = JsonUtil.jsonToDto(resp);
            String respCode = respDto.getAsString("respCode");//提取状态码
            if("200".equals(respCode))
            {
                params.put("tradeNo",respDto.getAsString("orderNo"));//设置渠道流水号
                Dto resultsDto = new BaseDto();
                String extra = respDto.getAsString("extra");
                if(StringUtil.isNotEmpty(extra) && extra.indexOf("code_url=") > -1)
                {
                    params.put("dcode",1000);//设置预下单状态码
                    params.put("dmsg","success");//设置预下单状态码描述
                    String payUrl = extra.substring(0,extra.indexOf("&code_img_url="));
                    resultsDto.put("payInfo",payUrl.replace("code_url=",""));//设置支付链接
                    params.put("results",resultsDto);//设置预下单响应结果
                }
                else
                {
                    params.put("dcode",-1000);
                    params.put("dmsg","获取不到支付链接!extra=" + extra);
                }

            }
            else
            {
                params.put("dcode",-1000);
                params.put("dmsg","下单失败!respCode=" + respCode + ",respMsg=" + respDto.getAsString("respMsg"));
            }
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg","下单失败!resp=" + resp);
        }
    }

    /**
     * ttpay-代付请求
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
        //收款银行简称/代码
        String bankAbbreviation = params.getAsString("bankAbbreviation");
        if(StringUtil.isEmpty(bankAbbreviation))
        {
            params.put("dmsg","收款银行简称/代码bankAbbreviation不能为空");
            return;
        }
        //收款银行编号/联行号校验
        String bankCode = params.getAsString("bankCode");
        if(StringUtil.isEmpty(bankCode))
        {
            params.put("dmsg","收款银行编号bankCode不能为空");
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
        String mkey = dfmerKey.substring(0,16);//取前16位
        requestDto.put("service","service.api.tx.apply");//设置接口类型
        requestDto.put("version","v1.1");//设置接口版本号,固定值v1.1
        requestDto.put("mchntId",merchantNo);//设置商户编号
        requestDto.put("payeeName",aesEncrypt(bankUserName,mkey));//设置收款人名称
        requestDto.put("payeeBankcardNo",aesEncrypt(bankAccount,mkey));//设置银行卡号
        requestDto.put("payeeBankName",aesEncrypt(bankAbbreviation,mkey));//设置银行简称
        requestDto.put("payeeBankCode",aesEncrypt(bankCode,mkey));//设置银行联行号
        requestDto.put("txAmount", (int)(smoney1 * 100) + "");//设置代付金额(单位:分)
        requestDto.put("txMerNo",payId);//设置商户订单号
        requestDto.put("txAccountType","0");//设置账户类型(0-对私 1-对公)

        //设置签名
        Object[] keys = requestDto.keySet().toArray();
        Arrays.sort(keys);
        StringBuilder originStr = new StringBuilder();
        for(Object key : keys)
        {
            if(StringUtil.isNotEmpty(requestDto.get(key)))
            {
                originStr.append(key).append("=").append(requestDto.get(key)).append("&");
            }
        }
        String content = originStr.toString();
        content = content.substring(0,content.length()-1);
        requestDto.put("signature",signRSA(content,dfRsaPrivateKey,"utf-8"));//设置签名

        /**
         * 发送请求
         */
        String reqParams = requestDto.toJson();
        logger.info("[ttpay-代付请求]请求参数=" + reqParams);
        String resp = HttpSendUtil.doHttpAndHttps(apiUrl + "/api/tx/apply",reqParams);
        params.put("resp",resp);
        logger.info("[ttpay-代付请求]响应结果=" + resp);
        if(StringUtil.isNotEmpty(resp))
        {
            Dto respDto = JsonUtil.jsonToDto(resp);
            if(respDto != null)
            {
                String code = respDto.getAsString("code");//提取应答码
                if("10000".equals(code))
                {
                    params.put("dcode",1000);
                    params.put("tradeNo",respDto.get("txNo"));//提取交易流水号
                    params.put("dmsg","代付请求发送成功");
                }
                else
                {
                    params.put("dcode",-1000);
                    params.put("dmsg","代付请求发送失败!code=" + code + ",msg=" + respDto.getAsString("msg"));
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
     * ttpay-充值订单查询
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
        //应用编号校验
        String appNo = params.getAsString("appNo");
        if(StringUtil.isEmpty(appNo))
        {
            params.put("dmsg","应用编号appNo不能为空");
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
         * 发送请求
         */
        String url = apiUrl + "/sdk/v1.0/payOrderResult/" + payId + "/" + appNo;
        logger.info("[ttpay-充值订单查询]请求路径=" + url);
        String resp = HttpClientUtil.callHttpPost_Dto(url,new BaseDto());
        params.put("resp",resp);
        logger.info("[ttpay-充值订单查询]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        if(StringUtil.isNotEmpty(resp))
        {
            JSONObject respObject = JSONObject.fromObject(resp);
            if(StringUtil.isNotEmpty(respObject))
            {
                String returnCode = respObject.getString("returnCode");//提取返回码
                if("200".equals(returnCode))
                {
                    //验证签名
                    String respSign = respObject.getString("signature");
                    respObject.remove("signature");
                    Object[] keys =  respObject.keySet().toArray();
                    Arrays.sort(keys);
                    StringBuilder originStr = new StringBuilder();
                    for(Object key : keys)
                    {
                        if(StringUtil.isNotEmpty(respObject.get(key)))
                        {
                            originStr.append(key).append("=").append(respObject.get(key)).append("&");
                        }
                    }
                    originStr.append("key=").append(secretKey);
                    String realSign = MD5.md5(originStr.toString());
                    if(respSign.equals(realSign))
                    {
                        //设置响应结果
                        params.put("dcode",1000);//设置查询状态码
                        params.put("dmsg","success");//设置查询状态码描述

                        //设置订单交易信息
                        Dto resultsDto = new BaseDto();
                        resultsDto.put("tradeNo",respObject.getString("orderNo"));//设置渠道流水号
                        resultsDto.put("smoney",respObject.getDouble("amount") / 100);//设置订单金额

                        //设置订单交易状态
                        String paySt = respObject.getString("paySt");//提取订单交易状态
                        if("2".equals(paySt))
                        {
                            resultsDto.put("status",1000);//设置交易状态为成功
                            resultsDto.put("msg","success");
                        }
                        else if("3".equals(paySt))
                        {
                            resultsDto.put("status",-1000);//设置交易状态为失败
                            resultsDto.put("msg","paySt=" + paySt);
                        }
                        else
                        {
                            resultsDto.put("status",1001);//设置交易状态为交易中
                            resultsDto.put("msg","订单交易状态paySt=" + paySt);
                        }
                        params.put("results",resultsDto);//设置查询响应结果
                    }
                    else
                    {
                        logger.error("[ttpay-充值订单查询]响应结果签名验证不通过!响应签名=" + respSign + ",验证签名=" + realSign);
                        params.put("dcode",-1000);
                        params.put("dmsg","响应结果签名不通过");
                    }
                }
                else
                {
                    params.put("dcode",-1000);
                    params.put("dmsg","订单查询失败!returnCode=" + returnCode + ",errorMsg=" + respObject.getString("errorMsg"));
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
     * ttpay-代付订单查询
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

        /**
         * 设置请求参数
         */
        Dto requestDto = new BaseDto();
        requestDto.put("service","service.api.tx.query");//接口类型
        requestDto.put("mchntId",merchantNo);//商户编号
        requestDto.put("txMerNo",payId);//商户订单号

        //设置签名
        Object[] keys = requestDto.keySet().toArray();
        Arrays.sort(keys);
        StringBuilder originStr = new StringBuilder();
        for(Object key : keys)
        {
            if(StringUtil.isNotEmpty(requestDto.get(key)))
            {
                originStr.append(key).append("=").append(requestDto.get(key)).append("&");
            }
        }
        String content = originStr.toString();
        content = content.substring(0,content.length()-1);
        requestDto.put("signature",signRSA(content,dfRsaPrivateKey,"utf-8"));//设置签名

        /**
         * 发送请求
         */
        String reqParams = requestDto.toJson();
        logger.info("[ttpay-代付订单查询]请求参数=" + reqParams);
        String resp = HttpSendUtil.doHttpAndHttps(apiUrl + "/api/tx/query",reqParams);
        params.put("resp",resp);
        logger.info("[ttpay-代付订单查询]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        if(StringUtil.isNotEmpty(resp))
        {
            Dto respDto = JsonUtil.jsonToDto(resp);
            if(respDto != null)
            {
                String code = respDto.getAsString("code");//提取响应码
                if("10000".equals(code))
                {
                    //设置响应结果
                    params.put("dcode",1000);//设置查询状态码
                    params.put("dmsg","success");//设置查询状态码描述

                    /**
                     * 设置订单交易信息
                     */
                    Dto resultsDto = new BaseDto();
                    resultsDto.put("smoney",respDto.getAsDoubleValue("txSuccessAmount") / 100);//设置订单金额
                    resultsDto.put("tradeNo",respDto.getAsString("txNo"));//设置交易流水号

                    //设置订单交易状态
                    String txStatus = respDto.getAsString("txStatus");//提取订单交易状态
                    if("2".equals(txStatus))
                    {
                        resultsDto.put("status",1000);//设置交易状态为成功
                        resultsDto.put("msg","success");
                    }
                    else if("3".equals(txStatus))
                    {
                        resultsDto.put("status",-1000);//设置交易状态为失败
                        resultsDto.put("msg",StringUtil.isNotEmpty(respDto.get("txFailReason"))? respDto.getAsString("txFailReason") : "代付失败");
                    }
                    else
                    {

                        resultsDto.put("status",1001);//设置交易状态为交易中
                        resultsDto.put("msg","订单交易状态txStatus=" + txStatus);//设置交易状态描述
                    }
                    params.put("results",resultsDto);//设置查询响应结果

                }
                else
                {
                    params.put("dcode",-1000);
                    params.put("dmsg","查询失败!code=" + code + ",msg=" + respDto.getAsString("msg"));
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
     * RSA私钥签名
     * @param   content     待签名数据
     * @param   privateKey  私钥
     * @param   charset     字符编码
     */
    public static String signRSA(String content,String privateKey,String charset)
    {
        try
        {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey.getBytes()));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);
            java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");
            signature.initSign(priKey);
            signature.update(content.getBytes(charset));
            byte[] signed = signature.sign();
            return new String(Base64.encodeBase64(signed));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * AES加密
     */
    public static String aesEncrypt(String str, String key) throws Exception
    {
        if (str == null || key == null) return null;
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes("utf-8"), "AES"));
        byte[] bytes = cipher.doFinal(str.getBytes("utf-8"));
        return Base64Util.encodeByte(bytes);
    }

    /**
     * AES解密
     */
    public static String aesDecrypt(String str, String key) throws Exception {
        if (str == null || key == null) return null;
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes("utf-8"), "AES"));
        byte[] bytes = Base64Util.decodeToByte(str);
        bytes = cipher.doFinal(bytes);
        return new String(bytes, "utf-8");
    }

    public static void main(String[] args)
    {
        try
        {
            Dto params = new BaseDto();
            params.put("appNo",appNo);//设置应编号
            params.put("appName","思岂网络");//设置商品名称
            params.put("apiUrl",apiUrl);//设置api地址
            params.put("secretKey",secretKey);//设置签名密钥
            params.put("rsaPublicKey",rsaPubkey);//设置RSA公钥

            //设置订单号
            String payId = "CZ" + DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME);//生成商户订单号(平台流水号)
            payId += new Random().nextInt(10);
            params.put("payId",payId);//设置商户订单号
            params.put("smoney",10);//设置订单金额
            params.put("notifyUrl","http://api.sqgoing.com/api/notify/ttpay");
            params.put("returnUrl","http://mobile.sqgoing.com/html/pay/notify/notify.html");
            params.put("clientIp","114.95.156.70");//设置ip
            //createQqWalletWapPay(params);
            //System.out.println(params.getAsString("dmsg"));
            //params.put("payId","CZ201904192150200942");
            //queryOrder(params);
            params.clear();
            payId = "TX" + DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME);//生成商户订单号
            payId += new Random().nextInt(10);
            params.put("apiUrl",dfapiUrl);//设置api地址
            params.put("merchantNo",dfmerId);//设置商户编号
            params.put("bankUserName","孙俊奇");//设置收款人
            params.put("bankAccount","6214850210345952");//设置收款账户
            params.put("bankAbbreviation","CMB");//设置收款银行简称
            params.put("bankCode","308584000013");//设置收款银行编号
            params.put("smoney","28");//设置付款金额
            params.put("payId",payId);//设置商户订单号
            params.put("payId","TX201904221145188044");
            //sendDaiFuRequest(params);
            queryDfOrder(params);
            System.out.println(params.getAsString("resp"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
