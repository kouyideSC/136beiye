package com.caipiao.ticket.util;

import com.caipiao.domain.lottery.Period;
import com.caipiao.domain.scheme.Scheme;
import com.caipiao.ticket.vo.SchemeVo;
import org.slf4j.Logger;

/**
 * 票工程日志输出工具类
 * Created by kouyi on 2017/11/28.
 */
public class LoggerUtil {
	/**
	 * 日志输出-info
	 * @param taskName
	 * @param scheme
	 * @param info
	 * @param logger
	 */
	public static void printInfo(String taskName, SchemeVo scheme, String info, Logger logger) {
		logger.info("[" + taskName + "] 彩种=" + scheme.getLotteryId() + " 方案号=" + scheme.getSchemeOrderId() + " " + info);
	}

	/**
	 * 日志输出-error-不是异常
	 * @param taskName
	 * @param scheme
	 * @param info
	 * @param logger
	 */
	public static void printError(String taskName, SchemeVo scheme, String info, Logger logger) {
		logger.info("[" + taskName + "] 彩种=" + scheme.getLotteryId() + " 方案号=" + scheme.getSchemeOrderId() + " " + info);
	}

	/**
	 * 日志输出-error
	 * @param taskName
	 * @param scheme
	 * @param info
	 * @param e
	 * @param logger
	 */
	public static void printError(String taskName, SchemeVo scheme, String info, Exception e, Logger logger) {
		logger.error("[" + taskName + "] 彩种=" + scheme.getLotteryId() + " 方案号=" + scheme.getSchemeOrderId() + " " + info, e);
	}

}
