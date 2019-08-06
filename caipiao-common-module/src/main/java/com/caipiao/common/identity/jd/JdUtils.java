package com.caipiao.common.identity.jd;

import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.HttpClientUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

/**
 * 京东万象身份校验工具类
 * @author sjq
 */
public class JdUtils
{
    private static final Logger logger = LoggerFactory.getLogger(JdUtils.class);

    private static final String appkey = "74a3bc213abc3e569af643f1353f12fa";//京东万象分发给用户的密钥
    private static final String auc_url = "https://way.jd.com/yingyan/verifi";//银行二/三/四要素鉴权地址
    private static final String realnameverify_rul = "https://way.jd.com/freedt/api_rest_police_identity";//实名认证地址
    private static final String realnameverify_rul_2 = "https://way.jd.com/yingyan/idcard";//实名认证地址

    /**
     * 银行三要素验证(真实姓名/身份证/卡号)
     * @author  mcdog
     * @param   params  参数对象
     *                  (name-真实姓名 idcard-身份证号码 bankno-银行卡号 banknum-银行编号 abbreviation-银行简称)
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
                || (StringUtil.isEmpty(params.get("banknum")) && StringUtil.isEmpty(params.get("abbreviation"))))
        {
            logger.error("[京东万象-银行三要素验证]参数校验不通过!接收原始参数=" + params.toString());
            params.put("dcode",-1000);
            params.put("dmsg","必要参数不能为空!");
            return;
        }

        /**
         * 初始化请求参数
         */
        //拼装请求参数对象
        Dto requestDto = new BaseDto();
        requestDto.put("bankcard",params.get("bankno"));
        requestDto.put("realName",params.get("name"));
        requestDto.put("cardNo",params.get("idcard"));
        requestDto.put("appkey",appkey);

