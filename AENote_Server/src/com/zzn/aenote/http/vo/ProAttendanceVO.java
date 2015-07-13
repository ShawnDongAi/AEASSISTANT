package com.zzn.aenote.http.vo;

import java.io.Serializable;
import java.util.Map;

public class ProAttendanceVO implements Serializable {

	private static final long serialVersionUID = -5208450861256686116L;

	private String project_id;
	private String project_name;
	private String parent_id;
	private String root_id;
	private int count;
	private int exception_count;

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

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	public String getRoot_id() {
		return root_id;
	}

	public void setRoot_id(String root_id) {
		this.root_id = root_id;
	}
	
	public int getException_count() {
		return exception_count;
	}

	public void setException_count(int exception_count) {
		this.exception_count = exception_count;
	}

	public static ProAttendanceVO assembleAttendance(Map<String, Object> attendance) {
		ProAttendanceVO vo = new ProAttendanceVO();
		vo.setCount(Integer.parseInt(attendance.get("count").toString()));
		vo.setCount(Integer.parseInt(attendance.get("exception_count").toString()));
		vo.setProject_id(attendance.get("project_id").toString());
		vo.setProject_name(attendance.get("project_name").toString());
		vo.setParent_id(attendance.get("parent_id").toString());
		vo.setRoot_id(attendance.get("root_id").toString());
		return vo;
	}
}
