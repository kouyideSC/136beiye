package com.caipiao.grab.task;

import com.caipiao.common.constants.Constants;
import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.lottery.JczqUtils;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.dao.common.TaskMapper;
import com.caipiao.domain.common.Task;
import com.caipiao.domain.cpadmin.BaseDto;
import com.caipiao.domain.cpadmin.Dto;
import com.caipiao.domain.match.GyjMatch;
import com.caipiao.domain.match.MatchFootBall;
import com.caipiao.domain.match.MatchFootBallResult;
import com.caipiao.domain.match.MatchFootBallSp;
import com.caipiao.domain.vo.JclqMatchVo;
import com.caipiao.domain.vo.JclqResultVo;
import com.caipiao.domain.vo.JczqMatchVo;
import com.caipiao.domain.vo.JczqResultVo;
import com.caipiao.grab.jc.handler.GrabForJczqMatch;
import com.caipiao.grab.jc.handler.GrabForJczqMatchResult;
import com.caipiao.grab.jc.handler.GrabForJczqMatchSp;
import com.caipiao.grab.util.JcUtil;
import com.caipiao.memcache.MemCached;
import com.caipiao.service.common.TaskService;
import com.caipiao.service.lottery.PeriodService;
import com.caipiao.service.match.*;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.xpath.axes.HasPositionalPredChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;

/**
 * 消费任务表中竞彩、老足彩文件更新任务
 * Created by kouyi on 2017/11/10.
 */
@Component("createUpdateDataTask")
public class CreateUpdateDataTask {
	private static Logger logger = LoggerFactory.getLogger(CreateUpdateDataTask.class);

	@Value("${jc.web.match}")
	private String position;
	@Autowired
	private JczqMatchService jczqMatchService;
	@Autowired
	private JclqMatchService jclqMatchService;
    @Autowired
    private JczqMatchResultService jczqMatchResultService;
	@Autowired
	private JclqMatchResultService jclqMatchResultService;
	@Autowired
	private GyjMatchService gyjMatchService;
	@Autowired
	private TaskService taskService;


	/**
	 * 竞彩足球对阵文件更新任务
 	 * @author kouyi
	 */
	public void jczqMatchFileTask() {
		try {
			boolean isHas = taskService.isHasTask(Constants.JCZQ_MATCH_UPDATE_TASK);
			if(isHas) {
				long start = System.currentTimeMillis();
				//执行任务-刷新生成足球对阵
				JSONArray main = new JSONArray();
				List<JczqMatchVo> jczqMatchs = jczqMatchService.queryJczqSaleMatchList();
				if(StringUtil.isNotEmpty(jczqMatchs)) {
					String[] leagueSortStrings = new String[100];
					Map<String, Integer> leaguesNumber = new HashMap<>();
					JSONObject second = new JSONObject();//按期次分组
					for(JczqMatchVo match : jczqMatchs) {
						match.setEtime(DateUtil.dateFormat(DateUtil.dateDefaultFormat(match.getEtime()), DateUtil.CHINESE_HOUR_TIME)+"截止");
						match.setHname(match.getHname().length() > 4 ?match.getHname().substring(0, 4):match.getHname());
						match.setGname(match.getGname().length() > 4 ?match.getGname().substring(0, 4):match.getGname());
						if(second.containsKey(match.getPeriod())) {
							JSONArray array = second.getJSONArray(match.getPeriod());
							array.add(match);
						} else {
							JSONArray array = new JSONArray();
							array.add(match);
							second.put(match.getPeriod(), array);
						}
						Integer sort = JcUtil.leagueSortMap.get(match.getName());
						if(StringUtil.isNotEmpty(sort)) {
							leagueSortStrings[sort - 1] = match.getName();
						}
						if(leaguesNumber.containsKey(match.getName())) {
							leaguesNumber.put(match.getName(),leaguesNumber.get(match.getName())+1);
						} else {
							leaguesNumber.put(match.getName(), 1);
						}
					}

					StringBuffer buffer = new StringBuffer();
					for(String name : leagueSortStrings) {
						if(StringUtil.isNotEmpty(name)) {
							buffer.append(name);
							buffer.append("-");
							buffer.append(leaguesNumber.get(name)).append(",");
						}
					}
					String names = buffer.toString();
					if(names.endsWith(",")) {
						names = names.substring(0, names.length() - 1);
					}
					boolean isTrue = false;
					Iterator ite = second.entrySet().iterator();
					while(ite.hasNext()) {//迭代比赛格式化
						JSONObject info = JSONObject.fromObject(ite.next());
						String key = info.getString("key");
						JSONArray value = info.getJSONArray("value");
						Date period = DateUtil.dateFormat(key,DateUtil.DEFAULT_DATE1);
						info.put("title", DateUtil.dateFormat(period, DateUtil.DEFAULT_DATE)  + " " +  DateUtil.getWeekStr(period) + " 有<font color='#F63F3F'>" + value.size() + "</font>场比赛可投注");
						if(!isTrue) {
							info.put("leagues", names);
							isTrue = true;
						} else {
							info.put("leagues", "");
						}
						main.add(info);
					}
				}
				FileUtils.write(new File(position + "/jczqMatch.json"), main.toString(), "UTF-8");
				int spendTime = (int)(System.currentTimeMillis()-start)/1000;
				taskService.updateTask(Constants.JCZQ_MATCH_UPDATE_TASK, spendTime);
				logger.info("[竞彩足球对阵刷新任务] 成功刷新竞彩足球对阵文件");
			}
		} catch (Exception e) {
			logger.error("[竞彩足球对阵刷新任务] 刷新文件异常", e);
		}
	}

