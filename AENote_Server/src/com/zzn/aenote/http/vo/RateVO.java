package com.zzn.aenote.http.vo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Map;

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
	private static SimpleDateFormat allFormater = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat formater = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");

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

	public static RateVO assembleRate(Map<String, Object> rate) {
		RateVO vo = new RateVO();
		vo.setUser_id(rate.get("user_id").toString());
		vo.setRate_user(rate.get("rate_user").toString());
		vo.setRate(Float.parseFloat(rate.get("rate").toString()));
		vo.setContent(rate.get("content").toString());
		String time = rate.get("time").toString().trim().replaceAll("\t", "")
				.replaceAll("\n", " ");
		try {
			vo.setTime(formater.format(allFormater.parse(time)));
		} catch (Exception e) {
			e.printStackTrace();
			vo.setTime(time);
		}
		vo.setRate_user_name(rate.get("rate_user_name").toString());
		vo.setRate_user_phone(rate.get("rate_user_phone").toString());
		vo.setProject_id(rate.get("project_id").toString());
		vo.setProject_name(rate.get("project_name").toString());
		vo.setRoot_project_name(rate.get("root_project_name").toString());
		return vo;
	}
}
