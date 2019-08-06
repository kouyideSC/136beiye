package com.caipiao.common.identity.juku;

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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * juku(巨酷)身份校验工具类
 * @author sjq
 */
public class JuKuUtils
{
    private static final Logger logger = LoggerFactory.getLogger(JuKuUtils.class);

    private static final String userName = "ayanzuiku";//巨酷数据用户名
    private static final String sdkey = "492eebbbd1b0a9ad6aedd4a47ac065ef";//巨酷数据分发给用户的密钥
    private static final String aucthreeitems_url = "http://api.suodui.cn/auc/aucthreeitems";//银行三要素鉴权地址
    private static final String aucfouritems_url = "http://api.suodui.cn/auc/aucfouritems";//银行四要素鉴权地址
    private static final String realnameverify_rul = "http://api.suodui.cn/auc/realnameverify";//实名认证地址

    /**
     * 银行三要素验证(真实姓名/身份证/卡号)
     * @author  mcdog
     * @param   params  参数对象
     *                  (必要参数:name-真实姓名 idcard-身份证号码 bankno-银行卡号 userip-用户机器的公网ip 非必要参数:requesttime-请求时间)
     *                  处理结果也保存在该对象中(dcode-验证状态 dmsg-验证状态描述),具体如下:
     *                  dcode=1000,验证通过,dcode != 1000,验证不通过
     */
    public static void aucThreeItems(Dto params) throws Exception
    {
        /**
         * 校验参数(真实姓名/身份证号码/银行卡号/服务器公网ip)
         */
        if(StringUtil.isEmpty(params.get("name"))
                || StringUtil.isEmpty(params.get("idcard"))
                || StringUtil.isEmpty(params.get("bankno"))
                || StringUtil.isEmpty(params.get("userip")))
        {
            logger.error("[银行三要素验证]参数校验不通过!接收原始参数:" + params.toString());
            params.put("dcode",-1000);
            params.put("dmsg","必要参数不能为空!");
            return;
        }

        /**
         * 初始化请求参数
         */
        if(StringUtil.isEmpty(params.get("username")))
        {
            params.put("username",userName);
        }
        if(StringUtil.isEmpty(params.get("requesttime")))
        {
            params.put("requesttime", DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME2));
        }
        //生成sign(顺序不能变)
        StringBuilder builder = new StringBuilder();
        builder.append(params.getAsString("username"));
        builder.append(params.getAsString("requesttime"));
        builder.append(params.getAsString("idcard"));
        builder.append(params.getAsString("bankno"));
        builder.append(params.getAsString("userip"));
        builder.append(sdkey);
        String sign = MD5.md5(builder.toString());//生成签名

        //拼装请求参数对象
        Dto requestDto = new BaseDto();
        requestDto.put("username",params.get("username"));
        requestDto.put("requesttime",params.get("requesttime"));
        requestDto.put("userip",params.get("userip"));
        requestDto.put("signmsg",sign);
        requestDto.put("truename",params.get("name"));
        requestDto.put("identityno",params.get("idcard"));
        requestDto.put("cardno",params.get("bankno"));

        /**
         * 发送请求
         */
        logger.info("[银行三要素验证]请求参数=" + requestDto.toString());
        String jsonStr = HttpClientUtil.callHttpPost_Dto(aucthreeitems_url,requestDto);
        logger.info("[银行三要素验证]响应结果=" + jsonStr);

