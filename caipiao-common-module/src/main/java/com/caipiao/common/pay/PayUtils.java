package com.caipiao.common.pay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付相关工具类
 * @author sjq
 */
public class PayUtils
{
    public static final Logger logger = LoggerFactory.getLogger(PayUtils.class);
    public static final String androidAppPackage = "cn.chance.lucky";//安卓包名
    public static final String iosAppFlag = "cn.RedSunLottery";//IOS应用唯一标识
    public static final Integer signType_md5 = 1;
    public static final Integer signType_rsa = 2;
    public static final Integer signType_res = 3;
    public static final String signType_md5_str = "MD5";
    public static final String signType_rsa_str = "RSA";
    public static final String signType_res_str = "RES";
    public static Map<Integer,String> signTypeMaps = new HashMap<Integer,String>();//签名方式集合
    public static Map<Integer,String> weixinTradeTypeMaps = new HashMap<Integer,String>();//微信交易类型集合

    static
    {
        signTypeMaps.put(signType_md5,signType_md5_str);
        signTypeMaps.put(signType_rsa,signType_rsa_str);
        signTypeMaps.put(signType_res,signType_res_str);

        weixinTradeTypeMaps.put(0,"WEB");
        weixinTradeTypeMaps.put(1,"APP");
        weixinTradeTypeMaps.put(2,"APP");
        weixinTradeTypeMaps.put(3,"MWEB");
        weixinTradeTypeMaps.put(4,"WEB");
    }

    /**
     * 产生指定长度的随机字符串
     * @author  mcdog
     * @param   length  指定长度
     */
    public static String getRandomStr(int length)
    {
        length = length <= 0? 1 : length;
        String KeyString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";//随机字符库
        StringBuffer sb = new StringBuffer();
        int len = KeyString.length();
        for(int i = 0; i < length; i ++)
        {
            sb.append(KeyString.charAt((int) Math.round(Math.random() * (len - 1))));
        }
        return sb.toString();
    }

    /**
     * 获取主机IP地址
     * @author  mcdog
     */
    public static String getHostIp()
    {
        String hostIp = "127.0.0.1";
        try
        {
            hostIp = InetAddress.getLocalHost().getHostAddress();
        }
        catch(Exception e)
        {
            logger.error("[获取主机IP地址]发生异常!异常信息:" + e);
        }
        return hostIp;
    }

    public static void main(String[] args) {
        System.out.println(getHostIp());
    }
}