	/**
	 * 竞彩足球赛果文件更新任务
	 * @author kouyi
	 */
	public void jczqResultFileTask() {
		try {
			boolean isHas = taskService.isHasTask(Constants.JCZQ_RESULT_UPDATE_TASK);
			if(isHas) {
				long start = System.currentTimeMillis();
				//执行任务-刷新生成足球赛果
				String begin = DateUtil.dateFormat(DateUtil.addDay(new Date(), -4), DateUtil.DEFAULT_DATE1);
				String end = DateUtil.dateFormat(new Date(), DateUtil.DEFAULT_DATE1);
				JSONArray main = new JSONArray();
				List<JczqResultVo> jczqResult = jczqMatchResultService.queryJczqResultList(begin, end);
				if(StringUtil.isNotEmpty(jczqResult)) {
					JSONObject second = new JSONObject();//按期次分组
					for(JczqResultVo match : jczqResult) {
						match.setSpfr(StringUtil.isEmpty(match.getSpfr()) ? "未开" : match.getSpfr());
						match.setSpfs(StringUtil.isEmpty(match.getSpfs()) ? "未开" : match.getSpfs());
						match.setRqspfr(StringUtil.isEmpty(match.getRqspfr()) ? "未开" : match.getRqspfr());
						match.setRqspfs(StringUtil.isEmpty(match.getRqspfs()) ? "未开" : match.getRqspfs());
						match.setBfr(StringUtil.isEmpty(match.getBfr()) ? "未开" : match.getBfr());
						match.setBfs(StringUtil.isEmpty(match.getBfs()) ? "未开" : match.getBfs());
						match.setZjqr(StringUtil.isEmpty(match.getZjqr()) ? "未开" : match.getZjqr());
						match.setZjqs(StringUtil.isEmpty(match.getZjqs()) ? "未开" : match.getZjqs());
						match.setBqcr(StringUtil.isEmpty(match.getBqcr()) ? "未开" : match.getBqcr());
						match.setBqcs(StringUtil.isEmpty(match.getBqcs()) ? "未开" : match.getBqcs());
						match.setMtime(match.getMid().substring(2,5) + " " + match.getName());
						match.setHname(match.getHname().length() > 4 ?match.getHname().substring(0, 4):match.getHname());
						match.setGname(match.getGname().length() > 4 ?match.getGname().substring(0, 4):match.getGname());
						if(second.containsKey(match.getPeriod())) {
							JSONArray array = second.getJSONArray(match.getPeriod());
							array.add(match);
						} else {
							if(second.size() >= 3) {//历史3期
								break;
							}
							JSONArray array = new JSONArray();
							array.add(match);
							second.put(match.getPeriod(), array);
						}
					}

					Iterator ite = second.entrySet().iterator();
					while(ite.hasNext()) {//迭代比赛格式化
						JSONObject info = JSONObject.fromObject(ite.next());
						String key = info.getString("key");
						JSONArray value = info.getJSONArray("value");
						Date period = DateUtil.dateFormat(key,DateUtil.DEFAULT_DATE1);
						info.put("title", DateUtil.dateFormat(period, DateUtil.CHINESE_DATE)  + " " +  DateUtil.getWeekStr(period) + " 共<font color='#F63F3F'>" + value.size() + "</font>场比赛");
						main.add(info);
					}
				}
				FileUtils.write(new File(position + "/jczqResult.json"), main.toString(), "UTF-8");
				int spendTime = (int)(System.currentTimeMillis()-start)/1000;
				taskService.updateTask(Constants.JCZQ_RESULT_UPDATE_TASK, spendTime);
				logger.info("[竞彩足球赛果刷新任务] 成功刷新竞彩足球赛果文件");
			}
		} catch (Exception e) {
			logger.error("[竞彩足球赛果刷新任务] 刷新文件异常", e);
		}
	}

