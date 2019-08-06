package com.caipiao.service.pay;

import com.caipiao.common.constants.*;
import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.pay.PayUtils;
import com.caipiao.common.pay.aoyou.AoyouUtils;
import com.caipiao.common.pay.bbpay.BbPayUtils;
import com.caipiao.common.pay.huichao.HuiChaoUtils;
import com.caipiao.common.pay.huichao.utils.RsaUtils;
import com.caipiao.common.pay.juhe.JuHe10381Utils;
import com.caipiao.common.pay.kj412.Kj412PayUtils;
import com.caipiao.common.pay.kj412.util.StringUtils;
import com.caipiao.common.pay.kuaijie.KuaiJieUtils;
import com.caipiao.common.pay.momo.MomoPayUtils;
import com.caipiao.common.pay.payfubao.PayFuBaoUtils;
import com.caipiao.common.pay.shengpay.ShengpayUtils;
import com.caipiao.common.pay.swiftpass.SwiftpassUtils;
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
import com.caipiao.common.util.DoubleUtil;
import com.caipiao.common.util.SortUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.common.*;
import com.caipiao.dao.user.UserAccountMapper;
import com.caipiao.dao.user.UserDetailMapper;
import com.caipiao.dao.user.UserPayMapper;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.code.ErrorCode;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.common.PayChannel;
import com.caipiao.domain.common.PayWay;
import com.caipiao.domain.common.PayWayChannel;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.user.UserAccount;
import com.caipiao.domain.user.UserPay;
import com.caipiao.service.config.SysConfig;
import com.caipiao.service.exception.ServiceException;
import com.caipiao.service.user.UserService;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.*;

/**
 * 支付(充值)业务处理类
 * @author  mcdog
 */
@Service("payService")
public class PayService
{
    private static Logger logger = LoggerFactory.getLogger(PayService.class);

    @Autowired
    private PayWayMapper payWayMapper;

    @Autowired
    private PayChannelMapper payChannelMapper;

    @Autowired
    private PayWayChannelMapper payWayChannelMapper;

    @Autowired
    private UserPayMapper userPayMapper;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private UserDetailMapper userDetailMapper;

    @Autowired
    UserService userService;

    /**
     * 获取充值方式
     * @author  mcdog
     * @param   params     查询参数对象
     * @param   result     处理结果对象
     */
    public void getPayways(Dto params, ResultBean result) throws ServiceException
    {
        /**
         * 校验系统是否开放
         */
        String openStatus = SysConfig.getString("SYSTEM_OPEN_STATUS");
        if(StringUtil.isEmpty(openStatus) || "0".equals(openStatus))
        {
            throw new ServiceException(ErrorCode_API.SERVER_ERROR,"系统升级维护中!暂不可用此功能。");
        }
        /**
         * 设置查询参数
         */
        params.put("status",1);//状态为已启用
        params.put("clientTypes",KeyConstants.loginUserMap.get(params.getAsString("appId")));//客户端类型

        /**
         * 查询充值方式
         */
        List<PayWay> paywayList = payWayMapper.queryPayWays(params);
        List<Dto> dataList = new ArrayList<Dto>();
        if(paywayList != null && paywayList.size() > 0)
        {
            for(PayWay payway : paywayList)
            {
                Dto data = new BaseDto("pid",payway.getId());//设置充值方式编号
                data.put("pname",payway.getPayName());//设置充值方式名称
                data.put("sname",payway.getPayShort());//设置充值方式简称
                data.put("logo",(SysConfig.getHostStatic() + payway.getPayThumbUrl()));//设置充值方式logo

                //设置充值方式业务编号
                if(PayConstants.CHANNEL_CODE_IN_PAY_WEIXIN == payway.getPayCode()
                        || PayConstants.CHANNEL_CODE_IN_PAY_ALIPAY == payway.getPayCode())
                {
                    data.put("pcode",0);//设置充值模式(0-app 1-h5)
                }
                else
                {
                    data.put("pcode",1);//设置充值模式(0-app 1-h5)
                }
                dataList.add(data);
            }
        }
        //设置返回数据
        result.setData(new BaseDto("list",dataList));
        result.setErrorCode(ErrorCode.SUCCESS);
    }

