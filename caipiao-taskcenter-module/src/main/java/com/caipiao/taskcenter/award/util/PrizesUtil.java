package com.caipiao.taskcenter.award.util;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.util.DateUtil;
import com.caipiao.common.util.StringUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 彩种对应玩法工具类-用以取奖级使用
 * @author kouyi
 * @date 2017-06-26 17:04:00
 */
public class PrizesUtil {
	public static final int[] p5 = new int[]{100000};//排列5固定奖金
	public static final int[] p3 = new int[]{1040, 346, 173};//排列3、福彩3D固定奖金
	public static final int[] qxc = new int[]{-1, -1, 1800, 300, 20, 5};//七星彩固定奖金
	public static final int[] qlc = new int[]{-1, -1, -1, 200, 50, 10, 5};//七乐彩固定奖金
	public static final int[] ssq = new int[]{-1, -1, 3000, 200, 10, 5};//双色球固定奖金
	public static final int[] dlt = new int[]{-1, -1, 10000, 3000, 300, 200, 100, 15, 5};//大乐透固定奖金
	public static final int[] dlt_ls = new int[]{-1, -1, -1, 5000, 1000, 500, 50, 5, 3};//大乐透乐善玩法固定奖金
	public static final int[] x115 = new int[]{13, 6, 19, 78, 540, 90, 26, 9, 130, 1170, 65, 195};//11选5固定奖金
	public static final int[] k3 = new int[]{80, 40, 25, 16, 12, 10, 9, 9, 10, 12, 16, 25, 40, 80, 40, 240, 40, 10, 15, 80, 8};//快3固定奖金
	public static final int[] jqc = new int[]{-1};//进球彩固定奖金
	public static final int[] bqc = new int[]{-1};//半全场固定奖金
	public static final int[] sfc = new int[]{-1,-1};//胜负彩固定奖金
	public static final int[] r9 = new int[]{-1};//任九固定奖金

	//排列3,福彩3D-3个奖级
	public static final String DIRECT_PRIZE = "直选";
	public static final String GROUP3_PRIZE = "组三";
	public static final String GROUP6_PRIZE = "组六";

	//七星彩,双色球-6个奖级 | 七乐彩
	public static final String ONE_PRIZE = "一等奖";
	public static final String TWO_PRIZE = "二等奖";
	public static final String THREE_PRIZE = "三等奖";
	public static final String FOUR_PRIZE = "四等奖";
	public static final String FIVE_PRIZE = "五等奖";
	public static final String SIX_PRIZE = "六等奖";

	//七乐彩-7个奖级
	public static final String SEVEN_PRIZE = "七等奖";
	//大乐透-9个奖级
	public static final String EIGHT_PRIZE = "八等奖";
	public static final String NINE_PRIZE = "九等奖";

	//胜负彩,任九,六场半全场,四场进球--1个奖级
	//胜负彩--2个奖级

	//11选5--12个奖级
	public static final String XW115_ONE_PRIZE = "前一直选";
	public static final String XW115_TWO_PRIZE = "任选二";
	public static final String XW115_THREE_PRIZE = "任选三";
	public static final String XW115_FOUR_PRIZE = "任选四";
	public static final String XW115_FIVE_PRIZE = "任选五";
	public static final String XW115_SIX_PRIZE = "任选六";
	public static final String XW115_SEVEN_PRIZE = "任选七";
	public static final String XW115_EIGHT_PRIZE = "任选八";
	public static final String XW115_TWO_DIRECT_PRIZE = "前二直选";
	public static final String XW115_THREE_DIRECT_PRIZE = "前三直选";
	public static final String XW115_TWO_GROUP_PRIZE = "前二组选";
	public static final String XW115_THREE_GROUP_PRIZE = "前三组选";

