package com.caipiao.common.pay.kj412;

import com.caipiao.common.pay.kj412.util.HttpUtil;
import com.caipiao.common.pay.kj412.util.StringUtils;
import net.sf.json.JSONObject;

/**
 * 生成订单交易demo
 * @author lzh
 *
 */
public class PaymentDemo {
	private static String sha512key="EC3490C296C8DD9EFB1F9E64ABA282E6";//测试key
	private static String postAddress="http://139.198.2.91:38089/forward_jt/service";//测试地址
	public static void main(String[] args) {
		//按文档组装json报文
		JSONObject requestData = new JSONObject();
		requestData.put("service", "hc.createorder");//接口路由
		requestData.put("merchantcode", "201805140000001");//商户号
		requestData.put("merchorder_no", "dd201808060015");//商户订单号
		requestData.put("money", "0.10");//金额
		requestData.put("paytype", "1");//交易类型
		requestData.put("backurl", "https://www.baidu.com");//异步通知地址
		requestData.put("subject", "测试");//商品标题
		requestData.put("returnurl", "https://www.baidu.com");//同步跳转地址
		requestData.put("sendip", "192.168.1.1");//用户ip
		requestData.put("transdate", "20170417121212");//订单发送时间
		requestData.put("sign", "");//签名
		//报文加密
		String sign = StringUtils.signSHA512(requestData.toString() +sha512key);
		//填充签名
		requestData.put("sign", sign);
		System.out.println(requestData.toString());
		String requestStr= HttpUtil.httpPost(requestData.toString(), postAddress);
		System.out.println("返回报文:"+requestStr);
	}
}
