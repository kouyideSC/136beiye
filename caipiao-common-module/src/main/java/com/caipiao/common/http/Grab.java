package com.caipiao.common.http;

import com.caipiao.common.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.org.mozilla.javascript.internal.UniqueTag;

import java.util.Calendar;
import java.util.Date;

/**
 * 数据抓取
 * @author kouyi
 * @param <T> 返回对象
 * @param <K> 解析处理对象
 */
public abstract class Grab<T, K> {
	private static final Logger logger = LoggerFactory.getLogger(Grab.class);
	public static String CHARTSET_UTF8 = "UTF-8";
	public static String CHARTSET_GBK = "GBK";
	private GrabHttp http;
	private GrabFilter filter;
	public GrabResult result;

	public Grab() {
		filter = new GrabFilter();
		http = new GrabHttp();
	}

	/**
	 * 抓取网页内容
	 * @param url
	 * @param k
	 * @param charset
	 * @param host
	 * @return
	 */
	public T collect(String url, K k, String charset, String host) {
		T obj = null;
		if(url.startsWith("https")) {
			result = http.getContentFromUrlHttps(url, charset, host);
		} else {
			result = http.getContentFromUrl(url, charset, host);
		}
		if (filter.check(result)) {
			try {
				if(!StringUtils.isBlank(result.getContent())){
					obj = (T) parse(result.getContent(), k);
				}			
			} catch (Exception e) {
				logger.error("数据抓取异常,状态码:{},URL:{}", result.getStatusCode(), url, e);
				result.setRemark("抓取异常");
			}
		} else {
			result.setRemark("抓取错误");
			logger.error("数据抓取错误,状态码:{},URL:{}", result.getStatusCode(), url);
		}
		return obj;
	}

	public abstract T parse(String content, K k) throws Exception;

	protected String parseString(Object obj) {
		return obj == null ? "" : obj.toString();
	}

	protected Double parseDouble(Object obj) {
		try{
			if (UniqueTag.NOT_FOUND == obj) {
				return null;
			}
			return Double.parseDouble(obj.toString());
		}catch (Exception e) {
			return 0.0;
		}
	}

	protected Long parseLong(Object obj) {
		try{
			if (UniqueTag.NOT_FOUND == obj) {
				return null;
			}
			return Long.parseLong(obj.toString());
		} catch (Exception e) {
			return 0l;
		}
	}

	protected Integer parseInt(Object obj) {
		try{
			if (UniqueTag.NOT_FOUND == obj) {
				return null;
			}
			return Integer.parseInt(obj.toString());
		}catch (Exception e) {
			return 0;
		}
	}

	protected Boolean parseBool(Object obj) {
		try{
			if (UniqueTag.NOT_FOUND == obj) {
				return null;
			}
			return Boolean.parseBoolean(obj.toString());
		}catch (Exception e) {
			return false;
		}
	}
	
	protected Short parseShort(Object obj) {
		try{
			if (UniqueTag.NOT_FOUND == obj) {
				return null;
			}
			return Short.parseShort(obj.toString());
		}catch (Exception e) {
			return 0;
		}
	}
	
	protected Date parseStringDate(Object obj) {
		try{
			if (UniqueTag.NOT_FOUND == obj) {
				return null;
			}
			String[] dates = parseString(obj).split("\\,");
			Calendar cal = Calendar.getInstance();
			cal.set(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]),
					Integer.parseInt(dates[3]), Integer.parseInt(dates[4]), Integer.parseInt(dates[5]));
			return DateUtil.dateDefaultFormat(DateUtil.dateDefaultFormat(cal.getTime()));
		} catch (Exception e) {
			return null;
		}
	}
}
