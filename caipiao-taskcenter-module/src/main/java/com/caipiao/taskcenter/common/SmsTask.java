package com.caipiao.taskcenter.common;

import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.common.MessageCode;
import com.caipiao.service.common.MessageCodeService;
import com.caipiao.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 短消息自动发送任务
 * Created by Kouyi on 2018/2/2
 */
@Component("smsTask")
public class SmsTask {
    private static Logger logger = LoggerFactory.getLogger(SmsTask.class);
    @Autowired
    private MessageCodeService messageCodeService;

    /**
     * 发送短消息任务
     * @author kouyi
     */
    public void autoSmsSendTask() {
        try {
            messageCodeService.sendMessageCodeTask();
        } catch (Exception e) {
            logger.error("[发送短消息任务] 异常", e);
        }
    }

}
