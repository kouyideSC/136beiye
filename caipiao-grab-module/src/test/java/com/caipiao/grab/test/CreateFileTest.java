package com.caipiao.grab.test;

import com.caipiao.grab.task.CreateUpdateDataTask;
import com.caipiao.grab.zucai.task.SfcPeriodTask;
import com.caipiao.service.common.TaskService;
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
public class CreateFileTest {
    @Autowired
    private CreateUpdateDataTask createUpdateDataTask;

    @Test
    public void testCreateFileData(){
        //createUpdateDataTask.jczqMatchFileTask();
        createUpdateDataTask.gjMatchFileTask();
        createUpdateDataTask.gyjMatchFileTask();
        //createUpdateDataTask.jclqMatchFileTask();
        //createUpdateDataTask.jczqResultFileTask();
        //createUpdateDataTask.jclqResultFileTask();
    }

}
