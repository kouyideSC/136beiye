package com.caipiao.admin.service.user;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.PayConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.plugin.InitPlugin;
import com.caipiao.common.scheme.SchemeUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.lottery.PeriodMapper;
import com.caipiao.dao.scheme.SchemeMapper;
import com.caipiao.dao.ticket.TicketMapper;
import com.caipiao.dao.user.*;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.lottery.Period;
import com.caipiao.domain.scheme.SchemeZhuiHao;
import com.caipiao.domain.user.UserAccount;
import com.caipiao.domain.user.UserDetail;
import com.caipiao.domain.user.UserPay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.*;

/**
 * 充值提现流水-服务类
 */
@Service("czTxService")
@Transactional
public class CzTxService
{
    private static final Logger logger = LoggerFactory.getLogger(CzTxService.class);

    @Autowired
    private UserPayMapper userPayMapper;
    @Autowired
    private UserAccountMapper userAccountMapper;
    @Autowired
    private UserDetailMapper userDetailMapper;

    /**
     * 查询用户充值提现流水
     * @author  mcdog
     */
    public List<Dto> queryUserPayInfos(Dto params)
    {
        return userPayMapper.queryUserPayInfos(params);
    }

    /**
     * 查询用户充值提现流水总计
     * @author  mcdog
     */
    public Dto querUserPayInfoCount(Dto params)
    {
        return userPayMapper.queryUserPayInfosCount(params);
    }

    /**
     * 线下人工转账成功确认
     * @param params
     * @return
     * @throws Exception
     */
    public int manualPaySuccess(Dto params) throws Exception {
        List<UserPay> userPayList = userPayMapper.queryUserPays(params);
        if(StringUtil.isEmpty(userPayList) || userPayList.size() > 1) {//数据有误
            return 1;//默认成功
        }
        UserPay userPay = userPayList.get(0);
        //不是提现流水||状态已经为处理成功
        if(userPay.getPayType() != PayConstants.PAY_TYPE_ENCHASHMENT || userPay.getStatus() == PayConstants.PAYORDER_STATUS_SUCCESS) {
            return 1;//默认成功
        }
        //更新订单
        Dto updateParams = new BaseDto();
        updateParams.put("id", userPay.getId());//设置商户订单号
        updateParams.put("status", PayConstants.PAYORDER_STATUS_SUCCESS);//设置订单状态为处理成功
        updateParams.put("channelCode", PayConstants.CHANNEL_CODE_OUT_RENGONGPAY);//设置渠道编号
        updateParams.put("channelDesc", "线下人工转账");//设置渠道描述
        updateParams.put("remark", "处理成功");//设置处理结果备注
        int count = userPayMapper.updateUserPay(updateParams);
        logger.info("[线下人工转账后台确认][付款成功]订单更新" + (count > 0? "成功" : "失败") + "!" + ",订单号=" + userPay.getPayId()
                + ",订单所属用户编号=" + params.getAsLong("id"));
        if(count > 0)
        {
            //如果订单的状态为处理失败
            if(userPay.getStatus() == PayConstants.PAYORDER_STATUS_FAILURE)
            {
                //更新用户账户信息
                updateParams.clear();
                updateParams = new BaseDto("userId",userPay.getUserId());
                updateParams.put("tbalance",-userPay.getMoney());//余额减去订单的金额
                updateParams.put("twithDraw",userPay.getMoney());//累计提现金额加上订单金额
                updateParams.put("offsetWithDraw",-userPay.getMoney());//可提现金额减去订单金额
                count = userAccountMapper.updateUserAccount(updateParams);//更新用户账户信息
                logger.info("[线下人工转账后台确认][付款成功]用户账户更新" + (count > 0? "成功" : "失败") + ",订单号=" + userPay.getPayId() + ",订单所属用户编号=" + userPay.getUserId());
            }
            else
            {
                //更新用户账户信息
                updateParams.clear();
                updateParams = new BaseDto("userId",userPay.getUserId());
                updateParams.put("offsetFrozen",-userPay.getMoney());//冻结金额减去订单的金额
                count = userAccountMapper.updateUserAccount(updateParams);//更新用户账户信息
                logger.info("[线下人工转账后台确认][付款成功]用户账户更新" + (count > 0? "成功" : "失败") + ",订单号=" + userPay.getPayId()
                        + ",订单所属用户编号=" + userPay.getUserId());
            }
            //更新账户流水状态为有效(已完成)
            if(count > 0)
            {
                updateParams.put("businessId", userPay.getPayId());//设置流水关联的订单号
                updateParams.put("channelCode", PayConstants.CHANNEL_CODE_OUT_RENGONGPAY);//设置渠道编号
                updateParams.put("channelDesc", "线下人工转账");//设置渠道描述
                updateParams.put("status",1);//设置流水状态为有效(已完成)
                updateParams.put("remark",userPay.getPayId());//设置备注
                count = userDetailMapper.updateUserDetailOfTx(updateParams);
                logger.info("[线下人工转账后台确认][付款成功]用户账户流水更新" + (count > 0? "成功" : "失败") + ",订单号=" + userPay.getPayId()
                        + ",订单所属用户编号=" + userPay.getUserId());
            }
        }
        else
        {
            logger.info("[线下人工转账后台确认][更新失败]订单状态更新失败!" + "订单所属用户编号=" + userPay.getUserId() + ",订单号=" + userPay.getPayId()
                    + ",订单金额=" + userPay.getMoney());
        }
        return count;
    }
}