	//快3-21个奖级
	public static final String K3_SUM_4_PRIZE = "和值4";
	public static final String K3_SUM_5_PRIZE = "和值5";
	public static final String K3_SUM_6_PRIZE = "和值6";
	public static final String K3_SUM_7_PRIZE = "和值7";
	public static final String K3_SUM_8_PRIZE = "和值8";
	public static final String K3_SUM_9_PRIZE = "和值9";
	public static final String K3_SUM_10_PRIZE = "和值10";
	public static final String K3_SUM_11_PRIZE = "和值11";
	public static final String K3_SUM_12_PRIZE = "和值12";
	public static final String K3_SUM_13_PRIZE = "和值13";
	public static final String K3_SUM_14_PRIZE = "和值14";
	public static final String K3_SUM_15_PRIZE = "和值15";
	public static final String K3_SUM_16_PRIZE = "和值16";
	public static final String K3_SUM_17_PRIZE = "和值17";
	public static final String K3_THREE_SAME_ALL_PRIZE = "三同号通选";
	public static final String K3_THREE_SAME_ALONE_PRIZE = "三同号单选";
	public static final String K3_THREE_DIFFERENT_PRIZE = "三不同号";
	public static final String K3_THREE_LINK_ALL_PRIZE = "三连号通选";
	public static final String K3_TWO_SAME_PRIZE = "二同号复选";
	public static final String K3_TWO_SAME_ALONE_PRIZE = "二同号单选";
	public static final String K3_TWO_DIFFERENT_PRIZE = "二不同号";

	public final static String defaultVote = "9696";

	/**
	 * 获取彩种奖级固定奖金-计算奖金使用
	 * @param lotId
	 * @param index
	 * @return
	 */
	public static int fixedMoney(String lotId, int index){
		switch (lotId) {
			case LotteryConstants.PL5:
				return p5[index];
			case LotteryConstants.PL3:
			case LotteryConstants.FC3D:
				return p3[index];
			case LotteryConstants.SSQ:
				return ssq[index];
			case LotteryConstants.QXC:
				return qxc[index];
			case LotteryConstants.DLT:
				return dlt[index];
			case LotteryConstants.DLT_LS://大乐透乐善玩法奖级
				return dlt_ls[index];
			case LotteryConstants.QLC:
				return qlc[index];
			case LotteryConstants.X511_SD:
			case LotteryConstants.X511_SH:
			case LotteryConstants.X511_GD:
				return x115[index];
			case LotteryConstants.K3_AH:
			case LotteryConstants.K3_JL:
			case LotteryConstants.K3_JS:
				return k3[index];
			case LotteryConstants.JQC:
				return jqc[index];
			case LotteryConstants.BQC:
				return bqc[index];
			case LotteryConstants.SFC:
				return sfc[index];
			case LotteryConstants.RXJ:
				return r9[index];
			default:
				return -1;
		}
	}

	/**
	 * 获取彩种奖级长度-计算奖金使用
	 * @param lotId
	 * @return
	 */
	public static int gradeNumber(String lotId){
		switch (lotId) {
			case LotteryConstants.JQC:
			case LotteryConstants.BQC:
			case LotteryConstants.RXJ:
			case LotteryConstants.PL5:
				return 1;
			case LotteryConstants.SFC:
				return 2;
			case LotteryConstants.PL3:
			case LotteryConstants.FC3D:
				return 3;
			case LotteryConstants.SSQ:
			case LotteryConstants.QXC:
				return 6;
			case LotteryConstants.QLC:
				return 7;
			case LotteryConstants.DLT:
				return 9;
			case LotteryConstants.X511_SD:
			case LotteryConstants.X511_SH:
			case LotteryConstants.X511_GD:
				return 12;
			case LotteryConstants.K3_AH:
			case LotteryConstants.K3_JL:
			case LotteryConstants.K3_JS:
				return 21;
			default:
				return 0;
		}
	}

