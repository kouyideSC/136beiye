package com.caipiao.taskcenter.award.util;

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
	 * @param matchID
	 * @param info
	 * @param logger
	 */
	public static void printJcInfo(String taskName, String matchID, String info, Logger logger) {
		logger.info("[" + taskName + "] 场次=" + matchID + " " + info);
	}

	/**
	 * 日志输出-error-不是异常
	 * @param matchID
	 * @param info
	 * @param logger
	 */
	public static void printJcError(String taskName, String matchID, String info, Logger logger) {
		logger.error("[" + taskName + "] 场次=" + matchID + " " + info);
	}

	/**
	 * 日志输出-error
	 * @param matchID
	 * @param info
	 * @param e
	 * @param logger
	 */
	public static void printJcError(String taskName, String matchID, String info, Exception e, Logger logger) {
		logger.error("[" + taskName + "] 场次=" + matchID + " " + info, e);
	}

	/**
	 * 日志输出-info
	 * @param taskName
	 * @param info
	 * @param logger
	 */
	public static void printJcInfo(String taskName, String info, Logger logger) {
		logger.info("[" + taskName + "] " + info);
	}

	/**
	 * 日志输出-error
	 * @param taskName
	 * @param info
	 * @param logger
	 */
	public static void printJcError(String taskName, String info, Logger logger) {
		logger.error("[" + taskName + "] " + info);
	}
}
