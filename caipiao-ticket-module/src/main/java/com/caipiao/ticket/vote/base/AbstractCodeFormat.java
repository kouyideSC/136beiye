package com.caipiao.ticket.vote.base;

import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.ticket.bean.CodeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 竞彩投注串转换抽象类
 * Created by kouyi on 2017/12/13.
 */
public abstract class AbstractCodeFormat {
	protected static Logger logger = LoggerFactory.getLogger(AbstractCodeFormat.class);
	public final String DL_SPLIT = "$";
	public final String XH_SPLIT = "*";
	public final String JH_SPLIT = "^";
	public final String SX_SPLIT = "|";
	public final String MH_SPLIT = ":";
	public final String DH_SPLIT = ",";
	public final String FH_SPLIT = ";";
	public final String DY_SPLIT = "=";
	public final String DF_SPLIT = ">";
	public final String SG_SPLIT = "//";
	public final String DG_SPLIT = "/";
	public final String HG_SPLIT = "-";

	//格式化投注信息为出票商格式
	public abstract CodeInfo getCodeBean(SchemeTicket ticket, GamePluginAdapter plugin);

	public String getSource(Long l, int type, String split) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 64; i++) {
			long t = 1L << i;
			if ((l & t) == t) {
				if (type == 0) {
					if (i < 10) {
						sb.append("0").append(i);
					} else {
						sb.append(i);
					}
				} else {
					sb.append(i);
				}
				if (split != null && split.length() > 0) {
					sb.append(split);
				}
			}
		}
		String s = sb.toString();
		if (split != null && split.length() > 0) {
			if (s.length() > split.length()) {
				s = s.substring(0, s.length() - split.length());
			}
		}
		return s;
	}

	public String getSourceZero(Long l) {
		StringBuffer sb = new StringBuffer();
		if (l < 10) {
			sb.append("0").append(l);
		} else {
			sb.append(l);
		}
		return sb.toString();
	}

}
