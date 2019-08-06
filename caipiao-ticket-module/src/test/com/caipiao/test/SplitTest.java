package com.caipiao.test;

import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.service.ticket.TicketService;
import com.caipiao.ticket.bean.SpInfo;
import com.caipiao.ticket.split.SplitTicket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * 拆票测试类
 * Created by Kouyi on 2017/12/11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring/spring-*.xml" })
public class SplitTest {
    @Autowired
    private SplitTicket splitTicket;

    @Test
    public void testCreateFileData() {
        splitTicket.orderSplitTicket();
    }
}
