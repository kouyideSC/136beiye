package com.caipiao.admin.service.task;

import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.common.TaskMapper;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.common.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 任务表相关业务处理服务
 */
@Service("taskService")
public class TaskService
{
    private static Logger logger = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    private TaskMapper taskMapper;

    /**
     * 更新任务
     * @param   taskName    任务名称(在任务表中任务的key)
     * @param   spendTime   任务耗时(单位:秒)
     */
    public void updateTask(String taskName, Integer spendTime) throws Exception
    {
        try
        {
            if(StringUtil.isEmpty(taskName))
            {
                return;
            }
            if(StringUtil.isEmpty(spendTime))
            {
                spendTime = 0;
            }
            taskMapper.updateTask(taskName, spendTime);
        }
        catch (Exception e)
        {
            logger.error("[更新task任务异常] errorDesc=" + e.getMessage());
            throw e;
        }
    }

    /**
     * 插入新任务
     * @param   task
     */
    public void saveTask(Task task) throws Exception
    {
        try
        {
            if(StringUtil.isEmpty(task))
            {
                return;
            }
            if(StringUtil.isEmpty(task.getBeginTime()))
            {
                task.setBeginTime(new Date());
            }
            taskMapper.insertTask(task);
        }
        catch (Exception e)
        {
            logger.error("[插入task任务异常] errorDesc=" + e.getMessage());
            throw e;
        }
    }

    /**
     * 查询未执行的任务
     * @param   taskName    任务名称(在任务表中任务的key)
     */
    public boolean isHasTask(String taskName) throws Exception
    {
        try
        {
            if(StringUtil.isEmpty(taskName))
            {
                return false;
            }
            Task tk = taskMapper.queryTaskInfo(taskName);
            if(StringUtil.isEmpty(tk))
            {
                return false;
            }
            return true;
        }
        catch (Exception e)
        {
            logger.error("[查询task任务异常] errorDesc=" + e.getMessage());
            throw e;
        }
    }
}