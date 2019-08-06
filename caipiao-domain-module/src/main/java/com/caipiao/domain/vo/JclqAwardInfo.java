package com.caipiao.domain.vo;

import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.plugin.jcutil.JcItemBean;
import com.caipiao.plugin.jcutil.JcItemCodeUtil;
import com.caipiao.plugin.lqutil.LqItemBean;
import com.caipiao.plugin.lqutil.LqItemCodeUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 竞彩篮球计奖数据对象
 * Created by kouyi on 2017/11/23.
 */
public class JclqAwardInfo implements Serializable {
	private static final long serialVersionUID = 1018812911551064662L;
	private Long id;//唯一编号
	private String matchCode;//对阵编号
	private Integer hScore = -1;//主队得分
	private Integer gScore = -1;//客队得分
	private double lose;//让分或大小分值
	private String score;//完整比分
	private Integer status;//比赛状态（-1:已取消 0-:已停售 1-:销售中 2-:已截止）
	private Integer state;//处理状态（0-待处理 1-自动撤单中 2-赛果获取中 3-已有赛果待审核 4-赛果人工审核成功
	// 5-系统审核成功 6-计算奖金成功 7-奖金汇总成功 8-奖金核对成功 9-自动派奖成功 10-过关统计完成
	// 11-战绩统计完成 12-派送返点成功 99-场次处理结束）
    private Date endTime;//销售截止时间
	private Date matchTime;//比赛时间
	private long bingoCode = -1;//命中匹配

	public void init() {
		long l = 0;
		String key = "";
		if (this.state >= 5 && this.status != 1 && this.status != -1) {//赛果已审核且未取消
			// SF
			if (this.hScore > this.gScore) {
				key = "3";
			} else {
				key = "0";
			}
			int pos = LqItemCodeUtil.getPosition(LqItemCodeUtil.SF, key);
			if (pos < 0) {
				throw new RuntimeException("胜负赛果异常 key=" + key + " 场次=" + matchCode);
			}
			l |= 1L << pos;

			// SFC
			key = this.getSFCKey();
			pos = LqItemCodeUtil.getPosition(LqItemCodeUtil.SFC, key);
			if (pos < 0) {
				throw new RuntimeException("胜分差赛果异常 key=" + key + " 场次=" + matchCode);
			}
			l |= 1L << pos;
		} else {
			if ( this.status == -1) {//场次取消
				l = 0xFFFFFFFFFFFFFFFFL;
			} else {
				l = 0;
			}
		}
		this.bingoCode = l;
	}

	/**
	 * 格式化胜分差
	 * @return
	 */
	private String getSFCKey() {
		int hs = this.hScore;
		int ms = this.gScore;

		int val = hs - ms;
		String key = "";
		if (val > 0) {
			if(val > 0 & val <= 5){
				key = "01";
			} else if (val > 5 & val <= 10){
				key = "02";
			} else if (val > 10 & val <= 15){
				key = "03";
			} else if (val > 15 & val <= 20){
				key = "04";
			} else if (val > 20 & val <= 25){
				key = "05";
			} else {
				key = "06";
			}
		} else {
			val = Math.abs(val);
			if(val > 0 & val <= 5){
				key = "11";
			} else if (val > 5 & val <= 10){
				key = "12";
			} else if (val > 10 & val <= 15){
				key = "13";
			} else if (val > 15 & val <= 20){
				key = "14";
			} else if (val > 20 & val <= 25){
				key = "15";
			} else {
				key = "16";
			}
		}
		return key;
	}

	/**
	 * 获取命中结果
	 * @param item
	 * @param mapTicketSp
	 * @return
	 * @throws Exception
	 */
	public long getBingoCode(LqItemBean item, Map<String, String> mapTicketSp) throws Exception {
		if(this.state >= 5 && this.status != 1 && this.status != -1){
			int playType = getCodePlayType(item);
			if(playType == LqItemCodeUtil.SF || playType == LqItemCodeUtil.SFC){
				return bingoCode;
			}
			//让分胜负和大小分-取出出票让分和大小分值
			String lose = null;
			String[] choose = PluginUtil.splitter(item.getSourceCode(), "/");
			for(int k=0; k<choose.length; k++) {
				String key = item.getItemid() + "_" + playType + "_" + choose[k];
				if(mapTicketSp.containsKey(key)) {
					lose = mapTicketSp.get(key);
					break;
				}
			}
			if(lose == null) {
				throw new Exception("出票SP数据不完整 场次号=" + item.getItemid() + " 玩法=" + playType + " 投注项=" + item.getSourceCode());
			}
			this.lose = Double.parseDouble(lose.split("\\@")[0]);
			return getBingoCode(playType);
		} else {
			return -1;
		}
	}

