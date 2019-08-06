package com.caipiao.common.pay.kuaijie;

import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.util.HttpClientUtil;
import com.caipiao.domain.cpadmin.Dto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * +----------------------------------------------------------------------------
 * 快接支付-微信WAP支付(H5)的JAVA-demo示例 （注：其他接口的请求方式和签名规则都是通用的。）
 * +----------------------------------------------------------------------------
 * @author gd <464364696@qq.com>
 * @version v0.1.0 Build 2018.03.07
 * +------------------------------------------------------------------------------
 */
public class demo {
	public static void main(String[] args){
		//请求接口地址
		String url 			= "http://8af7538780.api.kj-pay.com/wechar/wap_pay";

		PrintWriter out 	= null;
		BufferedReader in 	= null;

		//拼装请求的字符串
		String param 		= "";
		//商户号
		String merchant_no  = "2018587955";
		//密钥
		String key 			= "dc94619a019b9b5de49136bafa50c74b";
		//签名
		String sign 		= "";
		//响应的结果(json格式)
		String result 		= "";

		Map<String, String> map = new HashMap<String, String>();
		map.put("merchant_no", merchant_no);

		map.put("merchant_order_no", "CZ201803121731202601");
		map.put("notify_url", "http://api.szmpyd.com/api/notify/weixin");

		//map.put("return_url", "http://www.kj-pay.com");

		map.put("start_time", "20180312140856");
		map.put("trade_amount", "0.01");

		//注：参数的值为中文必须得进行转码
		map.put("goods_name", demo.getEncoding("广博网络"));
		map.put("goods_desc", demo.getEncoding("广博网络"));

		map.put("user_ip", "114.92.14.194");
		map.put("pay_sence", demo.getEncoding("{\"type\":\"IOS\",\"bundle_id\":\"cn.RedSunLottery\",\"app_name\":\"广博网络\"}"));
		map.put("sign_type", "1");

		param 		= demo.buildSignStr(map);

		sign = "&sign="+getSign(param, key);
		param = param+sign;
		System.out.println("params:" + param);

		//System.out.println(param);
		try {
			URL realUrl = new URL(url);
			//打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();

			//设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");

			//发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);

			//获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			//发送请求参数
			out.print(param);
			//flush输出流的缓冲
			out.flush();
			//定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送POST请求出现异常！"+e);
			e.printStackTrace();
		}
		//使用finally块来关闭输出流、输入流
		finally{
			try{
				if(out!=null){
					out.close();
				}
				if(in!=null){
					in.close();
				}
			}
			catch(IOException ex){
				ex.printStackTrace();
			}
		}

		//返回的json 请您自行解析...
		System.out.println("result=" + result);
		Dto respDto = JsonUtil.jsonToDto(result);
		String status = respDto.getAsString("status");//提取状态码

		/*
		*=============================================================================================================================
		*以下预下单成功响应的json数据
		*{
		*	data :
		*	{
		*		trade_no : "K201710281509354698545982",
		*		pay_url : "https://wx.tenpay.com/cgi-bin/mmpayweb-bin/checkmweb?prepay_id=wx20171206192330fa68120fe20858600673&package=3508966682&redirect_url=http%3A%2F%2F%2Fh5pay.640game.com%2Fpeipeiuser%2Fhtml%2FpayOK.html",
		*		sign : "007321008c37b2a5810166bf185a1694"
		*	},
		*	info : "预交易下单成功",
		*	status : "1"
		*}
		*注:需要对data里面的字段进行签名验证（sign除外）保证数据的准确性和安全性。签名的方法和请求的方法一致，就不详情去写了......
		*=============================================================================================================================
		*/
	}

	/**
	 * 对字符串进行UTF-8转码
	 *
	 * @param str
	 * @return 字符串
	 */
	public static String getEncoding(String str) {
		String res = "";
		try {
			res = URLEncoder.encode(str, "UTF-8");
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * 对字符串进行UTF-8解码
	 *
	 * @param str
	 * @return 字符串
	 */
	public static String getDecoding(String str) {
		String res = "";
		try {
			res = URLDecoder.decode(str, "UTF-8");
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * 对Map进行排序
	 *
	 * @param params Map
	 * @return  a=1&b=2... 这样的字符串
	 */
	public static String buildSignStr(Map<String, String> params) {
		StringBuilder sb = new StringBuilder();
		// 将参数以参数名的字典升序排序
		Map<String, String> sortParams = new TreeMap<String, String>(params);
		// 遍历排序的字典,并拼接"key=value"格式
		for (Map.Entry<String, String> entry : sortParams.entrySet()) {
			if (sb.length()!=0) {
				sb.append("&");
			}
			sb.append(entry.getKey()).append("=").append(entry.getValue());
		}
		return sb.toString();
	}

	/**
	 * 对字符串md5加密
	 *
	 * @param str 传入要加密的字符串
	 * @return  MD5加密后的字符串
	 */
	public static String getMD5(String str) {
		try {
			// 生成一个MD5加密计算摘要
			MessageDigest md = MessageDigest.getInstance("MD5");
			// 计算md5函数
			md.update(str.getBytes("UTF-8"));
			// digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
			// BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
			String md5=new BigInteger(1, md.digest()).toString(16);
			//BigInteger会把0省略掉，需补全至32位
			return fillMD5(md5);
		} catch (Exception e) {
			throw new RuntimeException("MD5加密错误:"+e.getMessage(),e);
		}
	}

	public static String fillMD5(String md5){
		return md5.length()==32?md5:fillMD5("0"+md5);
	}

	/**
	 * 快接签名算法
	 *
	 * @param str  传入排序后的字符串
	 * @param key  密钥
	 * @return  MD5加密后的字符串
	 */
	public static String getSign(String str, String key){
		String sign = getMD5(getDecoding(str)+"&key="+key);
		return sign;
	}
}