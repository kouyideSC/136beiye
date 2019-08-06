package com.caipiao.common.pay.yizhi;

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

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 易旨支付工具类
 * @author sjq
 */
public class YizhiPayUtils
{
    private static final Logger logger = LoggerFactory.getLogger(YizhiPayUtils.class);
    public static final String merchantNo = "160027S181105007";//商户号
    public static final String apiUrl = "https://alipay.3c-buy.com";//api地址
    public static final String queryApiUrl = "http://jh.chinambpc.com";
    public static final String secretKey = "GTcGEhrIAIbGvdxSRtwiuovonbUDfD19";//签名密钥
    public static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDqfbPaf2k+oDxTetGEbrBC9GT4Vksbnc83gNKIVXdAKV3x9SapfNANtE16Dpf8HRQPzvt0QjIm7E6ycFH+3moiW5aZvK7Ed7T7T6duyMZ2fLXHezaea3C/h9trbd4Ctu+bxe4deJR1z0hWVdfvWAN0lsgP2838y1MFp3HHEpeCQQIDAQAB";//公钥
    public static final String privateKey = "MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBAOp9s9p/aT6gPFN60YRusEL0ZPhWSxudzzeA0ohVd0ApXfH1Jql80A20TXoOl/wdFA/O+3RCMibsTrJwUf7eaiJblpm8rsR3tPtPp27IxnZ8tcd7Np5rcL+H22tt3gK275vF7h14lHXPSFZV1+9YA3SWyA/bzfzLUwWncccSl4JBAgMBAAECgYEAoClpnfTmkrLpYW+DCbx8wc6h5Ik0VdGNBnED28DcacOXejsixCMPDD05qgOrxbSqzj1mvLq1/KXs/q3/I2ERBcWnPxvVu1BPeLkZW/y7amiCFbfbnQ+/QGygiIlbhvjxBpMj/j+D1DZcM9jNwYhqTGVhNqbat/d+zRl2k2BX4a0CQQD/DakO7yr+SHdmHVZZmoKIIKF1tXB0+5KhIn5msXAxncNd7RswNcFMVpwKN8+kn1eA+j0sPDnpBIj2gQxVowbXAkEA61yBO16BzZd+Uk+raBPrvkuCX1TIiLa/onL23Ycl63p26dJ7iMKDy8AmAGCFU5lAAGZlACRy+4XlKKcKeozUpwJBAPsHf+hB/6fMvS+VGmMQjv7aVu/ah3esD9Jmd9AtPganhx0P/F2D2t9+sw/fhJA7gE1ifgjmsfylSQSNWQ2EansCQQDV5IiRks62L7IeTccBW/FHTEocJunjzOkVUhNluZfNi8sbriyUWen6thITD6S7F6/hbSzm1zkjukhLDJtqSihLAkEA1w7J8bWgdBpkD8G/2+J7PEgp4TUiDTmL2lKR6kvE+G3zA9cjfmB61pBZEJaHsYOjkKK+Bq51zYS77dby3C1uiw==";//私钥
    public static final String ymdpublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDld3/MuKCKlr/jUT8iDYkhTJV+E7V+2e1KTO0BlwZLsNitthUENflmOX5y3tLSGmFo2MmclpYKOr16GQF+dGaT0i5NA70za2UUIqi4J8Vtmi1tLKEX2jQ3FGiLODbfBNjVBCSv4+ibP9I9k5x5FSLUVRaGs3ijuoM9/jajnZ33+QIDAQAB";//一麻袋公钥
    public static final String ymdmerchantNo = "46098";//一麻袋商户号
    public static final String ymdapiUrl = "https://gwapi.yemadai.com";//api地址

