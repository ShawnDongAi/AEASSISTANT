package com.zzn.aeassistant.vo;

import java.io.Serializable;

public class ProjectVO implements Serializable {

	private static final long serialVersionUID = 8812820014809331279L;

	private String PROJECT_ID;
	private String PROJECT_NAME;
	private String HEAD;
	private String PARENT_ID;
	private String ROOT_ID;
	private String CREATE_TIME;
	private String CREATE_USER;
	private String ADDRESS;
	private String LONGITUDE;
	private String LATITUDE;
	private String STATUS;

	public String getPROJECT_ID() {
		return PROJECT_ID;
	}

	public void setPROJECT_ID(String pROJECT_ID) {
		PROJECT_ID = pROJECT_ID;
	}

	public String getPROJECT_NAME() {
		return PROJECT_NAME;
	}

	public void setPROJECT_NAME(String pROJECT_NAME) {
		PROJECT_NAME = pROJECT_NAME;
	}

	public String getHEAD() {
		return HEAD;
	}

	public void setHEAD(String hEAD) {
		HEAD = hEAD;
	}

	public String getPARENT_ID() {
		return PARENT_ID;
	}

	public void setPARENT_ID(String pARENT_ID) {
		PARENT_ID = pARENT_ID;
	}

	public String getROOT_ID() {
		return ROOT_ID;
	}

	public void setROOT_ID(String rOOT_ID) {
		ROOT_ID = rOOT_ID;
	}

	public String getCREATE_TIME() {
		return CREATE_TIME.replaceAll("\n", "");
	}

	public void setCREATE_TIME(String cREATE_TIME) {
		CREATE_TIME = cREATE_TIME;
	}

	public String getCREATE_USER() {
		return CREATE_USER;
	}

	public void setCREATE_USER(String cREATE_USER) {
		CREATE_USER = cREATE_USER;
	}

	public String getADDRESS() {
		return ADDRESS;
	}

	public void setADDRESS(String aDDRESS) {
		ADDRESS = aDDRESS;
	}

	public String getLONGITUDE() {
		return LONGITUDE;
	}

	public void setLONGITUDE(String lONGITUDE) {
		LONGITUDE = lONGITUDE;
	}

	public String getLATITUDE() {
		return LATITUDE;
	}

	public void setLATITUDE(String lATITUDE) {
		LATITUDE = lATITUDE;
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
}
