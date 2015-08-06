package com.zzn.aeassistant.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserVO implements Serializable {

	private static final long serialVersionUID = 7205350340862920732L;

	private String USER_ID;
	private String USER_NAME;
	private String PHONE;
	private String REMARK;
	private List<ProjectVO> PROJECTS = new ArrayList<ProjectVO>();
	private String SEX = "0";
	private String PASSWORD;
	private String SMALL_HEAD;
	private String BIG_HEAD;
	private String CREATE_TIME;
	private String IDCARD;
	private String IDCARD_FRONT;
	private String IDCARD_BACK;
	private String IDCARD_HAND;

	public String getUSER_ID() {
		return USER_ID;
	}

	public void setUSER_ID(String uSER_ID) {
		USER_ID = uSER_ID;
	}

	public String getUSER_NAME() {
		return USER_NAME;
	}

	public void setUSER_NAME(String uSER_NAME) {
		USER_NAME = uSER_NAME;
	}

	public String getPHONE() {
		return PHONE;
	}

	public void setPHONE(String pHONE) {
		PHONE = pHONE;
	}

	public String getREMARK() {
		return REMARK;
	}

	public void setREMARK(String rEMARK) {
		REMARK = rEMARK;
	}

	public List<ProjectVO> getPROJECTS() {
		return PROJECTS;
	}

	public void setPROJECTS(List<ProjectVO> pROJECTS) {
		PROJECTS = pROJECTS;
	}

	public String getSEX() {
		return SEX;
	}

	public void setSEX(String sEX) {
		SEX = sEX;
	}

	public String getSMALL_HEAD() {
		return SMALL_HEAD;
	}

	public void setSMALL_HEAD(String sMALL_HEAD) {
		SMALL_HEAD = sMALL_HEAD;
	}

	public String getBIG_HEAD() {
		return BIG_HEAD;
	}

	public void setBIG_HEAD(String bIG_HEAD) {
		BIG_HEAD = bIG_HEAD;
	}

	public String getCREATE_TIME() {
		return CREATE_TIME;
	}

	public void setCREATE_TIME(String cREATE_TIME) {
		CREATE_TIME = cREATE_TIME;
	}

	public String getPASSWORD() {
		return PASSWORD;
	}

	public void setPASSWORD(String pASSWORD) {
		PASSWORD = pASSWORD;
	}

	public String getIDCARD() {
		return IDCARD;
	}

	public void setIDCARD(String iDCARD) {
		IDCARD = iDCARD;
	}

	public String getIDCARD_FRONT() {
		return IDCARD_FRONT;
	}

	public void setIDCARD_FRONT(String iDCARD_FRONT) {
		IDCARD_FRONT = iDCARD_FRONT;
	}

	public String getIDCARD_BACK() {
		return IDCARD_BACK;
	}

	public void setIDCARD_BACK(String iDCARD_BACK) {
		IDCARD_BACK = iDCARD_BACK;
	}

	public String getIDCARD_HAND() {
		return IDCARD_HAND;
	}

	public void setIDCARD_HAND(String iDCARD_HAND) {
		IDCARD_HAND = iDCARD_HAND;
	}
}