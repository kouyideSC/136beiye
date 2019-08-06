package com.caipiao.taskcenter.channel;

import com.caipiao.common.constants.KeyConstants;
import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.encrypt.MD5;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.util.HttpClientUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.common.util.TokenUtil;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.common.Channel;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.service.common.ChannelService;
import com.caipiao.service.exception.ServiceException;
import com.caipiao.service.scheme.SchemeService;
import com.caipiao.service.ticket.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 向商户发送出票通知相关任务
 * Created by Kouyi on 2018/3/20.
 */
@Component("channelTask")
public class ChannelTask {
    private static Logger logger = LoggerFactory.getLogger(ChannelTask.class);
    @Autowired
    private SchemeService schemeService;
    @Autowired
    private ChannelService channelService;

    /**
     * 商户出票通知
     * @author kouyi
     */
    public void channelOutTicketNotify() {
        try {
            Channel channel = new Channel();
            channel.setNotifyStatus(1);
            channel.setStatus(1);
            List<Channel> channelList = channelService.queryChannelList(channel);
            if(StringUtil.isEmpty(channelList)) {
                return;
            }
            for(Channel cl : channelList) {
                //不再有效时间内<开始时间
                if(StringUtil.isNotEmpty(channel.getBeginTime()) && channel.getBeginTime().getTime() > new Date().getTime()) {
                    continue;
                }
                //不再有效时间内>结束时间
                if(StringUtil.isNotEmpty(channel.getEndTime()) && channel.getEndTime().getTime() < new Date().getTime()) {
                    continue;
                }
                if(StringUtil.isEmpty(cl.getNotifyUrl())) {
                    continue;
                }
                List<Dto> schemeList = schemeService.queryChannelSchemeToNotify(cl.getChannelCode());
                if(StringUtil.isEmpty(schemeList)) {
                    continue;
                }
                //进行通知
                BaseDto dto = new BaseDto();
                for(Dto scheme : schemeList) {
                    Long id = scheme.getAsLong("id");
                    Integer number = scheme.getAsInteger("channelNotifyNumber");
                    try {
                        scheme.remove("id");
                        scheme.remove("channelNotifyNumber");
                        String jsonScheme = JsonUtil.JsonObject(scheme);
                        dto.put("body", jsonScheme);
                        dto.put("sign", MD5.md5(cl.getChannelCode() + jsonScheme + cl.getAuthKey()));
                        logger.info("[向商户("+cl.getChannelName()+")推送出票通知] 推送内容如下\n" + dto.toString());
                        String result = HttpClientUtil.callHttpPost_Dto(cl.getNotifyUrl(), dto);
                        if(StringUtil.isNotEmpty(result) && result.equals("ok")) {
                            number = 99;//商户处理成功
                        } else {
                            number ++;//继续通知-最高3次
                        }
                    } catch(Exception e) {
                        logger.error("[向商户("+cl.getChannelName()+")出票通知异常] 通知内容如下\n" + dto.toString(), e);
                        number ++;
                    }
                    dto.clear();
                    logger.info("[向商户("+cl.getChannelName()+")推送出票通知] 通知结果 订单号=" + scheme.getAsString("schemeOrderId") + " 推送次数=" + number);
                    schemeService.updateSchemeNotifyNumber(id, number);//更新通知次数
                }
            }
        } catch (Exception e) {
            logger.error("[商户出票通知] 异常", e);
        }
    }
}
