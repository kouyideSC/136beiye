package com.caipiao.taskcenter.pay;

import com.caipiao.common.constants.PayConstants;
import com.caipiao.common.pay.huichao.HuiChaoUtils;
import com.caipiao.common.pay.juhe.JuHe10381Utils;
import com.caipiao.common.pay.kj412.Kj412PayUtils;
import com.caipiao.common.pay.kuaifu.KuaiFuPayUtils;
import com.caipiao.common.pay.ttpay.TTPayUtils;
import com.caipiao.common.pay.yizhi.YizhiPayUtils;
import com.caipiao.common.pay.ypay.YPayUtils;
import com.caipiao.common.pay.zhaohang.ZhaohangPayUtils;
import com.caipiao.common.shuangqian.ShuangQianUtils;
import com.caipiao.common.pay.wlpay.WlPayUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.user.UserAccountMapper;
import com.caipiao.dao.user.UserDetailMapper;
import com.caipiao.dao.user.UserMapper;
import com.caipiao.dao.user.UserPayMapper;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.user.UserPay;
import com.caipiao.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

/**
 * 代付订单主动查询任务
 * @author 	sjq
 */
@Component("queryDaiFuOrderTask")
public class QueryDaiFuOrderTask
{
	private static Logger logger = LoggerFactory.getLogger(QueryDaiFuOrderTask.class);

	@Autowired
	private UserPayMapper userPayMapper;

	@Autowired
	private UserDetailMapper userDetailMapper;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UserService userService;

	@Autowired
	private UserAccountMapper userAccountMapper;

