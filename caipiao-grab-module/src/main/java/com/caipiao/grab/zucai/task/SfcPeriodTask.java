package com.caipiao.grab.zucai.task;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.http.Grab;
import com.caipiao.common.json.JsonUtil;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.lottery.Lottery;
import com.caipiao.domain.lottery.Period;
import com.caipiao.grab.vo.SfcMatchInfoVO;
import com.caipiao.grab.vo.SfcMatchVo;
import com.caipiao.grab.vo.SfcOkoMatchInfoVO;
import com.caipiao.grab.vo.SfcPeriodVO;
import com.caipiao.grab.zucai.handler.GrabForOkoSfcMatch;
import com.caipiao.grab.zucai.handler.GrabForSfcMatch;
import com.caipiao.grab.zucai.handler.GrabForSfcPeriod;
import com.caipiao.service.lottery.LotteryService;
import com.caipiao.service.lottery.PeriodService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.*;

/**
 * 胜负彩任九期次、对阵抓取数据处理服务
 * Created by kouyi on 2017/11/10.
 */
@Component("sfcPeriodTask")
public class SfcPeriodTask {
	private static Logger logger = LoggerFactory.getLogger(SfcPeriodTask.class);
	public final static int MAX_period_NUM = 5;//抓取未来最大期数

	@Value("${lottery.host.tc}")
	private String hostTc;//中国体彩网域名
	@Value("${sporttery.host.ak}")
	private String hostAk;//澳客网域名
	@Value("${grab.gw_sfc_period_url}")
	private String gwSfcPeriodUrl;//体彩官网期次
	@Value("${grab.gw_sfc_match_url}")
	private String gwSfcMatchUrl;//体彩官网对阵
	@Value("${grab.ako_sfc_match_url}")
	private String akoSfcMatchUrl;//澳客网对阵
	@Autowired
	private GrabForSfcPeriod grabForSfcPeriod;
	@Autowired
	private GrabForSfcMatch grabForSfcMatch;
	@Autowired
	private GrabForOkoSfcMatch grabForOkoSfcMatch;
	@Autowired
	private PeriodService periodService;
	@Autowired
	private LotteryService lotteryService;

	/**
	 * 抓取入口方法
	 */
	public void grabGwSfcMatch() {
		try {
			//抓取期次列表
			SfcPeriodVO periodVo = grabForSfcPeriod.collect(gwSfcPeriodUrl, null, Grab.CHARTSET_GBK, hostTc);
			if(StringUtil.isEmpty(periodVo) || StringUtil.isEmpty(periodVo.getTremList())){
				return;
			}

			int strem = 0;//最近一期已截止
			if (StringUtil.isNotEmpty(periodVo.getCtrem())) {
				strem = Integer.parseInt("20" + periodVo.getCtrem());//最近一期已截止
			} else {
				Period period = periodService.queryCurrentFirstPeriod(LotteryConstants.SFC);
				if (StringUtil.isNotEmpty(period)) {
					strem = Integer.parseInt(period.getPeriod())-1;
				}
			}

			Map<String, SfcMatchVo> mapMatchVo = new HashMap<>();//胜负彩和任九共用
			Map<String, Map<Integer, SfcOkoMatchInfoVO>> mapOkoMatchVo = new HashMap<>();//澳客胜负彩和任九共用
			String[] tremList = periodVo.getTremList();
			int number = 0;
			for (int i = tremList.length-1; i >= 0; i--) {
				String curStrem = "20" + tremList[i];
				try {
					if(number == 0 && !curStrem.equals(strem+"")) {
						continue;
					}
					if(number > MAX_period_NUM) {
						continue;
					}
					savePeriod(curStrem, LotteryConstants.SFC, mapMatchVo, mapOkoMatchVo);
					savePeriod(curStrem, LotteryConstants.RXJ, mapMatchVo, mapOkoMatchVo);
					number++;
				} catch (Exception e) {
					logger.error("[胜负彩任九对阵抓取] 期次="+curStrem+"数据处理异常", e);
				}
			}
			mapMatchVo.clear();
			mapOkoMatchVo.clear();
		} catch (Exception e) {
			logger.error("[胜负彩任九对阵抓取] 处理期次异常", e);
		}
	}

	/**
	 * 保存期次
	 * @param curStrem
	 * @param mapMatchVo
	 * @param lotteryId
	 * @throws Exception
	 */
	private void savePeriod(String curStrem, String lotteryId, Map<String, SfcMatchVo> mapMatchVo, Map<String, Map<Integer, SfcOkoMatchInfoVO>> mapOkMatchVo) throws Exception {
		Period period = periodService.queryPeriodByPerod(lotteryId, curStrem);
		if(StringUtil.isNotEmpty(period) && period.getState() > 3) {//已经审核过不再后续操作
			return;
		}

		SfcMatchVo matchVo = null;
		if(mapMatchVo.containsKey(curStrem)) {
			matchVo = mapMatchVo.get(curStrem);
		} else {
			matchVo = grabForSfcMatch.collect(MessageFormat.format(gwSfcMatchUrl, new Object[] {curStrem.substring(2)}), null, Grab.CHARTSET_GBK, hostTc);;
			if (StringUtil.isEmpty(matchVo)) {
				logger.info("[胜负彩任九比赛对阵数据抓取] 期次" + curStrem + "无比赛数据");
				return;
			}
			mapMatchVo.put(curStrem, matchVo);
		}

		String lotteryName = "胜负彩";
		if(lotteryId.equals(LotteryConstants.RXJ)) {
			lotteryName = "任九";
		}
		if (StringUtil.isEmpty(period)) {
			period = new Period();
			period.setLotteryId(lotteryId);
			period.setPeriod(curStrem);
			period.setSellStatus(LotteryConstants.STATUS_CLOSE);
			period.setDrawNumber("");
			period.setUpdateFlag(false);
			period.setPrizeGrade(getPrizeGrade(lotteryId));
			period.setDrawNumberTime(DateUtil.dateDefaultFormat(matchVo.getLterm().getOpenTimeFmt()));
			setperiodMatch(period, matchVo, mapOkMatchVo, true);
		} else {
			setperiodMatch(period, matchVo, mapOkMatchVo, false);
		}
		periodService.saveOrUpdateSfcPeriod(period);
		logger.info("[胜负彩任九比赛对阵数据抓取] "+lotteryName+"期次" +curStrem+ "保存或更新成功!");
	}

