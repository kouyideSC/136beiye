package com.caipiao.common.pay.haipay.test;

import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.caipiao.common.pay.haipay.Exception.CoderException;
import com.caipiao.common.pay.haipay.util.AESCoder;
import com.caipiao.common.pay.haipay.util.HttpsUtil;
import com.caipiao.common.pay.haipay.util.JKSUtil;
import com.caipiao.common.pay.haipay.util.SignUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.impl.client.CloseableHttpClient;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class AtpPayTest {

	public static final String SERVER_UTL="autopay.test.lnafk.com:8443";
	//public static final String SERVER_UTL = "127.0.0.1:8443";
	public static final String LONGIN_URL = "https://".concat(SERVER_UTL).concat("/api/auth");
	public static final String ATP_GET_URL = "https://".concat(SERVER_UTL).concat("/api/atp/get");
	public static final String ATP_APPLY_URL = "https://".concat(SERVER_UTL).concat("/api/atp/apply");
	public static final String ATP_DETAILS_GET_URL = "https://".concat(SERVER_UTL).concat("/api/atp/details/get");
	
	public static void main(String[] arge) {
		CloseableHttpClient  httpsClient = HttpsUtil.getHttpsClient(HttpsUtil.KEY_STORE_TRUST_PATH, HttpsUtil.KEY_STORE_TRUST_PASSWORD);
		String token = login(httpsClient);
		
		if(token!=null) {
			System.out.println("token:".concat(token));
			
			String batchid = UUID.randomUUID().toString().replaceAll("-", "");
			//String batchid="93c99b558b2247bbb07a3fee1a62b1d9";
			String applyDataStr = getApplyData(token, batchid);
			JSONObject jsonObj_apply = send(httpsClient, ATP_APPLY_URL, applyDataStr, token, token);
			System.out.println("resuslt:".concat(jsonObj_apply.toJSONString()));
			/**/
			
			/*
			String batchid = "bd1c12661efa4e2188a024917e13a02a";
			String getDataStr = getGetData(token, batchid);
			JSONObject jsonObj_get = send(httpsClient, ATP_GET_URL, getDataStr, token, token);
			System.out.println("resuslt:".concat(jsonObj_get.toJSONString()));
			*/
			
			/*
			//String batchid = "02916fc0b5a44362adc16addfa6962ce";
			String thirdVoucher = "02916fc0b5a44362adc16addfa6962ce";
			String getDetailsDataStr = getDetailsGetData(token, batchid, thirdVoucher);
			JSONObject jsonObj_detailsGet = send(httpsClient, ATP_DETAILS_GET_URL, getDetailsDataStr, token, token);
			System.out.println("resuslt:".concat(jsonObj_detailsGet.toJSONString()));
			*/
		}
	}
	
	private static String login (CloseableHttpClient  httpsClient) {
		JSONObject param = new JSONObject();
		param.put("username", "20000099");
		param.put("password", "111111abc");
		param.put("userType", "1");
		String text = HttpsUtil.post(httpsClient, LONGIN_URL, "refresh_token", param).replaceAll("\r|\n", "").trim();
		JSONObject jsonObj = JSONObject.parseObject(text);
		System.out.println("login resutl:".concat(jsonObj.toJSONString()));
		if("0".equals(jsonObj.getString("code")) && "success".equals(jsonObj.getString("msg"))) {
    		String token = jsonObj.getJSONObject("data").getString("access_token");
    		return token;
		}else {
			return null;
		}
	}
	
	private static String getApplyData(String key, String batchid) {
//		int settNumber = 10;
		BigDecimal settAmount = BigDecimal.valueOf(0);
		
		JSONArray dataArray = new JSONArray();
//		for(int i=0;i<settNumber;i++) {
			JSONObject pay = new JSONObject();
			pay.put("ThirdVoucher", UUID.randomUUID().toString().replaceAll("-", ""));//第三方主键
			//BigDecimal amount = BigDecimal.valueOf(i).multiply(BigDecimal.valueOf(100));
			pay.put("Amount", 2);//钱(分,整数)
			settAmount = settAmount.add(BigDecimal.valueOf(2));
			pay.put("PublicFlag", "2");//对公私标识,2:对私.目前只支持对公
			pay.put("BankAccountName", "孙成龙");//收款银行户名
			pay.put("BankAccountNumber", "6217003810026070147");//收款银行户名
//			pay.put("BankName","招商银行");//开户银行
//			pay.put("BankNetName","123123");//开户网点名称
//			pay.put("BankNumber","123123");//银行联行号
//			pay.put("Phone","1888888888888");//银行卡手机号
//			pay.put("BankProvince","辽宁省");//开户行所在省
//			pay.put("BankCity","沈阳市");//开户行所在市
//			pay.put("InIDType","01");//证件类型,01:身份证
//			pay.put("InIDNo","21000000000000000X");//证件号码
			dataArray.add(pay);
//		}
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("Batchid", batchid);
		jsonObj.put("SettNumber", 1);
		jsonObj.put("SettAmount", 2);
		jsonObj.put("Remark", "Remark");
		jsonObj.put("PayPassword", "123456");
		jsonObj.put("Batch", dataArray);
		System.out.println("params:".concat(jsonObj.toJSONString()));
		String jsonObjStr = null;
		try {
			jsonObjStr = AESCoder.encrypt(jsonObj.toJSONString(),key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObjStr;
	}
	
	private static String getGetData(String key, String batchid) {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("Batchid", batchid);
		String jsonObjStr = null;
		try {
			jsonObjStr = AESCoder.encrypt(jsonObj.toJSONString(),key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObjStr;
	}
	
	private static String getDetailsGetData(String key, String batchid, String thirdVoucher) {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("Batchid", batchid);
		jsonObj.put("ThirdVoucher", thirdVoucher);
		String jsonObjStr = null;
		try {
			jsonObjStr = AESCoder.encrypt(jsonObj.toJSONString(),key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObjStr;
	}
	
	private static JSONObject send(CloseableHttpClient httpsClient, String url, String data, String token, String key) {
		String id = UUID.randomUUID().toString().replaceAll("-", "");
		
		Map<String, String> paramsHM = new HashMap();
		paramsHM.put("id", id);
		paramsHM.put("mchId", "20000099");
		paramsHM.put("data", data);
		paramsHM.put("version", "1.0");
		String signType = "RSA";
		String sign = createSign(paramsHM, signType);
		
		JSONObject param = new JSONObject();
		param.put("id", id);
		param.put("mchId", "20000094");
		param.put("data", data);
		param.put("version", "1.0");
		param.put("signType", signType);
		param.put("sign", sign);
		System.out.println("params:".concat(param.toJSONString()));
		String text  = HttpsUtil.post(httpsClient, url, token, param).replaceAll("\r|\n", "").trim();
		JSONObject jsonObj = JSONObject.parseObject(text);
		if(jsonObj.containsKey("data")) {
			String resultStr = jsonObj.getString("data");
			try {
				resultStr = AESCoder.decrypt(resultStr, key);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			jsonObj.put("data", JSONObject.parseObject(resultStr));
		}
		return jsonObj;
	}
	
	private static String createSign(Map<String, String> params, String signType) {
		String sign = null;
		try {
			PublicKey publicKey = JKSUtil.getPublicKey("/home/weizheng/下载/127.0.0.1_keystore.jks", "linfutong_pwd", "autopay.test.lnafk.com");
			//PublicKey publicKey = JKSUtil.getPublicKey("/home/weizheng/127.0.0.1_keystore.jks", "linfutong_pwd", "127.0.0.1");
			String publicKeyStr = new String(Base64.encodeBase64URLSafeString(publicKey.getEncoded()));
			sign = SignUtil.createSign(publicKeyStr, "sign", params, signType);
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sign;
	}
}
