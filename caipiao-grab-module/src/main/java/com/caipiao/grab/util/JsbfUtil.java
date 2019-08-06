package com.caipiao.grab.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.jsbf.Schedule;
import org.joda.time.DateTime;

/**
 * 即时比分对阵工具类
 * Created by kouyi on 2017/11/06.
 */
public class JsbfUtil {
	public static String JC_PERIOD_BEGIN_TIME = "yyyy-MM-dd 08:00:00";
	public static String JC_PERIOD_END_TIME = "yyyy-MM-dd 11:30:00";
	public static String JSBF_MATCH_KEY = "a_";//即时比分抓取对阵缓存键
	public static String JSBF_CURDAY_KEY = "b_";//即时比分当天对阵缓存键
	public static String JSBF_LIVE_KEY = "c_";//即时比分比赛中的直播数据键
	public static int JSBF_MATCH_KEY_EXPIRE = 30*60*60;//即时比分抓取对阵缓存键过期时间
	public static int JSBF_CURDAY_KEY_EXPIRE = 60*60;//即时比分当天对阵缓存键过期时间
	public static Map<Integer, String> stateMap = new HashMap<>();
	public static List<String> show = new ArrayList<>();
	public static List<String> bfShow = new ArrayList<>();
	static {
		//系统状态码转换为中文-返回客户端
		stateMap.put(0, "未开");
		stateMap.put(1, "上半场");
		stateMap.put(2, "中场");
		stateMap.put(3, "下半场");
		stateMap.put(4, "加时");
		stateMap.put(-11, "待定");
		stateMap.put(-12, "腰斩");
		stateMap.put(-13, "中断");
		stateMap.put(-14, "推迟");
		stateMap.put(-1, "完场");
		stateMap.put(-10, "取消");

		//对阵过滤属性
		show.add("id");
		show.add("week");
		show.add("homeTeamId");
		show.add("guestTeamId");
		show.add("homeOrder");
		show.add("guestOrder");
		show.add("recommendCount");
		show.add("hot");
		show.add("updateTime");
		show.add("flag");

		//直播过滤属性
		bfShow.add("league");
		bfShow.add("period");
		bfShow.add("mid");
		bfShow.add("hname");
		bfShow.add("gname");
		bfShow.add("haddle");
	}

	/**
	 * 抓取好彩店网页中文状态-转换为系统状态码
	 * @param state
	 * @return
	 */
	public static int matchStateConvert(String state) {
		if(StringUtil.isEmpty(state)) {
			return 0;//默认未开
		}
		if("未开".indexOf(state) > -1) {
			return 0;
		}
		else if("上半场".indexOf(state) > -1) {
			return 1;
		}
		else if("下半场".indexOf(state) > -1) {
			return 3;
		}
		else if("中场".indexOf(state) > -1) {
			return 2;
		}
		else if("加时".indexOf(state) > -1) {
			return 4;
		}
		else if("待定".indexOf(state) > -1) {
			return -11;
		}
		else if("腰斩".indexOf(state) > -1) {
			return -12;
		}
		else if("中断".indexOf(state) > -1) {
			return -13;
		}
		else if("推迟".indexOf(state) > -1) {
			return -14;
		}
		else if("取消".indexOf(state) > -1) {
			return -10;
		} else {
			return -1;
		}
	}