        /**
         * 处理请求结果
         */
        Map respMap = JsonUtil.jsonToMap(jsonStr);
        if(respMap != null && respMap.size() > 0)
        {
            boolean success = (boolean)respMap.get("success");//提取请求状态码
            Object resultCode = respMap.get("resultCode");//提取验证状态码
            if(success && "0".equals(resultCode.toString()))
            {
                params.put("dcode",1000);
                params.put("dmsg",respMap.get("resultMsg"));
            }
            else
            {
                params.put("dcode",-1000);
                params.put("dmsg",respMap.get("resultMsg") == null? "验证失败" : respMap.get("resultMsg").toString());
            }
        }
    }

    /**
     * 银行四要素验证(真实姓名/身份证/卡号/手机号)
     * @author  mcdog
     * @param   params  参数对象
     *                  (必要参数:name-真实姓名 idcard-身份证号码 bankno-银行卡号 mobile-手机号 userip-用户机器的公网ip 非必要参数:requesttime-请求时间)
     *                  处理结果也保存在该对象中(dcode-验证状态 dmsg-验证状态描述),具体如下:
     *                  dcode=1000,验证通过,dcode != 1000,验证不通过
     */
    public static void aucFourItems(Dto params) throws Exception
    {
        /**
         * 校验参数(真实姓名/身份证号码/银行卡号/手机号/服务器公网ip)
         */
        if(StringUtil.isEmpty(params.get("name"))
                || StringUtil.isEmpty(params.get("idcard"))
                || StringUtil.isEmpty(params.get("bankno"))
                || StringUtil.isEmpty(params.get("mobile"))
                || StringUtil.isEmpty(params.get("userip")))
        {
            params.put("dcode",-1000);
            params.put("dmsg","必要参数不能为空!");
            return;
        }
        /**
         * 初始化请求参数
         */
        if(StringUtil.isEmpty(params.get("requesttime")))
        {
            params.put("requesttime", DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME2));
        }
        if(StringUtil.isEmpty(params.get("userip")))
        {
            params.put("userip","");
        }
        //生成sign(顺序不能变)
        StringBuilder builder = new StringBuilder();
        builder.append(params.getAsString("username"));
        builder.append(params.getAsString("requesttime"));
        builder.append(params.getAsString("idcard"));
        builder.append(params.getAsString("mobile"));
        builder.append(params.getAsString("bankno"));
        builder.append(params.getAsString("userip"));
        builder.append(sdkey);
        String sign = MD5.md5(builder.toString());//生成签名

        //拼装请求参数对象
        Dto requestDto = new BaseDto();
        requestDto.put("username",params.get("username"));
        requestDto.put("requesttime",params.get("requesttime"));
        requestDto.put("userip",params.get("userip"));
        requestDto.put("signmsg",sign);
        requestDto.put("truename",params.get("name"));
        requestDto.put("identityno",params.get("idcard"));
        requestDto.put("cardno",params.get("bankno"));
        requestDto.put("mobile",params.get("mobile"));

        /**
         * 发送请求
         */
        logger.info("[银行四要素验证]请求参数=" + requestDto.toString());
        String jsonStr = HttpClientUtil.callHttpPost_Dto(aucfouritems_url,requestDto);
        logger.info("[银行四要素验证]响应结果=" + jsonStr);

        /**
         * 处理请求结果
         */
        Map respMap = JsonUtil.jsonToMap(jsonStr);
        if(respMap != null && respMap.size() > 0)
        {
            boolean success = (boolean)respMap.get("success");//提取请求状态码
            Object resultCode = respMap.get("resultCode");//提取验证状态码
            if(success && "0".equals(resultCode.toString()))
            {
                params.put("dcode",1000);
                params.put("dmsg",respMap.get("resultMsg"));
            }
            else
            {
                params.put("dcode",-1000);
                params.put("dmsg",respMap.get("resultMsg") == null? "验证失败" : respMap.get("resultMsg").toString());
            }
        }
    }

    /**
     * 实名认证(真实姓名/身份证)
     * @author  mcdog
     * @param   params  参数对象
     *                  (必要参数:name-真实姓名 idcard-身份证号码 userip-用户机器的公网ip 非必要参数:requesttime-请求时间)
     *                  处理结果也保存在该对象中(dcode-验证状态 dmsg-验证状态描述),具体如下:
     *                  dcode=1000,验证通过,dcode != 1000,验证不通过
     */
    public static void realnameVerify(Dto params) throws Exception
    {
        /**
         * 校验参数(真实姓名/身份证号码/银行卡号/服务器公网ip)
         */
        if(StringUtil.isEmpty(params.get("name"))
                || StringUtil.isEmpty(params.get("idcard"))
                || StringUtil.isEmpty(params.get("userip")))
        {
            logger.error("[实名认证]参数校验不通过!接收原始参数:" + params.toString());
            params.put("dcode",-1000);
            params.put("dmsg","必要参数不能为空!");
            return;
        }

        /**
         * 初始化请求参数
         */
        if(StringUtil.isEmpty(params.get("username")))
        {
            params.put("username",userName);
        }
        if(StringUtil.isEmpty(params.get("requesttime")))
        {
            params.put("requesttime", DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME2));
        }
        //生成sign(顺序不能变)
        StringBuilder builder = new StringBuilder();
        builder.append(params.getAsString("username"));
        builder.append(params.getAsString("requesttime"));
        builder.append(params.getAsString("idcard"));
        builder.append(params.getAsString("userip"));
        builder.append(sdkey);
        String sign = MD5.md5(builder.toString());//生成签名

        //拼装请求参数对象
        Dto requestDto = new BaseDto();
        requestDto.put("username",params.get("username"));
        requestDto.put("requesttime",params.get("requesttime"));
        requestDto.put("userip",params.get("userip"));
        requestDto.put("signmsg",sign);
        requestDto.put("truename",params.get("name"));
        requestDto.put("identityno",params.get("idcard"));

        /**
         * 发送请求
         */
        logger.info("[实名认证]请求参数=" + requestDto.toString());
        String jsonStr = HttpClientUtil.callHttpPost_Dto(realnameverify_rul,requestDto);
        logger.info("[实名认证]响应结果=" + jsonStr);

        /**
         * 处理请求结果
         */
        Map respMap = JsonUtil.jsonToMap(jsonStr);
        if(respMap != null && respMap.size() > 0)
        {
            boolean success = (boolean)respMap.get("success");//提取请求状态码
            Object resultCode = respMap.get("resultCode");//提取验证状态码
            if(success && "0".equals(resultCode.toString()))
            {
                params.put("dcode",1000);
                params.put("dmsg",respMap.get("resultMsg"));
            }
            else
            {
                params.put("dcode",-1000);
                params.put("dmsg",respMap.get("resultMsg") == null? "验证失败" : respMap.get("resultMsg").toString());
            }
        }
    }

    public static void main(String[] args)
    {
        try
        {
            Dto params = new BaseDto();
            params.put("name","");
            params.put("idcard","");
            //params.put("mobile","13524944828");
            params.put("userip","");
            //params.put("bankno","6217000140013805450");
            realnameVerify(params);
            //aucThreeItems(params);
            System.out.println(params.getAsString("dmsg"));
        }
       catch (Exception e)
       {
           e.printStackTrace();
       }
    }
}
