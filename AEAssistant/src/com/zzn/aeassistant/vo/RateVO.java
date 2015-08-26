package com.zzn.aeassistant.vo;

import java.io.Serializable;

public class RateVO implements Serializable {
	private static final long serialVersionUID = -7041396373681301283L;

	private String user_id;
	private String rate_user;
	private float rate;
	private String content;
	private String time;
	private String rate_user_name;
	private String rate_user_phone;
	private String project_id;
	private String project_name;
	private String root_project_name;

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getRate_user() {
		return rate_user;
	}

	public void setRate_user(String rate_user) {
		this.rate_user = rate_user;
	}

	public float getRate() {
		return rate;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getRate_user_name() {
		return rate_user_name;
	}

	public void setRate_user_name(String rate_user_name) {
		this.rate_user_name = rate_user_name;
	}

	public String getRate_user_phone() {
		return rate_user_phone;
	}

	public void setRate_user_phone(String rate_user_phone) {
		this.rate_user_phone = rate_user_phone;
	}

	public String getProject_id() {
		return project_id;
	}

	public void setProject_id(String project_id) {
		this.project_id = project_id;
	}

	public String getProject_name() {
		return project_name;
	}

	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}

	public String getRoot_project_name() {
		return root_project_name;
	}

	public void setRoot_project_name(String root_project_name) {
		this.root_project_name = root_project_name;
	}
}
