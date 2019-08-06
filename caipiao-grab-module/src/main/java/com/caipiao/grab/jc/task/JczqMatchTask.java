package com.caipiao.grab.jc.task;

import com.caipiao.common.constants.Constants;
import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.http.Grab;
import com.caipiao.common.lottery.JczqUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.common.TaskMapper;
import com.caipiao.domain.common.Task;
import com.caipiao.domain.match.MatchFootBall;
import com.caipiao.domain.match.MatchFootBallResult;
import com.caipiao.domain.match.MatchFootBallSp;
import com.caipiao.grab.jc.handler.GrabForJczqMatch;
import com.caipiao.grab.jc.handler.GrabForJczqMatchResult;
import com.caipiao.grab.jc.handler.GrabForJczqMatchSp;
import com.caipiao.grab.jc.handler.GrabForJczqMatchStatus;
import com.caipiao.memcache.MemCached;
import com.caipiao.service.common.TaskService;
import com.caipiao.service.lottery.PeriodService;
import com.caipiao.service.match.JczqMatchResultService;
import com.caipiao.service.match.JczqMatchService;
import com.caipiao.service.match.JczqMatchSpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.*;

/**
 * 竞彩足球对阵、赔率、赛果抓取数据处理服务
 * Created by kouyi on 2017/11/10.
 */
@Component("jczqMatchTask")
public class JczqMatchTask {
	private static Logger logger = LoggerFactory.getLogger(JczqMatchTask.class);
	private static boolean isFirstGrabSp = false;
	@Autowired
	private MemCached memcache;
	@Value("${grab.gw_football_match_url}")
	private String gwFootBallMatchUrl;//竞彩官网对阵URL
	@Value("${grab.gw_football_status_url}")
	private String gwFootBallMatchStatusUrl;//竞彩官网对阵状态URL
	@Value("${grab.gw_football_odds_url}")
	private String gwFootBallOddsUrl;//竞彩官网赔率URL
	@Value("${grab.gw_football_result_url}")
	private String gwFootBallResultUrl;//竞彩官网赛果URL
	@Value("${sporttery.host.jc}")
	private String hostJc;//竞彩官网域名
	@Value("${sporttery.host.odds}")
	private String hostJcOdds;//竞彩官网赔率数据域名

	@Autowired
	private GrabForJczqMatch grabForJczqMatch;
	@Autowired
	private GrabForJczqMatchSp grabForJczqMatchSp;
	@Autowired
	private GrabForJczqMatchResult grabForJczqMatchResult;
	@Autowired
	private GrabForJczqMatchStatus grabForJczqMatchStatus;
	@Autowired
	private JczqMatchService jczqMatchService;
	@Autowired
	private JczqMatchSpService jczqMatchSpService;
    @Autowired
    private JczqMatchResultService jczqMatchResultService;
	@Autowired
	private PeriodService periodService;
	@Autowired
	private TaskService taskService;
	
	/**
	 * 抓取竞彩足球官网对阵任务
 	 * @author kouyi
	 */
	public void grabGwJczqMatch() {
		try {
			long start = System.currentTimeMillis();
            //抓取对阵
			Map<String,List<MatchFootBall>> matchMap = grabForJczqMatch.collect(gwFootBallMatchUrl, null, Grab.CHARTSET_GBK, hostJc);
			if(StringUtil.isEmpty(matchMap)){
				return;
			}
			//抓取对阵状态
			Map<String, MatchFootBall> statusMap = grabForJczqMatchStatus.collect(gwFootBallMatchStatusUrl, null, Grab.CHARTSET_GBK, hostJc);
			boolean isNewMatch = false;
			int count = 0;
			for (Map.Entry<String, List<MatchFootBall>> entry : matchMap.entrySet()) {
				String period = entry.getKey();
				List<MatchFootBall> matchList = entry.getValue();
				if(StringUtil.isEmpty(matchList)){
					continue;
				}
				Date endTime = null;//期次销售截止时间
				for (MatchFootBall matchFootBall : matchList) {
					if(StringUtil.isNotEmpty(statusMap) && statusMap.containsKey(matchFootBall.getMatchCode())) {
						MatchFootBall status = statusMap.get(matchFootBall.getMatchCode());
						if(status.getSpfStatus() == LotteryConstants.STATUS_STOP) {
							matchFootBall.setSpfStatus(status.getSpfStatus());
							matchFootBall.setSingleSpfStatus(status.getSpfStatus());
						}
						if(status.getRqspfStatus() == LotteryConstants.STATUS_STOP) {
							matchFootBall.setRqspfStatus(status.getRqspfStatus());
							matchFootBall.setSingleRqspfStatus(status.getRqspfStatus());
						}
						if(status.getBfStatus() == LotteryConstants.STATUS_STOP) {
							matchFootBall.setBfStatus(status.getBfStatus());
							matchFootBall.setSingleBfStatus(status.getBfStatus());
						}
						if(status.getZjqStatus() == LotteryConstants.STATUS_STOP) {
							matchFootBall.setZjqStatus(status.getZjqStatus());
							matchFootBall.setSingleZjqStatus(status.getZjqStatus());
						}
						if(status.getBqcStatus() == LotteryConstants.STATUS_STOP) {
							matchFootBall.setBqcStatus(status.getBqcStatus());
							matchFootBall.setSingleBqcStatus(status.getBqcStatus());
						}
					}
					isNewMatch |= jczqMatchService.saveOrUpdateMatch(matchFootBall);
					//最晚的比赛的截止时间作为奖期的截止时间
					if(null == endTime || endTime.getTime() < matchFootBall.getEndTime().getTime()) {
						endTime = matchFootBall.getEndTime();
					}
					count ++;
				}
				periodService.saveOrUpdateJcPeriod(LotteryConstants.JCZQ, period, endTime);
			}

			//场次数据有更新
			if(isNewMatch){
				//创建任务-更新对阵文件
				taskService.saveTask(new Task(Constants.JCZQ_MATCH_UPDATE_TASK));
			}
			long end = System.currentTimeMillis();
			matchMap.clear();
			logger.info("[竞彩足球对阵抓取] 成功抓取处理竞彩足球数据 " + count + " 条,用时" + (end - start) / 1000 + "秒");

			//为避免对阵和sp抓取任务同时执行时抓取sp方法取不到对阵，导致10分钟以内抓不到sp值 这里手动在成功抓取对阵后调用一次抓取sp任务
			if(!isFirstGrabSp) {
				grabGwJczqMatchSp();
			}
		} catch (Exception e) {
			logger.error("[竞彩足球对阵抓取] 处理对阵异常", e);
		}
	}

