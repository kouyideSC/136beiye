package com.caipiao.taskcenter.pay;

import com.caipiao.common.constants.Constants;
import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.PayConstants;
import com.caipiao.common.constants.UserConstants;
import com.caipiao.common.identity.juku.JuKuUtils;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.pay.huichao.HuiChaoUtils;
import com.caipiao.common.pay.juhe.JuHe10381Utils;
import com.caipiao.common.pay.kj412.Kj412PayUtils;
import com.caipiao.common.pay.kuaifu.KuaiFuPayUtils;
import com.caipiao.common.pay.kuaijie.KuaiJieUtils;
import com.caipiao.common.pay.shengpay.ShengpayUtils;
import com.caipiao.common.pay.ttpay.TTPayUtils;
import com.caipiao.common.pay.wlpay.WlPayUtils;
import com.caipiao.common.pay.yizhi.YizhiPayUtils;
import com.caipiao.common.pay.ypay.YPayUtils;
import com.caipiao.common.pay.zhaohang.ZhaohangPayUtils;
import com.caipiao.common.shuangqian.ShuangQianUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.common.BankMapper;
import com.caipiao.dao.common.PaymentWayMapper;
import com.caipiao.dao.user.UserAccountMapper;
import com.caipiao.dao.user.UserDetailMapper;
import com.caipiao.dao.user.UserMapper;
import com.caipiao.dao.user.UserPayMapper;
import com.caipiao.domain.common.Bank;
import com.caipiao.domain.common.PaymentWay;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.user.User;
import com.caipiao.domain.user.UserPay;
import com.caipiao.domain.vo.BankInfoVo;
import com.caipiao.service.common.TaskService;
import com.caipiao.service.config.SysConfig;
import com.caipiao.service.lottery.PeriodService;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

/**
 * 发送付款(提现)请求任务
 * @author 	sjq
 */
@Component("sendPaymentRequestTask")
public class SendPaymentRequestTask
{
	private static Logger logger = LoggerFactory.getLogger(SendPaymentRequestTask.class);

	@Value("${shengpay.payment.result.notify}")
	private String shengpayResultNotify;//盛付通付款结果通知地址

	@Autowired
	private UserPayMapper userPayMapper;

	@Autowired
	private PaymentWayMapper paymentWayMapper;

	@Autowired
	private UserDetailMapper userDetailMapper;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UserAccountMapper userAccountMapper;

	@Autowired
	private BankMapper bankMapper;