	/**
	 * 根据彩种和索引获取奖级公共类-计算奖金使用
	 * @param lotId
	 * @param index
	 * @return
	 */
	public static String getPrizeName(String lotId, int index) {
		switch (lotId) {
			case LotteryConstants.JQC:
			case LotteryConstants.BQC:
			case LotteryConstants.RXJ:
			case LotteryConstants.PL5:
				return getYDJPrizeName(index);
			case LotteryConstants.SFC:
				return getEDJPrizeName(index);
			case LotteryConstants.PL3:
			case LotteryConstants.FC3D:
				return getSDJPrizeName(index);
			case LotteryConstants.X511_SD:
			case LotteryConstants.X511_SH:
			case LotteryConstants.X511_GD:
				return get11x5PrizeName(index);
			case LotteryConstants.SSQ:
			case LotteryConstants.QLC:
			case LotteryConstants.QXC:
			case LotteryConstants.DLT:
				return getLQBPrizeName(index);
			case LotteryConstants.K3_AH:
			case LotteryConstants.K3_JL:
			case LotteryConstants.K3_JS:
				return getK3PrizeName(index);
			default:
				return "";
		}
	}

	//只有一个奖级的彩种奖级
	public static String getYDJPrizeName(int index) {
		switch (index) {
			case 0:
				return ONE_PRIZE;
			default:
				return "";
		}
	}

	//有两个奖级的彩种奖级
	public static String getEDJPrizeName(int index) {
		switch (index) {
			case 0:
				return ONE_PRIZE;
			case 1:
				return TWO_PRIZE;
			default:
				return "";
		}
	}

	//有三个奖级的彩种奖级
	public static String getSDJPrizeName(int index) {
		switch (index) {
			case 0:
				return DIRECT_PRIZE;
			case 1:
				return GROUP3_PRIZE;
			case 2:
				return GROUP6_PRIZE;
			default:
				return "";
		}
	}

	//11选5奖级
	public static String get11x5PrizeName(int index) {
		switch (index) {
			case 0:
				return XW115_ONE_PRIZE;
			case 1:
				return XW115_TWO_PRIZE;
			case 2:
				return XW115_THREE_PRIZE;
			case 3:
				return XW115_FOUR_PRIZE;
			case 4:
				return XW115_FIVE_PRIZE;
			case 5:
				return XW115_SIX_PRIZE;
			case 6:
				return XW115_SEVEN_PRIZE;
			case 7:
				return XW115_EIGHT_PRIZE;
			case 8:
				return XW115_TWO_DIRECT_PRIZE;
			case 9:
				return XW115_THREE_DIRECT_PRIZE;
			case 10:
				return XW115_TWO_GROUP_PRIZE;
			case 11:
				return XW115_THREE_GROUP_PRIZE;
			default:
				return "";
		}
	}

	//有六、七、九个奖级的彩种奖级
	public static String getLQBPrizeName(int index) {
		switch (index) {
			case 0:
				return ONE_PRIZE;
			case 1:
				return TWO_PRIZE;
			case 2:
				return THREE_PRIZE;
			case 3:
				return FOUR_PRIZE;
			case 4:
				return FIVE_PRIZE;
			case 5:
				return SIX_PRIZE;
			case 6:
				return SEVEN_PRIZE;
			case 7:
				return EIGHT_PRIZE;
			case 8:
				return NINE_PRIZE;
			default:
				return "";
		}
	}

	//快三奖级
	public static String getK3PrizeName(int index) {
		switch (index) {
			case 0:
				return K3_SUM_4_PRIZE;
			case 1:
				return K3_SUM_5_PRIZE;
			case 2:
				return K3_SUM_6_PRIZE;
			case 3:
				return K3_SUM_7_PRIZE;
			case 4:
				return K3_SUM_8_PRIZE;
			case 5:
				return K3_SUM_9_PRIZE;
			case 6:
				return K3_SUM_10_PRIZE;
			case 7:
				return K3_SUM_11_PRIZE;
			case 8:
				return K3_SUM_12_PRIZE;
			case 9:
				return K3_SUM_13_PRIZE;
			case 10:
				return K3_SUM_14_PRIZE;
			case 11:
				return K3_SUM_15_PRIZE;
			case 12:
				return K3_SUM_16_PRIZE;
			case 13:
				return K3_SUM_17_PRIZE;
			case 14:
				return K3_THREE_SAME_ALL_PRIZE;
			case 15:
				return K3_THREE_SAME_ALONE_PRIZE;
			case 16:
				return K3_THREE_DIFFERENT_PRIZE;
			case 17:
				return K3_THREE_LINK_ALL_PRIZE;
			case 18:
				return K3_TWO_SAME_PRIZE;
			case 19:
				return K3_TWO_SAME_ALONE_PRIZE;
			case 20:
				return K3_TWO_DIFFERENT_PRIZE;
			default:
				return "";
		}
	}