    /**
     * 易旨支付(一麻袋)-代付请求
     * @author  mcdog
     * @param   params  参数对象,代付请求结果也保存在该对象中(dcode-代付请求状态 dmsg-代付请求状态描述 resp-原始的代付请求响应字符串),具体如下:
     *                  dcode=1000,代付请求成功
     *                  dcode=-1000,代付请求失败
     *                  dcode=1001,付款请求失败(签名错误)
     */
    public static void transBatchBank(Dto params) throws Exception
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
        //一麻袋商户号校验
        String ymdmerchantNo = params.getAsString("ymdmerchantNo");
        if(StringUtil.isEmpty(ymdmerchantNo))
        {
            params.put("dmsg","一麻袋商户号ymdmerchantNo不能为空");
            return;
        }
        //RSA私钥校验
        String privateKey = params.getAsString("privateKey");
        if(StringUtil.isEmpty(privateKey))
        {
            params.put("dmsg","RSA私钥privateKey不能为空");
            return;
        }
        //一麻袋公钥校验
        String ymdpublicKey = params.getAsString("ymdpublicKey");
        if(StringUtil.isEmpty(ymdpublicKey))
        {
            params.put("dmsg","一麻袋公钥ymdpublicKey不能为空");
            return;
        }
        //通知地址校验
        String notifyUrl = params.getAsString("notifyUrl");//异步通知地址
        if(StringUtil.isEmpty(notifyUrl))
        {
            params.put("dmsg","通知地址notifyUrl不能为空");
            return;
        }
        //付款明细校验
        List<Dto> details = params.getAsList("details");//提取付款明细
        if(details == null || details.size() == 0)
        {
            params.put("dmsg","付款明细details不能为空");
            return;
        }

        /**
         * 设置请求参数
         */
        //设置基础参数
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        stringBuffer.append("<yemadai>");
        stringBuffer.append("<accountNumber>" + ymdmerchantNo + "</accountNumber>");//一麻袋商户号
        stringBuffer.append("<signType>RSA</signType>");//签名类型
        stringBuffer.append("<notifyURL>" + notifyUrl + "</notifyURL>");//异步通知地址
        stringBuffer.append("<tt>" + (StringUtil.isEmpty(params.get("isjj"))? 0 : params.getAsInteger("isjj")) + "</tt>");//是否加急,0-普通 1-加急,默认为0

        //设置付款明细
        RsaUtils rsaUtils = RsaUtils.getInstance();
        for(Dto detailDto : details)
        {
            /**
             * 校验参数
             */
            //单笔付款明细商户流水号(订单号)校验
            String payId = detailDto.getAsString("payId");//获取商户流水号(订单号)
            if(StringUtil.isEmpty(payId))
            {
                params.put("dmsg","单笔付款明细商户订单号payId不能为空");
                return;
            }
            else if(payId.length() > 20)
            {
                params.put("dmsg","单笔付款明细商户订单)payId长度不合法,长度不能超过20位");
                return;
            }
            //单笔付款明细付款金额校验
            String amountStr = detailDto.getAsString("amount");
            Double amount = detailDto.getAsDouble("amount");
            if(amount == null || amount.doubleValue() <= 0)
            {
                params.put("dmsg","单笔付款明细金额amount不合法,金额不能为空且必须大于0");
                return;
            }
            else if(amountStr.indexOf(".") > 0 && amountStr.substring(amountStr.indexOf(".")).length() > 3)
            {
                params.put("dmsg","单笔付款明细金额amount不合法,金额最多只能有2位小数");
                return;
            }
            //单笔付款明细收款银行校验
            String bankName = detailDto.getAsString("bankName");
            if(StringUtil.isEmpty(bankName))
            {
                params.put("dmsg","单笔付款明细收款银行bankName不能为空");
                return;
            }
            //单笔付款明细收款人校验
            String bankUserName = detailDto.getAsString("bankUserName");
            if(StringUtil.isEmpty(bankUserName))
            {
                params.put("dmsg","单笔付款明细收款人bankUserName不能为空");
                return;
            }
            //单笔付款明细收款银行账号校验
            String bankAccount = detailDto.getAsString("bankAccount");
            if(StringUtil.isEmpty(bankAccount))
            {
                params.put("dmsg","单笔付款明细收款银行账号bankAccount不能为空");
                return;
            }
            //单笔付款明细收款支行校验
            String branchName = detailDto.getAsString("branchName");
            if(StringUtil.isEmpty(branchName))
            {
                params.put("dmsg","单笔付款明细收款支行branchName不能为空");
                return;
            }
            //单笔付款明细款收款银行所在省份校验
            String province = detailDto.getAsString("province");
            if(StringUtil.isEmpty(province))
            {
                params.put("dmsg","单笔付款明细款收款银行所在省份province不能为空");
                return;
            }
            //单笔付款明细款收款银行所在城市校验
            String city = detailDto.getAsString("city");
            if(StringUtil.isEmpty(city))
            {
                params.put("dmsg","单笔付款明细款收款银行所在城市city不能为空");
                return;
            }

            //设置单笔付款明细
            stringBuffer.append("<transferList>");
            stringBuffer.append("<transId>" + payId + "</transId>");
            stringBuffer.append("<bankCode>" + bankName + "</bankCode>");
            stringBuffer.append("<provice>" + province + "</provice>");
            stringBuffer.append("<city>" + city + "</city>");
            stringBuffer.append("<branchName>" + branchName + "</branchName>");
            stringBuffer.append("<accountName>" + bankUserName + "</accountName>");
            stringBuffer.append("<cardNo>" + bankAccount + "</cardNo>");
            stringBuffer.append("<amount>" + String.format("%.2f",amount) + "</amount>");
            stringBuffer.append("<remark>" + (StringUtil.isEmpty(detailDto.get("remark"))? "付款" : detailDto.getAsString("remark")) + "</remark>");

            //设置签名
            String plain ="transId=" + payId + "&accountNumber=" + ymdmerchantNo + "&cardNo=" + bankAccount + "&amount=" + String.format("%.2f",amount);
            String signInfo = rsaUtils.signData(plain,privateKey);
            stringBuffer.append("<secureCode>" + signInfo + "</secureCode>");
            stringBuffer.append("</transferList>");
        }
        stringBuffer.append("</yemadai>");

