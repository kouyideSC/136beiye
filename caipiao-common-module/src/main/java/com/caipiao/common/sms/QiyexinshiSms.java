package com.caipiao.common.sms;

import com.caipiao.common.util.HttpClientUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.common.MessageCode;
import com.mina.rbc.util.xml.JXmlWapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import net.sf.json.JSONObject;
import org.apache.http.HttpStatus;

import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * QiyexinshiSms短信厂商发送工具类
 * Created by kouyi on 2019/08/01.
 */
public class QiyexinshiSms {
	private static String url = "http://218.244.141.161:8888/sms.aspx?";
	private static String params = "action=send&userid=1647&account=bl1820&password=beanli1820";
	private static String register_Content = "您注册会员的验证码是{0}，有效期10分钟后失效【136】";//注册验证码
	private static String backPwd_Content = "您本次找回密码操作的验证码是{0}，有效期10分钟后失效【136】";//找回密码
	private static String smrz_content = "您的实名认证验证码是{0}，有效期10分钟后失效【136】";

	public static void main(String[] args) {
		MessageCode code = new MessageCode();
		code.setMobile("18901694565");
		code.setContent("333333");
		code.setType(4);
		smsSend(code);
		System.out.println(code.getSendInfo());
	}

	/**
	 * 是否需要重新发送(true-发送失败 false-发送成功)
	 * @param code
	 * @return
	 */
	public static boolean isReSend(Integer code) {
		if(StringUtil.isEmpty(code)) {
			return false;
		}
		if(code == -1 || code == -200) {
			return true;
		}
		return false;
	}

	/**
	 * 短消息发送
	 * @param message
	 */
	public static void smsSend(MessageCode message) {
		try {
			if (StringUtil.isEmpty(message)) {
				return;
			}

			if(StringUtil.isEmpty(message.getMobile()) || StringUtil.isEmpty(message.getContent())) {
				return;
			}

			if(StringUtil.isEmpty(message.getType())) {
				message.setType(1);
			}

			//会员注册
			if(message.getType() == 1) {
				message.setContent(MessageFormat.format(register_Content, new Object[]{message.getContent()}));
			}
			//更换手机号
			else if(message.getType() == 2) {

			}
			//找回密码
			else if(message.getType() == 3) {
				message.setContent(MessageFormat.format(backPwd_Content, new Object[]{message.getContent()}));
			}
			//实名认证
			else if(message.getType() == 4) {
				message.setContent(MessageFormat.format(smrz_content, new Object[]{message.getContent()}));
			}
			//会员注册
			else {
				message.setContent(MessageFormat.format(register_Content, new Object[]{message.getContent()}));
			}

			params = params + "&mobile=" + message.getMobile() + "&content=" + message.getContent();
			String response = HttpClientUtil.callHttpGet(url+params);
			JXmlWapper responseXml = JXmlWapper.parse(response);
			String status = responseXml.getStringValue("returnstatus");
			String msg = responseXml.getStringValue("message");
			if(status.equals("Success")) {
				message.setSendCode(0);
			} else {
				message.setSendCode(-1);
			}
			message.setSendInfo(msg);
		} catch (Exception e) {
			message.setSendCode(-200);
			message.setSendInfo(e.getMessage());
		}
	}

}

