package com.caipiao.grab.jc.task;

import com.caipiao.common.constants.Constants;
import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.http.Grab;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.common.Task;
import com.caipiao.domain.match.*;
import com.caipiao.grab.jc.handler.*;
import com.caipiao.memcache.MemCached;
import com.caipiao.service.common.TaskService;
import com.caipiao.service.lottery.PeriodService;
import com.caipiao.service.match.JclqMatchResultService;
import com.caipiao.service.match.JclqMatchService;
import com.caipiao.service.match.JclqMatchSpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 竞彩篮球对阵、赔率、赛果抓取数据处理服务
 * Created by kouyi on 2017/11/10.
 */
@Component("jclqMatchTask")
public class JclqMatchTask {
	private static Logger logger = LoggerFactory.getLogger(JclqMatchTask.class);
	private static boolean isFirstGrabSp = false;
	@Autowired
	private MemCached memcache;
	@Value("${grab.gw_basketball_match_url}")
	private String gwbasketballMatchUrl;//竞彩官网对阵URL
	@Value("${grab.gw_basketball_status_url}")
	private String gwBasketBallMatchStatusUrl;//竞彩官网对阵状态URL
	@Value("${grab.gw_basketball_odds_url}")
	private String gwbasketballOddsUrl;//竞彩官网赔率URL
	@Value("${grab.gw_basketball_result_url}")
	private String gwbasketballResultUrl;//竞彩官网赛果URL
	@Value("${sporttery.host.jc}")
	private String hostJc;//竞彩官网域名
	@Value("${sporttery.host.odds}")
	private String hostJcOdds;//竞彩官网赔率数据域名

	@Autowired
	private GrabForJclqMatch grabForJclqMatch;
	@Autowired
	private GrabForJclqMatchStatus grabForJclqMatchStatus;
	@Autowired
	private GrabForJclqMatchSp grabForJclqMatchSp;
	@Autowired
	private GrabForJclqMatchResult grabForJclqMatchResult;
	@Autowired
	private JclqMatchService JclqMatchService;
	@Autowired
	private JclqMatchSpService JclqMatchSpService;
    @Autowired
    private JclqMatchResultService JclqMatchResultService;
	@Autowired
	private PeriodService periodService;
	@Autowired
	private TaskService taskService;

	/**
	 * 抓取竞彩篮球官网对阵任务
 	 * @author kouyi
	 */
	public void grabGwJclqMatch() {
		try {
			long start = System.currentTimeMillis();
            //抓取对阵
			Map<String,List<MatchBasketBall>> matchMap = grabForJclqMatch.collect(gwbasketballMatchUrl, null, Grab.CHARTSET_GBK, hostJc);
			if(StringUtil.isEmpty(matchMap)){
				return;
			}
			//抓取对阵状态
			Map<String, MatchBasketBall> statusMap = grabForJclqMatchStatus.collect(gwBasketBallMatchStatusUrl, null, Grab.CHARTSET_GBK, hostJc);
			boolean isNewMatch = false;//记录数据变化
			int count = 0;
			for (Entry<String, List<MatchBasketBall>> entry : matchMap.entrySet()) {
				String period = entry.getKey();
				List<MatchBasketBall> matchList = entry.getValue();
				if(StringUtil.isEmpty(matchList)){
					continue;
				}
				Date endTime = null;//期次销售截止时间
				for (MatchBasketBall match : matchList) {
					if(StringUtil.isNotEmpty(statusMap) && statusMap.containsKey(match.getMatchCode())) {
						MatchBasketBall status = statusMap.get(match.getMatchCode());
						if(status.getSfStatus() == LotteryConstants.STATUS_STOP) {
							match.setSfStatus(status.getSfStatus());
							match.setSingleSfStatus(status.getSfStatus());
						}
						if(status.getRfsfStatus() == LotteryConstants.STATUS_STOP) {
							match.setRfsfStatus(status.getRfsfStatus());
							match.setSingleRfsfStatus(status.getSingleRfsfStatus());
						}
						if(status.getDxfStatus() == LotteryConstants.STATUS_STOP) {
							match.setDxfStatus(status.getDxfStatus());
							match.setSingleDxfStatus(status.getSingleDxfStatus());
						}
						if(status.getSfcStatus() == LotteryConstants.STATUS_STOP) {
							match.setSfcStatus(status.getSfcStatus());
							match.setSingleSfcStatus(status.getSingleSfcStatus());
						}
					}
					isNewMatch |= JclqMatchService.saveOrUpdateMatch(match);
					//最晚的比赛的截止时间作为奖期的截止时间
					if(null == endTime || endTime.getTime() < match.getEndTime().getTime()) {
						endTime = match.getEndTime();
					}
					count ++;
				}
				periodService.saveOrUpdateJcPeriod(LotteryConstants.JCLQ, period, endTime);
			}

			//场次数据有更新
			if(isNewMatch){
				//创建任务-更新对阵文件
				taskService.saveTask(new Task(Constants.JCLQ_MATCH_UPDATE_TASK));
			}
			long end = System.currentTimeMillis();
			matchMap.clear();
			logger.info("[竞彩篮球对阵抓取] 成功抓取处理竞彩篮球数据 " + count + " 条,用时" + (end - start) / 1000 + "秒");

			//为避免对阵和sp抓取任务同时执行时抓取sp方法取不到对阵，导致10分钟以内抓不到sp值 这里手动在成功抓取对阵后调用一次抓取sp任务
			if(!isFirstGrabSp) {
				grabGwJclqMatchSp();
			}
		} catch (Exception e) {
			logger.error("[竞彩篮球对阵抓取] 处理对阵异常", e);
		}
	}