	/**
	 * 竞彩篮球对阵文件更新任务
	 * @author kouyi
	 */
	public void jclqMatchFileTask() {
		try {
			boolean isHas = taskService.isHasTask(Constants.JCLQ_MATCH_UPDATE_TASK);
			if(isHas) {
				long start = System.currentTimeMillis();
				//执行任务-刷新生成篮球对阵
				JSONArray main = new JSONArray();
				List<JclqMatchVo> jclqMatchs = jclqMatchService.queryJclqSaleMatchList();
				if(StringUtil.isNotEmpty(jclqMatchs)) {
					String[] leagueSortStrings = new String[10];
					Map<String, Integer> leaguesNumber = new HashMap<>();
					JSONObject second = new JSONObject();//按期次分组
					for(JclqMatchVo match : jclqMatchs) {
						match.setEtime(DateUtil.dateFormat(DateUtil.dateDefaultFormat(match.getEtime()), DateUtil.CHINESE_HOUR_TIME)+"截止");
						match.setHname(match.getHname().length() > 4 ?match.getHname().substring(0, 4):match.getHname());
						match.setGname(match.getGname().length() > 4 ?match.getGname().substring(0, 4):match.getGname());
						match.setDx(match.getDx().substring(0, match.getDx().length()-1));
						match.setRf(match.getRf().substring(0, match.getRf().length()-1));
						if(second.containsKey(match.getPeriod())) {
							JSONArray array = second.getJSONArray(match.getPeriod());
							array.add(match);
						} else {
							JSONArray array = new JSONArray();
							array.add(match);
							second.put(match.getPeriod(), array);
						}
						Integer sort = JcUtil.leagueSortMap_LQ.get(match.getName());
						if(StringUtil.isNotEmpty(sort)) {
							leagueSortStrings[sort - 1] = match.getName();
						}
						if(leaguesNumber.containsKey(match.getName())) {
							leaguesNumber.put(match.getName(),leaguesNumber.get(match.getName())+1);
						} else {
							leaguesNumber.put(match.getName(), 1);
						}
					}

					StringBuffer buffer = new StringBuffer();
					for(String name : leagueSortStrings) {
						if(StringUtil.isNotEmpty(name)) {
							buffer.append(name);
							buffer.append("-");
							buffer.append(leaguesNumber.get(name)).append(",");
						}
					}
					String names = buffer.toString();
					if(names.endsWith(",")) {
						names = names.substring(0, names.length() - 1);
					}
					boolean isTrue = false;
					Iterator ite = second.entrySet().iterator();
					while(ite.hasNext()) {//迭代比赛格式化
						JSONObject info = JSONObject.fromObject(ite.next());
						String key = info.getString("key");
						JSONArray value = info.getJSONArray("value");
						Date period = DateUtil.dateFormat(key,DateUtil.DEFAULT_DATE1);
						info.put("title", DateUtil.dateFormat(period, DateUtil.DEFAULT_DATE)  + " " +  DateUtil.getWeekStr(period) + " 有<font color='#F63F3F'>" + value.size() + "</font>场比赛可投注");
						if(!isTrue) {
							info.put("leagues", names);
							isTrue = true;
						} else {
							info.put("leagues", "");
						}
						main.add(info);
					}
				}
				FileUtils.write(new File(position + "/jclqMatch.json"), main.toString(), "UTF-8");
				int spendTime = (int)(System.currentTimeMillis()-start)/1000;
				taskService.updateTask(Constants.JCLQ_MATCH_UPDATE_TASK, spendTime);
				logger.info("[竞彩篮球对阵刷新任务] 成功刷新竞彩篮球对阵文件");
			}
		} catch (Exception e) {
			logger.error("[竞彩篮球对阵刷新任务] 刷新文件异常", e);
		}
	}