	/**
	 * 发送付款到银行账户的请求
	 * @author	sjq
	 */
	public void sendPaymentRequest()
	{
		try
		{
			//查询待发送付款请求的任务
			logger.info("[发送付款到银行账户的请求]开始执行.");
			Dto queryParams = new BaseDto();
			queryParams.put("payType","1");//业务类型为提现(0-充值 1-提现)
			queryParams.put("minStatus","0");
			queryParams.put("maxStatus","1");
			List<UserPay> userPayList = userPayMapper.queryUserPays(queryParams);
			if(userPayList != null && userPayList.size() > 0)
			{
				logger.info("[发送付款到银行账户的请求]本次共有" + userPayList.size() + "条待发送的付款到银行账户的请求.");
				for(UserPay userPay : userPayList)
				{
					try
					{
						/**
						 * 付款请求校验
						 */
						//验证用户类型
						User user = userMapper.queryUserInfoById(userPay.getUserId());
						if(user.getUserType() == UserConstants.USER_TYPE_VIRTUAL
								|| user.getUserType() == UserConstants.USER_TYPE_OUTMONEY)
						{
							logger.info("[发送付款到银行账户的请求]虚拟用户/出款账户的提现请求不予以处理!用户编号=" + userPay.getId()
									+ ",订单编号=" + userPay.getPayId()
									+ ",订单金额=" + userPay.getMoney());
							continue;
						}
						//获取本次使用的付款(提现)方式(渠道),无可用提现渠道则转为线下手工转账
						PaymentWay paymentWay = getPaymentWay(userPay);
                        if(paymentWay == null)
                        {
                            logger.error("[发送付款到银行账户的请求]系统当前获取不到任何可用的付款(提现)方式(渠道)!");
                            Dto updateDto = new BaseDto("id",userPay.getId());
                            updateDto.put("channelCode",PayConstants.CHANNEL_CODE_OUT_RENGONGPAY);
                            updateDto.put("channelDesc",PayConstants.channelCodeMap.get(PayConstants.CHANNEL_CODE_OUT_RENGONGPAY));
                            userPayMapper.updateUserPay(updateDto);
                            logger.error("[发送付款到银行账户的请求]已将提现渠道更改为线下人工转账!订单号=" + userPay.getPayId() + ",订单所属用户编号=" + userPay.getUserId());
                            continue;
                        }
                        //线下人工转账渠道则跳过
                        else if(paymentWay.getChannelCode().equals(PayConstants.CHANNEL_CODE_OUT_RENGONGPAY))
                        {
                            continue;
                        }

						/**
						 * 发起付款请求
						 */
						//判断本次使用的付款方式,如果是盛付通,则走盛付通渠道发送付款请求
						BankInfoVo bankInfoVo = userPay.getBankInfo();//提取银行卡信息
						Dto params = new BaseDto();
						if(paymentWay.getChannelCode() == PayConstants.CHANNEL_CODE_OUT_SHENGPAY)
						{
							//设置基础参数
							logger.info("[发送付款到银行账户的请求]使用盛付通渠道发送付款请求,订单所属用户编号=" + userPay.getUserId()
									+ ",订单号=" + userPay.getPayId()
									+ ",订单金额=" + userPay.getMoney());
							String batchNo = "B" + userPay.getPayId() + userPay.getId();//生成批次号
							params.put("batchNo",batchNo);//设置付款请求批次号
							params.put("callbackUrl", SysConfig.getHostApp() + SysConfig.getString("shengpay.payment.result.notify"));//设置回调地址(流程达到最终状态后通知商户的地址)
							params.put("totalAmount",userPay.getMoney());//设置本批次支付的总金额
							params.put("remark","付款");

							//设置付款明细
							List<Dto> details = new ArrayList<Dto>();
							Dto detailData = new BaseDto();
							detailData.put("payId",userPay.getPayId());//设置单笔付款明细-商户流水号(订单号)
							detailData.put("province",bankInfoVo.getBankProvince());//设置单笔付款明细-银行所在省份
							detailData.put("city",bankInfoVo.getBankCity());//设置单笔付款明细-银行所在城市
							detailData.put("branchName",bankInfoVo.getSubBankName());//设置单笔付款明细-银行支行名称
							detailData.put("bankName",bankInfoVo.getBankName());//设置单笔付款明细-银行名称
							detailData.put("accountType","C(个人)");//设置单笔付款明细-收款人账户类型(C-个人 B-企业)
							detailData.put("bankUserName",bankInfoVo.getAccountHolder());//设置单笔付款明细-收款方户名
							detailData.put("bankAccount",bankInfoVo.getBankCard());//设置单笔付款明细-收款方银行账号
							detailData.put("amount",userPay.getMoney());//设置单笔付款明细付款金额(单位:元)
							detailData.put("remark","付款");
							details.add(detailData);
							params.put("details",details);
							ShengpayUtils.transBatchBank(params);//发送付款到银行账户的请求
						}
						//聚合JuHe10381渠道
						else if(paymentWay.getChannelCode() == PayConstants.CHANNEL_CODE_OUT_JUHEPAY)
						{
							logger.info("[发送付款到银行账户的请求]使用聚合支付发送付款请求,订单所属用户编号=" + userPay.getUserId() + ",订单号=" + userPay.getPayId() + ",订单金额=" + userPay.getMoney());
							Dto queryDto = new BaseDto();
							queryDto.put("channelCode",paymentWay.getChannelCode());

							//查询并校验渠道银行配置
							boolean flag = true;
							queryDto.put("bankName",bankInfoVo.getBankName());
							Dto channelBankInfo = paymentWayMapper.queryPaymentWayBankInfo(queryDto);//查询渠道银行配置
							if(channelBankInfo == null || StringUtil.isEmpty(channelBankInfo.get("channelBankAbbreviation")))
							{
								logger.info("[发送付款到银行账户的请求]渠道" + paymentWay.getName() + "尚未配置银行-" + bankInfoVo.getBankName() + "!订单所属用户编号=" + userPay.getUserId() + ",订单号=" + userPay.getPayId());
								flag = false;
								params.put("dcode","2000");
								params.put("dmsg","渠道" + paymentWay.getName() + "尚未配置银行-" + bankInfoVo.getBankName());
							}
							//查询并校验渠道省份配置
							queryDto.put("areaCode",bankInfoVo.getBankProvinceCode());
							queryDto.put("type","0");
							Dto channelProvince = paymentWayMapper.queryPaymentWayAreaInfo(queryDto);//查询渠道省份配置
							if(channelProvince == null || StringUtil.isEmpty(channelProvince.get("channelAreaCode")))
							{
								logger.info("[发送付款到银行账户的请求]渠道" + paymentWay.getName() + "尚未配置省份-" + bankInfoVo.getBankProvince() + "!订单所属用户编号=" + userPay.getUserId() + ",订单号=" + userPay.getPayId());
								flag = false;
								params.put("dcode","2000");
								params.put("dmsg","渠道" + paymentWay.getName() + "尚未配置省份-" + bankInfoVo.getBankProvince());
							}
							//查询并校验渠道城市配置
							queryDto.put("areaCode",bankInfoVo.getBankCityCode());
							queryDto.put("type","1");
							Dto channelCity = paymentWayMapper.queryPaymentWayAreaInfo(queryDto);//查询渠道城市配置
							if(channelCity == null || StringUtil.isEmpty(channelCity.get("channelAreaCode")))
							{
								logger.info("[发送付款到银行账户的请求]渠道" + paymentWay.getName() + "尚未配置城市-" + bankInfoVo.getBankCity() + "!订单所属用户编号=" + userPay.getUserId() + ",订单号=" + userPay.getPayId());
								flag = false;
								params.put("dcode","2000");
								params.put("dmsg","渠道" + paymentWay.getName() + "尚未配置城市-" + bankInfoVo.getBankCity());
							}
							if(flag)
							{
								params.put("merchantNo",JuHe10381Utils.merchantNo);//设置商户号
								params.put("ckaccount",JuHe10381Utils.ckaccount);//设置出款账号
								params.put("dfpassword",JuHe10381Utils.dfpassword);//设置提现密码
								params.put("secretKey",JuHe10381Utils.dfSecretKey);//设置代付签名秘钥
								params.put("bankCode",channelBankInfo.get("channelBankAbbreviation"));//设置收款方银行编号
								params.put("bankAccount",bankInfoVo.getBankCard());//设置收款方银行卡号
								params.put("bankUserName",bankInfoVo.getAccountHolder());//设置收款方银行开户名
								params.put("provinceCode",channelProvince.get("channelAreaCode"));//设置收款方开户行所在省份代码
								params.put("cityCode",channelCity.get("channelAreaCode"));//设置收款方开户行所在城市代码
								params.put("amount",userPay.getMoney());//设置订单金额
								params.put("payId",userPay.getPayId());//设置商户订单号
								JuHe10381Utils.transBatchBank(params);//发送付款到银行账户的请求
							}
						}
						//汇潮支付渠道
						else if(paymentWay.getChannelCode() == PayConstants.CHANNEL_CODE_OUT_HUICHAOPAY)
						{
							logger.info("[发送付款到银行账户的请求]使用汇潮支付发送付款请求,订单所属用户编号=" + userPay.getUserId() + ",订单号=" + userPay.getPayId() + ",订单金额=" + userPay.getMoney());
							Dto queryDto = new BaseDto();
							queryDto.put("channelCode",paymentWay.getChannelCode());

							//查询并校验渠道银行配置
							boolean flag = true;
							queryDto.put("bankName",bankInfoVo.getBankName());
							Dto channelBankInfo = paymentWayMapper.queryPaymentWayBankInfo(queryDto);//查询渠道银行配置
							if(channelBankInfo == null || StringUtil.isEmpty(channelBankInfo.get("channelBankName")))
							{
								logger.info("[发送付款到银行账户的请求]渠道" + paymentWay.getName() + "尚未配置银行-" + bankInfoVo.getBankName() + "!订单所属用户编号=" + userPay.getUserId() + ",订单号=" + userPay.getPayId());
								flag = false;
								params.put("dcode","2000");
								params.put("dmsg","渠道" + paymentWay.getName() + "尚未配置银行-" + bankInfoVo.getBankName());
							}
							//查询并校验渠道省份配置
							queryDto.put("areaCode",bankInfoVo.getBankProvinceCode());
							queryDto.put("type","0");
							Dto channelProvince = paymentWayMapper.queryPaymentWayAreaInfo(queryDto);//查询渠道省份配置
							if(channelProvince == null || StringUtil.isEmpty(channelProvince.get("channelAreaName")))
							{
								logger.info("[发送付款到银行账户的请求]渠道" + paymentWay.getName() + "尚未配置省份-" + bankInfoVo.getBankProvince() + "!订单所属用户编号=" + userPay.getUserId() + ",订单号=" + userPay.getPayId());
								flag = false;
								params.put("dcode","2000");
								params.put("dmsg","渠道" + paymentWay.getName() + "尚未配置省份-" + bankInfoVo.getBankProvince());
							}
							//查询并校验渠道城市配置
							queryDto.put("areaCode",bankInfoVo.getBankCityCode());
							queryDto.put("type","1");
							Dto channelCity = paymentWayMapper.queryPaymentWayAreaInfo(queryDto);//查询渠道城市配置
							if(channelCity == null || StringUtil.isEmpty(channelCity.get("channelAreaName")))
							{
								logger.info("[发送付款到银行账户的请求]渠道" + paymentWay.getName() + "尚未配置城市-" + bankInfoVo.getBankCity() + "!订单所属用户编号=" + userPay.getUserId() + ",订单号=" + userPay.getPayId());
								flag = false;
								params.put("dcode","2000");
								params.put("dmsg","渠道" + paymentWay.getName() + "尚未配置城市-" + bankInfoVo.getBankCity());
							}
							if(flag)
							{
								//设置基础参数
								params.put("merchantNo",HuiChaoUtils.merchantNo);//设置商户号
								params.put("privateKey",HuiChaoUtils.privateKey);//设置RSA私钥
								params.put("ymdPublicKey",HuiChaoUtils.ymdPublicKey);//设置一麻袋RSA公钥
								params.put("notifyUrl", SysConfig.getHostApp() + SysConfig.getString("huichao.payment.result.notify"));//设置回调地址(流程达到最终状态后通知商户的地址)
								params.put("remark","付款");

								//设置付款明细
								List<Dto> details = new ArrayList<Dto>();
								Dto detailData = new BaseDto();
								detailData.put("payId",userPay.getPayId());//设置单笔付款明细-商户流水号(订单号)
								detailData.put("province",channelProvince.getAsString("channelAreaName"));//设置单笔付款明细-银行所在省份
								detailData.put("city",channelCity.getAsString("channelAreaName"));//设置单笔付款明细-银行所在城市
								detailData.put("bankName",channelBankInfo.get("channelBankName"));//设置单笔付款明细-银行名称

								//设置收款行支行名称,如果支行为空,则设置支行名称为 所在城市+银行名称
								if(StringUtil.isEmpty(bankInfoVo.getSubBankName()))
								{
									detailData.put("branchName",channelCity.getAsString("channelAreaName") + "支行");//设置单笔付款明细-银行支行名称
								}
								else
								{
									detailData.put("branchName",bankInfoVo.getSubBankName());//设置单笔付款明细-银行支行名称
								}
								detailData.put("bankUserName",bankInfoVo.getAccountHolder());//设置单笔付款明细-收款方户名
								detailData.put("bankAccount",bankInfoVo.getBankCard());//设置单笔付款明细-收款方银行账号
								detailData.put("amount",userPay.getMoney());//设置单笔付款明细付款金额(单位:元)
								detailData.put("remark","付款");
								details.add(detailData);
								params.put("details",details);
								HuiChaoUtils.transBatchBank(params);//发送付款到银行账户的请求
							}
						}
						//万两支付渠道
						else if(paymentWay.getChannelCode() == PayConstants.CHANNEL_CODE_OUT_WLPAY)
						{
                            logger.info("[发送付款到银行账户的请求]使用万两支付发送付款请求,订单所属用户编号=" + userPay.getUserId() + ",订单号=" + userPay.getPayId() + ",订单金额=" + userPay.getMoney());

                            //查询渠道银行配置
                            Dto queryDto = new BaseDto();
                            queryDto.put("channelCode",paymentWay.getChannelCode());
                            queryDto.put("bankName",bankInfoVo.getBankName());
                            Dto channelBankInfo = paymentWayMapper.queryPaymentWayBankInfo(queryDto);//查询渠道银行配置

                            //设置请求参数并发起代付请求
                            params.put("merchantNo",WlPayUtils.merchantNo);//设置商户号
                            params.put("secretKey",WlPayUtils.secretKey);//设置商户密钥
                            params.put("bankcode",channelBankInfo.getAsString("channelBankAbbreviation"));//设置银行类型
                            params.put("paymoney",userPay.getMoney());//设置代付金额
                            params.put("payId",userPay.getPayId());//设置批次号
                            params.put("bankaccount",bankInfoVo.getBankCard());//设置收款账号
                            params.put("bankusername",bankInfoVo.getAccountHolder());//设置收款人
                            params.put("bankprovince",bankInfoVo.getBankProvince() + (bankInfoVo.getBankProvince().indexOf("省") > -1? "" : "省"));//设置收款银行所在省份
                            params.put("bankcity",bankInfoVo.getBankCity());//设置收款银行所在城市
							if(StringUtil.isEmpty(bankInfoVo.getSubBankName()))
							{
								params.put("banksubname",bankInfoVo.getBankName() + bankInfoVo.getBankCity() + "支行");//设置收款银行支行
							}
							else
							{
								params.put("banksubname",bankInfoVo.getBankName());//设置收款银行支行
							}
                            params.put("bankaccounttype",2);//设置收款银行账户类型(1-对公 2-对私)
                            params.put("remark","付款");
                            WlPayUtils.sendDaiFuRequest(params);//发起代付请求
						}
						//双乾支付渠道
						else if(paymentWay.getChannelCode() == PayConstants.CHANNEL_CODE_OUT_SHUANGQPAY)
						{
							logger.info("[发送付款到银行账户的请求]使用双乾支付发送付款请求,订单所属用户编号=" + userPay.getUserId() + ",订单号=" + userPay.getPayId() + ",订单金额=" + userPay.getMoney());
							Dto queryDto = new BaseDto();
							queryDto.put("channelCode",paymentWay.getChannelCode());

							//查询并校验渠道银行配置
							boolean flag = true;
							queryDto.put("bankName",bankInfoVo.getBankName());
							Dto channelBankInfo = paymentWayMapper.queryPaymentWayBankInfo(queryDto);//查询渠道银行配置
							if(channelBankInfo == null || StringUtil.isEmpty(channelBankInfo.get("channelBankAbbreviation")))
							{
								logger.info("[发送付款到银行账户的请求]渠道" + paymentWay.getName() + "不支持银行-" + bankInfoVo.getBankName() + "!订单所属用户编号=" + userPay.getUserId() + ",订单号=" + userPay.getPayId());
								flag = false;
								params.put("dcode","2000");
								params.put("dmsg","渠道" + paymentWay.getName() + "不支持银行-" + bankInfoVo.getBankName());
							}
							if(flag)
							{
								params.put("totalAmount", userPay.getMoney());//设置订单金额
								params.put("id", userPay.getId());//设置唯一编号
								params.put("batchNo", userPay.getPayId());//设置批次号
								params.put("bankUserName",bankInfoVo.getAccountHolder());//设置单笔付款明细-收款方户名
								params.put("bankAccount",bankInfoVo.getBankCard());//设置单笔付款明细-收款方银行账号
								params.put("bankCode",channelBankInfo.getAsString("channelBankAbbreviation"));//设置银行编码
								params.put("bankCityCode",bankInfoVo.getBankCityCode());//设置银行城市编码
								ShuangQianUtils.transBatchBank(params);//发送付款到银行账户的请求
							}
						}
						//易旨支付(一麻袋)渠道
						else if(paymentWay.getChannelCode() == PayConstants.CHANNEL_CODE_OUT_YIZHIYIMADAIPAY)
						{
							logger.info("[发送付款到银行账户的请求]使用易旨支付(一麻袋)发送付款请求,订单所属用户编号=" + userPay.getUserId() + ",订单号=" + userPay.getPayId() + ",订单金额=" + userPay.getMoney());
							Dto queryDto = new BaseDto();
							queryDto.put("channelCode",paymentWay.getChannelCode());

							//查询并校验渠道银行配置
							boolean flag = true;
							queryDto.put("bankName",bankInfoVo.getBankName());
							Dto channelBankInfo = paymentWayMapper.queryPaymentWayBankInfo(queryDto);//查询渠道银行配置
							if(channelBankInfo == null || StringUtil.isEmpty(channelBankInfo.get("channelBankName")))
							{
								logger.info("[发送付款到银行账户的请求]渠道" + paymentWay.getName() + "尚未配置银行-" + bankInfoVo.getBankName() + "!订单所属用户编号=" + userPay.getUserId() + ",订单号=" + userPay.getPayId());
								flag = false;
								params.put("dcode","2000");
								params.put("dmsg","渠道" + paymentWay.getName() + "尚未配置银行-" + bankInfoVo.getBankName());
							}
							//查询并校验渠道省份配置
							queryDto.put("areaCode",bankInfoVo.getBankProvinceCode());
							queryDto.put("type","0");
							Dto channelProvince = paymentWayMapper.queryPaymentWayAreaInfo(queryDto);//查询渠道省份配置
							if(channelProvince == null || StringUtil.isEmpty(channelProvince.get("channelAreaName")))
							{
								logger.info("[发送付款到银行账户的请求]渠道" + paymentWay.getName() + "尚未配置省份-" + bankInfoVo.getBankProvince() + "!订单所属用户编号=" + userPay.getUserId() + ",订单号=" + userPay.getPayId());
								flag = false;
								params.put("dcode","2000");
								params.put("dmsg","渠道" + paymentWay.getName() + "尚未配置省份-" + bankInfoVo.getBankProvince());
							}
							//查询并校验渠道城市配置
							queryDto.put("areaCode",bankInfoVo.getBankCityCode());
							queryDto.put("type","1");
							Dto channelCity = paymentWayMapper.queryPaymentWayAreaInfo(queryDto);//查询渠道城市配置
							if(channelCity == null || StringUtil.isEmpty(channelCity.get("channelAreaName")))
							{
								logger.info("[发送付款到银行账户的请求]渠道" + paymentWay.getName() + "尚未配置城市-" + bankInfoVo.getBankCity() + "!订单所属用户编号=" + userPay.getUserId() + ",订单号=" + userPay.getPayId());
								flag = false;
								params.put("dcode","2000");
								params.put("dmsg","渠道" + paymentWay.getName() + "尚未配置城市-" + bankInfoVo.getBankCity());
							}
							if(flag)
							{
								//设置基础参数
								params.put("ymdmerchantNo",YizhiPayUtils.ymdmerchantNo);//设置商户号
								params.put("privateKey",YizhiPayUtils.privateKey);//设置RSA私钥
								params.put("ymdpublicKey",YizhiPayUtils.ymdpublicKey);//设置一麻袋公钥
								params.put("notifyUrl", SysConfig.getHostApp() + SysConfig.getString("yizhi.payment.result.notify"));//设置回调地址(流程达到最终状态后通知商户的地址)
								params.put("remark","付款");

								//设置付款明细
								List<Dto> details = new ArrayList<Dto>();
								Dto detailData = new BaseDto();
								detailData.put("payId",userPay.getPayId());//设置单笔付款明细-商户流水号(订单号)
								detailData.put("province",channelProvince.getAsString("channelAreaName"));//设置单笔付款明细-银行所在省份
								detailData.put("city",channelCity.getAsString("channelAreaName"));//设置单笔付款明细-银行所在城市
								detailData.put("bankName",channelBankInfo.get("channelBankName"));//设置单笔付款明细-银行名称

								//设置收款行支行名称,如果支行为空,则设置支行名称为 所在城市+银行名称
								if(StringUtil.isEmpty(bankInfoVo.getSubBankName()))
								{
									detailData.put("branchName",channelCity.getAsString("channelAreaName") + "支行");//设置单笔付款明细-银行支行名称
								}
								else
								{
									detailData.put("branchName",bankInfoVo.getSubBankName());//设置单笔付款明细-银行支行名称
								}
								detailData.put("bankUserName",bankInfoVo.getAccountHolder());//设置单笔付款明细-收款方户名
								detailData.put("bankAccount",bankInfoVo.getBankCard());//设置单笔付款明细-收款方银行账号
								detailData.put("amount",userPay.getMoney());//设置单笔付款明细付款金额(单位:元)
								detailData.put("remark","付款");
								details.add(detailData);
								params.put("details",details);
								YizhiPayUtils.transBatchBank(params);//发送付款到银行账户的请求
							}
						}
						//快付支付渠道
						else if(paymentWay.getChannelCode() == PayConstants.CHANNEL_CODE_OUT_KUAIFUPAY)
						{
							logger.info("[发送付款到银行账户的请求]使用快付支付发送付款请求,订单所属用户编号=" + userPay.getUserId() + ",订单号=" + userPay.getPayId() + ",订单金额=" + userPay.getMoney());
							Dto queryDto = new BaseDto();
							queryDto.put("channelCode",paymentWay.getChannelCode());

							//查询并校验渠道银行配置
							boolean flag = true;
							queryDto.put("bankName",bankInfoVo.getBankName());
							Dto channelBankInfo = paymentWayMapper.queryPaymentWayBankInfo(queryDto);//查询渠道银行配置
							if(channelBankInfo == null || StringUtil.isEmpty(channelBankInfo.get("channelBankName")))
							{
								logger.info("[发送付款到银行账户的请求]渠道" + paymentWay.getName() + "尚未配置银行-" + bankInfoVo.getBankName() + "!订单所属用户编号=" + userPay.getUserId() + ",订单号=" + userPay.getPayId());
								flag = false;
								params.put("dcode","2000");
								params.put("dmsg","渠道" + paymentWay.getName() + "尚未配置银行-" + bankInfoVo.getBankName());
							}
							//查询并校验渠道省份配置
							queryDto.put("areaCode",bankInfoVo.getBankProvinceCode());
							queryDto.put("type","0");
							Dto channelProvince = paymentWayMapper.queryPaymentWayAreaInfo(queryDto);//查询渠道省份配置
							if(channelProvince == null || StringUtil.isEmpty(channelProvince.get("channelAreaName")))
							{
								logger.info("[发送付款到银行账户的请求]渠道" + paymentWay.getName() + "尚未配置省份-" + bankInfoVo.getBankProvince() + "!订单所属用户编号=" + userPay.getUserId() + ",订单号=" + userPay.getPayId());
								flag = false;
								params.put("dcode","2000");
								params.put("dmsg","渠道" + paymentWay.getName() + "尚未配置省份-" + bankInfoVo.getBankProvince());
							}
							//查询并校验渠道城市配置
							queryDto.put("areaCode",bankInfoVo.getBankCityCode());
							queryDto.put("type","1");
							Dto channelCity = paymentWayMapper.queryPaymentWayAreaInfo(queryDto);//查询渠道城市配置
							if(channelCity == null || StringUtil.isEmpty(channelCity.get("channelAreaName")))
							{
								logger.info("[发送付款到银行账户的请求]渠道" + paymentWay.getName() + "尚未配置城市-" + bankInfoVo.getBankCity() + "!订单所属用户编号=" + userPay.getUserId() + ",订单号=" + userPay.getPayId());
								flag = false;
								params.put("dcode","2000");
								params.put("dmsg","渠道" + paymentWay.getName() + "尚未配置城市-" + bankInfoVo.getBankCity());
							}
							if(flag)
							{
								//设置请求参数
								params.put("merchantKey", KuaiFuPayUtils.merchantKey);//设置商户key
								params.put("bankUserName",bankInfoVo.getAccountHolder());//设置持卡人真实姓名
								params.put("bankAccount",bankInfoVo.getBankCard());//设置代付卡卡号
								params.put("bankCode",channelBankInfo.getAsString("channelBankAbbreviation"));//设置银行编码
								params.put("bankProvince",bankInfoVo.getBankProvince());//设置开户行所在省份
								params.put("bankCity",bankInfoVo.getBankCity());//设置开户行所在城市
								params.put("bankName",channelBankInfo.getAsString("channelBankName"));//设置开户行全称
								params.put("amount",userPay.getMoney());//设置代付金额(单位:元)
								params.put("moblieNo",user.getMobile());//设置银行预留手机号码
								params.put("payId",userPay.getPayId());//设置商户订单号
								params.put("secretKey",KuaiFuPayUtils.secretKey);
								KuaiFuPayUtils.sendDaiFuRequest(params);//发送付款到银行账户的请求
							}
						}
						//kj412支付渠道
						else if(paymentWay.getChannelCode() == PayConstants.CHANNEL_CODE_OUT_KJ142PAY)
						{
							//设置请求参数
							logger.info("[发送付款到银行账户的请求]使用kj412发送付款请求,订单所属用户编号=" + userPay.getUserId() + ",订单号=" + userPay.getPayId() + ",订单金额=" + userPay.getMoney());
							params.put("apiUrl",Kj412PayUtils.apiUrl);//设置api地址
							params.put("merchantNo", Kj412PayUtils.merchantNo);//设置商户号
							params.put("secretKey",Kj412PayUtils.secretKey);
							params.put("notifyUrl",SysConfig.getHostApp() + SysConfig.getString("kj412.payment.result.notify"));//设置异步通知地址
							params.put("bankUserName",bankInfoVo.getAccountHolder());//设置持卡人真实姓名
							params.put("bankAccount",bankInfoVo.getBankCard());//设置代付卡卡号
							params.put("smoney",userPay.getMoney() + 2);//设置代付金额(单位:元)(需要额外加上2元作为手续费)
							params.put("payId",userPay.getPayId());//设置商户订单号
							Kj412PayUtils.sendDaiFuRequest(params);//发送付款到银行账户的请求
						}
						//ttpay支付渠道
						else if(paymentWay.getChannelCode() == PayConstants.CHANNEL_CODE_OUT_TTPAY)
						{
							logger.info("[发送付款到银行账户的请求]使用ttpay发送付款请求,订单所属用户编号=" + userPay.getUserId() + ",订单号=" + userPay.getPayId() + ",订单金额=" + userPay.getMoney());
							Dto queryDto = new BaseDto();
							queryDto.put("channelCode",paymentWay.getChannelCode());

							//查询并校验渠道银行配置
							boolean flag = true;
							queryDto.put("bankName",bankInfoVo.getBankName());
							Dto channelBankInfo = paymentWayMapper.queryPaymentWayBankInfo(queryDto);//查询渠道银行配置
							if(channelBankInfo == null || StringUtil.isEmpty(channelBankInfo.get("channelBankName")))
							{
								logger.info("[发送付款到银行账户的请求]渠道" + paymentWay.getName() + "尚未配置银行-" + bankInfoVo.getBankName() + "!订单所属用户编号=" + userPay.getUserId() + ",订单号=" + userPay.getPayId());
								flag = false;
								params.put("dcode","2000");
								params.put("dmsg","渠道" + paymentWay.getName() + "尚未配置银行-" + bankInfoVo.getBankName());
							}
							if(flag)
							{
								//设置请求参数
                                params.put("apiUrl",TTPayUtils.dfapiUrl);//设置api地址
                                params.put("merchantNo",TTPayUtils.dfmerId);//设置商户编号
                                params.put("bankUserName",bankInfoVo.getAccountHolder());//设置收款人
                                params.put("bankAccount",bankInfoVo.getBankCard());//设置收款账户
								params.put("bankName",channelBankInfo.getAsString("channelBankName"));//设置收款银行名称
								params.put("bankAbbreviation",channelBankInfo.getAsString("channelBankAbbreviation"));//设置收款银行简称
                                params.put("bankCode",channelBankInfo.getAsString("channelBankCode"));//设置收款银行编号
                                params.put("smoney",userPay.getMoney());//设置付款金额
                                params.put("payId",userPay.getPayId());//设置商户订单号
								TTPayUtils.sendDaiFuRequest(params);//发送付款到银行账户的请求
							}
						}
						//ypay支付渠道
						else if(paymentWay.getChannelCode() == PayConstants.CHANNEL_CODE_OUT_YPAY)
						{
							//设置请求参数
							logger.info("[发送付款到银行账户的请求]使用ypay发送付款请求,订单所属用户编号=" + userPay.getUserId() + ",订单号=" + userPay.getPayId() + ",订单金额=" + userPay.getMoney());
							params.put("merchantNo", YPayUtils.merchantNo);//设置商户号
							params.put("secretKey",YPayUtils.secretKey);
							params.put("apiUrl",YPayUtils.apiUrl);//设置api地址
							params.put("bankUserName",bankInfoVo.getAccountHolder());//设置收款人
							params.put("bankAccount",bankInfoVo.getBankCard());//设置收款账户
							params.put("bankName",bankInfoVo.getBankName());//设置收款银行名称
							params.put("branchName",bankInfoVo.getSubBankName());//设置支行
							if(StringUtil.isEmpty(bankInfoVo.getSubBankName()))
							{
								params.put("branchName",bankInfoVo.getBankName() + bankInfoVo.getBankCity() + "支行");
							}
							params.put("province",bankInfoVo.getBankProvince());//设置开户行所在省份
							params.put("city",bankInfoVo.getBankCity());//设置开户行所在城市
							params.put("smoney",userPay.getMoney() + 2);//设置付款金额(需要加上2元的手续费)
							params.put("payId",userPay.getPayId());//设置商户订单号
							YPayUtils.sendDaiFuRequest(params);//发送付款到银行账户的请求
						}
						else
                        {
							continue; //无可用渠道-不处理
						}

						/**
						 * 处理付款请求结果
						 */
						//重新处理
						if("2000".equals(params.getAsString("dcode")))
						{
							//更新订单为重新处理
							Dto updateParams = new BaseDto();
							updateParams.put("payId",userPay.getPayId());//设置商户订单号
							updateParams.put("status",PayConstants.PAYORDER_STATUS_CXCL);//设置订单状态为重新处理
							updateParams.put("channelCode",paymentWay.getChannelCode());//设置渠道编号
							updateParams.put("channelDesc",paymentWay.getChannelDesc());//设置渠道编号描述
							updateParams.put("remark",StringUtil.isEmpty(params.get("dmsg"))? "等待重新处理" : params.getAsString("dmsg"));//设置处理结果备注
							int count = userPayMapper.updateUserPay(updateParams);
							logger.info("[发送付款到银行账户的请求][重新处理]订单状态更新" + (count > 0? "更新成功" : "更新失败"));
						}
						else
						{
							//根据付款请求状态更新订单状态和流水状态,1000-付款请求发送成功
							if("1000".equals(params.getAsString("dcode")))
							{
								//更新订单
								Dto updateParams = new BaseDto();
								updateParams.put("payId",userPay.getPayId());//设置商户订单号
								updateParams.put("status",PayConstants.PAYORDER_STATUS_CLZ);//设置订单状态为处理中
								updateParams.put("channelCode",paymentWay.getChannelCode());//设置渠道编号
								updateParams.put("channelDesc",paymentWay.getChannelDesc());//设置渠道描述
								updateParams.put("remark",params.get("dmsg"));//设置处理结果备注
								int count = userPayMapper.updateUserPay(updateParams);
								logger.info("[发送付款到银行账户的请求][付款请求发送成功]订单更新" + (count > 0? "成功" : "失败") + "!"
										+ "订单所属用户编号=" + userPay.getUserId()
										+ ",订单号=" + userPay.getPayId()
										+ ",订单金额=" + userPay.getMoney());
								if(count > 0)
								{
									//更新账户流水
									Dto updateDetailDto = new BaseDto();
									updateDetailDto.put("userId",userPay.getUserId());//设置用户编号
									updateDetailDto.put("businessId",userPay.getPayId());//设置流水关联的订单号(取商户订单号)
									updateDetailDto.put("channelCode",paymentWay.getChannelCode());//设置业务渠道编号
									updateDetailDto.put("channelDesc",paymentWay.getChannelDesc());//设置业务渠道描述
									updateDetailDto.put("status",0);//设置流水状态(-1-无效 0-处理中 1-有效(已完成)
									updateDetailDto.put("remark","提现[" + userPay.getPayId() + "]");//设置备注
									count = userDetailMapper.updateUserDetailOfTx(updateDetailDto);//更新账户流水状态(提现)
									logger.info("[发送付款到银行账户的请求][付款请求发送成功]账户流水更新" + (count > 0? "成功!" : "失败!")
											+ "订单所属用户编号=" + userPay.getUserId()
											+ ",订单号=" + userPay.getPayId()
											+ ",订单金额=" + userPay.getMoney());
								}
								else
								{
									logger.info("[发送付款到银行账户的请求][付款请求发送成功]订单状态更新失败!"
											+ "订单所属用户编号=" + userPay.getUserId()
											+ ",订单号=" + userPay.getPayId()
											+ ",订单金额=" + userPay.getMoney());
								}
							}
							else
							{
								//更新订单
								logger.info("[发送付款到银行账户的请求]付款请求发送失败!"
										+ "订单所属用户编号=" + userPay.getUserId()
										+ ",订单号=" + userPay.getPayId()
										+ ",订单金额=" + userPay.getMoney()
										+ ",请求状态=" + params.getAsString("dcode")
										+ ",请求状态消息=" + params.getAsString("dmsg"));
								Dto updateParams = new BaseDto();
								updateParams.put("payId",userPay.getPayId());//设置商户订单号
								updateParams.put("status",PayConstants.PAYORDER_STATUS_FAILURE);//设置订单状态为处理失败
								updateParams.put("channelCode",paymentWay.getChannelCode());//设置渠道编号
								updateParams.put("channelDesc",paymentWay.getChannelDesc());//设置渠道编号描述
								updateParams.put("remark",params.get("dmsg"));//设置处理结果备注
								int count = userPayMapper.updateUserPay(updateParams);
								logger.info("[发送付款到银行账户的请求][付款请求发送失败]订单更新" + (count > 0? "成功" : "失败") + "!"
										+ "订单所属用户编号=" + userPay.getUserId()
										+ ",订单号=" + userPay.getPayId()
										+ ",订单金额=" + userPay.getMoney());
								if(count > 0)
								{
									//更新账户流水
									Dto updateDto = new BaseDto();
									updateDto.put("userId",userPay.getUserId());//设置用户编号
									updateDto.put("businessId",userPay.getPayId());//设置流水关联的订单号(取商户订单号)
									updateDto.put("status",-1);//设置流水状态为无效
									count = userDetailMapper.updateUserDetailOfTx(updateDto);
									logger.info("[发送付款到银行账户的请求][付款请求发送失败]账户流水更新" + (count > 0? "成功" : "失败") + "!"
											+ "订单所属用户编号=" + userPay.getUserId()
											+ ",订单号=" + userPay.getPayId()
											+ ",订单金额=" + userPay.getMoney());
									if(count > 0)
									{
										//更新用户账户
										updateDto = new BaseDto("userId",userPay.getUserId());
										updateDto.put("tbalance",userPay.getMoney());//余额加上订单的金额
										updateDto.put("twithDraw",-userPay.getMoney());//累计提现金额减去订单金额
										updateDto.put("offsetFrozen",-userPay.getMoney());//冻结金额减去订单的金额
										updateDto.put("offsetWithDraw",userPay.getMoney());//可提现金额加上订单金额
										count = userAccountMapper.updateUserAccount(updateDto);//更新用户账户信息
										logger.info("[发送付款到银行账户的请求][付款请求发送失败]用户账户更新" + (count > 0? "成功!" : "失败!")
												+ "订单所属用户编号=" + userPay.getUserId()
												+ ",订单号=" + userPay.getPayId());
									}
									else
									{
										logger.info("[发送付款到银行账户的请求][付款请求发送失败]账户流水更新失败!"
												+ "订单所属用户编号=" + userPay.getUserId()
												+ ",订单号=" + userPay.getPayId()
												+ ",订单金额=" + userPay.getMoney());
									}
								}
							}
						}
					}
					catch(Exception e0)
					{
						logger.info("[发送付款到银行账户的请求]发生异常!"
								+ "订单所属用户编号=" + userPay.getUserId()
								+ ",订单号=" + userPay.getPayId()
								+ ",订单金额=" + userPay.getMoney()
								+ ",异常信息:" + e0);
					}
				}
			}
			else
			{
				logger.info("[发送付款到银行账户的请求]当前没有待发送的付款到银行账户的请求.本次任务结束");
			}
		}
		catch (Exception e)
		{
			logger.error("[发送付款到银行账户的请求]发生异常!异常信息：", e);
		}
	}