	/**
	 * 抓取竞彩篮球官网对阵赔率任务
	 * @author kouyi
	 */
	public void grabGwJclqMatchSp() {
		try {
			long start = System.currentTimeMillis();
			//查询销售中的对阵列表
			List<MatchBasketBall> allMatchs = JclqMatchService.queryMatchBasketBallList(new MatchBasketBall(1, 0));
			if(StringUtil.isEmpty(allMatchs)) {
				return;
			}

			//只要保证成功执行一次抓取sp任务 就通知grabGwJclqMatch任务不再手工调用
			isFirstGrabSp = true;

			int success = 0;
			boolean isNewSp = false;//记录数据变化
			for(MatchBasketBall match : allMatchs) {
				//取消|截止|后台手工设置的场次不自动更新
				if(match.getMatchTime().getTime() < (new Date()).getTime()
						|| match.getStatus().intValue() == LotteryConstants.STATUS_CANCEL || match.getUpdateFlag()){
					continue;
				}
				//抓取赔率
				MatchBasketBallSp matchSp = grabForJclqMatchSp.collect(MessageFormat.format(gwbasketballOddsUrl,
						new Object[] {match.getJcWebId()}), match.getMatchCode(), Grab.CHARTSET_GBK, hostJc);
				if(StringUtil.isEmpty(matchSp)){
					continue;
				}
				matchSp.setMatchCode(match.getMatchCode());
				isNewSp |= JclqMatchSpService.saveOrUpdateMatchSp(matchSp, match);
				success++;
			}

			//赔率数据有更新
			if(isNewSp){
				//创建任务-更新对阵文件
				taskService.saveTask(new Task(Constants.JCLQ_MATCH_UPDATE_TASK));
			}
			long end = System.currentTimeMillis();
			allMatchs.clear();
			logger.info("[竞彩篮球赔率抓取] 成功更新处理竞彩篮球赔率数据 " + success + " 条,用时" + (end - start) / 1000 + "秒");
		} catch (Exception e) {
			logger.error("[竞彩篮球赔率抓取] 处理赔率异常", e);
		}
	}

	/**
	 * 抓取竞彩篮球官网赛果任务
	 * @author kouyi
	 */
	public void grabGwJclqMatchResult() {
		try {
			long start = System.currentTimeMillis();
			List<MatchBasketBall> matchsNoResult = JclqMatchService.queryMatchBasketBallNoResultList();
			if(StringUtil.isEmpty(matchsNoResult)) {
				return;
			}
            //抓取赛果
			Map<String, MatchBasketBallResult> resultMap = grabForJclqMatchResult.collect(gwbasketballResultUrl, null,
					Grab.CHARTSET_GBK, hostJc);
			if(StringUtil.isEmpty(resultMap)){
				return;
			}
			//多页时-分页抓取数据
			if(resultMap.containsKey("pageSum")) {
                List<String> pages = resultMap.get("pageSum").getPageList();
                for(String url : pages) {
					Map<String, MatchBasketBallResult> tempPageMap = grabForJclqMatchResult.collect(url, null, Grab.CHARTSET_GBK, hostJc);
					if(StringUtil.isNotEmpty(tempPageMap)) {
						resultMap.putAll(tempPageMap);
					}
                }
                logger.info("[竞彩篮球赛果抓取] 成功抓取竞彩篮球赛果数据 " + (pages.size()+1) + " 页");
            }

            int success = 0;
			for(MatchBasketBall match : matchsNoResult) {
                if(!resultMap.containsKey(match.getMatchCode())) {
                    continue;
                }
                JclqMatchResultService.saveMatchResult(resultMap.get(match.getMatchCode()), match);
                success++;
            }

			//赔率数据有更新
			if(success > 0){
				//创建任务-更新赛果文件
				taskService.saveTask(new Task(Constants.JCLQ_RESULT_UPDATE_TASK));
			}
			long end = System.currentTimeMillis();
			logger.info("[竞彩篮球赛果抓取] 成功抓取竞彩篮球赛果数据 " + success + " 条,用时" + (end - start) / 1000 + "秒");
            resultMap.clear();
		} catch (Exception e) {
			logger.error("[竞彩篮球赛果抓取] 处理赛果异常", e);
		}
	}
}
