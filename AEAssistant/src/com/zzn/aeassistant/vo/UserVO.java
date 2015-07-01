package com.zzn.aeassistant.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

	public static UserVO assembleUserVO(Map<String, Object> userInfo) {
		UserVO user = new UserVO();
		if (userInfo.get("user_id") != null) {
			user.setUSER_ID(userInfo.get("user_id").toString());
		}
		if (userInfo.get("user_name") != null) {
			user.setUSER_NAME(userInfo.get("user_name").toString());
		}
		if (userInfo.get("phone") != null) {
			user.setPHONE(userInfo.get("phone").toString());
		}
		if (userInfo.get("remark") != null) {
			user.setREMARK(userInfo.get("remark").toString());
		}
		if (userInfo.get("sex") != null) {
			user.setSEX(userInfo.get("sex").toString());
		}
		if (userInfo.get("small_head") != null) {
			user.setSMALL_HEAD(userInfo.get("small_head").toString());
		}
		if (userInfo.get("big_head") != null) {
			user.setBIG_HEAD(userInfo.get("big_head").toString());
		}
		if (userInfo.get("create_time") != null) {
			user.setCREATE_TIME(userInfo.get("create_time").toString());
		}
		return user;
	}
}
