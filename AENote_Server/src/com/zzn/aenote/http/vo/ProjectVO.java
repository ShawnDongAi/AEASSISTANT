package com.zzn.aenote.http.vo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Map;

public class ProjectVO implements Serializable {

	private static final long serialVersionUID = 1590949897249696247L;

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
	private String CREATE_USER_NAME;
	private String CREATE_USER_PHONE;
	private String CREATE_USER_HEAD;
	private String ROOT_PROJECT_NAME;
	
	private static SimpleDateFormat allFormater = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm");

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
		return CREATE_TIME;
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

	public static ProjectVO assembleProject(Map<String, Object> project) {
		ProjectVO vo = new ProjectVO();
		vo.setPROJECT_ID(project.get("project_id").toString().trim());
		String projectName = project.get("project_name").toString().trim();
		if (projectName.startsWith("-")) {
			projectName = projectName.substring(1, projectName.length());
		}
		vo.setPROJECT_NAME(projectName);
		if (project.get("head") != null) {
			vo.setHEAD(project.get("head").toString().trim());
		}
		if (project.get("parent_id") != null) {
			vo.setPARENT_ID(project.get("parent_id").toString().trim());
		}
		vo.setROOT_ID(project.get("root_id").toString().trim());
		if (project.get("root_project_name") != null) {
			vo.setROOT_PROJECT_NAME(project.get("root_project_name").toString().trim());
		}
		String create_time = project.get("create_time").toString().trim().replaceAll("\t", "").replaceAll("\n", " ");
		try {
			vo.setCREATE_TIME(formater.format(allFormater.parse(create_time)));
		} catch (Exception e) {
			vo.setCREATE_TIME(create_time);
		}
		vo.setCREATE_USER(project.get("create_user").toString().trim());
		String user_name = "";
		String user_phone = "";
		String user_head = "";
		if (project.get("create_user_name") != null) {
			user_name = project.get("create_user_name").toString().trim();
		}
		if (project.get("create_user_phone") != null) {
			user_phone = project.get("create_user_phone").toString().trim();
		}
		if (project.get("create_user_head") != null) {
			user_head = project.get("create_user_head").toString().trim();
		}
		vo.setCREATE_USER_NAME(user_name);
		vo.setCREATE_USER_PHONE(user_phone);
		vo.setCREATE_USER_HEAD(user_head);
		if (project.get("address") != null) {
			vo.setADDRESS(project.get("address").toString().trim());
		}
		vo.setLONGITUDE(project.get("longitude").toString().trim());
		vo.setLATITUDE(project.get("latitude").toString().trim());
		vo.setSTATUS(project.get("status").toString().trim());
		return vo;
	}
}
