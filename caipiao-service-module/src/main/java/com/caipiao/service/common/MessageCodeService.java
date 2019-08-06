package com.caipiao.service.common;

import com.caipiao.common.constants.Constants;
import com.caipiao.common.sms.QiyexinshiSms;
import com.caipiao.common.user.UserUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.common.MessageCodeMapper;
import com.caipiao.dao.user.UserMapper;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.common.MessageCode;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.user.User;
import com.caipiao.memcache.MemCached;
import com.caipiao.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 消息相关业务处理服务
 * Created by kouyi on 2017/10/25.
 */
@Service("messageService")
public class MessageCodeService {
    private static Logger logger = LoggerFactory.getLogger(MessageCodeService.class);
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MessageCodeMapper messageDao;
    @Autowired
    private MemCached memcache;

    /**
     * 生成消息业务方法-注册验证码
     * @param message
     * @return
     */
    public void saveMessageCode(MessageCode message, ResultBean result) throws ServiceException {
        try {
            //手机号验证
            if(!UserUtils.checkMobile(message.getMobile())) {
                result.setErrorCode(ErrorCode_API.ERROR_USER_110001);
                return;
            }
            //手机号是否已存在
            User user = userMapper.queryUserInfoByMobile(message.getMobile());
            if(StringUtil.isNotEmpty(user)) {
                result.setErrorCode(ErrorCode_API.ERROR_USER_110002);
                return;
            }

            String key = Constants.AUTHCODE + message.getMobile();
            if(memcache.contains(key)) {//一分钟限制发送1次
                result.setErrorCode((ErrorCode_API.ERR_USER_110009));
                return;
            }
            memcache.set(key, message.getContent(), 60);//验证码缓存1分钟

            //立即发送=未设置发送时间或发送时间小于等于当前时间[如果当前发送失败,则后续定时任务继续发送]
            message.setState(0);//默认未发送
            message.setTryNumber(0);
            if(StringUtil.isEmpty(message.getBeginTime()) || (StringUtil.isNotEmpty(message.getBeginTime()) && message.getBeginTime().getTime() < new Date().getTime())) {
                QiyexinshiSms.smsSend(message);//发送
                if(!QiyexinshiSms.isReSend(message.getSendCode())) {
                   message.setState(1);//后续任务不再发送
                }
                message.setTryNumber(1);
            }

            MessageCode code = messageDao.queryMessageCode(message);
            if(StringUtil.isEmpty(code)) {
                messageDao.insertMessageCode(message);
            } else {
                message.setId(code.getId());
                messageDao.updateMessageCode(message);
            }
        } catch (Exception e) {
            logger.error("[生成注册验证码消息异常] mobile=" + message.getMobile() + " errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 生成消息业务方法-找回密码验证码
     * @param message
     * @return
     */
    public void backPasswordAuthCode(MessageCode message, ResultBean result) throws ServiceException {
        try {
            //手机号验证
            if(!UserUtils.checkMobile(message.getMobile())) {
                result.setErrorCode(ErrorCode_API.ERROR_USER_110001);
                return;
            }
            //手机号是否存在
            User userInfo = userMapper.queryUserInfoByMobile(message.getMobile());
            if(StringUtil.isEmpty(userInfo)) {
                result.setErrorCode(ErrorCode_API.ERROR_USER_110011);
                return;
            }
            if (userInfo.getStatus().intValue() != 1) {//用户状态
                result.setErrorCode(ErrorCode_API.ERROR_USER_110006);
                return;
            }
            message.setUserId(userInfo.getId());

            String key = Constants.BACKPWD_AUTHCODE + message.getMobile();
            if(memcache.contains(key)) {//一分钟限制发送1次
                result.setErrorCode((ErrorCode_API.ERR_USER_110009));
                return;
            }
            memcache.set(key, message.getContent(), 60);//验证码缓存1分钟

            //立即发送=未设置发送时间或发送时间小于等于当前时间[如果当前发送失败,则后续定时任务继续发送]
            message.setState(0);//默认未发送
            message.setTryNumber(0);
            if(StringUtil.isEmpty(message.getBeginTime()) || (StringUtil.isNotEmpty(message.getBeginTime()) && message.getBeginTime().getTime() < new Date().getTime())) {
                QiyexinshiSms.smsSend(message);//发送
                if(!QiyexinshiSms.isReSend(message.getSendCode())) {
                    message.setState(1);//后续任务不再发送
                }
                message.setTryNumber(1);
            }

            MessageCode code = messageDao.queryMessageCode(message);
            if(StringUtil.isEmpty(code)) {
                messageDao.insertMessageCode(message);
            } else {
                message.setId(code.getId());
                messageDao.updateMessageCode(message);
            }
        } catch (Exception e) {
            logger.error("[生成找回密码消息异常] mobile=" + message.getMobile() + " errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 发送并保存实名认证验证码
     * @author  mcdog
     * @param   message     验证码对象
     * @param   result      处理结果
     */
    public void identityRzAuthCode(MessageCode message, ResultBean result) throws ServiceException
    {
        try
        {
            //查询用户
            User userInfo = userMapper.queryUserInfoById(message.getUserId());
            if(userInfo == null)
            {
                logger.error("[发送并保持实名认证验证码]用户不存在!用户编号=" + message.getUserId());
                throw new ServiceException(ErrorCode_API.SUCCESS);
            }
            //验证手机号是否存在
            if(StringUtil.isEmpty(userInfo.getMobile()))
            {
                logger.error("[发送并保持实名认证验证码]用户手机号尚未绑定!用户编号=" + message.getUserId());
                result.setErrorCode(ErrorCode_API.ERROR_USER_110011);
                return;
            }
            //验证用户状态
            if (userInfo.getStatus().intValue() != 1) {
                result.setErrorCode(ErrorCode_API.ERROR_USER_110006);
                return;
            }
            //验证发送频率(一分钟内只能发送一次)
            String key = Constants.IDENTITYRZ_AUTHCODE + userInfo.getMobile();
            if(memcache.contains(key))
            {
                result.setErrorCode((ErrorCode_API.ERR_USER_110009));
                return;
            }
            memcache.set(key, message.getContent(), 60);//验证码缓存1分钟

            //发送验证码
            message.setMobile(userInfo.getMobile());//设置接收手机号
            message.setUserId(userInfo.getId());//设置验证码所属用户编号
            message.setState(0);//设置验证码发送状态
            message.setTryNumber(1);
            QiyexinshiSms.smsSend(message);//发送验证码
            if(!QiyexinshiSms.isReSend(message.getSendCode()))
            {
                message.setState(1);//验证码发送成功,则后续任务不再发送
            }

            //保存/更新验证码,如果验证码已存在,则更新,否则新增
            MessageCode code = messageDao.queryMessageCode(message);
            if(StringUtil.isEmpty(code))
            {
                messageDao.insertMessageCode(message);
            }
            else
            {
                message.setId(code.getId());
                messageDao.updateMessageCode(message);
            }
            result.setErrorCode(ErrorCode_API.SUCCESS);
            result.setErrorDesc("验证码发送成功");
        }
        catch (Exception e)
        {
            logger.error("[发送并保持实名认证验证码]发送异常!用户编号=" + message.getUserId() + ",异常信息=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 短消息自动发送任务
     * @return
     */
    public void sendMessageCodeTask() throws ServiceException {
        try {
            List<MessageCode> messageCodeList = messageDao.queryNoSendMessageCode();
            if(StringUtil.isEmpty(messageCodeList)) {
                return;
            }
            for(MessageCode code : messageCodeList) {
                if(StringUtil.isNotEmpty(code.getBeginTime()) && code.getBeginTime().getTime() > new Date().getTime()) {
                    continue;
                }
                if(StringUtil.isEmpty(code.getTryNumber())) {
                    code.setTryNumber(0);
                }
                code.setTryNumber(code.getTryNumber() + 1);//尝试发送次数
                QiyexinshiSms.smsSend(code);//发送
                if(!QiyexinshiSms.isReSend(code.getSendCode())) {//成功
                    code.setState(1);
                }
                messageDao.updateMessageCode(code);
            }
        } catch (Exception e) {
            logger.error("[短消息自动发送任务异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 发送并保存联合登录-绑定用户信息验证码
     * @author  mcdog
     * @param   message     验证码对象
     * @param   result      处理结果
     */
    public void unionBindUserInfoAuthCode(MessageCode message, ResultBean result) throws ServiceException
    {
        try
        {
            //验证手机号是否为空
            if(StringUtil.isEmpty(message.getMobile()))
            {
                logger.error("[发送并保存联合登录-绑定用户信息验证码]手机号不能为空!手机号=" + message.getMobile());
                result.setErrorCode(ErrorCode_API.ERROR_USER_110001);
                return;
            }
            //验证发送频率(一分钟内只能发送一次)
            String key = Constants.UNION_BINDUSERINFO_AUTHCODE + message.getMobile();
            if(memcache.contains(key))
            {
                result.setErrorCode((ErrorCode_API.ERR_USER_110009));
                return;
            }
            memcache.set(key, message.getContent(), 60);//验证码缓存1分钟

            //发送验证码
            message.setState(0);//设置验证码发送状态
            message.setTryNumber(1);
            QiyexinshiSms.smsSend(message);//发送验证码
            if(!QiyexinshiSms.isReSend(message.getSendCode()))
            {
                message.setState(1);//验证码发送成功,则后续任务不再发送
            }

            //保存/更新验证码,如果验证码已存在,则更新,否则新增
            MessageCode code = messageDao.queryMessageCode(message);
            if(StringUtil.isEmpty(code))
            {
                messageDao.insertMessageCode(message);
            }
            else
            {
                message.setId(code.getId());
                messageDao.updateMessageCode(message);
            }
            result.setErrorCode(ErrorCode_API.SUCCESS);
            result.setErrorDesc("验证码发送成功");
        }
        catch (Exception e)
        {
            logger.error("[发送并保存联合登录-绑定用户信息验证码]发送异常!手机号=" + message.getMobile() + ",异常信息=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }
}
