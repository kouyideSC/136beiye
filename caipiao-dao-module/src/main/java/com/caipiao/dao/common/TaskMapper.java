package com.caipiao.dao.common;

import com.caipiao.domain.common.Activity;
import com.caipiao.domain.common.Task;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务模块功能接口定义
 * @author kouyi 2017-11-03
 */
public interface TaskMapper {

    /**
     * 更新任务
     * @param taskName
     * @param spendTime
     * @return
     */
    int updateTask(@Param("taskName")String taskName, @Param("spendTime")Integer spendTime);

    /**
     * 新建任务记录
     * @param task
     * @return
     */
    int insertTask(Task task);

    /**
     * 查询任务记录
     * @param taskName
     * @return
     */
    Task queryTaskInfo(String taskName);

}