package com.caipiao.common.pay.shengpay;

import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.pay.PayUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.HttpClientUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * shengpay(盛付通)工具类
 */
public class ShengpayUtils
{
    private static final Logger logger = LoggerFactory.getLogger(ShengpayUtils.class);

    /**
     * 商户号
     */
    private static final String partnerId = "11477659";
    /**
     * RSA公钥
     */
    public static final String publicRsaKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnbc5ppHrxyhYJZ7oNkVrzd789OIS9Al4bukLBiwuvUnkMcsY6bKva8O0ekcDNPpZHhiwjE6oD7jz6ZMIq7zLgynJg+1AgfMZLVKnhVDn7LOx/Nx8zvExzVBwGAASlm+AvfH1zzl6ju/KcMVls64EjSD/76nVtCsf14E+sqQDIbatkZu78miNuE3g1voySBKRvt4glWtyDNT5VRhthTr+FkHaRpgZlB0C48e7tMbaH50j0Nl46wRuCxh6M5wPMeQUGQhXkoCO2etM1Ibz7xbrttr4SL8LwiDIoaz4u48GAB7IDtCiSocR/aM3TyuFW6bJAESaTHwpp3xnm6zVVoCxfQIDAQAB";
    /**
     * RSA私钥
     */
    public static final String privateRsaKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCdtzmmkevHKFglnug2RWvN3vz04hL0CXhu6QsGLC69SeQxyxjpsq9rw7R6RwM0+lkeGLCMTqgPuPPpkwirvMuDKcmD7UCB8xktUqeFUOfss7H83HzO8THNUHAYABKWb4C98fXPOXqO78pwxWWzrgSNIP/vqdW0Kx/XgT6ypAMhtq2Rm7vyaI24TeDW+jJIEpG+3iCVa3IM1PlVGG2FOv4WQdpGmBmUHQLjx7u0xtofnSPQ2XjrBG4LGHoznA8x5BQZCFeSgI7Z60zUhvPvFuu22vhIvwvCIMihrPi7jwYAHsgO0KJKhxH9ozdPK4VbpskARJpMfCmnfGebrNVWgLF9AgMBAAECggEBAJD+sQ61GpmHOrqjhALZAcpMFiygZyJaTpFQyKTNG89ETnmEqYu4jdO6IYNFI+qhuiqUMkNb0OBbfkqftZoFwOf+E9io1uRhdSUY1xAWmJIhhiHdJxUt40ZJjDRUZMJPEoIOH5l603TzeR8CK4TsGB0TWJiif+jauLRsixK6/HIRx4O2oO9uAo+X8HMKiRcX3PF4Vu0wKhtMHcTyBHNvztEbLr+2HePfp9ct0rjqHoSUH2qaQVm4KK+cqAONVDCbv9AxyJm6wLLvNtyejUqTPCCWOA6O3zVNqcidpOrd07zimXYMMWkk/tUI63Fwuu+/kFWEPyJOPXCY/Ev9B6nbqLUCgYEA4DM9XcEZCkz7P1ZwGDvMP7mz/kSzoHENXEPnl0NZ7M9PtA5ipy3VpDRLHaZqN4OYB/HdCJpQrn3XdDJB2HeqItEfpWXKlrLbAnK4P2Yz3v+obm2aIavYRtX7gVrtXHP1DFaOpZSo5V9XmEmqecL04k020SY6vCgHJoNbtG5wASsCgYEAtBXrj2PqRmk8BKD766bQyqhVZhmSP7MYwjPD0TMAsN/Xn8OJ/SA6rxNCvUpzSu47uWIE4aim7GH/ssXZdfBAUpJ//uP1tIUibPos2kBoNhIijkEsU1GQd0QHTe59hvMzk5T8JFR0vyMTBzUBhqRw5IyU+FBk/fX950GrhEacM/cCgYBbTWKjSJzq7fivjYLuy4NdPEIJvW6tWvzG4zxwBFSdtLtPp0ATtv229jjU5U2COAv0yFFsTHOo68SPJFxeTDU0IpfRooeDIWlBMPjJkNR07wZnmijXp4TXLBNVVXMuYGad1K4YzlaNumsQsIdFONjDRBCb0ga/U0m+LGZvLz5bqwKBgAlfdrIw3hmqEoCfLF14gjd6CW/V9uGv3aMF9LQntmp+TAe5sq3lBojkNL4LaPLGcX9VlbF1CLMW6qsQVyAnUAG8NEM89CXgJAmN/9WgMoxV50yGIdt4TE+Yz9SDjC8A63dL8s+lQapNFgly/dBXMLUEiCBgdtEtA3kM1vN8hVbDAoGBAIWvLp9S94v4T0OKhlm13VdmXOGwkimEOZNj3pFNcCf2LhZEVycF+J53BTadCUobtu1G9szdPKX5aumC82UnJaRTxl/zuxGTUo4D3jGOWivuPdfBZWRdKAM6qfnxhq9ZDlRMWDSMiZl5PJSYv71vLivifQMjxBvywpCRKsAAcwJ3";
    /**
     * MD5密钥
     */
    public static final String md5Key = "8ec831dd6af00f1c18";
    /**
     *批量付款到银行账户-付款请求地址
     */
    public static final String transBatchBankUrl = "https://bapi.shengpay.com/statement/api/batchPayment/transBatchBank";
    /**
     *批量付款到银行账户-批次状态查询请求地址
     */
    public static final String batchBankStatusUrl = "https://bapi.shengpay.com/statement/api/batchPayment/getBatchBankStatus";
    /**
     *批量付款到银行账户-多笔明细查询请求地址
     */
    public static final String batchBankUrl = "https://bapi.shengpay.com/statement/api/batchPayment/getBatchBank";

