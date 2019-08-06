package com.caipiao.common.pay.haipay.util;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class AESCoder {

    /**
     * 密钥算法
     */
    private static final String KEY_ALGORITHM = "AES";

    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    private static final String CHAR_SET = "utf-8";

    /**
     * 
     * 生成密钥
     * 
     * @return
     */
    public static String createKey() {
        byte[] key = initSecretKey();
        return Hex.encodeHexString(key);
    }

    /**
     * 初始化密钥
     * 
     * @return byte[] 密钥
     * @throws Exception
     */
    private static byte[] initSecretKey() {
        // 返回生成指定算法的秘密密钥的 KeyGenerator 对象
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new byte[0];
        }
        // 初始化此密钥生成器，使其具有确定的密钥大小
        // AES 要求密钥长度为 128
        kg.init(128);
        // 生成一个密钥
        SecretKey secretKey = kg.generateKey();
        return secretKey.getEncoded();
    }

    /**
     * 转换密钥
     * 
     * @param key
     *            二进制密钥
     * @return 密钥
     */
    public static Key toKey(byte[] key) {
        // 生成密钥
        return new SecretKeySpec(key, KEY_ALGORITHM);
    }

    /**
     * 加密
     * 
     * @param data
     *            待加密数据
     * @param key
     *            密钥
     * @return byte[] 加密数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, Key key) throws GeneralSecurityException  {
        return encrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    /**
     * 加密
     * 
     * @param data
     *            待加密数据
     * @param key
     *            二进制密钥
     * @return byte[] 加密数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, byte[] key) throws GeneralSecurityException {
        return encrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    /**
     * 加密
     * 
     * @param data
     *            待加密数据
     * @param key
     *            二进制密钥
     * @param cipherAlgorithm
     *            加密算法/工作模式/填充方式
     * @return byte[] 加密数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, byte[] key, String cipherAlgorithm) throws GeneralSecurityException {
        // 还原密钥
        Key k = toKey(key);
        return encrypt(data, k, cipherAlgorithm);
    }

    /**
     * 加密
     * 
     * @param data
     *            待加密数据
     * @param key
     *            密钥
     * @param cipherAlgorithm
     *            加密算法/工作模式/填充方式
     * @return byte[] 加密数据
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, Key key, String cipherAlgorithm) throws GeneralSecurityException  {
        // 实例化
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        // 使用密钥初始化，设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, key);
        // 执行操作
        return cipher.doFinal(data);
    }

    /**
     * 解密
     * 
     * @param data
     *            待解密数据
     * @param key
     *            二进制密钥
     * @return byte[] 解密数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws GeneralSecurityException {
        return decrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    /**
     * 解密
     * 
     * @param data
     *            待解密数据
     * @param key
     *            密钥
     * @return byte[] 解密数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, Key key) throws Exception {
        return decrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    /**
     * 解密
     * 
     * @param data
     *            待解密数据
     * @param key
     *            二进制密钥
     * @param cipherAlgorithm
     *            加密算法/工作模式/填充方式
     * @return byte[] 解密数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, byte[] key, String cipherAlgorithm) throws GeneralSecurityException {
        // 还原密钥
        Key k = toKey(key);
        return decrypt(data, k, cipherAlgorithm);
    }

    /**
     * 解密
     * 
     * @param data
     *            待解密数据
     * @param key
     *            密钥
     * @param cipherAlgorithm
     *            加密算法/工作模式/填充方式
     * @return byte[] 解密数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, Key key, String cipherAlgorithm) throws GeneralSecurityException {
        // 实例化
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        // 使用密钥初始化，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, key);
        // 执行操作
        return cipher.doFinal(data);
    }

    private static String showByteArray(byte[] data) {
        if (null == data) {
            return null;
        }
        StringBuilder sb = new StringBuilder("{");
        for (byte b : data) {
            sb.append(b).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * 密钥加密
     * @author:zhangwei14@ucfgroup.com 
     * @since:2016年8月2日 下午4:21:57
     * @param data 需要加密的数据
     * @param key  加密密钥
     * @return
     * @throws GeneralSecurityException
     * @throws DecoderException
     */
    public static String encrypt(String data,String key) throws Exception{
        byte[] keyByte = Hex.decodeHex(digestMD5(key).toCharArray());
        Key k = AESCoder.toKey(keyByte);
        byte[] encryptData = AESCoder.encrypt(data.getBytes(CHAR_SET), k);
        return Base64.encodeBase64String(encryptData);
    }
    
    /**
     * 密钥解密
     * @author:zhangwei14@ucfgroup.com 
     * @since:2016年8月2日 下午4:24:00
     * @param data 需要解密的数据
     * @param key  解密密钥
     * @return
     * @throws Exception
     */
    public static String decrypt(String data,String key) throws Exception {
        byte[] keyByte = Hex.decodeHex(digestMD5(key).toCharArray());
        Key k = AESCoder.toKey(keyByte);
        byte[] decryptData = AESCoder.decrypt(Base64.decodeBase64(data), k);
        return new String(decryptData,CHAR_SET);
    }

    public static void main(String[] args) throws Exception {
        byte[] key = initSecretKey();
        System.out.println("key：" + showByteArray(key));
        System.out.println("key：" + Hex.encodeHexString(key));
        System.out.println("key："
                           + showByteArray(Hex.decodeHex(Hex.encodeHexString(key).toCharArray())));
        key = Hex.decodeHex(Hex.encodeHexString(key).toCharArray());
        Key k = toKey(key);

        String data = "AES数据";
        System.out.println("加密前数据: string:" + data);
        System.out.println("加密前数据: byte[]:" + showByteArray(data.getBytes()));
        System.out.println();
        byte[] encryptData = encrypt(data.getBytes(), k);
        System.out.println("加密后数据: byte[]:" + showByteArray(encryptData));
        System.out.println("加密后数据: hexStr:" + Hex.encodeHexString(encryptData));
        System.out.println();
        byte[] decryptData = decrypt(encryptData, k);
        System.out.println("解密后数据: byte[]:" + showByteArray(decryptData));
        System.out.println("解密后数据: string:" + new String(decryptData));
        System.out.println();
        
        String data2 = "AES数据21212";
        String tempkey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCbj/6kF5xteqnO1rub+dq+0THaW9Hs/EO2GNuCE5dr703v8M6zLGVtReuCr0Oxc9aP+sMckOLtTglsm+McjiaDYsIGkcLXEwaV8ZFfLMswQ65rJd6rizUNLAsQIQiJvS4lfe3biGhdap/+gxQR2XFKgiKvghyQFMvn936rjiK83QIDAQAB";
        System.out.println("加密前数据: string:" + data2);
        System.out.println();
        String encryptData2 = encrypt(data2, tempkey);
        System.out.println("加密后数据: hexStr:" + encryptData2);
        System.out.println();
        String decryptData2 = decrypt(encryptData2, tempkey);
        System.out.println("解密后数据: string:" + decryptData2);


        System.out.println();
    }
    
    public static String digestMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] bDigests = md.digest(input.getBytes(CHAR_SET));

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
}