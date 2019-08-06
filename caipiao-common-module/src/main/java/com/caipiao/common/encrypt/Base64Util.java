package com.caipiao.common.encrypt;

import org.apache.commons.codec.binary.Base64;

/**
 * base64工具类
 * Created by kouyi on 2017/9/27.
 */
public class Base64Util {
    /**
     * 加密
     * 字符串加密为字符串
     * @param source
     * @return
     */
    public static String encode(String source) {
        return encodeByte(source.getBytes());
    }

    /**
     * 解密
     * 字符串解密为字符串
     * @param source
     * @return
     */
    public static String decode(String source) {
        return new String(decodeToByte(source));
    }

    /**
     * 加密
     * 加密byte[]类型，密文为字符串
     * @param b
     * @return
     */
    public static String encodeByte(byte[] b) {
        return new String(new Base64().encode(b));
    }

    /**
     * 解密
     * 将字符串解密为byte[]类型
     * @param source
     * @return
     */
    public static byte[] decodeToByte(String source) {
        return new Base64().decode(source.getBytes());
    }
}