    public static final String resultCode_success = "00";//业务状态码-成功
    public static final String resultCode_signFail = "03";//业务状态码-签名失败
    public static final String resultCode_needRecharge = "11";//业务状态码-账户余额不足

    public  static final String payStatusCode_cszt = "00";//单笔付款状态-初始状态
    public  static final String payStatusCode_fkz = "01";//单笔付款状态-付款中
    public  static final String payStatusCode_fkcg = "03";//单笔付款状态-付款成功
    public  static final String payStatusCode_fksb = "04";//单笔付款状态-付款失败
    public  static final String payStatusCode_zfwz = "05";//单笔付款状态-支付未知
    public  static final String payStatusCode_ytp = "06";//单笔付款状态-已退票
    public  static final String payStatusCode_jjbcz = "07";//单笔付款状态-请求交易不存在
    public  static final String payStatusCode_mxcf = "08";//单笔付款状态-明细查询数据重复

    /**
     * 付款到银行账户
     * @author  mcdog
     * @param   params  参数对象,付款到银行账户结果也保存在该对象中(dcode-付款申请状态 dmsg-付款申请状态描述 resp-原始的付款响应字符串),具体如下:
     *                  dcode=1000,付款成功
     *                  dcode=-1000,付款失败
     *                  dcode=1001,付款失败(签名错误)
     *                  dcode=1002,付款失败(账户余额不足)
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
        //批次号校验
        String batchNo = params.getAsString("batchNo");//获取批次号
        if(StringUtil.isEmpty(batchNo))
        {
            params.put("dmsg","批次号batchNo不能为空");
            return;
        }
        else if(batchNo.length() < 11 || batchNo.length() > 32)
        {
            params.put("dmsg","批次号batchNo长度不合法,长度范围应为11-32位");
            return;
        }
        //订单总金额校验
        String totalAmountStr = params.getAsString("totalAmount");
        Double totalAmount = params.getAsDouble("totalAmount");//获取订单总金额
        if(totalAmount == null || totalAmount.doubleValue() <= 0)
        {
            params.put("dmsg","订单总金额totalAmount不合法,金额不能为空且必须大于0");
            return;
        }
        else if(totalAmountStr.indexOf(".") > 0 && totalAmountStr.substring(totalAmountStr.indexOf(".")).length() > 3)
        {
            params.put("dmsg","订单总金额totalAmount不合法,金额最多只能有2位小数");
            return;
        }
        //通知地址校验
        if(StringUtil.isEmpty(params.get("callbackUrl")))
        {
            params.put("dmsg","通知地址callbackUrl不能为空");
            return;
        }

        /**
         * 设置请求参数
         */
        //设置基础参数
        Dto requstDto = new BaseDto();
        requstDto.put("batchNo",batchNo);//设置付款请求批次号
        requstDto.put("callbackUrl",params.get("callbackUrl"));//设置回调地址(流程达到最终状态后通知商户的地址)
        requstDto.put("totalAmount",String.format("%.2f",totalAmount));//设置本批次支付的总金额
        requstDto.put("customerNo",partnerId);//设置商户号
        requstDto.put("charset","utf-8");//设置字符集
        requstDto.put("signType","MD5");//设置签名方式
        requstDto.put("remark",StringUtil.isEmpty(params.get("remark"))? "付款" : params.get("remark"));

