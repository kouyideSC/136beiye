package com.caipiao.common.pay.haipay.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

public class RsaCoder {
    public static final String KEY_ALGORITHM = "RSA";

    public static final String PUBLIC_KEY = "RSAPublicKey";

    public static final String PRIVATE_KEY = "RSAPrivateKey";
    
    public static final String CHAR_SET = "utf-8";

    private static final int KEY_SIZE = 1024;
    
    /*** 最大分段数量*/
    private static final int MAX_BLOCK_SIZE = KEY_SIZE/8;

    private Map<String, Object> KEY_MAP;

    public RsaCoder() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);

        keyPairGen.initialize(KEY_SIZE);

        KeyPair keyPair = keyPairGen.generateKeyPair();

        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        this.KEY_MAP = new HashMap<String, Object>(2);
        KEY_MAP.put(PUBLIC_KEY, publicKey);
        KEY_MAP.put(PRIVATE_KEY, privateKey);
    }

    // 私钥解密
    public static byte[] decryptByPrivateKey(byte[] data, byte[] key) throws Exception {
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    // 私钥解密2
    public static String decryptByPrivateKey(String data, String key) throws IOException, Exception {
        byte[] dataByte = decryptByPrivateKey(Base64.decodeBase64(data), Base64.decodeBase64(key));
        return new String(dataByte);
    }

    // 公钥解密
    public static byte[] decryptByPublicKey(byte[] data, byte[] key) throws Exception {
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    // 公钥解密2
    public static String decryptByPublicKey(String data, String key) throws IOException, Exception {
        byte[] dataByte = decryptByPublicKey(Base64.decodeBase64(data), Base64.decodeBase64(key));
        return new String(dataByte);
    }

    // 公钥加密
    public static byte[] encryptByPublicKey(byte[] data, byte[] key) throws Exception {
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    // 公钥加密2
    public static String encryptByPublicKey(String data, String key) throws UnsupportedEncodingException, IOException, Exception {
        byte[] signByte = encryptByPublicKey(data.getBytes(CHAR_SET), Base64.decodeBase64(key));
        return Base64.encodeBase64String(signByte);
    }

    // 私钥加密
    public static byte[] encryptByPrivateKey(byte[] data, byte[] key) throws Exception {
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    // 私钥加密2
    public static String encryptByPrivateKey(String data, String key) throws IOException, Exception {
        byte[] signByte = encryptByPrivateKey(data.getBytes(), Base64.decodeBase64(key));
        return Base64.encodeBase64String(signByte);
    }

    // 私钥验证公钥密文
    public static boolean checkPublicEncrypt(String data, String sign, String pvKey) throws IOException, Exception {
        return data.equals(decryptByPrivateKey(sign, pvKey));
    }

    public static boolean checkPrivateEncrypt(String data, String sign, String pbKey) throws IOException, Exception {
        return data.equals(decryptByPublicKey(sign, pbKey));
    }
    
    /**
     * 字符串分段加密拆分方法
     * @author:zhangwei14@ucfgroup.com 
     * @since:2016年7月27日 上午11:57:46
     * @param data 需要分段加密的字符串
     * @param len 拆分长度
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static String[] splitString(String data, int len) throws UnsupportedEncodingException {
    	String[] results = new String[0];
    	byte[] dataBytes = data.getBytes(CHAR_SET);
    	if(data!=null && data.trim().length()!=0){
    		int x = dataBytes.length / len;
    		int y = dataBytes.length % len;
    		int z = 0;
    		if (y != 0) {
    			z = 1;
    		}
    		results = new String[x + z];
            byte[] arr;
            int offset = 0;
    		for (int i = 0; i < x + z; i++) {
    			arr = new byte[len];
    			int offsetLen = 0;
    			if (i == x + z - 1 && y != 0) {
    				System.arraycopy(dataBytes, offset, arr, 0, y); 
    			} else {
                    System.arraycopy(dataBytes, offset, arr, 0, len);
                    while(arr[arr.length-1]<0){
                    	offsetLen ++;
                    	arr = new byte[len-offsetLen];
                    	System.arraycopy(dataBytes, offset, arr, 0, len-offsetLen);
                    }
    			}
    			offset += len - offsetLen;
    			results[i] = trimToEmpty(new String(arr,CHAR_SET));
    		}
    	}
		return results;
	}
    
    private static String trimToEmpty(String str) {
        return str == null ? "" : str.trim();
    }
    
    /**
     * 字节数组分段解密拆分方法
     * @author:zhangwei14@ucfgroup.com 
     * @since:2016年7月27日 上午11:57:46
     * @param data 需要分段解密的字节数组
     * @param len 拆分长度
     * @return
     */
    private static byte[][] splitArray(byte[] data, int len) {
		int x = data.length / len;
		int y = data.length % len;
		int z = 0;
		if (y != 0) {
			z = 1;
		}
		byte[][] arrays = new byte[x + z][];
		byte[] arr;
		for (int i = 0; i < x + z; i++) {
			arr = new byte[len];
			if (i == x + z - 1 && y != 0) {
				System.arraycopy(data, i * len, arr, 0, y);
			} else {
				System.arraycopy(data, i * len, arr, 0, len);
			}
			arrays[i] = arr;
		}
		return arrays;
	}
    
    private static String bcd2Str(byte[] bytes) {
		char temp[] = new char[bytes.length * 2], val;

		for (int i = 0; i < bytes.length; i++) {
			val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
			temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

			val = (char) (bytes[i] & 0x0f);
			temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
		}
		return new String(temp);
	}
    
    private static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
		byte[] bcd = new byte[asc_len / 2];
		int j = 0;
		for (int i = 0; i < (asc_len + 1) / 2; i++) {
			bcd[i] = asc_to_bcd(ascii[j++]);
			bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
		}
		return bcd;
	}

	private static byte asc_to_bcd(byte asc) {
		byte bcd;
		if ((asc >= '0') && (asc <= '9'))
			bcd = (byte) (asc - '0');
		else if ((asc >= 'A') && (asc <= 'F'))
			bcd = (byte) (asc - 'A' + 10);
		else if ((asc >= 'a') && (asc <= 'f'))
			bcd = (byte) (asc - 'a' + 10);
		else
			bcd = (byte) (asc - 48);
		return bcd;
	}
    
    /**
     * 使用公钥分段加密方法
     * @author:zhangwei14@ucfgroup.com 
     * @since:2016年7月27日 上午11:54:39
     * @param data 需要加密的字符串
     * @param key  加密使用的公钥密钥
     * @return 分段加密后的密文字符串
     * @throws Exception
     */
    public static String encryptByPublicKeyWithSplit(String data, String key) throws Exception {
    	StringBuffer result = new StringBuffer();
    	String[] datas = splitString(data, MAX_BLOCK_SIZE - 11);
		for (String splitData : datas) {
			if(splitData!=null){
				byte[] signByte = encryptByPublicKey(splitData.getBytes(CHAR_SET), Base64.decodeBase64(key));
	    		result.append(bcd2Str(signByte));
			}
		}
        return result.toString();
    }
    
    /**
     * 使用私钥分段加密方法
     * @author:zhangwei14@ucfgroup.com 
     * @since:2016年7月27日 上午11:54:39
     * @param data 需要加密的字符串
     * @param key  加密使用的私钥密钥
     * @return 分段加密后的密文字符串
     * @throws Exception
     */
    public static String encryptByPrivateKeyWithSplit(String data, String key) throws Exception {
    	StringBuffer result = new StringBuffer();
    	String[] datas = splitString(data, MAX_BLOCK_SIZE - 11);
		for (String splitData : datas) {
			if(splitData!=null){
				byte[] signByte = encryptByPrivateKey(splitData.getBytes(CHAR_SET), Base64.decodeBase64(key));
	    		result.append(bcd2Str(signByte));
			}
		}
        return result.toString();
    }
    
    /**
     * 使用公钥分段解密方法
     * @author:zhangwei14@ucfgroup.com 
     * @since:2016年7月27日 上午11:54:39
     * @param data 需要解密的密文字符串
     * @param key  解密使用的公钥密钥
     * @return 分段解密后的明文字符串
     * @throws Exception
     */
    public static String decryptByPublicKeyWithSplit(String data, String key) throws Exception {
    	StringBuffer result = new StringBuffer();
		byte[] bytes = data.getBytes(CHAR_SET);
    	byte[][] dataArrays = splitArray(ASCII_To_BCD(bytes,bytes.length), MAX_BLOCK_SIZE);
		for (byte[] dataArray : dataArrays) {
	    	byte[] dataByte = decryptByPublicKey(dataArray, Base64.decodeBase64(key));
    		result.append(new String(dataByte));
		}
        return result.toString();
    }
    
    /**
     * 使用私钥分段解密方法
     * @author:zhangwei14@ucfgroup.com 
     * @since:2016年7月27日 上午11:54:39
     * @param data 需要解密的密文字符串
     * @param key  解密使用的私钥密钥
     * @return 分段解密后的明文字符串
     * @throws Exception
     */
    public static String decryptByPrivateKeyWithSplit(String data, String key) throws Exception {
    	StringBuffer result = new StringBuffer();
		byte[] bytes = data.getBytes(CHAR_SET);
    	byte[][] dataArrays = splitArray(ASCII_To_BCD(bytes,bytes.length), MAX_BLOCK_SIZE);
		for (byte[] dataArray : dataArrays) {
	    	byte[] dataByte = decryptByPrivateKey(dataArray, Base64.decodeBase64(key));
    		result.append(new String(dataByte));
		}
        return result.toString();
    }
    

    // 取得私钥
    public byte[] getPrivateKey() {
        Key key = (Key) this.KEY_MAP.get(PRIVATE_KEY);
        return key.getEncoded();
    }

    // 取得公钥
    public byte[] getPublicKey() {
        Key key = (Key) this.KEY_MAP.get(PUBLIC_KEY);
        return key.getEncoded();
    }

    // 取得私钥
    public String getPrivateKeyBase64() {
        Key key = (Key) this.KEY_MAP.get(PRIVATE_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

    // 取得公钥
    public String getPublicKeyBase64() {
        Key key = (Key) this.KEY_MAP.get(PUBLIC_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }
    
    public static void main(String[] arge) {
    	try {
	    	RsaCoder rsaCoder = new RsaCoder();
	    	String publicKeyBase64 = rsaCoder.getPublicKeyBase64();
	    	System.out.println("publicKeyBase64:"+publicKeyBase64);
	    	String privateKeyBase64 = rsaCoder.getPrivateKeyBase64();
	    	System.out.println("privateKeyBase64:"+privateKeyBase64);
	    	String info = "测试样例";
	    	String encryptByPublicKey = rsaCoder.encryptByPublicKey(info, publicKeyBase64);
	    	System.out.println("encryptByPublicKey:"+encryptByPublicKey);
	    	String decryptByPrivateKey = rsaCoder.decryptByPrivateKey(encryptByPublicKey, privateKeyBase64);
	    	System.out.println("decryptByPrivateKey:"+decryptByPrivateKey);
    	}catch(Exception e) {
    		System.out.println("error");
    	}
    }
}