	/**
	 * 设置对阵信息
	 * @param period
	 * @param matchVo
	 * @throws Exception
	 * @throws ParseException
	 */
	private void setperiodMatch(Period period, SfcMatchVo matchVo, Map<String, Map<Integer, SfcOkoMatchInfoVO>> mapOkMatchVo, boolean isNew) throws Exception {
		//时间
		if(isNew || !period.getUpdateFlag()) {
			period.setSellStartTime(DateUtil.dateDefaultFormat(matchVo.getLterm().getSaleStartTime()));
			period.setSellEndTime(DateUtil.addMinute(DateUtil.dateDefaultFormat(matchVo.getLterm().getSaleEndTime()), -20));//提前20分钟停售
			period.setAuthorityEndTime(DateUtil.dateDefaultFormat(matchVo.getLterm().getSaleEndTime()));//公开截止时间和官方保持一致
		}

		Map<Integer, SfcOkoMatchInfoVO> okoMatchInfoVO = null;//澳客对阵
		if(mapOkMatchVo.containsKey(matchVo.getLterm().getTerm())) {
			okoMatchInfoVO = mapOkMatchVo.get(matchVo.getLterm().getTerm());
		} else {
			okoMatchInfoVO = grabForOkoSfcMatch.collect(akoSfcMatchUrl + matchVo.getLterm().getTerm(), null, Grab.CHARTSET_GBK, hostAk);
			if(okoMatchInfoVO != null) {
				mapOkMatchVo.put(matchVo.getLterm().getTerm(), okoMatchInfoVO);
			}
		}

		boolean isUpdate = true;//是否更新对阵
		Map<String,Object> matchMap = new LinkedHashMap<String,Object>();
		if(StringUtil.isNotEmpty(period.getMatches())) {
			matchMap.putAll(JsonUtil.jsonToMap(period.getMatches()));
		}
		if(!isNew && period.getUpdateFlag()) {
			isUpdate = false;//不更新对阵则需要取出原对阵,只更新比分，
		}

		int index = 1;
		for (SfcMatchInfoVO matchInfo : matchVo.getMatch_vs()) {
			String matchTime = "";
			String score = matchInfo.getResults().replaceAll("-", ":").trim().replaceAll(" : ", ":");
			String sheng = "", ping = "", fu = "";
			if (StringUtil.isNotEmpty(okoMatchInfoVO) && okoMatchInfoVO.containsKey(index)) {//澳客网取比赛时间
				SfcOkoMatchInfoVO okoMatch = okoMatchInfoVO.get(index);//根据编号匹配
				matchTime = okoMatch.getMatchTime();
				if(StringUtil.isEmpty(score) || score.indexOf(":") == -1) {
					score = okoMatch.getScore().trim().replaceAll(" : ", ":");
				}
				sheng = okoMatch.getSheng().trim();
				ping = okoMatch.getPing().trim();
				fu = okoMatch.getFu().trim();
			} else {
				try {
					Date _matchTime = DateUtil.dateFormat(DateUtil.dateFormat(new Date(), DateUtil.DEFAULT_DATE0) + "年" + matchInfo.getMatchTime() + " 00:00:00", DateUtil.CHINESE_DATE_TIME_SECOND);
					matchTime = DateUtil.dateDefaultFormat(_matchTime);
				} catch (Exception ex) {//默认
					matchTime = DateUtil.dateDefaultFormat(new Date());
				}
			}

			Map<String,String> match = (Map<String,String>) (matchMap.get(index+""));
			if(StringUtil.isEmpty(match)) {
				match = new LinkedHashMap<>();
			}
			if(score.equals(":") || (StringUtil.isNotEmpty(matchTime)
					&& DateUtil.dateDefaultFormat(matchTime).getTime() > DateUtil.addMinute(new Date(), -115).getTime())) {
				score = "";
			}
			if(isUpdate) {//是否更新对阵
				match.put("index", index+"");
				match.put("matchname", matchInfo.getMatchname().trim());
				match.put("homeTeamView", matchInfo.getHomeTeamView().trim());
				match.put("awayTeamView", matchInfo.getAwayTeamView().trim());
				match.put("matchTime", matchTime);
				match.put("sheng", sheng.trim());
				match.put("ping", ping.trim());
				match.put("fu", fu.trim());
				match.put("score", score);
			} else {
				//没有比分则更新
				Object curScore = match.get("score");
				if(StringUtil.isEmpty(curScore) || ":".equals(curScore.toString())){
					match.put("score", score);
				}
			}
			matchMap.put(index+"", match);
			index++;
		}
		period.setMatches(JSONObject.fromObject(matchMap).toString());
	}

	/**
	 * 查询彩种奖级
	 * @param lotteryId
	 * @return
	 */
	private String getPrizeGrade(String lotteryId){
		try{
			Lottery lottery= lotteryService.queryLotteryInfo(lotteryId);
			if(StringUtil.isNotEmpty(lottery)){
				return lottery.getPrizeGrade();
			}
		} catch(Exception e){
			logger.error("[胜负彩任九对阵抓取] 同步奖级模板异常", e);
		}
		return "";
	}

}