	/**
	 * 竞彩篮球赛果文件更新任务
	 * @author kouyi
	 */
	public void jclqResultFileTask() {
		try {
			boolean isHas = taskService.isHasTask(Constants.JCLQ_RESULT_UPDATE_TASK);
			if(isHas) {
				long start = System.currentTimeMillis();
				//执行任务-刷新生成篮球赛果
				String begin = DateUtil.dateFormat(DateUtil.addDay(new Date(), -4), DateUtil.DEFAULT_DATE1);
				String end = DateUtil.dateFormat(new Date(), DateUtil.DEFAULT_DATE1);
				JSONArray main = new JSONArray();
				List<JclqResultVo> jczqResult = jclqMatchResultService.queryJclqResultList(begin, end);
				if(StringUtil.isNotEmpty(jczqResult)) {
					JSONObject second = new JSONObject();//按期次分组
					for(JclqResultVo match : jczqResult) {
						match.setSfr(StringUtil.isEmpty(match.getSfr()) ? "未开" : match.getSfr());
						match.setSfs(StringUtil.isEmpty(match.getSfs()) ? "未开" : match.getSfs());
						match.setRfsfr(StringUtil.isEmpty(match.getRfsfr()) ? "未开" : match.getRfsfr());
						match.setRfsfs(StringUtil.isEmpty(match.getRfsfs()) ? "未开" : match.getRfsfs());
						match.setDxfr(StringUtil.isEmpty(match.getDxfr()) ? "未开" : match.getDxfr());
						match.setDxfs(StringUtil.isEmpty(match.getDxfs()) ? "未开" : match.getDxfs());
						match.setSfcr(StringUtil.isEmpty(match.getSfcr()) ? "未开" : match.getSfcr());
						match.setSfcs(StringUtil.isEmpty(match.getSfcs()) ? "未开" : match.getSfcs());
						match.setMtime(match.getMid().substring(2,5) + " " + match.getName());
						match.setHname(match.getHname().length() > 4 ?match.getHname().substring(0, 4):match.getHname());
						match.setGname(match.getGname().length() > 4 ?match.getGname().substring(0, 4):match.getGname());
						if(second.containsKey(match.getPeriod())) {
							JSONArray array = second.getJSONArray(match.getPeriod());
							array.add(match);
						} else {
							if(second.size() >= 3) {//历史3期
								break;
							}
							JSONArray array = new JSONArray();
							array.add(match);
							second.put(match.getPeriod(), array);
						}
					}

					Iterator ite = second.entrySet().iterator();
					while(ite.hasNext()) {//迭代比赛格式化
						JSONObject info = JSONObject.fromObject(ite.next());
						String key = info.getString("key");
						JSONArray value = info.getJSONArray("value");
						Date period = DateUtil.dateFormat(key,DateUtil.DEFAULT_DATE1);
						info.put("title", DateUtil.dateFormat(period, DateUtil.CHINESE_DATE)  + " " +  DateUtil.getWeekStr(period) + " 共<font color='#F63F3F'>" + value.size() + "</font>场比赛");
						main.add(info);
					}
				}
				FileUtils.write(new File(position + "/jclqResult.json"), main.toString(), "UTF-8");
				int spendTime = (int)(System.currentTimeMillis()-start)/1000;
				taskService.updateTask(Constants.JCLQ_RESULT_UPDATE_TASK, spendTime);
				logger.info("[竞彩篮球赛果刷新任务] 成功刷新竞彩篮球赛果文件");
			}
		} catch (Exception e) {
			logger.error("[竞彩篮球赛果刷新任务] 刷新文件异常", e);
		}
	}

