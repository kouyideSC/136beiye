package com.caipiao.common.pay.ttpay;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpSendUtil {
	
	public static String getContents(InputStream in ) {
		BufferedReader bre = null;
		StringBuffer sb = new StringBuffer();
		String contents = "";
		try {
			bre = new BufferedReader(new InputStreamReader(in));
			while ((contents = bre.readLine()) != null) {// 判断最后一行不存在，为空结束循环
				sb.append(contents);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(bre!=null){
					bre.close();
				}
			} catch (Exception e2) {
			}
			try {
				if(in!=null){
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	public static String doHttpAndHttps(String url, String requestMessage)
			throws Exception {
		HttpURLConnection urlConnection = null;
		InputStream in = null;
		OutputStream out = null;
		try {
			urlConnection = openUrlConn(url);
			byte[] b = requestMessage.getBytes();
			out = urlConnection.getOutputStream();
			out.write(b);
			out.flush();
			in = urlConnection.getInputStream();
			String res = getContents(in);
			return res;
		} catch (Exception ce) {
			ce.printStackTrace();
			throw ce;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
				} finally {
					out = null;
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
				} finally {
					in = null;
				}
			}
			if (urlConnection != null) {
				urlConnection.disconnect();
				urlConnection = null;
			}
		}
	}

	private static HttpURLConnection openUrlConn(String url) throws Exception {
		URL httpurl = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) httpurl
				.openConnection();
		try {
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "text/plain");
			connection.setDoOutput(true);
			connection.setConnectTimeout(60*10*1000);
			connection.setReadTimeout(60*10*1000);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			return connection;
		} catch (Exception e) {
			throw new Exception("对方URL无法连接" + url, e);
		} finally {
			connection.disconnect();
			connection = null;
		}

	}
	
	
	public static int length(String value) {
		int valueLength = 0;
		String chinese = "[\u0391-\uFFE5]";
		/* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
		for (int i = 0; i < value.length(); i++) {
			/* 获取一个字符 */
			String temp = value.substring(i, i + 1);
			/* 判断是否为中文字符 */
			if (temp.matches(chinese)) {
				/* 中文字符长度为2 */
				valueLength += 2;
			} else {
				/* 其他字符长度为1 */
				valueLength += 1;
			}
		}
		return valueLength;
	}
	

}
