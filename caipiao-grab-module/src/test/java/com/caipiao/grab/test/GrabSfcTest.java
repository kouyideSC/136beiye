package com.caipiao.grab.test;

import com.caipiao.common.util.DateUtil;
import com.caipiao.domain.common.Task;
import com.caipiao.grab.jc.task.JclqMatchTask;
import com.caipiao.grab.jc.task.JczqMatchTask;
import com.caipiao.grab.zucai.task.SfcPeriodTask;
import com.caipiao.service.common.TaskService;
import com.caipiao.service.exception.ServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

/**
 * 抓取胜负彩数据测试类
 * Created by Kouyi on 2017/11/6.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring/spring-*.xml" })
public class GrabSfcTest {
    @Autowired
    private SfcPeriodTask sfcPeriodTask;
    @Autowired
    private TaskService taskService;

    @Test
    public void testGrabSfcData(){
        sfcPeriodTask.grabGwSfcMatch();//对阵
    }

}
