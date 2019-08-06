package com.caipiao.taskcenter.task;

import com.caipiao.common.constants.Constants;
import com.caipiao.common.constants.KeyConstants;
import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.common.Activity;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.vo.LotteryVo;
import com.caipiao.service.common.ActivityService;
import com.caipiao.service.common.TaskService;
import com.caipiao.service.config.SysConfig;
import com.caipiao.service.exception.ServiceException;
import com.caipiao.service.lottery.LotteryService;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生成首页数据文件更新任务
 * Created by kouyi on 2017/11/13.
 */
@Component("createUpdateHomeTask")
public class CreateUpdateHomeTask {
	private static Logger logger = LoggerFactory.getLogger(CreateUpdateHomeTask.class);
	private static int isOpen = 1;

	@Value("${lottery.api.home}")
	private String position;
	@Autowired
	private TaskService taskService;
	@Autowired
	private LotteryService lotteryService;
	@Autowired
	private ActivityService activityService;

	/**
	 * 首页数据文件更新任务
 	 * @author kouyi
	 */
	public void homeDataFileTask() {
		try {
			boolean isHas = taskService.isHasTask(Constants.BANNER_HOME_UPDATE_TASK);
				isHas |= taskService.isHasTask(Constants.NOTICE_HOME_UPDATE_TASK);
				isHas |= taskService.isHasTask(Constants.LOTTERY_HOME_UPDATE_TASK);

			if(!isHas) {
				int nowOpen = SysConfig.getInt("home_bottom_isopen");
				if(isOpen != nowOpen) {
					isHas |= true;
					isOpen = nowOpen;
				}
			}
			if(isHas) {
				long start = System.currentTimeMillis();
				createHomeDataFile();//app
				createH5HomeDataFile();//h5
				int spendTime = (int)(System.currentTimeMillis()-start)/1000;
				taskService.updateTask(Constants.BANNER_HOME_UPDATE_TASK, spendTime);
				taskService.updateTask(Constants.NOTICE_HOME_UPDATE_TASK, spendTime);
				taskService.updateTask(Constants.LOTTERY_HOME_UPDATE_TASK, spendTime);
				logger.info("[首页数据文件刷新任务] 成功刷新首页数据文件");
			}
		} catch (Exception e) {
			logger.error("[首页数据文件刷新任务] 刷新文件异常", e);
		}
	}

	/**
	 * 创建APP首页数据文件
	 * @throws ServiceException
	 * @throws Exception
	 */
	private void createHomeDataFile() throws ServiceException, Exception {
		JSONObject json = new JSONObject();
		//刷新有效的banner数据
		Activity act = new Activity();
		act.setActivityType(0);
		act.setIsbanner(1);
		act.setClientType(1);
		act.setIsShow(1);
		List<Activity> array = activityService.queryActivityList(act);
		if(StringUtil.isNotEmpty(array)) {
			for(Activity ar : array) {//地址转换
				if(StringUtil.isNotEmpty(ar.getPictureUrl())) {
					ar.setPictureUrl(SysConfig.getHostStatic() + ar.getPictureUrl() + "?v=" + KeyConstants.FIXED_VERSION);
				}
				/*if(StringUtil.isNotEmpty(ar.getLinkUrl())) {
					ar.setLinkUrl(SysConfig.getHostStatic() + ar.getLinkUrl());
				}*/
			}
		}
		json.put("banner", JsonUtil.JsonArray(array, Activity.column_banner));

		//刷新有效的公告数据
		array.clear();
		act.setActivityType(3);
		array = activityService.queryActivityList(act);
		json.put("notice", StringUtil.isNotEmpty(array) ? JsonUtil.JsonObject(array.get(0), Activity.column_notice) : null);

		//刷新生成首页彩种文件
		Dto queryDto = new BaseDto("client","1");
		queryDto.put("showInHome","1");
		List<LotteryVo> lotteryList = lotteryService.queryLotterySaleList(queryDto);
		List<LotteryVo> needRemoveList = new ArrayList<LotteryVo>();//需要移除掉的彩种(不需要在首页展示的彩种)
		if(StringUtil.isNotEmpty(lotteryList)) {
			for(LotteryVo lot : lotteryList) {//彩种编号转换
				if(lot.getLid().equals(LotteryConstants.GYJ)) {//前端只显示一个彩种
					needRemoveList.add(lot);
				}
				if(StringUtil.isNotEmpty(lot.getAimg()))
				{
					lot.setAimg(SysConfig.getHostStatic() + lot.getAimg() + "?v=" + KeyConstants.FIXED_VERSION);
				}
				else
				{
					lot.setAimg("");
				}
				lot.setLid(lot.getLid());
				lot.setIcon(SysConfig.getLotteryLogo(lot.getLid()) + "?v=" + KeyConstants.FIXED_VERSION);
			}
		}
		lotteryList.removeAll(needRemoveList);//移除掉不需要在首页展示的彩种
		json.put("lottery", JsonUtil.JsonArray(lotteryList));

		//生成资讯/优惠信息
		array.clear();
		List<Dto> zxyhList = new ArrayList<Dto>();
		act = new Activity();
		act.setActivityType(4);
		act.setClientType(1);
		act.setIsShow(1);
		array = activityService.queryActivityList(act);
		if(array != null && array.size() > 0)
		{
			for(Activity activity : array)
			{
				Dto dto = new BaseDto();
				dto.put("title",activity.getTitle());
				dto.put("content",activity.getContent());
				dto.put("build",activity.getBuild());
				dto.put("linkUrl",activity.getLinkUrl());
				dto.put("aimg",SysConfig.getHostStatic() + activity.getPictureUrl() + "?v=" + KeyConstants.FIXED_VERSION);
				zxyhList.add(dto);
			}
		}
		json.put("activitys",JsonUtil.JsonArray(zxyhList));

		json.put("bottom", SysConfig.getInt("HOME_BOTTOM_ISOPEN"));
		FileUtils.write(new File(position + "/home.json"), json.toString(), "UTF-8");
	}

