package com.caipiao.taskcenter.Test;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.constants.UserConstants;
import com.caipiao.common.util.CalculationUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.common.ActivityAddBonus;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.domain.scheme.SchemeMatches;
import com.caipiao.domain.user.User;
import com.caipiao.domain.user.UserAddBonusDetail;
import com.caipiao.service.common.ActivityService;
import com.caipiao.service.scheme.SchemeService;
import com.caipiao.service.user.UserService;
import com.caipiao.taskcenter.award.util.LoggerUtil;
import com.caipiao.taskcenter.award.util.PrizesUtil;
import com.caipiao.taskcenter.channel.ChannelTask;
import com.caipiao.taskcenter.check.CheckTask;
import com.caipiao.taskcenter.common.SmsTask;
import com.caipiao.taskcenter.pay.QueryDaiFuOrderTask;
import com.caipiao.taskcenter.pay.SendPaymentRequestTask;
import com.caipiao.taskcenter.ticket.SchemeTask;
import com.caipiao.taskcenter.ticket.TicketTask;
import com.caipiao.taskcenter.user.UserRebateTask;
import com.caipiao.taskcenter.user.UserSellTask;
import com.caipiao.taskcenter.user.UserTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 执行抓取开奖号码任务测试类
 * Created by Kouyi on 2017/11/11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring/spring-*.xml" })
public class TaskTest {
    @Autowired
    private TicketTask ticketTask;
    @Autowired
    private SchemeTask schemeTask;
    @Autowired
    private UserTask userTask;
    @Autowired
    private UserRebateTask userRebateTask;
    @Autowired
    private SmsTask smsTask;
    @Autowired
    private CheckTask checkTask;
    @Autowired
    private ChannelTask channelTask;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private UserSellTask userSellTask;
    @Autowired
    private SchemeService schemeService;
    @Autowired
    private UserService userService;
    @Autowired
    private SendPaymentRequestTask sendPaymentRequestTask;
    @Autowired
    private QueryDaiFuOrderTask queryDaiFuOrderTask;


    @Test
    public void testCreateFileData() throws Exception {
        //ticketTask.insideOutTicketStatus();
        //schemeTask.schemeOutTicketStatus();
        //userTask.userDayDateStatis();
        //userRebateTask.userRebateHandle();
        //smsTask.autoSmsSendTask();
        //checkTask.autoPlatFormCapitalData();
        //checkTask.autoOrderAndTicketData();
        //checkTask.autoVoteAndSitePrizeData();
        //checkTask.autoUserRebateData();
        //channelTask.channelOutTicketNotify();
        //userSellTask.userSellMoneyCommissionStatis();
        //checkTask.autoYesterdayTestTicketPrizeMoneyData();
        //sendPaymentRequestTask.sendPaymentRequest();
        //queryDaiFuOrderTask.doTask();
        //userTask.userFollowWeekDateStatis();
        //userTask.userFollowMonthDateStatis();
    }
}
