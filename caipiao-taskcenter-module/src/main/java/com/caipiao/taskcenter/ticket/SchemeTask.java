package com.caipiao.taskcenter.ticket;

import com.caipiao.common.constants.SchemeConstants;
import com.caipiao.common.constants.UserConstants;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.service.common.ActivityService;
import com.caipiao.service.scheme.SchemeService;
import com.caipiao.service.ticket.TicketService;
import com.caipiao.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户方案相关任务
 * Created by Kouyi on 2018/1/3.
 */
@Component("userSchemeTask")
public class SchemeTask {
    private static Logger logger = LoggerFactory.getLogger(SchemeTask.class);
    @Autowired
    private SchemeService schemeService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private UserService userService;
    @Autowired
    private ActivityService activityService;

    /**
     * 方案预约状态变更
     * @author kouyi
     */
    public void schemeOutTicketStatus() {
        try {
            List<Scheme> schemeList = schemeService.queryOutTicketingSchemeList();
            if(StringUtil.isEmpty(schemeList)) {
                return;
            }
            for(Scheme scheme : schemeList) {
                List<SchemeTicket> tickets = ticketService.queryTicketListBySchemeId(scheme.getSchemeOrderId());
                if (StringUtil.isEmpty(tickets)) {
                    continue;
                }

                int schemeStatus = 0;
                for (SchemeTicket ticket : tickets) {
                    if (ticket.getTicketStatus() > SchemeConstants.TICKET_STATUS_CAST) {//出票成功
                        schemeStatus++;
                    }
                    if (ticket.getTicketStatus() == SchemeConstants.TICKET_STATUS_FAIL || ticket.getTicketStatus() == SchemeConstants.TICKET_STATUS_CANCEL) {
                        schemeStatus = -99;//出票失败
                        break;
                    }
                    if (ticket.getTicketStatus() == SchemeConstants.TICKET_STATUS_WAITING || ticket.getTicketStatus() == SchemeConstants.TICKET_STATUS_CAST) {
                        schemeStatus = -199;//出票中
                        break;
                    }
                }

                if(schemeStatus == -199) {//出票中不处理
                    continue;
                }
                if (schemeStatus == tickets.size()) {//出票成功
                    scheme.setSchemeStatus(SchemeConstants.SCHEME_STATUS_CPCG);
                    //判断方案是否需要返利(普通用户中的普通会员和代理员&&没有参与加奖活动=返点)
                    if((scheme.getClientSource() == UserConstants.USER_PROXY_GENERAL
                            || scheme.getClientSource() == UserConstants.USER_STATUS_AGENT)
                            && (scheme.getChannelCode() == UserConstants.USER_TYPE_GENERAL
                            || scheme.getChannelCode() == UserConstants.USER_TYPE_VIRTUAL)
                            && activityService.isUserJoinActivityByUserId(scheme)==0) {
                        scheme.setBackStatus(1);
                    }
                }
                if (schemeStatus == -99) {//出票失败待撤单
                    scheme.setSchemeStatus(SchemeConstants.SCHEME_STATUS_TKF);
                }
                scheme.setSchemeStatusDesc(SchemeConstants.schemeStatusMap.get(scheme.getSchemeStatus()));
                int row = schemeService.updateSchemeTicketStatus(scheme);
                if(row > 0) {
                    logger.info("[方案预约状态变更] 方案号=" + scheme.getSchemeOrderId() + " 状态变更成功 出票结果=" + SchemeConstants.schemeStatusMap.get(scheme.getSchemeStatus()));
                    //更新用户累计消费
                    if(scheme.getSchemeStatus() == SchemeConstants.SCHEME_STATUS_CPCG) {//出票成功记录累计消费
                        userService.updateUserConsume(scheme.getSchemeUserId(), scheme.getSchemeMoney());
                        //出票成功-跟单表-订单状态=出票成功
                        schemeService.updateSchemeStatusFollow(scheme.getSchemeOrderId(), 1);
                    }
                } else {
                    logger.error("[方案预约状态变更] 方案号=" + scheme.getSchemeOrderId() + " 数据库状态变更失败 出票结果=" + SchemeConstants.schemeStatusMap.get(scheme.getSchemeStatus()));
                }
            }
        } catch (Exception e) {
            logger.error("[方案预约状态变更] 异常", e);
        }
    }
}
