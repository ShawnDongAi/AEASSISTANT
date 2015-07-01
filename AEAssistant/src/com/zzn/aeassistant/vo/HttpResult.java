package com.zzn.aeassistant.vo;

import java.io.Serializable;

public class HttpResult implements Serializable {
	private static final long serialVersionUID = -4740962860850034809L;
	public static final String CODE_SUCCESS = "200";
	public static final String CODE_FAILED = "404";
	private String RES_CODE = CODE_FAILED;
	private String RES_MESSAGE;
	private Object RES_OBJ;

	public String getRES_CODE() {
		return RES_CODE;
	}

	public void setRES_CODE(String rES_CODE) {
		RES_CODE = rES_CODE;
	}

	public String getRES_MESSAGE() {
		return RES_MESSAGE;
	}

	public void setRES_MESSAGE(String rES_MESSAGE) {
		RES_MESSAGE = rES_MESSAGE;
	}

	public Object getRES_OBJ() {
		return RES_OBJ;
	}

	public void setRES_OBJ(Object rES_OBJ) {
		RES_OBJ = rES_OBJ;
	}
}
