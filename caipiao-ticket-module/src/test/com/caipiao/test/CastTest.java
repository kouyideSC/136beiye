package com.caipiao.test;

import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.service.ticket.TicketService;
import com.caipiao.ticket.vote.huayang.HuaYangCastTicketTask;
import com.caipiao.ticket.vote.huaying.HuaYingCastTicketTask;
import com.caipiao.ticket.vote.jimi.JiMiCastTicketTask;
import com.caipiao.ticket.vote.nuomi.NuoMiCastTicketTask;
import com.caipiao.ticket.vote.ouke.OuKeCastTicketTask;
import com.mina.rbc.util.xml.JXmlWapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 拆票测试类
 * Created by Kouyi on 2017/12/11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring/spring-*.xml" })
public class CastTest {
    @Autowired
    private NuoMiCastTicketTask nuoMiCastTicketTask;
    @Autowired
    private JiMiCastTicketTask jiMiCastTicketTask;
    @Autowired
    private HuaYangCastTicketTask huaYangCastTicketTask;
    @Autowired
    private HuaYingCastTicketTask huaYingCastTicketTask;
    @Autowired
    private OuKeCastTicketTask ouKeCastTicketTask;

    @Test
    public void testCastTicket() {
        //nuoMiCastTicketTask.jcCastTicketTask();
        //nuoMiCastTicketTask.jcQueryTicketTask();
        //nuoMiCastTicketTask.jcAwardTicketTask();
        //jiMiCastTicketTask.jcCastTicketTask();
        //jiMiCastTicketTask.jcQueryTicketTask();
        //jiMiCastTicketTask.jcAwardTicketTask();
        //jiMiCastTicketTask.mpCastTicketTask();
        //jiMiCastTicketTask.mpQueryTicketTask();
        //jiMiCastTicketTask.mpAwardTicketTask();
        //huaYangCastTicketTask.mpCastTicketTask();
        //huaYangCastTicketTask.mpQueryTicketTask();
        //huaYangCastTicketTask.jcCastTicketTask();
        //huaYangCastTicketTask.jcQueryTicketTask();
        //huaYangCastTicketTask.jcAwardTicketTask();
        //huaYangCastTicketTask.mpAwardTicketTask();
        //huaYangCastTicketTask.kpCastTicketTask();
        //huaYangCastTicketTask.kpQueryTicketTask();
        //huaYangCastTicketTask.kpAwardTicketTask();
        //huaYingCastTicketTask.queryAccountBalance();
        //ouKeCastTicketTask.jcCastTicketTask();
        ouKeCastTicketTask.jcQueryTicketTask();
        //ouKeCastTicketTask.jcAwardTicketTask();
    }

}
