package com.caipiao.common.shuangqian;

import com.caipiao.common.pay.huichao.utils.HTTPClientUtils;
import com.caipiao.common.pay.huichao.utils.RsaUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.HttpClientUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 双乾支付-工具类
 * @author  kouyi
 */
public class ShuangQianUtils
{
    private static final Logger logger = LoggerFactory.getLogger(ShuangQianUtils.class);
    public static final String merchantNo = "202453";//商户号
    public static final String apiUrl = "https://df.95epay.cn/merchant/numberPaid.action";//代付地址
    public static final String queryUrl = "https://df.95epay.cn/merchant/numberPaidQuery.action";//查询地址
    public static final String pfxPath = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAOgyHMPBygAHuaU4mAIuGASWMbsg\n" +
            "5BXBBwqf9iXCy0eFEZTRW6RphAHAHxOaxR5h7RwBngDWHs4M90YKOp4J2eFBM68Tq57MXq4HHQ43\n" +
            "S8YW0WoKkd0S/S8rFTzXR+BtHQm4GZhozM0osafA+reohNHsJ4WCW3ettp3Ovobyn5+1AgMBAAEC\n" +
            "gYEAzsOpF6yea051wynVOsO/AX6ZhFAyqyh7U7vD3jZK/EIDuOiBWbPYKBLj2XpNo9OB6BmMkKQb\n" +
            "RFMJcqYzMn4FND2zlvjMUWRw3//HIyNkkxBNXaxg/R2CLXV7vJ7AM4nZqrGa9QsWJD8oYlNANlKO\n" +
            "aGY2BC+zU/ymbzeH2PsjDdUCQQD2t0z1XFlRYHz+iw3B9xnXYSU1BMhaTIHxjYuaNLyVqpuEPRKX\n" +
            "7+U848IL6E4gEZadV8VULEd6Tq9moRm/RusDAkEA8O7u3dDkNS2pbkYk2zmUNaVLcis4uY2TLIgV\n" +
            "EU9eCNgUaUZSPw8VUTChM+aI12tsc/yKfBobglU9iZfwVtsw5wJAJUe3ck/9EyXgXVpHvuqG1hvQ\n" +
            "0n0hhTuQWx7HXIrhgEAidc4AtELEwfmshb4ZqBDgUHBF2h1Cw8GGEZ0AVf3yXwJBANIJqgmL6vaF\n" +
            "KRiuY8EN8CvYPu53yYrRZkVDGQKvKy4QmbKxEFBS6OWPvGjPh+oshy6SMXRBGkxD3VLPN0Gw8m8C\n" +
            "QQDfD2xSpTWBCuhl2EjbVrm5U9gGNWiPatX0furliEPEsWHNFNP3Q6NtKts5NKrsndMuWmyCCkMh\n" +
            "n5mHllq7Qisa";//商户私钥
    public static final String passWord = "szmpyd"; //商户私钥证书密码
    public static final String certPath = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCR2eKff00xJwM21K4z7JyWx1RvovJCo7hjxyIf\n" +
            "ULbUteyw3eH+/x9JEbug4ck/xw6oRsXPXMq8EGHAGMuU7mgkMU++rFhXn9Q33qOCT5smXAfy472q\n" +
            "QQuhuDJQOKJUMRC6d+A33QXH0no9hjR+7gYSDzdiqNxmGfftQW7QDIvoLwIDAQAB";//双乾公钥

