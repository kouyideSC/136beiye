package com.caipiao.service.common;

import com.caipiao.common.constants.Constants;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.common.MessageCodeMapper;
import com.caipiao.dao.common.TaskMapper;
import com.caipiao.dao.user.UserMapper;
import com.caipiao.domain.base.ResultBean;
import com.caipiao.domain.code.ErrorCode_API;
import com.caipiao.domain.common.MessageCode;
import com.caipiao.domain.common.Task;
import com.caipiao.memcache.MemCached;
import com.caipiao.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 任务表相关业务处理服务
 * Created by kouyi on 2017/11/10.
 */
@Service("taskService")
public class TaskService {
    private static Logger logger = LoggerFactory.getLogger(TaskService.class);
    @Autowired
    private TaskMapper taskMapper;

    /**
     * 更新任务
     * @param taskName
     * @param spendTime
     * @return
     */
    public void updateTask(String taskName, Integer spendTime) throws ServiceException {
        try {
            if(StringUtil.isEmpty(taskName)) {
                return;
            }
            if(StringUtil.isEmpty(spendTime)) {
                spendTime = 0;
            }
            taskMapper.updateTask(taskName, spendTime);
        } catch (Exception e) {
            logger.error("[更新task任务异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 插入新任务
     * @param task
     * @return
     */
    public void saveTask(Task task) throws ServiceException {
        try {
            if(StringUtil.isEmpty(task)) {
                return;
            }
            if(StringUtil.isEmpty(task.getBeginTime())) {
                task.setBeginTime(new Date());
            }
            taskMapper.insertTask(task);
        } catch (Exception e) {
            logger.error("[插入task任务异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * 查询未执行的任务
     * @param taskName
     * @return
     */
    public boolean isHasTask(String taskName) throws ServiceException {
        try {
            if(StringUtil.isEmpty(taskName)) {
                return false;
            }

            Task tk = taskMapper.queryTaskInfo(taskName);
            if(StringUtil.isEmpty(tk)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("[查询task任务异常] errorDesc=" + e.getMessage());
            throw new ServiceException(ErrorCode_API.SERVER_ERROR, e.getMessage());
        }
    }

}
