package com.zzn.aenote.http.vo;

import java.io.Serializable;

public class BaseRep implements Serializable {

	private static final long serialVersionUID = 3418055662198311267L;
	private String RES_CODE;
	private String RES_MESSAGE;
	private Object RES_OBJ = new Object();

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
