package com.zzn.aenote.http.vo;

import java.io.Serializable;
import java.util.Map;

public class AttendanceVO implements Serializable {

	private static final long serialVersionUID = -7726149466890260677L;
	private String project_id;
	private String project_name;
	private String parent_id;
	private String root_id;
	private String date;
	private String imgURL;
	private String user_id;
	private String user_name;
	private String user_phone;
	private String address;
	private String longitude;
	private String latitude;
	private String normal;
	private String status;

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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getImgURL() {
		return imgURL;
	}

	public void setImgURL(String imgURL) {
		this.imgURL = imgURL;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_phone() {
		return user_phone;
	}

	public void setUser_phone(String user_phone) {
		this.user_phone = user_phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getNormal() {
		return normal;
	}

	public void setNormal(String normal) {
		this.normal = normal;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public static AttendanceVO assembleAttendance(Map<String, Object> attendance) {
		AttendanceVO vo = new AttendanceVO();
		vo.setUser_id(attendance.get("user_id").toString());
		vo.setUser_name(attendance.get("user_name").toString());
		vo.setUser_phone(attendance.get("phone").toString());
		vo.setProject_id(attendance.get("project_id").toString());
		vo.setProject_name(attendance.get("project_name").toString());
		vo.setParent_id(attendance.get("parent_id").toString());
		vo.setRoot_id(attendance.get("root_id").toString());
		vo.setDate(attendance.get("time").toString());
		vo.setImgURL(attendance.get("photo").toString());
		vo.setAddress(attendance.get("address").toString());
		vo.setLongitude(attendance.get("longitude").toString());
		vo.setLatitude(attendance.get("latitude").toString());
		vo.setNormal(attendance.get("normal").toString());
		vo.setStatus(attendance.get("status").toString());
		return vo;
	}
}