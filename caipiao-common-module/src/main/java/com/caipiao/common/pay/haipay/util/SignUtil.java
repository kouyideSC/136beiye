package com.caipiao.common.pay.haipay.util;

import com.caipiao.common.pay.haipay.Exception.CoderException;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SignUtil {
	
	public static final String DEFAULT_CHARSET_NAME = "UTF-8";
	/**
     * 生成签名
     * 
     * @param signKey
     * @param signName
     * @param params
     * @param type
     * @return
     * @throws GeneralSecurityException
     * @throws com.caipiao.common.pay.haipay.Exception.CoderException
     */
    public static String createSign(String signKey, String signName, Map<String, String> params,
                                    String type) throws GeneralSecurityException, CoderException {

        if ("MD5".equals(type)) {
            return digestMD5(signKey.concat(getSignData(signName, params)).toLowerCase());
        } else if ("RSA".equals(type)) {
            try {
                return RsaCoder.encryptByPublicKey(digestMD5(getSignData(signName, params)).toLowerCase(), signKey);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return "";
    }
    
    public static String digestMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] bDigests = md.digest(input.getBytes(DEFAULT_CHARSET_NAME));

            return byte2hex(bDigests);
        } catch (Exception e) {
            return "";
        }
    }
    
    private static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
        }
        return hs.toUpperCase();
    }
    
    /**
     * 获得加密字符串
     * 
     * @param signName
     * @param params
     * @return
     */
    public static String getSignData(String signName, Map<String, String> params) {
        StringBuffer content = new StringBuffer();
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            if (signName.equals(key)) {
                continue;
            }
            String value = params.get(key);
            value = null==value ? "" : value;
            content.append("&" + key + "=" + value);
        }
        String str = content.toString();
        if (isEmpty(str)) {
            return str;
        }
        if (str.startsWith("&")){
            return str.substring("&".length());
        }
        return str;
    }
    
    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
