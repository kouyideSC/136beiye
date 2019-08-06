package com.caipiao.domain.base;

import com.caipiao.domain.code.ErrorCode_API;

import java.io.Serializable;


/**
 * 接口返回标准bean
 * @user kouyi
 * @date 2017-09-20
 */
public class ResultBean extends ErrorCode_API implements Serializable {
	private static final long serialVersionUID = 1L;
	private int errorCode = SUCCESS;//错误号
	private String errorDesc = SUCCESS_MSG;//错误描叙
	private Object data; //对象数据

	public String errorMsg(int code) {
		return getCodeMsg(code);
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
		this.errorDesc = errorMsg(errorCode);
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