        /**
         * 发送请求
         */
        logger.info("[京东万象-银行三要素验证]请求参数=" + requestDto.toString());
        String jsonStr = HttpClientUtil.callHttpPost_Dto(auc_url,requestDto);
        logger.info("[京东万象-银行三要素验证]响应结果=" + jsonStr);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(jsonStr);
        if(respDto != null && respDto.size() > 0)
        {
            String code = respDto.getAsString("code");//提取查询状态码
            if("10000".equals(code))
            {
                Dto resultDto = (Dto)respDto.get("result");//提取验证结果
                String errcode = resultDto.getAsString("error_code");//提取验证状态码
                if("0".equals(errcode))
                {
                    Dto dataDto = (Dto)resultDto.get("result");//提取银行卡信息
                    Dto informationDto = (Dto)dataDto.get("information");
                    if((StringUtil.isNotEmpty(informationDto.get("banknum")) && StringUtil.isNotEmpty(params.get("banknum")) && params.getAsLong("banknum").longValue() == informationDto.getAsLong("banknum").longValue())
                        || (StringUtil.isNotEmpty(params.get("abbreviation")) && params.getAsString("abbreviation").equals(informationDto.getAsString("abbreviation"))))
                    {
                        params.put("dcode",1000);
                        params.put("dmsg",resultDto.getAsString("reason"));
                    }
                    else
                    {
                        params.put("dcode",-1001);
                        params.put("dmsg","卡号与所选择的银行不匹配");
                    }
                    params.put("result",dataDto);//设置银行卡信息
                }
                else
                {
                    params.put("dcode",-1001);
                    params.put("dmsg",StringUtil.isEmpty(resultDto.get("reason"))? "认证失败" : resultDto.getAsString("reason"));
                }
            }
            else
            {
                params.put("dcode",-1000);
                params.put("dmsg",StringUtil.isEmpty(respDto.get("msg"))? "认证失败" : respDto.getAsString("msg"));
            }
        }
        else
        {
            params.put("dmsg","认证失败!响应结果=" + jsonStr);
        }
    }

    /**
     * 银行四要素验证(真实姓名/身份证/卡号/手机号)
     * @author  mcdog
     * @param   params  参数对象
     *                  (必要参数:name-真实姓名 idcard-身份证号码 bankno-银行卡号 mobile-手机号 banknum-银行编号 abbreviation-银行简称)
     *                  处理结果也保存在该对象中(dcode-验证状态 dmsg-验证状态描述),具体如下:
     *                  dcode=1000,验证通过,dcode != 1000,验证不通过
     */
    public static void aucFourItems(Dto params) throws Exception
    {
        /**
         * 校验参数(真实姓名/身份证号码/银行卡号/服务器公网ip)
         */
        if(StringUtil.isEmpty(params.get("name"))
                || StringUtil.isEmpty(params.get("idcard"))
                || StringUtil.isEmpty(params.get("bankno"))
                || StringUtil.isEmpty(params.get("mobile"))
                || (StringUtil.isEmpty(params.get("banknum")) && StringUtil.isEmpty(params.get("abbreviation"))))
        {
            logger.error("[京东万象-银行四要素验证]参数校验不通过!接收原始参数=" + params.toString());
            params.put("dcode",-1000);
            params.put("dmsg","必要参数不能为空!");
            return;
        }

        /**
         * 初始化请求参数
         */
        //拼装请求参数对象
        Dto requestDto = new BaseDto();
        requestDto.put("acct_pan",params.get("bankno"));
        requestDto.put("acct_name",params.get("name"));
        requestDto.put("cert_id",params.get("idcard"));
        requestDto.put("phone_num",params.get("mobile"));
        requestDto.put("appkey",appkey);

        /**
         * 发送请求
         */
        logger.info("[京东万象-银行四要素验证]请求参数=" + requestDto.toString());
        String jsonStr = HttpClientUtil.callHttpPost_Dto(auc_url,requestDto);
        logger.info("[京东万象-银行四要素验证]响应结果=" + jsonStr);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(jsonStr);
        if(respDto != null && respDto.size() > 0)
        {
            String code = respDto.getAsString("code");//提取查询状态码
            if("10000".equals(code))
            {
                Dto resultDto = (Dto)respDto.get("result");//提取验证结果
                Dto errCode = (Dto)resultDto.get("resp");//提取验证状态码
                if("0".equals(errCode.getAsString("code")))
                {
                    Dto dataDto = (Dto)resultDto.get("data");//提取银行卡信息
                    if(StringUtil.isNotEmpty(params.get("banknum"))
                            && params.getAsLong("banknum").longValue() == dataDto.getAsLong("bank_id").longValue())
                    {
                        params.put("dcode",1000);
                        params.put("dmsg",errCode.getAsString("desc"));
                    }
                    //本次接口不返回银行简称 无法验证
                    /*else if(StringUtil.isNotEmpty(params.get("abbreviation"))
                            && params.getAsString("abbreviation").equals(dataDto.getAsString("bank_name")))
                    {
                        params.put("dcode",1000);
                        params.put("dmsg",errCode.getAsString("desc"));
                    }*/
                    else
                    {
                        params.put("dcode",-1001);
                        params.put("dmsg","卡号与所选择的银行不匹配");
                    }
                    params.put("result",dataDto);//设置银行卡信息
                }
                else
                {
                    params.put("dcode",-1001);
                    params.put("dmsg",StringUtil.isEmpty(resultDto.get("reason"))? "认证失败" : errCode.getAsString("desc"));
                }
            }
            else
            {
                params.put("dcode",-1000);
                params.put("dmsg",StringUtil.isEmpty(respDto.get("msg"))? "认证失败" : respDto.getAsString("msg"));
            }
        }
        else
        {
            params.put("dmsg","认证失败!响应结果=" + jsonStr);
        }
    }

    /**
     * 实名认证(真实姓名/身份证)
     * @author  mcdog
     * @param   params  参数对象
     *                  (必要参数:name-真实姓名 idcard-身份证号码)
     *                  处理结果也保存在该对象中(dcode-验证状态 dmsg-验证状态描述),具体如下:
     *                  dcode=1000,验证通过,dcode != 1000,验证不通过
     */
    public static void realnameVerify(Dto params) throws Exception
    {
        /**
         * 校验参数(真实姓名/身份证号码/银行卡号/服务器公网ip)
         */
        if(StringUtil.isEmpty(params.get("name"))
                || StringUtil.isEmpty(params.get("idcard")))
        {
            logger.error("[京东万象-实名认证]参数校验不通过!接收原始参数:" + params.toString());
            params.put("dcode",-1000);
            params.put("dmsg","必要参数不能为空!");
            return;
        }

        /**
         * 初始化请求参数
         */
        //拼装请求参数对象
        Dto requestDto = new BaseDto();
        requestDto.put("name",params.get("name"));
        requestDto.put("idCard",params.get("idcard"));
        requestDto.put("appkey",appkey);

        /**
         * 发送请求
         */
        logger.info("[京东万象-实名认证]请求参数=" + requestDto.toString());
        String jsonStr = HttpClientUtil.callHttpPost_Dto(realnameverify_rul,requestDto);
        logger.info("[京东万象-实名认证]响应结果=" + jsonStr);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(jsonStr);
        if(respDto != null && respDto.size() > 0)
        {
            String code = respDto.getAsString("code");//提取查询状态码
            if("10000".equals(code))
            {
                Dto resultDto = (Dto)respDto.get("result");//提取验证结果
                boolean success = (boolean)resultDto.get("success");//提取认证状态码
                if(success)
                {
                    Dto dataDto = (Dto)resultDto.get("data");//提取认证信息
                    String compareStatus = dataDto.getAsString("compareStatus");
                    if("SAME".equals(compareStatus))
                    {
                        params.put("dcode",1000);
                        params.put("dmsg","认证通过");
                        params.put("result",dataDto);//设置认证信息
                    }
                    else
                    {
                        params.put("dcode",-1000);
                        params.put("dmsg",dataDto.getAsString("compareStatusDesc"));
                    }
                }
                else
                {
                    params.put("dcode",-1000);
                    String error = resultDto.getAsString("error");
                    String errorDesc = resultDto.getAsString("errorDesc");
                    params.put("dmsg",StringUtil.isEmpty(errorDesc)? (StringUtil.isEmpty(error)? "认证失败" : error) : errorDesc);
                }
            }
            else
            {
                params.put("dcode",-1000);
                params.put("dmsg",StringUtil.isEmpty(respDto.get("msg"))? "认证失败" : respDto.getAsString("msg"));
            }
        }
        else
        {
            params.put("dmsg","认证失败!响应结果=" + jsonStr);
        }
    }

    /**
     * 实名认证(真实姓名/身份证)
     * @author  mcdog
     * @param   params  参数对象
     *                  (必要参数:name-真实姓名 idcard-身份证号码)
     *                  处理结果也保存在该对象中(dcode-验证状态 dmsg-验证状态描述),具体如下:
     *                  dcode=1000,验证通过,dcode != 1000,验证不通过
     */
    public static void realnameVerify_2(Dto params) throws Exception
    {
        /**
         * 校验参数(真实姓名/身份证号码/银行卡号/服务器公网ip)
         */
        if(StringUtil.isEmpty(params.get("name"))
                || StringUtil.isEmpty(params.get("idcard")))
        {
            logger.error("[京东万象-实名认证]参数校验不通过!接收原始参数:" + params.toString());
            params.put("dcode",-1000);
            params.put("dmsg","必要参数不能为空!");
            return;
        }

        /**
         * 初始化请求参数
         */
        //拼装请求参数对象
        Dto requestDto = new BaseDto();
        requestDto.put("name",params.get("name"));
        requestDto.put("cardno",params.get("idcard"));
        requestDto.put("appkey",appkey);

        /**
         * 发送请求
         */
        logger.info("[京东万象-实名认证]请求参数=" + requestDto.toString());
        String jsonStr = HttpClientUtil.callHttpPost_Dto(realnameverify_rul_2,requestDto);
        logger.info("[京东万象-实名认证]响应结果=" + jsonStr);

        /**
         * 处理请求结果
         */
        Dto respDto = JsonUtil.jsonToDto(jsonStr);
        if(respDto != null && respDto.size() > 0)
        {
            String code = respDto.getAsString("code");//提取查询状态码
            if("10000".equals(code))
            {
                Dto resultDto = (Dto)respDto.get("result");//提取验证结果
                Dto responseResp = (Dto)resultDto.get("resp");//提取认证结果
                if("0".equals(responseResp.getAsString("code")))
                {
                    params.put("dcode",1000);
                    params.put("dmsg","认证通过");
                    params.put("result",resultDto);//设置认证信息
                }
                else
                {
                    params.put("dcode",-1000);
                    String errorDesc = responseResp.getAsString("desc");
                    params.put("dmsg",StringUtil.isEmpty(errorDesc)?"认证失败" : errorDesc);
                }
            }
            else
            {
                params.put("dcode",-1000);
                params.put("dmsg",StringUtil.isEmpty(respDto.get("msg"))? "认证失败" : respDto.getAsString("msg"));
            }
        }
        else
        {
            params.put("dmsg","认证失败!响应结果=" + jsonStr);
        }
    }

    public static void main(String[] args)
    {
        try
        {
            Dto params = new BaseDto();
            params.put("name","寇毅");//真实姓名
            params.put("idcard","510723198709033758");//身份证号码
            params.put("bankno","6214851211905133");//银行卡号
            params.put("banknum","03080000");//银行编号
            params.put("abbreviation","招商银行");//银行简称
            params.put("mobile","13636633461");//手机号
            //realnameVerify(params);
            //realnameVerify_2(params);
            aucFourItems(params);
            System.out.println(params.getAsString("dmsg"));
        }
       catch (Exception e)
       {
           e.printStackTrace();
       }
    }
}
