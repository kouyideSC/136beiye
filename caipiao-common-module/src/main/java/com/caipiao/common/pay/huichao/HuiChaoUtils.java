package com.caipiao.common.pay.huichao;

import com.caipiao.common.pay.huichao.utils.HTTPClientUtils;
import com.caipiao.common.pay.huichao.utils.RsaUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.*;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 汇潮支付(一麻袋)-工具类
 * @author  mcdog
 */
public class HuiChaoUtils
{
    private static final Logger logger = LoggerFactory.getLogger(HuiChaoUtils.class);
    public static final String merchantNo = "45375";//商户号
    public static final String accountName = "广博网络";//
    public static final String apiUrl = "https://gwapi.yemadai.com";//api地址
    public static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC4BZE9aPA2oOHiUhR6TzIgRy707+eemsDo/DnzD2n3vUNcO+hy+0rxgmJ+PPm36HVvjy63CLI3OQMJaKC0/Qe21MfLZAY78jI/NxIoM7XqHzxBg+/itzUgiUdlBRROZNKMPSBaFLFiFu6m8pcUBvreqnzM0aHbTCtcydojtmOD5wIDAQAB";//公钥
    public static final String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALgFkT1o8Dag4eJSFHpPMiBHLvTv556awOj8OfMPafe9Q1w76HL7SvGCYn48+bfodW+PLrcIsjc5AwlooLT9B7bUx8tkBjvyMj83EigzteofPEGD7+K3NSCJR2UFFE5k0ow9IFoUsWIW7qbylxQG+t6qfMzRodtMK1zJ2iO2Y4PnAgMBAAECgYEAp0uIasfH+iHwuQvdygPNkkKkkdC4RRxzXFxRYoMU10CcyHE+Nan2y/C5EgLlEyil+rG0ynmBa2rNM/SGhYOzShi/fFGT+MEfXjUtJDeJLXu3G6bnUWndkqIYibbk+fmshy81hQm6TXh0bDvOdUjjEajYmUpCIMAof7tfsBtHywECQQD8C2qJlfQCwDK9UV+QaWjBgHVVdiFqL3L+Gu+Me+jbzbx+Shk28fm6pyi998fbxXXTCUGeflqho5z7KOyUisrhAkEAuuje7q8A4TJs6M0INag6VBs47pz8rUGkIGlnmocr6cDm7ouBlBHomQtV6xW2CxoSQQVvT2nsAZOx1b3MZ9CvxwJAeOjFF/Gel/85mAZEUNOwVDtajj/YMcdHY8zqI7uBbohYp0DGrcwQ39C2w8Ls1mn4Zt+m4fB9a9NASGBOdcfLIQJBAJAomgE34xLN5Kgtsz5HUS2bjW6kkFJFBYSmJ21NAjaZPMQRv1Bn+6FG1+6oYS7w3dFekrqKdKfGtWuopuYPU/MCQBVMzhzyastpwLnBh1xOlRbtleCqxVe0+BcIZAW1TqXa7StWaefbukuT4jW3ELTAipg5ufPNsSkkxiTXpeqngoA=";//私钥
    public static final String ymdPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC5/7jc03bPbHEes+u8uHtw4liJTpdZxwdhTbC3C2kVCe0VB8DLzw4wrP8uicAbyWDfOqKI5sFcKydgVKcWAdzVzUQADa9NjVg5Fk0BDeLlMXoXQdydQqLCqHB048AT85uEzV/hZKNod7wQFGfUaimk5urH2v6x5HOdKsXuLR60dwIDAQAB";//一麻袋公钥

