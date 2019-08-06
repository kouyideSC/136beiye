package com.caipiao.ticket.bean;

import com.caipiao.common.constants.LotteryConstants;
import com.caipiao.common.lottery.LotteryUtils;
import com.caipiao.common.util.StringUtil;
import com.caipiao.domain.ticket.SchemeTicket;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.plugin.lqutil.LqItemCodeUtil;
import com.caipiao.ticket.util.HuaYangTicketUtil;
import com.caipiao.ticket.util.JiMiTicketUtil;
import com.caipiao.ticket.util.NuoMiTicketUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 出票sp对象
 * Created by Kouyi on 2017/12/16.
 */
public class SpInfo implements Serializable {
	private static final long serialVersionUID = 729417880575175194L;

	public static void main(String[] args) {
		SchemeTicket ticket = new SchemeTicket();
		String codes = "";
		//codes = "F20171216003,3:4.75/0:1.58//F20171216004,3:3.30";
		//ticket.setPlayTypeId(LotteryConstants.JCZQRQSPF);
		//codes = "171221-003(3_41.88,0_10.45);171221-004(3_32.40)";
		//ticket.setPlayTypeId(LotteryConstants.JCZQCBF);
		//codes = "171221-003(10_47.70,90_28.63);171221-004(00_47.81,99_50.94)";
		//ticket.setPlayTypeId(LotteryConstants.JCZQBQC);
		//codes = "171221-003(33_10.60,31_1.55);171221-004(13_47.37,11_12.80);171221-005(03_40.22,00_11.34)";
		//ticket.setPlayTypeId(LotteryConstants.JCZQJQS);
		//codes = "171221-003(0_11.79,1_18.12);171221-004(6_36.04,7_49.70)";
		//ticket.setPlayTypeId(LotteryConstants.JCLQSF);
		//codes = "20171221-304(3_32.16,0_12.92);20171221-305(0_6.51)";
		//ticket.setPlayTypeId(LotteryConstants.JCLQDXF);
		//codes = "20171221-303(1_150.5_33.48,2_150.5_35.70);20171221-304(1_150.5_7.19,2_150.5_8.12);20171221-305(1_157_2.27)";
		//ticket.setPlayTypeId(LotteryConstants.JCLQRFSF);
		//codes = "20171221-303(3_6.5_11.64,0_6.5_29.23);20171221-304(0_3.5_15.29);20171221-305(3_1.5_24.75,0_1.5_33.22)";
		//ticket.setPlayTypeId(LotteryConstants.JCLQSFC);
		//codes = "20171221-304(7_28.50,12_27.60);20171221-305(1_41.13,6_37.38);20171221-306(7_39.78,1_21.62)";
		//ticket.setPlayTypeId(LotteryConstants.JCZQ);
		//codes = "213^171221-002(33_13.10);210^171221-003(3_11.60,1_24.39);210^171221-004(3_48.81)";
		//ticket.setPlayTypeId(LotteryConstants.JCLQ);
		//codes = "217^20171221-304(7_18.18,12_16.41);214^20171221-305(3_3.5_9.17)";
		//System.out.println(formatCodeSp(codes,ticket));
		//20171214302->DXF=3&150.5@1.75/0&150.5@1.75,20171214303->RFSF=3&-1.5@1.75/0&-1.5@1.75

		Map<String, String> spMap = getSchemeCodeSp("SPF|20171206001=3(1.65)/1(3.30),20171206002=3(1.97)/0(3.20),20171206003=3(1.12)/1(6.55)/0(12.00),20171206004=3(14.00)|2*1,3*1,4*1");
		codes = "HH|20171206001>JQS=3/4,20171206002>RQSPF=3/1,20171206003>RQSPF=3,20171206005>RQSPF=3/1|4*1";
		System.out.println(getTicketSp(codes, spMap));

		Map<String, String> spTicketMap = getSchemeCodeSp("20171206302->RFSF=0&-9.5@1.81,20171206303->DXF=0&216.5@1.69,20171206304->SF=0@4.35", "1960");
		System.out.println(spTicketMap.size());
	}

