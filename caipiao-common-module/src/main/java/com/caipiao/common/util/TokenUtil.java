package com.caipiao.common.util;

import com.caipiao.common.encrypt.RSA;

import java.util.Date;

import static com.caipiao.common.util.DateUtil.LOG_DATE_TIME;

/**
 * TOKEN工具类
 * Created by kouyi on 2017/9/30.
 */
public class TokenUtil {
    public static final String DEFAULT_PREFIX = "CP";
    public static final String mark = ";";
    public static final int EXPIRE = 30*24*60*60;//s
    public static final String tokenValueOne = "token=caipiao";
    public static final String tokenValueTwo = "key=";
    public static final String tokenValueThree = "time=";

    public static void main(String[] args) {
        System.out.println(generateToken(generateRandomKey("cs")));
    }

    /**
     * token字符串有效性检查
     * @param token
     * @param key
     * @return
     */
    public static Boolean checkValidToken(String token, String key) {
        try {
            if (StringUtil.isEmpty(token) || StringUtil.isEmpty(key)) {
                return false;
            }
            String[] tokens = RSA.decryptByPrivateKey(token).split("\\;");
            if (tokens.length != 3) {
                return false;
            }
            //参数段1检查
            if (!tokens[0].equalsIgnoreCase(tokenValueOne)) {
                return false;
            }
            //参数段2检查
            if (!tokens[1].equalsIgnoreCase(tokenValueTwo + key)) {
                return false;
            }
            String[] times = tokens[2].split("\\=");
            if (times.length != 2) {
                return false;
            }
            //参数段3检查
            if (!times[0].equalsIgnoreCase("time")) {
                return false;
            }
            //必须数字
            if(!NumberUtil.isNumber(times[1])) {
                return false;
            }
            //时间戳必须比当前时间小，且时间只差不能大于30天[token30天过期]
            if(new Date().getTime() < Long.parseLong(times[1]) || (new Date().getTime() - Long.parseLong(times[1]))/1000 > EXPIRE) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 生成用户TOKEN字符串
     * token原文串标准：token=caipiao;key=32位随机串;time=17位时间戳&
     * @param key
     *          随机秘钥
     * @return
     */
    public static String generateToken(String key) {
        try {
            if(StringUtil.isEmpty(key)) {
                key = generateRandomKey();
            }
            //token原文串标准：token=caipiao;key=32位随机串;time=时间戳
            String token = tokenValueOne + mark + tokenValueTwo + key + mark + tokenValueThree + new Date().getTime();
            token = RSA.encryptByPublicKey(token);
            return token;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 随机生成32位KEY字符串
     * @param prefix
     *          前缀-两个字符长度开头标识
     * @return
     */
    public static String generateRandomKey(String prefix) {
        if(StringUtil.isEmpty(prefix)) {
            prefix = DEFAULT_PREFIX;
        }

        prefix = prefix.toUpperCase();//大写
        StringBuffer str = new StringBuffer(prefix);
        str.append(mixString());
        return str.toString();
    }

    /**
     * 随机生成32位KEY字符串-默认前缀CP
     * @return
     */
    public static String generateRandomKey() {
        StringBuffer str = new StringBuffer(DEFAULT_PREFIX);
        str.append(mixString());
        return str.toString();
    }

    /**
     * 32位随机字符串
     * @return
     */
    private static synchronized String mixString() {
        StringBuffer buffer = new StringBuffer(DateUtil.dateFormat(new Date(), LOG_DATE_TIME));
        int len = 30 - buffer.length();
        int len2 = buffer.length();
        for (int i = 0; i < len; i++) {
            buffer.insert(getRandomPos(len2), getRandomChar());
            len2 = buffer.length();
        }
        return buffer.toString();
    }

    /**
     * 获取随机数
     * @param len
     * @return
     */
    private static int getRandomPos(int len) {
        if (len == 0){
            len = 1;
        }
        int pos = 0;
        pos = (int) (Math.random() * 1000);
        return pos % len;
    }

    /**
     * 获取随机字符
     * @return
     */
    public static char getRandomChar() {
        char c = 0;
        int b = (int) (Math.random() * 1000) % 26;
        c = (char) (b + 65);
        return c;
    }

}
