package com.caipiao.ticket.util;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.util.StringUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 彩种对应玩法工具类
 * @author kouyi
 * @date 2017-06-26 17:04:00
 */
public class PrizesUtil {
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
}