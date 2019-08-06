package com.caipiao.taskcenter.pay;

import com.caipiao.common.constants.PayConstants;
import com.caipiao.common.pay.bbpay.BbPayUtils;
import com.caipiao.common.pay.juhe.JuHe10381Utils;
import com.caipiao.common.pay.kj412.Kj412PayUtils;
import com.caipiao.common.pay.kuaijie.KuaiJieUtils;
import com.caipiao.common.pay.momo.MomoPayUtils;
import com.caipiao.common.pay.payfubao.PayFuBaoUtils;
import com.caipiao.common.pay.ttpay.TTPayUtils;
import com.caipiao.common.pay.weixin.WeixinConstants;
import com.caipiao.common.pay.weixin.WeixinUtils;
import com.caipiao.common.pay.wlpay.WlPayUtils;
import com.caipiao.common.pay.xunyoutong.XunYouTongUtils;
import com.caipiao.common.pay.yifutong.YifutongPayUtils;
import com.caipiao.common.pay.yizhi.YizhiPayUtils;
import com.caipiao.common.pay.ypay.YPayUtils;
import com.caipiao.common.pay.zhaohang.ZhaohangPayUtils;
import com.caipiao.common.pay.zhifu.ZhifuUtils;
import com.caipiao.common.pay.zhinengyun.ZhinengyunUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.user.UserDetailMapper;
import com.caipiao.dao.user.UserMapper;
import com.caipiao.dao.user.UserPayMapper;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 充值订单补单任务
 * @author 	sjq
 */
@Component("queryRechargeOrderTask")
public class QueryRechargeOrderTask
{
	private static Logger logger = LoggerFactory.getLogger(QueryRechargeOrderTask.class);

	@Autowired
	private UserPayMapper userPayMapper;

	@Autowired
	private UserDetailMapper userDetailMapper;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UserService userService;

