package com.caipiao.taskcenter.task;

import com.caipiao.common.constants.Constants;
import com.caipiao.common.constants.KeyConstants;
import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.common.Activity;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.service.common.ActivityService;
import com.caipiao.service.common.TaskService;
import com.caipiao.service.config.SysConfig;
import com.caipiao.service.lottery.PeriodService;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

/**
 * app启动页文件更新任务
 * @author 	mcdog
 */
@Component("createAppStartTask")
public class CreateAppStartTask
{
	private static Logger logger = LoggerFactory.getLogger(CreateAppStartTask.class);

	@Value("${app.start.filepath}")
	private String filepath;

	@Autowired
	private TaskService taskService;

	@Autowired
	private ActivityService activityService;

	/**
	 * 创建/更新app启动页文件
	 * @author	sjq
	 */
	public void createOrUpdateAppStartFile()
	{
		try
		{
			boolean hasTask = taskService.isHasTask(Constants.BANNER_APPSTART_UPDATE_TASK);
			if(hasTask)
			{
				long start = System.currentTimeMillis();
				List<Dto> dataList = new ArrayList<Dto>();
				Activity activity = new Activity();
				activity.setActivityType(5);
				activity.setClientType(1);
				activity.setIsShow(1);
				List<Activity> activityList = activityService.queryActivityList(activity);
				if(activityList != null && activityList.size() > 0)
				{
					for(Activity act : activityList)
					{
						Dto data = new BaseDto();
						data.put("build",act.getBuild());
						data.put("linkUrl",act.getLinkUrl());
						data.put("aimg", SysConfig.getHostStatic() + act.getPictureUrl() + "?v=" + KeyConstants.FIXED_VERSION);
						dataList.add(data);
					}
				}
				FileUtils.write(new File(filepath + "/start.json"), JsonUtil.JsonArray(dataList).toString(), "UTF-8");//生成启动页文件
				int spendTime = (int) (System.currentTimeMillis() - start) / 1000;
				taskService.updateTask(Constants.BANNER_APPSTART_UPDATE_TASK,spendTime);
			}
		}
		catch (Exception e)
		{
			logger.error("[创建/更新app启动页文件]发生异常!异常信息：", e);
		}
	}
}