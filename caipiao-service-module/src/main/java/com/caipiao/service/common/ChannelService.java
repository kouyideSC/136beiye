package com.caipiao.service.common;

import com.caipiao.common.constants.UserConstants;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.admin.account.AccountMapper;
import com.caipiao.dao.common.ChannelMapper;
import com.caipiao.dao.common.TaskMapper;
import com.caipiao.dao.user.UserAccountMapper;
import com.caipiao.dao.user.UserMapper;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.common.Channel;
import com.caipiao.domain.common.Task;
import com.caipiao.domain.user.User;
import com.caipiao.domain.user.UserAccount;
import com.caipiao.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 渠道相关业务处理服务
 * Created by kouyi on 2018/03/16.
 */
@Service("channelService")
public class ChannelService {
    private static Logger logger = LoggerFactory.getLogger(ChannelService.class);
    @Autowired
    private ChannelMapper channelMapper;
    @Autowired
    private UserAccountMapper userAccountMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 检查渠道是否合法-不包括出款账号
     * @param appId
     * @return
     */
    public void isValidChannelNoUser(String appId) throws ServiceException {
        try {
            if(StringUtil.isEmpty(appId)) {
                throw new ServiceException(ErrorCode_API.ERROR_100002);
            }
            Channel channel = channelMapper.queryChannelInfo(appId);
            if(StringUtil.isEmpty(channel) || channel.getStatus() != 1) {//未找到渠道或失效
                throw new ServiceException(ErrorCode_API.ERROR_100002);
            }
            //不再有效时间内<开始时间
            if(StringUtil.isNotEmpty(channel.getBeginTime()) && channel.getBeginTime().getTime() > new Date().getTime()) {
                throw new ServiceException(ErrorCode_API.ERROR_100002);
            }
            //不再有效时间内>结束时间
            if(StringUtil.isNotEmpty(channel.getEndTime()) && channel.getEndTime().getTime() < new Date().getTime()) {
                throw new ServiceException(ErrorCode_API.ERROR_100002);
            }
            //渠道出款账户未设置
            if(StringUtil.isEmpty(channel.getOutAccountUserId())) {
                throw new ServiceException(ErrorCode_API.ERROR_100005);
            }
        } catch (Exception e) {
            logger.error("[检查渠道是否合法-不包括出款账号异常] errorDesc=" + e.getMessage());
            int code = ErrorCode_API.SERVER_ERROR;
            if(e instanceof ServiceException) {
                code = ((ServiceException) e).getErrorCode();
            }
            throw new ServiceException(code, e.getMessage());
        }
    }

    /**
     * 检查渠道以及出款账户是否合法
     * @param appId
     * @return
     */
    public Channel isValidChannel(String appId) throws ServiceException {
        try {
            if(StringUtil.isEmpty(appId)) {
                throw new ServiceException(ErrorCode_API.ERROR_100002);
            }
            Channel channel = channelMapper.queryChannelInfo(appId);
            if(StringUtil.isEmpty(channel) || channel.getStatus() != 1) {//未找到渠道或失效
                throw new ServiceException(ErrorCode_API.ERROR_100002);
            }
            //不再有效时间内<开始时间
            if(StringUtil.isNotEmpty(channel.getBeginTime()) && channel.getBeginTime().getTime() > new Date().getTime()) {
                throw new ServiceException(ErrorCode_API.ERROR_100002);
            }
            //不再有效时间内>结束时间
            if(StringUtil.isNotEmpty(channel.getEndTime()) && channel.getEndTime().getTime() < new Date().getTime()) {
                throw new ServiceException(ErrorCode_API.ERROR_100002);
            }
            //渠道出款账户未设置
            if(StringUtil.isEmpty(channel.getOutAccountUserId())) {
                throw new ServiceException(ErrorCode_API.ERROR_100005);
            }
            User user = userMapper.queryUserInfoById(channel.getOutAccountUserId());
            //用户类型、状态是否正确
            if(StringUtil.isEmpty(user) || user.getUserType() != UserConstants.USER_TYPE_OUTMONEY || user.getStatus() != UserConstants.USER_STATUS_TRUE) {
                throw new ServiceException(ErrorCode_API.ERROR_100005);
            }
            //账户信息
            UserAccount account = userAccountMapper.queryUserAccountInfoByUserId(channel.getOutAccountUserId());
            if (StringUtil.isEmpty(account)) {
                throw new ServiceException(ErrorCode_API.ERROR_USER_110007);
            }
            //渠道账户余额是否充足
            if(account.getBalance() <= 0 && (channel.getOverstepAccount() + account.getBalance()) <= 0) {
                throw new ServiceException(ErrorCode_API.ERROR_100006);
            }
            return channel;
        } catch (Exception e) {
            logger.error("[检查渠道以及出款账户是否合法异常] errorDesc=" + e.getMessage());
            int code = ErrorCode_API.SERVER_ERROR;
            if(e instanceof ServiceException) {
                code = ((ServiceException) e).getErrorCode();
            }
            throw new ServiceException(code, e.getMessage());
        }
    }

    /**
     * 查询渠道列表
     * @return
     */
    public List<Channel> queryChannelList(Channel channel) throws ServiceException {
        try {
            return channelMapper.queryChannelList(channel);
        } catch (Exception e){
            logger.error("[查询渠道列表异常]", e);
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }
}