	/**
	 * 当前对阵数据与缓存中数据同步
	 * @param preSchedule
	 * @param curSchedule
	 */
	public static void schesynchro(Schedule preSchedule, Schedule curSchedule) {
		if(StringUtil.isEmpty(preSchedule) || StringUtil.isEmpty(curSchedule)) {
			return;
		}
		if(StringUtil.isNotEmpty(curSchedule.getMatchTime())) {
			preSchedule.setMatchTime(curSchedule.getMatchTime());
		}
		if(StringUtil.isNotEmpty(curSchedule.getBeginTime())) {
			preSchedule.setBeginTime(curSchedule.getBeginTime());
		}
		if(preSchedule.getMatchState() != -1) {
			preSchedule.setMatchState(curSchedule.getMatchState());
		}
		if(StringUtil.isNotEmpty(curSchedule.getHomeScore())) {
			preSchedule.setHomeScore(curSchedule.getHomeScore());
		}
		if(StringUtil.isNotEmpty(curSchedule.getGuestScore())) {
			preSchedule.setGuestScore(curSchedule.getGuestScore());
		}
		if(StringUtil.isNotEmpty(curSchedule.getHomeHalfScore())) {
			preSchedule.setHomeHalfScore(curSchedule.getHomeHalfScore());
		}
		if(StringUtil.isNotEmpty(curSchedule.getGuestHalfScore())) {
			preSchedule.setGuestHalfScore(curSchedule.getGuestHalfScore());
		}
		if(StringUtil.isNotEmpty(curSchedule.getHomeRed())) {
			preSchedule.setHomeRed(curSchedule.getHomeRed());
		}
		if(StringUtil.isNotEmpty(curSchedule.getGuestRed())) {
			preSchedule.setGuestRed(curSchedule.getGuestRed());
		}
		if(StringUtil.isNotEmpty(curSchedule.getHomeYellow())) {
			preSchedule.setHomeYellow(curSchedule.getHomeYellow());
		}
		if(StringUtil.isNotEmpty(curSchedule.getGuestYellow())) {
			preSchedule.setGuestYellow(curSchedule.getGuestYellow());
		}
	}
	
	/**
	 * 对阵数据是否发生变化-当日对阵缓存对比
	 * @param sche
	 * @return
	 */
	public static String sameMc(Schedule sche) {
		if (sche == null) {
			return "";
		}
		return sche.getScheduleId() + "_" + sche.getMatchTime() + "_" + sche.getMatchState() + "_" + sche.getHomeScore()
				+ "_" + sche.getGuestScore() + "_" + sche.getHomeHalfScore() + "_" + sche.getGuestHalfScore();
	}

	/**
	 * 对阵数据是否发生变化-change直播与当日对阵缓存对比
	 * @param sche
	 * @return
	 */
	public static String sameCh(Schedule sche) {
		if (sche == null) {
			return "";
		}
		return sche.getScheduleId() + "_" + sche.getBeginTime() + "_" + sche.getMatchState() + "_"
				+ sche.getHomeScore() + "_" + sche.getGuestScore() + "_" + sche.getHomeHalfScore() + "_"
				+ sche.getGuestHalfScore();
	}
	
	/**
	 * 即时比分当前期键值
	 * @return
	 */
	public static String jsbfkey(int n) {
		return JSBF_CURDAY_KEY + todaystr(n);
	}
	
	/**
	 * 当天格式字符串
	 * @return
	 */
	public static String todaystr(int n) {
		return today().plusDays(n).toString("yyyy-MM-dd");
	}
	
	/**
	 * 当前期
	 * @return
	 */
	public static DateTime today() {
		DateTime today = new DateTime();
		if (Integer.parseInt(today.toString("HHmm")) < 1130) {
			today = today.minusDays(1);
		}
		return today;
	}

	/**
	 * 下一期次
	 * @return
	 */
	public static String next() {
		DateTime today = new DateTime();
		return today.plusDays(1).toString("yyyy-MM-dd");
	}

	/**
	 * 下一期次
	 * @return
	 */
	public static String dayStr(int n) {
		DateTime today = new DateTime();
		return today.plusDays(n).toString("yyyy-MM-dd");
	}

	/**
	 * 获取查询日期
	 * @param day
	 * @return
	 */
	public static String matchTime(int day) {
		if(day != 0) {
			return today().plusDays(day).toString(JC_PERIOD_END_TIME);
		} else {
			return today().toString(JC_PERIOD_BEGIN_TIME);
		}
	}

}
