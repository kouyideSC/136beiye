package com.caipiao.domain.vo;

import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.plugin.jcutil.JcItemBean;
import com.caipiao.plugin.jcutil.JcItemCodeUtil;
import com.github.pagehelper.StringUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 竞彩计奖数据对象
 * Created by kouyi on 2017/12/27.
 */
public class JczqAwardInfo implements Serializable {
	private static final long serialVersionUID = 8782252252809006167L;
	private Long id;//唯一编号
	private String matchCode;//对阵编号
	private Integer hScore = -1;//主队得分
	private Integer gScore = -1;//客队得分
	private String score;//完整比分
	private Integer hhScore = -1;//半场主队得分
	private Integer hgScore = -1;//半场客队得分
	private Integer lose;//让球数
	private Integer status;//比赛状态（-1:已取消 0-:已停售 1-:销售中 2-:已截止）
	private Integer state;//处理状态 （0-待处理 1-自动撤单中 2-赛果获取中 3-已有赛果待审核 4-赛果人工审核成功 5-系统审核成功
	// 6-计算奖金成功 7-奖金汇总成功 8-奖金核对成功 9-自动派奖成功 10-过关统计完成 11-战绩统计完成
	// 12-派送返点成功 99-场次处理结束）
    private Date endTime;//销售截止时间
	private Date matchTime;//比赛时间
	private long bingoCode = -1;//命中匹配

