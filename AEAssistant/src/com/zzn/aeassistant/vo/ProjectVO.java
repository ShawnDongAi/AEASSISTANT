package com.zzn.aeassistant.vo;

import java.io.Serializable;

import com.zzn.aeassistant.view.tree.TreeNodeLabel;
import com.zzn.aeassistant.view.tree.TreeNodePid;

public class ProjectVO implements Serializable {

	private static final long serialVersionUID = 8812820014809331279L;

	@com.zzn.aeassistant.view.tree.TreeNodeId
	private String PROJECT_ID;
	@TreeNodeLabel
	private String PROJECT_NAME;
	private String HEAD;
	@TreeNodePid
	private String PARENT_ID;
	private String ROOT_ID;
	private String CREATE_TIME;
	private String CREATE_USER;
	private String ADDRESS;
	private String LONGITUDE;
	private String LATITUDE;
	private String STATUS;
	private String CREATE_USER_NAME;
	private String CREATE_USER_PHONE;
	private String CREATE_USER_HEAD;
	private String ROOT_PROJECT_NAME;

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

	public String getCREATE_USER_NAME() {
		return CREATE_USER_NAME;
	}

	public void setCREATE_USER_NAME(String cREATE_USER_NAME) {
		CREATE_USER_NAME = cREATE_USER_NAME;
	}

	public String getCREATE_USER_PHONE() {
		return CREATE_USER_PHONE;
	}

	public void setCREATE_USER_PHONE(String cREATE_USER_PHONE) {
		CREATE_USER_PHONE = cREATE_USER_PHONE;
	}

	public String getCREATE_USER_HEAD() {
		return CREATE_USER_HEAD;
	}

	public void setCREATE_USER_HEAD(String cREATE_USER_HEAD) {
		CREATE_USER_HEAD = cREATE_USER_HEAD;
	}

	public String getROOT_PROJECT_NAME() {
		return ROOT_PROJECT_NAME;
	}

	public void setROOT_PROJECT_NAME(String rOOT_PROJECT_NAME) {
		ROOT_PROJECT_NAME = rOOT_PROJECT_NAME;
	}
}