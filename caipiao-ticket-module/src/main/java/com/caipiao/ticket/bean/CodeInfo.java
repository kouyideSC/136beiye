package com.caipiao.ticket.bean;

import net.sf.json.JSONArray;

import java.io.Serializable;

/**
 * 对接出票商接口所需票信息对象
 * Created by Kouyi on 2017/12/13.
 */
public class CodeInfo implements Serializable {
	private static final long serialVersionUID = -3929814554100266212L;
	private int multiple;//倍数
	private int zhuShu;//注数
	private String playType;//玩法代码
	private String saleCode;//销售代码
	private String code;//投注串
	private String pass;//串关方式
	private int money;//投注金额
	private String orderId;//订单编号
	private String castType;//投注方式
	private String codeCopy;//投注串副本[华阳使用]
	private String lotteryId;//彩种编号[华阳使用]
	private int errorCode=0;//状态码
	private JSONArray array;//欧克出票商使用

	public JSONArray getArray() {
		return array;
	}

	public void setArray(JSONArray array) {
		this.array = array;
	}

	public String getLotteryId() {
		return lotteryId;
	}

	public void setLotteryId(String lotteryId) {
		this.lotteryId = lotteryId;
	}

	public String getCodeCopy() {
		return codeCopy;
	}

	public void setCodeCopy(String codeCopy) {
		this.codeCopy = codeCopy;
	}

	public int getMultiple() {
		return multiple;
	}

	public void setMultiple(int multiple) {
		this.multiple = multiple;
	}

	public int getZhuShu() {
		return zhuShu;
	}

	public void setZhuShu(int zhuShu) {
		this.zhuShu = zhuShu;
	}

	public String getPlayType() {
		return playType;
	}

	public void setPlayType(String playType) {
		this.playType = playType;
	}

	public String getSaleCode() {
		return saleCode;
	}

	public void setSaleCode(String saleCode) {
		this.saleCode = saleCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getCastType() {
		return castType;
	}

	public void setCastType(String castType) {
		this.castType = castType;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
}