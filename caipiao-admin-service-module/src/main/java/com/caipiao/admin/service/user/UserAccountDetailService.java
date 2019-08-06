package com.caipiao.admin.service.user;

import com.caipiao.common.constants.PayConstants;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.user.UserAccountMapper;
import com.caipiao.dao.user.UserDetailMapper;
import com.caipiao.dao.user.UserPayMapper;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.user.UserAccount;
import com.caipiao.domain.user.UserDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

/**
 * 用户账户流水-服务类
 */
@Service("userDetailService")
@Transactional
public class UserAccountDetailService
{
    private static final Logger logger = LoggerFactory.getLogger(UserAccountDetailService.class);

    @Autowired
    private UserDetailMapper userDetailMapper;

    @Autowired
    private UserAccountMapper userAccountMapper;

    /**
     * 查询用户账户流水
     * @author  mcdog
     */
    public List<Dto> queryUserAccountDetailInfo(Dto params)
    {
        return userDetailMapper.queryUserAccountDetailInfo(params);
    }

    /**
     * 查询用户账户流水总记录条数
     * @author  mcdog
     */
    public int queryUserAccountDetailCount(Dto params)
    {
        return userDetailMapper.queryUserAccountDetailCount(params);
    }

    /**
     * 管理后台加款/扣款
     * @author  mcdog
     */
    public synchronized int updateUserAccountForJkAndKk(Dto params) throws Exception
    {
        /**
         * 参数校验
         */
        //校验非空
        if(StringUtil.isEmpty(params.get("inType"))
                || StringUtil.isEmpty(params.get("userId"))
                || StringUtil.isEmpty(params.get("money")))
        {
            logger.error("[管理后台加款/扣款]参数校验不通过!缺少必要参数");
            params.put("dmsg","缺少必要参数");
            return 0;
        }
        //校验金额
        double money = params.getAsDouble("money");
        if(money <= 0)
        {
            logger.error("[管理后台加款/扣款]参数校验不通过!金额不合法");
            params.put("dmsg","金额必须大于0");
            return 0;
        }
        //校验账户变更类型
        int inType = params.getAsInteger("inType");//账户变更类型
        if(PayConstants.PAY_TYPE_RECHARGE != inType && PayConstants.PAY_TYPE_ENCHASHMENT != inType)
        {
            logger.error("[管理后台加款/扣款]参数校验不通过!账户变更类型错误");
            params.put("dmsg","账户变更类型错误!");
            return 0;
        }

        /**
         * 更新用户账户余额并添加账户流水
         */
        //更新用户账户余额等信息
        long userId = params.getAsLong("userId");
        UserAccount beforeUserAccount = userAccountMapper.queryUserAccountInfoByUserId(userId);//查询用户账户更新前信息
        UserAccount afterUserAccount = beforeUserAccount;//用户账户更新后信息
        Calendar current = Calendar.getInstance();
        Dto updateUserAccount = new BaseDto("userId",userId);

        //如果账户变更类型为加款,且提现方式为全部可提现时,同步更新用户账户的可提现金额
        boolean iskk = false;//是否为扣款
        if(PayConstants.PAY_TYPE_RECHARGE == inType)
        {
            params.put("channelCode",PayConstants.CHANNEL_CODE_IN_SYSTEM);//设置业务渠道为管理后台加款
            String txtype = params.getAsString("txtype");
            if("0".equals(txtype))
            {
                updateUserAccount.put("offsetWithDraw",money);//设置可提现金额变更(可提现金额 + 本次加款金额)
            }
            else if("1".equals(txtype))
            {
                updateUserAccount.put("offsetUnWithDraw",money);//设置不可提现金额变更(不可提现金额 + 本次加款金额)
            }
        }
        //如果账户变更类型为扣款,则同步更新不可提现金额(先扣不可提现金额,不可提现金额不够扣的时候再去扣可提现金额)
        else if(PayConstants.PAY_TYPE_ENCHASHMENT == inType)
        {
            iskk = true;
            params.put("channelCode",PayConstants.CHANNEL_CODE_OUT_SYSTEM);//设置业务渠道为管理后台扣款
            double czmoney = beforeUserAccount.getUnWithDraw() - money;//计算不可提现与要扣款的差值
            updateUserAccount.put("offsetUnWithDraw",-(czmoney >= 0? money : beforeUserAccount.getUnWithDraw()));//设置不可提现金额变更
            if(czmoney < 0)
            {
                updateUserAccount.put("offsetWithDraw",czmoney);//设置可提现金额变更
            }
        }
        updateUserAccount.put("tbalance",iskk? -money : money);//设置余额变更
        int result = userAccountMapper.updateUserAccount(updateUserAccount);
        if(result > 0)
        {
            //添加账户流水
            logger.info("[管理后台加款/扣款]用户账户变更成功,操作人=" + params.getAsString("current_login_personal") + ",变更用户编号=" + userId);
            afterUserAccount = userAccountMapper.queryUserAccountInfoByUserId(userId);//查询更新后的用户账户信息
            UserDetail userDetail = new UserDetail();
            userDetail.setUserId(userId);//设置账户id
            userDetail.setInType(PayConstants.PAY_TYPE_RECHARGE == inType? false : true);//设置流水类型
            userDetail.setChannelCode(params.getAsInteger("channelCode"));//设置业务渠道
            userDetail.setChannelDesc(PayConstants.channelCodeMap.get(userDetail.getChannelCode()));//设置业务渠道描述
            userDetail.setMoney(Math.abs(money));//交易金额
            userDetail.setLastBalance(beforeUserAccount.getBalance());//交易前账户余额
            userDetail.setBalance(afterUserAccount.getBalance());//交易后账户余额
            userDetail.setLastWithDraw(beforeUserAccount.getWithDraw());//交易前账户可提现金额
            userDetail.setWithDraw(afterUserAccount.getWithDraw());//交易后账户可提现金额
            userDetail.setLastUnWithDraw(beforeUserAccount.getUnWithDraw());//交易前账户不可提现金额
            userDetail.setUnWithDraw(afterUserAccount.getUnWithDraw());//交易后账户不可提现金额
            userDetail.setClientFrom(0);//设置客户端来源
            userDetail.setRemark(params.getAsString("remark"));//设置备注
            userDetail.setCreateTime(current.getTime());//流水时间
            result = userDetailMapper.insertUserDetail(userDetail);//添加流水
            if(result > 0)
            {
                logger.info("[管理后台加款/扣款]账户流水添加成功!操作人=" + params.getAsString("current_login_personal") + ",变更用户编号=" + userId);
            }
            else
            {
                logger.error("[管理后台加款/扣款]账户流水添加失败!操作人=" + params.getAsString("current_login_personal") + ",变更用户编号=" + userId);
            }
        }
        else
        {
            logger.error("[管理后台加款/扣款]用户账户变更失败!操作人=" + params.getAsString("current_login_personal") + ",变更用户编号=" + userId);
        }
        return result;
    }
}