	/**
	 * 猜冠军对阵文件更新任务
	 * @author kouyi
	 */
	public void gjMatchFileTask() {
		try {
			boolean isHas = taskService.isHasTask(Constants.GJ_MATCH_UPDATE_TASK);
			if(isHas) {
				long start = System.currentTimeMillis();
				//执行任务-刷新生成猜冠军对阵
				List<GyjMatch> gyjMatchs = gyjMatchService.queryMatchFootBallList(new GyjMatch(LotteryConstants.GJ));
				List<Dto> dataList = new ArrayList<Dto>();
				if(gyjMatchs != null && gyjMatchs.size() > 0)
				{
					Dto data = null;
					for(GyjMatch gyjMatch : gyjMatchs)
					{
						data = new BaseDto();
						data.put("leagueName",gyjMatch.getLeagueName());
						data.put("lotteryId",gyjMatch.getLotteryId());
						data.put("matchCode",gyjMatch.getMatchCode());
						data.put("period",gyjMatch.getPeriod());
						data.put("probability",gyjMatch.getProbability());
						data.put("sp",StringUtil.isEmpty(gyjMatch.getSp())? "" : (String.format("%.2f",gyjMatch.getSp())));
						data.put("status",gyjMatch.getStatus());
						data.put("teamId",gyjMatch.getTeamId());
						data.put("teamImg",gyjMatch.getTeamImg());
						data.put("teamName",gyjMatch.getTeamName());
						dataList.add(data);
					}
				}
				FileUtils.write(new File(position + "/gjMatch.json"), JsonUtil.JsonArray(dataList).toString(), "UTF-8");
				int spendTime = (int)(System.currentTimeMillis()-start)/1000;
				taskService.updateTask(Constants.GJ_MATCH_UPDATE_TASK, spendTime);
				logger.info("[猜冠军对阵刷新任务] 成功刷新猜冠军对阵文件");
			}
		} catch (Exception e) {
			logger.error("[猜冠军对阵刷新任务] 刷新文件异常", e);
		}
	}

	/**
	 * 冠亚军对阵文件更新任务
	 * @author kouyi
	 */
	public void gyjMatchFileTask() {
		try {
			boolean isHas = taskService.isHasTask(Constants.GYJ_MATCH_UPDATE_TASK);
			if(isHas) {
				long start = System.currentTimeMillis();
				//执行任务-刷新生成冠亚军对阵
				List<GyjMatch> gyjMatchs = gyjMatchService.queryMatchFootBallList(new GyjMatch(LotteryConstants.GYJ));
				List<Dto> dataList = new ArrayList<Dto>();
				if(gyjMatchs != null && gyjMatchs.size() > 0)
				{
					Dto data = null;
					for(GyjMatch gyjMatch : gyjMatchs)
					{
						data = new BaseDto();
						data.put("guestTeamId",gyjMatch.getGuestTeamId());
						data.put("guestTeamImg",gyjMatch.getGuestTeamImg());
						data.put("guestTeamName",gyjMatch.getGuestTeamName());
						data.put("leagueName",gyjMatch.getLeagueName());
						data.put("lotteryId",gyjMatch.getLotteryId());
						data.put("matchCode",gyjMatch.getMatchCode());
						data.put("period",gyjMatch.getPeriod());
						data.put("probability",gyjMatch.getProbability());
						data.put("sp",StringUtil.isEmpty(gyjMatch.getSp())? "" : (String.format("%.2f",gyjMatch.getSp())));
						data.put("status",gyjMatch.getStatus());
						data.put("teamId",gyjMatch.getTeamId());
						data.put("teamImg",gyjMatch.getTeamImg());
						data.put("teamName",gyjMatch.getTeamName());
						dataList.add(data);
					}
				}
				FileUtils.write(new File(position + "/gyjMatch.json"), JsonUtil.JsonArray(dataList).toString(), "UTF-8");
				int spendTime = (int)(System.currentTimeMillis()-start)/1000;
				taskService.updateTask(Constants.GYJ_MATCH_UPDATE_TASK, spendTime);
				logger.info("[冠亚军对阵刷新任务] 成功刷新冠亚军对阵文件");
			}
		} catch (Exception e) {
			logger.error("[冠亚军对阵刷新任务] 刷新文件异常", e);
		}
	}
}
