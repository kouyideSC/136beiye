package com.caipiao.common.encrypt;

import java.security.MessageDigest;
import java.util.Random;

/**
 * MD5工具类
 * Created by kouyi on 2017/9/22.
 */
public class MD5 {
	private static final int max = 99999999;
	
	/**
	 * 统一使用UTF-8编码
	 * @param source	
	 * 		加密前的字符串
	 * @return
	 * 		经过MD5加密后的16进制值
	 */
	public static String md5(String source) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			byte[] byteArray = messageDigest.digest(source.getBytes("UTF-8"));
			StringBuffer md5StrBuff = new StringBuffer();
			for (int i = 0; i < byteArray.length; i++) {
				if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
					md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
				} else {
					md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
				}
			}
			return md5StrBuff.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return source;
		}
	}

	/**
	 * 生成含有随机盐的密码
	 * @param str
	 * @return
	 */
	public static String md5Salt(String str) {
		Random r = new Random();
		StringBuilder sb = new StringBuilder(16);
		sb.append(r.nextInt(max)).append(r.nextInt(max));
		int len = sb.length();
		if (len < 16) {
			for (int i = 0; i < 16 - len; i++) {
				sb.append("0");
			}
		}
		String salt = sb.toString();
		str = md5(str + salt);
		char[] cs = new char[48];
		for (int i = 0; i < 48; i += 3) {
			cs[i] = str.charAt(i / 3 * 2);
			char c = salt.charAt(i / 3);
			cs[i + 1] = c;
			cs[i + 2] = str.charAt(i / 3 * 2 + 1);
		}
		return new String(cs);
	}

	/**
	 * 签名字符串正确性验证
	 * @param str
	 * @param md5Str
	 * @return
	 */                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
	public static boolean verify(String str, String md5Str) {
		char[] cs1 = new char[32];
		char[] cs2 = new char[16];
		for (int i = 0; i < 48; i += 3) {
			cs1[i / 3 * 2] = md5Str.charAt(i);
			cs1[i / 3 * 2 + 1] = md5Str.charAt(i + 2);
			cs2[i / 3] = md5Str.charAt(i + 1);
		}
		String salt = new String(cs2);
		return md5(str + salt).equals(new String(cs1));
	}

	public static void main(String[] args) {
		String password = md5Salt("123456789");
		System.out.println(password);
		System.out.println(verify("kouyi", password));
		System.out.println(md5Salt("12345678"));
		System.out.println(verify("12345678", password));
	}
}
