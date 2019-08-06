package com.caipiao.grab.util;

import java.util.HashMap;
import java.util.Map;

import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.zlk.Schedule;
import org.joda.time.DateTime;

/**
 * 即时比分对阵工具类
 * Created by kouyi on 2017/11/06.
 */
public class GrabForJsbfUtil {
	public static Map<String, String> show = new HashMap<String, String>();
	public static String BEGINFORMAT = "yyyy-MM-dd 08:00:00";
	public static String ENDFORMAT = "yyyy-MM-dd 10:30:00";
	public static int MATCHEXITIME = 30*60*60;
	public static int JSBFEXITIME = 60*60;
	public static String KEYSCHDULE = "a_";
	public static String KEYJSBF = "b_";
	public static String KEYODDSDX = "c_";
	public static String KEYODDSID = "d_";
	public static String KEYANALYSIS = "e_";
	public static String KEYMODEL = "f_";
	
	static {
		show.put("sclassId", "sclassId");
		show.put("matchSeason", "matchSeason");
		show.put("homeTeamId", "homeTeamId");
		show.put("guestTeamId", "guestTeamId");
		show.put("matchTime", "matchTime");
		show.put("homeOrder", "homeOrder");
		show.put("guestOrder", "guestOrder");
		show.put("recommendCount", "recommendCount");
		show.put("hot", "hot");
		show.put("updateTime", "updateTime");
		show.put("flag", "flag");
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
		if(!StringUtil.isEmpty(curSchedule.getMatchTime()) && !StringUtil.isEmpty(preSchedule.getMatchTime()) && preSchedule.getMatchTime().getTime() != curSchedule.getMatchTime().getTime()) {
			preSchedule.setMatchTime(curSchedule.getMatchTime());
		}
		if(!StringUtil.isEmpty(curSchedule.getMatchBeginTime()) && !StringUtil.isEmpty(preSchedule.getMatchBeginTime()) && preSchedule.getMatchBeginTime().getTime() != curSchedule.getMatchBeginTime().getTime()) {
			preSchedule.setMatchBeginTime(curSchedule.getMatchBeginTime());
		}
		if(preSchedule.getMatchState() != -1 && preSchedule.getMatchState() != curSchedule.getMatchState()) {
			preSchedule.setMatchState(curSchedule.getMatchState());
		}
		if(preSchedule.getHomeScore() != curSchedule.getHomeScore()) {
			preSchedule.setHomeScore(curSchedule.getHomeScore());
		}
		if(preSchedule.getGuestScore() != curSchedule.getGuestScore()) {
			preSchedule.setGuestScore(curSchedule.getGuestScore());
		}
		if(preSchedule.getHomeHalfScore() != curSchedule.getHomeHalfScore()) {
			preSchedule.setHomeHalfScore(curSchedule.getHomeHalfScore());
		}
		if(preSchedule.getGuestHalfScore() != curSchedule.getGuestHalfScore()) {
			preSchedule.setGuestHalfScore(curSchedule.getGuestHalfScore());
		}
		if(preSchedule.getHomeRed() != curSchedule.getHomeRed()) {
			preSchedule.setHomeRed(curSchedule.getHomeRed());
		}
		if(preSchedule.getGuestRed() != curSchedule.getGuestRed()) {
			preSchedule.setGuestRed(curSchedule.getGuestRed());
		}
		if(preSchedule.getHomeYellow() != curSchedule.getHomeYellow()) {
			preSchedule.setHomeYellow(curSchedule.getHomeYellow());
		}
		if(preSchedule.getGuestYellow() != curSchedule.getGuestYellow()) {
			preSchedule.setGuestYellow(curSchedule.getGuestYellow());
		}
	}
	
	/**
	 * 对阵数据是否发生变化-当日对阵缓存对比
	 * 
	 * @param sche
	 * @return
	 */
	public static String samedm(Schedule sche) {
		if (sche == null) {
			return "";
		}
		return sche.getScheduleId() + "_" + sche.getMatchTime() + "_" + sche.getMatchBeginTime() + "_"
				+ sche.getMatchState() + "_" + sche.getHomeScore() + "_" + sche.getGuestScore() + "_"
				+ sche.getHomeHalfScore() + "_" + sche.getGuestHalfScore() + "_" + sche.getHomeRed() + "_"
				+ sche.getGuestRed() + "_" + sche.getHomeYellow() + "_" + sche.getGuestYellow() + "_"
				+ sche.getRemark();
	}
	
	/**
	 * 对阵数据是否发生变化-change直播与当日对阵缓存对比
	 * 
	 * @param sche
	 * @return
	 */
	public static String samezm(Schedule sche) {
		if (sche == null) {
			return "";
		}
		return sche.getScheduleId() + "_" + sche.getMatchBeginTime() + "_" + sche.getMatchState() + "_"
				+ sche.getHomeScore() + "_" + sche.getGuestScore() + "_" + sche.getHomeHalfScore() + "_"
				+ sche.getGuestHalfScore() + "_" + sche.getHomeRed() + "_" + sche.getGuestRed() + "_"
				+ sche.getHomeYellow() + "_" + sche.getGuestYellow();
	}
	
	/**
	 * 即时比分当前期键值
	 * @return
	 */
	public static String jsbfkey(int n) {
		return KEYJSBF + todaystr(n);
	}
	
	/**
	 * 当天格式字符串
	 * @return
	 */
	public static String todaystr(int n) {
		return today().plusDays(n).toString("yyyyMMdd");
	}
	
	/**
	 * 当前期
	 * @return
	 */
	public static DateTime today() {
		DateTime today = new DateTime();
		if (Integer.parseInt(today.toString("HHmm")) < 1030) {
			today = today.minusDays(1);
		}
		return today;
	}
	
	
	/**
	 * 获取查询日期
	 * @param day
	 * @return
	 */
	public static String matchTime(int day) {
		if(day != 0) {
			return today().plusDays(day).toString(ENDFORMAT);
		} else {
			return today().toString(BEGINFORMAT);
		}
	}
	
}
