package com.caipiao.common.pay.haipay.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;

import org.apache.commons.io.IOUtils;

public class JKSUtil {
	
	public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA";
	
	public static PublicKey getPublicKey(String keyStoreFile, String storeFilePass, String keyAlias) {

		// 读取密钥是所要用到的工具类
		KeyStore ks;

		// 公钥类所对应的类
		PublicKey pubkey = null;
		try {

			// 得到实例对象
			ks = KeyStore.getInstance("JKS");
			FileInputStream fin;
			try {

				// 读取JKS文件
				fin = new FileInputStream(keyStoreFile);
				try {
					// 读取公钥
					ks.load(fin, storeFilePass.toCharArray());
					java.security.cert.Certificate cert = ks.getCertificate(keyAlias);
					pubkey = cert.getPublicKey();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (CertificateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return pubkey;
	}

	/**
	 * 得到私钥
	 * 
	 * @param keyStoreFile  私钥文件
	 * @param storeFilePass 私钥文件的密码
	 * @param keyAlias      别名
	 * @param keyAliasPass  密码
	 * @return
	 */

	public static PrivateKey getPrivateKey(String keyStoreFile, String storeFilePass, String keyAlias,
			String keyAliasPass) {
		KeyStore ks;
		PrivateKey prikey = null;
		try {
			ks = KeyStore.getInstance("JKS");
			FileInputStream fin;
			try {
				fin = new FileInputStream(keyStoreFile);
				try {
					try {
						ks.load(fin, storeFilePass.toCharArray());
						// 先打开文件
						prikey = (PrivateKey) ks.getKey(keyAlias, keyAliasPass.toCharArray());
						// 通过别名和密码得到私钥
					} catch (UnrecoverableKeyException e) {
						e.printStackTrace();
					} catch (CertificateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return prikey;
	}

	public static void main(String[] args) {
		PublicKey publicKey;
		PrivateKey privateKey=null;

		publicKey = getPublicKey("/home/weizheng/下载/127.0.0.1_keystore.jks", "linfutong_pwd", "autopay.test.lnafk.com");
		
		//System.out.println(publicKey.toString());
		String publicKeyStr = new String(Base64.encodeBase64URLSafeString(publicKey.getEncoded()));
		System.out.println("jks文件中的公钥:" + publicKeyStr);
		System.out.println("-------");
		//System.out.println(privateKey.toString());
		String privateKeyStr = new String(Base64.encodeBase64URLSafeString(privateKey.getEncoded()));
		System.out.println("jks文件中的私钥:" + privateKeyStr);
		
		try {
			/*String info = "加密测试";
			System.out.println("-------");
			System.out.println("加密:" + publicEncrypt(info, (RSAPublicKey)publicKey));
			System.out.println("解密:" + privateDecrypt(info, (RSAPrivateKey)privateKey));*/
			//RsaCoder rsaCoder = new RsaCoder();
			String info = "测试样例";
	    	String encryptByPublicKey = RsaCoder.encryptByPublicKey(info, publicKeyStr);
	    	System.out.println("encryptByPublicKey:"+encryptByPublicKey);
	    	String decryptByPrivateKey = RsaCoder.decryptByPrivateKey(encryptByPublicKey, privateKeyStr);
	    	System.out.println("decryptByPrivateKey:"+decryptByPrivateKey);
		}catch(Exception e) {
			System.out.println("error");
		}
		
	}
	
	 /**
     * 得到公钥
     * @param publicKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        return key;
    }

    /**
     * 得到私钥
     * @param privateKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        return key;
    }

	
	/**
     * 公钥加密
     * @param data
     * @param publicKey
     * @return
     */
    public static String publicEncrypt(String data, RSAPublicKey publicKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), publicKey.getModulus().bitLength()));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥解密
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateDecrypt(String data, RSAPrivateKey privateKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), privateKey.getModulus().bitLength()), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥加密
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateEncrypt(String data, RSAPrivateKey privateKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), privateKey.getModulus().bitLength()));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 公钥解密
     * @param data
     * @param publicKey
     * @return
     */

    public static String publicDecrypt(String data, RSAPublicKey publicKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), publicKey.getModulus().bitLength()), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize){
        int maxBlock = 0;
        if(opmode == Cipher.DECRYPT_MODE){
            maxBlock = keySize / 8;
        }else{
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try{
            while(datas.length > offSet){
                if(datas.length-offSet > maxBlock){
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                }else{
                    buff = cipher.doFinal(datas, offSet, datas.length-offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        }catch(Exception e){
            throw new RuntimeException("加解密阀值为["+maxBlock+"]的数据时发生异常", e);
        }
        byte[] resultDatas = out.toByteArray();
        IOUtils.closeQuietly(out);
        return resultDatas;
    }
	
}