        //设置付款明细
        List<Dto> detailList = new ArrayList<Dto>();
        List<Dto> details = params.getAsList("details");//提取付款明细
        double realTotalAmount = 0d;
        for(Dto detailDto : details)
        {
            /**
             * 校验参数
             */
            //单笔付款明细商户流水号(订单号)校验
            String payId = detailDto.getAsString("payId");//获取商户流水号(订单号)
            if(StringUtil.isEmpty(payId))
            {
                params.put("dmsg","单笔付款明细商户流水号(订单号)payId不能为空");
                return;
            }
            else if(payId.length() > 20)
            {
                params.put("dmsg","单笔付款明细商户流水号(订单号)payId长度不合法,长度不能超过20位");
                return;
            }
            //单笔付款明细收款银行校验
            if(StringUtil.isEmpty(detailDto.get("bankName")))
            {
                params.put("dmsg","单笔付款明细收款银行不能为空");
                return;
            }
            //单笔付款明细收款人账户类型校验
            String accountType = detailDto.getAsString("accountType");
            if(StringUtil.isEmpty(accountType))
            {
                params.put("dmsg","单笔付款明细收款人账户类型不能为空");
                return;
            }
            else if(!accountType.startsWith("C") && !accountType.startsWith("B"))
            {
                params.put("dmsg","单笔付款明细收款人账户类型不合法,类型只能为C(个人)或者B(企业)");
                return;
            }
            //单笔付款明细收款人户名校验
            if(StringUtil.isEmpty(detailDto.get("bankUserName")))
            {
                params.put("dmsg","单笔付款明细收款人户名不能为空");
                return;
            }
            //单笔付款明细款方银行账号校验
            if(StringUtil.isEmpty(detailDto.get("bankAccount")))
            {
                params.put("dmsg","单笔付款明细收款方银行账号不能为空");
                return;
            }
            //单笔付款明细付款金额校验
            String amountStr = detailDto.getAsString("amount");
            Double amount = detailDto.getAsDouble("amount");
            realTotalAmount += amount;
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
            //设置单笔付款明细
            Dto detailData = new BaseDto();
            detailData.put("id",payId);//设置单笔付款明细商户流水号(订单号)
            detailData.put("province",StringUtil.isEmpty(detailDto.get("province"))? "" : detailDto.get("province"));//设置单笔付款明细-银行所在省份
            detailData.put("city",StringUtil.isEmpty(detailDto.get("city"))? "" : detailDto.get("city"));//设置单笔付款明细-银行所在城市
            detailData.put("branchName",StringUtil.isEmpty(detailDto.get("branchName"))? "" : detailDto.get("branchName"));//设置单笔付款明细-银行支行名称
            detailData.put("bankName",detailDto.get("bankName"));//设置单笔付款明细银行名称
            detailData.put("accountType",accountType);//设置单笔付款明细收款人账户类型(C-个人 B-企业)
            detailData.put("bankUserName",detailDto.get("bankUserName"));//设置单笔付款明细收款方户名
            detailData.put("bankAccount",detailDto.get("bankAccount"));//设置单笔付款明细收款方银行账号
            detailData.put("amount",String.format("%.2f",amount));//设置单笔付款明细付款金额(单位:元)
            detailData.put("remark",StringUtil.isEmpty(detailDto.get("remark"))? "付款" : detailDto.get("remark"));//设置单笔付款明细备注
            detailList.add(detailData);
        }
        requstDto.put("details",detailList);//设置付款明细

        //校验付款明细实际订单总金额是否和传入的订单总金额相等
        if(realTotalAmount != totalAmount)
        {
            params.put("dmsg","订单总金额与实际的订单总金额不符,实际总金额:" + realTotalAmount + ",订单总金额:" + totalAmount);
            return;
        }

        //拼接签名原始字符串(顺序不能变)
        StringBuilder signBuilder = new StringBuilder();
        signBuilder.append(requstDto.getAsString("charset"));
        signBuilder.append(requstDto.getAsString("signType"));
        signBuilder.append(requstDto.getAsString("customerNo"));
        signBuilder.append(requstDto.getAsString("batchNo"));
        signBuilder.append(requstDto.getAsString("callbackUrl"));
        signBuilder.append(requstDto.getAsString("totalAmount"));
        for(Dto detail : detailList)
        {
            signBuilder.append(detail.getAsString("id"));
            signBuilder.append(detail.getAsString("province"));
            signBuilder.append(detail.getAsString("city"));
            signBuilder.append(detail.getAsString("branchName"));
            signBuilder.append(detail.getAsString("bankName"));
            signBuilder.append(detail.getAsString("accountType"));
            signBuilder.append(detail.getAsString("bankUserName"));
            signBuilder.append(detail.getAsString("bankAccount"));
            signBuilder.append(detail.getAsString("amount"));
            signBuilder.append(detail.getAsString("remark"));
        }