	public void init() {
		long l = 0;
		String key = "";
		if (this.state >= 5 && this.status != 1 && this.status != -1) {//赛果已审核且未取消
			// RQSPF
			if (this.hScore + this.lose > this.gScore) {
				key = "3";
			} else if (this.hScore + this.lose == this.gScore) {
				key = "1";
			} else {
				key = "0";
			}
			int pos = JcItemCodeUtil.getPosition(JcItemCodeUtil.RQSPF, key);
			if (pos < 0) {
				throw new RuntimeException("让球胜平负赛果异常 key=" + key + " 场次=" + matchCode);
			}
			l |= 1L << pos;

			// SPF
			if (this.hScore > this.gScore) {
				key = "3";
			} else if (this.hScore == this.gScore) {
				key = "1";
			} else {
				key = "0";
			}
			pos = JcItemCodeUtil.getPosition(JcItemCodeUtil.SPF, key);
			if (pos < 0) {
				throw new RuntimeException("胜平负赛果异常 key=" + key + " 场次=" + matchCode);
			}
			l |= 1L << pos;

			// JQS
			if (this.hScore + this.gScore >= 7) {
				key = "7";
			} else {
				key = (this.hScore + this.gScore) + "";
			}
			pos = JcItemCodeUtil.getPosition(JcItemCodeUtil.JQS, key);
			if (pos < 0) {
				throw new RuntimeException("进球数赛果异常 key=" + key + " 场次=" + matchCode);
			}
			l |= 1L << pos;

			// CBF
			key = this.getCbfValue();
			pos = JcItemCodeUtil.getPosition(JcItemCodeUtil.CBF, key);
			if (pos < 0) {
				throw new RuntimeException("猜比分赛果异常 key=" + key + " 场次=" + matchCode);
			}
			l |= 1L << pos;

			// BQC
			if (this.hhScore > this.hgScore) {
				key = "3-";
			} else if (this.hhScore == this.hgScore) {
				key = "1-";
			} else {
				key = "0-";
			}
			if (this.hScore > this.gScore) {
				key += "3";
			} else if (this.hScore == this.gScore) {
				key += "1";
			} else {
				key += "0";
			}
			pos = JcItemCodeUtil.getPosition(JcItemCodeUtil.BQC, key);
			if (pos < 0) {
				throw new RuntimeException("半全场赛果异常 key=" + key + " 场次=" + matchCode);
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
	 * 格式化比分值
	 * @return
	 */
	private String getCbfValue(){
		int hs = this.hScore;
		int ms = this.gScore;

		String key = hs + ":" + ms;
		if (hs > ms) {
			if (hs == ms && hs > 3) {
				key = "9:9";
			}
			if (hs == 4 && ms == 3) {
				key = "9:0";
			}
			if (hs == 5 && ms > 2) {
				key = "9:0";
			}
			if (hs > 5) {
				key = "9:0";
			}
		} else if (hs == ms) {
			if (hs > 3) {
				key = "9:9";
			}
		} else {
			if (ms == 4 && hs == 3) {
				key = "0:9";
			}
			if (ms == 5 && hs > 2) {
				key = "0:9";
			}
			if (ms > 5) {
				key = "0:9";
			}
		}
		return key;
	}

	/**
	 * 获取出票sp
	 * @param mapTicketSp
	 * @param item
	 * @return
	 */
	public String getSpValue(Map<String, String> mapTicketSp, JcItemBean item) {
		String s = "";
		if (this.status != -1) {
			s = getJcTicketSp(mapTicketSp, item);
		} else {
			s = "1";//比赛取消
		}
		return s;
	}

	/**
	 * 竞彩足球出票查询SP解析
	 * @param mapTicketSp
	 * @param item
	 * @return
	 */
	public String getJcTicketSp(Map<String, String> mapTicketSp, JcItemBean item) {
		String spValue = "";
		int playType = getCodePlayType(item);
		String result = "";
		if (playType == JcItemCodeUtil.SPF) {
			result = getSpfResult();
		}
		else if (playType == JcItemCodeUtil.RQSPF) {
			result = getRQSpfResult();
		}
		else if (playType == JcItemCodeUtil.JQS) {
			result = getJqsResult();
		}
		else if (playType == JcItemCodeUtil.CBF) {
			result = getCbfResult();
		}
		else if (playType == JcItemCodeUtil.BQC) {
			result = getBqcResult();
		}

		String key = item.getItemid() + "_" + playType + "_" + result;
		if(mapTicketSp.containsKey(key)) {
			spValue = mapTicketSp.get(key);
		}
		return spValue;
	}

	/**
	 * 获取玩法类型
	 * @param item
	 * @return
	 */
	private static int getCodePlayType(JcItemBean item){
		int type = -1;
		long tl = item.getCountItemType();
		for(int m = JcItemCodeUtil.RQSPF; m < JcItemCodeUtil.HH; m++){
			if(Long.bitCount(tl & (1L << m)) == 1){
				type = m;
				break;
			}
		}
		return type;
	}

	/**
	 * 胜平负结果
	 * @return
	 */
	private String getSpfResult(){
		int hs = this.hScore;
		int ms = this.gScore;
		String key = "";
		if (hs > ms) {
			key = "3";
		} else if (hs == ms) {
			key = "1";
		} else if (hs < ms) {
			key = "0";
		}
		return key;
	}

	/**
	 * 让球胜平负结果
	 * @return
	 */
	private String getRQSpfResult(){
		int hs = this.hScore + lose;
		int ms = this.gScore;
		String key = "";
		if (hs > ms) {
			key = "3";
		} else if (hs == ms) {
			key = "1";
		} else if (hs < ms) {
			key = "0";
		}
		return key;
	}

	/**
	 * 比分结果
	 * @return
	 */
	private String getCbfResult(){
		int hs = this.hScore;
		int ms = this.gScore;
		String key = hs + "" + ms;
		if (hs > ms) {
			if (hs == ms && hs > 3) {
				key = "99";
			}
			if (hs == 4 && ms == 3) {
				key = "90";
			}
			if (hs == 5 && ms > 2) {
				key = "90";
			}
			if (hs > 5) {
				key = "90";
			}
		} else if (hs == ms) {
			if (hs > 3) {
				key = "99";
			}
		} else {
			if (ms == 4 && hs == 3) {
				key = "09";
			}
			if (ms == 5 && hs > 2) {
				key = "09";
			}
			if (ms > 5) {
				key = "09";
			}
		}
		return key;
	}

	/**
	 * 半全场结果
	 * @return
	 */
	private String getBqcResult() {
		int hs = this.hScore;
		int ms = this.gScore;

		int hhs = this.hhScore;
		int hms = this.hgScore;

		String key = "";
		if (hhs > hms) {//胜
			if (hs > ms) {
				key = "33";
			} else if (hs == ms) {
				key = "31";
			} else {
				key = "30";
			}
		} else if (hhs == hms) {//平
			if (hs > ms) {
				key = "13";
			} else if (hs == ms) {
				key = "11";
			} else {
				key = "10";
			}
		} else {
			if (hs > ms) {
				key = "03";
			} else if (hs == ms) {
				key = "01";
			} else {
				key = "00";
			}
		}
		return key;
	}

	/**
	 * 进球数结果
	 * @return
	 */
	private String getJqsResult(){
		int hs = this.hScore;
		int ms = this.gScore;
		int t = hs + ms;
		if (t > 7) {
			t = 7;
		}
		return ""+t;
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

	public Integer getHhScore() {
		return hhScore;
	}

	public void setHhScore(Integer hhScore) {
		this.hhScore = hhScore;
	}

	public Integer getHgScore() {
		return hgScore;
	}

	public void setHgScore(Integer hgScore) {
		this.hgScore = hgScore;
	}

	public Integer getLose() {
		return lose;
	}

	public void setLose(Integer lose) {
		this.lose = lose;
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

	public long getBingoCode() {
		return bingoCode;
	}

	public void setBingoCode(long bingoCode) {
		this.bingoCode = bingoCode;
	}
}