package com.caipiao.grab.util;

import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.zlk.Schedule;
import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 竞彩对阵工具类
 * Created by kouyi on 2017/11/06.
 */
public class GrabForJcUtil {

	public static Map<String, String> teamNameMap = new HashMap<>();
	public static Map<String, String> zqTeamNameMap = new HashMap<>();
	static {
		teamNameMap.put("圣安东尼奥马刺","马刺");
		teamNameMap.put("孟菲斯灰熊","灰熊");
		teamNameMap.put("休斯敦火箭","火箭");
		teamNameMap.put("达拉斯独行侠","独行侠");
		teamNameMap.put("夏洛特黄蜂","黄蜂");
		teamNameMap.put("萨克拉门托国王","国王");
		teamNameMap.put("菲尼克斯太阳","太阳");
		teamNameMap.put("洛杉矶湖人","湖人");
		teamNameMap.put("洛杉矶快船","快船");
		teamNameMap.put("金州勇士","勇士");
		teamNameMap.put("犹他爵士","爵士");
		teamNameMap.put("波特兰开拓者","开拓者");
		teamNameMap.put("丹佛掘金","掘金");
		teamNameMap.put("明尼苏达森林狼","森林狼");
		teamNameMap.put("俄克拉荷马城雷霆","雷霆");
		teamNameMap.put("亚特兰大老鹰","老鹰");
		teamNameMap.put("夏洛特山猫","山猫");
		teamNameMap.put("迈阿密热火","热火");
		teamNameMap.put("奥兰多魔术","魔术");
		teamNameMap.put("华盛顿奇才","奇才");
		teamNameMap.put("芝加哥公牛","公牛");
		teamNameMap.put("密尔沃基雄鹿","雄鹿");
		teamNameMap.put("底特律活塞","活塞");
		teamNameMap.put("印第安那步行者","步行者");
		teamNameMap.put("克利夫兰骑士","骑士");
		teamNameMap.put("纽约尼克斯","尼克斯");
		teamNameMap.put("波士顿凯尔特人","凯尔特人");
		teamNameMap.put("费城76人","76人");
		teamNameMap.put("多伦多猛龙","猛龙");
		teamNameMap.put("布鲁克林篮网","篮网");
		teamNameMap.put("新奥尔良鹈鹕","鹈鹕");

		zqTeamNameMap.put("曼彻斯特联","曼联");
		zqTeamNameMap.put("曼彻斯特城","曼城");
		zqTeamNameMap.put("皇家马德里","皇马");
		zqTeamNameMap.put("阿雅克肖GFCO","阿雅GF");
		zqTeamNameMap.put("阿尔克马尔青年队","阿尔克青");
		zqTeamNameMap.put("阿贾克斯青年队","阿贾克青");
		zqTeamNameMap.put("乌德勒支青年队","乌德勒青");
		zqTeamNameMap.put("埃因霍温青年队","埃因霍青");
		zqTeamNameMap.put("沙尔克04","沙尔克");
		zqTeamNameMap.put("拉普拉塔体操","拉普体操");
		zqTeamNameMap.put("拉普拉塔大学生","拉大学生");
		zqTeamNameMap.put("IFK哥德堡","哥德堡");
		zqTeamNameMap.put("拜仁慕尼黑","拜仁");
		zqTeamNameMap.put("谢菲尔德星期三","谢周三");
		zqTeamNameMap.put("布里斯托尔城","布里斯城");
		zqTeamNameMap.put("布里斯托尔流浪","布里斯流");
		zqTeamNameMap.put("基尔马诺克","基马诺克");
		zqTeamNameMap.put("巴黎圣日尔曼","巴黎");
		zqTeamNameMap.put("阿斯顿维拉","维拉");
		zqTeamNameMap.put("门兴格拉德巴赫","门兴");
		zqTeamNameMap.put("PSV埃因霍温","埃因霍温");
		zqTeamNameMap.put("竞技俱乐部","竞技");
		zqTeamNameMap.put("圣彼得堡泽尼特","泽尼特");
		zqTeamNameMap.put("莫斯科中央陆军","莫陆军");
		zqTeamNameMap.put("莫斯科斯巴达","莫斯巴达");
		zqTeamNameMap.put("莫斯科火车头","莫火车头");
		zqTeamNameMap.put("莫斯科迪纳摩","莫迪纳摩");
		zqTeamNameMap.put("马德里竞技","马竞");
		zqTeamNameMap.put("维戈塞尔塔","塞尔塔");
		zqTeamNameMap.put("托特纳姆热刺","热刺");
		zqTeamNameMap.put("埃因霍温FC","埃因FC");
	}

	/**
	 * 根据周几和日期获取竞彩期次
	 * @param week
	 * @param date
	 * @return
	 */
	public static String getPeriodDay(String week, Date date) {
		String weekTmp = DateUtil.getWeekStr(date);
		while (!week.equals(weekTmp)){
			date = DateUtil.addDay(date,-1);
			weekTmp = DateUtil.getWeekStr(date);
		}
		return DateUtil.dateFormat(date, DateUtil.DEFAULT_DATE1);
	}

	/**
	 * NBA球队简称
	 * @param name
	 * @return
	 */
	public static String getShortName(String name) {
		if (StringUtil.isEmpty(name)) {
			return name;
		}
		String shortName = teamNameMap.get(name);
		if (StringUtil.isNotEmpty(shortName)) {
			return shortName;
		}
		return name;
	}

	/**
	 * 足球球队简称
	 * @param name
	 * @return
	 */
	public static String getShortNameZq(String name) {
		if (StringUtil.isEmpty(name)) {
			return name;
		}
		String shortName = zqTeamNameMap.get(name);
		if (StringUtil.isNotEmpty(shortName)) {
			return shortName;
		}
		return name;
	}

}
