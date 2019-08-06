package com.caipiao.common.util;

import java.util.ArrayList;
import java.util.List;

import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息/信息推送工具类
 * @author	mcdog
 */
public class PushMsgUtil 
{
	private static Logger logger = LoggerFactory.getLogger(PushMsgUtil.class);
	
	/**
	 * 推送批量消息到客户端
	 * @author	sjq
	 * @param 	params	参数对象,至少包含pushAddress和list参数(多个消息对象集合),其中单个对象参数具体对象定义如下:</br>
	 * 					ptype-推送类型,为1时表示推送给所有的客户端,默认推送给指定aid的客户端 </br>
	 * 					aid,需要推送的帐户id(ptype!=1时有效) </br>
	 */
	public static void pushMsgsToClient(Dto params)
	{
		try
		{
			if(StringUtil.isEmpty(params.get("pushAddress")))
			{
				logger.error("[推送批量消息到客户端]推送地址不能为空!");
				return;
			}
			if(params.get("list") != null)
			{
				String pushAddress = params.getAsString("pushAddress");
				List<Dto> dataList = params.getAsList("list"); 
				for(Dto data : dataList)
				{
					if(StringUtil.isNotEmpty(data.get("ptype")) || StringUtil.isNotEmpty(data.get("aid")))
					{
						HttpClientUtil.callHttpPost_Dto(pushAddress,data);
					}
				}
			}
		}
		catch(Exception e)
		{
			logger.error("[推送批量消息到客户端]发生异常!异常信息:",e);
		}
	}
	
	/**
	 * 推送消息到客户端
	 * @author	mcdog
	 * @param 	params	参数对象,至少包含如下参数:</br>
	 *                  1. pushAddress,推送地址
	 * 					2. ptype-推送类型,为1时表示推送给所有的客户端,默认推送给指定帐户客户端 </br>
	 * 					3. aid-需要推送的帐户id(ptype=1时可为空)</br>
	 */
	public static void pushMsgToClient(Dto params)
	{
		try
		{
			if(StringUtil.isEmpty(params.get("pushAddress")))
			{
				logger.error("[推送消息到客户端]推送地址不能为空!");
				return;
			}
			if(StringUtil.isNotEmpty(params.get("ptype")) || StringUtil.isNotEmpty(params.get("aid")))
			{
				HttpClientUtil.callHttpPost_Dto(params.getAsString("pushAddress"),params);
			}
		}
		catch(Exception e)
		{
			logger.error("[推送消息到客户端]发生异常!异常信息:",e);
		}
	}

	public static void main(String[] args) 
	{
		//推送批量消息(单个帐户)
		List<Dto> list = new ArrayList<Dto>();
		Dto dto = new BaseDto();
		dto.put("aid",27);
		dto.put("cptotal",1);
		list.add(dto);
		//dto = new BaseDto();
		//dto.put("aid",9);
		//dto.put("cptotal",2);
		//list.add(dto);
		Dto params = new BaseDto();
		params.put("pushAddress","http://47.101.36.76:9999/manager/sendmsg");
		params.put("list",list);
		//pushMsgsToClient(params);
		
		//推送批量消息(所有客户端)
		list = new ArrayList<Dto>();
		dto = new BaseDto();
		dto.put("ptype",1);
		dto.put("cptotal",2);
		list.add(dto);
		params = new BaseDto();
		params.put("pushAddress","http://47.101.36.76:9999/cpadmin/sendmsg");
		params.put("list",list);
		//pushMsgsToClient(params);
		
		//推送单条消息(单个帐户)
		dto = new BaseDto();
		dto.put("aid",9);
		dto.put("cptotal",1);
		dto.put("pushAddress","http://47.101.36.76:9999/manager/sendmsg");
		pushMsgToClient(dto);
		
		//推送单条消息(所有客户端)
		dto = new BaseDto();
		dto.put("ptype",1);
		dto.put("cptotal",1);
		dto.put("pushAddress","http://47.101.36.76:9999/cpadmin/sendmsg");
		//pushMsgToClient(dto);
	}
}