    /**
     * 双乾支付-付款到银行账户
     * @author  kouyi
     * @param   params  参数对象,付款到银行账户结果也保存在该对象中(dcode-付款申请状态 dmsg-付款申请状态描述 resp-原始的付款响应字符串),具体如下:
     *                  dcode=1000,付款成功
     *                  dcode=-1000,付款失败
     *                  dcode=1001,付款失败(签名错误)
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
        //商户号校验
        if(StringUtil.isEmpty(merchantNo))
        {
            params.put("dmsg","商户号merchantNo不能为空");
            return;
        }
        //订单金额校验
        String amountStr = params.getAsString("totalAmount");
        Double amount = params.getAsDouble("totalAmount");//获取订金额
        if(amount == null || amount.doubleValue() <= 0)
        {
            params.put("dmsg","订单总金额amount不合法,金额不能为空且必须大于0");
            return;
        }
        else if(amountStr.indexOf(".") > 0 && amountStr.substring(amountStr.indexOf(".")).length() > 3)
        {
            params.put("dmsg","订单总金额amount不合法,金额最多只能有2位小数");
            return;
        }
        //商户订单号校验
        String payId = params.getAsString("batchNo");//获取商户流水号(订单号)
        if(StringUtil.isEmpty(payId))
        {
            params.put("dmsg","商户订单号payId不能为空");
            return;
        }
        //收款方银行编号校验
        String bankCode = params.getAsString("bankCode");//获取收款方银行编号
        if(StringUtil.isEmpty(bankCode))
        {
            params.put("dmsg","收款方银行编号bankCode不能为空");
            return;
        }
        //收款人校验
        String bankUserName = params.getAsString("bankUserName");
        if(StringUtil.isEmpty(bankUserName))
        {
            params.put("dmsg","收款方账户名bankUserName不能为空");
            return;
        }
        //收款银行账号校验
        String bankAccount = params.getAsString("bankAccount");
        if(StringUtil.isEmpty(bankAccount))
        {
            params.put("dmsg","收款方银行账号bankAccount不能为空");
            return;
        }

        String id = params.getAsString("id");
        Map<String, String> comParam = new LinkedHashMap<String, String>();;//提交参数
        comParam.put("merno", merchantNo);//设置商户号
        comParam.put("time", DateUtil.formatDate(new Date(), DateUtil.LOG_DATE_TIME2));//设置发起时间
        comParam.put("totalAmount", amountStr);//设置订单金额
        comParam.put("num", "1");//设置笔数默认1
        comParam.put("batchNo", payId + "-" + id);//设置批次号

        String cityCode = params.getAsString("bankCityCode");
        StringBuffer content = new StringBuffer();
        content.append(bankUserName).append("|");
        content.append(bankCode).append("|");
        content.append(bankAccount).append("|");
        content.append("1").append("|");
        content.append(amountStr).append("|");
        content.append(payId).append("|");
        content.append(StringUtil.isEmpty(cityCode) ? "000" : cityCode).append("|");
        content.append(payId);
        comParam.put("content", content.toString());//设置内容体

        String sign = RsaHelper.signData(joinMapValue(comParam, '&'), pfxPath);
        comParam.put("remark", payId + "-" + id);//设置批次备注
        comParam.put("signature", sign);

        /**
         * 发送请求
         */
        logger.info("[双乾支付-付款到银行账户]请求原始参数=" + comParam.toString());
        String response = HttpClientUtil.callHttpPost_Map(apiUrl, comParam);
        logger.info("[双乾支付-付款到银行账户]响应原始结果=" + response);
        comParam.clear();
        /**
         * 处理请求结果
         */
        JSONObject responseJson = JSONObject.fromObject(response);
        comParam = responseMapData(responseJson, comParam);
        String beforeSignedData = joinMapValue(comParam, '&');
        String shSign = responseJson.getString("signature");
        logger.info("[双乾支付-付款到银行账户]响应签名参数为=" + shSign);
        if(RsaHelper.verifySignature(shSign, beforeSignedData, certPath)) {//验签
            String errCode = responseJson.getString("status");//提取错误码
            if("success".equals(errCode))
            {
                params.put("dcode",1000);
                params.put("dmsg","付款请求发送成功");
            }
            else
            {
                params.put("dcode",-1000);
                params.put("dmsg","付款请求失败!错误描述=" + responseJson.getString("remark"));
            }
        } else {
            params.put("dcode",-1000);
            params.put("dmsg","付款响应数据签名验证失败!");
        }
    }

    /**
     * 双乾支付-代付订单查询
     * @author  kouyi
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
         * 参数校验
         */
        //非空校验
        if(params == null || params.size() == 0)
        {
            params.put("dmsg","参数不能为空");
            return;
        }
        //商户号校验
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

        String id = params.getAsString("id");
        Map<String, String> comParam = new LinkedHashMap<String, String>();;//提交参数
        comParam.put("merno", merchantNo);//设置商户号
        comParam.put("batchNo", payId + "-" + id);//设置批次号
        comParam.put("orderNo", payId);//设置订单号

        String sign = RsaHelper.signData(joinMapValue(comParam, '&'), pfxPath);
        comParam.put("signature", sign);

        /**
         * 发送请求
         */
        logger.info("[双乾支付-订单查询]请求原始参数=" + comParam.toString());
        String response = HttpClientUtil.callHttpPost_Map(queryUrl, comParam);
        logger.info("[双乾支付-订单查询]响应原始结果=" + response);
        comParam.clear();

        /**
         * 处理请求结果
         */
        JSONObject responseJson = JSONObject.fromObject(response);
        //批次状态-成功
        if(responseJson.getString("searchResult").equals("success")) {
            //设置响应结果
            params.put("dcode",1000);//设置查询状态码
            params.put("dmsg","success");//设置查询状态码描述

            Dto resultsDto = new BaseDto();
            comParam = responseMapData(responseJson, comParam);
            String beforeSignedData = joinMapValue(comParam, '&');
            String shSign = responseJson.getString("signature");
            logger.info("[双乾支付-订单查询]响应签名参数为=" + shSign);
            if(RsaHelper.verifySignature(shSign, beforeSignedData, certPath)) {//验签
                String errCode = responseJson.getString("state");//提取批次状态
                logger.info("[双乾支付-订单查询]返回批次状态为=" + errCode);
                //解析单笔订单数据
                JSONArray contents = responseJson.getJSONArray("contents");
                if(contents.size() > 0) {
                    boolean isDo = false;//订单是否处理
                    for(int index = 0; index < contents.size(); index++) {
                        JSONObject jobject = contents.getJSONObject(index);
                        String orderId = jobject.getString("orderNumber");
                        if(orderId.equals(payId)) {
                            params.put("tradeNo", responseJson.getString("batchNo"));//设置渠道流水号
                            errCode = jobject.getString("state");//提取批次状态
                            logger.info("[双乾支付-订单查询]响应订单号(" + orderId + ")的状态为=" + errCode);
                            if("4".equals(errCode))
                            {
                                resultsDto.put("status",1000);//设置交易状态为成功
                                resultsDto.put("msg","success");
                            }
                            else if("3".equals(errCode))
                            {
                                resultsDto.put("status",-1000);//设置交易状态为交易退回
                                resultsDto.put("msg", "交易失败-已退回");//设置交易状态描述
                            } else {
                                resultsDto.put("status",1001);//设置交易状态为交易中
                                resultsDto.put("msg", "交易中");//设置交易状态描述
                            }
                            isDo = true;
                            break;
                        }
                    }
                    if(!isDo) {
                        resultsDto.put("status",1001);//设置交易状态为交易中
                        resultsDto.put("msg","提取不到有效的订单详细信息!订单详细为空!");
                    }
                }
            } else {
                resultsDto.put("status",-1000);//设置交易状态为交易中
                resultsDto.put("msg", "响应数据签名验证失败");//设置交易状态描述
            }
            params.put("results",resultsDto);//设置订单交易结果
        } else {
            params.put("dcode",-1000);
            params.put("dmsg","订单查询-返回失败!原因为" + responseJson.getString("remark"));
        }
    }

    /**
     * 签名前的字符串
     * @param map
     * @param connector
     * @return
     */
    public static String joinMapValue(Map<String, String> map, char connector)	{
        StringBuffer b = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()){
            b.append(entry.getKey());
            b.append('=');
            if (entry.getValue() != null){
                b.append(entry.getValue());
            }
            b.append(connector);
        }
        return b.toString().substring(0, b.length()-1);
    }

    /**
     * 格式化响应数据
     * @param responseJson
     * @param comParam
     * @return
     */
    public static Map responseMapData(JSONObject responseJson, Map<String, String> comParam) {
        Iterator<?> it = responseJson.keys();
        // 遍历jsonObject数据，添加到Map对象
        while(it.hasNext()){
            String key = String.valueOf(it.next());
            String value = responseJson.getString(key);
            if(key.equals("signature") || key.equals("remark")){
                continue;
            }
            comParam.put(key, value);
        }
        return comParam;
    }

}
