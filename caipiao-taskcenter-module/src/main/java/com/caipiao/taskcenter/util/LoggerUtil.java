package com.caipiao.taskcenter.util;

import com.caipiao.domain.lottery.Period;
import org.slf4j.Logger;

/**
 * 计奖日志输出工具类
 * Created by kouyi on 2017/6/14.
 */
public class LoggerUtil {
	/**
	 * 日志输出-info
	 * @param taskName
	 * @param period
	 * @param info
	 * @param logger
	 */
	public static void printInfo(String taskName, Period period, String info, Logger logger) {
		logger.info("[" + taskName + "] 彩种=" + period.getLotteryId() + " 期次=" + period.getPeriod() + " " + info);
	}

	/**
	 * 日志输出-error-不是异常
	 * @param period
	 * @param info
	 * @param logger
	 */
	public static void printError(String taskName, Period period, String info, Logger logger) {
		logger.error("[" + taskName + "] 彩种=" + period.getLotteryId() + " 期次=" + period.getPeriod() + " " + info);
	}

	/**
	 * 日志输出-error
	 * @param period
	 * @param info
	 * @param e
	 * @param logger
	 */
	public static void printError(String taskName, Period period, String info, Exception e, Logger logger) {
		logger.error("[" + taskName + "] 彩种=" + period.getLotteryId() + " 期次=" + period.getPeriod() + " " + info, e);
	}

	/**
	 * 日志输出-info
	 * @param taskName
	 * @param lotteryId
	 * @param matchID
	 * @param info
	 * @param logger
	 */
	public static void printJcInfo(String taskName, Integer lotteryId, String matchID, String info, Logger logger) {
		logger.info("[" + taskName + "] 彩种=" + lotteryId + " 场次=" + matchID + " " + info);
	}

	/**
	 * 日志输出-error-不是异常
	 * @param lotteryId
	 * @param matchID
	 * @param info
	 * @param logger
	 */
	public static void printJcError(String taskName, Integer lotteryId, String matchID, String info, Logger logger) {
		logger.error("[" + taskName + "] 彩种=" + lotteryId + " 场次=" + matchID + " " + info);
	}

	/**
	 * 日志输出-error
	 * @param lotteryId
	 * @param matchID
	 * @param info
	 * @param e
	 * @param logger
	 */
	public static void printJcError(String taskName, Integer lotteryId, String matchID, String info, Exception e, Logger logger) {
		logger.error("[" + taskName + "] 彩种=" + lotteryId + " 场次=" + matchID + " " + info, e);
	}

	/**
	 * 日志输出-info
	 * @param taskName
	 * @param lotteryId
	 * @param info
	 * @param logger
	 */
	public static void printJcInfo(String taskName, Integer lotteryId, String info, Logger logger) {
		logger.info("[" + taskName + "] 彩种=" + lotteryId + " " + info);
	}

}
