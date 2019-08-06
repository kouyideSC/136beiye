package com.caipiao.service.test;

import com.caipiao.admin.service.ticket.TicketService;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.dao.match.MatchBasketBallSpMapper;
import com.caipiao.dao.match.MatchFootBallSpMapper;
import com.caipiao.dao.ticket.TicketMapper;
import com.caipiao.dao.ticket.TicketVoteMapper;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.base.SchemeBean;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.jjyh.JjyhTwo;
import com.caipiao.memcache.MemCached;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Kouyi on 2017/11/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring/spring-*.xml" })
public class SchemeTest {
    @Autowired
    private TicketService ticketService;

    @Test
    public void testSchemeJJYH() {
        try {
            String codes = "RFSF|20180715302=3,20180715303=0,20180715305=3|3*1";
            //System.out.println(ticketService.settingTzContentSpAndLose("1710", codes));
            //20180715301->DXF=0&167.5@1.75,20180715302->DXF=0&165.5@1.75,20180715303->DXF=0&162.5@1.75,20180715305->SF=3@1.35
            //20180715301->DXF=0&167.5@1.75,20180715302->DXF=0&165.5@1.75,20180715303->DXF=0&162.5@1.75,20180715305->SF=3@1.35
            Dto ticket = new BaseDto();
            ticket.put("lotteryId", "1700");
            ticket.put("codes", "HH|20181102001>RQSPF=0,20181102002>SPF=1|2*1");
            ticket.put("schemeId", "JC201811021347022715291");
            Dto params = new BaseDto();
            ticketService.settingSchemeDetailForTicket(ticket, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
