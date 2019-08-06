package com.caipiao.grab.jc.task;

import com.caipiao.common.constants.Constants;
import com.caipiao.common.http.Grab;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.common.Task;
import com.caipiao.domain.match.GyjMatch;
import com.caipiao.grab.jc.handler.GrabForGjMatch;
import com.caipiao.grab.jc.handler.GrabForGyjMatch;
import com.caipiao.service.common.TaskService;
import com.caipiao.service.match.GyjMatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 冠亚军对阵、赔率、赛果抓取数据处理服务
 * Created by kouyi on 2018/04/04.
 */
@Component("gyjMatchTask")
public class gyjMatchTask {
	private static Logger logger = LoggerFactory.getLogger(gyjMatchTask.class);
	private static boolean isFirstGrabSp = false;
	@Value("${grab.gw_gj_match_url}")
	private String gwGjMatchUrl;//竞彩官网猜冠军URL
	@Value("${grab.gw_gyj_match_url}")
	private String gwGyjMatchUrl;//竞彩官网冠亚军URL
	@Value("${sporttery.host.jc}")
	private String hostJc;//竞彩官网域名
	@Value("${sporttery.host.odds}")
	private String hostJcOdds;//竞彩官网赔率数据域名

	@Autowired
	private GrabForGjMatch grabForGjMatch;
	@Autowired
	private GrabForGyjMatch grabForGyjMatch;
	@Autowired
	private GyjMatchService gyjMatchService;
	@Autowired
	private TaskService taskService;

	/**
	 * 抓取猜冠军官网对阵任务
 	 * @author kouyi
	 */
	public void grabGwGjMatch() {
		try {
			long start = System.currentTimeMillis();
            //抓取对阵
			List<GyjMatch> List = grabForGjMatch.collect(gwGjMatchUrl+System.currentTimeMillis(), null, Grab.CHARTSET_GBK, hostJcOdds);
			if(StringUtil.isEmpty(List)){
				return;
			}
			boolean isNewMatch = false;
			int count = 0;
			for (GyjMatch gyjMatch : List) {
				isNewMatch |= gyjMatchService.saveOrUpdateMatch(gyjMatch);
				count ++;
			}

			//场次数据有更新
			if(isNewMatch){
				//创建任务-更新对阵文件
				taskService.saveTask(new Task(Constants.GJ_MATCH_UPDATE_TASK));
			}
			long end = System.currentTimeMillis();
			List.clear();
			logger.info("[猜冠军对阵抓取] 成功抓取处理冠亚军数据 " + count + " 条,用时" + (end - start) / 1000 + "秒");
		} catch (Exception e) {
			logger.error("[猜冠军对阵抓取] 处理对阵异常", e);
		}
	}

	/**
	 * 抓取猜冠亚军官网对阵任务
	 * @author kouyi
	 */
	public void grabGwGyjMatch() {
		try {
			long start = System.currentTimeMillis();
			//抓取对阵
			List<GyjMatch> List = grabForGyjMatch.collect(gwGyjMatchUrl+System.currentTimeMillis(), null, Grab.CHARTSET_GBK, hostJcOdds);
			if(StringUtil.isEmpty(List)){
				return;
			}
			boolean isNewMatch = false;
			int count = 0;
			for (GyjMatch gyjMatch : List) {
				isNewMatch |= gyjMatchService.saveOrUpdateMatch(gyjMatch);
				count ++;
			}

			//场次数据有更新
			if(isNewMatch){
				//创建任务-更新对阵文件
				taskService.saveTask(new Task(Constants.GYJ_MATCH_UPDATE_TASK));
			}
			long end = System.currentTimeMillis();
			List.clear();
			logger.info("[冠亚军对阵抓取] 成功抓取处理冠亚军数据 " + count + " 条,用时" + (end - start) / 1000 + "秒");
		} catch (Exception e) {
			logger.error("[冠亚军对阵抓取] 处理对阵异常", e);
		}
	}
}