	/**
	 * 将出票sp串格式化为map-计奖使用
	 * @param codeSp
	 * @return
	 */
	public static Map<String, String> getSchemeCodeSp(String codeSp, String playType) {
		Map<String, String> spMap = new HashMap<>();
		codeSp = codeSp.replaceAll("\\->", "#");
		String[] sps = PluginUtil.splitter(codeSp, ",");
		if (codeSp.indexOf("#") > -1) {//混投
			for (String sp : sps) {
				String[] ms = PluginUtil.splitter(sp, "#");
				String[] alx = PluginUtil.splitter(ms[1], "=");
				int type = getPlayType(alx[0]);
				String[] gs = PluginUtil.splitter(alx[1], "/");
				for (String g : gs) {
					if(type == LqItemCodeUtil.RFSF || type == LqItemCodeUtil.DXF) {
						String[] s = PluginUtil.splitter(g, "&");
						spMap.put(ms[0] + "_" + type + "_" + s[0], s[1]);
					} else {
						String[] s = PluginUtil.splitter(g, "@");
						spMap.put(ms[0] + "_" + type + "_" + s[0], s[1]);
					}
				}
			}
		} else {
			int type = getPlayType(playType);
			for (String sp : sps) {
				String[] ms = PluginUtil.splitter(sp, "=");
				String[] xs = PluginUtil.splitter(ms[1], "/");
				for (String ch : xs) {
					String[] s = PluginUtil.splitter(ch, "@");
					spMap.put(ms[0] + "_" + type + "_" + s[0], s[1]);
				}
			}
		}
		return spMap;
	}

	/**
	 * 根据玩法字符串表示 返回对应int类型定义
	 * @param playType
	 * @return
	 */
	private static int getPlayType(String playType) {
		int r = 0;
		if(playType.equals("SF")){
			r = LqItemCodeUtil.SF;
		} else if (playType.equals("RFSF")) {
			r = LqItemCodeUtil.RFSF;
		} else if (playType.equals("SFC")) {
			r = LqItemCodeUtil.SFC;
		} else if (playType.equals("DXF")) {
			r = LqItemCodeUtil.DXF;
		} else if (playType.equals("1940")) {
			r = LqItemCodeUtil.SF;
		} else if (playType.equals("1950")) {
			r = LqItemCodeUtil.RFSF;
		} else if (playType.equals("1960")) {
			r = LqItemCodeUtil.SFC;
		} else if (playType.equals("1970")) {
			r = LqItemCodeUtil.DXF;
		}
		return r;
	}

	public static String getTicketSp(String codes, Map<String, String> spMap) {
		if(StringUtil.isEmpty(codes) || StringUtil.isEmpty(spMap)) {
			return null;
		}
		String[] cs = PluginUtil.splitter(codes, "|");
		if(cs.length != 3) {
			return null;
		}

		StringBuffer buffer = new StringBuffer();
		String[] sps = PluginUtil.splitter(cs[1], ",");
		if (codes.indexOf(">") > -1) {//混投
			for (int k=0; k<sps.length; k++) {
				String[] ms = PluginUtil.splitter(sps[k], ">");
				buffer.append(ms[0]);
				buffer.append("->");
				String[] xs = PluginUtil.splitter(ms[1], "=");
				buffer.append(xs[0]);
				buffer.append("=");
				String[] ss = PluginUtil.splitter(xs[1], "/");
				for (int n=0; n<ss.length; n++) {
					buffer.append(ss[n].replaceAll("\\:","").replaceAll("\\-",""));
					String key = ms[0] + "->" + xs[0] + "->" + ss[n];
					if(!spMap.containsKey(key)) {
						return null;
					}
					buffer.append(spMap.get(key));
					if(n != ss.length - 1) {
						buffer.append("/");
					}
				}
				if(k != sps.length - 1) {
					buffer.append(",");
				}
			}
		} else {
			for (int k=0; k<sps.length; k++) {
				String[] ms = PluginUtil.splitter(sps[k], "=");
				buffer.append(ms[0]);
				buffer.append("=");
				String[] xs = PluginUtil.splitter(ms[1], "/");
				for (int n=0; n<xs.length; n++) {
					buffer.append(xs[n].replaceAll("\\:","").replaceAll("\\-",""));
					String key = ms[0] + "->" + cs[0] + "->" + xs[n];
					if(!spMap.containsKey(key)) {
						return null;
					}
					buffer.append(spMap.get(key));
					if(n != xs.length - 1) {
						buffer.append("/");
					}
				}
				if(k != sps.length - 1) {
					buffer.append(",");
				}
			}
		}
		return buffer.toString();
	}