        //设置签名
        signBuilder.append(md5Key);
        String sign = MD5.md5(signBuilder.toString()).toUpperCase();
        requstDto.put("sign",sign);

        /**
         * 发送请求
         */
        String reqJson = requstDto.toJson();//将参数转换为json形式的字符串
        logger.info("[盛付通-付款到银行账户]请求参数=" + reqJson);
        String resp = HttpClientUtil.callHttpsPost_jsonString(transBatchBankUrl,reqJson);
        logger.info("[盛付通-付款到银行账户]响应结果=" + resp);
        params.put("resp",resp);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(resp);
        String resultCode = respDto.getAsString("resultCode");//提取付款申请状态码
        if(resultCode_success.equals(resultCode))
        {
            //拼接结果签名原始字符串(顺序不能变)
            String realSignString = "batchNo=" + respDto.getAsString("batchNo");
            realSignString += "resultCode=" + respDto.getAsString("resultCode");
            realSignString += "resultMessage=" + respDto.getAsString("resultMessage");
            String realSign = MD5.md5(realSignString + md5Key).toUpperCase();
            if(respDto.getAsString("sign").equals(realSign))
            {
                params.put("dcode",1000);
                params.put("dmsg","付款请求发送成功");
            }
            else
            {
                params.put("dcode",1001);
                params.put("dmsg","申请结果签名校验不通过");
            }

        }
        //签名错误
        else if(resultCode_signFail.equals(resultCode))
        {
            params.put("dcode",1001);
            params.put("dmsg","签名错误");
        }
        //账户余额不足
        else if(resultCode_needRecharge.equals(resultCode))
        {
            params.put("dcode",1002);
            params.put("dmsg","账户余额不足,请充值");
        }
        //其它不成功的情况
        else
        {
            params.put("dcode",-1000);
            params.put("dmsg",respDto.getAsString("resultMessage"));
        }
    }

    public static void main(String[] args)
    {
        try
        {
            //{"accountHolder":"王超","bankCard":"6223093310800068382","bankCity":"杭州市","bankCityCode":"3310",
            // "bankCode":"03160000","bankName":"浙商银行","bankProvince":"浙江","bankProvinceCode":"33",
            // "logo":"/image/bank/icon/czb.png","subBankName":"浙商银行杭州市分行"}
            String batchNo = "B" + DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME);
            String payId = "F" + DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME);
            Dto params = new BaseDto();
            params.put("batchNo","BTX201806230928016767");//设置付款请求批次号
            params.put("callbackUrl","http://api.szmpyd.com/api/shengpay");//设置回调地址(流程达到最终状态后通知商户的地址)
            params.put("totalAmount",String.format("%.2f",30d));//设置本批次支付的总金额
            params.put("remark","测试");
            List<Dto> details = new ArrayList<Dto>();
            Dto detailData = new BaseDto();
            detailData.put("payId","TX201806230928016767");//设置单笔付款明细商户流水号(订单号)
            detailData.put("province","浙江");//设置单笔付款明细银行所在省份
            detailData.put("city","杭州市");//设置单笔付款明细银行所在城市
            detailData.put("branchName","浙商银行杭州市分行");//设置单笔付款明细银行支行名称
            detailData.put("bankName","浙商银行");//设置单笔付款明细银行名称
            detailData.put("accountType","C(个人)");//设置单笔付款明细收款人账户类型(C-个人 B-企业)
            detailData.put("bankUserName","王超");//设置单笔付款明细收款方户名
            detailData.put("bankAccount","6223093310800068382");//设置单笔付款明细收款方银行账号
            detailData.put("amount",30);//设置单笔付款明细付款金额(单位:元)
            detailData.put("remark","测试");
            details.add(detailData);
            params.put("details",details);
            transBatchBank(params);
            System.out.println("resp:" + params.getAsString("resp"));
            System.out.println("dcode:" + params.getAsString("dcode"));
            System.out.println("dmsg:" + params.getAsString("dmsg"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
