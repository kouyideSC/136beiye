package com.caipiao.grab.http;

import com.caipiao.common.util.StringUtil;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * httpclient执行抓取动作
 * @author kouyi
 */
public class GrabHttp {
	private static final Logger logger = LoggerFactory.getLogger(GrabHttp.class);
	private DefaultHttpClient client;
	private final static String defaultHost = "";

	/**
	 * 创建HttpClient实例，并初始化连接参数
	 */
	public GrabHttp() {
		client = (DefaultHttpClient) GrabHttpManager.getHttpClient();
	}

	/**
	 * 根据url爬取网页内容-常规
	 * 
	 * @param url
	 * @return
	 */
	public GrabResult getContentFromUrl(String url, String charset, String host) {
		if(StringUtil.isEmpty(host)) {
			host = defaultHost;
		}

		HttpGet getHttp = new HttpGet(url);
		getHttp.addHeader("Accept-Charset", "GBK,utf-8");
		getHttp.addHeader("Accept", "*/*");
		getHttp.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
		getHttp.addHeader("Cookie", "");
		getHttp.addHeader("Host", host);
		getHttp.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.221 Safari/537.36 SE 2.X MetaSr 1.0");
		HttpResponse response;
		String content = null;
		int statusCode = 500;
		try {
			response = client.execute(getHttp);
			statusCode = response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				content = EntityUtils.toString(entity, charset);
			}
		} catch (Exception e) {
			logger.error("数据抓取异常", url, e);
		} finally {
			getHttp.releaseConnection();
		}
		return new GrabResult(url, content, statusCode);
	}

	/*public static void main(String[] args) throws Exception {
		String sl = new GrabHttp().getContentFromUrl("http://i.sporttery.cn/api/fb_match_info/get_pool_rs/?f_callback=pool_prcess&mid=105706&_=1521709197367", "utf-8", "i.sporttery.cn").getContent();
		if(sl.startsWith("pool_prcess(")) {
			sl = sl.replaceFirst("pool_prcess\\(","");
		}
		if(sl.endsWith(");")) {
			sl = sl.substring(0, sl.length()-2);
		}
		System.out.println(sl);
		JSONObject jsonObj = JSONObject.fromObject(sl);
		String code = jsonObj.getJSONObject("status").get("code").toString();
		System.out.println(code);
	}*/
}