    /**
     * 汇潮支付-付款到银行账户
     * @author  mcdog
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
        String merchantNo = params.getAsString("merchantNo");
        if(StringUtil.isEmpty(merchantNo))
        {
            params.put("dmsg","商户号merchantNo不能为空");
            return;
        }
        //RSA私钥校验
        String privateKey = params.getAsString("privateKey");
        if(StringUtil.isEmpty(merchantNo))
        {
            params.put("dmsg","RSA私钥privateKey不能为空");
            return;
        }
        //一麻袋RSA公钥校验
        String ymdPublicKey = params.getAsString("ymdPublicKey");
        if(StringUtil.isEmpty(ymdPublicKey))
        {
            params.put("dmsg","一麻袋RSA公钥ymdPublicKey不能为空");
            return;
        }
        //通知地址校验
        String notifyUrl = params.getAsString("notifyUrl");//异步通知地址
        if(StringUtil.isEmpty(notifyUrl))
        {
            params.put("dmsg","通知地址notifyUrl不能为空");
            return;
        }

        /**
         * 设置请求参数
         */
        //设置基础参数
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        stringBuffer.append("<yemadai>");
        stringBuffer.append("<accountNumber>" + merchantNo + "</accountNumber>");//商户号
        stringBuffer.append("<signType>RSA</signType>");//签名类型
        stringBuffer.append("<notifyURL>" + notifyUrl + "</notifyURL>");//异步通知地址
        stringBuffer.append("<tt>" + (StringUtil.isEmpty(params.get("isjj"))? 0 : params.getAsInteger("isjj")) + "</tt>");//是否加急,0-普通 1-加急,默认为0

        //设置付款明细
        RsaUtils rsaUtils = RsaUtils.getInstance();
        List<Dto> details = params.getAsList("details");//提取付款明细
        if(details == null || details.size() == 0)
        {
            params.put("dmsg","付款明细details不能为空");
            return;
        }
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
            String plain ="transId=" + payId + "&accountNumber=" + merchantNo + "&cardNo=" + bankAccount + "&amount=" + String.format("%.2f",amount);
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
        logger.info("[汇潮支付-付款到银行账户]请求原始参数=" + stringBuffer.toString() + ",请求实际参数=" + transData);
        String httpPost = connect(paramList,apiUrl + "/transfer/transferFixed");
        logger.info("[汇潮支付-付款到银行账户]响应原始结果=" + httpPost);
        String xmlstr = new String(base64.decode(httpPost),"UTF-8");
        params.put("resp",xmlstr);
        logger.info("[汇潮支付-付款到银行账户]响应实际结果=" + xmlstr);

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
                if(rsaUtils.verifySignature(respSign,plain,ymdPublicKey))
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
     * 汇潮支付-代付订单查询
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
        //RSA私钥校验
        String privateKey = params.getAsString("privateKey");
        if(StringUtil.isEmpty(privateKey))
        {
            params.put("dmsg","RSA私钥privateKey不能为空");
            return;
        }
        //一麻袋RSA公钥校验
        String ymdPublicKey = params.getAsString("ymdPublicKey");
        if(StringUtil.isEmpty(ymdPublicKey))
        {
            params.put("dmsg","一麻袋RSA公钥ymdPublicKey不能为空");
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
        stringBuffer.append("<merchantNumber>" + merchantNo + "</merchantNumber>");//商户号
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
        String plain = merchantNo + "&" + requestTime;
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
        logger.info("[汇潮支付-订单查询]请求原始参数=" + stringBuffer.toString() + ",请求实际参数=" + transData);
        String httpPost = connect(paramList,apiUrl + "/transfer/transferQueryFixed");
        logger.info("[汇潮支付-订单查询]原始响应结果=" + httpPost);
        String xmlstr = new String(base64.decode(httpPost),"utf-8");
        params.put("resp",xmlstr);
        logger.info("[汇潮支付-订单查询]实际响应结果=" + xmlstr);

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
                Dto detailDto = detailList.get(0);
                //resultsDto.put("tradeNo",respDto.getAsString("order"));//设置渠道流水号

                //设置订单交易信息
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
            String payId = "TX" + DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME);//生成商户订单号
            payId += new Random().nextInt(10);
            requstDto.put("merchantNo",merchantNo);
            requstDto.put("appName","付款");
            requstDto.put("apiUrl",apiUrl);
            requstDto.put("privateKey",privateKey);
            requstDto.put("ymdPublicKey",ymdPublicKey);
            requstDto.put("notifyUrl","http://api.szmpyd.com/api/notify/huichao");

            List<Dto> detailList = new ArrayList<Dto>();
            Dto detailDto = new BaseDto();
            detailDto.put("payId",payId);
            detailDto.put("amount",2);
            detailDto.put("bankName","广东发展银行");
            detailDto.put("branchName","佛山支行");
            detailDto.put("province","广东");
            detailDto.put("city","佛山");
            detailDto.put("bankUserName","孙俊奇");
            detailDto.put("bankAccount","6214620421000167543");
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