	/**
	 * 查询处理中的代付订单记录,根据订单交易状态更新订单信息
	 * @author	sjq
	 */
	public void doTask()
	{
		try
		{
			//查询待发送支付结果通知的充值订单
			logger.info("[代付订单主动查询]开始执行.");
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH,-1);//当前日期往前推1天
			Dto queryParams = new BaseDto();
			queryParams.put("payType","1");//业务类型为提现(0-充值 1-提现)
			queryParams.put("status","2");//订单状态为处理中
			queryParams.put("minCreateTime",DateUtil.formatDate(calendar.getTime(),DateUtil.DEFAULT_DATE_TIME));//订单最早创建时间
			List<UserPay> userPayList = userPayMapper.queryUserPays(queryParams);//查询待主动查询的代付订单记录
			if(userPayList != null && userPayList.size() > 0)
			{
				logger.info("[代付订单主动查询]本次共有" + userPayList.size() + "条待查询的订单.");
				for(UserPay userPay : userPayList)
				{
					try
					{
						/**
						 * 根据代付渠道查询订单交易状态
						 */
						Dto queryOrderDto = new BaseDto();
						Integer channelCode = userPay.getChannelCode();//提取代付渠道编号
						if(PayConstants.CHANNEL_CODE_OUT_SHENGPAY == channelCode)
						{
							//查询盛付通-代付订单
							continue;
						}
						//聚合支付
						else if(PayConstants.CHANNEL_CODE_OUT_JUHEPAY == channelCode)
						{
							//查询聚合支付-代付订单
							queryOrderDto.put("merchantNo",JuHe10381Utils.merchantNo);//设置商户号
							queryOrderDto.put("account", JuHe10381Utils.ckaccount);//设置出款账号
							queryOrderDto.put("secretKey",JuHe10381Utils.dfSecretKey);//设置代付签名秘钥
							queryOrderDto.put("payId", userPay.getPayId());//设置商户订单号
							JuHe10381Utils.queryDpayOrder(queryOrderDto);
						}
						//汇潮支付
						else if(PayConstants.CHANNEL_CODE_OUT_HUICHAOPAY == channelCode)
						{
							//查询汇潮支付-代付订单
							queryOrderDto.put("merchantNo",HuiChaoUtils.merchantNo);//设置商户号
							queryOrderDto.put("privateKey",HuiChaoUtils.privateKey);//设置RSA私钥
							queryOrderDto.put("ymdPublicKey",HuiChaoUtils.ymdPublicKey);//设置一麻袋RSA公钥
							queryOrderDto.put("payId", userPay.getPayId());//设置商户订单号
							HuiChaoUtils.queryOrder(queryOrderDto);
						}
						//万两支付
						else if(PayConstants.CHANNEL_CODE_OUT_WLPAY == channelCode)
						{
							//查询万两支付-代付订单
							queryOrderDto.put("merchantNo",WlPayUtils.merchantNo);//设置商户号
							queryOrderDto.put("secretKey",WlPayUtils.secretKey);//设置签名秘钥
							queryOrderDto.put("payId", userPay.getPayId());//设置商户订单号
							WlPayUtils.queryDaifuOrder(queryOrderDto);
						}
						//双乾支付
						else if(PayConstants.CHANNEL_CODE_OUT_SHUANGQPAY == channelCode)
						{
							//查询双乾支付-代付订单
							queryOrderDto.put("payId", userPay.getPayId());//设置商户订单号
							queryOrderDto.put("id", userPay.getId());//设置唯一编号
							ShuangQianUtils.queryOrder(queryOrderDto);
						}
						//易旨支付(一麻袋)
						else if(PayConstants.CHANNEL_CODE_OUT_YIZHIYIMADAIPAY == channelCode)
						{
							//查询易旨支付(一麻袋)-代付订单
							queryOrderDto.put("ymdmerchantNo", YizhiPayUtils.ymdmerchantNo);//设置商户号
							queryOrderDto.put("privateKey",YizhiPayUtils.privateKey);//设置RSA私钥
							queryOrderDto.put("ymdpublicKey",YizhiPayUtils.ymdpublicKey);//设置一麻袋公钥
							queryOrderDto.put("payId", userPay.getPayId());//设置商户订单号
							YizhiPayUtils.queryDfOrder(queryOrderDto);
						}
						//快付支付
						else if(PayConstants.CHANNEL_CODE_OUT_KUAIFUPAY == channelCode)
						{
							//查询快付支付-代付订单
							queryOrderDto.put("merchantKey", KuaiFuPayUtils.merchantKey);//设置商户key
							queryOrderDto.put("secretKey",KuaiFuPayUtils.secretKey);//设置支付秘钥
							queryOrderDto.put("payId", userPay.getPayId());//设置商户订单号
							KuaiFuPayUtils.queryDaifuOrder(queryOrderDto);
						}
						//kj412
						else if(PayConstants.CHANNEL_CODE_OUT_KJ142PAY == channelCode)
						{
							//查询kj412-代付订单
							queryOrderDto.put("apiUrl",Kj412PayUtils.apiUrl);//设置api地址
							queryOrderDto.put("merchantNo",Kj412PayUtils.merchantNo);//设置商户号
							queryOrderDto.put("secretKey",Kj412PayUtils.secretKey);//设置签名秘钥
							queryOrderDto.put("payId",userPay.getPayId());//设置商户订单号
							Kj412PayUtils.queryDfOrder(queryOrderDto);
						}
						//ttpay
						else if(PayConstants.CHANNEL_CODE_OUT_TTPAY == channelCode)
						{
							//查询ttpay-代付订单
							queryOrderDto.put("apiUrl", TTPayUtils.dfapiUrl);//设置api地址
							queryOrderDto.put("merchantNo",TTPayUtils.dfmerId);//设置商户号
							queryOrderDto.put("payId",userPay.getPayId());//设置商户订单号
							TTPayUtils.queryDfOrder(queryOrderDto);
						}
						//ypay
						else if(PayConstants.CHANNEL_CODE_OUT_YPAY == channelCode)
						{
							//查询ypay-代付订单
							queryOrderDto.put("merchantNo",YPayUtils.merchantNo);//设置商户号
							queryOrderDto.put("secretKey",YPayUtils.secretKey);//设置签名秘钥
							queryOrderDto.put("apiUrl", YPayUtils.apiUrl);//设置api地址
							queryOrderDto.put("payId",userPay.getPayId());//设置商户订单号
							YPayUtils.queryDfOrder(queryOrderDto);
						}
						else
						{
							return;//无渠道不处理
						}

						/**
						 * 处理订单查询结果
						 */
						//判断订单查询状态
						Integer dcode = Integer.parseInt(queryOrderDto.getAsString("dcode"));//提取订单查询状态
						if(dcode == 1000)
						{
							//初始化订单更新参数
							Dto updateDto = new BaseDto();
							updateDto.put("payId",userPay.getPayId());//设置商户订单号

							//判断订单交易状态,如果订单交易状态为成功
							Dto resultsDto = (Dto)queryOrderDto.get("results");//提取代付订单交易结果
							int status = resultsDto.getAsInteger("status");//提取代付订单交易状态
							if(status == 1000)
							{
								//付款成功,则更新订单状态为处理成功
								if(PayConstants.CHANNEL_CODE_OUT_HUICHAOPAY != channelCode
										&& PayConstants.CHANNEL_CODE_OUT_WLPAY != channelCode
										&& PayConstants.CHANNEL_CODE_OUT_KUAIFUPAY != channelCode
										&& PayConstants.CHANNEL_CODE_OUT_KJ142PAY != channelCode)
								{
									if(StringUtil.isEmpty(resultsDto.get("tradeNo")) && StringUtil.isEmpty(userPay.getChannelPayId()))
									{
										logger.info("[代付订单主动查询][付款成功]渠道流水号为空!已终止更新该订单的状态");
										continue;
									}
								}
								updateDto.put("channelPayId",StringUtil.isEmpty(userPay.getChannelPayId())? resultsDto.get("tradeNo") : userPay.getChannelPayId());
								updateDto.put("status",PayConstants.PAYORDER_STATUS_SUCCESS);//设置订单状态为处理成功
								updateDto.put("remark","处理成功");
								int count = userPayMapper.updateUserPay(updateDto);//更新订单状态
								logger.info("[代付订单主动查询][付款成功]订单状态更新" + (count > 0? "成功" : "失败") + ",订单号=" + userPay.getPayId() + ",订单所属用户编号=" + userPay.getUserId());
								if(count > 0)
								{
									//更新用户账户信息
									updateDto = new BaseDto("userId",userPay.getUserId());
									updateDto.put("offsetFrozen",-userPay.getMoney());//冻结金额减去订单的金额
									count = userAccountMapper.updateUserAccount(updateDto);//更新用户账户信息
									logger.info("[代付订单主动查询][付款成功]用户账户更新" + (count > 0? "成功" : "失败") + ",订单号=" + userPay.getPayId() + ",订单所属用户编号=" + userPay.getUserId());

									//更新账户流水状态为有效(已完成)
									if(count > 0)
									{
										updateDto = new BaseDto();
										updateDto.put("userId",userPay.getUserId());//设置用户编号
										updateDto.put("businessId",userPay.getPayId());//设置流水关联的订单号
										updateDto.put("status",1);//设置流水状态为有效(已完成)
										count = userDetailMapper.updateUserDetailOfTx(updateDto);
										logger.info("[代付订单主动查询][付款成功]用户账户流水更新" + (count > 0? "成功" : "失败") + ",订单号=" + userPay.getPayId() + ",订单所属用户编号=" + userPay.getUserId());
									}
								}
							}
							else if(status == -1000)
							{
								updateDto.put("status",PayConstants.PAYORDER_STATUS_FAILURE);//设置订单状态为处理失败
								updateDto.put("remark",StringUtil.isEmpty(resultsDto.get("msg"))? "付款失败" : resultsDto.get("msg"));
								int count = userPayMapper.updateUserPay(updateDto);//更新订单状态
								logger.info("[代付订单主动查询][付款失败]订单状态更新" + (count > 0? "成功" : "失败") + ",订单号=" + userPay.getPayId() + ",订单所属用户编号=" + userPay.getUserId());
								if(count > 0)
								{
									//更新账户流水状态为无效
									updateDto = new BaseDto();
									updateDto.put("userId",userPay.getUserId());//设置用户编号
									updateDto.put("businessId",userPay.getPayId());//设置流水关联的订单号
									updateDto.put("status",-1);//设置流水状态为无效
									count = userDetailMapper.updateUserDetailOfTx(updateDto);
									logger.info("[代付订单主动查询][付款失败]用户账户流水更新" + (count > 0? "成功" : "失败") + ",订单号=" + userPay.getPayId() + ",订单所属用户编号=" + userPay.getUserId());

									//更新用户账户信息
									updateDto = new BaseDto("userId",userPay.getUserId());
									updateDto.put("tbalance",userPay.getMoney());//余额加上订单的金额
									updateDto.put("twithDraw",-userPay.getMoney());//累计提现金额减去订单金额
									updateDto.put("offsetFrozen",-userPay.getMoney());//冻结金额减去订单的金额
									updateDto.put("offsetWithDraw",userPay.getMoney());//可提现金额加上订单金额
									count = userAccountMapper.updateUserAccount(updateDto);//更新用户账户信息
									logger.info("[代付订单主动查询][付款失败]用户账户更新" + (count > 0? "成功" : "失败") + ",订单号=" + userPay.getPayId() + ",订单所属用户编号=" + userPay.getUserId());
								}
							}
							else if(1001 == status)
							{
								logger.info("[代付订单主动查询][付款中或处理中]订单状态不做任何更新!订单号=" + userPay.getPayId() + ",订单所属用户编号=" + userPay.getUserId());
							}
						}
						else
						{
							logger.info("[代付订单主动查询]订单查询失败!订单号=" + userPay.getPayId() + ",订单所属用户编号=" + userPay.getUserId() + ",dmsg=" + queryOrderDto.getAsString("dmsg"));
						}
					}
					catch(Exception e0)
					{
						logger.error("[代付订单主动查询]发生异常!订单号=" + userPay.getPayId() + ",异常信息:" + e0);
					}
				}
			}
			else
			{
				logger.info("[代付订单主动查询]当前没有待主动查询的代付订单记录.本次任务结束");
			}
		}
		catch (Exception e)
		{
			logger.error("[代付订单主动查询]发生异常!异常信息：", e);
		}
	}
}