	/**
	 * 简单判断开奖号码合法性
	 * @param codes
	 * @param lotId
	 * @return
	 */
	public static boolean isNumber(String codes, String lotId) {
		if(StringUtil.isEmpty(codes)) {
			return false;
		}

		boolean success = false;
		switch (lotId) {
			case LotteryConstants.FC3D://福彩3D
			case LotteryConstants.K3_AH://安徽快3
			case LotteryConstants.K3_JL://吉林快3
			case LotteryConstants.K3_JS://江苏快3
			case LotteryConstants.PL3://排列3
				success = codes.length() == 5;
				break;
			case LotteryConstants.PL5://排列5
			case LotteryConstants.SSC_CQ://重庆时时彩
				success = codes.length() == 9;
				break;
			case LotteryConstants.QXC://七星彩
				success = codes.length() == 13;
				break;
			case LotteryConstants.X511_SD://山东11选5
			case LotteryConstants.X511_SH://上海11选5
			case LotteryConstants.X511_GD://广东11选5
				success = codes.length() == 14;
				break;
			case LotteryConstants.JQC://进球彩
				success = codes.length() == 15;
				break;
			case LotteryConstants.SSQ://双色球
			case LotteryConstants.DLT://大乐透
				success = codes.length() == 20;
				break;
			case LotteryConstants.BQC://半全场
			case LotteryConstants.QLC://七乐彩
				success = codes.length() == 23;
				break;
			case LotteryConstants.SFC://胜负彩
			case LotteryConstants.RXJ://任九
				success = codes.length() == 27;
				codes = codes.replaceAll("\\*", "3");
				break;
			case LotteryConstants.GJ://冠军
			case LotteryConstants.GYJ://冠亚军
				success = (codes.length() == 1 || codes.length() == 2);
				break;
			default:
				success = false;
		}

		codes = codes.replaceAll("\\|","").replaceAll("\\#","").replaceAll(",","");
		if(StringUtil.isEmpty(codes)) {//排除全符号
			return false;
		}
		return success &= codes.matches("[0-9]+");
	}

	//判断是否数字
	public static boolean isNumber(String codes) {
		if (StringUtil.isEmpty(codes)) {
			return false;
		}
		return codes.matches("[0-9]+");
	}

	/**
	 * 格式化计奖比例范围-返回map
	 * @param addBonusRate
	 * @return
	 */
	public static Map<String, Double> initRateMap(String addBonusRate) {
		Map<String, Double> map = new HashMap<>();
		if(StringUtil.isEmpty(addBonusRate)) {
			return map;
		}
		String[] bonusRates = addBonusRate.split("\\/");
		for(String rate : bonusRates) {
			String[] rates = rate.split("\\$");
			if(rates.length != 3) {
				continue;
			}
			if(rates[1].equals("*")) {
				rates[1] = "999999999";
			}
			if(StringUtil.parseDouble(rates[0]) >= StringUtil.parseDouble(rates[1])) {
				continue;//开始金额不能大于结束金额
			}
			map.put(StringUtil.parseDouble(rates[0]) + "|" + StringUtil.parseDouble(rates[1]), StringUtil.parseDouble(rates[2]));
		}
		return map;
	}

	/**
	 * 根据奖金返回加奖比例
	 * @param prizeTax
	 * @return
	 */
	public static double getAddBonusRate(Map<String, Double> map, double prizeTax) {
		if(StringUtil.isEmpty(map) || prizeTax <= 0) {
			return 0;
		}
		Iterator<Map.Entry<String, Double>> iterator = map.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry<String, Double> entry = iterator.next();
			String[] keys = entry.getKey().split("\\|");
			if(prizeTax > StringUtil.parseDouble(keys[0]) && prizeTax <= StringUtil.parseDouble(keys[1])) {
				return entry.getValue();
			}
		}
		return 0;
	}

}