package com.caipiao.common.pay.yizhi;

import com.caipiao.common.pay.yizhi.common.TestUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @ClassName: QuickPayDemo
 * @Description: 快捷交易demo
 * @date 2018年9月20日 下午1:55:28
 *
 */
public class QuickPayDemo {
	//测试用参数值
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	static String sdate=sdf.format(new Date());//格式化当前时间
	static String merchantOutOrderNo=sdf.format(new Date());//订单号，根据实际情况生成
	static String merid = "yft2017082500005";//分配的商户号
	static String noncestr="test2";//随机字符串，内容自定义
	static String orderMoney="10.00";//订单金额 单位 元
	static String orderTime=sdate;//下单时间
	static String sign="";//签名 请
	static String key = "gNociwieX1aCSkhvVemcXkaF9KVmkXm8";//商户号对应的密钥
	static String notifyUrl="ceshi";//用于接收回调通知的地址
	static String quickUrl="https://alipay.3c-buy.com/api/createQuickOrder";//支付接口地址 以接口文档为准		
	static String id="测试";//不参与验签
	
	//生成快捷支付链接
	public static void main(String[] args) throws Exception{
		String stringA=setParam();
		String stringsignTemp=stringA+"&key="+key;
		sign= TestUtil.getMD5(stringsignTemp);
		//将此链接送到前端页面打开，即可调起快捷支付
		String url=quickUrl+"?"+stringA+"&sign="+sign;
		System.out.print("url:"+url);
	}
	
	/**
	 * 对参数按照 key=value 的格式，并参照参数名 ASCII 码排序后得到字符串 stringA
	 * */
	private static String setParam() throws UnsupportedEncodingException {
		Map<String,String> paraMap = new HashMap<String,String>();
        paraMap.put("merchantOutOrderNo",merchantOutOrderNo);  
        paraMap.put("merid", merid);  
        paraMap.put("noncestr", noncestr);  
        paraMap.put("orderMoney",orderMoney);
        paraMap.put("orderTime", orderTime);  
        paraMap.put("notifyUrl",notifyUrl);
		String stringA =TestUtil.formatUrlMap(paraMap, true, false);
        stringA = URLDecoder.decode(stringA,"utf-8");
        return stringA;
	}	
}