    /**
     * 获取充值方式
     * @author  mcdog
     * @param   params     查询参数对象
     * @param   result     处理结果对象
     */
    public void getNewPayways(Dto params, ResultBean result) throws ServiceException
    {
        /**
         * 校验系统是否开放
         */
        String openStatus = SysConfig.getString("SYSTEM_OPEN_STATUS");
        if(StringUtil.isEmpty(openStatus) || "0".equals(openStatus))
        {
            throw new ServiceException(ErrorCode_API.SERVER_ERROR,"系统升级维护中!暂不可用此功能。");
        }
        //校验订单金额
        if(StringUtil.isEmpty(params.get("smoney")))
        {
            logger.error("[获取充值方式]参数校验不通过!用户编号=" + params.getAsString("userId") + ",接收原始参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        /**
         * 设置查询参数
         */
        params.put("status",1);//状态为已启用
        params.put("clientTypes",KeyConstants.loginUserMap.get(params.getAsString("appId")));//客户端类型

        /**
         * 查询充值方式
         */
        List<PayWay> paywayList = payWayMapper.queryPayWays(params);
        List<Dto> dataList = new ArrayList<Dto>();
        if(paywayList != null && paywayList.size() > 0)
        {
            double smoney = params.getAsDoubleValue("smoney");//订单金额
            Dto paywayChannelQueryDto = new BaseDto("clientTypes",params.get("clientTypes"));
            paywayChannelQueryDto.put("status","1");
            paywayChannelQueryDto.put("channelStatus","1");
            Calendar current = Calendar.getInstance();//当前时间
            int currentHour = current.get(Calendar.HOUR_OF_DAY);//当前时间-时
            int currentMinute = current.get(Calendar.MINUTE);//当前时间-分
            for(PayWay payway : paywayList)
            {
                /**
                 * 判断充值方式是否符合
                 */
                //查询已开通并已启用该充值方式的充值渠道
                List<PayWayChannel> notSupportList = new ArrayList<PayWayChannel>();
                paywayChannelQueryDto.put("paywayId",payway.getId());
                List<PayWayChannel> paywayChannelList = payWayChannelMapper.queryPayWayChannels(paywayChannelQueryDto);//查询已开通并已启用该充值方式的充值渠道
                if(paywayChannelList == null && paywayChannelList.size() == 0)
                {
                    continue;
                }
                //判断充值方式是否可用
                int status = 0;//充值方式可用状态,0-可用 1-不可用
                String desc = payway.getShowDesc();//充值方式显示描述
                for(PayWayChannel payWayChannel : paywayChannelList)
                {
                    if((StringUtil.isNotEmpty(payWayChannel.getMaxMoney()) && smoney > payWayChannel.getMaxMoney())
                            || StringUtil.isNotEmpty(payWayChannel.getMinMoney()) && smoney < payWayChannel.getMinMoney())
                    {
                        notSupportList.add(payWayChannel);
                        if(StringUtil.isNotEmpty(payWayChannel.getMaxMoney()) && StringUtil.isEmpty(payWayChannel.getMinMoney()))
                        {
                            desc = "单笔交易金额最大为" + payWayChannel.getMaxMoney() + "元";
                        }
                        else if(StringUtil.isNotEmpty(payWayChannel.getMinMoney()) && StringUtil.isEmpty(payWayChannel.getMaxMoney()))
                        {
                            desc = "单笔交易金额至少为" + payWayChannel.getMinMoney() + "元";
                        }
                        else
                        {
                            desc = "单笔交易金额范围为" + payWayChannel.getMinMoney() + "~" + payWayChannel.getMaxMoney() + "元";
                        }
                        continue;
                    }
                    //判断渠道是否只支持固定金额充值
                    if(StringUtil.isNotEmpty(payWayChannel.getFixedMoney()))
                    {
                        boolean flag = false;
                        String[] fixedMoney = payWayChannel.getFixedMoney().split(";");
                        for(String money : fixedMoney)
                        {
                            if(Double.parseDouble(money) == smoney)
                            {
                                flag = true;
                                break;
                            }
                        }
                        if(!flag)
                        {
                            notSupportList.add(payWayChannel);
                            desc = "允许的充值金额为:" + payWayChannel.getFixedMoney().replace(";","/");
                            continue;
                        }
                    }
                    //判断启用模式,如果为时间段
                    if(payWayChannel.getModel() == 1)
                    {
                        //判断当前时间是否在时间段的起始和结束时间之间,如果不在时间段区间内,则该付款方式不可用
                        if(current.before(payWayChannel.getTimeRangeStart()) || current.after(payWayChannel.getTimeRangeEnd()))
                        {
                            notSupportList.add(payWayChannel);
                            desc = "当前无可用渠道";
                            continue;
                        }
                    }
                    //如果启用模式为时间特征
                    else if(payWayChannel.getModel() == 2)
                    {
                        int count = 0;
                        String[] timeCharacters = payWayChannel.getTimeCharacter().split(";");//提取时间特征
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
                            notSupportList.add(payWayChannel);
                            desc = "当前无可用渠道";
                            continue;
                        }
                    }
                }
                if(paywayChannelList.size() == 1 && paywayChannelList.size() == notSupportList.size())
                {
                    status = 1;
                }
                else if(paywayChannelList.size() == notSupportList.size())
                {
                    continue;
                }

                /**
                 * 设置充值方式参数
                 */
                Dto data = new BaseDto("pid",payway.getId());//设置充值方式编号
                data.put("pname",payway.getPayName());//设置充值方式名称
                data.put("sname",payway.getPayShort());//设置充值方式简称
                data.put("logo",(SysConfig.getHostStatic() + payway.getPayThumbUrl()));//设置充值方式logo
                data.put("pstatus",status);//设置充值方式状态
                data.put("pdesc",status == 0? payway.getShowDesc() : desc);//设置充值方式显示描述

                //设置充值方式业务编号
                if(PayConstants.CHANNEL_CODE_IN_PAY_WEIXIN == payway.getPayCode()
                        || PayConstants.CHANNEL_CODE_IN_PAY_ALIPAY == payway.getPayCode())
                {
                    data.put("pcode",0);//设置充值模式(0-app 1-h5)
                }
                else
                {
                    data.put("pcode",1);//设置充值模式(0-app 1-h5)
                }
                dataList.add(data);
            }
        }
        //设置返回数据
        result.setData(new BaseDto("list",dataList));
        result.setErrorCode(ErrorCode.SUCCESS);
    }

    /**
     * 获取充值描述
     * @author  mcdog
     * @param   params     查询参数对象
     * @param   result     处理结果对象
     */
    public void getRechargeDesc(Dto params, ResultBean result) throws ServiceException
    {
        String rechargeDesc = SysConfig.getString("PAY_RECHARGE_DESC");//获取系统设置的充值描述
        if(StringUtil.isEmpty(rechargeDesc))
        {
            result.setErrorCode(ErrorCode.SERVER_ERROR);
        }
        else
        {
            result.setData(new BaseDto("desc",rechargeDesc));
            result.setErrorCode(ErrorCode.SUCCESS);
        }
    }

    /**
     * 获取充值交易参数
     * @author  mcdog
     * @param   params      参数对象
     * @param   result      处理结果对象
     */
    public synchronized void getPayParams(Dto params, ResultBean result) throws ServiceException,Exception
    {
        /**
         * 校验系统是否开放
         */
        String openStatus = SysConfig.getString("SYSTEM_OPEN_STATUS");
        if(StringUtil.isEmpty(openStatus) || "0".equals(openStatus))
        {
            throw new ServiceException(ErrorCode_API.SERVER_ERROR,"系统升级维护中!暂不可用此功能。");
        }

        /**
         * 校验参数
         */
        //判断充值方式/支付金额是否为空
        if(StringUtil.isEmpty(params.get("pid")) || StringUtil.isEmpty(params.get("smoney")))
        {
            logger.error("[获取充值交易参数]参数校验不通过!用户编号=" + params.getAsString("userId") + ",接收原始参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        //判断金额是否合法
        Double smoney = params.getAsDouble("smoney");
        String smoney1 = params.getAsString("smoney");
        if(smoney == null || smoney <= 0)
        {
            logger.error("[获取充值交易参数]金额不合法!用户编号=" + params.getAsString("userId") + ",金额=" + smoney);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        else if(smoney1.indexOf(".") > -1 && smoney1.substring(smoney1.indexOf(".") + 1).length() > 2)
        {
            logger.error("[获取充值交易参数]金额不合法!用户编号=" + params.getAsString("userId") + ",金额=" + smoney);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        //判断充值方式是存在
        String clientType = KeyConstants.loginUserMap.get(params.getAsString("appId")) + "";//获取客户端来源
        List<PayWay> payWaysList = payWayMapper.queryPayWays(new BaseDto("id",params.get("pid")));
        if(payWaysList == null || payWaysList.size() == 0)
        {
            logger.error("[获取充值交易参数]充值方式不存在!用户编号=" + params.getAsString("userId") + ",充值方式编号=" + params.getAsString("pid"));
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        //判断充值方式是否开放
        PayWay payWay = payWaysList.get(0);
        if(payWay.getStatus() != 1)
        {
            logger.error("[获取充值交易参数]充值方式暂未开放!用户编号=" + params.getAsString("userId") + ",充值方式编号=" + params.getAsString("pid"));
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        else if(payWay.getClientTypes().indexOf(clientType) < 0)
        {
            logger.error("[获取充值交易参数]充值方式暂未对客户端" + clientType + "开放!用户编号=" + params.getAsString("userId")
                    + ",支付方式编号=" + params.getAsString("pid")
                    + ",客户端来源=" + clientType);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }

        /**
         * 设置充值渠道配置
         */
        Dto paywayChannelQueryDto = new BaseDto("paywayId",params.get("pid"));
        paywayChannelQueryDto.put("clientTypes",clientType);
        paywayChannelQueryDto.put("status","1");
        paywayChannelQueryDto.put("channelStatus","1");
        List<PayWayChannel> paywayChannelList = payWayChannelMapper.queryPayWayChannels(paywayChannelQueryDto);//查询已开通并已启用该充值方式的充值渠道
        if(paywayChannelList == null || paywayChannelList.size() == 0)
        {
            logger.error("[获取充值交易参数]系统当前无任何开通且启用了" + payWay.getPayName() + "的充值渠道配置!"
                    + ",用户编号=" + params.getAsString("userId")
                    + ",充值方式编号=" + payWay.getId() + ",充值方式名称=" + payWay.getPayName()
                    + ",客户端来源=" + clientType);
            throw new ServiceException(ErrorCode_API.ERROR_PAY_NOPAYWAYCHANNEL,ErrorCode_API.ERROR_PAY_NOPAYWAYCHANNEL_MSG);
        }
        //非银联充值方式,则将订单金额+随机浮动金额(浮动金额范围为 0.01 <= 浮动金额 <= 0.99)
        if(PayConstants.CHANNEL_CODE_IN_PAY_UNIONPAY != payWay.getPayCode())
        {
            //个位为0的充值金额,随机加1-5元的浮动作为最终的充值金额
            if(smoney.intValue() % 10 == 0)
            {
                smoney += new Random().nextInt(5) + 1;
            }
            /*if(((int)smoney.doubleValue()) == DoubleUtil.roundDouble(smoney,2))
            {
                double floatMoney = DoubleUtil.roundDouble(new Random().nextDouble(),2);
                floatMoney = floatMoney == 0? 0.01 : (floatMoney == 1? 0.99 : floatMoney);
                smoney += floatMoney;//实际订单金额需要加上随机
            }*/
        }
        //判断当前是否有可用的充值渠道配置
        PayWayChannel payWayChannel = getPaywayChannel(paywayChannelList,smoney);//获取可用的充值渠道
        if(payWayChannel == null)
        {
            logger.error("[获取充值交易参数]系统当前无任何充值渠道配置支持" + payWay.getPayName()
                    + ",用户编号=" + params.getAsString("userId")
                    + ",充值方式编号=" + payWay.getId() + ",充值方式名称=" + payWay.getPayName()
                    + ",客户端来源=" + clientType);
            throw new ServiceException(ErrorCode_API.ERROR_PAY_NOPAYWAYCHANNEL,ErrorCode_API.ERROR_PAY_NOPAYWAYCHANNEL_MSG);
        }
        //判断充值渠道是否存在
        List<PayChannel> payChannelList = payChannelMapper.queryPayChannels(new BaseDto("id",payWayChannel.getPaychannelId()));//查询充值渠道
        if(payChannelList == null || payChannelList.size() == 0)
        {
            logger.error("[获取充值交易参数]充值渠道不存在!充值渠道编号=" + payWayChannel.getPaychannelId()
                    + ",充值方式编号=" + payWayChannel.getPaywayId());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_NOPAYWAYCHANNEL,ErrorCode_API.ERROR_PAY_NOPAYWAYCHANNEL_MSG);
        }
        PayChannel payChannel = payChannelList.get(0);

        /**
         * 设置预下单参数
         */
        Dto payDto = new BaseDto();
        payDto.put("merchantNo",payWayChannel.getMerchantNo());//设置商户号
        payDto.put("appNo",payWayChannel.getAppNo());//设置应用编号/产品编号
        payDto.put("appName",payWayChannel.getAppName());//设置应用名称/app名称/商品描述
        payDto.put("apiUrl",payWayChannel.getApiUrl());//设置api地址
        payDto.put("notifyUrl",payWayChannel.getNotifyUrl());//设置支付结果通知地址
        payDto.put("returnUrl",payWayChannel.getReturnUrl());//设置页面通知地址(支付成功后跳转的地址)
        payDto.put("signType",payWayChannel.getSignType());//设置签名方式
        payDto.put("secretKey",payWayChannel.getSecretKey());//设置签名密钥
        payDto.put("rsaPublicKey", payWayChannel.getRsaPublicKey());//设置RSA公钥
        payDto.put("rsaPrivateKey",payWayChannel.getRsaPrivateKey());//设置RSA私钥
        payDto.put("deviceInfo",payWayChannel.getDeviceInfo());//设置终端设备信息
        payDto.put("webAddress",payWayChannel.getWebAddress());//设置应用官网地址
        payDto.put("userIp",SysConfig.getString("API_HOST_IP"));//设置终端ip
        payDto.put("clientIp",params.get("requestIp"));//设置客户端ip
        payDto.put("clientFrom",KeyConstants.loginUserMap.get(params.getAsString("appId")));//设置客户端来源
        payDto.put("mobilehost",SysConfig.getString("MOBILE_HOST"));//设置移动端域名

        //设置商户订单号
        String payId = "CZ" + DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME);//生成商户订单号
        payId += new Random().nextInt(10);
        payDto.put("payId",payId);//设置商户订单号

        //设置系统配置的单次充值最大金额及订单金额
        String maxSmoney = SysConfig.getString("MAX_RECHARGE_SMONEY");
        if(StringUtil.isNotEmpty(maxSmoney))
        {
            payDto.put("maxSmoney",maxSmoney);//设置系统配置的单次充值最大金额
        }
        payDto.put("smoney",smoney);//设置订单金额

        /**
         * 根据充值方式和充值渠道发送预下单请求
         */
        //微信官方
        int pcode = 0;//充值模式(0-app 1-h5)
        if(PayConstants.PAYCHANNEL_CODE_WEIXIN == payChannel.getChannelCode())
        {
            /**
             * 根据充值方式发送预下单请求
             */
            //微信app预下单
            if(PayConstants.CHANNEL_CODE_IN_PAY_WEIXIN == payWay.getPayCode())
            {
                WeixinUtils.createAppPay(payDto);
            }
        }
        //快接
        else if(PayConstants.PAYCHANNEL_CODE_KUAIJIE == payChannel.getChannelCode()
                || PayConstants.PAYCHANNEL_CODE_KUAIJIE2 == payChannel.getChannelCode())
        {
            /**
             * 根据充值方式发送预下单请求
             */
            //微信app预下单
            if(PayConstants.CHANNEL_CODE_IN_PAY_WEIXIN == payWay.getPayCode())
            {
                KuaiJieUtils.createWeiXinAppPay(payDto);
            }
            //微信H5预下单
            else if(PayConstants.CHANNEL_CODE_IN_PAY_WEIXINH5 == payWay.getPayCode())
            {
                pcode = 1;
                KuaiJieUtils.createWeiXinWapPay(payDto);
            }
            //支付宝app预下单
            else if(PayConstants.CHANNEL_CODE_IN_PAY_ALIPAY == payWay.getPayCode())
            {
                KuaiJieUtils.createAlipayAppPay(payDto);
            }
            //支付宝H5预下单
            else if(PayConstants.CHANNEL_CODE_IN_PAY_ALIPAYH5 == payWay.getPayCode())
            {
                pcode = 1;
                KuaiJieUtils.createAlipayWapPay(payDto);
            }
            //QQ钱包(H5)预下单
            else if(PayConstants.CHANNEL_CODE_IN_PAY_QQWALLETH5 == payWay.getPayCode())
            {
                pcode = 1;
                KuaiJieUtils.createQqWalletWapPay(payDto);
            }
            //京东钱包(H5)预下单
            else if(PayConstants.CHANNEL_CODE_IN_PAY_JDWALLETH5 == payWay.getPayCode())
            {
                pcode = 1;
                KuaiJieUtils.createJdWalletWapPay(payDto);
            }
        }
        //贝付宝
        else if(PayConstants.PAYCHANNEL_CODE_PAYFUBAO == payChannel.getChannelCode())
        {
            //微信H5预下单
            if(PayConstants.CHANNEL_CODE_IN_PAY_WEIXINH5 == payWay.getPayCode())
            {
                pcode = 1;
                PayFuBaoUtils.createWeixinWapPay(payDto);
            }
            //支付宝H5预下单
            else if(PayConstants.CHANNEL_CODE_IN_PAY_ALIPAYH5 == payWay.getPayCode())
            {
                pcode = 1;
                PayFuBaoUtils.createAlipayWapPay(payDto);
            }
        }
        //威富通
        else if(PayConstants.PAYCHANNEL_CODE_SWIFTPASS == payChannel.getChannelCode())
        {
            //微信H5下单
            if(PayConstants.CHANNEL_CODE_IN_PAY_WEIXIN == payWay.getPayCode())
            {
                KuaiJieUtils.createWeiXinAppPay(payDto);
            }
        }
        //豆豆平台
        else if(PayConstants.PAYCHANNEL_CODE_DOUDOUPAY == payChannel.getChannelCode())
        {

        }
        //迅游通
        else if(PayConstants.PAYCHANNEL_CODE_XUNYOUTONG == payChannel.getChannelCode())
        {
            //QQ钱包(H5)预下单
            if(PayConstants.CHANNEL_CODE_IN_PAY_QQWALLETH5 == payWay.getPayCode())
            {
                pcode = 1;
                XunYouTongUtils.createQqWalletWapPay(payDto);
            }
            //京东钱包(H5)预下单
            else if(PayConstants.CHANNEL_CODE_IN_PAY_JDWALLETH5 == payWay.getPayCode())
            {
                pcode = 1;
                KuaiJieUtils.createJdWalletWapPay(payDto);
            }
        }
        //聚合支付10381
        else if(PayConstants.PAYCHANNEL_CODE_JUHE10381PAY == payChannel.getChannelCode())
        {
            //支付宝H5预下单
            if(PayConstants.CHANNEL_CODE_IN_PAY_ALIPAYH5 == payWay.getPayCode())
            {
                pcode = 1;
                JuHe10381Utils.createAlipayWapPay(payDto);
            }
        }
        //直付支付
        else if(PayConstants.PAYCHANNEL_CODE_ZHIFUPAY == payChannel.getChannelCode())
        {
            //支付宝H5预下单
            if(PayConstants.CHANNEL_CODE_IN_PAY_ALIPAYH5 == payWay.getPayCode())
            {
                pcode = 1;
                ZhifuUtils.createAlipayWapPay(payDto);
            }
        }
        //智能云收银
        else if(PayConstants.PAYCHANNEL_CODE_ZHINENGYUNPAY == payChannel.getChannelCode())
        {
            //支付宝H5预下单
            if(PayConstants.CHANNEL_CODE_IN_PAY_ALIPAYH5 == payWay.getPayCode())
            {
                pcode = 1;
                ZhinengyunUtils.createAlipayWapPay(payDto);
                if(1000 == payDto.getAsInteger("dcode"))
                {
                    double rsmoney = payDto.getAsDoubleValue("rsmoney");//提取订单实际金额
                    if(rsmoney > smoney)
                    {
                        logger.info("[获取充值交易参数]原充值金额=" + smoney + ",实际充值金额=" + rsmoney + ",订单号=" + payId + ",订单所属用户id=" + params.getAsString("userId"));
                        smoney = rsmoney;
                    }
                }
            }
        }
        //傲游支付
        else if(PayConstants.PAYCHANNEL_CODE_AOYOUPAY == payChannel.getChannelCode())
        {
            //支付宝H5预下单
            if(PayConstants.CHANNEL_CODE_IN_PAY_ALIPAYH5 == payWay.getPayCode())
            {
                pcode = 1;
                AoyouUtils.createAlipayWapPay(payDto);
            }
        }
        //BB支付
        else if(PayConstants.PAYCHANNEL_CODE_BBPAY == payChannel.getChannelCode())
        {
            //微信H5预下单
            if(PayConstants.CHANNEL_CODE_IN_PAY_WEIXINH5 == payWay.getPayCode())
            {
                pcode = 1;
                BbPayUtils.createWeixinWapPay(payDto);
            }
            //支付宝H5预下单
            else if(PayConstants.CHANNEL_CODE_IN_PAY_ALIPAYH5 == payWay.getPayCode())
            {
                pcode = 1;
                BbPayUtils.createAlipayWapPay(payDto);
            }
        }
        //陌陌付
        else if(PayConstants.PAYCHANNEL_CODE_MOMOPAY == payChannel.getChannelCode())
        {
            //支付宝H5预下单
            if(PayConstants.CHANNEL_CODE_IN_PAY_ALIPAYH5 == payWay.getPayCode())
            {
                pcode = 1;
                MomoPayUtils.createAlipayWapPay(payDto);
            }
        }
        //兆行支付
        else if(PayConstants.PAYCHANNEL_CODE_ZHAOXINGJUHEPAY == payChannel.getChannelCode())
        {
            //支付宝H5预下单
            if(PayConstants.CHANNEL_CODE_IN_PAY_ALIPAYH5 == payWay.getPayCode())
            {
                pcode = 1;
                ZhaohangPayUtils.createAlipayWapPay(payDto);
            }
        }
        //万两支付
        else if(PayConstants.PAYCHANNEL_CODE_WLPAY == payChannel.getChannelCode())
        {
            //银联预下单
            if(PayConstants.CHANNEL_CODE_IN_PAY_UNIONPAY == payWay.getPayCode())
            {
                pcode = 1;
                payDto.put("bankCode",WlPayUtils.BANKCODE_UNIONPAYWAP);
                WlPayUtils.createUnionWapPay(payDto);
            }
        }
        //亿富通支付
        else if(PayConstants.PAYCHANNEL_CODE_YIFUTONGPAY == payChannel.getChannelCode())
        {
            //支付宝H5预下单
            if(PayConstants.CHANNEL_CODE_IN_PAY_ALIPAYH5 == payWay.getPayCode())
            {
                pcode = 1;
                YifutongPayUtils.createAlipayWapPay(payDto);
            }
        }
        //易旨支付
        else if(PayConstants.PAYCHANNEL_CODE_YIZHIPAY == payChannel.getChannelCode())
        {
            //银联预下单
            if(PayConstants.CHANNEL_CODE_IN_PAY_UNIONPAY == payWay.getPayCode())
            {
                pcode = 1;
                YizhiPayUtils.createUnionWapPay(payDto);
            }
        }
        //ypay
        else if(PayConstants.PAYCHANNEL_CODE_YPAY == payChannel.getChannelCode())
        {
            //银联预下单
            if(PayConstants.CHANNEL_CODE_IN_PAY_UNIONPAY == payWay.getPayCode())
            {
                pcode = 1;
                YPayUtils.createUnionWapPay(payDto);
            }
        }
        //kj412
        else if(PayConstants.PAYCHANNEL_CODE_KJ142PAY == payChannel.getChannelCode())
        {
            //银联预下单
            if(PayConstants.CHANNEL_CODE_IN_PAY_UNIONPAY == payWay.getPayCode())
            {
                pcode = 1;
                Kj412PayUtils.createUnionWapPay(payDto);
            }
        }
        //ttpay
        else if(PayConstants.PAYCHANNEL_CODE_TTPAY == payChannel.getChannelCode())
        {
            //QQ支付
            if(PayConstants.CHANNEL_CODE_IN_PAY_QQWALLETH5 == payWay.getPayCode())
            {
                pcode = 1;
                TTPayUtils.createQqWalletWapPay(payDto);
            }
        }

        /**
         * 处理下单结果
         */
        //下单成功,则保存订单信息
        if(1000 == payDto.getAsInteger("dcode"))
        {
            UserPay userPay = new UserPay();
            userPay.setPayCode(payWay.getPayCode());//设置充值方式业务编号
            userPay.setPayDesc(payWay.getPayDesc());//设置充值方式业务描述
            userPay.setChannelCode(payChannel.getChannelCode());//设置充值渠道编号
            userPay.setChannelDesc(payChannel.getChannelDesc());//设置充值渠道描述
            userPay.setUserId(params.getAsLong("userId"));//设置用户编号
            userPay.setPayType(PayConstants.PAY_TYPE_RECHARGE);//设置业务类型
            userPay.setMoney(smoney);//设置交易金额
            userPay.setOmoney(StringUtil.isEmpty(payDto.get("rsmoney"))? smoney : payDto.getAsDouble("rsmoney"));//设置订单原始金额
            userPay.setPayId(payId);//设置商户订单号
            userPay.setChannelPayId(payDto.getAsString("tradeNo"));//设置渠道流水号
            userPay.setStatus(PayConstants.PAYORDER_STATUS_CLZ);//设置处理状态为处理中(-1-处理失败 0-待处理 1-等待重新处理 2-处理中 3-处理成功)
            userPay.setClientFrom(KeyConstants.loginUserMap.get(params.getAsString("appId")));//设置客户端来源
            userPay.setRequestIp(params.getAsString("requestIp"));//设置请求支付的客户端ip地址
            userPay.setCreateTime(new Date());//设置请求支付时间
            int count = userPayMapper.insertUserPay(userPay);//保存订单信息
            if(count > 0)
            {
                //设置返回数据
                logger.info("[获取充值交易参数]预下单成功!用户编号=" + params.getAsString("userId") + ",订单金额=" + smoney + ",订单号=" + payId);
                result.setErrorCode(ErrorCode_API.SUCCESS);
                Dto data = (Dto)payDto.get("results");//提取支付参数
                data.put("payId",userPay.getPayId());//设置商户订单号
                data.put("pid",payWay.getId());//设置充值方式编号
                data.put("pcode",pcode);//设置充值模式(0-app 1-h5)
                result.setData(data);//设置返回数据
            }
            else
            {
                throw new ServiceException(ErrorCode.SERVER_ERROR);
            }
        }
        else
        {
            logger.info("[获取充值交易参数]预下单失败!用户编号=" + params.getAsString("userId") + ",订单金额=" + smoney + ",dcode=" + payDto.getAsString("dcode") + ",dmsg=" + payDto.getAsString("dmsg"));
            result.setErrorCode(ErrorCode.SERVER_ERROR);
            return;
        }
    }

    /**
     * 获取充值结果
     * @author  mcdog
     * @param   params      查询参数对象
     * @param   result      处理结果对象
     */
    public void getPayResult(Dto params, ResultBean result) throws ServiceException
    {
        /**
         * 参数校验
         */
        if(StringUtil.isEmpty(params.get("payId")))
        {
            logger.error("[获取充值结果]用户编号=" + params.getAsString("userId") + ",参数校验不通过!接收原始参数=" + params.toString());
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
        //根据商户订单号查询订单信息
        String payId = params.getAsString("payId");//提取商户订单号
        List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",payId));
        if(userPayList == null || userPayList.size() == 0)
        {
            logger.error("[获取充值结果]用户编号=" + params.getAsString("userId") + ",查询不到订单信息!订单号=" + payId);
            throw new ServiceException(ErrorCode_API.ERROR_PAY_ORDERNOTEXIST,ErrorCode_API.ERROR_PAY_ORDERNOTEXIST_MSG);
        }
        //设置返回数据
        UserPay userPay = userPayList.get(0);
        Dto data = new BaseDto();
        data.put("money",userPay.getMoney());//设置充值金额
        data.put("channel",userPay.getPayDesc());//设置充值渠道(实际取的充值方式)

        //设置充值状态
        if(userPay.getStatus() == PayConstants.PAYORDER_STATUS_DCL
                || userPay.getStatus() == PayConstants.PAYORDER_STATUS_CXCL
                || userPay.getStatus() == PayConstants.PAYORDER_STATUS_CLZ)
        {
            data.put("status","0");//设置充值状态为充值中(-1-充值失败 0-充值中 1-支付成功)
            data.put("desc","充值结果处理中");//设置充值状态描述
        }
        else if(userPay.getStatus() == PayConstants.PAYORDER_STATUS_FAILURE)
        {
            data.put("status","-1");
            data.put("desc","充值失败");
        }
        else if(userPay.getStatus() == PayConstants.PAYORDER_STATUS_SUCCESS)
        {
            data.put("status","1");
            data.put("desc","充值成功");
        }
        //查询用户当前余额
        try
        {
            UserAccount userAccount = userAccountMapper.queryUserAccountInfoByUserId(params.getAsLong("userId"));
            if(userAccount == null)
            {
                throw new ServiceException(ErrorCode_API.SERVER_ERROR);
            }
            data.put("balance",userAccount.getBalance());
        }
        catch (Exception e)
        {
            logger.error("[获取充值结果]用户编号=" + params.getAsString("userId") + ",查询用户当前余额发生异常,异常信息:" + e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        result.setData(data);
        result.setErrorCode(ErrorCode_API.SUCCESS);
    }

    /**
     * 银联预订单
     * @author  mcdog
     * @param   params      参数对象
     * @param   result      处理结果对象
     */
    public synchronized void createUnionpayOrder(Dto params, ResultBean result) throws ServiceException,Exception
    {
        /**
         * 校验参数
         */
        //判断充值方式/支付金额是否为空
        if(StringUtil.isEmpty(params.get("pid"))
                || StringUtil.isEmpty(params.get("smoney"))
                || StringUtil.isEmpty(params.get("bankCode")))
        {
            logger.error("[银联预订单]参数校验不通过!用户编号=" + params.getAsString("userId") + ",接收原始参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        //判断金额是否合法
        Double smoney = params.getAsDouble("smoney");
        if(smoney == null || smoney <= 0)
        {
            logger.error("[银联预订单]金额不合法!用户编号=" + params.getAsString("userId") + ",金额=" + smoney);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        //判断充值方式是存在
        String clientType = KeyConstants.loginUserMap.get(params.getAsString("appId")) + "";//获取客户端来源
        List<PayWay> payWaysList = payWayMapper.queryPayWays(new BaseDto("id",params.get("pid")));
        if(payWaysList == null || payWaysList.size() == 0)
        {
            logger.error("[银联预订单]充值方式不存在!用户编号=" + params.getAsString("userId") + ",充值方式编号=" + params.getAsString("pid"));
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        //判断充值方式是否开放
        PayWay payWay = payWaysList.get(0);
        if(payWay.getStatus() != 1)
        {
            logger.error("[银联预订单]充值方式暂未开放!用户编号=" + params.getAsString("userId") + ",充值方式编号=" + params.getAsString("pid"));
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }
        else if(payWay.getClientTypes().indexOf(clientType) < 0)
        {
            logger.error("[银联预订单]充值方式暂未对客户端" + clientType + "开放!用户编号=" + params.getAsString("userId")
                    + ",支付方式编号=" + params.getAsString("pid")
                    + ",客户端来源=" + clientType);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR);
        }

        /**
         * 设置充值渠道配置
         */
        Dto paywayChannelQueryDto = new BaseDto("paywayId",params.get("pid"));
        paywayChannelQueryDto.put("clientTypes",clientType);
        paywayChannelQueryDto.put("status","1");
        paywayChannelQueryDto.put("channelStatus","1");
        List<PayWayChannel> paywayChannelList = payWayChannelMapper.queryPayWayChannels(paywayChannelQueryDto);//查询已开通并已启用该充值方式的充值渠道
        if(paywayChannelList == null || paywayChannelList.size() == 0)
        {
            logger.error("[银联预订单]系统当前无任何开通且启用了" + payWay.getPayName() + "的充值渠道配置!"
                    + ",用户编号=" + params.getAsString("userId")
                    + ",充值方式编号=" + payWay.getId() + ",充值方式名称=" + payWay.getPayName()
                    + ",客户端来源=" + clientType);
            throw new ServiceException(ErrorCode_API.ERROR_PAY_NOPAYWAYCHANNEL,ErrorCode_API.ERROR_PAY_NOPAYWAYCHANNEL_MSG);
        }
        //判断当前是否有可用的充值渠道配置
        PayWayChannel payWayChannel = getPaywayChannel(paywayChannelList,smoney);//获取可用的充值渠道
        if(payWayChannel == null)
        {
            logger.error("[银联预订单]系统当前无任何充值渠道配置支持" + payWay.getPayName()
                    + ",用户编号=" + params.getAsString("userId")
                    + ",充值方式编号=" + payWay.getId() + ",充值方式名称=" + payWay.getPayName()
                    + ",客户端来源=" + clientType);
            throw new ServiceException(ErrorCode_API.ERROR_PAY_NOPAYWAYCHANNEL,ErrorCode_API.ERROR_PAY_NOPAYWAYCHANNEL_MSG);
        }
        //判断充值渠道是否存在
        List<PayChannel> payChannelList = payChannelMapper.queryPayChannels(new BaseDto("id",payWayChannel.getPaychannelId()));//查询充值渠道
        if(payChannelList == null || payChannelList.size() == 0)
        {
            logger.error("[银联预订单]充值渠道不存在!充值渠道编号=" + payWayChannel.getPaychannelId()
                    + ",充值方式编号=" + payWayChannel.getPaywayId());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_NOPAYWAYCHANNEL,ErrorCode_API.ERROR_PAY_NOPAYWAYCHANNEL_MSG);
        }
        PayChannel payChannel = payChannelList.get(0);

        /**
         * 设置预下单参数
         */
        Dto payDto = new BaseDto();
        payDto.put("bankCode",params.get("bankCode"));//设置充值银行编号
        payDto.put("merchantNo",payWayChannel.getMerchantNo());//设置商户号
        payDto.put("appNo",payWayChannel.getAppNo());//设置应用编号/产品编号
        payDto.put("appName",payWayChannel.getAppName());//设置应用名称/app名称/商品描述
        payDto.put("apiUrl",payWayChannel.getApiUrl());//设置api地址
        payDto.put("notifyUrl",payWayChannel.getNotifyUrl());//设置支付结果通知地址
        payDto.put("returnUrl",payWayChannel.getReturnUrl());//设置页面通知地址
        payDto.put("signType",payWayChannel.getSignType());//设置签名方式
        payDto.put("secretKey",payWayChannel.getSecretKey());//设置签名密钥
        payDto.put("deviceInfo",payWayChannel.getDeviceInfo());//设置终端设备信息
        payDto.put("webAddress",payWayChannel.getWebAddress());//设置应用官网地址
        payDto.put("userIp",SysConfig.getString("API_HOST_IP"));//设置终端ip
        payDto.put("clientFrom",KeyConstants.loginUserMap.get(params.getAsString("appId")));//设置客户端来源
        payDto.put("mobilehost",SysConfig.getString("MOBILE_HOST"));//设置移动端域名

        //设置商户订单号
        String payId = "CZ" + DateUtil.formatDate(new Date(),DateUtil.LOG_DATE_TIME);//生成商户订单号
        payId += new Random().nextInt(10);
        payDto.put("payId",payId);//设置商户订单号

        //设置系统配置的单次充值最大金额及订单金额
        String maxSmoney = SysConfig.getString("MAX_RECHARGE_SMONEY");
        if(StringUtil.isNotEmpty(maxSmoney))
        {
            payDto.put("maxSmoney",smoney);//设置系统配置的单次充值最大金额
        }
        payDto.put("smoney",smoney);//设置订单金额

        /**
         * 根据充值渠道发送预下单请求
         */
        //迅游通
        if(PayConstants.PAYCHANNEL_CODE_XUNYOUTONG == payChannel.getChannelCode())
        {
            XunYouTongUtils.createUnionpayOrder(payDto);
        }
        else if(PayConstants.PAYCHANNEL_CODE_WLPAY == payChannel.getChannelCode())
        {
            WlPayUtils.createUnionWapPay(payDto);
        }

        /**
         * 处理下单结果
         */
        //下单成功,则保存订单信息
        if(1000 == payDto.getAsInteger("dcode"))
        {
            UserPay userPay = new UserPay();
            userPay.setPayCode(payWay.getPayCode());//设置充值方式业务编号
            userPay.setPayDesc(payWay.getPayDesc());//设置充值方式业务描述
            userPay.setChannelCode(payChannel.getChannelCode());//设置充值渠道编号
            userPay.setChannelDesc(payChannel.getChannelDesc());//设置充值渠道描述
            userPay.setUserId(params.getAsLong("userId"));//设置用户编号
            userPay.setPayType(PayConstants.PAY_TYPE_RECHARGE);//设置业务类型
            userPay.setMoney(smoney);//设置交易金额
            userPay.setOmoney(StringUtil.isEmpty(payDto.get("rsmoney"))? smoney : payDto.getAsDouble("rsmoney"));//设置订单原始金额
            userPay.setPayId(payId);//设置商户订单号
            userPay.setChannelPayId(payDto.getAsString("tradeNo"));//设置渠道流水号
            userPay.setStatus(PayConstants.PAYORDER_STATUS_CLZ);//设置处理状态为处理中(-1-处理失败 0-待处理 1-等待重新处理 2-处理中 3-处理成功)
            userPay.setClientFrom(KeyConstants.loginUserMap.get(params.getAsString("appId")));//设置客户端来源
            userPay.setRequestIp(params.getAsString("requestIp"));//设置请求支付的客户端ip地址
            userPay.setCreateTime(new Date());//设置请求支付时间
            int count = userPayMapper.insertUserPay(userPay);//保存订单信息
            if(count > 0)
            {
                //设置返回数据
                logger.info("[银联预订单]预下单成功!用户编号=" + params.getAsString("userId")
                        + ",订单金额=" + smoney
                        + ",订单号=" + payId);
                result.setErrorCode(ErrorCode_API.SUCCESS);
                Dto resultsDto = (Dto)payDto.get("results");//提取支付参数
                Dto data = new BaseDto("actions",("<script>location.href='" + resultsDto.getAsString("payInfo") + "';</script>"));
                result.setData(data);//设置返回数据
            }
            else
            {
                throw new ServiceException(ErrorCode.SERVER_ERROR);
            }
        }
        else
        {
            logger.info("[银联预订单]预下单失败!用户编号=" + params.getAsString("userId")
                    + ",订单金额=" + smoney
                    + ",dcode=" + payDto.getAsString("dcode")
                    + ",dmsg=" + payDto.getAsString("dmsg"));
            result.setErrorCode(ErrorCode.SERVER_ERROR);
            return;
        }
    }

    /**
     * 处理微信官方充值结果通知
     * @author  mcdog
     * @param   params     参数对象,通知处理结果(dcode-处理状态 dmsg-处理状态描述)也保存在该对象中,具体如下:
     *                     dcode=1000,处理成功
     *                     dcode=-1000,处理失败
     *                     dcode=130001,签名错误
     *                     dcode=130002,订单不存在
     *                     dcode=130003,交易金额不一致
     */
    public synchronized void doWeixinPayResult(Dto params) throws ServiceException,Exception
    {
        /**
         * 提取通知参数,根据相关参数值更新支付结果
         */
        String returnCode = params.getAsString("return_code");//提取通信状态码
        String resultCode = params.getAsString("result_code");//提取业务处理状态码
        if(WeixinUtils.success.equals(returnCode))
        {
            //获取订单信息
            String payId = params.getAsString("out_trade_no");//提取商户订单号
            List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",payId));
            if(userPayList == null || userPayList.size() == 0)
            {
                logger.error("[处理微信官方支付结果通知]查询不到相关的订单信息!订单号=" + payId);
                throw new ServiceException(ErrorCode_API.ERROR_PAY_ORDERNOTEXIST,ErrorCode_API.ERROR_PAY_ORDERNOTEXIST_MSG);
            }
            //获取充值渠道配置
            UserPay userPay = userPayList.get(0);
            Dto paywayChannelQueryDto = new BaseDto();
            paywayChannelQueryDto.put("payCode",userPay.getPayCode());
            paywayChannelQueryDto.put("channelCode",userPay.getChannelCode());
            List<PayWayChannel> payWayChannelList = payWayChannelMapper.queryPayWayChannels(paywayChannelQueryDto);
            if(payWayChannelList == null || payWayChannelList.size() == 0)
            {
                logger.error("[处理微信官方支付结果通知]查询不到相关的充值渠道配置!订单号=" + payId
                        + ",充值方式业务编号=" + userPay.getPayCode()
                        + ",充值渠道编号=" + userPay.getChannelCode());
                throw new ServiceException(ErrorCode.SERVER_ERROR);
            }
            //校验签名
            PayWayChannel payWayChannel = payWayChannelList.get(0);
            String respSign = params.getAsString("sign");
            params.remove("sign");
            params.put("secretKey",payWayChannel.getSecretKey());
            String realSign = "";
            if(PayUtils.signType_md5 == payWayChannel.getSignType())
            {
                realSign =WeixinUtils.getMd5Sign(params);
            }
            params.remove("secretKey");
            if(!respSign.equals(realSign))
            {
                logger.error("[处理微信官方支付结果通知]通知签名验证不通过!通知参数=" + params.toString());
                throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
            }
            //判断订单状态,如果已经处理过,则不再处理,直接响应成功
            if(userPay.getStatus() == -1 || userPay.getStatus() == 1)
            {
                logger.error("[处理微信官方充值结果通知]订单已被处理过,本次不再处理!订单号=" + userPay.getPayId());
                params.put("dcode",1000);
                params.put("dmsg","SUCCESS");
                return;
            }
            //初始化订单更新参数
            Dto updateDto = new BaseDto();
            updateDto.put("payId",payId);//设置商户订单号
            updateDto.put("cpayId",params.getAsString("transaction_id"));//设置渠道流水号
            updateDto.put("pbank",params.getAsString("bank_type"));//设置用户付款银行标识
            updateDto.put("pbankDesc", WeixinConstants.payTypeMaps.get(updateDto.getAsString("pbank")));//设置用户付款银行描述

            //判断业务处理状态,如果处理成功,则走更新用户余额及支付状态的逻辑
            params.put("dcode",-1000);
            params.put("dmsg","处理失败");
            if(WeixinUtils.success.equals(resultCode))
            {
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
                updateDto.put("smoney",params.getAsDoubleValue("total_fee") / 100);//设置订单交易金额
                userPayMapper.doRecharge(updateDto);//调用充值方法

                logger.info("[处理微信官方支付结果通知]订单号=" + payId + ",处理结果:dcode:" + updateDto.getAsString("dcode") + ",dmsg:" + updateDto.getAsString("dmsg"));

                //判断处理结果状态,根据状态设置相应的响应结果
                int dcode = updateDto.getAsInteger("dcode");//提取处理结果状态码
                if(dcode == 1000)
                {
                    //处理成功
                    params.put("dcode",1000);
                    params.put("dmsg","SUCCESS");
                    params.put("opmethod","微信官方支付结果通知");
                    params.put("activityType", "2");//设置活动类型为特定活动(0-彩票首页焦点图 1-咨询首页焦点图 2-特定活动 3-公告)
                    params.put("couponType","1");//设置优惠券赠送类型为充值送(0-注册送 1-充值送)
                    params.put("smoney",updateDto.get("smoney"));//设置金额
                    userService.sendCoupon(params,userPay.getUserId());//根据条件给用户赠送优惠券
                    return;
                }
                //订单不存在
                else if(dcode == 1001)
                {
                    params.put("dcode",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST);
                    params.put("dmsg",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST_MSG);
                    return;
                }
                //订单金额不一致
                else if(dcode == 1002)
                {
                    params.put("dcode",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY);
                    params.put("dmsg",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY_MSG);
                    return;
                }
            }
            //业务处理失败,则只将用户支付订单的状态改为支付失败
            else
            {
                //更新订单状态为处理失败
                updateDto.put("status",PayConstants.PAYORDER_STATUS_FAILURE);//设置订单状态为处理失败
                int count = userPayMapper.updateUserPay(updateDto);//更新订单状态
                if(count > 0)
                {
                    params.put("dcode",1000);
                    params.put("dmsg","SUCCESS");
                    return;
                }
            }
        }
        else
        {
            logger.error("[处理微信官方支付结果通知]通信失败!return_code=" + returnCode + ",return_msg=" + params.getAsString("return_msg"));
            throw new ServiceException(ErrorCode_API.SERVER_ERROR,"通信失败!return_code=" + returnCode);
        }
    }

    /**
     * 处理威富通-微信-充值结果通知
     * @author  mcdog
     * @param   params     参数对象,通知处理结果(dcode-处理状态 dmsg-处理状态描述)也保存在该对象中,具体如下:
     *                     dcode=1000,处理成功
     *                     dcode=-1000,处理失败
     *                     dcode=130001,签名错误
     *                     dcode=130002,订单不存在
     *                     dcode=130003,交易金额不一致
     */
    public synchronized void doSwiftpassWeixinPayResult(Dto params) throws ServiceException,Exception
    {
        /**
         * 提取通知参数,根据相关参数值更新支付结果
         */
        String status = params.getAsString("status");//提取通信状态码
        if("0".equals(status))
        {
            //获取订单信息
            String payId = params.getAsString("out_trade_no");//提取商户订单号
            List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",payId));
            if(userPayList == null || userPayList.size() == 0)
            {
                logger.error("[处理威富通-微信-充值结果通知]查询不到相关的订单信息!订单号=" + payId);
                throw new ServiceException(ErrorCode_API.ERROR_PAY_ORDERNOTEXIST,ErrorCode_API.ERROR_PAY_ORDERNOTEXIST_MSG);
            }
            //获取充值渠道配置
            UserPay userPay = userPayList.get(0);
            Dto paywayChannelQueryDto = new BaseDto();
            paywayChannelQueryDto.put("payCode",userPay.getPayCode());
            paywayChannelQueryDto.put("channelCode",userPay.getChannelCode());
            List<PayWayChannel> payWayChannelList = payWayChannelMapper.queryPayWayChannels(paywayChannelQueryDto);
            if(payWayChannelList == null || payWayChannelList.size() == 0)
            {
                logger.error("[处理威富通-微信-充值结果通知]查询不到相关的充值渠道配置!订单号=" + payId
                        + ",充值方式业务编号=" + userPay.getPayCode()
                        + ",充值渠道编号=" + userPay.getChannelCode());
                throw new ServiceException(ErrorCode.SERVER_ERROR);
            }
            //校验签名
            PayWayChannel payWayChannel = payWayChannelList.get(0);
            String respSign = params.getAsString("sign");
            params.remove("sign");
            params.put("secretKey",payWayChannel.getSecretKey());
            String realSign = SwiftpassUtils.getMd5Sign(params);
            params.remove("secretKey");
            if(!respSign.equals(realSign))
            {
                logger.error("[处理威富通-微信-充值结果通知]通知签名验证不通过!通知参数=" + params.toString());
                throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
            }
            //判断订单状态,如果已经处理过,则不再处理,直接响应成功
            if(userPay.getStatus() == -1 || userPay.getStatus() == 1)
            {
                logger.error("[处理威富通-微信-充值结果通知]订单已被处理过,本次不再处理!订单号=" + userPay.getPayId());
                params.put("dcode",1000);
                params.put("dmsg","success");
                return;
            }
            //初始化订单更新参数
            Dto updateDto = new BaseDto();
            updateDto.put("payId",payId);//设置商户订单号
            updateDto.put("cpayId",params.getAsString("transaction_id"));//设置渠道流水号
            updateDto.put("pbank",params.getAsString("bank_type"));//设置用户付款银行标识
            updateDto.put("pbankDesc", WeixinConstants.payTypeMaps.get(updateDto.getAsString("pbank")));//设置用户付款银行描述

            //判断业务处理状态,如果处理成功,则走更新用户余额及支付状态的逻辑
            params.put("dcode",-1000);
            params.put("dmsg","处理失败");
            String resultCode = params.getAsString("result_code");//提取业务处理状态码
            if("0".equals(resultCode) && "0".equals(params.getAsString("pay_result")))
            {
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
                updateDto.put("smoney",params.getAsDoubleValue("total_fee") / 100);//设置订单交易金额
                userPayMapper.doRecharge(updateDto);//调用充值方法

                logger.info("[处理威富通-微信-充值结果通知]payId=" + payId + ",处理结果:dcode:" + updateDto.getAsString("dcode") + ",dmsg:" + updateDto.getAsString("dmsg"));

                //判断处理结果状态,根据状态设置相应的响应结果
                int dcode = updateDto.getAsInteger("dcode");//提取处理结果状态码
                if(dcode == 1000)
                {
                    //处理成功
                    params.put("dcode",1000);
                    params.put("dmsg","success");
                    params.put("opmethod","威富通-微信-充值结果通知");
                    params.put("activityType", "2");//设置活动类型为特定活动(0-彩票首页焦点图 1-咨询首页焦点图 2-特定活动 3-公告)
                    params.put("couponType","1");//设置优惠券赠送类型为充值送(0-注册送 1-充值送)
                    params.put("smoney",updateDto.get("smoney"));//设置金额
                    userService.sendCoupon(params,userPay.getUserId());//根据条件给用户赠送优惠券
                    return;
                }
                //订单不存在
                else if(dcode == 1001)
                {
                    params.put("dcode",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST);
                    params.put("dmsg",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST_MSG);
                    return;
                }
                //订单金额不一致
                else if(dcode == 1002)
                {
                    params.put("dcode",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY);
                    params.put("dmsg",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY_MSG);
                    return;
                }
            }
            //业务处理失败,则只将用户支付订单的状态改为支付失败
            else
            {
                //更新订单状态为处理失败
                updateDto.put("status",PayConstants.PAYORDER_STATUS_FAILURE);//设置订单状态为处理失败
                int count = userPayMapper.updateUserPay(updateDto);//更新订单状态
                if(count > 0)
                {
                    params.put("dcode",1000);
                    params.put("dmsg","success");
                }
            }
        }
        else
        {
            logger.error("[处理威富通-微信-充值结果通知]通信失败!status:" + status + ",message:" + params.getAsString("message"));
            throw new ServiceException(ErrorCode_API.SERVER_ERROR,"通信失败!status=" + status);
        }
    }

    /**
     * 处理快接支付-充值结果通知
     * @author  mcdog
     * @param   params     参数对象,通知处理结果(dcode-处理状态 dmsg-处理状态描述)也保存在该对象中,具体如下:
     *                     dcode=1000,处理成功
     *                     dcode=-1000,处理失败
     *                     dcode=130001,签名错误
     *                     dcode=130002,订单不存在
     *                     dcode=130003,交易金额不一致
     */
    public synchronized void doKuaijiePayResultNotify(Dto params) throws ServiceException,Exception
    {
        /**
         * 提取通知参数,根据相关参数值更新支付结果
         */
        //获取订单信息
        String payId = params.getAsString("merchant_order_no");//提取商户订单号
        List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",payId));
        if(userPayList == null || userPayList.size() == 0)
        {
            logger.error("[处理快接支付-充值结果通知]查询不到相关的订单信息!订单号=" + payId);
            throw new ServiceException(ErrorCode_API.ERROR_PAY_ORDERNOTEXIST,ErrorCode_API.ERROR_PAY_NOTSUPPORT_MSG);
        }
        //获取充值渠道配置
        UserPay userPay = userPayList.get(0);
        Dto paywayChannelQueryDto = new BaseDto();
        paywayChannelQueryDto.put("payCode",userPay.getPayCode());
        paywayChannelQueryDto.put("channelCode",userPay.getChannelCode());
        List<PayWayChannel> payWayChannelList = payWayChannelMapper.queryPayWayChannels(paywayChannelQueryDto);
        if(payWayChannelList == null || payWayChannelList.size() == 0)
        {
            logger.error("[处理快接支付-充值结果通知]查询不到相关的充值渠道配置!订单号=" + payId
                    + ",充值方式业务编号=" + userPay.getPayCode()
                    + ",充值渠道编号=" + userPay.getChannelCode());
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
        //校验签名
        PayWayChannel payWayChannel = payWayChannelList.get(0);
        String respSign = params.getAsString("sign");
        params.remove("sign");
        String realSign = "";
        if(PayUtils.signType_md5 == payWayChannel.getSignType())
        {
            realSign = MD5.md5(URLDecoder.decode(SortUtils.getOrderByAsciiAscFromDto(params,false),"UTF-8") + "&key=" + payWayChannel.getSecretKey());
        }
        if(!respSign.equals(realSign))
        {
            logger.error("[处理快接支付-充值结果通知]通知签名验证不通过!通知参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //判断订单状态,如果已经处理过,则不再处理,直接响应成功
        if(userPay.getStatus() == -1 || userPay.getStatus() == 1)
        {
            logger.error("[处理快接支付-充值结果通知]订单已被处理过,本次不再处理!订单号=" + userPay.getPayId());
            params.put("dcode",1000);
            params.put("dmsg","success");
            return;
        }
        //初始化订单更新参数
        params.put("dcode",-1000);
        params.put("dmsg","处理失败");
        Dto updateDto = new BaseDto();
        updateDto.put("payId",payId);//设置商户订单号
        updateDto.put("cpayId",params.getAsString("trade_no"));//设置渠道流水号
        updateDto.put("pbank",params.getAsString("pay_channel"));//设置用户付款银行标识
        updateDto.put("pbankDesc",updateDto.getAsString("pay_channel_name"));//设置用户付款银行描述

        //判断业务处理状态,如果处理成功,则走更新用户余额及支付状态的逻辑
        String status = params.getAsString("status");//提取订单状态码
        if("Success".equals(status))
        {
            updateDto.put("ptime",DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//设置订单处理完成时间
            updateDto.put("smoney",params.getAsDoubleValue("amount"));//设置订单交易金额
            userPayMapper.doRecharge(updateDto);//调用充值方法
            logger.info("[处理快接支付-充值结果通知][充值成功]payId=" + payId + ",处理结果:dcode=" + updateDto.getAsString("dcode") + ",dmsg=" + updateDto.getAsString("dmsg"));

            //判断处理结果状态,根据状态设置相应的响应结果
            int dcode = updateDto.getAsInteger("dcode");//提取处理结果状态码
            if(dcode == 1000)
            {
                //处理成功
                params.put("dcode",1000);
                params.put("dmsg","success");
                params.put("opmethod","快接支付-充值结果通知");
                params.put("activityType", "2");//设置活动类型为特定活动(0-彩票首页焦点图 1-咨询首页焦点图 2-特定活动 3-公告)
                params.put("couponType","1");//设置优惠券赠送类型为充值送(0-注册送 1-充值送)
                params.put("smoney",updateDto.get("smoney"));//设置金额
                userService.sendCoupon(params,userPay.getUserId());//根据条件给用户赠送优惠券
                return;
            }
            //订单不存在
            else if(dcode == 1001)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST_MSG);
                return;
            }
            //订单金额不一致
            else if(dcode == 1002)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY_MSG);
                return;
            }
        }
        else if("Fail".equals(status))
        {
            //更新订单状态为处理失败
            logger.error("[处理快接支付-充值结果通知][充值失败]订单号=" + payId + ",status=" + status + ",msg=" + params.getAsString("msg"));
            updateDto.put("status",PayConstants.PAYORDER_STATUS_FAILURE);//设置订单状态为处理失败
            int count = userPayMapper.updateUserPay(updateDto);//更新订单状态
            if(count > 0)
            {
                logger.error("[处理快接支付-充值结果通知][充值失败]订单状态更新成功!订单号=" + payId);
                params.put("dcode",1000);
                params.put("dmsg","success");
            }
        }
    }

    /**
     * 处理贝付宝-微信-充值结果通知
     * @author  mcdog
     * @param   params     参数对象,通知处理结果(dcode-处理状态 dmsg-处理状态描述)也保存在该对象中,具体如下:
     *                     dcode=1000,处理成功
     *                     dcode=-1000,处理失败
     *                     dcode=130001,签名错误
     *                     dcode=130002,订单不存在
     *                     dcode=130003,交易金额不一致
     */
    public synchronized void doPayfubaoWeixinPayResultNotify(Dto params) throws ServiceException,Exception
    {
        /**
         * 提取通知参数,根据相关参数值更新支付结果
         */
        //获取订单信息
        String payId = params.getAsString("orderno");//提取商户订单号
        List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",payId));
        if(userPayList == null || userPayList.size() == 0)
        {
            logger.error("[处理贝付宝-微信-充值结果通知]查询不到相关的订单信息!订单号=" + payId);
            throw new ServiceException(ErrorCode_API.ERROR_PAY_ORDERNOTEXIST,ErrorCode_API.ERROR_PAY_NOTSUPPORT_MSG);
        }
        //获取充值渠道配置
        UserPay userPay = userPayList.get(0);
        Dto paywayChannelQueryDto = new BaseDto();
        paywayChannelQueryDto.put("payCode",userPay.getPayCode());
        paywayChannelQueryDto.put("channelCode",userPay.getChannelCode());
        List<PayWayChannel> payWayChannelList = payWayChannelMapper.queryPayWayChannels(paywayChannelQueryDto);
        if(payWayChannelList == null || payWayChannelList.size() == 0)
        {
            logger.error("[处理贝付宝-微信-充值结果通知]查询不到相关的充值渠道配置!订单号=" + payId
                    + ",充值方式业务编号=" + userPay.getPayCode()
                    + ",充值渠道编号=" + userPay.getChannelCode());
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
        //校验签名
        PayWayChannel payWayChannel = payWayChannelList.get(0);
        String respSign = params.getAsString("sign");
        String realSign = params.getAsString("orderno") + params.getAsString("fee") + payWayChannel.getSecretKey();
        realSign = MD5.md5(realSign).toLowerCase();
        if(!respSign.equals(realSign))
        {
            logger.error("[处理贝付宝-微信-充值结果通知]通知签名验证不通过!通知参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //判断订单状态,如果已经处理过,则不再处理,直接响应成功
        if(userPay.getStatus() == -1 || userPay.getStatus() == 1)
        {
            logger.error("[处理贝付宝-微信-充值结果通知]订单已被处理过,本次不再处理!订单号=" + userPay.getPayId());
            params.put("dcode",1000);
            params.put("dmsg","success");
            return;
        }

        /**
         * 更新用户余额及订单支付状态
         */
        //初始化订单更新参数
        params.put("dcode",-1000);
        params.put("dmsg","处理失败");
        Dto updateDto = new BaseDto();
        updateDto.put("payId",payId);//设置商户订单号
        updateDto.put("cpayId",userPay.getChannelPayId());//设置渠道流水号

        updateDto.put("ptime",DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//设置订单处理完成时间
        updateDto.put("smoney",userPay.getMoney());//设置订单交易金额
        userPayMapper.doRecharge(updateDto);//调用充值方法
        logger.info("[处理贝付宝-微信-充值结果通知][充值成功]payId=" + payId + ",处理结果:dcode=" + updateDto.getAsString("dcode") + ",dmsg=" + updateDto.getAsString("dmsg"));

        //判断处理结果状态,根据状态设置相应的响应结果
        int dcode = updateDto.getAsInteger("dcode");//提取处理结果状态码
        if(dcode == 1000)
        {
            //处理成功
            params.put("dcode",1000);
            params.put("dmsg","success");
            params.put("opmethod","贝付宝-微信-充值结果通知");
            params.put("activityType", "2");//设置活动类型为特定活动(0-彩票首页焦点图 1-咨询首页焦点图 2-特定活动 3-公告)
            params.put("couponType","1");//设置优惠券赠送类型为充值送(0-注册送 1-充值送)
            params.put("smoney",updateDto.get("smoney"));//设置金额
            userService.sendCoupon(params,userPay.getUserId());//根据条件给用户赠送优惠券
            return;
        }
        //订单不存在
        else if(dcode == 1001)
        {
            params.put("dcode",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST);
            params.put("dmsg",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST_MSG);
            return;
        }
        //订单金额不一致
        else if(dcode == 1002)
        {
            params.put("dcode",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY);
            params.put("dmsg",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY_MSG);
            return;
        }
    }

    /**
     * 处理贝付宝-支付宝-充值结果通知
     * @author  mcdog
     * @param   params     参数对象,通知处理结果(dcode-处理状态 dmsg-处理状态描述)也保存在该对象中,具体如下:
     *                     dcode=1000,处理成功
     *                     dcode=-1000,处理失败
     *                     dcode=130001,签名错误
     *                     dcode=130002,订单不存在
     *                     dcode=130003,交易金额不一致
     */
    public synchronized void doPayfubaoAlipayPayResultNotify(Dto params) throws ServiceException,Exception
    {
        /**
         * 提取通知参数,根据相关参数值更新支付结果
         */
        //获取订单信息
        String payId = params.getAsString("orderno");//提取商户订单号
        List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",payId));
        if(userPayList == null || userPayList.size() == 0)
        {
            logger.error("[处理贝付宝-支付宝-充值结果通知]查询不到相关的订单信息!订单号=" + payId);
            throw new ServiceException(ErrorCode_API.ERROR_PAY_ORDERNOTEXIST,ErrorCode_API.ERROR_PAY_NOTSUPPORT_MSG);
        }
        //获取充值渠道配置
        UserPay userPay = userPayList.get(0);
        Dto paywayChannelQueryDto = new BaseDto();
        paywayChannelQueryDto.put("payCode",userPay.getPayCode());
        paywayChannelQueryDto.put("channelCode",userPay.getChannelCode());
        List<PayWayChannel> payWayChannelList = payWayChannelMapper.queryPayWayChannels(paywayChannelQueryDto);
        if(payWayChannelList == null || payWayChannelList.size() == 0)
        {
            logger.error("[处理贝付宝-支付宝-充值结果通知]查询不到相关的充值渠道配置!订单号=" + payId
                    + ",充值方式业务编号=" + userPay.getPayCode()
                    + ",充值渠道编号=" + userPay.getChannelCode());
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
        //校验签名
        PayWayChannel payWayChannel = payWayChannelList.get(0);
        String respSign = params.getAsString("sign");
        String realSign = params.getAsString("orderno") + params.getAsString("fee") + payWayChannel.getSecretKey();
        realSign = MD5.md5(realSign).toLowerCase();
        if(!respSign.equals(realSign))
        {
            logger.error("[处理贝付宝-支付宝-充值结果通知]通知签名验证不通过!通知参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //判断订单状态,如果已经处理过,则不再处理,直接响应成功
        if(userPay.getStatus() == -1 || userPay.getStatus() == 1)
        {
            logger.error("[处理贝付宝-支付宝-充值结果通知]订单已被处理过,本次不再处理!订单号=" + userPay.getPayId());
            params.put("dcode",1000);
            params.put("dmsg","success");
            return;
        }
        //初始化订单更新参数
        params.put("dcode",-1000);
        params.put("dmsg","处理失败");
        Dto updateDto = new BaseDto();
        updateDto.put("payId",payId);//设置商户订单号
        updateDto.put("cpayId",userPay.getChannelPayId());//设置渠道流水号

        //判断业务处理状态,如果处理成功,则走更新用户余额及支付状态的逻辑
        updateDto.put("ptime",DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//设置订单处理完成时间
        updateDto.put("smoney",userPay.getMoney());//设置订单交易金额
        userPayMapper.doRecharge(updateDto);//调用充值方法
        logger.info("[处理贝付宝-支付宝-充值结果通知][充值成功]payId=" + payId + ",处理结果:dcode=" + updateDto.getAsString("dcode") + ",dmsg=" + updateDto.getAsString("dmsg"));

        //判断处理结果状态,根据状态设置相应的响应结果
        int dcode = updateDto.getAsInteger("dcode");//提取处理结果状态码
        if(dcode == 1000)
        {
            //处理成功
            params.put("dcode",1000);
            params.put("dmsg","success");
            params.put("opmethod","贝付宝-支付宝-充值结果通知");
            params.put("activityType", "2");//设置活动类型为特定活动(0-彩票首页焦点图 1-咨询首页焦点图 2-特定活动 3-公告)
            params.put("couponType","1");//设置优惠券赠送类型为充值送(0-注册送 1-充值送)
            params.put("smoney",updateDto.get("smoney"));//设置金额
            userService.sendCoupon(params,userPay.getUserId());//根据条件给用户赠送优惠券
            return;
        }
        //订单不存在
        else if(dcode == 1001)
        {
            params.put("dcode",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST);
            params.put("dmsg",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST_MSG);
            return;
        }
        //订单金额不一致
        else if(dcode == 1002)
        {
            params.put("dcode",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY);
            params.put("dmsg",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY_MSG);
            return;
        }
    }

    /**
     * 处理迅游通-QQ钱包-充值结果通知
     * @author  mcdog
     * @param   params     参数对象,通知处理结果(dcode-处理状态 dmsg-处理状态描述)也保存在该对象中,具体如下:
     *                     dcode=1000,处理成功
     *                     dcode=-1000,处理失败
     *                     dcode=130001,签名错误
     *                     dcode=130002,订单不存在
     *                     dcode=130003,交易金额不一致
     */
    public synchronized void doXunyoutongQqWalletPayResultNotify(Dto params) throws ServiceException,Exception
    {
        /**
         * 提取通知参数,校验订单信息
         */
        //获取订单信息
        String payId = params.getAsString("outTradeNo");//提取商户订单号
        List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",payId));
        if(userPayList == null || userPayList.size() == 0)
        {
            logger.error("[处理迅游通-QQ钱包-充值结果通知]查询不到相关的订单信息!订单号=" + payId);
            throw new ServiceException(ErrorCode_API.ERROR_PAY_ORDERNOTEXIST,ErrorCode_API.ERROR_PAY_NOTSUPPORT_MSG);
        }
        //获取充值渠道配置
        UserPay userPay = userPayList.get(0);
        Dto paywayChannelQueryDto = new BaseDto();
        paywayChannelQueryDto.put("payCode",userPay.getPayCode());
        paywayChannelQueryDto.put("channelCode",userPay.getChannelCode());
        List<PayWayChannel> payWayChannelList = payWayChannelMapper.queryPayWayChannels(paywayChannelQueryDto);
        if(payWayChannelList == null || payWayChannelList.size() == 0)
        {
            logger.error("[处理迅游通-QQ钱包-充值结果通知]查询不到相关的充值渠道配置!订单号=" + payId
                    + ",充值方式业务编号=" + userPay.getPayCode()
                    + ",充值渠道编号=" + userPay.getChannelCode());
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
        //校验签名
        PayWayChannel payWayChannel = payWayChannelList.get(0);
        String respSign = params.getAsString("sign");
        params.remove("sign");
        String realSign = SortUtils.getOrderByAsciiAscFromDto(params,false) + "&paySecret=" + payWayChannel.getSecretKey();
        realSign = MD5.md5(realSign).toUpperCase();
        if(!respSign.equals(realSign))
        {
            logger.error("[处理迅游通-QQ钱包-充值结果通知]通知签名验证不通过!通知参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //校验产品类型
        String productType = params.getAsString("productType");//提取产品类型
        if(StringUtil.isEmpty(productType) || !"70000203".equals(productType))
        {
            logger.error("[处理迅游通-QQ钱包-充值结果通知]产品类型验证不通过!通知参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //判断订单状态,如果已经处理过,则不再处理,直接响应成功
        if(userPay.getStatus() == -1 || userPay.getStatus() == 1)
        {
            logger.error("[处理迅游通-QQ钱包-充值结果通知]订单已被处理过,本次不再处理!订单号=" + userPay.getPayId());
            params.put("dcode",1000);
            params.put("dmsg","SUCCESS");
            return;
        }

        /**
         * 根据充值结果更新订单及账户信息
         */
        //初始化订单更新参数
        params.put("dcode",-1000);
        params.put("dmsg","处理失败");
        Dto updateDto = new BaseDto();
        updateDto.put("payId",payId);//设置商户订单号
        updateDto.put("cpayId",params.getAsString("trxNo"));//设置渠道流水号

        //判断业务处理状态,如果处理成功,则走更新用户余额及支付状态的逻辑
        String status = params.getAsString("tradeStatus");//提取订单状态码
        if("SUCCESS".equals(status))
        {
            updateDto.put("ptime",DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//设置订单处理完成时间
            updateDto.put("smoney",params.getAsDoubleValue("orderPrice"));//设置订单交易金额
            userPayMapper.doRecharge(updateDto);//调用充值方法
            logger.info("[处理迅游通-QQ钱包-充值结果通知][充值成功]payId=" + payId + ",处理结果:dcode=" + updateDto.getAsString("dcode") + ",dmsg=" + updateDto.getAsString("dmsg"));

            //判断处理结果状态,根据状态设置相应的响应结果
            int dcode = updateDto.getAsInteger("dcode");//提取处理结果状态码
            if(dcode == 1000)
            {
                //处理成功
                params.put("dcode",1000);
                params.put("dmsg","success");
                params.put("opmethod","处理迅游通-QQ钱包-充值结果通知");
                params.put("activityType", "2");//设置活动类型为特定活动(0-彩票首页焦点图 1-咨询首页焦点图 2-特定活动 3-公告)
                params.put("couponType","1");//设置优惠券赠送类型为充值送(0-注册送 1-充值送)
                params.put("smoney",updateDto.get("smoney"));//设置金额
                userService.sendCoupon(params,userPay.getUserId());//根据条件给用户赠送优惠券
                return;
            }
            //订单不存在
            else if(dcode == 1001)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST_MSG);
                return;
            }
            //订单金额不一致
            else if(dcode == 1002)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY_MSG);
                return;
            }
        }
        //等待支付,不做任何处理,并响应FAIL
        else if("WAITING_PAYMENT".equals(status))
        {
            logger.info("[处理迅游通-QQ钱包-充值结果通知][等待支付]本次通知不对订单做任何处理!订单号=" + payId);
            params.put("dcode",-1000);
            params.put("dmsg","fail");
            return;
        }
        else
        {
            //更新订单状态为处理失败
            logger.error("[处理迅游通-QQ钱包-充值结果通知][充值失败]订单号=" + payId + ",status=" + status);
            updateDto.put("status",PayConstants.PAYORDER_STATUS_FAILURE);//设置订单状态为处理失败
            int count = userPayMapper.updateUserPay(updateDto);//更新订单状态
            if(count > 0)
            {
                logger.error("[处理快接支付-QQ钱包-充值结果通知][充值失败]订单状态更新成功!订单号=" + payId);
                params.put("dcode",1000);
                params.put("dmsg","success");
            }
        }
    }

    /**
     * 处理迅游通-京东钱包-充值结果通知
     * @author  mcdog
     * @param   params     参数对象,通知处理结果(dcode-处理状态 dmsg-处理状态描述)也保存在该对象中,具体如下:
     *                     dcode=1000,处理成功
     *                     dcode=-1000,处理失败
     *                     dcode=130001,签名错误
     *                     dcode=130002,订单不存在
     *                     dcode=130003,交易金额不一致
     */
    public synchronized void doXunyoutongJdWalletPayResultNotify(Dto params) throws ServiceException,Exception
    {
        /**
         * 提取通知参数,校验订单信息
         */
        //获取订单信息
        String payId = params.getAsString("outTradeNo");//提取商户订单号
        List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",payId));
        if(userPayList == null || userPayList.size() == 0)
        {
            logger.error("[处理迅游通-京东钱包-充值结果通知]查询不到相关的订单信息!订单号=" + payId);
            throw new ServiceException(ErrorCode_API.ERROR_PAY_ORDERNOTEXIST,ErrorCode_API.ERROR_PAY_NOTSUPPORT_MSG);
        }
        //获取充值渠道配置
        UserPay userPay = userPayList.get(0);
        Dto paywayChannelQueryDto = new BaseDto();
        paywayChannelQueryDto.put("payCode",userPay.getPayCode());
        paywayChannelQueryDto.put("channelCode",userPay.getChannelCode());
        List<PayWayChannel> payWayChannelList = payWayChannelMapper.queryPayWayChannels(paywayChannelQueryDto);
        if(payWayChannelList == null || payWayChannelList.size() == 0)
        {
            logger.error("[处理迅游通-京东钱包-充值结果通知]查询不到相关的充值渠道配置!订单号=" + payId
                    + ",充值方式业务编号=" + userPay.getPayCode()
                    + ",充值渠道编号=" + userPay.getChannelCode());
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
        //校验签名
        PayWayChannel payWayChannel = payWayChannelList.get(0);
        String respSign = params.getAsString("sign");
        params.remove("sign");
        String realSign = SortUtils.getOrderByAsciiAscFromDto(params,false) + "&paySecret=" + payWayChannel.getSecretKey();
        realSign = MD5.md5(realSign).toUpperCase();
        if(!respSign.equals(realSign))
        {
            logger.error("[处理迅游通-京东钱包-充值结果通知通知签名验证不通过!通知参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //校验产品类型
        String productType = params.getAsString("productType");//提取产品类型
        if(StringUtil.isEmpty(productType) || !"80000203".equals(productType))
        {
            logger.error("[处理迅游通-京东钱包-充值结果通知]产品类型验证不通过!通知参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //判断订单状态,如果已经处理过,则不再处理,直接响应成功
        if(userPay.getStatus() == -1 || userPay.getStatus() == 1)
        {
            logger.error("[处理迅游通-京东钱包-充值结果通知]订单已被处理过,本次不再处理!订单号=" + userPay.getPayId());
            params.put("dcode",1000);
            params.put("dmsg","SUCCESS");
            return;
        }

        /**
         * 根据充值结果更新订单及账户信息
         */
        //初始化订单更新参数
        params.put("dcode",-1000);
        params.put("dmsg","处理失败");
        Dto updateDto = new BaseDto();
        updateDto.put("payId",payId);//设置商户订单号
        updateDto.put("cpayId",params.getAsString("trxNo"));//设置渠道流水号

        //判断业务处理状态,如果处理成功,则走更新用户余额及支付状态的逻辑
        String status = params.getAsString("tradeStatus");//提取订单状态码
        if("SUCCESS".equals(status))
        {
            updateDto.put("ptime",DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//设置订单处理完成时间
            updateDto.put("smoney",params.getAsDoubleValue("orderPrice"));//设置订单交易金额
            userPayMapper.doRecharge(updateDto);//调用充值方法
            logger.info("[处理迅游通-京东钱包-充值结果通知][充值成功]payId=" + payId + ",处理结果:dcode=" + updateDto.getAsString("dcode") + ",dmsg=" + updateDto.getAsString("dmsg"));

            //判断处理结果状态,根据状态设置相应的响应结果
            int dcode = updateDto.getAsInteger("dcode");//提取处理结果状态码
            if(dcode == 1000)
            {
                //处理成功
                params.put("dcode",1000);
                params.put("dmsg","success");
                params.put("opmethod","处理迅游通-京东钱包-充值结果通知");
                params.put("activityType", "2");//设置活动类型为特定活动(0-彩票首页焦点图 1-咨询首页焦点图 2-特定活动 3-公告)
                params.put("couponType","1");//设置优惠券赠送类型为充值送(0-注册送 1-充值送)
                params.put("smoney",updateDto.get("smoney"));//设置金额
                userService.sendCoupon(params,userPay.getUserId());//根据条件给用户赠送优惠券
                return;
            }
            //订单不存在
            else if(dcode == 1001)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST_MSG);
                return;
            }
            //订单金额不一致
            else if(dcode == 1002)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY_MSG);
                return;
            }
        }
        //等待支付,不做任何处理,并响应FAIL
        else if("WAITING_PAYMENT".equals(status))
        {
            logger.info("[处理迅游通-京东钱包-充值结果通知][等待支付]本次通知不对订单做任何处理!订单号=" + payId);
            params.put("dcode",-1000);
            params.put("dmsg","fail");
            return;
        }
        else
        {
            //更新订单状态为处理失败
            logger.error("[处理迅游通-京东钱包-充值结果通知][充值失败]订单号=" + payId + ",status=" + status);
            updateDto.put("status",PayConstants.PAYORDER_STATUS_FAILURE);//设置订单状态为处理失败
            int count = userPayMapper.updateUserPay(updateDto);//更新订单状态
            if(count > 0)
            {
                logger.error("[处理迅游通-京东钱包-充值结果通知][充值失败]订单状态更新成功!订单号=" + payId);
                params.put("dcode",1000);
                params.put("dmsg","success");
            }
        }
    }

    /**
     * 处理盛付通付款结果通知
     * @author  mcdog
     * @param   params     参数对象,通知处理结果(dcode-处理状态 dmsg-处理状态描述)也保存在该对象中,具体如下:
     *                     dcode=1000,处理成功
     *                     dcode=-1000,处理失败
     *                     dcode=130001,签名错误
     *                     dcode=130002,订单不存在
     */
    public synchronized void doShengpayPaymentResult(Dto params) throws ServiceException,Exception
    {
        //校验签名
        String respSign = params.getAsString("sign");
        params.remove("sign");
        String realSign = "charset=" + params.getAsString("charset");
        realSign += "batchNo=" + params.getAsString("batchNo");
        realSign += "batchPayStatus=" + params.getAsString("batchPayStatus");
        realSign += "batchPayStatusMsg=" + params.getAsString("batchPayStatusMsg");
        realSign += "resultMemo=" + params.getAsString("resultMemo");
        realSign = realSign + ShengpayUtils.md5Key;
        realSign = MD5.md5(realSign).toUpperCase();//生成md5签名
        if(!respSign.equals(realSign))
        {
            logger.error("[盛付通付款结果通知]通知签名验证不通过!通知参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //获取订单信息
        List<Map<String,Object>> detailList = params.getAsList("details");
        if(detailList == null || detailList.size() == 0)
        {
            logger.error("[盛付通付款结果通知]无任何订单信息!通知参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //循环订单信息,根据订单的付款状态更新订单状态和用户账户信息
        for(Map<String,Object> detailMap : detailList)
        {
            String payId = detailMap.get("id").toString();//提取商户订单号(平台流水号)
            List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",payId));
            if(userPayList == null || userPayList.size() == 0)
            {
                logger.error("[盛付通付款结果通知]查询不到相关的订单信息!订单号=" + payId);
                continue;
            }
            //判断订单状态,如果已经处理过,则不再处理,直接响应成功
            UserPay userPay = userPayList.get(0);
            if(userPay.getStatus() == PayConstants.PAYORDER_STATUS_FAILURE
                    || userPay.getStatus() == PayConstants.PAYORDER_STATUS_SUCCESS)
            {
                logger.error("[盛付通付款结果通知]订单已经处理过,不再重复处理!订单号=" + payId);
                continue;
            }
            //判断订单金额是否一致
            if(Double.parseDouble(detailMap.get("amount").toString()) != userPay.getMoney())
            {
                logger.error("[盛付通付款结果通知]订单金额不一致!已终止对该订单的处理!订单号=" + payId);
                continue;
            }
            //初始化订单更新参数
            Dto updateDto = new BaseDto();
            updateDto.put("payId",payId);//设置商户订单号(平台流水号)
            updateDto.put("channelPayId",detailMap.get("orderNo").toString());//设置盛付通订单号

            //判断订单的状态
            String payStatusCode = detailMap.get("payStatusCode").toString();
            if(ShengpayUtils.payStatusCode_fkcg.equals(payStatusCode))
            {
                //付款成功,则更新订单状态为处理成功
                updateDto.put("status",PayConstants.PAYORDER_STATUS_SUCCESS);//设置订单状态为处理成功
                updateDto.put("remark","处理成功");
                int count = userPayMapper.updateUserPay(updateDto);//更新订单状态
                logger.info("[盛付通付款结果通知][付款成功]订单状态更新" + (count > 0? "成功" : "失败") + ",订单号=" + payId + ",订单所属用户编号=" + userPay.getUserId());
                if(count > 0)
                {
                    //更新用户账户信息
                    updateDto = new BaseDto("userId",userPay.getUserId());
                    updateDto.put("offsetFrozen",-userPay.getMoney());//冻结金额减去订单的金额
                    count = userAccountMapper.updateUserAccount(updateDto);//更新用户账户信息
                    logger.info("[盛付通付款结果通知][付款成功]用户账户更新" + (count > 0? "成功" : "失败") + ",订单号=" + payId + ",订单所属用户编号=" + userPay.getUserId());

                    //更新账户流水状态为有效(已完成)
                    if(count > 0)
                    {
                        updateDto = new BaseDto();
                        updateDto.put("userId",userPay.getUserId());//设置用户编号
                        updateDto.put("businessId",payId);//设置流水关联的订单号
                        updateDto.put("status",1);//设置流水状态为有效(已完成)
                        count = userDetailMapper.updateUserDetailOfTx(updateDto);
                        logger.info("[盛付通付款结果通知][付款成功]用户账户流水更新" + (count > 0? "成功" : "失败") + ",订单号=" + payId + ",订单所属用户编号=" + userPay.getUserId());
                    }
                }
            }
            else if(ShengpayUtils.payStatusCode_fksb.equals(payStatusCode))
            {
                //付款失败,则更新订单状态为处理失败,并更新用户账户信息
                updateDto.put("status",PayConstants.PAYORDER_STATUS_FAILURE);//设置订单状态为处理失败
                updateDto.put("remark",detailMap.get("payStatus"));
                int count = userPayMapper.updateUserPay(updateDto);//更新订单状态
                logger.info("[盛付通付款结果通知][付款失败]订单状态更新" + (count > 0? "成功" : "失败") + ",订单号=" + payId + ",订单所属用户编号=" + userPay.getUserId());
                if(count > 0)
                {
                    //更新账户流水状态为无效
                    updateDto = new BaseDto();
                    updateDto.put("userId",userPay.getUserId());//设置用户编号
                    updateDto.put("businessId",payId);//设置流水关联的订单号
                    updateDto.put("status",-1);//设置流水状态为无效
                    count = userDetailMapper.updateUserDetailOfTx(updateDto);
                    logger.info("[盛付通付款结果通知][付款失败]用户账户流水更新" + (count > 0? "成功" : "失败")
                            + ",订单号=" + payId
                            + ",订单所属用户编号=" + userPay.getUserId());

                    //更新用户账户信息
                    updateDto = new BaseDto("userId",userPay.getUserId());
                    updateDto.put("tbalance",userPay.getMoney());//余额加上订单的金额
                    updateDto.put("twithDraw",-userPay.getMoney());//累计提现金额减去订单金额
                    updateDto.put("offsetFrozen",-userPay.getMoney());//冻结金额减去订单的金额
                    updateDto.put("offsetWithDraw",userPay.getMoney());//可提现金额加上订单金额
                    count = userAccountMapper.updateUserAccount(updateDto);//更新用户账户信息
                    logger.info("[盛付通付款结果通知][付款失败]用户账户更新" + (count > 0? "成功" : "失败")
                            + ",订单号=" + payId
                            + ",订单所属用户编号=" + userPay.getUserId());
                }
            }
            else
            {
                logger.info("[盛付通付款结果通知]订单不做任何处理,接收到的订单状态码=" + payStatusCode
                        + ",订单号=" + payId
                        + ",订单所属用户编号=" + userPay.getUserId());
            }
        }
        params.put("dcode",1000);
        params.put("dmsg","ok");
        logger.info("[盛付通付款结果通知]批量付款结果通知全部处理完毕");
    }

    /**
     * 处理聚合支付-代付结果通知
     * @author  mcdog
     * @param   params     参数对象,通知处理结果(dcode-处理状态 dmsg-处理状态描述)也保存在该对象中,具体如下:
     *                     dcode=1000,处理成功
     *                     dcode=-1000,处理失败
     *                     dcode=130001,签名错误
     *                     dcode=130002,订单不存在
     */
    public synchronized void doJuheDpayResult(Dto params) throws ServiceException,Exception
    {
        //校验签名
        String respSign = params.getAsString("signature");
        params.remove("signature");
        String realSign = SortUtils.getOrderByAsciiAscFromDto(params,false);
        realSign += "&key=" + JuHe10381Utils.dfSecretKey;
        realSign = MD5.md5(realSign).toUpperCase();
        if(!respSign.equals(realSign))
        {
            logger.error("[处理聚合支付-代付结果通知]通知签名验证不通过!通知参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //获取订单信息
        String cpayId = params.get("mem_order").toString();//提取商户订单号(平台流水号)
        List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",cpayId));
        if(userPayList == null || userPayList.size() == 0)
        {
            logger.error("[处理聚合支付-代付结果通知]查询不到相关的订单信息!渠道流水号=" + cpayId);
            return;
        }
        //判断订单状态,如果已经处理过,则不再处理,直接响应成功
        UserPay userPay = userPayList.get(0);
        String payId = userPay.getPayId();
        if(userPay.getStatus() == PayConstants.PAYORDER_STATUS_FAILURE
                || userPay.getStatus() == PayConstants.PAYORDER_STATUS_SUCCESS)
        {
            logger.error("[处理聚合支付-代付结果通知]订单已经处理过,不再重复处理!订单号=" + payId);
            return;
        }
        //判断订单金额是否一致
        if(params.getAsDoubleValue("amount") / 100 != userPay.getMoney().doubleValue())
        {
            logger.error("[处理聚合支付-代付结果通知]订单金额不一致!已终止对该订单的处理!订单号=" + payId);
            return;
        }
        //初始化订单更新参数
        Dto updateDto = new BaseDto();
        updateDto.put("payId",payId);//设置商户订单号(平台流水号)
        updateDto.put("channelPayId", params.get("order").toString());//设置聚合支付订单号

        //判断订单的状态
        String code = params.getAsString("code");
        if("11".equals(code))
        {
            //付款成功,则更新订单状态为处理成功
            updateDto.put("status",PayConstants.PAYORDER_STATUS_SUCCESS);//设置订单状态为处理成功
            updateDto.put("remark","处理成功");
            int count = userPayMapper.updateUserPay(updateDto);//更新订单状态
            logger.info("[处理聚合支付-代付结果通知][付款成功]订单状态更新" + (count > 0? "成功" : "失败") + ",订单号=" + payId + ",订单所属用户编号=" + userPay.getUserId());
            if(count > 0)
            {
                //更新用户账户信息
                updateDto = new BaseDto("userId",userPay.getUserId());
                updateDto.put("offsetFrozen",-userPay.getMoney());//冻结金额减去订单的金额
                count = userAccountMapper.updateUserAccount(updateDto);//更新用户账户信息
                logger.info("[处理聚合支付-代付结果通知][付款成功]用户账户更新" + (count > 0? "成功" : "失败") + ",订单号=" + payId + ",订单所属用户编号=" + userPay.getUserId());

                //更新账户流水状态为有效(已完成)
                if(count > 0)
                {
                    updateDto = new BaseDto();
                    updateDto.put("userId",userPay.getUserId());//设置用户编号
                    updateDto.put("businessId",payId);//设置流水关联的订单号
                    updateDto.put("status",1);//设置流水状态为有效(已完成)
                    count = userDetailMapper.updateUserDetailOfTx(updateDto);
                    logger.info("[处理聚合支付-代付结果通知][付款成功]用户账户流水更新" + (count > 0? "成功" : "失败") + ",订单号=" + payId + ",订单所属用户编号=" + userPay.getUserId());
                }
            }
        }
        else
        {
            logger.info("[处理聚合支付-代付结果通知]订单不做任何处理,接收到的订单状态码=" + code
                    + ",订单号=" + payId
                    + ",订单所属用户编号=" + userPay.getUserId());
        }
        params.put("dcode",1000);
        params.put("dmsg","ok");
        logger.info("[处理聚合支付-代付结果通知]处理完毕");
    }

    /**
     * 处理聚合支付-支付宝-充值结果通知
     * @author  mcdog
     * @param   params     参数对象,通知处理结果(dcode-处理状态 dmsg-处理状态描述)也保存在该对象中,具体如下:
     *                     dcode=1000,处理成功
     *                     dcode=-1000,处理失败
     *                     dcode=130001,签名错误
     *                     dcode=130002,订单不存在
     *                     dcode=130003,交易金额不一致
     */
    public synchronized void doJuheAlipayPayResultNotify(Dto params) throws ServiceException,Exception
    {
        /**
         * 提取通知参数,校验订单信息
         */
        //获取订单信息
        String payId = params.getAsString("orderno");//提取商户订单号
        List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",payId));
        if(userPayList == null || userPayList.size() == 0)
        {
            logger.error("[处理聚合支付-支付宝-充值结果通知]查询不到相关的订单信息!订单号=" + payId);
            throw new ServiceException(ErrorCode_API.ERROR_PAY_ORDERNOTEXIST,ErrorCode_API.ERROR_PAY_NOTSUPPORT_MSG);
        }
        //获取充值渠道配置
        UserPay userPay = userPayList.get(0);
        Dto paywayChannelQueryDto = new BaseDto();
        paywayChannelQueryDto.put("payCode",userPay.getPayCode());
        paywayChannelQueryDto.put("channelCode",userPay.getChannelCode());
        List<PayWayChannel> payWayChannelList = payWayChannelMapper.queryPayWayChannels(paywayChannelQueryDto);
        if(payWayChannelList == null || payWayChannelList.size() == 0)
        {
            logger.error("[处理聚合支付-支付宝-充值结果通知]查询不到相关的充值渠道配置!订单号=" + payId
                    + ",充值方式业务编号=" + userPay.getPayCode()
                    + ",充值渠道编号=" + userPay.getChannelCode());
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
        //校验签名
        PayWayChannel payWayChannel = payWayChannelList.get(0);
        String respSign = params.getAsString("signature");
        params.remove("signature");
        String realSign = SortUtils.getOrderByAsciiAscFromDto(params,false) + "&key=" + payWayChannel.getSecretKey();
        realSign = MD5.md5(realSign).toUpperCase();
        if(!respSign.equals(realSign))
        {
            logger.error("[处理聚合支付-支付宝-充值结果通知]通知签名验证不通过!通知参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //判断订单状态,如果已经处理过,则不再处理,直接响应成功
        if(userPay.getStatus() == -1 || userPay.getStatus() == 3)
        {
            logger.error("[处理聚合支付-支付宝-充值结果通知]订单已被处理过,本次不再处理!订单号=" + userPay.getPayId());
            params.put("dcode",1000);
            params.put("dmsg","success");
            return;
        }

        /**
         * 根据充值结果更新订单及账户信息
         */
        //初始化订单更新参数
        params.put("dcode",-1000);
        params.put("dmsg","处理失败");
        Dto updateDto = new BaseDto();
        updateDto.put("payId",payId);//设置商户订单号
        updateDto.put("cpayId",userPay.getChannelPayId());//设置渠道流水号

        //判断业务处理状态,如果处理成功,则走更新用户余额及支付状态的逻辑
        String code = params.getAsString("code");//提取订单状态码
        if("11".equals(code))
        {
            updateDto.put("ptime",DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//设置订单处理完成时间
            updateDto.put("smoney",params.getAsDoubleValue("transamt") / 100);//设置订单交易金额
            userPayMapper.doRecharge(updateDto);//调用充值方法
            logger.info("[处理聚合支付-支付宝-充值结果通知][充值成功]订单号=" + payId + ",处理结果:dcode=" + updateDto.getAsString("dcode") + ",dmsg=" + updateDto.getAsString("dmsg"));

            //判断处理结果状态,根据状态设置相应的响应结果
            int dcode = updateDto.getAsInteger("dcode");//提取处理结果状态码
            if(dcode == 1000)
            {
                //处理成功
                params.put("dcode",1000);
                params.put("dmsg","success");
                params.put("opmethod","处理聚合支付-支付宝-充值结果通知");
                params.put("activityType", "2");//设置活动类型为特定活动(0-彩票首页焦点图 1-咨询首页焦点图 2-特定活动 3-公告)
                params.put("couponType","1");//设置优惠券赠送类型为充值送(0-注册送 1-充值送)
                params.put("smoney",updateDto.get("smoney"));//设置金额
                userService.sendCoupon(params,userPay.getUserId());//根据条件给用户赠送优惠券
                return;
            }
            //订单不存在
            else if(dcode == 1001)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST_MSG);
                return;
            }
            //订单金额不一致
            else if(dcode == 1002)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY_MSG);
                return;
            }
        }
    }

    /**
     * 处理直付支付-充值结果通知
     * @author  mcdog
     * @param   params     参数对象,通知处理结果(dcode-处理状态 dmsg-处理状态描述)也保存在该对象中,具体如下:
     *                     dcode=1000,处理成功
     *                     dcode=-1000,处理失败
     *                     dcode=130001,签名错误
     *                     dcode=130002,订单不存在
     *                     dcode=130003,交易金额不一致
     */
    public synchronized void doZhifuPayResultNotify(Dto params) throws ServiceException,Exception
    {
        /**
         * 提取通知参数,校验订单信息
         */
        //获取订单信息
        String payId = params.getAsString("trade_no");//提取商户订单号
        List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",payId));
        if(userPayList == null || userPayList.size() == 0)
        {
            logger.error("[处理直付支付-充值结果通知]查询不到相关的订单信息!订单号=" + payId);
            throw new ServiceException(ErrorCode_API.ERROR_PAY_ORDERNOTEXIST,ErrorCode_API.ERROR_PAY_NOTSUPPORT_MSG);
        }
        //获取充值渠道配置
        UserPay userPay = userPayList.get(0);
        Dto paywayChannelQueryDto = new BaseDto();
        paywayChannelQueryDto.put("payCode",userPay.getPayCode());
        paywayChannelQueryDto.put("channelCode",userPay.getChannelCode());
        List<PayWayChannel> payWayChannelList = payWayChannelMapper.queryPayWayChannels(paywayChannelQueryDto);
        if(payWayChannelList == null || payWayChannelList.size() == 0)
        {
            logger.error("[处理直付支付-充值结果通知]查询不到相关的充值渠道配置!订单号=" + payId
                    + ",充值方式业务编号=" + userPay.getPayCode()
                    + ",充值渠道编号=" + userPay.getChannelCode());
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
        //校验签名
        PayWayChannel payWayChannel = payWayChannelList.get(0);
        String respSign = params.getAsString("sign");
        params.remove("sign");
        String realSign = SortUtils.getOrderByAsciiAscFromDto(params,false) + "&secret=" + payWayChannel.getSecretKey();
        realSign = MD5.md5(realSign);
        if(!respSign.equals(realSign))
        {
            logger.error("[处理直付支付-充值结果通知]通知签名验证不通过!通知参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //判断订单状态,如果已经处理过,则不再处理,直接响应成功
        if(userPay.getStatus() == -1 || userPay.getStatus() == 3)
        {
            logger.error("[处理直付支付-充值结果通知]订单已被处理过,本次不再处理!订单号=" + userPay.getPayId());
            params.put("dcode",1000);
            params.put("dmsg","success");
            return;
        }

        /**
         * 根据充值结果更新订单及账户信息
         */
        //初始化订单更新参数
        params.put("dcode",-1000);
        params.put("dmsg","处理失败");
        Dto updateDto = new BaseDto();
        updateDto.put("payId",payId);//设置商户订单号
        updateDto.put("cpayId",userPay.getChannelPayId());//设置渠道流水号

        //更新用户余额及支付状态的逻辑
        updateDto.put("ptime",DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//设置订单处理完成时间
        updateDto.put("smoney",params.getAsDoubleValue("money") / 100);//设置订单交易金额
        userPayMapper.doRecharge(updateDto);//调用充值方法
        logger.info("[处理直付支付-充值结果通知][充值成功]订单号=" + payId + ",处理结果:dcode=" + updateDto.getAsString("dcode") + ",dmsg=" + updateDto.getAsString("dmsg"));

        //判断处理结果状态,根据状态设置相应的响应结果
        int dcode = updateDto.getAsInteger("dcode");//提取处理结果状态码
        if(dcode == 1000)
        {
            //处理成功
            params.put("dcode",1000);
            params.put("dmsg","success");
            params.put("opmethod","处理直付支付-充值结果通知");
            params.put("activityType", "2");//设置活动类型为特定活动(0-彩票首页焦点图 1-咨询首页焦点图 2-特定活动 3-公告)
            params.put("couponType","1");//设置优惠券赠送类型为充值送(0-注册送 1-充值送)
            params.put("smoney",updateDto.get("smoney"));//设置金额
            userService.sendCoupon(params,userPay.getUserId());//根据条件给用户赠送优惠券
            return;
        }
        //订单不存在
        else if(dcode == 1001)
        {
            params.put("dcode",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST);
            params.put("dmsg",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST_MSG);
            return;
        }
        //订单金额不一致
        else if(dcode == 1002)
        {
            params.put("dcode",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY);
            params.put("dmsg",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY_MSG);
            return;
        }
    }

    /**
     * 处理汇潮支付-代付结果通知
     * @author  mcdog
     * @param   params     参数对象,通知处理结果(dcode-处理状态 dmsg-处理状态描述)也保存在该对象中,具体如下:
     *                     dcode=1000,处理成功
     *                     dcode=-1000,处理失败
     *                     dcode=130001,签名错误
     *                     dcode=130002,订单不存在
     */
    public synchronized void doHuichaoDpayResult(Dto params) throws ServiceException,Exception
    {
        //校验签名
        String respSign = params.getAsString("SignInfo");
        String plain = "MerNo=" + HuiChaoUtils.merchantNo + "&MerBillNo=" + params.getAsString("MerBillNo")
                + "&CardNo=" + params.getAsString("CardNo") + "&Amount=" + params.getAsString("Amount")
                + "&Succeed=" + params.getAsString("Succeed") + "&BillNo=" + params.getAsString("BillNo");
        RsaUtils rsaUtils = RsaUtils.getInstance();
        if(!rsaUtils.verifySignature(respSign,plain,HuiChaoUtils.ymdPublicKey))
        {
            logger.error("[处理汇潮支付-代付结果通知]通知签名验证不通过!通知参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //获取订单信息
        String cpayId = params.get("MerBillNo").toString();//提取商户订单号
        List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",cpayId));
        if(userPayList == null || userPayList.size() == 0)
        {
            logger.error("[处理汇潮支付-代付结果通知]查询不到相关的订单信息!订单号=" + cpayId);
            return;
        }
        //判断订单状态,如果已经处理过,则不再处理,直接响应成功
        UserPay userPay = userPayList.get(0);
        String payId = userPay.getPayId();
        if(userPay.getStatus() == PayConstants.PAYORDER_STATUS_FAILURE
                || userPay.getStatus() == PayConstants.PAYORDER_STATUS_SUCCESS)
        {
            logger.error("[处理汇潮支付-代付结果通知]订单已经处理过,不再重复处理!订单号=" + payId);
            return;
        }
        //判断订单金额是否一致
        if(params.getAsDoubleValue("Amount") != userPay.getMoney().doubleValue())
        {
            logger.error("[处理汇潮支付-代付结果通知]订单金额不一致!已终止对该订单的处理!订单号=" + payId);
            return;
        }
        //初始化订单更新参数
        Dto updateDto = new BaseDto();
        updateDto.put("payId",payId);//设置商户订单号
        updateDto.put("channelPayId", params.get("BillNo").toString());//设置渠道流水号

        //判断订单的状态
        String succeed = params.getAsString("Succeed");//提取订单交易状态,00-成功 11-转账退回
        if("00".equals(succeed))
        {
            //付款成功,则更新订单状态为处理成功
            updateDto.put("status",PayConstants.PAYORDER_STATUS_SUCCESS);//设置订单状态为处理成功
            updateDto.put("remark","处理成功");
            int count = userPayMapper.updateUserPay(updateDto);//更新订单状态
            logger.info("[处理汇潮支付-代付结果通知][付款成功]订单状态更新" + (count > 0? "成功" : "失败") + ",订单号=" + payId + ",订单所属用户编号=" + userPay.getUserId());
            if(count > 0)
            {
                //更新用户账户信息
                updateDto = new BaseDto("userId",userPay.getUserId());
                updateDto.put("offsetFrozen",-userPay.getMoney());//冻结金额减去订单的金额
                count = userAccountMapper.updateUserAccount(updateDto);//更新用户账户信息
                logger.info("[处理汇潮支付-代付结果通知][付款成功]用户账户更新" + (count > 0? "成功" : "失败") + ",订单号=" + payId + ",订单所属用户编号=" + userPay.getUserId());

                //更新账户流水状态为有效(已完成)
                if(count > 0)
                {
                    updateDto = new BaseDto();
                    updateDto.put("userId",userPay.getUserId());//设置用户编号
                    updateDto.put("businessId",payId);//设置流水关联的订单号
                    updateDto.put("status",1);//设置流水状态为有效(已完成)
                    count = userDetailMapper.updateUserDetailOfTx(updateDto);
                    logger.info("[处理汇潮支付-代付结果通知][付款成功]用户账户流水更新" + (count > 0? "成功" : "失败") + ",订单号=" + payId + ",订单所属用户编号=" + userPay.getUserId());
                }
            }
        }
        //转账退回
        else if("11".equals(succeed))
        {
            updateDto.put("status",PayConstants.PAYORDER_STATUS_FAILURE);//设置订单状态为处理失败
            updateDto.put("remark",StringUtil.isEmpty(params.get("Result"))? "付款失败" : params.get("Result"));
            int count = userPayMapper.updateUserPay(updateDto);//更新订单状态
            logger.info("[处理汇潮支付-代付结果通知][付款失败]订单状态更新" + (count > 0? "成功" : "失败") + ",订单号=" + userPay.getPayId() + ",订单所属用户编号=" + userPay.getUserId());
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
        else
        {
            logger.info("[处理汇潮支付-代付结果通知]订单不做任何处理,接收到的订单状态码=" + succeed + ",订单号=" + payId + ",订单所属用户编号=" + userPay.getUserId());
        }
        params.put("dcode",1000);
        params.put("dmsg","ok");
        logger.info("[处理汇潮支付-代付结果通知]处理完毕");
    }

    /**
     * 处理智能云-充值结果通知
     * @author  mcdog
     * @param   params     参数对象,通知处理结果(dcode-处理状态 dmsg-处理状态描述)也保存在该对象中,具体如下:
     *                     dcode=1000,处理成功
     *                     dcode=-1000,处理失败
     *                     dcode=130001,签名错误
     *                     dcode=130002,订单不存在
     *                     dcode=130003,交易金额不一致
     */
    public synchronized void doZhinengyunPayResultNotify(Dto params) throws ServiceException,Exception
    {
        /**
         * 提取通知参数,校验订单信息
         */
        //获取订单信息
        String payId = params.getAsString("orderid");//提取商户订单号
        List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",payId));
        if(userPayList == null || userPayList.size() == 0)
        {
            logger.error("[处理智能云-充值结果通知]查询不到相关的订单信息!订单号=" + payId);
            throw new ServiceException(ErrorCode_API.ERROR_PAY_ORDERNOTEXIST,ErrorCode_API.ERROR_PAY_NOTSUPPORT_MSG);
        }
        //获取充值渠道配置
        UserPay userPay = userPayList.get(0);
        Dto paywayChannelQueryDto = new BaseDto();
        paywayChannelQueryDto.put("payCode",userPay.getPayCode());
        paywayChannelQueryDto.put("channelCode",userPay.getChannelCode());
        List<PayWayChannel> payWayChannelList = payWayChannelMapper.queryPayWayChannels(paywayChannelQueryDto);
        if(payWayChannelList == null || payWayChannelList.size() == 0)
        {
            logger.error("[处理智能云-充值结果通知]查询不到相关的充值渠道配置!订单号=" + payId
                    + ",充值方式业务编号=" + userPay.getPayCode()
                    + ",充值渠道编号=" + userPay.getChannelCode());
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
        //校验秘钥
        PayWayChannel payWayChannel = payWayChannelList.get(0);
        String respSign = params.getAsString("key");
        String realSign = params.getAsString("orderid") + params.getAsString("ordno")
                + params.getAsString("price") + params.getAsString("realprice" + payWayChannel.getSecretKey());
        realSign = MD5.md5(realSign).toLowerCase();
        if(!respSign.equals(realSign))
        {
            logger.error("[处理智能云-充值结果通知]通知秘钥验证不通过!通知参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //判断订单状态,如果已经处理过,则不再处理,直接响应成功
        if(userPay.getStatus() == -1 || userPay.getStatus() == 3)
        {
            logger.error("[处理智能云-充值结果通知]订单已被处理过,本次不再处理!订单号=" + userPay.getPayId());
            params.put("dcode",1000);
            params.put("dmsg","success");
            return;
        }

        /**
         * 根据充值结果更新订单及账户信息
         */
        //初始化订单更新参数
        params.put("dcode",-1000);
        params.put("dmsg","处理失败");
        Dto updateDto = new BaseDto();
        updateDto.put("payId",payId);//设置商户订单号
        updateDto.put("cpayId",StringUtil.isEmpty(userPay.getChannelPayId())? params.get("ordno") : userPay.getChannelPayId());//设置渠道流水号

        //更新用户余额及支付状态的逻辑
        updateDto.put("ptime",DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//设置订单处理完成时间
        updateDto.put("smoney",params.getAsDoubleValue("realprice"));//设置订单交易金额
        userPayMapper.doRecharge(updateDto);//调用充值方法
        logger.info("[处理智能云-充值结果通知][充值成功]订单号=" + payId + ",处理结果:dcode=" + updateDto.getAsString("dcode") + ",dmsg=" + updateDto.getAsString("dmsg"));

        //判断处理结果状态,根据状态设置相应的响应结果
        int dcode = updateDto.getAsInteger("dcode");//提取处理结果状态码
        if(dcode == 1000)
        {
            //处理成功
            params.put("dcode",1000);
            params.put("dmsg","success");
            params.put("opmethod","处理智能云-充值结果通知");
            params.put("activityType", "2");//设置活动类型为特定活动(0-彩票首页焦点图 1-咨询首页焦点图 2-特定活动 3-公告)
            params.put("couponType","1");//设置优惠券赠送类型为充值送(0-注册送 1-充值送)
            params.put("smoney",updateDto.get("smoney"));//设置金额
            userService.sendCoupon(params,userPay.getUserId());//根据条件给用户赠送优惠券
            return;
        }
        //订单不存在
        else if(dcode == 1001)
        {
            params.put("dcode",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST);
            params.put("dmsg",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST_MSG);
            return;
        }
        //订单金额不一致
        else if(dcode == 1002)
        {
            params.put("dcode",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY);
            params.put("dmsg",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY_MSG);
            return;
        }
    }

    /**
     * 处理傲游支付-充值结果通知
     * @author  mcdog
     * @param   params     参数对象,通知处理结果(dcode-处理状态 dmsg-处理状态描述)也保存在该对象中,具体如下:
     *                     dcode=1000,处理成功
     *                     dcode=-1000,处理失败
     *                     dcode=130001,签名错误
     *                     dcode=130002,订单不存在
     *                     dcode=130003,交易金额不一致
     */
    public synchronized void doAoyouPayResultNotify(Dto params) throws ServiceException,Exception
    {
        /**
         * 提取通知参数,校验订单信息
         */
        //获取订单信息
        String payId = params.getAsString("orderid");//提取商户订单号
        List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",payId));
        if(userPayList == null || userPayList.size() == 0)
        {
            logger.error("[处理傲游支付-充值结果通知]查询不到相关的订单信息!订单号=" + payId);
            throw new ServiceException(ErrorCode_API.ERROR_PAY_ORDERNOTEXIST,ErrorCode_API.ERROR_PAY_NOTSUPPORT_MSG);
        }
        //获取充值渠道配置
        UserPay userPay = userPayList.get(0);
        Dto paywayChannelQueryDto = new BaseDto();
        paywayChannelQueryDto.put("payCode",userPay.getPayCode());
        paywayChannelQueryDto.put("channelCode",userPay.getChannelCode());
        List<PayWayChannel> payWayChannelList = payWayChannelMapper.queryPayWayChannels(paywayChannelQueryDto);
        if(payWayChannelList == null || payWayChannelList.size() == 0)
        {
            logger.error("[处理傲游支付-充值结果通知]查询不到相关的充值渠道配置!订单号=" + payId
                    + ",充值方式业务编号=" + userPay.getPayCode()
                    + ",充值渠道编号=" + userPay.getChannelCode());
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
        //校验签名
        PayWayChannel payWayChannel = payWayChannelList.get(0);
        String respSign = params.getAsString("sign");
        params.remove("sign");
        String realSign = SortUtils.getOrderByAsciiAscFromDto(params,false) + "&key=" + payWayChannel.getSecretKey();
        realSign = MD5.md5(realSign).toUpperCase();
        if(!respSign.equals(realSign))
        {
            logger.error("[处理傲游支付-充值结果通知]通知签名验证不通过!通知参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //判断订单状态,如果已经处理过,则不再处理,直接响应成功
        if(userPay.getStatus() == -1 || userPay.getStatus() == 3)
        {
            logger.error("[处理傲游支付-充值结果通知]订单已被处理过,本次不再处理!订单号=" + userPay.getPayId());
            params.put("dcode",1000);
            params.put("dmsg","success");
            return;
        }

        /**
         * 根据充值结果更新订单及账户信息
         */
        //初始化订单更新参数
        params.put("dcode",-1000);
        params.put("dmsg","处理失败");
        Dto updateDto = new BaseDto();
        updateDto.put("payId",payId);//设置商户订单号
        updateDto.put("cpayId",StringUtil.isEmpty(userPay.getChannelPayId())? params.get("transaction_id") : userPay.getChannelPayId());//设置渠道流水号

        //判断业务处理状态,如果处理成功,则走更新用户余额及支付状态的逻辑
        String returncode = params.getAsString("returncode");//提取订单状态码
        if("00".equals(returncode))
        {
            updateDto.put("ptime",DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//设置订单处理完成时间
            updateDto.put("smoney",params.getAsDoubleValue("amount"));//设置订单交易金额
            userPayMapper.doRecharge(updateDto);//调用充值方法
            logger.info("[处理傲游支付-充值结果通知][充值成功]订单号=" + payId + ",处理结果:dcode=" + updateDto.getAsString("dcode") + ",dmsg=" + updateDto.getAsString("dmsg"));

            //判断处理结果状态,根据状态设置相应的响应结果
            int dcode = updateDto.getAsInteger("dcode");//提取处理结果状态码
            if(dcode == 1000)
            {
                //处理成功
                params.put("dcode",1000);
                params.put("dmsg","success");
                params.put("opmethod","处理傲游支付-充值结果通知");
                params.put("activityType", "2");//设置活动类型为特定活动(0-彩票首页焦点图 1-咨询首页焦点图 2-特定活动 3-公告)
                params.put("couponType","1");//设置优惠券赠送类型为充值送(0-注册送 1-充值送)
                params.put("smoney",updateDto.get("smoney"));//设置金额
                userService.sendCoupon(params,userPay.getUserId());//根据条件给用户赠送优惠券
                return;
            }
            //订单不存在
            else if(dcode == 1001)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST_MSG);
                return;
            }
            //订单金额不一致
            else if(dcode == 1002)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY_MSG);
                return;
            }
        }
        else
        {
            //更新订单状态为失败
            updateDto.put("status",PayConstants.PAYORDER_STATUS_FAILURE);
            updateDto.put("remark","充值失败,订单状态码=" + returncode);
            int count = userPayMapper.updateUserPay(updateDto);
            logger.info("[处理傲游支付-充值结果通知][充值失败]订单状态更新" + (count > 0? "成功" : "失败") + "!订单号=" + userPay.getPayId() + ",订单所属用户编号=" + userPay.getUserId());
        }
    }

    /**
     * 处理BB支付-充值结果通知
     * @author  mcdog
     * @param   params     参数对象,通知处理结果(dcode-处理状态 dmsg-处理状态描述)也保存在该对象中,具体如下:
     *                     dcode=1000,处理成功
     *                     dcode=-1000,处理失败
     *                     dcode=130001,签名错误
     *                     dcode=130002,订单不存在
     *                     dcode=130003,交易金额不一致
     */
    public synchronized void doBbPayResultNotify(Dto params) throws ServiceException,Exception
    {
        /**
         * 提取通知参数,校验订单信息
         */
        //获取订单信息
        String payId = params.getAsString("orderid");//提取商户订单号
        List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",payId));
        if(userPayList == null || userPayList.size() == 0)
        {
            logger.error("[处理BB支付-充值结果通知]查询不到相关的订单信息!订单号=" + payId);
            throw new ServiceException(ErrorCode_API.ERROR_PAY_ORDERNOTEXIST,ErrorCode_API.ERROR_PAY_NOTSUPPORT_MSG);
        }
        //获取充值渠道配置
        UserPay userPay = userPayList.get(0);
        Dto paywayChannelQueryDto = new BaseDto();
        paywayChannelQueryDto.put("payCode",userPay.getPayCode());
        paywayChannelQueryDto.put("channelCode",userPay.getChannelCode());
        List<PayWayChannel> payWayChannelList = payWayChannelMapper.queryPayWayChannels(paywayChannelQueryDto);
        if(payWayChannelList == null || payWayChannelList.size() == 0)
        {
            logger.error("[处理BB支付-充值结果通知]查询不到相关的充值渠道配置!订单号=" + payId
                    + ",充值方式业务编号=" + userPay.getPayCode()
                    + ",充值渠道编号=" + userPay.getChannelCode());
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
        //校验签名
        PayWayChannel payWayChannel = payWayChannelList.get(0);
        String respSign = params.getAsString("sign");
        params.remove("sign");
        String realSign = SortUtils.getOrderByAsciiAscFromDto(params,false) + "&key=" + payWayChannel.getSecretKey();
        realSign = MD5.md5(realSign).toUpperCase();
        if(!respSign.equals(realSign))
        {
            logger.error("[处理BB支付-充值结果通知]通知签名验证不通过!通知参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //判断订单状态,如果已经处理过,则不再处理,直接响应成功
        if(userPay.getStatus() == -1 || userPay.getStatus() == 3)
        {
            logger.error("[处理BB支付-充值结果通知]订单已被处理过,本次不再处理!订单号=" + userPay.getPayId());
            params.put("dcode",1000);
            params.put("dmsg","success");
            return;
        }

        /**
         * 根据充值结果更新订单及账户信息
         */
        //初始化订单更新参数
        params.put("dcode",-1000);
        params.put("dmsg","处理失败");
        Dto updateDto = new BaseDto();
        updateDto.put("payId",payId);//设置商户订单号
        updateDto.put("cpayId",StringUtil.isEmpty(userPay.getChannelPayId())? params.get("transaction_id") : userPay.getChannelPayId());//设置渠道流水号

        //判断业务处理状态,如果处理成功,则走更新用户余额及支付状态的逻辑
        String returncode = params.getAsString("returncode");//提取订单状态码
        if("00".equals(returncode))
        {
            updateDto.put("ptime",DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//设置订单处理完成时间
            updateDto.put("smoney",params.getAsDoubleValue("amount"));//设置订单交易金额
            userPayMapper.doRecharge(updateDto);//调用充值方法
            logger.info("[处理BB支付-充值结果通知][充值成功]订单号=" + payId + ",处理结果:dcode=" + updateDto.getAsString("dcode") + ",dmsg=" + updateDto.getAsString("dmsg"));

            //判断处理结果状态,根据状态设置相应的响应结果
            int dcode = updateDto.getAsInteger("dcode");//提取处理结果状态码
            if(dcode == 1000)
            {
                //处理成功
                params.put("dcode",1000);
                params.put("dmsg","success");
                params.put("opmethod","处理BB支付-充值结果通知");
                params.put("activityType", "2");//设置活动类型为特定活动(0-彩票首页焦点图 1-咨询首页焦点图 2-特定活动 3-公告)
                params.put("couponType","1");//设置优惠券赠送类型为充值送(0-注册送 1-充值送)
                params.put("smoney",updateDto.get("smoney"));//设置金额
                userService.sendCoupon(params,userPay.getUserId());//根据条件给用户赠送优惠券
                return;
            }
            //订单不存在
            else if(dcode == 1001)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST_MSG);
                return;
            }
            //订单金额不一致
            else if(dcode == 1002)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY_MSG);
                return;
            }
        }
    }

    /**
     * 处理陌陌付-支付宝-充值结果通知
     * @author  mcdog
     * @param   params     参数对象,通知处理结果(dcode-处理状态 dmsg-处理状态描述)也保存在该对象中,具体如下:
     *                     dcode=1000,处理成功
     *                     dcode=-1000,处理失败
     *                     dcode=130001,签名错误
     *                     dcode=130002,订单不存在
     *                     dcode=130003,交易金额不一致
     */
    public synchronized void doMomoAlipayPayResultNotify(Dto params) throws ServiceException,Exception
    {
        /**
         * 提取通知参数,校验订单信息
         */
        //获取订单信息
        String payId = params.getAsString("orderid");//提取商户订单号
        List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",payId));
        if(userPayList == null || userPayList.size() == 0)
        {
            logger.error("[处理陌陌付-支付宝-充值结果通知]查询不到相关的订单信息!订单号=" + payId);
            throw new ServiceException(ErrorCode_API.ERROR_PAY_ORDERNOTEXIST,ErrorCode_API.ERROR_PAY_NOTSUPPORT_MSG);
        }
        //获取充值渠道配置
        UserPay userPay = userPayList.get(0);
        Dto paywayChannelQueryDto = new BaseDto();
        paywayChannelQueryDto.put("payCode",userPay.getPayCode());
        paywayChannelQueryDto.put("channelCode",userPay.getChannelCode());
        List<PayWayChannel> payWayChannelList = payWayChannelMapper.queryPayWayChannels(paywayChannelQueryDto);
        if(payWayChannelList == null || payWayChannelList.size() == 0)
        {
            logger.error("[处理陌陌付-支付宝-充值结果通知]查询不到相关的充值渠道配置!订单号=" + payId
                    + ",充值方式业务编号=" + userPay.getPayCode()
                    + ",充值渠道编号=" + userPay.getChannelCode());
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
        //校验签名
        PayWayChannel payWayChannel = payWayChannelList.get(0);
        String respSign = params.getAsString("sign");
        params.remove("sign");
        params.remove("attach");
        String realSign = SortUtils.getOrderByAsciiAscFromDto(params,false) + "&key=" + payWayChannel.getSecretKey();
        realSign = MD5.md5(realSign).toUpperCase();
        if(!respSign.equals(realSign))
        {
            logger.error("[处理陌陌付-支付宝-充值结果通知]通知签名验证不通过!通知参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //判断订单状态,如果已经处理过,则不再处理,直接响应成功
        if(userPay.getStatus() == -1 || userPay.getStatus() == 3)
        {
            logger.error("[处理陌陌付-支付宝-充值结果通知]订单已被处理过,本次不再处理!订单号=" + userPay.getPayId());
            params.put("dcode",1000);
            params.put("dmsg","success");
            return;
        }

        /**
         * 根据充值结果更新订单及账户信息
         */
        //初始化订单更新参数
        params.put("dcode",-1000);
        params.put("dmsg","处理失败");
        Dto updateDto = new BaseDto();
        updateDto.put("payId",payId);//设置商户订单号
        updateDto.put("cpayId",params.getAsString("transaction_id"));//设置渠道流水号

        //判断业务处理状态,如果处理成功,则走更新用户余额及支付状态的逻辑
        String code = params.getAsString("returncode");//提取订单状态码
        if("00".equals(code))
        {
            updateDto.put("ptime",DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//设置订单处理完成时间
            updateDto.put("smoney",params.getAsDoubleValue("amount"));//设置订单交易金额
            userPayMapper.doRecharge(updateDto);//调用充值方法
            logger.info("[处理陌陌付-支付宝-充值结果通知][充值成功]订单号=" + payId + ",处理结果:dcode=" + updateDto.getAsString("dcode") + ",dmsg=" + updateDto.getAsString("dmsg"));

            //判断处理结果状态,根据状态设置相应的响应结果
            int dcode = updateDto.getAsInteger("dcode");//提取处理结果状态码
            if(dcode == 1000)
            {
                //处理成功
                params.put("dcode",1000);
                params.put("dmsg","success");
                params.put("opmethod","处理陌陌付-支付宝-充值结果通知");
                params.put("activityType", "2");//设置活动类型为特定活动(0-彩票首页焦点图 1-咨询首页焦点图 2-特定活动 3-公告)
                params.put("couponType","1");//设置优惠券赠送类型为充值送(0-注册送 1-充值送)
                params.put("smoney",updateDto.get("smoney"));//设置金额
                userService.sendCoupon(params,userPay.getUserId());//根据条件给用户赠送优惠券
                return;
            }
            //订单不存在
            else if(dcode == 1001)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST_MSG);
                return;
            }
            //订单金额不一致
            else if(dcode == 1002)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY_MSG);
                return;
            }
        }
    }

    /**
     * 处理万两支付-充值结果通知
     * @author  mcdog
     * @param   params     参数对象,通知处理结果(dcode-处理状态 dmsg-处理状态描述)也保存在该对象中,具体如下:
     *                     dcode=1000,处理成功
     *                     dcode=-1000,处理失败
     *                     dcode=130001,签名错误
     *                     dcode=130002,订单不存在
     *                     dcode=130003,交易金额不一致
     */
    public synchronized void doWlpayPayResultNotify(Dto params) throws ServiceException,Exception
    {
        /**
         * 提取通知参数,校验订单信息
         */
        //获取订单信息
        String payId = params.getAsString("ordernumber");//提取商户订单号
        List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",payId));
        if(userPayList == null || userPayList.size() == 0)
        {
            logger.error("[处理万两支付-充值结果通知]查询不到相关的订单信息!订单号=" + payId);
            throw new ServiceException(ErrorCode_API.ERROR_PAY_ORDERNOTEXIST,ErrorCode_API.ERROR_PAY_NOTSUPPORT_MSG);
        }
        //获取充值渠道配置
        UserPay userPay = userPayList.get(0);
        Dto paywayChannelQueryDto = new BaseDto();
        paywayChannelQueryDto.put("payCode",userPay.getPayCode());
        paywayChannelQueryDto.put("channelCode",userPay.getChannelCode());
        List<PayWayChannel> payWayChannelList = payWayChannelMapper.queryPayWayChannels(paywayChannelQueryDto);
        if(payWayChannelList == null || payWayChannelList.size() == 0)
        {
            logger.error("[处理万两支付-充值结果通知]查询不到相关的充值渠道配置!订单号=" + payId
                    + ",充值方式业务编号=" + userPay.getPayCode()
                    + ",充值渠道编号=" + userPay.getChannelCode());
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
        //校验签名
        PayWayChannel payWayChannel = payWayChannelList.get(0);
        String respSign = params.getAsString("sign");
        StringBuilder realSignBuilder = new StringBuilder();
        realSignBuilder.append("partner=" + params.getAsString("partner"));
        realSignBuilder.append("&ordernumber=" + params.getAsString("ordernumber"));
        realSignBuilder.append("&orderstatus=" + params.getAsString("orderstatus"));
        realSignBuilder.append("&paymoney=" + params.getAsString("paymoney"));
        realSignBuilder.append(payWayChannel.getSecretKey());
        String realSign = MD5.md5(new String(realSignBuilder.toString().getBytes(),"GB2312")).toLowerCase();
        if(!respSign.equals(realSign))
        {
            logger.error("[处理万两支付-充值结果通知]通知签名验证不通过!通知参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //判断订单状态,如果已经处理过,则不再处理,直接响应成功
        if(userPay.getStatus() == -1 || userPay.getStatus() == 3)
        {
            logger.error("[处理万两支付-充值结果通知]订单已被处理过,本次不再处理!订单号=" + userPay.getPayId());
            params.put("dcode",1000);
            params.put("dmsg","success");
            return;
        }

        /**
         * 根据充值结果更新订单及账户信息
         */
        //初始化订单更新参数
        params.put("dcode",-1000);
        params.put("dmsg","处理失败");
        Dto updateDto = new BaseDto();
        updateDto.put("payId",payId);//设置商户订单号
        updateDto.put("cpayId",params.getAsString("sysnumber"));//设置渠道流水号

        //判断业务处理状态,如果处理成功,则走更新用户余额及支付状态的逻辑
        String code = params.getAsString("orderstatus");//提取订单状态码
        if("1".equals(code))
        {
            updateDto.put("ptime",DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//设置订单处理完成时间
            updateDto.put("smoney",params.getAsDoubleValue("paymoney"));//设置订单交易金额
            userPayMapper.doRecharge(updateDto);//调用充值方法
            logger.info("[处理万两支付-充值结果通知][充值成功]订单号=" + payId + ",处理结果:dcode=" + updateDto.getAsString("dcode") + ",dmsg=" + updateDto.getAsString("dmsg"));

            //判断处理结果状态,根据状态设置相应的响应结果
            int dcode = updateDto.getAsInteger("dcode");//提取处理结果状态码
            if(dcode == 1000)
            {
                //处理成功
                params.put("dcode",1000);
                params.put("dmsg","success");
                params.put("opmethod","处理万两支付-充值结果通知");
                params.put("activityType", "2");//设置活动类型为特定活动(0-彩票首页焦点图 1-咨询首页焦点图 2-特定活动 3-公告)
                params.put("couponType","1");//设置优惠券赠送类型为充值送(0-注册送 1-充值送)
                params.put("smoney",updateDto.get("smoney"));//设置金额
                userService.sendCoupon(params,userPay.getUserId());//根据条件给用户赠送优惠券
                return;
            }
            //订单不存在
            else if(dcode == 1001)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST_MSG);
                return;
            }
            //订单金额不一致
            else if(dcode == 1002)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY_MSG);
                return;
            }
        }
    }

    /**
     * 处理兆行支付-充值结果通知
     * @author  mcdog
     * @param   params     参数对象,通知处理结果(dcode-处理状态 dmsg-处理状态描述)也保存在该对象中,具体如下:
     *                     dcode=1000,处理成功
     *                     dcode=-1000,处理失败
     *                     dcode=130001,签名错误
     *                     dcode=130002,订单不存在
     *                     dcode=130003,交易金额不一致
     */
    public synchronized void doZhaohangPayResultNotify(Dto params) throws ServiceException,Exception
    {
        /**
         * 提取通知参数,校验订单信息
         */
        //获取订单信息
        String payId = params.getAsString("orderCode");//提取商户订单号
        List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",payId));
        if(userPayList == null || userPayList.size() == 0)
        {
            logger.error("[处理兆行支付-充值结果通知]查询不到相关的订单信息!订单号=" + payId);
            throw new ServiceException(ErrorCode_API.ERROR_PAY_ORDERNOTEXIST,ErrorCode_API.ERROR_PAY_NOTSUPPORT_MSG);
        }
        //获取充值渠道配置
        UserPay userPay = userPayList.get(0);
        Dto paywayChannelQueryDto = new BaseDto();
        paywayChannelQueryDto.put("payCode",userPay.getPayCode());
        paywayChannelQueryDto.put("channelCode",userPay.getChannelCode());
        List<PayWayChannel> payWayChannelList = payWayChannelMapper.queryPayWayChannels(paywayChannelQueryDto);
        if(payWayChannelList == null || payWayChannelList.size() == 0)
        {
            logger.error("[处理兆行支付-充值结果通知]查询不到相关的充值渠道配置!订单号=" + payId
                    + ",充值方式业务编号=" + userPay.getPayCode()
                    + ",充值渠道编号=" + userPay.getChannelCode());
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
        //校验签名
        PayWayChannel payWayChannel = payWayChannelList.get(0);
        String respSign = params.getAsString("sign");
        params.remove("sign");
        String realSign = SortUtils.getOrderByAsciiAscFromDto(params,false) + "&token=" + payWayChannel.getSecretKey();
        realSign = MD5.md5(realSign).toLowerCase();
        if(!respSign.equals(realSign))
        {
            logger.error("[处理兆行支付-充值结果通知]通知签名验证不通过!通知参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //判断订单状态,如果已经处理过,则不再处理,直接响应成功
        if(userPay.getStatus() == -1 || userPay.getStatus() == 3)
        {
            logger.error("[处理兆行支付-充值结果通知]订单已被处理过,本次不再处理!订单号=" + userPay.getPayId());
            params.put("dcode",1000);
            params.put("dmsg","success");
            return;
        }

        /**
         * 根据充值结果更新订单及账户信息
         */
        //初始化订单更新参数并调用充值方法
        params.put("dcode",-1000);
        params.put("dmsg","处理失败");
        Dto updateDto = new BaseDto();
        updateDto.put("payId",payId);//设置商户订单号
        updateDto.put("cpayId",params.getAsString("tradeNo"));//设置渠道流水号
        updateDto.put("ptime",DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//设置订单处理完成时间
        updateDto.put("smoney",params.getAsDoubleValue("realPrice"));//设置订单交易金额(取实际付款金额)
        userPayMapper.doRecharge(updateDto);//调用充值方法
        logger.info("[处理兆行支付-充值结果通知][充值成功]订单号=" + payId + ",处理结果:dcode=" + updateDto.getAsString("dcode") + ",dmsg=" + updateDto.getAsString("dmsg"));

        //判断处理结果状态,根据状态设置相应的响应结果
        int dcode = updateDto.getAsInteger("dcode");//提取处理结果状态码
        if(dcode == 1000)
        {
            //处理成功
            params.put("dcode",1000);
            params.put("dmsg","success");
            params.put("opmethod","处理兆行支付-充值结果通知");
            params.put("activityType", "2");//设置活动类型为特定活动(0-彩票首页焦点图 1-咨询首页焦点图 2-特定活动 3-公告)
            params.put("couponType","1");//设置优惠券赠送类型为充值送(0-注册送 1-充值送)
            params.put("smoney",updateDto.get("smoney"));//设置金额
            userService.sendCoupon(params,userPay.getUserId());//根据条件给用户赠送优惠券
            return;
        }
        //订单不存在
        else if(dcode == 1001)
        {
            params.put("dcode",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST);
            params.put("dmsg",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST_MSG);
            return;
        }
        //订单金额不一致
        else if(dcode == 1002)
        {
            params.put("dcode",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY);
            params.put("dmsg",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY_MSG);
            return;
        }
    }

    /**
     * 处理亿富通支付-充值结果通知
     * @author  mcdog
     * @param   params     参数对象,通知处理结果(dcode-处理状态 dmsg-处理状态描述)也保存在该对象中,具体如下:
     *                     dcode=1000,处理成功
     *                     dcode=-1000,处理失败
     *                     dcode=130001,签名错误
     *                     dcode=130002,订单不存在
     *                     dcode=130003,交易金额不一致
     */
    public synchronized void doYifutongPayResultNotify(Dto params) throws ServiceException,Exception
    {
        /**
         * 提取通知参数,校验订单信息
         */
        //获取订单信息
        String payId = params.getAsString("p2_ordernumber");//提取商户订单号
        List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",payId));
        if(userPayList == null || userPayList.size() == 0)
        {
            logger.error("[处理亿富通支付-充值结果通知]查询不到相关的订单信息!订单号=" + payId);
            throw new ServiceException(ErrorCode_API.ERROR_PAY_ORDERNOTEXIST,ErrorCode_API.ERROR_PAY_NOTSUPPORT_MSG);
        }
        //获取充值渠道配置
        UserPay userPay = userPayList.get(0);
        Dto paywayChannelQueryDto = new BaseDto();
        paywayChannelQueryDto.put("payCode",userPay.getPayCode());
        paywayChannelQueryDto.put("channelCode",userPay.getChannelCode());
        List<PayWayChannel> payWayChannelList = payWayChannelMapper.queryPayWayChannels(paywayChannelQueryDto);
        if(payWayChannelList == null || payWayChannelList.size() == 0)
        {
            logger.error("[处理亿富通支付-充值结果通知]查询不到相关的充值渠道配置!订单号=" + payId
                    + ",充值方式业务编号=" + userPay.getPayCode()
                    + ",充值渠道编号=" + userPay.getChannelCode());
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
        //校验签名
        PayWayChannel payWayChannel = payWayChannelList.get(0);
        String respSign = params.getAsString("p10_sign");
        StringBuilder signBuilder = new StringBuilder();
        signBuilder.append(params.getAsString("p1_yingyongnum"));
        signBuilder.append("&" + params.getAsString("p2_ordernumber"));
        signBuilder.append("&" + params.getAsString("p3_money"));
        signBuilder.append("&" + params.getAsString("p4_zfstate"));
        signBuilder.append("&" + params.getAsString("p5_orderid"));
        signBuilder.append("&" + params.getAsString("p6_productcode"));
        signBuilder.append("&" + params.getAsString("p7_bank_card_code"));
        signBuilder.append("&" + params.getAsString("p8_charset"));
        signBuilder.append("&" + params.getAsString("p9_signtype"));
        signBuilder.append("&" + params.getAsString("p11_pdesc"));
        signBuilder.append("&" + payWayChannel.getSecretKey());
        String realSign = MD5.md5(signBuilder.toString()).toUpperCase();
        if(!respSign.equals(realSign))
        {
            logger.error("[处理亿富通支付-充值结果通知]通知签名验证不通过!通知参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //判断订单状态,如果已经处理过,则不再处理,直接响应成功
        if(userPay.getStatus() == -1 || userPay.getStatus() == 3)
        {
            logger.error("[处理亿富通支付-充值结果通知]订单已被处理过,本次不再处理!订单号=" + userPay.getPayId());
            params.put("dcode",1000);
            params.put("dmsg","success");
            return;
        }

        /**
         * 根据充值结果更新订单及账户信息
         */
        //初始化订单更新参数并调用充值方法
        params.put("dcode",-1000);
        params.put("dmsg","处理失败");
        Dto updateDto = new BaseDto();
        updateDto.put("payId",payId);//设置商户订单号
        updateDto.put("cpayId",params.getAsString("p5_orderid"));//设置渠道流水号

        //判断业务处理状态,如果处理成功,则走更新用户余额及支付状态的逻辑
        String state = params.getAsString("p4_zfstate");//提取订单状态码(1-成功 其它均为失败)
        if("1".equals(state))
        {
            updateDto.put("ptime",DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//设置订单处理完成时间
            updateDto.put("smoney",params.getAsDoubleValue("p3_money"));//设置订单交易金额
            userPayMapper.doRecharge(updateDto);//调用充值方法
            logger.info("[处理亿富通支付-充值结果通知][充值成功]订单号=" + payId + ",处理结果:dcode=" + updateDto.getAsString("dcode") + ",dmsg=" + updateDto.getAsString("dmsg"));

            //判断处理结果状态,根据状态设置相应的响应结果
            int dcode = updateDto.getAsInteger("dcode");//提取处理结果状态码
            if(dcode == 1000)
            {
                //处理成功
                params.put("dcode",1000);
                params.put("dmsg","success");
                params.put("opmethod","处理亿富通支付-充值结果通知");
                params.put("activityType", "2");//设置活动类型为特定活动(0-彩票首页焦点图 1-咨询首页焦点图 2-特定活动 3-公告)
                params.put("couponType","1");//设置优惠券赠送类型为充值送(0-注册送 1-充值送)
                params.put("smoney",updateDto.get("smoney"));//设置金额
                userService.sendCoupon(params,userPay.getUserId());//根据条件给用户赠送优惠券
                return;
            }
            //订单不存在
            else if(dcode == 1001)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST_MSG);
                return;
            }
            //订单金额不一致
            else if(dcode == 1002)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY_MSG);
                return;
            }
        }
    }

    /**
     * 处理易旨支付(一麻袋)-代付结果通知
     * @author  mcdog
     * @param   params     参数对象,通知处理结果(dcode-处理状态 dmsg-处理状态描述)也保存在该对象中,具体如下:
     *                     dcode=1000,处理成功
     *                     dcode=-1000,处理失败
     *                     dcode=130001,签名错误
     *                     dcode=130002,订单不存在
     */
    public synchronized void doYizhiDpayResult(Dto params) throws ServiceException,Exception
    {
        //校验签名
        String respSign = params.getAsString("SignInfo");
        String plain = "MerNo=" + YizhiPayUtils.ymdmerchantNo + "&MerBillNo=" + params.getAsString("MerBillNo")
                + "&CardNo=" + params.getAsString("CardNo") + "&Amount=" + params.getAsString("Amount")
                + "&Succeed=" + params.getAsString("Succeed") + "&BillNo=" + params.getAsString("BillNo");
        RsaUtils rsaUtils = RsaUtils.getInstance();
        if(!rsaUtils.verifySignature(respSign,plain,YizhiPayUtils.ymdpublicKey))
        {
            logger.error("[处理易旨支付(一麻袋)-代付结果通知]通知签名验证不通过!通知参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //获取订单信息
        String cpayId = params.get("MerBillNo").toString();//提取商户订单号
        List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",cpayId));
        if(userPayList == null || userPayList.size() == 0)
        {
            logger.error("[处理易旨支付(一麻袋)-代付结果通知]查询不到相关的订单信息!订单号=" + cpayId);
            return;
        }
        //判断订单状态,如果已经处理过,则不再处理,直接响应成功
        UserPay userPay = userPayList.get(0);
        String payId = userPay.getPayId();
        if(userPay.getStatus() == PayConstants.PAYORDER_STATUS_FAILURE
                || userPay.getStatus() == PayConstants.PAYORDER_STATUS_SUCCESS)
        {
            logger.error("[处理易旨支付(一麻袋)-代付结果通知]订单已经处理过,不再重复处理!订单号=" + payId);
            return;
        }
        //判断订单金额是否一致
        if(params.getAsDoubleValue("Amount") != userPay.getMoney().doubleValue())
        {
            logger.error("[处理易旨支付(一麻袋)-代付结果通知]订单金额不一致!已终止对该订单的处理!订单号=" + payId);
            return;
        }
        //初始化订单更新参数
        Dto updateDto = new BaseDto();
        updateDto.put("payId",payId);//设置商户订单号
        updateDto.put("channelPayId", params.get("BillNo").toString());//设置渠道流水号

        //判断订单的状态
        String succeed = params.getAsString("Succeed");//提取订单交易状态,00-成功 11-转账退回
        if("00".equals(succeed))
        {
            //付款成功,则更新订单状态为处理成功
            updateDto.put("status",PayConstants.PAYORDER_STATUS_SUCCESS);//设置订单状态为处理成功
            updateDto.put("remark","处理成功");
            int count = userPayMapper.updateUserPay(updateDto);//更新订单状态
            logger.info("[处理易旨支付(一麻袋)-代付结果通知][付款成功]订单状态更新" + (count > 0? "成功" : "失败") + ",订单号=" + payId + ",订单所属用户编号=" + userPay.getUserId());
            if(count > 0)
            {
                //更新用户账户信息
                updateDto = new BaseDto("userId",userPay.getUserId());
                updateDto.put("offsetFrozen",-userPay.getMoney());//冻结金额减去订单的金额
                count = userAccountMapper.updateUserAccount(updateDto);//更新用户账户信息
                logger.info("[处理易旨支付(一麻袋)-代付结果通知][付款成功]用户账户更新" + (count > 0? "成功" : "失败") + ",订单号=" + payId + ",订单所属用户编号=" + userPay.getUserId());

                //更新账户流水状态为有效(已完成)
                if(count > 0)
                {
                    updateDto = new BaseDto();
                    updateDto.put("userId",userPay.getUserId());//设置用户编号
                    updateDto.put("businessId",payId);//设置流水关联的订单号
                    updateDto.put("status",1);//设置流水状态为有效(已完成)
                    count = userDetailMapper.updateUserDetailOfTx(updateDto);
                    logger.info("[处理易旨支付(一麻袋)-代付结果通知][付款成功]用户账户流水更新" + (count > 0? "成功" : "失败") + ",订单号=" + payId + ",订单所属用户编号=" + userPay.getUserId());
                }
            }
        }
        //转账退回
        else if("11".equals(succeed))
        {
            updateDto.put("status",PayConstants.PAYORDER_STATUS_FAILURE);//设置订单状态为处理失败
            updateDto.put("remark",StringUtil.isEmpty(params.get("Result"))? "付款失败" : params.get("Result"));
            int count = userPayMapper.updateUserPay(updateDto);//更新订单状态
            logger.info("[处理易旨支付(一麻袋)-代付结果通知][付款失败]订单状态更新" + (count > 0? "成功" : "失败") + ",订单号=" + userPay.getPayId() + ",订单所属用户编号=" + userPay.getUserId());
            if(count > 0)
            {
                //更新账户流水状态为无效
                updateDto = new BaseDto();
                updateDto.put("userId",userPay.getUserId());//设置用户编号
                updateDto.put("businessId",userPay.getPayId());//设置流水关联的订单号
                updateDto.put("status",-1);//设置流水状态为无效
                count = userDetailMapper.updateUserDetailOfTx(updateDto);
                logger.info("[处理易旨支付(一麻袋)-代付结果通知][付款失败]用户账户流水更新" + (count > 0? "成功" : "失败") + ",订单号=" + userPay.getPayId() + ",订单所属用户编号=" + userPay.getUserId());

                //更新用户账户信息
                updateDto = new BaseDto("userId",userPay.getUserId());
                updateDto.put("tbalance",userPay.getMoney());//余额加上订单的金额
                updateDto.put("twithDraw",-userPay.getMoney());//累计提现金额减去订单金额
                updateDto.put("offsetFrozen",-userPay.getMoney());//冻结金额减去订单的金额
                updateDto.put("offsetWithDraw",userPay.getMoney());//可提现金额加上订单金额
                count = userAccountMapper.updateUserAccount(updateDto);//更新用户账户信息
                logger.info("[处理易旨支付(一麻袋)-代付结果通知][付款失败]用户账户更新" + (count > 0? "成功" : "失败") + ",订单号=" + userPay.getPayId() + ",订单所属用户编号=" + userPay.getUserId());
            }
        }
        else
        {
            logger.info("[处理易旨支付(一麻袋)-代付结果通知]订单不做任何处理,接收到的订单状态码=" + succeed + ",订单号=" + payId + ",订单所属用户编号=" + userPay.getUserId());
        }
        params.put("dcode",1000);
        params.put("dmsg","ok");
        logger.info("[处理易旨支付(一麻袋)-代付结果通知]处理完毕");
    }

    /**
     * 处理易旨支付-充值结果通知
     * @author  mcdog
     * @param   params     参数对象,通知处理结果(dcode-处理状态 dmsg-处理状态描述)也保存在该对象中,具体如下:
     *                     dcode=1000,处理成功
     *                     dcode=-1000,处理失败
     *                     dcode=130001,签名错误
     *                     dcode=130002,订单不存在
     *                     dcode=130003,交易金额不一致
     */
    public synchronized void doYizhiPayResultNotify(Dto params) throws ServiceException,Exception
    {
        /**
         * 提取通知参数,校验订单信息
         */
        //获取订单信息
        String payId = params.getAsString("merchantOutOrderNo");//提取商户订单号
        List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",payId));
        if(userPayList == null || userPayList.size() == 0)
        {
            logger.error("[处理易旨支付-充值结果通知]查询不到相关的订单信息!订单号=" + payId);
            throw new ServiceException(ErrorCode_API.ERROR_PAY_ORDERNOTEXIST,ErrorCode_API.ERROR_PAY_NOTSUPPORT_MSG);
        }
        //获取充值渠道配置
        UserPay userPay = userPayList.get(0);
        Dto paywayChannelQueryDto = new BaseDto();
        paywayChannelQueryDto.put("payCode",userPay.getPayCode());
        paywayChannelQueryDto.put("channelCode",userPay.getChannelCode());
        List<PayWayChannel> payWayChannelList = payWayChannelMapper.queryPayWayChannels(paywayChannelQueryDto);
        if(payWayChannelList == null || payWayChannelList.size() == 0)
        {
            logger.error("[处理易旨支付-充值结果通知]查询不到相关的充值渠道配置!订单号=" + payId
                    + ",充值方式业务编号=" + userPay.getPayCode()
                    + ",充值渠道编号=" + userPay.getChannelCode());
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
        //校验签名
        PayWayChannel payWayChannel = payWayChannelList.get(0);
        String respSign = params.getAsString("sign");
        params.remove("sign");
        params.remove("id");
        params.remove("aliNo");
        String realSign = SortUtils.getOrderByAsciiAscFromDto(params,false) + "&key=" + payWayChannel.getSecretKey();
        realSign = MD5.md5(realSign);
        if(!respSign.equals(realSign))
        {
            logger.error("[处理易旨支付-充值结果通知]通知签名验证不通过!通知参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //判断订单状态,如果已经处理过,则不再处理,直接响应成功
        if(userPay.getStatus() == -1 || userPay.getStatus() == 3)
        {
            logger.error("[处理易旨支付-充值结果通知]订单已被处理过,本次不再处理!订单号=" + userPay.getPayId());
            params.put("dcode",1000);
            params.put("dmsg","success");
            return;
        }

        /**
         * 根据充值结果更新订单及账户信息
         */
        //初始化订单更新参数并调用充值方法
        params.put("dcode",-1000);
        params.put("dmsg","处理失败");
        Dto updateDto = new BaseDto();
        updateDto.put("payId",payId);//设置商户订单号
        updateDto.put("cpayId",params.getAsString("orderNo"));//设置渠道流水号

        //判断业务处理状态,如果处理成功,则走更新用户余额及支付状态的逻辑
        String result = params.getAsString("payResult");//提取订单状态码(1-成功 其它均为失败)
        if("1".equals(result))
        {
            updateDto.put("ptime",DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//设置订单处理完成时间
            Dto msgDto = JsonUtil.jsonToDto(params.getAsString("msg"));
            updateDto.put("smoney",msgDto.getAsDoubleValue("payMoney"));//设置订单交易金额
            userPayMapper.doRecharge(updateDto);//调用充值方法
            logger.info("[处理易旨支付-充值结果通知][充值成功]订单号=" + payId + ",处理结果:dcode=" + updateDto.getAsString("dcode") + ",dmsg=" + updateDto.getAsString("dmsg"));

            //判断处理结果状态,根据状态设置相应的响应结果
            int dcode = updateDto.getAsInteger("dcode");//提取处理结果状态码
            if(dcode == 1000)
            {
                //处理成功
                params.put("dcode",1000);
                params.put("dmsg","success");
                params.put("opmethod","处理易旨支付-充值结果通知");
                params.put("activityType", "2");//设置活动类型为特定活动(0-彩票首页焦点图 1-咨询首页焦点图 2-特定活动 3-公告)
                params.put("couponType","1");//设置优惠券赠送类型为充值送(0-注册送 1-充值送)
                params.put("smoney",updateDto.get("smoney"));//设置金额
                userService.sendCoupon(params,userPay.getUserId());//根据条件给用户赠送优惠券
                return;
            }
            //订单不存在
            else if(dcode == 1001)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST_MSG);
                return;
            }
            //订单金额不一致
            else if(dcode == 1002)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY_MSG);
                return;
            }
        }
    }

    /**
     * 处理ypay-充值结果通知
     * @author  mcdog
     * @param   params     参数对象,通知处理结果(dcode-处理状态 dmsg-处理状态描述)也保存在该对象中,具体如下:
     *                     dcode=1000,处理成功
     *                     dcode=-1000,处理失败
     *                     dcode=130001,签名错误
     *                     dcode=130002,订单不存在
     *                     dcode=130003,交易金额不一致
     */
    public synchronized void doYPayResultNotify(Dto params) throws ServiceException,Exception
    {
        /**
         * 提取通知参数,校验订单信息
         */
        //获取订单信息
        String payId = params.getAsString("orderid");//提取商户订单号
        List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",payId));
        if(userPayList == null || userPayList.size() == 0)
        {
            logger.error("[处理ypay-充值结果通知]查询不到相关的订单信息!订单号=" + payId);
            throw new ServiceException(ErrorCode_API.ERROR_PAY_ORDERNOTEXIST,ErrorCode_API.ERROR_PAY_NOTSUPPORT_MSG);
        }
        //获取充值渠道配置
        UserPay userPay = userPayList.get(0);
        Dto paywayChannelQueryDto = new BaseDto();
        paywayChannelQueryDto.put("payCode",userPay.getPayCode());
        paywayChannelQueryDto.put("channelCode",userPay.getChannelCode());
        List<PayWayChannel> payWayChannelList = payWayChannelMapper.queryPayWayChannels(paywayChannelQueryDto);
        if(payWayChannelList == null || payWayChannelList.size() == 0)
        {
            logger.error("[处理ypay-充值结果通知]查询不到相关的充值渠道配置!订单号=" + payId
                    + ",充值方式业务编号=" + userPay.getPayCode()
                    + ",充值渠道编号=" + userPay.getChannelCode());
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
        //校验签名
        PayWayChannel payWayChannel = payWayChannelList.get(0);
        String respSign = params.getAsString("sign");
        params.remove("sign");
        String realSign = SortUtils.getOrderByAsciiAscFromDto(params,false) + "&key=" + payWayChannel.getSecretKey();
        realSign = MD5.md5(realSign).toUpperCase();
        if(!respSign.equals(realSign))
        {
            logger.error("[处理ypay-充值结果通知]通知签名验证不通过!通知参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //判断订单状态,如果已经处理过,则不再处理,直接响应成功
        if(userPay.getStatus() == -1 || userPay.getStatus() == 3)
        {
            logger.error("[处理ypay-充值结果通知]订单已被处理过,本次不再处理!订单号=" + userPay.getPayId());
            params.put("dcode",1000);
            params.put("dmsg","success");
            return;
        }

        /**
         * 根据充值结果更新订单及账户信息
         */
        //初始化订单更新参数并调用充值方法
        params.put("dcode",-1000);
        params.put("dmsg","处理失败");
        Dto updateDto = new BaseDto();
        updateDto.put("payId",payId);//设置商户订单号
        updateDto.put("cpayId",params.getAsString("transaction_id"));//设置渠道流水号

        //判断业务处理状态,如果处理成功,则走更新用户余额及支付状态的逻辑
        String result = params.getAsString("returncode");//提取订单状态码(00-成功 其它均为失败)
        if("00".equals(result))
        {
            updateDto.put("ptime",DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//设置订单处理完成时间
            updateDto.put("smoney",params.getAsDoubleValue("amount"));//设置订单交易金额
            userPayMapper.doRecharge(updateDto);//调用充值方法
            logger.info("[处理ypay-充值结果通知][充值成功]订单号=" + payId + ",处理结果:dcode=" + updateDto.getAsString("dcode") + ",dmsg=" + updateDto.getAsString("dmsg"));

            //判断处理结果状态,根据状态设置相应的响应结果
            int dcode = updateDto.getAsInteger("dcode");//提取处理结果状态码
            if(dcode == 1000)
            {
                //处理成功
                params.put("dcode",1000);
                params.put("dmsg","success");
                params.put("opmethod","处理ypay支付-充值结果通知");
                params.put("activityType", "2");//设置活动类型为特定活动(0-彩票首页焦点图 1-咨询首页焦点图 2-特定活动 3-公告)
                params.put("couponType","1");//设置优惠券赠送类型为充值送(0-注册送 1-充值送)
                params.put("smoney",updateDto.get("smoney"));//设置金额
                userService.sendCoupon(params,userPay.getUserId());//根据条件给用户赠送优惠券
                return;
            }
            //订单不存在
            else if(dcode == 1001)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST_MSG);
                return;
            }
            //订单金额不一致
            else if(dcode == 1002)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY_MSG);
                return;
            }
        }
    }

    /**
     * 处理kj412-充值结果通知
     * @author  mcdog
     * @param   params     参数对象,通知处理结果(dcode-处理状态 dmsg-处理状态描述)也保存在该对象中,具体如下:
     *                     dcode=1000,处理成功
     *                     dcode=-1000,处理失败
     *                     dcode=130001,签名错误
     *                     dcode=130002,订单不存在
     *                     dcode=130003,交易金额不一致
     */
    public synchronized void doKj412PayResultNotify(Dto params) throws ServiceException,Exception
    {
        /**
         * 提取通知参数,校验订单信息
         */
        Base64 base64 = new Base64();
        String resp = new String(base64.decode(params.getAsString("respContent")));//base64解密
        JSONObject respObject = JSONObject.fromObject(resp);//实际通知参数对象

        //获取订单信息
        String payId = respObject.getString("merchorder_no");//提取商户订单号
        List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",payId));
        if(userPayList == null || userPayList.size() == 0)
        {
            logger.error("[处理kj412-充值结果通知]查询不到相关的订单信息!订单号=" + payId);
            throw new ServiceException(ErrorCode_API.ERROR_PAY_ORDERNOTEXIST,ErrorCode_API.ERROR_PAY_NOTSUPPORT_MSG);
        }
        //获取充值渠道配置
        UserPay userPay = userPayList.get(0);
        Dto paywayChannelQueryDto = new BaseDto();
        paywayChannelQueryDto.put("payCode",userPay.getPayCode());
        paywayChannelQueryDto.put("channelCode",userPay.getChannelCode());
        List<PayWayChannel> payWayChannelList = payWayChannelMapper.queryPayWayChannels(paywayChannelQueryDto);
        if(payWayChannelList == null || payWayChannelList.size() == 0)
        {
            logger.error("[处理kj412-充值结果通知]查询不到相关的充值渠道配置!订单号=" + payId
                    + ",充值方式业务编号=" + userPay.getPayCode()
                    + ",充值渠道编号=" + userPay.getChannelCode());
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
        //校验签名
        PayWayChannel payWayChannel = payWayChannelList.get(0);
        String respSign = respObject.getString("sign");
        respObject.put("sign","");
        String realSign = StringUtils.signSHA512(respObject.toString() + payWayChannel.getSecretKey());
        if(!respSign.equals(realSign))
        {
            logger.error("[处理kj412-充值结果通知]通知签名验证不通过!通知参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //判断订单状态,如果已经处理过,则不再处理,直接响应成功
        if(userPay.getStatus() == -1 || userPay.getStatus() == 3)
        {
            logger.error("[处理kj412-充值结果通知]订单已被处理过,本次不再处理!订单号=" + userPay.getPayId());
            params.put("dcode",1000);
            params.put("dmsg","success");
            return;
        }

        /**
         * 根据充值结果更新订单及账户信息
         */
        //初始化订单更新参数并调用充值方法
        params.put("dcode",-1000);
        params.put("dmsg","处理失败");
        Dto updateDto = new BaseDto();
        updateDto.put("payId",payId);//设置商户订单号
        //updateDto.put("cpayId",params.getAsString("transaction_id"));//设置渠道流水号

        //判断业务处理状态,如果处理成功,则走更新用户余额及支付状态的逻辑
        String result = respObject.getString("retcode");//提取订单状态码(00-成功)
        if("00".equals(result))
        {
            updateDto.put("ptime",DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//设置订单处理完成时间
            updateDto.put("smoney",respObject.getDouble("money"));//设置订单交易金额
            userPayMapper.doRecharge(updateDto);//调用充值方法
            logger.info("[处理kj412-充值结果通知][充值成功]订单号=" + payId + ",处理结果:dcode=" + updateDto.getAsString("dcode") + ",dmsg=" + updateDto.getAsString("dmsg"));

            //判断处理结果状态,根据状态设置相应的响应结果
            int dcode = updateDto.getAsInteger("dcode");//提取处理结果状态码
            if(dcode == 1000)
            {
                //处理成功
                params.put("dcode",1000);
                params.put("dmsg","success");
                params.put("opmethod","处理kj412-充值结果通知");
                params.put("activityType", "2");//设置活动类型为特定活动(0-彩票首页焦点图 1-咨询首页焦点图 2-特定活动 3-公告)
                params.put("couponType","1");//设置优惠券赠送类型为充值送(0-注册送 1-充值送)
                params.put("smoney",updateDto.get("smoney"));//设置金额
                userService.sendCoupon(params,userPay.getUserId());//根据条件给用户赠送优惠券
                return;
            }
            //订单不存在
            else if(dcode == 1001)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST_MSG);
                return;
            }
            //订单金额不一致
            else if(dcode == 1002)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY_MSG);
                return;
            }
        }
    }

    /**
     * 处理kj412-代付结果通知
     * @author  mcdog
     * @param   params     参数对象,通知处理结果(dcode-处理状态 dmsg-处理状态描述)也保存在该对象中,具体如下:
     *                     dcode=1000,处理成功
     *                     dcode=-1000,处理失败
     *                     dcode=130001,签名错误
     *                     dcode=130002,订单不存在
     */
    public synchronized void doKj412DpayResult(Dto params) throws ServiceException,Exception
    {
        /**
         * 提起通知参数,校验订单信息
         */
        Base64 base64 = new Base64();
        String resp = new String(base64.decode(params.getAsString("respContent")));//base64解密
        JSONObject respObject = JSONObject.fromObject(resp);//实际通知参数对象

        //校验签名
        String respSign = respObject.getString("sign");
        respObject.put("sign","");
        String realSign = StringUtils.signSHA512(respObject.toString() + Kj412PayUtils.secretKey);
        if(!respSign.equals(realSign))
        {
            logger.error("[处理kj412-代付结果通知]通知签名验证不通过!通知参数=" + respObject.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //获取订单信息
        String cpayId = respObject.getString("merchorder_no");//提取商户订单号
        List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",cpayId));
        if(userPayList == null || userPayList.size() == 0)
        {
            logger.error("[处理kj412-代付结果通知]查询不到相关的订单信息!订单号=" + cpayId);
            return;
        }
        //判断订单状态,如果已经处理过,则不再处理,直接响应成功
        UserPay userPay = userPayList.get(0);
        String payId = userPay.getPayId();
        if(userPay.getStatus() == PayConstants.PAYORDER_STATUS_FAILURE
                || userPay.getStatus() == PayConstants.PAYORDER_STATUS_SUCCESS)
        {
            logger.error("[处理kj412-代付结果通知]订单已经处理过,不再重复处理!订单号=" + payId);
            return;
        }
        //判断订单金额是否一致
        if((respObject.getDouble("money") -2 )!= userPay.getMoney().doubleValue())
        {
            logger.error("[处理kj412-代付结果通知]订单金额不一致!已终止对该订单的处理!订单号=" + payId);
            return;
        }
        //初始化订单更新参数
        Dto updateDto = new BaseDto();
        updateDto.put("payId",payId);//设置商户订单号
        //updateDto.put("channelPayId",StringUtil.isEmpty(respObject.get("payorderno"))? "" : respObject.getString("payorderno"));//设置渠道流水号

        //判断订单的状态
        String retcode = respObject.getString("retcode");//提取订单交易状态,00-成功
        if("00".equals(retcode))
        {
            //付款成功,则更新订单状态为处理成功
            updateDto.put("status",PayConstants.PAYORDER_STATUS_SUCCESS);//设置订单状态为处理成功
            updateDto.put("remark","处理成功");
            int count = userPayMapper.updateUserPay(updateDto);//更新订单状态
            logger.info("[处理kj412-代付结果通知][付款成功]订单状态更新" + (count > 0? "成功" : "失败") + ",订单号=" + payId + ",订单所属用户编号=" + userPay.getUserId());
            if(count > 0)
            {
                //更新用户账户信息
                updateDto = new BaseDto("userId",userPay.getUserId());
                updateDto.put("offsetFrozen",-userPay.getMoney());//冻结金额减去订单的金额
                count = userAccountMapper.updateUserAccount(updateDto);//更新用户账户信息
                logger.info("[处理kj412-代付结果通知][付款成功]用户账户更新" + (count > 0? "成功" : "失败") + ",订单号=" + payId + ",订单所属用户编号=" + userPay.getUserId());

                //更新账户流水状态为有效(已完成)
                if(count > 0)
                {
                    updateDto = new BaseDto();
                    updateDto.put("userId",userPay.getUserId());//设置用户编号
                    updateDto.put("businessId",payId);//设置流水关联的订单号
                    updateDto.put("status",1);//设置流水状态为有效(已完成)
                    count = userDetailMapper.updateUserDetailOfTx(updateDto);
                    logger.info("[处理kj412-代付结果通知][付款成功]用户账户流水更新" + (count > 0? "成功" : "失败") + ",订单号=" + payId + ",订单所属用户编号=" + userPay.getUserId());
                }
            }
        }
        else
        {
            logger.info("[处理kj412-代付结果通知]订单不做任何处理,接收到的订单状态码=" + retcode + ",订单号=" + payId + ",订单所属用户编号=" + userPay.getUserId());
        }
        params.put("dcode",1000);
        params.put("dmsg","ok");
        logger.info("[处理kj412-代付结果通知]处理完毕");
    }

    /**
     * 处理ttpay-充值结果通知
     * @author  mcdog
     * @param   params     参数对象,通知处理结果(dcode-处理状态 dmsg-处理状态描述)也保存在该对象中,具体如下:
     *                     dcode=1000,处理成功
     *                     dcode=-1000,处理失败
     *                     dcode=130001,签名错误
     *                     dcode=130002,订单不存在
     *                     dcode=130003,交易金额不一致
     */
    public synchronized void doTtPayResultNotify(Dto params) throws ServiceException,Exception
    {
        //获取订单信息
        String payId = params.getAsString("mchntOrderNo");//提取商户订单号
        List<UserPay> userPayList = userPayMapper.queryUserPays(new BaseDto("payId",payId));
        if(userPayList == null || userPayList.size() == 0)
        {
            logger.error("[处理ttpay-充值结果通知]查询不到相关的订单信息!订单号=" + payId);
            throw new ServiceException(ErrorCode_API.ERROR_PAY_ORDERNOTEXIST,ErrorCode_API.ERROR_PAY_NOTSUPPORT_MSG);
        }
        //获取充值渠道配置
        UserPay userPay = userPayList.get(0);
        Dto paywayChannelQueryDto = new BaseDto();
        paywayChannelQueryDto.put("payCode",userPay.getPayCode());
        paywayChannelQueryDto.put("channelCode",userPay.getChannelCode());
        List<PayWayChannel> payWayChannelList = payWayChannelMapper.queryPayWayChannels(paywayChannelQueryDto);
        if(payWayChannelList == null || payWayChannelList.size() == 0)
        {
            logger.error("[处理ttpay-充值结果通知]查询不到相关的充值渠道配置!订单号=" + payId
                    + ",充值方式业务编号=" + userPay.getPayCode()
                    + ",充值渠道编号=" + userPay.getChannelCode());
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
        //校验签名
        PayWayChannel payWayChannel = payWayChannelList.get(0);
        String respSign = params.getAsString("signature");
        params.remove("signature");
        String realSign = MD5.md5(SortUtils.getOrderByAsciiAscFromDto(params,false) + "&key=" + payWayChannel.getSecretKey());
        if(!respSign.equals(realSign))
        {
            logger.error("[处理ttpay-充值结果通知]通知签名验证不通过!通知参数=" + params.toString());
            throw new ServiceException(ErrorCode_API.ERROR_PAY_SIGNERROR,ErrorCode_API.ERROR_PAY_SIGNERROR_MSG);
        }
        //判断订单状态,如果已经处理过,则不再处理,直接响应成功
        if(userPay.getStatus() == -1 || userPay.getStatus() == 3)
        {
            logger.error("[处理ttpay-充值结果通知]订单已被处理过,本次不再处理!订单号=" + userPay.getPayId());
            params.put("dcode",1000);
            params.put("dmsg","success");
            return;
        }

        /**
         * 根据充值结果更新订单及账户信息
         */
        //初始化订单更新参数并调用充值方法
        params.put("dcode",-1000);
        params.put("dmsg","处理失败");
        Dto updateDto = new BaseDto();
        updateDto.put("payId",payId);//设置商户订单号
        updateDto.put("cpayId",params.getAsString("orderNo"));//设置渠道流水号

        //判断业务处理状态,如果处理成功,则走更新用户余额及支付状态的逻辑
        String paySt = params.getAsString("paySt");//提取订单状态码(0-待支付 1-支付中 2-支付成功 3-支付失败 4-已关闭)
        if("2".equals(paySt))
        {
            updateDto.put("ptime",DateUtil.formatDate(new Date(),DateUtil.DEFAULT_DATE_TIME));//设置订单处理完成时间
            updateDto.put("smoney",params.getAsDoubleValue("amount") / 100);//设置订单交易金额
            userPayMapper.doRecharge(updateDto);//调用充值方法
            logger.info("[处理ttpay-充值结果通知][充值成功]订单号=" + payId + ",处理结果:dcode=" + updateDto.getAsString("dcode") + ",dmsg=" + updateDto.getAsString("dmsg"));

            //判断处理结果状态,根据状态设置相应的响应结果
            int dcode = updateDto.getAsInteger("dcode");//提取处理结果状态码
            if(dcode == 1000)
            {
                //处理成功
                params.put("dcode",1000);
                params.put("dmsg","success");
                params.put("opmethod","处理ttpay-充值结果通知");
                params.put("activityType", "2");//设置活动类型为特定活动(0-彩票首页焦点图 1-咨询首页焦点图 2-特定活动 3-公告)
                params.put("couponType","1");//设置优惠券赠送类型为充值送(0-注册送 1-充值送)
                params.put("smoney",updateDto.get("smoney"));//设置金额
                userService.sendCoupon(params,userPay.getUserId());//根据条件给用户赠送优惠券
                return;
            }
            //订单不存在
            else if(dcode == 1001)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_ORDERNOTEXIST_MSG);
                return;
            }
            //订单金额不一致
            else if(dcode == 1002)
            {
                params.put("dcode",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY);
                params.put("dmsg",ErrorCode_API.ERROR_PAY_MONEYNOTUNCONFORMITY_MSG);
                return;
            }
        }
        else if("3".equals(paySt))
        {
            //更新订单状态为处理失败
            logger.error("[处理ttpay-充值结果通知][充值失败]订单号=" + payId + ",paySt=" + paySt);
            updateDto.put("status",PayConstants.PAYORDER_STATUS_FAILURE);//设置订单状态为处理失败
            int count = userPayMapper.updateUserPay(updateDto);//更新订单状态
            if(count > 0)
            {
                logger.error("[处理ttpay-充值结果通知][充值失败]订单状态更新成功!订单号=" + payId);
                params.put("dcode",1000);
                params.put("dmsg","success");
            }
        }
    }

    /**
     * 根据可用的充值渠道按权重选择一个充值渠道
     * @author	sjq
     */
    public PayWayChannel getPaywayChannel(List<PayWayChannel> paywayChannelList,Double smoney)
    {
        PayWayChannel paywayChannel = null;
        if(paywayChannelList != null && paywayChannelList.size() > 0)
        {
            //筛选,只保留当前实际可用的充值渠道
            Calendar current = Calendar.getInstance();//当前时间
            int currentHour = current.get(Calendar.HOUR_OF_DAY);//当前时间-时
            int currentMinute = current.get(Calendar.MINUTE);//当前时间-分
            List<PayWayChannel> realPaywayChannelList = new ArrayList<PayWayChannel>();
            for(PayWayChannel pwc : paywayChannelList)
            {
                //判断渠道是否有单笔充值金额限制
                if((StringUtil.isNotEmpty(pwc.getMaxMoney()) && smoney > pwc.getMaxMoney())
                        || StringUtil.isNotEmpty(pwc.getMinMoney()) && smoney < pwc.getMinMoney())
                {
                    continue;
                }
                //判断渠道是否只支持固定金额充值
                if(StringUtil.isNotEmpty(pwc.getFixedMoney()))
                {
                    boolean flag = false;
                    String[] fixedMoney = pwc.getFixedMoney().split(";");
                    for(String money : fixedMoney)
                    {
                        if(Double.parseDouble(money) == smoney)
                        {
                            flag = true;
                            break;
                        }
                    }
                    if(!flag)
                    {
                        continue;
                    }
                }
                //判断启用模式,如果为时间段
                if(pwc.getModel() == 1)
                {
                    //判断当前时间是否在时间段的起始和结束时间之间,如果不在时间段区间内,则该付款方式不可用
                    if(current.before(pwc.getTimeRangeStart()) || current.after(pwc.getTimeRangeEnd()))
                    {
                        continue;
                    }
                }
                //如果启用模式为时间特征
                else if(pwc.getModel() == 2)
                {
                    int count = 0;
                    String[] timeCharacters = pwc.getTimeCharacter().split(";");//提取时间特征
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
                realPaywayChannelList.add(pwc);
            }
            //根据充值渠道的权重确定本次使用哪个充值渠道
            if(realPaywayChannelList.size() > 0)
            {
                if(realPaywayChannelList.size() == 1)
                {
                    paywayChannel = realPaywayChannelList.get(0);//只有一个可用的充值渠道,则直接取第一个
                }
                else
                {
                    //计算总权重
                    double sumWeight = 0d;
                    for(PayWayChannel pwc : realPaywayChannelList)
                    {
                        sumWeight += pwc.getWeight();
                    }
                    //计算各个充值渠道所分布的权重空间
                    List<Double> rateList = new ArrayList<Double>();
                    Double tempWeight = 0d;
                    for(PayWayChannel pwc : realPaywayChannelList)
                    {
                        tempWeight += pwc.getWeight();
                        rateList.add(tempWeight / sumWeight);
                    }
                    double rand = Math.random();//生成一个0~1的随机数
                    rateList.add(rand);//将随机数加入权重集合中
                    Collections.sort(rateList);//将权重升序排列
                    int index = rateList.indexOf(rand);//获取随机数所在的区间位置
                    paywayChannel = realPaywayChannelList.get(index);//提取随机数所在区间的充值渠道
                }
            }
        }
        return paywayChannel;
    }
}