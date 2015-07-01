package com.zzn.aeassistant.vo;

import java.io.Serializable;

public class AttchVO implements Serializable {
	public static final String TYPE_HEAD = "0";
	public static final String TYPE_IMG = "1";
	public static final String TYPE_AUDIO = "2";
	public static final String TYPE_DOC = "3";
	public static final String TYPE_EXCEL = "4";
	public static final String TYPE_PDF = "5";
	public static final String TYPE_OTHER = "6";
	public static final String TYPE_ATTENCHANCE = "7";

	private static final long serialVersionUID = -7363868126506219881L;
	private String ATTCH_ID;
	private String TYPE;
	private String URL;
	private String LOCAL_PATH;
	private String NAME;

	public String getATTCH_ID() {
		return ATTCH_ID;
	}

	public void setATTCH_ID(String aTTCH_ID) {
		ATTCH_ID = aTTCH_ID;
	}

	public String getTYPE() {
		return TYPE;
	}

	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public String getLOCAL_PATH() {
		return LOCAL_PATH;
	}

	public void setLOCAL_PATH(String lOCAL_PATH) {
		LOCAL_PATH = lOCAL_PATH;
	}

	public String getNAME() {
		return NAME;
	}

	public void setNAME(String nAME) {
		NAME = nAME;
	}

	public static String getAttchType(String attchName) {
		if (attchName.toLowerCase().endsWith(".bmp")
				|| attchName.toLowerCase().endsWith(".jpg")
				|| attchName.toLowerCase().endsWith(".jpeg")
				|| attchName.toLowerCase().endsWith(".png")
				|| attchName.toLowerCase().endsWith(".gif")) {
			return TYPE_IMG;
		} else if (attchName.toLowerCase().endsWith(".amr")) {
			return TYPE_AUDIO;
		} else if (attchName.toLowerCase().endsWith(".doc")
				|| attchName.toLowerCase().endsWith(".docx")) {
			return TYPE_DOC;
		} else if (attchName.toLowerCase().endsWith(".xls")
				|| attchName.toLowerCase().endsWith(".xlsx")) {
			return TYPE_EXCEL;
		} else if (attchName.toLowerCase().endsWith(".pdf")) {
			return TYPE_PDF;
		} else {
			return TYPE_OTHER;
		}
	}
}
