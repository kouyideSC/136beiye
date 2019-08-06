package com.caipiao.common.pay.haipay.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

public class HttpsUtil {
	
	public static final String KEY_STORE_TRUST_PATH = "/home/weizheng/下载/127.0.0.1_keystore.jks"; // truststore的路径
	public static final String KEY_STORE_TRUST_PASSWORD = "linfutong_pwd"; // truststore的密码
	public static final String CHAR_SET = "utf-8";

	public static CloseableHttpClient getHttpsClient() {
		return getHttpsClient(KEY_STORE_TRUST_PATH, KEY_STORE_TRUST_PASSWORD);
	}
	
	public static CloseableHttpClient getHttpsClient(String keyFilePath, String keyPWD) {
		CloseableHttpClient httpClient = null;
		try {
			SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(new File(keyFilePath), keyPWD.toCharArray()).build();
			SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext);
	    	httpClient = HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return httpClient;
	}
	
	public static String post(CloseableHttpClient httpsClient, String longinUrl, String token, JSONObject param) {
		String resultStr = null;
		HttpPost httpPost = new HttpPost(longinUrl);
		httpPost.setHeader("Accept-Encoding", "gzip,deflate");//表示返回的数据是压缩的zip格式
		if(token!=null) {
			httpPost.addHeader("AutoPay_PayApi_Token", token);
		}
    	// 创建请求参数 使用URL实体转换工具
    	UrlEncodedFormEntity entityParam = getParamEntry(param, CHAR_SET);
        httpPost.setEntity(entityParam);
		try {
			CloseableHttpResponse response = httpsClient.execute(httpPost);
			resultStr = EntityUtils.toString(response.getEntity());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	    return resultStr;
		
	}
	
	public static UrlEncodedFormEntity getParamEntry(JSONObject jsonObj, String charSet) {
		UrlEncodedFormEntity entityParam = null;
		List<BasicNameValuePair> list = new LinkedList<>();
        for(Entry<String, Object> entry: jsonObj.entrySet()) {
        	String key = entry.getKey();
        	String value = entry.getValue().toString();
        	BasicNameValuePair param = new BasicNameValuePair(key, value);
        	list.add(param);
        }
        try {
			entityParam = new UrlEncodedFormEntity(list, charSet);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        return entityParam;
	}
}
