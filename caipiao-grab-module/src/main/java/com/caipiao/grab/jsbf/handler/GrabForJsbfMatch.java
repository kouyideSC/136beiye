package com.caipiao.grab.jsbf.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.caipiao.common.http.Grab;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.NumberUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.jsbf.Schedule;
import com.caipiao.grab.jsbf.task.JsbfTask;
import com.caipiao.grab.util.JcUtil;
import com.caipiao.grab.util.JsbfUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import sun.org.mozilla.javascript.internal.NativeArray;

/**
 * 抓取即时比分对阵
 * Created by kouyi on 2017/11/22.
 */
@Component("grabForJsbfMatch")
public class GrabForJsbfMatch extends Grab<List<Schedule>, String> {
	private static Logger logger = LoggerFactory.getLogger(GrabForJsbfMatch.class);

	@Override
	public List<Schedule> parse(String content, String param) {
		try {
			if(StringUtil.isEmpty(content)){
				logger.info("[即时比分对阵抓取] 页面数据为空!");
				return null;
			}
			Document doc = Jsoup.parse(content);
			Element table = doc.getElementById("MatchTable");
			if (StringUtil.isEmpty(table)) {
				logger.info("[即时比分对阵抓取] 获取MatchTable标签无数据!");
				return null;
			}
			Elements trs = table.getElementsByTag("tr");
			if(StringUtil.isEmpty(trs)) {
				logger.info("[即时比分对阵抓取] 无比赛数据!");
				return null;
			}
			List<Schedule> list = new ArrayList<Schedule>();
			for(int ix = 1; ix < trs.size(); ix++) {
				if(ix % 2 == 0) {
					continue;
				}
				Elements tds = trs.get(ix).getElementsByTag("td");
				if(StringUtil.isEmpty(tds)) {
					logger.info("[即时比分对阵抓取] 比赛数据列为空!");
					continue;
				}
				Schedule dule = new Schedule();
				String weekJcId = tds.get(0).text();
				dule.setJcId(getId(weekJcId));
				dule.setWeek(weekJcId.substring(0, weekJcId.indexOf(dule.getJcId())));
				String scheId = getId(tds.get(5).attr("onclick"));
				if(StringUtil.isEmpty(scheId)) {
					logger.info("[即时比分对阵抓取] 解析scheduleId为空!");
					continue;
				}
				dule.setScheduleId(scheId);
				dule.setLeague(trs.get(ix).attr("gamename"));
				dule.setHomeTeam(tds.get(4).getElementsByTag("a").text());
				dule.setGuestTeam(tds.get(6).getElementsByTag("a").text());
				dule.setMatchState(JsbfUtil.matchStateConvert(tds.get(3).text()));
				String score = tds.get(5).text();
				if(!score.equals("-") && score.split("\\-").length == 2) {
					dule.setHomeScore(parseInt(score.split("\\-")[0]));
					dule.setGuestScore(parseInt(score.split("\\-")[1]));
				}
				String halfScore = tds.get(7).text();
				if(!halfScore.equals("-") && halfScore.split("\\-").length == 2) {
					dule.setHomeHalfScore(parseInt(halfScore.split("\\-")[0]));
					dule.setGuestHalfScore(parseInt(halfScore.split("\\-")[1]));
				}
				dule.setMatchTime(DateUtil.dateDefaultFormat(DateUtil.getCurYear() + "-" + tds.get(2).text() + ":00"));
				dule.setPeriod(JcUtil.getPeriodDayJsbf(dule.getWeek(), dule.getMatchTime()));
				dule.setSperiod(StringUtil.isNotEmpty(dule.getPeriod())? dule.getPeriod().replace("-","") : "");
				list.add(dule);
			}
			return list;
		} catch (Exception e) {
			logger.error("[即时比分对阵抓取] 解析数据异常", e);
		}
		return null;
	}

	/**
	 * 截取竞彩场次对应的球探即时比分赛事ID
	 * @param content
	 * @return
	 */
	private String getId(String content) {
		String regEx="[^0-9.]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(content);
		if(m.find()) {
			String scheId = m.replaceAll("").trim();
			if(NumberUtil.isNumber(scheId)) {
				return scheId;
			}
		}
		return null;
	}
	
}
