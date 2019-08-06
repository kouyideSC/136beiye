package com.caipiao.ticket.vote.huayang.format;

import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.plugin.Lottery1010;
import com.caipiao.plugin.Lottery1560;
import com.caipiao.plugin.helper.GameCastMethodDef;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.sturct.GameCastCode;
import com.caipiao.ticket.bean.CodeInfo;
import com.caipiao.ticket.vote.base.AbstractCodeFormat;

/**
 * 十一运夺金
 * Created by kouyi on 2017/03/29.
 */
public class CodeFormat1560 extends AbstractCodeFormat {
	/**
	 * 格式化投注信息为出票商格式
	 * @param ticket
	 * @param plugin
	 * @return
	 */
	public CodeInfo getCodeBean(SchemeTicket ticket, GamePluginAdapter plugin){
		CodeInfo cb = new CodeInfo();
		try {
			GameCastCode[] gccs = plugin.parseGameCastCodes(ticket.getCodes());
			if(gccs.length > 5) {
				cb.setErrorCode(3);//单张票不能超过5注单式票
				return cb;
			}

			int sumMoney = 0;
			String saleType = "";
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < gccs.length; i++) {
				GameCastCode gcc = gccs[i];
				String code = "";
				if (gcc.getCastMethod() == GameCastMethodDef.CASTTYPE_SINGLE || gcc.getCastMethod() == GameCastMethodDef.CASTTYPE_MULTI) {// 单式、复式
					if (gcc.getPlayMethod() == Lottery1560.Q2) {// 前二直选
						if (gcc.getCastMoney() > 2) {
							saleType = "3";// 复式
							code = getSource(gcc.getFirst(), 0, DH_SPLIT) + XH_SPLIT + getSource(gcc.getSecond(), 0, DH_SPLIT);
						} else {
							saleType = "0";// 单式
							code = getSource(gcc.getFirst(), 0, DH_SPLIT) + getSource(gcc.getSecond(), 0, DH_SPLIT);
						}
						cb.setPlayType("9");
					} else if (gcc.getPlayMethod() == Lottery1560.Q3) {// 前三直选
						if (gcc.getCastMoney() > 2) {
							saleType = "3";// 复式
							code = getSource(gcc.getFirst(), 0, DH_SPLIT) + XH_SPLIT + getSource(gcc.getSecond(), 0, DH_SPLIT) + XH_SPLIT + getSource(gcc.getThird(), 0, DH_SPLIT);
						} else {
							saleType = "0";// 单式
							code = getSource(gcc.getFirst(), 0, DH_SPLIT) + getSource(gcc.getSecond(), 0, DH_SPLIT) + getSource(gcc.getThird(), 0, DH_SPLIT);
						}
						cb.setPlayType("10");

					} else if (gcc.getPlayMethod() == Lottery1560.R8) {// 任选八
						if (gcc.getCastMoney() > 2) {
							//不支持玩法
						} else {
							saleType = "0";// 单式
							code = getSource(gcc.getFirst(), 0, DH_SPLIT);
						}
						cb.setPlayType("8");
					} else {
						if (gcc.getPlayMethod() == Lottery1560.R1) {
							cb.setPlayType("1");// 任选一
						} else if (gcc.getPlayMethod() == Lottery1560.R2) {
							cb.setPlayType("2");// 任选二
						} else if (gcc.getPlayMethod() == Lottery1560.R3) {
							cb.setPlayType("3");// 任选三
						} else if (gcc.getPlayMethod() == Lottery1560.R4) {
							cb.setPlayType("4");// 任选四
						} else if (gcc.getPlayMethod() == Lottery1560.R5) {
							cb.setPlayType("5");// 任选五
						} else if (gcc.getPlayMethod() == Lottery1560.R6) {
							cb.setPlayType("6");// 任选六
						} else if (gcc.getPlayMethod() == Lottery1560.R7) {
							cb.setPlayType("7");// 任选七
						} else if (gcc.getPlayMethod() == Lottery1560.Z2) {
							cb.setPlayType("11");// 前二组选
						} else if (gcc.getPlayMethod() == Lottery1560.Z3) {
							cb.setPlayType("12");// 前三组选
						}
						if (gcc.getCastMoney() > 2) {
							saleType = "1";// 复式
						} else {
							saleType = "0";// 单式
						}
						code = getSource(gcc.getFirst(), 0, DH_SPLIT);
					}
				} else if (gcc.getCastMethod() == GameCastMethodDef.CASTTYPE_DANTUO) {// 胆拖
					if (gcc.getPlayMethod() == Lottery1560.R1 || gcc.getPlayMethod() == Lottery1560.Q2 || gcc.getPlayMethod() == Lottery1560.Q3) {
						return null;
					}
					if (gcc.getPlayMethod() == Lottery1560.R2) {
						cb.setPlayType("2");// 任选二
					} else if (gcc.getPlayMethod() == Lottery1560.R3) {
						cb.setPlayType("3");// 任选三
					} else if (gcc.getPlayMethod() == Lottery1560.R4) {
						cb.setPlayType("4");// 任选四
					} else if (gcc.getPlayMethod() == Lottery1560.R5) {
						cb.setPlayType("5");// 任选五
					} else if (gcc.getPlayMethod() == Lottery1560.R6) {
						cb.setPlayType("6");// 任选六
					} else if (gcc.getPlayMethod() == Lottery1560.R7) {
						cb.setPlayType("7");// 任选七
					} else if (gcc.getPlayMethod() == Lottery1560.Z2) {
						cb.setPlayType("11");// 前二组选
					} else if (gcc.getPlayMethod() == Lottery1560.Z3) {
						cb.setPlayType("12");// 前三组选
					}
					code = getSource(gcc.getFirst(), 0, DH_SPLIT) + XH_SPLIT + getSource(gcc.getSecond(), 0, DH_SPLIT);
					saleType = "2";//拖胆
				} else {
					return null;
				}
				sb.append(code.replaceAll(",", ""));
				if(i != gccs.length - 1) {
					sb.append(JH_SPLIT);
				}
				sumMoney += gcc.getCastMoney();
			}

			if(sumMoney * ticket.getMultiple() != ticket.getMoney().intValue()) {//判断金额是否一致，防止票表被修改
				cb.setErrorCode(3);
			} else {
				cb.setCode(sb.toString());
				cb.setOrderId(ticket.getTicketId());
				cb.setMoney(ticket.getMoney().intValue());
				cb.setMultiple(ticket.getMultiple());
				cb.setZhuShu(cb.getMoney() / cb.getMultiple() / 2);
				cb.setSaleCode(saleType);
			}
		} catch (Exception e) {
			logger.error("[华阳快频提票]-> 格式化票信息异常 票号=" + ticket.getTicketId(), e);
			cb.setErrorCode(3);
		}
		return cb;
	}

	public static void main(String[] args) throws Exception {
		String codes="01,03,05,06,07:5:2";
		Lottery1560 gcp = new Lottery1560();
		CodeFormat1560 gcc = new CodeFormat1560();
		SchemeTicket t = new SchemeTicket();
		t.setMoney(2d);
		t.setMultiple(1);
		t.setCodes(codes);
		t.setLotteryId("1560");
		CodeInfo bean=gcc.getCodeBean(t, gcp);
		System.out.println(bean.getCode());
		System.out.println(bean.getSaleCode());
		System.out.println(bean.getPlayType());
	}
}