	//获取RFSF|DXF 赛果
	public long getBingoCode(int playType){
		long l = 0;
		String key = "";
		if (this.state >= 5 && this.status != 1 && this.status != -1) {//赛果已审核且未取消
			int pos = 0;
			// RFSF
			if(playType == LqItemCodeUtil.RFSF){
				if (this.hScore + this.lose > this.gScore) {
					key = "3";
				} else {
					key = "0";
				}
				pos = LqItemCodeUtil.getPosition(LqItemCodeUtil.RFSF, key);
				if (pos < 0) {
					throw new RuntimeException("让球胜负赛果异常 key=" + key + " 场次=" + matchCode);
				}
				l |= 1L << pos;
			}

			// DXF
			if(playType == LqItemCodeUtil.DXF){
				if (this.hScore + this.gScore > this.lose) {
					key = "3";
				} else {
					key = "0";
				}
				pos = LqItemCodeUtil.getPosition(LqItemCodeUtil.DXF, key);
				if (pos < 0) {
					throw new RuntimeException("大小分赛果异常 key=" + key + " 场次=" + matchCode);
				}
				l |= 1L << pos;
			}
		} else {
			if (this.status == 1) {
				l = 0xFFFFFL;
			} else {
				l = 0;
			}
		}
		return l;
	}

	/**
	 * 获取出票sp
	 * @param mapTicketSp
	 * @param item
	 * @return
	 */
	public String getSpValue(Map<String, String> mapTicketSp, LqItemBean item) throws Exception {
		String s = "";
		if (this.status != -1) {
			s = getJcTicketSp(mapTicketSp, item);
		} else {
			s = "1";//比赛取消
		}
		return s;
	}

	/**
	 * 竞彩篮球出票查询SP解析
	 * @param mapTicketSp
	 * @param item
	 * @return
	 */
	public String getJcTicketSp(Map<String, String> mapTicketSp, LqItemBean item) throws Exception {
		String spValue = "";
		int playType = getCodePlayType(item);
		String result = "";
		if (playType == LqItemCodeUtil.SF) {
			result = getSfResult();
		}
		else if (playType == LqItemCodeUtil.RFSF) {
			String lose = null;
			String[] choose = PluginUtil.splitter(item.getSourceCode(), "/");
			for(int k=0; k<choose.length; k++) {
				String key = item.getItemid() + "_" + playType + "_" + choose[k];
				if(mapTicketSp.containsKey(key)) {
					lose = mapTicketSp.get(key);
					break;
				}
			}
			if(lose == null) {
				throw new Exception("出票SP数据不完整 场次号=" + item.getItemid() + " 玩法=" + playType + " 投注项=" + item.getSourceCode());
			}
			result = getRfsfResult(Double.parseDouble(lose.split("\\@")[0]));
		}
		else if (playType == LqItemCodeUtil.SFC) {
			result = getSFCKey();
		}
		else if (playType == LqItemCodeUtil.DXF) {
			String lose = null;
			String[] choose = PluginUtil.splitter(item.getSourceCode(), "/");
			for(int k=0; k<choose.length; k++) {
				String key = item.getItemid() + "_" + playType + "_" + choose[k];
				if(mapTicketSp.containsKey(key)) {
					lose = mapTicketSp.get(key);
					break;
				}
			}
			if(lose == null) {
				throw new Exception("出票SP数据不完整 场次号=" + item.getItemid() + " 玩法=" + playType + " 投注项=" + item.getSourceCode());
			}
			result = getDxfSpValue(Double.parseDouble(lose.split("\\@")[0]));
		}

		String key = item.getItemid() + "_" + playType + "_" + result;
		if(mapTicketSp.containsKey(key)) {
			spValue = mapTicketSp.get(key);
			if(playType == LqItemCodeUtil.RFSF || playType == LqItemCodeUtil.DXF){
				spValue = PluginUtil.splitter(spValue, "@")[1];
			}
		}
		return spValue;
	}

	/**
	 * 获取玩法类型
	 * @param item
	 * @return
	 */
	private static int getCodePlayType(LqItemBean item){
		int type = -1;
		long tl = item.getCountItemType();
		for(int i = LqItemCodeUtil.SF; i < LqItemCodeUtil.HH; i++){
			if(Long.bitCount(tl & (1L << i)) == 1){
				type = i;
				break;
			}
		}
		return type;
	}

	/**
	 * 胜负结果
	 * @return
	 */
	private String getSfResult(){
		int hs = this.hScore;
		int ms = this.gScore;
		String key = "";
		if (hs > ms) {
			key = "3";
		} else {
			key = "0";
		}
		return key;
	}

	/**
	 * 让分胜负结果
	 * @return
	 */
	private String getRfsfResult(double rf){
		double hs = this.hScore + rf;
		int ms = this.gScore;
		String key = "";
		if (hs > ms) {
			key = "3";
		} else {
			key = "0";
		}
		return key;
	}

	/**
	 * 大小分结果
	 * @param dxf
	 * @return
	 */
	private String getDxfSpValue(double dxf){
		int hs = this.hScore + this.gScore;
		String key = "";
		if(dxf < hs){
			key = "3";
		} else {
			key = "0";
		}
		return key;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMatchCode() {
		return matchCode;
	}

	public void setMatchCode(String matchCode) {
		this.matchCode = matchCode;
	}

	public Integer gethScore() {
		return hScore;
	}

	public void sethScore(Integer hScore) {
		this.hScore = hScore;
	}

	public Integer getgScore() {
		return gScore;
	}

	public void setgScore(Integer gScore) {
		this.gScore = gScore;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getMatchTime() {
		return matchTime;
	}

	public void setMatchTime(Date matchTime) {
		this.matchTime = matchTime;
	}

	public double getLose() {
		return lose;
	}

	public void setLose(double lose) {
		this.lose = lose;
	}
}