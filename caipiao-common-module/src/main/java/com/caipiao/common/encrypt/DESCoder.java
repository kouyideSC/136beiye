package com.caipiao.common.encrypt;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * 糯米出票DES加密工具类
 * Created by Kouyi on 2017/12/11.
 */
public class DESCoder {
	private static final String CHARSET = "utf-8";
	public static String desEncrypt(String message, String desKey) {
		String encryptStr = null;
		try {
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, genSecretKey(desKey));
			byte cipherText[] = cipher.doFinal(message.trim().getBytes("utf-8"));
			byte encryptByte[] = Base64.encodeBase64(cipherText);
			encryptStr = new String(encryptByte, "UTF-8");
		} catch (Throwable e) {
			throw new RuntimeException("des加密发生异常", e);
		}
		return encryptStr;
	}

	public static String desDecrypt(String cipherText, String desKey) {
		String decryptStr = null;
		try {
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			byte input[] = Base64.decodeBase64(cipherText.trim().getBytes("UTF-8"));
			cipher.init(Cipher.DECRYPT_MODE, genSecretKey(desKey));
			byte output[] = cipher.doFinal(input);
			decryptStr = new String(output, "utf-8");
		}
		catch (Throwable e) {
			throw new RuntimeException("des解密发生异常", e);
		}
		return decryptStr;
	}

	private static SecretKey genSecretKey(String key)
		throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException {
		DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		return secretKey;
	}

	protected static byte[] hexStringToByte(String hex) {
		int len = hex.length() / 2;
		byte result[] = new byte[len];
		char achar[] = hex.toCharArray();
		for (int i = 0; i < len; i++)
		{
			int pos = i * 2;
			result[i] = (byte)(toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}

		return result;
	}

	private static byte toByte(char c) {
		byte b = (byte)"0123456789ABCDEF".indexOf(c);
		return b;
	}
}
