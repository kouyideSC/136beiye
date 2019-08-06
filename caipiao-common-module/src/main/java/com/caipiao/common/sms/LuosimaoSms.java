package com.caipiao.common.sms;

import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.common.MessageCode;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.tools.classfile.StackMapTable_attribute;
import net.sf.json.JSONObject;
import org.apache.http.HttpStatus;

import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * LuosimaoSms短信厂商发送工具类
 * Created by kouyi on 2017/11/14.
 */
public class LuosimaoSms {
	private static String url = "http://sms-api.luosimao.com/v1/send.json";
	private static String key = "key-cefa615cee7b46f01cfff7e312f27955";
	private static String register_Content = "您注册176商城会员的验证码是{0}，有效期10分钟后失效【176商城】";//注册验证码
	private static String backPwd_Content = "您本次找回密码操作的验证码是{0}，有效期10分钟后失效【176商城】";//找回密码
	private static String smrz_content = "您的实名认证验证码是{0}，有效期10分钟后失效【176商城】";

	private static Map<Integer, String> descMap = new HashMap<>();
	static {
		descMap.put(0, "发送成功");
		descMap.put(-10, "key不正确");
		descMap.put(-11, "滥发违规内容，验证码被刷等，请联系客服解除");
		descMap.put(-20, "短信余额不足");
		descMap.put(-30, "短信内容为空");
		descMap.put(-31, "短信内容存在敏感词");
		descMap.put(-32, "短信内容缺少签名信息");
		descMap.put(-33, "短信过长，超过300字(含签名)");
		descMap.put(-34, "签名不可用");
		descMap.put(-40, "错误的手机号");
		descMap.put(-41, "号码在黑名单中");
		descMap.put(-42, "验证码类短信发送频率过快");
		descMap.put(-50, "请求发送IP不在白名单内");
		descMap.put(-100, "网络连接失败");
	}

	public static void main(String[] args) {
		MessageCode code = new MessageCode();
		code.setMobile("18901694565");
		code.setContent("456789");
		code.setType(3);
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
		if(code == -10 || code == -20 || code == -32 || code == -33 ||code == -34 || code == -50 || code == -100 || code == -200) {
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
			Client client = Client.create();
			client.addFilter(new HTTPBasicAuthFilter("api", key));
			WebResource webResource = client.resource(url);
			MultivaluedMapImpl formData = new MultivaluedMapImpl();
			formData.add("mobile", message.getMobile());
			if(StringUtil.isEmpty(message.getType())) {
				message.setType(1);
			}
			if(message.getType() == 2) {//更换手机号

			}
			else if(message.getType() == 3) {//找回密码
				formData.add("message", MessageFormat.format(backPwd_Content, new Object[]{message.getContent()}));
			}
			//实名认证
			else if(message.getType() == 4)
			{
				formData.add("message", MessageFormat.format(smrz_content, new Object[]{message.getContent()}));
			}
			else {
				formData.add("message", MessageFormat.format(register_Content, new Object[]{message.getContent()}));
			}

			ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
			int status = response.getStatus();
			if(status == HttpStatus.SC_OK) {
				JSONObject jsonObj = JSONObject.fromObject(response.getEntity(String.class));
				int error_code = jsonObj.getInt("error");
				message.setSendCode(error_code);
				message.setSendInfo(descMap.get(error_code));
			} else {
				message.setSendCode(-100);
				message.setSendInfo(descMap.get(-100));
			}
		} catch (Exception e) {
			message.setSendCode(-200);
			message.setSendInfo(e.getMessage());
		}
	}

}