        /**
         * 发送请求
         */
        Base64 base64 = new Base64();
        String transData = base64.encodeToString(stringBuffer.toString().getBytes("UTF-8"));
        List<NameValuePair> paramList = new ArrayList<NameValuePair>();
        paramList.add(new BasicNameValuePair("transData",transData));
        logger.info("[易旨支付(一麻袋)-代付请求]请求原始参数=" + stringBuffer.toString() + ",请求实际参数=" + transData);
        String httpPost = connect(paramList,ymdapiUrl + "/transfer/transferFixed");
        logger.info("[易旨支付(一麻袋)-代付请求]响应原始结果=" + httpPost);
        String xmlstr = new String(base64.decode(httpPost),"UTF-8");
        params.put("resp",xmlstr);
        logger.info("[易旨支付(一麻袋)-代付请求]响应实际结果=" + xmlstr);

        /**
         * 处理请求结果
         */
        xmlstr = xmlstr.substring(xmlstr.indexOf("<?xml"));//去掉状态码
        Dto respDto = parseDtoFromXml(xmlstr);
        String errCode = respDto.getAsString("errCode");//提取错误码
        if("0000".equals(errCode))
        {
            List<Dto> detailList = (List<Dto>)respDto.get("transferList");//提取详细
            if(detailList != null && detailList.size() > 0)
            {
                Dto detailDto = detailList.get(0);
                String resCode = detailDto.getAsString("resCode");//提取交易状态码
                String respSign = detailDto.getAsString("secureCode");//提取请求响应签名

                //验证签名
                String plain ="transId=" + detailDto.getAsString("transId") + "&resCode=" + resCode + "&accountNumber=" + merchantNo + "&cardNo=" + detailDto.getAsString("cardNo") + "&amount=" + detailDto.getAsString("amount");
                if(rsaUtils.verifySignature(respSign,plain,ymdpublicKey))
                {
                    if("0000".equals(resCode))
                    {
                        params.put("dcode",1000);
                        params.put("dmsg","付款请求发送成功");
                    }
                    else if("ERR1003".equals(resCode))
                    {
                        params.put("dcode",1001);
                        params.put("dmsg","请求签名验证错误");
                    }
                    //余额不足
                    else if("ERR1010".equals(resCode))
                    {
                        params.put("dcode",-1000);
                        params.put("dmsg","余额不足");
                    }
                    //超过单笔限额
                    else if("ERR5003".equals(resCode))
                    {
                        params.put("dcode",-1000);
                        params.put("dmsg","下发超过单笔限额设置");
                    }
                    //超过单日限额
                    else if("ERR5005".equals(resCode))
                    {
                        params.put("dcode",-1000);
                        params.put("dmsg","商户下发超过单日限额");
                    }
                    else
                    {
                        params.put("dcode",-1000);
                        params.put("dmsg","付款请求失败!交易状态码=" + resCode);
                    }
                }
                else
                {
                    params.put("dcode",1001);
                    params.put("dmsg","响应签名验证错误");
                }
            }
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg","付款请求失败!错误状态码=" + errCode);
        }
    }

    /**
     * 易旨支付-银联预下单
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
        requestDto.put("merchantOutOrderNo",payId);//商户订单号
        requestDto.put("merid",merchantNo);//商户号
        requestDto.put("noncestr",System.currentTimeMillis());//随机参数,长度不大于32位
        requestDto.put("notifyUrl",notifyUrl);//异步通知地址
        requestDto.put("orderMoney",String.format("%.2f",smoney1));//订单金额(单位:元),要么是整数,要么保留2位小数
        requestDto.put("orderTime",DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME2));//订单时间,yyyyMMddHHmmss
        if(StringUtil.isNotEmpty(params.get("returnUrl")))
        {
            requestDto.put("returnUrl",params.getAsString("returnUrl"));//支付完成跳转页面
        }
        //设置签名
        Dto signDto = new BaseDto();
        signDto.putAll(requestDto);
        signDto.remove("id");//商户自定义参数不参与签名
        signDto.remove("returnUrl");//支付完成跳转页面不参与签名
        signDto.remove("subMerchantName");//商户名称不参与签名
        String sign = URLDecoder.decode(SortUtils.getOrderByAsciiAscFromDto(signDto,false),"utf-8");
        sign += "&key=" + secretKey;
        sign = MD5.md5(sign).toLowerCase();
        requestDto.put("sign",sign);

        //设置表单提交地址及渠道充值渠道编号
        requestDto.put("action",apiUrl + "/api/createQuickOrder");//设置表单提交地址
        requestDto.put("pccode", PayConstants.PAYCHANNEL_CODE_YIZHIPAY);//设置渠道编号

        /**
         * 设置表单参数
         */
        String urlparams = requestDto.toSeparatorString("&");
        logger.info("[易旨支付-银联预下单]请求表单参数=" + urlparams);
        params.put("dcode",1000);//设置预下单状态码
        params.put("dmsg","success");//设置预下单状态码描述
        Dto resultsDto = new BaseDto();
        resultsDto.put("payInfo", params.getAsString("mobilehost") + PayConstants.unionpayWapPage + "?" + urlparams);//设置支付链接
        params.put("results",resultsDto);
    }

    /**
     * 易旨支付(一麻袋)-代付订单查询
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
        params.put("dmsg","订单查询失败");//设置默认状态码描述
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
        //一麻袋商户号校验
        String ymdmerchantNo = params.getAsString("ymdmerchantNo");
        if(StringUtil.isEmpty(ymdmerchantNo))
        {
            params.put("dmsg","一麻袋商户号ymdmerchantNo不能为空");
            return;
        }
        //RSA私钥校验
        String privateKey = params.getAsString("privateKey");
        if(StringUtil.isEmpty(privateKey))
        {
            params.put("dmsg","RSA私钥privateKey不能为空");
            return;
        }
        //一麻袋公钥校验
        String ymdpublicKey = params.getAsString("ymdpublicKey");
        if(StringUtil.isEmpty(ymdpublicKey))
        {
            params.put("dmsg","一麻袋公钥ymdpublicKey不能为空");
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
        //设置基础参数
        String requestTime = DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME2);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        stringBuffer.append("<yemadai>");
        stringBuffer.append("<merchantNumber>" + ymdmerchantNo + "</merchantNumber>");//商户号
        stringBuffer.append("<signType>RSA</signType>");//签名方式
        stringBuffer.append("<mertransferID>" + payId + "</mertransferID>");//商户订单号

        //时间范围查询条件
        if(StringUtil.isNotEmpty(params.get("beginTime")))
        {
            stringBuffer.append("<queryTimeBegin>" + params.getAsString("beginTime") + "</queryTimeBegin>");//下发开始时间,格式为yyyy-MM-dd HH:mm:ss
        }
        if(StringUtil.isNotEmpty(params.get("endTime")))
        {
            stringBuffer.append("<queryTimeEnd>" + params.getAsString("endTime") + "</queryTimeEnd>");//下发结束时间,格式为yyyy-MM-dd HH:mm:ss
        }
        //设置请求时间
        stringBuffer.append("<requestTime>" + requestTime + "</requestTime>");//请求时间,格式为yyyyMMddHHmmss

        //设置签名
        RsaUtils rsaUtils = RsaUtils.getInstance();
        String plain = ymdmerchantNo + "&" + requestTime;
        String signInfo = rsaUtils.signData(plain,privateKey);
        stringBuffer.append("<sign>" + signInfo + "</sign>");
        stringBuffer.append("</yemadai>");

        /**
         * 发送请求
         */
        Base64 base64 = new Base64();
        String transData = base64.encodeToString(stringBuffer.toString().getBytes("UTF-8"));
        List<NameValuePair> paramList = new ArrayList<NameValuePair>();
        paramList.add(new BasicNameValuePair("requestDomain",transData));
        logger.info("[易旨支付(一麻袋)-代付订单查询]请求原始参数=" + stringBuffer.toString() + ",请求实际参数=" + transData);
        String httpPost = connect(paramList,ymdapiUrl + "/transfer/transferQueryFixed");
        logger.info("[易旨支付(一麻袋)-代付订单查询]原始响应结果=" + httpPost);
        String xmlstr = new String(base64.decode(httpPost),"utf-8");
        params.put("resp",xmlstr);
        logger.info("[易旨支付(一麻袋)-代付订单查询]实际响应结果=" + xmlstr);

        /**
         * 处理请求结果
         */
        xmlstr = xmlstr.substring(xmlstr.indexOf("<?xml"));//去掉状态码
        Dto respDto = parseDtoFromXml(xmlstr);
        String code = respDto.getAsString("code");//提取状态码
        if("0000".equals(code))
        {
            //设置响应结果
            params.put("dcode",1000);//设置查询状态码
            params.put("dmsg","success");//设置查询状态码描述

            /**
             * 设置订单交易信息
             */
            Dto resultsDto = new BaseDto();
            List<Dto> detailList = (List<Dto>)respDto.get("transfer");//提取详细
            if(detailList != null && detailList.size() > 0)
            {
                //设置订单交易信息
                Dto detailDto = detailList.get(0);
                String state = detailDto.getAsString("state");//提取订单交易状态
                if("00".equals(state))
                {
                    resultsDto.put("status",1000);//设置交易状态为成功
                    resultsDto.put("msg","success");
                }
                else if("11".equals(state))
                {
                    resultsDto.put("status",-1000);//设置交易状态为交易失败
                    resultsDto.put("msg",StringUtil.isEmpty(detailDto.get("memo"))? "交易失败" : detailDto.getAsString("memo"));//设置交易状态描述
                }
                else
                {
                    resultsDto.put("status",1001);//设置交易状态为交易中
                    resultsDto.put("msg",StringUtil.isEmpty(detailDto.get("memo"))? "交易中" : detailDto.getAsString("memo"));//设置交易状态描述
                }
            }
            else
            {
                resultsDto.put("status",1001);//设置交易状态为交易中
                resultsDto.put("msg","提取不到有效的订单详细信息!订单详细为空!");
            }
            params.put("results",resultsDto);//设置订单交易结果
        }
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg","订单查询失败,状态码=" + code);
        }
    }

    /**
     * 易旨支付-充值订单查询
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
        requestDto.put("merid",merchantNo);//商户号
        requestDto.put("merchantOutOrderNo",payId);//商户订单号
        requestDto.put("noncestr",System.currentTimeMillis());//随机参数,长度不大于32位

        //设置签名
        String sign = SortUtils.getOrderByAsciiAscFromDto(requestDto,false) + "&key=" + secretKey;
        sign = MD5.md5(sign).toLowerCase();
        requestDto.put("sign",sign);

        /**
         * 发送请求
         */
        logger.info("[易旨支付-充值订单查询]请求参数=" + requestDto.toString());
        String resp = HttpClientUtil.callHttpPost_Dto(apiUrl + "/api/queryOrder",requestDto);
        params.put("resp",resp);
        logger.info("[易旨支付-充值订单查询]响应结果=" + resp);

        /**
         * 处理请求结果
         */
        if(StringUtil.isNotEmpty(resp))
        {
            Dto respDto = JsonUtil.jsonToDto(resp);
            if(StringUtil.isEmpty(respDto.get("code")))
            {
                //设置响应结果
                params.put("dcode",1000);//设置查询状态码
                params.put("dmsg","success");//设置查询状态码描述

                //设置订单交易信息
                Dto resultsDto = new BaseDto();
                resultsDto.put("tradeNo",respDto.getAsString("orderNo"));//设置渠道流水号(支付宝交易订单号)
                resultsDto.put("smoney",respDto.getAsDoubleValue("orderMoney"));//设置订单金额(取实际支付金额)

                //设置订单交易状态
                String payResult = respDto.getAsString("payResult");//提取订单交易状态
                if("1".equals(payResult))
                {
                    resultsDto.put("status",1000);//设置交易状态为成功
                    resultsDto.put("msg","success");
                }
                else if("0".equals(payResult))
                {
                    resultsDto.put("status",-1000);//设置交易状态为交易失败
                    resultsDto.put("msg","订单交易状态payResult=" + payResult);//设置交易状态描述
                }
                else
                {
                    resultsDto.put("status",1001);//设置交易状态为交易中
                    resultsDto.put("msg","订单交易状态payResult=" + payResult);//设置交易状态描述
                }
                params.put("results",resultsDto);//设置查询响应结果
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

    /**
     * 连接类
     * @param   nvps    请求参数
     * @param   url     请求地址
     */
    private static String connect(List<NameValuePair> nvps, String url)
    {
        try
        {
            HTTPClientUtils h = new HTTPClientUtils();
            String httpPost = h.httpPostPara(nvps,url);
            return httpPost;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * xml字符串转Dto
     * @author  mcdog
     * @param   xmlstr  xml字符串
     */
    public static Dto parseDtoFromXml(String xmlstr)
    {
        Dto dto = new BaseDto();
        if(StringUtil.isNotEmpty(xmlstr))
        {
            Document doc = null;
            try
            {
                doc = DocumentHelper.parseText(xmlstr);
            }
            catch(DocumentException e)
            {
                e.printStackTrace();
            }
            elementToDto(dto,doc.getRootElement());
        }
        return dto;
    }

    /**
     * 遍历子节点
     */
    public static void elementToDto(Dto dto, Element rootElement)
    {
        List<Element> elements = rootElement.elements();//获得当前节点的子节点

        //递归子节点
        for(Element element : elements)
        {
            if(element.elements().size()>0)
            {
                ArrayList<Dto> list = new ArrayList<Dto>();
                elementChildToList(list,element);
                dto.put(element.getName(),list);
            }
            else
            {
                dto.put(element.getName(),element.getText());
            }
        }
    }

    /**
     * 递归子节点的子节点
     */
    public static void elementChildToList(ArrayList<Dto> arrayList, Element rootElement)
    {
        //获得当前节点的子节点
        List<Element> elements = rootElement.elements();
        if(elements.size()>0)
        {
            ArrayList<Dto> list = new ArrayList<Dto>();
            Dto sameTempDto = new BaseDto();
            for(Element element:elements)
            {
                elementChildToList(list,element);
                sameTempDto.put(element.getName(), element.getText());
            }
            arrayList.add(sameTempDto);
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
            requstDto.put("notifyUrl","http://api.szmpyd.com/api/notify/yizhi");
            requstDto.put("returnUrl","http://mobile.szmpyd.com/html/pay/notify/notify.html");
            requstDto.put("payId",payId);
            requstDto.put("smoney",10.58);
            //createUnionWapPay(requstDto);
            //requstDto.put("apiUrl",queryApiUrl);
            //requstDto.put("payId","CZ201811091332163000");
            //queryOrder(requstDto);
            //System.out.println(requstDto.getAsString("resp"));

            //代付
            requstDto = new BaseDto();
            payId = "TX" + DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME);//生成商户订单号
            payId += new Random().nextInt(10);
            requstDto.put("ymdmerchantNo",ymdmerchantNo);
            requstDto.put("appName","付款");
            requstDto.put("privateKey",privateKey);
            requstDto.put("ymdpublicKey",ymdpublicKey);
            requstDto.put("notifyUrl","http://api.szmpyd.com/api/notify/yizhi/df");

            List<Dto> detailList = new ArrayList<Dto>();
            Dto detailDto = new BaseDto();
            detailDto.put("payId",payId);
            detailDto.put("amount",10);
            detailDto.put("bankName","招商银行");
            detailDto.put("branchName","中远两湾城支行");
            detailDto.put("province","上海");
            detailDto.put("city","上海");
            detailDto.put("bankUserName","孙俊奇");
            detailDto.put("bankAccount","6214850210345952");
            detailList.add(detailDto);
            requstDto.put("details",detailList);
            transBatchBank(requstDto);
            //requstDto.put("payId","TX201807060951048034");
            //requstDto.put("beginTime","2018-07-06 09:50:00");
            //requstDto.put("endTime","2018-07-06 09:55:00");
            //queryOrder(requstDto);
            System.out.println(requstDto.getAsString("dmsg"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