	/**
	 * 查询待发送支付结果通知的充值订单记录,根据订单交易状态更新订单信息
	 * @author	sjq
	 */
	public void doTask()
	{
		try
		{
			//查询待发送支付结果通知的充值订单
			logger.info("[任务-充值订单补单]开始执行.");
			Dto queryParams = new BaseDto();
			queryParams.put("payType","0");//业务类型为充值(0-充值 1-提现)
			queryParams.put("status","2");//订单状态为处理中
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.HOUR_OF_DAY,-1);//当前日期往前推1个小时
			queryParams.put("minCreateTime",DateUtil.formatDate(calendar.getTime(),DateUtil.DEFAULT_DATE_TIME));//订单最早创建时间
			List<Dto> userPayList = userPayMapper.queryRechargeForNeedNotify(queryParams);//查询待发送支付结果通知的充值订单记录
			if(userPayList != null && userPayList.size() > 0)
			{
				logger.info("[任务-充值订单补单]本次共有" + userPayList.size() + "条充值订单补单.");
				for(Dto userpayDto : userPayList)
				{
					try
					{
						/**
						 * 设置订单查询参数
						 */
						Long userId = userpayDto.getAsLong("userId");
						Dto queryOrderDto = new BaseDto();
						queryOrderDto.put("merchantNo",userpayDto.get("merchantNo"));//设置商户号
						queryOrderDto.put("appNo",userpayDto.get("appNo"));//设置应用编号/产品编号
						queryOrderDto.put("appName",userpayDto.get("appName"));//设置应用名称/app名称/商品描述
						queryOrderDto.put("apiUrl",userpayDto.get("apiUrl"));//设置api地址
						queryOrderDto.put("signType",userpayDto.get("signType"));//设置签名方式
						queryOrderDto.put("secretKey",userpayDto.get("secretKey"));//设置签名密钥

						//设置商户订单号和渠道流水号
						String payId = userpayDto.getAsString("payId");//提取商户订单号
						String cpayId = userpayDto.getAsString("channelPayId");//提取渠道流水号
						queryOrderDto.put("payId",payId);//设置商户订单号
						queryOrderDto.put("cpayId",cpayId);//设置渠道流水号
						queryOrderDto.put("clientFrom",userpayDto.get("clientFrom"));//设置客户端来源
						queryOrderDto.put("deviceInfo",userpayDto.get("deviceInfo"));//设置设备信息

						/**
						 * 根据充值渠道查询订单交易状态
						 */
						Integer payCode = userpayDto.getAsInteger("payCode");//提取充值方式业务编号
						Integer channelCode = userpayDto.getAsInteger("channelCode");//提取充值渠道编号
						if(PayConstants.PAYCHANNEL_CODE_WEIXIN == channelCode)
						{
							/**
							 * 根据充值方式查询订单交易状态
							 */
							//微信app
							if(PayConstants.CHANNEL_CODE_IN_PAY_WEIXIN == payCode || PayConstants.CHANNEL_CODE_IN_PAY_WEIXINH5 == payCode)
							{
								WeixinUtils.queryOrder(queryOrderDto);
							}
						}
						//快接
						else if(PayConstants.PAYCHANNEL_CODE_KUAIJIE == channelCode
								|| PayConstants.PAYCHANNEL_CODE_KUAIJIE2 == channelCode)
						{
							KuaiJieUtils.queryOrder(queryOrderDto);
						}
						//贝付宝
						else if(PayConstants.PAYCHANNEL_CODE_PAYFUBAO == channelCode)
						{
							/**
							 * 根据充值方式查询订单交易状态
							 */
							//微信
							if(PayConstants.CHANNEL_CODE_IN_PAY_WEIXIN == payCode || PayConstants.CHANNEL_CODE_IN_PAY_WEIXINH5 == payCode)
							{
								PayFuBaoUtils.queryWeixinWapOrder(queryOrderDto);
							}
							//支付宝
							else if(PayConstants.CHANNEL_CODE_IN_PAY_ALIPAY == payCode || PayConstants.CHANNEL_CODE_IN_PAY_ALIPAYH5 == payCode)
							{
								PayFuBaoUtils.queryAlipayWapOrder(queryOrderDto);
							}
						}
						//威富通
						else if(PayConstants.PAYCHANNEL_CODE_SWIFTPASS == channelCode)
						{
							/**
							 * 根据充值方式查询订单交易状态
							 */
							//微信
							if(PayConstants.CHANNEL_CODE_IN_PAY_WEIXIN == payCode || PayConstants.CHANNEL_CODE_IN_PAY_WEIXINH5 == payCode)
							{
							}
						}
						//豆豆平台
						else if(PayConstants.PAYCHANNEL_CODE_DOUDOUPAY == channelCode)
						{
							/**
							 * 根据充值方式查询订单交易状态
							 */
							//微信
							if(PayConstants.CHANNEL_CODE_IN_PAY_WEIXIN == payCode || PayConstants.CHANNEL_CODE_IN_PAY_WEIXINH5 == payCode)
							{
							}
						}
						//迅游通
						else if(PayConstants.PAYCHANNEL_CODE_XUNYOUTONG == channelCode)
						{
							/**
							 * 根据充值方式查询订单交易状态
							 */
							//QQ钱包/京东钱包
							if(PayConstants.CHANNEL_CODE_IN_PAY_QQWALLETH5 == payCode
									|| PayConstants.CHANNEL_CODE_IN_PAY_JDWALLETH5 == payCode)
							{
								XunYouTongUtils.queryOrder(queryOrderDto);
							}
						}
						//聚合10381支付
						else if(PayConstants.PAYCHANNEL_CODE_JUHE10381PAY == channelCode)
						{
							JuHe10381Utils.queryOrder(queryOrderDto);
						}
						//直付支付
						else if(PayConstants.PAYCHANNEL_CODE_ZHIFUPAY == channelCode)
						{
							ZhifuUtils.queryOrder(queryOrderDto);
						}
						//智能云收银
						else if(PayConstants.PAYCHANNEL_CODE_ZHINENGYUNPAY == channelCode)
						{
							ZhinengyunUtils.queryOrder(queryOrderDto);
						}
						//傲游支付
						else if(PayConstants.PAYCHANNEL_CODE_AOYOUPAY == channelCode)
						{
							continue;//傲游支付暂未提供订单查询接口,跳过
						}
						//BB支付
						else if(PayConstants.PAYCHANNEL_CODE_BBPAY == channelCode)
						{
							BbPayUtils.queryOrder(queryOrderDto);
						}
						//陌陌付
						else if(PayConstants.PAYCHANNEL_CODE_MOMOPAY == channelCode)
						{
							MomoPayUtils.queryOrder(queryOrderDto);
						}
						//兆行支付
						else if(PayConstants.PAYCHANNEL_CODE_ZHAOXINGJUHEPAY == channelCode)
						{
							ZhaohangPayUtils.queryOrder(queryOrderDto);
						}
						//万两支付
						else if(PayConstants.PAYCHANNEL_CODE_WLPAY == channelCode)
						{
							WlPayUtils.queryOrder(queryOrderDto);
						}
						//亿富通支付
						else if(PayConstants.PAYCHANNEL_CODE_YIFUTONGPAY == channelCode)
						{
							queryOrderDto.put("apiUrl",YifutongPayUtils.queryApiUrl);
							YifutongPayUtils.queryOrder(queryOrderDto);
						}
						//易旨支付
						else if(PayConstants.PAYCHANNEL_CODE_YIZHIPAY == channelCode)
						{
							queryOrderDto.put("apiUrl", YizhiPayUtils.queryApiUrl);
							YizhiPayUtils.queryOrder(queryOrderDto);
						}
						//ypay
						else if(PayConstants.PAYCHANNEL_CODE_YPAY == channelCode)
						{
							YPayUtils.queryOrder(queryOrderDto);
						}
						//kj412
						else if(PayConstants.PAYCHANNEL_CODE_KJ142PAY == channelCode)
						{
							Kj412PayUtils.queryOrder(queryOrderDto);
						}
						//ttpay
						else if(PayConstants.PAYCHANNEL_CODE_TTPAY == channelCode)
						{
							TTPayUtils.queryOrder(queryOrderDto);
						}

						/**
						 * 处理订单查询结果
						 */
						//判断业务处理状态,如果处理成功,则走更新用户余额及支付状态的逻辑
						Integer dcode = Integer.parseInt(queryOrderDto.getAsString("dcode"));
						if(dcode == 1000)
						{
							//初始化订单更新参数
							Dto updateDto = new BaseDto();
							updateDto.put("payId",payId);//设置商户订单号

							//判断订单交易状态,如果订单交易状态为成功
							Dto resultsDto = (Dto)queryOrderDto.get("results");
							int status = resultsDto.getAsInteger("status");
							if(status == 1000)
							{
								updateDto.put("cpayId",StringUtil.isEmpty(cpayId)? resultsDto.get("tradeNo") : cpayId);//设置渠道流水号
								updateDto.put("pbank",resultsDto.get("bankType"));//设置用户付款银行标识
								updateDto.put("pbankDesc", WeixinConstants.payTypeMaps.get(updateDto.getAsString("pbank")));//设置用户付款银行描述

								//设置用户付款银行卡类型
								if(StringUtil.isNotEmpty(updateDto.get("pbank")) && updateDto.getAsString("pbank").endsWith("CREDIT"))
								{
									updateDto.put("pbankType",1);//设置用户付款银行卡类型为信用卡
								}
								else
								{
									updateDto.put("pbankType",0);//设置用户付款银行卡类型为信用卡
								}
								updateDto.put("ptime",DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//设置订单处理完成时间
								updateDto.put("smoney",resultsDto.get("smoney"));//设置订单交易金额
								userPayMapper.doRecharge(updateDto);//调用充值方法
								logger.info("[任务-充值订单补单][充值成功]处理结果:dcode=" + updateDto.getAsString("dcode")
										+ ",dmsg=" + updateDto.getAsString("dmsg")
										+ "订单号=" + payId);

								//判断处理结果状态,根据状态设置相应的响应结果
								dcode = updateDto.getAsInteger("dcode");//提取处理结果状态码
								if(dcode == 1000)
								{
									//处理成功
									Dto sendCouponDto = new BaseDto();
									sendCouponDto.put("opmethod","充值补单");
									sendCouponDto.put("activityType", "2");//设置活动类型为特定活动(0-彩票首页焦点图 1-咨询首页焦点图 2-特定活动 3-公告)
									sendCouponDto.put("couponType","1");//设置优惠券赠送类型为充值送(0-注册送 1-充值送)
									sendCouponDto.put("smoney",updateDto.get("smoney"));
									userService.sendCoupon(sendCouponDto,userId);//根据条件给用户赠送优惠券
								}
								//订单不存在
								else if(dcode == 1001)
								{
									logger.error("[任务-充值订单补单][充值成功]订单不存在!订单号=" + payId + ",订单所属用户编号=" + userId);
								}
								//订单金额不一致
								else if(dcode == 1002)
								{
									logger.error("[任务-充值订单补单][充值成功]订单金额校验不一致!订单号=" + payId
											+ ",商户订单金额=" + userpayDto.getAsString("money")
											+ ",渠道查询到的订单金额=" + resultsDto.getAsString("smoney"));
								}
							}
							else if(status == -1000)
							{
								//更新订单状态为处理失败
								updateDto.put("status",PayConstants.PAYORDER_STATUS_FAILURE);//设置订单状态为处理失败
								int count = userPayMapper.updateUserPay(updateDto);//更新订单状态
								if(count > 0)
								{
									logger.info("[任务-充值订单补单][充值失败]订单状态更新成功!订单号=" + payId + ",订单所属用户编号=" + userId);
								}
								else
								{
									logger.info("[任务-充值订单补单][充值失败]订单状态更新失败!订单号=" + payId + ",订单所属用户编号=" + userId);
								}
							}
							else if(1001 == status)
							{
								logger.info("[任务-充值订单补单][充值中或未支付]订单状态不做任何更新!订单号=" + payId + ",订单所属用户编号=" + userId);
							}
						}
						else
						{
							logger.info("[任务-充值订单补单]订单查询失败!订单号=" + payId + ",订单所属用户编号=" + userId + ",dmsg=" + queryOrderDto.getAsString("dmsg"));
						}
					}
					catch(Exception e0)
					{
						logger.error("[任务-充值订单补单]发生异常!订单号=" + userpayDto.getAsString("payId") + ",异常信息:" + e0);
					}
				}
			}
			else
			{
				logger.info("[任务-充值订单补单]当前没有待发送支付结果通知的充值订单记录.本次任务结束");
			}
		}
		catch (Exception e)
		{
			logger.error("[任务-充值订单补单]发生异常,异常信息：", e);
		}
	}
}