	/**
	 * 获取付款(提现)方式(渠道)
	 * @author	sjq
	 */
	public PaymentWay getPaymentWay(UserPay userPay)
	{
		PaymentWay paymentWay = null;

		//获取系统目前可用的付款(提现)方式(渠道)
		Calendar current = Calendar.getInstance();//当前时间
		int currentHour = current.get(Calendar.HOUR_OF_DAY);//当前时间-时
		int currentMinute = current.get(Calendar.MINUTE);//当前时间-分
		Dto params = new BaseDto("status",1);
		List<PaymentWay> list = paymentWayMapper.queryPaymentWays(params);
		if(list != null && list.size() > 0)
		{
			//筛选,只保留当前实际可用的付款方式
            BankInfoVo bankInfoVo = userPay.getBankInfo();//提取提现银行卡信息
			List<PaymentWay> paymentWayList = new ArrayList<PaymentWay>();
			for(PaymentWay pw : list)
			{
				//判断渠道是否有单金额限制
				if((StringUtil.isNotEmpty(pw.getMaxMoney()) && userPay.getMoney() > pw.getMaxMoney())
						|| StringUtil.isNotEmpty(pw.getMinMoney()) && userPay.getMoney() < pw.getMinMoney())
				{
					continue;
				}
				//判断启用模式,如果为时间段
				if(pw.getModel() == 1)
				{
					//判断当前时间是否在时间段的起始和结束时间之间,如果不在时间段区间内,则该付款方式不可用
					if(current.before(pw.getTimeRangeStart()) || current.after(pw.getTimeRangeEnd()))
					{
						continue;
					}
				}
				//如果启用模式为时间特征
				else if(pw.getModel() == 2)
				{
					int count = 0;
					String[] timeCharacters = pw.getTimeCharacter().split(";");//提取时间特征
					for(String timeCharacter : timeCharacters)
					{
						//判断当前时间点是否在时间特征表述的范围内,只有当前时间在时间特征的范围内,该付款方式才可用
						String[] times = timeCharacter.split("~");
						String[] timeStart = times[0].split(":");
						String[] timeEnd = times[1].split(":");
						if((currentHour == Integer.parseInt(timeStart[0]) && currentMinute >= Integer.parseInt(timeStart[1])))
						{
							count ++;
						}
						else if(currentHour == Integer.parseInt(timeEnd[0]) && currentMinute <= Integer.parseInt(timeEnd[1]))
						{
							count ++;
						}
						else if(currentHour > Integer.parseInt(timeStart[0]) && currentHour < Integer.parseInt(timeEnd[0]))
						{
							count ++;
						}
					}
					if(count == 0)
					{
						continue;
					}
				}
				//查询渠道银行配置,如果该渠道尚未配置提现银行(不需要提现银行配置的渠道除外),则该提现渠道不可用
				if(pw.getChannelCode() != PayConstants.CHANNEL_CODE_OUT_KJ142PAY
						&& pw.getChannelCode() != PayConstants.CHANNEL_CODE_OUT_YPAY)
				{
					Dto queryDto = new BaseDto();
					queryDto.put("channelCode",pw.getChannelCode());
					queryDto.put("bankName",bankInfoVo.getBankName());
					Dto channelBankInfo = paymentWayMapper.queryPaymentWayBankInfo(queryDto);//查询渠道银行配置
					if(channelBankInfo == null)
					{
						continue;
					}
				}
				paymentWayList.add(pw);
			}
			//根据付款方式的比例确定本次使用哪种付款方式
			if(paymentWayList.size() > 0)
			{
				if(paymentWayList.size() == 1)
				{
					paymentWay = paymentWayList.get(0);//只有一个可用的付款方式,则直接取第一个
				}
				else
				{
					//计算总权重
					double sumRate = 0d;
					for(PaymentWay pw : paymentWayList)
					{
						sumRate += pw.getRate();
					}
					//计算各个付款方式所分布的权重空间
					List<Double> rateList = new ArrayList<Double>();
					Double tempRate = 0d;
					for(PaymentWay pw : paymentWayList)
					{
						tempRate += pw.getRate();
						rateList.add(tempRate / sumRate);
					}
					double rand = Math.random();//生成一个0~1的随机数
					rateList.add(rand);//将随机数加入权重集合中
					Collections.sort(rateList);//将权重升序排列
					int index = rateList.indexOf(rand);//获取随机数所在的区间位置
					paymentWay = paymentWayList.get(index);//提取随机数所在区间的付款方式
				}
			}
		}
		return paymentWay;
	}
}