	/**
	 * 将用户订单sp串格式化为map用来生成票对应的sp串-算奖使用
	 * @param schemeCodeSp
	 * @return
	 */
	public static Map<String, String> getSchemeCodeSp(String schemeCodeSp) {
		Map<String, String> spMap = new HashMap<>();
		schemeCodeSp = schemeCodeSp.replaceAll("\\(", "&").replaceAll("\\)", "");
		String[] cs = PluginUtil.splitter(schemeCodeSp, "|");
		if(cs.length != 3) {
			return null;
		}

		String[] tdan = PluginUtil.splitter(cs[1], "$");
		for(String dan : tdan) {
			String[] sps = PluginUtil.splitter(dan, ",");
			if (schemeCodeSp.indexOf(">") > -1) {//混投
				for (String sp : sps) {
					String[] ms = PluginUtil.splitter(sp, ">");
					String[] xs = PluginUtil.splitter(ms[1], "+");
					for (String ch : xs) {
						String[] alx = PluginUtil.splitter(ch, "=");
						String[] fs = PluginUtil.splitter(alx[0], "&");//针对让球胜平负、让分胜负、大小分处理分值
						if (alx[0].indexOf(LotteryConstants.JCWF_PREFIX_RQSPF) > -1 || alx[0].indexOf(LotteryConstants.JCWF_PREFIX_RFSF) > -1 || alx[0].indexOf(LotteryConstants.JCWF_PREFIX_DXF) > -1) {
							alx[0] = fs[0];
						}
						String[] gs = PluginUtil.splitter(alx[1], "/");
						for (String g : gs) {
							String[] s = PluginUtil.splitter(g, "&");
							if (alx[0].indexOf(LotteryConstants.JCWF_PREFIX_RFSF) > -1 || alx[0].indexOf(LotteryConstants.JCWF_PREFIX_DXF) > -1) {
								spMap.put(ms[0] + "->" + alx[0] + "->" + s[0], "&" + fs[1] + "@" + s[1]);
							} else {
								spMap.put(ms[0] + "->" + alx[0] + "->" + s[0], "@" + s[1]);
							}
						}
					}
				}
			} else {
				for (String sp : sps) {
					String[] ms = PluginUtil.splitter(sp, "=");
					String[] fs = PluginUtil.splitter(ms[0], "&");//针对让球胜平负、让分胜负、大小分处理分值
					if (cs[0].indexOf(LotteryConstants.JCWF_PREFIX_RQSPF) > -1 || cs[0].indexOf(LotteryConstants.JCWF_PREFIX_RFSF) > -1 || cs[0].indexOf(LotteryConstants.JCWF_PREFIX_DXF) > -1) {
						ms[0] = fs[0];
					}
					String[] xs = PluginUtil.splitter(ms[1], "/");
					for (String ch : xs) {
						String[] alx = PluginUtil.splitter(ch, "&");
						if (cs[0].indexOf(LotteryConstants.JCWF_PREFIX_RFSF) > -1 || cs[0].indexOf(LotteryConstants.JCWF_PREFIX_DXF) > -1) {
							spMap.put(ms[0] + "->" + cs[0] + "->" + alx[0], "&" + fs[1] + "@" + alx[1]);
						} else {
							spMap.put(ms[0] + "->" + cs[0] + "->" + alx[0], "@" + alx[1]);
						}
					}
				}
			}
		}
		return spMap;
	}
}