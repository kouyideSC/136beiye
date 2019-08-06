package com.caipiao.common.util;

import com.caipiao.common.encrypt.RSA;
import com.caipiao.domain.vo.UserVo;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static com.caipiao.common.util.DateUtil.LOG_DATE_TIME;

/**
 * COOKIE工具类
 * Created by kouyi on 2018/3/16.
 */
public class CookieUtil {
    public static final String COOKIE_VALUE = "CHANNELCVALUE";
    public static final String COOKIE_KEY = "CHANNELCKEY";
    public static final int EXPIRE = 3600;//秒3600=1小时
    private static final String DEFAULT_PREFIX = "CK";
    private static final String mark = ";";
    private static final String cookieValueOne = "cookie=h5login";
    private static final String cookieValueTwo = "key=";
    private static final String cookieValueThree = "time=";


    public static void main(String[] args) {
        System.out.println(generateRandomKey());
    }

    /**
     * cookie写入
     * @param vo
     * @param response
     */
    public static void writeUserCookie(UserVo vo, HttpServletResponse response) {
        //写入加密串-用户信息
        Cookie cookieValue = new Cookie(COOKIE_VALUE, vo.getToken());
        cookieValue.setMaxAge(EXPIRE);
        cookieValue.setPath("/");
        cookieValue.setDomain(".tuiqiuxiong.com");
        response.addCookie(cookieValue);
        //写入解密key
        Cookie cookieKey = new Cookie(COOKIE_KEY, vo.getKey());
        cookieKey.setMaxAge(EXPIRE);
        cookieKey.setPath("/");
        cookieKey.setDomain(".tuiqiuxiong.com");
        response.addCookie(cookieKey);
    }

    /**
     * 获取cookie中存放的用户编号
     * @cookie:cookie=h5login;userId;key=32位随机串;time=17位时间戳&
     * @return
     */
    public static Long getCookieUserId(HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();
            if(StringUtil.isEmpty(cookies)) {
                return null;
            }
            String cookieStr = "";
            String cookieKey = "";
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals(COOKIE_VALUE)) {
                    cookieStr = cookie.getValue();
                }
                if(cookie.getName().equals(COOKIE_KEY)) {
                    cookieKey = cookie.getValue();
                }
            }
            if (StringUtil.isEmpty(cookieStr) || StringUtil.isEmpty(cookieKey)) {
                return null;
            }
            String[] values = RSA.decryptByPrivateKey(cookieStr).split("\\;");
            if (values.length != 4) {
                return null;
            }
            //参数段1检查
            if (!values[0].equalsIgnoreCase(cookieValueOne)) {
                return null;
            }
            //参数段2检查
            if (!values[2].equalsIgnoreCase(cookieValueTwo + cookieKey)) {
                return null;
            }
            String[] times = values[3].split("\\=");
            if (times.length != 2) {
                return null;
            }
            //参数段3检查
            if (!times[0].equalsIgnoreCase("time")) {
                return null;
            }
            //必须数字
            if(!NumberUtil.isNumber(times[1])) {
                return null;
            }
            //时间戳必须比当前时间小,且时间只差不能大于8小时[cookie8小时过期]
            if(new Date().getTime() < Long.parseLong(times[1]) || (new Date().getTime() - Long.parseLong(times[1]))/1000 > EXPIRE) {
                return null;
            }
            //获取用户编号
            return Long.parseLong(values[1]);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 生成用户cookie字符串
     * cookie原文串标准：cookie=h5login;userId;key=32位随机串;time=17位时间戳&
     * @param key
     *          随机秘钥
     * @return
     */
    public static String generateCookie(String key, Long userId) {
        try {
            if(StringUtil.isEmpty(userId)) {
                return "";
            }
            if(StringUtil.isEmpty(key)) {
                key = generateRandomKey(DEFAULT_PREFIX);
            }
            //cookie原文串标准：cookie=caipiao;key=32位随机串;time=时间戳
            String cookie = cookieValueOne + mark + userId + mark + cookieValueTwo + key + mark + cookieValueThree + new Date().getTime();
            cookie = RSA.encryptByPublicKey(cookie);
            return cookie;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 随机生成32位KEY字符串
     * @return
     */
    public static String generateRandomKey() {
        StringBuffer str = new StringBuffer(DEFAULT_PREFIX);
        str.append(mixString());
        return str.toString();
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