	/**
	 * 创建H5首页数据文件
	 * @throws ServiceException
	 * @throws Exception
	 */
	private void createH5HomeDataFile() throws ServiceException, Exception {
		JSONObject json = new JSONObject();
		//刷新有效的banner数据
		Activity act = new Activity();
		act.setActivityType(0);
		act.setIsbanner(1);
		act.setClientType(2);
		act.setIsShow(1);
		List<Activity> array = activityService.queryActivityList(act);
		if(StringUtil.isNotEmpty(array)) {
			for(Activity ar : array) {//地址转换
				if(StringUtil.isNotEmpty(ar.getPictureUrl())) {
					ar.setPictureUrl(SysConfig.getHostStatic() + ar.getPictureUrl() + "?v=" + KeyConstants.FIXED_VERSION);
				}
				/*if(StringUtil.isNotEmpty(ar.getLinkUrl())) {
					ar.setLinkUrl(SysConfig.getHostStatic() + ar.getLinkUrl());
				}*/
			}
		}
		json.put("banner", JsonUtil.JsonArray(array, Activity.column_banner));

		//刷新有效的公告数据
		array.clear();
		act.setActivityType(3);
		array = activityService.queryActivityList(act);
		json.put("notice", StringUtil.isNotEmpty(array) ? JsonUtil.JsonObject(array.get(0), Activity.column_notice) : null);

		//刷新生成首页彩种文件
		Dto queryDto = new BaseDto("client","2");
		queryDto.put("showInHome","1");
		List<LotteryVo> lotteryList = lotteryService.queryLotterySaleList(queryDto);
		List<LotteryVo> needRemoveList = new ArrayList<LotteryVo>();//需要移除掉的彩种(不需要在首页展示的彩种)
		if(StringUtil.isNotEmpty(lotteryList)) {
			for(LotteryVo lot : lotteryList) {//彩种编号转换
				if(lot.getLid().equals(LotteryConstants.GYJ)) {//前端只显示一个彩种
					needRemoveList.add(lot);
				}
				if(StringUtil.isNotEmpty(lot.getAimg()))
				{
					lot.setAimg(SysConfig.getHostStatic() + lot.getAimg() + "?v=" + KeyConstants.FIXED_VERSION);
				}
				else
				{
					lot.setAimg("");
				}
				lot.setLid(lot.getLid());
				lot.setIcon(SysConfig.getLotteryLogo(lot.getLid()) + "?v=" + KeyConstants.FIXED_VERSION);
			}
		}
		lotteryList.removeAll(needRemoveList);//移除掉不需要在首页展示的彩种
		json.put("lottery", JsonUtil.JsonArray(lotteryList));

		//生成资讯/优惠信息
		array.clear();
		List<Dto> zxyhList = new ArrayList<Dto>();
		act = new Activity();
		act.setActivityType(4);
		act.setClientType(2);
		act.setIsShow(1);
		array = activityService.queryActivityList(act);
		if(array != null && array.size() > 0)
		{
			for(Activity activity : array)
			{
				Dto dto = new BaseDto();
				dto.put("title",activity.getTitle());
				dto.put("content",activity.getContent());
				dto.put("build",activity.getBuild());
				dto.put("linkUrl",activity.getLinkUrl());
				dto.put("aimg",SysConfig.getHostStatic() + activity.getPictureUrl() + "?v=" + KeyConstants.FIXED_VERSION);
				zxyhList.add(dto);
			}
		}
		json.put("activitys",JsonUtil.JsonArray(zxyhList));

		json.put("bottom", SysConfig.getInt("HOME_BOTTOM_ISOPEN"));
		FileUtils.write(new File(position + "/h5home.json"), json.toString(), "UTF-8");
	}
}