	/**
	 * 抓取竞彩足球官网对阵赔率任务
	 * @author kouyi
	 */
	public void grabGwJczqMatchSp() {
		try {
			long start = System.currentTimeMillis();
			//查询销售中的对阵列表
			List<MatchFootBall>	allMatchs = jczqMatchService.queryMatchFootBallList(new MatchFootBall(1, 0));
			if(StringUtil.isEmpty(allMatchs)) {
				return;
			}

			//只要保证成功执行一次抓取sp任务 就通知grabGwJczqMatch任务不再手工调用
			isFirstGrabSp = true;

			int success = 0;
			boolean isNewSp = false;//记录数据变化
			for(MatchFootBall match : allMatchs) {
				//取消|截止|后台手工设置的场次不自动更新
				if(match.getMatchTime().getTime() < (new Date()).getTime()
						|| match.getStatus().intValue() == LotteryConstants.STATUS_CANCEL || match.getUpdateFlag()){
					continue;
				}
				//抓取赔率
				MatchFootBallSp matchSp = grabForJczqMatchSp.collect(MessageFormat.format(gwFootBallOddsUrl + System.currentTimeMillis(), new Object[] {match.getJcWebId()}), match.getMatchCode(), Grab.CHARTSET_GBK, hostJcOdds);
				if(StringUtil.isEmpty(matchSp)){
					logger.info("[竞彩足球赔率抓取] 场次号="+match.getMatchCode()+"无赔率数据!");
					continue;
				}
				matchSp.setMatchCode(match.getMatchCode());
				isNewSp |= jczqMatchSpService.saveOrUpdateMatchSp(matchSp, match);
				success++;
			}

			//赔率数据有更新
			if(isNewSp){
				//创建任务-更新对阵文件
				taskService.saveTask(new Task(Constants.JCZQ_MATCH_UPDATE_TASK));
			}
			long end = System.currentTimeMillis();
			logger.info("[竞彩足球赔率抓取] 成功更新处理竞彩足球赔率数据 " + success + " 条,用时" + (end - start) / 1000 + "秒");
            allMatchs.clear();
		} catch (Exception e) {
			logger.error("[竞彩足球赔率抓取] 处理赔率异常", e);
		}
	}

	/**
	 * 抓取竞彩足球官网赛果任务
	 * @author kouyi
	 */
	public void grabGwJczqMatchResult() {
		try {
			long start = System.currentTimeMillis();
			List<MatchFootBall> matchsNoResult = jczqMatchService.queryMatchFootBallNoResultList();
			if(StringUtil.isEmpty(matchsNoResult)) {
				return;
			}
            //抓取赛果
			Map<String, MatchFootBallResult> resultMap = grabForJczqMatchResult.collect(gwFootBallResultUrl, null, Grab.CHARTSET_GBK, hostJc);
			if(StringUtil.isEmpty(resultMap)){
				return;
			}
			//多页时-分页抓取数据
			if(resultMap.containsKey("pageSum")) {
                List<String> pages = resultMap.get("pageSum").getPageList();
                for(String url : pages) {
					Map<String, MatchFootBallResult> tempPageMap = grabForJczqMatchResult.collect(url, null, Grab.CHARTSET_GBK, hostJc);
                    if(StringUtil.isNotEmpty(tempPageMap)) {
						resultMap.putAll(tempPageMap);
					}
                }
                logger.info("[竞彩足球赛果抓取] 成功抓取竞彩足球赛果数据 " + (pages.size()+1) + " 页");
            }

            int success = 0;
			for(MatchFootBall match : matchsNoResult) {
                if(!resultMap.containsKey(match.getMatchCode())) {
                    continue;
                }
                jczqMatchResultService.saveMatchResult(resultMap.get(match.getMatchCode()), match);
                success++;
            }

			//赔率数据有更新
			if(success > 0){
				//创建任务-更新赛果文件
				taskService.saveTask(new Task(Constants.JCZQ_RESULT_UPDATE_TASK));
			}
			long end = System.currentTimeMillis();
			logger.info("[竞彩足球赛果抓取] 成功抓取竞彩足球赛果数据 " + success + " 条,用时" + (end - start) / 1000 + "秒");
            resultMap.clear();
		} catch (Exception e) {
			logger.error("[竞彩足球赛果抓取] 处理赛果异常", e);
		}
	}
}
