package com.caipiao.taskcenter.Test;

import com.caipiao.taskcenter.task.CreateUpdateHistoryPeriodTask;
import com.caipiao.taskcenter.task.CreateUpdateHomeTask;
import com.caipiao.taskcenter.task.CreateUpdatePeriodTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 执行task任务生成文件测试类
 * Created by Kouyi on 2017/11/11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring/spring-*.xml" })
public class TaskFileTest {
    @Autowired
    private CreateUpdatePeriodTask createUpdatePeriodTask;
    @Autowired
    private CreateUpdateHomeTask createUpdateHomeTask;

    @Autowired
    private CreateUpdateHistoryPeriodTask createUpdateHistoryPeriodTask;

    @Test
    public void testCreateFileData(){
        //createUpdatePeriodTask.numberDistoryPeriodFileTask();
        createUpdateHomeTask.homeDataFileTask();
        //createUpdatePeriodTask.createUpdatePeriodFile();
        //createUpdateHistoryPeriodTask.createUpdateHistoryPeriodFile();
    }

}
