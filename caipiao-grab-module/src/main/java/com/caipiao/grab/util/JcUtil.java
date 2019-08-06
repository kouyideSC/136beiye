package com.caipiao.grab.util;

import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 竞彩对阵工具类
 * Created by kouyi on 2017/11/06.
 */
public class JcUtil {
	public static Map<String, Integer> leagueSortMap = new HashMap<>();
	public static Map<String, Integer> leagueSortMap_LQ = new HashMap<>();

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
	 * 根据周几和日期获取竞彩期次-即时比分抓取好彩店
	 * @param week
	 * @param date
	 * @return
	 */
	public static String getPeriodDayJsbf(String week, Date date) {
		String weekTmp = DateUtil.getWeekStr(date);
		while (!week.equals(weekTmp)){
			date = DateUtil.addDay(date,-1);
			weekTmp = DateUtil.getWeekStr(date);
		}
		return DateUtil.dateFormat(date, DateUtil.DEFAULT_DATE);
	}

	static {
		//五大联赛
		leagueSortMap.put("英超",1);
		leagueSortMap.put("西甲",2);
		leagueSortMap.put("德甲",3);
		leagueSortMap.put("意甲",4);
		leagueSortMap.put("法甲",5);

		leagueSortMap.put("世界杯",6);
		leagueSortMap.put("欧冠",7);
		leagueSortMap.put("欧罗巴",8);
		leagueSortMap.put("世预赛",9);
		leagueSortMap.put("亚青赛",10);
		leagueSortMap.put("亚预赛",11);
		leagueSortMap.put("亚冠", 12);
		leagueSortMap.put("欧青预赛", 13);

		leagueSortMap.put("荷甲",15);
		leagueSortMap.put("苏超",16);
		leagueSortMap.put("葡超",17);
		leagueSortMap.put("俄超",18);
		leagueSortMap.put("澳超",19);
		leagueSortMap.put("日职",20);
		leagueSortMap.put("韩职",21);
		leagueSortMap.put("比甲",22);
		leagueSortMap.put("瑞超",23);
		leagueSortMap.put("挪超",24);
		leagueSortMap.put("巴甲",25);
		leagueSortMap.put("阿甲",26);
		leagueSortMap.put("美职足",27);
		leagueSortMap.put("墨超",28);
		leagueSortMap.put("智利甲",29);

		leagueSortMap.put("英冠",40);
		leagueSortMap.put("英甲",41);
		leagueSortMap.put("德乙",42);
		leagueSortMap.put("法乙",43);
		leagueSortMap.put("日乙",44);
		leagueSortMap.put("荷乙",45);
		leagueSortMap.put("中北美冠",46);

		leagueSortMap.put("英联赛杯",50);
		leagueSortMap.put("英足总杯",51);
		leagueSortMap.put("英锦标赛",52);
		leagueSortMap.put("苏足总杯",53);
		leagueSortMap.put("国王杯",54);
		leagueSortMap.put("德国杯",55);
		leagueSortMap.put("意大利杯",56);
		leagueSortMap.put("法国杯",57);
		leagueSortMap.put("法联赛杯",58);
		leagueSortMap.put("荷兰杯",59);
		leagueSortMap.put("苏联赛杯",60);
		leagueSortMap.put("葡联赛杯",61);
		leagueSortMap.put("葡萄牙杯",62);
		leagueSortMap.put("澳杯",63);
		leagueSortMap.put("日联赛杯",64);
		leagueSortMap.put("日超杯",65);
		leagueSortMap.put("天皇杯",66);
		leagueSortMap.put("韩足总杯",67);
		leagueSortMap.put("比利时杯",68);
		leagueSortMap.put("瑞典杯",69);
		leagueSortMap.put("挪威杯",70);
		leagueSortMap.put("巴西杯",71);
		leagueSortMap.put("圣保罗锦",72);
		leagueSortMap.put("解放者杯",73);
		leagueSortMap.put("阿根廷杯",74);
		leagueSortMap.put("阿超杯",75);
		leagueSortMap.put("墨西哥杯",76);
		leagueSortMap.put("智利杯",77);
		leagueSortMap.put("智超杯",78);

		leagueSortMap.put("世俱杯",85);
		leagueSortMap.put("优胜者杯",86);
		leagueSortMap.put("俱乐部杯",87);
		leagueSortMap.put("四强赛",88);
		leagueSortMap.put("国际赛",89);
		leagueSortMap.put("亚洲U2",90);
		leagueSortMap.put("女四强赛",91);

		leagueSortMap_LQ.put("美职篮",1);
		leagueSortMap_LQ.put("欧篮联",2);
		leagueSortMap_LQ.put("欧洲篮球",3);
		leagueSortMap_LQ.put("美大学篮",4);
	}
}
