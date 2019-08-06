package com.caipiao.common.pay.qq;

import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.pay.weixin.WeixinUtils;
import com.caipiao.domain.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * QQ支付工具类
 * @author sjq
 */
public class QqUtils
{
    private static final Logger logger = LoggerFactory.getLogger(QqUtils.class);
    public static final String appNo = "1106707223";//应用号(appId)

    public static void main(String[] args) {
       /* for(int i = 0; i <=2000; i ++)
        {
            System.out.println("" + new Random().nextInt(10) + new Random().nextInt(10) + new Random().nextInt(10) + new Random().nextInt(10));
        }*/
        /*try
        {
            String key = "live800sqgoing8899";//签名加密key
            String userId = "50";//用户编号
            String name = "章泽天";//用户昵称
            String memo = "";//自定义参数,设置为空字符串
            long timestamp = System.currentTimeMillis();//当前时间戳

            //生成hashCode
            String hashCode = userId + name + memo + timestamp + key;
            hashCode = URLEncoder.encode(hashCode,"utf-8");//URL编码,编码字符集为utf-8
            hashCode = MD5.md5(hashCode);//md5加密

            //生成info
            String info = "userId=" + userId + "&name=" + name + "&memo=" + memo + "&hashCode=" + hashCode + "&timestamp=" + timestamp;
            info = URLEncoder.encode(info,"utf-8");

            //拼接完整的链接地址
            String url = "https://vs28.verifiedsafesite.com/chat/Hotline/channel.jsp?cid=5008371&cnfid=5327&j=8644364192&s=1";
            url += "&info=" + info;
            System.out.println(url);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/
    }
}